package com.example.apitest.data_classes

import com.google.gson.annotations.SerializedName

data class CategoryStatistics(
    @SerializedName("total_num_of_questions") val totalNumOfQuestions: Int,
    @SerializedName("total_num_of_pending_questions") val totalNumOfPendingQuestions: Int,
    @SerializedName("total_num_of_verified_questions") val totalNumOfVerifiedQuestions: Int,
    @SerializedName("total_num_of_rejected_questions") val totalNumOfRejectedQuestions: Int
)