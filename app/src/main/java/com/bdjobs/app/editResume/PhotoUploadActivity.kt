package com.bdjobs.app.editResume

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.PhotoInfoModel
import com.bdjobs.app.API.ModelClasses.PhotoUploadResponseModel
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.loadCircularImageFromUrl
import com.bdjobs.app.Utilities.show
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.yalantis.ucrop.UCrop
import cz.msebera.android.httpclient.Header
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import kotlinx.android.synthetic.main.activity_photo_upload.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class PhotoUploadActivity : AppCompatActivity() {


    private lateinit var registrationCommunicator: RegistrationCommunicator
    private lateinit var returnView: View
    internal var filePaths = ArrayList<String>()
    private val REQ_CAMERA_IMAGE = 40
    private val READ_REQUEST_CODE = 42
    private val MY_PERMISSIONS_REQUEST_CAMERA = 2
    private lateinit var bitmap: Bitmap
    private var encodedString: String = ""
    private var userId: String = ""
    private var decodeId: String = ""
    private var folderName: String = ""
    private var folderId: String = ""
    private var imageName: String = ""
    private var isResumeUpdate: String = ""
    internal var params = RequestParams()
    internal var reqParams = RequestParams()
    private lateinit var dialog: Dialog
    private lateinit var bdjobsUserSession: BdjobsUserSession
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_upload)

        bdjobsUserSession = BdjobsUserSession(this@PhotoUploadActivity)
        progressDialog = ProgressDialog(this@PhotoUploadActivity)

        onClick()


    }


    private fun onClick() {

        regPhotoUploadImageView.setOnClickListener {

            showDialog(this@PhotoUploadActivity)


        }

        regPhotoUploadButton.setOnClickListener {

            if (encodedString.isBlank()) {

                registrationCommunicator.bcGoToStepCongratulation()
            } else {


                progressDialog.show()
                progressDialog.setTitle("Photo Uploading.......")


                ApiServiceMyBdjobs.create().getPhotoInfo(bdjobsUserSession.userId, bdjobsUserSession.decodId).enqueue(object : Callback<PhotoInfoModel> {
                    override fun onFailure(call: Call<PhotoInfoModel>, t: Throwable) {

                        Log.d("PhotoUpload", " onFailure ${t.message}")
                    }

                    override fun onResponse(call: Call<PhotoInfoModel>, response: Response<PhotoInfoModel>) {

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
                            params.put("userid", bdjobsUserSession.userId)
                            params.put("decodeid", bdjobsUserSession.decodId)
                            params.put("folderName", folderName)
                            params.put("folderId", folderId)
                            params.put("imageName", imageName)
                            params.put("isResumeUpdate", isResumeUpdate)
                            params.put("status", "upload")




                            makeHTTPCall()

                        }


                    }


                })


            }

        }

        regChangePhotoButton.setOnClickListener {

            showDialog(this@PhotoUploadActivity)

        }

        photoDeleteButton.setOnClickListener {


            deletePhoto()


        }


    }


    fun makeHTTPCall() {

        val client = AsyncHttpClient()
        client.post("http://my.bdjobs.com/apps/mybdjobs/v1/upload_img.aspx", params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                /*  progressDialog.dismiss()*/
                var obj: JSONObject? = null
                /*  try {*/
                val response = String(responseBody)

                Log.d("dgdsgdghj", " response ${response}")

                /*   obj = JSONObject(response)

                   val msg = obj.getString("message")

                  val dataObj = obj.getJSONArray("data")*/


                val gson = Gson()
                val photoUploadModel = gson.fromJson(response, PhotoUploadResponseModel::class.java)
                val photoUrl = photoUploadModel.data[0].path
                Log.d("dgdsgdghj", " $photoUrl")
                val bdjobsUserSession = BdjobsUserSession(this@PhotoUploadActivity)
                bdjobsUserSession.updateUserPicUrl(photoUrl)


                toast(photoUploadModel.message)
                progressDialog.dismiss()
                Log.d("PhotoUploda", "response ${photoUploadModel.message} ")

                noPhotoTV.text = "You can change or delete your photo"
                photoInfoTV.hide()
                regPhotoUploadButton.hide()
                regChangePhotoButton.show()


                /* } catch (e: JSONException) {
                     e.printStackTrace()
                     Log.e("PhotoAPI", e.message)
                 } catch (e: UnsupportedEncodingException) {
                     e.printStackTrace()
                 }*/


            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray,
                                   error: Throwable) {
                /* progressDialog.dismiss()*/
                Log.e("photoAPI", error.message)
                toast(error.message!!)
            }
        })
    }


    private fun deletePhoto() {


        /*  val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setTitle("Are you sure to delete this data?")
        dialog.show()*/


        val builder = AlertDialog.Builder(this@PhotoUploadActivity)


        builder.setMessage("Are you sure you want to delet this photo?")


        builder.setPositiveButton("YES") { dialog, which ->
            progressDialog.setTitle("Deleting")
            progressDialog.show()
            ApiServiceMyBdjobs.create()
                    .getPhotoInfo(bdjobsUserSession.userId, bdjobsUserSession.decodId)
                    .enqueue(object : Callback<PhotoInfoModel> {
                        override fun onResponse(call: Call<PhotoInfoModel>, response: Response<PhotoInfoModel>) {

                            Log.d("dgdsgdghj", "getPhotoInfo response ${response.body()!!} ")

                            if (response.body()!!.statuscode.equals("0", true)) {


                                folderName = response.body()!!.data[0].folderName
                                folderId = response.body()!!.data[0].folderId
                                imageName = response.body()!!.data[0].imageName

                                /*   ApiServiceMyBdjobs.create().DeletePhoto(folderName, folderId, imageName, isResumeUpdate, "delete", bdjobsUserSession.userId!!, bdjobsUserSession.decodId!!).enqueue(object : Callback<PhotoInfoModel> {
                                       override fun onResponse(call: Call<PhotoInfoModel>, response: Response<PhotoInfoModel>) {

                                           Log.d("dgdsgdghj","DeletePhoto response ${response.body()!!} ")

                                           if (response.body()!!.statuscode.equals("1",true)) {
                                               progressDialog.dismiss()

                                               toast("Photo Deleted")
                                           } else {
                                               progressDialog.dismiss()

                                               toast("Failed")
                                           }

                                       }

                                       override fun onFailure(call: Call<PhotoInfoModel>, t: Throwable) {
                                           progressDialog.dismiss()
                                           Log.e("photoAPI", t.message)
                                       }
                                   })
   */

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
                        }

                        override fun onFailure(call: Call<PhotoInfoModel>, t: Throwable) {
                            progressDialog.dismiss()
                            Log.e("photoAPI", t.message)
                        }
                    })
        }



        builder.setNegativeButton("No") { dialog, which ->

            dialog.dismiss()
        }


        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setTitle("Are you sure to delete this data?")
        dialog.show()

    }


    fun makeHTTPCallForDelete() {

        val client = AsyncHttpClient()
        client.post("http://my.bdjobs.com/apps/mybdjobs/v1/upload_img.aspx", reqParams, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                /*  progressDialog.dismiss()*/
                var obj: JSONObject? = null
                /*  try {*/
                val response = String(responseBody)

                Log.d("Deltete", " response ${response}")

                /*   obj = JSONObject(response)

                   val msg = obj.getString("message")

                  val dataObj = obj.getJSONArray("data")*/


                val gson = Gson()
                val photoUploadModel = gson.fromJson(response, PhotoUploadResponseModel::class.java)
                val photoUrl = photoUploadModel.data[0].path
                Log.d("Deltete", " $photoUrl")
                val bdjobsUserSession = BdjobsUserSession(this@PhotoUploadActivity)
                bdjobsUserSession.updateUserPicUrl(photoUrl)


                toast(photoUploadModel.message)
                progressDialog.dismiss()
                Log.d("Deltete", "response dlelete ${photoUploadModel.message} ")

                noPhotoTV.text = "You can change or delete your photo"
                photoInfoTV.hide()
                regPhotoUploadButton.hide()
                regChangePhotoButton.show()


                /* } catch (e: JSONException) {
                     e.printStackTrace()
                     Log.e("PhotoAPI", e.message)
                 } catch (e: UnsupportedEncodingException) {
                     e.printStackTrace()
                 }*/


            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray,
                                   error: Throwable) {
                /* progressDialog.dismiss()*/
                Log.e("Deltete", error.message)
                toast(error.message!!)
            }
        })
    }

    /*  private fun initializer() {
          progressDialog = ProgressDialog(this)
          progressDialog.setMessage("Please Wait..")
          progressDialog.setTitle("Saving")
          progressDialog.setCancelable(false)

          photoBrowseBTN = findViewById(R.id.photoBrowseBTN) as AppCompatButton
          deletePhotoBTN = findViewById(R.id.deletePhotoBTN) as AppCompatButton
          cameraBTN = findViewById(R.id.cameraBTN) as AppCompatButton
          UploadBTN = findViewById(R.id.UploadBTN) as AppCompatButton

          photoIMGV = findViewById(R.id.photoIMGV) as ImageView
      }*/

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

            // Should we show an explanation?
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()

                } else {
                    /* Toasty.error(this@PhotoUpload, "Permission Denied").show()*/
                }
                return
            }
        }
    }


    fun performFileSearch() {


        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(filePaths)
                .setActivityTheme(R.style.AppTheme)
                .enableCameraSupport(false)
                .pickPhoto(this)
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


        if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO && resultCode == Activity.RESULT_OK && data != null) {

            var uri: Uri? = null
            uri = Uri.fromFile(File(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)[0]))
            Log.d("dfgh", "Uri: " + uri!!.toString())
            val myDirectory = File("/sdcard/BDJOBS")
            if (!myDirectory.exists()) {
                myDirectory.mkdirs()
            }

            val file = File("/sdcard/BDJOBS/bdjobsProfilePic.jpg")
            if (file.exists()) {
                val deleted = file.delete()
            }
            val destinationUri = Uri.fromFile(File("/sdcard/BDJOBS/bdjobsProfilePic.jpg"))
            UCrop.of(uri, destinationUri).withAspectRatio(9f, 10f).start(this@PhotoUploadActivity)
        }

        Log.d("dfgh", " resultCode " + resultCode + " RESULT_OK " + RESULT_OK +
                "requestCode " + requestCode + "  UCrop.REQUEST_CROP " + UCrop.REQUEST_CROP + " resultData " + data)

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP && data != null) {
            val resultUri = UCrop.getOutput(data)
            val tempURI = Uri.fromFile(File("/sdcard/"))


            regPhotoUploadImageView.loadCircularImageFromUrl(tempURI.toString())
            regPhotoUploadImageView.loadCircularImageFromUrl(resultUri.toString())
            dialog.dismiss()
            val path = resultUri!!.path
            /*  UploadBTN.setVisibility(View.GONE)*/
            val file = File(path)
            val size = file.length()
            val fileSizeInKB = size / 1024
            // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            val fileSizeInMB = fileSizeInKB / 1024
            if (fileSizeInMB > 3) {
                Toast.makeText(this, "Image is greater than 3MB", Toast.LENGTH_SHORT).show()
            } else if (fileSizeInMB <= 3) {
                //getEncodedStringFromImagePath().execute(path)
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

        super.onActivityResult(requestCode, resultCode, data)

    }


    private fun showDialog(activity: Activity) {
        dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.photo_upload_dialog_layout)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val deleteImageView = dialog.findViewById<ImageView>(R.id.deleteIV)
        val photoUploadTV = dialog.findViewById<TextView>(R.id.photoUploadTV)
        val cameraButton = dialog.findViewById<Button>(R.id.camera_button)
        val galleryButton = dialog.findViewById<TextView>(R.id.gallery_button)


        cameraButton.text = "Capture Photo"
        galleryButton.text = "Browse Gallery"
        photoUploadTV.text = "Upload Photo"
        /* dialogButton.setOnClickListener( View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 dialog.dismiss();
             }
         })*/

        /* dialogButton.setOO*/

        deleteImageView.setOnClickListener {

            dialog.dismiss()
        }
        cameraButton.setOnClickListener {

            RequestPermissionAndOpenCamera()

        }

        galleryButton.setOnClickListener {

            performFileSearch()

        }

        dialog.show()

    }

}
