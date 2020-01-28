package com.bdjobs.app.ManageResume

//import droidninja.filepicker.FilePickerBuilder
//import droidninja.filepicker.FilePickerConst
//import droidninja.filepicker.models.sort.SortingTypes
import android.app.Activity
import android.app.Fragment
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.UploadResume
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.logException
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.gms.ads.AdRequest
import com.vincent.filepicker.Constant
import com.vincent.filepicker.activity.NormalFilePickActivity
import com.vincent.filepicker.filter.entity.NormalFile
import kotlinx.android.synthetic.main.fragment_upload_resume.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody


class UploadResumeFragment : Fragment() {
    private lateinit var communicator: ManageResumeCommunicator
    private lateinit var bdjobsUserSession: BdjobsUserSession
    val filePaths: ArrayList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.bdjobs.app.R.layout.fragment_upload_resume, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        communicator = activity as ManageResumeCommunicator
        bdjobsUserSession = BdjobsUserSession(activity)

        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)

    }


    override fun onResume() {
        super.onResume()

        submitTV?.setOnClickListener {
            browseFile()
        }
        backIV.setOnClickListener {
            communicator.backButtonPressed()
        }


    }

    private fun browseFile() {
        val wordFileTypes = arrayOf(".doc", ".docx")
        val pdfFileTypes = arrayOf(".pdf")
//        FilePickerBuilder.instance.setMaxCount(1)
//                .setSelectedFiles(filePaths)
//                .enableDocSupport(false)
//                .sortDocumentsBy(SortingTypes.name)
//                .showFolderView(true)
//                .addFileSupport("MS WORD FILES", wordFileTypes, R.drawable.ic_microsoft_word)
//                .addFileSupport("PDF FILES", pdfFileTypes, R.drawable.ic_pdf)
//                .setActivityTheme(R.style.AppTheme)
//                .pickFile(activity)
        val intent4 =  Intent(activity, NormalFilePickActivity::class.java)
        intent4.putExtra(Constant.MAX_NUMBER, 1)
        intent4.putExtra(NormalFilePickActivity.SUFFIX,  arrayOf<String>("doc", "docx", "pdf"))
        activity.startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Log.d("UploadResume", "requestCode=$requestCode \nresultCode=$resultCode \ndata=$data")
        if (requestCode == Constant.REQUEST_CODE_PICK_FILE && resultCode == Activity.RESULT_OK && data != null) {


            val file : List<NormalFile>? = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE)

            file?.forEach {
                //Log.d("rakib",it.path.toString())
            }

            try {
                val uri = Uri.parse(File(file!![0].path).toString())
                checkFilleSize(uri)
            } catch (e: Exception){
                logException(e)
            }


        }
    }


    private fun checkFilleSize(uri: Uri) {
        val fileInformation = FileInformation()
        val fileinfo = fileInformation.getfileInformation(getApplicationContext(), uri)
        val size = fileinfo.fileSize
        val fileSizeInKB = size / 1024

        if (fileinfo.extension!!.equalIgnoreCase("pdf") || fileinfo.extension!!.equalIgnoreCase("doc") || fileinfo.extension!!.equalIgnoreCase("docx")) {

            //Log.d("UploadResume", "UploadResume size: ${fileSizeInKB} type: ${fileinfo.extension}")

            if (fileSizeInKB == 0L) {
                toast("It is an invalid file")
            } else if (fileSizeInKB > 1024) {
                toast("You can not upload file more than 1MB in size")
            } else {


                val mediaType = "application/" + fileinfo.extension

                val filePath = uri.path

                //Log.d("UploadResume", "filePath= $filePath")


                val requestFile = File(filePath).asRequestBody(mediaType.toMediaType())

//               RequestBody.create(MediaType?.parse(mediaType), File(filePath))

                val multipartBodyPart = MultipartBody.Part.createFormData("File", fileinfo.fileName, requestFile)

                val userid = createPartFromString(bdjobsUserSession.userId!!)
                val decodeid = createPartFromString(bdjobsUserSession.decodId!!)
                val status = createPartFromString("upload")
                val fileExtension = createPartFromString(fileinfo.extensionwithDot!!)
                val fileType = createPartFromString(fileinfo.type!!)
                val fileName = createPartFromString(fileinfo.fileName!!)

                val map: HashMap<String, RequestBody> = HashMap()

                map["userid"] = userid
                map["decodeid"] = decodeid
                map["status"] = status
                map["fileExtension"] = fileExtension
                map["fileType"] = fileType
                map["fileName"] = fileName

                //Log.d("UploadResume", "userid = $userid\ndecodeid= $decodeid\nstatus= $status\nfileExtension= $fileExtension\nfileType= $fileType\nfileName= $fileName")

                uploadCVtoServer(multipartBodyPart, map)

            }
        } else {
            toast("Please select a valid pdf or doc or docx file")
        }
    }

    private fun uploadCVtoServer(multipartBodyPart: MultipartBody.Part, map: HashMap<String, RequestBody>) {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Please Wait..")
        progressDialog.setTitle("Saving")
        progressDialog.setCancelable(false)
        progressDialog.show()

        ApiServiceMyBdjobs.create().uploadCV(
                partMap = map,
                file = multipartBodyPart
        ).enqueue(object : Callback<UploadResume> {
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
                } catch (e: Exception) {
                    logException(e)
                }
            }
        })
    }


    private fun createPartFromString(s: String): RequestBody {

        return s.toRequestBody("text/plain".toMediaType())

//        RequestBody.create(MediaType.parse("text/plain"), s)
    }


}
