package com.example.apitest.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apitest.R
import com.example.apitest.data_classes.LeaderBoardModel
import com.example.apitest.databinding.FragmentLeaderboardBinding
import com.example.apitest.firebase.FirebaseSetup
import com.example.apitest.recycler_view_adapters.LeaderBoardAdapter
import com.example.apitest.utilities.AchievementsManager
import com.example.apitest.utilities.Constants

class LeaderboardFragment : Fragment() {

    private var binding: FragmentLeaderboardBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false)

        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        binding?.leaderBoardRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
        setLeaderBoard(Constants.allTimeScore)

        binding?.RadioGroup?.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.rbAllTime -> setLeaderBoard(Constants.allTimeScore)
                R.id.rbWeekly -> setLeaderBoard(Constants.weeklyScore)
                R.id.rbMonthly -> setLeaderBoard(Constants.monthlyScore)
            }
        }



        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }


    private fun setLeaderBoard(type: String) {
        FirebaseSetup().getLeaderBoardData(type, object : FirebaseSetup.LeaderBoardDataCallback {
            override fun onLeaderBoardDataFetched(leaderBoardModel: LeaderBoardModel?) {
                if (leaderBoardModel != null) {
                    val allRanks = leaderBoardModel.allRanks

                    val adapter = LeaderBoardAdapter(allRanks, type)
                    binding?.leaderBoardRecyclerView?.adapter = adapter


                    FirebaseSetup().getUserRank(type, object : FirebaseSetup.UserRankCallback {
                        override fun onUserRankFetched(rank: Int?) {
                            if (rank != null && rank <= 3) {
                                AchievementsManager(requireContext()).checkLeaderboardPosition(rank)
                            }
                        }
                    })


                }
            }
        })
    }



}

