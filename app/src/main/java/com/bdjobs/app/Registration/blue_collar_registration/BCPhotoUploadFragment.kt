package com.bdjobs.app.Registration.blue_collar_registration

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.app.Fragment
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
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
import com.bdjobs.app.API.ModelClasses.ResendOtpModel
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationBaseActivity
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.callHelpLine
import com.bdjobs.app.Utilities.loadCircularImageFromUrl
import com.bdjobs.app.Utilities.showProgressBar
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.yalantis.ucrop.UCrop
import cz.msebera.android.httpclient.Header
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import kotlinx.android.synthetic.main.fav_list_layout.*
import kotlinx.android.synthetic.main.footer_bc_layout.*
import kotlinx.android.synthetic.main.fragment_bc_photo_upload.*
import kotlinx.android.synthetic.main.photo_upload_dialog_layout.*
import org.jetbrains.anko.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import retrofit2.Callback
import java.io.UnsupportedEncodingException


class BCPhotoUploadFragment : Fragment() {


    private lateinit var registrationCommunicator: RegistrationCommunicator
    private lateinit var returnView: View
    internal var filePaths = ArrayList<String>()
    private val REQ_CAMERA_IMAGE = 40
    private val READ_REQUEST_CODE = 42
    private val MY_PERMISSIONS_REQUEST_CAMERA = 2
    private lateinit var bitmap: Bitmap
    private lateinit var encodedString: String
    private lateinit var userId: String
    private lateinit var decodeId: String
    private lateinit var folderName: String
    private lateinit var folderId: String
    private lateinit var imageName: String
    private lateinit var isResumeUpdate: String
    internal var params = RequestParams()
    private lateinit var dialog: Dialog

    /*  val registrationBaseActivity = RegistrationBaseActivity()*/
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


    private fun onClick() {

        completeButton.setOnClickListener {

            if (encodedString.isBlank()) {

                registrationCommunicator.bcGoToStepCongratulation()
            } else {

                registrationCommunicator.showProgressBar()


                ApiServiceMyBdjobs.create().getPhotoInfo(registrationCommunicator.getUserId(), registrationCommunicator.getDecodeId()).enqueue(object : Callback<PhotoInfoModel> {
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
                            params.put("userid", registrationCommunicator.getUserId())
                            params.put("decodeid", registrationCommunicator.getDecodeId())
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

        photoUploadImageView.setOnClickListener {


            showDialog(activity)

        }

         supportTextView.setOnClickListener {

            activity.callHelpLine()

         }

         bcHelpLineLayout.setOnClickListener {

             activity.callHelpLine()
         }

    }

    private fun initialization() {

        registrationCommunicator = activity as RegistrationCommunicator


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
                val bdjobsUserSession = BdjobsUserSession(activity)
                bdjobsUserSession.updateUserPicUrl(photoUrl)

                toast(photoUploadModel.message)
                registrationCommunicator.hideProgressBar()
                registrationCommunicator.bcGoToStepCongratulation()


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
        Log.d("dfgh", "  onActivityResult called")


        Log.d("FragmentResultPhoto", "requestCode: $requestCode, resultCode:$resultCode, data:$resultData")

        if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO && resultCode == Activity.RESULT_OK && resultData != null) {

            Log.d("dfgh", "  1 condition")
            var uri: Uri? = null
            uri = Uri.fromFile(File(resultData.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA).get(0)))
            Log.d("dfgh", "Uri: " + uri!!.toString())
            Log.d("PhotoUpload", "Uri: " + uri!!.toString())
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
            UCrop.of(uri, destinationUri).withAspectRatio(9f, 10f).start(activity)
        }

        Log.d("dfgh", " resultCode $resultCode RESULT_OK $RESULT_OK " +
                "requestCode $requestCode  UCrop.REQUEST_CROP ${UCrop.REQUEST_CROP} resultData $resultData ")

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP && resultData != null) {

            Log.d("dfgh", "  3 Crop condition")

            val resultUri = UCrop.getOutput(resultData)
            val tempURI = Uri.fromFile(File("/sdcard/"))
            photoUploadImageView.loadCircularImageFromUrl(tempURI.toString())
            photoUploadImageView.loadCircularImageFromUrl(resultUri.toString())
            /* photoUploadImageView.imageURI = tempURI
             photoUploadImageView.imageURI = resultUri*/


            dialog.dismiss()

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

            Log.d("ImageUpload", "")

            val cropError = UCrop.getError(resultData!!)
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
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.photo_upload_dialog_layout)
        dialog.getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val deleteImageView = dialog.findViewById<ImageView>(R.id.deleteIV)
        val cameraButton = dialog.findViewById<Button>(R.id.camera_button)
        val galleryButton = dialog.findViewById<TextView>(R.id.gallery_button)


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


    private fun RequestPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.CAMERA)) {

                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), MY_PERMISSIONS_REQUEST_CAMERA)

            } else {
                ActivityCompat.requestPermissions(activity,
                        arrayOf(Manifest.permission.CAMERA),
                        MY_PERMISSIONS_REQUEST_CAMERA)
            }

        } else {

            openCamera()

        }
    }


    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, System.currentTimeMillis().toString() + ".jpg")
        val mCapturedImageURI = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intentPicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI)
        startActivityForResult(intentPicture, REQ_CAMERA_IMAGE)
    }


}
