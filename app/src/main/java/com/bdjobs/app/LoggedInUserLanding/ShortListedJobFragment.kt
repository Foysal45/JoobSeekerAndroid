package com.bdjobs.app.LoggedInUserLanding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.Fragment
import android.text.Html
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.API.ModelClasses.JobListModelData
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Jobs.JoblistAdapter
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.loadCircularImageFromUrl
import com.bdjobs.app.Utilities.toSimpleDateString
import kotlinx.android.synthetic.main.fragment_shortlisted_job_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ShortListedJobFragment : Fragment() {
    lateinit var bdjobsDB: BdjobsDB
    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var joblistAdapter: JoblistAdapter
    lateinit var homeCommunicator: HomeCommunicator

    var favListSize = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_shortlisted_job_layout, container, false)!!
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsDB = BdjobsDB.getInstance(activity)
        bdjobsUserSession = BdjobsUserSession(activity)
        homeCommunicator = activity as HomeCommunicator
        profilePicIMGV?.loadCircularImageFromUrl(bdjobsUserSession.userPicUrl)
        searchIMGV?.setOnClickListener {
            homeCommunicator.goToKeywordSuggestion()
        }
    }

    override fun onResume() {
        super.onResume()

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
                joblistAdapter = JoblistAdapter(activity)
                shortListRV?.adapter = joblistAdapter
                joblistAdapter?.addAllTest(jobList)
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