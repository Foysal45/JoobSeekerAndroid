package com.bdjobs.app.LoggedInUserLanding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.Fragment
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.API.ModelClasses.MoreHorizontalData
import com.bdjobs.app.Employers.EmployersBaseActivity
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.Settings.SettingBaseActivity
import com.bdjobs.app.Utilities.openUrlInBrowser
import kotlinx.android.synthetic.main.fragment_more_layout.*
import org.jetbrains.anko.startActivity

class MoreFragment : Fragment() {

    private var horizontalAdapter: HorizontalAdapter? = null
    private var horizontaList: ArrayList<MoreHorizontalData> = ArrayList()
    lateinit var homeCommunicator: HomeCommunicator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_more_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        homeCommunicator = activity as HomeCommunicator
        initializeViews()
        clearAddPopulateData()
        onclick()
    }
    private fun onclick() {
        employerList_MBTN.setOnClickListener {
            homeCommunicator.goToFollowedEmployerList("employer")
        }
        generalSearch_MBTN.setOnClickListener {
            startActivity<JobBaseActivity>("keyword" to "")
        }
        appGuides_MBTN.setOnClickListener {
            activity.openUrlInBrowser("http://bdjobs.com/apps/ios/index.html")
        }
        rateUs_MBTN.setOnClickListener {
            activity.openUrlInBrowser("https://play.google.com/store/apps/details?id=com.bdjobs.app")
        }
        feedback_MBTN.setOnClickListener {
            activity.openUrlInBrowser("https://jobs.bdjobs.com/feedback.asp")
        }
        privacypolicy_MBTN.setOnClickListener {
            activity.openUrlInBrowser("https://bdjobs.com/policy/Privacy_policy.asp")
        }
        terms_MBTN.setOnClickListener {
            activity.openUrlInBrowser("https://www.bdjobs.com/tos.asp")
        }
        new_job_MBTN.setOnClickListener {
            startActivity<JobBaseActivity>("postedWithin" to "1")
        }
        deadline_MBTN.setOnClickListener {
            startActivity<JobBaseActivity>("deadline" to "2")
        }
        part_time_MBTN.setOnClickListener {
            startActivity<JobBaseActivity>("jobNature" to "PartTime")
        }
        contractual_MBTN.setOnClickListener {
            startActivity<JobBaseActivity>("jobNature" to "Contract")
        }
        government_MBTN.setOnClickListener {
            startActivity<JobBaseActivity>("organization" to "1")
        }
        overseas_MBTN.setOnClickListener {
            startActivity<JobBaseActivity>("location" to "Country")
        }
        interviewinvitation_MBTN.setOnClickListener {
            homeCommunicator.goToInterviewInvitation("homePage")
        }
        settings_MBTN.setOnClickListener {
            startActivity<SettingBaseActivity>()
        }
    }
    private fun clearAddPopulateData() {
        /*this  function deletes duplicates data lists  */
        if (horizontaList.isNullOrEmpty()){
            populateData()
        }
        else {
            horizontalAdapter?.removeAll()
            horizontaList.clear()
            populateData()
        }
        horizontalAdapter?.addAll(horizontaList)
    }
    private fun populateData() {
        horizontaList.add(MoreHorizontalData(R.drawable.ic_manage, "Manage\nResume"))
        horizontaList.add(MoreHorizontalData(R.drawable.ic_favorite, "Favorite\nSearch"))
        horizontaList.add(MoreHorizontalData(R.drawable.ic_emplist_ic, "Employer\nList"))
        horizontaList.add(MoreHorizontalData(R.drawable.ic_followed, "Followed\nEmployers"))
        horizontaList.add(MoreHorizontalData(R.drawable.ic_applied, "Applied\nJobs"))
    }
    private fun initializeViews(){
        horizontalAdapter = HorizontalAdapter(activity)
        horizontal_RV!!.adapter = horizontalAdapter
        horizontal_RV!!.setHasFixedSize(true)
        horizontal_RV?.layoutManager = LinearLayoutManager(activity, LinearLayout.HORIZONTAL, false)
        Log.d("initPag", "called")

    }

}