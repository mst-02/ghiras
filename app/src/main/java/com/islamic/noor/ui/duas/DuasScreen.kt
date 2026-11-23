package com.islamic.noor.ui.duas

import android.content.Intent
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.material3.TextButton
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

data class DuaItem(
    val id: Int,
    val title: String,
    val text: String,
    val reference: String,
    val category: String,
    val isCustom: Boolean = false
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
    DuaItem(
        id = 3,
        title = "دعاء الثبات ورفع الدرجات",
        text = "اللَّهُمَّ أَبْدِلْهُ دَاراً خَيْراً مِنْ دَارِهِ، وَأَهْلاً خَيْراً مِنْ أَهْلِهِ، وَأَدْخِلْهُ الْجَنَّةَ، وَأَعِذْهُ مِنْ عَذَابِ الْقَبْرِ، وَمِنْ عَذَابِ النَّارِ، اللَّهُمَّ اجْعَلْ عَنْ يَمِينِهِ نُوراً، حَتَّى تَبْعَثَهُ آمِناً مُطْمَئِنّاً.",
        reference = "مأثور",
        category = "أدعية للمتوفين"
    ),
    DuaItem(
        id = 4,
        title = "دعاء الشفاعة وثقل الميزان",
        text = "اللَّهُمَّ شَفِّعْ فِيهِ نَبِيَّنَا مُحَمَّداً ﷺ، وَاحْشُرْهُ تَحْتَ لِوَائِهِ، وَاسْقِهِ مِنْ يَدِهِ الشَّرِيفَةِ شَرْبَةً هَنِيئَةً لاَ يَظْمَأُ بَعْدَهَا أَبَداً، وَاجْعَلْهُ فِي أَعْلَى عِلِّيِّينَ.",
        reference = "مأثور",
        category = "أدعية للمتوفين"
    ),

    // قسم الرزق والفرج
    DuaItem(
        id = 5,
        title = "دعاء قضاء الدين وسعة الرزق",
        text = "«اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَأَعُوذُ بِكَ مِنَ الْعَجْزِ وَالْكَسَلِ، وَأَعُوذُ بِكَ مِنَ الْجُبْنِ وَالْبُخْلِ، وَأَعُوذُ بِكَ مِنْ غَلَبَةِ الدَّيْنِ وَقَهْرِ الرِّجَالِ».",
        reference = "صحيح البخاري",
        category = "الرزق وتفريج الهم"
    ),
    DuaItem(
        id = 6,
        title = "دعاء الكفاية بالحلال",
        text = "«اللَّهُمَّ اكْفِنِي بِحَلاَلِكَ عَنْ حَرَامِكَ، وَأَغْنِنِي بِفَضْلِكَ عَمَّنْ سِوَاكَ».",
        reference = "جامع الترمذي",
        category = "الرزق وتفريج الهم"
    ),
    DuaItem(
        id = 7,
        title = "دعاء ذي النون لتفريج الكرب",
        text = "«لاَ إِلَـهَ إِلاَّ أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ»؛ ما دعا بها مكروب إلا فرج الله عنه كربته.",
        reference = "رواه الترمذي والنسائي",
        category = "الرزق وتفريج الهم"
    ),
    DuaItem(
        id = 8,
        title = "دعاء الفرج واليسر",
        text = "«اللَّهُمَّ لاَ سَهْلَ إِلاَّ مَا جَعَلْتَهُ سَهْلاً، وَأَنْتَ تَجْعَلُ الْحَزْنَ إِذَا شِئْتَ سَهْلاً».",
        reference = "رواه ابن حبان",
        category = "الرزق وتفريج الهم"
    ),

    // قسم الشفاء والعافية
    DuaItem(
        id = 9,
        title = "دعاء عيادة المريض بالشفاء",
        text = "«اللَّهُمَّ رَبَّ النَّاسِ، أَذْهِبِ الْبَاسَ، اشْفِهِ وَأَنْتَ الشَّافِي، لاَ شِفَاءَ إِلاَّ شِفَاؤُكَ، شِفَاءً لاَ يُغَادِرُ سَقَمًا».",
        reference = "متفق عليه",
        category = "الشفاء والعافية"
    ),
    DuaItem(
        id = 10,
        title = "الرقية المأثورة بالبسملة",
        text = "«بِسْمِ اللَّهِ (ثلاثاً)، أَعُوذُ بِاللَّهِ وَقُدْرَتِهِ مِنْ شَرِّ مَا أَجِدُ وَأُحَاذِرُ (سبع مرات)».",
        reference = "صحيح مسلم",
        category = "الشفاء والعافية"
    ),
    DuaItem(
        id = 11,
        title = "دعاء العفو والعافية الشاملة",
        text = "«اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي دِينِي وَدُنْيَايَ وَأَهْلِي وَمَالِي، اللَّهُمَّ اسْتُرْ عَوْرَاتِي وَآمِنْ رَوْعَاتِي».",
        reference = "سنن أبي داود",
        category = "الشفاء والعافية"
    ),

    // جوامع الدعاء وبر الوالدين
    DuaItem(
        id = 12,
        title = "دعاء بر الوالدين والرحمة لهما",
        text = "﴿رَبِّ اغْفِرْ لِي وَلِوَالِدَيَّ وَلِمَن دَخَلَ بَيْتِيَ مُؤْمِناً وَلِلْمُؤْمِنِينَ وَالْمُؤْمِنَاتِ﴾، ﴿رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيراً﴾.",
        reference = "سورة نوح والإسراء",
        category = "جوامع الدعاء"
    ),
    DuaItem(
        id = 13,
        title = "دعاء حسنة الدنيا والآخرة",
        text = "«رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ».",
        reference = "سورة البقرة: 201",
        category = "جوامع الدعاء"
    ),
    DuaItem(
        id = 14,
        title = "سيد الاستغفار",
        text = "«اللَّهُمَّ أَنْتَ رَبِّي لا إِلَهَ إِلا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لا يَغْفِرُ الذُّنُوبَ إِلا أَنْتَ».",
        reference = "صحيح البخاري",
        category = "جوامع الدعاء"
    ),
    DuaItem(
        id = 15,
        title = "دعاء الثبات على الدين",
        text = "«يَا مُقَلِّبَ الْقُلُوبِ ثَبِّتْ قَلْبِي عَلَى دِينِكَ، اللَّهُمَّ مُصَرِّفَ الْقُلُوبِ صَرِّفْ قُلُوبَنَا عَلَى طَاعَتِكَ».",
        reference = "رواه الترمذي ومسلم",
        category = "جوامع الدعاء"
    )
)

