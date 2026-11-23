package com.islamic.noor.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.islamic.noor.ui.theme.EmeraldDeep
import com.islamic.noor.ui.theme.EmeraldMedium
import com.islamic.noor.ui.theme.GoldBright
import com.islamic.noor.ui.theme.GoldChampagne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

data class AppReleaseInfo(
    val tagName: String,
    val versionName: String,
    val title: String,
    val changelog: String,
    val downloadUrl: String,
    val apkSizeMb: Double
)

object AppUpdateManager {
    private const val GITHUB_REPO = "mst-02/ghiras"
    private const val RELEASES_API = "https://api.github.com/repos/$GITHUB_REPO/releases/latest"

    /**
     * Checks GitHub for a release newer than [currentVersionName].
     */
    suspend fun checkLatestRelease(currentVersionName: String): AppReleaseInfo? = withContext(Dispatchers.IO) {
        try {
            val url = URL(RELEASES_API)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 7000
                readTimeout = 7000
                setRequestProperty("Accept", "application/vnd.github.v3+json")
                setRequestProperty("User-Agent", "Ghiras-App-Updater")
            }

            if (connection.responseCode == 200) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(responseText)

                val tagName = json.optString("tag_name", "")
                val releaseTitle = json.optString("name", "تحديث جديد")
                val body = json.optString("body", "تحسينات عامة وإصلاحات لتطبيق غِراس.")

                val cleanRemoteVersion = tagName.removePrefix("v").trim()
                val cleanLocalVersion = currentVersionName.removePrefix("v").trim()

                if (isNewerVersion(cleanRemoteVersion, cleanLocalVersion)) {
                    // Find APK asset
                    val assets = json.optJSONArray("assets")
                    if (assets != null && assets.length() > 0) {
                        for (i in 0 until assets.length()) {
                            val asset = assets.getJSONObject(i)
                            val assetName = asset.optString("name", "")
                            if (assetName.endsWith(".apk", ignoreCase = true)) {
                                val downloadUrl = asset.optString("browser_download_url", "")
                                val sizeBytes = asset.optLong("size", 0L)
                                val sizeMb = if (sizeBytes > 0) sizeBytes / (1024.0 * 1024.0) else 17.0

                                return@withContext AppReleaseInfo(
                                    tagName = tagName,
                                    versionName = cleanRemoteVersion,
                                    title = releaseTitle,
                                    changelog = body,
                                    downloadUrl = downloadUrl,
                                    apkSizeMb = sizeMb
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }

    /**
     * Semver comparison: returns true if [remote] > [local]
     */
    fun isNewerVersion(remote: String, local: String): Boolean {
        try {
            val remoteParts = remote.split(".").map { it.filter { char -> char.isDigit() }.toIntOrNull() ?: 0 }
            val localParts = local.split(".").map { it.filter { char -> char.isDigit() }.toIntOrNull() ?: 0 }

            val maxLen = maxOf(remoteParts.size, localParts.size)
            for (i in 0 until maxLen) {
                val r = remoteParts.getOrElse(i) { 0 }
                val l = localParts.getOrElse(i) { 0 }
                if (r > l) return true
                if (r < l) return false
            }
        } catch (_: Exception) {
            return remote != local
        }
        return false
    }

    /**
     * Downloads the APK file with follow-redirects support and opens Android Package Installer.
     */
    suspend fun downloadAndInstallApk(
        context: Context,
        downloadUrl: String,
        onProgress: (Float) -> Unit,
        onComplete: () -> Unit,
        onError: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            val updateDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.cacheDir, "updates")
            if (!updateDir.exists()) updateDir.mkdirs()

            val apkFile = File(updateDir, "ghiras-update.apk")
            if (apkFile.exists()) apkFile.delete()

            // Follow HTTP redirects (GitHub releases redirect to AWS S3 / fastly)
            var currentUrl = downloadUrl
            var connection: HttpURLConnection
            var redirects = 0
            while (true) {
                val url = URL(currentUrl)
                connection = (url.openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = false
                    connectTimeout = 12000
                    readTimeout = 20000
                    setRequestProperty("User-Agent", "Ghiras-App-Updater")
                }
                val status = connection.responseCode
                if (status == HttpURLConnection.HTTP_MOVED_TEMP ||
                    status == HttpURLConnection.HTTP_MOVED_PERM ||
                    status == HttpURLConnection.HTTP_SEE_OTHER ||
                    status == 307 || status == 308
                ) {
                    currentUrl = connection.getHeaderField("Location")
                    redirects++
                    if (redirects > 6) throw IllegalStateException("Too many redirects")
                    continue
                }
                break
            }

            if (connection.responseCode != 200) {
                withContext(Dispatchers.Main) {
                    onError("تعذر الاتصال بخادم التحديث (${connection.responseCode})")
                }
                return@withContext
            }

            val totalBytes = connection.contentLength.toFloat()
            var downloadedBytes = 0L

            val input: InputStream = connection.inputStream
            val output = FileOutputStream(apkFile)

            val buffer = ByteArray(8 * 1024)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
                downloadedBytes += bytesRead
                if (totalBytes > 0) {
                    val progress = (downloadedBytes / totalBytes).coerceIn(0f, 1f)
                    withContext(Dispatchers.Main) {
                        onProgress(progress)
                    }
                }
            }

            output.flush()
            output.close()
            input.close()

            withContext(Dispatchers.Main) {
                onProgress(1f)
                onComplete()
                triggerApkInstall(context, apkFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onError(e.localizedMessage ?: "حدث خطأ أثناء تنزيل التحديث")
            }
        }
    }

    /**
     * Triggers the Android system package installer for the downloaded APK.
     */
    fun triggerApkInstall(context: Context, apkFile: File) {
        try {
            val apkUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(installIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback for Android 8+ unknown source permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !context.packageManager.canRequestPackageInstalls()) {
                val permissionIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(permissionIntent)
            }
        }
    }
}

/**
 * Islamic luxury update dialog.
 */
@Composable
fun GhirasUpdateDialog(
    releaseInfo: AppReleaseInfo,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isFinished by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { if (!isDownloading) onDismiss() }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Icon
                Surface(
                    shape = CircleShape,
                    color = EmeraldMedium.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldBright),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SystemUpdate,
                            contentDescription = null,
                            tint = GoldBright,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "تحديث جديد لغِراس متوفر!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "الإصدار الجديد: ${releaseInfo.tagName} (${String.format("%.1f", releaseInfo.apkSizeMb)} ميجابايت)",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldChampagne,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Changelog Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "أبرز التحسينات الجديدة:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = releaseInfo.changelog,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Downloading Progress
                if (isDownloading) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LinearProgressIndicator(
                            progress = { downloadProgress },
                            color = GoldBright,
                            trackColor = EmeraldDeep.copy(alpha = 0.3f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isFinished) "اكتمل التنزيل، جاري فتح شاشة التثبيت..." else "جاري تنزيل التحديث: ${(downloadProgress * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                if (!isDownloading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("لاحقاً", fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                isDownloading = true
                                errorMessage = null
                                coroutineScope.launch {
                                    AppUpdateManager.downloadAndInstallApk(
                                        context = context,
                                        downloadUrl = releaseInfo.downloadUrl,
                                        onProgress = { downloadProgress = it },
                                        onComplete = {
                                            isFinished = true
                                        },
                                        onError = {
                                            isDownloading = false
                                            errorMessage = it
                                        }
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = GoldBright,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تحديث الآن", fontSize = 13.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
