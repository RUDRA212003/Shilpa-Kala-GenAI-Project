package com.example.shilpakalashowcase.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GoldArtisan,
    onPrimary = Color.Black,

    primaryContainer = Brown40,
    onPrimaryContainer = Sand80,

    secondary = BrownGrey80,

    background = Color(0xFF1B1B1B),
    surface = Color(0xFF242424),

    onSurface = Color.White,
    onBackground = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Brown40,
    onPrimary = IvoryLight,

    primaryContainer = GoldArtisan,
    onPrimaryContainer = Brown40,

    secondary = Terracotta40,
    onSecondary = Color.White,

    tertiary = DeepGold,

    background = BackgroundLight,
    surface = SurfaceLight,

    onSurface = Brown40,
    onBackground = Brown40
)

@Composable
fun ShilpaKalaShowcaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme =
        if (darkTheme) DarkColorScheme
        else LightColorScheme

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {

            val activity = view.context as Activity
            val window = activity.window

            window.statusBarColor = colorScheme.background.toArgb()

            WindowCompat
                .getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}