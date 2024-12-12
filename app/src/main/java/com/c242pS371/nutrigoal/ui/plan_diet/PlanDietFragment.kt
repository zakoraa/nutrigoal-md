package com.c242pS371.nutrigoal.ui.plan_diet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.c242pS371.nutrigoal.data.remote.entity.UserEntity
import com.c242pS371.nutrigoal.databinding.FragmentPlanDietBinding
import com.c242pS371.nutrigoal.ui.common.HistoryViewModel
import com.c242pS371.nutrigoal.ui.plan_diet.adapter.DateAdapter
import com.c242pS371.nutrigoal.ui.plan_diet.adapter.DateItem
import com.c242pS371.nutrigoal.ui.plan_diet.adapter.FoodPagerAdapter
import com.c242pS371.nutrigoal.ui.survey.SurveyViewModel
import com.c242pS371.nutrigoal.utils.DateFormatter.parseDateToMonthAndDay

class PlanDietFragment : Fragment() {
    private var _binding: FragmentPlanDietBinding? = null
    private val binding get() = _binding!!
    private val surveyViewModel: SurveyViewModel by activityViewModels()
    private val historyViewModel: HistoryViewModel by activityViewModels()

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
        setUpDateAdapter()

        historyViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
        surveyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

    }

    private fun setUpDateAdapter() {
        historyViewModel.historyResult.observe(viewLifecycleOwner) {
            val dateList = it.perDay?.mapIndexed { _, perDayItem ->
                val (month, day) = parseDateToMonthAndDay(perDayItem.dietTime)
                DateItem(month, day)
            } ?: emptyList()

            binding.apply {
                val lastPerDay = it.perDay?.lastIndex ?: -1
                val perDay = it.perDay?.get(lastPerDay)

                val userEntity =
                    UserEntity(
                        id = it.userId,
                        height = perDay?.height,
                        bodyWeight = perDay?.bodyWeight,
                        dietCategory = perDay?.dietCategory,
                        hasGastricIssue = perDay?.hasGastricIssue,
                        age = perDay?.age,
                        gender = it.gender,
                        activityLevel = perDay?.activityLevel,
                        mealSchedule = perDay?.mealSchedule
                    )
                btnToAddFoodRecommendation.setOnClickListener {
                    val intent =
                        Intent(requireActivity(), AddFoodRecommendationActivity::class.java)
                    intent.putExtra(EXTRA_PLAN_DIET_USER, userEntity)
                    intent.putExtra(EXTRA_PER_DAY, perDay)
                    intent.putExtra(IS_NULL, false)
                    startActivity(intent)
                }

                val adapter = DateAdapter(dateList) { position ->
                    viewPager.currentItem = position
                }
                recyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                recyclerView.setHasFixedSize(true)
                recyclerView.adapter = adapter

                val pagerAdapter = FoodPagerAdapter(requireParentFragment(), dateList, it)
                viewPager.adapter = pagerAdapter

                adapter.setOnItemClickListener { position ->
                    viewPager.currentItem = position
                }

                viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        adapter.setSelectedPosition(position)
                    }
                })

            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                shimmerLoading.visibility = View.VISIBLE
                viewPager.visibility = View.GONE
                btnToAddFoodRecommendation.visibility = View.GONE
            } else {
                shimmerLoading.visibility = View.GONE
                viewPager.visibility = View.VISIBLE
                btnToAddFoodRecommendation.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        const val EXTRA_PLAN_DIET_USER = "extra_plan_diet_user"
        const val EXTRA_PER_DAY = "extra_per_day"
        const val IS_NULL = "is_null"
    }
}