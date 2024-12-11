package com.c242pS371.nutrigoal.ui.plan_diet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.databinding.PlanDietDateItemBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateAdapter(
    private val items: List<DateItem>,
    private val viewPagerCallback: (Int) -> Unit
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private var selectedPosition = 0
    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setSelectedPosition(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousPosition)
        notifyItemChanged(selectedPosition)
        viewPagerCallback(selectedPosition)
    }

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
                setSelectedPosition(clickedPosition)
                onItemClickListener?.invoke(clickedPosition)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class DateViewHolder(private val binding: PlanDietDateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DateItem, isSelected: Boolean) {
            binding.apply {
                val calendar = Calendar.getInstance(Locale.ENGLISH).apply {
                    set(Calendar.MONTH, item.month - 1)
                }
                val monthFormat = SimpleDateFormat("MMM", Locale.ENGLISH)
                val monthName = monthFormat.format(calendar.time)

                tvDay.text = "${item.day}"
                tvMonth.text = monthName
                val context = itemView.context
                val color =
                    if (isSelected) context.getColor(R.color.primary) else context.getColor(R.color.grey)
                tvMonth.setTextColor(color)
                tvDay.setTextColor(color)
            }
        }
    }
}

data class DateItem(
    val month: Int,
    val day: Int
)
