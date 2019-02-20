package com.bdjobs.app.ManageResume

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.transitFragment


class ManageResumeActivity : Activity(), ManageResumeCommunicator {

    private lateinit var bdjobsUserSession: BdjobsUserSession
    private val emailResumeFragment = EmailResumeFragment()
    private val uploadResumeFragment = UploadResumeFragment()
    private var timesEmailedMyResumeFragment = TimesEmailedMyResumeFragment()
    private var downloadResumeFragment = DownloadResumeFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_resume)
        bdjobsUserSession = BdjobsUserSession(applicationContext)

        var from = intent.getStringExtra("from")
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
        }


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
}
