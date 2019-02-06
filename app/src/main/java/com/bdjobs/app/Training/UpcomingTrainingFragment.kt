package com.bdjobs.app.Training


import android.os.Bundle
import android.app.Fragment
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
import kotlinx.android.synthetic.main.fragment_upcoming_training.*
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
    private var bdjobsUserSession : BdjobsUserSession? = null
    private var upcomingTrainingAdapter: UpcomingTrainingAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming_training, container, false)
    }

    override fun onResume() {
        super.onResume()
        bdjobsUserSession = BdjobsUserSession(activity!!)
        upcomingTrainingAdapter= UpcomingTrainingAdapter(activity!!)
        loadTrainingList()
    }

    private fun loadTrainingList() {
        ApiServiceMyBdjobs.create().getTrainingList(
                userID = bdjobsUserSession?.userId,
                decodeID = bdjobsUserSession?.decodId

        ).enqueue(object : Callback<TrainingList>{
            override fun onFailure(call: Call<TrainingList>, t: Throwable) {

            }

            override fun onResponse(call: Call<TrainingList>, response: Response<TrainingList>) {
                if (response.isSuccessful){
                    trainListRV?.adapter = upcomingTrainingAdapter
                    trainListRV?.setHasFixedSize(true)
                    trainListRV?.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                    Log.d("initPag", "called")
                    trainListRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                    upcomingTrainingAdapter?.addAll(response.body()?.data as List<TrainingListData>)
                }

                 }

        })

    }


}
