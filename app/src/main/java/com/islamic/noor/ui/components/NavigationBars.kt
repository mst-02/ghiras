package com.islamic.noor.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.ui.navigation.Screen
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.EmeraldMedium
import com.islamic.noor.ui.theme.GoldBright
import com.islamic.noor.ui.theme.GoldChampagne

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                // Luxury Geometric Ghiras Emblem (Sprout in 8-Point Star)
                GhirasEmblemIcon(modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "غرسٌ طيب وأجرٌ دائم",
                        fontSize = 10.sp,
                        color = GoldChampagne.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .size(40.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = GoldChampagne,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        actions = {
            // Elegant Framed Hijri Date
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0D1210))
                    .border(1.dp, GoldChampagne.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 9.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "١٥ جمادى الأولى ١٤٤٨",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldBright
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldDeep),
        modifier = modifier
    )
}

/**
 * Luxury 8-pointed star Ghiras logo icon with golden accents
 */
@Composable
fun GhirasEmblemIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val sizePx = size.minDimension
        val center = Offset(size.width / 2f, size.height / 2f)
        val outerRadius = sizePx * 0.48f
        val innerRadius = sizePx * 0.32f

        // Draw 8-point geometric star (Rub el Hizb)
        val starPath = Path()
        val numPoints = 16
        for (i in 0 until numPoints) {
            val radius = if (i % 2 == 0) outerRadius else innerRadius
            val angle = (i * Math.PI / 8.0) - (Math.PI / 2.0)
            val x = center.x + (radius * Math.cos(angle)).toFloat()
            val y = center.y + (radius * Math.sin(angle)).toFloat()
            if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
        }
        starPath.close()

        drawPath(
            path = starPath,
            brush = Brush.radialGradient(
                colors = listOf(GoldChampagne, Color(0xFF9E7C3A)),
                center = center,
                radius = outerRadius
            )
        )

        // Center emerald seed representing Ghiras
        drawCircle(
            color = EmeraldDeep,
            radius = innerRadius * 0.65f,
            center = center
        )

        // Inner sprout point
        drawCircle(
            color = GoldBright,
            radius = innerRadius * 0.28f,
            center = center
        )
    }
}

@Composable
fun ModernAppBottomNav(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHomeSelected = currentRoute == Screen.Home.route ||
            currentRoute == Screen.Quran.route ||
            currentRoute?.startsWith("surah_detail") == true ||
            currentRoute == Screen.Azkar.route ||
            currentRoute == Screen.Tasbeeh.route ||
            currentRoute == Screen.Qibla.route ||
            currentRoute == Screen.Duas.route ||
            currentRoute == Screen.GoodDeeds.route ||
            currentRoute == Screen.GroupKhatmah.route ||
            currentRoute == Screen.Hadith.route

    val isContestSelected = currentRoute == Screen.Contest.route
    val isProfileSelected = currentRoute == Screen.Profile.route

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 18.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. المسابقات
            NavTabItem(
                icon = Icons.Default.EmojiEvents,
                label = "المسابقات",
                isSelected = isContestSelected,
                onClick = { onNavigate(Screen.Contest.route) }
            )

            // 2. الزر الرئيسي في المنتصف بتصميم بارز وفخم
            CenterFloatingHomeButton(
                isSelected = isHomeSelected,
                onClick = { onNavigate(Screen.Home.route) }
            )

            // 3. الإعدادات
            NavTabItem(
                icon = Icons.Default.Settings,
                label = "الإعدادات",
                isSelected = isProfileSelected,
                onClick = { onNavigate(Screen.Profile.route) }
            )
        }
    }
}

@Composable
private fun NavTabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) GoldChampagne else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) GoldChampagne else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun CenterFloatingHomeButton(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .offset(y = (-14).dp)
            .size(62.dp)
            .shadow(12.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = if (isSelected) listOf(GoldChampagne, EmeraldDeep) else listOf(EmeraldMedium, EmeraldDeep)
                )
            )
            .border(2.dp, GoldChampagne.copy(alpha = 0.8f), CircleShape)
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mosque,
                contentDescription = "الرئيسية",
                tint = if (isSelected) EmeraldDeep else GoldChampagne,
                modifier = Modifier.size(26.dp)
            )
            Text(
                text = "الرئيسية",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) EmeraldDeep else Color.White
            )
        }
    }
}
