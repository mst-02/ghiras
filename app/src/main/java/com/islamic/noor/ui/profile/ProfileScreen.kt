package com.islamic.noor.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.WbSunny
import com.islamic.noor.domain.model.AppThemeMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.data.repository.UserRepository
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

val IslamicAvatars = listOf(
    Pair("رمز شخصي", Icons.Default.Person),
    Pair("مسجد مبارك", Icons.Default.Mosque),
    Pair("كتاب الله", Icons.Default.AutoStories),
    Pair("غراس الخير", Icons.Default.Eco),
    Pair("نور وضياء", Icons.Default.Star),
    Pair("هلال مبارك", Icons.Default.NightlightRound)
)

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userRepository: UserRepository = remember { UserRepository.instance }
) {
    val userProfile by userRepository.userProfile.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    var editName by remember(userProfile.name) { mutableStateOf(userProfile.name) }
    var editPhone by remember(userProfile.phone) { mutableStateOf(userProfile.phone) }

    var notificationsEnabled by remember { mutableStateOf(true) }
    var hapticFeedbackEnabled by remember { mutableStateOf(true) }
    var showAvatarDialog by remember { mutableStateOf(false) }

    val currentAvatarIcon = IslamicAvatars.getOrNull(userProfile.avatarIndex)?.second ?: Icons.Default.Person

    // Dialog state for legal / info popups
    var activeDialogTitle by remember { mutableStateOf<String?>(null) }
    var activeDialogContent by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Profile Card
        item(key = "profile_card") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Clickable Avatar with Camera Badge
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
                                Text(
                                    text = "اضغط على الصورة لتغييرها",
                                    fontSize = 10.sp,
                                    color = GoldDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                if (isEditing) {
                                    userRepository.updateProfile(editName, editPhone)
                                }
                                isEditing = !isEditing
                            }
                        ) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = if (isEditing) "حفظ" else "تعديل",
                                tint = iconAdaptiveColor()
                            )
                        }
                    }

                    // Edit Fields Expandable
                    AnimatedVisibility(visible = isEditing) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HorizontalDivider()
                            OutlinedTextField(
                                value = editName,
                                onValueChange = { editName = it },
                                label = { Text("الاسم") },
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

        // 3. Settings & Preferences
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

        // 4. Legal, FAQ, and About Us (من نحن في الآخر كما طلب)
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
                            activeDialogContent = "• نحترم خصوصية مستخدمينا؛ بياناتك الشخصية (الاسم ورقم الهاتف) تُستخدم فقط لغرض التواصل عند الفوز في المسابقة.\n• جميع بيانات الأذكار والمصحف والتسبيح تُحفظ محلياً على هاتفك دون مشاركتها مع أي طرف ثالث.\n• التطبيق لا يقوم بجمع أي بيانات حساسة."
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // 3. الأسئلة الشائعة
                    SettingsMenuRow(
                        icon = Icons.Default.QuestionAnswer,
                        title = "الأسئلة الشائعة",
                        onClick = {
                            activeDialogTitle = "الأسئلة الشائعة"
                            activeDialogContent = "س: هل المسابقة تتطلب أي دفع مالي؟\nج: لا، المشاركة مجانية بالكامل بنسبة 100%.\n\nس: كيف يتم اختيار الفائز؟\nج: يتم إجراء سحب إلكتروني عشوائي أسبوعياً بين أصحاب الإجابات الصحيحة.\n\nس: هل يعمل التطبيق بدون إنترنت؟\nج: نعم، القرآن الكريم، الأذكار، القبلة، والسبحة تعمل بالكامل بدون إنترنت (أوفلاين)."
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // 4. من نحن (تكون في آخر القائمة كما طلب)
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

    // Popup Info Dialog
    if (activeDialogTitle != null && activeDialogContent != null) {
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
        )
    }

    // Avatar Selection Dialog
    if (showAvatarDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarDialog = false },
            title = {
                Text("اختر الصورة الشخصية", fontWeight = FontWeight.Bold, color = titleColor())
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "اختر رمزاً إسلامياً مباركاً يعبر عنك في رحاب غِراس:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        itemsIndexed(IslamicAvatars) { index, pair ->
                            val isSelected = userProfile.avatarIndex == index
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        userRepository.setAvatar(index)
                                        showAvatarDialog = false
                                    }
                                    .padding(4.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) GoldChampagne else (if (isDarkThemeActive()) EmeraldMedium else EmeraldLight),
                                    border = if (isSelected) BorderStroke(2.dp, GoldBright) else null,
                                    modifier = Modifier.size(52.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = pair.second,
                                            contentDescription = pair.first,
                                            tint = if (isSelected) EmeraldDeep else (if (isDarkThemeActive()) GoldBright else EmeraldDeep),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = pair.first,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) GoldBright else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarDialog = false }) {
                    Text("إلغاء", color = titleColor())
                }
            }
        )
    }
}

@Composable
private fun SettingsMenuRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = iconAdaptiveColor(), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = Color.Gray.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    com.islamic.noor.ui.theme.NoorTheme {
        ProfileScreen()
    }
}
