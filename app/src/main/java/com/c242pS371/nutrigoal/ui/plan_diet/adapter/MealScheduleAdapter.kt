package com.c242pS371.nutrigoal.ui.plan_diet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.c242pS371.nutrigoal.databinding.MealScheduleItemBinding

class MealScheduleAdapter(
    private val items: List<String?>
) : RecyclerView.Adapter<MealScheduleAdapter.MealScheduleViewHolder>() {

    inner class MealScheduleViewHolder(val binding: MealScheduleItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealScheduleViewHolder {
        val binding = MealScheduleItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MealScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealScheduleViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            if (item !== null) {
                tvTime.text = item
            } else {
                tvTime.visibility = View.GONE
                ivClock.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = items.size
}

