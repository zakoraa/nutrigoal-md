package com.nutrigoal.nutrigoal.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.DecelerateInterpolator
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.data.ResultState
import com.nutrigoal.nutrigoal.data.local.database.DailyCheckInPreference
import com.nutrigoal.nutrigoal.data.local.entity.UserLocalEntity
import com.nutrigoal.nutrigoal.data.remote.entity.DietCategory
import com.nutrigoal.nutrigoal.data.remote.entity.PerDayItem
import com.nutrigoal.nutrigoal.data.remote.entity.SurveyRequest
import com.nutrigoal.nutrigoal.data.remote.entity.UserEntity
import com.nutrigoal.nutrigoal.data.remote.response.HistoryResponse
import com.nutrigoal.nutrigoal.data.remote.response.SurveyResponse
import com.nutrigoal.nutrigoal.databinding.ActivityMainBinding
import com.nutrigoal.nutrigoal.databinding.PopUpCheckInBinding
import com.nutrigoal.nutrigoal.ui.auth.AuthViewModel
import com.nutrigoal.nutrigoal.ui.auth.LoginActivity
import com.nutrigoal.nutrigoal.ui.auth.LoginActivity.Companion.EXTRA_SURVEY
import com.nutrigoal.nutrigoal.ui.common.HistoryViewModel
import com.nutrigoal.nutrigoal.ui.settings.SettingsViewModel
import com.nutrigoal.nutrigoal.ui.survey.SurveyViewModel
import com.nutrigoal.nutrigoal.utils.AppUtil.getGenderCode
import com.nutrigoal.nutrigoal.utils.AppUtil.getTodayDataFromPerDay
import com.nutrigoal.nutrigoal.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: AuthViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val surveyViewModel: SurveyViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()
    private val historyResponse = HistoryResponse()
    private var index = 0

    @Inject
    lateinit var dailyCheckInPreference: DailyCheckInPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAppThemes()

        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController =
            findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        getSurveyResult()
        bottomNavScrollAnimation()
        setUpView()
    }

    private fun setUpView() {
        viewModel.getSession()


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

        lifecycleScope.launch {
            surveyViewModel.surveyResponseState.collect { result ->
                handleGetSurveyResult(result)
            }
        }

        lifecycleScope.launch {
            historyViewModel.historyResponseState.collect { result ->
                handleGetHistoryResult(result)
            }
        }
    }

    private fun getSurveyResult() {
        val userEntity = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(
                EXTRA_SURVEY,
                UserEntity::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_SURVEY)
        }

        if (userEntity !== null) {

            val surveyRequest = SurveyRequest(
                age = userEntity.age ?: 0,
                height = userEntity.height ?: 0f,
                weight = userEntity.bodyWeight ?: 0f,
                gender = getGenderCode(userEntity.gender.toString()),
                activity_level = userEntity.activityLevel ?: 1,
                diet_category = userEntity.dietCategory ?: DietCategory.VEGAN.toString(),
                has_gastric_issue = userEntity.hasGastricIssue.toString(),
                food_preference = userEntity.foodPreference ?: emptyList()
            )
            surveyViewModel.getSurveyResult(surveyRequest)
        } else {
            if (index == 0) {
                historyViewModel.getHistoryResult(Firebase.auth.currentUser?.uid ?: "")
            }
        }
    }


    private fun handleGetSurveyResult(result: ResultState<SurveyResponse?>) {
        when (result) {
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> {
                if (index == 0) {
                    showLoading(false)

                    lifecycleScope.launch {
                        val hasCheckedInToday = dailyCheckInPreference.hasCheckedInToday()

                        if (!hasCheckedInToday) {
                            showMealTimePopup()
                        }
                    }
                    val calendar = Calendar.getInstance()
                    val dateFormat =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)

                    val createdAt = dateFormat.format(calendar.time)
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                    val dietTime = dateFormat.format(calendar.time)
                    val it = result.data
                    val data = it?.recommendedFoodBasedOnCalories
                    historyResponse.gender = data?.rfbocGender
                    historyResponse.perDay = listOf(
                        PerDayItem(
                            id = UUID.randomUUID().toString(),
                            bodyWeight = data?.rfbocWeightKg,
                            age = data?.rfbocAge,
                            height = data?.rfbocHeightCm,
                            activityLevel = data?.rfbocActivityLevel,
                            dietCategory = data?.rfbocDietType,
                            hasGastricIssue = data?.rfbocHistoryOfGastritisOrGerd,
                            foodPreference = it?.favoriteFoodName?.ffnName,
                            createdAt = createdAt,
                            dietTime = dietTime,
                        ),
                    )
                    historyViewModel.addHistory(historyResponse)
                }
                index += 1
            }

            is ResultState.Error -> {
                showLoading(false)
                ToastUtil.showToast(this, getString(R.string.error_get_user))
            }

            is ResultState.Initial -> {}

        }
    }

    private fun handleGetHistoryResult(result: ResultState<HistoryResponse?>) {
        when (result) {
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> {
                val it = result.data
                val index = getTodayDataFromPerDay(it)
                if (index != -1) {
                    val perDay = it?.perDay?.get(index)
                    val surveyRequest = SurveyRequest(
                        age = perDay?.age ?: 0,
                        height = perDay?.height ?: 0f,
                        weight = perDay?.bodyWeight ?: 0f,
                        gender = getGenderCode(it?.gender.toString()),
                        activity_level = perDay?.activityLevel.toString()
                            .split(" ")[0].toIntOrNull() ?: 1,
                        diet_category = perDay?.dietCategory
                            ?: DietCategory.KETO.toString(),
                        has_gastric_issue = perDay?.hasGastricIssue ?: "false",
                        food_preference = perDay?.foodPreference ?: emptyList()
                    )
                    surveyViewModel.getSurveyResult(surveyRequest)
                }
            }

            is ResultState.Error -> {
                showLoading(false)
                ToastUtil.showToast(this, getString(R.string.error_get_user))
            }

            is ResultState.Initial -> {}

        }
    }


    private fun showMealTimePopup() {
        val bindingPopup = PopUpCheckInBinding.inflate(LayoutInflater.from(this))
        val breakfastTimes = resources.getStringArray(R.array.breakfast_times)
        val launchTimes = resources.getStringArray(R.array.launch_times)
        val dinnerTimes = resources.getStringArray(R.array.dinner_times)

        val breakfastAdapter =
            ArrayAdapter(this, R.layout.dropdown_item, breakfastTimes)
        val launchAdapter = ArrayAdapter(this, R.layout.dropdown_item, launchTimes)
        val dinnerAdapter = ArrayAdapter(this, R.layout.dropdown_item, dinnerTimes)

        with(bindingPopup) {
            dropdownBreakfastTime.setText(breakfastTimes[0])
            dropdownLunchTime.setText(launchTimes[0])
            dropdownDinnerTime.setText(dinnerTimes[0])

            dropdownBreakfastTime.setAdapter(breakfastAdapter)
            dropdownLunchTime.setAdapter(launchAdapter)
            dropdownDinnerTime.setAdapter(dinnerAdapter)

            val alertDialog = AlertDialog.Builder(this@MainActivity)
                .setView(root)
                .setCancelable(false)
                .setPositiveButton("Check-in") { dialog, _ ->
                    val breakfastTime = dropdownBreakfastTime.text.toString()
                    val lunchTime = dropdownLunchTime.text.toString()
                    val dinnerTime = dropdownDinnerTime.text.toString()

                    saveMealTimes(breakfastTime, lunchTime, dinnerTime)
                    lifecycleScope.launch {
                        dailyCheckInPreference.saveCheckInDate()
                    }
                    dialog.dismiss()
                }
                .create()
            alertDialog.show()

            alertDialog.window?.setBackgroundDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.card_background,
                    theme
                )
            )
        }
    }

    private fun saveMealTimes(
        breakfastTime: String,
        lunchTime: String,
        dinnerTime: String
    ) {
        val sharedPreferences = getSharedPreferences("MealTimes", MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("Breakfast", breakfastTime)
            putString("Lunch", lunchTime)
            putString("Dinner", dinnerTime)
            apply()
        }
    }

    private fun getAppThemes() {
        settingsViewModel.getThemeSettings()
            .observe(this@MainActivity) { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
    }

    private fun handleGetUser(result: ResultState<UserEntity?>) {
        when (result) {
            is ResultState.Loading -> {}
            is ResultState.Success -> {
                historyResponse.userId = result.data?.id
                viewModel.setCurrentUser(result.data)
            }

            is ResultState.Error -> {
                ToastUtil.showToast(this, getString(R.string.error_get_user))
            }

            is ResultState.Initial -> {}

        }
    }

    private fun handleGetUserSession(result: ResultState<UserLocalEntity>) {
        when (result) {
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> {
                if (!result.data.isLogin) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    viewModel.getCurrentUser()
                }
                showLoading(false)
            }

            is ResultState.Error -> {
                showLoading(false)
                ToastUtil.showToast(this, getString(R.string.error_login))
            }

            is ResultState.Initial -> {}

        }
    }

    private fun showLoading(isLoading: Boolean) {
//        with(binding) {
//            if (isLoading) {
//                progressBar.visibility = View.VISIBLE
//            } else {
//                progressBar.visibility = View.GONE
//            }
//        }

    }

    private fun bottomNavScrollAnimation() {
        with(binding) {
            nestedScrollView.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                    if (scrollY > oldScrollY) {
                        bottomNavCard.animate()
                            .translationY(bottomNavCard.height.toFloat() + 70)
                            .setInterpolator(
                                DecelerateInterpolator()
                            ).start()
                    } else if (scrollY < oldScrollY) {
                        bottomNavCard.animate().translationY(0f)
                            .setInterpolator(DecelerateInterpolator()).start()
                    }
                },
            )
        }
    }
}