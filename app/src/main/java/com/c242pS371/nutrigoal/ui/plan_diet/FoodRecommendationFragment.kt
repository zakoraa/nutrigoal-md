package com.c242pS371.nutrigoal.ui.plan_diet

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.data.ResultState
import com.c242pS371.nutrigoal.data.remote.entity.PerDayItem
import com.c242pS371.nutrigoal.data.remote.response.HistoryResponse
import com.c242pS371.nutrigoal.databinding.FragmentFoodRecommendationBinding
import com.c242pS371.nutrigoal.ui.common.HistoryViewModel
import com.c242pS371.nutrigoal.ui.plan_diet.adapter.FoodPagerAdapter
import com.c242pS371.nutrigoal.ui.plan_diet.adapter.MealScheduleAdapter
import com.c242pS371.nutrigoal.ui.plan_diet.adapter.RecommendationFoodAdapter
import com.c242pS371.nutrigoal.utils.ToastUtil
import kotlinx.coroutines.launch

class FoodRecommendationFragment : Fragment() {
    private var _binding: FragmentFoodRecommendationBinding? = null
    private val binding get() = _binding!!
    private val historyViewModel: HistoryViewModel by activityViewModels()
    private var historyResponse: HistoryResponse? = null
    private var perDayItem: PerDayItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodRecommendationBinding.inflate(inflater, container, false)

        setUpView()
        setUpAction()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpView() {
        setUpSurveyResultData()
        lifecycleScope.launch {
            historyViewModel.updateSelectedFoodRecommendationState.collect {
                handleUpdateSelectedFoodRecommendation(it)
            }
        }
    }

    private fun handleUpdateSelectedFoodRecommendation(result: ResultState<Unit?>) {
        when (result) {
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> {
                ToastUtil.showToast(
                    requireContext(),
                    getString(R.string.add_food_recommendation_success)
                )
                showLoading(false)
            }

            is ResultState.Error -> {
                ToastUtil.showToast(
                    requireContext(),
                    getString(R.string.error_add_food_recommendation)
                )
                showLoading(false)
            }

            is ResultState.Initial -> {}
        }
    }

    private fun setUpAction() {
        binding.apply {

        }
    }

    private fun setUpSurveyResultData() {
        arguments?.let {
            historyResponse = if (Build.VERSION.SDK_INT >= 33) {
                it.getParcelable(FoodPagerAdapter.HISTORY_DATA, HistoryResponse::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.getParcelable(FoodPagerAdapter.HISTORY_DATA)
            }

            perDayItem = if (Build.VERSION.SDK_INT >= 33) {
                it.getParcelable(FoodPagerAdapter.PER_DAY, PerDayItem::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.getParcelable(FoodPagerAdapter.PER_DAY)
            }
        }

        binding.apply {
            tvCalorieNeeds.text = perDayItem?.calorieNeeds.toString()

            val mealCount = when (perDayItem?.activityLevel) {
                1 -> 2
                2 -> 2
                3 -> 3
                4 -> 3
                5 -> 3
                else -> 1
            }

            tvMealScheduleCount.text = getString(R.string.meal_schedule_count, mealCount.toString())

            val gradient = LinearGradient(
                0f, 0f, 0f, tvCalorieNeeds.textSize,
                intArrayOf(
                    ContextCompat.getColor(requireContext(), R.color.primary),
                    ContextCompat.getColor(requireContext(), R.color.primary),
                    ContextCompat.getColor(requireContext(), R.color.primary),
                    ContextCompat.getColor(requireContext(), R.color.primary),
                    ContextCompat.getColor(requireContext(), R.color.primary_80),
                    ContextCompat.getColor(requireContext(), R.color.primary_80),
                    ContextCompat.getColor(requireContext(), R.color.primary_80),
                    ContextCompat.getColor(requireContext(), R.color.primary_80),
                    ContextCompat.getColor(requireContext(), R.color.primary_30)
                ),
                null,
                Shader.TileMode.CLAMP
            )
            tvCalorieNeeds.paint.shader = gradient
        }
        setUpDateAdapter()
    }

    private fun setUpDateAdapter() {

        val selectedRecommendationFood =
            perDayItem?.selectedFoodRecommendation?.toMutableList() ?: mutableListOf()

        binding.apply {
            val mealSchedules = listOf(
                perDayItem?.mealSchedule?.breakfastTime,
                perDayItem?.mealSchedule?.launchTime,
                perDayItem?.mealSchedule?.dinnerTime,
            )


            val mealScheduleAdapter = MealScheduleAdapter(mealSchedules)
            rvMealSchedule.setHasFixedSize(true)
            rvMealSchedule.setLayoutManager(object :
                LinearLayoutManager(requireContext(), HORIZONTAL, false) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            })
            rvMealSchedule.adapter = mealScheduleAdapter

            val recommendationFoodAdapter = RecommendationFoodAdapter(
                requireContext(),
                perDayItem,
                perDayItem?.foodRecommendation ?: emptyList(),
                selectedRecommendationFood
            )
            rvRecommendationFood.setLayoutManager(object :
                LinearLayoutManager(requireContext()) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            })
            rvRecommendationFood.setHasFixedSize(true)
            rvRecommendationFood.adapter = recommendationFoodAdapter
            val maximumSelect = when (perDayItem?.activityLevel) {
                1 -> 2
                2 -> 2
                3 -> 3
                4 -> 3
                5 -> 3
                else -> 2
            }
            btnSave.setOnClickListener {
                if (selectedRecommendationFood.size != maximumSelect) {
                    ToastUtil.showToast(
                        requireContext(),
                        getString(R.string.error_select_recommended_food, maximumSelect.toString())
                    )
                } else {
                    historyViewModel.updateSelectedFoodRecommendation(
                        historyResponse?.userId ?: "",
                        perDayItem?.id ?: "",
                        selectedRecommendationFood,
                    )
                }
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                cardCalorieNeeds.visibility = View.GONE
                cardTableRecommendation.visibility = View.GONE
                shimmerBtnSave.visibility = View.VISIBLE
                btnSave.visibility = View.GONE
            } else {
                cardCalorieNeeds.visibility = View.VISIBLE
                cardTableRecommendation.visibility = View.VISIBLE
                shimmerBtnSave.visibility = View.GONE
                btnSave.visibility = View.VISIBLE
            }
        }

    }
}