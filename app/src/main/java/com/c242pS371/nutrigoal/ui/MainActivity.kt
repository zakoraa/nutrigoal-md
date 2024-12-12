package com.c242pS371.nutrigoal.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.data.ResultState
import com.c242pS371.nutrigoal.data.local.entity.UserLocalEntity
import com.c242pS371.nutrigoal.data.remote.entity.DietCategory
import com.c242pS371.nutrigoal.data.remote.entity.FoodRecommendationItem
import com.c242pS371.nutrigoal.data.remote.entity.PerDayItem
import com.c242pS371.nutrigoal.data.remote.entity.SurveyRequest
import com.c242pS371.nutrigoal.data.remote.entity.UserEntity
import com.c242pS371.nutrigoal.data.remote.response.HistoryResponse
import com.c242pS371.nutrigoal.data.remote.response.SurveyResponse
import com.c242pS371.nutrigoal.databinding.ActivityMainBinding
import com.c242pS371.nutrigoal.databinding.PopUpCheckInBinding
import com.c242pS371.nutrigoal.ui.auth.AuthViewModel
import com.c242pS371.nutrigoal.ui.auth.LoginActivity
import com.c242pS371.nutrigoal.ui.auth.LoginActivity.Companion.EXTRA_SURVEY
import com.c242pS371.nutrigoal.ui.common.HistoryViewModel
import com.c242pS371.nutrigoal.ui.plan_diet.AddFoodRecommendationActivity
import com.c242pS371.nutrigoal.ui.plan_diet.PlanDietFragment.Companion.EXTRA_PER_DAY
import com.c242pS371.nutrigoal.ui.plan_diet.PlanDietFragment.Companion.EXTRA_PLAN_DIET_USER
import com.c242pS371.nutrigoal.ui.plan_diet.PlanDietFragment.Companion.IS_NULL
import com.c242pS371.nutrigoal.ui.settings.SettingsViewModel
import com.c242pS371.nutrigoal.ui.survey.Survey1Activity
import com.c242pS371.nutrigoal.ui.survey.SurveyViewModel
import com.c242pS371.nutrigoal.utils.AppUtil.getDietTimeDataFromPerDay
import com.c242pS371.nutrigoal.utils.AppUtil.getLastWeightFromPerDay
import com.c242pS371.nutrigoal.utils.AppUtil.getTodayDataFromPerDay
import com.c242pS371.nutrigoal.utils.DateFormatter
import com.c242pS371.nutrigoal.utils.InputValidator
import com.c242pS371.nutrigoal.utils.ThemeUtil
import com.c242pS371.nutrigoal.utils.ToastUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: AuthViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val surveyViewModel: SurveyViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()
    private val historyResponse = HistoryResponse()
    private var userEntity: UserEntity? = null
    private val inputValidator: InputValidator by lazy { InputValidator(this@MainActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeUtil =
            ThemeUtil(lifecycleOwner = this@MainActivity, settingsViewModel = settingsViewModel)
        themeUtil.getAppThemes()

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
        setUpAction()
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

    }

    private fun handleAddHistory(result: ResultState<Unit?>) {
        when (result) {
            is ResultState.Loading -> {}
            is ResultState.Success -> {
                historyViewModel.getHistoryResult(Firebase.auth.currentUser?.uid ?: "")
            }

            is ResultState.Error -> {}
            is ResultState.Initial -> {}
        }
    }

    private fun getSurveyResult() {
        userEntity = if (Build.VERSION.SDK_INT >= 33) {
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
                age = userEntity?.age ?: 0,
                height = userEntity?.height ?: 0f,
                weight = userEntity?.bodyWeight ?: 0f,
                gender = userEntity?.gender ?: true,
                activity_level = userEntity?.activityLevel ?: 1,
                diet_category = userEntity?.dietCategory ?: DietCategory.VEGAN.toString(),
                has_gastric_issue = userEntity?.hasGastricIssue ?: false,
                food_preference = userEntity?.foodPreference ?: emptyList()
            )
            surveyViewModel.getSurveyResult(surveyRequest)
        } else {
            historyViewModel.getHistoryResult(Firebase.auth.currentUser?.uid ?: "")
        }
    }

    private fun handleGetSurveyResult(result: ResultState<SurveyResponse?>) {
        when (result) {
            is ResultState.Loading -> {}
            is ResultState.Success -> {
                val calendar = Calendar.getInstance()
                val dateFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)

                val createdAt = dateFormat.format(calendar.time)
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                val dietTime = dateFormat.format(calendar.time)
                val it = result.data
                val data = it?.recommendedFoodBasedOnCalories
                historyResponse.gender = data?.rfbocGender

                val foodRecommendationList = it?.recommendedFoodPreference?.map { item ->
                    FoodRecommendationItem(
                        id = item?.rfpId,
                        name = item?.name,
                        calories = item?.calories,
                        protein = item?.proteinG,
                        carbohydrate = item?.carbohydrateG,
                        fat = item?.fatG
                    )
                }
                val perDayId = UUID.randomUUID().toString()
                historyResponse.perDay = listOf(
                    PerDayItem(
                        id = perDayId,
                        bodyWeight = data?.rfbocWeightKg,
                        age = data?.rfbocAge,
                        height = data?.rfbocHeightCm,
                        activityLevel = data?.rfbocActivityLevel,
                        dietCategory = data?.rfbocDietType,
                        hasGastricIssue = data?.rfbocHistoryOfGastritisOrGerd,
                        foodPreference = it?.favoriteFoodName?.ffnName,
                        foodRecommendation = foodRecommendationList,
                        mealSchedule = userEntity?.mealSchedule,
                        calorieNeeds = it?.recommendedFoodBasedOnCalories?.rfbocDailyCalorieNeeds?.toFloatOrNull(),
                        createdAt = createdAt,
                        dietTime = dietTime,
                    ),
                )
                historyViewModel.addHistory(historyResponse)
            }

            is ResultState.Error -> {
                ToastUtil.showToast(this, getString(R.string.error_get_user))
            }

            is ResultState.Initial -> {}

        }
    }

    private fun handleGetHistoryResult(result: ResultState<HistoryResponse?>) {
        when (result) {
            is ResultState.Loading -> {}
            is ResultState.Success -> {
                val it = result.data
                if (it !== null) {
                    val index = getTodayDataFromPerDay(it)
                    if (index != -1) {
                        val perDay = it.perDay?.get(index)
                        val surveyRequest = SurveyRequest(
                            age = perDay?.age ?: 0,
                            height = perDay?.height ?: 0f,
                            weight = perDay?.bodyWeight ?: 0f,
                            gender = it.gender ?: false,
                            activity_level = perDay?.activityLevel ?: 1,
                            diet_category = perDay?.dietCategory
                                ?: DietCategory.KETO.toString(),
                            has_gastric_issue = perDay?.hasGastricIssue ?: false,
                            food_preference = perDay?.foodPreference ?: emptyList()
                        )
                        val lastWeightIndex = getLastWeightFromPerDay(it)
                        val lastWeight = it.perDay?.get(lastWeightIndex)
                        val dietTimeIndex = getDietTimeDataFromPerDay(it)
                        val dietTimePerDay = it.perDay?.get(dietTimeIndex)
                        val sharedPreferences = getSharedPreferences("firstDietTime", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        val calendar = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                        val nowDate = DateFormatter.parseDateToDay(dateFormat.format(calendar.time))
                        val dateTimeDate = DateFormatter.parseDateToDay(dietTimePerDay?.dietTime)

                        if (perDay?.bodyWeight == null || perDay.height == null) {
                            showMealTimePopup(perDay, lastWeight)
                            if (nowDate == dateTimeDate) {
                                editor.putBoolean("isNew", false)
                                editor.apply()
                            } else {
                                editor.putBoolean("isNew", true)
                                editor.apply()
                            }
                        } else {
                            val breakfastTime = dietTimePerDay?.mealSchedule?.breakfastTime
                            val lunchTime = dietTimePerDay?.mealSchedule?.launchTime
                            val dinnerTime = dietTimePerDay?.mealSchedule?.dinnerTime
                            if (nowDate == dateTimeDate) {
                                editor.putBoolean("isNew", false)
                                editor.apply()
                            } else {
                                editor.putBoolean("isNew", true)
                                editor.apply()
                            }
                            saveMealTimes(breakfastTime, lunchTime, dinnerTime)
                        }
                        surveyViewModel.getSurveyResult(surveyRequest)
                    } else {
                        val perDay =
                            it.perDay?.get((it.perDay?.size?.minus(1) ?: 0).coerceAtLeast(0))
                        val user =
                            UserEntity(
                                id = it.userId,
                                height = perDay?.height,
                                bodyWeight = perDay?.bodyWeight,
                                dietCategory = perDay?.dietCategory,
                                hasGastricIssue = perDay?.hasGastricIssue,
                                age = perDay?.age,
                                gender = it.gender,
                                activityLevel = perDay?.activityLevel,
                            )
                        val intent = Intent(this, AddFoodRecommendationActivity::class.java)
                        intent.putExtra(EXTRA_PLAN_DIET_USER, user)
                        intent.putExtra(EXTRA_PER_DAY, perDay)
                        intent.putExtra(IS_NULL, true)
                        startActivity(intent)
                    }
                } else {
                    val userEntity = UserEntity(
                        username = com.google.firebase.ktx.Firebase.auth.currentUser?.displayName,
                        email = com.google.firebase.ktx.Firebase.auth.currentUser?.email,
                        photoProfile = com.google.firebase.ktx.Firebase.auth.currentUser?.photoUrl.toString()
                    )
                    val intent = Intent(this, Survey1Activity::class.java)
                    intent.putExtra(EXTRA_SURVEY, userEntity)
                    startActivity(intent)
                    finish()
                }
            }

            is ResultState.Error -> {
                ToastUtil.showToast(this, getString(R.string.error_get_user))
            }

            is ResultState.Initial -> {}

        }
    }


    private fun showMealTimePopup(perDay: PerDayItem?, previousPerDay: PerDayItem?) {
        val bindingPopup = PopUpCheckInBinding.inflate(LayoutInflater.from(this))

        with(bindingPopup) {
            edHeight.setText(previousPerDay?.height.toString())
            edBodyWeight.setText(previousPerDay?.bodyWeight.toString())
            val alertDialog = AlertDialog.Builder(this@MainActivity)
                .setView(root)
                .setCancelable(false)
                .setPositiveButton("Check-in", null)
                .create()

            alertDialog.show()

            val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {

                val height = edHeight.text?.trim().toString()
                val bodyWeight = edBodyWeight.text?.trim().toString()

                val breakfastTime = perDay?.mealSchedule?.breakfastTime
                val lunchTime = perDay?.mealSchedule?.launchTime
                val dinnerTime = perDay?.mealSchedule?.dinnerTime

                userEntity?.height = height.toFloatOrNull()
                userEntity?.bodyWeight = bodyWeight.toFloatOrNull()

                val heightError =
                    inputValidator.validateInput(edHeight, getString(R.string.height))
                val bodyWeightError =
                    inputValidator.validateInput(edBodyWeight, getString(R.string.body_weight))

                inputValidator.checkValidation(tvErrorHeight, heightError)
                inputValidator.checkValidation(tvErrorBodyWeight, bodyWeightError)

                if (heightError == null && bodyWeightError == null) {
                    historyViewModel.updateUserBodyWeightAndHeight(
                        Firebase.auth.currentUser?.uid ?: "",
                        perDay?.id ?: "",
                        height.toFloat(),
                        bodyWeight.toFloat()
                    )
                    saveMealTimes(breakfastTime, lunchTime, dinnerTime)

                    lifecycleScope.launch {
                        historyViewModel.updateUserBodyWeightAndHeightState.collect { result ->
                            handleUpdateBodyWeightAndHeight(result, alertDialog)
                        }
                    }

                }
            }

            alertDialog.window?.setBackgroundDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.card_background,
                    theme
                )
            )
        }
    }

    private fun handleUpdateBodyWeightAndHeight(
        result: ResultState<Unit?>,
        alertDialog: AlertDialog
    ) {
        when (result) {
            is ResultState.Loading -> {}
            is ResultState.Success -> {
                binding.swipeRefreshLayout.isRefreshing = true
                refreshHistoryData()
                alertDialog.dismiss()

            }

            is ResultState.Error -> {}
            is ResultState.Initial -> {}
        }
    }

    private fun saveMealTimes(
        breakfastTime: String?,
        lunchTime: String?,
        dinnerTime: String?
    ) {
        val sharedPreferences = getSharedPreferences("MealTimes", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.clear()

        if (breakfastTime != null) {
            editor.putString("Breakfast", breakfastTime)
        }

        if (lunchTime != null) {
            editor.putString("Lunch", lunchTime)
        }

        if (dinnerTime != null) {
            editor.putString("Dinner", dinnerTime)
        }

        editor.apply()
    }

    private fun handleGetUser(result: ResultState<UserEntity?>) {
        when (result) {
            is ResultState.Loading -> {}
            is ResultState.Success -> {
                historyResponse.userId = result.data?.id
                viewModel.setCurrentUser(result.data)

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

                lifecycleScope.launch {
                    historyViewModel.addHistoryResponseState.collect {
                        handleAddHistory(it)
                    }
                }
            }

            is ResultState.Error -> ToastUtil.showToast(this, getString(R.string.error_get_user))
            is ResultState.Initial -> {}

        }
    }

    private fun handleGetUserSession(result: ResultState<UserLocalEntity>) {
        when (result) {
            is ResultState.Loading -> {}
            is ResultState.Success -> {
                if (!result.data.isLogin) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    viewModel.getCurrentUser()
                }

            }

            is ResultState.Error -> ToastUtil.showToast(this, getString(R.string.error_login))
            is ResultState.Initial -> {}

        }
    }


    private fun setUpAction() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshHistoryData()
        }
    }

    private fun refreshHistoryData() {
        historyViewModel.getHistoryResult(Firebase.auth.currentUser?.uid ?: "")
        binding.swipeRefreshLayout.isRefreshing = false
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