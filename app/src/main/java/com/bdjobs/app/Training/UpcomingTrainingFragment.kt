package com.bdjobs.app.Training


import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.TrainingList
import com.bdjobs.app.API.ModelClasses.TrainingListData
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.fragment_upcoming_training.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpcomingTrainingFragment : Fragment() {
    private var topBottomPadding: Int = 0
    private var leftRightPadding: Int = 0
    private var bdjobsUserSession: BdjobsUserSession? = null
    private var upcomingTrainingAdapter: UpcomingTrainingAdapter? = null
    private var trainingCommunicator: TrainingCommunicator? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming_training, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsUserSession = BdjobsUserSession(activity!!)
        upcomingTrainingAdapter = UpcomingTrainingAdapter(activity!!)
        trainingCommunicator = activity as TrainingCommunicator

        val scale = resources.displayMetrics.density
        topBottomPadding = (8 * scale + 0.5f).toInt()
        leftRightPadding = (16 * scale + 0.5f).toInt()
        //Log.d("scale", "s = " + scale)

        if (Constants.matchedTraining) {

            allTV.performClick()
            lastOnClickAction()
        } else if (!Constants.matchedTraining) {

            matchedTV.performClick()
            allOnClickAction()

        }


        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)

    }

    override fun onResume() {
        super.onResume()

        backIMV.setOnClickListener {
            trainingCommunicator?.backButtonClicked()
        }
        matchedTV.setOnClickListener {
            if (!Constants.matchedTraining) {

                lastOnClickAction()
            }
        }

        allTV.setOnClickListener {
            if (Constants.matchedTraining) {

                allOnClickAction()
            }

        }


    }

    private fun lastOnClickAction() {
        // lastSelected()
        lastSelected()
        loadTrainingList("")
        Constants.matchedTraining = true
    }

    private fun allOnClickAction() {
        // allSelected()
        allSelected()
        loadTrainingList(bdjobsUserSession?.trainingId!!)
        Constants.matchedTraining = false

    }

    private fun loadTrainingList(trainid: String) {
        try {
            trainListRV?.hide()
            shimmer_view_container_trainingList?.show()
            shimmer_view_container_trainingList?.startShimmerAnimation()
            numberTV.text = "0"
            ApiServiceMyBdjobs.create().getTrainingList(
                    userID = bdjobsUserSession?.userId,
                    decodeID = bdjobsUserSession?.decodId,
                    traingId = trainid,
                    AppsDate = ""

            ).enqueue(object : Callback<TrainingList> {
                override fun onFailure(call: Call<TrainingList>, t: Throwable) {

                }

                override fun onResponse(call: Call<TrainingList>, response: Response<TrainingList>) {

                    try {
                        /*Log.d("value", "userid = " + bdjobsUserSession?.userId
                                + "decodeid = " + bdjobsUserSession?.decodId
                                + "trainid= " + trainid
                                + "AppsDate= " + ""
                        )*/

                        if (response.isSuccessful) {

                            trainListRV?.adapter = upcomingTrainingAdapter
                            trainListRV?.setHasFixedSize(true)
                            trainListRV?.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                            //Log.d("initPag", response.body()?.data?.size.toString())
                            trainListRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                            upcomingTrainingAdapter?.removeAll()
                            upcomingTrainingAdapter?.addAll(response.body()?.data as List<TrainingListData>)
                            numberTV?.text = response.body()?.data?.size.toString()
                            //
                        }
                        trainListRV?.show()
                        shimmer_view_container_trainingList?.hide()
                        shimmer_view_container_trainingList?.stopShimmerAnimation()
                    } catch (e: Exception) {
                        logException(e)
                    }


                }

            })
        } catch (e: Exception) {
            logException(e)
        }

    }

/*    private fun lastSelected() {
        lastmonth_MBTN.setBackgroundColor(Color.parseColor("#424242"))
        all_MBTN.setBackgroundColor(Color.parseColor("#FFFFFF"))
        all_MBTN?.setTextColor(ColorStateList.valueOf(Color.parseColor("#424242")))
        lastmonth_MBTN?.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
    }

    private fun allSelected() {

        lastmonth_MBTN.setBackgroundColor(Color.parseColor("#FFFFFF"))
        all_MBTN.setBackgroundColor(Color.parseColor("#424242"))
        lastmonth_MBTN?.setTextColor(ColorStateList.valueOf(Color.parseColor("#424242")))
        all_MBTN?.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
    }*/

    private fun lastSelected() {
        matchedTV.setTextColor(Color.parseColor("#FFFFFF"))
        matchedTV.setBackgroundResource(R.drawable.success_active_bg)
        allTV.setTextColor(Color.parseColor("#424242"))
        allTV.setBackgroundResource(R.drawable.pending_inactive_bg)
        matchedTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
        allTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
//        state = 0
//        verifyStatus = "1"

    }

    private fun allSelected() {
        allTV.setTextColor(Color.parseColor("#FFFFFF"))
        allTV.setBackgroundResource(R.drawable.pending_active_bg)
        matchedTV.setTextColor(Color.parseColor("#424242"))
        matchedTV.setBackgroundResource(R.drawable.success_inactive_bg)
        matchedTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
        allTV.setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding)
//        state = 1
//        verifyStatus = "0"
    }
}
