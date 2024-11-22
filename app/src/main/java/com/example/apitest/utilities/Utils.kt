package com.example.apitest.utilities

import android.app.Dialog
import android.content.Context
import android.widget.Toast
import com.example.apitest.R
import com.example.apitest.data_classes.UserModel

object Utils {

    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun showProgressBar(context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.loading_progress_bar)
        dialog.show()
        return dialog
    }

    fun desiredScore(userInfo: UserModel, type: String):Int{
        return when(type){
            Constants.allTimeScore-> userInfo.allTimeScore
            Constants.weeklyScore->userInfo.weeklyScore
            Constants.monthlyScore->userInfo.monthlyScore
            else -> 0
        }
    }
}