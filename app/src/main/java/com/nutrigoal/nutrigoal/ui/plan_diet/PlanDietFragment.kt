package com.nutrigoal.nutrigoal.ui.plan_diet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.nutrigoal.nutrigoal.databinding.FragmentPlanDietBinding
import com.nutrigoal.nutrigoal.ui.common.HistoryViewModel
import com.nutrigoal.nutrigoal.ui.survey.AddFoodRecommendationActivity
import com.nutrigoal.nutrigoal.ui.survey.SurveyViewModel
import com.nutrigoal.nutrigoal.utils.DateFormatter.parseDateToMonthAndDay

class PlanDietFragment : Fragment() {
    private var _binding: FragmentPlanDietBinding? = null
    private val binding get() = _binding!!
    private val surveyViewModel: SurveyViewModel by activityViewModels()
    private val historyViewModel: HistoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanDietBinding.inflate(inflater, container, false)

        setUpAction()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpView() {
        historyViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
        surveyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        setUpDateAdapter()

    }

    private fun setUpAction() {
        binding.apply {
            btnToAddFoodRecommendation.setOnClickListener {
                startActivity(Intent(requireActivity(), AddFoodRecommendationActivity::class.java))
            }
        }
    }


    private fun setUpDateAdapter() {

        historyViewModel.historyResult.observe(viewLifecycleOwner) {
            Log.d("FLORAAAAA", "PAANSIH: ${it} ")
            val dateList = it.perDay?.mapIndexed { _, perDayItem ->
                val (month, day) = parseDateToMonthAndDay(perDayItem.createdAt)
                DateItem(month, day)
            } ?: emptyList()

            binding.apply {
                val adapter = DateAdapter(dateList) { position ->
                    viewPager.currentItem = position
                }
                recyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                recyclerView.setHasFixedSize(true)
                recyclerView.adapter = adapter

                val pagerAdapter = FoodPagerAdapter(requireParentFragment(), dateList)
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
            } else {
                shimmerLoading.visibility = View.GONE
                viewPager.visibility = View.VISIBLE
            }
        }

    }


}