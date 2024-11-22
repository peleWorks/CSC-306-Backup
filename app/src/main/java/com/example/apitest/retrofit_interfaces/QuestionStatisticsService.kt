package com.example.apitest.retrofit_interfaces

import com.example.apitest.data_classes.QuestionStatistics
import retrofit2.Call
import retrofit2.http.GET

interface QuestionStatisticsService {
    @GET("api_count_global.php")
    fun getData(): Call<QuestionStatistics>
}