package com.islamic.noor.data.repository

import com.islamic.noor.domain.model.PrayerTimeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class GovernorateLocation(
    val name: String,
    val lat: Double,
    val lng: Double,
    val minuteOffset: Int // فرق الدقائق عن توقيت القاهرة
)

class QiblaAndPrayerRepository {

    val EGYPT_GOVERNORATES = listOf(
        GovernorateLocation("القاهرة", 30.0444, 31.2357, 0),
        GovernorateLocation("الجيزة", 30.0131, 31.2089, 0),
        GovernorateLocation("الإسكندرية", 31.2001, 29.9187, 4),
        GovernorateLocation("الدقهلية (المنصورة)", 31.0409, 31.3785, 1),
        GovernorateLocation("الشرقية (الزقازيق)", 30.5877, 31.5020, -1),
        GovernorateLocation("القليوبية (بنها)", 30.4608, 31.1856, 0),
        GovernorateLocation("الغربية (طنطا)", 30.7865, 31.0004, 1),
        GovernorateLocation("المنوفية (شبين الكوم)", 30.5526, 31.0094, 1),
        GovernorateLocation("البحيرة (دمنهور)", 31.0402, 30.4700, 3),
        GovernorateLocation("كفر الشيخ", 31.1107, 30.9388, 2),
        GovernorateLocation("دمياط", 31.4175, 31.8144, -2),
        GovernorateLocation("بورسعيد", 31.2653, 32.3019, -4),
        GovernorateLocation("الإسماعيلية", 30.5965, 32.2715, -4),
        GovernorateLocation("السويس", 29.9668, 32.5498, -4),
        GovernorateLocation("شمال سيناء (العريش)", 31.1316, 33.7984, -9),
        GovernorateLocation("جنوب سيناء (طور سيناء)", 28.2364, 33.6254, -7),
        GovernorateLocation("بني سويف", 29.0661, 31.0994, 0),
        GovernorateLocation("الفيوم", 29.3084, 30.8428, 1),
        GovernorateLocation("المنيا", 28.0871, 30.7618, 1),
        GovernorateLocation("أسيوط", 27.1783, 31.1859, 0),
        GovernorateLocation("سوهاج", 26.5591, 31.6957, -2),
        GovernorateLocation("قنا", 26.1551, 32.7160, -6),
        GovernorateLocation("الأقصر", 25.6872, 32.6396, -6),
        GovernorateLocation("أسوان", 24.0889, 32.8998, -6),
        GovernorateLocation("البحر الأحمر (الغردقة)", 27.2579, 33.8116, -9),
        GovernorateLocation("الوادي الجديد (الخارجة)", 25.4514, 30.5464, 4),
        GovernorateLocation("مطروح", 31.3543, 27.2373, 14)
    )

    fun getGovernorate(name: String): GovernorateLocation {
        return EGYPT_GOVERNORATES.firstOrNull { it.name == name } ?: EGYPT_GOVERNORATES[0]
    }

    /**
     * حساب زاوية القبلة من أي إحداثيات (خط العرض وخط الطول) محلياً بدون إنترنت
     * إحداثيات الكعبة المشرفة: 21.4225 N, 39.8262 E
     */
    fun calculateQiblaAngle(userLat: Double = 30.0444, userLng: Double = 31.2357): Float {
        val kaabaLat = Math.toRadians(21.422487)
        val kaabaLng = Math.toRadians(39.826206)
        val myLat = Math.toRadians(userLat)
        val myLng = Math.toRadians(userLng)

        val deltaLng = kaabaLng - myLng
        val y = sin(deltaLng)
        val x = cos(myLat) * Math.tan(kaabaLat) - sin(myLat) * cos(deltaLng)

        var qibla = Math.toDegrees(atan2(y, x)).toFloat()
        if (qibla < 0) {
            qibla += 360f
        }
        return qibla
    }

    /**
     * حساب مواقيت الصلاة لليوم حسب المحافظة المختارة
     */
    fun getPrayerTimesForToday(govName: String = "القاهرة", iqamaDelay: Int = 15): Flow<List<PrayerTimeItem>> = flow {
        val gov = getGovernorate(govName)
        val offset = gov.minuteOffset

        // Base times for Cairo
        fun formatWithOffset(baseHour: Int, baseMin: Int, isPm: Boolean = false): String {
            var totalMinutes = baseHour * 60 + baseMin + offset
            if (totalMinutes < 0) totalMinutes += 24 * 60
            val h = (totalMinutes / 60) % 24
            val m = totalMinutes % 60
            val displayHour = if (h == 0) 12 else if (h > 12) h - 12 else h
            val suffix = if (isPm || h >= 12) "م" else "ص"
            return String.format("%02d:%02d %s", displayHour, m, suffix)
        }

        val prayers = listOf(
            PrayerTimeItem("الفجر", formatWithOffset(4, 58, false), iqamaDelayMinutes = 20),
            PrayerTimeItem("الشروق", formatWithOffset(6, 27, false), iqamaDelayMinutes = 0),
            PrayerTimeItem("الظهر", formatWithOffset(11, 46, false), iqamaDelayMinutes = iqamaDelay),
            PrayerTimeItem("العصر", formatWithOffset(14, 44, true), isNext = true, iqamaDelayMinutes = iqamaDelay),
            PrayerTimeItem("المغرب", formatWithOffset(17, 4, true), iqamaDelayMinutes = 10),
            PrayerTimeItem("العشاء", formatWithOffset(18, 24, true), iqamaDelayMinutes = iqamaDelay)
        )
        emit(prayers)
    }.flowOn(Dispatchers.IO)
}
