package com.nutrigoal.nutrigoal.ui.plan_diet

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.data.remote.entity.RecommendedFoodPreferenceItem
import com.nutrigoal.nutrigoal.databinding.RecommendationFoodCardBinding
import com.nutrigoal.nutrigoal.utils.ToastUtil

class RecommendationFoodAdapter(
    private val context: Context,
    private val foodList: List<RecommendedFoodPreferenceItem?>,
    private val selectedRecommendationFood: MutableList<RecommendedFoodPreferenceItem?>
) : RecyclerView.Adapter<RecommendationFoodAdapter.RecommendationFoodViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendationFoodViewHolder {
        val binding =
            RecommendationFoodCardBinding.inflate(LayoutInflater.from(context), parent, false)
        return RecommendationFoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecommendationFoodViewHolder, position: Int) {
        val foodItem = foodList[position]
        holder.bind(foodItem)
    }

    override fun getItemCount(): Int = foodList.size

    inner class RecommendationFoodViewHolder(private val binding: RecommendationFoodCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(foodItem: RecommendedFoodPreferenceItem?) {
            binding.apply {
                val context = itemView.context
                tvName.text = foodItem?.name
                tvCaloriesValue.text = "${foodItem?.calories}"
                btnSelect.setOnClickListener {
                    if (selectedRecommendationFood.size == 2) {
                        ToastUtil.showToast(
                            context,
                            context.getString(R.string.only_choose, "${2}")
                        )
                    }
                    if (selectedRecommendationFood.size < 2 && !selectedRecommendationFood.contains(
                            foodItem
                        )
                    ) {
                        selectedRecommendationFood.add(foodItem)
                        updateButtonState(true, btnSelect)
                    } else if (selectedRecommendationFood.contains(foodItem)) {
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
                button.setTextColor(ContextCompat.getColor(context, R.color.primary))
                button.setBackgroundResource(R.drawable.recommended_food_button)
            }
        }
    }
}

