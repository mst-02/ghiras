package com.islamic.noor.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// High-End Luxury Islamic Palette
val EmeraldDeep = Color(0xFF072B22)
val EmeraldMedium = Color(0xFF0D3F33)
val EmeraldLight = Color(0xFFEAF3EF)

val GoldChampagne = Color(0xFFC5A059)
val GoldLight = Color(0xFFF2E7D0)
val GoldDark = Color(0xFF9E7C3A)
val GoldBright = Color(0xFFDFB663)

// Semantic Aliases
val GreenPrimary = EmeraldDeep
val GreenDark = EmeraldDeep
val GreenLight = EmeraldLight
val GoldAccent = GoldChampagne
val GreenPrimaryDark = EmeraldDeep

// Neutral Backgrounds
val SurfaceLight = Color(0xFFFFFFFF)
val BackgroundLight = Color(0xFFF6F8F7)
val CardBorderLight = Color(0xFFE2EBE6)

val TextPrimary = Color(0xFF14211D)
val TextSecondary = Color(0xFF53665F)
val TextMuted = Color(0xFF869991)
val TextPrimaryLight = TextPrimary

// High-contrast Dark Theme Colors (Clean & Ultra-Readable)
val BackgroundDark = Color(0xFF0D1210)
val SurfaceDark = Color(0xFF161D1A)
val CardBorderDark = Color(0xFF283530)
val TextPrimaryDark = Color(0xFFF2EFEB)
val TextSecondaryDark = Color(0xFFAAB8B2)

// Quran Reading Backgrounds
val QuranBgLight = Color(0xFFFFFFFF)
val QuranBgSepia = Color(0xFFFBF7EE)
val QuranTextSepia = Color(0xFF231F1A)
val QuranBgDark = Color(0xFF131816)
val QuranTextDark = Color(0xFFE2E7E4)

// Adaptive Helpers for Guaranteed High-Contrast
@Composable
fun titleColor(): Color = if (isDarkThemeActive()) GoldBright else EmeraldDeep

@Composable
fun bodyColor(): Color = if (isDarkThemeActive()) TextPrimaryDark else TextPrimary

@Composable
fun subtextColor(): Color = if (isDarkThemeActive()) TextSecondaryDark else TextSecondary

@Composable
fun cardBackgroundColor(): Color = if (isDarkThemeActive()) SurfaceDark else SurfaceLight

@Composable
fun cardBorderColor(): Color = if (isDarkThemeActive()) CardBorderDark else CardBorderLight

@Composable
fun iconAdaptiveColor(): Color = if (isDarkThemeActive()) GoldChampagne else EmeraldDeep

@Composable
fun buttonAdaptiveColor(): Color = if (isDarkThemeActive()) EmeraldMedium else EmeraldDeep

@Composable
fun isDarkThemeActive(): Boolean = MaterialTheme.colorScheme.background == BackgroundDark
