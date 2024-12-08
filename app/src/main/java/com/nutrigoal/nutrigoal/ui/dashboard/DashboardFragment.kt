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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.databinding.FragmentDashboardBinding
import com.nutrigoal.nutrigoal.ui.auth.AuthViewModel
import com.nutrigoal.nutrigoal.ui.survey.SurveyViewModel
import java.util.Calendar
import java.util.Locale


class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()
    private val surveyViewModel: SurveyViewModel by activityViewModels()

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
        setUpNutrientsCharts()
        setUpWeightProgressAdapter()

        surveyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                cardBmi.visibility = View.GONE
                cardBmiShimmerBase.visibility = View.VISIBLE
                cardBmiShimmerLoading.visibility = View.VISIBLE
                cardNutrients.visibility = View.GONE
                cardBodyWeightProgress.visibility = View.GONE
                cardBodyWeightInfo.visibility = View.GONE
                shimmerLoading.visibility = View.VISIBLE
            } else {
                cardBmi.visibility = View.VISIBLE
                cardBmiShimmerBase.visibility = View.GONE
                cardBmiShimmerLoading.visibility = View.GONE
                cardNutrients.visibility = View.VISIBLE
                cardBodyWeightProgress.visibility = View.VISIBLE
                cardBodyWeightInfo.visibility = View.VISIBLE
                shimmerLoading.visibility = View.GONE
                handleShowCurrentUserData()
            }
        }

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

    private fun setUpNutrientsCharts() {
        surveyViewModel.surveyResult.observe(viewLifecycleOwner) { surveyResult ->
            binding.apply {
                val caloriesChart = chartCalories
                val proteinChart = chartProtein
                val fatChart = chartFat
                val carbohydratesChart = chartCarbohydrates

                val recommendedFoodPreferences =
                    surveyResult?.recommendedFoodPreference ?: emptyList()

                val entriesCount = maxOf(recommendedFoodPreferences.size, 7)

                val dates = (1..entriesCount).map { it.toFloat() }
                val dateLabels = (1..entriesCount).map { "Day $it" }

                val caloriesEntries = recommendedFoodPreferences.mapIndexed { index, item ->
                    Entry(dates[index], item?.calories?.toString()?.toFloat() ?: 0f)
                }

                val proteinEntries = recommendedFoodPreferences.mapIndexed { index, item ->
                    Entry(dates[index], item?.proteinG.toString().toFloat())
                }

                val fatEntries = recommendedFoodPreferences.mapIndexed { index, item ->
                    Entry(dates[index], item?.fatG.toString().toFloat())
                }

                val carbohydratesEntries = recommendedFoodPreferences.mapIndexed { index, item ->
                    Entry(dates[index], item?.carbohydrateG.toString().toFloat())
                }

                setupSingleChart(
                    caloriesChart,
                    caloriesEntries,
                    dateLabels,
                    "Calories",
                    R.color.primary,
                    5f,
                )
                setupSingleChart(
                    proteinChart,
                    proteinEntries,
                    dateLabels,
                    "Protein",
                    R.color.secondary
                )
                setupSingleChart(
                    fatChart,
                    fatEntries,
                    dateLabels,
                    "Fat",
                    R.color.error
                )
                setupSingleChart(
                    carbohydratesChart,
                    carbohydratesEntries,
                    dateLabels,
                    "Carbohydrates",
                    R.color.tertiary
                )
            }
        }
    }


    private fun setupSingleChart(
        chart: LineChart,
        entries: List<Entry>,
        labels: List<String>,
        label: String,
        colorRes: Int,
        textSize: Float = 10f
    ) {
        val dataSet = LineDataSet(entries, label).apply {
            color = ContextCompat.getColor(requireContext(), colorRes)
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.textColor)
            lineWidth = 3f
            valueTextSize = textSize
        }

        chart.data = LineData(dataSet)

        val xAxis = chart.xAxis
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textColor)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.granularity = 1f
        xAxis.labelCount = 7

        val yAxis = chart.axisLeft
        yAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textColor)
        chart.axisRight.isEnabled = false
        chart.description.text = ""
        chart.animateX(1000)
        chart.invalidate()
    }


    private fun handleShowCurrentUserData() {
        with(binding) {
            viewModel.currentUser.observe(viewLifecycleOwner) {
                tvCurrentWeight.text = it?.bodyWeight.toString()
                tvGreetings.text =
                    getString(R.string.dashboard_greetings, it?.username)
                setBMILineWidths(it?.bodyWeight ?: 0f, it?.height ?: 0f)
            }

        }
    }

    private fun calculateBMI(weight: Float, height: Float): Float {
        if (height <= 0){return 0f}
        val heightInMeter = height / 100
        return weight / (heightInMeter * heightInMeter)
    }

    private fun setBMILineWidths(weight: Float, height: Float) {
        val bmiValue = calculateBMI(weight, height)
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