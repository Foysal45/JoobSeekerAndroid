package com.bdjobs.app.ajkerDeal.utilities

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.bdjobs.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun createNewFile(context: Context, type: FileType = FileType.Video): File? {

    return try {
        val storageDir: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM), context.getString(R.string.app_name_folder))
        } else {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), context.getString(R.string.app_name_folder))
        }
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val timeStamp: String = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        var prefix = ""
        var suffix = ""
        when (type) {
            FileType.Picture -> {
                prefix = "IMG_${timeStamp}_"
                suffix = ".jpg"
            }
            FileType.Video -> {
                prefix = "VID_${timeStamp}_"
                suffix = ".mp4"
            }
        }
        File.createTempFile(prefix, suffix, storageDir).apply {
            val filePath = absolutePath
            Timber.d("createNewFile TempFilePath: $filePath")
            if (type == FileType.Video) {
                val videoPath = absolutePath
            }
        }
    } catch (e: Exception) {
        Timber.d(e)
        null
    }

    /*val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val relativeLocation = Environment.DIRECTORY_DCIM + "/" + getString(R.string.app_name)
    var contentUri = Uri.EMPTY
    var mimeType = ""
    var name = ""
    var displayNameKey = ""
    var relativePathKey = ""
    when (type) {
        FileType.Picture -> {
            name = "IMG_${timeStamp}.jpg"
            mimeType = "image/jpeg"
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            displayNameKey = MediaStore.Images.ImageColumns.DISPLAY_NAME
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                relativePathKey = MediaStore.Images.ImageColumns.RELATIVE_PATH
            }
        }
        FileType.Video -> {
            name = "VID_${timeStamp}.mp4"
            mimeType = "video/mp4"
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            displayNameKey = MediaStore.Video.VideoColumns.DISPLAY_NAME
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                relativePathKey = MediaStore.Video.VideoColumns.RELATIVE_PATH
            }
        }
    }
    val contentValues = ContentValues().apply {
        put(displayNameKey, name)
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(relativePathKey, relativeLocation)
        }
    }
    Timber.d("createNewFile relativePath: $relativeLocation")


    return try {
        val uri: Uri? = contentResolver.insert(contentUri, contentValues)
        uri?.let {
            Timber.d("createNewFile contentUri: $it")
            val uriForFile = FileProvider.getUriForFile(this, "com.ajkerdeal.app.livestream.fileprovider", File(it.path ?: ""))
            fileUri = uriForFile
            Timber.d("createNewFile fileUri: $fileUri")
            return File(fileUri.path ?: "")
        }
        null
    } catch (e: Exception) {
        Timber.d(e)
        null
    }*/

}

fun moveToDICM(context: Context, sourcePath: String, type: FileType = FileType.Video, listener: (isSuccess: Boolean, path: String) -> Unit) {

    CoroutineScope(Dispatchers.IO).launch {

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val relativeLocation = Environment.DIRECTORY_DCIM + "/" + context.getString(R.string.app_name_folder)
        var contentUri = Uri.EMPTY
        var mimeType = ""
        var name = ""
        var displayNameKey = ""
        var relativePathKey = ""
        when (type) {
            FileType.Picture -> {
                name = "IMG_${timeStamp}.jpg"
                mimeType = "image/jpeg"
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                displayNameKey = MediaStore.Images.ImageColumns.DISPLAY_NAME
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    relativePathKey = MediaStore.Images.ImageColumns.RELATIVE_PATH
                }
            }
            FileType.Video -> {
                name = "VID_${timeStamp}.mp4"
                mimeType = "video/mp4"
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                //val contentUri = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                displayNameKey = MediaStore.Video.VideoColumns.DISPLAY_NAME
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    relativePathKey = MediaStore.Video.VideoColumns.RELATIVE_PATH
                }
            }
        }
        val contentValues = ContentValues().apply {
            put(displayNameKey, name)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(relativePathKey, relativeLocation)
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }
        }
        Timber.d("createNewFile relativePath: $relativeLocation")

        try {
            val uri: Uri? = context.contentResolver.insert(contentUri, contentValues)
            if (uri != null) {
                context.contentResolver.openFileDescriptor(uri, "w").use { pfd ->
                    val outputStream = FileOutputStream(pfd!!.fileDescriptor)
                    val inputStream = File(sourcePath).inputStream()
                    val buf = ByteArray(8192)
                    var len = 0
                    while (inputStream.read(buf).also { length -> len = length } > 0) {
                        outputStream.write(buf, 0, len)
                    }
                    outputStream.close()
                    inputStream.close()
                    pfd.close()
                }

                val contentValuesUpdate = ContentValues().apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Video.Media.IS_PENDING, 0)
                    }
                }
                val row = context.contentResolver.update(uri, contentValuesUpdate, null, null)
                if (row > 0) {
                    if (File(sourcePath).delete()) {
                        Timber.d("File moved")
                    }
                }
                val videoPath = com.bdjobs.app.ajkerDeal.utilities.FileUtils(context).getPath(uri)
                listener.invoke(true, videoPath)
            }
        } catch (e: Exception) {
            Timber.d(e)
            listener.invoke(false, sourcePath)
        }
    }

}

