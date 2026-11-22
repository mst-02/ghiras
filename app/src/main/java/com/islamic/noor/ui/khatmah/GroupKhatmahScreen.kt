package com.islamic.noor.ui.khatmah

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.data.repository.GhirasRepository
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.EmeraldLight
import com.islamic.noor.ui.theme.GoldChampagne
import com.islamic.noor.ui.theme.GoldDark

@Composable
fun GroupKhatmahScreen(
    onBack: () -> Unit = {},
    repository: GhirasRepository = remember { GhirasRepository.instance }
) {
    val khatmahs by repository.getGroupKhatmahs().collectAsState(initial = emptyList())
    val activeKhatmah = khatmahs.firstOrNull()

    // 30 Juz status: 0 = unassigned, 1 = claimed by user, 2 = completed by group
    val juzStatus = remember {
        mutableStateMapOf<Int, Int>().apply {
            // Pre-seed some completed parts for realism
            for (i in 1..18) {
                this[i] = 2 // completed
            }
            this[19] = 1 // claimed by current user
            for (i in 20..30) {
                this[i] = 0 // available
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item(key = "header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع",
                        tint = EmeraldDeep
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "الختمة الجماعية التشاركية",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeep
                    )
                    Text(
                        text = "شارك الأهل والأحباب في ختم كتاب الله ونيل البركة معاً",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                }
            }
        }

        // Active Khatmah Progress Card
        item(key = "progress_card") {
            val completedCount = juzStatus.values.count { it == 2 }
            val progress = completedCount.toFloat() / 30f

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = GoldChampagne.copy(alpha = 0.2f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = GoldDark,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = activeKhatmah?.title ?: "ختمة الأهل والأحباب",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDeep
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldLight
                        ) {
                            Text(
                                text = "الموعد: ${activeKhatmah?.deadlineDate ?: "الجمعة"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldDeep,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "تم إنجاز $completedCount من 30 جزءاً",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = EmeraldDeep,
                        trackColor = Color.LightGray.copy(alpha = 0.35f),
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }

        // Instructions for Senior Users
        item(key = "instructions") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(color = EmeraldDeep, label = "تمت القراءة")
                LegendItem(color = GoldDark, label = "قيد قراءتي")
                LegendItem(color = Color.LightGray, label = "متاح للحجز")
            }
        }

        // 30 Juz Interactive Grid
        item(key = "juz_grid") {
            Column {
                Text(
                    text = "اختر الجزء لقراءته أو تأكيد إتمامه:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDeep,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Render 30 parts in rows of 5 for elderly accessibility
                for (row in 0 until 6) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (col in 0 until 5) {
                            val juzNumber = row * 5 + col + 1
                            val status = juzStatus[juzNumber] ?: 0

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when (status) {
                                            2 -> EmeraldDeep
                                            1 -> GoldDark
                                            else -> MaterialTheme.colorScheme.surface
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        when (status) {
                                            2 -> EmeraldDeep
                                            1 -> GoldDark
                                            else -> Color.LightGray.copy(alpha = 0.5f)
                                        },
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        // Cycle status: 0 -> 1 -> 2 -> 0
                                        juzStatus[juzNumber] = when (status) {
                                            0 -> 1
                                            1 -> 2
                                            else -> 0
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "جزء $juzNumber",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (status == 0) MaterialTheme.colorScheme.onSurface else Color.White
                                    )
                                    if (status == 2) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(color, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
        )
    }
}
