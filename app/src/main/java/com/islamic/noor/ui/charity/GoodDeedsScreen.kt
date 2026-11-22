package com.islamic.noor.ui.charity

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.data.repository.GhirasRepository
import com.islamic.noor.domain.model.CharityProject
import com.islamic.noor.domain.model.GoodDeedDoor
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.EmeraldLight
import com.islamic.noor.ui.theme.GoldChampagne
import com.islamic.noor.ui.theme.GoldDark

@Composable
fun GoodDeedsScreen(
    onBack: () -> Unit = {},
    repository: GhirasRepository = remember { GhirasRepository.instance }
) {
    val deeds by repository.getGoodDeeds().collectAsState(initial = emptyList())
    val projects by repository.charityProjects.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: مشاريع الصدقة، 1: أبواب الأجر
    var dailyReminderEnabled by remember { mutableStateOf(true) }

    // Dialog state for pledging shares
    var selectedProjectForPledge by remember { mutableStateOf<CharityProject?>(null) }
    var donorName by remember { mutableStateOf("") }
    var donorPhone by remember { mutableStateOf("") }
    var sharesCount by remember { mutableIntStateOf(1) }
    var showSuccessToast by remember { mutableStateOf(false) }

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
                        text = "غِراس الخير: مشاريع الصدقة الجارية",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeep
                    )
                    Text(
                        text = "غرسٌ طيب في الدنيا وأجرٌ دائم في الآخرة",
                        style = MaterialTheme.typography.bodySmall,
                        color = GoldDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Navigation Tabs (المشاريع التشاركية vs أبواب الأجر)
        item(key = "tabs") {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldDeep
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "مشاريع الصدقة الجارية",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "أبواب الخير وثوابها",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        if (selectedTab == 0) {
            // Explanatory Banner for Elderly Users
            item(key = "projects_intro") {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldDeep,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = GoldChampagne,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "نحن نتكفل بالتنفيذ الميداني ونوفر لك إمكانية المساهمة بأسهم ميسرة لتنال أجر الصدقة الجارية لك أو لوالديك.",
                            fontSize = 12.sp,
                            color = EmeraldDeep,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Projects List
            items(projects, key = { it.id }) { project ->
                CharityProjectCard(
                    project = project,
                    onPledgeClick = { selectedProjectForPledge = project }
                )
            }
        } else {
            // Reminder Toggle Card
            item(key = "reminder_card") {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GoldChampagne.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = GoldChampagne.copy(alpha = 0.2f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = GoldDark,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "تذكير يومي بعمل صالح",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDeep
                                )
                                Text(
                                    text = "إشعار خفيف يذكرك بباب من أبواب الخير كل صباح",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                                )
                            }
                        }

                        Switch(
                            checked = dailyReminderEnabled,
                            onCheckedChange = { dailyReminderEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = EmeraldDeep
                            )
                        )
                    }
                }
            }

            // Deed Cards with Hadith Evidence
            items(deeds, key = { it.id }) { deed ->
                GoodDeedCard(deed = deed)
            }
        }
    }

    // Pledge Share Dialog
    selectedProjectForPledge?.let { proj ->
        AlertDialog(
            onDismissRequest = { selectedProjectForPledge = null },
            title = {
                Text(
                    text = "حجز سهم في ${proj.title}",
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDeep
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "قيمة السهم الواحد: ${proj.sharePrice} ج.م",
                        fontWeight = FontWeight.SemiBold,
                        color = GoldDark,
                        fontSize = 13.sp
                    )

                    OutlinedTextField(
                        value = donorName,
                        onValueChange = { donorName = it },
                        label = { Text("الاسم الكريم (أو فاعل خير)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = donorPhone,
                        onValueChange = { donorPhone = it },
                        label = { Text("رقم الهاتف للتواصل والتأكيد") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "عدد الأسهم:", fontSize = 13.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = { if (sharesCount > 1) sharesCount-- },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldLight),
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("-", color = EmeraldDeep, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "$sharesCount", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = { sharesCount++ },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }
                    }

                    Text(
                        text = "المبلغ الإجمالي للمساهمة: ${sharesCount * proj.sharePrice} ج.م",
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeep,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.pledgeShare(proj.id, sharesCount)
                        selectedProjectForPledge = null
                        showSuccessToast = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
                ) {
                    Text("تأكيد المساهمة", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedProjectForPledge = null }) {
                    Text("إلغاء", color = Color.Gray)
                }
            }
        )
    }

    if (showSuccessToast) {
        AlertDialog(
            onDismissRequest = { showSuccessToast = false },
            title = {
                Text("تقبل الله طاعتكم", fontWeight = FontWeight.Bold, color = EmeraldDeep)
            },
            text = {
                Text("تم تسجيل مساهمتكم بنجاح وسيتواصل معكم فريق غِراس لإتمام الإجراء وإرسال توثيق التنفيذ بإذن الله.")
            },
            confirmButton = {
                Button(
                    onClick = { showSuccessToast = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
                ) {
                    Text("تم", color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun CharityProjectCard(
    project: CharityProject,
    onPledgeClick: () -> Unit
) {
    val progress = (project.collectedAmount.toFloat() / project.targetAmount.toFloat()).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp),
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
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (project.category.contains("ماء")) Icons.Default.WaterDrop else Icons.Default.Mosque,
                                contentDescription = null,
                                tint = GoldDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = project.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDeep
                        )
                        Text(
                            text = project.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldLight
                ) {
                    Text(
                        text = "$percentage%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeep,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = project.description,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = EmeraldDeep,
                trackColor = Color.LightGray.copy(alpha = 0.35f),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "تم جمع: ${project.collectedAmount} ج.م",
                    fontSize = 11.sp,
                    color = EmeraldDeep,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "الهدف: ${project.targetAmount} ج.م",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "السهم: ${project.sharePrice} ج.م",
                    fontWeight = FontWeight.Bold,
                    color = GoldDark,
                    fontSize = 13.sp
                )

                Button(
                    onClick = onPledgeClick,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("حجز سهم الآن", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun GoodDeedCard(deed: GoodDeedDoor) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = EmeraldDeep.copy(alpha = 0.1f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = EmeraldDeep,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = deed.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeep
                    )
                    Text(
                        text = deed.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "فضل هذا العمل وثوابه:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = deed.rewardHadith,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = deed.source,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
