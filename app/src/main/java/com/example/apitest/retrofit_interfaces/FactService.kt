package com.example.apitest.retrofit_interfaces

import com.example.apitest.data_classes.FactResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface FactService {
    @Headers("X-Api-Key: gM8TEIQ2OA9X/eT64UH+Pg==wmvOUFOSvvzbQ3aY")
    @GET("v1/facts")
    fun getFact(): Call<List<FactResponse>>
}