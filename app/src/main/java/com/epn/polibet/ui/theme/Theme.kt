package com.epn.polibet.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Colores inspirados en plataformas profesionales de apuestas
private val PoliBetPrimary = Color(0xFF00D4FF) // Azul cyan vibrante
private val PoliBetPrimaryDark = Color(0xFF0099CC)
private val PoliBetSecondary = Color(0xFF00FF88) // Verde éxito
private val PoliBetTertiary = Color(0xFFFF6B35) // Naranja energético
private val PoliBetError = Color(0xFFFF4757)
private val PoliBetWarning = Color(0xFFFFD700)
private val PoliBetSuccess = Color(0xFF2ED573)

// Colores de fondo y superficie
private val DarkBackground = Color(0xFF0A0E1A) // Azul muy oscuro
private val DarkSurface = Color(0xFF1A1F2E) // Azul grisáceo oscuro
private val DarkSurfaceVariant = Color(0xFF2A2F3E)
private val CardBackground = Color(0xFF1E2332)

// Gradientes para deportes
val FootballGradient = listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
val BasketballGradient = listOf(Color(0xFFFF5722), Color(0xFFD84315))
val TennisGradient = listOf(Color(0xFFFF9800), Color(0xFFE65100))
val VolleyballGradient = listOf(Color(0xFF9C27B0), Color(0xFF6A1B9A))
val BaseballGradient = listOf(Color(0xFF3F51B5), Color(0xFF283593))
val BoxingGradient = listOf(Color(0xFFF44336), Color(0xFFC62828))

private val DarkColorScheme = darkColorScheme(
    primary = PoliBetPrimary,
    onPrimary = Color.Black,
    primaryContainer = PoliBetPrimaryDark,
    onPrimaryContainer = Color.White,

    secondary = PoliBetSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF004D40),
    onSecondaryContainer = PoliBetSecondary,

    tertiary = PoliBetTertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF8B2500),
    onTertiaryContainer = PoliBetTertiary,

    error = PoliBetError,
    onError = Color.White,
    errorContainer = Color(0xFF4D1F1F),
    onErrorContainer = PoliBetError,

    background = DarkBackground,
    onBackground = Color.White,

    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFB0BEC5),

    outline = Color(0xFF37474F),
    outlineVariant = Color(0xFF263238)
)

private val LightColorScheme = lightColorScheme(
    primary = PoliBetPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE1F5FE),
    onPrimaryContainer = PoliBetPrimaryDark,

    secondary = Color(0xFF00C853),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E8),
    onSecondaryContainer = Color(0xFF00C853),

    tertiary = PoliBetTertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE0B2),
    onTertiaryContainer = PoliBetTertiary,

    error = PoliBetError,
    onError = Color.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = PoliBetError,

    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A1A1A),

    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF666666),

    outline = Color(0xFFE0E0E0),
    outlineVariant = Color(0xFFF0F0F0)
)

@Composable
fun PoliBetTheme(
    darkTheme: Boolean = true, // Por defecto tema oscuro como Stake
    dynamicColor: Boolean = false, // Deshabilitado para mantener nuestros colores
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
