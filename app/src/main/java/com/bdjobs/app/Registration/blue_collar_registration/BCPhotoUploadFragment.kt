package com.bdjobs.app.Registration.blue_collar_registration

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.app.Fragment
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.PhotoInfoModel
import com.bdjobs.app.API.ModelClasses.PhotoUploadResponseModel
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.callHelpLine
import com.bdjobs.app.Utilities.d
import com.bdjobs.app.Utilities.loadCircularImageFromUrl
import com.bdjobs.app.Utilities.logException
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.yalantis.ucrop.UCrop
import cz.msebera.android.httpclient.Header
import droidninja.filepicker.FilePickerConst
import kotlinx.android.synthetic.main.footer_bc_layout.*
import kotlinx.android.synthetic.main.fragment_bc_photo_upload.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File


class BCPhotoUploadFragment : Fragment() {


    private lateinit var registrationCommunicator: RegistrationCommunicator
    private lateinit var returnView: View
    private val REQ_CAMERA_IMAGE = 40
    private val MY_PERMISSIONS_REQUEST_CAMERA = 2
    private lateinit var bitmap: Bitmap
    private var encodedString: String = ""
    private var userId: String = ""
    private var decodeId: String = ""
    private var folderName: String = ""
    private var folderId: String = ""
    private var imageName: String = ""
    private var isResumeUpdate: String = ""
    private var params = RequestParams()
    private var dialog: Dialog? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        returnView = inflater.inflate(R.layout.fragment_bc_photo_upload, container, false)
        return returnView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialization()
        onClick()
        registrationCommunicator.checkInviteCodeEligibility()
    }


    private fun onClick() {

        completeButton?.setOnClickListener {

            if (encodedString.isBlank()) {

                registrationCommunicator.bcGoToStepCongratulation()
            } else {

                registrationCommunicator.showProgressBar()
                ApiServiceMyBdjobs.create().getPhotoInfo(registrationCommunicator.getUserId(), registrationCommunicator.getDecodeId()).enqueue(object : Callback<PhotoInfoModel> {
                    override fun onFailure(call: Call<PhotoInfoModel>, t: Throwable) {

                        Log.d("PhotoUpload", " onFailure ${t.message}")
                    }

                    override fun onResponse(call: Call<PhotoInfoModel>, response: Response<PhotoInfoModel>) {
                        try {

                            Log.d("PhotoUpload", " response ${response.body()!!.statuscode}")
                            Log.d("PhotoUpload", " response ${response.body()!!.message}")

                            if (response.body()!!.statuscode.equals("0", true)) {

                                userId = response.body()!!.data[0].userId
                                decodeId = response.body()!!.data[0].decodId
                                folderName = response.body()!!.data[0].folderName
                                folderId = response.body()!!.data[0].folderId
                                imageName = response.body()!!.data[0].imageName
                                isResumeUpdate = response.body()!!.data[0].isResumeUpdate


                                params.put("Image", encodedString)
                                params.put("userid", registrationCommunicator.getUserId())
                                params.put("decodeid", registrationCommunicator.getDecodeId())
                                params.put("folderName", folderName)
                                params.put("folderId", folderId)
                                params.put("imageName", imageName)
                                params.put("isResumeUpdate", isResumeUpdate)
                                params.put("status", "upload")

                                makeHTTPCall()
                            }
                        } catch (Exc: Exception) {

                            logException(Exc)
                        }


                    }
                })
            }
        }

        photoUploadImageView?.setOnClickListener {
            showDialog(activity)
        }

        supportTextView?.setOnClickListener {
            activity.callHelpLine()
        }

        bcHelpLineLayout?.setOnClickListener {
            activity.callHelpLine()
        }

    }

    private fun initialization() {
        registrationCommunicator = activity as RegistrationCommunicator

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                    d("openCamera called")

                } else {

                }
                return
            }
        }
    }

    fun makeHTTPCall() {

        val client = AsyncHttpClient()
        client.post("http://my.bdjobs.com/apps/mybdjobs/v1/upload_img.aspx", params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {


                try {
                    val response = String(responseBody)
                    Log.d("dgdsgdghj", " response ${response}")

                    val gson = Gson()
                    val photoUploadModel = gson.fromJson(response, PhotoUploadResponseModel::class.java)
                    val photoUrl = photoUploadModel.data[0].path
                    Log.d("dgdsgdghj", " $photoUrl")
                    val bdjobsUserSession = BdjobsUserSession(activity)
                    bdjobsUserSession.updateUserPicUrl(photoUrl.trim())

                    registrationCommunicator.hideProgressBar()
                    registrationCommunicator.bcGoToStepCongratulation()

                } catch (exp: Exception) {

                    logException(exp)

                }


            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                try {
                    try {

                        Log.e("photoAPI", error.message)
                    } catch (e: Exception) {
                        logException(e)
                    }

                    try {
                        activity?.toast(error.message!!)
                    } catch (e: Exception) {
                        logException(e)
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        })
    }

    fun performFileSearch() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), FilePickerConst.REQUEST_CODE_PHOTO)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        Log.d("dfgh", "  onActivityResult called")

        try {
            if (resultCode != Activity.RESULT_CANCELED && requestCode != null && resultData != null) {

                /*  Log.d("FragmentResultPhoto", "requestCode: $requestCode, resultCode:$resultCode, data:$resultData")*/
                if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO && resultCode == Activity.RESULT_OK && resultData != null) {
                    var fileUri: Uri? = null

                    val selectedImageUri = resultData.data
                    val tempPath = getPathCloud(selectedImageUri, activity)
                    val url = resultData.data!!.toString()
                    if (url.startsWith("content://com.google.android.apps") || url.startsWith("content://com.android.providers") || url.startsWith("content://media/external")) {

                        try {
                            val `is` = activity.contentResolver.openInputStream(selectedImageUri!!)
                            if (`is` != null) {
                                deleteCache(activity)
                                bitmap = BitmapFactory.decodeStream(`is`)
                                if (bitmap != null) {
                                    val tempUri = getImageUri(activity, bitmap)
                                    // CALL THIS METHOD TO GET THE ACTUAL PATHa
                                    var finalFile: File? = null
                                    try {
                                        finalFile = File(getRealPathFromURI(tempUri))
                                        fileUri = Uri.fromFile(finalFile)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                } else {
                                    Toast.makeText(activity, "Invalid Image has been selected! Please Choose image again", Toast.LENGTH_LONG).show()
                                }

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    ////////////////////////////////////////////
                    Log.d("dfgh", "  1 condition")


                    Log.d("dfgh", "Uri: " + fileUri!!.toString())
                    Log.d("PhotoUpload", "Uri: " + fileUri.toString())
                    val myDirectory = File("/sdcard/BDJOBS")
                    if (!myDirectory.exists()) {
                        myDirectory.mkdirs()
                    }

                    val file = File("/sdcard/BDJOBS/bdjobsProfilePic.jpg")

                    Log.d("dfgh", "  2 condition")
                    if (file.exists()) {
                        val deleted = file.delete()
                    }
                    val destinationUri = Uri.fromFile(File("/sdcard/BDJOBS/bdjobsProfilePic.jpg"))
                    UCrop.of(fileUri, destinationUri).withAspectRatio(9f, 10f).start(activity)
                }

                Log.d("dfgh", " resultCode $resultCode RESULT_OK $RESULT_OK " +
                        "requestCode $requestCode  UCrop.REQUEST_CROP ${UCrop.REQUEST_CROP} resultData $resultData ")

                if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP && resultData != null) {

                    Log.d("dfgh", "  3 Crop condition")

                    val resultUri = UCrop.getOutput(resultData)
                    val tempURI = Uri.fromFile(File("/sdcard/"))
                    photoUploadImageView.loadCircularImageFromUrl(tempURI.toString())
                    photoUploadImageView.loadCircularImageFromUrl(resultUri.toString())


                    dialog?.dismiss()

                    val path = resultUri!!.path
                    val file = File(path)
                    val size = file.length()
                    val fileSizeInKB = size / 1024
                    // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                    val fileSizeInMB = fileSizeInKB / 1024
                    if (fileSizeInMB > 3) {
                        Toast.makeText(activity, "Image is greater than 3MB", Toast.LENGTH_SHORT).show()
                    } else if (fileSizeInMB <= 3) {

                        doAsync {
                            try {
                                var options: BitmapFactory.Options? = null
                                options = BitmapFactory.Options()
                                options.inSampleSize = 3

                                bitmap = BitmapFactory.decodeFile(path, options)
                                val stream = ByteArrayOutputStream()
                                // Must compress the Image to reduce image size to make upload easy
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                                val byte_arr = stream.toByteArray()
                                // Encode Image to String
                                encodedString = Base64.encodeToString(byte_arr, 0)
                            } catch (e: Exception) {
                                error("SEMVcb" + e.toString())
                            }
                        }
                    }

                } else if (resultCode == UCrop.RESULT_ERROR) {

                    Log.d("ImageUpload", "")

                    val cropError = UCrop.getError(resultData)
                    Log.d("ImageUpload", " cropError $cropError")
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


        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun getLastImagePath(): String {
        val imageColumns = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
        val imageOrderBy = MediaStore.Images.Media._ID + " DESC"
        val imageCursor = activity.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy)
        if (imageCursor.moveToFirst()) {
            val id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID))
            val fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA))
            Log.d("Path", fullPath)
            return fullPath
        } else {
            return ""
        }
    }


    private fun showDialog(activity: Activity) {
        dialog = Dialog(activity)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.photo_upload_dialog_layout)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val deleteImageView = dialog?.findViewById<ImageView>(R.id.deleteIV)
        val cameraButton = dialog?.findViewById<Button>(R.id.camera_button)
        val galleryButton = dialog?.findViewById<TextView>(R.id.gallery_button)

        deleteImageView?.setOnClickListener {
            dialog?.dismiss()
        }
        cameraButton?.setOnClickListener {
            RequestPermissionAndOpenCamera()
        }

        galleryButton?.setOnClickListener {
            performFileSearch()
        }

        dialog?.show()

    }


    private fun RequestPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), MY_PERMISSIONS_REQUEST_CAMERA)
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), MY_PERMISSIONS_REQUEST_CAMERA)
            }

        } else {
            openCamera()
        }
    }


    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, System.currentTimeMillis().toString() + ".jpg")
        val mCapturedImageURI = activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intentPicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI)
        startActivityForResult(intentPicture, REQ_CAMERA_IMAGE)
    }


    private fun getPathCloud(uri: Uri, activity: Activity): String {
        var cursor: Cursor? = null
        try {
            val projection = arrayOf(MediaStore.MediaColumns.DATA)
            cursor = activity.contentResolver.query(uri, projection, null, null, null)
            if (cursor!!.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                val path = cursor.getString(column_index)
                cursor.close()
                return path
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    private fun getImageUri(inContext: Activity, inImage: Bitmap): Uri {
        var path = ""
        try {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "bdJobsProfilePic", null)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("PhotoInvalid", "getImageUri: " + e.message)
            Toast.makeText(inContext, "Invalid Image has been selected!", Toast.LENGTH_SHORT).show()
        }

        return Uri.parse(path)
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val cursor: Cursor? = activity.contentResolver.query(uri, null, null, null, null)
        cursor!!.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }

    private fun deleteCache(context: Context) {
        try {
            val dir = context.cacheDir
            if (dir != null && dir.isDirectory) {
                deleteDir(dir)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        return dir!!.delete()
    }


}
