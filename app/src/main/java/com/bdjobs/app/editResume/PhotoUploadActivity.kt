package com.bdjobs.app.editResume

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
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
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.BitmapCompat
import androidx.core.view.isVisible
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.PhotoInfoModel
import com.bdjobs.app.API.ModelClasses.PhotoUploadResponseModel
import com.bdjobs.app.Ads.Ads
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdRequest
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.squareup.picasso.LruCache
import com.squareup.picasso.Picasso
import com.squareup.picasso.PicassoTools
import com.yalantis.ucrop.UCrop
import cz.msebera.android.httpclient.Header
//import droidninja.filepicker.FilePickerConst
import kotlinx.android.synthetic.main.activity_photo_upload.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

class PhotoUploadActivity : Activity() {



    private val REQ_CAMERA_IMAGE = 40
    private val REQ_SELECT_IMAGE = 41
    private val MY_PERMISSIONS_REQUEST_CAMERA = 2
    private var bitmap: Bitmap? = null
    private var encodedString: String? = ""
    private var userId: String? = ""
    private var decodeId: String? = ""
    private var folderName: String? = ""
    private var folderId: String? = ""
    private var imageName: String? = ""
    private var isResumeUpdate: String? = ""
    internal var params = RequestParams()
    internal var reqParams = RequestParams()
    private var dialog: Dialog? = null
    private lateinit var bdjobsUserSession: BdjobsUserSession
    private lateinit var progressDialog: ProgressDialog
    private lateinit var resultUri: Uri
    private var changeClickStatus = false

    private fun setupToolbar(title: String?) {
        tv_tb_title?.text = title
        tbUploadPhoto?.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        tbUploadPhoto?.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_upload)
        bdjobsUserSession = BdjobsUserSession(this@PhotoUploadActivity)
        progressDialog = ProgressDialog(this@PhotoUploadActivity)
        if (!bdjobsUserSession.userPicUrl.isNullOrEmpty()) {
            editResPhotoUploadImageView.loadCircularImageFromUrl(bdjobsUserSession.userPicUrl)
            Log.d("dgdsgdghjOnRes", "Session url ${bdjobsUserSession.userPicUrl}")
            noPhotoTV?.text = "You can change or delete your photo"
            photoInfoTV?.hide()
            editResPhotoUploadButton?.hide()
            editResChangePhotoButton?.show()
            ic_edit_photo?.show()
            photoDeleteButton?.show()
        } else if (bdjobsUserSession.userPicUrl.isNullOrEmpty()) {
            noPhotoTV?.show()
            photoInfoTV?.show()
            editResPhotoUploadButton?.show()
            editResChangePhotoButton?.hide()
            ic_edit_photo?.hide()
            photoDeleteButton?.hide()
        }

//        val adRequest = AdRequest.Builder().build()
//        adViewPhoto?.loadAd(adRequest)

