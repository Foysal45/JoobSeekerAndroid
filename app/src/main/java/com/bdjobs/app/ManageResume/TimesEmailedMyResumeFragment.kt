package com.bdjobs.app.ManageResume


import android.os.Bundle
import android.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.TimesEmailed
import com.bdjobs.app.API.ModelClasses.TimesEmailedData
import com.bdjobs.app.Jobs.PaginationScrollListener

import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_times_emailed_my_resume.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class TimesEmailedMyResumeFragment : Fragment() {

    private val PAGE_START = 1
    private var TOTAL_PAGES: Int? = null
    private var pgNo: Int = PAGE_START
    private var isLastPages = false
    private var isLoadings = false
    private var layoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null
    private var bdjobsUserSession : BdjobsUserSession? = null

    private lateinit var manageCommunicator: ManageResumeCommunicator
    private lateinit var timesEmailedMyResumeAdapter: TimesEmailedMyResumeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_times_emailed_my_resume, container, false)
    }

    override fun onResume() {
        super.onResume()
        manageCommunicator = activity as ManageResumeCommunicator
        bdjobsUserSession = BdjobsUserSession(activity!!)
        backIMV.setOnClickListener {
           // manageCommunicator.backButtonClicked()
        }
        initPagination()
    }

    private fun initPagination() {
        timesEmailedMyResumeAdapter = TimesEmailedMyResumeAdapter(activity!!)
        emailedResumeRV!!.setHasFixedSize(true)
        emailedResumeRV!!.adapter = timesEmailedMyResumeAdapter
        layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        emailedResumeRV!!.layoutManager = layoutManager as RecyclerView.LayoutManager?
        Log.d("initPag", "called")
        emailedResumeRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

        emailedResumeRV?.addOnScrollListener(object : PaginationScrollListener(layoutManager!!) {
            override val isLoading: Boolean
                get() = isLoadings
            override val totalPageCount: Int
                get() = TOTAL_PAGES!!
            override val isLastPage: Boolean
                get() = isLastPages

            override fun loadMoreItems() {
                isLoadings = true
                pgNo++
                loadNextPage()
            }
        })
        loadFirstPage()

    }

    private fun loadFirstPage() {
        emailedResumeRV?.hide()
        numberTV?.hide()
        shimmer_view_container_emailedResumeList?.show()
        shimmer_view_container_emailedResumeList?.startShimmerAnimation()
        ApiServiceMyBdjobs.create().emailedMyResume(
                userID = bdjobsUserSession?.userId,
                decodeID = bdjobsUserSession?.decodId,
                pageNumber = pgNo.toString(),
                itemsPerPage = "20",
                isActivityDate = "0"
        ).enqueue(object : Callback<TimesEmailed>{
            override fun onFailure(call: Call<TimesEmailed>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<TimesEmailed>, response: Response<TimesEmailed>) {
                try {
                    TOTAL_PAGES = response?.body()?.common?.totalNumberOfPage?.toInt()
                    var totalEmailRecords = response?.body()?.common?.totalNumberOfEmail
                    if (!response?.body()?.data.isNullOrEmpty()) {
                        emailedResumeRV!!.visibility = View.VISIBLE
                        timesEmailedMyResumeAdapter?.removeAll()
                        timesEmailedMyResumeAdapter?.addAll((response?.body()?.data as List<TimesEmailedData>?)!!)

                        if (pgNo <= TOTAL_PAGES!! && TOTAL_PAGES!! > 1) {
                            Log.d("loadif", "$TOTAL_PAGES and $pgNo ")
                            timesEmailedMyResumeAdapter?.addLoadingFooter()
                        } else {
                            Log.d("loadelse", "$TOTAL_PAGES and $pgNo ")
                            isLastPages = true
                        }

                    }

                    emailedResumeRV?.show()
                    numberTV?.show()
                    shimmer_view_container_emailedResumeList?.hide()
                    shimmer_view_container_emailedResumeList?.stopShimmerAnimation()
                }
                catch (e : Exception){

                }
                }

        })
    }

    private fun loadNextPage() {
        ApiServiceMyBdjobs.create().emailedMyResume(
                userID = bdjobsUserSession?.userId,
                decodeID = bdjobsUserSession?.decodId,
                pageNumber = pgNo.toString(),
                itemsPerPage = "20",
                isActivityDate = "0"
        ).enqueue(object : Callback<TimesEmailed>{
            override fun onFailure(call: Call<TimesEmailed>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<TimesEmailed>, response: Response<TimesEmailed>) {
                try {

                    TOTAL_PAGES = response.body()?.common?.totalNumberOfPage?.toInt()
                    timesEmailedMyResumeAdapter?.removeLoadingFooter()
                    isLoadings = false

                    timesEmailedMyResumeAdapter?.addAll((response?.body()?.data as List<TimesEmailedData>?)!!)


                    if (pgNo != TOTAL_PAGES)
                        timesEmailedMyResumeAdapter?.addLoadingFooter()
                    else {
                        isLastPages = true
                    }


                }
                catch (e : Exception){

                }
            }

        })

    }


}
