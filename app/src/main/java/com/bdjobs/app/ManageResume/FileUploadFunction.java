package com.bdjobs.app.ManageResume;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;
import java.io.File;

/**
 * Created by maymuna on 6/19/2016.
 */
public class FileUploadFunction {

    public Pdf_file_info getfileInformation(Context ctx, Uri uri)
    {
        Pdf_file_info fileinfo = null;
        try {
            fileinfo = new Pdf_file_info();
            if(uri.getScheme().equals(ContentResolver.SCHEME_CONTENT))
            {
                Cursor returnCursor =
                        ctx.getContentResolver().query(uri, null, null, null, null);

                returnCursor.moveToFirst();
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);

                //File Name
                fileinfo.fileName = returnCursor.getString(nameIndex);

                //File Size
                fileinfo.fileSize = returnCursor.getInt(sizeIndex);

                //File Type
                fileinfo.type = ctx.getContentResolver().getType(uri);

                //File Extension


                String filenameArray[] = fileinfo.fileName.split("\\.");
                fileinfo.extension = filenameArray[filenameArray.length - 1];
                fileinfo.extensionwithDot = "."+fileinfo.extension;
                System.out.println("extension and "+fileinfo.extension+" "+fileinfo.extensionwithDot);

                returnCursor.close();

            }
            else {
                File f = new File(uri.getPath());
                fileinfo.fileName = f.getName();
                fileinfo.fileSize = f.length();

                //File Type
                fileinfo.extension = MimeTypeMap.getFileExtensionFromUrl(uri
                        .toString());
                fileinfo.extensionwithDot = "."+fileinfo.extension;

                fileinfo.type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        fileinfo.extension.toLowerCase());


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return fileinfo;

    }
  /*  public static void setUploadFile(Context ctx, String uId, String dId, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorlistener, Uri uri)throws Exception {

        final Pdf_file_info fileinfo = new FileUploadFunction().getfileInformation(ctx,uri);
        String url = "http://my.bdjobs.com/apps/mybdjobs/file_upload.aspx";

        Map<String, String> params = new HashMap<>();
        params.put("userid", uId);
        params.put("decodeid", dId);
        params.put("status", "upload");
        params.put("fileExtension", fileinfo.extensionwithDot);
        params.put("fileType", fileinfo.type);
        params.put("fileName", fileinfo.fileName);

        InputStream inputStream = null;
        try {
            inputStream = ctx.getContentResolver().openInputStream(uri);
        }catch (Exception e){
            throw  new Exception("This file is in the cloude , not in the device. Please download it first into device");
        }
        assert inputStream != null;
        final byte[] bytes = IOUtils.toByteArray(inputStream);

        inputStream.close();

        Log.e("TAG", "bbytes:"+bytes.length+" "+fileinfo.extensionwithDot+" "+fileinfo.type+" "+fileinfo.fileName);



        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST,
                url,
                null,
                params,
                responseListener,
                errorlistener
        ) {

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("File", new DataPart(fileinfo.fileName, bytes, fileinfo.type));
                return params;
            }
        };

        //Use your implementation of volley , if you have any
        VolleySingleton.getInstance(ctx).addToRequestQueue(multipartRequest);

    }

*/
}
