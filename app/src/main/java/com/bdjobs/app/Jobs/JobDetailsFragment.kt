package com.bdjobs.app.Jobs

import android.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.ClientAdModel
import com.bdjobs.app.API.ModelClasses.JobListModel
import com.bdjobs.app.API.ModelClasses.JobListModelData
import com.bdjobs.app.Ads.Ads
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.google.android.gms.ads.AdRequest
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_jobdetail_layout.*
import kotlinx.android.synthetic.main.fragment_jobdetail_layout.adView_container
import kotlinx.android.synthetic.main.fragment_jobdetail_layout.ivClientAd
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class JobDetailsFragment : Fragment() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    var jobDetailAdapter: JobDetailAdapter? = null
    private var currentPage = 1
    private lateinit var communicator: JobCommunicator
    private var snapHelper: SnapHelper? = null
    private var  jobListGet: MutableList<JobListModelData>? = null
    private var TOTAL_PAGES: Int? = 0
    private var totalRecordsFound: Int? = 0
    private var isLoadings = false
    private var isLastPages = false
    private var keyword: String? = ""
    private var location: String? = ""
    private var category: String? = ""
    private var newsPaper: String? = ""
    private var industry: String? = ""
    private var organization: String? = ""
    private var gender: String? = ""
    private var experience: String? = ""
    private var jobType: String? = ""
    private var jobLevel: String? = ""
    private var jobNature: String? = ""
    private var postedWithin: String? = ""
    private var deadline: String? = ""
    private var age: String? = ""
    private var army: String? = ""
    private var workPlace : String? = ""
    private var personWithDisability : String ? = ""
    private var facilitiesForPWD : String ? = ""


    var currentJobPosition = 0
    var shareJobPosition = 0

    lateinit var bdjobsDB: BdjobsDB
    lateinit var session: BdjobsUserSession

    companion object {
        private const val TAG = "JobDetails"
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_jobdetail_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsDB = BdjobsDB.getInstance(activity)
        session = BdjobsUserSession(activity)


//        showClientAD()

        alertTV?.isSelected = true
        communicator = activity as JobCommunicator

        shareJobPosition = communicator.getItemClickPosition()
        Log.d(TAG, "onActivityCreated: JP: $shareJobPosition")
        communicator.setCurrentJobPosition(communicator.getItemClickPosition())
        getData()

        snapHelper = PagerSnapHelper()
        jobDetailRecyclerView.onFlingListener = null
        (snapHelper as PagerSnapHelper).attachToRecyclerView(jobDetailRecyclerView)
        jobDetailRecyclerView.setHasFixedSize(true)

        layoutManager = LinearLayoutManager(activity, LinearLayout.HORIZONTAL, false)
        jobDetailRecyclerView?.layoutManager = layoutManager
        //Log.d("PositionTest", "snapHelper   ${snapHelper!!.getSnapPosition(jobDetailRecyclerView)}")
        jobDetailAdapter = JobDetailAdapter(activity!!)
        jobDetailRecyclerView?.adapter = jobDetailAdapter

        jobDetailRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    currentJobPosition = getCurrentItem()
                    Log.d(TAG, "onScrollStateChanged: CurrentJobPosition: $currentJobPosition")
                    //Log.d("PositionTest", "snapHelper   $currentJobPosition")

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

                Log.d(TAG, "loadMoreItemsgfjfg Called ")

                //Log.d("djggsgdjdg", "keyword $keyword  location $location  category $category  ")
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
                        version = "",
                        workPlace = workPlace,
                        personWithDisability = personWithDisability,
                        facilitiesForPWD = facilitiesForPWD

                )

                //loadNextPage("", "", "", category, "", "02041526JSBJ2", "", "", "", "", "", "", "", keyword, "", location, "", "", currentPage, "", "", "", "", "")
            }


        })

    }

    override fun onResume() {
        super.onResume()
//
        showClientAD()
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

    private fun showClientAD() {

        Log.d(TAG, "showClientAD: ShowClientAd")

        try {
            ApiServiceJobs.create().clientAdBanner("jobdetail")
                    .enqueue(object : Callback<ClientAdModel> {
                        override fun onResponse(call: Call<ClientAdModel>, response: Response<ClientAdModel>) {
                            Timber.d("Client ad fetched!")

                            try {
                                if (response.isSuccessful) {
                                    if (response.code() == 200) {
                                        if (response.body()?.data!!.isNotEmpty() && response.body()!!.data[0].imageurl.isNotEmpty()) {

                                            ivClientAd.visibility = View.VISIBLE
                                            adView_container.visibility = View.GONE

                                            val dimensionInDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, response.body()!!.data[0].height.toFloat(),
                                                    resources.displayMetrics).toInt()


                                            ivClientAd.layoutParams.height = dimensionInDp

                                            ivClientAd.requestLayout()

                                            Picasso.get()
                                                    .load(response.body()!!.data[0].imageurl)
                                                    .into(ivClientAd)


                                            ivClientAd.setOnClickListener {
                                                activity.launchUrl(response.body()!!.data[0].adurl)
                                            }

                                        }
                                        else {
                                            Log.d(TAG, "onResponse: 1")
                                            ivClientAd.visibility = View.GONE
                                            adView.loadAd(AdRequest.Builder().build())
//                                            mAdView.adListener = this
                                            adView.visibility = View.VISIBLE
//                                            adView_container.visibility = View.VISIBLE
//                                            Ads.loadAdaptiveBanner(context, adView_container)
                                        }
                                    } else {
                                        Log.d(TAG, "onResponse: 2")
                                        Timber.d("Response code: ${response.code()}")
                                        ivClientAd.visibility = View.GONE
                                        adView.loadAd(AdRequest.Builder().build())
//                                            mAdView.adListener = this
                                        adView.visibility = View.VISIBLE
//                                        adView_container.visibility = View.VISIBLE
//                                        Ads.loadAdaptiveBanner(context, adView_container)
                                    }
                                } else {
                                    Timber.d("Unsuccessful response")
                                    Log.d(TAG, "onResponse: 3")
                                    ivClientAd.visibility = View.GONE
                                    adView.loadAd(AdRequest.Builder().build())
//                                            mAdView.adListener = this
                                    adView.visibility = View.VISIBLE
//                                    adView_container.visibility = View.VISIBLE
//                                    Ads.loadAdaptiveBanner(context, adView_container)
                                }
                            } catch (e: Exception) {
                            }
                        }

                        override fun onFailure(call: Call<ClientAdModel>, t: Throwable) {
                            Timber.e("Client ad fetching failed due to: ${t.localizedMessage} .. Showing ADMob AD")

                            try {
                                Log.d(TAG, "onResponse: 4")
                                ivClientAd.visibility = View.GONE
                                adView.loadAd(AdRequest.Builder().build())
//                                            mAdView.adListener = this
                                adView.visibility = View.VISIBLE
//                                adView_container.visibility = View.VISIBLE
//                                Ads.loadAdaptiveBanner(context, adView_container)
                            } catch (e: Exception) {
                            }
                        }
                    })
        } catch (e: Exception) {}
    }

    private fun getData() {

        try {
            jobListGet = communicator.getJobList()!!
            //Log.d("Job detail fragment", "${jobListGet?.size}")
        } catch (e: Exception) {
            logException(e)
        }


        try {
            //Log.d("djggsgdjdg", "jobListGet ${jobListGet!!.size}")

            //Log.d("djggsgdjdg", "clickedPosition ${communicator.getItemClickPosition()}")

            //Log.d("djggsgdjdg", "getCurrentPageNumber: ${communicator.getCurrentPageNumber()}")

            //Log.d("djggsgdjdg", "getTotalPage: ${communicator.getTotalPage()}")
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
        workPlace = communicator.getWorkPlace()
        personWithDisability = communicator.getPersonWithDisability()
        facilitiesForPWD = communicator.getFacilitiesForPWD()


    }

    private fun loadNextPage(jobLevel: String?, newsPaper: String?, armyp: String?, blueColur: String?, category: String?, deadline: String?, encoded: String?, experince: String?, gender: String?, genderB: String?, industry: String?, isFirstRequest: String?, jobnature: String?, jobType: String?, keyword: String?, lastJPD: String?, location: String?, organization: String?, pageId: String?, pageNumber: Int, postedWithIn: String?, age: String?, rpp: String?, slno: String?, version: String?, workPlace: String?, personWithDisability : String?, facilitiesForPWD : String?) {
        //Log.d("ArrayTestJobdetail", " loadNextPage called\n ")

        Log.d(TAG, "loadNextPage: $pageNumber")

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
                version = version,
                workPlace = workPlace,
                personWithDisability = personWithDisability,
                facilitiesForPWD = facilitiesForPWD)
        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {

                try {
                    if (response.isSuccessful) {

                        //Log.d("ArrayTestJobdetail", " response.isSuccessful")
                        val responseData = response.body()?.string()

                        try {

                            val jobListModel = Gson().fromJson(responseData,JobListModel::class.java)

                            val resp_jobs = response.body()

                            jobDetailAdapter?.removeLoadingFooter()
                            isLoadings = false

                            val results = jobListModel?.data

                            if (!results.isNullOrEmpty()) {

                                jobDetailAdapter?.addAll(results)
                                jobDetailAdapter?.showHideShortListedIcon(position = currentJobPosition)
                            }

                            TOTAL_PAGES = jobListModel?.common?.totalpages
                            if (currentPage >= TOTAL_PAGES!!) {
                                isLastPages = true
                            } else {
                                jobDetailAdapter?.addLoadingFooter()
                            }
                            communicator.setTotalPage(jobListModel?.common?.totalpages)
                            communicator.setIsLoading(isLoadings)
                            communicator.setLastPasge(isLastPages)
                            communicator.setTotalJob(jobListModel?.common?.totalRecordsFound!!)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            ApiServiceJobs.create().responseBroken(url = "${call?.request()?.url}", params = "${call?.request()?.url?.query}", encoded = Constants.ENCODED_JOBS, userId = session.userId, response = responseData, appId = "1").enqueue(object : Callback<ResponseBody>{
                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}

                                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                }

                            })
                        }

                    } else {
                        //Log.d("TAG", "not successful: ")
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                //Log.d("TAG", "not successful!! onFail")
            }
        })
    }

    private fun loadFirstPage() {

        Log.d(TAG, "loadFirstPage: ")

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

        BackIMGV?.setOnClickListener {
            communicator.backButtonPressesd()
        }

        filterIMGV?.setOnClickListener {
            jobDetailAdapter!!.shareJobs(shareJobPosition)
            //Log.d("ShareJob", "currentJobPosition $shareJobPosition")
        }

        shortListIMGV?.setOnClickListener {
            jobDetailAdapter!!.shorlistAndUnshortlistJob(shareJobPosition)

        }
        shortListIMGV2?.setOnClickListener {
            jobDetailAdapter!!.reportthisJob(shareJobPosition)

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
            communicator.setWorkPlace(workPlace)
            communicator.setPersonWithDisability(personWithDisability)
            communicator.setFacilitiesForPWD(facilitiesForPWD)
        } catch (e: Exception) {
            logException(e)
        }

        super.onPause()
    }


}