package com.bdjobs.app.Employers

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.bdjobs.app.API.ModelClasses.FollowEmployerListData
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.transitFragment


/*=============================================================================
 |
 |Author :  Firoz Hasan
 |Date : 22 Dec 2018
 |BASIC IDEA : IN ONE MODULE/PACKAGE THERE WILL BE ONE BASE ACTIVITY
 |             WHICH GONNA CONTROL ALL DIFFERENT FRAGMETS. IN THIS CASE WE HAVE
 |             FOLLOWEDEMPLOYERLISTFRAGMENT AND EMPLOYERSCOMMUNICATOR(INTERFACE)
 |             WHICH WILL BE USE AS A BRIDGE BETWEEN activity and fragments
 |Purpose: In FollowedEmployersListFragment we instantiate
 |         BdjobsDB (roomdb) to get followedemployerlist then send that lists
 |         to adapter and performs unfollow operation by deleting it
 |         fm roomdb and calling rest api.
 |
 |
 |
 |
 |
 |
 |
 |
 |
 +-----------------------------------------------------------------------------
 */
class EmployersBaseActivity : Activity(), EmployersCommunicator {
    override fun setFollowedListSize(value: Int?) {
        if (value != null) {
            followedListSize = value
        }
    }

    override fun getFollowedListSize(): Int? {
       return  followedListSize
    }

    override fun setCurrentPage(value: Int?) {
        if (value != null) {
            currentPage = value
        }
    }

    override fun setTotalPage(value: Int?) {
        if (value != null) {
            TOTAL_PAGES = value
        }
    }

    override fun setIsloading(value: Boolean?) {
        if (value != null) {
            isLoadings = value
        }
    }

    override fun setIsLastPage(value: Boolean?) {
        if (value != null) {
            isLastPages = value
        }
    }

    override fun getCurrentPage(): Int? {
        return currentPage
    }

    override fun getTotalPage(): Int? {
        return TOTAL_PAGES
    }

    override fun getIsloading(): Boolean? {
        return isLoadings
    }

    override fun getIsLastPage(): Boolean? {
        return isLastPages
    }

    private var currentPage = 1
    private var TOTAL_PAGES: Int? = 1
    private var isLoadings = false
    private var isLastPages = false
    private var followedListSize = 0


    private var followedEmployerList: ArrayList<FollowEmployerListData>? = ArrayList()


    override fun setFollowedEmployerList(empList: ArrayList<FollowEmployerListData>?) {
        followedEmployerList = empList
    }

    override fun getFollowedEmployerList(): ArrayList<FollowEmployerListData>? {
        return followedEmployerList
    }

    override fun positionClicked(position: Int?) {
        this.positionClicked = position
    }

    override fun getPositionClicked(): Int? {
        return this.positionClicked
    }

    private var positionClicked: Int? = 0
    private var companyid = ""
    private var companyname = ""
    private var value = ""
    private val followedEmployersListFragment = FollowedEmployersListFragment()
    private val employerJobListFragment = EmployerJobListFragment()
    private val employerListFragment = EmployerListFragment()
    private val employerViewedMyResumeFragment = EmployerViewedMyResumeFragment()
    private val employerMessageListFragment = EmployerMessageListFragment()
    private val employerMessageDetailFragment = EmployerMessageDetailFragment()
    private var jobId = ""
    private var time = ""

    private var messageId = ""

    override fun getTime(): String {
        return time
    }

    override fun gotoJobListFragment(companyID: String?, companyName: String?) {
        companyid = companyID!!
        companyname = companyName!!

        transitFragment(employerJobListFragment, R.id.fragmentHolder, true)
    }

    override fun getCompanyID(): String {
        return companyid
    }

    override fun getCompanyName(): String {
        return companyname
    }


    override fun backButtonPressed() {
        onBackPressed()
    }

    override fun getJobId(): String {
        return jobId
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employers_base)

        try {
            time = intent.getStringExtra("time")
        } catch (e: Exception) {
        }

        try {
            jobId = intent.getStringExtra("jobId")
        } catch (e: Exception) {
        }

        try {
            value = intent.getStringExtra("from")
            //  value = "emplist"
        } catch (e: Exception) {
            logException(e)
        }

        try {
            companyid = intent.getStringExtra("companyid")
        } catch (e: Exception) {
            logException(e)
        }

        try {
            companyname = intent.getStringExtra("companyname")
        } catch (e: Exception) {
            logException(e)
        }

        //   transitFragment(employerViewedMyResumeFragment, R.id.fragmentHolder)
        Log.d("value", "value = $value")
        if (value?.equals("follow")) {
            transitFragment(followedEmployersListFragment, R.id.fragmentHolder)
        } else if (value?.equals("employer")) {
            transitFragment(employerListFragment, R.id.fragmentHolder)
        } else if (value?.equals("joblist")) {
            transitFragment(employerJobListFragment, R.id.fragmentHolder)
        } else if (value?.equals("vwdMyResume")) {
            transitFragment(employerViewedMyResumeFragment, R.id.fragmentHolder)
        } else if (value?.equals("employerMessageList")) {
            transitFragment(employerMessageListFragment, R.id.fragmentHolder)
        }

    }


    override fun scrollToUndoPosition(position: Int) {
        if (position >= 0)
            followedEmployersListFragment.scrollToUndoPosition(position)
    }

    override fun decrementCounter(position: Int) {
        followedEmployersListFragment.decrementCounter(position)
    }


    override fun setMessageId(messageId: String) {

        this.messageId = messageId

    }

    override fun getMessageId(): String {
        return messageId
    }

    override fun gotoMessageDetail() {
        transitFragment(employerMessageDetailFragment, R.id.fragmentHolder, true)
    }


}
