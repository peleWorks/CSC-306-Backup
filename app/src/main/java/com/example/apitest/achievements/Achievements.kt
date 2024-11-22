package com.example.apitest.achievements

import com.example.apitest.R
import com.example.apitest.data_classes.Achievement

object Achievements {

    val PERFECT_SCORE = Achievement(
        id = "perfect_score",
        title = "Perfect Score",
        description = "Get all questions right in a quiz",
        iconResId = R.drawable.ic_trophy,
        unlocked = false
    )

    val DAILY_CHAMPION = Achievement(
        id = "daily_champion",
        title = "Daily Champion",
        description = "Complete a daily quiz",
        iconResId = R.drawable.ic_calendar,
        unlocked = false
    )

    val TOP_THREE = Achievement(
        id = "top_three",
        title = "Leaderboard Elite",
        description = "Place in the top 3 of any leaderboard",
        iconResId = R.drawable.ic_medal,
        unlocked = false
    )

    val ALL_ACHIEVEMENTS = listOf(PERFECT_SCORE, DAILY_CHAMPION, TOP_THREE)



}