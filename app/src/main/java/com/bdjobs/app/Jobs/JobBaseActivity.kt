package com.bdjobs.app.Jobs

//import com.google.android.gms.ads.AdRequest
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.ClientAdModel
import com.bdjobs.app.API.ModelClasses.JobListModelData
import com.bdjobs.app.Ads.Ads
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.Login.LoginBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.SuggestiveSearch.SuggestiveSearchActivity
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.ajkerDeal.ui.home.page_home.HomeNewFragment
import com.bdjobs.app.databases.External.DataStorage
import com.bdjobs.app.databases.internal.BdjobsDB
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_job_landing.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

class JobBaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener, JobCommunicator {

    override fun goToAjkerDealLive(containerId:Int) {

        Timber.d("going to Ajker deal")
        transitFragmentX(HomeNewFragment(),containerId,false)
    }

    override fun setTotalAppliedJobs(appliedJobsCount: Int) {
        this.appliedJobsCount = appliedJobsCount
    }

    override fun getTotalAppliedJobs(): Int {
        return appliedJobsCount
    }

    override fun setTotalPage(pages: Int?) {
        this.totalPages = pages
    }

    override fun showShortListIcon() {
        jobDetailsFragment.showShrtListIcon()
    }

    override fun hideShortListIcon() {
        jobDetailsFragment.hideShrtListIcon()
    }

    override fun gotoJobList() {
        transitFragment(joblistFragment, R.id.jobFragmentHolder)
    }

    override fun setCurrentJobPosition(from: Int) {
        this.currentJbPosition = from
    }

    override fun getCurrentJobPosition(): Int {
        return this.currentJbPosition
    }

    override fun getBackFrom(): String {
        return this.backFrom
    }

    override fun setBackFrom(from: String) {
        this.backFrom = from
    }

    override fun showShortListedIcon() {
        jobDetailsFragment.showShortListedIcon()
    }

    override fun showUnShortListedIcon() {
        jobDetailsFragment.showUnShortListedIcon()
    }

    var currentJbPosition: Int = 0

    private var totalRecordsFound: Int? = null
    private val joblistFragment = JoblistFragment()
    private val jobDetailsFragment = JobDetailsFragment()
    private val advanceSearchFragment = AdvanceSearchFragment()
    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null
    private val generalSearch = GeneralSearch()


    private var jobList1: MutableList<JobListModelData>? = null
    var clickedPosition: Int = 0
    var pgNumber: Int? = 1
    var totalPages: Int? = 0
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var appliedJobsCount: Int = 0

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
    private var filterID: String? = ""
    private var filterName: String? = ""
    private var from: String? = ""
    private var nId = ""
    private var seen = false

    private var workPlace: String? = ""
    private var personWithDisability: String? = ""
    private var facilitiesForPWD: String? = ""

    lateinit var dataStorage: DataStorage
    lateinit var bdjobsDB: BdjobsDB
    lateinit var bdjobsUserSession: BdjobsUserSession


    private var backFrom = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_landing)
        dataStorage = DataStorage(applicationContext)
        bdjobsDB = BdjobsDB.getInstance(applicationContext)
        bdjobsUserSession = BdjobsUserSession(applicationContext)


        val jobids = ArrayList<String>()
        val lns = ArrayList<String>()
        val deadline = ArrayList<String>()

        val inte  = intent.data

        clickedPosition = intent.getIntExtra("position", 0)
        val jobList: MutableList<JobListModelData> = java.util.ArrayList()

        try {
            jobids.add(inte?.getQueryParameter("id")!!)
            lns.add(inte.getQueryParameter("ln")!!)
            deadline.add("")
            for (i in 0 until jobids.size) {
                val jobListModelData = JobListModelData(
                        jobid = jobids[i],
                        jobTitle = "",
                        companyName = "",
                        deadline = "",
                        eduRec = "",
                        experience = "",
                        standout = "0",
                        logo = "",
                        lantype = lns[i]
                )
                jobList.add(jobListModelData)
                //Log.d("employerJobid", "jobid: ${jobids[i]} ln: ${lns[i]}")
            }

            setJobList(jobList)
            totalRecordsFound = jobList.size
            pgNumber = 1
            totalPages = 1
            isLastPage = true
            transitFragment(jobDetailsFragment, R.id.jobFragmentHolder)
        } catch (e: Exception) {
            getData()
        }

        adView.loadAd(AdRequest.Builder().build())
        showClientAD()
