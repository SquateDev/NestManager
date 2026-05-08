package dev.squatedev.nestmanager.VirtualUtils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.content.pm.SigningInfo
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class Utils(context: Context) {

    private val appContext: Context = context.applicationContext
    private val packageManager: PackageManager = appContext.packageManager
    private val tag: String = "NestManager"

    companion object {
        @Volatile
        private var instance: Utils? = null

        fun getInstance(context: Context): Utils {
            return instance ?: synchronized(this) {
                instance ?: Utils(context).also { instance = it }
            }
        }
    }

    fun getAppInfo(packageName: String): AppInfo? {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            }

            val appInfo: ApplicationInfo? = packageInfo.applicationInfo
            val appName = if (appInfo != null) {
                packageManager.getApplicationLabel(appInfo).toString()
            } else {
                packageName
            }

            val version = packageInfo.versionName ?: "Unknown"
            val signature = getRealSignature(packageName)
            val icon = appInfo?.icon ?: 0

            AppInfo(appName, packageName, version, signature, icon)
        } catch (e: Exception) {
            Log.e(tag, "Error: ${e.message}")
            null
        }
    }

    fun getRealSignature(packageName: String): String {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val signingInfo: SigningInfo? = packageInfo.signingInfo
                if (signingInfo != null && signingInfo.hasMultipleSigners()) {
                    signingInfo.apkContentsSigners
                } else {
                    signingInfo?.signingCertificateHistory
                }
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            if (signatures.isNullOrEmpty()) return "No signature"

            getSignatureHash(signatures)
        } catch (e: Exception) {
            Log.e(tag, "Error: ${e.message}")
            "Unknown"
        }
    }

    private fun getSignatureHash(signatures: Array<Signature>): String {
        return try {
            val signature = signatures[0].toByteArray()
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val certificate = certificateFactory.generateCertificate(ByteArrayInputStream(signature)) as X509Certificate

            val messageDigest = MessageDigest.getInstance("SHA-256")
            val certHash = messageDigest.digest(certificate.encoded)
            bytesToHex(certHash)
        } catch (e: Exception) {
            Log.e(tag, "Error: ${e.message}")
            "Error"
        }
    }

    @SuppressLint("SdCardPath")
    fun copyApkData(packageName: String): Boolean {
        return try {
            val sourcePath = "${appContext.filesDir.parent}/data/data/$packageName"
            val sourceDir = File(sourcePath)

            if (!sourceDir.exists()) return false

            val nestDir = File(getRootPath(), "NestManager/apk_modified")

            if (!nestDir.exists()) nestDir.mkdirs()

            val targetDir = File(nestDir, packageName)
            copyDirectory(sourceDir, targetDir)
            true
        } catch (e: Exception) {
            Log.e(tag, "Copy failed: ${e.message}")
            false
        }
    }

    private fun copyDirectory(source: File, target: File) {
        if (!target.exists()) target.mkdirs()

        source.listFiles()?.forEach { file ->
            val destFile = File(target, file.name)
            if (file.isDirectory) {
                copyDirectory(file, destFile)
            } else {
                copyFile(file, destFile)
            }
        }
    }

    private fun copyFile(source: File, dest: File) {
        FileInputStream(source).use { input ->
            FileOutputStream(dest).use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = "0123456789abcdef"[v ushr 4]
            hexChars[i * 2 + 1] = "0123456789abcdef"[v and 0x0F]
        }
        return String(hexChars)
    }

    private fun getRootPath(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            Environment.getExternalStorageDirectory().absolutePath
         else
            Environment.getExternalStorageDirectory().absolutePath

    }

    fun createFolder(folderName: String): Boolean {
        return try {
            val folder = File(getRootPath(), folderName)
            if (!folder.exists()) folder.mkdirs() else true
        } catch (e: Exception) {
            Log.e(tag, "Create folder failed: ${e.message}")
            false
        }
    }

    fun createFile(path: String, fileName: String): Boolean {
        return try {
            val dir = File(getRootPath(), path)
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, fileName)
            file.createNewFile()
        } catch (e: Exception) {
            Log.e(tag, "Create file failed: ${e.message}")
            false
        }
    }

    fun writeFile(path: String, fileName: String, content: String): Boolean {
        return try {
            val dir = File(getRootPath(), path)
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, fileName)
            FileOutputStream(file).use { output ->
                output.write(content.toByteArray())
            }
            true
        } catch (e: Exception) {
            Log.e(tag, "Write file failed: ${e.message}")
            false
        }
    }

    fun writeFileBytes(path: String, fileName: String, data: ByteArray): Boolean {
        return try {
            val dir = File(getRootPath(), path)
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, fileName)
            FileOutputStream(file).use { output ->
                output.write(data)
            }
            true
        } catch (e: Exception) {
            Log.e(tag, "Write file bytes failed: ${e.message}")
            false
        }
    }

    fun readFile(path: String, fileName: String): String {
        return try {
            val file = File(getRootPath(), "$path/$fileName")
            if (!file.exists()) return ""
            FileInputStream(file).use { input ->
                val bytes = input.readBytes()
                String(bytes)
            }
        } catch (e: Exception) {
            Log.e(tag, "Read file failed: ${e.message}")
            ""
        }
    }

    fun readFileBytes(path: String, fileName: String): ByteArray {
        return try {
            val file = File(getRootPath(), "$path/$fileName")
            if (!file.exists()) return ByteArray(0)
            FileInputStream(file).use { input ->
                input.readBytes()
            }
        } catch (e: Exception) {
            Log.e(tag, "Read file bytes failed: ${e.message}")
            ByteArray(0)
        }
    }

    fun deleteFile(path: String, fileName: String): Boolean {
        return try {
            val file = File(getRootPath(), "$path/$fileName")
            if (file.exists())  file.delete() else false
        } catch (e: Exception) {
            Log.e(tag, "Delete file failed: ${e.message}")
            false
        }
    }

    fun deleteFolder(folderName: String): Boolean {
        return try {
            val folder = File(getRootPath(), folderName)
            if (folder.exists()) deleteRecursively(folder) else false
        } catch (e: Exception) {
            Log.e(tag, "Delete folder failed: ${e.message}")
            false
        }
    }

    private fun deleteRecursively(file: File): Boolean {
        if (file.isDirectory) file.listFiles()?.forEach { deleteRecursively(it) }
        return file.delete()
    }

    fun fileExists(path: String, fileName: String): Boolean {
        val file = File(getRootPath(), "$path/$fileName")
        return file.exists()
    }

    fun folderExists(folderName: String): Boolean {
        val folder = File(getRootPath(), folderName)
        return folder.exists() && folder.isDirectory
    }

    fun listFiles(path: String): List<String> {
        return try {
            val dir = File(getRootPath(), path)
            if (dir.exists() && dir.isDirectory)
                dir.listFiles()?.map { it.name } ?: emptyList()
             else
                emptyList()
        } catch (e: Exception) {
            Log.e(tag, "List files failed: ${e.message}")
            emptyList()
        }
    }

    fun getFileSize(path: String, fileName: String): Long {
        return try {
            val file = File(getRootPath(), "$path/$fileName")
            if (file.exists())
                file.length()
             else
                0
        } catch (e: Exception) {
            Log.e(tag, "Get file size failed: ${e.message}")
            0
        }
    }

    fun toast(text: String){
        Toast.makeText(appContext, text, Toast.LENGTH_SHORT).show()
    }
    fun log_e(text: String){
        Log.e(tag, text)
    }
    fun log_d(text: String){
        Log.d(tag, text)
    }
    fun log_i(text: String){
        Log.i(tag, text)
    }

}

data class AppInfo(
    val apkName: String,
    val packageName: String,
    val versionApk: String,
    val signApk: String,
    val apkIcon: Int
)