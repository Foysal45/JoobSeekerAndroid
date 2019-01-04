package com.bdjobs.app.LoggedInUserLanding


import android.os.Bundle
import android.app.Fragment
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.AppliedJobsData
import com.bdjobs.app.API.ModelClasses.AppliedJobsModel
import com.bdjobs.app.Jobs.PaginationScrollListener

import com.bdjobs.app.R
import com.bdjobs.app.R.id.shimmer_view_container_JobList
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_applied_jobs.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AppliedJobsFragment : Fragment() {

    private lateinit var bdjobsUsersession: BdjobsUserSession
    private var appliedJobsAdapter: AppliedJobsAdapter? = null
    private val PAGE_START = 1
    private var TOTAL_PAGES: Int? = null
    private var pgNo: Int = PAGE_START
    private var isLastPages = false
    private var isLoadings = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_applied_jobs, container, false)

    }

    private fun initializeViews() {
        appliedJobsAdapter = AppliedJobsAdapter(activity)
        appliedJobsRV!!.adapter = appliedJobsAdapter
        appliedJobsRV!!.setHasFixedSize(true)
        appliedJobsRV?.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        Log.d("initPag", "called")
        appliedJobsRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        appliedJobsRV?.addOnScrollListener(object : PaginationScrollListener((appliedJobsRV.layoutManager as LinearLayoutManager?)!!) {
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

    override fun onResume() {
        super.onResume()
        bdjobsUsersession = BdjobsUserSession(activity)
        initializeViews()

    }

    private fun loadFirstPage() {

        appliedJobsRV.hide()
        favCountTV.hide()
        shimmer_view_container_appliedJobList.show()
        shimmer_view_container_appliedJobList.startShimmerAnimation()


        ApiServiceMyBdjobs.create().getAppliedJobs(
                userId = bdjobsUsersession.userId,
                decodeId = bdjobsUsersession.decodId,
                isActivityDate = "0",
                pageNumber = pgNo.toString(),
                itemsPerPage = "5"
        ).enqueue(object : Callback<AppliedJobsModel> {
            override fun onFailure(call: Call<AppliedJobsModel>, t: Throwable) {
                toast("${t.message}")
            }

            override fun onResponse(call: Call<AppliedJobsModel>, response: Response<AppliedJobsModel>) {


                Log.d("callAppliURl", "url: ${call?.request()} and ")
             //   TOTAL_PAGES = response.body()?.common?.totalpages?.toInt()
                TOTAL_PAGES = 5
                var totalRecords = 10
                        //response.body()?.common?.totalrecordsfound

                Log.d("callAppliURl", response.body()?.data.toString())

                if (!response?.body()?.data.isNullOrEmpty()) {
                    appliedJobsRV!!.visibility = View.VISIBLE
                    var value = response.body()?.data
                    appliedJobsAdapter?.removeAll()
                    appliedJobsAdapter?.addAll(value as List<AppliedJobsData>)

                    if (pgNo <= TOTAL_PAGES!! && TOTAL_PAGES!! > 1) {
                        Log.d("loadif", "$TOTAL_PAGES and $pgNo ")
                        appliedJobsAdapter?.addLoadingFooter()
                    } else {
                        Log.d("loadelse", "$TOTAL_PAGES and $pgNo ")
                        isLastPages = true
                    }

                }

                val styledText = "<b><font color='#13A10E'>${totalRecords}</font></b> Employers now offering Jobs"
                favCountTV.text = Html.fromHtml(styledText)

                appliedJobsRV.show()
                favCountTV.show()
                shimmer_view_container_appliedJobList.hide()
                shimmer_view_container_appliedJobList.stopShimmerAnimation()
            }

        })
    }
    private fun loadNextPage() {
        ApiServiceMyBdjobs.create().getAppliedJobs(
                userId = bdjobsUsersession.userId,
                decodeId = bdjobsUsersession.decodId,
                isActivityDate = "0",
                pageNumber = pgNo.toString(),
                itemsPerPage = "5"
        ).enqueue(object : Callback<AppliedJobsModel> {
            override fun onFailure(call: Call<AppliedJobsModel>, t: Throwable) {
                toast("${t.message}")
            }

            override fun onResponse(call: Call<AppliedJobsModel>, response: Response<AppliedJobsModel>) {

                Log.d("callAppliURl", "url: ${call?.request()} and $pgNo")
                Log.d("callAppliURl", response.body()?.data.toString())
                TOTAL_PAGES = TOTAL_PAGES?.plus(1)

                        //response.body()?.common?.totalpages?.toInt()
                appliedJobsAdapter?.removeLoadingFooter()
                isLoadings = false

                appliedJobsAdapter?.addAll((response?.body()?.data as List<AppliedJobsData>?)!!)


                if (pgNo != TOTAL_PAGES)
                    appliedJobsAdapter?.addLoadingFooter()
                else {
                    isLastPages = true
                }


            }

        })
    }


}
