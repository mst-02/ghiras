package com.islamic.noor.ui.contest

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.noor.data.repository.ContestRepository
import com.islamic.noor.data.repository.LocalMockContestRepository
import com.islamic.noor.data.repository.UserRepository
import com.islamic.noor.domain.model.ContestQuestion
import com.islamic.noor.domain.model.ContestSubmission
import com.islamic.noor.domain.model.ContestWinner
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.EmeraldLight
import com.islamic.noor.ui.theme.EmeraldMedium
import com.islamic.noor.ui.theme.GoldBright
import com.islamic.noor.ui.theme.GoldChampagne
import com.islamic.noor.ui.theme.GoldDark
import com.islamic.noor.ui.theme.buttonAdaptiveColor
import com.islamic.noor.ui.theme.titleColor
import kotlinx.coroutines.launch

@Composable
fun ContestScreen(
    modifier: Modifier = Modifier,
    repository: ContestRepository = remember { LocalMockContestRepository() },
    userRepository: UserRepository = remember { UserRepository.instance }
) {
    val coroutineScope = rememberCoroutineScope()
    val userProfile by userRepository.userProfile.collectAsState()

    var question by remember {
        mutableStateOf(
            ContestQuestion(
                id = "week_1",
                title = "مسابقة الأسبوع",
                questionText = "ما هي السورة الكريمة التي تُلقب بـ «عروس القرآن»؟",
                options = listOf("سورة يس", "سورة الرحمن", "سورة الملك"),
                deadlineDate = "تنتهي الجمعة القادمة",
                prizeAmount = "جائزة نقدية أسبوعية"
            )
        )
    }
    val winners by repository.getPreviousWinners().collectAsState(initial = emptyList())

    var selectedOptionIndex by remember { mutableIntStateOf(-1) }
    var isSubmitting by remember { mutableStateOf(false) }
    var isSubmittedSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Dialogs
    var showInfoDialog by remember { mutableStateOf(false) }
    var showLoginPromptDialog by remember { mutableStateOf(false) }
    var showAdminEditDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header with Info (i) Icon
        item(key = "contest_top_header") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = question.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = titleColor()
                        )
                        Text(
                            text = question.deadlineDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Admin badge if logged in as Admin
                        if (userProfile.isAdmin) {
                            IconButton(onClick = { showAdminEditDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "لوحة التحكم",
                                    tint = GoldDark
                                )
                            }
                        }

                        // Info Icon (i)
                        IconButton(onClick = { showInfoDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "معلومات المسابقة",
                                tint = EmeraldMedium,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Question & Options Card
        item(key = "question_body") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "السؤال الديني:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = question.questionText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 26.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Options
                    question.options.forEachIndexed { index, option ->
                        val isSelected = selectedOptionIndex == index
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) EmeraldLight else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedOptionIndex = index }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedOptionIndex = index },
                                    colors = RadioButtonDefaults.colors(selectedColor = GoldBright)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) titleColor() else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Direct "إرسال الإجابة" button
                    if (!isSubmittedSuccess) {
                        Button(
                            onClick = {
                                if (selectedOptionIndex == -1) {
                                    errorMessage = "يرجى اختيار إجابة أولاً"
                                    return@Button
                                }
                                errorMessage = null

                                // Check if user is logged in
                                if (!userProfile.isLoggedIn) {
                                    showLoginPromptDialog = true
                                    return@Button
                                }

                                // User is logged in: submit immediately
                                isSubmitting = true
                                coroutineScope.launch {
                                    val submission = ContestSubmission(
                                        questionId = question.id,
                                        userName = userProfile.name,
                                        userPhone = userProfile.phone,
                                        selectedOptionIndex = selectedOptionIndex
                                    )
                                    val result = repository.submitAnswer(submission)
                                    isSubmitting = false
                                    if (result.isSuccess) {
                                        isSubmittedSuccess = true
                                        userRepository.incrementContestsJoined()
                                    } else {
                                        errorMessage = result.exceptionOrNull()?.message ?: "حدث خطأ أثناء الإرسال"
                                    }
                                }
                            },
                            enabled = !isSubmitting,
                            colors = ButtonDefaults.buttonColors(containerColor = buttonAdaptiveColor()),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                            } else {
                                Text(
                                    text = "إرسال الإجابة والمشاركة بالقرعة",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    } else {
                        // Success Confirmation
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = GoldBright,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "تم استلام إجابتك بنجاح",
                                    fontWeight = FontWeight.Bold,
                                    color = titleColor(),
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "تم تسجيلك في سحب مسابقة هذا الأسبوع.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Previous Winners Section
        item(key = "winners_title") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = GoldDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "سجل الفائزين السابقين",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = titleColor()
                )
            }
        }

        items(
            items = winners,
            key = { it.weekNumber }
        ) { winner ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = winner.winnerName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = titleColor()
                        )
                        Text(
                            text = "${winner.weekNumber} • تم التسليم",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    Text(
                        text = winner.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    )
                }
            }
        }
    }

    // Modal 1: Info (i) dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Text(
                    text = "معلومات وقواعد المسابقة",
                    fontWeight = FontWeight.Bold,
                    color = titleColor()
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• المشاركة في المسابقة مجانية تماماً لجميع مستخدمي التطبيق دون أي رسوم.")
                    Text("• يتم طرح سؤال ديني أسبوعياً وتحديثه كل يوم جمعة.")
                    Text("• يتم إجراء قرعة إلكترونية عادلة وعشوائية بين جميع المشاركين.")
                    Text("• تتواصل إدارة التطبيق مع الفائز لتسليمه الجائزة النقدية مباشرة.")
                    Text("• شركة Google ليست راعياً أو طرفاً في المسابقة بأي شكل.")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = buttonAdaptiveColor())
                ) {
                    Text("حسناً", color = Color.White)
                }
            }
        )
    }

    // Modal 2: Fast Login / Admin Login Dialog
    if (showLoginPromptDialog) {
        var inputName by remember { mutableStateOf("") }
        var inputPhoneOrPass by remember { mutableStateOf("") }
        var loginError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showLoginPromptDialog = false },
            title = {
                Text(
                    text = "تسجيل الدخول للمشاركة",
                    fontWeight = FontWeight.Bold,
                    color = GoldBright
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "يرجى إدخال اسمك ورقم هاتفك لحفظ مشاركتك في القرعة:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )

                    OutlinedTextField(
                        value = inputName,
                        onValueChange = { inputName = it },
                        label = { Text("الاسم (أو اسم المستخدم)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldChampagne,
                            focusedLabelColor = GoldChampagne
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = inputPhoneOrPass,
                        onValueChange = { inputPhoneOrPass = it },
                        label = { Text("رقم الهاتف (أو كلمة المرور)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldChampagne,
                            focusedLabelColor = GoldChampagne
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (loginError != null) {
                        Text(loginError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }

                    Text(
                        text = "ملاحظة: للأدمن، اكتب admin وكلمة المرور 1234",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldChampagne.copy(alpha = 0.85f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Check if admin login
                        if (inputName.trim().lowercase() == "admin") {
                            val isAdminOk = userRepository.loginAdmin(inputName, inputPhoneOrPass)
                            if (isAdminOk) {
                                showLoginPromptDialog = false
                            } else {
                                loginError = "كلمة مرور الأدمن غير صحيحة"
                            }
                            return@Button
                        }

                        // Normal user login
                        if (inputName.isBlank() || inputPhoneOrPass.isBlank()) {
                            loginError = "يرجى إدخال الاسم ورقم الهاتف"
                            return@Button
                        }

                        userRepository.loginUser(inputName, inputPhoneOrPass)
                        showLoginPromptDialog = false

                        // Submit answer now that user is logged in
                        isSubmitting = true
                        coroutineScope.launch {
                            val submission = ContestSubmission(
                                questionId = question.id,
                                userName = inputName,
                                userPhone = inputPhoneOrPass,
                                selectedOptionIndex = selectedOptionIndex
                            )
                            val result = repository.submitAnswer(submission)
                            isSubmitting = false
                            if (result.isSuccess) {
                                isSubmittedSuccess = true
                                userRepository.incrementContestsJoined()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
                ) {
                    Text("دخول وإرسال الإجابة", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginPromptDialog = false }) {
                    Text("إلغاء", color = GoldChampagne, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Modal 3: Admin Question Editor Dialog
    if (showAdminEditDialog) {
        var editQText by remember { mutableStateOf(question.questionText) }
        var opt1 by remember { mutableStateOf(question.options.getOrNull(0) ?: "") }
        var opt2 by remember { mutableStateOf(question.options.getOrNull(1) ?: "") }
        var opt3 by remember { mutableStateOf(question.options.getOrNull(2) ?: "") }

        AlertDialog(
            onDismissRequest = { showAdminEditDialog = false },
            title = { Text("لوحة تحكم الأدمن: تعديل السؤال", fontWeight = FontWeight.Bold, color = titleColor()) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editQText,
                        onValueChange = { editQText = it },
                        label = { Text("نص السؤال الديني") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = opt1,
                        onValueChange = { opt1 = it },
                        label = { Text("الخيار الأول") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = opt2,
                        onValueChange = { opt2 = it },
                        label = { Text("الخيار الثاني") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = opt3,
                        onValueChange = { opt3 = it },
                        label = { Text("الخيار الثالث") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        question = question.copy(
                            questionText = editQText,
                            options = listOf(opt1, opt2, opt3)
                        )
                        showAdminEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = buttonAdaptiveColor())
                ) {
                    Text("حفظ السؤال الجديد", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminEditDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ContestScreenPreview() {
    com.islamic.noor.ui.theme.NoorTheme {
        ContestScreen()
    }
}
