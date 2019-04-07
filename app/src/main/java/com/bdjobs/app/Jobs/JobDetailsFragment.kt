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
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_jobdetail_layout.*
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
    private var TOTAL_PAGES: Int? = 0
    private var totalRecordsFound: Int? = 0
    private var isLoadings = false
    private var isLastPages = false
    private var keyword :String?= ""
    private var location:String? = ""
    private var category:String? = ""
    private var newsPaper:String? = ""
    private var industry:String? = ""
    private var organization:String? = ""
    private var gender:String? = ""
    private var experience:String? = ""
    private var jobType:String? = ""
    private var jobLevel:String? = ""
    private var jobNature:String? = ""
    private var postedWithin:String? = ""
    private var deadline:String? = ""
    private var age:String? = ""
    private var army:String? = ""

    var currentJobPosition = 0
    var shareJobPosition = 0

    lateinit var bdjobsDB: BdjobsDB


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_jobdetail_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsDB = BdjobsDB.getInstance(activity)
        alertTV?.isSelected = true
        communicator = activity as JobCommunicator

        shareJobPosition = communicator.getItemClickPosition()
        communicator.setCurrentJobPosition(communicator.getItemClickPosition())
        getData()

        snapHelper = PagerSnapHelper()
        jobDetailRecyclerView.onFlingListener = null
        (snapHelper as PagerSnapHelper).attachToRecyclerView(jobDetailRecyclerView)
        jobDetailRecyclerView.setHasFixedSize(true)

        layoutManager = LinearLayoutManager(activity, LinearLayout.HORIZONTAL, false)
        jobDetailRecyclerView?.layoutManager = layoutManager
        Log.d("PositionTest", "snapHelper   ${snapHelper!!.getSnapPosition(jobDetailRecyclerView)}")
        jobDetailAdapter = JobDetailAdapter(activity!!)
        jobDetailRecyclerView?.adapter = jobDetailAdapter

        jobDetailRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    currentJobPosition = getCurrentItem()
                    Log.d("PositionTest", "snapHelper   $currentJobPosition")

                    shareJobPosition = currentJobPosition
                    communicator.setCurrentJobPosition(currentJobPosition)
                    jobDetailAdapter?.showHideShortListedIcon(currentJobPosition)
                    totalRecordsFound?.let { it ->
                        try {
                            if (it > 1) {
                                counterTV?.show()
                            } else {
                                counterTV?.hide()
                            }
                        } catch (e: Exception) {
                        }
                    }

                    counterTV?.let { tv ->
                        tv.text = "Job ${currentJobPosition + 1}/$totalRecordsFound"

                    }
                }
            }
        })


        onClick()
        loadFirstPage()
        communicator.setBackFrom("jobdetails")

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
                loadNextPage(
                        jobLevel = jobLevel,
                        newsPaper = newsPaper,
                        armyp = army,
                        blueColur = "",
                        category = category,
                        deadline = deadline,
                        encoded = Constants.ENCODED_JOBS,
                        experince = experience,
                        gender = gender,
                        genderB = "",
                        industry = industry,
                        isFirstRequest = "",
                        jobnature = jobNature,
                        jobType = jobType,
                        keyword = keyword,
                        lastJPD = "",
                        location = location,
                        organization = organization,
                        pageId = "",
                        pageNumber = currentPage,
                        postedWithIn = postedWithin,
                        age = age,
                        rpp = "",
                        slno = "",
                        version = ""
                )

                //loadNextPage("", "", "", category, "", "02041526JSBJ2", "", "", "", "", "", "", "", keyword, "", location, "", "", currentPage, "", "", "", "", "")
            }


        })

    }


    private fun getCurrentItem(): Int {
        return (jobDetailRecyclerView.layoutManager as LinearLayoutManager)
                .findFirstVisibleItemPosition()
    }

    fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager ?: return RecyclerView.SCROLL_STATE_IDLE
        val snapView = findSnapView(layoutManager) ?: return RecyclerView.SCROLL_STATE_IDLE
        return layoutManager.getPosition(snapView)
    }


    private fun getData() {

        try {
            jobListGet = communicator.getJobList()!!
        } catch (e: Exception) {
            logException(e)
        }


        try {
            Log.d("djggsgdjdg", "jobListGet ${jobListGet!!.size}")

            Log.d("djggsgdjdg", "clickedPosition ${communicator.getItemClickPosition()}")

            Log.d("djggsgdjdg", "getCurrentPageNumber: ${communicator.getCurrentPageNumber()}")

            Log.d("djggsgdjdg", "getTotalPage: ${communicator.getTotalPage()}")
        } catch (e: Exception) {
            logException(e)
        }



        totalRecordsFound = communicator.getTotalJobCount()
        currentPage = communicator.getCurrentPageNumber()
        TOTAL_PAGES = communicator.getTotalPage()
        isLastPages = communicator.getLastPasge()
        keyword = communicator.getKeyword()
        location = communicator.getLocation()
        category = communicator.getCategory()
        newsPaper = communicator.getNewsPaper()
        industry = communicator.getIndustry()
        organization = communicator.getOrganization()
        gender = communicator.getGender()
        experience = communicator.getExperience()
        jobType = communicator.getJobType()
        jobLevel = communicator.getJobLevel()
        jobNature = communicator.getJobNature()
        postedWithin = communicator.getPostedWithin()
        deadline = communicator.getDeadline()

        age = communicator.getAge()
        army = communicator.getArmy()


    }

    private fun loadNextPage(jobLevel: String?, newsPaper: String?, armyp: String?, blueColur: String?, category: String?, deadline: String?, encoded: String?, experince: String?, gender: String?, genderB: String?, industry: String?, isFirstRequest: String?, jobnature: String?, jobType: String?, keyword: String?, lastJPD: String?, location: String?, organization: String?, pageId: String?, pageNumber: Int, postedWithIn: String?, age: String?, rpp: String?, slno: String?, version: String?) {
        Log.d("ArrayTestJobdetail", " loadNextPage called\n ")



        val call = ApiServiceJobs.create().getJobList(jobLevel = jobLevel,
                Newspaper = newsPaper,
                armyp = armyp,
                bc = blueColur,
                category = category,
                deadline = deadline,
                encoded = encoded,
                experience = experince,
                gender = gender,
                genderB = genderB,
                industry = industry,
                isFirstRequest = isFirstRequest,
                jobNature = jobnature,
                jobType = jobType,
                keyword = keyword,
                lastJPD = lastJPD,
                location = location,
                org = organization,
                pageid = pageId,
                pg = pageNumber,
                postedWithin = postedWithIn,
                qAge = age,
                rpp = rpp,
                slno = slno,
                version = version)
        call.enqueue(object : Callback<JobListModel> {

            override fun onResponse(call: Call<JobListModel>?, response: Response<JobListModel>) {

                try {
                    if (response.isSuccessful) {

                        Log.d("ArrayTestJobdetail", " response.isSuccessful")

                        val resp_jobs = response.body()

                        jobDetailAdapter?.removeLoadingFooter()
                        isLoadings = false

                        val results = response.body()?.data

                        if (!results.isNullOrEmpty()) {

                            jobDetailAdapter?.addAll(results)
                            jobDetailAdapter?.showHideShortListedIcon(position = currentJobPosition)
                        }

                        TOTAL_PAGES = resp_jobs?.common?.totalpages
                        if (currentPage >= TOTAL_PAGES!!) {
                            isLastPages = true
                        } else {
                            jobDetailAdapter?.addLoadingFooter()
                        }
                        communicator.setTotalPage(resp_jobs?.common?.totalpages)
                        communicator.setIsLoading(isLoadings)
                        communicator.setLastPasge(isLastPages)
                        communicator.setTotalJob(resp_jobs?.common?.totalRecordsFound!!)

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


    private fun loadFirstPage() {

        try {
            jobDetailAdapter?.addAll(jobListGet as List<JobListModelData>)
            if (currentPage == TOTAL_PAGES!!) {

                isLastPages = true
            }

            jobDetailAdapter?.showHideShortListedIcon(position = communicator.getItemClickPosition())


            totalRecordsFound?.let { it ->
                try {
                    if (it > 1) {
                        counterTV?.show()
                    } else {
                        counterTV?.hide()
                    }
                } catch (e: Exception) {
                }
            }


            counterTV?.let { tv ->
                tv.text = "Job ${communicator.getItemClickPosition() + 1}/$totalRecordsFound"

            }


        } catch (e: Exception) {
            logException(e)
        }

    }


    private fun onClick() {

        BackIMGV.setOnClickListener {
            communicator.backButtonPressesd()
        }

        filterIMGV.setOnClickListener {
            jobDetailAdapter!!.shareJobs(shareJobPosition)
            Log.d("ShareJob", "currentJobPosition $shareJobPosition")
        }

        shortListIMGV.setOnClickListener {
            jobDetailAdapter!!.shorlistAndUnshortlistJob(shareJobPosition)

        }
    }


    fun showShortListedIcon() {
        shortListIMGV?.setImageDrawable(activity?.getDrawable(R.drawable.ic_star_black_24dp_filled))
    }

    fun showUnShortListedIcon() {
        shortListIMGV?.setImageDrawable(activity?.getDrawable(R.drawable.ic_star_black_24dp))
    }

    fun showShrtListIcon() {
        shortListIMGV?.visibility = View.VISIBLE
    }

    fun hideShrtListIcon() {
        shortListIMGV?.visibility = View.INVISIBLE
    }

    override fun onPause() {
        try {
            communicator.setTotalJob(totalRecordsFound)
            communicator.setpageNumber(currentPage)
            communicator.setTotalPage(TOTAL_PAGES)
            communicator.setLastPasge(isLastPages)

            communicator.setKeyword(keyword)
            communicator.setLocation(location)
            communicator.setCategory(category)
            communicator.setNewsPaper(newsPaper)
            communicator.setIndustry(industry)
            communicator.setOrganization(organization)
            communicator.setGender(gender)
            communicator.setExperience(experience)
            communicator.setJobType(jobType)
            communicator.setJobLevel(jobLevel)
            communicator.setJobNature(jobNature)
            communicator.setPostedWithin(postedWithin)
            communicator.setDeadline(deadline)
            communicator.setAge(age)
            communicator.setArmy(army)
        } catch (e: Exception) {
            logException(e)
        }

        super.onPause()
    }


}