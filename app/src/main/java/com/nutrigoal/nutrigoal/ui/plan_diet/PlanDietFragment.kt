package com.nutrigoal.nutrigoal.ui.plan_diet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrigoal.nutrigoal.data.remote.entity.RecommendedFoodPreferenceItem
import com.nutrigoal.nutrigoal.databinding.FragmentPlanDietBinding
import com.nutrigoal.nutrigoal.ui.survey.SurveyViewModel

class PlanDietFragment : Fragment() {
    private var _binding: FragmentPlanDietBinding? = null
    private val binding get() = _binding!!
    private val surveyViewModel: SurveyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanDietBinding.inflate(inflater, container, false)

        setUpView()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpView() {
        surveyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
        setUpDateAdapter()
        setUpSurveyResultData()

    }

    private fun setUpSurveyResultData() {
        binding.apply {
            surveyViewModel.surveyResult.observe(viewLifecycleOwner) {
                tvCalorieNeeds.text = it.recommendedFoodBasedOnCalories?.rfbocDailyCalorieNeeds
            }
        }
    }

    private fun setUpDateAdapter() {
        val dateList = listOf(
            DateItem("Des", "20"),
            DateItem("Des", "21"),
            DateItem("Des", "22"),
            DateItem("Des", "23"),
            DateItem("Des", "24"),
            DateItem("Des", "25"),
            DateItem("Des", "26"),
        )

        val adapter = DateAdapter(dateList)

        val selectedRecommendationFood = mutableListOf<RecommendedFoodPreferenceItem?>()
        binding.apply {
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter

            surveyViewModel.surveyResult.observe(viewLifecycleOwner) {

                val recommendationFoodAdapter = RecommendationFoodAdapter(
                    requireContext(),
                    it?.recommendedFoodPreference ?: emptyList(),
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
    }


    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                cardCalorieNeeds.visibility = View.GONE
                cardTableRecommendation.visibility = View.GONE
                shimmerLoading.visibility = View.VISIBLE
            } else {
                cardCalorieNeeds.visibility = View.VISIBLE
                cardTableRecommendation.visibility = View.VISIBLE
                shimmerLoading.visibility = View.GONE
            }
        }

    }


}