package com.nutrigoal.nutrigoal.ui.plan_diet

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.data.remote.entity.RecommendedFoodPreferenceItem
import com.nutrigoal.nutrigoal.databinding.FragmentFoodRecommendationBinding
import com.nutrigoal.nutrigoal.ui.survey.SurveyViewModel

class FoodRecommendationFragment : Fragment() {
    private var _binding: FragmentFoodRecommendationBinding? = null
    private val binding get() = _binding!!
    private val surveyViewModel: SurveyViewModel by activityViewModels()

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
        surveyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
        setUpDateAdapter()
        setUpSurveyResultData()

    }

    private fun setUpAction() {
        binding.apply {

        }
    }

    private fun setUpSurveyResultData() {
        binding.apply {
            surveyViewModel.surveyResult.observe(viewLifecycleOwner) {
                tvCalorieNeeds.text = it.recommendedFoodBasedOnCalories?.rfbocDailyCalorieNeeds
            }

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
    }

    private fun setUpDateAdapter() {

        val selectedRecommendationFood = mutableListOf<RecommendedFoodPreferenceItem?>()
        binding.apply {

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
            } else {
                cardCalorieNeeds.visibility = View.VISIBLE
                cardTableRecommendation.visibility = View.VISIBLE
            }
        }

    }
}