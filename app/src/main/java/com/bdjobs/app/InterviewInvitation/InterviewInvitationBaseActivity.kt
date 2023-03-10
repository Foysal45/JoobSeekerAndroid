package com.bdjobs.app.InterviewInvitation

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.ads.Ads
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.utilities.Constants
import com.bdjobs.app.utilities.logDataForAnalytics
import com.bdjobs.app.utilities.logException
import com.bdjobs.app.utilities.transitFragment
import com.bdjobs.app.videoInterview.ui.interview_details.VideoInterviewDetailsFragment
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_interview_invitation_base.*
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class InterviewInvitationBaseActivity : FragmentActivity(), InterviewInvitationCommunicator {

    lateinit var bdjobsDB: BdjobsDB
    lateinit var bdjobsUserSession: BdjobsUserSession
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun goToInvitationDetailsForAppliedJobs(jobID: String, companyName: String, jobTitle: String) {
        //Log.d("rakib interface", "$from $jobTitle $jobID $companyName")
        this.jobID = jobID
        this.jobTitle = jobTitle
        this.companyName = companyName
        transitFragment(interviewInvitationDetailsFragment, R.id.interViewfragmentHolder, false)
    }

    override fun goToVideoInterviewDetails(jobID: String, companyName: String, jobTitle: String) {
        this.jobID = jobID
        this.jobTitle = jobTitle
        this.companyName = companyName
        transitFragment(videoInterviewDetailsFragment, R.id.interViewfragmentHolder, false)
    }


    private var jobID = ""
    private var companyName = ""
    private var jobTitle = ""
    private var from = ""
    private var invitationDate = ""
    private var invitationTime = ""
    private var venue = ""
    private var lat = ""
    private var lan = ""
    private var time = ""
    private var value = ""
    private var type = ""
    private var nId = ""
    private var seen = false
    private var url = ""

    private val interveiwInvitationListFragment = InterveiwInvitationListFragment()
    private val interviewInvitationDetailsFragment = InterviewInvitationDetailsFragment()
    private val videoInterviewDetailsFragment = VideoInterviewDetailsFragment()

    override fun getTime(): String {
        return time
    }

    override fun getVideoUrl(): String {
        return url
    }

    override fun getFrom(): String {
        return from
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interview_invitation_base)
        //Log.d("rakib", "onCreate")

        bdjobsDB = BdjobsDB.getInstance(applicationContext)
        bdjobsUserSession = BdjobsUserSession(applicationContext)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        Ads.loadAdaptiveBanner(this@InterviewInvitationBaseActivity, adView)


    }


    override fun onResume() {
        super.onResume()
        //Log.d("rakib", "onResume")
        //Timber.d("rakib onResume")
        try {
            from = intent.getStringExtra("from").toString()
            //Log.d("rakib","$from")
        } catch (e: Exception) {
            logException(e)
        }


        try {
            time = intent.getStringExtra("time").toString()
        } catch (e: Exception) {
            logException(e)
        }



        try {
            jobID = intent.getStringExtra("jobid").toString()

        } catch (e: Exception) {
            logException(e)
        }



        try {
            companyName = intent.getStringExtra("companyname").toString()
        } catch (e: Exception) {
            logException(e)
        }



        try {
            jobTitle = intent.getStringExtra("jobtitle").toString()
        } catch (e: Exception) {
            logException(e)
        }

        try {
            type = intent.getStringExtra("type").toString()
            //Log.d("rakib","$type")

        } catch (e: Exception) {
            logException(e)
        }

        try {
            nId = intent.getStringExtra("nid").toString()
        } catch (e: Exception) {
            logException(e)
        }

        try {
            seen = intent.getBooleanExtra("seen", false)
        } catch (e: Exception) {
            logException(e)
        }

        try {
            url = intent.getStringExtra("videoUrl").toString()
        } catch (e: Exception) {
            logException(e)
        }

        //Log.d("utuytujtjtgdju56u", "$from $jobTitle $jobID $companyName")
        //Timber.d("rakib $from")


        when {
            from.equals("notification") -> {

                doAsync {
                    try {
                        bdjobsDB.notificationDao().updateNotificationTableByClickingNotification(Date(), true, nId, type)
                        val count = bdjobsDB.notificationDao().getNotificationCount()
                        bdjobsUserSession = BdjobsUserSession(this@InterviewInvitationBaseActivity)
                        bdjobsUserSession.updateNotificationCount(count)
                    } catch (e: Exception) {
                    }
//                    val seen = bdjobsDB.notificationDao().getNotificationSeenStatus(nId)
                    if (!seen) {

                        try {
                            logDataForAnalytics(Constants.NOTIFICATION_TYPE_INTERVIEW_INVITATION, applicationContext, jobID, nId)
                        } catch (e: Exception) {
                        }

                        try {
                            ApiServiceJobs.create().sendDataForAnalytics(
                                    userID = bdjobsUserSession.userId, decodeID = bdjobsUserSession.decodId, uniqueID = nId, notificationType = Constants.NOTIFICATION_TYPE_INTERVIEW_INVITATION, encode = Constants.ENCODED_JOBS, sentTo = "Android"
                            ).enqueue(
                                    object : Callback<String> {
                                        override fun onFailure(call: Call<String>, t: Throwable) {

                                        }

                                        override fun onResponse(call: Call<String>, response: Response<String>) {

                                            try {
                                                if (response.isSuccessful) {
                                                }
                                            } catch (e: Exception) {
                                                logException(e)
                                            }
                                        }
                                    }
                            )
                        } catch (e: Exception) {
                        }
                    }
                }
                if (type == Constants.NOTIFICATION_TYPE_INTERVIEW_INVITATION)
                    goToInvitationDetailsForAppliedJobs(jobID, companyName, jobTitle)
                else
                    goToVideoInterviewDetails(jobID, companyName, jobTitle)
            }

            from.equals("notificationList") -> {

                if (!seen) {

                    try {
                        logDataForAnalytics(Constants.NOTIFICATION_TYPE_INTERVIEW_INVITATION, applicationContext, jobID, nId)
                    } catch (e: Exception) {
                    }

                    try {
                        ApiServiceJobs.create().sendDataForAnalytics(
                                userID = bdjobsUserSession.userId, decodeID = bdjobsUserSession.decodId, uniqueID = nId, notificationType = Constants.NOTIFICATION_TYPE_INTERVIEW_INVITATION, encode = Constants.ENCODED_JOBS, sentTo = "Android"
                        ).enqueue(
                                object : Callback<String> {
                                    override fun onFailure(call: Call<String>, t: Throwable) {

                                    }

                                    override fun onResponse(call: Call<String>, response: Response<String>) {

                                        try {
                                            if (response.isSuccessful) {
                                            }
                                        } catch (e: Exception) {
                                            logException(e)
                                        }
                                    }
                                }
                        )
                    } catch (e: Exception) {
                    }
                }

                goToInvitationDetailsForAppliedJobs(jobID, companyName, jobTitle)
            }

            from.equals("videoInterviewNotificationList") -> {

                if (!seen) {

                    try {
                        logDataForAnalytics(Constants.NOTIFICATION_TYPE_VIDEO_INTERVIEW, applicationContext, jobID, nId)
                    } catch (e: Exception) {
                    }

                    try {
                        ApiServiceJobs.create().sendDataForAnalytics(
                                userID = bdjobsUserSession.userId, decodeID = bdjobsUserSession.decodId, uniqueID = nId, notificationType = Constants.NOTIFICATION_TYPE_VIDEO_INTERVIEW, encode = Constants.ENCODED_JOBS, sentTo = "Android"
                        ).enqueue(
                                object : Callback<String> {
                                    override fun onFailure(call: Call<String>, t: Throwable) {

                                    }

                                    override fun onResponse(call: Call<String>, response: Response<String>) {

                                        try {
                                            if (response.isSuccessful) {
                                            }
                                        } catch (e: Exception) {
                                            logException(e)
                                        }
                                    }
                                }
                        )
                    } catch (e: Exception) {
                    }
                }

                goToVideoInterviewDetails(jobID, companyName, jobTitle)
            }

            from.equals("appliedjobs") -> goToInvitationDetailsForAppliedJobs(jobID, companyName, jobTitle)

            else -> transitFragment(interveiwInvitationListFragment, R.id.interViewfragmentHolder)
        }

    }


    override fun backButtonClicked() {
        try {
            onBackPressed()
        } catch (e: Exception) {
        }
    }

    override fun goToInvitationDetails(jobID: String, companyName: String, jobTitle: String) {
        this.jobID = jobID
        this.jobTitle = jobTitle
        this.companyName = companyName
        transitFragment(interviewInvitationDetailsFragment, R.id.interViewfragmentHolder, true)
    }

    override fun goToVenueDirection(invitationDate: String, invitationTime: String, venue: String, lat: String, lan: String) {
        this.invitationDate = invitationDate
        this.invitationTime = invitationTime
        this.venue = venue
        this.lat = lat
        this.lan = lan
    }

    override fun getCompanyJobID(): String {
        return jobID
    }

    override fun getCompanyJobTitle(): String {
        return jobTitle
    }

    override fun getCompanyNm(): String {
        return companyName
    }

    override fun getInviteTime(): String {
        return invitationTime
    }

    override fun getInviteDate(): String {
        return invitationDate
    }

    override fun getCompanyVenue(): String {
        return venue
    }

    override fun getCompanyLat(): String {
        return lat
    }

    override fun getCompanyLan(): String {
        return lan
    }
}
