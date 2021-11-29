package com.bdjobs.app.ManageResume

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.UploadResume
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.gms.ads.AdRequest
import com.otaliastudios.cameraview.CameraView.PERMISSION_REQUEST_CODE
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.models.sort.SortingTypes
import kotlinx.android.synthetic.main.fragment_upload_resume.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private const val ACTION_OPEN_DOCUMENT_REQUEST = 0x1046

class UploadResumeFragment : Fragment() {

    companion object {
        const val ALL_FILES_ACCESS_REQUEST = 2296
    }

    private lateinit var communicator: ManageResumeCommunicator
    private lateinit var bdjobsUserSession: BdjobsUserSession
    val filePaths: ArrayList<String> = ArrayList()

    private lateinit var progressDialog : ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_upload_resume, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        progressDialog = ProgressDialog(activity)
        communicator = activity as ManageResumeCommunicator
        bdjobsUserSession = BdjobsUserSession(activity)

        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)

    }

    override fun onResume() {
        super.onResume()

        fetchPersonalizedResumeStat()

        submitTV?.setOnClickListener {
            if (bdjobsUserSession.isCvPosted.equals("True",ignoreCase = true)) {
                if (!checkPermission()){
                    requestPermission()
                } else {
                    if (SDK_INT >= Build.VERSION_CODES.R) {
                        openStorageAccess()
                    } else {
                        browseFile()
                    }
                }
            } else {
                try {
                    if (isAdded) {
                        Toast.makeText(
                            context,
                            "Please post Bdjobs resume to upload Personalized Resume",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e:Exception){}
            }

        }
        backIV.setOnClickListener {
            communicator.backButtonPressed()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun fetchPersonalizedResumeStat() {
        activity.showProgressBar(loadingProgressBar)
        cl_personalized_resume_stat.hide()
        GlobalScope.launch {
            try {
                val response = ApiServiceMyBdjobs.create().personalizedResumeStat(
                    bdjobsUserSession.userId,
                    bdjobsUserSession.decodId,
                    bdjobsUserSession.isCvPosted
                )

                try {
                    if (activity!=null) {
                        runOnUiThread {

                            if (!activity.isFinishing) {
                                activity.stopProgressBar(loadingProgressBar)
                            }

                            cl_personalized_resume_stat.show()
                        }
                    }
                } catch (e: Exception) {
                }


                if (response.statuscode == "0" && response.message == "Success") {
                    val data = response.data!![0]

                    val statCalculatedFrom = if (data.personalizedCalculatedFromDate!="") {
                        try {
                            formatDateVP(data.personalizedCalculatedFromDate)
                        } catch (e: Exception) {
                            formatDateVP(data.personalizedCalculatedFromDate!!,
                                SimpleDateFormat("M/dd/yyyy", Locale.US)
                            )
                        }
                    } else ""

                    try {
                        if (activity!=null) {
                            runOnUiThread {

                                if (statCalculatedFrom.isNotEmpty()) {

                                    cv_no_personalized_resume.hide()
                                    tv_stat_calculated_from.show()
                                    tv_label_stat_personalized_resume.show()
                                    cl_stat_personalized_resume.show()


                                    tv_personalized_resume_view_count.text = if (data.personalizedViewed!="0") data.personalizedViewed else "-"
                                    tv_personalized_resume_download_count.text = if (data.personalizedDownload!="0")  data.personalizedDownload else "-"
                                    tv_personalized_resume_emailed_count.text = if (data.personalizedEmailed!="0")  data.personalizedEmailed else "-"

                                    tv_stat_calculated_from.text =
                                        "Statistics calculated from $statCalculatedFrom"

                                    //for production build 2.9.0
    //                            Timber.tag("UploadResumeFragment").d("Stat not null")
    //                            tv_stat_calculated_from.hide()
    //                            tv_label_stat_personalized_resume.hide()
    //                            cl_stat_personalized_resume.hide()

                                } else {
                                    tv_stat_calculated_from.hide()
                                    tv_label_stat_personalized_resume.hide()
                                    cl_stat_personalized_resume.hide()

                                    cv_no_personalized_resume.show()
                                }


                            }
                        }
                    } catch (e: Exception) {
                    }


                } else {
                    try {
                        if (activity!=null) {
                            runOnUiThread {
                                toast("Sorry, personalized resume stat fetching failed!")
                            }
                        }
                    } catch (e: Exception) {
                    }

                }

            } catch (e: Exception) {
                Timber.e("Exception while fetching personalized resume stat: ${e.localizedMessage}")
                try {
                    if (activity!=null) {
                        runOnUiThread {
                            activity.stopProgressBar(loadingProgressBar)
                            toast("Sorry, personalized resume stat fetching failed: ${e.localizedMessage}")
                        }
                    }
                } catch (e: Exception) {
                }
            }


        }
    }

    private fun checkPermission(): Boolean {
        /*return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val checkReadStorage = ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE)
            val checkWriteStorage = ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE)
            checkReadStorage == PackageManager.PERMISSION_GRANTED && checkWriteStorage == PackageManager.PERMISSION_GRANTED
        }*/
        val checkReadStorage = ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE)
        val checkWriteStorage = ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE)
        return checkReadStorage == PackageManager.PERMISSION_GRANTED && checkWriteStorage == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        /*if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", getApplicationContext().packageName))
                startActivityForResult(intent, ALL_FILES_ACCESS_REQUEST)
            } catch (e: java.lang.Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, ALL_FILES_ACCESS_REQUEST)
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(activity, arrayOf(WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }*/
        ActivityCompat.requestPermissions(activity, arrayOf(WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }

    private fun browseFile() {
        val wordFileTypes = arrayOf("doc", "docx")
        val pdfFileTypes = arrayOf("pdf")
        FilePickerBuilder.instance.setMaxCount(1)
            .setActivityTheme(R.style.LibAppTheme)
            .enableDocSupport(false)
            .sortDocumentsBy(SortingTypes.name)
            .showFolderView(true)
            .addFileSupport("MS WORD FILES", wordFileTypes, R.drawable.ic_microsoft_word)
            .addFileSupport("PDF FILES", pdfFileTypes, R.drawable.ic_pdf)
            .pickFile(activity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (SDK_INT >= Build.VERSION_CODES.R && requestCode == ACTION_OPEN_DOCUMENT_REQUEST && resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            fileUri?.let { uri ->
                GlobalScope.launch {
                    withContext(Dispatchers.IO){
                        try {
                            val inputStream = context.contentResolver.openInputStream(uri)
                            val bytes = inputStream?.readBytes()
                            val fileExtensions = FileUtil.instance.getFileExtension(context.contentResolver.getType(uri)!!)
                            val file = FileUtil.instance.writeBytes(context, bytes!!, fileExtensions)
                            checkFileSize(Uri.fromFile(file))
                        } catch (ex: java.lang.Exception){
                            ex.printStackTrace()
                        }
                    }
                }
            }
        } else if(SDK_INT < Build.VERSION_CODES.R && requestCode == FilePickerConst.REQUEST_CODE_DOC && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_DOCS)?.get(0)
            if (uri != null) {
                GlobalScope.launch {
                    checkFileSize(uri)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                val readStoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val writeStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (readStoragePermission && writeStoragePermission) {
                    // perform action when allow permission success
//                    browseFile()
                    if (SDK_INT >= Build.VERSION_CODES.R) {
                        openStorageAccess()
                    } else {
                        browseFile()
                    }
                } else {
                    Toast.makeText(activity, "Allow permission for storage access!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun checkFileSize(uri: Uri) {
        val fileInformation = FileInformation()
        val fileInfo = FileUtil.instance.getFileInformation(getApplicationContext(), uri)
        val size = fileInfo.fileSize
        val fileSizeInKB = size / 1024

        if (fileInfo.extension!!.equalIgnoreCase("pdf") || fileInfo.extension!!.equalIgnoreCase("doc") || fileInfo.extension!!.equalIgnoreCase("docx")
        ) {
            //Log.d("UploadResume", "UploadResume size: ${fileSizeInKB} type: ${fileinfo.extension}")
            when {
                fileSizeInKB == 0L -> {
                    toast("It is an invalid file")
                }
                fileSizeInKB > 1024 -> {
                    toast("You can not upload file more than 1MB in size")
                }
                else -> {
                    val mediaType = "application/" + fileInfo.extension
                    val filePath = uri.path
                    //Log.d("UploadResume", "filePath= $filePath")
                    //val requestFile = File(filePath).asRequestBody(mediaType.toMediaType())
                    val requestFile = FileUtil.instance.getFileFromUri(activity, uri).asRequestBody(mediaType.toMediaType())
        //               RequestBody.create(MediaType?.parse(mediaType), File(filePath))
                    val multipartBodyPart = MultipartBody.Part.createFormData("File", fileInfo.fileName, requestFile)

                    val userid = createPartFromString(bdjobsUserSession.userId!!)
                    val decodeid = createPartFromString(bdjobsUserSession.decodId!!)
                    val status = createPartFromString("upload")
                    val fileExtension = createPartFromString(fileInfo.extensionwithDot!!)
                    val fileType = createPartFromString(fileInfo.type!!)
                    val fileName = createPartFromString(fileInfo.fileName!!)

                    val map: HashMap<String, RequestBody> = HashMap()
                    map["userid"] = userid
                    map["decodeid"] = decodeid
                    map["status"] = status
                    map["fileExtension"] = fileExtension
                    map["fileType"] = fileType
                    map["fileName"] = fileName
                    //Log.d("UploadResume", "userid = $userid\ndecodeid= $decodeid\nstatus= $status\nfileExtension= $fileExtension\nfileType= $fileType\nfileName= $fileName")
                    uploadCVtoServer(multipartBodyPart, map, uri)
                }
            }
        } else {
            try {
                if (isAdded) {
                    Toast.makeText(
                        context,
                        "Please select a valid pdf or doc or docx file",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e:Exception){}
        }
    }

    private suspend fun uploadCVtoServer(
        multipartBodyPart: MultipartBody.Part,
        map: HashMap<String, RequestBody>,
        uri: Uri
    ) {

        withContext(Dispatchers.Main){
            progressDialog.setMessage("Please Wait..")
            progressDialog.setTitle("Saving")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        ApiServiceMyBdjobs.create().uploadCV(partMap = map, file = multipartBodyPart).enqueue(object : Callback<UploadResume> {
            override fun onFailure(call: Call<UploadResume>, t: Throwable) {
                try {
                    progressDialog.dismiss()
                    error("onFailure", t)
                    Log.e("UploadResume", t.toString())
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<UploadResume>, response: Response<UploadResume>) {
                try {
                    progressDialog.dismiss()
                    toast(response.body()?.message!!)
                    //Log.d("UploadResume", "response: ${response.body()}")
                    bdjobsUserSession.updateUserCVUploadStatus("0")
                    communicator.gotoDownloadResumeFragment()
                    val body = response.body()
                    if (SDK_INT >= Build.VERSION_CODES.R && response.isSuccessful && body?.statuscode == "0"){
                        uri.path?.let {
                            FileUtil.instance.deleteFile(File(it))
                            Log.e("UploadResume", "Temp file deleted")
                        }
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        })
    }

    private fun createPartFromString(s: String): RequestBody {
        return s.toRequestBody("text/plain".toMediaType())
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDateVP(lastUpdate: String?,format: SimpleDateFormat= SimpleDateFormat("M/dd/yyyy HH:mm:ss a")): String {
        var lastUpdate1 = lastUpdate
        var formatter = format
        val date = formatter.parse(lastUpdate1!!)
        formatter = SimpleDateFormat("dd MMM yyyy")
        lastUpdate1 = formatter.format(date!!)
        Timber.d("Last updated at: $lastUpdate1")
        return lastUpdate1
    }

    private fun openStorageAccess() {
        val mimeTypes = arrayOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword", "application/pdf")
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        }
        startActivityForResult(intent, ACTION_OPEN_DOCUMENT_REQUEST)
    }
}
