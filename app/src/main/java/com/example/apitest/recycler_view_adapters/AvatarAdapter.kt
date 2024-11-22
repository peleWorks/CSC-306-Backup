package com.example.apitest.recycler_view_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apitest.R
import com.example.apitest.databinding.ItemAvatarBinding

class AvatarAdapter(
    private var selectedAvatar: String,
    private val onAvatarSelected: (String) -> Unit
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    private val avatars = listOf(
        "avatar_1", "avatar_2", "avatar_3", "avatar_4",
        "avatar_5", "avatar_6", "avatar_7"
    )

    inner class AvatarViewHolder(val binding: ItemAvatarBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val binding = ItemAvatarBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AvatarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val avatar = avatars[position]
        val context = holder.itemView.context

        // Set the avatar image
        val resourceId = context.resources.getIdentifier(
            avatar, "drawable", context.packageName
        )
        holder.binding.avatarImage.setImageResource(resourceId)

        // Set background based on selection
        holder.binding.avatarContainer.setBackgroundResource(
            if (avatar == selectedAvatar) R.drawable.selected_avatar_background
            else android.R.color.transparent
        )

        holder.itemView.setOnClickListener {
            val previousSelected = selectedAvatar
            selectedAvatar = avatar
            onAvatarSelected(avatar)

            // Update the UI
            notifyItemChanged(avatars.indexOf(previousSelected))
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = avatars.size
}