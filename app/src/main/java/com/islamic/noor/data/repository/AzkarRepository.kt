package com.islamic.noor.data.repository

import com.islamic.noor.domain.model.HadithItem
import com.islamic.noor.domain.model.ZekrItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AzkarRepository {

    val customCategories = MutableStateFlow<List<String>>(listOf("أذكاري المخصصة"))
    val customAzkarList = MutableStateFlow<List<ZekrItem>>(emptyList())
    val favoriteIds = MutableStateFlow<Set<Int>>(emptySet())

    fun getStandardCategories(): List<String> = listOf(
        "أذكار الصباح",
        "أذكار المساء",
        "أذكار النوم",
        "أذكار الاستيقاظ",
        "أذكار بعد الصلاة",
        "أذكار المسجد"
    )

    fun addCustomCategory(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank() && !customCategories.value.contains(trimmed)) {
            customCategories.value = customCategories.value + trimmed
        }
    }

    fun getAzkarByCategory(category: String): Flow<List<ZekrItem>> {
        if (category == "الأذكار المفضلة") {
            return flow {
                val favs = (ALL_AZKAR + customAzkarList.value).filter { favoriteIds.value.contains(it.id) }
                emit(favs)
            }.flowOn(Dispatchers.IO)
        }
        if (customCategories.value.contains(category) || category == "أذكاري المخصصة") {
            return flow {
                emit(customAzkarList.value.filter { it.category == category })
            }.flowOn(Dispatchers.IO)
        }
        return flow {
            val items = ALL_AZKAR.filter { it.category == category }
            emit(if (items.isNotEmpty()) items else ALL_AZKAR.take(3))
        }.flowOn(Dispatchers.IO)
    }

    fun addCustomZekr(category: String, text: String, count: Int, reward: String = "") {
        val newItem = ZekrItem(
            id = (System.currentTimeMillis() % 100000).toInt(),
            category = category,
            text = text.trim(),
            count = count,
            reward = reward.trim(),
            reference = "ذكر خاص محفوظ محلياً",
            isCustom = true
        )
        customAzkarList.value = customAzkarList.value + newItem
    }

    fun deleteCustomZekr(id: Int) {
        customAzkarList.value = customAzkarList.value.filter { it.id != id }
    }

    fun editCustomZekr(id: Int, text: String, count: Int, reward: String) {
        customAzkarList.value = customAzkarList.value.map {
            if (it.id == id) {
                it.copy(text = text.trim(), count = count, reward = reward.trim())
            } else it
        }
    }

    fun toggleFavorite(id: Int) {
        val current = favoriteIds.value
        favoriteIds.value = if (current.contains(id)) {
            current - id
        } else {
            current + id
        }
    }

    fun isFavorite(id: Int): Boolean = favoriteIds.value.contains(id)

    fun getAllHadiths(): Flow<List<HadithItem>> = flow {
        emit(SAMPLE_HADITHS)
    }.flowOn(Dispatchers.IO)

    companion object {
        val instance = AzkarRepository()

        val ALL_AZKAR = listOf(
            // الصباح
            ZekrItem(1, "أذكار الصباح", "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لاَ إِلَـهَ إِلاَّ اللهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.", 1, "حفظ المسلم وكفايته يومه", "رواه مسلم"),
            ZekrItem(2, "أذكار الصباح", "اللَّهُمَّ أَنْتَ رَبِّي لاَ إِلَهَ إِلاَّ أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لاَ يَغْفِرُ الذُّنُوبَ إِلاَّ أَنْتَ.", 1, "سيد الاستغفار، من قالها موقناً بها ومات دخل الجنة", "رواه البخاري"),
            ZekrItem(3, "أذكار الصباح", "سُبْحَانَ اللهِ وَبِحَمْدِهِ: عَدَدَ خَلْقِهِ، وَرِضَا نَفْسِهِ، وَزِنَةَ عَرْشِهِ، وَمِدَادَ كَلِمَاتِهِ.", 3, "أعظم ثواباً وأثقل في الميزان", "رواه مسلم"),
            ZekrItem(4, "أذكار الصباح", "بِسْمِ اللَّهِ الَّذِي لاَ يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الأَرْضِ وَلاَ فِي السَّمَاءِ وَهُوَ السَّمِيعُ العَلِيمُ.", 3, "لم يضره شيء حتى يمسي", "رواه أبو داود والترمذي"),

            // المساء
            ZekrItem(5, "أذكار المساء", "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لاَ إِلَـهَ إِلاَّ اللهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.", 1, "حفظ المسلم ليلته", "رواه مسلم"),
            ZekrItem(6, "أذكار المساء", "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ.", 3, "لم يضره شيء في ليلته", "رواه مسلم"),
            ZekrItem(7, "أذكار المساء", "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ وَإِلَيْكَ الْمَصِيرُ.", 1, "شكر نعمة المساء", "رواه الترمذي"),

            // النوم
            ZekrItem(8, "أذكار النوم", "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ.", 1, "حفظ الروح عند النوم", "رواه البخاري ومسلم"),
            ZekrItem(9, "أذكار النوم", "اللَّهُمَّ قِنِي عَذَابَكَ يَوْمَ تَبْعَثُ عِبَادَكَ.", 3, "سنة النبي قبل النوم", "رواه أبو داود"),

            // الاستيقاظ
            ZekrItem(10, "أذكار الاستيقاظ", "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ.", 1, "شكر نعمة رد الروح والاستيقاظ", "رواه البخاري"),
            ZekrItem(11, "أذكار الاستيقاظ", "لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، سُبْحَانَ اللَّهِ، وَالْحَمْدُ لِلَّهِ، وَلاَ إِلَهَ إِلاَّ اللَّهُ، وَاللَّهُ أَكْبَرُ، وَلاَ حَوْلَ وَلاَ قُوَّةَ إِلاَّ بِاللَّهِ الْعَلِيِّ الْعَظِيمِ.", 1, "إن دعا استجيب له، وإن صلى قُبلت صلاته", "رواه البخاري"),

            // بعد الصلاة
            ZekrItem(12, "أذكار بعد الصلاة", "أَسْتَغْفِرُ اللهَ (ثلاثاً)، اللَّهُمَّ أَنْتَ السَّلاَمُ وَمِنْكَ السَّلاَمُ، تَبَارَكْتَ يَا ذَا الْجَلاَلِ وَالإِكْرَامِ.", 1, "سنة بعد الفريضة مباشرة", "رواه مسلم"),
            ZekrItem(13, "أذكار بعد الصلاة", "سُبْحَانَ اللهِ (33)، الْحَمْدُ لِلَّهِ (33)، اللهُ أَكْبَرُ (33)، لا إله إلا الله وحده لا شريك له له الملك وله الحمد وهو على كل شيء قدير.", 33, "غُفرت خطاياه وإن كانت مثل زبد البحر", "رواه مسلم"),

            // المسجد
            ZekrItem(14, "أذكار المسجد", "اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ (عند دخول المسجد).", 1, "دعاء دخول المسجد", "رواه مسلم"),
            ZekrItem(15, "أذكار المسجد", "اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ (عند الخروج من المسجد).", 1, "دعاء الخروج من المسجد", "رواه مسلم")
        )

        val SAMPLE_HADITHS = listOf(
            HadithItem(1, "الأربعين النووية", "الحديث الأول: إخلاص النية", "عَنْ أَمِيرِ الْمُؤْمِنِينَ عُمَرَ بْنِ الْخَطَّابِ رضي الله عنه: «إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى...»", "رواه البخاري ومسلم", "كل عمل صالح يبتغي به المسلم وجه الله فهو عبادة مأجور عليها."),
            HadithItem(2, "الأربعين النووية", "الحديث الثالث عشر: محبة الخير", "عَنْ أَنَسٍ رضي الله عنه أَنَّ رَسُولَ اللَّهِ صلى الله عليه وسلم قَالَ: «لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ».", "رواه البخاري ومسلم", "من كمال الإيمان سلامة الصدر ومحبة الخير للناس."),
            HadithItem(3, "رياض الصالحين", "باب الصبر واليقين", "عَنْ صُهَيْبٍ رضي الله عنه قَالَ: قَالَ رَسُولُ اللَّهِ صلى الله عليه وسلم: «عَجَبًا لأَمْرِ الْمُؤْمِنِ إِنَّ أَمْرَهُ كُلَّهُ خَيْرٌ، إِنْ أَصَابَتْهُ سَرَّاءُ شَكَرَ فَكَانَ خَيْرًا لَهُ، وَإِنْ أَصَابَتْهُ ضَرَّاءُ صَبَرَ فَكَانَ خَيْرًا لَهُ».", "رواه مسلم", "المؤمن رابح في كل أحواله بين الشكر والصبر.")
        )
    }
}
