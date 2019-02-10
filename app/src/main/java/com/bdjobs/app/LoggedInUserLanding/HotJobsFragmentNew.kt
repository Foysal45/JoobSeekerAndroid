package com.bdjobs.app.LoggedInUserLanding


import android.os.Bundle
import android.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.HotJobs
import com.bdjobs.app.API.ModelClasses.HotJobsData

import com.bdjobs.app.R
import com.bdjobs.app.Utilities.error
import kotlinx.android.synthetic.main.fragment_hot_jobs_fragment_new.*
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
class HotJobsFragmentNew : Fragment() {
    private var hotjobsAdapterNew: HotjobsAdapterNew? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hot_jobs_fragment_new, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        hotjobsAdapterNew = HotjobsAdapterNew(activity!!)
        loadHotJobsData()

    }

    private fun loadHotJobsData() {
        ApiServiceJobs.create().getHotJobs().enqueue(object : Callback<HotJobs> {
            override fun onFailure(call: Call<HotJobs>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<HotJobs>, response: Response<HotJobs>) {
                Log.d("hehe", response.body().toString())
                if (response.isSuccessful){
                    hotjobList_RV?.adapter = hotjobsAdapterNew
                    hotjobList_RV?.setHasFixedSize(true)
                    hotjobList_RV?.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                    Log.d("initPag", response.body()?.data?.size.toString())
                    hotjobList_RV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                    hotjobsAdapterNew?.removeAll()
                    hotjobsAdapterNew?.addAll(response.body()?.data as List<HotJobsData>)

                }

            }

        })

    }


}
