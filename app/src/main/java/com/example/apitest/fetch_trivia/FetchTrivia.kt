package com.example.apitest.fetch_trivia

import android.content.Context
import android.content.Intent
import com.example.apitest.activities.TriviaActivity
import com.example.apitest.data_classes.CategoryStatistics
import com.example.apitest.data_classes.QuestionStatistics
import com.example.apitest.data_classes.TriviaResponse
import com.example.apitest.retrofit_interfaces.QuestionStatisticsService
import com.example.apitest.retrofit_interfaces.TriviaService
import com.example.apitest.utilities.Constants
import com.example.apitest.utilities.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FetchTrivia(private val context: Context) {

    fun getQuiz(amount: Int, category: Int?, difficulty: String?, type: String?) {

        if (Constants.isNetworkAvailable(context)) {
            val pbDialog = Utils.showProgressBar(context)

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://opentdb.com/")
                .addConverterFactory(GsonConverterFactory.create()).build()

            val service: TriviaService = retrofit.create(TriviaService::class.java)

            val getDataCall: Call<TriviaResponse> = service.getQuiz(
                amount, category, difficulty, type
            )

            getDataCall.enqueue(object : Callback<TriviaResponse> {
                override fun onResponse(
                    call: Call<TriviaResponse>,
                    response: Response<TriviaResponse>
                ) {
                    pbDialog.cancel()
                    if (response.isSuccessful) {
                        val responseData: TriviaResponse = response.body()!!
                        val questionList = ArrayList(responseData.results)

                        if (questionList.isNotEmpty()) {
                            val intent = Intent(context, TriviaActivity::class.java)
                            intent.putExtra("questionList", questionList)
                            context.startActivity(intent)
                        } else {
                            Utils.showToast(
                                context, "Sorry, we don't have $amount questions available" +
                                        " for this topic."
                            )
                        }

                    } else {
                        Utils.showToast(context, "Response Failed")
                    }
                }

                override fun onFailure(call: Call<TriviaResponse>, t: Throwable) {
                    pbDialog.cancel()
                    Utils.showToast(context, "Failure in response")
                }

            })
        } else {
            Utils.showToast(context, "Network is not available")
        }

    }

    fun getQuestionStatsList(callBack: QuestionStatCallback) {
        if (Constants.isNetworkAvailable(context)) {
            val pbDialog = Utils.showProgressBar(context)
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://opentdb.com/")
                .addConverterFactory(GsonConverterFactory.create()).build()

            val service: QuestionStatisticsService =
                retrofit.create(QuestionStatisticsService::class.java)
            val getDataCall: Call<QuestionStatistics> = service.getData()

            getDataCall.enqueue(object : Callback<QuestionStatistics> {
                override fun onResponse(
                    call: Call<QuestionStatistics>,
                    response: Response<QuestionStatistics>
                ) {
                    pbDialog.cancel()
                    if (response.isSuccessful) {
                        val questionStatistics: QuestionStatistics = response.body()!!
                        val categoryMap = questionStatistics.categories
                        callBack.onQuestionStatFetched(categoryMap)

                    } else {
                        Utils.showToast(context, "Error Code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<QuestionStatistics>, response: Throwable) {
                    pbDialog.cancel()
                    Utils.showToast(context, "API Call Failure")
                }

            })
        } else {
            Utils.showToast(context, "Network is Not Available")
        }
    }

    interface QuestionStatCallback {
        fun onQuestionStatFetched(map: Map<String, CategoryStatistics>)
    }








}