package com.bdjobs.app.Employers

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.FollowEmployerListData
import com.bdjobs.app.API.ModelClasses.MessageDataModel
import com.bdjobs.app.Ads.Ads
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.InterviewInvitation.InterveiwInvitationListFragment
import com.bdjobs.app.InterviewInvitation.InterviewInvitationDetailsFragment
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.logDataForAnalytics
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.transitFragment
import kotlinx.android.synthetic.main.activity_employers_base.*
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


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

    lateinit var bdjobsDB: BdjobsDB
    lateinit var bdjobsUserSession: BdjobsUserSession

    var records: Int? = null

    override fun getTotalRecords(): Int? {
        return records
    }

    override fun setTotalRecords(value: Int?) {
        this.records = value
    }

    override fun setEmployerMessageList(employerMessageList: ArrayList<MessageDataModel>?) {
        this.employerMessageList = employerMessageList
    }

    override fun getEmployerMessageList(): ArrayList<MessageDataModel>? {
        return employerMessageList
    }

    override fun setFollowedListSize(value: Int?) {
        if (value != null) {
            followedListSize = value
        }
    }

    override fun getFollowedListSize(): Int? {
        return followedListSize
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
    private var employerMessageList: ArrayList<MessageDataModel>? = ArrayList()

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
    private var nId = ""
    private var seen = false

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

        Ads.loadAdaptiveBanner(this@EmployersBaseActivity, adView)


        bdjobsDB = BdjobsDB.getInstance(applicationContext)
        bdjobsUserSession = BdjobsUserSession(applicationContext)

        try {
            time = intent.getStringExtra("time") ?: "1"
        } catch (e: Exception) {
        }

        try {
            jobId = if (intent.getStringExtra("jobId") == null)  "" else intent.getStringExtra("jobId").toString()
        } catch (e: Exception) {
        }

        try {
            value = intent.getStringExtra("from").toString()

            //  value = "emplist"
        } catch (e: Exception) {
            logException(e)
        }

        try {
            companyid = intent.getStringExtra("companyid").toString()
        } catch (e: Exception) {
            logException(e)
        }

        try {
            companyname = intent.getStringExtra("companyname").toString()
        } catch (e: Exception) {
            logException(e)
        }

        try {
            nId = intent.getStringExtra("nid").toString()
        } catch (e: Exception) {
            logException(e)
        }

        try {
            seen = intent.getBooleanExtra("seen",false)
        } catch (e: Exception) {
            logException(e)
        }

        //   transitFragment(employerViewedMyResumeFragment, R.id.fragmentHolder)
        //Log.d("value", "value = $value")
        if (value?.equals("follow")) {
            transitFragment(followedEmployersListFragment, R.id.fragmentHolder)
        } else if (value?.equals("employer")) {
            transitFragment(employerListFragment, R.id.fragmentHolder)
        } else if (value?.equals("joblist")) {
            transitFragment(employerJobListFragment, R.id.fragmentHolder)
        } else if (value?.equals("vwdMyResume")) {
            transitFragment(employerViewedMyResumeFragment, R.id.fragmentHolder)
        } else if (value?.equals("notificationList")) {

            if (!seen) {
                try {
                    logDataForAnalytics(Constants.NOTIFICATION_TYPE_CV_VIEWED, applicationContext, jobId, nId)
                } catch (e: Exception) {
                }


                try {
                    ApiServiceJobs.create().sendDataForAnalytics(
                            userID = bdjobsUserSession.userId, decodeID = bdjobsUserSession.decodId, uniqueID = nId, notificationType = Constants.NOTIFICATION_TYPE_CV_VIEWED, encode = Constants.ENCODED_JOBS, sentTo = "Android"
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

            transitFragment(employerViewedMyResumeFragment, R.id.fragmentHolder)

        } else if (value?.equals("notification")) {

            if (!seen) {
                try {
                    logDataForAnalytics(Constants.NOTIFICATION_TYPE_CV_VIEWED, applicationContext, jobId, nId)
                } catch (e: Exception) {
                }

                try {
                    ApiServiceJobs.create().sendDataForAnalytics(
                            userID = bdjobsUserSession.userId, decodeID = bdjobsUserSession.decodId, uniqueID = nId, notificationType = Constants.NOTIFICATION_TYPE_CV_VIEWED, encode = Constants.ENCODED_JOBS, sentTo = "Android"
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
                bdjobsDB.notificationDao().updateNotificationTableByClickingNotification(Date(), true, nId, Constants.NOTIFICATION_TYPE_CV_VIEWED)
                val count = bdjobsDB.notificationDao().getNotificationCount()
                bdjobsUserSession = BdjobsUserSession(this@EmployersBaseActivity)
                bdjobsUserSession.updateNotificationCount(count)
                //Log.d("rakib", "noti count $count $jobId")
            }
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
