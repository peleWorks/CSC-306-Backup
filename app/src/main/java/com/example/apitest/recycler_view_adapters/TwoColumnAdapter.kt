package com.example.apitest.recycler_view_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apitest.R
import com.example.apitest.data_classes.Category
import com.example.apitest.data_classes.CategoryStatistics

class TwoColumnAdapter(
    private val items: List<Category>,
    private val categoryStatistics: Map<String, CategoryStatistics>
) :
    RecyclerView.Adapter<TwoColumnAdapter.ViewHolder>() {

    private var pressed: OnPressed? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.category_ImageView)
        val categoryName: TextView = itemView.findViewById(R.id.category_name_TextView)
        val questionCount: TextView = itemView.findViewById(R.id.num_of_questions_TextView)
    }

    interface OnPressed {
        fun onClick(id: Int)
    }

    fun setOnPressed(onPressed: OnPressed) {
        this.pressed = onPressed
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TwoColumnAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: TwoColumnAdapter.ViewHolder, position: Int) {
        val item = items[position]
        val numberOfQuestions = categoryStatistics[item.id]?.totalNumOfQuestions
        viewHolder.image.setImageResource(item.image)
        viewHolder.categoryName.text = item.name
        viewHolder.questionCount.text =
            viewHolder.itemView.context.getString(R.string.num_of_questions, numberOfQuestions)
        viewHolder.itemView.setOnClickListener {
            if (pressed != null) {
                pressed!!.onClick(item.id.toInt())
            }
        }

    }

}