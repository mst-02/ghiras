package com.islamic.noor.ui.azkar

import android.content.Context
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Star
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

data class StandardCategory(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

@Composable
fun AzkarScreen(
    modifier: Modifier = Modifier,
    onSubCategoryChange: (String?, (() -> Unit)?) -> Unit = { _, _ -> },
    repository: AzkarRepository = remember { AzkarRepository.instance }
) {
    val context = LocalContext.current
    var activeCategory by remember { mutableStateOf<String?>(null) }
    val customCategories by repository.customCategories.collectAsState()
    val favoriteIds by repository.favoriteIds.collectAsState()

    // Notify top bar and handle sub-navigation
    LaunchedEffect(activeCategory) {
        if (activeCategory != null) {
            onSubCategoryChange(activeCategory) { activeCategory = null }
        } else {
            onSubCategoryChange(null, null)
        }
    }

    // Intercept back button when inside an Azkar category to return to the categories list first
    BackHandler(enabled = activeCategory != null) {
        activeCategory = null
    }

    var showCreateCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    val standardCategories = listOf(
        StandardCategory("أذكار الصباح", "حصن يومك بذكر الله من بعد صلاة الفجر", Icons.Default.WbSunny),
        StandardCategory("أذكار المساء", "السكينة والحفظ من بعد صلاة العصر حتى الغروب", Icons.Default.WbTwilight),
        StandardCategory("أذكار النوم", "آيات الحفظ وأدعية السكينة قبل النوم", Icons.Default.Bedtime),
        StandardCategory("أذكار الاستيقاظ", "الحمد لله الذي أحيانا بعد ما أماتنا", Icons.Default.WbSunny),
        StandardCategory("أذكار بعد الصلاة", "الاستغفار والتسبيح والتحميد عقب كل فريضة", Icons.Default.Mosque),
        StandardCategory("أذكار المسجد", "دعاء المشي إلى المسجد ودعاء الدخول والخروج", Icons.Default.Mosque)
    )

    if (activeCategory == null) {
        // Categories Overview
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            item(key = "header") {
                Text(
                    text = "أقسام الأذكار وحصن المسلم",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = titleColor(),
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            // المفضلة Card (لو في عناصر مفضلة)
            if (favoriteIds.isNotEmpty()) {
                item(key = "favorites_category") {
                    Card(
                        onClick = { activeCategory = "الأذكار المفضلة" },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFE53935).copy(alpha = 0.5f)),
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
                                color = Color(0xFFE53935).copy(alpha = 0.15f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = Color(0xFFE53935),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "الأذكار المفضلة",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = titleColor()
                                )
                                Text(
                                    text = "${favoriteIds.size} أذكار تم تمييزها بالمفضلة",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                                )
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = GoldChampagne)
                        }
                    }
                }
            }

            // Standard Categories (Badges removed for clean aesthetic!)
            items(standardCategories, key = { it.title }) { cat ->
                Card(
                    onClick = { activeCategory = cat.title },
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
                                    imageVector = cat.icon,
                                    contentDescription = null,
                                    tint = GoldBright,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cat.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = titleColor()
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = cat.subtitle,
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

            // Header for Custom User Categories (Below أذكار المسجد as requested!)
            item(key = "custom_categories_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الأذكار والأقسام المخصصة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = titleColor()
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldChampagne.copy(alpha = 0.2f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showCreateCategoryDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = GoldDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "إنشاء قسم أذكار جديد",
                                color = GoldDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Custom Categories Cards
            items(customCategories, key = { it }) { customCat ->
                Card(
                    onClick = { activeCategory = customCat },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldChampagne.copy(alpha = 0.6f)),
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
                            color = EmeraldLight,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = EmeraldDeep,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = customCat,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = titleColor()
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GoldChampagne.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "مخصص لك",
                                        color = GoldDark,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "يمكنك إضافة وتعديل أذكارك الخاصة بهذا القسم",
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
        // Detail View for Selected Category
        AzkarDetailView(
            categoryName = activeCategory!!,
            onBack = { activeCategory = null },
            repository = repository
        )
    }

    // Dialog for creating new custom category
    if (showCreateCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCreateCategoryDialog = false },
            title = {
                Text("إنشاء قسم أذكار جديد", fontWeight = FontWeight.Bold, color = titleColor())
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "اكتب اسم القسم (مثل: أذكار الحفظ، أذكار السفر، أذكار المذاكرة):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        placeholder = { Text("اسم القسم الجديد...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            repository.addCustomCategory(newCategoryName)
                            newCategoryName = ""
                            showCreateCategoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
                ) {
                    Text("إضافة القسم", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateCategoryDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
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
    val favoriteIds by repository.favoriteIds.collectAsState()
    val countsMap = remember { mutableStateMapOf<Int, Int>() }

    // Dialogs for custom adhkar (Add / Edit)
    var showCreateZekrDialog by remember { mutableStateOf(false) }
    var editingZekr by remember { mutableStateOf<ZekrItem?>(null) }
    var inputZekrText by remember { mutableStateOf("") }
    var inputZekrCount by remember { mutableStateOf("33") }
    var inputZekrFadl by remember { mutableStateOf("") }

    val isCustomCategory = repository.customCategories.value.contains(categoryName) || categoryName == "أذكاري المخصصة"

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
            if (isCustomCategory) {
                FloatingActionButton(
                    onClick = {
                        inputZekrText = ""
                        inputZekrCount = "33"
                        inputZekrFadl = ""
                        editingZekr = null
                        showCreateZekrDialog = true
                    },
                    containerColor = EmeraldDeep,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "إضافة ذكر لهذا القسم")
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
            // Header Row (بدون سهم رجوع مكرر؛ الرجوع موحد في الشريط العلوي للتطبيق)
            item(key = "category_header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = titleColor()
                    )

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

            if (azkarList.isEmpty()) {
                item(key = "empty_azkar") {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "لا توجد أذكار مضافة في هذا القسم حتى الآن.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            if (isCustomCategory) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        inputZekrText = ""
                                        inputZekrCount = "33"
                                        inputZekrFadl = ""
                                        showCreateZekrDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
                                ) {
                                    Text("أضف ذكرك الأول الآن", color = Color.White)
                                }
                            }
                        }
                    }
                }
            } else if (visibleAzkar.isEmpty()) {
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
                    val isFav = favoriteIds.contains(zekr.id)

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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Counter Pill
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

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Favorite button
                                        IconButton(
                                            onClick = { repository.toggleFavorite(zekr.id) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                contentDescription = "المفضلة",
                                                tint = if (isFav) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    // Action buttons for custom items: Edit / Delete
                                    if (zekr.isCustom) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = {
                                                    editingZekr = zekr
                                                    inputZekrText = zekr.arabicText
                                                    inputZekrCount = zekr.targetCount.toString()
                                                    inputZekrFadl = zekr.fadl
                                                    showCreateZekrDialog = true
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = GoldChampagne, modifier = Modifier.size(16.dp))
                                            }
                                            IconButton(
                                                onClick = { repository.deleteCustomZekr(zekr.id) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color(0xFFC62828), modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    } else {
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
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = zekr.arabicText,
                                    fontSize = 17.sp,
                                    lineHeight = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Start
                                )

                                if (zekr.fadl.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "الفضل: ${zekr.fadl}",
                                        fontSize = 12.sp,
                                        color = GoldChampagne,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                if (zekr.reference.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = zekr.reference,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog for adding or editing custom zekr
    if (showCreateZekrDialog) {
        AlertDialog(
            onDismissRequest = { showCreateZekrDialog = false },
            title = {
                Text(
                    text = if (editingZekr != null) "تعديل الذكر الخاص" else "إضافة ذكر لقسم «$categoryName»",
                    fontWeight = FontWeight.Bold,
                    color = titleColor()
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = inputZekrText,
                        onValueChange = { inputZekrText = it },
                        label = { Text("نص الذكر الكريم") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    OutlinedTextField(
                        value = inputZekrCount,
                        onValueChange = { inputZekrCount = it },
                        label = { Text("عدد المرات (مثال: 33 أو 100)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = inputZekrFadl,
                        onValueChange = { inputZekrFadl = it },
                        label = { Text("فضل الذكر (اختياري)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val count = inputZekrCount.toIntOrNull() ?: 33
                        if (inputZekrText.isNotBlank()) {
                            if (editingZekr != null) {
                                repository.editCustomZekr(editingZekr!!.id, inputZekrText, count, inputZekrFadl)
                            } else {
                                repository.addCustomZekr(categoryName, inputZekrText, count, inputZekrFadl)
                            }
                            showCreateZekrDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
                ) {
                    Text(if (editingZekr != null) "حفظ التعديل" else "إضافة الذكر", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateZekrDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
