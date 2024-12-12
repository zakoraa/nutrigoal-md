package com.c242pS371.nutrigoal.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.data.ResultState
import com.c242pS371.nutrigoal.data.local.entity.UserLocalEntity
import com.c242pS371.nutrigoal.data.remote.entity.UserEntity
import com.c242pS371.nutrigoal.data.remote.response.HistoryResponse
import com.c242pS371.nutrigoal.data.remote.response.SurveyResponse
import com.c242pS371.nutrigoal.databinding.FragmentDashboardBinding
import com.c242pS371.nutrigoal.ui.auth.AuthViewModel
import com.c242pS371.nutrigoal.ui.common.HistoryViewModel
import com.c242pS371.nutrigoal.ui.survey.SurveyViewModel
import com.c242pS371.nutrigoal.utils.AppUtil
import com.c242pS371.nutrigoal.utils.DateFormatter.parseDateToMonthAndDay
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()
    private val historyViewModel: HistoryViewModel by activityViewModels()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setUpView() {
        lifecycleScope.launch {
            viewModel.userLocalEntitySessionState.collect { result ->
                handleGetUserSession(result)
            }
        }

        lifecycleScope.launch {
            viewModel.currentUserState.collect { result ->
                handleGetUser(result)
            }
        }

    }

    private fun handleGetUser(result: ResultState<UserEntity?>) {
        when (result) {
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> {
                historyViewModel.isLoading.observe(viewLifecycleOwner) {
                    showLoading(it)
                    surveyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                        showLoading(isLoading)
                    }
                }

                historyViewModel.historyResult.observe(viewLifecycleOwner) {
                    setUpWeightProgressAdapter(it)
                    setUpNutrientsCharts(it)
                    handleShowCurrentUserData(it)
                }
            }

            is ResultState.Error -> showLoading(false)
            is ResultState.Initial -> {}

        }
    }

    private fun handleGetSurveyResult(result: ResultState<SurveyResponse?>) {
        when (result) {
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> showLoading(false)
            is ResultState.Error -> showLoading(false)
            is ResultState.Initial -> {}
        }
    }

    private fun handleGetUserSession(result: ResultState<UserLocalEntity>) {
        when (result) {
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> showLoading(false)
            is ResultState.Error -> showLoading(false)
            is ResultState.Initial -> {}
        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                cardNutrients.visibility = View.GONE
                cardBodyWeightProgress.visibility = View.GONE
                cardBodyWeightInfo.visibility = View.GONE
                shimmerLoading.visibility = View.VISIBLE
            } else {
                cardNutrients.visibility = View.VISIBLE
                cardBodyWeightProgress.visibility = View.VISIBLE
                cardBodyWeightInfo.visibility = View.VISIBLE
                shimmerLoading.visibility = View.GONE
            }
        }
    }

    private fun setUpWeightProgressAdapter(historyResponse: HistoryResponse?) {
        val weightList = historyResponse?.perDay?.mapIndexedNotNull { index, perDayItem ->
            if (perDayItem.bodyWeight != null) {
                val (month, day) = parseDateToMonthAndDay(perDayItem.createdAt)
                WeightProgress(
                    month = month,
                    day = day,
                    title = "Day ${index + 1}",
                    bodyWeight = perDayItem.bodyWeight ?: 0f
                )
            } else {
                null
            }
        } ?: emptyList()

        val adapter = BodyWeightProgressAdapter(weightList)
        with(binding) {
            tvWeightGainPercentage.text = getString(
                R.string.weight_percentage_format,
                calculateWeightPercentage(weightList)
            )
            tvBodyWeightProgressRange.text =
                if (weightList.size == 1) getString(R.string.today) else {
                    getString(R.string.last_days, weightList.size.toString())
                }
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

    private fun setUpNutrientsCharts(historyResponse: HistoryResponse?) {
        binding.apply {
            val caloriesChart = chartCalories
            val proteinChart = chartProtein
            val fatChart = chartFat
            val carbohydratesChart = chartCarbohydrates

            val dateLabels = mutableListOf<String>()

            val caloriesEntries = mutableListOf<Entry>()
            val proteinEntries = mutableListOf<Entry>()
            val fatEntries = mutableListOf<Entry>()
            val carbohydratesEntries = mutableListOf<Entry>()

            historyResponse?.perDay?.forEachIndexed { dayIndex, perDayItem ->
                if (dayIndex == 0) {
                    if (perDayItem.selectedFoodRecommendation == null) {
                        tvNoDataNutrients.visibility = View.VISIBLE
                        llNutrients.visibility = View.GONE
                    } else {
                        tvNoDataNutrients.visibility = View.GONE
                        llNutrients.visibility = View.VISIBLE
                        val dayNumber = 1

                        val dateFormatInput =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                        val dateFormatOutput = SimpleDateFormat("d MMM", Locale.ENGLISH)

                        val dietTimeString = perDayItem.dietTime
                        val date = dateFormatInput.parse(dietTimeString.toString())

                        val formattedDate = dateFormatOutput.format(date ?: "")
                        dateLabels.add(formattedDate)

                        tvNutrientsRange.text = getString(R.string.today)
                        var totalCalories = 0f
                        var totalProtein = 0f
                        var totalFat = 0f
                        var totalCarbs = 0f

                        perDayItem.selectedFoodRecommendation?.forEach { foodItem ->
                            totalCalories += foodItem.calories ?: 0f
                            totalProtein += foodItem.protein ?: 0f
                            totalFat += foodItem.fat ?: 0f
                            totalCarbs += foodItem.carbohydrate ?: 0f
                        }

                        caloriesEntries.add(Entry(dayNumber.toFloat(), totalCalories))
                        proteinEntries.add(Entry(dayNumber.toFloat(), totalProtein))
                        fatEntries.add(Entry(dayNumber.toFloat(), totalFat))
                        carbohydratesEntries.add(Entry(dayNumber.toFloat(), totalCarbs))
                    }
                } else {
                    if (perDayItem.selectedFoodRecommendation != null) {

                        val dayNumber = dayIndex + 1

                        val dateFormatInput =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                        val dateFormatOutput = SimpleDateFormat("d MMM", Locale.ENGLISH)

                        val dietTimeString = perDayItem.dietTime
                        val date = dateFormatInput.parse(dietTimeString.toString())

                        val formattedDate = dateFormatOutput.format(date ?: "")
                        dateLabels.add(formattedDate)

                        tvNutrientsRange.text =
                            getString(R.string.last_days, dayNumber.toString())


                        var totalCalories = 0f
                        var totalProtein = 0f
                        var totalFat = 0f
                        var totalCarbs = 0f

                        perDayItem.selectedFoodRecommendation?.forEach { foodItem ->
                            totalCalories += foodItem.calories ?: 0f
                            totalProtein += foodItem.protein ?: 0f
                            totalFat += foodItem.fat ?: 0f
                            totalCarbs += foodItem.carbohydrate ?: 0f
                        }

                        caloriesEntries.add(Entry(dayNumber.toFloat(), totalCalories))
                        proteinEntries.add(Entry(dayNumber.toFloat(), totalProtein))
                        fatEntries.add(Entry(dayNumber.toFloat(), totalFat))
                        carbohydratesEntries.add(Entry(dayNumber.toFloat(), totalCarbs))
                    }
                }
            }

            setupSingleChart(
                caloriesChart,
                caloriesEntries,
                dateLabels,
                ContextCompat.getString(requireContext(), R.string.calories_title),
                R.color.primary,
            )
            setupSingleChart(
                proteinChart,
                proteinEntries,
                dateLabels,
                ContextCompat.getString(requireContext(), R.string.protein_title),
                R.color.secondary
            )
            setupSingleChart(
                fatChart,
                fatEntries,
                dateLabels,
                ContextCompat.getString(requireContext(), R.string.fat_title),
                R.color.error
            )
            setupSingleChart(
                carbohydratesChart,
                carbohydratesEntries,
                dateLabels,
                ContextCompat.getString(requireContext(), R.string.carbohydrates_title),
                R.color.tertiary
            )
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

    private fun handleShowCurrentUserData(historyResponse: HistoryResponse?) {
        with(binding) {
            viewModel.currentUser.observe(viewLifecycleOwner) {
                tvGreetings.text =
                    getString(R.string.dashboard_greetings, it?.username)
            }
            val index = AppUtil.getTodayDataFromPerDay(historyResponse)
            val perDay = historyResponse?.perDay?.get(index)
            tvCurrentWeight.text = perDay?.bodyWeight.toString()
            setBMILineWidths(
                perDay?.bodyWeight
                    ?: 0f,
                perDay?.height
                    ?: 0f
            )
        }

    }

    private fun calculateBMI(weight: Float, height: Float): Float {
        if (height <= 0) {
            return 0f
        }
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
            tvCurrentBmiValue.text = getString(R.string.bmi_value_format, bmiValue)

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

    private fun setUpBMIType(bmiValue: Float) {
        val bmiType = when {
            bmiValue < 18.5 -> BMIType.UNDER_WEIGHT
            bmiValue < 25 -> BMIType.NORMAL_WEIGHT
            bmiValue < 30 -> BMIType.OVER_WEIGHT
            bmiValue < 35 -> BMIType.OBESITY
            bmiValue <= 40 -> BMIType.EXTREME_OBESITY
            else -> BMIType.EXTREME_OBESITY
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

    private fun calculateWeightPercentage(weightList: List<WeightProgress>): Float {
        if (weightList.isEmpty()) return 0f

        val firstWeight = weightList.firstOrNull()?.bodyWeight ?: 0f
        val lastWeight = weightList.lastOrNull()?.bodyWeight ?: 0f

        if (firstWeight == 0f || lastWeight == 0f) return 0f

        val weightDifference = lastWeight - firstWeight
        return (weightDifference / firstWeight) * 100
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