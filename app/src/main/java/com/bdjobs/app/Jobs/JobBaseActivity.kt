package com.bdjobs.app.Jobs

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.bdjobs.app.API.ModelClasses.JobListModelData
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.Login.LoginBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.SuggestiveSearch.SuggestiveSearchActivity
import com.bdjobs.app.Utilities.*
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_job_landing.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.uiThread
import java.util.*

class JobBaseActivity : Activity(), ConnectivityReceiver.ConnectivityReceiverListener, JobCommunicator {
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


    lateinit var dataStorage: DataStorage
    lateinit var bdjobsDB: BdjobsDB

    private var backFrom = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_landing)
        dataStorage = DataStorage(applicationContext)
        bdjobsDB = BdjobsDB.getInstance(applicationContext)
        getData()

        try {
            adView?.hide()
            val deviceInfo = getDeviceInformation()
            val screenSize = deviceInfo[Constants.KEY_SCREEN_SIZE]

            screenSize?.let{it->
                if(it.toFloat()>5.0){
                    val adRequest = AdRequest.Builder().build()
                    adView?.loadAd(adRequest)
                    adView?.show()
                }
            }
        } catch (e: Exception) {
            logException(e)
        }

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

                Log.d("eryfdh", "from: $from --- typedData : $typedData")

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


        Log.d("wtji", "jobase=>\nkeyword: $keyword \nlocation: $location\n category:$category")
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

                    Log.d("shortListedJob", "shortListedJob: $clickedPosition")
                    var shortListFilter = ""
                    try {
                        shortListFilter = intent.getStringExtra("shortListFilter")
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
                    val lns = intent.getStringArrayListExtra("lns")
                    val deadline = intent.getStringArrayListExtra("deadline")
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
                        Log.d("employerJobid", "jobid: ${jobids[i]} ln: ${lns[i]}")
                    }

                    setJobList(jobList)
                    totalRecordsFound = jobList.size
                    pgNumber = 1
                    totalPages = 1
                    isLastPage = true
                    transitFragment(jobDetailsFragment, R.id.jobFragmentHolder)
                }

                "generalsearch" -> {
                    transitFragment(generalSearch, R.id.jobFragmentHolder)
                }

                else -> transitFragment(joblistFragment, R.id.jobFragmentHolder)
            }

        } catch (e: Exception) {
            logException(e)
            transitFragment(joblistFragment, R.id.jobFragmentHolder)
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
        onBackPressed()
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
        Log.d("setJobList", "setJobList: ${jobList?.size}")
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

    override fun onBackPressed() {
        try {
            val bdjobsUserSession = BdjobsUserSession(applicationContext)
            Log.d("wreiifb", "From: $from")
            if (bdjobsUserSession.isLoggedIn!! && !from.isNullOrBlank() && from?.equalIgnoreCase("guestuser")!!) {
                val joblistFragment = fragmentManager.findFragmentByTag(simpleClassName(joblistFragment))
                if (joblistFragment != null && joblistFragment.isVisible) {
                    val intent = Intent(this@JobBaseActivity, MainLandingActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finishAffinity()
                } else {
                    super.onBackPressed()
                }
            }else if (bdjobsUserSession.isLoggedIn!! && !from.isNullOrBlank() && from?.equalIgnoreCase("employer")!!) {
                finish()
            } else {
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
        Log.d("rakib"," came restart")
        jobDetailsFragment.jobDetailAdapter?.notifyDataSetChanged()

    }
}
