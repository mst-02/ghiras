package com.islamic.noor.ui.hadith

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.EmeraldLight
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
        explanation = "البر جماع كل خير وطاعة وإحسان، والإثم ما اضطربت منه النفس ونفرت منه الفطرة السليمة."
    ),
    HadithItem(
        id = 5,
        title = "يسر الدين وسماحته",
        matn = "«إِنَّ الدِّينَ يُسْرٌ، وَلَنْ يُشَادَّ الدِّينَ أَحَدٌ إِلاَّ غَلَبَهُ، فَسَدِّدُوا وَقَارِبُوا وَأَبْشِرُوا، وَاسْتَعِينُوا بِالْغَدْوَةِ وَالرَّوْحَةِ وَشَيْءٍ مِنَ الدُّلْجَةِ».",
        narrator = "أبو هريرة رضي الله عنه",
        source = "صحيح البخاري",
        category = "صحيح البخاري",
        explanation = "شريعة الإسلام مبنية على التيسير ورفع الحرج؛ فداوم على العمل الصالح بالقصد والتوسط واغتنام أوقات النشاط."
    )
)

@Composable
fun HadithScreen(
    onBack: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("الكل") }

    val categories = listOf("الكل", "صحيح البخاري", "صحيح مسلم", "الأربعون النووية", "رياض الصالحين")

    val filteredHadiths = remember(searchQuery, selectedCategory) {
        SampleHadiths.filter { hadith ->
            val matchesCategory = selectedCategory == "الكل" || hadith.category == selectedCategory
            val matchesQuery = searchQuery.isBlank() ||
                    hadith.title.contains(searchQuery.trim(), ignoreCase = true) ||
                    hadith.matn.contains(searchQuery.trim(), ignoreCase = true) ||
                    hadith.explanation.contains(searchQuery.trim(), ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search Bar
        item(key = "search_bar") {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث في نصوص الأحاديث أو الرواة...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "بحث",
                        tint = GoldChampagne
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = titleColor(),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                singleLine = true
            )
        }

        // Category Pills
        item(key = "category_pills") {
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

        // Hadiths List
        if (filteredHadiths.isEmpty()) {
            item(key = "empty_state") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا توجد أحاديث مطابقة لبحثك.",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            items(filteredHadiths, key = { it.id }) { hadith ->
                HadithCardItem(hadith = hadith)
            }
        }
    }
}

@Composable
private fun HadithCardItem(hadith: HadithItem) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = GoldChampagne.copy(alpha = 0.15f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = GoldDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
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
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = hadith.matn,
                fontSize = 16.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "رواه: ${hadith.narrator} (${hadith.source})",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = GoldChampagne,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isExpanded) "إخفاء الشرح" else "شرح الحديث",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldChampagne
                    )
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
