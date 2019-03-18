package com.bdjobs.app.LoggedInUserLanding

import android.app.Fragment
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ModelClasses.JobListModelData
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Jobs.JoblistAdapter
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import kotlinx.android.synthetic.main.fragment_shortlisted_job_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.selector
import org.jetbrains.anko.uiThread
import java.util.*

class ShortListedJobFragment : Fragment() {
    lateinit var bdjobsDB: BdjobsDB
    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var joblistAdapter: JoblistAdapter
    lateinit var homeCommunicator: HomeCommunicator


    var favListSize = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_shortlisted_job_layout, container, false)!!
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsDB = BdjobsDB.getInstance(activity)
        bdjobsUserSession = BdjobsUserSession(activity)
        homeCommunicator = activity as HomeCommunicator
        profilePicIMGV?.loadCircularImageFromUrl(bdjobsUserSession.userPicUrl)
        searchIMGV?.setOnClickListener {
            homeCommunicator.gotoJobSearch()
        }
        profilePicIMGV?.setOnClickListener {
            homeCommunicator.gotoEditresume()
        }

        filterTV?.setOnClickListener {
            val  deadline = arrayOf("Today", "Tomorrow", "Next 2 days","Next 3 days","Next 4 days")
            selector("Jobs expire in", deadline.toList()) { dialogInterface, i ->
                showShortListFIlterList(deadline[i])
            }
        }

        crossBTN?.setOnClickListener {
            showShortListFIlterList("")
        }
    }

    override fun onResume() {
        super.onResume()
        val shortListFilter = homeCommunicator.getShortListFilter()
        showShortListFIlterList(shortListFilter)
    }

    private fun showShortListFIlterList(shortListFilter: String) {
        filterTV.text = shortListFilter
        homeCommunicator.setShortListFilter(shortListFilter)
        when(shortListFilter){
            ""->{
                crossBTN.hide()
                getShortListedJobs()
            }
            "Today"->{
                crossBTN.show()
                val calendar = Calendar.getInstance()
                val deadlineToday = calendar.time
                getShortListedJobsByDeadline(deadlineToday)
            }
            "Tomorrow"->{
                crossBTN.show()
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                val deadlineNextDay = calendar.time
                getShortListedJobsByDeadline(deadlineNextDay)
            }
            "Next 2 days"->{
                crossBTN.show()
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, 2)
                val deadlineNext2Days = calendar.time
                getShortListedJobsByDeadline(deadlineNext2Days)
            }
            "Next 3 days"->{
                crossBTN.show()
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, 3)
                val deadlineNext3Days = calendar.time
                getShortListedJobsByDeadline(deadlineNext3Days)
            }
            "Next 4 days"->{
                crossBTN.show()
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, 4)
                val deadlineNext4Days = calendar.time
                getShortListedJobsByDeadline(deadlineNext4Days)
            }
        }
    }

    private fun getShortListedJobsByDeadline(deadline:Date) {
        doAsync {

            val shortListedJobs = bdjobsDB.shortListedJobDao().getShortListedJobsBYDeadline(deadline)

            val jobList: MutableList<JobListModelData> = java.util.ArrayList()

            for (item in shortListedJobs) {
                Log.d("shortListedJobs","Deadline: ${item.deadline}")
                val jobListModelData = JobListModelData(
                        jobid = item.jobid,
                        jobTitle = item.jobtitle,
                        companyName = item.companyname,
                        deadline = item.deadline?.toSimpleDateString(),
                        eduRec = item.eduRec,
                        experience = item.experience,
                        standout = item.standout,
                        logo = item.logo,
                        lantype = item.lantype
                )
                jobList.add(jobListModelData)
            }

            uiThread {
                joblistAdapter = JoblistAdapter(activity)
                shortListRV?.adapter = joblistAdapter
                joblistAdapter.addAllTest(jobList)
                joblistAdapter.notifyDataSetChanged()

                favListSize= jobList.size

                if (favListSize> 1) {
                    val styledText = "<b><font color='#13A10E'>$favListSize</font></b> Shortlisted jobs"
                    jobCountTV?.text = Html.fromHtml(styledText)
                } else {
                    val styledText = "<b><font color='#13A10E'>$favListSize</font></b> Shortlisted job"
                    jobCountTV?.text = Html.fromHtml(styledText)
                }
            }

        }
    }

    private fun getShortListedJobs() {
        doAsync {

            val shortListedJobs = bdjobsDB.shortListedJobDao().getAllShortListedJobs()

            val jobList: MutableList<JobListModelData> = java.util.ArrayList()

            for (item in shortListedJobs) {
                Log.d("shortListedJobs","Deadline: ${item.deadline}")
                val jobListModelData = JobListModelData(
                        jobid = item.jobid,
                        jobTitle = item.jobtitle,
                        companyName = item.companyname,
                        deadline = item.deadline?.toSimpleDateString(),
                        eduRec = item.eduRec,
                        experience = item.experience,
                        standout = item.standout,
                        logo = item.logo,
                        lantype = item.lantype
                )
                jobList.add(jobListModelData)
            }

            uiThread {
                try {
                    joblistAdapter = JoblistAdapter(activity)
                    shortListRV?.adapter = joblistAdapter
                    joblistAdapter.addAllTest(jobList)
                    joblistAdapter.notifyDataSetChanged()

                    favListSize = jobList.size

                    if (favListSize > 1) {
                        val styledText = "<b><font color='#13A10E'>$favListSize</font></b> Shortlisted jobs"
                        jobCountTV?.text = Html.fromHtml(styledText)
                    } else {
                        val styledText = "<b><font color='#13A10E'>$favListSize</font></b> Shortlisted job"
                        jobCountTV?.text = Html.fromHtml(styledText)
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }

        }
    }


    fun scrollToUndoPosition(position:Int){
        shortListRV?.scrollToPosition(position)
        favListSize++
        if (favListSize> 1) {
            val styledText = "<b><font color='#13A10E'>$favListSize</font></b> Shortlisted jobs"
            jobCountTV?.text = Html.fromHtml(styledText)
        } else {
            val styledText = "<b><font color='#13A10E'>$favListSize</font></b> Shortlisted job"
            jobCountTV?.text = Html.fromHtml(styledText)
        }

    }

    fun decrementCounter(){
        favListSize--
        if (favListSize> 1) {
            val styledText = "<b><font color='#13A10E'>$favListSize</font></b> Shortlisted jobs"
            jobCountTV?.text = Html.fromHtml(styledText)
        } else {
            val styledText = "<b><font color='#13A10E'>$favListSize</font></b> Shortlisted job"
            jobCountTV?.text = Html.fromHtml(styledText)
        }
    }
}