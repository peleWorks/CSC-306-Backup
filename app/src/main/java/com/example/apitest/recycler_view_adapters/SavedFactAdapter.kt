package com.example.apitest.recycler_view_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apitest.data_classes.SavedFact
import com.example.apitest.databinding.ItemSavedFactBinding

class SavedFactAdapter(private val facts: List<SavedFact>) :
    RecyclerView.Adapter<SavedFactAdapter.SavedFactViewHolder>() {

    class SavedFactViewHolder(private val binding: ItemSavedFactBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(fact: SavedFact) {
            binding.factNumber.text = "Fact ZAP #${fact.factNumber}"
            binding.factText.text = fact.factText
            binding.savedDate.text = fact.savedDate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedFactViewHolder {
        val binding = ItemSavedFactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavedFactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedFactViewHolder, position: Int) {
        holder.bind(facts[position])
    }

    override fun getItemCount() = facts.size
}
