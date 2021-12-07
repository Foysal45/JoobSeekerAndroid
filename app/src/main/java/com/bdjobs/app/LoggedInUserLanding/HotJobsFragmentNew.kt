package com.bdjobs.app.LoggedInUserLanding


import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.HotJobs
import com.bdjobs.app.API.ModelClasses.HotJobsData
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.utilities.*
import com.bdjobs.app.databases.internal.BdjobsDB
import kotlinx.android.synthetic.main.fragment_hot_jobs_fragment_new.*
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class HotJobsFragmentNew : Fragment() {

    private lateinit var bdjobsUserSession: BdjobsUserSession
    private lateinit var bdjobsDB: BdjobsDB
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
        hotjobsAdapterNew = HotjobsAdapterNew(requireContext())
        bdjobsUserSession = BdjobsUserSession(requireContext())
        bdjobsDB = BdjobsDB.getInstance(requireContext())
        loadHotJobsData()
        onclick()
    }

    fun updateNotificationView(count: Int?) {
        //Log.d("rakib", "in hot jobs fragment $count")
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
//        bdjobsUserSession = BdjobsUserSession(requireContext())
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

        messageIMGV?.setOnClickListener {
            homeCommunicator.goToMessages()
        }

        profilePicIMGV?.loadCircularImageFromUrl(BdjobsUserSession(requireContext()).userPicUrl?.trim())
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

    override fun onResume() {
        super.onResume()
        showNotificationCount()
        showMessageCount()
    }

    private fun showNotificationCount() {
        try {
            bdjobsUserSession = BdjobsUserSession(requireContext())
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

    fun updateMessageView(count: Int?) {
        //Log.d("rakib", "in home fragment $count")
        if (count!! > 0) {
            messageCountTV?.show()
            if (count <= 99)
                messageCountTV?.text = "$count"
            else
                messageCountTV?.text = "99+"
        } else {
            messageCountTV?.hide()
        }
    }

    private fun showMessageCount() {
        try {

            doAsync {
                bdjobsUserSession = BdjobsUserSession(requireContext())
                val count = bdjobsDB.notificationDao().getMessageCount()
                Timber.d("Messages count: $count")
                bdjobsUserSession.updateMessageCount(count)
            }

            if (bdjobsUserSession.messageCount!! <= 0) {
                messageCountTV?.hide()
            } else {
                messageCountTV?.show()
                if (bdjobsUserSession.messageCount!! > 99) {
                    messageCountTV?.text = "99+"

                } else {
                    messageCountTV?.text = "${bdjobsUserSession.messageCount!!}"

                }
            }
        } catch (e: Exception) {
        }
    }
}
