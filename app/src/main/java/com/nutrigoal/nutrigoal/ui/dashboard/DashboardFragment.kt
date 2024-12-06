package com.nutrigoal.nutrigoal.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
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
import java.util.Calendar
import java.util.Locale


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
        setBMILineWidths()
    }

    private fun setUpWeightProgressAdapter() {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val weightList = listOf(
            WeightProgress(12, currentDay - 5, "Day 1", 70f),
            WeightProgress(12, currentDay - 4, "Day 2", 71f),
            WeightProgress(12, currentDay - 3, "Day 3", 70f),
            WeightProgress(12, currentDay - 2, "Day 4", 75f),
            WeightProgress(12, currentDay - 1, "Day 5", 71f),
            WeightProgress(12, currentDay, "Day 6", 71f),
        )

        val adapter = BodyWeightProgressAdapter(weightList)
        with(binding) {
            rvWeightProgress.adapter = adapter
            rvWeightProgress.setHasFixedSize(true)
            rvWeightProgress.setLayoutManager(object : LinearLayoutManager(requireContext()) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }.apply {
                reverseLayout = true
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

    private fun calculateBMI(weight: Float, height: Float): Float {
        if (height <= 0) throw IllegalArgumentException("Height must be more than 0")
        val heightInMeter = height / 100
        return weight / (heightInMeter * heightInMeter)
    }

    private fun setBMILineWidths() {
        val bmiValue = calculateBMI(71.0f, 170f)
        val differenceWidth = 15.0
        val totalWidth = 40.0 - differenceWidth
        val underWeight = 18.5 - differenceWidth
        val normalWeight = 25 - differenceWidth
        val overWeight = 30 - differenceWidth
        val obesity = 35.0 - differenceWidth

        val bmiValuePercent = ((bmiValue - differenceWidth) / totalWidth).toFloat()
        val underWeightPercent = (underWeight / totalWidth).toFloat()
        val normalWeightPercent = (normalWeight / totalWidth).toFloat()
        val overWeightPercent = (overWeight / totalWidth).toFloat()
        val obesityPercent = (obesity / totalWidth).toFloat()
        val extremeObesityPercent = 1f

        with(binding) {
            tvCurrentBmiValue.text = String.format("%.1f", bmiValue)

            bmiMarker.layoutParams =
                (bmiMarker.layoutParams as ConstraintLayout.LayoutParams).apply {
                    matchConstraintPercentWidth = bmiValuePercent
                }

            underWeightLine.layoutParams =
                (underWeightLine.layoutParams as ConstraintLayout.LayoutParams).apply {
                    matchConstraintPercentWidth = underWeightPercent
                }

            normalWeightLine.layoutParams =
                (normalWeightLine.layoutParams as ConstraintLayout.LayoutParams).apply {
                    matchConstraintPercentWidth = normalWeightPercent
                }

            overWeightLine.layoutParams =
                (overWeightLine.layoutParams as ConstraintLayout.LayoutParams).apply {
                    matchConstraintPercentWidth = overWeightPercent
                }

            obesityLine.layoutParams =
                (obesityLine.layoutParams as ConstraintLayout.LayoutParams).apply {
                    matchConstraintPercentWidth = obesityPercent
                }

            extremeObesityLine.layoutParams =
                (extremeObesityLine.layoutParams as ConstraintLayout.LayoutParams).apply {
                    matchConstraintPercentWidth = extremeObesityPercent
                }

            tvUnderUnderWeight.text = String.format("15")
            tvUnderWeight.text = String.format("18.5")
            tvNormalWeight.text = String.format("25")
            tvOverWeight.text = String.format("30")
            tvObesity.text = String.format("35")
            tvExtremeObesity.text = String.format("40")

            val additionalPercent = 0.02f

            tvUnderWeight.layoutParams =
                (tvUnderWeight.layoutParams as ConstraintLayout.LayoutParams).apply {
                    matchConstraintPercentWidth = underWeightPercent + additionalPercent
                }

            tvNormalWeight.layoutParams =
                (tvNormalWeight.layoutParams as ConstraintLayout.LayoutParams).apply {
                    matchConstraintPercentWidth = normalWeightPercent + additionalPercent
                }

            tvOverWeight.layoutParams =
                (tvOverWeight.layoutParams as ConstraintLayout.LayoutParams).apply {
                    matchConstraintPercentWidth = overWeightPercent + additionalPercent
                }

            tvObesity.layoutParams =
                (tvObesity.layoutParams as ConstraintLayout.LayoutParams).apply {
                    matchConstraintPercentWidth = obesityPercent + additionalPercent
                }
        }
        setUpBMIType(bmiValue)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpBMIType(bmiValue: Float) {
        val bmiType = when {
            bmiValue < 18.5 -> BMIType.UNDER_WEIGHT
            bmiValue < 25 -> BMIType.NORMAL_WEIGHT
            bmiValue < 30 -> BMIType.OVER_WEIGHT
            bmiValue < 35 -> BMIType.OBESITY
            bmiValue <= 40 -> BMIType.EXTREME_OBESITY
            else -> throw IllegalArgumentException("BMI value out of range")
        }


        val color = when (bmiType) {
            BMIType.UNDER_WEIGHT -> R.color.primary
            BMIType.NORMAL_WEIGHT -> R.color.secondary_50
            BMIType.OVER_WEIGHT -> R.color.yellow
            BMIType.OBESITY -> R.color.tertiary
            BMIType.EXTREME_OBESITY -> R.color.error
        }

        with(binding) {
            tvBmiType.text = bmiType.toString()
            tvBmiType.setTextColor(resources.getColor(color, null))
        }
    }

}

enum class BMIType(private val displayName: String) {
    UNDER_WEIGHT("Under Weight"),
    NORMAL_WEIGHT("Normal Weight"),
    OVER_WEIGHT("Over Weight"),
    OBESITY("Obesity"),
    EXTREME_OBESITY("Extreme Obesity");

    override fun toString(): String {
        return displayName
    }
}