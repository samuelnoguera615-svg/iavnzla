package com.veneca.app.model

import androidx.compose.runtime.Immutable

enum class AppThemeMode(val label: String) {
    SYSTEM("Seguir el Sistema"),
    LIGHT("Modo Claro"),
    DARK("Modo Oscuro")
}

enum class CurrencyDisplayMode(val label: String) {
    DUAL("Dual ($ USD y Bs.)"),
    USD_ONLY("Solo Dólares ($ USD)"),
    VES_ONLY("Solo Bolívares (Bs. BCV)")
}

@Immutable
data class UserSettings(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val currencyMode: CurrencyDisplayMode = CurrencyDisplayMode.DUAL,
    val customBcvRate: Double = 918.77,
    val highPerformanceMode: Boolean = false,
    val showIaCuotasBadges: Boolean = true,
    val enableNotifications: Boolean = true
)
