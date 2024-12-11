package com.c242pS371.nutrigoal.ui.plan_diet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.data.remote.entity.FoodRecommendationItem
import com.c242pS371.nutrigoal.data.remote.entity.PerDayItem
import com.c242pS371.nutrigoal.databinding.RecommendationFoodCardBinding
import com.c242pS371.nutrigoal.utils.ToastUtil
import java.util.Locale

class RecommendationFoodAdapter(
    private val context: Context,
    private val perDay: PerDayItem?,
    private val foodList: List<FoodRecommendationItem>,
    private val selectedRecommendationFood: MutableList<FoodRecommendationItem>
) : RecyclerView.Adapter<RecommendationFoodAdapter.RecommendationFoodViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendationFoodViewHolder {
        val binding =
            RecommendationFoodCardBinding.inflate(LayoutInflater.from(context), parent, false)
        return RecommendationFoodViewHolder(binding)
    }

    override fun getItemCount(): Int = foodList.size

    override fun onBindViewHolder(holder: RecommendationFoodViewHolder, position: Int) {
        val foodItem = foodList[position]
        holder.bind(foodItem)
    }

    inner class RecommendationFoodViewHolder(private val binding: RecommendationFoodCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(foodItem: FoodRecommendationItem?) {
            binding.apply {
                val context = itemView.context
                tvName.text = foodItem?.name?.split(" ")?.joinToString(" ") { text ->
                    text.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                }
                tvCaloriesValue.text = "${foodItem?.calories}"

                val maximumSelect = when (perDay?.activityLevel) {
                    1 -> 2
                    2 -> 2
                    3 -> 3
                    4 -> 3
                    5 -> 3
                    else -> 2
                }

                val isSelected = selectedRecommendationFood.contains(foodItem)
                updateButtonState(isSelected, btnSelect)

                btnSelect.setOnClickListener {
                    if (selectedRecommendationFood.size == maximumSelect && !selectedRecommendationFood.contains(
                            foodItem
                        )
                    ) {
                        ToastUtil.showToast(
                            context,
                            context.getString(R.string.only_choose, "$maximumSelect")
                        )
                        return@setOnClickListener
                    }

                    if (!selectedRecommendationFood.contains(foodItem)) {
                        foodItem?.let {
                            selectedRecommendationFood.add(it)
                        }
                        updateButtonState(true, btnSelect)
                    } else {
                        selectedRecommendationFood.remove(foodItem)
                        updateButtonState(false, btnSelect)
                    }
                }
            }
        }

        private fun updateButtonState(isSelected: Boolean, button: TextView) {
            val context = itemView.context
            if (isSelected) {
                button.text = context.getString(R.string.selected)
                button.setTextColor(ContextCompat.getColor(context, R.color.white))
                button.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_80))
            } else {
                button.text = context.getString(R.string.select)
                button.setTextColor(ContextCompat.getColor(context, R.color.primary_80))
                button.setBackgroundResource(R.drawable.recommended_food_button)
            }
        }
    }

}