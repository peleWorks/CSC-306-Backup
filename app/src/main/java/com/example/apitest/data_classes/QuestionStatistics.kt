package com.example.apitest.data_classes

data class QuestionStatistics(
    val overall: OverallStatistics,
    val categories: Map<String, CategoryStatistics>
)



