package com.bdjobs.app.utilities

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.bdjobs.app.manageResume.FileInformation
import com.bdjobs.app.utilities.Constants.Companion.DOCX
import com.bdjobs.app.utilities.Constants.Companion.PDF
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class FileUtil {

    companion object {
        val instance = FileUtil()
    }

    @Throws(Exception::class)
    fun getFileFromUri(context: Context, uri: Uri): File {
        var path: String? = ""
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId: String = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    path = Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                val id: String = DocumentsContract.getDocumentId(uri)
                val contentUri: Uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), id.toLong()
                )
                path = getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) { // MediaProvider
                val docId: String = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                path = getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            path = getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            path = uri.path
        }
        return File(path)
    }

    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Files.FileColumns.DATA
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver
                .query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun getFileInformation(ctx: Context, uri: Uri): FileInformation {
        val fileInfo = FileInformation()
        val file = getFileFromUri(ctx, uri)
        fileInfo.extension = file.extension
        fileInfo.fileName = file.name
        fileInfo.fileSize = file.length()
        fileInfo.extensionwithDot = "." + file.extension
        fileInfo.type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileInfo.extension!!.toLowerCase())
        return fileInfo
    }

    fun writeBytesAsPdf(context: Context, bytes : ByteArray) : File {
        val path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS + "/Bdjobs/")
        val file = File.createTempFile("temp",".pdf", path)
        val os = FileOutputStream(file)
        os.write(bytes)
        os.flush()
        os.close()
        return file
    }

    fun writeBytes(context: Context, bytes : ByteArray, filename: String) : File {
        val path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS + "/Bdjobs/")
        val file = File.createTempFile("temp", ".$filename", path)
        val os = FileOutputStream(file)
        os.write(bytes)
        os.flush()
        os.close()
        return file
    }

    fun deleteFile(file: File) : Boolean {
        return file.exists() && file.delete()
    }


    fun getNewFile(filepath : String, context : Context):File{
        val dir = File(context.getExternalFilesDir(null)!!.absoluteFile, "video_resume")
        dir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return  File(dir.path + File.separator + "bdjobs_" + filepath + timeStamp + ".mp4")
    }
    fun getFilename(
        context: Context, uri: Uri?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Files.FileColumns.DATA
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri!!, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun getFileExtension(mimeType : String) : String {
        return when (mimeType) {
            PDF -> {
                "pdf"
            }
            DOCX -> {
                "docx"
            }
            else -> {
                "doc"
            }
        }
    }
}