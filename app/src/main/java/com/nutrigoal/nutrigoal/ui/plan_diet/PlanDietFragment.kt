package com.nutrigoal.nutrigoal.ui.plan_diet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.data.ResultState
import com.nutrigoal.nutrigoal.data.remote.response.SurveyResponse
import com.nutrigoal.nutrigoal.databinding.FragmentPlanDietBinding
import com.nutrigoal.nutrigoal.ui.survey.SurveyViewModel
import com.nutrigoal.nutrigoal.utils.ToastUtil
import kotlinx.coroutines.launch

class PlanDietFragment : Fragment() {
    private var _binding: FragmentPlanDietBinding? = null
    private val binding get() = _binding!!
    private val surveyViewModel: SurveyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        lifecycleScope.launch {
            surveyViewModel.surveyResponseState.collect { result ->
                handleGetSurveyResult(result)
            }
        }

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
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = adapter

    }

    private fun handleGetSurveyResult(result: ResultState<SurveyResponse?>) {
        when (result) {
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> {
                showLoading(false)
            }

            is ResultState.Error -> {
                showLoading(false)
                ToastUtil.showToast(requireContext(), getString(R.string.error_get_user))
            }

            is ResultState.Initial -> {}

        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
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