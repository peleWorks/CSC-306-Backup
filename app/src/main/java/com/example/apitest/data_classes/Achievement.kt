package com.example.apitest.data_classes

data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val iconResId: Int = 0,
    val unlocked: Boolean = false,
    val unlockedDate: Long = 0
)
