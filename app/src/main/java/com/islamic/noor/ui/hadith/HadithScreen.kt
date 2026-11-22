package com.islamic.noor.ui.hadith

import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.EmeraldLight
import com.islamic.noor.ui.theme.GoldBright
import com.islamic.noor.ui.theme.GoldChampagne
import com.islamic.noor.ui.theme.GoldDark
import com.islamic.noor.ui.theme.titleColor

data class HadithItem(
    val id: Int,
    val title: String,
    val matn: String,
    val narrator: String,
    val source: String,
    val category: String,
    val explanation: String
)

val SampleHadiths = listOf(
    HadithItem(
        id = 1,
        title = "إنما الأعمال بالنيات",
        matn = "«إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى، فَمَنْ كَانَتْ هِجْرَتُهُ إِلَى دُنْيَا يُصِيبُهَا، أَوْ إِلَى امْرَأَةٍ يَنْكِحُهَا، فَهِجْرَتُهُ إِلَى مَا هَاجَرَ إِلَيْهِ».",
        narrator = "عمر بن الخطاب رضي الله عنه",
        source = "صحيح البخاري ومسلم",
        category = "صحيح البخاري",
        explanation = "هذا الحديث أصل عظيم من أصول الإسلام، ويدل على أن قبول أي عمل عند الله مشروط بإخلاص النية لله وحده."
    ),
    HadithItem(
        id = 2,
        title = "فضل التسبيح وعظيم وزنه",
        matn = "«كَلِمَتَانِ خَفِيفَتَانِ عَلَى اللِّسَانِ، ثَقِيلَتَانِ فِي الْمِيزَانِ، حَبِيبَتَانِ إِلَى الرَّحْمَنِ: سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ».",
        narrator = "أبو هريرة رضي الله عنه",
        source = "صحيح البخاري ومسلم",
        category = "صحيح مسلم",
        explanation = "بيان لسعة فضل الله ورحمته بعباده؛ كلمات يسيرة باللسان تثقل ميزان الحسنات يوم القيامة."
    ),
    HadithItem(
        id = 3,
        title = "محبة الخير للمسلمين",
        matn = "«لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لِأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ».",
        narrator = "أنس بن مالك رضي الله عنه",
        source = "متفق عليه",
        category = "الأربعون النووية",
        explanation = "كمال الإيمان يقتضي سلامة الصدر ومحبة الهداية والخير والرزق والبركة لسائر المسلمين كما يحبها الإنسان لنفسه."
    ),
    HadithItem(
        id = 4,
        title = "البر وحسن الخلق",
        matn = "«الْبِرُّ حُسْنُ الْخُلُقِ، وَالإِثْمُ مَا حَاكَ فِي نَفْسِكَ، وَكَرِهْتَ أَنْ يَطَّلِعَ عَلَيْهِ النَّاسُ».",
        narrator = "النواس بن سمعان رضي الله عنه",
        source = "صحيح مسلم",
        category = "رياض الصالحين",
        explanation = "حسن الخلق رأس الفضائل، والفطرة السليمة تنفر من الإثم وتضيق به الصدور."
    ),
    HadithItem(
        id = 5,
        title = "عجب لأمر المؤمن",
        matn = "«عَجَبًا لأَمْرِ الْمُؤْمِنِ، إِنَّ أَمْرَهُ كُلَّهُ خَيْرٌ، وَلَيْسَ ذَاكَ لأَحَدٍ إِلاَّ لِلْمُؤْمِنِ، إِنْ أَصَابَتْهُ سَرَّاءُ شَكَرَ فَكَانَ خَيْرًا لَهُ، وَإِنْ أَصَابَتْهُ ضَرَّاءُ صَبَرَ فَكَانَ خَيْرًا لَهُ».",
        narrator = "صهيب الرومي رضي الله عنه",
        source = "صحيح مسلم",
        category = "صحيح مسلم",
        explanation = "المؤمن رابح في جميع أحواله؛ إن جاءه الفضل شكر فاز، وإن نزل به البلاء صبر وأُجر."
    )
)

@Composable
fun HadithScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("الكل", "صحيح البخاري", "صحيح مسلم", "الأربعون النووية", "رياض الصالحين")
    var selectedCategory by remember { mutableStateOf("الكل") }
    val favoriteHadithIds = remember { mutableStateListOf<Int>() }

    val filteredHadiths = remember(searchQuery, selectedCategory) {
        SampleHadiths.filter { hadith ->
            val matchesCategory = selectedCategory == "الكل" || hadith.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                hadith.title.contains(searchQuery.trim()) ||
                hadith.matn.contains(searchQuery.trim()) ||
                hadith.narrator.contains(searchQuery.trim())
            matchesCategory && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search Box
        item(key = "search_box") {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث في متون الأحاديث أو الرواة...") },
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
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Category Filter Pills
        item(key = "category_chips") {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) titleColor() else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) titleColor() else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // Hadith Cards List
        items(filteredHadiths, key = { it.id }) { hadith ->
            val isFav = favoriteHadithIds.contains(hadith.id)
            HadithCard(
                hadith = hadith,
                isFavorite = isFav,
                onToggleFavorite = {
                    if (isFav) favoriteHadithIds.remove(hadith.id)
                    else favoriteHadithIds.add(hadith.id)
                },
                onShare = {
                    val shareText = "${hadith.title}\n\n${hadith.matn}\n\nرواه: ${hadith.narrator} (${hadith.source})\n\nتطبيق غِراس - غرسٌ طيب وأجرٌ دائم"
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "مشاركة الحديث الشريف"))
                }
            )
        }
    }
}

@Composable
private fun HadithCard(
    hadith: HadithItem,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Favorite + Title + Category Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "المفضلة",
                            tint = if (isFavorite) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = hadith.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = titleColor()
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldLight
                ) {
                    Text(
                        text = hadith.category,
                        color = EmeraldDeep,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Matn (Hadith Text)
            Text(
                text = hadith.matn,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Narrator info + Share + Explanation toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "رواه: ${hadith.narrator}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Share Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldChampagne.copy(alpha = 0.18f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onShare() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "مشاركة", tint = GoldDark, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("مشاركة", color = GoldDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Explanation toggle
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isExpanded = !isExpanded }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isExpanded) "إخفاء الشرح" else "الشرح",
                                color = GoldChampagne,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "الشرح الميسر:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = titleColor()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = hadith.explanation,
                        fontSize = 13.sp,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}
