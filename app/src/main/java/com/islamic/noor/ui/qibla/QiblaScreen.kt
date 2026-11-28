package com.islamic.noor.ui.qibla

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.data.repository.QiblaAndPrayerRepository
import com.islamic.noor.data.repository.UserRepository
import com.islamic.noor.domain.model.PrayerTimeItem
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.EmeraldLight
import com.islamic.noor.ui.theme.GoldBright
import com.islamic.noor.ui.theme.GoldChampagne
import com.islamic.noor.ui.theme.GoldDark
import com.islamic.noor.ui.theme.titleColor
import kotlin.math.abs

@Composable
fun QiblaScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    repository: QiblaAndPrayerRepository = remember { QiblaAndPrayerRepository() },
    userRepository: UserRepository = remember { UserRepository.instance }
) {
    val context = LocalContext.current
    val userProfile by userRepository.userProfile.collectAsState()
    var showGovDropdown by remember { mutableStateOf(false) }

    val effectiveCoords = remember(userProfile.selectedGovernorate) {
        repository.getEffectiveCoordinates(userProfile.selectedGovernorate)
    }

    val qiblaAngle = remember(effectiveCoords) {
        repository.calculateQiblaAngle(effectiveCoords.latitude, effectiveCoords.longitude)
    }

    val prayerTimes by repository.getPrayerTimesForToday(userProfile.selectedGovernorate, userProfile.iqamaDelayMinutes).collectAsState(initial = emptyList())

    var azimuth by remember { mutableFloatStateOf(0f) }

    fun triggerVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                v?.vibrate(40)
            }
        } catch (_: Exception) {}
    }

    // Sensor listener with smooth low-pass filter
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)

        var smoothedAzimuth = 0f
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                val currentDegrees = if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    Math.toDegrees(orientation[0].toDouble()).toFloat()
                } else {
                    event.values[0]
                }

                val alpha = 0.15f
                smoothedAzimuth += alpha * (currentDegrees - smoothedAzimuth)
                azimuth = (smoothedAzimuth + 360) % 360
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        rotationSensor?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    // Relative angle to Kaaba
    val diff = (qiblaAngle - azimuth + 360) % 360
    val isAligned = diff < 6 || diff > 354

    val needleRotation by animateFloatAsState(
        targetValue = qiblaAngle - azimuth,
        animationSpec = spring(stiffness = 300f),
        label = "qibla_needle"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. High-Contrast Islamic Brass Compass Dial
        item(key = "compass_card") {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, if (isAligned) Color(0xFF2E7D32) else GoldChampagne.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "بوصلة القبلة المشرفة",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = titleColor()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "وجّه الهاتف بحيث يشير السهم الذهبي إلى الكعبة المشرفة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Authentic Islamic Brass Compass Dial
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(260.dp)
                    ) {
                        // Dial Canvas
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2, size.height / 2)
                            val radius = size.minDimension / 2 - 8.dp.toPx()

                            // Outer dark brass disc
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFF22302A), Color(0xFF101714)),
                                    center = center,
                                    radius = radius
                                ),
                                radius = radius,
                                center = center
                            )

                            // Outer gold ring
                            drawCircle(
                                color = if (isAligned) Color(0xFF4CAF50) else GoldChampagne,
                                radius = radius,
                                center = center,
                                style = Stroke(width = 3.5.dp.toPx())
                            )

                            // Inner decorative gold ring
                            drawCircle(
                                color = GoldChampagne.copy(alpha = 0.35f),
                                radius = radius * 0.72f,
                                center = center,
                                style = Stroke(width = 1.dp.toPx())
                            )

                            // Degree tick marks
                            for (deg in 0 until 360 step 15) {
                                val angleRad = Math.toRadians(deg.toDouble())
                                val isCardinal = deg % 90 == 0
                                val startR = radius - (if (isCardinal) 12.dp.toPx() else 6.dp.toPx())
                                val endR = radius - 2.dp.toPx()
                                val startX = center.x + (startR * Math.cos(angleRad)).toFloat()
                                val startY = center.y + (startR * Math.sin(angleRad)).toFloat()
                                val endX = center.x + (endR * Math.cos(angleRad)).toFloat()
                                val endY = center.y + (endR * Math.sin(angleRad)).toFloat()

                                drawLine(
                                    color = if (isCardinal) GoldBright else GoldDark.copy(alpha = 0.6f),
                                    start = Offset(startX, startY),
                                    end = Offset(endX, endY),
                                    strokeWidth = if (isCardinal) 2.5.dp.toPx() else 1.2.dp.toPx()
                                )
                            }
                        }

                        // Cardinal Directions (Arabic: ش، ج، ق، غ)
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "ش (N)",
                                color = Color(0xFFE53935),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 16.dp)
                            )
                            Text(
                                text = "ج (S)",
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 16.dp)
                            )
                            Text(
                                text = "ق (E)",
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 16.dp)
                            )
                            Text(
                                text = "غ (W)",
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 16.dp)
                            )
                        }

                        // Rotating Pointer Needle
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(needleRotation),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val center = Offset(size.width / 2, size.height / 2)
                                val needleLength = size.minDimension * 0.38f
                                val needleWidth = 14.dp.toPx()

                                // Top Arrowhead pointing to Kaaba
                                val topNeedle = Path().apply {
                                    moveTo(center.x, center.y - needleLength)
                                    lineTo(center.x + needleWidth / 2f, center.y)
                                    lineTo(center.x - needleWidth / 2f, center.y)
                                    close()
                                }
                                drawPath(
                                    path = topNeedle,
                                    brush = Brush.linearGradient(
                                        colors = if (isAligned)
                                            listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
                                        else
                                            listOf(GoldBright, GoldDark)
                                    )
                                )

                                // Bottom Arrowhead
                                val bottomNeedle = Path().apply {
                                    moveTo(center.x, center.y + needleLength * 0.65f)
                                    lineTo(center.x + needleWidth / 2.5f, center.y)
                                    lineTo(center.x - needleWidth / 2.5f, center.y)
                                    close()
                                }
                                drawPath(
                                    path = bottomNeedle,
                                    color = Color.White.copy(alpha = 0.45f)
                                )

                                // Center Pivot Jewel
                                drawCircle(
                                    color = if (isAligned) Color(0xFF4CAF50) else GoldBright,
                                    radius = 8.dp.toPx(),
                                    center = center
                                )
                                drawCircle(
                                    color = Color(0xFF1B2420),
                                    radius = 4.dp.toPx(),
                                    center = center
                                )
                            }

                            // Kaaba Marker Badge at top of the needle
                            Surface(
                                shape = CircleShape,
                                color = if (isAligned) Color(0xFF2E7D32) else GoldDark,
                                border = androidx.compose.foundation.BorderStroke(2.dp, GoldBright),
                                modifier = Modifier
                                    .size(36.dp)
                                    .offset(y = (-95).dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Mosque,
                                        contentDescription = "الكعبة المشرفة",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Alignment Status Banner
                    AnimatedVisibility(visible = isAligned) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF2E7D32).copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32)),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "أنت الآن في اتجاه القبلة المشرفة تماماً",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Text(
                        text = "زاوية الهاتف الحالية: ${azimuth.toInt()}° • القبلة: ${qiblaAngle.toInt()}°",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = titleColor()
                    )
                }
            }
        }

        // 2. Today's Prayer Timetable
        item(key = "prayer_times_header") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp, 18.dp)
                        .background(GoldChampagne, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "مواقيت صلاة اليوم",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = titleColor()
                )
            }
        }

        items(prayerTimes, key = { it.nameArabic }) { prayer ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (prayer.isNext) (if (com.islamic.noor.ui.theme.isDarkThemeActive()) EmeraldDeep else EmeraldLight) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = if (prayer.isNext) androidx.compose.foundation.BorderStroke(1.5.dp, GoldBright) else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (prayer.isNext) GoldChampagne else EmeraldLight,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Mosque,
                                    contentDescription = null,
                                    tint = if (prayer.isNext) EmeraldDeep else EmeraldDeep,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = prayer.nameArabic,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (prayer.isNext) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            if (prayer.iqamaDelayMinutes > 0) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "الإقامة بعد ${prayer.iqamaDelayMinutes} دقيقة",
                                        fontSize = 11.sp,
                                        color = if (prayer.isNext) GoldChampagne else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = prayer.timeFormatted,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (prayer.isNext) GoldChampagne else titleColor()
                    )
                }
            }
        }
    }
}
