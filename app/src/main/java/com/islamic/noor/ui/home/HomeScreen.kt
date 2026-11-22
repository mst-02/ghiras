package com.islamic.noor.ui.home

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.data.repository.GhirasRepository
import com.islamic.noor.ui.navigation.Screen
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.EmeraldLight
import com.islamic.noor.ui.theme.EmeraldMedium
import com.islamic.noor.ui.theme.GoldBright
import com.islamic.noor.ui.theme.GoldChampagne
import com.islamic.noor.ui.theme.GoldDark
import com.islamic.noor.ui.theme.titleColor

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    ghirasRepository: GhirasRepository = remember { GhirasRepository.instance }
) {
    var isDuaRead by remember { mutableStateOf(false) }
    var isVerseFlipped by remember { mutableStateOf(false) }
    var showVerseStoryDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. دعاء اليوم في الأول تماماً مع علامة إتمام القراءة تحت على الشمال
        item(key = "daily_dua") {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, GoldChampagne.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                    .clickable { isDuaRead = !isDuaRead }
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                        imageVector = Icons.Default.WbSunny,
                                        contentDescription = null,
                                        tint = GoldBright,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "دعاء اليوم المبارك",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = titleColor()
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDuaRead) EmeraldLight else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (isDuaRead) "تمت القراءة" else "اضغط للقراءة",
                                color = if (isDuaRead) EmeraldDeep else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "«اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا طَيِّبًا، وَعَمَلاً مُتَقَبَّلاً، اللَّهُمَّ اجْعَلْ يَوْمَنَا هَذَا خَيْرًا وَبَرَكَةً وَفَرَجًا مِنْ كُلِّ ضِيقٍ».",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 17.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // علامة تم القراءة تحت على الشمال
                        if (isDuaRead) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "تمت قراءة الدعاء بحمد الله",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        } else {
                            Text(
                                text = "اضغط على الدعاء لتأكيد القراءة",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }

                        Text(
                            text = "سنن ابن ماجه",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        // 2. مواعيد الصلاة وزر القبلة
        item(key = "prayer_card") {
            PrayerTimelineCard(onQiblaClick = { onNavigate(Screen.Qibla.route) })
        }

        // 3. أبواب الخير والخدمات الرئيسية
        item(key = "services_title") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp, 16.dp)
                        .background(GoldChampagne, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "الأقسام والخدمات الإسلامية",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = titleColor()
                )
            }
        }

        // 4. الأقسام الثمانية (بأسماء واضحة ومختصرة حسب التوجيه)
        item(key = "services_grid") {
            ServicesGrid(onNavigate = onNavigate)
        }

        // 5. آية وتدبر (قابلة للقلب وعرض الشرح وقصة الآية)
        item(key = "verse_reflection") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isVerseFlipped = !isVerseFlipped }
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "آية وتدبر",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = titleColor()
                        )
                        Text(
                            text = "سورة البقرة: 152",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "«فَاذْكُرُونِي أَذْكُرْكُمْ وَاشْكُرُوا لِي وَلَا تَكْفُرُونِ»",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isVerseFlipped) "إخفاء التفسير ▲" else "اضغط لقراءة التفسير وبيان الآية ▼",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldChampagne,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    AnimatedVisibility(visible = isVerseFlipped) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Text(
                                text = "التفسير الميسر: اذكروني بطاعتي وشكري بالقلب واللسان والجوارح، أذكركم بمغفرتي وثوابي وتفريج كربكم، واشكروا لي على نعمي ولا تجحدوها.",
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = { showVerseStoryDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("قصة الآية وعِبرتها", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. من هدي النبوة (حديث شريف)
        item(key = "daily_hadith") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(Screen.Hadith.route) }
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "من هدي النبوة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = titleColor()
                        )
                        Text(
                            text = "عرض الأحاديث ←",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldChampagne
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "قال رسول الله ﷺ: «كَلِمَتَانِ خَفِيفَتَانِ عَلَى اللِّسَانِ، ثَقِيلَتَانِ فِي الْمِيزَانِ، حَبِيبَتَانِ إِلَى الرَّحْمَنِ: سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ».",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "صحيح البخاري ومسلم",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // 7. زر مسابقة الأسبوع في آخر الصفحة تحت خالص
        item(key = "contest_banner") {
            WeeklyContestBottomCard(onClick = { onNavigate(Screen.Contest.route) })
        }
    }

    // Story Dialog for the Verse
    if (showVerseStoryDialog) {
        AlertDialog(
            onDismissRequest = { showVerseStoryDialog = false },
            title = {
                Text(
                    text = "عِبرة الآية وفضل الذكر",
                    fontWeight = FontWeight.Bold,
                    color = titleColor()
                )
            },
            text = {
                Text(
                    text = "قال سعيد بن جبير: «فاذكروني بطاعتي أذكركم بمغفرتي ورحمتي». ومن كرم الله وفضله العظيم أنه لا يذكر عبدٌ ربه في ملأ إلا ذكره الله في ملأ خير منهم، وكان السلف إذا نزلت بهم الشدائد فزعوا إلى الذكر والصلاة عملاً بهذه الآية الكريمة، فنالوا السكينة وتيسير كل عسير.",
                    lineHeight = 24.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showVerseStoryDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
                ) {
                    Text("إغلاق", color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun ServicesGrid(onNavigate: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Row 1: المصحف الشريف & الأذكار
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LuxuryFeatureCard(
                title = "المصحف الشريف",
                subtitle = "قراءة عثمانية وبحث آيات",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                accentColor = EmeraldDeep,
                onClick = { onNavigate(Screen.Quran.route) },
                modifier = Modifier.weight(1f)
            )

            LuxuryFeatureCard(
                title = "الأذكار",
                subtitle = "أذكار الصباح والمساء واليوم",
                icon = Icons.Default.Book,
                accentColor = GoldDark,
                onClick = { onNavigate(Screen.Azkar.route) },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: السبحة & اتجاه القبلة
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LuxuryFeatureCard(
                title = "السبحة",
                subtitle = "عداد مرن واختيار الذكر",
                icon = Icons.Default.FormatListNumbered,
                accentColor = EmeraldDeep,
                onClick = { onNavigate(Screen.Tasbeeh.route) },
                modifier = Modifier.weight(1f)
            )

            LuxuryFeatureCard(
                title = "اتجاه القبلة",
                subtitle = "بوصلة دقيقة ومواقيت الصلاة",
                icon = Icons.Default.CompassCalibration,
                accentColor = GoldDark,
                onClick = { onNavigate(Screen.Qibla.route) },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: الختمة الجماعية & الأدعية
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LuxuryFeatureCard(
                title = "الختمة الجماعية",
                subtitle = "ختم القرآن مع الأهل والأحباب",
                icon = Icons.Default.Groups,
                accentColor = EmeraldDeep,
                onClick = { onNavigate(Screen.GroupKhatmah.route) },
                modifier = Modifier.weight(1f)
            )

            LuxuryFeatureCard(
                title = "الأدعية",
                subtitle = "للمتوفين والرزق والشفاء",
                icon = Icons.Default.Favorite,
                accentColor = GoldDark,
                onClick = { onNavigate(Screen.Duas.route) },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 4: غراس الخير & الأحاديث النبوية
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LuxuryFeatureCard(
                title = "غِراس الخير",
                subtitle = "حفر آبار ومساجد وأسهم صدقة",
                icon = Icons.Default.Spa,
                accentColor = EmeraldDeep,
                onClick = { onNavigate(Screen.GoodDeeds.route) },
                modifier = Modifier.weight(1f)
            )

            LuxuryFeatureCard(
                title = "الأحاديث النبوية",
                subtitle = "صحيح البخاري ومسلم وبحث",
                icon = Icons.Default.AutoAwesome,
                accentColor = GoldDark,
                onClick = { onNavigate(Screen.Hadith.route) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun LuxuryFeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = GoldChampagne.copy(alpha = 0.15f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = GoldBright,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = titleColor()
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun PrayerTimelineCard(onQiblaClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(EmeraldDeep, EmeraldMedium)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Mosque,
                                contentDescription = null,
                                tint = GoldChampagne,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "الصلاة القادمة: صلاة العصر",
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "متبقي 01:25:00 على الأذان",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onQiblaClick() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CompassCalibration,
                                contentDescription = "القبلة",
                                tint = GoldChampagne,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "القبلة",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MinimalPrayerPill("الفجر", "04:58")
                    MinimalPrayerPill("الشروق", "06:27")
                    MinimalPrayerPill("الظهر", "11:46")
                    MinimalPrayerPill("العصر", "02:44", isActive = true)
                    MinimalPrayerPill("المغرب", "05:04")
                    MinimalPrayerPill("العشاء", "06:24")
                }
            }
        }
    }
}

@Composable
private fun MinimalPrayerPill(name: String, time: String, isActive: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isActive) GoldChampagne else Color.White.copy(alpha = 0.12f))
            .padding(horizontal = 7.dp, vertical = 5.dp)
    ) {
        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) EmeraldDeep else Color.White
        )
        Text(
            text = time,
            fontSize = 10.sp,
            color = if (isActive) EmeraldDeep else Color.White.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun WeeklyContestBottomCard(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, GoldChampagne.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = GoldChampagne.copy(alpha = 0.2f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = "مسابقة الأسبوع",
                        tint = GoldDark,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "مسابقة الأسبوع (جوائز قيمة)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = titleColor()
                )
                Text(
                    text = "جاوب على سؤال اليوم مجاناً وادخل السحب الأسبوعي",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = GoldChampagne
            )
        }
    }
}
