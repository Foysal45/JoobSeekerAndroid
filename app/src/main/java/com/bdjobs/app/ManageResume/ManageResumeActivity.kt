package com.bdjobs.app.ManageResume

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.transitFragment


class ManageResumeActivity : AppCompatActivity(), ManageResumeCommunicator {
    override fun gotouploaddone() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun gotoupload() {
        transitFragment(uploadResumeFragment, R.id.fragmentHolder, true)
    }



    lateinit var bdjobsUserSession: BdjobsUserSession
    private val emailResumeFragment = EmailResumeFragment()

    private val uploadResumeFragment = UploadResumeFragment()

    var cvUpload: String = ""


    override fun isGetCvUploaded(): String {
        return cvUpload
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_resume)
        bdjobsUserSession = BdjobsUserSession(applicationContext)

        try {
            cvUpload = intent.getStringExtra("cvUploaded")
        } catch (e: Exception) {
            logException(e)
        }

        transitFragment(emailResumeFragment, R.id.fragmentHolder)

    }

    override fun backButtonPressed() {
        onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        uploadResumeFragment.onActivityResult(requestCode, resultCode, data)
    }
}
