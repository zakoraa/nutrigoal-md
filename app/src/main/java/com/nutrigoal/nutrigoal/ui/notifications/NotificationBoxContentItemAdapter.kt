package com.nutrigoal.nutrigoal.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nutrigoal.nutrigoal.databinding.NotificationBoxContentItemBinding

class NotificationBoxContentItemAdapter(private val items: List<NotificationBoxContentItem>) :
    RecyclerView.Adapter<NotificationBoxContentItemAdapter.NotificationBoxContentItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationBoxContentItemViewHolder {
        val binding =
            NotificationBoxContentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return NotificationBoxContentItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationBoxContentItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class NotificationBoxContentItemViewHolder(private val binding: NotificationBoxContentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotificationBoxContentItem) {
            with(binding) {
                tvTitle.text = item.title
                imageView.setImageResource(item.imageResId)
                tvTime.text = item.time
            }
        }
    }
}

data class NotificationBoxContentItem(
    val imageResId: Int,
    val title: String,
    val time: String
)