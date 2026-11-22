package com.islamic.noor.data.repository

import com.islamic.noor.domain.model.CharityProject
import com.islamic.noor.domain.model.DeceasedDua
import com.islamic.noor.domain.model.GoodDeedDoor
import com.islamic.noor.domain.model.GroupKhatmah
import com.islamic.noor.domain.model.PeaceMessage
import com.islamic.noor.domain.model.Reciter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GhirasRepository {

    // 1. رسالة طمأنينة اليوم (عن الصبر والفرج والسكينة)
    fun getDailyPeaceMessage(): Flow<PeaceMessage> = flow {
        emit(
            PeaceMessage(
                id = 1,
                title = "رسالة طمأنينة لقلبك اليوم",
                text = "«وَبَشِّرِ الصَّابِرِينَ الَّذِينَ إِذَا أَصَابَتْهُم مُّصِيبَةٌ قَالُوا إِنَّا لِلَّهِ وَإِنَّا إِلَيْهِ رَاجِعُونَ * أُولَٰئِكَ عَلَيْهِمْ صَلَوَاتٌ مِّن رَّبِّهِمْ وَرَحْمَةٌ ۖ وَأُولَٰئِكَ هُمُ الْمُهْتَدُونَ». اطمئن؛ ما أخر الله أمراً إلا لخير، وما صرف عنك شيئاً إلا ليعوضك بالأجمل.",
                source = "سورة البقرة: 155-157",
                theme = "الصبر والفرج"
            )
        )
    }.flowOn(Dispatchers.IO)

    // 2. أدعية للمتوفين (دعاء للميت ونور القبر والصدقة الجارية)
    fun getDeceasedDuas(): Flow<List<DeceasedDua>> = flow {
        emit(
            listOf(
                DeceasedDua(
                    id = 1,
                    title = "دعاء جامع للميت بالرحمة والمغفرة",
                    text = "اللَّهُمَّ اغْفِرْ لَهُ وَارْحَمْهُ، وَعَافِهِ وَاعْفُ عَنْهُ، وَأَكْرِمْ نُزُلَهُ، وَوَسِّعْ مُدْخَلَهُ، وَاغْسِلْهُ بِالْمَاءِ وَالثَّلْجِ وَالْبَرَدِ، وَنَقِّهِ مِنَ الْخَطَايَا كَمَا نَقَّيْتَ الثَّوْبَ الأَبْيَضَ مِنَ الدَّنَسِ.",
                    reference = "صحيح مسلم"
                ),
                DeceasedDua(
                    id = 2,
                    title = "دعاء لتنوير القبر والفسحة فيه",
                    text = "اللَّهُمَّ اجْعَلْ قَبْرَهُ رَوْضَةً مِنْ رِيَاضِ الْجَنَّةِ، وَلاَ تَجْعَلْهُ حُفْرَةً مِنْ حُفَرِ النَّارِ، اللَّهُمَّ افْسَحْ لَهُ فِي قَبْرِهِ مَدَّ بَصَرِهِ، وَافْرُشْ قَبْرَهُ مِنْ فِرَاشِ الْجَنَّةِ.",
                    reference = "دعاء مأثور"
                ),
                DeceasedDua(
                    id = 3,
                    title = "دعاء الرفعة في المهديين",
                    text = "اللَّهُمَّ اغْفِرْ لَهُ وَارْفَعْ دَرَجَتَهُ فِي الْمَهْدِيِّينَ، وَاخْلُفْهُ فِي عَقِبِهِ فِي الْغَابِرِينَ، وَاغْفِرْ لَنَا وَلَهُ يَا رَبَّ الْعَالَمِينَ، وَافْسَحْ لَهُ فِي قَبْرِهِ وَنَوِّرْ لَهُ فِيهِ.",
                    reference = "صحيح مسلم"
                )
            )
        )
    }.flowOn(Dispatchers.IO)

    // 3. غراس الخير: أبواب الخير وأجرها وثوابها
    fun getGoodDeeds(): Flow<List<GoodDeedDoor>> = flow {
        emit(
            listOf(
                GoodDeedDoor(
                    id = 1,
                    title = "بناء المساجد وعمارتها",
                    description = "المساهمة في بناء بيت يذكر فيه اسم الله وإعانة المصلين.",
                    rewardHadith = "قال رسول الله ﷺ: «مَنْ بَنَى مَسْجِدًا لِلَّهِ كَمَفْحَصِ قَطَاةٍ أَوْ أَصْغَرَ، بَنَى اللَّهُ لَهُ بَيْتًا فِي الْجَنَّةِ».",
                    source = "رواه ابن ماجه وصححه الألباني"
                ),
                GoodDeedDoor(
                    id = 2,
                    title = "سُقيا الماء وتوفير الشرب",
                    description = "توفير المياه للمحتاجين، حفر الآبار، أو وضع مبردات مياه.",
                    rewardHadith = "سُئل النبي ﷺ: أَيُّ الصَّدَقَةِ أَفْضَلُ؟ قَالَ: «سَقْيُ الْمَاءِ».",
                    source = "رواه أبو داود والنسائي"
                ),
                GoodDeedDoor(
                    id = 3,
                    title = "صلة الأرحام والإحسان للأهل",
                    description = "زيارة الأقارب، الاتصال بهم، وتفقد أحوالهم والإحسان إليهم.",
                    rewardHadith = "قال رسول الله ﷺ: «مَنْ سَرَّهُ أَنْ يُبْسَطَ لَهُ فِي رِزْقِهِ، وَأَنْ يُنْسَأَ لَهُ فِي أَثَرِهِ، فَلْيَصِلْ رَحِمَهُ».",
                    source = "صحيح البخاري ومسلم"
                ),
                GoodDeedDoor(
                    id = 4,
                    title = "عيادة المريض وتفريج كربته",
                    description = "زيارة المريض والدعاء له والتخفيف عن أهله.",
                    rewardHadith = "قال رسول الله ﷺ: «إِنَّ الْمُسْلِمَ إِذَا عَادَ أَخَاهُ الْمُسْلِمَ، لَمْ يَزَلْ فِي خُرْفَةِ الْجَنَّةِ حَتَّى يَرْجِعَ».",
                    source = "صحيح مسلم"
                ),
                GoodDeedDoor(
                    id = 5,
                    title = "إطعام الطعام وكفالة المحتاج",
                    description = "سد جوع الفقير وإكرام الضيف ومساعدة الأسر المتعففة.",
                    rewardHadith = "«يَا أَيُّهَا النَّاسُ: أَفْشُوا السَّلاَمَ، وَأَطْعِمُوا الطَّعَامَ، وَصِلُوا الأَرْحَامَ، وَصَلُّوا بِاللَّيْلِ وَالنَّاسُ نِيَامٌ، تَدْخُلُوا الْجَنَّةَ بِسَلاَمٍ».",
                    source = "رواه الترمذي وصححه"
                )
            )
        )
    }.flowOn(Dispatchers.IO)

    // 4. مشاريع الصدقة الجارية (حفر آبار، فرش مساجد، إطعام المحتاجين) - طلب العميل
    private val _projects = MutableStateFlow(
        listOf(
            CharityProject(
                id = "proj_well",
                title = "مشروع حفر بئر وسُقيا ماء",
                location = "قرى المحتاجين بالمناطق النائية",
                description = "توفير مياه شرب نقية للمسلمين، صدقة جارية مستمرة لك ولوالديك.",
                targetAmount = 30000,
                collectedAmount = 21500,
                sharePrice = 100,
                category = "سُقيا الماء"
            ),
            CharityProject(
                id = "proj_mosque",
                title = "مشروع فرش وتجهيز مسجد الرحمة",
                location = "المسجد الكبير بالقرية",
                description = "المساهمة في شراء السجاد وتجهيزات الصوت ومبردات المياه لراحة المصلين.",
                targetAmount = 20000,
                collectedAmount = 14200,
                sharePrice = 100,
                category = "عمارة المساجد"
            ),
            CharityProject(
                id = "proj_food",
                title = "كراتين الخير للأسر المتعففة",
                location = "توزيع محلي للأرامل والأيتام",
                description = "توفير المواد الغذائية الأساسية لبيوت المحتاجين وإدخال السرور عليهم.",
                targetAmount = 15000,
                collectedAmount = 11800,
                sharePrice = 150,
                category = "إطعام الطعام"
            )
        )
    )
    val charityProjects = _projects.asStateFlow()

    fun pledgeShare(projectId: String, shares: Int) {
        val updated = _projects.value.map { proj ->
            if (proj.id == projectId) {
                val addition = shares * proj.sharePrice
                proj.copy(collectedAmount = (proj.collectedAmount + addition).coerceAtMost(proj.targetAmount))
            } else proj
        }
        _projects.value = updated
    }

    // 5. الختمة الجماعية
    fun getGroupKhatmahs(): Flow<List<GroupKhatmah>> = flow {
        emit(
            listOf(
                GroupKhatmah(
                    id = "khatmah_1",
                    title = "ختمة الأهل والأحباب التشاركية",
                    totalParts = 30,
                    completedParts = 21,
                    participantsCount = 14,
                    deadlineDate = "الجمعة القادمة"
                ),
                GroupKhatmah(
                    id = "khatmah_2",
                    title = "ختمة أسبوعية عامة (غِراس)",
                    totalParts = 30,
                    completedParts = 9,
                    participantsCount = 7,
                    deadlineDate = "خلال 4 أيام"
                )
            )
        )
    }.flowOn(Dispatchers.IO)

    // 6. قائمة قراء وشيوخ التطبيق (شيوخ العميل + كبار القراء)
    fun getReciters(): Flow<List<Reciter>> = flow {
        emit(
            listOf(
                Reciter("sh_abdelhalim", "الشيخ عبد الحليم", "تلاوة خاشعة وندية", isLocalSheikh = true),
                Reciter("sh_rabie", "الشيخ ربيع", "قراءة متقنة ومجودة بأحكام التجويد", isLocalSheikh = true),
                Reciter("sh_fawzy", "الشيخ فوزي", "صوت عذب ومؤثر من القلب", isLocalSheikh = true),
                Reciter("sh_doghim", "الشيخ دغيم", "تلاوة وقورة وهادئة", isLocalSheikh = true),
                Reciter("sh_minshawi", "الشيخ محمد صديق المنشاوي", "المصحف المرتل برواية حفص", isLocalSheikh = false),
                Reciter("sh_hussary", "الشيخ محمود خليل الحصري", "المصحف المعلم وإتقان المخارج", isLocalSheikh = false)
            )
        )
    }.flowOn(Dispatchers.IO)

    companion object {
        val instance = GhirasRepository()
    }
}
