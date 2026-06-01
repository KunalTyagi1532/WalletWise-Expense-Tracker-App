package com.example.expensetracker.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.expensetracker.utils.CurrencyManager

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs =
        application.getSharedPreferences(
            "theme_prefs",
            Context.MODE_PRIVATE
        )

    var isDarkMode =
        mutableStateOf(
            prefs.getBoolean("dark_mode", false)
        )

    fun toggleTheme() {

        val newValue = !isDarkMode.value

        isDarkMode.value = newValue

        prefs.edit()
            .putBoolean("dark_mode", newValue)
            .apply()
    }
}