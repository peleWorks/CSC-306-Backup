package com.example.apitest.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.apitest.data_classes.UserModel
import com.example.apitest.firebase.FirebaseSetup
import java.time.LocalDate

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val checkAvailability = intent.getBooleanExtra("checkAvailability", false)

        if (checkAvailability) {
            checkQuizAvailability(context)
        } else {
            showNotification(context)
        }
    }

    private fun checkQuizAvailability(context: Context) {
        val firebaseSetup = FirebaseSetup()
        firebaseSetup.getUserInfo(object : FirebaseSetup.UserInfoCallback {
            override fun onUserInfoFetched(userInfo: UserModel?) {
                userInfo?.let { user ->
                    val currentDate = LocalDate.now().toString()
                    if (user.lastAttemptDate != currentDate) {
                        showNotification(context)
                    }
                }
            }
        })
    }

    private fun showNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, NotificationScheduler.CHANNEL_ID)
            .setContentTitle("Daily Quiz Available!")
            .setContentText("Your daily quiz is now available. Test your knowledge!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NotificationScheduler.NOTIFICATION_ID, notification)
    }
}