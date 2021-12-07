package com.bdjobs.app.GuestUserLanding

import android.app.Activity
import android.os.Bundle
import android.text.Html
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.HotJobs
import com.bdjobs.app.API.ModelClasses.HotJobsData
import com.bdjobs.app.ads.Ads
import com.bdjobs.app.LoggedInUserLanding.HotjobsAdapterNew
import com.bdjobs.app.Login.LoginBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.utilities.*
import kotlinx.android.synthetic.main.activity_guest_user_job_search.*
import kotlinx.android.synthetic.main.activity_hot_jobs.*
//import kotlinx.android.synthetic.main.activity_hot_jobs.adView
import kotlinx.android.synthetic.main.activity_hot_jobs.favCountTV
import kotlinx.android.synthetic.main.activity_hot_jobs.hotjobList_RV
import kotlinx.android.synthetic.main.activity_hot_jobs.shimmer_view_container_hotJobList
import kotlinx.android.synthetic.main.activity_job_landing.*
import kotlinx.android.synthetic.main.fragment_hot_jobs_fragment_new.*
import org.jetbrains.anko.intentFor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HotJobsActivity : Activity() {

    private var hotjobsAdapterNew: HotjobsAdapterNew? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hot_jobs)

        hotjobsAdapterNew = HotjobsAdapterNew(this@HotJobsActivity)


        backIMV?.setOnClickListener {
            onBackPressed()
        }

        hot_jobs_profile_iv?.setOnClickListener {
            startActivity(intentFor<LoginBaseActivity>(Constants.key_go_to_home to true))
        }

        loadHotJobsData()

//        try {
//            adView?.hide()
//            val deviceInfo = getDeviceInformation()
//            val screenSize = deviceInfo[Constants.KEY_SCREEN_SIZE]
//
//            screenSize?.let{it->
//                if(it.toFloat()>5.0){
//                    val adRequest = AdRequest.Builder().build()
//                    adView?.loadAd(adRequest)
//                    adView?.show()
//                }
//            }
//        } catch (e: Exception) {
//            logException(e)
//        }
        Ads.loadAdaptiveBanner(this@HotJobsActivity,adView_container_hot_jobs)

    }

    private fun loadHotJobsData() {
        hotjobList_RV?.hide()
        favCountTV?.hide()
        shimmer_view_container_hotJobList?.show()
        shimmer_view_container_hotJobList?.startShimmer()

        if (Constants.hotjobs.isNullOrEmpty()) {
            ApiServiceJobs.create().getHotJobs().enqueue(object : Callback<HotJobs> {
                override fun onFailure(call: Call<HotJobs>, t: Throwable) {
                    error("onFailure", t)
                }

                override fun onResponse(call: Call<HotJobs>, response: Response<HotJobs>) {
                    try {
                        //Log.d("hehe", response.body().toString())
                        if (response.isSuccessful) {
                            hotjobList_RV?.adapter = hotjobsAdapterNew
                            hotjobList_RV?.setHasFixedSize(true)
                            //Log.d("initPag", response.body()?.data?.size.toString())
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
                        shimmer_view_container_hotJobList?.stopShimmer()
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
                shimmer_view_container_hotJobList?.stopShimmer()
            } catch (e: Exception) {
                logException(e)
            }
        }
    }

}
