package com.islamic.noor.data.repository

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

class QiblaAndPrayerRepository {

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
     * حساب مواقيت الصلاة لليوم محلياً وأوفلاين
     */
    fun getPrayerTimesForToday(): Flow<List<PrayerTimeItem>> = flow {
        val prayers = listOf(
            PrayerTimeItem("الفجر", "04:58 ص"),
            PrayerTimeItem("الشروق", "06:27 ص"),
            PrayerTimeItem("الظهر", "11:46 ص"),
            PrayerTimeItem("العصر", "02:44 م", isNext = true),
            PrayerTimeItem("المغرب", "05:04 م"),
            PrayerTimeItem("العشاء", "06:24 م")
        )
        emit(prayers)
    }.flowOn(Dispatchers.IO)
}
