package com.islamic.noor.ui.duas

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
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
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.EmeraldLight
import com.islamic.noor.ui.theme.GoldChampagne
import com.islamic.noor.ui.theme.GoldDark
import com.islamic.noor.ui.theme.titleColor

data class DuaItem(
    val id: Int,
    val title: String,
    val text: String,
    val reference: String,
    val category: String
)

val ComprehensiveDuasList = listOf(
    // قسم المتوفين
    DuaItem(
        id = 1,
        title = "دعاء جامع بالرحمة والمغفرة",
        text = "اللَّهُمَّ اغْفِرْ لَهُ وَارْحَمْهُ، وَعَافِهِ وَاعْفُ عَنْهُ، وَأَكْرِمْ نُزُلَهُ، وَوَسِّعْ مُدْخَلَهُ، وَاغْسِلْهُ بِالْمَاءِ وَالثَّلْجِ وَالْبَرَدِ، وَنَقِّهِ مِنَ الْخَطَايَا كَمَا نَقَّيْتَ الثَّوْبَ الأَبْيَضَ مِنَ الدَّنَسِ.",
        reference = "صحيح مسلم",
        category = "أدعية للمتوفين"
    ),
    DuaItem(
        id = 2,
        title = "دعاء لتنوير القبر والفسحة فيه",
        text = "اللَّهُمَّ اجْعَلْ قَبْرَهُ رَوْضَةً مِنْ رِيَاضِ الْجَنَّةِ، وَلاَ تَجْعَلْهُ حُفْرَةً مِنْ حُفَرِ النَّارِ، اللَّهُمَّ افْسَحْ لَهُ فِي قَبْرِهِ مَدَّ بَصَرِهِ، وَافْرُشْ قَبْرَهُ مِنْ فِرَاشِ الْجَنَّةِ.",
        reference = "مأثور",
        category = "أدعية للمتوفين"
    ),
    // قسم الرزق والفرج
    DuaItem(
        id = 3,
        title = "دعاء قضاء الدين وسعة الرزق",
        text = "«اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَأَعُوذُ بِكَ مِنَ الْعَجْزِ وَالْكَسَلِ، وَأَعُوذُ بِكَ مِنَ الْجُبْنِ وَالْبُخْلِ، وَأَعُوذُ بِكَ مِنْ غَلَبَةِ الدَّيْنِ وَقَهْرِ الرِّجَالِ».",
        reference = "صحيح البخاري",
        category = "الرزق وتفريج الهم"
    ),
    DuaItem(
        id = 4,
        title = "دعاء الكفاية بالحلال",
        text = "«اللَّهُمَّ اكْفِنِي بِحَلاَلِكَ عَنْ حَرَامِكَ، وَأَغْنِنِي بِفَضْلِكَ عَمَّنْ سِوَاكَ».",
        reference = "جامع الترمذي",
        category = "الرزق وتفريج الهم"
    ),
    // قسم الشفاء
    DuaItem(
        id = 5,
        title = "دعاء عيادة المريض بالشفاء",
        text = "«اللَّهُمَّ رَبَّ النَّاسِ، أَذْهِبِ الْبَاسَ، اشْفِهِ وَأَنْتَ الشَّافِي، لاَ شِفَاءَ إِلاَّ شِفَاؤُكَ، شِفَاءً لاَ يُغَادِرُ سَقَمًا».",
        reference = "متفق عليه",
        category = "الشفاء والعافية"
    ),
    // جوامع الدعاء
    DuaItem(
        id = 6,
        title = "دعاء حسنة الدنيا والآخرة",
        text = "«رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ».",
        reference = "سورة البقرة: 201",
        category = "جوامع الدعاء"
    )
)

@Composable
fun DuasScreen(
    onBack: () -> Unit = {}
) {
    val categories = listOf("أدعية للمتوفين", "الرزق وتفريج الهم", "الشفاء والعافية", "جوامع الدعاء")
    var selectedCategory by remember { mutableStateOf("أدعية للمتوفين") }
    var deceasedName by remember { mutableStateOf("") }

    val filteredDuas = remember(selectedCategory) {
        ComprehensiveDuasList.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Category Pills
        item(key = "categories") {
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

        // Name Personalization Card (Active when in deceased category)
        if (selectedCategory == "أدعية للمتوفين") {
            item(key = "name_input") {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GoldChampagne.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = GoldChampagne.copy(alpha = 0.2f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = GoldDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "تخصيص الدعاء باسم فقيدك الغالي",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = titleColor()
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = deceasedName,
                            onValueChange = { deceasedName = it },
                            placeholder = { Text("مثال: أبي الحبيب، أمي الغالية، أو الاسم...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = titleColor(),
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        )

                        if (deceasedName.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "سيتم إدراج «$deceasedName» تلقائياً في نصوص الأدعية بالأسفل بإذن الله.",
                                fontSize = 12.sp,
                                color = titleColor(),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Duas List
        items(filteredDuas, key = { it.id }) { dua ->
            DuaDisplayCard(dua = dua, deceasedName = deceasedName)
        }
    }
}

@Composable
private fun DuaDisplayCard(dua: DuaItem, deceasedName: String) {
    val personalizedText = if (deceasedName.isNotBlank() && dua.category == "أدعية للمتوفين") {
        dua.text.replace("لَهُ", "لـ $deceasedName")
            .replace("قَبْرَهُ", "قبر $deceasedName")
            .replace("مَدَّ بَصَرِهِ", "مد بصره")
    } else {
        dua.text
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = GoldChampagne,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = dua.title,
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
                        text = dua.reference,
                        color = EmeraldDeep,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = personalizedText,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "مشاركة الدعاء",
                        tint = GoldChampagne
                    )
                }
            }
        }
    }
}
