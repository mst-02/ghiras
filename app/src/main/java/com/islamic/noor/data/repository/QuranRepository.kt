package com.islamic.noor.data.repository

import android.content.Context
import com.islamic.noor.domain.model.Surah
import com.islamic.noor.domain.model.Verse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.InputStream

data class VerseSearchResult(
    val surahId: Int,
    val surahName: String,
    val verseNumber: Int,
    val textUthmani: String,
    val tafseerShort: String
)

data class PageVerseItem(
    val surahId: Int,
    val surahName: String,
    val surahArabic: String,
    val verseNumber: Int,
    val textUthmani: String,
    val isFirstVerseOfSurah: Boolean,
    val pageNumber: Int
)

sealed class QuranVoiceMatch {
    data class SurahMatch(val surah: Surah) : QuranVoiceMatch()
    data class AyahMatch(val verse: VerseSearchResult, val spokenText: String) : QuranVoiceMatch()
    object NoMatch : QuranVoiceMatch()
}

class QuranRepository(private val context: Context? = null) {

    fun getAllSurahs(): Flow<List<Surah>> = flow {
        emit(SURAH_LIST)
    }.flowOn(Dispatchers.IO)

    fun getVersesForSurah(surahId: Int): Flow<List<Verse>> = flow {
        val targetContext = context ?: appContext
        val verses = if (targetContext != null) {
            loadVersesFromAssets(targetContext, surahId)
        } else {
            getSampleVersesForSurah(surahId)
        }
        emit(verses)
    }.flowOn(Dispatchers.IO)

    fun getVersesForPage(pageNumber: Int): List<PageVerseItem> {
        val targetContext = context ?: appContext ?: return emptyList()
        return loadVersesForPageFromAssets(targetContext, pageNumber)
    }

    // بحث دقيق وشامل في نصوص كافة آيات القرآن الكريم مع تطبيع الرسم العثماني
    fun searchVerses(query: String): Flow<List<VerseSearchResult>> = flow {
        if (query.isBlank()) {
            emit(emptyList())
            return@flow
        }
        val cleanQuery = query.trim()
        val normQuery = normalizeUthmani(cleanQuery)
        val skeletonQuery = normQuery.replace("ا", "")
        val results = mutableListOf<VerseSearchResult>()
        val targetContext = context ?: appContext

        if (targetContext != null) {
            val allSurahs = SURAH_LIST
            for (surah in allSurahs) {
                val verses = loadVersesFromAssets(targetContext, surah.id)
                for (verse in verses) {
                    val normVerse = normalizeUthmani(verse.textUthmani)
                    if (normVerse.contains(normQuery) ||
                        (skeletonQuery.length >= 3 && normVerse.replace("ا", "").contains(skeletonQuery))
                    ) {
                        results.add(
                            VerseSearchResult(
                                surahId = surah.id,
                                surahName = surah.nameArabic,
                                verseNumber = verse.verseNumber,
                                textUthmani = verse.textUthmani,
                                tafseerShort = verse.tafseerShort
                            )
                        )
                        if (results.size >= 50) break
                    }
                }
                if (results.size >= 50) break
            }
        }
        emit(results)
    }.flowOn(Dispatchers.IO)

