package com.islamic.noor.ui.hadith

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
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
import androidx.compose.runtime.mutableIntStateOf
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
    ),
    HadithItem(
        id = 6,
        title = "خيركم من تعلم القرآن وعلمه",
        matn = "«خَيْرُكُمْ مَنْ تَعَلَّمَ القُرْآنَ وَعَلَّمَهُ».",
        narrator = "عثمان بن عفان رضي الله عنه",
        source = "صحيح البخاري",
        category = "صحيح البخاري",
        explanation = "أشرف العلوم كلام الله تعالى، وأفضل الناس من بذل وقته في حفظه وفهمه وتعليمه للناس."
    ),
    HadithItem(
        id = 7,
        title = "من سلك طريقاً يلتمس فيه علماً",
        matn = "«مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا، سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الْجَنَّةِ».",
        narrator = "أبو هريرة رضي الله عنه",
        source = "صحيح مسلم",
        category = "رياض الصالحين",
        explanation = "الحث البالغ على طلب العلم الشرعي والنافع، وأنه أيسر الطرق الموصلة إلى رضوان الله وجنته."
    ),
    HadithItem(
        id = 8,
        title = "احفظ الله يحفظك",
        matn = "«يَا غُلاَمُ، إِنِّي أُعَلِّمُكَ كَلِمَاتٍ: احْفَظِ اللَّهَ يَحْفَظْكَ، احْفَظِ اللَّهَ تَجِدْهُ تُجَاهَكَ، إِذَا سَأَلْتَ فَاسْأَلِ اللَّهَ، وَإِذَا اسْتَعَنْتَ فَاسْتَعِنْ بِاللَّهِ، وَاعْلَمْ أَنَّ الأُمَّةَ لَوْ اجْتَمَعَتْ عَلَى أَنْ يَنْفَعُوكَ بِشَيْءٍ لَمْ يَنْفَعُوكَ إِلاَّ بِشَيْءٍ قَدْ كَتَبَهُ اللَّهُ لَكَ، وَلَوْ اجْتَمَعُوا عَلَى أَنْ يَضُرُّوكَ بِشَيْءٍ لَمْ يَضُرُّوكَ إِلاَّ بِشَيْءٍ قَدْ كَتَبَهُ اللَّهُ عَلَيْكَ، رُفِعَتِ الأَقْلاَمُ وَجَفَّتِ الصُّحُفُ».",
        narrator = "عبد الله بن عباس رضي الله عنهما",
        source = "جامع الترمذي وصححه الألباني",
        category = "الأربعون النووية",
        explanation = "وصية جامعة في التوحيد والتوكل على الله، ومراقبة أوامره ونواهيه في السر والعلانية."
    ),
    HadithItem(
        id = 9,
        title = "الصدقة تطفئ الخطيئة",
        matn = "«وَالصَّدَقَةُ تُطْفِئُ الْخَطِيئَةَ كَمَا يُطْفِئُ الْمَاءُ النَّارَ، وَصَلاَةُ الرَّجُلِ مِنْ جَوْفِ اللَّيْلِ».",
        narrator = "معاذ بن جبل رضي الله عنه",
        source = "سنن الترمذي وصححه الألباني",
        category = "رياض الصالحين",
        explanation = "عظيم أثر البذل والإنفاق في تكفير الذنوب ومحو السيئات، مع فضل قيام الليل."
    ),
    HadithItem(
        id = 10,
        title = "إماطة الأذى عن الطريق صدقة",
        matn = "«كُلُّ سُلاَمَى مِنَ النَّاسِ عَلَيْهِ صَدَقَةٌ، كُلَّ يَوْمٍ تَطْلُعُ فِيهِ الشَّمْسُ: تَعْدِلُ بَيْنَ الاثْنَيْنِ صَدَقَةٌ، وَتُعِينُ الرَّجُلَ فِي دَابَّتِهِ فَتَحْمِلُهُ عَلَيْهَا أَوْ تَرْفَعُ لَهُ عَلَيْهَا مَتَاعَهُ صَدَقَةٌ، وَالْكَلِمَةُ الطَّيِّبَةُ صَدَقَةٌ، وَكُلُّ خُطْوَةٍ تَمْشِيهَا إِلَى الصَّلاَةِ صَدَقَةٌ، وَتُمِيطُ الأَذَى عَنِ الطَّرِيقِ صَدَقَةٌ».",
        narrator = "أبو هريرة رضي الله عنه",
        source = "صحيح البخاري ومسلم",
        category = "صحيح البخاري",
        explanation = "أبواب الصدقة واسعة تشمل كل نفع للمسلمين كالإصلاح، والإعانة، وطيب الكلام، والمشي للمساجد، وتنظيف المرافق."
    ),
    HadithItem(
        id = 11,
        title = "من صلى علي صلاة صلى الله عليه بها عشراً",
        matn = "«مَنْ صَلَّى عَلَيَّ صَلاَةً وَاحِدَةً، صَلَّى اللَّهُ عَلَيْهِ عَشْرَ صَلَوَاتٍ، وَحُطَّتْ عَنْهُ عَشْرُ خَطِيئَاتٍ، وَرُفِعَتْ لَهُ عَشْرُ دَرَجَاتٍ».",
        narrator = "أنس بن مالك رضي الله عنه",
        source = "سنن النسائي وصححه الألباني",
        category = "رياض الصالحين",
        explanation = "فضل الإكثار من الصلاة والسلام على النبي صلى الله عليه وسلم وثوابها المضاعف."
    ),
    HadithItem(
        id = 12,
        title = "دع ما يريبك إلى ما لا يريبك",
        matn = "«دَعْ مَا يَرِيبُكَ إِلَى مَا لاَ يَرِيبُكَ؛ فَإِنَّ الصِّدْقَ طُمَأْنِينَةٌ، وَإِنَّ الْكَذِبَ رِيبَةٌ».",
        narrator = "الحسن بن علي رضي الله عنهما",
        source = "سنن الترمذي وقال حسن صحيح",
        category = "الأربعون النووية",
        explanation = "قاعدة جليلة في الورع والابتعاد عن الشبهات والشكوك لسلامة الدين والعرض."
    ),
    HadithItem(
        id = 13,
        title = "يسروا ولا تعسروا",
        matn = "«يَسِّرُوا وَلاَ تُعَسِّرُوا، وَبَشِّرُوا وَلاَ تُنَفِّرُوا».",
        narrator = "أنس بن مالك رضي الله عنه",
        source = "صحيح البخاري ومسلم",
        category = "صحيح البخاري",
        explanation = "منهج الإسلام قائم على التيسير ورفع الحرج والتبشير برحمة الله وفضله في الدعوة والتعامل."
    ),
    HadithItem(
        id = 14,
        title = "مثل المؤمنين في توادهم وتراحمهم",
        matn = "«مَثَلُ الْمُؤْمِنِينَ فِي تَوَادِّهِمْ، وَتَرَاحُمِهِمْ، وَتَعَاطُفِهِمْ مَثَلُ الْجَسَدِ، إِذَا اشْتَكَى مِنْهُ عُضْوٌ تَدَاعَى لَهُ سَائِرُ الْجَسَدِ بِالسَّهَرِ وَالْحُمَّى».",
        narrator = "النعمان بن بشير رضي الله عنه",
        source = "صحيح مسلم",
        category = "صحيح مسلم",
        explanation = "بيان عمق الأخوة الإيمانية والتضامن بين أفراد الأمة الإسلامية في السراء والضراء."
    ),
    HadithItem(
        id = 15,
        title = "المسلم من سلم المسلمون من لسانه ويده",
        matn = "«الْمُسْلِمُ مَنْ سَلِمَ الْمُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ، وَالْمُهَاجِرُ مَنْ هَجَرَ مَا نَهَى اللَّهُ عَنْهُ».",
        narrator = "عبد الله بن عمرو رضي الله عنهما",
        source = "صحيح البخاري ومسلم",
        category = "صحيح البخاري",
        explanation = "حقيقة الإسلام تتجلى في كف الأذى وحفظ اللسان واليد عن حقوق الناس وحرماتهم."
    )
)

