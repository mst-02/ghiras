package com.islamic.noor.ui.qibla

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    repository: QiblaAndPrayerRepository = remember { QiblaAndPrayerRepository() }
) {
    val context = LocalContext.current
    var azimuth by remember { mutableFloatStateOf(0f) }
    val qiblaAngle = remember { repository.calculateQiblaAngle() } // 136° لمصر تقريباً
    val prayerTimes by repository.getPrayerTimesForToday().collectAsState(initial = emptyList())

    // Sensor listener with low-pass filter
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
    val isAligned = diff < 8 || diff > 352

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
        // Qibla Dial Card
        item(key = "compass_card") {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
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
                        text = "ضع الهاتف مستوياً ودر حتى يشير السهم إلى الكعبة المشرفة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Realistic Brass / Islamic Dial (260dp)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(260.dp)
                    ) {
                        // Dial background canvas
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2, size.height / 2)
                            val radius = size.minDimension / 2 - 8.dp.toPx()

                            // Outer gold ring
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFF22302A), Color(0xFF101714)),
                                    center = center,
                                    radius = radius
                                ),
                                radius = radius,
                                center = center
                            )

                            drawCircle(
                                color = GoldChampagne,
                                radius = radius,
                                center = center,
                                style = Stroke(width = 3.dp.toPx())
                            )

                            // Degree tick marks
                            for (deg in 0 until 360 step 30) {
                                val angleRad = Math.toRadians(deg.toDouble())
                                val startR = radius - 8.dp.toPx()
                                val endR = radius - 2.dp.toPx()
                                val startX = center.x + (startR * Math.cos(angleRad)).toFloat()
                                val startY = center.y + (startR * Math.sin(angleRad)).toFloat()
                                val endX = center.x + (endR * Math.cos(angleRad)).toFloat()
                                val endY = center.y + (endR * Math.sin(angleRad)).toFloat()

                                drawLine(
                                    color = if (deg % 90 == 0) GoldBright else GoldDark.copy(alpha = 0.5f),
                                    start = Offset(startX, startY),
                                    end = Offset(endX, endY),
                                    strokeWidth = if (deg % 90 == 0) 2.5.dp.toPx() else 1.5.dp.toPx()
                                )
                            }
                        }

                        // Cardinal Direction Markers
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "N (ش)",
                                color = Color(0xFFE53935),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 14.dp)
                            )
                            Text(
                                text = "S (ج)",
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 14.dp)
                            )
                            Text(
                                text = "E (ق)",
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 14.dp)
                            )
                            Text(
                                text = "W (غ)",
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 14.dp)
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

                                // Top Arrowhead (Golden / Emerald pointing to Kaaba)
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

                                // Bottom Arrowhead (Silver/Dark tail)
                                val bottomNeedle = Path().apply {
                                    moveTo(center.x, center.y + needleLength * 0.7f)
                                    lineTo(center.x + needleWidth / 2f, center.y)
                                    lineTo(center.x - needleWidth / 2f, center.y)
                                    close()
                                }
                                drawPath(
                                    path = bottomNeedle,
                                    color = Color.LightGray.copy(alpha = 0.5f)
                                )

                                // Center Pivot
                                drawCircle(color = GoldBright, radius = 9.dp.toPx(), center = center)
                                drawCircle(color = EmeraldDeep, radius = 4.dp.toPx(), center = center)
                            }
                        }

                        // Kaaba Icon badge
                        Surface(
                            shape = CircleShape,
                            color = GoldChampagne,
                            modifier = Modifier
                                .size(42.dp)
                                .align(Alignment.Center)
                                .offset(y = (-95).dp)
                                .border(2.dp, EmeraldDeep, CircleShape)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Mosque,
                                    contentDescription = "الكعبة المشرفة",
                                    tint = EmeraldDeep,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Alignment State Banner
                    AnimatedVisibility(visible = isAligned) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF2E7D32).copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "أنت الآن باتجاه القبلة الشريفة تماماً ✓",
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            text = "زاوية القبلة: ${qiblaAngle.toInt()}° (جنوب شرق)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = titleColor()
                        )
                        Text(
                            text = "اتجاه هاتفك: ${azimuth.toInt()}°",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Today's Prayer Times (High-Contrast Guaranteed)
        item(key = "prayer_schedule_title") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp, 16.dp)
                        .background(GoldChampagne, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "مواقيت الصلاة لليوم (القاهرة وضواحيها)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = titleColor()
                )
            }
        }

        items(prayerTimes, key = { it.name }) { prayer ->
            PrayerTimeRowItem(prayer = prayer)
        }
    }
}

@Composable
private fun PrayerTimeRowItem(prayer: PrayerTimeItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (prayer.isActive) EmeraldLight else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = if (prayer.isActive)
            androidx.compose.foundation.BorderStroke(1.5.dp, GoldChampagne)
        else
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = if (prayer.isActive) GoldChampagne else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Mosque,
                            contentDescription = null,
                            tint = if (prayer.isActive) EmeraldDeep else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = prayer.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (prayer.isActive) EmeraldDeep else MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "${prayer.time} ص/م",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (prayer.isActive) EmeraldDeep else GoldDark
            )
        }
    }
}
