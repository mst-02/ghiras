package com.islamic.noor.ui.khatmah

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.data.repository.GhirasRepository
import com.islamic.noor.data.repository.UserRepository
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.EmeraldLight
import com.islamic.noor.ui.theme.EmeraldMedium
import com.islamic.noor.ui.theme.GoldBright
import com.islamic.noor.ui.theme.GoldChampagne
import com.islamic.noor.ui.theme.GoldDark
import com.islamic.noor.ui.theme.titleColor

data class KhatmahFriend(
    val id: Int,
    val name: String,
    val juzAssigned: Int,
    val progressPercent: Int,
    val isCurrentUser: Boolean = false
)

@Composable
fun GroupKhatmahScreen(
    onBack: () -> Unit = {},
    onOpenQuran: (Int) -> Unit = {},
    repository: GhirasRepository = remember { GhirasRepository.instance },
    userRepository: UserRepository = remember { UserRepository.instance }
) {
    val context = LocalContext.current
    val khatmahs by repository.getGroupKhatmahs().collectAsState(initial = emptyList())
    val userProfile by userRepository.userProfile.collectAsState()
    val activeKhatmah = khatmahs.firstOrNull()

    // 30 Juz status: 0 = unassigned, 1 = claimed by user, 2 = completed by group
    val juzStatus = remember {
        mutableStateMapOf<Int, Int>().apply {
            for (i in 1..18) {
                this[i] = 2 // completed
            }
            this[19] = 1 // claimed by current user
            for (i in 20..30) {
                this[i] = 0 // available
            }
        }
    }

    // Friends list participating in this Khatmah
    val friendsList = remember {
        mutableStateListOf(
            KhatmahFriend(1, if (userProfile.name.isNotBlank()) userProfile.name else "أنت", 19, 70, isCurrentUser = true),
            KhatmahFriend(2, "أحمد عبد الله", 1, 100),
            KhatmahFriend(3, "محمود حسن", 2, 100),
            KhatmahFriend(4, "إبراهيم خليل", 3, 45),
            KhatmahFriend(5, "يوسف إبراهيم", 4, 100)
        )
    }

    var showAddFriendDialog by remember { mutableStateOf(false) }
    var newFriendName by remember { mutableStateOf("") }
    var newFriendJuz by remember { mutableIntStateOf(20) }

    val completedCount = juzStatus.values.count { it == 2 }
    val progress = (completedCount.toFloat() / 30f).coerceIn(0f, 1f)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Card
        item(key = "header_card") {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = GoldChampagne.copy(alpha = 0.2f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = GoldDark,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = activeKhatmah?.title ?: "ختمة الأهل والأحباب",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = titleColor()
                                )
                                Text(
                                    text = "موعد الختم: ${activeKhatmah?.deadlineDate ?: "يوم الجمعة المباركة"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "تم إنجاز $completedCount من 30 جزءاً",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = titleColor()
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = GoldDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EmeraldDeep,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }

        // 2. Direct Resume Reading Button for the User's Active Part
        item(key = "resume_reading_button") {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = EmeraldDeep,
                border = BorderStroke(1.2.dp, GoldBright),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        onOpenQuran(userProfile.lastReadSurahId)
                        Toast.makeText(context, "جاري فتح المصحف الشريف لمتابعة القراءة...", Toast.LENGTH_SHORT).show()
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = GoldBright, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "استكمال قراءة الجزء قيد القراءة",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "سورة ${userProfile.lastReadSurahName} • الآية ${userProfile.lastReadVerseNumber}",
                                color = GoldChampagne,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = "فتح المصحف ←",
                        color = GoldBright,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 3. Friends Participation Section (الأصدقاء المشاركون ومتابعة تقدمهم)
        item(key = "friends_section_header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(4.dp, 16.dp)
                            .background(GoldChampagne, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "الأصدقاء المشاركون في الختمة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = titleColor()
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldChampagne.copy(alpha = 0.2f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showAddFriendDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = GoldDark, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة صديق", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldDark)
                    }
                }
            }
        }

        items(friendsList, key = { it.id }) { friend ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, if (friend.isCurrentUser) GoldBright else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (friend.isCurrentUser) GoldChampagne else EmeraldLight,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (friend.isCurrentUser) GoldDark else EmeraldDeep,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = friend.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = titleColor()
                            )
                            Text(
                                text = "مكلف بـ: الجزء ${friend.juzAssigned}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (friend.progressPercent == 100) Color(0xFF2E7D32).copy(alpha = 0.15f) else GoldChampagne.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (friend.progressPercent == 100) "مكتمل ✓" else "${friend.progressPercent}% قيد القراءة",
                            color = if (friend.progressPercent == 100) Color(0xFF2E7D32) else GoldDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 4. Instructions & Legend
        item(key = "legend") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(color = EmeraldDeep, label = "تمت القراءة")
                LegendItem(color = GoldDark, label = "قيد قراءتي")
                LegendItem(color = MaterialTheme.colorScheme.surfaceVariant, label = "متاح للحجز")
            }
        }

        // 5. 30 Juz Interactive Grid
        item(key = "juz_grid") {
            Column {
                Text(
                    text = "أجزاء المصحف الشريف (انقر لتحديد أو تأكيد الجزء):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = titleColor(),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Render 30 parts in rows of 5
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
                                            1 -> GoldBright
                                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                        },
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        // Cycle status: 0 (متاح) -> 1 (قيد قراءتي) -> 2 (تمت القراءة) -> 0
                                        val nextSt = when (status) {
                                            0 -> 1
                                            1 -> 2
                                            else -> 0
                                        }
                                        juzStatus[juzNumber] = nextSt
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "جزء $juzNumber",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (status == 0) MaterialTheme.colorScheme.onSurface else Color.White
                                    )
                                    if (status == 2) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    } else if (status == 1) {
                                        Text("قيد القراءة", fontSize = 9.sp, color = GoldChampagne, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Friend to Khatmah Dialog
    if (showAddFriendDialog) {
        AlertDialog(
            onDismissRequest = { showAddFriendDialog = false },
            title = {
                Text("إضافة صديق للختمة", fontWeight = FontWeight.Bold, color = titleColor())
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newFriendName,
                        onValueChange = { newFriendName = it },
                        label = { Text("اسم الصديق أو القريب") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = "$newFriendJuz",
                        onValueChange = { newFriendJuz = it.toIntOrNull() ?: 1 },
                        label = { Text("رقم الجزء المخصص له (1-30)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFriendName.isNotBlank()) {
                            friendsList.add(
                                KhatmahFriend(
                                    id = friendsList.size + 1,
                                    name = newFriendName.trim(),
                                    juzAssigned = newFriendJuz.coerceIn(1, 30),
                                    progressPercent = 0
                                )
                            )
                            juzStatus[newFriendJuz.coerceIn(1, 30)] = 1
                            showAddFriendDialog = false
                            Toast.makeText(context, "تمت إضافة الصديق للختمة بنجاح ✨", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
                ) {
                    Text("إضافة", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFriendDialog = false }) {
                    Text("إلغاء", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
        )
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
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
        )
    }
}
