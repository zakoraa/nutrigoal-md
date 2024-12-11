package com.c242pS371.nutrigoal.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.data.local.entity.NotificationType
import com.c242pS371.nutrigoal.databinding.NotificationBoxContentItemBinding

class NotificationBoxContentItemAdapter(
    private val items: List<NotificationBoxContentItem>,
    private val notificationsViewModel: NotificationsViewModel
) :
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

                val context = itemView.context

                if (item.notificationType == NotificationType.TIME_TO_EAT) {
                    btnDone.setOnClickListener {
                        notificationsViewModel.updateNotificationAsConfirmed(item.id)
                    }
                } else {
                    root.setOnClickListener {
                        notificationsViewModel.updateNotificationAsConfirmed(item.id)
                    }
                }

                if (!item.isConfirmed) {
                    notifSign.visibility = View.VISIBLE
                    if (item.notificationType == NotificationType.TIME_TO_EAT) {
                        btnDone.visibility = View.VISIBLE
                    }
                    root.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.primary_30
                        )
                    )
                } else {
                    notifSign.visibility = View.GONE
                    if (item.notificationType == NotificationType.TIME_TO_EAT) {
                        btnDone.visibility = View.GONE
                    }
                    root.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.background
                        )
                    )
                }
            }
        }
    }
}

data class NotificationBoxContentItem(
    val id: Int,
    val imageResId: Int,
    val title: String,
    val time: String,
    val notificationType: NotificationType,
    val isConfirmed: Boolean
)