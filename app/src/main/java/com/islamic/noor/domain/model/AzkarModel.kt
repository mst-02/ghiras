package com.islamic.noor.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class ZekrItem(
    val id: Int,
    val category: String, // أذكار الصباح، أذكار المساء، أذكار النوم، أذكار الاستيقاظ، أذكار بعد الصلاة، أذكاري الخاصة
    val text: String,
    val count: Int,
    val reward: String = "",
    val reference: String = "",
    val isCustom: Boolean = false
) {
    val targetCount: Int get() = count
    val arabicText: String get() = text
    val fadl: String get() = reward
}

@Immutable
data class HadithItem(
    val id: Int,
    val bookName: String,
    val chapter: String,
    val hadithText: String,
    val narrator: String,
    val explanation: String = ""
)
