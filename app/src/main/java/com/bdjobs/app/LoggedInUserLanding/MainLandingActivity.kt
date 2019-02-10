package com.bdjobs.app.LoggedInUserLanding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.FileInfo
import com.bdjobs.app.API.ModelClasses.InviteCodeHomeModel
import com.bdjobs.app.API.ModelClasses.StatsModelClass
import com.bdjobs.app.API.ModelClasses.StatsModelClassData
import com.bdjobs.app.AppliedJobs.AppliedJobsActivity
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.InviteCodeInfo
import com.bdjobs.app.Employers.EmployersBaseActivity
import com.bdjobs.app.FavouriteSearch.FavouriteSearchBaseActivity
import com.bdjobs.app.InterviewInvitation.InterviewInvitationBaseActivity
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.SuggestiveSearch.SuggestiveSearchActivity
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.BdjobsUserRequestCode
import com.bdjobs.app.Utilities.Constants.Companion.key_from
import com.bdjobs.app.Utilities.Constants.Companion.key_typedData
import com.crashlytics.android.Crashlytics
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main_landing.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainLandingActivity : Activity(), HomeCommunicator {

    override fun getInviteCodepcOwnerID(): String? {
        return pcOwnerID
    }

    override fun getInviteCodeStatus(): String? {
       return inviteCodeStatus
    }

/*    override fun goToEmployerViewedMyResume(from: String) {
        startActivity<EmployersBaseActivity>("from" to from)
    }*/

    override fun getInviteCodeUserType(): String? {
        return inviteCodeuserType
    }

    private lateinit var bdjobsDB: BdjobsDB

    private val homeFragment = HomeFragment()
    private val hotJobsFragment = HotJobsFragment()
    private val hotJobsFragmentnew = HotJobsFragmentNew()
    private val moreFragment = MoreFragment()
    private val shortListedJobFragment = ShortListedJobFragment()
    private val mybdjobsFragment = MyBdjobsFragment()
    private lateinit var session: BdjobsUserSession
    private var lastMonthStats: List<StatsModelClassData?>? = null
    private var allTimeStats: List<StatsModelClassData?>? = null
    private var inviteCodeuserType: String? = null
    private var pcOwnerID: String? = null
    private var inviteCodeStatus: String? = null
    var cvUpload: String = "" // if this value = 0 or 4 then cv file is uploaded else not uploaded


    override fun isGetCvUploaded(): String {
        return cvUpload
    }

    override fun decrementCounter() {
        shortListedJobFragment.decrementCounter()
    }

    override fun scrollToUndoPosition(position: Int) {
        shortListedJobFragment.scrollToUndoPosition(position)
    }

    private var time: String = ""

    override fun goToEmployerViewedMyResume(from: String) {
        startActivity<EmployersBaseActivity>(
                "from" to from,
                "time" to time
        )
    }

    override fun setTime(time: String) {
        this.time = time
    }

    override fun goToAppliedJobs() {
        startActivity<AppliedJobsActivity>("time" to time)
    }

    override fun getLastStatsData(): List<StatsModelClassData?>? {
        return lastMonthStats
    }

    override fun getAllStatsData(): List<StatsModelClassData?>? {
        return allTimeStats
    }


    override fun goToInterviewInvitation(from: String) {
        startActivity<InterviewInvitationBaseActivity>("from" to from)
    }


    override fun backButtonClicked() {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (hotJobsFragment.getWebviewBacKStack()) {
            super.onBackPressed()
        }
    }


    override fun goToFollowedEmployerList(from: String) {
        startActivity<EmployersBaseActivity>(
                "from" to from,
                "time" to time
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_landing)
        bdjobsDB = BdjobsDB.getInstance(this@MainLandingActivity)
        session = BdjobsUserSession(applicationContext)
        Crashlytics.setUserIdentifier(session.userId)
        bottom_navigation?.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottom_navigation?.selectedItemId = R.id.navigation_home



        if (isBlueCollarUser()) {
            getInviteCodeInformation()
        }

        getStatsData("0")
        getStatsData("1")
        getIsCvUploaded()

        tetsLog()
    }

    private fun getInviteCodeInformation() {
        doAsync {
            val inviteCodeUserInfo = bdjobsDB.inviteCodeUserInfoDao().getInviteCodeInformation(session.userId!!)
            uiThread {

                if (inviteCodeUserInfo.isNullOrEmpty()) {

                    Log.d("inviteCodeUserInfo", "userID = ${session.userId},\n" +
                            "decodeID = ${session.decodId},\n" +
                            "mobileNumber = ${session.userName},\n" +
                            "catId = ${getBlueCollarUserId()},\n" +
                            "deviceID = ${getDeviceID()}")
                    updateInviteCodeOwnerInformation()

                } else {
                    inviteCodeuserType = inviteCodeUserInfo[0].userType
                    pcOwnerID = inviteCodeUserInfo[0].pcOwnerID
                    inviteCodeStatus = inviteCodeUserInfo[0].inviteCodeStatus


                    if(!inviteCodeuserType?.equalIgnoreCase("u")!!){
                        updateInviteCodeOwnerInformation()
                    }

                    Log.d("inviteCodeUserInfo", "pcOwnerID = $pcOwnerID")
                }
            }
        }
    }

    private fun updateInviteCodeOwnerInformation() {
        ApiServiceMyBdjobs.create().getInviteCodeUserOwnerInfo(
                userID = session.userId,
                decodeID = session.decodId,
                mobileNumber = session.userName,
                catId = getBlueCollarUserId().toString(),
                deviceID = getDeviceID()
        ).enqueue(object : Callback<InviteCodeHomeModel> {
                    override fun onFailure(call: Call<InviteCodeHomeModel>, t: Throwable) {
                        error("onFailure", t)
                    }

                    override fun onResponse(call: Call<InviteCodeHomeModel>, response: Response<InviteCodeHomeModel>) {

                        if (response.body()?.statuscode == Constants.api_request_result_code_ok) {

                            val inviteCodeInfo = InviteCodeInfo(
                                    userId = session.userId,
                                    userType = response.body()?.data?.get(0)?.userType,
                                    pcOwnerID = response.body()?.data?.get(0)?.pcOwnerID,
                                    inviteCodeStatus = response.body()?.data?.get(0)?.inviteCodeStatus
                            )
                            Log.d("inviteCodeUserInfo", "userID = ${session.userId},\n" +
                                    "userType = ${response.body()?.data?.get(0)?.userType},\n" +
                                    "pcOwnerID = ${response.body()?.data?.get(0)?.pcOwnerID},\n" +
                                    "inviteCodeStatus = ${response.body()?.data?.get(0)?.inviteCodeStatus}")

                            doAsync {
                                bdjobsDB.inviteCodeUserInfoDao().insertInviteCodeUserInformation(inviteCodeInfo)
                            }
                            inviteCodeuserType = inviteCodeInfo.userType
                            pcOwnerID = inviteCodeInfo.pcOwnerID
                            inviteCodeStatus = inviteCodeInfo.inviteCodeStatus
                        }
                    }
                })
    }

    override fun goToKeywordSuggestion() {
        val intent = Intent(this@MainLandingActivity, SuggestiveSearchActivity::class.java)
        intent.putExtra(Constants.key_from, Constants.key_jobtitleET)
        intent.putExtra(key_typedData, "")
        window.exitTransition = null
        startActivityForResult(intent, BdjobsUserRequestCode)
    }

    override fun goToFavSearchFilters() {
        startActivity<FavouriteSearchBaseActivity>()
    }

    override fun goToJoblistFromLastSearch() {
        startActivity<JobBaseActivity>("from" to "lastsearch")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BdjobsUserRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                val typedData = data?.getStringExtra(key_typedData)
                val from = data?.getStringExtra(key_from)
                startActivity<JobBaseActivity>(
                        Constants.key_jobtitleET to typedData)
            }
        }
    }

    override fun goToJobSearch(favID: String) {
        startActivity<JobBaseActivity>("from" to "favsearch", "filterid" to favID)
    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                transitFragment(homeFragment, R.id.landingPageFragmentHolderFL)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_shortlisted_jobs -> {
                transitFragment(shortListedJobFragment, R.id.landingPageFragmentHolderFL)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_hotjobs -> {
                transitFragment(hotJobsFragmentnew, R.id.landingPageFragmentHolderFL)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_mybdjobs -> {
                transitFragment(mybdjobsFragment, R.id.landingPageFragmentHolderFL)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_more -> {
                //  transitFragment(appliedJobsFragment, R.id.landingPageFragmentHolderFL)
                transitFragment(moreFragment, R.id.landingPageFragmentHolderFL)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    fun tetsLog() {

        Log.d("XZXfg", "\nisCvPosted = ${session.isCvPosted}\n" +
                "userPicUrl = ${session.userPicUrl}\n" +
                "name = ${session.fullName}\n" +
                "email = ${session.email}\n" +
                "userId = ${session.userId}\n" +
                "decodId = ${session.decodId}\n" +
                "userName = ${session.userName}\n" +
                "AppsDate = ${session.AppsDate}\n" +
                "age = ${session.age}\n" +
                "exp = ${session.exp}\n" +
                "catagoryId = ${session.catagoryId}\n" +
                "gender = ${session.gender}\n" +
                "resumeUpdateON = ${session.resumeUpdateON}\n" +
                "IsResumeUpdate = ${session.IsResumeUpdate}\n" +
                "trainingId = ${session.trainingId}\n")
    }

    override fun shortListedClicked(Position: Int) {
        startActivity<JobBaseActivity>("from" to "shortListedJob", "position" to Position)

    }

    private fun getStatsData(activityDate: String) {
        ApiServiceMyBdjobs.create().mybdjobStats(
                userId = session.userId,
                decodeId = session.decodId,
                isActivityDate = activityDate,
                trainingId = session.trainingId,
                isResumeUpdate = session.IsResumeUpdate

        )
                .enqueue(object : Callback<StatsModelClass> {
                    override fun onFailure(call: Call<StatsModelClass>, t: Throwable) {
                        toast("${t.message}")
                    }

                    override fun onResponse(call: Call<StatsModelClass>, response: Response<StatsModelClass>) {

                        try {
                            if (activityDate == "0") {
                                allTimeStats = response.body()?.data
                            } else if (activityDate == "1") {
                                lastMonthStats = response.body()?.data
                            }

                            Log.d("respp", "$allTimeStats /n $lastMonthStats")
                        } catch (e: Exception) {
                            logException(e)
                        }
                    }

                })
    }

    private fun getIsCvUploaded() {
        ApiServiceMyBdjobs.create().getCvFileAvailable(
                userID = session.userId,
                decodeID = session.decodId

        ).enqueue(object : Callback<FileInfo> {
            override fun onFailure(call: Call<FileInfo>, t: Throwable) {
                error("onFailure", t)
                toast("${t.toString()}")
            }

            override fun onResponse(call: Call<FileInfo>, response: Response<FileInfo>) {
                //toast("${response.body()?.statuscode}")
                if (response.isSuccessful){
                    cvUpload = response.body()?.statuscode!!
                    Log.d("value", "val " + cvUpload)

                }
            }

        })

    }

}
