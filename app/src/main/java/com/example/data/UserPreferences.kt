package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.typography.FontHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppSettings(
    val defaultFontFamily: String = FontHelper.FONT_JAMEEL_NASTALEEQ,
    val defaultFontSizeSp: Float = 18f,
    val defaultMarginMm: Float = 20f,
    val defaultLineSpacing: Float = 1.4f,
    val isDarkMode: Boolean? = null, // null = follow system
    val showMarginGuidesDefault: Boolean = true,
    val autoSaveIntervalSec: Int = 3
)

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("draftings_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun loadSettings(): AppSettings {
        val darkModeSetting = if (prefs.contains(KEY_DARK_MODE)) prefs.getBoolean(KEY_DARK_MODE, false) else null
        return AppSettings(
            defaultFontFamily = prefs.getString(KEY_FONT, FontHelper.FONT_JAMEEL_NASTALEEQ) ?: FontHelper.FONT_JAMEEL_NASTALEEQ,
            defaultFontSizeSp = prefs.getFloat(KEY_FONT_SIZE, 18f),
            defaultMarginMm = prefs.getFloat(KEY_MARGIN, 20f),
            defaultLineSpacing = prefs.getFloat(KEY_LINE_SPACING, 1.4f),
            isDarkMode = darkModeSetting,
            showMarginGuidesDefault = prefs.getBoolean(KEY_MARGIN_GUIDES, true)
        )
    }

    fun updateFontFamily(font: String) {
        prefs.edit().putString(KEY_FONT, font).apply()
        _settings.value = _settings.value.copy(defaultFontFamily = font)
    }

    fun updateFontSize(size: Float) {
        prefs.edit().putFloat(KEY_FONT_SIZE, size).apply()
        _settings.value = _settings.value.copy(defaultFontSizeSp = size)
    }

    fun updateMargin(margin: Float) {
        prefs.edit().putFloat(KEY_MARGIN, margin).apply()
        _settings.value = _settings.value.copy(defaultMarginMm = margin)
    }

    fun updateLineSpacing(spacing: Float) {
        prefs.edit().putFloat(KEY_LINE_SPACING, spacing).apply()
        _settings.value = _settings.value.copy(defaultLineSpacing = spacing)
    }

    fun updateDarkMode(darkMode: Boolean?) {
        if (darkMode == null) {
            prefs.edit().remove(KEY_DARK_MODE).apply()
        } else {
            prefs.edit().putBoolean(KEY_DARK_MODE, darkMode).apply()
        }
        _settings.value = _settings.value.copy(isDarkMode = darkMode)
    }

    fun updateMarginGuides(show: Boolean) {
        prefs.edit().putBoolean(KEY_MARGIN_GUIDES, show).apply()
        _settings.value = _settings.value.copy(showMarginGuidesDefault = show)
    }

    companion object {
        private const val KEY_FONT = "key_default_font"
        private const val KEY_FONT_SIZE = "key_default_font_size"
        private const val KEY_MARGIN = "key_default_margin"
        private const val KEY_LINE_SPACING = "key_default_line_spacing"
        private const val KEY_DARK_MODE = "key_dark_mode"
        private const val KEY_MARGIN_GUIDES = "key_margin_guides"
    }
}
