package com.nutrigoal.nutrigoal.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
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
        handleBodyWeightProgressChart()
        setupCaloriesChart()
        setupProteinChart()
        setupFatChart()
        setupCarbohydratesChart()
    }

    private fun handleBodyWeightProgressChart() {
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
            xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.primary)

            val yAxis = chartBodyWeightProgress.axisLeft
            yAxis.axisMinimum = 55f
            yAxis.axisMaximum = 70f
            yAxis.axisLineWidth = 2f
            yAxis.axisLineColor = ContextCompat.getColor(requireContext(), R.color.primary)
            yAxis.labelCount = 5
            yAxis.textColor = ContextCompat.getColor(requireContext(), R.color.primary)

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

    private fun setupCaloriesChart() {
        val lineChart = binding.chartCalories

        val entries = listOf(
            Entry(0f, 1800f),
            Entry(1f, 2000f),
            Entry(2f, 1900f),
            Entry(3f, 2200f),
            Entry(4f, 2100f)
        )
        val dataSet = LineDataSet(entries, "Calories")
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.primary)
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 10f

        lineChart.data = LineData(dataSet)
        setupChartDefaults(lineChart)
    }

    private fun setupProteinChart() {
        val barChart = binding.chartProtein

        val entries = listOf(
            BarEntry(0f, 80f),
            BarEntry(1f, 100f),
            BarEntry(2f, 90f),
            BarEntry(3f, 120f),
            BarEntry(4f, 110f)
        )
        val dataSet = BarDataSet(entries, "Protein")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 10f

        barChart.data = BarData(dataSet)
        setupChartDefaults(barChart)
    }

    private fun setupFatChart() {
        val pieChart = binding.chartFat

        val entries = listOf(
            PieEntry(30f, "Saturated"),
            PieEntry(50f, "Unsaturated"),
            PieEntry(20f, "Trans Fat")
        )
        val dataSet = PieDataSet(entries, "Fat")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 10f

        pieChart.data = PieData(dataSet)
        pieChart.description = Description().apply { text = "Fat Distribution" }
        pieChart.setUsePercentValues(true)
        pieChart.animateY(1000)
    }

    private fun setupCarbohydratesChart() {
        val horizontalBarChart = binding.chartCarbohydrates

        val entries = listOf(
            BarEntry(0f, 200f),
            BarEntry(1f, 250f),
            BarEntry(2f, 230f),
            BarEntry(3f, 260f),
            BarEntry(4f, 240f)
        )
        val dataSet = BarDataSet(entries, "Carbohydrates")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 10f

        horizontalBarChart.data = BarData(dataSet)
        setupChartDefaults(horizontalBarChart)
    }

    private fun setupChartDefaults(chart: Chart<*>) {
        chart.description = Description().apply { text = "" }
        chart.animateY(1000)
        chart.invalidate()
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