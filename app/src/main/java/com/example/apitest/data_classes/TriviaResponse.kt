package com.example.apitest.data_classes

import com.google.gson.annotations.SerializedName

data class TriviaResponse(
    @SerializedName("response_code") val responseCode: Int,
    val results: List<TriviaQuestion>
)


