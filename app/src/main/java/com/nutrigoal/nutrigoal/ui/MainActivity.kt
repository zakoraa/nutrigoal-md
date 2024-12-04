package com.nutrigoal.nutrigoal.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.data.ResultState
import com.nutrigoal.nutrigoal.data.local.entity.User
import com.nutrigoal.nutrigoal.data.remote.entity.UserEntity
import com.nutrigoal.nutrigoal.databinding.ActivityMainBinding
import com.nutrigoal.nutrigoal.databinding.PopUpCheckInBinding
import com.nutrigoal.nutrigoal.ui.auth.AuthViewModel
import com.nutrigoal.nutrigoal.ui.auth.LoginActivity
import com.nutrigoal.nutrigoal.ui.settings.SettingsViewModel
import com.nutrigoal.nutrigoal.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: AuthViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController =
            findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

        bottomNavScrollAnimation()
        setUpView()
    }

    private fun setUpView() {
        getAppThemes()
        viewModel.getSession()

        lifecycleScope.launch {
            viewModel.userSessionState.collect { result ->
                handleGetUserSession(result)
            }
        }

        lifecycleScope.launch {
            viewModel.currentUserState.collect { result ->
                handleGetUser(result)
            }
        }

        showMealTimePopup()
    }

    private fun showMealTimePopup() {
        val bindingPopup = PopUpCheckInBinding.inflate(LayoutInflater.from(this))
        val breakfastTimes = resources.getStringArray(R.array.breakfast_times)
        val launchTimes = resources.getStringArray(R.array.launch_times)
        val dinnerTimes = resources.getStringArray(R.array.dinner_times)

        val breakfastAdapter = ArrayAdapter(this, R.layout.dropdown_item, breakfastTimes)
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
                    dialog.dismiss()
                }
                .create()

            alertDialog.show()
        }
    }

    private fun saveMealTimes(breakfastTime: String, lunchTime: String, dinnerTime: String) {
        val sharedPreferences = getSharedPreferences("MealTimes", Context.MODE_PRIVATE)
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
            is ResultState.Loading -> showLoading(true)
            is ResultState.Success -> {
                viewModel.setCurrentUser(result.data)
            }

            is ResultState.Error -> {
                showLoading(false)
                ToastUtil.showToast(this, getString(R.string.error_get_user))
            }

            is ResultState.Initial -> {}

        }
    }

    private fun handleGetUserSession(result: ResultState<User>) {
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
        with(binding) {
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                nestedScrollView.visibility = View.INVISIBLE
            } else {
                progressBar.visibility = View.GONE
                nestedScrollView.visibility = View.VISIBLE
            }
        }

    }

    private fun bottomNavScrollAnimation() {
        with(binding) {
            nestedScrollView.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                    if (scrollY > oldScrollY) {
                        bottomNavCard.animate()
                            .translationY(bottomNavCard.height.toFloat() + 70).setInterpolator(
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