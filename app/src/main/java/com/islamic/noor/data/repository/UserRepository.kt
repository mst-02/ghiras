package com.islamic.noor.data.repository

import com.islamic.noor.data.local.GhirasPreferences
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
            totalTasbeeh = 0,
            totalContestsJoined = 0
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    fun initFromPreferences() {
        try {
            val loaded = GhirasPreferences.loadProfile()
            _userProfile.value = loaded
        } catch (_: Exception) {}
    }

    private fun persist(profile: UserProfile) {
        _userProfile.value = profile
        try {
            GhirasPreferences.saveProfile(profile)
        } catch (_: Exception) {}
    }

    fun loginUser(name: String, phone: String) {
        val updated = _userProfile.value.copy(
            isLoggedIn = true,
            isAdmin = false,
            name = name.trim(),
            phone = phone.trim()
        )
        persist(updated)
    }

    fun loginAdmin(username: String, pass: String): Boolean {
        return if (username.trim().lowercase() == "admin" && pass.trim() == "1234") {
            val updated = _userProfile.value.copy(
                isLoggedIn = true,
                isAdmin = true,
                name = "مدير النظام (Admin)",
                phone = "01000000000"
            )
            persist(updated)
            true
        } else {
            false
        }
    }

    fun logout() {
        persist(UserProfile())
    }

    fun updateProfile(name: String, phone: String) {
        val updated = _userProfile.value.copy(
            name = name,
            phone = phone
        )
        persist(updated)
    }

    fun incrementTasbeeh() {
        val newTotal = _userProfile.value.totalTasbeeh + 1
        val updated = _userProfile.value.copy(totalTasbeeh = newTotal)
        _userProfile.value = updated
        try {
            GhirasPreferences.saveTotalTasbeeh(newTotal)
        } catch (_: Exception) {}
    }

    fun resetTasbeeh() {
        val updated = _userProfile.value.copy(totalTasbeeh = 0)
        _userProfile.value = updated
        try {
            GhirasPreferences.saveTotalTasbeeh(0)
        } catch (_: Exception) {}
    }

    fun incrementTasbeehCount() = incrementTasbeeh()

    fun incrementContestsJoined() {
        val updated = _userProfile.value.copy(
            totalContestsJoined = _userProfile.value.totalContestsJoined + 1
        )
        persist(updated)
    }

    fun updateReadingPreferences(fontSize: Float, lineHeight: Float, theme: ReadingTheme) {
        val updated = _userProfile.value.copy(
            quranFontSize = fontSize,
            quranLineHeight = lineHeight,
            readingTheme = theme
        )
        persist(updated)
    }

    fun setAppTheme(themeMode: AppThemeMode) {
        val updated = _userProfile.value.copy(appTheme = themeMode)
        persist(updated)
    }

    fun setAvatar(index: Int) {
        val updated = _userProfile.value.copy(avatarIndex = index)
        persist(updated)
    }

    fun saveBookmark(surahId: Int, surahName: String, verseNumber: Int) {
        val updated = _userProfile.value.copy(
            lastReadSurahId = surahId,
            lastReadSurahName = surahName,
            lastReadVerseNumber = verseNumber
        )
        _userProfile.value = updated
        try {
            GhirasPreferences.saveBookmark(surahId, surahName, verseNumber)
        } catch (_: Exception) {}
    }

    fun setAthanSoundEnabled(enabled: Boolean) {
        val updated = _userProfile.value.copy(athanSoundEnabled = enabled)
        persist(updated)
    }

    fun setIqamaDelay(minutes: Int) {
        val updated = _userProfile.value.copy(iqamaDelayMinutes = minutes)
        persist(updated)
    }

    fun setGovernorate(gov: String, isAuto: Boolean = false, lat: Double? = null, lng: Double? = null) {
        val updated = _userProfile.value.copy(selectedGovernorate = gov)
        _userProfile.value = updated
        try {
            GhirasPreferences.saveGovernorate(gov, isAuto, lat, lng)
        } catch (_: Exception) {}
    }

    companion object {
        val instance = UserRepository()
    }
}
