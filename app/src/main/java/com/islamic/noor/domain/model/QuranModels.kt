package com.islamic.noor.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Surah(
    val id: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val versesCount: Int,
    val revelationType: String, // مكي أو مدني
    val pageNumber: Int
)

@Immutable
data class Verse(
    val id: Int,
    val verseNumber: Int,
    val textUthmani: String,
    val tafseerShort: String = "",
    val surahId: Int = 1,
    val translation: String = ""
)
