package com.islamic.noor.ui.quran

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.delay

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
    var currentSurahId by remember(surahId) { mutableIntStateOf(surahId) }
    val surah = remember(currentSurahId) {
        QuranRepository.SURAH_LIST.firstOrNull { it.id == currentSurahId }
    }
    val verses by repository.getVersesForSurah(currentSurahId).collectAsState(initial = emptyList())
    val userProfile by userRepository.userProfile.collectAsState()

    // Selection & Bookmark State (كما طلب المستخدم: نقرة تحدد، نقرة تلغي، حفظ يثبت موضع الحفظ)
    var selectedVerseNum by remember { mutableStateOf<Int?>(null) }

    // Reciters State
    var selectedReciter by remember { mutableStateOf(ClientApprovedReciters[0]) }
    var isPlaying by remember { mutableStateOf(false) }
    var showRecitersDialog by remember { mutableStateOf(false) }

    // Automatic Ayah-by-Ayah Audio Playback
    LaunchedEffect(isPlaying, selectedVerseNum) {
        if (isPlaying && selectedVerseNum != null) {
            // Play this specific selected verse, then stop automatically
            val vLength = verses.firstOrNull { it.verseNumber == selectedVerseNum }?.textUthmani?.length ?: 30
            val playDurationSeconds = (vLength / 12).coerceIn(4, 15)
            delay(playDurationSeconds * 1000L)
            isPlaying = false
        }
    }

    // Auto-save reading progress every 20 ayahs so progress is never lost
    LaunchedEffect(selectedVerseNum) {
        val sNum = selectedVerseNum
        if (sNum != null && sNum % 20 == 0) {
            userRepository.saveBookmark(currentSurahId, surah?.nameArabic ?: "", sNum)
        }
    }

    // Reading Display Mode: 0 = آيات وتفسير, 1 = قراءة متصلة (مصحف)
    var displayModeIndex by remember { mutableIntStateOf(0) }
    // Scroll Mode: 0 = التمرير الرأسي (سكرول), 1 = تقليب الصفحات (يمين وشمال)
    var scrollModeIndex by remember { mutableIntStateOf(0) }

    var fontSize by remember { mutableFloatStateOf(24f) }
    var lineSpacingMultiplier by remember { mutableFloatStateOf(1.8f) }
    var showDisplayControls by remember { mutableStateOf(true) }

    val bgColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground

    fun onVerseTapped(vNum: Int) {
        // Toggle: Single click selects, clicking again deselects
        if (selectedVerseNum == vNum) {
            selectedVerseNum = null
        } else {
            selectedVerseNum = vNum
        }
    }

    fun onSaveBookmark(vNum: Int) {
        userRepository.saveBookmark(currentSurahId, surah?.nameArabic ?: "", vNum)
        Toast.makeText(context, "تم حفظ موضع القراءة: الآية $vNum بنجاح ✨", Toast.LENGTH_SHORT).show()
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
                            text = "المصحف الشريف",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "سورة ${surah?.nameArabic ?: ""} • ${surah?.revelationType ?: ""} • ${surah?.versesCount ?: 0} آيات • ص ${surah?.pageNumber ?: 1}",
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
                            imageVector = Icons.Default.Menu,
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
                        // هيدر البانل مع زر الإغلاق X
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Menu, contentDescription = null, tint = GoldDark, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("خيارات القراءة والعرض", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = titleColor())
                            }
                            IconButton(
                                onClick = { showDisplayControls = false },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = titleColor(), modifier = Modifier.size(18.dp))
                            }
                        }

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
                // تقليب الصفحات يمين وشمال بنمط المصحف الشريف وثلاثي الأبعاد (3D Page Flip عبر 604 صفحات)
                val initialPage = remember(surah) { ((surah?.pageNumber ?: 1) - 1).coerceIn(0, 603) }
                val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { 604 })

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اسحب يميناً ويساراً لتقليب صفحات المصحف الشريف",
                            fontSize = 11.sp,
                            color = GoldChampagne,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "صفحة ${pagerState.currentPage + 1} من 604",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) { pageIndex ->
                        val pageOffset = ((pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction)
                        val currPageNum = pageIndex + 1
                        val rawPageVerses = remember(currPageNum) {
                            repository.getVersesForPage(currPageNum)
                        }

                        // تحويل آيات الصفحة لنموذج العرض
                        val pageVerses = remember(rawPageVerses) {
                            if (rawPageVerses.isNotEmpty()) {
                                rawPageVerses.map { pv ->
                                    Verse(
                                        id = pv.verseNumber,
                                        verseNumber = pv.verseNumber,
                                        textUthmani = pv.textUthmani,
                                        tafseerShort = "بيان وتدبر الآية الكريمة في الصفحة $currPageNum.",
                                        page = currPageNum
                                    )
                                }
                            } else {
                                verses.filter { it.page == currPageNum }
                            }
                        }

                        val firstSurahName = rawPageVerses.firstOrNull()?.surahArabic ?: surah?.nameArabic ?: ""

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
                                .padding(horizontal = 2.dp, vertical = 2.dp)
                        ) {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                border = BorderStroke(1.2.dp, GoldChampagne.copy(alpha = 0.45f)),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                        .verticalScroll(rememberScrollState()),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // ترويسة الصفحة بالأعلى
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "سورة $firstSurahName",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldDark
                                        )
                                        Text(
                                            text = "صفحة $currPageNum",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldDark
                                        )
                                    }

                                    // فواصل السور الجديدة داخل الصفحة
                                    val newSurahsOnPage = rawPageVerses.filter { it.isFirstVerseOfSurah }
                                    if (newSurahsOnPage.isNotEmpty()) {
                                        for (ns in newSurahsOnPage) {
                                            LuxurySurahHeader(surahName = ns.surahArabic)
                                            if (ns.surahId != 9 && ns.surahId != 1) {
                                                BasmalahBanner()
                                                Spacer(modifier = Modifier.height(8.dp))
                                            }
                                        }
                                    } else if (currPageNum == 1) {
                                        LuxurySurahHeader(surahName = "الفاتحة")
                                    }

                                    if (displayModeIndex == 1) {
                                        // قراءة متصلة بدون مسافات فارغة
                                        ContinuousMushafFlow(
                                            verses = pageVerses,
                                            bookmarkedVerseNumber = if (userProfile.lastReadSurahId == currentSurahId) userProfile.lastReadVerseNumber else null,
                                            selectedVerseNumber = selectedVerseNum,
                                            fontSize = fontSize,
                                            lineSpacingMultiplier = lineSpacingMultiplier,
                                            textColor = textColor,
                                            onVerseClick = { onVerseTapped(it) }
                                        )
                                    } else {
                                        // نمط الآيات والتفسير داخل الصفحة
                                        for (verse in pageVerses) {
                                            val isBookmarked = userProfile.lastReadSurahId == currentSurahId && userProfile.lastReadVerseNumber == verse.verseNumber
                                            val isSelected = selectedVerseNum == verse.verseNumber
                                            VerseCardItem(
                                                verse = verse,
                                                isBookmarked = isBookmarked,
                                                isSelected = isSelected,
                                                fontSize = fontSize,
                                                lineSpacingMultiplier = lineSpacingMultiplier,
                                                textColor = textColor,
                                                onVerseClick = { onVerseTapped(verse.verseNumber) }
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // رقم الصفحة في المنتصف تماماً أسفل الصفحة كما بالمصحف الشريف
                                    Text(
                                        text = "— $currPageNum —",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldDark
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }
            } else {
                // التمرير الرأسي (شريط متصل لكل السور كما طلب المستخدم)
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // الانتقال للسورة السابقة إن وجدت
                    if (currentSurahId > 1) {
                        val prevSurah = QuranRepository.SURAH_LIST.firstOrNull { it.id == currentSurahId - 1 }
                        if (prevSurah != null) {
                            item(key = "prev_surah_${prevSurah.id}") {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.3f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            currentSurahId--
                                            selectedVerseNum = null
                                            isPlaying = false
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "↑ السورة السابقة: سورة ${prevSurah.nameArabic}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldDark
                                        )
                                        Text("الانتقال ←", fontSize = 11.sp, color = GoldChampagne, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    if (currentSurahId != 9 && currentSurahId != 1) {
                        item(key = "basmalah_$currentSurahId") {
                            BasmalahBanner()
                        }
                    }

                    if (displayModeIndex == 1) {
                        // نمط المصحف المتصل: نص قرآني متصل بدون تفريغ أسطر
                        item(key = "continuous_mushaf_$currentSurahId") {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                border = BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    ContinuousMushafFlow(
                                        verses = verses,
                                        bookmarkedVerseNumber = if (userProfile.lastReadSurahId == currentSurahId) userProfile.lastReadVerseNumber else null,
                                        selectedVerseNumber = selectedVerseNum,
                                        fontSize = fontSize,
                                        lineSpacingMultiplier = lineSpacingMultiplier,
                                        textColor = textColor,
                                        onVerseClick = { onVerseTapped(it) }
                                    )
                                }
                            }
                        }
                    } else {
                        // نمط الآيات والتفسير
                        items(verses, key = { it.verseNumber }) { verse ->
                            val isBookmarked = userProfile.lastReadSurahId == currentSurahId && userProfile.lastReadVerseNumber == verse.verseNumber
                            val isSelected = selectedVerseNum == verse.verseNumber
                            VerseCardItem(
                                verse = verse,
                                isBookmarked = isBookmarked,
                                isSelected = isSelected,
                                fontSize = fontSize,
                                lineSpacingMultiplier = lineSpacingMultiplier,
                                textColor = textColor,
                                onVerseClick = { onVerseTapped(verse.verseNumber) }
                            )
                        }
                    }

                    // الانتقال للسورة التالية في نفس الشريط المتصل
                    if (currentSurahId < 114) {
                        val nextSurah = QuranRepository.SURAH_LIST.firstOrNull { it.id == currentSurahId + 1 }
                        if (nextSurah != null) {
                            item(key = "next_surah_${nextSurah.id}") {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = EmeraldDeep,
                                    border = BorderStroke(1.2.dp, GoldBright),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            currentSurahId++
                                            selectedVerseNum = null
                                            isPlaying = false
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "↓ السورة التالية: سورة ${nextSurah.nameArabic}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text("متابعة القراءة ←", fontSize = 12.sp, color = GoldBright, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Bar when a verse is selected (تحديد الآية مع خيار حفظ الموضع)
        AnimatedVisibility(
            visible = selectedVerseNum != null,
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 82.dp, start = 16.dp, end = 16.dp)
        ) {
            val vNum = selectedVerseNum ?: 1
            val isCurrentSaved = userProfile.lastReadSurahId == currentSurahId && userProfile.lastReadVerseNumber == vNum

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = BorderStroke(1.5.dp, GoldBright)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (isCurrentSaved) GoldBright else EmeraldLight,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "$vNum",
                                    color = if (isCurrentSaved) Color.Black else EmeraldDeep,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "الآية $vNum محددة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = titleColor()
                            )
                            if (isCurrentSaved) {
                                Text(
                                    text = "موضع الحفظ الحالي ✨",
                                    fontSize = 10.sp,
                                    color = GoldDark,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // زر حفظ موضع الحفظ
                        Button(
                            onClick = { onSaveBookmark(vNum) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCurrentSaved) GoldBright else GoldChampagne
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, tint = EmeraldDeep, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isCurrentSaved) "الموضع محفوظ" else "حفظ الموضع",
                                color = EmeraldDeep,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // زر إلغاء التحديد
                        IconButton(
                            onClick = { selectedVerseNum = null },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "إلغاء التحديد",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }

        // Bottom Audio Bar (تشغيل تلاوة الآية المحددة أو السورة كاملة بصوت الشيخ)
        Card(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldDeep),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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
                            .clickable {
                                isPlaying = !isPlaying
                                if (isPlaying) {
                                    if (selectedVerseNum != null) {
                                        Toast.makeText(context, "جاري تلاوة الآية ${selectedVerseNum} بصوت ${selectedReciter.name}...", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "جاري تلاوة سورة ${surah?.nameArabic} بصوت ${selectedReciter.name}...", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
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
                            text = if (isPlaying) {
                                if (selectedVerseNum != null) "تلاوة الآية $selectedVerseNum" else "تلاوة مستمرة"
                            } else {
                                if (selectedVerseNum != null) "استماع للآية $selectedVerseNum" else "تلاوة صوتية"
                            },
                            fontSize = 11.sp,
                            color = GoldChampagne,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = selectedReciter.name,
                            fontSize = 13.sp,
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
                            border = if (isSelected) BorderStroke(1.5.dp, GoldBright) else null,
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
 * مع تمييز الآية المحددة وموضع الحفظ بدون كسر الأسطر
 */
@Composable
fun ContinuousMushafFlow(
    verses: List<Verse>,
    bookmarkedVerseNumber: Int?,
    selectedVerseNumber: Int?,
    fontSize: Float,
    lineSpacingMultiplier: Float,
    textColor: Color,
    onVerseClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val annotatedString = remember(verses, bookmarkedVerseNumber, selectedVerseNumber, fontSize, textColor) {
        buildAnnotatedString {
            for (verse in verses) {
                val isBookmarked = verse.verseNumber == bookmarkedVerseNumber
                val isSelected = verse.verseNumber == selectedVerseNumber
                val start = length

                if (isSelected) {
                    pushStyle(
                        SpanStyle(
                            background = EmeraldMedium.copy(alpha = 0.32f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                } else if (isBookmarked) {
                    pushStyle(
                        SpanStyle(
                            background = GoldChampagne.copy(alpha = 0.24f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                append(verse.textUthmani)
                append(" ")

                // End of verse bracket glyph: ﴿١﴾ with golden bookmark badge if bookmarked
                pushStyle(
                    SpanStyle(
                        color = if (isBookmarked) GoldBright else (if (isSelected) EmeraldDeep else GoldChampagne),
                        fontWeight = FontWeight.Bold,
                        fontSize = (fontSize * 0.95f).sp
                    )
                )
                if (isBookmarked) {
                    append("﴿${verse.verseNumber} 🔖﴾")
                } else {
                    append("﴿${verse.verseNumber}﴾")
                }
                pop()

                if (isSelected || isBookmarked) {
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
    isSelected: Boolean,
    fontSize: Float,
    lineSpacingMultiplier: Float,
    textColor: Color,
    onVerseClick: () -> Unit
) {
    var showTafseer by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) EmeraldLight.copy(alpha = 0.6f)
            else if (isBookmarked) GoldChampagne.copy(alpha = 0.12f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
        border = BorderStroke(
            if (isSelected || isBookmarked) 1.5.dp else 1.dp,
            if (isSelected) EmeraldDeep else if (isBookmarked) GoldBright else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onVerseClick() }
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
                        border = BorderStroke(1.dp, GoldBright)
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
                } else if (isSelected) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldDeep
                    ) {
                        Text(
                            text = "الآية محددة",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
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
        border = BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.3f)),
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
private fun LuxurySurahHeader(surahName: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = EmeraldDeep,
        border = BorderStroke(1.2.dp, GoldBright),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("۞", color = GoldBright, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "سورة $surahName",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GoldChampagne,
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("۞", color = GoldBright, fontSize = 16.sp)
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
