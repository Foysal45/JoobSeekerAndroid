package com.bdjobs.app.Jobs

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.bdjobs.app.API.ModelClasses.JobListModelData
import com.bdjobs.app.ConnectivityCheck.ConnectivityReceiver
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_job_landing.*

class JobBaseActivity : Activity(), ConnectivityReceiver.ConnectivityReceiverListener ,JobCommunicator{



    private var totalRecordsFound: Int? = null
    private val joblistFragment = JoblistFragment()
    private val jobDetailsFragment = JobDetailsFragment()
    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null

    //new
    private var jobList1: MutableList<JobListModelData>? = null
    var clickedPosition : Int = 0
    var pgNumber : Int? = 1
    var totalPages: Int? = 0
    var isLastPage : Boolean = false
    var isLoading : Boolean = false

    /////////


    private var keyword = ""
    private var location = ""
    private var category = ""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_landing)
        getData()

        transitFragment(joblistFragment, R.id.jobFragmentHolder)
    }


    private fun getData(){

        val intent = this.intent
        keyword = intent.getStringExtra(Constants.key_jobtitleET)
        location = intent.getStringExtra(Constants.key_loacationET)
        category = intent.getStringExtra(Constants.key_categoryET)



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


    override fun setLocation(location: String) {
       this.location = location
    }

    override fun getLocation(): String {
      return location
    }

    override fun setKeyword(keyword: String) {
       this.keyword = keyword
    }

    override fun getKeyword(): String {
      return keyword
    }

    override fun getCategory(): String {
        return category
    }

    override fun setCategory(category: String) {
       this.category = category
    }


    override fun scrolledJobsNumber(position: Int) {
        Log.d("scrolledJobNumber","scrolledJobNumber: $position")
         jobDetailsFragment.changeJobNumber(position)

    }

    override fun onItemClicked(position: Int) {

        clickedPosition = position
        transitFragment(jobDetailsFragment, R.id.jobFragmentHolder,true)
    }


    override fun getJobList(): MutableList<JobListModelData>? {
        return jobList1
    }

    override fun setJobList(jobList: MutableList<JobListModelData>?) {
        jobList1 = jobList
        Log.d("setJobList","setJobList: ${jobList?.size}")
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


    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun totalJobCount(totalJobFound: Int?) {
        totalRecordsFound = totalJobFound
    }

    override fun getTotalJobCount(): Int? {
        return totalRecordsFound
    }


}
