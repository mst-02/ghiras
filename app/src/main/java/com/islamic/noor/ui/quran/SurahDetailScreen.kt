package com.islamic.noor.ui.quran

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.data.repository.QuranRepository
import com.islamic.noor.data.repository.UserRepository
import com.islamic.noor.domain.model.Reciter
import com.islamic.noor.domain.model.Verse
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.EmeraldLight
import com.islamic.noor.ui.theme.EmeraldMedium
import com.islamic.noor.ui.theme.GoldBright
import com.islamic.noor.ui.theme.GoldChampagne
import com.islamic.noor.ui.theme.GoldDark
import com.islamic.noor.ui.theme.isDarkThemeActive
import com.islamic.noor.ui.theme.titleColor

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
    repository: QuranRepository = remember { QuranRepository() },
    userRepository: UserRepository = remember { UserRepository.instance }
) {
    val context = LocalContext.current
    val surah = remember(surahId) {
        QuranRepository.SURAH_LIST.firstOrNull { it.id == surahId }
    }
    val verses by repository.getVersesForSurah(surahId).collectAsState(initial = emptyList())
    val userProfile by userRepository.userProfile.collectAsState()

    // Reciters State
    var selectedReciter by remember { mutableStateOf(ClientApprovedReciters[0]) }
    var isPlaying by remember { mutableStateOf(false) }
    var showRecitersDialog by remember { mutableStateOf(false) }

    // Reading Display Mode: 0 = آيات وتفسير, 1 = قراءة متصلة (مصحف)
    var displayModeIndex by remember { mutableIntStateOf(0) }
    // Scroll Mode: 0 = التمرير الرأسي (سكرول), 1 = تقليب الصفحات (يمين وشمال)
    var scrollModeIndex by remember { mutableIntStateOf(0) }

    var fontSize by remember { mutableFloatStateOf(24f) }
    var lineSpacingMultiplier by remember { mutableFloatStateOf(1.8f) }
    var showDisplayControls by remember { mutableStateOf(false) }

    val bgColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground

    fun onBookmarkVerse(verseNum: Int) {
        userRepository.saveBookmark(surahId, surah?.nameArabic ?: "", verseNum)
        Toast.makeText(context, "تم حفظ موضع القراءة: الآية $verseNum", Toast.LENGTH_SHORT).show()
    }

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
                            contentDescription = "خيارات القراءة والعرض",
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
                        // نمط القراءة: آيات وتفسير vs قراءة متصلة (مصحف)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("نمط القراءة:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ModeChoiceChip(
                                    label = "آيات وتفسير",
                                    isSelected = displayModeIndex == 0,
                                    onClick = { displayModeIndex = 0 }
                                )
                                ModeChoiceChip(
                                    label = "قراءة متصلة (مصحف)",
                                    isSelected = displayModeIndex == 1,
                                    onClick = { displayModeIndex = 1 }
                                )
                            }
                        }

                        // طريقة التمرير: التمرير لأعلى (سكرول) vs تقليب الصفحات
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("طريقة التمرير:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ModeChoiceChip(
                                    label = "تمرير رأسي",
                                    isSelected = scrollModeIndex == 0,
                                    onClick = { scrollModeIndex = 0 }
                                )
                                ModeChoiceChip(
                                    label = "تقليب الصفحات",
                                    isSelected = scrollModeIndex == 1,
                                    onClick = { scrollModeIndex = 1 }
                                )
                            }
                        }

                        // حجم الخط وتباعد الأسطر
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

            // Main Content Area
            if (scrollModeIndex == 1) {
                // تقليب الصفحات يمين وشمال بنمط المصحف الشريف وثلاثي الأبعاد (3D Page Flip)
                val pagesMap: Map<Int, List<Verse>> = remember(verses) {
                    if (verses.isEmpty()) emptyMap()
                    else verses.groupBy { it.page }
                }
                val pageList: List<List<Verse>> = remember(pagesMap) { pagesMap.values.toList() }
                val totalPages = pageList.size.coerceAtLeast(1)
                val pagerState = rememberPagerState(pageCount = { totalPages })

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اسحب يميناً ويساراً لتقليب صفحات المصحف",
                            fontSize = 11.sp,
                            color = GoldChampagne,
                            fontWeight = FontWeight.Bold
                        )
                        val currPageNum = pageList.getOrNull(pagerState.currentPage)?.firstOrNull()?.page ?: (pagerState.currentPage + 1)
                        Text(
                            text = "صفحة $currPageNum من 604",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) { pageIndex ->
                        val pageOffset = ((pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction)
                        val pageVerses = pageList.getOrElse(pageIndex) { emptyList() }
                        val currPage = pageVerses.firstOrNull()?.page ?: (pageIndex + 1)

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    cameraDistance = 14 * density
                                    rotationY = (-pageOffset.coerceIn(-1f, 1f) * 90f)
                                    alpha = 1f - kotlin.math.abs(pageOffset).coerceIn(0f, 0.45f)
                                    transformOrigin = if (pageOffset > 0) {
                                        androidx.compose.ui.graphics.TransformOrigin(0f, 0.5f)
                                    } else {
                                        androidx.compose.ui.graphics.TransformOrigin(1f, 0.5f)
                                    }
                                }
                                .padding(horizontal = 2.dp, vertical = 4.dp)
                        ) {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldChampagne.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(14.dp)
                                        .verticalScroll(rememberScrollState()),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Page Top Header
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "سورة ${surah?.nameArabic ?: ""}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldDark
                                        )
                                        Text(
                                            text = "صفحة $currPage",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldDark
                                        )
                                    }

                                    if (pageIndex == 0 && surahId != 9 && surahId != 1) {
                                        BasmalahBanner()
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }

                                    if (displayModeIndex == 1) {
                                        // قراءة متصلة في نفس السطر بدون مسافات فارغة
                                        ContinuousMushafFlow(
                                            verses = pageVerses,
                                            bookmarkedVerseNumber = if (userProfile.lastReadSurahId == surahId) userProfile.lastReadVerseNumber else null,
                                            fontSize = fontSize,
                                            lineSpacingMultiplier = lineSpacingMultiplier,
                                            textColor = textColor,
                                            onVerseClick = { onBookmarkVerse(it) }
                                        )
                                    } else {
                                        // آيات وتفسير داخل الصفحة
                                        for (verse in pageVerses) {
                                            val isBookmarked = userProfile.lastReadSurahId == surahId && userProfile.lastReadVerseNumber == verse.verseNumber
                                            VerseCardItem(
                                                verse = verse,
                                                isBookmarked = isBookmarked,
                                                fontSize = fontSize,
                                                lineSpacingMultiplier = lineSpacingMultiplier,
                                                textColor = textColor,
                                                onBookmarkClick = { onBookmarkVerse(verse.verseNumber) }
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // التمرير الرأسي (سكرول مستمر)
                if (displayModeIndex == 1) {
                    // نمط المصحف المتصل: نص قرآني متصل بدون تفريغ أسطر
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (surahId != 9 && surahId != 1) {
                            item(key = "basmalah") {
                                BasmalahBanner()
                            }
                        }

                        item(key = "continuous_mushaf") {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    ContinuousMushafFlow(
                                        verses = verses,
                                        bookmarkedVerseNumber = if (userProfile.lastReadSurahId == surahId) userProfile.lastReadVerseNumber else null,
                                        fontSize = fontSize,
                                        lineSpacingMultiplier = lineSpacingMultiplier,
                                        textColor = textColor,
                                        onVerseClick = { onBookmarkVerse(it) }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // نمط الآيات والتفسير (بطاقة لكل آية مع زر التفسير وموضع الحفظ النظيف)
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
                            key = { it.id }
                        ) { verse ->
                            val isBookmarked = userProfile.lastReadSurahId == surahId && userProfile.lastReadVerseNumber == verse.verseNumber
                            VerseCardItem(
                                verse = verse,
                                isBookmarked = isBookmarked,
                                fontSize = fontSize,
                                lineSpacingMultiplier = lineSpacingMultiplier,
                                textColor = textColor,
                                onBookmarkClick = { onBookmarkVerse(verse.verseNumber) }
                            )
                        }
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

/**
 * نص قرآني متصل بدون تفريغ أسطر (نفس سطر القراءة كالمصحف الشريف)
 */
@Composable
fun ContinuousMushafFlow(
    verses: List<Verse>,
    bookmarkedVerseNumber: Int?,
    fontSize: Float,
    lineSpacingMultiplier: Float,
    textColor: Color,
    onVerseClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val annotatedString = remember(verses, bookmarkedVerseNumber, fontSize, textColor) {
        buildAnnotatedString {
            for (verse in verses) {
                val isBookmarked = verse.verseNumber == bookmarkedVerseNumber
                val start = length

                if (isBookmarked) {
                    pushStyle(
                        SpanStyle(
                            background = GoldChampagne.copy(alpha = 0.28f),
                            color = GoldDark,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                append(verse.textUthmani)
                append(" ")

                pushStyle(
                    SpanStyle(
                        color = GoldBright,
                        fontWeight = FontWeight.Bold,
                        fontSize = (fontSize * 0.82f).sp
                    )
                )
                append("﴿${verse.verseNumber}﴾")
                pop()

                if (isBookmarked) {
                    pushStyle(
                        SpanStyle(
                            color = Color.White,
                            background = GoldDark,
                            fontSize = (fontSize * 0.52f).sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    append(" موضع الحفظ ")
                    pop()
                    pop()
                }

                append(" ")

                val end = length
                addStringAnnotation(
                    tag = "VERSE",
                    annotation = "${verse.verseNumber}",
                    start = start,
                    end = end
                )
            }
        }
    }

    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = fontSize.sp,
            lineHeight = (fontSize * lineSpacingMultiplier).sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            textAlign = TextAlign.Justify
        ),
        modifier = modifier.fillMaxWidth(),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "VERSE", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    val vNum = annotation.item.toIntOrNull()
                    if (vNum != null) {
                        onVerseClick(vNum)
                    }
                }
        }
    )
}

@Composable
private fun VerseCardItem(
    verse: Verse,
    isBookmarked: Boolean,
    fontSize: Float,
    lineSpacingMultiplier: Float,
    textColor: Color,
    onBookmarkClick: () -> Unit
) {
    var showTafseer by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isBookmarked) GoldChampagne.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isBookmarked) GoldChampagne else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBookmarkClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: موضع الحفظ فقط يظهر عند الحفظ بدون أزرار حفظ غير مرغوبة
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isBookmarked) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldChampagne.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldBright)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = GoldBright,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "موضع الحفظ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldDark
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Tafseer info icon
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

            // Verse text with embedded number glyph ﴿1﴾
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
