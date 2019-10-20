package com.bdjobs.app.InterviewInvitation

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.transitFragment
import org.jetbrains.anko.toast

class InterviewInvitationBaseActivity : Activity(), InterviewInvitationCommunicator {


    override fun goToInvitationDetailsForAppliedJobs(jobID: String, companyName: String, jobTitle: String) {
        Log.d("rakib interface", "$from $jobTitle $jobID $companyName")
        this.jobID = jobID
        this.jobTitle = jobTitle
        this.companyName = companyName
        transitFragment(interviewInvitationDetailsFragment, R.id.interViewfragmentHolder, false)
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

    private val interveiwInvitationListFragment = InterveiwInvitationListFragment()
    private val interviewInvitationDetailsFragment = InterviewInvitationDetailsFragment()

    override fun getTime(): String {
        return time
    }

    override fun getFrom(): String {
        return from
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interview_invitation_base)
        Log.d("rakib", "onCreate")

    }


    override fun onResume() {
        super.onResume()
        Log.d("rakib", "onResume")
        try {
            from = intent.getStringExtra("from")
        } catch (e: Exception) {
            logException(e)
        }


        try {
            time = intent.getStringExtra("time")
        } catch (e: Exception) {
            logException(e)
        }



        try {
            jobID = intent.getStringExtra("jobid")
        } catch (e: Exception) {
            logException(e)
        }



        try {
            companyName = intent.getStringExtra("companyname")
        } catch (e: Exception) {
            logException(e)
        }



        try {
            jobTitle = intent.getStringExtra("jobtitle")
        } catch (e: Exception) {
            logException(e)
        }


        Log.d("utuytujtjtgdju56u", "$from $jobTitle $jobID $companyName")


        when {
            from?.equals("notification") -> {
                goToInvitationDetailsForAppliedJobs(jobID, companyName, jobTitle)
            }
            from?.equals("appliedjobs") -> goToInvitationDetailsForAppliedJobs(jobID, companyName, jobTitle)
            else -> transitFragment(interveiwInvitationListFragment, R.id.interViewfragmentHolder)
        }

    }


    override fun backButtonClicked() {
        onBackPressed()
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
