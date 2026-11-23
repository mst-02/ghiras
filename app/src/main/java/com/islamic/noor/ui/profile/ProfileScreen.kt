package com.islamic.noor.ui.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.data.repository.QiblaAndPrayerRepository
import com.islamic.noor.data.repository.UserRepository
import com.islamic.noor.domain.model.AppThemeMode
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.EmeraldLight
import com.islamic.noor.ui.theme.EmeraldMedium
import com.islamic.noor.ui.theme.GoldBright
import com.islamic.noor.ui.theme.GoldChampagne
import com.islamic.noor.ui.theme.GoldDark
import com.islamic.noor.ui.theme.buttonAdaptiveColor
import com.islamic.noor.ui.theme.iconAdaptiveColor
import com.islamic.noor.ui.theme.isDarkThemeActive
import com.islamic.noor.ui.theme.titleColor

val IslamicAvatarIcons = listOf(
    Icons.AutoMirrored.Filled.MenuBook, // مصحف
    Icons.Default.Mosque,               // مسجد
    Icons.Default.Star,                 // نجمة إسلامية
    Icons.Default.WbSunny,              // إشراقة فجر
    Icons.Default.Person,               // شخص وقور
    Icons.Default.AutoAwesome           // نور وهداية
)

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userRepository: UserRepository = remember { UserRepository.instance },
    prayerRepository: QiblaAndPrayerRepository = remember { QiblaAndPrayerRepository() }
) {
    val context = LocalContext.current
    val userProfile by userRepository.userProfile.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(userProfile.name) }
    var editPhone by remember { mutableStateOf(userProfile.phone) }

    var notificationsEnabled by remember { mutableStateOf(userProfile.notificationsEnabled) }
    var hapticFeedbackEnabled by remember { mutableStateOf(true) }
    var showAvatarDialog by remember { mutableStateOf(false) }
    var showGovernorateMenu by remember { mutableStateOf(false) }
    var showUpdateSuccessDialog by remember { mutableStateOf(false) }

    // Dialog state for info
    var activeDialogTitle by remember { mutableStateOf<String?>(null) }
    var activeDialogContent by remember { mutableStateOf<String?>(null) }

    val currentAvatarIcon = IslamicAvatarIcons.getOrElse(userProfile.avatarIndex) { IslamicAvatarIcons[0] }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Profile Header Card
        item(key = "profile_header") {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Avatar with camera icon
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clickable { showAvatarDialog = true }
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isDarkThemeActive()) EmeraldMedium else EmeraldLight,
                                    border = BorderStroke(2.dp, GoldBright),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = currentAvatarIcon,
                                            contentDescription = "الصورة الشخصية",
                                            tint = if (isDarkThemeActive()) GoldBright else EmeraldDeep,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = GoldChampagne,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Alignment.BottomEnd)
                                        .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.PhotoCamera,
                                            contentDescription = "تغيير الصورة",
                                            tint = EmeraldDeep,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = if (userProfile.name.isNotBlank()) userProfile.name else "زائر كريم",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = titleColor()
                                )
                                Text(
                                    text = if (userProfile.phone.isNotBlank()) userProfile.phone else "لم يتم تسجيل رقم الهاتف",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                editName = userProfile.name
                                editPhone = userProfile.phone
                                isEditing = !isEditing
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "تعديل البيانات",
                                tint = GoldChampagne
                            )
                        }
                    }

                    // Edit Profile Panel
                    AnimatedVisibility(visible = isEditing) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            OutlinedTextField(
                                value = editName,
                                onValueChange = { editName = it },
                                label = { Text("الاسم الكريم") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = editPhone,
                                onValueChange = { editPhone = it },
                                label = { Text("رقم الهاتف") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(
                                onClick = {
                                    userRepository.updateProfile(editName, editPhone)
                                    isEditing = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = buttonAdaptiveColor()),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("حفظ البيانات", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // 2. Stats Row
        item(key = "stats_row") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.FormatListNumbered, contentDescription = null, tint = GoldDark, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${userProfile.totalTasbeeh}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = titleColor()
                        )
                        Text(
                            text = "تسبيحة منجزة",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        )
                    }
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GoldDark, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${userProfile.totalContestsJoined}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = titleColor()
                        )
                        Text(
                            text = "مشاركات في المسابقة",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                        )
                    }
                }
            }
        }

        // 3. App Preferences (المظهر العام: نهار/ليل)
        item(key = "app_preferences") {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "تفضيلات التطبيق",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = titleColor()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // وضع النهار ووضع الليل (Day / Night App-wide Toggle)
                    Text(
                        text = "مظهر التطبيق العام",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // وضع النهار
                        val isLightMode = userProfile.appTheme == AppThemeMode.LIGHT
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isLightMode) GoldChampagne.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = if (isLightMode) BorderStroke(1.5.dp, GoldBright) else BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { userRepository.setAppTheme(AppThemeMode.LIGHT) }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.WbSunny, contentDescription = null, tint = if (isLightMode) GoldDark else Color.Gray, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "وضع النهار",
                                    fontWeight = if (isLightMode) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isLightMode) titleColor() else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // وضع الليل
                        val isDarkMode = userProfile.appTheme == AppThemeMode.DARK
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDarkMode) EmeraldMedium.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = if (isDarkMode) BorderStroke(1.5.dp, GoldBright) else BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { userRepository.setAppTheme(AppThemeMode.DARK) }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.DarkMode, contentDescription = null, tint = if (isDarkMode) GoldBright else Color.Gray, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "وضع الليل",
                                    fontWeight = if (isDarkMode) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isDarkMode) GoldBright else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = iconAdaptiveColor())
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("تنبيهات مواقيت الصلاة", fontWeight = FontWeight.Medium)
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GoldChampagne,
                                checkedTrackColor = EmeraldMedium
                            )
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Vibration, contentDescription = null, tint = iconAdaptiveColor())
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("الاهتزاز اللمسي للسبحة", fontWeight = FontWeight.Medium)
                        }
                        Switch(
                            checked = hapticFeedbackEnabled,
                            onCheckedChange = { hapticFeedbackEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GoldChampagne,
                                checkedTrackColor = EmeraldMedium
                            )
                        )
                    }
                }
            }
        }

        // 4. إعدادات الأذان والإقامة ومحافظة الحساب (حسب طلب العميل تماماً)
        item(key = "athan_and_iqama") {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "إعدادات الأذان والإقامة والموقع",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = titleColor()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // صوت الأذان كاملاً
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = GoldChampagne)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("صوت الأذان كاملاً", fontWeight = FontWeight.Medium)
                                Text(
                                    "تشغيل الأذان بصوت ندي عند دخول الوقت",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                        Switch(
                            checked = userProfile.athanSoundEnabled,
                            onCheckedChange = { userRepository.setAthanSoundEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GoldChampagne,
                                checkedTrackColor = EmeraldMedium
                            )
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    // وقت الإقامة بعد الأذان
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = GoldChampagne)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("تنبيه وقت الإقامة بعد الأذان:", fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(10, 15, 20).forEach { mins ->
                                val isSelected = userProfile.iqamaDelayMinutes == mins
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) (if (isDarkThemeActive()) GoldDark else EmeraldDeep) else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { userRepository.setIqamaDelay(mins) }
                                ) {
                                    Text(
                                        text = "$mins دقائق",
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    // المحافظة لمواقيت الصلاة
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showGovernorateMenu = true }
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoldChampagne)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("المحافظة لمواقيت الصلاة", fontWeight = FontWeight.Medium)
                                    Text(
                                        "المحددة حالياً: ${userProfile.selectedGovernorate}",
                                        fontSize = 11.sp,
                                        color = GoldDark
                                    )
                                }
                            }
                            Text("تغيير ←", color = GoldChampagne, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        DropdownMenu(
                            expanded = showGovernorateMenu,
                            onDismissRequest = { showGovernorateMenu = false }
                        ) {
                            prayerRepository.EGYPT_GOVERNORATES.forEach { gov ->
                                DropdownMenuItem(
                                    text = { Text(gov.name) },
                                    onClick = {
                                        userRepository.setGovernorate(gov.name)
                                        showGovernorateMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Legal, FAQ, and About Us (من نحن في الآخر كما طلب)
        item(key = "legal_and_about") {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "المعلومات والسياسات",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = titleColor()
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // 1. شروط الاستخدام
                    SettingsMenuRow(
                        icon = Icons.Default.Description,
                        title = "شروط الاستخدام",
                        onClick = {
                            activeDialogTitle = "شروط الاستخدام"
                            activeDialogContent = "• استخدام تطبيق غِراس متاح مجاناً لجميع المسلمين.\n• لا يجوز استخدام التطبيق في أي أغراض تخالف تعاليم الشريعة الإسلامية.\n• المسابقات والجوائز تخضع للقرعة العشوائية العادلة وبدون أي رسوم دفع على المستخدمين."
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // 2. سياسة الخصوصية
                    SettingsMenuRow(
                        icon = Icons.Default.Security,
                        title = "سياسة الخصوصية",
                        onClick = {
                            activeDialogTitle = "سياسة الخصوصية"
                            activeDialogContent = "• نحترم خصوصيتك احتراماً تاماً؛ بياناتك محفوظة على جهازك محلياً.\n• لا نقوم بمشاركة أي أرقام هواتف أو بيانات مع أي جهة خارجية أو إعلانية مطلقاً.\n• التطبيق يعمل بصورة كاملة أوفلاين لحفظ خصوصية قراءاتك وعباداتك."
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // 3. الأسئلة الشائعة
                    SettingsMenuRow(
                        icon = Icons.Default.HelpOutline,
                        title = "الأسئلة الشائعة",
                        onClick = {
                            activeDialogTitle = "الأسئلة الشائعة"
                            activeDialogContent = "س: هل التطبيق مجاني بالكامل؟\nج: نعم، وقف خيري لوجه الله تعالى دون أي إعلانات مزعجة.\n\nس: كيف يتم السحب على الجوائز؟\nج: يتم سحب أسبوعي إلكتروني عشوائي لمن يجيب على سؤال المسابقة بشكل صحيح.\n\nس: هل يعمل التطبيق بدون إنترنت؟\nج: نعم، المصحف والأذكار والأدعية والقبلة والسبحة تعمل بدون إنترنت بالكامل."
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // 4. من نحن (في آخر القائمة تماماً كما طُلب)
                    SettingsMenuRow(
                        icon = Icons.Default.Info,
                        title = "من نحن",
                        onClick = {
                            activeDialogTitle = "من نحن"
                            activeDialogContent = "«غِراس» هو تطبيق إسلامي مبارك، شعاره «غرسٌ طيب في الدنيا وأجرٌ دائم في الآخرة».\n\nصُمم خصيصاً ليكون سهلاً، مريحاً للعين، وبخطوط واضحة تناسب الجميع وكبار السن، ليكون رفيقك في تلاوة القرآن الكريم، الختمة الجماعية، الأذكار، السبحة، اتجاه القبلة، رسائل الطمأنينة اليومية، أدعية المتوفين، وأبواب غراس الخير، مع مسابقات أسبوعية مجانية بالكامل.\n\nنسأل الله تعالى أن يتقبله خالصاً لوجهه الكريم صدقة جارية ونفعاً للأمة."
                        }
                    )
                }
            }
        }
    }

    // Avatar Selection Dialog
    if (showAvatarDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarDialog = false },
            title = {
                Text("اختر الصورة الشخصية", fontWeight = FontWeight.Bold, color = titleColor())
            },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(IslamicAvatarIcons) { index, icon ->
                        val isSelected = userProfile.avatarIndex == index
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) GoldChampagne else (if (isDarkThemeActive()) EmeraldMedium else EmeraldLight),
                            border = if (isSelected) BorderStroke(2.5.dp, GoldBright) else null,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .clickable {
                                    userRepository.setAvatar(index)
                                    showAvatarDialog = false
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) EmeraldDeep else (if (isDarkThemeActive()) GoldBright else EmeraldDeep),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarDialog = false }) {
                    Text("إغلاق", color = titleColor(), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Update check dialog
    if (showUpdateSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showUpdateSuccessDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("حالة التحديث", fontWeight = FontWeight.Bold, color = titleColor())
                }
            },
            text = {
                Text(
                    "تطبيق غِراس محدث إلى أحدث نسخة مطروحة (v1.2.0).\nكافة الإعدادات والميزات الجديدة متزامنة وجاهزة للاستخدام.",
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showUpdateSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
                ) {
                    Text("ممتاز", color = Color.White)
                }
            }
        )
    }

    // Modal Dialog for Legal & About
    if (activeDialogTitle != null) {
        AlertDialog(
            onDismissRequest = {
                activeDialogTitle = null
                activeDialogContent = null
            },
            title = {
                Text(
                    text = activeDialogTitle ?: "",
                    fontWeight = FontWeight.Bold,
                    color = titleColor()
                )
            },
            text = {
                Text(
                    text = activeDialogContent ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (activeDialogTitle == "من نحن") {
                        // زر التحديث التلقائي المطلوب داخل "من نحن"
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldDeep,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    val browserIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://github.com/mst-02/ghiras/releases/latest")
                                    )
                                    context.startActivity(browserIntent)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = GoldBright,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "تحديث التطبيق",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Button(
                        onClick = {
                            activeDialogTitle = null
                            activeDialogContent = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = buttonAdaptiveColor())
                    ) {
                        Text("إغلاق", color = Color.White)
                    }
                }
            }
        )
    }
}

@Composable
private fun SettingsMenuRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconAdaptiveColor(),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = GoldChampagne.copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
        )
    }
}
