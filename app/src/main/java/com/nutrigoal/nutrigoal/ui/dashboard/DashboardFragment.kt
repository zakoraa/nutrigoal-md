package com.nutrigoal.nutrigoal.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
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
        setUpWeightProgressChart()
        setUpNutrientsChart()
    }

    private fun setUpWeightProgressChart() {
        val xValues =
            mutableListOf("20 Dec", "21 Dec", "22 Dec", "23 Dec", "24 Dec", "25 Dec", "26 Dec")

        with(binding) {
            chartBodyWeightProgress.axisRight.setDrawLabels(false)
            val description = Description()
            description.text = ""
            chartBodyWeightProgress.description = description

            val xAxis = chartBodyWeightProgress.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
            xAxis.labelCount = 7
            xAxis.granularity = 1f
            xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textColor)

            val yAxis = chartBodyWeightProgress.axisLeft
            yAxis.axisMinimum = 55f
            yAxis.axisMaximum = 70f
            yAxis.axisLineWidth = 2f
            yAxis.axisLineColor = ContextCompat.getColor(requireContext(), R.color.textColor)
            yAxis.labelCount = 5
            yAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textColor)

            val entries: MutableList<BarEntry> = ArrayList()
            entries.add(BarEntry(0f, 60f))
            entries.add(BarEntry(1f, 61f))
            entries.add(BarEntry(2f, 63f))
            entries.add(BarEntry(3f, 64f))
            entries.add(BarEntry(4f, 63f))
            entries.add(BarEntry(5f, 62f))
            entries.add(BarEntry(6f, 61f))

            val dataSet = BarDataSet(entries, "Body Weight")
            dataSet.colors = listOf(
                ContextCompat.getColor(requireContext(), R.color.error),
                ContextCompat.getColor(requireContext(), R.color.primary_80),
                ContextCompat.getColor(requireContext(), R.color.secondary),
                ContextCompat.getColor(requireContext(), R.color.tertiary),
                ContextCompat.getColor(requireContext(), R.color.onBackground),
                ContextCompat.getColor(requireContext(), R.color.primaryVariant),
                ContextCompat.getColor(requireContext(), R.color.primary)
            )
            dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.textColor)
            dataSet.valueTextSize = 12f
            dataSet.setDrawValues(true)

            val barData = BarData(dataSet)
            chartBodyWeightProgress.data = barData

            chartBodyWeightProgress.setFitBars(true)
            chartBodyWeightProgress.animateY(1000)
            chartBodyWeightProgress.invalidate()
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
                Glide.with(requireContext())
                    .load(it?.photoProfile)
                    .placeholder(R.drawable.photo_profile)
                    .into(binding.ivPhotoProfile)
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