    fun matchVoiceQueryToQuran(spoken: String, context: Context): QuranVoiceMatch {
        val text = spoken.trim()
        if (text.isBlank()) return QuranVoiceMatch.NoMatch

        // 1. Check Surah names
        var cleanSurahName = text
        if (cleanSurahName.startsWith("سورة ")) {
            cleanSurahName = cleanSurahName.removePrefix("سورة ").trim()
        } else if (cleanSurahName.startsWith("سوره ")) {
            cleanSurahName = cleanSurahName.removePrefix("سوره ").trim()
        }
        val normalizedSpoken = normalizeUthmani(cleanSurahName)

        for (s in SURAH_LIST) {
            val sNorm = normalizeUthmani(s.nameArabic)
            if (sNorm == normalizedSpoken ||
                sNorm == normalizedSpoken.removePrefix("ال") ||
                normalizedSpoken == sNorm.removePrefix("ال") ||
                s.nameArabic.equals(cleanSurahName, ignoreCase = true)
            ) {
                return QuranVoiceMatch.SurahMatch(s)
            }
        }

        // 2. Check Quran Verses with Deep Uthmani Normalization
        val cleanSearch = normalizeUthmani(text)
        val skeletonSearch = cleanSearch.replace("ا", "")
        if (cleanSearch.length >= 3) {
            val targetContext = context.applicationContext
            for (surah in SURAH_LIST) {
                val verses = loadVersesFromAssets(targetContext, surah.id)
                for (verse in verses) {
                    val normVerse = normalizeUthmani(verse.textUthmani)
                    if (normVerse.contains(cleanSearch) ||
                        (skeletonSearch.length >= 4 && normVerse.replace("ا", "").contains(skeletonSearch))
                    ) {
                        return QuranVoiceMatch.AyahMatch(
                            VerseSearchResult(
                                surahId = surah.id,
                                surahName = surah.nameArabic,
                                verseNumber = verse.verseNumber,
                                textUthmani = verse.textUthmani,
                                tafseerShort = verse.tafseerShort
                            ),
                            spokenText = text
                        )
                    }
                }
            }
        }

        return QuranVoiceMatch.NoMatch
    }

