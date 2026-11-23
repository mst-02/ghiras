package com.islamic.noor.data.local

import android.content.Context
import android.content.SharedPreferences
import com.islamic.noor.domain.model.AppThemeMode
import com.islamic.noor.domain.model.ReadingTheme
import com.islamic.noor.domain.model.UserProfile

/**
 * مدير التخزين الدائم لتطبيق غِراس (Ghiras Preferences)
 * يضمن حفظ كافة اختيارات المستخدم، المحافظة، إحداثيات الـ GPS،
 * موضع الحفظ بالمصحف، وإعدادات المظهر حتى بعد إغلاق التطبيق أو إعادة تشغيل الهاتف.
 */
object GhirasPreferences {
    private const val PREF_NAME = "ghiras_app_preferences"
    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    private fun get(): SharedPreferences {
        return prefs ?: throw IllegalStateException("GhirasPreferences must be initialized first with context")
    }

    // Keys
    private const val KEY_SELECTED_GOV = "selected_governorate"
    private const val KEY_IS_AUTO_GPS = "is_auto_gps_location"
    private const val KEY_SAVED_LAT = "saved_latitude"
    private const val KEY_SAVED_LNG = "saved_longitude"

    private const val KEY_LAST_READ_SURAH_ID = "last_read_surah_id"
    private const val KEY_LAST_READ_SURAH_NAME = "last_read_surah_name"
    private const val KEY_LAST_READ_VERSE = "last_read_verse_number"

    private const val KEY_TOTAL_TASBEEH = "total_tasbeeh"
    private const val KEY_ATHAN_SOUND = "athan_sound_enabled"
    private const val KEY_IQAMA_DELAY = "iqama_delay_minutes"
    private const val KEY_NOTIFICATIONS = "notifications_enabled"

    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_PHONE = "user_phone"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_IS_ADMIN = "is_admin"
    private const val KEY_AVATAR_INDEX = "avatar_index"

    private const val KEY_APP_THEME = "app_theme_mode"
    private const val KEY_READING_THEME = "reading_theme_mode"
    private const val KEY_FONT_SIZE = "quran_font_size"
    private const val KEY_LINE_HEIGHT = "quran_line_height"

    // Governorate & GPS
    fun saveGovernorate(gov: String, isAuto: Boolean = false, lat: Double? = null, lng: Double? = null) {
        get().edit().apply {
            putString(KEY_SELECTED_GOV, gov)
            putBoolean(KEY_IS_AUTO_GPS, isAuto)
            if (lat != null && lng != null) {
                putString(KEY_SAVED_LAT, lat.toString())
                putString(KEY_SAVED_LNG, lng.toString())
            }
            apply()
        }
    }

    fun getSelectedGovernorate(): String {
        return get().getString(KEY_SELECTED_GOV, "القاهرة") ?: "القاهرة"
    }

    fun isAutoGPS(): Boolean {
        return get().getBoolean(KEY_IS_AUTO_GPS, false)
    }

    fun getSavedCoordinates(): Pair<Double, Double>? {
        val latStr = get().getString(KEY_SAVED_LAT, null)
        val lngStr = get().getString(KEY_SAVED_LNG, null)
        return if (latStr != null && lngStr != null) {
            val lat = latStr.toDoubleOrNull()
            val lng = lngStr.toDoubleOrNull()
            if (lat != null && lng != null) Pair(lat, lng) else null
        } else null
    }

    // Quran Bookmark
    fun saveBookmark(surahId: Int, surahName: String, verseNumber: Int) {
        get().edit().apply {
            putInt(KEY_LAST_READ_SURAH_ID, surahId)
            putString(KEY_LAST_READ_SURAH_NAME, surahName)
            putInt(KEY_LAST_READ_VERSE, verseNumber)
            apply()
        }
    }

    // Tasbeeh
    fun saveTotalTasbeeh(total: Int) {
        get().edit().putInt(KEY_TOTAL_TASBEEH, total).apply()
    }

    // User Profile load/save
    fun saveProfile(profile: UserProfile) {
        get().edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, profile.isLoggedIn)
            putBoolean(KEY_IS_ADMIN, profile.isAdmin)
            putString(KEY_USER_NAME, profile.name)
            putString(KEY_USER_PHONE, profile.phone)
            putInt(KEY_TOTAL_TASBEEH, profile.totalTasbeeh)
            putBoolean(KEY_NOTIFICATIONS, profile.notificationsEnabled)
            putBoolean(KEY_ATHAN_SOUND, profile.athanSoundEnabled)
            putInt(KEY_IQAMA_DELAY, profile.iqamaDelayMinutes)
            putString(KEY_SELECTED_GOV, profile.selectedGovernorate)
            putFloat(KEY_FONT_SIZE, profile.quranFontSize)
            putFloat(KEY_LINE_HEIGHT, profile.quranLineHeight)
            putString(KEY_READING_THEME, profile.readingTheme.name)
            putString(KEY_APP_THEME, profile.appTheme.name)
            putInt(KEY_AVATAR_INDEX, profile.avatarIndex)
            putInt(KEY_LAST_READ_SURAH_ID, profile.lastReadSurahId)
            putString(KEY_LAST_READ_SURAH_NAME, profile.lastReadSurahName)
            putInt(KEY_LAST_READ_VERSE, profile.lastReadVerseNumber)
            apply()
        }
    }

    fun loadProfile(): UserProfile {
        val p = get()
        val readingThemeName = p.getString(KEY_READING_THEME, ReadingTheme.SEPIA.name)
        val appThemeName = p.getString(KEY_APP_THEME, AppThemeMode.DARK.name)

        val readingTheme = try {
            ReadingTheme.valueOf(readingThemeName ?: ReadingTheme.SEPIA.name)
        } catch (_: Exception) {
            ReadingTheme.SEPIA
        }

        val appTheme = try {
            AppThemeMode.valueOf(appThemeName ?: AppThemeMode.DARK.name)
        } catch (_: Exception) {
            AppThemeMode.DARK
        }

        return UserProfile(
            isLoggedIn = p.getBoolean(KEY_IS_LOGGED_IN, false),
            isAdmin = p.getBoolean(KEY_IS_ADMIN, false),
            name = p.getString(KEY_USER_NAME, "") ?: "",
            phone = p.getString(KEY_USER_PHONE, "") ?: "",
            totalTasbeeh = p.getInt(KEY_TOTAL_TASBEEH, 0),
            totalContestsJoined = 0,
            notificationsEnabled = p.getBoolean(KEY_NOTIFICATIONS, true),
            athanSoundEnabled = p.getBoolean(KEY_ATHAN_SOUND, true),
            iqamaDelayMinutes = p.getInt(KEY_IQAMA_DELAY, 15),
            selectedGovernorate = p.getString(KEY_SELECTED_GOV, "القاهرة") ?: "القاهرة",
            quranFontSize = p.getFloat(KEY_FONT_SIZE, 24f),
            quranLineHeight = p.getFloat(KEY_LINE_HEIGHT, 1.8f),
            readingTheme = readingTheme,
            appTheme = appTheme,
            avatarIndex = p.getInt(KEY_AVATAR_INDEX, 0),
            lastReadSurahId = p.getInt(KEY_LAST_READ_SURAH_ID, 1),
            lastReadSurahName = p.getString(KEY_LAST_READ_SURAH_NAME, "الفاتحة") ?: "الفاتحة",
            lastReadVerseNumber = p.getInt(KEY_LAST_READ_VERSE, 1)
        )
    }
}