//        Ads.loadAdaptiveBanner(this@JobBaseActivity,adView_container)

    }

    private fun showClientAD() {

        try {
            ApiServiceJobs.create().clientAdBanner("jobsearch")
                    .enqueue(object : Callback<ClientAdModel> {
                        override fun onResponse(call: Call<ClientAdModel>, response: Response<ClientAdModel>) {
                            Timber.d("Client ad fetched!")

                            try {
                                if (response.isSuccessful) {
                                    if (response.code() == 200) {
                                        if (response.body()?.data!!.isNotEmpty() && response.body()!!.data[0].imageurl.isNotEmpty()) {

                                            ivClientAd.visibility = View.VISIBLE
                                            adView.visibility = View.GONE

                                            val dimensionInDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, response.body()!!.data[0].height.toFloat(),
                                                    resources.displayMetrics).toInt()


                                            ivClientAd.layoutParams.height = dimensionInDp

                                            ivClientAd.requestLayout()

                                            Picasso.get()
                                                    .load(response.body()!!.data[0].imageurl)
                                                    .into(ivClientAd)


                                            ivClientAd.setOnClickListener {
                                                this@JobBaseActivity.launchUrl(response.body()!!.data[0].adurl)
                                            }

                                        } else {
                                            ivClientAd.visibility = View.GONE
//                                            adView.visibility = View.VISIBLE
//                                            adView.loadAd(AdRequest.Builder().build())
//                                            Ads.loadAdaptiveBanner(this@JobBaseActivity, adView_container)
                                        }
                                    } else {
                                        Timber.d("Response code: ${response.code()}")
                                        ivClientAd.visibility = View.GONE
//                                        adView.visibility = View.VISIBLE
//                                        adView.loadAd(AdRequest.Builder().build())
//                                        Ads.loadAdaptiveBanner(this@JobBaseActivity, adView_container)
                                    }
                                }
                                else {
                                    Timber.d("Unsuccessful response")
                                    ivClientAd.visibility = View.GONE
//                                    adView.visibility = View.VISIBLE
//                                    adView.loadAd(AdRequest.Builder().build())
//                                    Ads.loadAdaptiveBanner(this@JobBaseActivity, adView_container)
                                }
                            } catch (e: Exception) {
                            }
                        }

                        override fun onFailure(call: Call<ClientAdModel>, t: Throwable) {
                            Timber.e("Client ad fetching failed due to: ${t.localizedMessage} .. Showing ADMob AD")
                            try {
                                ivClientAd.visibility = View.GONE
//                                adView.visibility = View.VISIBLE
//                                adView.loadAd(AdRequest.Builder().build())
//                                Ads.loadAdaptiveBanner(this@JobBaseActivity, adView_container)
                            } catch (e: Exception) {
                            }
                        }
                    })
        } catch (e: Exception) {}
    }

    override fun goToSuggestiveSearch(from: String, typedData: String?) {
        try {
            val intent = Intent(this@JobBaseActivity, SuggestiveSearchActivity::class.java)
            intent.putExtra(Constants.key_from, from)
            intent.putExtra(Constants.key_typedData, typedData)
            window?.exitTransition = null
            startActivityForResult(intent, Constants.BdjobsUserRequestCode)
        } catch (e: Exception) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.BdjobsUserRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                val typedData = data?.getStringExtra(Constants.key_typedData)
                val from = data?.getStringExtra(Constants.key_from)

                //Log.d("eryfdh", "from: $from --- typedData : $typedData")

                when (from) {
                    Constants.key_jobtitleET -> setKeyword(typedData)
                    Constants.key_loacationET -> setLocation(dataStorage.getLocationIDByName(typedData))
                    Constants.key_categoryET -> setCategory(dataStorage.getCategoryIDByName(typedData))
                    Constants.key_special_categoryET -> setCategory(dataStorage.getCategoryIDByBanglaName(typedData))
                    Constants.key_newspaperET -> setNewsPaper(dataStorage.getNewspaperIDByName(typedData))
                    Constants.key_industryET -> setIndustry(dataStorage.getJobSearcIndustryIDbyName(typedData))
                }
            }
        }
    }

    override fun setFilterID(filterID: String?) {
        this.filterID = filterID
    }

    override fun setFilterName(filterName: String?) {
        this.filterName = filterName
    }

    override fun goToAdvanceSearch() {
        transitFragment(advanceSearchFragment, R.id.jobFragmentHolder, true)
    }

    override fun getFilterID(): String? {
        return filterID
    }

    override fun getFilterName(): String? {
        return filterName
    }

    private fun getData() {

        val intent = this.intent
        try {
            setKeyword(intent.getStringExtra(Constants.key_jobtitleET))
            //keyword = intent.getStringExtra(Constants.key_jobtitleET)
        } catch (e: Exception) {
            logException(e)
        }
        try {
            setLocation(intent.getStringExtra(Constants.key_loacationET))
            //location = intent.getStringExtra(Constants.key_loacationET)
        } catch (e: Exception) {
            logException(e)
        }
        try {
            setCategory(intent.getStringExtra(Constants.key_categoryET))
            //category = intent.getStringExtra(Constants.key_categoryET)
        } catch (e: Exception) {
            logException(e)
        }

        try {
            nId = intent.getStringExtra("nid").toString()
        } catch (e: Exception) {
            logException(e)
        }

        //Log.d("wtji", "jobase=>\nkeyword: $keyword \nlocation: $location\n category:$category")
////
        try {
            //postedWithin = intent.getStringExtra("postedWithin")
            setPostedWithin(intent.getStringExtra("postedWithin"))
        } catch (e: Exception) {
            logException(e)
        }
        try {
            // deadline = intent.getStringExtra("deadline")
            setDeadline(intent.getStringExtra("deadline"))
        } catch (e: Exception) {
            logException(e)
        }
        try {
            //jobNature = intent.getStringExtra("jobNature")
            setJobNature(intent.getStringExtra("jobNature"))
        } catch (e: Exception) {
            logException(e)
        }
        try {
            //organization = intent.getStringExtra("organization")
            setOrganization(intent.getStringExtra("organization"))
        } catch (e: Exception) {
            logException(e)
        }

        try {
            seen = intent.getBooleanExtra("seen", false)
        } catch (e: Exception) {
            logException(e)
        }


        try {
            from = intent.getStringExtra("from")
            when (from) {
                "lastsearch" -> doAsync {
                    val lastSearch = bdjobsDB.lastSearchDao().getLastSearch()[0]
                    setKeyword(lastSearch.keyword)
                    setCategory(lastSearch.category)
                    setLocation(lastSearch.location)
                    setIndustry(lastSearch.industry)
                    setNewsPaper(lastSearch.newsPaper)
                    setOrganization(lastSearch.organization)
                    setGender(lastSearch.gender)
                    setExperience(lastSearch.experince)
                    setJobType(lastSearch.jobType)
                    setJobLevel(lastSearch.jobLevel)
                    setJobNature(lastSearch.jobnature)
                    setPostedWithin(lastSearch.postedWithIn)
                    setDeadline(lastSearch.deadline)
                    setAge(lastSearch.age)
                    setArmy(lastSearch.armyp)
                    setWorkPlace(lastSearch.workPlace)
                    setPersonWithDisability(lastSearch.personWithDisability)
                    setFacilitiesForPWD(lastSearch.facilitiesForPWD)

                    uiThread {
                        transitFragment(joblistFragment, R.id.jobFragmentHolder)
                    }
                }
                "favsearch" -> {
                    filterID = intent.getStringExtra("filterid")
                    doAsync {
                        try {
                            val favSearch = bdjobsDB.favouriteSearchFilterDao().getFavouriteSearchByID(filterID)
                            setFilterName(favSearch.filtername)
                            setKeyword(favSearch.keyword)
                            setCategory(favSearch.functionalCat)
                            setLocation(favSearch.location)
                            setIndustry(favSearch.industrialCat)
                            setNewsPaper(favSearch.newspaper)
                            setOrganization(favSearch.organization)
                            setGender(favSearch.gender)
                            setExperience(favSearch.experience)
                            setJobType(favSearch.jobtype)
                            setJobLevel(favSearch.joblevel)
                            setJobNature(favSearch.jobnature)
                            setPostedWithin(favSearch.postedon)
                            setDeadline(favSearch.deadline)
                            setAge(favSearch.age)
                            setArmy(favSearch.retiredarmy)
                            setWorkPlace(favSearch.workPlace)
                            setPersonWithDisability(favSearch.personWithDisability)
                            setFacilitiesForPWD(favSearch.facilitiesForPWD)
                        } catch (e: Exception) {
                            logException(e)
                        }
                        uiThread {
                            transitFragment(joblistFragment, R.id.jobFragmentHolder)
                        }
                    }
                }

                "shortListedJob" -> {
                    try {
                        clickedPosition = intent.getIntExtra("position", 0)
                    } catch (e: Exception) {
                        logException(e)
                    }

                    //Log.d("shortListedJob", "shortListedJob: $clickedPosition")
                    var shortListFilter = ""
                    try {
                        shortListFilter = intent.getStringExtra("shortListFilter").toString()
                    } catch (e: Exception) {
                        logException(e)
                    }

                    when (shortListFilter) {
                        "" -> {
                            showAllshortListedJobs()
                        }
                        "Today" -> {
                            val calendar = Calendar.getInstance()
                            val deadlineToday = calendar.time
                            getShortListedJobsByDeadline(deadlineToday)
                        }
                        "Tomorrow" -> {
                            val calendar = Calendar.getInstance()
                            calendar.add(Calendar.DAY_OF_YEAR, 1)
                            val deadlineNextDay = calendar.time
                            getShortListedJobsByDeadline(deadlineNextDay)
                        }
                        "Next 2 days" -> {
                            val calendar = Calendar.getInstance()
                            calendar.add(Calendar.DAY_OF_YEAR, 2)
                            val deadlineNext2Days = calendar.time
                            getShortListedJobsByDeadline(deadlineNext2Days)
                        }
                        "Next 3 days" -> {
                            val calendar = Calendar.getInstance()
                            calendar.add(Calendar.DAY_OF_YEAR, 3)
                            val deadlineNext3Days = calendar.time
                            getShortListedJobsByDeadline(deadlineNext3Days)
                        }
                        "Next 4 days" -> {
                            val calendar = Calendar.getInstance()
                            calendar.add(Calendar.DAY_OF_YEAR, 4)
                            val deadlineNext4Days = calendar.time
                            getShortListedJobsByDeadline(deadlineNext4Days)
                        }
                    }

                }

                "employer" -> {
                    clickedPosition = intent.getIntExtra("position", 0)
                    val jobList: MutableList<JobListModelData> = java.util.ArrayList()
                    val jobids = intent.getStringArrayListExtra("jobids")
                    //Log.d("rakib", "job id ${jobids[0]}")
                    val lns = intent.getStringArrayListExtra("lns")
                    val deadline = intent.getStringArrayListExtra("deadline")
                    if (jobids != null) {
                        for (i in 0 until jobids.size) {
                            val jobListModelData = JobListModelData(
                                    jobid = jobids.get(i),
                                    jobTitle = "",
                                    companyName = "",
                                    deadline = "",
                                    eduRec = "",
                                    experience = "",
                                    standout = "0",
                                    logo = "",
                                    lantype = lns?.get(i)
                            )
                            jobList.add(jobListModelData)
                            //Log.d("employerJobid", "jobid: ${jobids[i]} ln: ${lns[i]}")
                        }
                    }

                    setJobList(jobList)
                    totalRecordsFound = jobList.size
                    pgNumber = 1
                    totalPages = 1
                    isLastPage = true
                    transitFragment(jobDetailsFragment, R.id.jobFragmentHolder)
                }

                "notification" -> {
                    clickedPosition = intent.getIntExtra("position", 0)
                    val jobList: MutableList<JobListModelData> = java.util.ArrayList()
                    val jobids = intent.getStringArrayListExtra("jobids")
                    val lns = intent.getStringArrayListExtra("lns")
                    val deadline = intent.getStringArrayListExtra("deadline")
                    if (jobids != null) {
                        for (i in 0 until jobids.size) {
                            val jobListModelData = JobListModelData(
                                    jobid = jobids.get(i),
                                    jobTitle = "",
                                    companyName = "",
                                    deadline = "",
                                    eduRec = "",
                                    experience = "",
                                    standout = "0",
                                    logo = "",
                                    lantype = lns?.get(i)
                            )
                            jobList.add(jobListModelData)
                            //Log.d("employerJobid", "jobid: ${jobids[i]} ln: ${lns[i]}")
                        }
                    }

                    setJobList(jobList)
                    totalRecordsFound = jobList.size
                    pgNumber = 1
                    totalPages = 1
                    isLastPage = true

                    if (!seen) {
                        jobids?.get(0)?.let { logDataForAnalytics(Constants.NOTIFICATION_TYPE_MATCHED_JOB, applicationContext, it, nId) }

                        try {
                            ApiServiceJobs.create().sendDataForAnalytics(
                                    userID = bdjobsUserSession.userId, decodeID = bdjobsUserSession.decodId, uniqueID = nId, notificationType = Constants.NOTIFICATION_TYPE_MATCHED_JOB, encode = Constants.ENCODED_JOBS, sentTo = "Android"
                            ).enqueue(
                                    object : Callback<String> {
                                        override fun onFailure(call: Call<String>, t: Throwable) {

                                        }

                                        override fun onResponse(call: Call<String>, response: Response<String>) {

                                            try {
                                                if (response.isSuccessful) {
                                                }
                                            } catch (e: Exception) {
                                                logException(e)
                                            }
                                        }
                                    }
                            )
                        } catch (e: Exception) {
                        }
                    }

                    doAsync {
                        bdjobsDB.notificationDao().updateNotificationTableByClickingNotification(Date(), true, nId, Constants.NOTIFICATION_TYPE_MATCHED_JOB)
                        val count = bdjobsDB.notificationDao().getNotificationCount()
                        bdjobsUserSession = BdjobsUserSession(this@JobBaseActivity)
                        bdjobsUserSession.updateNotificationCount(count)
                    }

                    transitFragment(jobDetailsFragment, R.id.jobFragmentHolder)
                }

                "notificationList" -> {
                    clickedPosition = intent.getIntExtra("position", 0)
                    val jobList: MutableList<JobListModelData> = java.util.ArrayList()
                    val jobids = intent.getStringArrayListExtra("jobids")
                    val lns = intent.getStringArrayListExtra("lns")
                    val deadline = intent.getStringArrayListExtra("deadline")
                    if (jobids != null) {
                        for (i in 0 until jobids.size) {
                            val jobListModelData = JobListModelData(
                                    jobid = jobids.get(i),
                                    jobTitle = "",
                                    companyName = "",
                                    deadline = "",
                                    eduRec = "",
                                    experience = "",
                                    standout = "0",
                                    logo = "",
                                    lantype = lns?.get(i)
                            )
                            jobList.add(jobListModelData)
                            //Log.d("employerJobid", "jobid: ${jobids[i]} ln: ${lns[i]}")
                        }
                    }


                    if (!seen) {
                        try {
                            jobids?.get(0)?.let { logDataForAnalytics(Constants.NOTIFICATION_TYPE_MATCHED_JOB, applicationContext, it, nId) }
                        } catch (e: Exception) {
                        }

                        try {
                            ApiServiceJobs.create().sendDataForAnalytics(
                                    userID = bdjobsUserSession.userId, decodeID = bdjobsUserSession.decodId, uniqueID = nId, notificationType = Constants.NOTIFICATION_TYPE_MATCHED_JOB, encode = Constants.ENCODED_JOBS, sentTo = "Android"
                            ).enqueue(
                                    object : Callback<String> {
                                        override fun onFailure(call: Call<String>, t: Throwable) {

                                        }

                                        override fun onResponse(call: Call<String>, response: Response<String>) {

                                            try {
                                                if (response.isSuccessful) {
                                                }
                                            } catch (e: Exception) {
                                                logException(e)
                                            }
                                        }
                                    }
                            )
                        } catch (e: Exception) {
                        }
                    }

                    setJobList(jobList)
                    totalRecordsFound = jobList.size
                    pgNumber = 1
                    totalPages = 1
                    isLastPage = true
                    transitFragment(jobDetailsFragment, R.id.jobFragmentHolder)
                }

                "generalsearch" -> {
                    transitFragment(generalSearch, R.id.jobFragmentHolder, false)
                }

                "alljobsearch" -> {
                    transitFragment(joblistFragment, R.id.jobFragmentHolder, false)
                }

                else -> transitFragment(joblistFragment, R.id.jobFragmentHolder, false)
            }

        } catch (e: Exception) {
            logException(e)
            //transitFragment(joblistFragment, R.id.jobFragmentHolder)
        }

    }

    private fun getShortListedJobsByDeadline(deadline: Date) {
        doAsync {
            val shortListedJobs = bdjobsDB.shortListedJobDao().getShortListedJobsBYDeadline(deadline)

            val jobList: MutableList<JobListModelData> = java.util.ArrayList()

            for (item in shortListedJobs) {

                val jobListModelData = JobListModelData(
                        jobid = item.jobid,
                        jobTitle = item.jobtitle,
                        companyName = item.companyname,
                        deadline = item.deadline?.toSimpleDateString(),
                        eduRec = item.eduRec,
                        experience = item.experience,
                        standout = item.standout,
                        logo = item.logo,
                        lantype = item.lantype
                )

                jobList.add(jobListModelData)
            }

            uiThread {
                setJobList(jobList)
                totalRecordsFound = jobList.size
                pgNumber = 1
                totalPages = 1
                isLastPage = true
                transitFragment(jobDetailsFragment, R.id.jobFragmentHolder)
            }
        }

    }

    private fun showAllshortListedJobs() {
        doAsync {
            val shortListedJobs = bdjobsDB.shortListedJobDao().getAllShortListedJobs()

            val jobList: MutableList<JobListModelData> = java.util.ArrayList()

            for (item in shortListedJobs) {

                val jobListModelData = JobListModelData(
                        jobid = item.jobid,
                        jobTitle = item.jobtitle,
                        companyName = item.companyname,
                        deadline = item.deadline?.toSimpleDateString(),
                        eduRec = item.eduRec,
                        experience = item.experience,
                        standout = item.standout,
                        logo = item.logo,
                        lantype = item.lantype
                )

                jobList.add(jobListModelData)
            }

            uiThread {
                setJobList(jobList)
                totalRecordsFound = jobList.size
                pgNumber = 1
                totalPages = 1
                isLastPage = true
                transitFragment(jobDetailsFragment, R.id.jobFragmentHolder)
            }
        }
    }


    override fun backButtonPressesd() {
        Timber.tag("job rakib").d("back button in activity")
        onBackPress()
    }

    override fun goToLoginPage() {
        startActivity(intentFor<LoginBaseActivity>(Constants.key_go_to_home to false))
    }

    override fun onPostResume() {
        super.onPostResume()
        registerReceiver(internetBroadCastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(internetBroadCastReceiver)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            mSnackBar = Snackbar
                    .make(jobsBaseCL, getString(R.string.alert_no_internet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.turn_on_wifi)) {
                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    }
                    .setActionTextColor(resources.getColor(R.color.colorWhite))
            mSnackBar?.show()
        } else {
            mSnackBar?.dismiss()
        }
    }


    override fun getLocation(): String? {
        return this.location
    }


    override fun getKeyword(): String? {
        return this.keyword
    }

    override fun getCategory(): String? {
        return this.category
    }


    override fun onItemClicked(position: Int) {
        clickedPosition = position
        transitFragment(jobDetailsFragment, R.id.jobFragmentHolder, true)
    }


    override fun getJobList(): MutableList<JobListModelData>? {
        return jobList1
    }

    override fun setJobList(jobList: MutableList<JobListModelData>?) {
        jobList1 = jobList
        //Log.d("setJobList", "setJobList: ${jobList?.size}")
    }

    override fun setPosition(position: Int) {
        clickedPosition = position
    }

    override fun getItemClickPosition(): Int {
        return clickedPosition
    }

    override fun setpageNumber(pageNumber: Int) {
        pgNumber = pageNumber
    }

    override fun getCurrentPageNumber(): Int {
        return pgNumber!!
    }

    override fun setTotalJob(totalPage: Int?) {
        totalRecordsFound = totalPage

    }

    override fun setLastPasge(lastPage: Boolean) {
        isLastPage = lastPage
    }

    override fun setIsLoading(value: Boolean) {
        isLoading = value
    }

    override fun getTotalPage(): Int {
        return totalPages!!
    }

    override fun getLastPasge(): Boolean {
        return isLastPage
    }

    override fun getIsLoading(): Boolean {
        return isLoading
    }

    override fun getOrganization(): String? {
        return this.organization
    }

    override fun getGender(): String? {
        return this.gender
    }

    override fun getExperience(): String? {
        return this.experience
    }

    override fun getJobType(): String? {
        return this.jobType
    }

    override fun getJobLevel(): String? {
        return this.jobLevel
    }

    override fun getJobNature(): String? {
        return this.jobNature
    }

    override fun getPostedWithin(): String? {
        return this.postedWithin
    }

    override fun getDeadline(): String? {
        return this.deadline
    }

    override fun getAge(): String? {
        return this.age
    }

    override fun getArmy(): String? {
        return this.army
    }

    override fun getWorkPlace(): String? {
        return this.workPlace
    }

    override fun getPersonWithDisability(): String? {
        return this.personWithDisability
    }

    override fun getFacilitiesForPWD(): String? {
        return this.facilitiesForPWD
    }

    override fun setOrganization(value: String?) {
        this.organization = value
    }

    override fun setGender(value: String?) {
        this.gender = value
    }

    override fun setExperience(value: String?) {
        this.experience = value
    }

    override fun setJobType(value: String?) {
        this.jobType = value
    }

    override fun setJobLevel(value: String?) {
        this.jobLevel = value
    }

    override fun setJobNature(value: String?) {
        this.jobNature = value
    }

    override fun setPostedWithin(value: String?) {
        this.postedWithin = value
    }

    override fun setDeadline(value: String?) {
        this.deadline = value
    }

    override fun setAge(value: String?) {
        this.age = value
    }

    override fun setArmy(value: String?) {
        this.army = value
    }

    override fun setWorkPlace(value: String?) {
        this.workPlace = value
    }

    override fun setPersonWithDisability(value: String?) {
        this.personWithDisability = value
    }

    override fun setFacilitiesForPWD(value: String?) {
        this.facilitiesForPWD = value
    }

    override fun getNewsPaper(): String? {
        return this.newsPaper
    }

    override fun getIndustry(): String? {
        return this.industry
    }

    override fun setKeyword(value: String?) {
        this.keyword = value
    }

    override fun setCategory(value: String?) {
        this.category = value
    }

    override fun setLocation(value: String?) {
        this.location = value
    }

    override fun setIndustry(value: String?) {
        this.industry = value
    }

    override fun setNewsPaper(value: String?) {
        this.newsPaper = value
    }

     fun onBackPress() {
        try {
            val bdjobsUserSession = BdjobsUserSession(applicationContext)
            //Log.d("wreiifb", "From: $from")
            if (bdjobsUserSession.isLoggedIn!! && !from.isNullOrBlank() && from?.equalIgnoreCase("guestuser")!!) {
                Timber.tag("job rakib").d("if")
                val joblistFragment = fragmentManager.findFragmentByTag(simpleClassName(joblistFragment))
                if (joblistFragment != null && joblistFragment.isVisible) {
                    Timber.tag("job rakib").d("inner if")
                    val intent = Intent(this@JobBaseActivity, MainLandingActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finishAffinity()
                } else {
                    Timber.tag("job rakib").d("inner else")
                    super.onBackPressed()
                }
            }else if (bdjobsUserSession.isLoggedIn && !from.isNullOrBlank() && from?.equalIgnoreCase("employer")!!) {
                Timber.tag("job rakib").d("else if")
                finish()
            } else {
                Timber.tag("job rakib").d("else")
//                finish()
                super.onBackPressed()
            }
        } catch (e: Exception) {
            logException(e)
        }
    }

    override fun totalJobCount(totalJobFound: Int?) {
        totalRecordsFound = totalJobFound
    }

    override fun getTotalJobCount(): Int? {
        return totalRecordsFound
    }


    override fun onRestart() {
        super.onRestart()
        jobDetailsFragment.jobDetailAdapter?.reload()

    }
}
