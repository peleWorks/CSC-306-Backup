package com.example.apitest.data_classes

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TriviaQuestion(
    val type: String,
    val difficulty: String,
    val category: String,
    val question: String,
    @SerializedName("correct_answer") val correctAnswer: String,
    @SerializedName("incorrect_answers") val incorrectAnswers: List<String>
) : Serializable