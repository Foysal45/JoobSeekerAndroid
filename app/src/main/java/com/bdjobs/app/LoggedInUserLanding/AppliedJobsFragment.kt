package com.bdjobs.app.LoggedInUserLanding


import android.os.Bundle
import android.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.AppliedJobsModel

import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import kotlinx.android.synthetic.main.fragment_applied_jobs.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AppliedJobsFragment : Fragment() {

    private lateinit var bdjobsUsersession: BdjobsUserSession
    private var appliedJobsAdapter: AppliedJobsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_applied_jobs, container, true)

    }

    private fun initializeViews() {
        appliedJobsAdapter = AppliedJobsAdapter(activity)
        appliedJobsRV!!.adapter = appliedJobsAdapter
        appliedJobsRV!!.setHasFixedSize(true)
        appliedJobsRV?.layoutManager = LinearLayoutManager(activity, LinearLayout.HORIZONTAL, false)
        Log.d("initPag", "called")
        appliedJobsRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

    }

    override fun onResume() {
        super.onResume()
        bdjobsUsersession = BdjobsUserSession(activity)
        initializeViews()
        loadFirstPage()
    }

    private fun loadFirstPage() {
        ApiServiceMyBdjobs.create().getAppliedJobs(
                userId = bdjobsUsersession.userId,
                decodeId = bdjobsUsersession.decodId,
                isActivityDate = "0",
                pageNumber = "5"
        ).enqueue(object : Callback<AppliedJobsModel> {
            override fun onFailure(call: Call<AppliedJobsModel>, t: Throwable) {
                toast("${t.message}")
            }

            override fun onResponse(call: Call<AppliedJobsModel>, response: Response<AppliedJobsModel>) {
                Log.d("mmm", response.body().toString())
            }

        })
    }


}
