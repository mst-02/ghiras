package com.islamic.noor.ui.night

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.ui.theme.GoldChampagne
import com.islamic.noor.ui.theme.GoldDark

private val NightBgColor = Color(0xFF0F1412)
private val NightCardColor = Color(0xFF18201D)
private val NightTextColor = Color(0xFFEDE8DD)
private val NightSubtextColor = Color(0xFFA5B2AC)

@Composable
fun NightModeScreen(
    onBack: () -> Unit = {}
) {
    var fontSize by remember { mutableFloatStateOf(22f) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NightBgColor),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item(key = "header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = GoldChampagne
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = null,
                            tint = GoldChampagne,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "وضع الليل والسكينة",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = NightTextColor
                        )
                    }
                    Text(
                        text = "إضاءة مريحة للعين وآيات الحفظ والسكينة قبل النوم",
                        style = MaterialTheme.typography.bodySmall,
                        color = NightSubtextColor
                    )
                }
            }
        }

        // Font Size Adjuster for Elderly Comfort
        item(key = "font_adjuster") {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NightCardColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = null,
                        tint = GoldChampagne,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "حجم الخط: ${fontSize.toInt()}sp",
                        fontSize = 13.sp,
                        color = NightTextColor,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Slider(
                        value = fontSize,
                        onValueChange = { fontSize = it },
                        valueRange = 18f..32f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = GoldChampagne,
                            activeTrackColor = GoldDark,
                            inactiveTrackColor = Color.DarkGray
                        )
                    )
                }
            }
        }

        // Ayat Al-Kursi (Primary Focal Point)
        item(key = "ayat_alkursi") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NightCardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldChampagne.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = GoldChampagne.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = GoldChampagne,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "آية الكرسي (الحافظة بإذن الله)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldChampagne
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldChampagne.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "سورة البقرة: 255",
                                color = GoldChampagne,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "﴿اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ﴾",
                        fontSize = fontSize.sp,
                        lineHeight = (fontSize * 1.7).sp,
                        color = NightTextColor,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "من قرأها حين يأوي إلى فراشه لم يزل عليه من الله حافظ حتى يصبح.",
                        fontSize = 12.sp,
                        color = NightSubtextColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Bedtime Dua 1
        item(key = "sleep_dua_1") {
            NightDuaCard(
                title = "دعاء النوم الجامع",
                arabicText = "«بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي وَبِكَ أَرْفَعُهُ، إِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ».",
                source = "صحيح البخاري ومسلم",
                fontSize = fontSize
            )
        }

        // Bedtime Dua 2
        item(key = "sleep_dua_2") {
            NightDuaCard(
                title = "تسليم النفس لله تعالى",
                arabicText = "«اللَّهُمَّ أَسْلَمْتُ نَفْسِي إِلَيْكَ، وَفَوَّضْتُ أَمْرِي إِلَيْكَ، وَوَجَّهْتُ وَجْهِي إِلَيْكَ، وَأَلْجَأْتُ ظَهْرِي إِلَيْكَ، رَغْبَةً وَرَهْبَةً إِلَيْكَ، لاَ مَلْجَأَ وَلاَ مَنْجَا مِنْكَ إِلاَّ إِلَيْكَ، آمَنْتُ بِكِتَابِكَ الَّذِي أَنْزَلْتَ، وَبِنَبِيِّكَ الَّذِي أَرْسَلْتَ».",
                source = "صحيح البخاري ومسلم",
                fontSize = fontSize
            )
        }
    }
}

@Composable
private fun NightDuaCard(
    title: String,
    arabicText: String,
    source: String,
    fontSize: Float
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NightCardColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GoldChampagne
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = arabicText,
                fontSize = fontSize.sp,
                lineHeight = (fontSize * 1.6).sp,
                color = NightTextColor,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = source,
                fontSize = 11.sp,
                color = NightSubtextColor
            )
        }
    }
}
