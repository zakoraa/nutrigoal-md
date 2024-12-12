package com.c242pS371.nutrigoal.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.databinding.CardWeightProgressItemBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BodyWeightProgressAdapter(private val weightList: List<WeightProgress>) :
    RecyclerView.Adapter<BodyWeightProgressAdapter.BodyWeightProgressViewHolder>() {

    private var previousWeight: Float? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BodyWeightProgressViewHolder {
        val binding = CardWeightProgressItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BodyWeightProgressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BodyWeightProgressViewHolder, position: Int) {
        val weightProgress = weightList[position]
        holder.bind(weightProgress, previousWeight)
        previousWeight = weightProgress.bodyWeight
    }

    override fun getItemCount(): Int = weightList.size

    inner class BodyWeightProgressViewHolder(private val binding: CardWeightProgressItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WeightProgress, previousWeight: Float?) {
            with(binding) {
                val calendar = Calendar.getInstance(Locale.ENGLISH)
                val currentMonth = calendar.get(Calendar.MONTH) + 1
                val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
                calendar.set(Calendar.MONTH, item.month - 1)
                val monthFormat = SimpleDateFormat("MMM", Locale.ENGLISH)
                val monthName = monthFormat.format(calendar.time)
                tvDay.text = "${item.day}"
                tvMonth.text = monthName

                if (item.month == currentMonth && item.day == currentDay) {
                    tvMonth.setTextColor(binding.root.context.getColor(R.color.primary))
                    tvDay.setTextColor(binding.root.context.getColor(R.color.primary))
                } else {
                    tvMonth.setTextColor(binding.root.context.getColor(R.color.grey))
                    tvDay.setTextColor(binding.root.context.getColor(R.color.grey))
                }
                tvDayTitle.text = item.title
                tvBodyWeight.text = String.format("${item.bodyWeight} kg")

                val weightChange = calculateWeightChange(item.bodyWeight, previousWeight)

                tvWeightChange.text = weightChange.text
                tvWeightChange.setTextColor(weightChange.color)
            }
        }

        private fun calculateWeightChange(
            currentWeight: Float,
            previousWeight: Float?
        ): WeightChange {
            val context = itemView.context
            return if (previousWeight == null) {
                WeightChange("+0.0", context.getColor(R.color.error))
            } else {
                val difference = currentWeight - previousWeight
                if (currentWeight >= previousWeight) {
                    WeightChange("+${difference}", context.getColor(R.color.error))
                } else {
                    WeightChange("$difference", context.getColor(R.color.primary))
                }
            }
        }

    }

    data class WeightChange(val text: String, val color: Int)
}

data class WeightProgress(
    val month: Int,
    val day: Int,
    val title: String,
    val bodyWeight: Float,
)
