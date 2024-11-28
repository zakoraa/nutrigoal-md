package com.nutrigoal.nutrigoal.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.databinding.SettingBoxContentItemBinding

class SettingBoxContentItemAdapter(private val items: List<SettingBoxContentItem>) :
    RecyclerView.Adapter<SettingBoxContentItemAdapter.ProfileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val binding =
            SettingBoxContentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class ProfileViewHolder(private val binding: SettingBoxContentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SettingBoxContentItem) {
            with(binding) {
                tvTitle.text = item.title
                toggleButton.isChecked = item.isToggleButton
                endIcon.setImageResource(item.endIconResId)

                if (item.isToggleButton) {
                    toggleButton.visibility = View.VISIBLE
                    endIcon.visibility = View.GONE
                } else {
                    toggleButton.visibility = View.GONE
                    endIcon.visibility = View.VISIBLE
                }
            }

        }
    }
}

data class SettingBoxContentItem(
    val title: String,
    var isToggleButton: Boolean = false,
    val endIconResId: Int = R.drawable.ic_arrow_forward
)