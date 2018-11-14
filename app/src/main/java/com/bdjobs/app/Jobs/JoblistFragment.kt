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
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_jobdetail_layout.*
import kotlinx.android.synthetic.main.fragment_joblist_layout.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoblistFragment : Fragment() {

    private lateinit var session: BdjobsUserSession
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var joblistAdapter: JoblistAdapter? = null
    private val PAGE_START = 1
    private var currentPage = PAGE_START
    private var TOTAL_PAGES: Int? = null
    private var isLoadings = false
    private var isLastPages = false

    private lateinit var communicator: JobCommunicator


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_joblist_layout, container, false)!!

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)

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

        loadFirstPage("", "", "", "", "", "02041526JSBJ2", "", "", "", "", "", "", "", "", "", "", "", "", 1, "", "", "", "", "")


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

                loadNextPage("", "", "", "", "", "02041526JSBJ2", "", "", "", "", "", "", "", "", "", "", "", "", currentPage, "", "", "", "", "")

            }


        })


    }


    private fun fetchResults(response: Response<GetResponseJobLIst>): List<DataItem?>? {
        val topRatedMovies = response.body() as GetResponseJobLIst
        return topRatedMovies.data
    }


    private fun loadFirstPage(newsPaper: String, armyp: String, blueColur: String, category: String, deadline: String, encoded: String, experince: String, gender: String, genderB: String, industry: String, isFirstRequest: String, jobnature: String, jobType: String, keyword: String, lastJPD: String, location: String, organization: String, pageId: String, pageNumber: Int, postedWithIn: String, age: String, rpp: String, slno: String, version: String) {
        jobListRecyclerView.hide()
        shimmer_view_container_JobList.show()
        shimmer_view_container_JobList.startShimmerAnimation()

        val call = ApiServiceJobs.create().getJobList(newsPaper, armyp, blueColur, category, deadline, encoded, experince, gender, genderB, industry, isFirstRequest, jobnature, jobType, keyword, lastJPD, location, organization, pageId, pageNumber, postedWithIn, age, rpp, slno, version)
        call?.enqueue(object : Callback<GetResponseJobLIst> {
            override fun onResponse(call: Call<GetResponseJobLIst>, response: Response<GetResponseJobLIst>) {

                if (response.isSuccessful) {

                    jobListRecyclerView.show()
                    shimmer_view_container_JobList.hide()
                    shimmer_view_container_JobList.stopShimmerAnimation()


                    val resp_jobs = response.body()

                    val c_name: String = response.body()?.data?.get(1)?.companyName.toString()

                    TOTAL_PAGES = resp_jobs?.common?.totalpages
                    communicator.totalJobCount(resp_jobs?.common?.totalpages)

                    Log.d("TAG", "page count...: $currentPage")
                    Log.d("TAG", "TOTAL_PAGES...: $TOTAL_PAGES")

                    /* progressBar.visibility = View.GONE*/
                    //Log.d("resp1st", "data element: ${results?.get(3)?.companyName}")
                    val results = response.body()?.data


                    joblistAdapter?.addAll(results as List<DataItem>)
                    if (currentPage <= TOTAL_PAGES!!)
                    /* joblistAdapter?.addLoadingFooter()*/
                    else
                        isLastPages = true
                    val handler = Handler()
                    handler.postDelayed({
                        joblistAdapter?.addLoadingFooter()
                    }, 100)


                    communicator.setTotalPage(TOTAL_PAGES!!)
                    communicator.setIsLoading(isLoadings)
                    communicator.setLastPasge(isLastPages)

                } else {
                    /*Log.d("TAG", "not successful: $TAG")*/
                }
            }

            override fun onFailure(call: Call<GetResponseJobLIst>, t: Throwable) {
                t.printStackTrace()

                Log.d("ApiTest", "On failure called : ${t.message}")


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
                        /* Log.d("TAG", "company name: $c_name")*/

                        joblistAdapter?.removeLoadingFooter()
                        isLoadings = false

                        val results = fetchResults(response)
                        joblistAdapter?.addAll(results as List<DataItem>)

                        if (currentPage != TOTAL_PAGES)
                            joblistAdapter?.addLoadingFooter()
                        else {
                            isLastPages = true

                        }


                        communicator.setIsLoading(isLoadings)
                        communicator.setTotalPage(TOTAL_PAGES!!)
                        communicator.setLastPasge(isLastPages)

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

    private fun onClick(){


       backIV.setOnClickListener {

           toast("Back Pressed")
           activity.finish()

        }
    }


}