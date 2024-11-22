package com.example.apitest.data_classes

import java.io.Serializable

data class Result(
    val time: Int,
    val type: String,
    val difficulty: String,
    val score: Int
) : Serializable
