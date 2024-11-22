package com.example.apitest.retrofit_interfaces

import com.example.apitest.data_classes.TriviaResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaService {
    @GET("api.php")
    fun getQuiz(
        @Query("amount") amount: Int,
        @Query("category") category: Int?,
        @Query("difficulty") difficulty: String?,
        @Query("type") type: String?
    ): Call<TriviaResponse>
}

