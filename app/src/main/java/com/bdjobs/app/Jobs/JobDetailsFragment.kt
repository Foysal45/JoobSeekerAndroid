package com.bdjobs.app.Jobs

import android.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_jobdetail_layout.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobDetailsFragment : Fragment() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var jobDetailNewAdapter: JobDetailNewAdapter? = null
    private var currentPage = 1
    private lateinit var communicator: JobCommunicator
    private var snapHelper: SnapHelper? = null
    private var jobListGet: MutableList<DataItem>? = null
    private var TOTAL_PAGES: Int? = null
    private var isLoadings = false
    private var isLastPages = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_jobdetail_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        communicator = activity as JobCommunicator

        jobListGet = communicator.getJobList()!!
        val lastPosition = jobListGet?.size!! - 1
        jobListGet!!.removeAt(lastPosition)

        currentPage = communicator.getCurrentPageNumber()
        TOTAL_PAGES = communicator.getTotalPage()
        isLastPages = communicator.getLastPasge()

        snapHelper = PagerSnapHelper()
        (snapHelper as PagerSnapHelper).attachToRecyclerView(jobDetailRecyclerView)
        jobDetailRecyclerView.setHasFixedSize(true)

        layoutManager = LinearLayoutManager(activity, LinearLayout.HORIZONTAL, false)
        jobDetailRecyclerView?.layoutManager = layoutManager
        jobDetailNewAdapter = JobDetailNewAdapter(activity!!)
        jobDetailRecyclerView?.adapter = jobDetailNewAdapter


        loadFirstPage()

        Handler().postDelayed({ jobDetailRecyclerView?.scrollToPosition(communicator.getItemClickPosition()) }, 200)

        jobDetailRecyclerView!!.addOnScrollListener(object : PaginationScrollListener(layoutManager!! as LinearLayoutManager) {

            override val totalPageCount: Int
                get() = TOTAL_PAGES!!
            override val isLastPage: Boolean
                get() = isLastPages
            override var isLoading: Boolean = false
                get() = isLoadings

            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1

                Log.d("loadMoreItemsgfjfg", " Called ")
                loadNextPage("", "", "", "", "", "02041526JSBJ2", "", "", "", "", "", "", "", "", "", "", "", "", currentPage, "", "", "", "", "")

            }


        })


    }


    private fun loadNextPage(newsPaper: String, armyp: String, blueColur: String, category: String, deadline: String, encoded: String, experince: String, gender: String, genderB: String, industry: String, isFirstRequest: String, jobnature: String, jobType: String, keyword: String, lastJPD: String, location: String, organization: String, pageId: String, pageNumber: Int, postedWithIn: String, age: String, rpp: String, slno: String, version: String) {
        Log.d("ArrayTest", " loadNextPage called")
        val call = ApiServiceJobs.create().getJobList(newsPaper, armyp, blueColur, category, deadline, encoded, experince, gender, genderB, industry, isFirstRequest, jobnature, jobType, keyword, lastJPD, location, organization, pageId, pageNumber, postedWithIn, age, rpp, slno, version)
        call?.enqueue(object : Callback<GetResponseJobLIst> {

            override fun onResponse(call: Call<GetResponseJobLIst>?, response: Response<GetResponseJobLIst>) {


                if (response.isSuccessful) {

                    try {
                        val c_name: String = response.body()?.data?.get(1)?.companyName.toString()

                        jobDetailNewAdapter?.removeLoadingFooter()
                        isLoadings = false

                        val results = fetchResults(response)
                        jobDetailNewAdapter?.addAll(results as List<DataItem>)

                        if (currentPage != TOTAL_PAGES)
                            jobDetailNewAdapter?.addLoadingFooter()
                        else {
                            isLastPages = true

                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Log.d("TAG", "not successful: ")


                }

            }

            override fun onFailure(call: Call<GetResponseJobLIst>?, t: Throwable?) {
                toast("On Failure")
                Log.d("TAG", "not successful!! onFail")

            }
        })
    }


    private fun fetchResults(response: Response<GetResponseJobLIst>): List<DataItem?>? {
        val topRatedMovies = response.body() as GetResponseJobLIst
        return topRatedMovies.data
    }


    private fun loadFirstPage() {


        jobDetailNewAdapter?.addAll(jobListGet as List<DataItem>)
        if (currentPage <= TOTAL_PAGES!!)
        /* jobDetailNewAdapter?.addLoadingFooter()*/
        else
            isLastPages = true
        val handler = Handler()
        handler.postDelayed({
            jobDetailNewAdapter?.addLoadingFooter()
        }, 100)
    }
}