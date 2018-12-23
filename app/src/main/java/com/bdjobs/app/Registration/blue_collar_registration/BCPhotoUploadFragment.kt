package com.bdjobs.app.Registration.blue_collar_registration

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.yalantis.ucrop.UCrop
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import kotlinx.android.synthetic.main.fragment_bc_photo_upload.*
import org.jetbrains.anko.imageURI
import org.jetbrains.anko.runOnUiThread
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.ArrayList


class BCPhotoUploadFragment : Fragment() {



    private lateinit var registrationCommunicator: RegistrationCommunicator
    private lateinit var returnView: View
    internal var filePaths = ArrayList<String>()
    private val REQ_CAMERA_IMAGE = 40
    private val READ_REQUEST_CODE = 42
    private val MY_PERMISSIONS_REQUEST_CAMERA = 2
    private lateinit var bitmap: Bitmap
    internal lateinit var encodedString: String
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        returnView = inflater.inflate(R.layout.fragment_bc_photo_upload, container, false)
        return returnView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialization()
        onClick()
    }


    private fun onClick(){

        completeButton.setOnClickListener {

            registrationCommunicator.bcGoToStepCongratulation()


        }

        photoUploadImageView.setOnClickListener {


            performFileSearch()

        }

    }

    private fun initialization(){

        registrationCommunicator = activity as RegistrationCommunicator


    }

    fun performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        //intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        //intent.setType("image/*");

        //startActivityForResult(intent, READ_REQUEST_CODE);

        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(filePaths)
                .setActivityTheme(R.style.AppTheme)
                .enableCameraSupport(false)
                .pickPhoto(activity)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        Log.d("dfgh","  onActivityResult called")




        if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO && resultCode == Activity.RESULT_OK && resultData != null) {

            Log.d("dfgh","  1 condition")
            var uri: Uri? = null
            uri = Uri.fromFile(File(resultData.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA).get(0)))
            Log.d("dfgh", "Uri: " + uri!!.toString())
            Log.d("PhotoUpload", "Uri: " + uri!!.toString())
            val myDirectory = File("/sdcard/BDJOBS")
            if (!myDirectory.exists()) {
                myDirectory.mkdirs()
            }

            val file = File("/sdcard/BDJOBS/bdjobsProfilePic.jpg")

            Log.d("dfgh","  2 condition")
            if (file.exists()) {
                val deleted = file.delete()
            }
            val destinationUri = Uri.fromFile(File("/sdcard/BDJOBS/bdjobsProfilePic.jpg"))
            UCrop.of(uri, destinationUri).withAspectRatio(9f, 10f).start(activity,69)
        }

        Log.d("dfgh"," resultCode $resultCode RESULT_OK $RESULT_OK " +
                "requestCode $requestCode  UCrop.REQUEST_CROP ${UCrop.REQUEST_CROP} resultData $resultData ")

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP && resultData != null) {

            Log.d("dfgh","  3 Crop condition")

            val resultUri = UCrop.getOutput(resultData)
            val tempURI = Uri.fromFile(File("/sdcard/"))
            photoUploadImageView.setImageURI(tempURI)

            photoUploadImageView.imageURI = tempURI

            photoUploadImageView.imageURI = resultUri
            photoUploadImageView.setImageURI(resultUri)


            val path = resultUri!!.path
          /*  UploadBTN.setVisibility(View.GONE)*/
            val file = File(path)
            val size = file.length()
            val fileSizeInKB = size / 1024
            // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            val fileSizeInMB = fileSizeInKB / 1024
            if (fileSizeInMB > 3) {
                Toast.makeText(activity, "Image is greater than 3MB", Toast.LENGTH_SHORT).show()
            } else if (fileSizeInMB <= 3) {
                getEncodedStringFromImagePath().execute(path)
            }


        } else if (resultCode == UCrop.RESULT_ERROR) {

            Log.d("ImageUpload","")

            val cropError = UCrop.getError(resultData!!)
            Log.d("ImageUpload"," cropError $cropError")
        }

        if (requestCode == REQ_CAMERA_IMAGE && resultCode == Activity.RESULT_OK) {

            val path = getLastImagePath()
            val SourceUri = Uri.fromFile(File(path))
            val myDirectory = File("/sdcard/BDJOBS")
            if (!myDirectory.exists()) {
                myDirectory.mkdirs()
            }

            val file = File("/sdcard/BDJOBS/bdjobsProfilePic.jpg")
            if (file.exists()) {
                val deleted = file.delete()
            }
            val destinationUri = Uri.fromFile(File("/sdcard/BDJOBS/bdjobsProfilePic.jpg"))
            UCrop.of(SourceUri, destinationUri).withAspectRatio(9f, 10f).start(activity)
        }

    }


    private fun getLastImagePath(): String {
        val imageColumns = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
        val imageOrderBy = MediaStore.Images.Media._ID + " DESC"
        val imageCursor =activity.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy)
        if (imageCursor.moveToFirst()) {
            val id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID))
            val fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA))
            Log.d("Path", fullPath)
            return fullPath
        } else {
            return ""
        }
    }

    private inner class getEncodedStringFromImagePath : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg f_url: String): String? {
            try {

                var options: BitmapFactory.Options? = null
                options = BitmapFactory.Options()
                options.inSampleSize = 3

                bitmap = BitmapFactory.decodeFile(f_url[0], options)
                val stream = ByteArrayOutputStream()
                // Must compress the Image to reduce image size to make upload easy
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val byte_arr = stream.toByteArray()
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, 0)
            } catch (e: Exception) {
                println("SEMVcb" + e.toString())
                activity.runOnUiThread(Runnable { Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show() })
            }

            return null
        }

        override fun onPostExecute(file_url: String) {

           /* UploadBTN.setVisibility(View.VISIBLE)*/
        }
    }
}
