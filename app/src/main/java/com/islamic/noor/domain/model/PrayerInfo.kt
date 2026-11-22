package com.islamic.noor.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class PrayerTimeItem(
    val nameArabic: String,
    val timeFormatted: String,
    val isNext: Boolean = false
) {
    val name: String get() = nameArabic
    val time: String get() = timeFormatted
    val isActive: Boolean get() = isNext
}

@Immutable
data class QiblaData(
    val qiblaDirection: Float, // Direction towards Kaaba from North (0 - 360)
    val compassHeading: Float, // Current device heading from North
    val needleAngle: Float // Calculated needle angle (qiblaDirection - compassHeading)
)
