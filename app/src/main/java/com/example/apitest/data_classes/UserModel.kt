package com.example.apitest.data_classes

data class UserModel(
    val id:String = "",
    val emailId:String = "",
    val name:String = "",
    val image:String = "",
    val allTimeScore:Int = 0,
    val weeklyScore:Int = 0,
    val monthlyScore:Int = 0,
    val lastGameScore:Int = 0,
    val preferredDifficulty: String = "any",
    val preferredQuestionCount: Int = 10,
    val dailyStreak: Int = 0,
    val lastAttemptDate: String = "",
    val joinDate: String = "",
    val savedFacts: List<SavedFact> = listOf(),
    val achievements: List<Achievement> = listOf(),
    var notificationTime: Int = -1


)
