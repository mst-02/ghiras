package com.islamic.noor.ui.duas

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
        title = "دعاء جامع بالرحمة والمغفرة للمتوفى",
        text = "«اللَّهُمَّ اغْفِرْ لَهُ وَارْحَمْهُ، وَعَافِهِ وَاعْفُ عَنْهُ، وَأَكْرِمْ نُزُلَهُ، وَوَسِّعْ مُدْخَلَهُ، وَاغْسِلْهُ بِالْمَاءِ وَالثَّلْجِ وَالْبَرَدِ، وَنَقِّهِ مِنَ الْخَطَايَا كَمَا نَقَّيْتَ الثَّوْبَ الأَبْيَضَ مِنَ الدَّنَسِ».",
        reference = "صحيح مسلم",
        category = "أدعية للمتوفين"
    ),
    DuaItem(
        id = 2,
        title = "دعاء لتنوير القبر والفسحة فيه",
        text = "«اللَّهُمَّ اجْعَلْ قَبْرَهُ رَوْضَةً مِنْ رِيَاضِ الْجَنَّةِ، وَلاَ تَجْعَلْهُ حُفْرَةً مِنْ حُفَرِ النَّارِ، اللَّهُمَّ افْسَحْ لَهُ فِي قَبْرِهِ مَدَّ بَصَرِهِ، وَافْرُشْ قَبْرَهُ مِنْ فِرَاشِ الْجَنَّةِ».",
        reference = "مأثور عن السلف",
        category = "أدعية للمتوفين"
    ),
    DuaItem(
        id = 3,
        title = "دعاء الثبات ورفع الدرجات",
        text = "«اللَّهُمَّ أَبْدِلْهُ دَاراً خَيْراً مِنْ دَارِهِ، وَأَهْلاً خَيْراً مِنْ أَهْلِهِ، وَأَدْخِلْهُ الْجَنَّةَ، وَأَعِذْهُ مِنْ عَذَابِ الْقَبْرِ، وَمِنْ عَذَابِ النَّارِ، اللَّهُمَّ اجْعَلْ عَنْ يَمِينِهِ نُوراً، حَتَّى تَبْعَثَهُ آمِناً مُطْمَئِنّاً».",
        reference = "مأثور",
        category = "أدعية للمتوفين"
    ),
    DuaItem(
        id = 4,
        title = "دعاء الشفاعة وثقل الميزان",
        text = "«اللَّهُمَّ شَفِّعْ فِيهِ نَبِيَّنَا مُحَمَّداً ﷺ، وَاحْشُرْهُ تَحْتَ لِوَائِهِ، وَاسْقِهِ مِنْ يَدِهِ الشَّرِيفَةِ شَرْبَةً هَنِيئَةً لاَ يَظْمَأُ بَعْدَهَا أَبَداً، وَاجْعَلْهُ فِي أَعْلَى عِلِّيِّينَ».",
        reference = "مأثور",
        category = "أدعية للمتوفين"
    ),

    // قسم الوالدين
    DuaItem(
        id = 5,
        title = "دعاء القرآن الكريم للوالدين",
        text = "«رَبَّنَا اغْفِرْ لِي وَلِوَالِدَيَّ وَلِلْمُؤْمِنِينَ يَوْمَ يَقُومُ الْحِسَابُ ، رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا».",
        reference = "سورة إبراهيم والإسراء",
        category = "أدعية للوالدين"
    ),
    DuaItem(
        id = 6,
        title = "دعاء العافية والبر بالوالدين",
        text = "«اللَّهُمَّ اجْعَلْ أُمِّي وَأَبِي مِنْ سُعَدَاءِ الدَّارَيْنِ، اللَّهُمَّ أَلْبِسْهُمَا ثَوْبَ الصِّحَّةِ وَالْعَافِيَةِ، وَارْزُقْنِي بِرَّهُمَا وَرِضَاهُمَا فِي حَيَاتِهِمَا وَبَعْدَ مَمَاتِهِمَا».",
        reference = "مأثور",
        category = "أدعية للوالدين"
    ),
    DuaItem(
        id = 7,
        title = "دعاء مغفرة ذنوب الوالدين",
        text = "«اللَّهُمَّ اغْفِرْ لِوَالِدَيَّ وَارْحَمْهُمَا وَعَافِهِمَا، وَاجْزِهِمَا عَنِّي خَيْرَ الْجَزَاءِ عَلَى كُلِّ لَحْظَةٍ سَهِرَا فِيهَا مِنْ أَجْلِي».",
        reference = "مأثور",
        category = "أدعية للوالدين"
    ),

    // قسم الرزق والفرج
    DuaItem(
        id = 8,
        title = "دعاء قضاء الدين وتفريج الهم",
        text = "«اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَأَعُوذُ بِكَ مِنَ الْعَجْزِ وَالْكَسَلِ، وَأَعُوذُ بِكَ مِنَ الْجُبْنِ وَالْبُخْلِ، وَأَعُوذُ بِكَ مِنْ غَلَبَةِ الدَّيْنِ وَقَهْرِ الرِّجَالِ».",
        reference = "صحيح البخاري",
        category = "الرزق وتفريج الهم"
    ),
    DuaItem(
        id = 9,
        title = "دعاء الكفاية بالحلال وسعة الرزق",
        text = "«اللَّهُمَّ اكْفِنِي بِحَلاَلِكَ عَنْ حَرَامِكَ، وَأَغْنِنِي بِفَضْلِكَ عَمَّنْ سِوَاكَ».",
        reference = "جامع الترمذي",
        category = "الرزق وتفريج الهم"
    ),
    DuaItem(
        id = 10,
        title = "دعاء ذي النون لتفريج الكروب",
        text = "«لاَ إِلَـهَ إِلاَّ أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ»؛ ما دعا بها مكروب إلا فرج الله عنه كربته.",
        reference = "رواه الترمذي والنسائي",
        category = "الرزق وتفريج الهم"
    ),

    // قسم الشفاء
    DuaItem(
        id = 11,
        title = "دعاء النبي ﷺ لعيادة المريض",
        text = "«اللَّهُمَّ رَبَّ النَّاسِ، أَذْهِبِ الْبَاسَ، اشْفِ أَنْتَ الشَّافِي، لاَ شِفَاءَ إِلاَّ شِفَاؤُكَ، شِفَاءً لاَ يُغَادِرُ سَقَماً».",
        reference = "متفق عليه",
        category = "الشفاء والعافية"
    ),
    DuaItem(
        id = 12,
        title = "دعاء سؤال العفو والعافية",
        text = "«اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالآخِرَةِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي دِينِي وَدُنْيَايَ وَأَهْلِي وَمَالِي».",
        reference = "سنن أبي داود وابن ماجه",
        category = "الشفاء والعافية"
    ),

    // جوامع الدعاء
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

