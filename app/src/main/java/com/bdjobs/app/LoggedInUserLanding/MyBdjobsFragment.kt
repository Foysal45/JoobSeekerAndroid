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
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.MybdjobsData
import com.bdjobs.app.API.ModelClasses.StatsModelClass
import com.bdjobs.app.API.ModelClasses.StatsModelClassData
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.EditResLandingActivity
import kotlinx.android.synthetic.main.fragment_mybdjobs_layout.*
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyBdjobsFragment : Fragment() {


    private var mybdjobsAdapter: MybdjobsAdapter? = null
    private var bdjobsList: ArrayList<MybdjobsData> = ArrayList()
    private lateinit var communicator: HomeCommunicator
    private var lastMonthStatsData: List<StatsModelClassData?>? = null
    private var allStatsData: List<StatsModelClassData?>? = null
    val background_resources = intArrayOf(R.drawable.online_application, R.drawable.times_emailed, R.drawable.viewed_resume, R.drawable.employer_followed, R.drawable.interview_invitation, R.drawable.message_employers)
    val icon_resources = intArrayOf(R.drawable.ic_online_application, R.drawable.ic_times_emailed_my_resume, R.drawable.ic_view_resum, R.drawable.ic_employers_followed, R.drawable.ic_interview_invitation_1, R.drawable.ic_messages_by_employer)
    private lateinit var session: BdjobsUserSession


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_mybdjobs_layout, container, false)!!

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        communicator = activity as HomeCommunicator
        session = BdjobsUserSession(activity)
        initializeViews()


    }

    override fun onResume() {
        super.onResume()
        //  getStatsData()
        onClick()
    }

    private fun initializeViews() {
        mybdjobsAdapter = MybdjobsAdapter(activity)
        myBdjobsgridView_RV!!.adapter = mybdjobsAdapter
        myBdjobsgridView_RV!!.setHasFixedSize(true)
        myBdjobsgridView_RV?.layoutManager = GridLayoutManager(activity, 2, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?
        Log.d("initPag", "called")
        myBdjobsgridView_RV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
    }

    private fun onClick() {
        lastmonth_MBTN?.setOnClickListener {
            getStatsData(1.toString())
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

            /*    if (bdjobsList.isNullOrEmpty()) {
                    populateDataLastMonthStats()
                } else {
                    mybdjobsAdapter?.removeAll()
                    bdjobsList.clear()
                    populateDataLastMonthStats()
                }*/

        }
        all_MBTN?.setOnClickListener {
            //testing
            getStatsData(0.toString())
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
            /*  if (bdjobsList.isNullOrEmpty()) {
                  populateDataAllMonthStats()
              } else {
                  mybdjobsAdapter?.removeAll()
                  bdjobsList.clear()
                  populateDataAllMonthStats()
              }*/
        }

        nextButtonFAB?.setOnClickListener {
            startActivity<EditResLandingActivity>()
        }

        Log.d("sagor", "sagor= " + Constants.myBdjobsStatsLastMonth)
        if (Constants.myBdjobsStatsLastMonth) {
            lastmonth_MBTN?.performClick()
        } else if (!Constants.myBdjobsStatsLastMonth) {
            all_MBTN?.performClick()

        }
    }

    /* private fun getStatsData() {
         lastMonthStatsData = communicator.getLastStatsData()
         allStatsData = communicator.getAllStatsData()
         getStatsData()
     }*/


    private fun populateDataLastMonthStats() {
        try {
            for ((index, value) in lastMonthStatsData!!.withIndex()) {
                if (index < (lastMonthStatsData?.size!! - 1)) {
                    bdjobsList.add(MybdjobsData(value?.count!!, value.title!!, background_resources[index], icon_resources[index]))
               Log.d("vvuu","${ bdjobsList?.get(index)?.itemID} = ${value?.count!!}")

                   // bdjobsList?.get(index)?.
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
    private fun populateDataAllMonthStats2() {
        try {
            for ((index, value) in allStatsData!!.withIndex()) {
                if (index < (allStatsData?.size!! - 1)) {
               //     bdjobsList.add(MybdjobsData(value?.count!!, value.title!!, background_resources[index], icon_resources[index]))
              bdjobsList?.get(index)?.itemID = value?.count!!

                }
            }
            mybdjobsAdapter?.addAll(bdjobsList)
        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun getStatsData(activityDate: String) {
        activity.showProgressBar(mybdjobsLoadingProgressBar)
       /* myBdjobsgridView_RV?.visibility = View.INVISIBLE
        shimmer_view_container_JobList?.show()
        shimmer_view_container_JobList?.startShimmerAnimation()*/

        ApiServiceMyBdjobs.create().mybdjobStats(
                userId = session.userId,
                decodeId = session.decodId,
                isActivityDate = activityDate,
                trainingId = session.trainingId,
                isResumeUpdate = session.IsResumeUpdate

        ).enqueue(object : Callback<StatsModelClass> {
            override fun onFailure(call: Call<StatsModelClass>, t: Throwable) {

            }

            override fun onResponse(call: Call<StatsModelClass>, response: Response<StatsModelClass>) {
                activity?.stopProgressBar(mybdjobsLoadingProgressBar)
                try {
                   /* myBdjobsgridView_RV?.visibility = View.VISIBLE
                    shimmer_view_container_JobList?.hide()
                    shimmer_view_container_JobList?.stopShimmerAnimation()*/

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
                        //  populateDataLastMonthStats()
                        if (bdjobsList.isNullOrEmpty()) {
                            populateDataLastMonthStats()
                        } else {
                            mybdjobsAdapter?.removeAll()
                            bdjobsList.clear()
                            populateDataLastMonthStats()
                          /*  mybdjobsAdapter?.mybdjobsItems?.get(0)?.itemID = lastMonthStatsData?.get(0)?.count!!
*/
                        }
                    }

                    Log.d("respp", " === $allStatsData \n $lastMonthStatsData")
                } catch (e: Exception) {
                    logException(e)
                }
            }

        })
    }
}