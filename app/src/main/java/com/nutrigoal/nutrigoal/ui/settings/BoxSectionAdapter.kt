package com.nutrigoal.nutrigoal.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nutrigoal.nutrigoal.databinding.SettingBoxItemBinding

class BoxSectionAdapter(private val sections: List<BoxSection>) :
    RecyclerView.Adapter<BoxSectionAdapter.BoxSectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoxSectionViewHolder {
        val binding = SettingBoxItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BoxSectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoxSectionViewHolder, position: Int) {
        val section = sections[position]
        holder.bind(section)
    }

    override fun getItemCount(): Int = sections.size

    inner class BoxSectionViewHolder(private val binding: SettingBoxItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(section: BoxSection) {
            binding.tvTitleSection.text = section.title

            val adapter = SettingBoxContentItemAdapter(section.boxSections)
            binding.recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.recyclerView.adapter = adapter
        }
    }
}


data class BoxSection(
    val title: String,
    val boxSections: List<SettingBoxContentItem>
)