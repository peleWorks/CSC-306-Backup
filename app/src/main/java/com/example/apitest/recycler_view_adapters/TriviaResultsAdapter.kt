package com.example.apitest.recycler_view_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apitest.R
import com.example.apitest.data_classes.Result
import java.util.Locale

class TriviaResultsAdapter(private val list: ArrayList<Result>) :
    RecyclerView.Adapter<TriviaResultsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionNumber: TextView = view.findViewById(R.id.question_number)
        val questionTime: TextView = view.findViewById(R.id.question_time)
        val questionType: TextView = view.findViewById(R.id.question_type)
        val questionDifficulty: TextView = view.findViewById(R.id.question_difficulty)
        val questionScore: TextView = view.findViewById(R.id.question_score)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.questionNumber.text = String.format(
            Locale.getDefault(),
            "%d.", position + 1
        )

        holder.questionTime.text = String.format(
            Locale.getDefault(),
            "%d", item.time
        )

        holder.questionType.text = when (item.type) {
            "boolean" -> "true/false"
            else -> item.type
        }

        holder.questionDifficulty.text = item.difficulty
        holder.questionScore.text = String.format(Locale.getDefault(), "%d", item.score)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.question_stats, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}