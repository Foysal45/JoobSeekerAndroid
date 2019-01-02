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
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapePathModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_mybdjobs_layout.*
import org.jetbrains.anko.toast

class MyBdjobsFragment : Fragment() {


    private var mybdjobsAdapter: MybdjobsAdapter? = null
    private var bdjobsList: ArrayList<MybdjobsData> = ArrayList()
    private lateinit var communicator: HomeCommunicator
    private var lastMonthStatsData : List<StatsModelClassData?>? = null

    val background_resources = intArrayOf( R.drawable.online_application,R.drawable.times_emailed,R.drawable.viewed_resume,R.drawable.employer_followed,R.drawable.interview_invitation,R.drawable.message_employers)
    val icon_resources = intArrayOf( R.drawable.ic_privacy_policy,R.drawable.ic_privacy_policy,R.drawable.ic_privacy_policy,R.drawable.ic_privacy_policy,R.drawable.ic_privacy_policy,R.drawable.ic_privacy_policy)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater?.inflate(R.layout.fragment_mybdjobs_layout, container, false)!!

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        communicator = activity as HomeCommunicator

        fab()
        lastmonth_MBTN?.setOnClickListener {

            lastmonth_MBTN.setBackgroundResource(R.drawable.left_rounded_background_black)
            all_MBTN.setBackgroundResource(R.drawable.right_rounded_background)

            all_MBTN.setTextColor(ColorStateList.valueOf(Color.parseColor("#000000")))
            lastmonth_MBTN.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
         //   all_MBTN.setTextColor(Color.parseColor("FFFFFF"))


        }

        all_MBTN.setOnClickListener {


           lastmonth_MBTN.setBackgroundResource(R.drawable.left_rounded_background)
            all_MBTN.setBackgroundResource(R.drawable.right_rounded_background_black)
       //     lastmonth_MBTN.setTextColor(Color.parseColor("FFFFFF"))
            lastmonth_MBTN.setTextColor(ColorStateList.valueOf(Color.parseColor("#000000")))
            all_MBTN.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))

        }

        lastmonth_MBTN.performClick()


        initializeViews()




    }

    override fun onResume() {
        super.onResume()
        getLastMonthStats()
        if (bdjobsList.isNullOrEmpty()){
            populateData()
        }
        else {
            mybdjobsAdapter?.removeAll()
            bdjobsList.clear()
            populateData()
            Log.d("ddd", "asche")
        }
    }

    private fun getLastMonthStats() {
        lastMonthStatsData = communicator.getLastStatsData()

    }

    private fun fab(){


        nextButtonFAB.setOnClickListener { view ->
            Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()
        }
    }
    private fun initializeViews(){
        mybdjobsAdapter = MybdjobsAdapter(activity)
        myBdjobsgridView_RV!!.adapter = mybdjobsAdapter
        myBdjobsgridView_RV!!.setHasFixedSize(true)
        myBdjobsgridView_RV?.layoutManager = GridLayoutManager (activity, 2, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?
        Log.d("initPag", "called")
        myBdjobsgridView_RV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator() as RecyclerView.ItemAnimator?
    }
    private fun populateData(){
        for( (index, value) in lastMonthStatsData!!.withIndex()){
            if(index<(lastMonthStatsData?.size!!-1)){
                bdjobsList.add(MybdjobsData(value?.count!!, value?.title!!,background_resources[index], icon_resources[index]))
            }
        }







       /* bdjobsList.add(MybdjobsData(lastMonthStatsData?.get(0)?.count!!, lastMonthStatsData?.get(0)?.title!!, R.drawable.online_application, R.drawable.ic_online_application))
        bdjobsList.add(MybdjobsData(lastMonthStatsData?.get(1)?.count!!, lastMonthStatsData?.get(1)?.title!!,R.drawable.times_emailed, R.drawable.ic_view_resum))
        bdjobsList.add(MybdjobsData(lastMonthStatsData?.get(2)?.count!!, lastMonthStatsData?.get(2)?.title!!,R.drawable.viewed_resume, R.drawable.ic_privacy_policy))
        bdjobsList.add(MybdjobsData(lastMonthStatsData?.get(3)?.count!!, lastMonthStatsData?.get(3)?.title!!,R.drawable.employer_followed, R.drawable.ic_privacy_policy))
        bdjobsList.add(MybdjobsData(lastMonthStatsData?.get(4)?.count!!, lastMonthStatsData?.get(4)?.title!!,R.drawable.interview_invitation, R.drawable.ic_privacy_policy))
        bdjobsList.add(MybdjobsData(lastMonthStatsData?.get(5)?.count!!, lastMonthStatsData?.get(5)?.title!!,R.drawable.message_employers, R.drawable.ic_privacy_policy))

        // horizontaList.add(horizontalDataa
        //Log.d("ddd", bdjobsList.toString())*/


           // mybdjobsAdapter?.removeAll()
        mybdjobsAdapter?.addAll(bdjobsList)


    }
}