package com.example.myhipmi.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtil {
    @Throws(Exception::class)
    fun from(context: Context, uri: Uri): File {
        val contentResolver: ContentResolver = context.contentResolver
        val inputStream: InputStream = contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open input stream from URI")

        // Use cache dir to write temp file
        val fileName = queryFileName(context, uri) ?: "temp_image_${System.currentTimeMillis()}.jpg"
        val tempFile = File(context.cacheDir, fileName)
        val outputStream = FileOutputStream(tempFile)

        inputStream.use { input ->
            outputStream.use { out ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    out.write(buffer, 0, read)
                }
                out.flush()
            }
        }
        return tempFile
    }

    private fun queryFileName(context: Context, uri: Uri): String? {
        return try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndexOpenableColumnName()
                    if (index >= 0) it.getString(index) else null
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }
    private fun android.database.Cursor.getColumnIndexOpenableColumnName(): Int {
        val nameCols = arrayOf("_display_name", "display_name")
        for (col in nameCols) {
            val idx = try { getColumnIndex(col) } catch (_: Exception) { -1 }
            if (idx >= 0) return idx
        }
        return -1
    }
}