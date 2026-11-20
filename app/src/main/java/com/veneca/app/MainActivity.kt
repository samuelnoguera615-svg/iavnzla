package com.veneca.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.veneca.app.model.AppThemeMode
import com.veneca.app.ui.MarketplaceViewModel
import com.veneca.app.ui.screen.MarketplaceScreen
import com.veneca.app.ui.theme.VenecaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MarketplaceViewModel = viewModel()
            val state by viewModel.uiState.collectAsState()

            val isDark = when (state.settings.themeMode) {
                AppThemeMode.DARK -> true
                AppThemeMode.LIGHT -> false
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            VenecaTheme(darkTheme = isDark) {
                MarketplaceScreen(viewModel = viewModel)
            }
        }
    }
}
