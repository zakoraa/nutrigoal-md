package com.nutrigoal.nutrigoal.ui.settings

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.databinding.SettingBoxContentItemBinding
import com.nutrigoal.nutrigoal.ui.auth.LoginActivity
import com.nutrigoal.nutrigoal.ui.profile.ProfileActivity

class SettingBoxContentItemAdapter(private val items: List<SettingBoxContentItem>) :
    RecyclerView.Adapter<SettingBoxContentItemAdapter.SettingsBoxContentItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SettingsBoxContentItemViewHolder {
        val binding =
            SettingBoxContentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SettingsBoxContentItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SettingsBoxContentItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class SettingsBoxContentItemViewHolder(private val binding: SettingBoxContentItemBinding) :
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

                    val context = itemView.context

                    when (item.title) {
                        getString(context, R.string.profile) -> {
                            itemView.setOnClickListener {
                                val intent = Intent(context, ProfileActivity::class.java)
                                context.startActivity(intent)
                            }
                        }

                        getString(context, R.string.logout) -> {
                            itemView.setOnClickListener {
                                val intent = Intent(context, LoginActivity::class.java).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                context.startActivity(intent)
                            }
                        }
                    }


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