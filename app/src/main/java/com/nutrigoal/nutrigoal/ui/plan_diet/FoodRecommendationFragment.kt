package com.nutrigoal.nutrigoal.ui.plan_diet

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.data.remote.entity.FoodRecommendationItem
import com.nutrigoal.nutrigoal.data.remote.entity.PerDayItem
import com.nutrigoal.nutrigoal.data.remote.response.HistoryResponse
import com.nutrigoal.nutrigoal.databinding.FragmentFoodRecommendationBinding
import com.nutrigoal.nutrigoal.ui.common.HistoryViewModel

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
        historyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
        setUpSurveyResultData()
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

        val selectedRecommendationFood = mutableListOf<FoodRecommendationItem?>()
        binding.apply {

            val recommendationFoodAdapter = RecommendationFoodAdapter(
                requireContext(),
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

        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                cardCalorieNeeds.visibility = View.GONE
                cardTableRecommendation.visibility = View.GONE
            } else {
                cardCalorieNeeds.visibility = View.VISIBLE
                cardTableRecommendation.visibility = View.VISIBLE
            }
        }

    }
}