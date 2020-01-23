package com.bdjobs.app.ManageResume

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.transitFragment


class ManageResumeActivity : Activity(), ManageResumeCommunicator {

    private lateinit var bdjobsUserSession: BdjobsUserSession
    private val emailResumeFragment = EmailResumeFragment()
    private val uploadResumeFragment = UploadResumeFragment()
    private var timesEmailedMyResumeFragment = TimesEmailedMyResumeFragment()
    private var timesEmailedMyResumeFilterFragment = TimesEmailedMyResumeFilterFragment()
    private var downloadResumeFragment = DownloadResumeFragment()
    var cvUpload: String? = ""
    private var from = ""
    private var subject = ""
    private var toEmail = ""
    private var jobID = ""
    private var backFrom = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_resume)
        bdjobsUserSession = BdjobsUserSession(applicationContext)

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

        //Log.d("manage", "jobid== $jobID toemail = $toEmail subj = $subject")
        getIsCvUploaded()

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

    override fun setBackFrom(backFrom: String) {
        this.backFrom = backFrom
    }

    override fun getBackFrom(): String {
        return backFrom
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

    override fun gotoTimesResumeFilterFrag() {
        transitFragment(timesEmailedMyResumeFilterFragment,R.id.fragmentHolder,addToBackStack = false)
    }

    override fun backButtonPressed() {
        onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        uploadResumeFragment.onActivityResult(requestCode, resultCode, data)
    }
    private fun getIsCvUploaded() {
        /*shimmer_view_container_manage?.show()
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
                        //Log.d("value", "val " + cvUpload)


                        if (from.equalIgnoreCase("uploadResume")) {
                            if (Constants.cvUploadStatus.equalIgnoreCase("0") || Constants.cvUploadStatus.equalIgnoreCase("4")) {
                                gotoDownloadResumeFragment()
                            } else {
                                gotoResumeUploadFragment()
                            }
                        }else if(from.equalIgnoreCase("emailResume")){
                            Constants.timesEmailedResumeLast = false
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

        })*/


        try {

                cvUpload = bdjobsUserSession.cvUploadStatus
                //Log.d("value", "val " + cvUpload)


                if (from.equalIgnoreCase("uploadResume")) {
                    if (bdjobsUserSession.cvUploadStatus?.equalIgnoreCase("0")!! || bdjobsUserSession.cvUploadStatus?.equalIgnoreCase("4")!!) {
                        gotoDownloadResumeFragment()
                    } else {
                        gotoResumeUploadFragment()
                    }
                }else if(from.equalIgnoreCase("emailResume")){
                    Constants.timesEmailedResumeLast = false
                    gotoTimesResumeFrag()
                }
                else if(from.equalIgnoreCase("timesEmailedResume")){
                    transitFragment(timesEmailedMyResumeFragment, R.id.fragmentHolder)
                }
                else if (from.equalIgnoreCase("emailResumeCompose")){
                    transitFragment(emailResumeFragment, R.id.fragmentHolder)
                }

        } catch (e: Exception) {
        }

    }
}
