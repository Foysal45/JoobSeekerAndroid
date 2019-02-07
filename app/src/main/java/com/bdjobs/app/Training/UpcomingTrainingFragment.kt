package com.bdjobs.app.Training


import android.os.Bundle
import android.app.Fragment
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.TrainingList
import com.bdjobs.app.API.ModelClasses.TrainingListData

import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_upcoming_training.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class UpcomingTrainingFragment : Fragment() {
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

        if (Constants.matchedTraining) {
            last()
            lastmonth_MBTN.performClick()
            loadTrainingList(bdjobsUserSession?.trainingId!!)
            Constants.matchedTraining = true
        } else if (!Constants.matchedTraining) {
            all()
            all_MBTN.performClick()
            Constants.matchedTraining = false
            loadTrainingList("")

        }

    }

    override fun onResume() {
        super.onResume()


        /*  if (Constants.matchedTraining) {
              lastmonth_MBTN.performClick()
          } else if (!Constants.matchedTraining) {
              all_MBTN.performClick()
          }*/
        backIMV.setOnClickListener {
            trainingCommunicator?.backButtonClicked()
        }
        lastmonth_MBTN.setOnClickListener {
            if (!Constants.matchedTraining) {
                last()
                loadTrainingList(bdjobsUserSession?.trainingId!!)
                Constants.matchedTraining = true
            }
        }

        all_MBTN.setOnClickListener {
            if (Constants.matchedTraining) {
                all()
                Constants.matchedTraining = false
                loadTrainingList("")
            }

        }


    }

    private fun loadTrainingList(trainid: String) {
        trainListRV?.hide()
        shimmer_view_container_trainingList?.show()
        shimmer_view_container_trainingList?.startShimmerAnimation()

        ApiServiceMyBdjobs.create().getTrainingList(
                userID = bdjobsUserSession?.userId,
                decodeID = bdjobsUserSession?.decodId,
                traingId = trainid,
                AppsDate = ""

        ).enqueue(object : Callback<TrainingList> {
            override fun onFailure(call: Call<TrainingList>, t: Throwable) {

            }

            override fun onResponse(call: Call<TrainingList>, response: Response<TrainingList>) {

                Log.d("value", "userid = " + bdjobsUserSession?.userId
                        + "decodeid = " + bdjobsUserSession?.decodId
                        + "trainid= " + trainid
                        + "AppsDate= " + ""


                )

                if (response.isSuccessful) {

                    trainListRV?.adapter = upcomingTrainingAdapter
                    trainListRV?.setHasFixedSize(true)
                    trainListRV?.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                    Log.d("initPag", response.body()?.data?.size.toString())
                    trainListRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                    upcomingTrainingAdapter?.removeAll()
                    upcomingTrainingAdapter?.addAll(response.body()?.data as List<TrainingListData>)
                }
                trainListRV?.show()
                shimmer_view_container_trainingList?.hide()
                shimmer_view_container_trainingList?.stopShimmerAnimation()


            }

        })

    }

    private fun last() {
      //  lastmonth_MBTN?.setBackgroundResource(R.drawable.left_rounded_background_black)
      //  all_MBTN?.setBackgroundResource(R.drawable.right_rounded_background)
        all_MBTN?.setTextColor(ColorStateList.valueOf(Color.parseColor("#000000")))
        lastmonth_MBTN?.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
    }

    private fun all() {
      //  lastmonth_MBTN?.setBackgroundResource(R.drawable.left_rounded_background)
     //   all_MBTN?.setBackgroundResource(R.drawable.right_rounded_background_black)
        lastmonth_MBTN?.setTextColor(ColorStateList.valueOf(Color.parseColor("#000000")))
        all_MBTN?.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
    }


}
