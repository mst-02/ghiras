package com.islamic.noor.ui.tasbeeh

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.data.repository.AzkarRepository
import com.islamic.noor.data.repository.UserRepository
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.EmeraldLight
import com.islamic.noor.ui.theme.EmeraldMedium
import com.islamic.noor.ui.theme.GoldBright
import com.islamic.noor.ui.theme.GoldChampagne
import com.islamic.noor.ui.theme.GoldDark
import com.islamic.noor.ui.theme.titleColor

@Composable
fun TasbeehScreen(
    modifier: Modifier = Modifier,
    userRepository: UserRepository = remember { UserRepository.instance },
    azkarRepository: AzkarRepository = remember { AzkarRepository() }
) {
    val context = LocalContext.current

    val initialPhrases = listOf(
        "سُبْحَانَ اللَّهِ",
        "الْحَمْدُ لِلَّهِ",
        "لَا إِلَهَ إِلَّا اللَّهُ",
        "اللَّهُ أَكْبَرُ",
        "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ وَأَتُوبُ إِلَيْهِ",
        "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ",
        "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
        "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ",
        "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
        "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ",
        "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ"
    )
    val phrases = remember { mutableStateListOf(*initialPhrases.toTypedArray()) }
    val customAzkar by azkarRepository.customAzkarList.collectAsState()

    var currentZekr by remember { mutableStateOf(phrases.first()) }
    var count by remember { mutableIntStateOf(0) }
    var totalRounds by remember { mutableIntStateOf(0) }
    var isPressed by remember { mutableStateOf(false) }

    // Bead Size Controller (180dp to 300dp)
    var circleSizeDp by remember { mutableFloatStateOf(240f) }
    var showSizeSlider by remember { mutableStateOf(false) }

    // Search and select dhikr dialog
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 800f),
        label = "bead_scale"
    )

    fun triggerHapticFeedback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                v?.vibrate(25)
            }
        } catch (_: Exception) {}
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Action Bar (Resize toggle, Dhikr switcher, Reset)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Search Dhikr Button
                IconButton(onClick = { showSearchDialog = true }) {
                    Surface(
                        shape = CircleShape,
                        color = GoldChampagne.copy(alpha = 0.2f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Search, contentDescription = "بحث واختيار ذكر", tint = GoldDark)
                        }
                    }
                }

                // Randomize Dhikr Button
                IconButton(onClick = {
                    val allAvailable = phrases + customAzkar.map { it.arabicText }
                    currentZekr = allAvailable.random()
                    count = 0
                }) {
                    Surface(
                        shape = CircleShape,
                        color = GoldChampagne.copy(alpha = 0.2f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Shuffle, contentDescription = "تبديل عشوائي", tint = GoldDark)
                        }
                    }
                }

                // Resize Slider Toggle
                IconButton(onClick = { showSizeSlider = !showSizeSlider }) {
                    Surface(
                        shape = CircleShape,
                        color = if (showSizeSlider) GoldChampagne else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.FormatSize, contentDescription = "تعديل الحجم", tint = if (showSizeSlider) EmeraldDeep else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Reset Button
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFC62828).copy(alpha = 0.12f),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        count = 0
                        totalRounds = 0
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "تصفير",
                        tint = Color(0xFFC62828),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "تصفير",
                        color = Color(0xFFC62828),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Bead Size Slider Panel
        AnimatedVisibility(visible = showSizeSlider) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "حجم خرزة التسبيح:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = titleColor())
                        Text(text = "${circleSizeDp.toInt()}dp", fontSize = 12.sp, color = GoldChampagne, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = circleSizeDp,
                        onValueChange = { circleSizeDp = it },
                        valueRange = 180f..300f,
                        colors = SliderDefaults.colors(thumbColor = EmeraldDeep, activeTrackColor = EmeraldDeep)
                    )
                }
            }
        }

        // Active Dhikr Banner (Clickable to change)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showSearchDialog = true }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "الذكر الحالي (اضغط لتغييره أو البحث):",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentZekr,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = titleColor(),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Stats Display (Count & Total Rounds)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier.padding(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("الدورات (مجموعات 33)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text("$totalRounds", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = titleColor())
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier.padding(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("الهدف الحالي", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text("33", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GoldChampagne)
                }
            }
        }

        // Luxury Resizable Bead
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .size(circleSizeDp.dp)
                .scale(scale)
                .shadow(16.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(EmeraldMedium, EmeraldDeep)
                    )
                )
                .border(4.dp, GoldChampagne, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    triggerHapticFeedback()
                    count++
                    userRepository.incrementTasbeehCount()
                    if (count >= 33) {
                        totalRounds++
                        count = 0
                    }
                }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = count.toString(),
                    fontSize = (circleSizeDp * 0.28).sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldBright,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "اضغط للتسبيح",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = (circleSizeDp * 0.055).sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }

    // Search & Select Dhikr Dialog
    if (showSearchDialog) {
        val allOptions = remember(customAzkar) {
            (phrases + customAzkar.map { it.arabicText }).distinct()
        }

        val filteredOptions = remember(searchQuery, allOptions) {
            if (searchQuery.isBlank()) allOptions
            else allOptions.filter { it.contains(searchQuery.trim(), ignoreCase = true) }
        }

        AlertDialog(
            onDismissRequest = { showSearchDialog = false },
            title = {
                Text(text = "اختيار ذكر للسبحة", fontWeight = FontWeight.Bold, color = titleColor())
            },
            text = {
                Column(modifier = Modifier.height(350.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("ابحث عن ذكر...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldChampagne) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = null)
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(filteredOptions) { phrase ->
                            val isSelected = phrase == currentZekr
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) EmeraldLight else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldDeep) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentZekr = phrase
                                        count = 0
                                        showSearchDialog = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = phrase,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) EmeraldDeep else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSearchDialog = false }) {
                    Text("إغلاق", color = titleColor())
                }
            }
        )
    }
}
