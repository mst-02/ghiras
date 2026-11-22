param(
    [string]$RepoUrl
)

if (-not $RepoUrl) {
    Write-Host "==========================================================" -ForegroundColor Cyan
    Write-Host "  غِراس (Ghiras) - أداة الربط والرفع السريع إلى GitHub  " -ForegroundColor Gold
    Write-Host "==========================================================" -ForegroundColor Cyan
    $RepoUrl = Read-Host "أدخل رابط مستودع GitHub الخاص بك (مثال: https://github.com/USERNAME/ghiras.git)"
}

if (-not $RepoUrl) {
    Write-Host "لم يتم إدخال رابط المستودع. تم الإلغاء." -ForegroundColor Red
    exit 1
}

Write-Host "جاري فحص إعدادات Git..." -ForegroundColor Yellow
$remotes = git remote

if ($remotes -contains "origin") {
    Write-Host "تحديث رابط origin..." -ForegroundColor Cyan
    git remote set-url origin $RepoUrl
} else {
    Write-Host "إضافة رابط origin..." -ForegroundColor Cyan
    git remote add origin $RepoUrl
}

Write-Host "تأكيد فرع main..." -ForegroundColor Cyan
git branch -M main

Write-Host "رفع التغييرات إلى GitHub..." -ForegroundColor Green
git push -u origin main

if ($LASTEXITCODE -eq 0) {
    Write-Host "`nتم رفع وتحديث المشروع على GitHub بنجاح تام! 🎉" -ForegroundColor Green
    Write-Host "لأي تحديث قادم، كل ما عليك هو كتابة: git push" -ForegroundColor Cyan
} else {
    Write-Host "`nتنبيه: تأكد من إنشاء مستودع فارغ على حسابك، وتسجيل الدخول في Git." -ForegroundColor Yellow
}
