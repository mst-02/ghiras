package com.islamic.noor.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GoldChampagne,
    onPrimary = BackgroundDark,
    secondary = GoldBright,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldDeep,
    onPrimary = SurfaceLight,
    secondary = GoldChampagne,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight
)

@Composable
fun NoorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = EmeraldDeep.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
