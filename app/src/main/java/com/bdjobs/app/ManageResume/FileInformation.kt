package com.bdjobs.app.ManageResume

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File

/**
 * Created by Rubayet on 20-Mar-17.
 */

class FileInformation {


    var type: String? = null
    var fileName: String? = null
    var extension: String? = null
    var extensionwithDot: String? = null
    var fileSize: Long = 0


    fun getFileInformation(ctx: Context, uri: Uri): FileInformation {
        val fileInfo = FileInformation()
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val returnCursor = ctx.contentResolver.query(uri, null, null, null, null)!!

            returnCursor.moveToFirst()
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)

            //File Name
            fileInfo.fileName = returnCursor.getString(nameIndex)

            //File Size
            fileInfo.fileSize = returnCursor.getInt(sizeIndex).toLong()

            //File Type
            fileInfo.type = ctx.contentResolver.getType(uri)

            //File Extension


            val filenameArray = fileInfo.fileName!!.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            fileInfo.extension = filenameArray[filenameArray.size - 1]
            fileInfo.extensionwithDot = "." + fileInfo.extension!!

            returnCursor.close()

        } else {
            val f = File(uri.path)
            fileInfo.fileName = f.name
            fileInfo.fileSize = f.length()

            //File Type
//            fileinfo.extension = MimeTypeMap.getFileExtensionFromUrl(uri
//                    .toString())
//
            fileInfo.extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())

            fileInfo.extensionwithDot = "." + fileInfo.extension!!

            fileInfo.type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileInfo.extension!!.toLowerCase())


        }
        return fileInfo
    }
}
