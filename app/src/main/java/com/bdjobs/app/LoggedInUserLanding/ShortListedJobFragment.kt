package com.bdjobs.app.LoggedInUserLanding

import android.app.Fragment
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.JobListModel
import com.bdjobs.app.API.ModelClasses.JobListModelData
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Jobs.JoblistAdapter
import com.bdjobs.app.Jobs.PaginationScrollListener
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import kotlinx.android.synthetic.main.fragment_shortlisted_job_layout.*
import org.jetbrains.anko.selector
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShortListedJobFragment : Fragment() {
    lateinit var bdjobsDB: BdjobsDB
    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var joblistAdapter: JoblistAdapter
    lateinit var homeCommunicator: HomeCommunicator
    private var currentPage = 1
    private var TOTAL_PAGES: Int? = null
    private var isLoadings = false
    private var isLastPages = false
    var totalRecordsFound = 0
    private var layoutManager: RecyclerView.LayoutManager? = null
    var favListSize = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_shortlisted_job_layout, container, false)!!
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsDB = BdjobsDB.getInstance(activity)
        bdjobsUserSession = BdjobsUserSession(activity)
        homeCommunicator = activity as HomeCommunicator
        profilePicIMGV?.loadCircularImageFromUrl(bdjobsUserSession.userPicUrl)
        searchIMGV?.setOnClickListener {
            homeCommunicator.gotoJobSearch()
        }
        profilePicIMGV?.setOnClickListener {
            homeCommunicator.gotoEditresume()
        }

        filterTV?.setOnClickListener {
            val deadline = arrayOf("Today", "Tomorrow", "Next 2 days", "Next 3 days", "Next 4 days")
            selector("Jobs expire in", deadline.toList()) { dialogInterface, i ->
                showShortListFIlterList(deadline[i])
            }
        }
        crossBTN?.setOnClickListener {
            showShortListFIlterList("")
        }
    }

    override fun onResume() {
        super.onResume()
        val shortListFilter = homeCommunicator.getShortListFilter()
        showShortListFIlterList(shortListFilter)
    }

    private fun showShortListFIlterList(shortListFilter: String) {
        filterTV.text = shortListFilter
        homeCommunicator.setShortListFilter(shortListFilter)
        when (shortListFilter) {
            "" -> {
                crossBTN.hide()
                getShortListedJobsByDeadline("")
            }
            "Today" -> {
                crossBTN.show()
                getShortListedJobsByDeadline("1")
            }
            "Tomorrow" -> {
                crossBTN.show()
                getShortListedJobsByDeadline("2")
            }
            "Next 2 days" -> {
                crossBTN.show()
                getShortListedJobsByDeadline("3")
            }
            "Next 3 days" -> {
                crossBTN.show()
                getShortListedJobsByDeadline("4")
            }
            "Next 4 days" -> {
                crossBTN.show()
                getShortListedJobsByDeadline("5")
            }
        }
    }

    private fun getShortListedJobsByDeadline(deadline: String) {
        val styledText = "<b><font color='#13A10E'>0</font></b> Shortlisted job"
        jobCountTV?.text = Html.fromHtml(styledText)
        currentPage = 1
        TOTAL_PAGES = null
        isLoadings = false
        isLastPages = false
        shortListRV?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        shortListRV?.layoutManager = layoutManager
        joblistAdapter = JoblistAdapter(activity)
        shortListRV?.adapter = joblistAdapter

        shortListRV?.addOnScrollListener(object : PaginationScrollListener(layoutManager!! as LinearLayoutManager) {

            override val totalPageCount: Int
                get() = TOTAL_PAGES!!
            override val isLastPage: Boolean
                get() = isLastPages
            override var isLoading: Boolean = false
                get() = isLoadings

            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1

                loadNextPage(
                        deadline = deadline,
                        pageNumber = currentPage,
                        rpp = "10"
                )
            }
        })

        loadFisrtPageTest(
                deadline = deadline,
                pageNumber = currentPage,
                rpp = "10"
        )

    }

    private fun loadFisrtPageTest(deadline: String, rpp: String, pageNumber: Int) {
        noDataLL?.hide()
        shortListRV.hide()
        shimmer_view_container_JobList?.show()
        shimmer_view_container_JobList?.startShimmerAnimation()

        val call = ApiServiceJobs.create().getStoreJobList(
                p_id = bdjobsUserSession.userId,
                encoded = Constants.ENCODED_JOBS,
                deadline = deadline,
                rpp = rpp,
                pg = pageNumber
        )
        call.enqueue(object : Callback<JobListModel> {

            override fun onResponse(call: Call<JobListModel>?, response: Response<JobListModel>) {

                try {
                    if (response.isSuccessful) {
                        shimmer_view_container_JobList?.hide()
                        shimmer_view_container_JobList?.stopShimmerAnimation()

                        val jobResponse = response.body()
                        val totalJobs = jobResponse?.common?.totalRecordsFound

                        if (totalJobs!! > 0) {
                            noDataLL?.hide()
                            shortListRV?.show()
                            Log.d("totalJobs", "data ase")
                        } else {
                            noDataLL?.show()
                            shortListRV?.hide()
                            Log.d("totalJobs", "zero")
                        }

                        TOTAL_PAGES = jobResponse?.common?.totalpages


                        Log.d("dkgjn", " Total page " + jobResponse?.common?.totalpages)
                        Log.d("dkgjn", " totalRecordsFound " + jobResponse?.common?.totalRecordsFound)

                        //communicator.totalJobCount(jobResponse?.common?.totalRecordsFound)
                        val results = response.body()?.data

                        if (!results.isNullOrEmpty()) {
                            joblistAdapter.addAllTest(results)
                        }

                        if (currentPage == TOTAL_PAGES!!) {
                            isLastPages = true
                        } else {
                            joblistAdapter.addLoadingFooter()
                        }


                        if (totalJobs> 1) {
                            val styledText = "<b><font color='#13A10E'>$totalJobs</font></b> Shortlisted jobs"
                            jobCountTV?.text = Html.fromHtml(styledText)
                        } else {
                            val styledText = "<b><font color='#13A10E'>$totalJobs</font></b> Shortlisted job"
                            jobCountTV?.text = Html.fromHtml(styledText)
                        }


                        totalRecordsFound = jobResponse.common?.totalRecordsFound
                        favListSize = totalRecordsFound
                    } else {
                        /*Log.d("TAG", "not successful: $TAG")*/
                    }
                } catch (e: Exception) {
                    logException(e)
                }

            }

            override fun onFailure(call: Call<JobListModel>?, t: Throwable) {
                Log.d("TAG", "not successful!! onFail")
                error("onFailure", t)
            }
        })
    }

    private fun loadNextPage(deadline: String, rpp: String, pageNumber: Int) {
        Log.d("ArrayTest", " loadNextPage called")

        val call = ApiServiceJobs.create().getStoreJobList(
                p_id = bdjobsUserSession.userId,
                encoded = Constants.ENCODED_JOBS,
                deadline = deadline,
                rpp = rpp,
                pg = pageNumber
        )
        call.enqueue(object : Callback<JobListModel> {

            override fun onResponse(call: Call<JobListModel>?, response: Response<JobListModel>) {

                try {
                    Log.d("Paramtest", "response :   ${response.body().toString()}")
                    if (response.isSuccessful) {

                        try {
                            val resp_jobs = response.body()
                            TOTAL_PAGES = resp_jobs?.common?.totalpages
                            joblistAdapter.removeLoadingFooter()
                            isLoadings = false

                            val results = response.body()?.data
                            joblistAdapter.addAllTest(results as List<JobListModelData>)

                            if (currentPage == TOTAL_PAGES) {
                                isLastPages = true
                            } else {
                                joblistAdapter.addLoadingFooter()
                            }

                            /*communicator.setIsLoading(isLoadings)
                            communicator.setLastPasge(isLastPages)
                            communicator.setTotalJob(resp_jobs?.common!!.totalRecordsFound!!.toInt())*/



                            totalRecordsFound = resp_jobs?.common?.totalRecordsFound!!
                            favListSize = totalRecordsFound

                            if (totalRecordsFound.toInt() > 1) {
                                val styledText = "<b><font color='#13A10E'>$totalRecordsFound</font></b> Shortlisted jobs"
                                jobCountTV?.text = Html.fromHtml(styledText)
                            } else {
                                val styledText = "<b><font color='#13A10E'>$totalRecordsFound</font></b> Shortlisted job"
                                jobCountTV?.text = Html.fromHtml(styledText)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.d("TAG", "not successful: ")
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onFailure(call: Call<JobListModel>?, t: Throwable?) {
                Log.d("TAG", "not successful!! onFail")
            }
        })
    }


    fun scrollToUndoPosition(position: Int) {
        shortListRV?.scrollToPosition(position)
        favListSize++
        if (favListSize > 1) {
            val styledText = "<b><font color='#13A10E'>$favListSize</font></b> Shortlisted jobs"
            jobCountTV?.text = Html.fromHtml(styledText)
        } else {
            val styledText = "<b><font color='#13A10E'>$favListSize</font></b> Shortlisted job"
            jobCountTV?.text = Html.fromHtml(styledText)
        }
    }

    fun decrementCounter() {
        favListSize--
        if (favListSize > 1) {
            val styledText = "<b><font color='#13A10E'>$favListSize</font></b> Shortlisted jobs"
            jobCountTV?.text = Html.fromHtml(styledText)
        } else {
            val styledText = "<b><font color='#13A10E'>$favListSize</font></b> Shortlisted job"
            jobCountTV?.text = Html.fromHtml(styledText)
        }
    }
}