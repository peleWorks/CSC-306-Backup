package com.example.apitest.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.text.HtmlCompat
import com.example.apitest.R
import com.example.apitest.data_classes.Category

object Constants {

    val difficulties = listOf("Any", "Easy", "Medium", "Hard")
    val types = listOf("Any", "Multiple Choice", "True/false")
    const val user = "USER"
    const val allTimeScore = "allTimeScore"
    const val weeklyScore = "weeklyScore"
    const val monthlyScore = "monthlyScore"
    const val lastGameScore = "lastGameScore"

    fun getCategories(): ArrayList<Category> {
        val categoryList = listOf(
            Pair("9", Pair(R.drawable.general_knowledge, "General Knowledge")),
            Pair("10", Pair(R.drawable.books, "Books")),
            Pair("11", Pair(R.drawable.movies, "Film")),
            Pair("12", Pair(R.drawable.music, "Music")),
            Pair("13", Pair(R.drawable.musicals, "Musicals & Theatres")),
            Pair("14", Pair(R.drawable.television, "Television")),
            Pair("15", Pair(R.drawable.video_games, "Video Games")),
            Pair("16", Pair(R.drawable.board_game, "Board Games")),
            Pair("17", Pair(R.drawable.science, "Science & Nature")),
            Pair("18", Pair(R.drawable.computer, "Computers")),
            Pair("19", Pair(R.drawable.maths, "Mathematics")),
            Pair("20", Pair(R.drawable.mythology, "Mythology")),
            Pair("21", Pair(R.drawable.sports, "Sports")),
            Pair("22", Pair(R.drawable.geography, "Geography")),
            Pair("23", Pair(R.drawable.history, "History")),
            Pair("24", Pair(R.drawable.politics, "Politics")),
            Pair("25", Pair(R.drawable.art, "Art")),
            Pair("26", Pair(R.drawable.celebrities, "Celebrities")),
            Pair("27", Pair(R.drawable.animals, "Animals")),
            Pair("28", Pair(R.drawable.vehicles, "Vehicles")),
            Pair("29", Pair(R.drawable.comic, "Comics")),
            Pair("30", Pair(R.drawable.gadgets, "Gadgets")),
            Pair("31", Pair(R.drawable.anime, "Anime & Manga")),
            Pair("32", Pair(R.drawable.cartoon, "Cartoons & Animations"))
        )

        return ArrayList(categoryList.map { Category(it.first, it.second.first, it.second.second) })
    }

    fun randomizeOptions(
        correctAnswer: String,
        incorrectAnswers: List<String>
    ): Pair<String, List<String>> {
        val answersList = mutableListOf<String>()

        answersList.add(decodeString(correctAnswer))

        for (i in incorrectAnswers) {
            answersList.add(decodeString(i))
        }

        answersList.shuffle()
        return Pair(correctAnswer, answersList)
    }


    fun getCategoryArray(): List<String> {
        val categories = getCategories()
        val result = mutableListOf<String>()
        result.add("Any")
        for (i in categories)
            result.add(i.name)
        return result
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun decodeString(htmlEncoded: String): String {
        return HtmlCompat.fromHtml(htmlEncoded, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    }




}