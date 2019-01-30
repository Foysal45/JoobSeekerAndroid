package com.bdjobs.app.ManageResume;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by Rubayet on 20-Mar-17.
 */

class Pdf_file_info {


        private String type;
        private String fileName;
        private String extension;
        private String extensionwithDot;
        private long fileSize;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

         String getFileName() {
            return fileName;
        }

        private void setFileName(String fileName) {
            this.fileName = fileName;
        }

        String getExtension() {
            return extension;
        }

        private void setExtension(String extension) {
            this.extension = extension;
        }

        public String getExtensionwithDot() {
            return extensionwithDot;
        }

        private void setExtensionwithDot(String extensionwithDot) {
            this.extensionwithDot = extensionwithDot;
        }

        public long getFileSize() {
            return fileSize;
        }

        private void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }


    Pdf_file_info getfileInformation(Context ctx, Uri uri) {
        Pdf_file_info fileinfo = new Pdf_file_info();
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            Cursor returnCursor =
                    ctx.getContentResolver().query(uri, null, null, null, null);

            assert returnCursor != null;
            returnCursor.moveToFirst();
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);

            //File Name
            fileinfo.setFileName(returnCursor.getString(nameIndex));

            //File Size
            fileinfo.setFileSize(returnCursor.getInt(sizeIndex));

            //File Type
            fileinfo.setType(ctx.getContentResolver().getType(uri));

            //File Extension


            String filenameArray[] = fileinfo.getFileName().split("\\.");
            fileinfo.setExtension(filenameArray[filenameArray.length - 1]);
            fileinfo.setExtensionwithDot("." + fileinfo.getExtension());

            returnCursor.close();

        } else {
            File f = new File(uri.getPath());
            fileinfo.setFileName(f.getName());
            fileinfo.setFileSize(f.length());

            //File Type
            fileinfo.setExtension(MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString()));
            fileinfo.setExtensionwithDot("." + fileinfo.getExtension());

            fileinfo.setType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileinfo.getExtension().toLowerCase()));


        }
        return fileinfo;

    }
}