@Composable
fun DuasScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val categories = listOf("أدعية للمتوفين", "الرزق وتفريج الهم", "الشفاء والعافية", "جوامع الدعاء", "أدعيتي الخاصة")
    var selectedCategory by remember { mutableStateOf("أدعية للمتوفين") }
    var deceasedName by remember { mutableStateOf("") }
    val customDuasList = remember { mutableStateListOf<DuaItem>() }
    val favoriteDuaIds = remember { mutableStateListOf<Int>() }

    var showAddCustomDuaDialog by remember { mutableStateOf(false) }
    var newDuaTitle by remember { mutableStateOf("") }
    var newDuaText by remember { mutableStateOf("") }

    val filteredDuas = remember(selectedCategory, customDuasList.toList()) {
        if (selectedCategory == "أدعيتي الخاصة") {
            customDuasList.toList()
        } else {
            ComprehensiveDuasList.filter { it.category == selectedCategory }
        }
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
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Add custom dua button when in custom category
        if (selectedCategory == "أدعيتي الخاصة") {
            item(key = "add_custom_dua_btn") {
                Button(
                    onClick = { showAddCustomDuaDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إضافة دعاء خاص بك", color = Color.White, fontWeight = FontWeight.Bold)
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
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
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
        if (filteredDuas.isEmpty() && selectedCategory == "أدعيتي الخاصة") {
            item(key = "empty_custom") {
                Text(
                    text = "لم تقم بإضافة أدعية خاصة بعد. اضغط على الزر بالأعلى لإضافة دعائك.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                )
            }
        } else {
            items(filteredDuas, key = { it.id }) { dua ->
                val isFav = favoriteDuaIds.contains(dua.id)
                DuaDisplayCard(
                    dua = dua,
                    deceasedName = deceasedName,
                    isFavorite = isFav,
                    onToggleFavorite = {
                        if (isFav) favoriteDuaIds.remove(dua.id)
                        else favoriteDuaIds.add(dua.id)
                    },
                    onShare = { textToShare ->
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "${dua.title}\n\n$textToShare\n\nتطبيق غِراس - غرسٌ طيب وأجرٌ دائم")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "مشاركة الدعاء المبارك"))
                    }
                )
            }
        }
    }

    // Add Custom Dua Dialog
    if (showAddCustomDuaDialog) {
        AlertDialog(
            onDismissRequest = { showAddCustomDuaDialog = false },
            title = { Text("إضافة دعاء خاص", fontWeight = FontWeight.Bold, color = titleColor()) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newDuaTitle,
                        onValueChange = { newDuaTitle = it },
                        label = { Text("عنوان الدعاء (مثال: دعاء التوفيق، دعاء للأولاد)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newDuaText,
                        onValueChange = { newDuaText = it },
                        label = { Text("نص الدعاء المبارك") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDuaTitle.isNotBlank() && newDuaText.isNotBlank()) {
                            val newItem = DuaItem(
                                id = (System.currentTimeMillis() % 100000).toInt(),
                                title = newDuaTitle.trim(),
                                text = newDuaText.trim(),
                                reference = "دعاء شخصي محفوظ محلياً",
                                category = "أدعيتي الخاصة",
                                isCustom = true
                            )
                            customDuasList.add(newItem)
                            newDuaTitle = ""
                            newDuaText = ""
                            showAddCustomDuaDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
                ) {
                    Text("إضافة الدعاء", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCustomDuaDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun DuaDisplayCard(
    dua: DuaItem,
    deceasedName: String,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onShare: (String) -> Unit
) {
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
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
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
                fontSize = 17.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = GoldChampagne.copy(alpha = 0.18f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onShare(personalizedText) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة",
                            tint = GoldDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "مشاركة الدعاء",
                            color = GoldDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
