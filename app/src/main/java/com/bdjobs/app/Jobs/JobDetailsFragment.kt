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
import com.bdjobs.app.API.ModelClasses.JobListModel
import com.bdjobs.app.API.ModelClasses.JobListModelData
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.transitFragment
import kotlinx.android.synthetic.main.fragment_jobdetail_layout.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class JobDetailsFragment : Fragment() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var jobDetailAdapter: JobDetailAdapter? = null
    private var currentPage = 1
    private lateinit var communicator: JobCommunicator
    private var snapHelper: SnapHelper? = null
    private var jobListGet: MutableList<JobListModelData>? = null
    private var TOTAL_PAGES: Int? = null
    private var totalRecordsFound: Int? = null
    private var isLoadings = false
    private var isLastPages = false
    private var keyword = ""
    private var location = ""
    private var category = ""
    var currentJobPosition = 0
    var shareJobPosition = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_jobdetail_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        communicator = activity as JobCommunicator

        shareJobPosition = communicator.getItemClickPosition()
        getData()

        snapHelper = PagerSnapHelper()
        jobDetailRecyclerView.setOnFlingListener(null);
        (snapHelper as PagerSnapHelper).attachToRecyclerView(jobDetailRecyclerView)
        jobDetailRecyclerView.setHasFixedSize(true)

        layoutManager = LinearLayoutManager(activity, LinearLayout.HORIZONTAL, false)
        jobDetailRecyclerView?.layoutManager = layoutManager
        Log.d("PositionTest","snapHelper   ${snapHelper!!.getSnapPosition(jobDetailRecyclerView)}"  )
        jobDetailAdapter = JobDetailAdapter(activity!!)
        jobDetailRecyclerView?.adapter = jobDetailAdapter

        jobDetailRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    currentJobPosition = getCurrentItem()
                    Log.d("PositionTest","snapHelper   $currentJobPosition"  )

                    shareJobPosition = currentJobPosition

                    counterTV?.let {tv->
                        tv.text = "Job ${currentJobPosition+1}/$totalRecordsFound" }
                }
            }
        })


        onClick()
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

                Log.d("djggsgdjdg", "keyword $keyword  location $location  category $category  ")

                    loadNextPage("", "", "", category, "", "02041526JSBJ2", "", "", "", "", "", "", "", keyword, "", location, "", "", currentPage, "", "", "", "", "")
            }


        })






    }


    private fun getCurrentItem(): Int {
        return (jobDetailRecyclerView.getLayoutManager() as LinearLayoutManager)
                .findFirstVisibleItemPosition()
    }

    fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager ?: return RecyclerView.SCROLL_STATE_IDLE
        val snapView = findSnapView(layoutManager) ?: return RecyclerView.SCROLL_STATE_IDLE
        return layoutManager.getPosition(snapView)
    }




    private fun getData() {

        jobListGet = communicator.getJobList()!!


        Log.d("djggsgdjdg", "jobListGet ${jobListGet!!.size}")

        Log.d("djggsgdjdg", "clickedPosition ${communicator.getItemClickPosition()}")

        Log.d("djggsgdjdg", "clickedPosition data ${jobListGet!!.get(communicator.getItemClickPosition()).jobTitle}}")
        /* val lastPosition = jobListGet?.size!!-1
         jobListGet!!.removeAt(lastPosition)*/

        Log.d("djggsgdjdg", "clickedPosition data ${jobListGet!!.get(communicator.getItemClickPosition()).jobTitle}}")



        totalRecordsFound = communicator.getTotalJobCount()
        currentPage = communicator.getCurrentPageNumber()
        TOTAL_PAGES = communicator.getTotalPage()
        isLastPages = communicator.getLastPasge()

        keyword = communicator.getKeyword()
        location = communicator.getLocation()
        category = communicator.getCategory()

        Log.d("djggsgdjdg", "keyword $keyword  location $location  category $category  ")


    }

    private fun loadNextPage(newsPaper: String, armyp: String, blueColur: String, category: String, deadline: String, encoded: String, experince: String, gender: String, genderB: String, industry: String, isFirstRequest: String, jobnature: String, jobType: String, keyword: String, lastJPD: String, location: String, organization: String, pageId: String, pageNumber: Int, postedWithIn: String, age: String, rpp: String, slno: String, version: String) {
        Log.d("ArrayTestJobdetail", " loadNextPage called")


        val call = ApiServiceJobs.create().getJobList(newsPaper, armyp, blueColur, category, deadline, encoded, experince, gender, genderB, industry, isFirstRequest, jobnature, jobType, keyword, lastJPD, location, organization, pageId, pageNumber, postedWithIn, age, rpp, slno, version)
        call?.enqueue(object : Callback<JobListModel> {

            override fun onResponse(call: Call<JobListModel>?, response: Response<JobListModel>) {


                if (response.isSuccessful) {

                    Log.d("ArrayTestJobdetail", " response.isSuccessful")

                    val resp_jobs = response.body()

                    jobDetailAdapter?.removeLoadingFooter()
                    isLoadings = false

                    val results = response.body()?.data

                    if (!results.isNullOrEmpty()) {

                        jobDetailAdapter?.addAll(results)

                    }


                    if (currentPage == TOTAL_PAGES) {

                        isLastPages = true

                    } else {

                        jobDetailAdapter?.addLoadingFooter()
                    }


                } else {
                    Log.d("TAG", "not successful: ")


                }

            }

            override fun onFailure(call: Call<JobListModel>?, t: Throwable?) {
                toast("On Failure")
                Log.d("TAG", "not successful!! onFail")

            }
        })
    }


    private fun loadFirstPage() {

        jobDetailAdapter?.addAll(jobListGet as List<JobListModelData>)
        if (currentPage == TOTAL_PAGES!!) {

            isLastPages = true
        }

        counterTV?.let {tv->
            tv.text = "Job ${communicator.getItemClickPosition()+1}/$totalRecordsFound" }

    }


    private fun onClick() {

        BackIMGV.setOnClickListener {
          communicator.backButtonPressesd()
        }

        filterIMGV.setOnClickListener {
            jobDetailAdapter!!.shareJobs(shareJobPosition)
            Log.d("ShareJob","currentJobPosition $shareJobPosition")
        }


    }
}