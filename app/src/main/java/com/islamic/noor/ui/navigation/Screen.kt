package com.islamic.noor.ui.navigation

sealed class Screen(val route: String, val title: String) {
    // 3 Primary Bottom Nav Destinations
    object Home : Screen("home", "غِراس")
    object Contest : Screen("contest", "مسابقة الأسبوع")
    object Profile : Screen("profile", "الإعدادات")

    // Core Services
    object Quran : Screen("quran", "المصحف الشريف")
    object SurahDetail : Screen("surah_detail/{surahId}", "تلاوة القرآن") {
        fun createRoute(surahId: Int) = "surah_detail/$surahId"
    }
    object Azkar : Screen("azkar", "الأذكار")
    object Tasbeeh : Screen("tasbeeh", "السبحة")
    object Qibla : Screen("qibla", "اتجاه القبلة")

    // Client Features
    object Duas : Screen("duas", "الأدعية المباركة")
    object GoodDeeds : Screen("good_deeds", "غراس الخير")
    object GroupKhatmah : Screen("group_khatmah", "الختمة الجماعية")
    object Hadith : Screen("hadith", "الأحاديث النبوية")
}
