package com.example.apitest.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.apitest.R
import com.example.apitest.data_classes.UserModel
import com.example.apitest.databinding.ActivityDailyQuizNotificationBinding
import com.example.apitest.firebase.FirebaseSetup
import com.example.apitest.notifications.NotificationScheduler

class DailyQuizNotificationActivity : AppCompatActivity() {
    private var binding: ActivityDailyQuizNotificationBinding? = null
    private val firebaseSetup = FirebaseSetup()
    private val notificationScheduler = NotificationScheduler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyQuizNotificationBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarTitle?.text = getString(R.string.daily_quiz_notification_time_Toolbar)

        // Create notification channel
        notificationScheduler.createNotificationChannel(this)

        // Set up notification time selection
        binding?.saveNotificationTime?.setOnClickListener {
            val selectedHour = when (binding?.notificationTimeGroup?.checkedRadioButtonId) {
                R.id.time_8am -> 8
                R.id.time_10am -> 10
                R.id.time_12pm -> 12
                R.id.time_2pm -> 14
                R.id.time_4pm -> 16
                R.id.time_6pm -> 18
                R.id.time_8pm -> 20
                R.id.when_available -> NotificationScheduler.WHEN_AVAILABLE
                else -> return@setOnClickListener
            }

            // Schedule notification
            notificationScheduler.scheduleNotification(this, selectedHour)

            // Save preference to user profile
            firebaseSetup.getUserInfo(object : FirebaseSetup.UserInfoCallback {
                override fun onUserInfoFetched(userInfo: UserModel?) {
                    userInfo?.let {
                        it.notificationTime = selectedHour
                        firebaseSetup.updateUserPreferences(it)
                    }
                }
            })

            finish()
        }

        // Load saved preference
        firebaseSetup.getUserInfo(object : FirebaseSetup.UserInfoCallback {
            override fun onUserInfoFetched(userInfo: UserModel?) {
                userInfo?.let {
                    val radioButtonId = when (it.notificationTime) {
                        8 -> R.id.time_8am
                        10 -> R.id.time_10am
                        12 -> R.id.time_12pm
                        14 -> R.id.time_2pm
                        16 -> R.id.time_4pm
                        18 -> R.id.time_6pm
                        20 -> R.id.time_8pm
                        NotificationScheduler.WHEN_AVAILABLE -> R.id.when_available
                        else -> null
                    }
                    radioButtonId?.let { id ->
                        binding?.notificationTimeGroup?.check(id)
                    }
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}