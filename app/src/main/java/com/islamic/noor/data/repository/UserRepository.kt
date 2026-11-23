package com.islamic.noor.data.repository

import com.islamic.noor.domain.model.AppThemeMode
import com.islamic.noor.domain.model.ReadingTheme
import com.islamic.noor.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository {

    private val _userProfile = MutableStateFlow(
        UserProfile(
            isLoggedIn = false,
            isAdmin = false,
            name = "",
            phone = "",
            totalTasbeeh = 340,
            totalContestsJoined = 0
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    fun loginUser(name: String, phone: String) {
        _userProfile.value = _userProfile.value.copy(
            isLoggedIn = true,
            isAdmin = false,
            name = name.trim(),
            phone = phone.trim()
        )
    }

    fun loginAdmin(username: String, pass: String): Boolean {
        return if (username.trim().lowercase() == "admin" && pass.trim() == "1234") {
            _userProfile.value = _userProfile.value.copy(
                isLoggedIn = true,
                isAdmin = true,
                name = "مدير النظام (Admin)",
                phone = "01000000000"
            )
            true
        } else {
            false
        }
    }

    fun logout() {
        _userProfile.value = UserProfile()
    }

    fun updateProfile(name: String, phone: String) {
        _userProfile.value = _userProfile.value.copy(
            name = name,
            phone = phone
        )
    }

    fun incrementTasbeeh() {
        _userProfile.value = _userProfile.value.copy(
            totalTasbeeh = _userProfile.value.totalTasbeeh + 1
        )
    }

    fun resetTasbeeh() {
        _userProfile.value = _userProfile.value.copy(
            totalTasbeeh = 0
        )
    }

    fun incrementTasbeehCount() = incrementTasbeeh()

    fun incrementContestsJoined() {
        _userProfile.value = _userProfile.value.copy(
            totalContestsJoined = _userProfile.value.totalContestsJoined + 1
        )
    }

    fun updateReadingPreferences(fontSize: Float, lineHeight: Float, theme: ReadingTheme) {
        _userProfile.value = _userProfile.value.copy(
            quranFontSize = fontSize,
            quranLineHeight = lineHeight,
            readingTheme = theme
        )
    }

    fun setAppTheme(themeMode: AppThemeMode) {
        _userProfile.value = _userProfile.value.copy(appTheme = themeMode)
    }

    fun setAvatar(index: Int) {
        _userProfile.value = _userProfile.value.copy(avatarIndex = index)
    }

    fun saveBookmark(surahId: Int, surahName: String, verseNumber: Int) {
        _userProfile.value = _userProfile.value.copy(
            lastReadSurahId = surahId,
            lastReadSurahName = surahName,
            lastReadVerseNumber = verseNumber
        )
    }

    fun setAthanSoundEnabled(enabled: Boolean) {
        _userProfile.value = _userProfile.value.copy(athanSoundEnabled = enabled)
    }

    fun setIqamaDelay(minutes: Int) {
        _userProfile.value = _userProfile.value.copy(iqamaDelayMinutes = minutes)
    }

    fun setGovernorate(gov: String) {
        _userProfile.value = _userProfile.value.copy(selectedGovernorate = gov)
    }

    companion object {
        val instance = UserRepository()
    }
}
