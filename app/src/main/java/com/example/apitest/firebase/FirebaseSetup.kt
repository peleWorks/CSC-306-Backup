package com.example.apitest.firebase

import android.graphics.BitmapFactory
import android.util.Log
import com.example.apitest.R
import com.example.apitest.data_classes.LeaderBoardModel
import com.example.apitest.data_classes.SavedFact
import com.example.apitest.data_classes.UserModel
import com.example.apitest.utilities.Constants
import com.google.android.gms.tasks.Task
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class FirebaseSetup {

    private val mFireStore = FirebaseFirestore.getInstance()


    fun registerUser(userInfo: UserModel) {
        mFireStore.collection(Constants.user)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
    }



    fun getUserInfo(callback: UserInfoCallback) {
        val userDocument = FirebaseFirestore.getInstance().collection(Constants.user).document(getCurrentUserId())

        userDocument.get().addOnSuccessListener { documentSnapshot ->
            val userInfo = documentSnapshot.toObject(UserModel::class.java)
            callback.onUserInfoFetched(userInfo)
        }.addOnFailureListener { e ->
            // Handle the error here
            callback.onUserInfoFetched(null)
        }
    }




    fun getCurrentUserId(): String {
        val currentUser = Firebase.auth.currentUser
        var currentUserId = ""
        if (currentUser != null)
            currentUserId = currentUser.uid
        return currentUserId
    }


    fun setProfileImage(imageRef: String?, view: ShapeableImageView) {
        if (!imageRef.isNullOrEmpty()) {
            // Check if the imageRef is for an avatar (avatar_1 to avatar_7)
            if (imageRef.startsWith("avatar_")) {
                // Get the drawable resource ID
                val resourceId = view.context.resources.getIdentifier(
                    imageRef, // avatar_1, avatar_2, etc.
                    "drawable",
                    view.context.packageName
                )
                if (resourceId != 0) {
                    view.setImageResource(resourceId)
                } else {
                    // Set a default avatar if the resource is not found
                    view.setImageResource(R.drawable.default_picture)
                }
            } else {
                // Handle Firebase Storage images
                val storageRef = FirebaseStorage.getInstance().reference
                val pathReference = storageRef.child(imageRef)
                val ONE_MEGABYTE: Long = 1024 * 1024
                pathReference.getBytes(ONE_MEGABYTE)
                    .addOnSuccessListener { byteArray ->
                        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                        view.setImageBitmap(bmp)
                    }
                    .addOnFailureListener {
                        // Set default avatar on failure
                        view.setImageResource(R.drawable.default_picture)
                    }
            }
        } else {
            // Set default avatar if imageRef is null or empty
            view.setImageResource(R.drawable.default_picture)
        }
    }





    fun updateScore(newScore: Int) {
        val userDocument = mFireStore.collection(Constants.user).document(getCurrentUserId())
        getUserInfo(object : UserInfoCallback {
            override fun onUserInfoFetched(userInfo: UserModel?) {
                userInfo?.let {
                    val newAllTimeScore = userInfo.allTimeScore + newScore
                    val newWeeklyScore = userInfo.weeklyScore + newScore
                    val newMonthlyScore = userInfo.monthlyScore + newScore
                    userDocument.update(
                        Constants.allTimeScore, newAllTimeScore,
                        Constants.weeklyScore, newWeeklyScore,
                        Constants.monthlyScore, newMonthlyScore,
                        Constants.lastGameScore, newScore
                    ).addOnSuccessListener {
                        Log.e("DataUpdate", "Updated")
                    }.addOnFailureListener {
                        Log.e("DataUpdate", "Failed")
                    }
                }
            }
        })
    }



    fun getUserRank(type: String,callback:UserRankCallback){
        var rank: Int? = null
        mFireStore.collection(Constants.user).orderBy(type, Query.Direction.DESCENDING)
            .get().addOnSuccessListener { result ->
                rank = 1
                val usrId = getCurrentUserId()
                for (document in result) {
                    if (document.id == usrId)
                        break
                    rank = rank!! + 1
                }
                callback.onUserRankFetched(rank)
            }.addOnFailureListener {
                Log.e("QueryResult", "Failure")
                callback.onUserRankFetched(null)
            }
    }

    fun getLeaderBoardData(type: String, callBack: LeaderBoardDataCallback) {

        ScoreManagement().checkAndResetScores {
            mFireStore.collection(Constants.user)
                .orderBy(type, Query.Direction.DESCENDING)
                .get().addOnSuccessListener { result ->
                    val allRanks = mutableListOf<UserModel?>()
                    for (document in result) {
                        val userInfo = document.toObject(UserModel::class.java)
                        allRanks.add(userInfo)
                    }
                    callBack.onLeaderBoardDataFetched(
                        LeaderBoardModel(allRanks)
                    )
                }.addOnFailureListener {
                    callBack.onLeaderBoardDataFetched(null)
                }
        }
    }


    fun doesDocumentExist(documentId: String): Task<Boolean> {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection(Constants.user).document(documentId)

        return documentRef.get()
            .continueWith { task ->
                if (task.isSuccessful) {
                    task.result?.exists() ?: false
                } else {
                    // Handle the error
                    false
                }
            }
    }

    fun updateUserPreferences(userInfo: UserModel) {
        mFireStore.collection(Constants.user)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("FirebaseSetup", "User preferences updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseSetup", "Error updating user preferences", e)
            }
    }

    fun saveFact(factNumber: Int, factText: String) {
        val userDocument = mFireStore.collection(Constants.user).document(getCurrentUserId())

        getUserInfo(object : UserInfoCallback {
            override fun onUserInfoFetched(userInfo: UserModel?) {
                userInfo?.let {
                    val currentDate = java.text.SimpleDateFormat("dd MMM yyyy",
                        java.util.Locale.getDefault()).format(java.util.Date())

                    val newFact = SavedFact(factNumber, factText, currentDate)
                    val updatedFacts = (userInfo.savedFacts + newFact)

                    userDocument.update("savedFacts", updatedFacts)
                        .addOnSuccessListener {
                            Log.d("FirebaseSetup", "Fact saved successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirebaseSetup", "Error saving fact", e)
                        }
                }
            }
        })
    }


    interface UserInfoCallback {
        fun onUserInfoFetched(userInfo: UserModel?)
    }

    interface UserRankCallback {
        fun onUserRankFetched(rank:Int?)
    }

    interface LeaderBoardDataCallback{
        fun onLeaderBoardDataFetched(leaderBoardModel: LeaderBoardModel?)
    }





}