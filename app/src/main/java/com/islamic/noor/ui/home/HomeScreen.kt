package com.islamic.noor.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.data.repository.GhirasRepository
import com.islamic.noor.data.repository.QiblaAndPrayerRepository
import com.islamic.noor.data.repository.UserRepository
import com.islamic.noor.ui.navigation.Screen
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.EmeraldLight
import com.islamic.noor.ui.theme.EmeraldMedium
import com.islamic.noor.ui.theme.GoldBright
import com.islamic.noor.ui.theme.GoldChampagne
import com.islamic.noor.ui.theme.GoldDark
import com.islamic.noor.ui.theme.titleColor
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    ghirasRepository: GhirasRepository = remember { GhirasRepository.instance },
    userRepository: UserRepository = remember { UserRepository.instance }
) {
    val userProfile by userRepository.userProfile.collectAsState()
    var isDuaRead by remember { mutableStateOf(false) }
    var isVerseFlipped by remember { mutableStateOf(false) }
    var isHadithFlipped by remember { mutableStateOf(false) }
    var showWirdConfigDialog by remember { mutableStateOf(false) }
    var showVerseStoryDialog by remember { mutableStateOf(false) }

    val verseRotation by animateFloatAsState(
        targetValue = if (isVerseFlipped) 180f else 0f,
        animationSpec = spring(stiffness = 500f),
        label = "verse_flip"
    )

    val hadithRotation by animateFloatAsState(
        targetValue = if (isHadithFlipped) 180f else 0f,
        animationSpec = spring(stiffness = 500f),
        label = "hadith_flip"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. كارت "اذهب لوردك اليومي" في أعلى الصفحة الرئيسية
        item(key = "daily_wird_card") {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, GoldBright.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
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
                                color = EmeraldDeep,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Mosque,
                                        contentDescription = null,
                                        tint = GoldBright,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "وردك اليومي المبارك",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = titleColor()
                                )
                                Text(
                                    text = userProfile.dailyWirdTitle.ifBlank { "لم تحدد ورداً بعد — اضغط للتحديد" },
                                    fontSize = 11.sp,
                                    color = GoldDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // زر تعديل الورد
                        Surface(
                            shape = CircleShape,
                            color = GoldChampagne.copy(alpha = 0.2f),
                            modifier = Modifier
                                .size(34.dp)
                                .clickable { showWirdConfigDialog = true }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "تعديل الورد",
                                    tint = GoldBright,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // زر اذهب لوردك اليومي
                        Button(
                            onClick = {
                                if (userProfile.dailyWirdTitle.isBlank()) {
                                    showWirdConfigDialog = true
                                } else {
                                    val sId = userProfile.dailyWirdSurahId.coerceAtLeast(1)
                                    onNavigate("surah_detail/$sId")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("اذهب لوردك اليومي", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = GoldBright, modifier = Modifier.size(16.dp))
                            }
                        }

                        // زر تبديل تم الإنجاز
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (userProfile.isDailyWirdDone) EmeraldLight else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (userProfile.isDailyWirdDone) Color(0xFF2E7D32) else GoldChampagne.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { userRepository.toggleDailyWirdDone() }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (userProfile.isDailyWirdDone) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (userProfile.isDailyWirdDone) "تم الإنجاز ✨" else "إتمام",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (userProfile.isDailyWirdDone) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. مواعيد الصلاة مع عداد تنازلي حي بالثواني
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

        // 4. الأقسام الثمانية
        item(key = "services_grid") {
            ServicesGrid(onNavigate = onNavigate)
        }

        // 5. دعاء اليوم المبارك (نزل تحت فوق آية وتدبر بالضبط كما طلب المستخدم)
        item(key = "daily_dua") {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.2.dp, GoldChampagne.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                    .clickable { isDuaRead = !isDuaRead }
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "«اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا طَيِّبًا، وَعَمَلًا مُتَقَبَّلًا»",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 30.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // علامة القراءة فقط أسفل البطاقة بكل وضوح
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isDuaRead) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "تمت قراءة الدعاء بحمد الله",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        } else {
                            Text(
                                text = "اضغط على البطاقة لتأكيد القراءة",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                            )
                        }
                    }
                }
            }
        }

        // 5. آية وتدبر مع حركة تقليب ثلاثية الأبعاد 3D Flip
        item(key = "verse_reflection") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        rotationY = verseRotation
                        cameraDistance = 14 * density
                    }
                    .clickable { isVerseFlipped = !isVerseFlipped }
            ) {
                if (verseRotation <= 90f) {
                    // الوجه الأمامي: الآية الكريمة
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
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 19.sp,
                            lineHeight = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor(),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "اضغط على البطاقة لقلبها وعرض التفسير والتدبر ↻",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldDark,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    // الوجه الخلفي: التفسير والعِبرة (معكوس أفقياً ليكون النص طبيعياً)
                    Column(
                        modifier = Modifier
                            .padding(18.dp)
                            .graphicsLayer { rotationY = 180f }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "التفسير الميسر والتدبر",
                                fontWeight = FontWeight.Bold,
                                color = GoldChampagne,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "اضغط للعودة ↻",
                                fontSize = 11.sp,
                                color = GoldDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "فاذكروني -أيها المؤمنون- بطاعتي وعبادتي أذكركم بالثواب والمغفرة، واشكروا لي نعمي الجليلة عليكم ولا تجحدوها. ومن كرم الله وفضله أنه من ذكره في نفسه ذكره في نفسه، ومن ذكره في ملأ ذكره في ملأ خير منهم وأبرك.",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
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

        // 6. من هدي النبوة مع حركة تقليب ثلاثية الأبعاد 3D Flip
        item(key = "daily_hadith") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        rotationY = hadithRotation
                        cameraDistance = 14 * density
                    }
                    .clickable { isHadithFlipped = !isHadithFlipped }
            ) {
                if (hadithRotation <= 90f) {
                    // الوجه الأمامي: متن الحديث الشريف
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
                                text = "اضغط لعرض الشرح ↻",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldDark
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "قال رسول الله ﷺ: «كَلِمَتَانِ خَفِيفَتَانِ عَلَى اللِّسَانِ، ثَقِيلَتَانِ فِي الْمِيزَانِ، حَبِيبَتَانِ إِلَى الرَّحْمَنِ: سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ».",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 26.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "متفق عليه (صحيح البخاري ومسلم)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                } else {
                    // الوجه الخلفي: الشرح والقصة والعِبرة
                    Column(
                        modifier = Modifier
                            .padding(18.dp)
                            .graphicsLayer { rotationY = 180f }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "شرح الحديث وقصته",
                                fontWeight = FontWeight.Bold,
                                color = GoldChampagne,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "اضغط للعودة ↻",
                                fontSize = 11.sp,
                                color = GoldDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ختم به الإمام البخاري صحيحه، وفيه بيان سعة رحمة الله وفضله؛ حيث جعل كلمات يسيرة النطق تملأ الميزان حَسَنات يوم القيامة وتستوجب محبة الرحمن. وكان الصحابة يلهجون بها في مجالسهم وتجارتهم وصباحهم ومسائهم لثقلها العظيم عند الله.",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
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

    // نافذة تحديد وتخصيص الورد اليومي
    if (showWirdConfigDialog) {
        val wirdPresets = listOf(
            Triple("سورة البقرة (الصفحات ١-٤)", 2, "تلاوة وحفظ بدايات سورة البقرة"),
            Triple("سورة الكهف كاملة", 18, "نور ما بين الجمعتين وبركة الأسبوع"),
            Triple("سورة يس والواقعة", 36, "قلب القرآن وجلب الرزق والسكينة"),
            Triple("سورة الملك (المنجية من عذاب القبر)", 67, "قراءة يومية قبل النوم للحفظ والشفاعة"),
            Triple("جزء عمّ (الجزء ٣٠)", 78, "قصار السور والتدبر السريع"),
            Triple("أذكار الصباح والمساء والاستغفار", 1, "تحصين النفس بالأوراد النبوية المباركة")
        )

        AlertDialog(
            onDismissRequest = { showWirdConfigDialog = false },
            shape = RoundedCornerShape(22.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Mosque, contentDescription = null, tint = GoldBright, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تحديد وردك اليومي المبارك",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = titleColor()
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "اختر وردك اليومي ليظهر في واجهتك وتصل إليه بضغطة زر واحدة كل يوم:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )

                    wirdPresets.forEach { (title, surahId, desc) ->
                        val isSelected = userProfile.dailyWirdTitle == title
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) EmeraldLight else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) GoldBright else Color.Transparent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    userRepository.setDailyWird(title, surahId)
                                    showWirdConfigDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) EmeraldDeep else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = desc,
                                        fontSize = 10.sp,
                                        color = if (isSelected) EmeraldDeep.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                                    )
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldDeep, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showWirdConfigDialog = false }) {
                    Text("إغلاق", color = titleColor(), fontWeight = FontWeight.Bold)
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
                subtitle = "قراءة عثمانية وتفسير",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                accentColor = GoldBright,
                onClick = { onNavigate(Screen.Quran.route) },
                modifier = Modifier.weight(1f)
            )

            LuxuryFeatureCard(
                title = "الأذكار وحصن المسلم",
                subtitle = "أذكار يومية وإعداد مخصص",
                icon = Icons.Default.Spa,
                accentColor = EmeraldLight,
                onClick = { onNavigate(Screen.Azkar.route) },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: السبحة & القبلة
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LuxuryFeatureCard(
                title = "السبحة الإلكترونية",
                subtitle = "حجم مخصص وعداد مرن",
                icon = Icons.Default.FormatListNumbered,
                accentColor = GoldChampagne,
                onClick = { onNavigate(Screen.Tasbeeh.route) },
                modifier = Modifier.weight(1f)
            )

            LuxuryFeatureCard(
                title = "اتجاه القبلة",
                subtitle = "بوصلة مستشعر بدون نت",
                icon = Icons.Default.CompassCalibration,
                accentColor = GoldDark,
                onClick = { onNavigate(Screen.Qibla.route) },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: الختمة الجماعية & أدعية للمتوفين
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LuxuryFeatureCard(
                title = "الختمة الجماعية",
                subtitle = "ختم القرآن مع الأهل والأحباب",
                icon = Icons.Default.Groups,
                accentColor = EmeraldLight,
                onClick = { onNavigate(Screen.GroupKhatmah.route) },
                modifier = Modifier.weight(1f)
            )

            LuxuryFeatureCard(
                title = "أدعية للمتوفين",
                subtitle = "صدقة جارية وتخصيص الاسم",
                icon = Icons.Default.Favorite,
                accentColor = Color(0xFFC62828),
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
                subtitle = "أبواب الصدقات وفضلها",
                icon = Icons.Default.VolunteerActivism,
                accentColor = GoldBright,
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
    val context = LocalContext.current
    val userProfile by UserRepository.instance.userProfile.collectAsState()
    val prayerTimes by QiblaAndPrayerRepository.instance.getPrayerTimesForToday(userProfile.selectedGovernorate).collectAsState(initial = emptyList())

    var livePrayerInfo by remember {
        mutableStateOf(QiblaAndPrayerRepository.instance.getNextPrayerLiveInfo(userProfile.selectedGovernorate))
    }

    LaunchedEffect(userProfile.selectedGovernorate) {
        while (true) {
            livePrayerInfo = QiblaAndPrayerRepository.instance.getNextPrayerLiveInfo(userProfile.selectedGovernorate)
            delay(1000)
        }
    }

    // Trigger vibration when next prayer arrives
    LaunchedEffect(livePrayerInfo.prayerName, livePrayerInfo.remainingSeconds) {
        if (livePrayerInfo.remainingSeconds <= 1L && userProfile.notificationsEnabled) {
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    val vm = context.getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager
                    vm?.defaultVibrator?.vibrate(android.os.VibrationEffect.createWaveform(longArrayOf(0, 500, 300, 500), -1))
                } else {
                    @Suppress("DEPRECATION")
                    val v = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? android.os.Vibrator
                    v?.vibrate(longArrayOf(0, 500, 300, 500), -1)
                }
            } catch (_: Exception) {}
        }
    }

    val hours = livePrayerInfo.remainingSeconds / 3600
    val minutes = (livePrayerInfo.remainingSeconds % 3600) / 60
    val seconds = livePrayerInfo.remainingSeconds % 60
    val countdownFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)

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
                                text = "الموعد القادم: ${livePrayerInfo.prayerName}",
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        // Real-time ticking countdown
                        Text(
                            text = "متبقي $countdownFormatted (${livePrayerInfo.timeFormatted})",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.95f),
                            fontWeight = FontWeight.Medium
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

                // Prayer Timeline (Real-time computed astronomical times including Sunrise)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (prayerTimes.isNotEmpty()) {
                        prayerTimes.forEach { prayer ->
                            PrayerPill(
                                name = prayer.nameArabic,
                                time = prayer.timeFormatted.replace(" ص", "").replace(" م", ""),
                                isNext = prayer.isNext
                            )
                        }
                    } else {
                        PrayerPill("الفجر", "04:58", false)
                        PrayerPill("الشروق", "06:25", false)
                        PrayerPill("الظهر", "11:46", false)
                        PrayerPill("العصر", "02:44", true)
                        PrayerPill("المغرب", "05:04", false)
                        PrayerPill("العشاء", "06:24", false)
                    }
                }
            }
        }
    }
}

@Composable
private fun PrayerPill(name: String, time: String, isNext: Boolean) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isNext) GoldChampagne else Color.White.copy(alpha = 0.14f),
        border = if (isNext) androidx.compose.foundation.BorderStroke(1.2.dp, GoldBright) else null,
        modifier = Modifier.padding(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                color = if (isNext) EmeraldDeep else Color.White,
                fontSize = 10.sp,
                fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = time,
                color = if (isNext) EmeraldDeep else Color.White.copy(alpha = 0.9f),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun WeeklyContestBottomCard(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldChampagne.copy(alpha = 0.7f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = GoldDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "مسابقة غِراس الأسبوعية",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = titleColor()
                    )
                    Text(
                        text = "جاوب على سؤال اليوم مجاناً وادخل السحب",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = GoldChampagne.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "شارك الآن ←",
                    color = GoldDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
