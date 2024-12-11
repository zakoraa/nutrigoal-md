package com.c242pS371.nutrigoal.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleOwner
import com.c242pS371.nutrigoal.ui.settings.SettingsViewModel

class ThemeUtil(
    private val lifecycleOwner: LifecycleOwner,
    private val settingsViewModel: SettingsViewModel
) {
    fun getAppThemes() {
        settingsViewModel.getThemeSettings()
            .observe(lifecycleOwner) { isDarkModeActive: Boolean? ->
                if (isDarkModeActive == null) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
    }
}