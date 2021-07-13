package com.bdjobs.app.ManageResume

import android.annotation.SuppressLint
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
import com.bdjobs.app.ManageResume.utils.formatDateVP
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.gms.ads.AdRequest
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.models.sort.SortingTypes
import kotlinx.android.synthetic.main.fragment_upload_resume.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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


class UploadResumeFragment : Fragment() {
    private lateinit var communicator: ManageResumeCommunicator
    private lateinit var bdjobsUserSession: BdjobsUserSession
    val filePaths: ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_upload_resume, container, false)
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

        fetchPersonalizedResumeStat()

        submitTV?.setOnClickListener {
            browseFile()
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

                runOnUiThread {
                    activity.stopProgressBar(loadingProgressBar)
                    cl_personalized_resume_stat.show()
                }


                if (response.statuscode == "0" && response.message == "Success") {
                    val data = response.data!![0]

                    val statCalculatedFrom = data.personalizedCalculatedFromDate?.let {
                        formatDateVP(
                            it
                        )
                    }

                    runOnUiThread {

                        if (!statCalculatedFrom.isNullOrEmpty()) {

                            cv_no_personalized_resume.hide()

                            tv_stat_calculated_from.show()
                            tv_label_stat_personalized_resume.show()
                            cl_stat_personalized_resume.show()


                            tv_personalized_resume_view_count.text = data.personalizedViewed
                            tv_personalized_resume_download_count.text = data.personalizedDownload
                            tv_personalized_resume_emailed_count.text = data.personalizedEmailed

                            tv_stat_calculated_from.text =
                                "Statistics calculated from $statCalculatedFrom"
                        } else {
                            tv_stat_calculated_from.hide()
                            tv_label_stat_personalized_resume.hide()
                            cl_stat_personalized_resume.hide()

                            cv_no_personalized_resume.show()
                        }


                    }

                } else runOnUiThread { toast("Sorry, personalized resume stat fetching failed!") }

            } catch (e: Exception) {
                Timber.e("Exception while fetching personalized resume stat: ${e.localizedMessage}")
                runOnUiThread {
                    activity.stopProgressBar(loadingProgressBar)
                    toast("Sorry, personalized resume stat fetching failed: ${e.localizedMessage}")
                }

            }


        }
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

//        if (requestCode == Constant.REQUEST_CODE_PICK_FILE && resultCode == Activity.RESULT_OK && data != null) {
//
//
//            val file : List<NormalFile>? = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE)
//
//            file?.forEach {
//                //Log.d("rakib",it.path.toString())
//            }
//
//            try {
//                val uri = Uri.parse(File(file!![0].path).toString())
//                checkFilleSize(uri)
//            } catch (e: Exception){
//                logException(e)
//            }
//        }


        if (requestCode == FilePickerConst.REQUEST_CODE_DOC && resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == FilePickerConst.REQUEST_CODE_DOC && resultCode == Activity.RESULT_OK && data != null) {
                val uri = Uri.fromFile(
                    File(
                        data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS)?.get(0)
                    )
                )
                checkFilleSize(uri)
            }
        }
    }

    private fun checkFilleSize(uri: Uri) {
        val fileInformation = FileInformation()
        val fileinfo = fileInformation.getfileInformation(getApplicationContext(), uri)
        val size = fileinfo.fileSize
        val fileSizeInKB = size / 1024

        if (fileinfo.extension!!.equalIgnoreCase("pdf") || fileinfo.extension!!.equalIgnoreCase("doc") || fileinfo.extension!!.equalIgnoreCase(
                "docx"
            )
        ) {

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

                val multipartBodyPart =
                    MultipartBody.Part.createFormData("File", fileinfo.fileName, requestFile)

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

    private fun uploadCVtoServer(
        multipartBodyPart: MultipartBody.Part,
        map: HashMap<String, RequestBody>
    ) {
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
