package com.example.apitest.fetch_trivia

import android.content.Context
import android.content.SharedPreferences
import com.example.apitest.data_classes.FactResponse
import com.example.apitest.retrofit_interfaces.FactService
import com.example.apitest.utilities.Constants
import com.example.apitest.utilities.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FetchFact(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "FactPreferences",
        Context.MODE_PRIVATE
    )

    private fun getAndIncrementFactNumber(): Int {
        val currentNumber = sharedPreferences.getInt("factCounter", 0)
        sharedPreferences.edit().putInt("factCounter", currentNumber + 1).apply()
        return currentNumber + 1
    }

    fun getFact(callback: FactCallback) {
        if (Constants.isNetworkAvailable(context)) {
            val pbDialog = Utils.showProgressBar(context)

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://api.api-ninjas.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: FactService = retrofit.create(FactService::class.java)
            val getDataCall: Call<List<FactResponse>> = service.getFact()

            getDataCall.enqueue(object : Callback<List<FactResponse>> {
                override fun onResponse(
                    call: Call<List<FactResponse>>,
                    response: Response<List<FactResponse>>
                ) {
                    pbDialog.cancel()
                    if (response.isSuccessful) {
                        val factList = response.body()
                        if (!factList.isNullOrEmpty()) {
                            val factNumber = getAndIncrementFactNumber()
                            callback.onFactFetched(factNumber, factList[0].fact)
                        } else {
                            Utils.showToast(context, "No fact received")
                        }
                    } else {
                        Utils.showToast(context, "Error Code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<FactResponse>>, t: Throwable) {
                    pbDialog.cancel()
                    Utils.showToast(context, "API Call Failure")
                }
            })
        } else {
            Utils.showToast(context, "Network is Not Available")
        }
    }

    interface FactCallback {
        fun onFactFetched(factNumber: Int, fact: String)
    }
}