fun saveLogoToStorage(context: Context, @DrawableRes resourceId: Int): String {

    val bm = (ResourcesCompat.getDrawable(context.resources, resourceId, null) as BitmapDrawable).bitmap
    val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM), context.getString(R.string.app_name_folder))
    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }
    val file = File(storageDir, "logo.png")

    try {
        file.outputStream().use { outStream ->
            bm.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream.flush()
            outStream.close()
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
    return file.absolutePath
}

/*fun compressVideoWithFFmpeg(context: Context, sourcePath: String, async: Boolean = true, listener: CompressionListener) {

    val timeStamp: String = SimpleDateFormat("yyyyMMdd_hhmmss", Locale.US).format(Date())
    val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM), context.getString(R.string.app_name_folder))
    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }
    val compressFilePath = File(storageDir, "VID_$timeStamp.mp4").absolutePath
    //val logoPath = File(storageDir, "logo.png").absolutePath
    *//*if (!File(logoPath).exists()) {
        saveLogoToStorage(context, R.drawable.logo_100)
    }*//*
    Timber.d("sourcePath: $sourcePath")
    Timber.d("destinationPath: $compressFilePath")
    //Timber.d("logoPath: $logoPath")

    try {
        val info = FFprobe.getMediaInformation(sourcePath)
        val streamList = info.streams
        Timber.d("FFprobeDebug $streamList")
        val streamVideo = streamList.filter { it.type == "video" }
        if (streamVideo.isEmpty()) {
            listener.onFailed("No video stream found")
            return
        }
        val stream = streamVideo.first()
        val width = stream.width
        val height = stream.height
        val durationInSecond = info.duration.toDouble()
        val videoDurationInMilliseconds = (durationInSecond * 1000).toLong()

        val scale = if (height > width) {
            if (height > 854) {
                "480:-2"//w:h 'min(480,iw)'
            } else {
                ""
            }
        } else {
            if (width > 854) {
                "-2:480"//w:h 'min(480,ih)'
            } else {
                ""
            }
        }
        val newBitrate = "1M"

        var command = "-i '$sourcePath' "
        //water mark
        //command += "-i $logoPath " +
        //command += "-filter_complex \"scale=$scale,overlay=20:20\" " +
        if (scale.isNotEmpty()) {
            command += "-vf scale=$scale "
        }
        command += "-b:v $newBitrate "
        command += "-maxrate $newBitrate "
        command += "'$compressFilePath'"

        Timber.d("Command: $command")

        Config.enableStatisticsCallback { statistics ->
            if (statistics == null) return@enableStatisticsCallback
            val timeInMilliseconds = statistics.time
            if (timeInMilliseconds > 0) {
                val percentage = (timeInMilliseconds.toDouble() / videoDurationInMilliseconds) * 100
                listener.onProgress(percentage.roundToInt())
            }
        }

        if (async) {
            FFmpeg.executeAsync(command) { _, returnCode ->
                ffmpegResult(compressFilePath, returnCode, listener)
            }
        } else {
            val resultCode = FFmpeg.execute(command)
            ffmpegResult(compressFilePath, resultCode, listener)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        listener.onFailed("Execution failed with ${e.message}")
    }

}*/

/*private fun ffmpegResult(outputPath: String, resultCode: Int, listener: CompressionListener) {
    when (resultCode) {
        Config.RETURN_CODE_SUCCESS -> {
            Timber.d("Async command execution completed successfully")
            listener.onSuccess(outputPath)
        }
        Config.RETURN_CODE_CANCEL -> {
            Timber.d("Async command execution cancelled by user")
            //binding.progressBar.isIndeterminate = false
            listener.onCancel("Execution cancelled by user")
        }
        else -> {
            Timber.d("Async command execution failed with $resultCode")
            Config.printLastCommandOutput(Log.INFO)
            //binding.progressBar.isIndeterminate = false
            listener.onFailed("Execution failed with $resultCode")
        }
    }
}*/

interface CompressionListener {
    fun onSuccess(path: String)
    fun onFailed(msg: String)
    fun onCancel(msg: String)
    fun onProgress(progress: Int)
}

