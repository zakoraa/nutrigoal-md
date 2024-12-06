package com.nutrigoal.nutrigoal.ui.plan_diet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.databinding.PlanDietDateItemBinding

class DateAdapter(private val items: List<DateItem>) :
    RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding =
            PlanDietDateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(items[position], position == selectedPosition)

        holder.itemView.setOnClickListener {
            val clickedPosition = holder.adapterPosition
            if (clickedPosition != RecyclerView.NO_POSITION) {
                val previousPosition = selectedPosition
                selectedPosition = clickedPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
            }
        }
    }


    override fun getItemCount(): Int = items.size

    inner class DateViewHolder(private val binding: PlanDietDateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DateItem, isSelected: Boolean) {
            with(binding) {
                tvMonth.text = item.month
                tvDay.text = item.day
                val context = itemView.context

                if (isSelected) {
                    tvMonth.setTextColor(context.getColor(R.color.primary))
                    tvDay.setTextColor(context.getColor(R.color.primary))
                } else {
                    tvMonth.setTextColor(context.getColor(R.color.grey))
                    tvDay.setTextColor(context.getColor(R.color.grey))
                }
            }
        }
    }
}

data class DateItem(
    val month: String,
    val day: String
)
