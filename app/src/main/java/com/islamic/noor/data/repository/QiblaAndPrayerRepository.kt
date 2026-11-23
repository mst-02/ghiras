package com.islamic.noor.data.repository

import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.Prayer
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import com.islamic.noor.data.local.GhirasPreferences
import com.islamic.noor.domain.model.PrayerTimeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class GovernorateLocation(
    val name: String,
    val lat: Double,
    val lng: Double,
    val minuteOffset: Int,
    val isAuto: Boolean = false
)

data class NextPrayerLiveInfo(
    val prayerName: String,
    val timeFormatted: String,
    val remainingSeconds: Long
)

class QiblaAndPrayerRepository {

    val EGYPT_GOVERNORATES = listOf(
        GovernorateLocation("تحديد تلقائي عبر الموقع (GPS)", 30.0444, 31.2357, 0, isAuto = true),
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
        return EGYPT_GOVERNORATES.firstOrNull { it.name == name } ?: EGYPT_GOVERNORATES[1]
    }

    fun getEffectiveCoordinates(govName: String): Coordinates {
        if (govName.contains("GPS") || govName.contains("تلقائي")) {
            val saved = try { GhirasPreferences.getSavedCoordinates() } catch (_: Exception) { null }
            if (saved != null) {
                return Coordinates(saved.first, saved.second)
            }
        }
        val gov = getGovernorate(govName)
        return Coordinates(gov.lat, gov.lng)
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
     * حساب مواقيت الصلاة الفلكية الدقيقة لليوم شاملاً الشروق
     * طبقاً لمعايير الهيئة المصرية العامة للمساحة (Egyptian General Authority of Survey)
     */
    fun getPrayerTimesForToday(govName: String = "القاهرة", iqamaDelay: Int = 15): Flow<List<PrayerTimeItem>> = flow {
        val coordinates = getEffectiveCoordinates(govName)
        val now = Date()
        val dateComponents = DateComponents.from(now)
        val params = CalculationMethod.EGYPTIAN.parameters.apply {
            madhab = Madhab.SHAFI
        }
        val pt = PrayerTimes(coordinates, dateComponents, params)

        val timeFormatter = SimpleDateFormat("hh:mm", Locale.getDefault())
        fun formatWithSuffix(date: Date?): String {
            if (date == null) return "--:--"
            val cal = Calendar.getInstance().apply { time = date }
            val hour24 = cal.get(Calendar.HOUR_OF_DAY)
            val suffix = if (hour24 >= 12) "م" else "ص"
            return "${timeFormatter.format(date)} $suffix"
        }

        val nextP = pt.nextPrayer()

        val prayers = listOf(
            PrayerTimeItem("الفجر", formatWithSuffix(pt.fajr), isNext = nextP == Prayer.FAJR, iqamaDelayMinutes = 20),
            PrayerTimeItem("الشروق", formatWithSuffix(pt.sunrise), isNext = nextP == Prayer.SUNRISE, iqamaDelayMinutes = 0),
            PrayerTimeItem("الظهر", formatWithSuffix(pt.dhuhr), isNext = nextP == Prayer.DHUHR, iqamaDelayMinutes = iqamaDelay),
            PrayerTimeItem("العصر", formatWithSuffix(pt.asr), isNext = nextP == Prayer.ASR, iqamaDelayMinutes = iqamaDelay),
            PrayerTimeItem("المغرب", formatWithSuffix(pt.maghrib), isNext = nextP == Prayer.MAGHRIB, iqamaDelayMinutes = 10),
            PrayerTimeItem("العشاء", formatWithSuffix(pt.isha), isNext = nextP == Prayer.ISHA, iqamaDelayMinutes = iqamaDelay)
        )
        emit(prayers)
    }.flowOn(Dispatchers.IO)

    /**
     * حساب وتحديد الصلاة القادمة الحقيقية لحظياً والوقت المتبقي لها بالثواني
     */
    fun getNextPrayerLiveInfo(govName: String = "القاهرة"): NextPrayerLiveInfo {
        val coordinates = getEffectiveCoordinates(govName)
        val now = Date()
        val dateComponents = DateComponents.from(now)
        val params = CalculationMethod.EGYPTIAN.parameters.apply {
            madhab = Madhab.SHAFI
        }
        val pt = PrayerTimes(coordinates, dateComponents, params)

        val nextP = pt.nextPrayer()
        var targetDate: Date? = null
        var prayerTitle = "صلاة الفجر"

        when (nextP) {
            Prayer.FAJR -> {
                targetDate = pt.fajr
                prayerTitle = "صلاة الفجر"
            }
            Prayer.SUNRISE -> {
                targetDate = pt.sunrise
                prayerTitle = "شروق الشمس"
            }
            Prayer.DHUHR -> {
                targetDate = pt.dhuhr
                prayerTitle = "صلاة الظهر"
            }
            Prayer.ASR -> {
                targetDate = pt.asr
                prayerTitle = "صلاة العصر"
            }
            Prayer.MAGHRIB -> {
                targetDate = pt.maghrib
                prayerTitle = "صلاة المغرب"
            }
            Prayer.ISHA -> {
                targetDate = pt.isha
                prayerTitle = "صلاة العشاء"
            }
            else -> {
                // After Isha: Calculate tomorrow's Fajr
                val cal = Calendar.getInstance().apply {
                    time = now
                    add(Calendar.DAY_OF_YEAR, 1)
                }
                val tomorrowPt = PrayerTimes(coordinates, DateComponents.from(cal.time), params)
                targetDate = tomorrowPt.fajr
                prayerTitle = "صلاة الفجر"
            }
        }

        val remainingSecs = if (targetDate != null) {
            ((targetDate.time - now.time) / 1000).coerceAtLeast(0)
        } else 0L

        val timeFormatter = SimpleDateFormat("hh:mm", Locale.getDefault())
        val suffix = if (targetDate != null) {
            val c = Calendar.getInstance().apply { time = targetDate }
            if (c.get(Calendar.HOUR_OF_DAY) >= 12) "م" else "ص"
        } else ""

        val formattedTime = if (targetDate != null) "${timeFormatter.format(targetDate)} $suffix" else "--:--"

        return NextPrayerLiveInfo(
            prayerName = prayerTitle,
            timeFormatted = formattedTime,
            remainingSeconds = remainingSecs
        )
    }

    companion object {
        val instance = QiblaAndPrayerRepository()
    }
}
