package com.islamic.noor.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class PeaceMessage(
    val id: Int,
    val title: String,
    val text: String,
    val source: String,
    val theme: String // الصبر، الفرج، رحمة الله، تيسير الأمور
)

@Immutable
data class GroupKhatmah(
    val id: String,
    val title: String,
    val totalParts: Int = 30,
    val completedParts: Int = 18,
    val participantsCount: Int = 12,
    val deadlineDate: String = "الجمعة القادمة"
)

@Immutable
data class DeceasedDua(
    val id: Int,
    val title: String,
    val text: String,
    val reference: String = "مأثور من السنة"
)

@Immutable
data class GoodDeedDoor(
    val id: Int,
    val title: String,
    val description: String,
    val rewardHadith: String,
    val source: String
)

@Immutable
data class CharityProject(
    val id: String,
    val title: String,
    val location: String,
    val description: String,
    val targetAmount: Long,
    val collectedAmount: Long,
    val sharePrice: Long = 100, // سهم بـ 100 ج.م
    val category: String, // حفر آبار وسقيا ماء، عمارة وفرش مساجد، إطعام الأسر
    val organizerPhone: String = "01000000000"
)

@Immutable
data class Reciter(
    val id: String,
    val name: String,
    val title: String,
    val isLocalSheikh: Boolean = true
)
