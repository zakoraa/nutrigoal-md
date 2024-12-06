package com.nutrigoal.nutrigoal.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.databinding.FragmentDashboardBinding
import com.nutrigoal.nutrigoal.ui.auth.AuthViewModel


class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setUpView()
        return root
    }

    private fun setUpView() {
        handleShowHeader()
        setUpNutrientsChart()
        setUpWeightProgressAdapter()
    }

    private fun setUpWeightProgressAdapter() {
        val weightList = listOf(
            WeightProgress(12, 1, "Day 1", 70f),
            WeightProgress(12, 2, "Day 2", 71f),
            WeightProgress(12, 3, "Day 3", 70f),
            WeightProgress(12, 4, "Day 4", 75f),
            WeightProgress(12, 5, "Day 5", 71f),
            WeightProgress(12, 6, "Day 6", 71f),
        )

        val adapter = BodyWeightProgressAdapter(weightList)
        with(binding) {
            rvWeightProgress.adapter = adapter
            rvWeightProgress.setHasFixedSize(true)
            rvWeightProgress.setLayoutManager(object : LinearLayoutManager(requireContext()) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            })
        }
    }

    private fun setUpNutrientsChart() {
        val lineChart = binding.chartNutrients

        val dates = (1..10).map { it.toFloat() }
        val dateLabels = (1..10).map { "Day $it" }

        val caloriesEntries =
            dates.mapIndexed { index, date -> Entry(date, (1800 + index * 10).toFloat()) }
        val proteinEntries =
            dates.mapIndexed { index, date -> Entry(date, (80 + index * 2).toFloat()) }
        val fatEntries = dates.mapIndexed { index, date -> Entry(date, (30 + index).toFloat()) }
        val carbohydratesEntries =
            dates.mapIndexed { index, date -> Entry(date, (200 + index * 5).toFloat()) }

        val caloriesDataSet = LineDataSet(caloriesEntries, "Calories").apply {
            color = ContextCompat.getColor(requireContext(), R.color.primary)
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.textColor)
            lineWidth = 2f
        }
        val proteinDataSet = LineDataSet(proteinEntries, "Protein").apply {
            color = ContextCompat.getColor(requireContext(), R.color.secondary)
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.textColor)
            lineWidth = 2f
        }
        val fatDataSet = LineDataSet(fatEntries, "Fat").apply {
            color = ContextCompat.getColor(requireContext(), R.color.error)
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.textColor)
            lineWidth = 2f
        }
        val carbohydratesDataSet = LineDataSet(carbohydratesEntries, "Carbohydrates").apply {
            color = ContextCompat.getColor(requireContext(), R.color.tertiary)
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.textColor)
            lineWidth = 2f
        }

        val lineData = LineData(caloriesDataSet, proteinDataSet, fatDataSet, carbohydratesDataSet)
        lineChart.data = lineData

        val xAxis = lineChart.xAxis
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textColor)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(dateLabels)
        xAxis.granularity = 1f
        xAxis.labelCount = 7

        val yAxis = lineChart.axisLeft
        yAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textColor)
        lineChart.axisRight.isEnabled = false
        lineChart.description.text = ""
        lineChart.animateX(1000)
        lineChart.invalidate()
    }


    private fun handleShowHeader() {
        with(binding) {
            viewModel.currentUser.observe(viewLifecycleOwner) {
                tvGreetings.text =
                    getString(R.string.dashboard_greetings, it?.username)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}