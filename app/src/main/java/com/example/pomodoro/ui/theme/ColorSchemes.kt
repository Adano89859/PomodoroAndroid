package com.example.pomodoro.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Colores para tema Fresita 🍓
private val FresitaPink = Color(0xFFFF6B9D)
private val FresitaPinkDark = Color(0xFFE85285)
private val FresitaRed = Color(0xFFFF4081)
private val FresitaCream = Color(0xFFFFF0F5)
private val FresitaPeach = Color(0xFFFFB6C1)

// Colores para tema Oceánico 🌊
private val OceanBlue = Color(0xFF0288D1)
private val OceanDeep = Color(0xFF01579B)
private val OceanLightBlue = Color(0xFF4FC3F7) // RENOMBRADO
private val OceanFoam = Color(0xFFE1F5FE)
private val OceanTeal = Color(0xFF00ACC1)

// Colores para tema Bosque 🌲
private val ForestGreen = Color(0xFF388E3C)
private val ForestDark = Color(0xFF1B5E20)
private val ForestLightGreen = Color(0xFF66BB6A) // RENOMBRADO
private val ForestMint = Color(0xFFE8F5E9)
private val ForestMoss = Color(0xFF4CAF50)

// Colores para tema Sunset 🌅
private val SunsetOrange = Color(0xFFFF6F00)
private val SunsetDeep = Color(0xFFE65100)
private val SunsetPeach = Color(0xFFFFB74D)
private val SunsetCream = Color(0xFFFFF3E0)
private val SunsetPink = Color(0xFFFF8A65)

// Colores para tema Morado 💜
private val PurpleMain = Color(0xFF7B1FA2)
private val PurpleDark = Color(0xFF4A148C)
private val PurpleLightShade = Color(0xFFBA68C8) // RENOMBRADO
private val PurplePale = Color(0xFFF3E5F5)
private val PurpleVivid = Color(0xFF9C27B0)

object AppColorSchemes {

    // TEMA FRESITA 🍓 (Modo Claro)
    val FresitaLight = lightColorScheme(
        primary = FresitaPink,
        onPrimary = Color.White,
        primaryContainer = FresitaCream,
        onPrimaryContainer = FresitaPinkDark,

        secondary = FresitaPeach,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFFE4E1),
        onSecondaryContainer = FresitaRed,

        tertiary = FresitaRed,
        onTertiary = Color.White,

        background = Color(0xFFFFFAFA),
        onBackground = Color(0xFF2D2D2D),

        surface = Color.White,
        onSurface = Color(0xFF2D2D2D),
        surfaceVariant = FresitaCream,
        onSurfaceVariant = Color(0xFF5D5D5D),

