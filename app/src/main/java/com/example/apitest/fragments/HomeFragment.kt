package com.example.apitest.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.apitest.activities.CustomTriviaActivity
import com.example.apitest.activities.RandomFactActivity
import com.example.apitest.data_classes.CategoryStatistics
import com.example.apitest.data_classes.UserModel
import com.example.apitest.databinding.FragmentHomeBinding
import com.example.apitest.fetch_trivia.FetchTrivia
import com.example.apitest.firebase.FirebaseSetup
import com.example.apitest.recycler_view_adapters.TwoColumnAdapter
import com.example.apitest.utilities.Constants
import com.example.apitest.utilities.Utils

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private val firebaseSetup = FirebaseSetup()

    override fun onResume() {
        super.onResume()
        updateStreakDisplay()
    }

    private fun updateStreakDisplay() {
        firebaseSetup.getUserInfo(object : FirebaseSetup.UserInfoCallback {
            override fun onUserInfoFetched(userInfo: UserModel?) {
                userInfo?.let { user ->
                    binding?.streakNumber?.text = user.dailyStreak.toString()
                }
            }
        })
    }

    private fun handleDailyQuestion() {
        firebaseSetup.getUserInfo(object : FirebaseSetup.UserInfoCallback {
            override fun onUserInfoFetched(userInfo: UserModel?) {
                userInfo?.let { user ->
                    val currentDate = java.time.LocalDate.now().toString()

                    if (user.lastAttemptDate == currentDate) {
                        // User has already attempted today's question
                        Utils.showToast(requireContext(), "You've already attempted today's question! Come back tomorrow!")
                    } else {
                        // User hasn't attempted today's question
                        val fetchTrivia = FetchTrivia(requireContext())
                        fetchTrivia.getQuiz(1, null, null, null)
                    }
                }
            }
        })
    }



    private fun loadQuizWithUserPreferences(categoryId: Int? = null) {

        firebaseSetup.getUserInfo(object : FirebaseSetup.UserInfoCallback {
            override fun onUserInfoFetched(userInfo: UserModel?) {
                userInfo?.let { user ->
                    val fetchTrivia = FetchTrivia(requireContext())
                    fetchTrivia.getQuiz(
                        amount = user.preferredQuestionCount,
                        category = categoryId,
                        difficulty = if (user.preferredDifficulty == "any") null else user.preferredDifficulty,
                        type = null
                    )
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding?.root
        val fetchTrivia = FetchTrivia(requireContext())
        val recyclerViewCategoryList = binding?.categories

        updateStreakDisplay()


        recyclerViewCategoryList?.layoutManager = GridLayoutManager(requireContext(), 2)


        fetchTrivia.getQuestionStatsList(object : FetchTrivia.QuestionStatCallback {
            override fun onQuestionStatFetched(map: Map<String, CategoryStatistics>) {
                val adapter = TwoColumnAdapter(Constants.getCategories(), map)
                recyclerViewCategoryList?.adapter = adapter
                adapter.setOnPressed(object : TwoColumnAdapter.OnPressed {

                    override fun onClick(id: Int) {
                        loadQuizWithUserPreferences(id)
                    }

                })
            }
        })


        binding?.dailyQuestionButton?.setOnClickListener {
            //Streak here
            handleDailyQuestion()
        }

        binding?.customQuizButton?.setOnClickListener {
            startActivity(Intent(requireContext(), CustomTriviaActivity::class.java))
        }

        binding?.randomFactButton?.setOnClickListener {
            startActivity(Intent(requireContext(), RandomFactActivity::class.java))
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}