data class HadithCategoryInfo(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

val HadithCategories = listOf(
    HadithCategoryInfo(
        title = "صحيح البخاري",
        subtitle = "أصح كتب الحديث النبوي الشريف وأعلاها منزلة",
        icon = Icons.AutoMirrored.Filled.MenuBook
    ),
    HadithCategoryInfo(
        title = "صحيح مسلم",
        subtitle = "الأحاديث المسندة الصحيحة المتصلة عن رسول الله ﷺ",
        icon = Icons.Default.AutoAwesome
    ),
    HadithCategoryInfo(
        title = "الأربعون النووية",
        subtitle = "جوامع الكلم وقواعد الدين العظيمة للإمام النووي",
        icon = Icons.Default.Mosque
    ),
    HadithCategoryInfo(
        title = "رياض الصالحين",
        subtitle = "زاد المسلم اليومي في الأخلاق والآداب والفضائل والتربية",
        icon = Icons.Default.Spa
    )
)

object HadithFavoritesManager {
    val favorites = mutableSetOf<Int>(1, 2)

    fun isFavorite(id: Int): Boolean = favorites.contains(id)

    fun toggleFavorite(id: Int) {
        if (favorites.contains(id)) favorites.remove(id)
        else favorites.add(id)
    }

    fun count(): Int = favorites.size
}

@Composable
fun HadithScreen(
    onBack: () -> Unit = {},
    onSubCategoryChange: (String?, (() -> Unit)?) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var activeCategory by remember { mutableStateOf<String?>(null) }
    var favUpdateTrigger by remember { mutableIntStateOf(0) }

    // Notify top bar and handle sub-navigation
    LaunchedEffect(activeCategory) {
        if (activeCategory != null) {
            onSubCategoryChange(activeCategory) { activeCategory = null }
        } else {
            onSubCategoryChange(null, null)
        }
    }

    // Intercept back button when inside a category to return to Hadith categories list
    BackHandler(enabled = activeCategory != null) {
        activeCategory = null
    }

    val displayedHadiths = remember(searchQuery, activeCategory, favUpdateTrigger) {
        SampleHadiths.filter { hadith ->
            val matchesCategory = when (activeCategory) {
                null -> true
                "الأحاديث المفضلة" -> HadithFavoritesManager.isFavorite(hadith.id)
                else -> hadith.category == activeCategory
            }
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
        // Back header if inside a category
        if (activeCategory != null) {
            item(key = "back_category_header") {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
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
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "الرجوع إلى تصنيفات الأحاديث • $activeCategory",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = titleColor()
                        )
                    }
                }
            }
        }

        // Search Box
        item(key = "search_box") {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(if (activeCategory == null) "ابحث في جميع الأحاديث أو الرواة..." else "ابحث في أحاديث $activeCategory...")
                },
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

        // 1. If at root and not searching: Show Categories as interactive Cards/Buttons
        if (activeCategory == null && searchQuery.isBlank()) {
            // Favorite Hadiths Card
            item(key = "fav_hadiths_card") {
                Card(
                    onClick = { activeCategory = "الأحاديث المفضلة" },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFE53935).copy(alpha = 0.3f)),
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
                            color = Color(0xFFE53935).copy(alpha = 0.15f),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color(0xFFE53935),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "الأحاديث المفضلة",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = titleColor()
                            )
                            Text(
                                text = "${HadithFavoritesManager.count()} حديث تم تمييزه بالمفضلة",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                            )
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = GoldChampagne)
                    }
                }
            }

            // Categories Section Title
            item(key = "categories_section_title") {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp, 16.dp)
                            .background(GoldChampagne, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "كتب وأبواب السنة النبوية المطهرة",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = titleColor()
                    )
                }
            }

            // Category Button Cards
            items(HadithCategories, key = { it.title }) { cat ->
                val count = SampleHadiths.count { it.category == cat.title }
                Card(
                    onClick = { activeCategory = cat.title },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
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
                                text = "$count حديث",
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
            // 2. Inside Category or Active Search: Show Hadiths Cards List
            if (displayedHadiths.isEmpty()) {
                item(key = "empty_hadiths") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد أحاديث مطابقة في هذا الباب",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                items(displayedHadiths, key = { it.id }) { hadith ->
                    val isFav = HadithFavoritesManager.isFavorite(hadith.id)
                    HadithCard(
                        hadith = hadith,
                        isFavorite = isFav,
                        onToggleFavorite = {
                            HadithFavoritesManager.toggleFavorite(hadith.id)
                            favUpdateTrigger++
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
