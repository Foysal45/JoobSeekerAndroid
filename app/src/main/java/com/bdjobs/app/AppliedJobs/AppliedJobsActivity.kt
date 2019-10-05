package com.bdjobs.app.AppliedJobs

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.bdjobs.app.API.ModelClasses.AppliedJobModelExprience
import com.bdjobs.app.InterviewInvitation.InterviewInvitationBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.transitFragment
import org.jetbrains.anko.startActivity

class AppliedJobsActivity : Activity(), AppliedJobsCommunicator {
    override fun setStatus(status: String) {
        this.status = status
    }

    override fun getStatus(): String {
        return status
    }

    override fun setFrom(value: String) {
        from = value
    }

    override fun getFrom(): String {
        return from
    }

    private var title: String = ""
    private var jobid: String = ""
    private var company: String = ""
    private var experienceList: ArrayList<AppliedJobModelExprience>? = ArrayList()
    private var from = ""
    private var status = ""

    override fun setTitle(title: String) {
        this.title = title
    }

    override fun getTitle2(): String {
        return title
    }

    override fun setComapany(company: String) {
        this.company = company
    }

    override fun getCompany(): String {
        return company
    }

    override fun setexperienceList(AppliedJobExprience: ArrayList<AppliedJobModelExprience>) {
        this.experienceList = AppliedJobExprience
    }

    override fun getExperience(): ArrayList<AppliedJobModelExprience> {
        return experienceList!!
    }

    override fun setjobID(jobid: String) {
        this.jobid = jobid
    }

    override fun getjobID(): String {
        return jobid
    }


    override fun gotoEmployerInteractionFragment() {
        transitFragment(employerInteractionFragment, R.id.fragmentHolder, true)
    }

    override fun gotoInterviewInvitationDetails(from: String, jobID: String, companyName: String, jobTitle: String) {

        startActivity<InterviewInvitationBaseActivity>(
                "from" to from,
                "jobid" to jobID,
                "companyname" to companyName,
                "jobtitle" to jobTitle
        )

    }


    override fun scrollToUndoPosition(position: Int) {
        if (position >= 0) {
            appliedJobsFragment.scrollToUndoPosition(position)
        }
    }

    override fun decrementCounter() {
        appliedJobsFragment.decrementCounter()
    }

    override fun incrementAvailableJobCounter() {
        appliedJobsFragment.incrementAvailableJobCount()
    }

    private val appliedJobsFragment = AppliedJobsFragment()
    private val employerInteractionFragment = EmployerInteractionFragment()
    private var time: String = ""

    override fun getTime(): String {
        return time
    }


    override fun backButtonPressed() {
        onBackPressed()
    }

    override fun gotoAppliedJobsFragment() {
        transitFragment(appliedJobsFragment, R.id.fragmentHolder, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applied_jobs)
        try {
            time = intent.getStringExtra("time")
        } catch (e: Exception) {
            logException(e)
        }
        Log.d("time", "time: " + time)
        gotoAppliedJobsFragment()
    }
}
