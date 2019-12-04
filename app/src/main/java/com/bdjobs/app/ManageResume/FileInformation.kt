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
        private set
    var extension: String? = null
        private set
    var extensionwithDot: String? = null
        private set
    var fileSize: Long = 0
        private set


    fun getfileInformation(ctx: Context, uri: Uri): FileInformation {
        val fileinfo = FileInformation()
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val returnCursor = ctx.contentResolver.query(uri, null, null, null, null)!!

            returnCursor.moveToFirst()
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)

            //File Name
            fileinfo.fileName = returnCursor.getString(nameIndex)

            //File Size
            fileinfo.fileSize = returnCursor.getInt(sizeIndex).toLong()

            //File Type
            fileinfo.type = ctx.contentResolver.getType(uri)

            //File Extension


            val filenameArray = fileinfo.fileName!!.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            fileinfo.extension = filenameArray[filenameArray.size - 1]
            fileinfo.extensionwithDot = "." + fileinfo.extension!!

            returnCursor.close()

        } else {
            val f = File(uri.path)
            fileinfo.fileName = f.name
            fileinfo.fileSize = f.length()

            //File Type
//            fileinfo.extension = MimeTypeMap.getFileExtensionFromUrl(uri
//                    .toString())
//
            fileinfo.extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())

            fileinfo.extensionwithDot = "." + fileinfo.extension!!

            fileinfo.type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileinfo.extension!!.toLowerCase())


        }
        return fileinfo

    }
}
