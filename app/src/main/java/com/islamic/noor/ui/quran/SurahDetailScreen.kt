package com.islamic.noor.ui.quran

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.data.repository.QuranRepository
import com.islamic.noor.domain.model.Reciter
import com.islamic.noor.domain.model.Verse
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.EmeraldLight
import com.islamic.noor.ui.theme.EmeraldMedium
import com.islamic.noor.ui.theme.GoldBright
import com.islamic.noor.ui.theme.GoldChampagne
import com.islamic.noor.ui.theme.GoldDark
import com.islamic.noor.ui.theme.titleColor
import com.islamic.noor.ui.theme.isDarkThemeActive

// Client's verified reciters without copyright issues
val ClientApprovedReciters = listOf(
    Reciter("sh_abdelhalim", "الشيخ عبد الحليم", "تلاوة خاشعة وندية", isLocalSheikh = true),
    Reciter("sh_rabie", "الشيخ ربيع", "قراءة متقنة ومجودة بأحكام التجويد", isLocalSheikh = true),
    Reciter("sh_fawzy", "الشيخ فوزي", "صوت عذب ومؤثر من القلب", isLocalSheikh = true),
    Reciter("sh_doghim", "الشيخ دغيم", "تلاوة وقورة وهادئة", isLocalSheikh = true)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahDetailScreen(
    surahId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    repository: QuranRepository = remember { QuranRepository() }
) {
    val surah = remember(surahId) {
        QuranRepository.SURAH_LIST.firstOrNull { it.id == surahId }
    }
    val verses by repository.getVersesForSurah(surahId).collectAsState(initial = emptyList())

    // Reciters State
    var selectedReciter by remember { mutableStateOf(ClientApprovedReciters[0]) }
    var isPlaying by remember { mutableStateOf(false) }
    var showRecitersDialog by remember { mutableStateOf(false) }

    // Reading Modes: 0 = التمرير الرأسي (سكرول), 1 = تقليب الصفحات (يمين وشمال)
    var readingModeIndex by remember { mutableIntStateOf(0) }
    var fontSize by remember { mutableFloatStateOf(24f) }
    var lineSpacingMultiplier by remember { mutableFloatStateOf(1.8f) }
    var showDisplayControls by remember { mutableStateOf(false) }

    val bgColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(bottom = 74.dp)
        ) {
            // Top App Bar
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "سورة ${surah?.nameArabic ?: ""}",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "${surah?.revelationType ?: ""} • ${surah?.versesCount ?: 0} آيات • ص ${surah?.pageNumber ?: 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldChampagne
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                },
                actions = {
                    IconButton(onClick = { showDisplayControls = !showDisplayControls }) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "خيارات القراءة",
                            tint = if (showDisplayControls) GoldBright else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldDeep)
            )

            // Reading Options Panel
            AnimatedVisibility(visible = showDisplayControls) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // نمط القراءة: التمرير لأعلى vs تقليب صفحات يمين وشمال
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("طريقة العرض:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ModeChoiceChip(
                                    label = "التمرير لأعلى (سكرول)",
                                    isSelected = readingModeIndex == 0,
                                    onClick = { readingModeIndex = 0 }
                                )
                                ModeChoiceChip(
                                    label = "تقليب الصفحات (يمين/شمال)",
                                    isSelected = readingModeIndex == 1,
                                    onClick = { readingModeIndex = 1 }
                                )
                            }
                        }

                        // حجم الخط
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("حجم الخط: ${fontSize.toInt()}sp", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text("تباعد الأسطر: ${String.format("%.1f", lineSpacingMultiplier)}x", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            Slider(
                                value = fontSize,
                                onValueChange = { fontSize = it },
                                valueRange = 18f..38f,
                                colors = SliderDefaults.colors(
                                    thumbColor = GoldChampagne,
                                    activeTrackColor = GoldChampagne,
                                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // Mode 1: تقليب الصفحات يمين وشمال (Horizontal Page Flip)
            if (readingModeIndex == 1) {
                val pagerState = rememberPagerState(pageCount = { (verses.size / 4).coerceAtLeast(1) })
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اسحب يميناً ويساراً لتقليب صفحات المصحف",
                            fontSize = 12.sp,
                            color = GoldChampagne,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "صفحة ${pagerState.currentPage + 1} من ${pagerState.pageCount}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) { pageIndex ->
                        val startIndex = pageIndex * 4
                        val pageVerses = verses.drop(startIndex).take(4)

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp)
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (pageIndex == 0 && surahId != 9 && surahId != 1) {
                                    Text(
                                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                        fontSize = (fontSize + 2).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = titleColor(),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(bottom = 14.dp)
                                    )
                                }

                                for (verse in pageVerses) {
                                    Text(
                                        text = "${verse.textUthmani} ﴿${verse.verseNumber}﴾",
                                        fontSize = fontSize.sp,
                                        lineHeight = (fontSize * lineSpacingMultiplier).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor,
                                        textAlign = TextAlign.Justify,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Mode 0: التمرير الرأسي (سكرول مستمر للآيات مع التفسير)
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (surahId != 9 && surahId != 1) {
                        item(key = "basmalah") {
                            BasmalahBanner()
                        }
                    }

                    items(
                        items = verses,
                        key = { it.id },
                        contentType = { "verse_item" }
                    ) { verse ->
                        VerseCardItem(
                            verse = verse,
                            fontSize = fontSize,
                            lineSpacingMultiplier = lineSpacingMultiplier,
                            textColor = textColor
                        )
                    }
                }
            }
        }

        // Floating Audio Player Bar at Bottom (Only Client Approved Reciters)
        Card(
            shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldDeep),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(72.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play / Pause Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = GoldChampagne,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable { isPlaying = !isPlaying }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "إيقاف مؤقت" else "استماع",
                                tint = EmeraldDeep,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (isPlaying) "جاري الاستماع الآن" else "تلاوة صوتية",
                            fontSize = 11.sp,
                            color = GoldChampagne,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = selectedReciter.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Choose Reciter Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showRecitersDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = GoldChampagne,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تغيير القارئ",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Reciters Selection Dialog (Client approved 4 Shuyookh)
    if (showRecitersDialog) {
        AlertDialog(
            onDismissRequest = { showRecitersDialog = false },
            title = {
                Text(
                    text = "اختر القارئ المفضل",
                    fontWeight = FontWeight.Bold,
                    color = titleColor()
                )
            },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ClientApprovedReciters, key = { it.id }) { reciter ->
                        val isSelected = selectedReciter.id == reciter.id
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) (if (isDarkThemeActive()) EmeraldMedium else EmeraldLight) else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, GoldBright) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedReciter = reciter
                                    showRecitersDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = reciter.name,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) titleColor() else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = reciter.title,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GoldChampagne.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "قارئ معتمد",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRecitersDialog = false }) {
                    Text("إغلاق", color = titleColor(), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun VerseCardItem(
    verse: Verse,
    fontSize: Float,
    lineSpacingMultiplier: Float,
    textColor: Color
) {
    var showTafseer by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = GoldChampagne.copy(alpha = 0.15f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = verse.verseNumber.toString(),
                            fontWeight = FontWeight.Bold,
                            color = GoldDark,
                            fontSize = 12.sp
                        )
                    }
                }

                IconButton(
                    onClick = { showTafseer = !showTafseer },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "التفسير",
                        tint = if (showTafseer) GoldChampagne else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${verse.textUthmani} ﴿${verse.verseNumber}﴾",
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = (fontSize * lineSpacingMultiplier).sp,
                textAlign = TextAlign.Right,
                color = textColor,
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(visible = showTafseer) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "التفسير الميسر: ${verse.tafseerShort}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BasmalahBanner() {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = titleColor(),
                fontSize = 22.sp
            )
        }
    }
}

@Composable
private fun ModeChoiceChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) (if (isDarkThemeActive()) GoldDark else EmeraldDeep) else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