        Ads.loadAdaptiveBanner(this@PhotoUploadActivity,adViewPhoto)

    }

    override fun onResume() {
        super.onResume()
        setupToolbar(getString(R.string.hint_upload_photo))
        onClick()
        Ads.showNativeAd(ad_small_template,this@PhotoUploadActivity)
    }

    private fun onClick() {
        editResPhotoUploadImageView.setOnClickListener {
            showDialog(this@PhotoUploadActivity)
        }
        editResPhotoUploadButton.setOnClickListener {
            showDialog(this@PhotoUploadActivity)
        }
        editResChangePhotoButton.setOnClickListener {
            changeClickStatus = true
            showDialog(this@PhotoUploadActivity)
        }
        photoDeleteButton.setOnClickListener {
            deletePhoto()
        }
    }

    fun makeHTTPCall() {

        val client = AsyncHttpClient()
        client.post("http://my.bdjobs.com/apps/mybdjobs/v1/upload_img.aspx", params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header?>?, responseBody: ByteArray) {

                try {
                    val response = String(responseBody)

                    Log.d("dgdsgdghj", " response ${response}")

                    val gson = Gson()
                    val photoUploadModel = gson.fromJson(response, PhotoUploadResponseModel::class.java)
                    val photoUrl = photoUploadModel.data?.get(0)?.path
                    Log.d("dgdsgdghj", photoUrl)

                    bdjobsUserSession.updateUserPicUrl(photoUrl?.trim().toString())

                    Log.d("PhotoUploda", "response ${photoUploadModel.message} ")
                    noPhotoTV?.text = "You can change or delete your photo"
                    photoInfoTV?.hide()
                    editResPhotoUploadButton?.hide()
                    editResChangePhotoButton?.show()
                    photoDeleteButton?.show()
                    progressDialog?.dismiss()

                    if (ic_edit_photo.isVisible) {
                        PicassoTools().clearCache(Picasso.get())
                        toast("Photo has been updated successfully.")

                    } else {

                        toast(photoUploadModel.message.toString())
                    }

                    ic_edit_photo?.show()
                } catch (e: Exception) {

                    logException(e)

                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header?>?, responseBody: ByteArray?, error: Throwable?) {
                try {
                    Log.e("photoAPI", error?.message)
                } catch (e: Exception) {
                    logException(e)
                }


            }
        })
    }

    private fun uploadPhoto() {
        progressDialog.setMessage("Uploading Photo..")
        progressDialog.show()
        progressDialog.setCancelable(false)
        ApiServiceMyBdjobs.create().getPhotoInfo(bdjobsUserSession.userId, bdjobsUserSession.decodId).enqueue(object : Callback<PhotoInfoModel> {
            override fun onFailure(call: Call<PhotoInfoModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<PhotoInfoModel>, response: Response<PhotoInfoModel>) {
                try {
                    Log.d("PhotoUpload", " response message ${response.body()!!}")
                    Log.d("PhotoUpload", " response statuscode ${response.body()!!.statuscode}")
                    Log.d("PhotoUpload", " response message ${response.body()!!.message}")


                    if (response.body()!!.statuscode.equals("0", true)) {

                        userId = response.body()?.data?.get(0)?.userId
                        decodeId = response.body()?.data?.get(0)?.decodId
                        folderName = response.body()?.data?.get(0)?.folderName
                        folderId = response.body()?.data?.get(0)?.folderId
                        imageName = response.body()?.data?.get(0)?.imageName
                        isResumeUpdate =response.body()?.data?.get(0)?.isResumeUpdate
                        params.put("Image", encodedString)
                        params.put("userid", bdjobsUserSession.userId)
                        params.put("decodeid", bdjobsUserSession.decodId)
                        params.put("folderName", folderName)
                        params.put("folderId", folderId)
                        params.put("imageName", imageName)
                        params.put("isResumeUpdate", isResumeUpdate)
                        params.put("status", "upload")
                        makeHTTPCall()

                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        })


    }


    private fun deletePhoto() {

        val builder = AlertDialog.Builder(this@PhotoUploadActivity)

        builder.setPositiveButton("Confirm") { dialog, which ->
            progressDialog.setMessage("Deleting Photo.....")
            progressDialog.show()
            progressDialog.setCancelable(false)
            ApiServiceMyBdjobs.create()
                    .getPhotoInfo(bdjobsUserSession.userId, bdjobsUserSession.decodId)
                    .enqueue(object : Callback<PhotoInfoModel> {
                        override fun onResponse(call: Call<PhotoInfoModel>, response: Response<PhotoInfoModel>) {

                            try {
                                Log.d("dgdsgdghj", "getPhotoInfo response ${response.body()!!} ")

                                if (response.body()!!.statuscode.equals("0", true)) {


                                    folderName = response.body()?.data?.get(0)?.folderName
                                    folderId = response.body()?.data?.get(0)?.folderId
                                    imageName = response.body()?.data?.get(0)?.imageName

                                    reqParams.put("Image", encodedString)
                                    reqParams.put("userid", bdjobsUserSession.userId)
                                    reqParams.put("decodeid", bdjobsUserSession.decodId)
                                    reqParams.put("folderName", folderName)
                                    reqParams.put("folderId", folderId)
                                    reqParams.put("imageName", imageName)
                                    reqParams.put("isResumeUpdate", isResumeUpdate)
                                    reqParams.put("status", "delete")

                                    makeHTTPCallForDelete()

                                } else {
                                    progressDialog.dismiss()
                                    toast("Failed ")
                                }
                            } catch (e: Exception) {
                                logException(e)
                            }
                        }

                        override fun onFailure(call: Call<PhotoInfoModel>, t: Throwable) {
                            progressDialog.dismiss()
                            Log.e("photoAPI", t.message)
                        }
                    })
        }



        builder.setNegativeButton("cancel") { dialog, which ->

            dialog.dismiss()
        }


        val dialog = builder.create()
        dialog?.setCancelable(false)
        dialog?.setTitle("Are you sure to delete this photo?")
        dialog?.show()

    }


    fun makeHTTPCallForDelete() {

        val client = AsyncHttpClient()
        client.post("http://my.bdjobs.com/apps/mybdjobs/v1/upload_img.aspx", reqParams, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {

                try {
                    val response = String(responseBody)

                    Log.d("Deltete", " response ${response}")


                    Log.d("rakib", "photo deleted")

                    val gson = Gson()
                    val photoUploadModel = gson.fromJson(response, PhotoUploadResponseModel::class.java)

                    progressDialog?.dismiss()
                    noPhotoTV?.text = "No photo is uploaded yet"
                    photoInfoTV?.text = "Upload JPG, GIF, PNG or BMP Max size of photo is 3MB"
                    photoInfoTV?.show()
                    editResPhotoUploadButton?.show()
                    editResChangePhotoButton?.hide()
                    ic_edit_photo?.hide()
                    photoDeleteButton?.hide()
                    editResPhotoUploadImageView?.setImageResource(R.drawable.ic_photo_upload)
                    bdjobsUserSession.updateUserPicUrl("")
                    PicassoTools().clearCache(Picasso.get())
                    toast(photoUploadModel.message.toString())

                    Log.d("Deltete", "response dlelete ${photoUploadModel.message} ")
                } catch (e: Exception) {
                    logException(e)
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {

                try {
                    try {
                        Log.e("Deltete", error.message)
                    } catch (e: Exception) {
                        logException(e)
                    }
                    try {
                        toast(error.message!!)
                    } catch (e: Exception) {
                        logException(e)
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        })
    }


    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, System.currentTimeMillis().toString() + ".jpg")
        val mCapturedImageURI = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intentPicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI)
        startActivityForResult(intentPicture, REQ_CAMERA_IMAGE)
    }

    private fun RequestPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA)) {

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), MY_PERMISSIONS_REQUEST_CAMERA)

            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CAMERA),
                        MY_PERMISSIONS_REQUEST_CAMERA)
            }

        } else {
            openCamera()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (Constants.isDirectCall) finish()
        clPhotoUpload.closeKeyboard(this@PhotoUploadActivity)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()

                } else {

                }
                return
            }
        }
    }


    fun performFileSearch() {
        //gkjhkgh
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_SELECT_IMAGE)
    }


    private fun getLastImagePath(): String {
        val imageColumns = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
        val imageOrderBy = MediaStore.Images.Media._ID + " DESC"
        val imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy)
        if (imageCursor.moveToFirst()) {
            val id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID))
            val fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA))
            Log.d("Path", fullPath)
            return fullPath
        } else {
            return ""
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("dfgh", "requestCode: $requestCode, resultCode:$resultCode, data:$data")

        try {
            if (resultCode != RESULT_CANCELED) {

                if (requestCode == REQ_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {

                    var fileUri: Uri? = null
                    val selectedImageUri = data.data
                    val url = data.data!!.toString()
                    if (url.startsWith("content://com.google.android.apps") || url.startsWith("content://com.android.providers") || url.startsWith("content://media/external")) {

                        try {
                            val `is` = contentResolver.openInputStream(selectedImageUri!!)
                            if (`is` != null) {
                                deleteCache(applicationContext)

                                val options = BitmapFactory.Options()
                                options.inSampleSize = 2
                                bitmap = BitmapFactory.decodeStream(`is`,null,options)

//                                bitmap = BitmapFactory.decodeStream(`is`)

                                Log.d("rakib", "${BitmapCompat.getAllocationByteCount(bitmap!!)}")

                                if (bitmap != null) {
                                    val tempUri = getImageUri(this@PhotoUploadActivity, bitmap!!)
                                    // CALL THIS METHOD TO GET THE ACTUAL PATHa
                                    var finalFile: File? = null
                                    try {
                                        finalFile = File(getRealPathFromURI(tempUri))
                                        fileUri = Uri.fromFile(finalFile)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                } else {
                                    Toast.makeText(applicationContext, "Invalid Image has been selected! Please Choose image again", Toast.LENGTH_LONG).show()
                                }

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    val myDirectory = File("/sdcard/BDJOBS")
                    if (!myDirectory.exists()) {
                        myDirectory.mkdirs()
                    }

                    val file = File("/sdcard/BDJOBS/bdjobsProfilePic.jpg")
                    if (file.exists()) {
                        val deleted = file.delete()
                    }
                    val destinationUri = Uri.fromFile(File("/sdcard/BDJOBS/bdjobsProfilePic.jpg"))
                    try {
                        UCrop.of(fileUri!!, destinationUri).withAspectRatio(9f, 10f).start(this@PhotoUploadActivity)
                    } catch (e: Exception) {
                        logException(e)
                    }
                }

                Log.d("dfgh", " New call resultCode " + resultCode + " RESULT_OK " + RESULT_OK +
                        "requestCode " + requestCode + "  UCrop.REQUEST_CROP " + UCrop.REQUEST_CROP + " resultData " + data)

                if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP && data != null) {
                    resultUri = UCrop.getOutput(data)!!

                    editResPhotoUploadImageView.loadCircularImageFromUrlWithoutCach(resultUri.toString())

                    uploadPhoto()

                    editResPhotoUploadButton.isEnabled = true
                    editResPhotoUploadButton.show()
                    editResChangePhotoButton.hide()

                    dialog?.dismiss()
                    val path = resultUri.path

                    val file = File(path)
                    val size = file.length()
                    val fileSizeInKB = size / 1024
                    // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                    val fileSizeInMB = fileSizeInKB / 1024
                    if (fileSizeInMB > 3) {
                        Toast.makeText(this, "Image is greater than 3MB", Toast.LENGTH_SHORT).show()
                    } else if (fileSizeInMB <= 3) {

                        doAsync {
                            try {
                                var options: BitmapFactory.Options? = null
                                options = BitmapFactory.Options()
                                options.inSampleSize = 3

                                bitmap = BitmapFactory.decodeFile(path, options)
                                val stream = ByteArrayOutputStream()
                                // Must compress the Image to reduce image size to make upload easy
                                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                                val byte_arr = stream.toByteArray()
                                // Encode Image to String
                                encodedString = Base64.encodeToString(byte_arr, 0)
                            } catch (e: Exception) {
                                error("SEMVcb $e")
                            }
                        }

                    }


                } else if (resultCode == UCrop.RESULT_ERROR) {
                    val cropError = UCrop.getError(data!!)
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
                    UCrop.of(SourceUri, destinationUri).withAspectRatio(9f, 10f).start(this@PhotoUploadActivity)
                }

            }
        } catch (e: Exception) {
            logException(e)
        }


    }


    private fun showDialog(activity: Activity) {
        dialog = Dialog(activity)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.edit_res_photo_diaolg_layout)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        //window.setGravity(Gravity.CENTER);
        val deleteImageView = dialog?.findViewById<ImageView>(R.id.deleteIV)
        val photoUploadTV = dialog?.findViewById<TextView>(R.id.photoUploadTV)
        val cameraButton = dialog?.findViewById<Button>(R.id.camera_button)
        val galleryButton = dialog?.findViewById<TextView>(R.id.gallery_button)
        val ad_small_template = dialog?.findViewById<TemplateView>(R.id.ad_small_template)
        Ads.showNativeAd(ad_small_template!!, this@PhotoUploadActivity)


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
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
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
