package com.islamic.noor.ui.azkar

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.data.repository.AzkarRepository
import com.islamic.noor.domain.model.ZekrItem
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.EmeraldLight
import com.islamic.noor.ui.theme.GoldBright
import com.islamic.noor.ui.theme.GoldChampagne
import com.islamic.noor.ui.theme.GoldDark
import com.islamic.noor.ui.theme.titleColor

data class AzkarCategoryItem(
    val title: String,
    val subtitle: String,
    val badge: String,
    val icon: ImageVector
)

@Composable
fun AzkarScreen(
    modifier: Modifier = Modifier,
    repository: AzkarRepository = remember { AzkarRepository() }
) {
    val context = LocalContext.current
    var activeCategory by remember { mutableStateOf<String?>(null) }

    val mainCategories = listOf(
        AzkarCategoryItem("أذكار الصباح", "حصن يومك بذكر الله من بعد صلاة الفجر", "يبدأ بعد الفجر", Icons.Default.WbSunny),
        AzkarCategoryItem("أذكار المساء", "السكينة والحفظ من بعد صلاة العصر حتى الغروب", "يبدأ بعد العصر", Icons.Default.WbTwilight),
        AzkarCategoryItem("أذكار النوم", "آيات الحفظ وأدعية السكينة قبل النوم", "راحة واطمئنان", Icons.Default.Bedtime),
        AzkarCategoryItem("أذكار الاستيقاظ", "الحمد لله الذي أحيانا بعد ما أماتنا", "عند القيام", Icons.Default.WbSunny),
        AzkarCategoryItem("أذكار بعد الصلاة", "الاستغفار والتسبيح والتحميد عقب كل فريضة", "عقب الصلوات", Icons.Default.Mosque),
        AzkarCategoryItem("أذكار المسجد", "دعاء المشي إلى المسجد ودعاء الدخول والخروج", "آداب المسجد", Icons.Default.Mosque)
    )

    if (activeCategory == null) {
        // Category Cards Overview (مستطيلات أنيقة كما طلب المستخدم تماماً)
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            item(key = "header") {
                Text(
                    text = "أقسام الأذكار وحصن المسلم",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = titleColor(),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            items(mainCategories, key = { it.title }) { cat ->
                AzkarCategoryCard(
                    category = cat,
                    onClick = { activeCategory = cat.title }
                )
            }

            // قسم "أنشئ أذكارك" بالأسفل تماماً كما طلب المستخدم
            item(key = "custom_azkar_card") {
                Card(
                    onClick = { activeCategory = "أذكاري المخصصة" },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldChampagne.copy(alpha = 0.8f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GoldChampagne.copy(alpha = 0.2f),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = GoldBright,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "أنشئ أذكارك الخاصة",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = titleColor()
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldLight
                                ) {
                                    Text(
                                        text = "مخصص لك",
                                        color = EmeraldDeep,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "أضف أذكاراً وأدعية بأعداد تكرار تختارها بنفسك",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = GoldChampagne
                        )
                    }
                }
            }
        }
    } else {
        // Detailed Azkar List for Selected Category
        AzkarDetailView(
            categoryName = activeCategory!!,
            onBack = { activeCategory = null },
            repository = repository
        )
    }
}

@Composable
private fun AzkarCategoryCard(
    category: AzkarCategoryItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = GoldChampagne.copy(alpha = 0.15f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        tint = GoldBright,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = titleColor()
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EmeraldLight
                    ) {
                        Text(
                            text = category.badge,
                            color = EmeraldDeep,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = category.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = GoldChampagne
            )
        }
    }
}

@Composable
private fun AzkarDetailView(
    categoryName: String,
    onBack: () -> Unit,
    repository: AzkarRepository
) {
    val context = LocalContext.current
    val azkarList by repository.getAzkarByCategory(categoryName).collectAsState(initial = emptyList())
    val countsMap = remember { mutableStateMapOf<Int, Int>() }
    var showCreateDialog by remember { mutableStateOf(false) }

    fun triggerVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                v?.vibrate(30)
            }
        } catch (_: Exception) {}
    }

    Scaffold(
        floatingActionButton = {
            if (categoryName == "أذكاري المخصصة") {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = EmeraldDeep,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "إضافة ذكر")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Category Top Bar
            item(key = "category_header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع للأقسام",
                                tint = titleColor()
                            )
                        }
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = titleColor()
                        )
                    }

                    // Reset button
                    IconButton(onClick = { countsMap.clear() }) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "إعادة ضبط الأذكار",
                            tint = GoldChampagne
                        )
                    }
                }
            }

            item(key = "info_banner") {
                Text(
                    text = "اضغط على أي مكان بالبطاقة لإنقاص العداد (تختفي البطاقة تلقائياً عند الإتمام):",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Visible Items
            val visibleAzkar = azkarList.filter { zekr ->
                val remaining = countsMap.getOrPut(zekr.id) { zekr.targetCount }
                remaining > 0
            }

            if (visibleAzkar.isEmpty()) {
                item(key = "all_completed") {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldLight,
                                modifier = Modifier.size(60.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = EmeraldDeep,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "تقبل الله طاعتكم",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = titleColor()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "أتممت جميع أذكار هذا القسم بحمد الله.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { countsMap.clear() },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
                            ) {
                                Text("إعادة البدء من جديد", color = Color.White)
                            }
                        }
                    }
                }
            } else {
                items(
                    items = visibleAzkar,
                    key = { it.id }
                ) { zekr ->
                    val remaining = countsMap[zekr.id] ?: zekr.targetCount

                    AnimatedVisibility(
                        visible = remaining > 0,
                        exit = fadeOut(animationSpec = tween(350)) + shrinkVertically(animationSpec = tween(350))
                    ) {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    triggerVibration()
                                    if (remaining > 1) {
                                        countsMap[zekr.id] = remaining - 1
                                    } else {
                                        countsMap[zekr.id] = 0
                                    }
                                }
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = EmeraldLight
                                    ) {
                                        Text(
                                            text = "باقي: $remaining من ${zekr.targetCount}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldDeep,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }

                                    Surface(
                                        shape = CircleShape,
                                        color = GoldChampagne.copy(alpha = 0.2f),
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "$remaining",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = GoldDark
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = zekr.arabicText,
                                    fontSize = 18.sp,
                                    lineHeight = 30.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                if (zekr.fadl.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = zekr.fadl,
                                        fontSize = 11.sp,
                                        color = GoldDark,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Create Custom Dhikr Dialog
    if (showCreateDialog) {
        var text by remember { mutableStateOf("") }
        var count by remember { mutableIntStateOf(33) }
        var fadl by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(text = "إنشاء ذكر خاص بك", fontWeight = FontWeight.Bold, color = titleColor())
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("نص الذكر أو الدعاء") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = count.toString(),
                        onValueChange = { count = it.toIntOrNull() ?: 1 },
                        label = { Text("عدد التكرار") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = fadl,
                        onValueChange = { fadl = it },
                        label = { Text("فضل الذكر (اختياري)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (text.isNotBlank()) {
                            repository.addCustomZekr(text, count, fadl)
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
                ) {
                    Text("حفظ الذكر", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("إلغاء", color = Color.Gray)
                }
            }
        )
    }
}
