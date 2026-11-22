package com.islamic.noor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.islamic.noor.data.repository.UserRepository
import com.islamic.noor.domain.model.AppThemeMode
import com.islamic.noor.ui.azkar.AzkarScreen
import com.islamic.noor.ui.charity.GoodDeedsScreen
import com.islamic.noor.ui.components.AppTopBar
import com.islamic.noor.ui.components.ModernAppBottomNav
import com.islamic.noor.ui.contest.ContestScreen
import com.islamic.noor.ui.duas.DuasScreen
import com.islamic.noor.ui.hadith.HadithScreen
import com.islamic.noor.ui.home.HomeScreen
import com.islamic.noor.ui.khatmah.GroupKhatmahScreen
import com.islamic.noor.ui.navigation.Screen
import com.islamic.noor.ui.night.NightModeScreen
import com.islamic.noor.ui.profile.ProfileScreen
import com.islamic.noor.ui.qibla.QiblaScreen
import com.islamic.noor.ui.quran.QuranScreen
import com.islamic.noor.ui.quran.SurahDetailScreen
import com.islamic.noor.ui.tasbeeh.TasbeehScreen
import com.islamic.noor.ui.theme.NoorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val userProfile by UserRepository.instance.userProfile.collectAsState()
            val isDark = when (userProfile.appTheme) {
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            NoorTheme(darkTheme = isDark) {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val screenTitle = when {
        currentRoute == Screen.Home.route -> "غِراس"
        currentRoute == Screen.Contest.route -> "مسابقة الأسبوع"
        currentRoute == Screen.Profile.route -> "الملف الشخصي والإعدادات"
        currentRoute == Screen.Quran.route -> "المصحف الشريف"
        currentRoute?.startsWith("surah_detail") == true -> "تلاوة القرآن"
        currentRoute == Screen.Azkar.route -> "الأذكار وحصن المسلم"
        currentRoute == Screen.Qibla.route -> "اتجاه القبلة ومواقيت الصلاة"
        currentRoute == Screen.Tasbeeh.route -> "السبحة"
        currentRoute == Screen.Duas.route -> "الأدعية المباركة"
        currentRoute == Screen.Hadith.route -> "الأحاديث النبوية الشريفة"
        currentRoute == Screen.GoodDeeds.route -> "غراس الخير وثوابها"
        currentRoute == Screen.GroupKhatmah.route -> "الختمة الجماعية"
        else -> "غِراس"
    }

    val isRootScreen = currentRoute == Screen.Home.route ||
            currentRoute == Screen.Contest.route ||
            currentRoute == Screen.Profile.route

    Scaffold(
        topBar = {
            if (currentRoute?.startsWith("surah_detail") != true) {
                AppTopBar(
                    title = screenTitle,
                    onBackClick = if (!isRootScreen) {
                        { navController.popBackStack() }
                    } else null
                )
            }
        },
        bottomBar = {
            if (currentRoute?.startsWith("surah_detail") != true) {
                ModernAppBottomNav(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (route == Screen.Home.route) {
                            navController.popBackStack(Screen.Home.route, inclusive = false)
                        } else {
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(Screen.Contest.route) {
                ContestScreen()
            }

            composable(Screen.Profile.route) {
                ProfileScreen()
            }

            composable(Screen.Quran.route) {
                QuranScreen(
                    onSurahClick = { surahId ->
                        navController.navigate(Screen.SurahDetail.createRoute(surahId))
                    }
                )
            }

            composable(
                route = Screen.SurahDetail.route,
                arguments = listOf(navArgument("surahId") { type = NavType.IntType })
            ) { backStackEntry ->
                val surahId = backStackEntry.arguments?.getInt("surahId") ?: 1
                SurahDetailScreen(
                    surahId = surahId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Azkar.route) {
                AzkarScreen()
            }

            composable(Screen.Qibla.route) {
                QiblaScreen()
            }

            composable(Screen.Tasbeeh.route) {
                TasbeehScreen()
            }

            composable(Screen.Duas.route) {
                DuasScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Hadith.route) {
                HadithScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.GoodDeeds.route) {
                GoodDeedsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.GroupKhatmah.route) {
                GroupKhatmahScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
