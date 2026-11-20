package com.veneca.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Paleta Oficial iavnzla (Unión Binance P2P + Alibaba + Cuotas)
val IaVnzlaPurple = Color(0xFF6366F1) // Indigo/Purple principal
val IaVnzlaPurpleDark = Color(0xFF4338CA) // Indigo 700
val IaVnzlaPurpleDeep = Color(0xFF312E81) // Indigo 900
val IaVnzlaPurpleLight = Color(0xFFEEF2FF) // Indigo 50
val IaVnzlaPurpleBorder = Color(0xFFC7D2FE)

val VenecaGreen = Color(0xFF10B981) // Emerald 500
val VenecaGreenDark = Color(0xFF047857) // Emerald 700
val VenecaGreenLight = Color(0xFFD1FAE5) // Emerald 100

val VenecaAmber = Color(0xFFF59E0B) // Binance Gold / Amber 500
val VenecaAmberLight = Color(0xFFFEF3C7) // Amber 100
val VenecaAmberDark = Color(0xFFB45309)

val VenecaIndigo = IaVnzlaPurple
val VenecaIndigoLight = IaVnzlaPurpleLight

val VenecaSlateDark = Color(0xFF0F172A) // Slate 900
val VenecaSlate = Color(0xFF334155) // Slate 700
val VenecaMuted = Color(0xFF64748B) // Slate 500
val VenecaBorder = Color(0xFFE2E8F0) // Slate 200
val VenecaBackground = Color(0xFFF8FAFC) // Slate 50
val VenecaSurface = Color(0xFFFFFFFF)

// Gradientes Oficiales iavnzla
val IaVnzlaBannerGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED), Color(0xFF6366F1))
)

val IaVnzlaCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF312E81), Color(0xFF1E1B4B))
)

val IaVnzlaGoldGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFF59E0B), Color(0xFFD97706))
)

private val LightColorScheme = lightColorScheme(
    primary = IaVnzlaPurple,
    onPrimary = Color.White,
    primaryContainer = IaVnzlaPurpleLight,
    onPrimaryContainer = IaVnzlaPurpleDark,
    secondary = VenecaGreen,
    onSecondary = Color.White,
    secondaryContainer = VenecaGreenLight,
    onSecondaryContainer = VenecaGreenDark,
    tertiary = VenecaAmber,
    onTertiary = Color.Black,
    background = VenecaBackground,
    surface = VenecaSurface,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurface = VenecaSlateDark,
    onSurfaceVariant = VenecaMuted,
    outline = VenecaBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8),
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFEEF2FF),
    secondary = Color(0xFF34D399),
    onSecondary = Color(0xFF064E3B),
    secondaryContainer = Color(0xFF065F46),
    onSecondaryContainer = Color(0xFFD1FAE5),
    tertiary = VenecaAmber,
    onTertiary = Color.Black,
    background = Color(0xFF0B1120),
    surface = Color(0xFF131D31),
    surfaceVariant = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155)
)

@Composable
fun VenecaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
