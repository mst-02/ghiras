package com.islamic.noor.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class ContestQuestion(
    val id: String,
    val title: String,
    val questionText: String,
    val options: List<String>,
    val deadlineDate: String,
    val prizeAmount: String,
    val isWeekly: Boolean = true,
    val termsUrl: String = ""
)

@Immutable
data class ContestSubmission(
    val questionId: String,
    val userName: String,
    val userPhone: String,
    val selectedOptionIndex: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Immutable
data class ContestWinner(
    val weekNumber: String,
    val winnerName: String,
    val prizeDescription: String,
    val date: String
)
