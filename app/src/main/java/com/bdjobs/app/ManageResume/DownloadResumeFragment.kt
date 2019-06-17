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
import com.bdjobs.app.Utilities.*
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.fragment_download_resume.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
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

        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)

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
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Resume of ${bdjobsUserSession.userName}")
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, downloadLink)
            startActivity(Intent.createChooser(sharingIntent, "Share"))
        }

        backIV1.setOnClickListener {
            alert("Are you sure you want to delete your uploaded resume?", "Confirmation") {
                yesButton {
                    downloadOrDeleteCV("delete")
                }
                noButton { dialog ->
                    dialog.dismiss()
                }
            }.show()

        }
    }

    private fun downloadOrDeleteCV(action: String) {
        activity?.showProgressBar(loadingProgressBar)
        ApiServiceMyBdjobs.create().downloadDeleteCV(
                userID = bdjobsUserSession.userId,
                decodeID = bdjobsUserSession.decodId,
                status = action
        ).enqueue(object : Callback<UploadResume> {
            override fun onFailure(call: Call<UploadResume>, t: Throwable) {
                try {
                    activity?.stopProgressBar(loadingProgressBar)
                    error("onFailure", t)
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<UploadResume>, response: Response<UploadResume>) {
                try {
                    activity?.stopProgressBar(loadingProgressBar)
                    if (response.body()?.statuscode == Constants.api_request_result_code_ok) {
                        if (action.equalIgnoreCase("download")) {
                            downloadLink = response.body()?.data?.get(0)?.path
                            Log.d("downloadResume", "Downloadlink: $downloadLink")
                        } else {
                            toast(response.body()?.message!!)
                            bdjobsUserSession.updateUserCVUploadStatus("3")
                            communicator.gotoResumeUploadFragment()
                        }
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        })
    }

}