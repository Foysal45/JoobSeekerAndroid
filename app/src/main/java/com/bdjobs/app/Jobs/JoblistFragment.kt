package com.bdjobs.app.Jobs

import android.app.Fragment
import android.os.Bundle
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
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_joblist_layout.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoblistFragment : Fragment() {

    private lateinit var session: BdjobsUserSession
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var joblistAdapter: JoblistAdapter? = null


    private var currentPage = 1
    private var TOTAL_PAGES: Int? = null
    private var isLoadings = false
    private var isLastPages = false
    private lateinit var communicator: JobCommunicator
    private var keyword = ""
    private var location = ""
    private var category = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_joblist_layout, container, false)!!

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)

    }


    private fun getData() {


        keyword = communicator.getKeyword()
        location = communicator.getLocation()
        category = communicator.getCategory()

        suggestiveSearchET.text = keyword

        joblistAdapter!!.clear()

        /*  loadFirstPage("", "", "", category, "", "02041526JSBJ2", "", "", "", "", "", "", "", keyword, "", location, "", "", 1, "", "", "", "", "")
  */


        loadFisrtPageTest("", "", "", category, "", "02041526JSBJ2", "", "", "", "", "", "", "", keyword, "", location, "", "", 1, "", "", "", "", "")


    }


    override fun onResume() {
        super.onResume()
        currentPage = 1
        if (session?.isLoggedIn!!) {
            HomeIMGV.show()
        } else {
            HomeIMGV.hide()
        }


        jobListRecyclerView?.setHasFixedSize(true)
        communicator = activity as JobCommunicator

        layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        jobListRecyclerView?.layoutManager = layoutManager
        joblistAdapter = JoblistAdapter(activity)
        jobListRecyclerView?.adapter = joblistAdapter

        onClick()
        getData()




        jobListRecyclerView!!.addOnScrollListener(object : PaginationScrollListener(layoutManager!! as LinearLayoutManager) {

            override val totalPageCount: Int
                get() = TOTAL_PAGES!!
            override val isLastPage: Boolean
                get() = isLastPages
            override var isLoading: Boolean = false
                get() = isLoadings

            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1

                communicator.setpageNumber(currentPage)


                loadNextPage("", "", "", category, "", "02041526JSBJ2", "", "", "", "", "", "", "", keyword, "", location, "", "", currentPage, "", "", "", "", "")
            }


        })

    }

    private fun loadFisrtPageTest(newsPaper: String, armyp: String, blueColur: String, category: String, deadline: String, encoded: String, experince: String, gender: String, genderB: String, industry: String, isFirstRequest: String, jobnature: String, jobType: String, keyword: String, lastJPD: String, location: String, organization: String, pageId: String, pageNumber: Int, postedWithIn: String, age: String, rpp: String, slno: String, version: String) {


        Log.d("Paramtest", "First page Page Number $currentPage  category: $category")

        jobListRecyclerView.hide()
        filterLayout.hide()
        shimmer_view_container_JobList.show()
        shimmer_view_container_JobList.startShimmerAnimation()

        val call = ApiServiceJobs.create().getJobList(newsPaper, armyp, blueColur, category, deadline, encoded, experince, gender, genderB, industry, isFirstRequest, jobnature, jobType, keyword, lastJPD, location, organization, pageId, pageNumber, postedWithIn, age, rpp, slno, version)
        call?.enqueue(object : Callback<JobListModel> {

            override fun onResponse(call: Call<JobListModel>?, response: Response<JobListModel>) {

                if (response.isSuccessful) {
                    jobListRecyclerView.show()
                    filterLayout.show()
                    shimmer_view_container_JobList.hide()
                    shimmer_view_container_JobList.stopShimmerAnimation()

                    val jobResponse = response.body()

                    TOTAL_PAGES = jobResponse?.common?.totalpages


                    Log.d("dkgjn", " Total page " + jobResponse?.common?.totalpages)
                    Log.d("dkgjn", " totalRecordsFound " + jobResponse?.common?.totalRecordsFound)

                    communicator.totalJobCount(jobResponse?.common?.totalRecordsFound)
                    val results = response.body()?.data

                    if (!results.isNullOrEmpty()) {

                        joblistAdapter?.addAllTest(results)

                    }




                    if (currentPage == TOTAL_PAGES!!) {

                        isLastPages = true

                    } else {


                        joblistAdapter?.addLoadingFooter()

                    }


                    val totalJobs = jobResponse!!.common!!.totalRecordsFound

                    jobCounterTV.text = "$totalJobs Jobs"

                    communicator.totalJobCount(jobResponse!!.common!!.totalRecordsFound!!)
                    communicator.setIsLoading(isLoadings)
                    communicator.setLastPasge(isLastPages)

                } else {
                    /*Log.d("TAG", "not successful: $TAG")*/
                }

            }

            override fun onFailure(call: Call<JobListModel>?, t: Throwable?) {
                toast("On Failure")
                Log.d("TAG", "not successful!! onFail")

            }
        })
    }

    private fun loadNextPage(newsPaper: String, armyp: String, blueColur: String, category: String, deadline: String, encoded: String, experince: String, gender: String, genderB: String, industry: String, isFirstRequest: String, jobnature: String, jobType: String, keyword: String, lastJPD: String, location: String, organization: String, pageId: String, pageNumber: Int, postedWithIn: String, age: String, rpp: String, slno: String, version: String) {
        Log.d("ArrayTest", " loadNextPage called")

        Log.d("Paramtest", "Next page Page Number $currentPage  category: $category")


        val call = ApiServiceJobs.create().getJobList(newsPaper, armyp, blueColur, category, deadline, encoded, experince, gender, genderB, industry, isFirstRequest, jobnature, jobType, keyword, lastJPD, location, organization, pageId, pageNumber, postedWithIn, age, rpp, slno, version)
        call?.enqueue(object : Callback<JobListModel> {

            override fun onResponse(call: Call<JobListModel>?, response: Response<JobListModel>) {

                Log.d("Paramtest", "response :   ${response.body().toString()}")
                if (response.isSuccessful) {

                    try {
                        val resp_jobs = response.body()
                        TOTAL_PAGES = resp_jobs?.common?.totalpages
                        joblistAdapter?.removeLoadingFooter()
                        isLoadings = false

                        val results = response.body()?.data
                        joblistAdapter?.addAllTest(results as List<JobListModelData>)

                        if (currentPage == TOTAL_PAGES) {
                            isLastPages = true
                        } else {


                            joblistAdapter?.addLoadingFooter()


                        }


                        communicator.setIsLoading(isLoadings)
                        communicator.totalJobCount(resp_jobs!!.common!!.totalRecordsFound!!)
                        communicator.setLastPasge(isLastPages)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Log.d("TAG", "not successful: ")


                }

            }

            override fun onFailure(call: Call<JobListModel>?, t: Throwable?) {

                Log.d("TAG", "not successful!! onFail")

            }
        })
    }


    private fun onClick() {
        backIV.setOnClickListener {
            communicator.backButtonPressesd()
        }
    }


}