package com.islamic.noor.ui.quran

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.islamic.noor.data.repository.QuranRepository
import com.islamic.noor.data.repository.QuranVoiceMatch
import com.islamic.noor.data.repository.UserRepository
import com.islamic.noor.domain.model.Surah
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.EmeraldLight
import com.islamic.noor.ui.theme.GoldBright
import com.islamic.noor.ui.theme.GoldChampagne
import com.islamic.noor.ui.theme.GoldDark
import com.islamic.noor.ui.theme.titleColor

@Composable
fun QuranScreen(
    onSurahClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    repository: QuranRepository = remember { QuranRepository() },
    userRepository: UserRepository = remember { UserRepository.instance }
) {
    val context = LocalContext.current
    val surahs by repository.getAllSurahs().collectAsState(initial = emptyList())
    val userProfile by userRepository.userProfile.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showKhatmDuaDialog by remember { mutableStateOf(false) }

    val surahListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)

    // Always start at the beginning of the Quran (Al-Fatihah) when opening the screen or when surahs are ready
    LaunchedEffect(surahs.size) {
        if (surahs.isNotEmpty()) {
            surahListState.scrollToItem(0)
        }
    }

    // Step-by-step back handler: Clear search or dismiss dialog before popping back
    BackHandler(enabled = searchQuery.isNotBlank() || showKhatmDuaDialog) {
        if (showKhatmDuaDialog) {
            showKhatmDuaDialog = false
        } else {
            searchQuery = ""
        }
    }

    // In-App Speech Recognition State
    var isListening by remember { mutableStateOf(false) }
    var showVoiceOverlay by remember { mutableStateOf(false) }
    var voiceStatusMessage by remember { mutableStateOf("تفضل بالتلاوة أو نطق اسم السورة...") }

    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else null
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                speechRecognizer?.destroy()
            } catch (_: Exception) {}
        }
    }

    fun startInAppVoiceListening() {
        if (speechRecognizer == null) {
            Toast.makeText(context, "التعرف الصوتي غير متوفر على هذا الجهاز", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA")
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                voiceStatusMessage = "تفضل بالتلاوة أو نطق اسم السورة..."
            }
            override fun onBeginningOfSpeech() {
                voiceStatusMessage = "جاري الاستماع لصوتك العذب..."
            }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                voiceStatusMessage = "جاري التدقيق والمطابقة مع آيات القرآن الكريم..."
            }
            override fun onError(error: Int) {
                isListening = false
                showVoiceOverlay = false
                Toast.makeText(context, "لم يتم التقاط صوت واضح، يرجى المحاولة مرة أخرى", Toast.LENGTH_SHORT).show()
            }
            override fun onResults(results: Bundle?) {
                isListening = false
                showVoiceOverlay = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spoken = matches?.firstOrNull()?.trim()

                if (!spoken.isNullOrBlank()) {
                    val matchResult = repository.matchVoiceQueryToQuran(spoken, context)
                    when (matchResult) {
                        is QuranVoiceMatch.SurahMatch -> {
                            searchQuery = matchResult.surah.nameArabic
                            Toast.makeText(context, "✨ تم التعرف على: سورة ${matchResult.surah.nameArabic}", Toast.LENGTH_SHORT).show()
                        }
                        is QuranVoiceMatch.AyahMatch -> {
                            searchQuery = matchResult.spokenText
                            Toast.makeText(context, "✨ تم العثور على الآية في سورة ${matchResult.verse.surahName}", Toast.LENGTH_SHORT).show()
                        }
                        is QuranVoiceMatch.NoMatch -> {
                            Toast.makeText(context, "⚠️ لم يتم العثور على سورة أو آية مطابقة. يرجى نطق اسم السورة أو مقطع من الآية بدقة.", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "لم يتم التقاط كلمات واضحة", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        try {
            speechRecognizer.startListening(intent)
            isListening = true
            showVoiceOverlay = true
            voiceStatusMessage = "جاري التهيئة والاستماع..."
        } catch (_: Exception) {
            Toast.makeText(context, "تعذر بدء التسجيل الصوتي", Toast.LENGTH_SHORT).show()
        }
    }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startInAppVoiceListening()
        } else {
            Toast.makeText(context, "يرجى منح إذن الميكروفون للبحث الصوتي في القرآن", Toast.LENGTH_SHORT).show()
        }
    }

    val verseSearchResults by repository.searchVerses(searchQuery).collectAsState(initial = emptyList())

    val filteredSurahs = remember(searchQuery, surahs) {
        if (searchQuery.isBlank()) surahs
        else surahs.filter {
            it.nameArabic.contains(searchQuery.trim()) ||
            it.nameEnglish.contains(searchQuery.trim(), ignoreCase = true) ||
            it.id.toString() == searchQuery.trim()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Box for Verses & Surahs with In-App Voice Recognition
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث في الآيات أو اسم السورة...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "بحث", tint = GoldChampagne)
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "مسح")
                            }
                        }
                        IconButton(onClick = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                startInAppVoiceListening()
                            } else {
                                recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "البحث الصوتي القرآني",
                                tint = GoldBright
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = titleColor(),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            )

            // Content Area: Search Results or Surah List
            if (searchQuery.isNotBlank() && verseSearchResults.isNotEmpty()) {
                // Verse Search Results View
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item(key = "search_header") {
                        Text(
                            text = "نتائج البحث في الآيات الكريمة (${verseSearchResults.size} آية):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = titleColor(),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    items(verseSearchResults, key = { "${it.surahId}_${it.verseNumber}" }) { result ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSurahClick(result.surahId) }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
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
                                            text = "سورة ${result.surahName} • آية ${result.verseNumber}",
                                            color = EmeraldDeep,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    Text(
                                        text = "فتح السورة ←",
                                        fontSize = 11.sp,
                                        color = GoldChampagne,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "«${result.textUthmani}»",
                                    fontSize = 16.sp,
                                    lineHeight = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                if (result.tafseerShort.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "التفسير الميسر: ${result.tafseerShort}",
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Surah List View
                LazyColumn(
                    state = surahListState,
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredSurahs, key = { it.id }) { surah ->
                        SurahListItemCard(
                            surah = surah,
                            onClick = { onSurahClick(surah.id) }
                        )
                    }

                    // 2. بطاقة "دعاء ختم القرآن الكريم" في آخر القائمة تماماً بعد سورة الناس (114) كما طلب المستخدم!
                    if (searchQuery.isBlank()) {
                        item(key = "khatm_quran_dua_bottom") {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldChampagne.copy(alpha = 0.8f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                                    .clickable { showKhatmDuaDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = GoldChampagne.copy(alpha = 0.2f),
                                            modifier = Modifier.size(44.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.AutoAwesome,
                                                    contentDescription = null,
                                                    tint = GoldBright,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "دعاء ختم القرآن الكريم",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = titleColor()
                                            )
                                            Text(
                                                text = "الدعاء المأثور والجامع لخيري الدنيا والآخرة",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = EmeraldLight
                                    ) {
                                        Text(
                                            text = "قراءة الدعاء",
                                            color = EmeraldDeep,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // بطاقة استكمال القراءة الثابتة بالأسفل فوق شريط التنقل السفلي حسب طلب المستخدم بالضبط
        if (searchQuery.isBlank() && userProfile.lastReadSurahName.isNotBlank()) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldChampagne.copy(alpha = 0.8f)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onSurahClick(userProfile.lastReadSurahId) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = GoldChampagne.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    tint = GoldBright,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "استكمال القراءة",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = titleColor()
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "سورة ${userProfile.lastReadSurahName} • الآية ${userProfile.lastReadVerseNumber}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = EmeraldDeep
                    ) {
                        Text(
                            text = "متابعة ←",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }

    // Full Khatm Al-Quran Dua Dialog (Azkar Card Style)
    if (showKhatmDuaDialog) {
        val khatmCards = listOf(
            "اللَّهُمَّ ارْحَمْنِي بِالقُرْآنِ، وَاجْعَلْهُ لِي إِمَاماً وَنُوراً وَهُدًى وَرَحْمَةً.",
            "اللَّهُمَّ ذَكِّرْنِي مِنْهُ مَا نَسِيتُ، وَعَلِّمْنِي مِنْهُ مَا جَهِلْتُ، وَارْزُقْنِي تِلاَوَتَهُ آنَاءَ اللَّيْلِ وَأَطْرَافَ النَّهَارِ، وَاجْعَلْهُ لِي حُجَّةً يَا رَبَّ العَالَمِينَ.",
            "اللَّهُمَّ أَصْلِحْ لِي دِينِي الَّذِي هُوَ عِصْمَةُ أَمْرِي، وَأَصْلِحْ لِي دُنْيَايَ الَّتِي فِيهَا مَعَاشِي، وَأَصْلِحْ لِي آخِرَتِي الَّتِي فِيهَا مَعَادِي، وَاجْعَلِ الحَيَاةَ زِيَادَةً لِي فِي كُلِّ خَيْرٍ، وَاجْعَلِ المَوْتَ رَاحَةً لِي مِنْ كُلِّ شَرٍّ.",
            "اللَّهُمَّ اجْعَلْ خَيْرَ عُمْرِي آخِرَهُ، وَخَيْرَ عَمَلِي خَوَاتِمَهُ، وَخَيْرَ أَيَّامِي يَوْمَ أَلْقَاكَ فِيهِ.",
            "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِيشَةً هَنِيَّةً، وَمِيتَةً سَوِيَّةً، وَمَرَدّاً غَيْرَ مُخْزٍ وَلاَ فَاضِحٍ.",
            "اللَّهُمَّ اقْسِمْ لَنَا مِنْ خَشْيَتِكَ مَا تَحُولُ بِهِ بَيْنَنَا وَبَيْنَ مَعْصِيَتِكَ، وَمِنْ طَاعَتِكَ مَا تُبَلِّغُنَا بِهِ جَنَّتَكَ، وَمِنَ اليَقِينِ مَا تُهَوِّنُ بِهِ عَلَيْنَا مَصَائِبَ الدُّنْيَا، وَمَتِّعْنَا بِأَسْمَاعِنَا وَأَبْصَارِنَا وَقُوَّتِنَا مَا أَحْيَيْتَنَا."
        )

        AlertDialog(
            onDismissRequest = { showKhatmDuaDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GoldChampagne,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "دعاء ختم القرآن الكريم",
                        fontWeight = FontWeight.Bold,
                        color = titleColor()
                    )
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(khatmCards.size) { idx ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = EmeraldLight,
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${idx + 1}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldDeep
                                            )
                                        }
                                    }
                                    Text(
                                        text = "دعاء مبارك",
                                        fontSize = 11.sp,
                                        color = GoldDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = khatmCards[idx],
                                    fontSize = 14.sp,
                                    lineHeight = 24.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Right
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showKhatmDuaDialog = false
                        Toast.makeText(context, "تقبل الله ختمتكم المباركة وجعلها في ميزان حسناتكم ✨", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("تمت قراءة دعاء الختم بحمد الله ✨", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // In-App Quran Voice Search Listening Dialog (تسجيل وبحث صوتي مباشر داخل التطبيق)
    if (showVoiceOverlay) {
        val infiniteTransition = rememberInfiniteTransition(label = "voice_pulse")
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 0.94f,
            targetValue = 1.14f,
            animationSpec = infiniteRepeatable(
                animation = tween(850),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_scale"
        )

        AlertDialog(
            onDismissRequest = {
                try {
                    speechRecognizer?.stopListening()
                    speechRecognizer?.cancel()
                } catch (_: Exception) {}
                isListening = false
                showVoiceOverlay = false
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "البحث الصوتي في القرآن الكريم",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = titleColor()
                    )
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = EmeraldDeep,
                        border = androidx.compose.foundation.BorderStroke(2.dp, GoldBright),
                        modifier = Modifier
                            .size(80.dp)
                            .scale(pulseScale)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = GoldBright,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = voiceStatusMessage,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = EmeraldDeep,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "انطق اسم السورة (مثل: سورة الكهف)\nأو اقرأ مقطعاً صحيحاً من الآية الكريمة",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        try {
                            speechRecognizer?.stopListening()
                            speechRecognizer?.cancel()
                        } catch (_: Exception) {}
                        isListening = false
                        showVoiceOverlay = false
                    }
                ) {
                    Text("إلغاء", color = titleColor(), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun SurahListItemCard(
    surah: Surah,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Surah Number Diamond Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GoldChampagne.copy(alpha = 0.15f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = surah.id.toString(),
                            fontWeight = FontWeight.Bold,
                            color = GoldDark,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = surah.nameArabic,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = titleColor()
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${surah.revelationType} • ${surah.versesCount} آيات • ص ${surah.pageNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Arabic Surah Title Calligraphy style
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = EmeraldLight
            ) {
                Text(
                    text = "سُورَةُ ${surah.nameArabic}",
                    color = EmeraldDeep,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}
