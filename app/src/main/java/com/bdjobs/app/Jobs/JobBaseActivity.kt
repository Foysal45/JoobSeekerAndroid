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
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.simpleClassName
import com.bdjobs.app.Utilities.transitFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_job_landing.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.uiThread

class JobBaseActivity : Activity(), ConnectivityReceiver.ConnectivityReceiverListener, JobCommunicator {


    private var totalRecordsFound: Int? = null
    private val joblistFragment = JoblistFragment()
    private val jobDetailsFragment = JobDetailsFragment()
    private val advanceSearchFragment = AdvanceSearchFragment()
    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null


    private var jobList1: MutableList<JobListModelData>? = null
    var clickedPosition: Int = 0
    var pgNumber: Int? = 1
    var totalPages: Int? = 0
    var isLastPage: Boolean = false
    var isLoading: Boolean = false


    private var keyword = ""
    private var location = ""
    private var category = ""
    private var newsPaper = ""
    private var industry = ""
    private var organization = ""
    private var gender = ""
    private var experience = ""
    private var jobType = ""
    private var jobLevel = ""
    private var jobNature = ""
    private var postedWithin = ""
    private var deadline = ""
    private var age = ""
    private var army = ""


    lateinit var dataStorage: DataStorage
    lateinit var bdjobsDB: BdjobsDB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_landing)
        dataStorage = DataStorage(applicationContext)
        bdjobsDB = BdjobsDB.getInstance(applicationContext)
        getData()

    }


    override fun goToSuggestiveSearch(from: String, typedData: String) {
        val intent = Intent(this@JobBaseActivity, SuggestiveSearchActivity::class.java)
        intent.putExtra(Constants.key_from, from)
        intent.putExtra(Constants.key_typedData, typedData)
        window.exitTransition = null
        startActivityForResult(intent, Constants.BdjobsUserRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.BdjobsUserRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                val typedData = data?.getStringExtra(Constants.key_typedData)
                val from = data?.getStringExtra(Constants.key_from)
                when (from) {
                    Constants.key_jobtitleET -> setKeyword(typedData!!)
                    Constants.key_loacationET -> setLocation(dataStorage.getLocationIDByName(typedData!!)!!)
                    Constants.key_categoryET -> setCategory(dataStorage.getCategoryIDByName(typedData!!)!!)
                    Constants.key_special_categoryET -> setCategory(dataStorage.getCategoryIDByBanglaName(typedData!!)!!)
                    Constants.key_newspaperET -> setNewsPaper(dataStorage.getNewspaperIDByName(typedData!!)!!)
                    Constants.key_industryET -> setIndustry(dataStorage.getJobSearcIndustryIDbyName(typedData!!)!!)
                }
            }
        }
    }

    override fun goToAdvanceSearch() {
        transitFragment(advanceSearchFragment, R.id.jobFragmentHolder, true)
    }


    private fun getData() {

        val intent = this.intent
        try {
            keyword = intent.getStringExtra(Constants.key_jobtitleET)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            location = intent.getStringExtra(Constants.key_loacationET)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            category = intent.getStringExtra(Constants.key_categoryET)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val from = intent.getStringExtra("from")
            if (from == "lastsearch") {
                doAsync {
                    val lastSearch = bdjobsDB.lastSearchDao().getLastSearch()[0]
                    setKeyword(lastSearch.keyword!!)
                    setCategory(lastSearch.category!!)
                    setLocation(lastSearch.location!!)
                    setIndustry(lastSearch.industry!!)
                    setNewsPaper(lastSearch.newsPaper!!)
                    setOrganization(lastSearch.organization!!)
                    setGender(lastSearch.gender!!)
                    setExperience(lastSearch.experince!!)
                    setJobType(lastSearch.jobType!!)
                    setJobLevel(lastSearch.jobLevel!!)
                    setJobNature(lastSearch.jobnature!!)
                    setPostedWithin(lastSearch.postedWithIn!!)
                    setDeadline(lastSearch.deadline!!)
                    setAge(lastSearch.age!!)
                    setArmy(lastSearch.armyp!!)

                    uiThread {
                        transitFragment(joblistFragment, R.id.jobFragmentHolder)
                    }
                }
            } else {
                transitFragment(joblistFragment, R.id.jobFragmentHolder)
            }

        } catch (e: Exception) {
            transitFragment(joblistFragment, R.id.jobFragmentHolder)
        }


        try {
            val from = intent.getStringExtra("from")
            if (from == "favsearch") {
                val filterID = intent.getStringExtra("filterid")

                doAsync {
                    val lastSearch = bdjobsDB.favouriteSearchFilterDao().getFavouriteSearchByID(filterID)
                    setKeyword(lastSearch.keyword!!)
                    setCategory(lastSearch.functionalCat!!)
                    setLocation(lastSearch.location!!)
                    setIndustry(lastSearch.industrialCat!!)
                    setNewsPaper(lastSearch.newspaper!!)
                    setOrganization(lastSearch.organization!!)
                    setGender(lastSearch.gender!!)
                    setExperience(lastSearch.experience!!)
                    setJobType(lastSearch.jobtype!!)
                    setJobLevel(lastSearch.joblevel!!)
                    setJobNature(lastSearch.jobnature!!)
                    setPostedWithin(lastSearch.postedon!!)
                    setDeadline(lastSearch.deadline!!)
                    setAge(lastSearch.age!!)
                    setArmy(lastSearch.retiredarmy!!)

                    uiThread {
                        transitFragment(joblistFragment, R.id.jobFragmentHolder)
                    }
                }
            } else {
                transitFragment(joblistFragment, R.id.jobFragmentHolder)
            }

        } catch (e: Exception) {
            transitFragment(joblistFragment, R.id.jobFragmentHolder)
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


    override fun getLocation(): String {
        return this.location
    }


    override fun getKeyword(): String {
        return this.keyword
    }

    override fun getCategory(): String {
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

    override fun setTotalJob(totalPage: Int) {
        totalPages = totalPage

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

    override fun getOrganization(): String {
        return this.organization
    }

    override fun getGender(): String {
        return this.gender
    }

    override fun getExperience(): String {
        return this.experience
    }

    override fun getJobType(): String {
        return this.jobType
    }

    override fun getJobLevel(): String {
        return this.jobLevel
    }

    override fun getJobNature(): String {
        return this.jobNature
    }

    override fun getPostedWithin(): String {
        return this.postedWithin
    }

    override fun getDeadline(): String {
        return this.deadline
    }

    override fun getAge(): String {
        return this.age
    }

    override fun getArmy(): String {
        return this.army
    }

    override fun setOrganization(value: String) {
        this.organization = value
    }

    override fun setGender(value: String) {
        this.gender = value
    }

    override fun setExperience(value: String) {
        this.experience = value
    }

    override fun setJobType(value: String) {
        this.jobType = value
    }

    override fun setJobLevel(value: String) {
        this.jobLevel = value
    }

    override fun setJobNature(value: String) {
        this.jobNature = value
    }

    override fun setPostedWithin(value: String) {
        this.postedWithin = value
    }

    override fun setDeadline(value: String) {
        this.deadline = value
    }

    override fun setAge(value: String) {
        this.age = value
    }

    override fun setArmy(value: String) {
        this.army = value
    }

    override fun getNewsPaper(): String {
        return this.newsPaper
    }

    override fun getIndustry(): String {
        return this.industry
    }

    override fun setKeyword(value: String) {
        this.keyword = value
    }

    override fun setCategory(value: String) {
        this.category = value
    }

    override fun setLocation(value: String) {
        this.location = value
    }

    override fun setIndustry(value: String) {
        this.industry = value
    }

    override fun setNewsPaper(value: String) {
        this.newsPaper = value
    }

    override fun onBackPressed() {
        val bdjobsUserSession = BdjobsUserSession(applicationContext)
        if (bdjobsUserSession.isLoggedIn!!) {
            val joblistFragment = fragmentManager.findFragmentByTag(simpleClassName(joblistFragment))
            if (joblistFragment != null && joblistFragment.isVisible) {
                val intent = Intent(this@JobBaseActivity, MainLandingActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finishAffinity()
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun totalJobCount(totalJobFound: Int?) {
        totalRecordsFound = totalJobFound
    }

    override fun getTotalJobCount(): Int? {
        return totalRecordsFound
    }


}
