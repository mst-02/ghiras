package com.islamic.noor.ui.quran

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.data.repository.QuranRepository
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
    repository: QuranRepository = remember { QuranRepository() }
) {
    val surahs by repository.getAllSurahs().collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var showKhatmDuaDialog by remember { mutableStateOf(false) }

    val verseSearchResults by repository.searchVerses(searchQuery).collectAsState(initial = emptyList())

    val filteredSurahs = remember(searchQuery, surahs) {
        if (searchQuery.isBlank()) surahs
        else surahs.filter {
            it.nameArabic.contains(searchQuery.trim()) ||
            it.nameEnglish.contains(searchQuery.trim(), ignoreCase = true) ||
            it.id.toString() == searchQuery.trim()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Box for Verses & Surahs
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("ابحث في الآيات أو اسم السورة...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "بحث", tint = GoldChampagne)
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "مسح")
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

        // Khatm Al-Quran Dua Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clickable { showKhatmDuaDialog = true }
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
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = GoldBright,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "دعاء ختم القرآن الكريم",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = titleColor()
                        )
                        Text(
                            text = "الدعاء المأثور والجامع لخيري الدنيا والآخرة",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldLight
                ) {
                    Text(
                        text = "قراءة الدعاء",
                        color = EmeraldDeep,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Content Area: Search Results or Surah List
        if (searchQuery.isNotBlank() && verseSearchResults.isNotEmpty()) {
            // Verse Search Results View
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
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
                                    text = "التفسير: ${result.tafseerShort}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Surahs List (114 Surahs)
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = filteredSurahs,
                    key = { it.id },
                    contentType = { "surah_row" }
                ) { surah ->
                    SurahRowItem(
                        surah = surah,
                        onClick = { onSurahClick(surah.id) }
                    )
                }
            }
        }
    }

    // Khatm Al-Quran Dua Dialog
    if (showKhatmDuaDialog) {
        val scrollState = rememberScrollState()
        AlertDialog(
            onDismissRequest = { showKhatmDuaDialog = false },
            title = {
                Text(
                    text = "دعاء ختم القرآن الكريم",
                    fontWeight = FontWeight.Bold,
                    color = titleColor()
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .height(380.dp)
                        .verticalScroll(scrollState)
                ) {
                    Text(
                        text = QuranRepository.KHATM_AL_QURAN_DUA,
                        fontSize = 16.sp,
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showKhatmDuaDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
                ) {
                    Text("تم الدعاء بحمد الله", color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun SurahRowItem(
    surah: Surah,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Number in Octagonal / Round Badge
            Surface(
                shape = CircleShape,
                color = GoldChampagne.copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = surah.id.toString(),
                        fontWeight = FontWeight.Bold,
                        color = GoldDark,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "سورة ${surah.nameArabic}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = titleColor()
                )
                Text(
                    text = "${surah.revelationType} • ${surah.versesCount} آيات • ص ${surah.pageNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }

            Text(
                text = surah.nameEnglish,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = GoldChampagne
            )
        }
    }
}
