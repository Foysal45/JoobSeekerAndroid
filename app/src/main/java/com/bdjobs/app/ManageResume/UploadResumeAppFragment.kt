package com.bdjobs.app.ManageResume

import android.app.Activity.RESULT_OK
import android.app.Fragment
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.ADDorUpdateModel
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.facebook.FacebookSdk.getApplicationContext
import kotlinx.android.synthetic.main.fragment_upload_resume.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.util.ArrayList
import java.util.HashMap

class UploadResumeAppFragment : Fragment() {
    lateinit var communicator: ManageResumeCommunicator
    internal var filePaths = ArrayList<String>()
    private val RESULT_LOAD_IMG = 1
    lateinit var bdjobsUserSession: BdjobsUserSession
    internal lateinit var multipartBodyPart: MultipartBody.Part
    internal var map = HashMap<String, RequestBody>()
    internal lateinit var requestFile: RequestBody
    internal lateinit var progressDialog: ProgressDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_upload_resume, container, false)
    }

    private fun initializer() {
        progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Please Wait..")
        progressDialog.setTitle("Saving")
        progressDialog.setCancelable(false)

    }

    override fun onResume() {
        super.onResume()
       // initializer()
        communicator = activity as ManageResumeCommunicator
        bdjobsUserSession = BdjobsUserSession(activity)
        submitTV?.setOnClickListener {
            //communicator.gotouploaddone()
            toast("clicked")
            BrowseFile()
            // photoupload()

        }
    }

    private fun BrowseFile() {
        try {

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, RESULT_LOAD_IMG)
        } catch (e: ActivityNotFoundException) {
            //val errordetails = Extra.getExceptionDetails(this@Pdf_Browse_Upload, e)
            // val arraydetails = errordetails.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            //  System.out.println("where error occurred"+arraydetails[0]+" "+arraydetails[1]);
            // if (arraydetails.size != 0 && arraydetails.size == 2) {
            //      Extra.ErrorSend(e.message, arraydetails[0], arraydetails[1], this@Pdf_Browse_Upload)
        }
        //  runOnUiThread(Runnable { Toast.makeText(getApplicationContext(), "No image chooser application found!", Toast.LENGTH_LONG).show() })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMG) {
                var uri: Uri? = null
                if (data != null) {
                    uri = data.data
                    Log.d("uri", uri.toString())

                    checkFilleSize(uri)
                }

            }

        }

    }

    private fun createPartFromString(s: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), s)
    }

    private fun checkFilleSize(uri: Uri) {
        val filefunction = FileUploadFunction()
        val fileinfo = filefunction.getfileInformation(getApplicationContext(), uri)
        val size = fileinfo.fileSize
        val fileSizeInKB = size / 1024
        Log.d("uri", fileSizeInKB.toString())
        if (fileSizeInKB == 0L) {
            toast("It is an invalid file")
        } else if (fileSizeInKB > 500L) {
            toast("You can not upload file more than 500KB in size")
        } else {
            if (fileinfo.extension.equalIgnoreCase("pdf") || fileinfo.extension.equalIgnoreCase("doc") || fileinfo.extension.equalIgnoreCase("docx")) {
                toast("ok")
                val mediaType = "application/" + fileinfo.extension
                requestFile = RequestBody.create(MediaType.parse(mediaType), File(uri.path))
                multipartBodyPart = MultipartBody.Part.createFormData("File", fileinfo.fileName, requestFile)
                val userid = createPartFromString(bdjobsUserSession.userId!!)
                val decodeid = createPartFromString(bdjobsUserSession.decodId!!)
                val status = createPartFromString("upload")
                val fileExtension = createPartFromString(fileinfo.extensionwithDot)
                val fileType = createPartFromString(fileinfo.type)
                val fileName = createPartFromString(fileinfo.fileName)

                map.put("userid", userid)
                map.put("decodeid", decodeid)
                map.put("status", status)
                map.put("fileExtension", fileExtension)
                map.put("fileType", fileType)
                map.put("fileName", fileName)

                uploadCV()
            }

        }
    }

    private fun uploadCV() {
      //  progressDialog.setTitle("Saving")
     //   progressDialog.show()



        ApiServiceMyBdjobs.create().UploadCV(
                partMap = map,
                file = multipartBodyPart

        ).enqueue(object : retrofit2.Callback<ADDorUpdateModel>{
            override fun onFailure(call: Call<ADDorUpdateModel>, t: Throwable) {
                toast("${t.message}")
            }

            override fun onResponse(call: Call<ADDorUpdateModel>, response: Response<ADDorUpdateModel>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        toast("${response.body()?.message}")
                    }
                    else{
                        //   progressDialog.dismiss()
                    }
                }
            }

        })
    }

}
