package com.example.apitest.recycler_view_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.apitest.R
import com.example.apitest.data_classes.UserModel
import com.example.apitest.firebase.FirebaseSetup
import com.example.apitest.utilities.Utils
import com.google.android.material.imageview.ShapeableImageView

class LeaderBoardAdapter(private val itemList: List<UserModel?>, private val type:String) :
    RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRank: TextView = itemView.findViewById(R.id.tvLeaderboardRank)
        val ivImage: ShapeableImageView = itemView.findViewById(R.id.ivLeaderBoardProfilePic)
        val tvName: TextView = itemView.findViewById(R.id.tvLeaderboardName)
        val tvPoints: TextView = itemView.findViewById(R.id.tvLeaderboardScore)
        val cardView: CardView = itemView.findViewById(R.id.CardView_leaderboard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.leader_board_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userInfo = itemList[position]

        userInfo?.let { user ->

            holder.tvRank.text = (position + 1).toString()


            holder.tvName.text = user.name
            holder.tvPoints.text = String.format("%d", Utils.desiredScore(user, type))

            FirebaseSetup().setProfileImage(user.image, holder.ivImage)


            when (position) {
                0 -> {
                    holder.cardView.setCardBackgroundColor(
                        ContextCompat.getColor(holder.itemView.context, R.color.gold))
                    holder.tvPoints.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                }
                1 -> {
                    holder.cardView.setCardBackgroundColor(
                        ContextCompat.getColor(holder.itemView.context, R.color.silver))
                    holder.tvPoints.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                }
                2 -> {
                    holder.cardView.setCardBackgroundColor(
                        ContextCompat.getColor(holder.itemView.context, R.color.bronze))
                    holder.tvPoints.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                }
                else -> {
                    holder.cardView.setCardBackgroundColor(
                        ContextCompat.getColor(holder.itemView.context, R.color.background_secondary)
                    )
                }
            }
        }
    }
}