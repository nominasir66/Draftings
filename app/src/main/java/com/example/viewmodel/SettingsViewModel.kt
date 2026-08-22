package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.AppSettings
import com.example.data.PreferencesManager
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    val settings: StateFlow<AppSettings> = preferencesManager.settings

    fun setFontFamily(font: String) {
        preferencesManager.updateFontFamily(font)
    }

    fun setFontSize(size: Float) {
        preferencesManager.updateFontSize(size)
    }

    fun setMargin(marginMm: Float) {
        preferencesManager.updateMargin(marginMm)
    }

    fun setLineSpacing(spacing: Float) {
        preferencesManager.updateLineSpacing(spacing)
    }

    fun setDarkMode(darkMode: Boolean?) {
        preferencesManager.updateDarkMode(darkMode)
    }

    fun setMarginGuides(show: Boolean) {
        preferencesManager.updateMarginGuides(show)
    }
}
