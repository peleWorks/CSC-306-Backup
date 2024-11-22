package com.example.apitest.utilities

import android.content.Context
import com.example.apitest.achievements.Achievements
import com.example.apitest.data_classes.Achievement
import com.example.apitest.data_classes.UserModel
import com.example.apitest.firebase.FirebaseSetup

class AchievementsManager(private val context: Context) {

    private val firebaseSetup = FirebaseSetup()

    fun checkQuizCompletion(totalQuestions: Int, correctAnswers: Int) {
        if (totalQuestions > 1 && totalQuestions == correctAnswers) {
            unlockAchievement(Achievements.PERFECT_SCORE)
        }
    }

    fun checkDailyQuizCompletion() {
        unlockAchievement(Achievements.DAILY_CHAMPION)
    }

    fun checkLeaderboardPosition(position: Int) {
        if (position <= 3) {
            unlockAchievement(Achievements.TOP_THREE)
        }
    }




    private fun unlockAchievement(achievement: Achievement) {
        firebaseSetup.getUserInfo(object : FirebaseSetup.UserInfoCallback {
            override fun onUserInfoFetched(userInfo: UserModel?) {
                userInfo?.let { user ->
                    val currentAchievements = user.achievements.toMutableList()
                    val achievementIndex = currentAchievements.indexOfFirst { it.id == achievement.id }

                    //only unlock if not already unlocked
                    if (achievementIndex == -1 || !currentAchievements[achievementIndex].unlocked) {
                        val unlockedAchievement = achievement.copy(
                            unlocked = true,
                            unlockedDate = System.currentTimeMillis()
                        )

                        if (achievementIndex == -1) {
                            currentAchievements.add(unlockedAchievement)
                        } else {
                            currentAchievements[achievementIndex] = unlockedAchievement
                        }

                        // Update Firebase
                        val updatedUser = user.copy(achievements = currentAchievements)
                        firebaseSetup.updateUserPreferences(updatedUser)

                        // Show achievement unlock notification
                        showAchievementUnlocked(unlockedAchievement)
                    }
                }
            }
        })
    }

    private fun showAchievementUnlocked(achievement: Achievement) {
        val message = "Achievement Unlocked: ${achievement.title}"
        Utils.showToast(context, message)
    }
}