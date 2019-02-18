package com.bdjobs.app.ManageResume

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.UploadResume
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.openUrlInBrowser
import kotlinx.android.synthetic.main.fragment_download_resume.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DownloadResumeFragment : Fragment() {

    private lateinit var communicator: ManageResumeCommunicator
    private lateinit var bdjobsUserSession: BdjobsUserSession
    var downloadLink: String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.bdjobs.app.R.layout.fragment_download_resume, container, false)
    }


    override fun onResume() {
        super.onResume()

        communicator = activity as ManageResumeCommunicator
        bdjobsUserSession = BdjobsUserSession(activity)

        downloadOrDeleteCV("download")

        backIV.setOnClickListener {
            communicator.backButtonPressed()
        }

        submitTV.setOnClickListener {
           activity.openUrlInBrowser(downloadLink)
        }

        submitTV1.setOnClickListener {
            communicator.gotoEmailResumeFragment()
        }
        submitTV3.setOnClickListener {

            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent?.type = "text/plain"
            sharingIntent?.putExtra(android.content.Intent.EXTRA_SUBJECT, "Resume of ${bdjobsUserSession.userName}")
            sharingIntent?.putExtra(android.content.Intent.EXTRA_TEXT, downloadLink)
            startActivity(Intent.createChooser(sharingIntent, "Share"))
        }

        backIV1.setOnClickListener {


        }
    }

    private fun downloadOrDeleteCV(action: String) {
        val dialog = indeterminateProgressDialog("Please wait")
        dialog.setCancelable(false)
        dialog.show()
        ApiServiceMyBdjobs.create().downloadDeleteCV(
                userID = bdjobsUserSession.userId,
                decodeID = bdjobsUserSession.decodId,
                status = action
        ).enqueue(object : Callback<UploadResume> {
            override fun onFailure(call: Call<UploadResume>, t: Throwable) {
                dialog.dismiss()
                error("onFailure", t)
            }

            override fun onResponse(call: Call<UploadResume>, response: Response<UploadResume>) {
                dialog.dismiss()
                if (response?.body()?.statuscode == Constants.api_request_result_code_ok) {
                    if (action.equalIgnoreCase("download")) {
                        downloadLink = response.body()?.data?.get(0)?.path
                        Log.d("downloadResume", "Downloadlink: $downloadLink")
                    } else {
                        toast(response?.body()?.message!!)
                        communicator.gotoResumeUploadFragment()
                    }
                }
            }
        })
    }

}