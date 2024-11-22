package com.example.apitest.recycler_view_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apitest.data_classes.Achievement
import com.example.apitest.databinding.ItemAchievementBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AchievementAdapter : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {
    private var achievements = listOf<Achievement>()

    inner class AchievementViewHolder(private val binding: ItemAchievementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(achievement: Achievement) {
            binding.apply {
                achievementIcon.setImageResource(achievement.iconResId)
                achievementTitle.text = achievement.title
                achievementDescription.text = achievement.description


                if (achievement.unlocked) {
                    val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                        .format(Date(achievement.unlockedDate))
                    achievementDate.text = "Unlocked: $dateStr"
                    achievementDate.visibility = View.VISIBLE
                } else {
                    achievementDate.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(achievements[position])
    }

    override fun getItemCount() = achievements.size

    fun updateAchievements(newAchievements: List<Achievement>) {
        achievements = newAchievements.filter { it.unlocked }
        notifyDataSetChanged()
    }
}