data class DuaCategoryItem(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

val MainDuaCategories = listOf(
    DuaCategoryItem("أدعية للمتوفين", "أدعية بالرحمة والمغفرة والفسحة في القبور", Icons.Default.Favorite),
    DuaCategoryItem("أدعية للوالدين", "بر الوالدين والدعاء لهما بالرحمة والعافية", Icons.Default.VolunteerActivism),
    DuaCategoryItem("الرزق وتفريج الهم", "تفريج الكروب وسعة الرزق وقضاء الديون والبركة", Icons.Default.Spa),
    DuaCategoryItem("الشفاء والعافية", "الرقية الشرعية وطلب الشفاء من كل داء وسقم", Icons.Default.Healing),
    DuaCategoryItem("جوامع الدعاء", "جوامع الدعاء النبوي لخيري الدنيا والآخرة", Icons.Default.AutoAwesome)
)

@Composable
fun DuasScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var activeCategory by remember { mutableStateOf<String?>(null) }
    val favoriteDuaIds = remember { mutableStateListOf<Int>() }

    // Intercept back button when inside a category to return to categories list
    BackHandler(enabled = activeCategory != null) {
        activeCategory = null
    }

    val filteredDuas = remember(activeCategory) {
        when (activeCategory) {
            null -> emptyList()
            else -> ComprehensiveDuasList.filter { it.category == activeCategory }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Root Screen: Show Categories as interactive Cards/Buttons
        if (activeCategory == null) {
            item(key = "duas_section_title") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp, 16.dp)
                            .background(GoldChampagne, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "أبواب الأدعية النبوية والمأثورة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = titleColor()
                    )
                }
            }

            items(MainDuaCategories, key = { it.title }) { cat ->
                val count = ComprehensiveDuasList.count { it.category == cat.title }
                Card(
                    onClick = { activeCategory = cat.title },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GoldChampagne.copy(alpha = 0.15f),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = null,
                                    tint = GoldBright,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cat.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = titleColor()
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = cat.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldLight
                        ) {
                            Text(
                                text = "$count دعاء",
                                color = EmeraldDeep,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = GoldChampagne
                        )
                    }
                }
            }
        } else {
            // 2. Inside Category View: Back Header
            item(key = "back_category_header") {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { activeCategory = null }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = GoldChampagne,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "الرجوع لكافة أبواب الأدعية",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldDark
                        )
                    }
                }
            }

            item(key = "active_category_banner") {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldDeep,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = GoldBright,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = activeCategory ?: "",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            items(filteredDuas, key = { it.id }) { dua ->
                val isFav = favoriteDuaIds.contains(dua.id)
                DuaCard(
                    dua = dua,
                    isFavorite = isFav,
                    onToggleFavorite = {
                        if (isFav) favoriteDuaIds.remove(dua.id)
                        else favoriteDuaIds.add(dua.id)
                    },
                    onShare = {
                        val shareText = "${dua.title}\n\n${dua.text}\n\nالمصدر: ${dua.reference}\n\nتطبيق غِراس - غرسٌ طيب وأجرٌ دائم"
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "مشاركة الدعاء المبارك"))
                    }
                )
            }
        }
    }
}

@Composable
private fun DuaCard(
    dua: DuaItem,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Favorite + Title
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
                        text = dua.category,
                        color = EmeraldDeep,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dua Text
            Text(
                text = dua.text,
                fontSize = 16.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Justify,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Footer: Reference + Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "المصدر: ${dua.reference}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldDark,
                    fontWeight = FontWeight.Medium
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onShare() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة",
                            tint = titleColor(),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "مشاركة",
                            color = titleColor(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
