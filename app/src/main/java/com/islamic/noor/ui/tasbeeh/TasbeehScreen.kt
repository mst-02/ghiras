package com.islamic.noor.ui.tasbeeh

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
    azkarRepository: AzkarRepository = remember { AzkarRepository.instance }
) {
    val context = LocalContext.current

    val defaultPhrases = listOf(
        "سُبْحَانَ اللَّهِ" to 33,
        "الْحَمْدُ لِلَّهِ" to 33,
        "لَا إِلَهَ إِلَّا اللَّهُ" to 100,
        "اللَّهُ أَكْبَرُ" to 33,
        "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ وَأَتُوبُ إِلَيْهِ" to 100,
        "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ" to 33,
        "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ ، سُبْحَانَ اللَّهِ الْعَظِيمِ" to 100,
        "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ" to 100,
        "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ" to 33,
        "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ" to 33,
        "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ" to 33
    )

    val customAzkar by azkarRepository.customAzkarList.collectAsState()

    var currentZekr by remember { mutableStateOf(defaultPhrases.first().first) }
    var targetCount by remember { mutableIntStateOf(defaultPhrases.first().second) }
    var count by remember { mutableIntStateOf(0) }
    var totalRounds by remember { mutableIntStateOf(0) }
    var isPressed by remember { mutableStateOf(false) }

    // Dialog state to choose Zekr
    var showZekrPicker by remember { mutableStateOf(false) }

    // Bead Size Controller (180dp to 280dp)
    var circleSizeDp by remember { mutableFloatStateOf(240f) }
    var showControllerPanel by remember { mutableStateOf(false) }

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

    fun resetAllCounters() {
        count = 0
        totalRounds = 0
        userRepository.resetTasbeeh()
        Toast.makeText(context, "تم تصفير العداد والمجموع الكلي بنجاح 🔄", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Action Bar: تحكم الحجم + تصفير مباشر + الهدف
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // زر تصفير سريع ومباشر
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFC62828).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color(0xFFC62828).copy(alpha = 0.5f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { resetAllCounters() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = "تصفير", tint = Color(0xFFC62828), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تصفير العداد", color = Color(0xFFC62828), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                // زر التحكم في حجم الحبة
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (showControllerPanel) GoldChampagne.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showControllerPanel = !showControllerPanel }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = GoldDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("الحجم", color = titleColor(), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            // Target Indicator
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = EmeraldLight
            ) {
                Text(
                    text = "الهدف: $targetCount",
                    color = EmeraldDeep,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        // Consolidated Controller Panel (Slider)
        AnimatedVisibility(visible = showControllerPanel) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "حجم حبة السبحة: ${circleSizeDp.toInt()}dp",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor()
                    )

                    Slider(
                        value = circleSizeDp,
                        onValueChange = { circleSizeDp = it },
                        valueRange = 180f..280f,
                        colors = SliderDefaults.colors(
                            thumbColor = GoldChampagne,
                            activeTrackColor = GoldChampagne,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }

        // Dhikr Box (الضغط عليه يفتح قائمة كاملة بالأذكار والأذكار المخصصة)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(1.2.dp, GoldChampagne.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .clickable { showZekrPicker = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle / Switch Button on the LEFT
                Surface(
                    shape = CircleShape,
                    color = GoldChampagne.copy(alpha = 0.2f),
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable {
                            val allCombined = defaultPhrases + customAzkar.map { it.text to it.count }
                            val picked = allCombined.random()
                            currentZekr = picked.first
                            targetCount = picked.second
                            count = 0
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "تبديل عشوائي",
                            tint = GoldDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Dhikr Text (Takes center/remaining space)
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "الذكر الحالي (اضغط لتغيير الذكر):",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.FormatListNumbered, contentDescription = null, tint = GoldDark, modifier = Modifier.size(13.dp))
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = currentZekr,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = titleColor(),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.width(42.dp))
            }
        }

        // Stats Display (Count & Total Rounds & Total Tasbeeh)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier.padding(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("الدورات المكتملة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text("$totalRounds", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = titleColor())
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier.padding(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("المجموع الكلي", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    val userProfile by userRepository.userProfile.collectAsState()
                    Text("${userProfile.totalTasbeeh}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GoldDark)
                }
            }
        }

        // The Central Interactive Bead (تصميم زمردي فاخر بدون هالة بيضاء مزعجة)
        Box(
            modifier = Modifier
                .size(circleSizeDp.dp)
                .scale(scale)
                .shadow(elevation = 14.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            EmeraldMedium,
                            EmeraldDeep
                        )
                    )
                )
                .border(3.5.dp, GoldBright, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    triggerHapticFeedback()
                    count++
                    userRepository.incrementTasbeeh()

                    if (count >= targetCount) {
                        count = 0
                        totalRounds++
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$count",
                    fontSize = (circleSizeDp * 0.28f).sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "اضغط للتسبيح",
                    fontSize = 12.sp,
                    color = GoldChampagne,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }

    // Modal Dialog to Select from All Azkar + Custom Azkar
    if (showZekrPicker) {
        val allCombined = defaultPhrases + customAzkar.map { it.text to it.count }

        AlertDialog(
            onDismissRequest = { showZekrPicker = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FormatListNumbered, contentDescription = null, tint = GoldChampagne)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("اختر الذكر المطلوب", fontWeight = FontWeight.Bold, color = titleColor())
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allCombined) { pair ->
                        val zText = pair.first
                        val zTarget = pair.second
                        val isSelected = currentZekr == zText

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) EmeraldLight else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isSelected) GoldBright else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    currentZekr = zText
                                    targetCount = zTarget
                                    count = 0
                                    showZekrPicker = false
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 11.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = zText,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) EmeraldDeep else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) GoldChampagne else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "$zTarget مرة",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) EmeraldDeep else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showZekrPicker = false }) {
                    Text("إغلاق", color = titleColor(), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
