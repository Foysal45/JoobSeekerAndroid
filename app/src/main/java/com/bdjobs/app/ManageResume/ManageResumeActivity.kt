package com.bdjobs.app.ManageResume

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.FileInfo
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.transitFragment
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageResumeActivity : AppCompatActivity(), ManageResumeCommunicator {
    lateinit var bdjobsUserSession: BdjobsUserSession
    private val emailResumeFragment = EmailResumeFragment()
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

    /*private fun callNetwork() {
        ApiServiceMyBdjobs.create().getCvFileAvailable(
                userID = bdjobsUserSession.userId,
                decodeID = bdjobsUserSession.decodId

        ).enqueue(object : Callback<FileInfo> {
            override fun onFailure(call: Call<FileInfo>, t: Throwable) {
                error("onFailure", t)
                toast("${t.toString()}")
            }

            override fun onResponse(call: Call<FileInfo>, response: Response<FileInfo>) {
                toast("${response.body()?.statuscode}")
                if (response.isSuccessful){
                    cvUpload = response.body()?.statuscode!!
                    Log.d("value", "val " + cvUpload)
                    transitFragment(emailResumeFragment, R.id.fragmentHolder)
                }
            }

        })

    }*/
}
