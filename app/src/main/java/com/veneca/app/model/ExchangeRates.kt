package com.veneca.app.model

import androidx.compose.runtime.Immutable

@Immutable
data class LiveExchangeRates(
    val bcvRate: Double = 918.77,
    val usdtRate: Double = 1066.78,
    val lastUpdated: String = "Sincronizado",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
