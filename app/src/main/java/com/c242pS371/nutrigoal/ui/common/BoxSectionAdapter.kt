package com.c242pS371.nutrigoal.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.c242pS371.nutrigoal.databinding.BoxSectionItemBinding
import com.c242pS371.nutrigoal.ui.auth.AuthViewModel
import com.c242pS371.nutrigoal.ui.notifications.NotificationBoxContentItem
import com.c242pS371.nutrigoal.ui.notifications.NotificationBoxContentItemAdapter
import com.c242pS371.nutrigoal.ui.notifications.NotificationsViewModel
import com.c242pS371.nutrigoal.ui.settings.SettingBoxContentItem
import com.c242pS371.nutrigoal.ui.settings.SettingBoxContentItemAdapter
import com.c242pS371.nutrigoal.ui.settings.SettingsViewModel

class BoxSectionAdapter<T>(
    private val sections: List<BoxSection<T>>,
    private val viewModel: AuthViewModel,
    private val settingsViewModel: SettingsViewModel,
    private val notificationsViewModel: NotificationsViewModel,
    private val historyViewModel: HistoryViewModel,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<BoxSectionAdapter<T>.BoxSectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoxSectionViewHolder {
        val binding = BoxSectionItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BoxSectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoxSectionViewHolder, position: Int) {
        val section = sections[position]
        holder.bind(section)
    }

    override fun getItemCount(): Int = sections.size

    inner class BoxSectionViewHolder(private val binding: BoxSectionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(section: BoxSection<T>) {
            with(binding) {
                tvTitleSection.text = section.title

                val adapter = when (val items = section.boxSections) {
                    else -> when {
                        items.isNotEmpty() && items[0] is SettingBoxContentItem -> {
                            @Suppress("UNCHECKED_CAST")
                            (SettingBoxContentItemAdapter(
                                items as List<SettingBoxContentItem>,
                                viewModel,
                                settingsViewModel,
                                historyViewModel,
                                lifecycleOwner,
                            ))
                        }

                        items.isNotEmpty() && items[0] is NotificationBoxContentItem -> {
                            @Suppress("UNCHECKED_CAST")
                            (NotificationBoxContentItemAdapter(items as List<NotificationBoxContentItem>, notificationsViewModel ))
                        }

                        else -> throw IllegalArgumentException("Unsupported item type in BoxSection")
                    }
                }
                recyclerView.adapter = adapter
                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = object : LinearLayoutManager(root.context) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
            }

        }
    }
}

data class BoxSection<T>(
    val title: String,
    val boxSections: List<T>
)