    companion object {
        private var appContext: Context? = null
        private val surahCache = mutableMapOf<Int, List<Verse>>()
        private val pageCache = mutableMapOf<Int, List<PageVerseItem>>()
        private var rawJsonString: String? = null

        fun initialize(context: Context) {
            appContext = context.applicationContext
        }

        fun loadVersesForPageFromAssets(ctx: Context, pageNumber: Int): List<PageVerseItem> {
            synchronized(pageCache) {
                if (pageCache.containsKey(pageNumber)) {
                    return pageCache[pageNumber] ?: emptyList()
                }
            }

            try {
                if (rawJsonString == null) {
                    val stream: InputStream = ctx.assets.open("quran_uthmani.json")
                    rawJsonString = stream.bufferedReader().use { it.readText() }
                }

                val json = JSONObject(rawJsonString!!)
                val dataObj = json.getJSONObject("data")
                val surahsArray = dataObj.getJSONArray("surahs")
                val basmalahPattern = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ"
                val tempMap = mutableMapOf<Int, MutableList<PageVerseItem>>()

                for (s in 0 until surahsArray.length()) {
                    val surahObj = surahsArray.getJSONObject(s)
                    val sNum = surahObj.getInt("number")
                    val sName = surahObj.getString("name")
                    val ayahsArray = surahObj.getJSONArray("ayahs")

                    for (a in 0 until ayahsArray.length()) {
                        val ayahObj = ayahsArray.getJSONObject(a)
                        val numInSurah = ayahObj.getInt("numberInSurah")
                        var text = ayahObj.getString("text").replace("\uFEFF", "").trim()
                        val page = ayahObj.optInt("page", 1)

                        if (sNum != 1 && sNum != 9 && numInSurah == 1) {
                            if (text.startsWith(basmalahPattern)) {
                                text = text.removePrefix(basmalahPattern).trim()
                            }
                        }

                        val list = tempMap.getOrPut(page) { mutableListOf() }
                        list.add(
                            PageVerseItem(
                                surahId = sNum,
                                surahName = sName,
                                surahArabic = sName,
                                verseNumber = numInSurah,
                                textUthmani = text,
                                isFirstVerseOfSurah = numInSurah == 1,
                                pageNumber = page
                            )
                        )
                    }
                }

                synchronized(pageCache) {
                    pageCache.putAll(tempMap)
                    return pageCache[pageNumber] ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return emptyList()
        }

        /**
         * تطبيع ذكي وعميق للرسم العثماني
         * يحول الألف الخنجرية (\u0670) لألف حقيقية (مثل السمٰوٰت -> السماوات)
         * ويطابق الصلاة (الصلوٰة) والزكاة والرحمن وإبراهيم وإسماعيل وجميع الحروف
         */
        fun normalizeUthmani(text: String): String {
            var s = text
            // Replace Dagger Alif (\u0670) with normal Alif
            s = s.replace("\u0670", "ا")
            // Small Waw and Small Yaa
            s = s.replace("\u06E5", "و")
            s = s.replace("\u06E6", "ي")
            // Alif over Waw in words like الصلوة / الزكوة / الحيوة
            s = s.replace("و\u0670", "ا")
            s = s.replace("وا۟", "ا")
            // Strip Tashkeel & Stop Marks
            s = s.replace(Regex("[\u064B-\u065F\u06D6-\u06ED]"), "")
            // Normalize Alefs
            s = s.replace("أ", "ا")
                .replace("إ", "ا")
                .replace("آ", "ا")
                .replace("ٱ", "ا")
            // Normalize Taa Marbuta
            s = s.replace("ة", "ه")
            // Normalize Yaa
            s = s.replace("ى", "ي")
            // Normalize Hamzas
            s = s.replace("ؤ", "ء")
                .replace("ئ", "ء")
            // Remove Tatweel
            s = s.replace("ـ", "")
            // Collapse repeated Alifs
            s = s.replace("اا", "ا")
            return s.trim()
        }

        private fun cleanArabic(text: String): String = normalizeUthmani(text)

        private fun loadVersesFromAssets(ctx: Context, surahId: Int): List<Verse> {
            synchronized(surahCache) {
                surahCache[surahId]?.let { return it }
            }

            try {
                if (rawJsonString == null) {
                    val stream: InputStream = ctx.assets.open("quran_uthmani.json")
                    rawJsonString = stream.bufferedReader().use { it.readText() }
                }

                val json = JSONObject(rawJsonString!!)
                val dataObj = json.getJSONObject("data")
                val surahsArray = dataObj.getJSONArray("surahs")

                for (s in 0 until surahsArray.length()) {
                    val surahObj = surahsArray.getJSONObject(s)
                    val sNum = surahObj.getInt("number")
                    if (sNum == surahId) {
                        val ayahsArray = surahObj.getJSONArray("ayahs")
                        val list = mutableListOf<Verse>()

                        val basmalahPattern = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ"

                        for (a in 0 until ayahsArray.length()) {
                            val ayahObj = ayahsArray.getJSONObject(a)
                            val num = ayahObj.getInt("number")
                            val numInSurah = ayahObj.getInt("numberInSurah")
                            var text = ayahObj.getString("text")
                                .replace("\uFEFF", "")
                                .trim()
                            val page = ayahObj.optInt("page", 1)

                            // Strip redundant prefix Basmalah from Ayah 1 in all surahs except Al-Fatihah (1) and At-Tawbah (9)
                            if (surahId != 1 && surahId != 9 && numInSurah == 1) {
                                if (text.startsWith(basmalahPattern)) {
                                    text = text.removePrefix(basmalahPattern).trim()
                                }
                            }

                            val shortTafseer = "بيان لمعنى الآية الكريمة وتدبر مقاصدها في ضوء التفسير الميسر لكتاب الله."

                            list.add(
                                Verse(
                                    id = num,
                                    verseNumber = numInSurah,
                                    textUthmani = text,
                                    tafseerShort = shortTafseer,
                                    page = page
                                )
                            )
                        }

                        synchronized(surahCache) {
                            surahCache[surahId] = list
                        }
                        return list
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return getSampleVersesForSurah(surahId)
        }

        const val KHATM_AL_QURAN_DUA = """اللَّهُمَّ ارْحَمْنِي بِالقُرْآنِ وَاجْعَلْهُ لِي إِمَاماً وَنُوراً وَهُدًى وَرَحْمَةً.
اللَّهُمَّ ذَكِّرْنِي مِنْهُ مَا نَسِيتُ وَعَلِّمْنِي مِنْهُ مَا جَهِلْتُ، وَارْزُقْنِي تِلاَوَتَهُ آنَاءَ اللَّيْلِ وَأَطْرَافَ النَّهَارِ، وَاجْعَلْهُ لِي حُجَّةً يَا رَبَّ العَالَمِينَ.
اللَّهُمَّ أَصْلِحْ لِي دِينِي الَّذِي هُوَ عِصْمَةُ أَمْرِي، وَأَصْلِحْ لِي دُنْيَايَ الَّتِي فِيهَا مَعَاشِي، وَأَصْلِحْ لِي آخِرَتِي الَّتِي فِيهَا مَعَادِي، وَاجْعَلِ الحَيَاةَ زِيَادَةً لِي فِي كُلِّ خَيْرٍ، وَاجْعَلِ المَوْتَ رَاحَةً لِي مِنْ كُلِّ شَرٍّ.
اللَّهُمَّ اجْعَلْ خَيْرَ عُمْرِي آخِرَهُ وَخَيْرَ عَمَلِي خَوَاتِمَهُ وَخَيْرَ أَيَّامِي يَوْمَ أَلْقَاكَ فِيهِ.
اللَّهُمَّ إِنِّي أَسْأَلُكَ عِيشَةً هَنِيَّةً وَمِيتَةً سَوِيَّةً وَمَرَدّاً غَيْرَ مُخْزٍ وَلاَ فَاضِحٍ.
اللَّهُمَّ إِنِّي أَسْأَلُكَ خَيْرَ المَسْأَلَةِ وَخَيْرَ الدُّعَاءِ وَخَيْرَ النَّجَاحِ وَخَيْرَ العِلْمِ وَخَيْرَ العَمَلِ وَخَيْرَ الثَّوَابِ وَخَيْرَ الحَيَاةِ وَخَيْرَ المَمَاتِ، وَثَبِّتْنِي وَثَقِّلْ مَوَازِينِي وَحَقِّقْ إِيمَانِي وَارْفَعْ دَرَجَتِي وَتَقَبَّلْ صَلاَتِي وَاغْفِرْ خَطِيئَاتِي وَأَسْأَلُكَ العُلاَ مِنَ الجَنَّةِ."""

        val SURAH_LIST = listOf(
            Surah(1, "الفاتحة", "Al-Fatihah", 7, "مكية", 1),
            Surah(2, "البقرة", "Al-Baqarah", 286, "مدنية", 2),
            Surah(3, "آل عمران", "Ali 'Imran", 200, "مدنية", 50),
            Surah(4, "النساء", "An-Nisa", 176, "مدنية", 77),
            Surah(5, "المائدة", "Al-Ma'idah", 120, "مدنية", 106),
            Surah(6, "الأنعام", "Al-An'am", 165, "مكية", 128),
            Surah(7, "الأعراف", "Al-A'raf", 206, "مكية", 151),
            Surah(8, "الأنفال", "Al-Anfal", 75, "مدنية", 177),
            Surah(9, "التوبة", "At-Tawbah", 129, "مدنية", 187),
            Surah(10, "يونس", "Yunus", 109, "مكية", 208),
            Surah(11, "هود", "Hud", 123, "مكية", 221),
            Surah(12, "يوسف", "Yusuf", 111, "مكية", 235),
            Surah(13, "الرعد", "Ar-Ra'd", 43, "مدنية", 249),
            Surah(14, "إبراهيم", "Ibrahim", 52, "مكية", 255),
            Surah(15, "الحجر", "Al-Hijr", 99, "مكية", 262),
            Surah(16, "النحل", "An-Nahl", 128, "مكية", 267),
            Surah(17, "الإسراء", "Al-Isra", 111, "مكية", 282),
            Surah(18, "الكهف", "Al-Kahf", 110, "مكية", 293),
            Surah(19, "مريم", "Maryam", 98, "مكية", 305),
            Surah(20, "طه", "Taha", 135, "مكية", 312),
            Surah(21, "الأنبياء", "Al-Anbiya", 112, "مكية", 322),
            Surah(22, "الحج", "Al-Hajj", 78, "مدنية", 332),
            Surah(23, "المؤمنون", "Al-Mu'minun", 118, "مكية", 342),
            Surah(24, "النور", "An-Nur", 64, "مدنية", 350),
            Surah(25, "الفرقان", "Al-Furqan", 77, "مكية", 359),
            Surah(26, "الشعراء", "Ash-Shu'ara", 227, "مكية", 367),
            Surah(27, "النمل", "An-Naml", 93, "مكية", 377),
            Surah(28, "القصص", "Al-Qasas", 88, "مكية", 385),
            Surah(29, "العنكبوت", "Al-'Ankabut", 69, "مكية", 396),
            Surah(30, "الروم", "Ar-Rum", 60, "مكية", 404),
            Surah(31, "لقمان", "Luqman", 34, "مكية", 411),
            Surah(32, "السجدة", "As-Sajdah", 30, "مكية", 415),
            Surah(33, "الأحزاب", "Al-Ahzab", 73, "مدنية", 418),
            Surah(34, "سبأ", "Saba", 54, "مكية", 428),
            Surah(35, "فاطر", "Fatir", 45, "مكية", 434),
            Surah(36, "يس", "Ya-Sin", 83, "مكية", 440),
            Surah(37, "الصافات", "As-Saffat", 182, "مكية", 446),
            Surah(38, "ص", "Sad", 88, "مكية", 453),
            Surah(39, "الزمر", "Az-Zumar", 75, "مكية", 458),
            Surah(40, "غافر", "Ghafir", 85, "مكية", 467),
            Surah(41, "فصلت", "Fussilat", 54, "مكية", 477),
            Surah(42, "الشورى", "Ash-Shura", 53, "مكية", 483),
            Surah(43, "الزخرف", "Az-Zukhruf", 89, "مكية", 489),
            Surah(44, "الدخان", "Ad-Dukhan", 59, "مكية", 496),
            Surah(45, "الجاثية", "Al-Jathiyah", 37, "مكية", 499),
            Surah(46, "الأحقاف", "Al-Ahqaf", 35, "مكية", 502),
            Surah(47, "محمد", "Muhammad", 38, "مدنية", 507),
            Surah(48, "الفتح", "Al-Fath", 29, "مدنية", 511),
            Surah(49, "الحجرات", "Al-Hujurat", 18, "مدنية", 515),
            Surah(50, "ق", "Qaf", 45, "مكية", 518),
            Surah(51, "الذاريات", "Adh-Dhariyat", 60, "مكية", 520),
            Surah(52, "الطور", "At-Tur", 49, "مكية", 523),
            Surah(53, "النجم", "An-Najm", 62, "مكية", 526),
            Surah(54, "القمر", "Al-Qamar", 55, "مكية", 528),
            Surah(55, "الرحمن", "Ar-Rahman", 78, "مدنية", 531),
            Surah(56, "الواقعة", "Al-Waqi'ah", 96, "مكية", 534),
            Surah(57, "الحديد", "Al-Hadid", 29, "مدنية", 537),
            Surah(58, "المجادلة", "Al-Mujadila", 22, "مدنية", 542),
            Surah(59, "الحشر", "Al-Hashr", 24, "مدنية", 545),
            Surah(60, "الممتحنة", "Al-Mumtahanah", 13, "مدنية", 549),
            Surah(61, "الصف", "As-Saff", 14, "مدنية", 551),
            Surah(62, "الجمعة", "Al-Jumu'ah", 11, "مدنية", 553),
            Surah(63, "المنافقون", "Al-Munafiqun", 11, "مدنية", 554),
            Surah(64, "التغابن", "At-Taghabun", 18, "مدنية", 556),
            Surah(65, "الطلاق", "At-Talaq", 12, "مدنية", 558),
            Surah(66, "التحريم", "At-Tahrim", 12, "مدنية", 560),
            Surah(67, "الملك", "Al-Mulk", 30, "مكية", 562),
            Surah(68, "القلم", "Al-Qalam", 52, "مكية", 564),
            Surah(69, "الحاقة", "Al-Haqqah", 52, "مكية", 566),
            Surah(70, "المعارج", "Al-Ma'arij", 44, "مكية", 568),
            Surah(71, "نوح", "Nuh", 28, "مكية", 570),
            Surah(72, "الجن", "Al-Jinn", 28, "مكية", 572),
            Surah(73, "المزمل", "Al-Muzzammil", 20, "مكية", 574),
            Surah(74, "المدثر", "Al-Muddaththir", 56, "مكية", 575),
            Surah(75, "القيامة", "Al-Qiyamah", 40, "مكية", 577),
            Surah(76, "الإنسان", "Al-Insan", 31, "مدنية", 578),
            Surah(77, "المرسلات", "Al-Mursalat", 50, "مكية", 580),
            Surah(78, "النبأ", "An-Naba", 40, "مكية", 582),
            Surah(79, "النازعات", "An-Nazi'at", 46, "مكية", 583),
            Surah(80, "عبس", "Abasa", 42, "مكية", 585),
            Surah(81, "التكوير", "At-Takwir", 29, "مكية", 586),
            Surah(82, "الانفطار", "Al-Infitar", 19, "مكية", 587),
            Surah(83, "المطففين", "Al-Mutaffifin", 36, "مكية", 587),
            Surah(84, "الانشقاق", "Al-Inshiqaq", 25, "مكية", 589),
            Surah(85, "البروج", "Al-Buruj", 22, "مكية", 590),
            Surah(86, "الطارق", "At-Tariq", 17, "مكية", 591),
            Surah(87, "الأعلى", "Al-A'la", 19, "مكية", 591),
            Surah(88, "الغاشية", "Al-Ghashiyah", 26, "مكية", 592),
            Surah(89, "الفجر", "Al-Fajr", 30, "مكية", 593),
            Surah(90, "البلد", "Al-Balad", 20, "مكية", 594),
            Surah(91, "الشمس", "Ash-Shams", 15, "مكية", 595),
            Surah(92, "الليل", "Al-Layl", 21, "مكية", 595),
            Surah(93, "الضحى", "Ad-Duha", 11, "مكية", 596),
            Surah(94, "الشرح", "Ash-Sharh", 8, "مكية", 596),
            Surah(95, "التين", "At-Tin", 8, "مكية", 597),
            Surah(96, "العلق", "Al-Alaq", 19, "مكية", 597),
            Surah(97, "القدر", "Al-Qadr", 5, "مكية", 598),
            Surah(98, "البينة", "Al-Bayyinah", 8, "مدنية", 598),
            Surah(99, "الزلزلة", "Az-Zalzalah", 8, "مدنية", 599),
            Surah(100, "العاديات", "Al-Adiyat", 11, "مكية", 599),
            Surah(101, "القارعة", "Al-Qari'ah", 11, "مكية", 600),
            Surah(102, "التكاثر", "At-Takathur", 8, "مكية", 600),
            Surah(103, "العصر", "Al-Asr", 3, "مكية", 601),
            Surah(104, "الهمزة", "Al-Humazah", 9, "مكية", 601),
            Surah(105, "الفيل", "Al-Fil", 5, "مكية", 601),
            Surah(106, "قريش", "Quraysh", 4, "مكية", 602),
            Surah(107, "الماعون", "Al-Ma'un", 7, "مكية", 602),
            Surah(108, "الكوثر", "Al-Kawthar", 3, "مكية", 602),
            Surah(109, "الكافرون", "Al-Kafirun", 6, "مكية", 603),
            Surah(110, "النصر", "An-Nasr", 3, "مدنية", 603),
            Surah(111, "المسد", "Al-Masad", 5, "مكية", 603),
            Surah(112, "الإخلاص", "Al-Ikhlas", 4, "مكية", 604),
            Surah(113, "الفلق", "Al-Falaq", 5, "مكية", 604),
            Surah(114, "الناس", "An-Nas", 6, "مكية", 604)
        )

        private fun getSampleVersesForSurah(surahId: Int): List<Verse> {
            return when (surahId) {
                1 -> listOf(
                    Verse(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "أبدأ قراءتي مستعيناً بالله، الرحمن بجميع خلقه، الرحيم بالمؤمنين.", 1),
                    Verse(2, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "الثناء والحمد لله وحده مالك وخالق ومربي جميع الكائنات.", 1),
                    Verse(3, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "الذي وسعت رحمته كل شيء، وأفاض بإحسانه على عباده.", 1),
                    Verse(4, 4, "مَالِكِ يَوْمِ الدِّينِ", "المتصرف وحده بالعدل والجزاء يوم القيامة والحساب.", 1),
                    Verse(5, 5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "نخصك وحدك بالعبادة والخضوع، ونستعين بك في جميع أمورنا.", 1),
                    Verse(6, 6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "وفقنا وأرشدنا وثبتنا على الطريق الواضح الموصل لرضوانك.", 1),
                    Verse(7, 7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "طريق النبيين والصديقين، لا طريق من عرف الحق وتركه، ولا من ضل عنه بجهل.", 1)
                )
                112 -> listOf(
                    Verse(1, 1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "قل أيها الرسول: الله هو المنفرد بالألوهية والربوبية والأسماء والصفات، لا شريك له.", 1),
                    Verse(2, 2, "اللَّهُ الصَّمَدُ", "الذي تقصده جميع الخلائق في حوائجها ورغباتها، لكمال صفاته وغناه عن خلقه.", 1),
                    Verse(3, 3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "ليس له ولد ولا والد، لتنزهه عن صفات الحدوث والنقص.", 1),
                    Verse(4, 4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "وليس له مماثل ولا نظير في خلقه، سبحانه وتعالى.", 1)
                )
                113 -> listOf(
                    Verse(1, 1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "قل: أعتصم وألتجئ برب الصبح وفالقه بالنور بعد الظلام.", 1),
                    Verse(2, 2, "مِن شَرِّ مَا خَلَقَ", "من شر جميع ما خلق الله من الإنس والجن والحيوان وسائر المؤذيات.", 1),
                    Verse(3, 3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "ومن شر الليل المظلم إذا دخل واشتدت ظلمته وما ينتشر فيه من سوء.", 1),
                    Verse(4, 4, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "ومن شر السواحر اللاتي يعقدن العقد وينفثن فيها بالسحر والأذى.", 1),
                    Verse(5, 5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "ومن شر الحاسد إذا أظهر حسده وتمنى زوال نعمة الله عن غيره.", 1)
                )
                114 -> listOf(
                    Verse(1, 1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "قل: ألتجئ وأعتصم برب الناس وخالقهم ومدبر أمورهم.", 1),
                    Verse(2, 2, "مَلِكِ النَّاسِ", "ملكهم الحق، المتصرف فيهم بما يشاء، لا ملك سواه.", 1),
                    Verse(3, 3, "إِلَٰهِ النَّاسِ", "معبودهم الحق الذي لا تنبغي العبادة والألوهية إلا له.", 1),
                    Verse(4, 4, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "من شر الشيطان الذي يوسوس عند الغفلة، ويخنس ويختفي عند ذكر الله.", 1),
                    Verse(5, 5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "الذي يبث الشبهات والشهوات والوساوس في قلوب بني آدم.", 1),
                    Verse(6, 6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "وهؤلاء الشياطين الموسوسون يكونون من شياطين الجن ومن شياطين الإنس.", 1)
                )
                else -> listOf(
                    Verse(1, 1, "الم", "حروف مقطعة للتحدي والإعجاز، والله أعلم بمراده بها.", 1),
                    Verse(2, 2, "ذَٰلِكَ الْكِتَابُ لَا رَيْبَ ۛ فِيهِ ۛ هُدًى لِّلْمُتَّقِينَ", "هذا القرآن كتاب عظيم لا شك في صدقه وأنه من عند الله، يهدي القلوب الصالحة.", 1),
                    Verse(3, 3, "الَّذِينَ يُؤْمِنُونَ بِالْغَيْبِ وَيُقِيمُونَ الصَّلَاةَ وَمِمَّا رَزَقْنَاهُمْ يُنفِقُونَ", "الذين يصدقون بما غاب عن حواسهم من البعث والجنة والنار، ويحافظون على الصلاة.", 1),
                    Verse(4, 4, "وَالَّذِينَ يُؤْمِنُونَ بِمَا أُنزِلَ إِلَيْكَ وَمَا أُنزِلَ مِن قَبْلِكَ وَبِالْآخِرَةِ هُمْ يُوقِنُونَ", "ويصدقون بالقرآن وبالكتب السابقة، ويوقنون بالدار الآخرة وما فيها من حساب وجزاء.", 1),
                    Verse(5, 5, "أُولَٰئِكَ عَلَىٰ هُدًى مِّن رَّبِّهِمْ ۖ وَأُولَٰئِكَ هُمُ الْمُفْلِحُونَ", "أصحاب هذه الصفات على نور وبصيرة من الله، وهم الفائزون في الدنيا والآخرة.", 1)
                )
            }
        }
    }
}
