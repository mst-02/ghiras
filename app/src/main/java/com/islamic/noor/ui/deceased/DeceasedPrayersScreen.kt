package com.islamic.noor.ui.deceased

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.data.repository.GhirasRepository
import com.islamic.noor.domain.model.DeceasedDua
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.GoldChampagne
import com.islamic.noor.ui.theme.GoldDark

@Composable
fun DeceasedPrayersScreen(
    onBack: () -> Unit = {},
    repository: GhirasRepository = remember { GhirasRepository.instance }
) {
    val duas by repository.getDeceasedDuas().collectAsState(initial = emptyList())
    var deceasedName by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
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
                        text = "أدعية للمتوفين والصدقة الجارية",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeep
                    )
                    Text(
                        text = "«إذا مات الإنسان انقطع عنه عمله إلا من ثلاثة: صدقة جارية، أو علم ينتفع به، أو ولد صالح يدعو له»",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Deceased Name Input Card (Personalization for Seniors)
        item(key = "name_input") {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldChampagne.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = GoldChampagne.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = GoldDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "تخصيص الدعاء باسم فقيدك الغالي",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDeep
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = deceasedName,
                        onValueChange = { deceasedName = it },
                        placeholder = { Text("مثال: أبي الحبيب، أمي الغالية، أو الاسم...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldDeep,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.6f)
                        )
                    )

                    if (deceasedName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "سيتم إدراج «$deceasedName» تلقائياً في نصوص الأدعية بالأسفل بإذن الله.",
                            fontSize = 12.sp,
                            color = EmeraldDeep,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Duas List
        items(duas, key = { it.id }) { dua ->
            DeceasedDuaCard(dua = dua, deceasedName = deceasedName)
        }
    }
}

@Composable
private fun DeceasedDuaCard(dua: DeceasedDua, deceasedName: String) {
    val personalizedText = if (deceasedName.isNotBlank()) {
        dua.text.replace("لَهُ", "لـ $deceasedName")
            .replace("قَبْرَهُ", "قبر $deceasedName")
            .replace("مَدَّ بَصَرِهِ", "مد بصره")
    } else {
        dua.text
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = GoldDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = dua.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeep
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldDeep.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = dua.reference,
                        color = EmeraldDeep,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = personalizedText,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 19.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "مشاركة الدعاء",
                        tint = GoldDark
                    )
                }
            }
        }
    }
}
