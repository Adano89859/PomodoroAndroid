package com.example.pomodoro.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.pomodoro.data.model.AppTheme

@Composable
fun PomodoroTheme(
    appTheme: AppTheme = AppTheme.FRESITA_LIGHT,
    dynamicColor: Boolean = false, // Deshabilitado por defecto para usar nuestros temas
    content: @Composable () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    val colorScheme = when {
        // Material You (Android 12+) - solo si se habilita explícitamente
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Temas personalizados
        else -> getColorSchemeForTheme(appTheme, isDarkTheme)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                !isThemeDark(appTheme, isDarkTheme)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
private fun getColorSchemeForTheme(theme: AppTheme, systemIsDark: Boolean): ColorScheme {
    return when (theme) {
        AppTheme.FRESITA_LIGHT -> AppColorSchemes.FresitaLight
        AppTheme.FRESITA_DARK -> AppColorSchemes.FresitaDark
        AppTheme.OCEAN_LIGHT -> AppColorSchemes.OceanLight
        AppTheme.OCEAN_DARK -> AppColorSchemes.OceanDark
        AppTheme.FOREST_LIGHT -> AppColorSchemes.ForestLight
        AppTheme.FOREST_DARK -> AppColorSchemes.ForestDarkScheme
        AppTheme.SUNSET_LIGHT -> AppColorSchemes.SunsetLight
        AppTheme.SUNSET_DARK -> AppColorSchemes.SunsetDark
        AppTheme.PURPLE_LIGHT -> AppColorSchemes.PurpleLight
        AppTheme.PURPLE_DARK -> AppColorSchemes.PurpleDarkScheme
        AppTheme.SYSTEM -> {
            // Si es "Sistema", usar Fresita según el modo del sistema
            if (systemIsDark) AppColorSchemes.FresitaDark else AppColorSchemes.FresitaLight
        }
    }
}

private fun isThemeDark(theme: AppTheme, systemIsDark: Boolean): Boolean {
    return when (theme) {
        AppTheme.FRESITA_DARK,
        AppTheme.OCEAN_DARK,
        AppTheme.FOREST_DARK,
        AppTheme.SUNSET_DARK,
        AppTheme.PURPLE_DARK -> true
        AppTheme.SYSTEM -> systemIsDark
        else -> false
    }
}