        error = Color(0xFFD32F2F),
        onError = Color.White
    )

    // TEMA FRESITA OSCURO 🍓🌙
    val FresitaDark = darkColorScheme(
        primary = FresitaPink,
        onPrimary = Color(0xFF2D0011),
        primaryContainer = Color(0xFF5D1F33),
        onPrimaryContainer = FresitaPeach,

        secondary = FresitaPeach,
        onSecondary = Color(0xFF2D0011),
        secondaryContainer = Color(0xFF4D2633),
        onSecondaryContainer = Color(0xFFFFD4DD),

        tertiary = FresitaRed,
        onTertiary = Color.White,

        background = Color(0xFF1C1B1E),
        onBackground = Color(0xFFE6E1E6),

        surface = Color(0xFF2B2930),
        onSurface = Color(0xFFE6E1E6),
        surfaceVariant = Color(0xFF3E3A40),
        onSurfaceVariant = Color(0xFFCAC5CB),

        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005)
    )

    // TEMA OCEÁNICO 🌊
    val OceanLight = lightColorScheme(
        primary = OceanBlue,
        onPrimary = Color.White,
        primaryContainer = OceanFoam,
        onPrimaryContainer = OceanDeep,

        secondary = OceanTeal,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFB2EBF2),
        onSecondaryContainer = Color(0xFF006064),

        tertiary = OceanLightBlue,
        onTertiary = Color.White,

        background = Color(0xFFF5FCFF),
        onBackground = Color(0xFF1A1C1E),

        surface = Color.White,
        onSurface = Color(0xFF1A1C1E),
        surfaceVariant = OceanFoam,
        onSurfaceVariant = Color(0xFF44474E),

        error = Color(0xFFD32F2F),
        onError = Color.White
    )

    val OceanDark = darkColorScheme(
        primary = OceanLightBlue,
        onPrimary = OceanDeep,
        primaryContainer = Color(0xFF004D66),
        onPrimaryContainer = Color(0xFFB3E5FC),

        secondary = OceanTeal,
        onSecondary = Color(0xFF003842),
        secondaryContainer = Color(0xFF005662),
        onSecondaryContainer = Color(0xFFB2EBF2),

        background = Color(0xFF0D1F2D),
        onBackground = Color(0xFFE1E2E5),

        surface = Color(0xFF1A2A3A),
        onSurface = Color(0xFFE1E2E5),
        surfaceVariant = Color(0xFF2A3A4A),
        onSurfaceVariant = Color(0xFFC4C6CF)
    )

    // TEMA BOSQUE 🌲
    val ForestLight = lightColorScheme(
        primary = ForestGreen,
        onPrimary = Color.White,
        primaryContainer = ForestMint,
        onPrimaryContainer = ForestDark,

        secondary = ForestMoss,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFC8E6C9),
        onSecondaryContainer = Color(0xFF1B5E20),

        tertiary = ForestLightGreen,
        onTertiary = Color.White,

        background = Color(0xFFF1F8F4),
        onBackground = Color(0xFF1A1C1E),

        surface = Color.White,
        onSurface = Color(0xFF1A1C1E),
        surfaceVariant = ForestMint,
        onSurfaceVariant = Color(0xFF44474E)
    )

    val ForestDarkScheme = darkColorScheme(
        primary = ForestLightGreen,
        onPrimary = ForestDark,
        primaryContainer = Color(0xFF1B5E20),
        onPrimaryContainer = Color(0xFFC8E6C9),

        secondary = ForestMoss,
        onSecondary = Color(0xFF0D3818),
        secondaryContainer = Color(0xFF2E7D32),
        onSecondaryContainer = Color(0xFFDCEFDC),

        background = Color(0xFF121212),
        onBackground = Color(0xFFE1E2E5),

        surface = Color(0xFF1E1E1E),
        onSurface = Color(0xFFE1E2E5),
        surfaceVariant = Color(0xFF2A3A2E),
        onSurfaceVariant = Color(0xFFC4C6CF)
    )

    // TEMA SUNSET 🌅
    val SunsetLight = lightColorScheme(
        primary = SunsetOrange,
        onPrimary = Color.White,
        primaryContainer = SunsetCream,
        onPrimaryContainer = SunsetDeep,

        secondary = SunsetPink,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFFE0B2),
        onSecondaryContainer = Color(0xFFE65100),

        tertiary = SunsetPeach,
        onTertiary = Color.White,

        background = Color(0xFFFFFBF5),
        onBackground = Color(0xFF1A1C1E),

        surface = Color.White,
        onSurface = Color(0xFF1A1C1E),
        surfaceVariant = SunsetCream,
        onSurfaceVariant = Color(0xFF44474E)
    )

    val SunsetDark = darkColorScheme(
        primary = SunsetPeach,
        onPrimary = SunsetDeep,
        primaryContainer = Color(0xFFBF360C),
        onPrimaryContainer = Color(0xFFFFE0B2),

        secondary = SunsetPink,
        onSecondary = Color(0xFF4E2600),
        secondaryContainer = Color(0xFF8D4E00),
        onSecondaryContainer = Color(0xFFFFDDB3),

        background = Color(0xFF1C1410),
        onBackground = Color(0xFFE6E1DC),

        surface = Color(0xFF2A221C),
        onSurface = Color(0xFFE6E1DC),
        surfaceVariant = Color(0xFF3A2E24),
        onSurfaceVariant = Color(0xFFD0C4BA)
    )

    // TEMA MORADO 💜
    val PurpleLight = lightColorScheme(
        primary = PurpleMain,
        onPrimary = Color.White,
        primaryContainer = PurplePale,
        onPrimaryContainer = PurpleDark,

        secondary = PurpleVivid,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFE1BEE7),
        onSecondaryContainer = Color(0xFF4A148C),

        tertiary = PurpleLightShade,
        onTertiary = Color.White,

        background = Color(0xFFFAF8FC),
        onBackground = Color(0xFF1A1C1E),

        surface = Color.White,
        onSurface = Color(0xFF1A1C1E),
        surfaceVariant = PurplePale,
        onSurfaceVariant = Color(0xFF44474E)
    )

    val PurpleDarkScheme = darkColorScheme(
        primary = PurpleLightShade,
        onPrimary = PurpleDark,
        primaryContainer = Color(0xFF6A1B9A),
        onPrimaryContainer = Color(0xFFE1BEE7),

        secondary = PurpleVivid,
        onSecondary = Color(0xFF38006B),
        secondaryContainer = Color(0xFF6A1B9A),
        onSecondaryContainer = Color(0xFFF3E5F5),

        background = Color(0xFF1C1520),
        onBackground = Color(0xFFE6E0E9),

        surface = Color(0xFF2B2430),
        onSurface = Color(0xFFE6E0E9),
        surfaceVariant = Color(0xFF3A3040),
        onSurfaceVariant = Color(0xFFCAC4CF)
    )
}