package com.c242pS371.nutrigoal.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.c242pS371.nutrigoal.databinding.ProfileItemBinding

class ProfileItemAdapter(private val items: List<ProfileItem>) :
    RecyclerView.Adapter<ProfileItemAdapter.ProfileItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileItemViewHolder {
        val binding =
            ProfileItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ProfileItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class ProfileItemViewHolder(private val binding: ProfileItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProfileItem) {
            with(binding) {
                tvTitle.text = item.title
                tvDescription.text = item.description
            }
        }
    }
}

data class ProfileItem(
    val title: String,
    val description: String,
)