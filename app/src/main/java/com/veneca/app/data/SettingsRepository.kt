package com.veneca.app.data

import android.content.Context
import com.veneca.app.model.AppThemeMode
import com.veneca.app.model.CurrencyDisplayMode
import com.veneca.app.model.UserSettings

class SettingsRepository(context: Context) {
    private val preferences = context.getSharedPreferences("iavnzla_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME = "app_theme_mode"
        private const val KEY_CURRENCY = "currency_display_mode"
        private const val KEY_BCV_RATE = "custom_bcv_rate"
        private const val KEY_HIGH_PERF = "high_performance_mode"
        private const val KEY_SHOW_IACUOTAS = "show_iacuotas_badges"
        private const val KEY_NOTIF = "enable_notifications"
        private const val KEY_ACCEPTED_TERMS = "has_accepted_terms_iavnzla"
        private const val KEY_IS_LOGGED_IN = "is_user_logged_in_iavnzla"
    }

    fun hasAcceptedTerms(): Boolean {
        return preferences.getBoolean(KEY_ACCEPTED_TERMS, false)
    }

    fun setAcceptedTerms(accepted: Boolean) {
        preferences.edit().putBoolean(KEY_ACCEPTED_TERMS, accepted).apply()
    }

    fun isUserLoggedIn(): Boolean {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setUserLoggedIn(loggedIn: Boolean) {
        preferences.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply()
    }

    fun getSettings(): UserSettings {
        val themeStr = preferences.getString(KEY_THEME, AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name
        val currencyStr = preferences.getString(KEY_CURRENCY, CurrencyDisplayMode.DUAL.name) ?: CurrencyDisplayMode.DUAL.name
        val bcvRate = preferences.getFloat(KEY_BCV_RATE, 918.77f).toDouble()
        val highPerf = preferences.getBoolean(KEY_HIGH_PERF, false)
        val showIaCuotas = preferences.getBoolean(KEY_SHOW_IACUOTAS, true)
        val notif = preferences.getBoolean(KEY_NOTIF, true)

        val theme = runCatching { AppThemeMode.valueOf(themeStr) }.getOrDefault(AppThemeMode.SYSTEM)
        val currency = runCatching { CurrencyDisplayMode.valueOf(currencyStr) }.getOrDefault(CurrencyDisplayMode.DUAL)

        return UserSettings(
            themeMode = theme,
            currencyMode = currency,
            customBcvRate = bcvRate,
            highPerformanceMode = highPerf,
            showIaCuotasBadges = showIaCuotas,
            enableNotifications = notif
        )
    }

    fun saveSettings(settings: UserSettings) {
        preferences.edit()
            .putString(KEY_THEME, settings.themeMode.name)
            .putString(KEY_CURRENCY, settings.currencyMode.name)
            .putFloat(KEY_BCV_RATE, settings.customBcvRate.toFloat())
            .putBoolean(KEY_HIGH_PERF, settings.highPerformanceMode)
            .putBoolean(KEY_SHOW_IACUOTAS, settings.showIaCuotasBadges)
            .putBoolean(KEY_NOTIF, settings.enableNotifications)
            .apply()
    }
}
