package com.example.apitest.fragments

import PreferencesBottomSheet
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apitest.R
import com.example.apitest.activities.SignInActivity
import com.example.apitest.data_classes.UserModel
import com.example.apitest.databinding.FragmentProfileBinding
import com.example.apitest.firebase.FirebaseSetup
import com.example.apitest.recycler_view_adapters.AchievementAdapter
import com.example.apitest.recycler_view_adapters.SavedFactAdapter
import com.example.apitest.utilities.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private lateinit var achievementsAdapter: AchievementAdapter
    private lateinit var auth: FirebaseAuth
    private var binding: FragmentProfileBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root = binding?.root


        setupMenu()
        setupAchievements()


        auth = Firebase.auth
        val user = auth.currentUser

        if (user == null) {
            startActivity(Intent(requireContext(), SignInActivity::class.java))
            return root
        }


        loadUserProfile()


        updateScoreView("allTimeScore", binding?.tvUserRanking!!)
        updateScoreView("weeklyScore", binding?.tvWeeklyRank!!)
        updateScoreView("monthlyScore", binding?.tvMonthlyRank!!)


        binding?.cvSignOut?.setOnClickListener {
            if (auth.currentUser != null) {
                auth.signOut()
                val intent = Intent(requireContext(), SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }



        return root
    }

    private fun loadUserProfile() {
        val progressBar =  Utils.showProgressBar(requireContext())
        FirebaseSetup().getUserInfo(object : FirebaseSetup.UserInfoCallback {
            override fun onUserInfoFetched(userInfo: UserModel?) {
                progressBar.cancel()
                userInfo?.let {

                    binding?.tvUserPoints?.text = String.format("%d", it.allTimeScore)
                    binding?.tvLastGameScore?.text = String.format("%d", it.lastGameScore)
                    binding?.tvWeeklyScore?.text = String.format("%d", it.weeklyScore)
                    binding?.tvMonthlyScore?.text = String.format("%d", it.monthlyScore)
                    binding?.tvUserName?.text = it.name
                    binding?.joinDate?.text = it.joinDate


                    val resourceId = resources.getIdentifier(
                        it.image.lowercase(), "drawable", requireContext().packageName
                    )
                    binding?.userProfilePic?.setImageResource(resourceId)


                    binding?.savedFactsRecyclerView?.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    binding?.savedFactsRecyclerView?.adapter =
                        SavedFactAdapter(it.savedFacts)

                    if (it.savedFacts.isEmpty()) {
                        binding?.savedFactsTextView?.visibility = View.GONE
                        binding?.savedFactsRecyclerView?.visibility = View.GONE
                    } else {
                        binding?.savedFactsTextView?.visibility = View.VISIBLE
                        binding?.savedFactsRecyclerView?.visibility = View.VISIBLE
                    }
                }
            }
        })
    }


    private fun updateScoreView(type: String, textView: TextView) {
        FirebaseSetup().getUserRank(type, object : FirebaseSetup.UserRankCallback {
            override fun onUserRankFetched(rank: Int?) {
                rank?.let {
                    textView.text = it.toString()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupMenu() {
        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.profile_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_settings -> {
                        PreferencesBottomSheet().show(
                            parentFragmentManager,
                            PreferencesBottomSheet.TAG
                        )
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    private fun setupAchievements() {
        achievementsAdapter = AchievementAdapter()
        binding?.achievementsRecyclerView?.apply {
            adapter = achievementsAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }


        FirebaseSetup().getUserInfo(object : FirebaseSetup.UserInfoCallback {
            override fun onUserInfoFetched(userInfo: UserModel?) {
                userInfo?.let { user ->
                    Log.d("ProfileFragment", "Raw Achievements: ${user.achievements}")
                    val unlockedAchievements = user.achievements.filter { it.unlocked }
                    Log.d("ProfileFragment", "Unlocked Achievements: $unlockedAchievements")

                    if (unlockedAchievements.isNotEmpty()) {

                        achievementsAdapter.updateAchievements(unlockedAchievements)
                        binding?.achievementsTextView?.visibility = View.VISIBLE
                        binding?.achievementsRecyclerView?.visibility = View.VISIBLE
                    } else {

                        binding?.achievementsTextView?.visibility = View.GONE
                        binding?.achievementsRecyclerView?.visibility = View.GONE
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
    }



}
