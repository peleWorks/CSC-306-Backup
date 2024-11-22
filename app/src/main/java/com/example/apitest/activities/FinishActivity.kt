package com.example.apitest.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apitest.data_classes.Result
import com.example.apitest.data_classes.UserModel
import com.example.apitest.databinding.ActivityFinishBinding
import com.example.apitest.firebase.FirebaseSetup
import com.example.apitest.recycler_view_adapters.TriviaResultsAdapter
import com.example.apitest.utilities.AchievementsManager
import com.example.apitest.utilities.Utils
import java.util.Locale

class FinishActivity : AppCompatActivity() {

    private val firebaseSetup = FirebaseSetup()

    private var binding: ActivityFinishBinding? = null

    private lateinit var achievementsManager: AchievementsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        achievementsManager = AchievementsManager(this)

        val resultList = intent.getSerializableExtraProvider<ArrayList<Result>>("resultList")
            ?: arrayListOf()

        val endScore = getEndScore(resultList)
        FirebaseSetup().updateScore(endScore)

        if (resultList.size == 1) {
            calculateScoreWithStreak(resultList)
            achievementsManager.checkDailyQuizCompletion()
        } else {
            // Regular quiz, no streak bonus
            val endScore = getEndScore(resultList)
            FirebaseSetup().updateScore(endScore)
            displayResults(resultList, endScore)
        }

        val correctAnswers = resultList.count { it.score > 0}
        achievementsManager.checkQuizCompletion(resultList.size, correctAnswers)




        binding?.resultsRecyclerview?.layoutManager = LinearLayoutManager(this)
        val resultsAdapter = TriviaResultsAdapter(resultList)

        binding?.resultsRecyclerview?.adapter = resultsAdapter

        binding?.totalScore?.text = String.format(
            Locale.getDefault(),
            "Total Points: %d", getEndScore(resultList)
        )

        binding?.homeButton?.setOnClickListener {
            finish()
        }


    }


    private fun getEndScore(list: ArrayList<Result>): Int {
        var endScore = 0
        for (i in list)
            endScore += i.score
        return endScore
    }


    private inline fun <reified T : java.io.Serializable> Intent.getSerializableExtraProvider(
        identifierParameter: String
    ): T? {
        return this.getSerializableExtra(identifierParameter, T::class.java)
    }

    private fun calculateScoreWithStreak(resultList: ArrayList<Result>) {
        firebaseSetup.getUserInfo(object : FirebaseSetup.UserInfoCallback {
            override fun onUserInfoFetched(userInfo: UserModel?) {
                userInfo?.let { user ->
                    val baseScore = getEndScore(resultList)
                    val isCorrect = resultList[0].score > 0

                    // Calculate streak bonus
                    val streakBonus = if (isCorrect) {
                        // Current streak before increment
                        user.dailyStreak
                    } else {
                        0 // No bonus if answer was wrong
                    }

                    val totalScore = baseScore + streakBonus

                    // Update Firebase with total score
                    FirebaseSetup().updateScore(totalScore)

                    // Update streak and display results
                    updateDailyStreak(isCorrect)
                    displayResults(resultList, totalScore, streakBonus)
                }
            }
        })
    }


    private fun displayResults(resultList: ArrayList<Result>, totalScore: Int, streakBonus: Int = 0) {
        // Update RecyclerView
        binding?.resultsRecyclerview?.layoutManager = LinearLayoutManager(this)
        val resultsAdapter = TriviaResultsAdapter(resultList)
        binding?.resultsRecyclerview?.adapter = resultsAdapter

        // Display total score with streak bonus if applicable
        if (streakBonus > 0) {
            binding?.totalScore?.text = String.format(
                Locale.getDefault(),
                "Total Points: %d (Including streak bonus: +%d)",
                totalScore,
                streakBonus
            )
        } else {
            binding?.totalScore?.text = String.format(
                Locale.getDefault(),
                "Total Points: %d",
                totalScore
            )
        }
    }





    private fun updateDailyStreak(isCorrect: Boolean) {
        firebaseSetup.getUserInfo(object : FirebaseSetup.UserInfoCallback {
            override fun onUserInfoFetched(userInfo: UserModel?) {
                userInfo?.let { user ->
                    val currentDate = java.time.LocalDate.now().toString()
                    val newStreak = if (isCorrect) user.dailyStreak + 1 else 0

                    // Create updated user model
                    val updatedUser = user.copy(
                        dailyStreak = newStreak,
                        lastAttemptDate = currentDate
                    )

                    // Update Firebase
                    firebaseSetup.updateUserPreferences(updatedUser)

                    // Show streak message
                    val message = if (isCorrect) {
                        "Correct! Daily streak: $newStreak"
                    } else {
                        "Incorrect! Daily streak reset to 0"
                    }
                    Utils.showToast(this@FinishActivity, message)
                }
            }
        })
    }
}



