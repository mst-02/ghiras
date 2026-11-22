package com.islamic.noor.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class UserProfile(
    val isLoggedIn: Boolean = false,
    val isAdmin: Boolean = false,
    val name: String = "",
    val phone: String = "",
    val totalTasbeeh: Int = 0,
    val totalContestsJoined: Int = 0,
    val notificationsEnabled: Boolean = true,
    val quranFontSize: Float = 24f,
    val quranLineHeight: Float = 1.8f,
    val readingTheme: ReadingTheme = ReadingTheme.SEPIA,
    val appTheme: AppThemeMode = AppThemeMode.DARK,
    val avatarIndex: Int = 0
)

enum class AppThemeMode {
    LIGHT,   // وضع النهار
    DARK,    // وضع الليل
    SYSTEM   // تلقائي
}

enum class ReadingTheme {
    LIGHT,   // وضع النهار
    SEPIA,   // وضع الراحة للعين (ورق مصحف دافئ)
    DARK     // وضع الليل
}
