package com.bdjobs.app.ManageResume

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.FileInfo
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.transitFragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_resume)
        bdjobsUserSession = BdjobsUserSession(applicationContext)
        getIsCvUploaded()

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
    private fun getIsCvUploaded() {
        ApiServiceMyBdjobs.create().getCvFileAvailable(
                userID = bdjobsUserSession.userId,
                decodeID = bdjobsUserSession.decodId

        ).enqueue(object : Callback<FileInfo> {
            override fun onFailure(call: Call<FileInfo>, t: Throwable) {
                error("onFailure", t)
                toast("${t.toString()}")
            }

            override fun onResponse(call: Call<FileInfo>, response: Response<FileInfo>) {
                //toast("${response.body()?.statuscode}")
                if (response.isSuccessful) {
                    cvUpload = response.body()?.statuscode!!
                    Constants.cvUploadStatus = cvUpload
                    Log.d("value", "val " + cvUpload)

                }
            }

        })

    }
}
