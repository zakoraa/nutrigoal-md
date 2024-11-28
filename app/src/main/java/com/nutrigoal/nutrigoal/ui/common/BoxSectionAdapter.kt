package com.nutrigoal.nutrigoal.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nutrigoal.nutrigoal.databinding.BoxSectionItemBinding
import com.nutrigoal.nutrigoal.ui.notifications.NotificationBoxContentItem
import com.nutrigoal.nutrigoal.ui.notifications.NotificationBoxContentItemAdapter
import com.nutrigoal.nutrigoal.ui.settings.SettingBoxContentItem
import com.nutrigoal.nutrigoal.ui.settings.SettingBoxContentItemAdapter

class BoxSectionAdapter<T>(private val sections: List<BoxSection<T>>) :
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
                            (SettingBoxContentItemAdapter(items as List<SettingBoxContentItem>))
                        }

                        items.isNotEmpty() && items[0] is NotificationBoxContentItem -> {
                            @Suppress("UNCHECKED_CAST")
                            (NotificationBoxContentItemAdapter(items as List<NotificationBoxContentItem>))
                        }

                        else -> throw IllegalArgumentException("Unsupported item type in BoxSection")
                    }
                }
                recyclerView.layoutManager = LinearLayoutManager(root.context)
                recyclerView.adapter = adapter
            }

        }
    }
}

data class BoxSection<T>(
    val title: String,
    val boxSections: List<T>
)