package com.example.apitest.firebase

import android.util.Log
import com.example.apitest.utilities.Constants
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Calendar
import java.util.Date

class ScoreManagement {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun checkAndResetScores(callback: () -> Unit) {
        // Get the current time
        val calendar = Calendar.getInstance()
        val currentTime = calendar.time

        // Check if we need to reset weekly scores (every Monday)
        val isMonday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
        val isBeforeNoonOnMonday = isMonday && calendar.get(Calendar.HOUR_OF_DAY) < 12

        // Check if we need to reset monthly scores (1st of each month)
        val isFirstOfMonth = calendar.get(Calendar.DAY_OF_MONTH) == 1
        val isBeforeNoonOnFirstDay = isFirstOfMonth && calendar.get(Calendar.HOUR_OF_DAY) < 12

        // Get the last reset times from a special document
        mFireStore.collection("system")
            .document("scoreResets")
            .get()
            .addOnSuccessListener { document ->
                val lastWeeklyReset = document?.getTimestamp("lastWeeklyReset")?.toDate()
                val lastMonthlyReset = document?.getTimestamp("lastMonthlyReset")?.toDate()

                var needsWeeklyReset = false
                var needsMonthlyReset = false

                // Check if weekly reset is needed
                if (isMonday) {
                    if (lastWeeklyReset == null) {
                        needsWeeklyReset = true
                    } else {
                        val lastResetCal = Calendar.getInstance()
                        lastResetCal.time = lastWeeklyReset
                        needsWeeklyReset = calendar.get(Calendar.WEEK_OF_YEAR) !=
                                lastResetCal.get(Calendar.WEEK_OF_YEAR) ||
                                calendar.get(Calendar.YEAR) !=
                                lastResetCal.get(Calendar.YEAR)
                    }
                }

                // Check if monthly reset is needed
                if (isFirstOfMonth) {
                    if (lastMonthlyReset == null) {
                        needsMonthlyReset = true
                    } else {
                        val lastResetCal = Calendar.getInstance()
                        lastResetCal.time = lastMonthlyReset
                        needsMonthlyReset = calendar.get(Calendar.MONTH) !=
                                lastResetCal.get(Calendar.MONTH) ||
                                calendar.get(Calendar.YEAR) !=
                                lastResetCal.get(Calendar.YEAR)
                    }
                }

                if (needsWeeklyReset || needsMonthlyReset) {
                    performReset(needsWeeklyReset, needsMonthlyReset, currentTime, callback)
                } else {
                    callback()
                }
            }
            .addOnFailureListener {
                callback()
            }
    }

    private fun performReset(weeklyReset: Boolean, monthlyReset: Boolean, currentTime: Date, callback: () -> Unit) {
        // Start a batch write
        val batch = mFireStore.batch()

        // Update the reset timestamps
        val systemRef = mFireStore.collection("system").document("scoreResets")
        val updates = mutableMapOf<String, Any>()

        if (weeklyReset) {
            updates["lastWeeklyReset"] = Timestamp(currentTime)
        }
        if (monthlyReset) {
            updates["lastMonthlyReset"] = Timestamp(currentTime)
        }

        batch.set(systemRef, updates, SetOptions.merge())

        // Get all users and reset their scores
        mFireStore.collection(Constants.user)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docRef = mFireStore.collection(Constants.user).document(document.id)
                    val userUpdates = mutableMapOf<String, Any>()

                    if (weeklyReset) {
                        userUpdates[Constants.weeklyScore] = 0
                    }
                    if (monthlyReset) {
                        userUpdates[Constants.monthlyScore] = 0
                    }

                    batch.update(docRef, userUpdates)
                }

                // Commit all the changes
                batch.commit()
                    .addOnSuccessListener {
                        callback()
                    }
                    .addOnFailureListener { e ->
                        Log.e("ScoreManagement", "Error resetting scores", e)
                        callback()
                    }
            }
    }

    // Call this once to initialize the system document
    fun initializeSystemDocument() {
        val currentTime = Timestamp(Date())
        mFireStore.collection("system")
            .document("scoreResets")
            .set(mapOf(
                "lastWeeklyReset" to currentTime,
                "lastMonthlyReset" to currentTime
            ), SetOptions.merge())
    }
}