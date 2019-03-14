package com.bdjobs.app.ManageResume

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.FileInfo
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import kotlinx.android.synthetic.main.activity_manage_resume.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ManageResumeActivity : Activity(), ManageResumeCommunicator {

    private lateinit var bdjobsUserSession: BdjobsUserSession
    private val emailResumeFragment = EmailResumeFragment()
    private val uploadResumeFragment = UploadResumeFragment()
    private var timesEmailedMyResumeFragment = TimesEmailedMyResumeFragment()
    private var downloadResumeFragment = DownloadResumeFragment()
    var cvUpload: String = ""
    private var from = ""
    private var subject = ""
    private var toEmail = ""
    private var jobID = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_resume)
        bdjobsUserSession = BdjobsUserSession(applicationContext)
        getIsCvUploaded()

        try {
            from = intent.getStringExtra("from")
        } catch (e: Exception) {
            logException(e)
        }
        try {
            subject  = intent.getStringExtra("subject")
        } catch (e: Exception) {
            logException(e)
        }
        try {
            toEmail  = intent.getStringExtra("emailAddress")
        } catch (e: Exception) {
            logException(e)
        }
        try {
            jobID  = intent.getStringExtra("jobid")
        } catch (e: Exception) {
            logException(e)
        }

        Log.d("manage", "jobid== $jobID toemail = $toEmail subj = $subject")
        /*      try {
                  from = intent.getStringExtra("from")

                  if (from.equalIgnoreCase("emailResumeCompose")){
                      subject  = intent.getStringExtra("subject")
                      toEmail  = intent.getStringExtra("emailAddress")
                      jobID  = intent.getStringExtra("jobid")
                      gotoEmailResumeFragment()
                  }
              } catch (e: Exception) {
                  logException(e)
              }*/

/*        var from = intent.getStringExtra("from")
     //   from = intent.getStringExtra("timesEmailedResume")

        if (from.equalIgnoreCase("uploadResume")) {
            if (Constants.cvUploadStatus.equalIgnoreCase("0") || Constants.cvUploadStatus.equalIgnoreCase("4")) {
                gotoDownloadResumeFragment()
            } else {
                gotoResumeUploadFragment()
            }
        }else if(from.equalIgnoreCase("emailResume")){
            gotoTimesResumeFrag()
        }
        else if(from.equalIgnoreCase("timesEmailedResume")){
            transitFragment(timesEmailedMyResumeFragment, R.id.fragmentHolder)
        }*/


    }
    override fun getjobID(): String {
        return jobID
    }
    override fun getEmailTo(): String {
        return toEmail
    }
    override  fun getSubject(): String {
        return subject
    }

    override fun gotoEmailResumeFragment() {
        transitFragment(emailResumeFragment, R.id.fragmentHolder,addToBackStack = true)
    }


    override fun gotoDownloadResumeFragment() {
        transitFragment(downloadResumeFragment, R.id.fragmentHolder)
    }

    override fun gotoResumeUploadFragment() {
        transitFragment(uploadResumeFragment, R.id.fragmentHolder)
    }


    override fun gotoTimesResumeFrag() {
        transitFragment(timesEmailedMyResumeFragment, R.id.fragmentHolder)
    }

    override fun backButtonPressed() {
        onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        uploadResumeFragment.onActivityResult(requestCode, resultCode, data)
    }
    private fun getIsCvUploaded() {
        shimmer_view_container_manage?.show()
        shimmer_view_container_manage?.startShimmerAnimation()
        ApiServiceMyBdjobs.create().getCvFileAvailable(
                userID = bdjobsUserSession.userId,
                decodeID = bdjobsUserSession.decodId

        ).enqueue(object : Callback<FileInfo> {
            override fun onFailure(call: Call<FileInfo>, t: Throwable) {
                error("onFailure", t)
                toast("${t.toString()}")
                shimmer_view_container_manage?.hide()
                shimmer_view_container_manage?.stopShimmerAnimation()
            }

            override fun onResponse(call: Call<FileInfo>, response: Response<FileInfo>) {
                //toast("${response.body()?.statuscode}")
                try {
                    if (response.isSuccessful) {
                        cvUpload = response.body()?.statuscode!!
                        Constants.cvUploadStatus = cvUpload
                        Log.d("value", "val " + cvUpload)


                        if (from.equalIgnoreCase("uploadResume")) {
                            if (Constants.cvUploadStatus.equalIgnoreCase("0") || Constants.cvUploadStatus.equalIgnoreCase("4")) {
                                gotoDownloadResumeFragment()
                            } else {
                                gotoResumeUploadFragment()
                            }
                        }else if(from.equalIgnoreCase("emailResume")){
                            gotoTimesResumeFrag()
                        }
                        else if(from.equalIgnoreCase("timesEmailedResume")){
                            transitFragment(timesEmailedMyResumeFragment, R.id.fragmentHolder)
                        }
                        else if (from.equalIgnoreCase("emailResumeCompose")){
                            transitFragment(emailResumeFragment, R.id.fragmentHolder)
                        }

                    }
                    shimmer_view_container_manage?.hide()
                    shimmer_view_container_manage?.stopShimmerAnimation()
                } catch (e: Exception) {
                }
            }

        })

    }
}
