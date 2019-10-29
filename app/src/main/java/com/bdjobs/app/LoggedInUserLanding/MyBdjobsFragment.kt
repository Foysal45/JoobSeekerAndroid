package com.bdjobs.app.LoggedInUserLanding

import android.app.Fragment
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.MybdjobsData
import com.bdjobs.app.API.ModelClasses.StatsModelClassData
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import kotlinx.android.synthetic.main.fragment_mybdjobs_layout.*

class MyBdjobsFragment : Fragment() {
    private lateinit var bdjobsUserSession : BdjobsUserSession
    private var mybdjobsAdapter: MybdjobsAdapter? = null
    private var bdjobsList: ArrayList<MybdjobsData> = ArrayList()
    private lateinit var communicator: HomeCommunicator
    private var lastMonthStatsData: List<StatsModelClassData?>? = null
    private var allStatsData: List<StatsModelClassData?>? = null
    val background_resources = intArrayOf(R.drawable.online_application, R.drawable.times_emailed, R.drawable.viewed_resume, R.drawable.employer_followed, R.drawable.interview_invitation, R.drawable.message_employers)
    val icon_resources = intArrayOf(R.drawable.ic_online_application, R.drawable.ic_times_emailed_my_resume, R.drawable.ic_view_resum, R.drawable.ic_employers_followed, R.drawable.ic_interview_invitation_1, R.drawable.ic_messages_by_employer)
    private lateinit var session: BdjobsUserSession
    private fun populateDataModel() {
        try {
            mybdjobsAdapter?.removeAll()
            bdjobsList.clear()
            mybdjobsAdapter?.notifyDataSetChanged()
            bdjobsList.add(MybdjobsData(session.mybdjobscount_jobs_applied_lastmonth!!, Constants.session_key_mybdjobscount_jobs_applied, background_resources[0], icon_resources[0]))
            bdjobsList.add(MybdjobsData(session.mybdjobscount_times_emailed_resume_lastmonth!!, Constants.session_key_mybdjobscount_times_emailed_resume, background_resources[1], icon_resources[1]))
            bdjobsList.add(MybdjobsData(session.mybdjobscount_employers_viwed_resume_lastmonth!!, Constants.session_key_mybdjobscount_employers_viwed_resume, background_resources[2], icon_resources[2]))
            bdjobsList.add(MybdjobsData(session.mybdjobscount_employers_followed_lastmonth!!, Constants.session_key_mybdjobscount_employers_followed, background_resources[3], icon_resources[3]))
            bdjobsList.add(MybdjobsData(session.mybdjobscount_interview_invitation_lastmonth!!, Constants.session_key_mybdjobscount_interview_invitation, background_resources[4], icon_resources[4]))
            bdjobsList.add(MybdjobsData(session.mybdjobscount_message_by_employers_lastmonth!!,  Constants.session_key_mybdjobscount_message_by_employers, background_resources[5], icon_resources[5]))
            mybdjobsAdapter?.addAll(bdjobsList)
        } catch (e: Exception) {
        }
    }
    private fun populateDataModelALL() {
        try {
            mybdjobsAdapter?.removeAll()
            bdjobsList.clear()
            mybdjobsAdapter?.notifyDataSetChanged()
            bdjobsList.add(MybdjobsData(session.mybdjobscount_jobs_applied_alltime!!, Constants.session_key_mybdjobscount_jobs_applied, background_resources[0], icon_resources[0]))
            bdjobsList.add(MybdjobsData(session.mybdjobscount_times_emailed_resume_alltime!!, Constants.session_key_mybdjobscount_times_emailed_resume, background_resources[1], icon_resources[1]))
            bdjobsList.add(MybdjobsData(session.mybdjobscount_employers_viwed_resume_alltime!!, Constants.session_key_mybdjobscount_employers_viwed_resume, background_resources[2], icon_resources[2]))
            bdjobsList.add(MybdjobsData(session.mybdjobscount_employers_followed_alltime!!, Constants.session_key_mybdjobscount_employers_followed, background_resources[3], icon_resources[3]))
            bdjobsList.add(MybdjobsData(session.mybdjobscount_interview_invitation_alltime!!, Constants.session_key_mybdjobscount_interview_invitation, background_resources[4], icon_resources[4]))
            bdjobsList.add(MybdjobsData(session.mybdjobscount_message_by_employers_alltime!!,  Constants.session_key_mybdjobscount_message_by_employers, background_resources[5], icon_resources[5]))
            mybdjobsAdapter?.addAll(bdjobsList)
        } catch (e: Exception) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mybdjobs_layout, container, false)!!

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        communicator = activity as HomeCommunicator
        bdjobsUserSession = BdjobsUserSession(activity)

    }

    override fun onResume() {
        super.onResume()
        session = BdjobsUserSession(activity)
        initializeViews()
        onClick()
        getCountData()
        showNotificationCount()
    }

    private fun showNotificationCount() {
        try {
            bdjobsUserSession = BdjobsUserSession(activity)
            if (bdjobsUserSession.notificationCount!! <= 0) {
                notificationCountTV?.hide()
            } else {
                notificationCountTV?.show()
                if (bdjobsUserSession.notificationCount!! > 99) {
                    notificationCountTV?.text = "99+"

                } else {
                    notificationCountTV?.text = "${bdjobsUserSession.notificationCount!!}"

                }
            }
        } catch (e: Exception) {
        }
    }

    private fun getCountData() {
        Log.d("\nmybdjobsCountTest","mybdjobscount_jobs_applied_lastmonth: ${session.mybdjobscount_jobs_applied_lastmonth}\n" +
                "mybdjobscount_times_emailed_resume_lastmonth: ${session.mybdjobscount_times_emailed_resume_lastmonth}\n" +
                "mybdjobscount_employers_viwed_resume_lastmonth: ${session.mybdjobscount_employers_viwed_resume_lastmonth}\n" +
                "mybdjobscount_employers_followed_lastmonth: ${session.mybdjobscount_employers_followed_lastmonth}\n" +
                "mybdjobscount_interview_invitation_lastmonth: ${session.mybdjobscount_interview_invitation_lastmonth}\n" +
                "mybdjobscount_message_by_employers_lastmonth: ${session.mybdjobscount_message_by_employers_lastmonth}\n\n\n" +

                "mybdjobscount_jobs_applied_alltime: ${session.mybdjobscount_jobs_applied_alltime}\n" +
                "mybdjobscount_times_emailed_resume_alltime: ${session.mybdjobscount_times_emailed_resume_alltime}\n" +
                "mybdjobscount_employers_viwed_resume_alltime: ${session.mybdjobscount_employers_viwed_resume_alltime}\n" +
                "mybdjobscount_employers_followed_alltime: ${session.mybdjobscount_employers_followed_alltime}\n" +
                "mybdjobscount_interview_invitation_lastmonth: ${session.mybdjobscount_interview_invitation_alltime}\n" +
                "mybdjobscount_message_by_employers_alltime: ${session.mybdjobscount_message_by_employers_alltime}\n"
        )
    }

    private fun initializeViews() {
        mybdjobsAdapter = MybdjobsAdapter(activity)
        myBdjobsgridView_RV?.adapter = mybdjobsAdapter
        myBdjobsgridView_RV?.setHasFixedSize(true)
        myBdjobsgridView_RV?.layoutManager = GridLayoutManager(activity, 2, RecyclerView.VERTICAL, false)
        Log.d("initPag", "called")
        myBdjobsgridView_RV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        populateDataModel()
    }

    private fun onClick() {
        searchIMGV.setOnClickListener {
            communicator.gotoJobSearch()
        }

        profilePicIMGV.setOnClickListener {
            communicator.gotoEditresume()
        }

        notificationIMGV?.setOnClickListener {
            communicator.goToNotifications()
        }

        profilePicIMGV?.loadCircularImageFromUrl(BdjobsUserSession(activity).userPicUrl?.trim())

        lastmonth_MBTN?.setOnClickListener {
            getStatsData(1.toString())
            populateDataModel()
            communicator.setTime("1")
            Constants.myBdjobsStatsLastMonth = true
            Constants.timesEmailedResumeLast = true
            lastmonth_MBTN?.setBackgroundResource(R.drawable.left_rounded_background_black)
            all_MBTN?.setBackgroundResource(R.drawable.right_rounded_background)
            all_MBTN?.setTextColor(ColorStateList.valueOf(Color.parseColor("#000000")))
            lastmonth_MBTN?.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
            //  populateDataLastMonthStats()
            lastmonth_MBTN?.isEnabled = false
            all_MBTN?.isEnabled = true

        }
        all_MBTN?.setOnClickListener {
            getStatsData(0.toString())
            populateDataModelALL()
            communicator.setTime("0")
            Constants.myBdjobsStatsLastMonth = false
            Constants.timesEmailedResumeLast = false
            lastmonth_MBTN?.setBackgroundResource(R.drawable.left_rounded_background)
            all_MBTN?.setBackgroundResource(R.drawable.right_rounded_background_black)
            lastmonth_MBTN?.setTextColor(ColorStateList.valueOf(Color.parseColor("#000000")))
            all_MBTN?.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
            //  populateDataAllMonthStats()
            all_MBTN?.isEnabled = false
            lastmonth_MBTN?.isEnabled = true
        }

        nextButtonFAB?.setOnClickListener {
            communicator.showManageResumePopup()
        }

        Log.d("sagor", "sagor= " + Constants.myBdjobsStatsLastMonth)
        if (Constants.myBdjobsStatsLastMonth) {
            lastmonth_MBTN?.performClick()
        } else if (!Constants.myBdjobsStatsLastMonth) {
            all_MBTN?.performClick()
        }
    }

    private fun populateDataLastMonthStats() {
        try {
            for ((index, value) in lastMonthStatsData!!.withIndex()) {
                if (index < (lastMonthStatsData?.size!! - 1)) {
                    bdjobsList.add(MybdjobsData(value?.count!!, value.title!!, background_resources[index], icon_resources[index]))
                    Log.d("vvuu", "${bdjobsList?.get(index)?.itemID} = ${value?.count!!}")
                }
            }
            mybdjobsAdapter?.addAll(bdjobsList)
        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun populateDataAllMonthStats() {
        try {
            for ((index, value) in allStatsData!!.withIndex()) {
                if (index < (allStatsData?.size!! - 1)) {
                    bdjobsList.add(MybdjobsData(value?.count!!, value.title!!, background_resources[index], icon_resources[index]))
                }
            }
            mybdjobsAdapter?.addAll(bdjobsList)
        } catch (e: Exception) {
            logException(e)
        }
    }



    private fun getStatsData(activityDate: String) {
        //activity.showProgressBar(mybdjobsLoadingProgressBar)

      /*  mybdjobsLoadingProgressBar?.show()

        ApiServiceMyBdjobs.create().mybdjobStats(
                userId = session.userId,
                decodeId = session.decodId,
                isActivityDate = activityDate,
                trainingId = session.trainingId,
                isResumeUpdate = session.IsResumeUpdate

        ).enqueue(object : Callback<StatsModelClass> {
            override fun onFailure(call: Call<StatsModelClass>, t: Throwable) {
                try {
                    error("onFailure", t)
                    //activity?.stopProgressBar(mybdjobsLoadingProgressBar)
                    mybdjobsLoadingProgressBar?.hide()
                    activity?.toast(R.string.message_common_error)
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<StatsModelClass>, response: Response<StatsModelClass>) {
                // activity?.stopProgressBar(mybdjobsLoadingProgressBar)
                mybdjobsLoadingProgressBar?.hide()
                try {
                    if (activityDate == "0") {
                        allStatsData = response.body()?.data
                        //populateDataAllMonthStats()
                        if (bdjobsList.isNullOrEmpty()) {
                            populateDataAllMonthStats()
                        } else {
                            mybdjobsAdapter?.removeAll()
                            bdjobsList.clear()
                            populateDataAllMonthStats()
                        }
                    } else if (activityDate == "1") {
                        lastMonthStatsData = response.body()?.data
                        if (bdjobsList.isNullOrEmpty()) {
                            populateDataLastMonthStats()
                        } else {
                            mybdjobsAdapter?.removeAll()
                            bdjobsList.clear()
                            populateDataLastMonthStats()
                        }
                    }

                    // Log.d("respp", " === $allStatsData \n $lastMonthStatsData")
                } catch (e: Exception) {
                    logException(e)
                }
            }

        })*/
    }

    fun updateNotificationView(count: Int?) {
        Log.d("rakib", "in mybdjobs fragment $count")
        if (count!! > 0) {
            notificationCountTV?.show()
            if (count <= 99)
                notificationCountTV?.text = "$count"
            else
                notificationCountTV?.text = "99+"
        } else {
            notificationCountTV?.hide()
        }

//        notificationCountTV?.show()
//        bdjobsUserSession = BdjobsUserSession(activity)
//        if (bdjobsUserSession.notificationCount!! > 99){
//            notificationCountTV?.text = "99+"
//
//        } else{
//            notificationCountTV?.text = "${bdjobsUserSession.notificationCount!!}"
//
//        }
    }
}