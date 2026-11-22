package com.islamic.noor.data.repository

import com.islamic.noor.domain.model.ContestQuestion
import com.islamic.noor.domain.model.ContestSubmission
import com.islamic.noor.domain.model.ContestWinner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface ContestRepository {
    fun getCurrentQuestion(): Flow<ContestQuestion>
    fun getPreviousWinners(): Flow<List<ContestWinner>>
    suspend fun submitAnswer(submission: ContestSubmission): Result<Boolean>
}

/**
 * جاهز للعمل حالياً كـ Mock Data كاملة للتنقل والتجربة بدون أي سيرفر أو تكاليف.
 * وعند الرغبة في ربط سيرفر، يقوم المطور بإنشاء كلاس RemoteContestRepository
 * وتغيير سطر واحد فقط دون المساس بأي شاشة في التطبيق.
 */
class LocalMockContestRepository : ContestRepository {

    override fun getCurrentQuestion(): Flow<ContestQuestion> = flow {
        emit(
            ContestQuestion(
                id = "week_42",
                title = "مسابقة الأسبوع الدينية (المشاركة مجانية)",
                questionText = "ما هي السورة الكريمة التي تُلقب بـ «عروس القرآن»؟",
                options = listOf(
                    "سورة يس",
                    "سورة الرحمن",
                    "سورة الملك",
                    "سورة الكهف"
                ),
                deadlineDate = "الجمعة القادمة الساعة ٩ مساءً",
                prizeAmount = "جائزة مالية (تحويل فوري فودافون كاش / إنستاباي)",
                isWeekly = true,
                termsUrl = "المسابقة مجانية بالكامل، ويتم اختيار الفائز بقرعة إلكترونية عادلة بين أصحاب الإجابات الصحيحة."
            )
        )
    }.flowOn(Dispatchers.IO)

    override fun getPreviousWinners(): Flow<List<ContestWinner>> = flow {
        emit(
            listOf(
                ContestWinner("الأسبوع الماضي", "أحمد محمود كمال (القاهرة)", "تم التحويل عبر إنستاباي", "منذ ٥ أيام"),
                ContestWinner("الأسبوع قبل الماضي", "محمود إبراهيم سيد (الجيزة)", "تم التحويل عبر فودافون كاش", "منذ ١٢ يوماً"),
                ContestWinner("الأسبوع الثالث", "عمر خالد عبد العزيز (الإسكندرية)", "تم التحويل عبر إنستاباي", "منذ ١٩ يوماً")
            )
        )
    }.flowOn(Dispatchers.IO)

    override suspend fun submitAnswer(submission: ContestSubmission): Result<Boolean> {
        // محاكاة إرسال الإجابة وحفظها في الخلفية
        delay(600) // لمحاكاة طلب شبكة سريع
        return if (submission.userName.isNotBlank() && submission.userPhone.isNotBlank()) {
            Result.success(true)
        } else {
            Result.failure(IllegalArgumentException("يرجى إدخال الاسم ورقم الهاتف للمشاركة"))
        }
    }
}
