package com.bdjobs.app.LoggedInUserLanding

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.Fragment
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Color.*
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.MybdjobsData
import com.bdjobs.app.API.ModelClasses.StatsModelClassData
import com.bdjobs.app.R
import com.bdjobs.app.editResume.EditResLandingActivity
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapePathModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_mybdjobs_layout.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MyBdjobsFragment : Fragment() {


    private var mybdjobsAdapter: MybdjobsAdapter? = null
    private var bdjobsList: ArrayList<MybdjobsData> = ArrayList()
    private lateinit var communicator: HomeCommunicator
    private var lastMonthStatsData: List<StatsModelClassData?>? = null
    private var allStatsData: List<StatsModelClassData?>? = null
    val background_resources = intArrayOf(R.drawable.online_application, R.drawable.times_emailed, R.drawable.viewed_resume, R.drawable.employer_followed, R.drawable.interview_invitation, R.drawable.message_employers)
    val icon_resources = intArrayOf(R.drawable.ic_online_application, R.drawable.ic_times_emailed_my_resume, R.drawable.ic_view_resum, R.drawable.ic_employers_followed, R.drawable.ic_interview_invitation_1, R.drawable.ic_messages_by_employer)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater?.inflate(R.layout.fragment_mybdjobs_layout, container, false)!!

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        communicator = activity as HomeCommunicator
        initializeViews()


    }

    override fun onResume() {
        super.onResume()
        getStatsData()
        onClick()
    }

    private fun initializeViews() {
        mybdjobsAdapter = MybdjobsAdapter(activity)
        myBdjobsgridView_RV!!.adapter = mybdjobsAdapter
        myBdjobsgridView_RV!!.setHasFixedSize(true)
        myBdjobsgridView_RV?.layoutManager = GridLayoutManager(activity, 2, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?
        Log.d("initPag", "called")
        myBdjobsgridView_RV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator() as RecyclerView.ItemAnimator?
    }

    private fun onClick() {
        lastmonth_MBTN?.setOnClickListener {
            communicator.setTime("1")
            lastmonth_MBTN.setBackgroundResource(R.drawable.left_rounded_background_black)
            all_MBTN.setBackgroundResource(R.drawable.right_rounded_background)
            all_MBTN.setTextColor(ColorStateList.valueOf(Color.parseColor("#000000")))
            lastmonth_MBTN.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))

            if (bdjobsList.isNullOrEmpty()) {
                populateDataLastMonthStats()
            } else {
                mybdjobsAdapter?.removeAll()
                bdjobsList.clear()
                populateDataLastMonthStats()
            }

        }
        all_MBTN.setOnClickListener {
//testing
            communicator.setTime("0")
            lastmonth_MBTN.setBackgroundResource(R.drawable.left_rounded_background)
            all_MBTN.setBackgroundResource(R.drawable.right_rounded_background_black)
            lastmonth_MBTN.setTextColor(ColorStateList.valueOf(Color.parseColor("#000000")))
            all_MBTN.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))

            if (bdjobsList.isNullOrEmpty()) {
                populateDataAllMonthStats()
            } else {
                mybdjobsAdapter?.removeAll()
                bdjobsList.clear()
                populateDataAllMonthStats()
            }
        }
        lastmonth_MBTN.performClick()

        nextButtonFAB.setOnClickListener {
            startActivity<EditResLandingActivity>()
        }

    }

    private fun getStatsData() {
        lastMonthStatsData = communicator.getLastStatsData()
        allStatsData = communicator.getAllStatsData()

    }


    private fun populateDataLastMonthStats() {
        for ((index, value) in lastMonthStatsData!!.withIndex()) {
            if (index < (lastMonthStatsData?.size!! - 1)) {
                bdjobsList.add(MybdjobsData(value?.count!!, value?.title!!, background_resources[index], icon_resources[index]))
            }
        }
        mybdjobsAdapter?.addAll(bdjobsList)
    }

    private fun populateDataAllMonthStats() {
        for ((index, value) in allStatsData!!.withIndex()) {
            if (index < (allStatsData?.size!! - 1)) {
                bdjobsList.add(MybdjobsData(value?.count!!, value?.title!!, background_resources[index], icon_resources[index]))
            }
        }
        mybdjobsAdapter?.addAll(bdjobsList)
    }
}