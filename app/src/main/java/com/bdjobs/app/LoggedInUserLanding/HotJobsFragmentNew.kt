package com.bdjobs.app.LoggedInUserLanding


import android.app.Fragment
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.HotJobs
import com.bdjobs.app.API.ModelClasses.HotJobsData
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import kotlinx.android.synthetic.main.fragment_hot_jobs_fragment_new.*
import kotlinx.android.synthetic.main.fragment_hot_jobs_fragment_new.notificationIMGV
import kotlinx.android.synthetic.main.fragment_hot_jobs_fragment_new.profilePicIMGV
import kotlinx.android.synthetic.main.fragment_hot_jobs_fragment_new.searchIMGV
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HotJobsFragmentNew : Fragment() {

    private lateinit var bdjobsUserSession: BdjobsUserSession
    private var hotjobsAdapterNew: HotjobsAdapterNew? = null
    lateinit var homeCommunicator: HomeCommunicator
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hot_jobs_fragment_new, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        homeCommunicator = activity as HomeCommunicator
        hotjobsAdapterNew = HotjobsAdapterNew(activity!!)
        bdjobsUserSession = BdjobsUserSession(activity)
        loadHotJobsData()
        onclick()
    }

    fun updateNotificationView(count: Int?) {
        Log.d("rakib", "in hot jobs fragment $count")
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

    private fun onclick() {
        searchIMGV.setOnClickListener {
            homeCommunicator.gotoJobSearch()
        }

        profilePicIMGV.setOnClickListener {
            homeCommunicator.gotoEditresume()
        }

        notificationIMGV?.setOnClickListener {
            homeCommunicator.goToNotifications()
        }

        profilePicIMGV?.loadCircularImageFromUrl(BdjobsUserSession(activity).userPicUrl?.trim())
    }

    private fun loadHotJobsData() {
        hotjobList_RV?.hide()
        favCountTV?.hide()
        shimmer_view_container_hotJobList?.show()
        shimmer_view_container_hotJobList?.startShimmerAnimation()

        if (Constants.hotjobs.isNullOrEmpty()) {
            ApiServiceJobs.create().getHotJobs().enqueue(object : Callback<HotJobs> {
                override fun onFailure(call: Call<HotJobs>, t: Throwable) {
                    error("onFailure", t)
                }

                override fun onResponse(call: Call<HotJobs>, response: Response<HotJobs>) {
                    try {
                        Log.d("hehe", response.body().toString())
                        if (response.isSuccessful) {
                            hotjobList_RV?.adapter = hotjobsAdapterNew
                            hotjobList_RV?.setHasFixedSize(true)
                            Log.d("initPag", response.body()?.data?.size.toString())
                            hotjobList_RV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                            hotjobsAdapterNew?.removeAll()
                            hotjobsAdapterNew?.addAll(response.body()?.data as List<HotJobsData>)

                            Constants.hotjobs = response.body()?.data

                        }

                        if (response.body()?.data?.size?.toString()?.toInt()!! > 1) {
                            val styledText = "<b><font color='#13A10E'>${response.body()?.data?.size?.toString()}</font></b> Hot Jobs"
                            favCountTV?.text = Html.fromHtml(styledText)
                        } else {
                            val styledText = "<b><font color='#13A10E'>${response.body()?.data?.size?.toString()}</font></b> Hot Job"
                            favCountTV?.text = Html.fromHtml(styledText)
                        }

                        hotjobList_RV?.show()
                        favCountTV?.show()
                        shimmer_view_container_hotJobList?.hide()
                        shimmer_view_container_hotJobList?.stopShimmerAnimation()
                    } catch (e: Exception) {
                        logException(e)
                    }
                }
            })
        } else {
            try {
                hotjobList_RV?.adapter = hotjobsAdapterNew
                hotjobList_RV?.setHasFixedSize(true)
                hotjobList_RV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                hotjobsAdapterNew?.removeAll()
                hotjobsAdapterNew?.addAll(Constants.hotjobs as List<HotJobsData>)

                if (Constants.hotjobs?.size!! > 1) {
                    val styledText = "<b><font color='#13A10E'>${Constants.hotjobs?.size}</font></b> Hot Jobs"
                    favCountTV?.text = Html.fromHtml(styledText)
                } else {
                    val styledText = "<b><font color='#13A10E'>${Constants.hotjobs?.size}</font></b> Hot Job"
                    favCountTV?.text = Html.fromHtml(styledText)
                }

                hotjobList_RV?.show()
                favCountTV?.show()
                shimmer_view_container_hotJobList?.hide()
                shimmer_view_container_hotJobList?.stopShimmerAnimation()
            } catch (e: Exception) {
                logException(e)
            }
        }
    }

    override fun onResume() {
        super.onResume()
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
}
