package com.bdjobs.app.ManageResume

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.UploadResume
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.fragment_download_resume.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class DownloadResumeFragment : android.app.Fragment() {

    private lateinit var communicator: ManageResumeCommunicator
    private lateinit var bdjobsUserSession: BdjobsUserSession
    var downloadLink: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(com.bdjobs.app.R.layout.fragment_download_resume, container, false)
    }


    override fun onResume() {
        super.onResume()

        communicator = activity as ManageResumeCommunicator
        bdjobsUserSession = BdjobsUserSession(context = activity)

        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)

        downloadOrDeleteCV("download")
        fetchPersonalizedResumeStat()
        fetchPersonalizedResumeStat()

        backIV.setOnClickListener {
            communicator.backButtonPressed()
        }

        btn_download_personalized_resume.setOnClickListener {
            activity.openUrlInBrowser(downloadLink)
        }

        btn_email_personalized_resume.setOnClickListener {
            communicator.gotoEmailResumeFragment()
        }

        btn_share_personalized_resume.setOnClickListener {

            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Resume of ${bdjobsUserSession.userName}"
            )
            sharingIntent.putExtra(Intent.EXTRA_TEXT, downloadLink)
            startActivity(Intent.createChooser(sharingIntent, "Share"))
        }

        btn_delete_personalized_resume.setOnClickListener {
            alert("Are you sure you want to delete your uploaded resume?", "Confirmation") {
                yesButton {
                    downloadOrDeleteCV("delete")
                }
                noButton { dialog ->
                    dialog.dismiss()
                }
            }.show()

        }

        btn_view_personalized_resume.setOnClickListener {
            activity.startActivity(Intent(activity,ViewPersonalizedResume::class.java)
                .putExtra("PDF_URL",downloadLink))
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
//                    activity?.stopProgressBar(loadingProgressBar)
                    if (response.body()?.statuscode == Constants.api_request_result_code_ok) {
                        if (action.equalIgnoreCase("download")) {
                            downloadLink = response.body()?.data?.get(0)?.path
                            //Log.d("downloadResume", "Downloadlink: $downloadLink")
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

                if (activity!=null) {
                    runOnUiThread {
                        activity.stopProgressBar(loadingProgressBar)
                        cl_personalized_resume_stat.show()
                    }
                }

                if (response.statuscode == "0" && response.message == "Success") {
                    val data = response.data!![0]

//                    downloadLink = data.personalizedFilePath

                    val statCalculatedFrom = if (data.personalizedCalculatedFromDate!="") try {
                        formatDateVP(data.personalizedCalculatedFromDate)
                    } catch (e: Exception) {
                        formatDateVP(data.personalizedCalculatedFromDate,
                            SimpleDateFormat("M/dd/yyyy", Locale.US)
                        )
                    } else ""
                    val lastUpdatedOn = if (data.personalizedLastUpdateDate!="") try {
                        formatDateVP(data.personalizedLastUpdateDate)
                    } catch (e: Exception) {
                        formatDateVP(data.personalizedLastUpdateDate,
                            SimpleDateFormat("M/dd/yyyy", Locale.US)
                        )
                    } else ""

                    bdjobsUserSession.personalizedResumeSessionStatCalculatedFrom = statCalculatedFrom

                    if (activity!=null) {
                        runOnUiThread {
                            tv_personalized_resume_view_count.text = data.personalizedViewed
                            tv_personalized_resume_download_count.text = data.personalizedDownload
                            tv_personalized_resume_emailed_count.text = data.personalizedEmailed

                            tv_stat_calculated_from.text = "Statistics calculated from $statCalculatedFrom"
                            tv_last_upload_personalized_resume.text = "Last Upload: $lastUpdatedOn"

                            tv_file_name_personalized_resume.text = data.personalizedFileName

                            if (data.personalizefileType=="2") {
                                tv_file_name_personalized_resume.setCompoundDrawablesWithIntrinsicBounds(0,
                                    R.drawable.ic_ms_word,0,0)
                            } else {
                                tv_file_name_personalized_resume.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_pdf_personalized_resume,0,0)
                            }

                        }
                    }


                } else {
                    if (activity!=null) {
                        runOnUiThread { toast("Sorry, personalized resume stat fetching failed!") }
                    }

                }

            } catch (e: Exception) {
                Timber.e("Exception while fetching personalized resume stat: ${e.localizedMessage}")
                if (activity!=null) {
                    runOnUiThread {
                        activity.stopProgressBar(loadingProgressBar)
                        toast("Sorry, personalized resume stat fetching failed: ${e.localizedMessage}")
                    }
                }


            }


        }
    }
    
    @SuppressLint("SimpleDateFormat")
    private fun formatDateVP(lastUpdate: String?,format: SimpleDateFormat= SimpleDateFormat("M/dd/yyyy HH:mm:ss a")): String {
        var lastUpdate1 = lastUpdate
        var formatter = format
        val date = formatter.parse(lastUpdate1!!)
        formatter = SimpleDateFormat("dd MMM yyyy")
        lastUpdate1 = formatter.format(date!!)

        Timber.d("Last updated at: $lastUpdate1")

        return lastUpdate1

    }

}