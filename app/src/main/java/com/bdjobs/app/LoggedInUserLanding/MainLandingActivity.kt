package com.bdjobs.app.LoggedInUserLanding

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.FollowEmployerListData
import com.bdjobs.app.API.ModelClasses.InviteCodeHomeModel
import com.bdjobs.app.API.ModelClasses.InviteCodeUserStatusModel
import com.bdjobs.app.API.ModelClasses.StatsModelClassData
import com.bdjobs.app.ads.Ads
import com.bdjobs.app.AppliedJobs.AppliedJobsActivity
import com.bdjobs.app.BroadCastReceivers.BackgroundJobBroadcastReceiver
import com.bdjobs.app.BroadCastReceivers.MorningNotificationReceiver
import com.bdjobs.app.BroadCastReceivers.NightNotificationReceiver
import com.bdjobs.app.Employers.EmployerJobListFragment
import com.bdjobs.app.Employers.EmployersBaseActivity
import com.bdjobs.app.FavouriteSearch.FavouriteSearchBaseActivity
import com.bdjobs.app.InterviewInvitation.InterviewInvitationBaseActivity
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.myJobs.FollowedEmployersFragment
import com.bdjobs.app.myJobs.MyJobsFragment
import com.bdjobs.app.manageResume.ManageResumeActivity
import com.bdjobs.app.Notification.Models.CommonNotificationModel
import com.bdjobs.app.Notification.NotificationBaseActivity
import com.bdjobs.app.Notification.NotificationHelper
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.SuggestiveSearch.SuggestiveSearchActivity
import com.bdjobs.app.utilities.*
import com.bdjobs.app.utilities.Constants.Companion.BdjobsUserRequestCode
import com.bdjobs.app.utilities.Constants.Companion.isDeviceInfromationSent
import com.bdjobs.app.utilities.Constants.Companion.key_typedData
import com.bdjobs.app.utilities.Constants.Companion.sendDeviceInformation
import com.bdjobs.app.Web.WebActivity
import com.bdjobs.app.ajkerDeal.ui.home.page_home.HomeNewFragment
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.databases.internal.InviteCodeInfo
import com.bdjobs.app.databases.internal.Notification
import com.bdjobs.app.editResume.EditResLandingActivity
import com.bdjobs.app.editResume.PhotoUploadActivity
import com.bdjobs.app.editResume.educationInfo.AcademicBaseActivity
import com.bdjobs.app.editResume.otherInfo.OtherInfoBaseActivity
import com.bdjobs.app.editResume.personalInfo.PersonalInfoActivity
import com.bdjobs.app.liveInterview.LiveInterviewActivity
import com.bdjobs.app.resume_dashboard.ResumeDashboardBaseActivity
import com.bdjobs.app.videoInterview.VideoInterviewActivity
import com.bdjobs.app.videoResume.VideoResumeActivity
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main_landing.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainLandingActivity : AppCompatActivity(), HomeCommunicator,
    BackgroundJobBroadcastReceiver.NotificationUpdateListener {

    override fun goToAjkerDealLive(containerId:Int) {
        try {
            transitFragmentX(HomeNewFragment(),containerId,false)
        } catch (e: Exception) {
        }

    }

    override fun onUpdateNotification() {
        BdjobsUserSession(this@MainLandingActivity).let {
            val count = it.notificationCount
            homeFragment.updateNotificationView(count)
            homeFragment.updateMessageView(it.messageCount)
            hotJobsFragmentnew.updateNotificationView(count)
            hotJobsFragmentnew.updateMessageView(it.messageCount)
            myJobsFragment.updateNotificationView(count)
            myJobsFragment.updateMessageView(it.messageCount)
            mybdjobsFragment.updateNotificationView(count)
            mybdjobsFragment.updateMessageView(it.messageCount)
            moreFragment.updateNotificationView(count)
            moreFragment.updateMessageView(it.messageCount)

            homeFragment.updateInvitationCountView()
        }
    }

    override fun showManageResumePopup() {
        val dialog = Dialog(this@MainLandingActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.layout_manage_resume_pop_up)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val editResume = dialog.findViewById<Button>(R.id.editResume)
        val viewResume = dialog.findViewById<Button>(R.id.viewResume)
        val uploadResume = dialog.findViewById<Button>(R.id.uploadResume)
        val videoResume = dialog.findViewById<Button>(R.id.videoResume)
        val cancelIV = dialog.findViewById<ImageView>(R.id.deleteIV)

        val ad_small_template = dialog.findViewById<TemplateView>(R.id.ad_small_template)

        Ads.showNativeAd(ad_small_template, this@MainLandingActivity)

        editResume?.setOnClickListener {
            startActivity<EditResLandingActivity>()
        }
        viewResume?.setOnClickListener {
            if (!session.isCvPosted?.equalIgnoreCase("true")!!) {
                try {
                    val alertd = alert("To Access this feature please post your resume") {
                        title = "Your resume is not posted!"
                        positiveButton("Post Resume") { startActivity<EditResLandingActivity>() }
                        negativeButton("Cancel") { dd ->
                            dd.dismiss()
                        }
                    }
                    alertd.isCancelable = false
                    alertd.show()
                } catch (e: Exception) {
                    logException(e)
                }
            } else {
                try {
                    val str1 = random()
                    val str2 = random()
                    val id = str1 + session.userId + session.decodId + str2
                    startActivity<WebActivity>(
                        "url" to "https://mybdjobs.bdjobs.com/mybdjobs/masterview_for_apps.asp?id=$id",
                        "from" to "cvview"
                    )
                } catch (e: Exception) {
                    logException(e)
                }
            }
        }
        uploadResume?.setOnClickListener {
            startActivity<ManageResumeActivity>(
                "from" to "uploadResume"
            )
        }
        cancelIV?.setOnClickListener {
            dialog.dismiss()
        }

        videoResume?.setOnClickListener {
            startActivity<VideoResumeActivity>()
        }


        dialog.show()
    }

    private fun random(): String {
        val chars = "abcdefghijklmnopqrstuvwxyz12345678910".toCharArray()
        val sb = StringBuilder()
        val random = Random()
        for (i in 0..4) {
            val c = chars[random.nextInt(chars.size)]
            sb.append(c)
        }
        val output = sb.toString()
        println(output)
        return output
    }


    override fun gotoJobSearch() {
        startActivity<JobBaseActivity>("from" to "generalsearch")
    }

    override fun gotoAllJobSearch() {
        startActivity<JobBaseActivity>("from" to "alljobsearch")
    }

    override fun gotoEditresume() {
        startActivity<EditResLandingActivity>()
    }

    override fun gotoTimesEmailedResume(times_last: Boolean) {
        startActivity<ManageResumeActivity>(
            "from" to "timesEmailedResume",
            "time_last" to times_last

        )
    }


    override fun setShortListFilter(filter: String) {
        this.shortListFilter = filter
    }

    override fun getShortListFilter(): String {
        return shortListFilter
    }

    private var shortListFilter: String = ""


    override fun goToShortListedFragment(deadline: Int) {
        bottom_navigation?.selectedItemId = R.id.navigation_shortlisted_jobs
    }

    override fun getInviteCodepcOwnerID(): String? {
        return pcOwnerID
    }

    override fun getInviteCodeStatus(): String? {
        return inviteCodeStatus
    }

    override fun getInviteCodeUserType(): String? {
        return inviteCodeuserType
    }

    private lateinit var bdjobsDB: BdjobsDB
    private lateinit var broadcastReceiver: BackgroundJobBroadcastReceiver
    private val intentFilter = IntentFilter(Constants.BROADCAST_DATABASE_UPDATE_JOB)

    private val homeFragment = HomeFragment()
    private val hotJobsFragmentnew = HotJobsFragmentNew()
    private val moreFragment = MoreFragment()
    private val shortListedJobFragment = ShortListedJobFragment()
    private val myJobsFragment = MyJobsFragment()
    private val mybdjobsFragment = MyBdjobsFragment()
    private lateinit var session: BdjobsUserSession
    private var lastMonthStats: List<StatsModelClassData?>? = null
    private var allTimeStats: List<StatsModelClassData?>? = null
    private var inviteCodeuserType: String? = null
    private var pcOwnerID: String? = null
    private var inviteCodeStatus: String? = null
    var cvUpload: String = "" // if this value = 0 or 4 then cv file is uploaded else not uploaded
    private lateinit var mNotificationHelper: NotificationHelper

    private var time: String = ""
    private var totalJobCount:Int = 0

    private var companyId = ""
    private var companyname = ""
    private var positionClicked: Int? = 0
    private val employerJobListFragment = EmployerJobListFragment()
    private val followedEmployersFragment = FollowedEmployersFragment()
    private var followedEmployerList: ArrayList<FollowEmployerListData>? = ArrayList()
    private var totalFollowedEmployersCount:Int = 0
    private var totalFavSearchCount:Int = 0


    override fun isGetCvUploaded(): String {
        return cvUpload
    }

    override fun decrementCounter() {
        shortListedJobFragment.decrementCounter(totalJobCount)
    }

    override fun scrollToUndoPosition(position: Int) {
        shortListedJobFragment.scrollToUndoPosition(position)
    }


    override fun goToEmployerViewedMyResume(from: String) {
        startActivity<EmployersBaseActivity>(
            "from" to from,
            "time" to time
        )
    }

    override fun setTime(time: String) {
        this.time = time
    }

    override fun getTime(): String {
        return time
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
        startActivity<InterviewInvitationBaseActivity>("from" to from, "time" to time)
    }

    override fun goToVideoInvitation(from: String) {
        startActivity<VideoInterviewActivity>("from" to from, "time" to time)
    }

    override fun goToLiveInvitation(from: String) {
        startActivity<LiveInterviewActivity>("from" to from, "time" to time)
    }

    override fun backButtonClicked() {
        onBackPressed()
    }

    override fun onBackPressed() {

        if (bottom_navigation.selectedItemId == R.id.navigation_home) {
            val exitDialog = Dialog(this@MainLandingActivity)
            exitDialog.setContentView(R.layout.dialog_exit_layout)
            exitDialog.setCancelable(true)
            exitDialog.show()
            val yesBtn = exitDialog.findViewById(R.id.onlineApplyOkBTN) as Button
            val noBtn = exitDialog.findViewById(R.id.onlineApplyCancelBTN) as Button
            val adSmallTemplate = exitDialog.findViewById<TemplateView>(R.id.ad_small_template)
            Ads.showNativeAd(adSmallTemplate, this)

            yesBtn.setOnClickListener {
                try {
                    exitDialog.dismiss()
                    if (Ads.mInterstitialAd != null && Ads.mInterstitialAd?.isLoaded!!) {
                        Ads.mInterstitialAd?.show()
                    } else {
                        super.onBackPressed()
                    }
                } catch (e: Exception) {
                }
            }

            noBtn.setOnClickListener {
                exitDialog.dismiss()
            }
        } else {
            bottom_navigation.selectedItemId = R.id.navigation_home
        }



    }


    override fun goToFollowedEmployerList(from: String) {

        if (from=="employer") {
            startActivity<EmployersBaseActivity>(
                "from" to from,
                "time" to time
            )
        } else {
            transitFragmentX(myJobsFragment,R.id.landingPageFragmentHolderFL,false, bundleOf("from" to from))
            bottom_navigation?.selectedItemId = R.id.navigation_shortlisted_jobs
        }



    }

    override fun goToResumeManager() {
        startActivity<ResumeDashboardBaseActivity>()
//        startActivity<ResumeManagerActivity>()

    }

    override fun getTotalShortlistedJobCounter(): Int {
        return totalJobCount
    }

    override fun setTotalShortlistedJobCounter(count: Int) {
        this.totalJobCount = count
    }

    override fun gotoJobListFragment(companyID: String?, companyName: String?) {
        companyId = companyID!!
        companyname = companyName!!

        startActivity(Intent(this,EmployersBaseActivity::class.java)
            .putExtra("from","joblist")
            .putExtra("companyid",companyID)
            .putExtra("companyname",companyName)
        )
    }

    override fun positionClicked(position: Int?) {
        this.positionClicked = position
    }

    override fun getPositionClicked(): Int? {
        return this.positionClicked
    }

    override fun decrementCounterFollowedEmp(position: Int) {
//        followedEmployersFragment.decrementCounter(position)
    }

    override fun setFollowedEmployerList(empList: java.util.ArrayList<FollowEmployerListData>?) {
        this.followedEmployerList=empList
    }

    override fun getFollowedEmployerList(): java.util.ArrayList<FollowEmployerListData>? {
        return  followedEmployerList
    }

    override fun getCompanyID(): String {
        return companyId
    }

    override fun getCompanyName(): String {
        return companyname
    }

    override fun getTotalFollowedEmployersCount(): Int {
        return totalFollowedEmployersCount
    }

    override fun setTotalFollowedEmployersCount(count: Int) {
        this.totalFollowedEmployersCount = count
    }

    override fun getTotalFavouriteSearchCount(): Int {
        return totalFavSearchCount
    }

    override fun setTotalFavouriteSearchCount(count: Int) {
        this.totalFavSearchCount = count
    }

    override fun goToEditMode(favID: String) {
        startActivity(Intent(this,FavouriteSearchBaseActivity::class.java)
            .putExtra("from","MyJobs")
            .putExtra("favID",favID)
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_landing)
        bdjobsDB = BdjobsDB.getInstance(this@MainLandingActivity)

        scheduleMorningNotification()
        scheduleNightNotification()

        broadcastReceiver = BackgroundJobBroadcastReceiver()
        mNotificationHelper = NotificationHelper(this)
        session = BdjobsUserSession(applicationContext)
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.setUserId(session.userId.toString())
        bottom_navigation?.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottom_navigation?.selectedItemId = R.id.navigation_home

        if (intent.getStringExtra("from") == "notification") {
            Timber.d("came here ")
            setShortListFilter("Next 2 days")
            goToShortListedFragment(2)
        }

        try {
            createShortcut(this@MainLandingActivity)
        } catch (e: Exception) {
        }

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
            })

        loadAd()


        if (!isDeviceInfromationSent) {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult ->
                val token = instanceIdResult.token
                sendDeviceInformation(token, this@MainLandingActivity)
            }
        }


        if (isBlueCollarUser()) {
            getInviteCodeInformation()
            getUserStatus(
                userId = session.userId!!,
                decodeId = session.decodId!!,
                invitedUserId = session.userId!!
            )
        }

//        insertTempMessage()
    }

    /**
     * this fun is just for testing purpose
     */
    @SuppressLint("SimpleDateFormat")
    private fun insertTempMessage() {
        doAsync {

            val model = CommonNotificationModel(
                title = "??????????????? ????????????????????? (Video Resume)",
                pId = "1575873",
//                body = "Soumik",
                type = "pm",
                link = "https://mybdjobs.bdjobs.com/mybdjobs/videoResume/video_resume_home.asp",
                activityNode = "com.bdjobs.app.videoResume.VideoResumeActivity",
                notificationId = "65",
                LogoSrc = "https://bdjobs.com/NotificationMessageimages/videoresumeslogo.png",
                imgSrc = "https://bdjobs.com/NotificationMessageimages/videoresumesbanner_ver2.jpg",
                imageLink = "https://bdjobs.com/NotificationMessageimages/videoresumesbanner_ver2.jpg"
            )

            val model2 = CommonNotificationModel(
                title = "Hello",
                body = "Soumik",
                link = "https://www.bdjobs.com/"
            )

            val model3 = CommonNotificationModel(
                title = "Hello",
                body = "Soumik",
                link = "https://www.bdjobs.com/",
                activityNode = "com.bdjobs.app.videoResume.VideoResume"
            )

            val model4 = CommonNotificationModel(
                title = "Hello",
                body = "Soumik",
                activityNode = "com.bdjobs.app.videoResume.VideoResumeActivity"
            )

            val model5 = CommonNotificationModel(
                title = "Hello",
                body = "Soumik"
            )

            val list = bdjobsDB.notificationDao().getMessages("pm")
            val timeList = ArrayList<String>()
            val simpleDateFormat = SimpleDateFormat("HH:mm")

            for (i in list.indices) {
                Timber.d("Time: ${list[i].arrivalTime?.time}")

                val formattedTime = simpleDateFormat.format(list[i].arrivalTime?.time)

                timeList.add(formattedTime)

                Timber.d("Formatted time: $formattedTime")
            }

            Timber.d("TimeList size: ${timeList.size} .. TimeList: ${Gson().toJson(timeList)}")

            for (i in 0 until 5) {
                Timber.d("Date: ${Date()}")
                val time = simpleDateFormat.format(Date())
                Timber.d("Time: $time")
                if (time !in timeList) {
                    bdjobsDB.notificationDao().insertNotification(
                        Notification(
                            title = model.title,
                            body = model.body,
                            type = model.type,
                            imageLink = model.imageLink,
                            link = model.link,
                            notificationId = model.notificationId,
                            arrivalTime = Date(),
                            payload = Gson().toJson(model).replace("\\n", "\n")
                        )
                    )

//                    bdjobsDB.notificationDao().insertNotification(
//                        Notification(
//                            jobTitle="dfjk", imageLink="", companyName="Bdjobs Test Account - Az",
//                            link="https://mybdjobs.bdjobs.com/mybdjobs/invite-interview-detail.asp?nstatus=1&Notification=6773237&id=954216",
//                         type="li", title="Interview Invitation", body =  "Bdjobs Test Account - Az had sent you a Live Interview schedule. Be sure to take part in the interview." ,
//                            notificationId="6773237", serverId ="954216",
//                            arrivalTime = Date(),)
//                    )
//
//                    bdjobsDB.notificationDao().insertNotification(
//                        Notification(
//                            jobTitle="peon", imageLink="", companyName="Utopia Test Company",
//                            link="https://mybdjobs.bdjobs.com/mybdjobs/invite-interview-detail.asp?nstatus=1&Notification=6773237&id=954216",
//                            type="vi", title="Interview Invitation", body = "Utopia Test Company had sent you a Video Interview invitation. Be sure to take part in the interview."
//                            ,notificationId="6773237", serverId ="833906",
//                            arrivalTime = Date(),)
//                    )

//                    bdjobsDB.notificationDao().insertNotification(
//                        Notification(
//                            jobTitle="peon", imageLink="", companyName="Bdjobs Test Account - Az had sent you a Live Interview schedule. Be sure to take part in the interview.",
//                            link="https://mybdjobs.bdjobs.com/mybdjobs/invite-interview-detail.asp?nstatus=1&Notification=6773237&id=954216",
//                            type="li", title="Interview Invitation", notificationId="6773237", serverId ="954216",
//                            arrivalTime = Date(),)
//                    )

                    timeList.add(time)
                }

            }


//            bdjobsDB.notificationDao().insertNotification(
//                    Notification(
//                            title = "Test2",
//                            body = "This is a test notification",
//                            type = "pm",
//                            imageLink = "https://picsum.photos/seed/picsum/200/300",
//                            link = "www.google.com",
//                            notificationId = "002",
//                            arrivalTime = Date()
//                    )
//            )
//
//            bdjobsDB.notificationDao().insertNotification(
//                    Notification(
//                            title = "Test3",
//                            body = "This is a test notification",
//                            type = "pm",
//                            imageLink = "https://picsum.photos/seed/picsum/200/300",
//                            link = "www.google.com",
//                            notificationId = "003",
//                            arrivalTime = Date(),
//                            payload = Gson().toJson(model2).replace("\\n", "\n")
//                    )
//            )
//
//            bdjobsDB.notificationDao().insertNotification(
//                    Notification(
//                            title = "Test4",
//                            body = "This is a test notification",
//                            type = "pm",
//                            imageLink = "https://picsum.photos/seed/picsum/200/300",
//                            link = "www.google.com",
//                            notificationId = "004",
//                            arrivalTime = Date(),
//                            payload = Gson().toJson(model3).replace("\\n", "\n")
//
//                    )
//            )
//
//            bdjobsDB.notificationDao().insertNotification(
//                    Notification(
//                            title = "Test5",
//                            body = "This is a test notification",
//                            type = "pm",
//                            imageLink = "https://picsum.photos/seed/picsum/200/300",
//                            link = "www.google.com",
//                            notificationId = "005",
//                            arrivalTime = Date(),
//                            payload = Gson().toJson(model4).replace("\\n", "\n")
//
//                    )
//            )
//
//            bdjobsDB.notificationDao().insertNotification(
//                    Notification(
//                            title = "Test6",
//                            body = "This is a test notification",
//                            type = "pm",
//                            imageLink = "https://picsum.photos/seed/picsum/200/300",
//                            link = "www.google.com",
//                            notificationId = "006",
//                            payload = Gson().toJson(model5).replace("\\n", "\n")
//
//                    )
//            )
//
//            bdjobsDB.notificationDao().insertNotification(
//                    Notification(
//                            title = "Test7",
//                            body = "This is a test notification",
//                            type = "pm",
//                            imageLink = "https://picsum.photos/seed/picsum/200/300",
//                            notificationId = "004",
//                            payload = Gson().toJson(model5).replace("\\n", "\n")
//
//                    )
//            )

            BdjobsUserSession(this@MainLandingActivity).updateMessageCount(
                bdjobsDB.notificationDao().getMessageCount()
            )
        }
    }

    private fun loadAd() {
        try {
            Ads.mInterstitialAd = InterstitialAd(this@MainLandingActivity)
            Ads.mInterstitialAd!!.adUnitId = Constants.INTERSTITIAL_AD_UNIT_ID
            Ads.mInterstitialAd!!.loadAd(AdRequest.Builder().build())
            Ads.mInterstitialAd!!.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    //Log.d("mInterstitialAd", "Ad Loaded")
                }

                override fun onAdFailedToLoad(errorCode: Int) {
                    // Code to be executed when an ad request fails.
                }

                override fun onAdOpened() {
                    // Code to be executed when the ad is displayed.
                }

                override fun onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                override fun onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                override fun onAdClosed() {
                    session.isExpirationMessageShown = false
                    finishAffinity()
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun getInviteCodeInformation() {
        doAsync {
            val inviteCodeUserInfo =
                bdjobsDB.inviteCodeUserInfoDao().getInviteCodeInformation(session.userId!!)
            uiThread {

                if (inviteCodeUserInfo.isNullOrEmpty()) {
                    updateInviteCodeOwnerInformation()

                } else {
                    inviteCodeuserType = inviteCodeUserInfo[0].userType
                    pcOwnerID = inviteCodeUserInfo[0].pcOwnerID
                    inviteCodeStatus = inviteCodeUserInfo[0].inviteCodeStatus


                    if (!inviteCodeuserType?.equalIgnoreCase("u")!!) {
                        updateInviteCodeOwnerInformation()
                    }
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

            override fun onResponse(
                call: Call<InviteCodeHomeModel>,
                response: Response<InviteCodeHomeModel>
            ) {

                if (response.body()?.statuscode == Constants.api_request_result_code_ok) {

                    val inviteCodeInfo = InviteCodeInfo(
                        userId = session.userId,
                        userType = response.body()?.data?.get(0)?.userType,
                        pcOwnerID = response.body()?.data?.get(0)?.pcOwnerID,
                        inviteCodeStatus = response.body()?.data?.get(0)?.inviteCodeStatus
                    )

                    doAsync {
                        bdjobsDB.inviteCodeUserInfoDao()
                            .insertInviteCodeUserInformation(inviteCodeInfo)
                    }
                    inviteCodeuserType = inviteCodeInfo.userType
                    pcOwnerID = inviteCodeInfo.pcOwnerID
                    inviteCodeStatus = inviteCodeInfo.inviteCodeStatus
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiver, intentFilter)
        BackgroundJobBroadcastReceiver.notificationUpdateListener = this
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    override fun goToKeywordSuggestion() {
        val intent = Intent(this@MainLandingActivity, SuggestiveSearchActivity::class.java)
        intent.putExtra(Constants.key_from, Constants.key_jobtitleET)
        intent.putExtra(key_typedData, "")
        window.exitTransition = null
        startActivityForResult(intent, BdjobsUserRequestCode)
    }

    override fun goToNotifications() {
        startActivity<NotificationBaseActivity>()
    }

    override fun goToMessages() {
        startActivity<NotificationBaseActivity>(
            "from" to "message"
        )
    }

    override fun goToFavSearchFilters() {
//        startActivity<FavouriteSearchBaseActivity>()
//        transitFragmentX(myJobsFragment, R.id.landingPageFragmentHolderFL, false)
//        replaceFragment(R.id.landingPageFragmentHolderFL,myJobsFragment, bundleOf("from" to "favSearch"))
        transitFragmentX(myJobsFragment,R.id.landingPageFragmentHolderFL,false, bundleOf("from" to "favSearch"))
        bottom_navigation?.selectedItemId = R.id.navigation_shortlisted_jobs
    }

    override fun goToJoblistFromLastSearch() {
        startActivity<JobBaseActivity>("from" to "lastsearch")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BdjobsUserRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                val typedData = data?.getStringExtra(key_typedData)
                startActivity<JobBaseActivity>(
                    Constants.key_jobtitleET to typedData
                )
            }
        }
    }

    override fun goToJobSearch(favID: String) {
        startActivity<JobBaseActivity>("from" to "favsearch", "filterid" to favID)
    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                transitFragmentX(homeFragment, R.id.landingPageFragmentHolderFL, false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_shortlisted_jobs -> {
                transitFragmentX(myJobsFragment, R.id.landingPageFragmentHolderFL, false)
//                transitFragment(myJobsFragment,R.id.landingPageFragmentHolderFL,false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_hotjobs -> {
                transitFragmentX(hotJobsFragmentnew, R.id.landingPageFragmentHolderFL, false)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_mybdjobs -> {
                transitFragmentX(mybdjobsFragment, R.id.landingPageFragmentHolderFL, false)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_more -> {
                transitFragmentX(moreFragment, R.id.landingPageFragmentHolderFL, false)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun shortListedClicked(
        jobids: ArrayList<String>,
        lns: ArrayList<String>,
        deadline: ArrayList<String>
    ) {
        startActivity<JobBaseActivity>(
            "from" to "employer",
            "jobids" to jobids,
            "lns" to lns,
            "position" to 0,
            "deadline" to deadline
        )
    }


    private fun getUserStatus(userId: String, decodeId: String, invitedUserId: String) {

        ApiServiceMyBdjobs.create().getInviteCodeUserStatus(
            userID = userId,
            decodeID = decodeId,
            invited_user_id = invitedUserId
        ).enqueue(
            object : Callback<InviteCodeUserStatusModel> {
                override fun onFailure(call: Call<InviteCodeUserStatusModel>, t: Throwable) {
                    error("onFailure", t)
                }

                override fun onResponse(
                    call: Call<InviteCodeUserStatusModel>,
                    response: Response<InviteCodeUserStatusModel>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val photoInfo = response.body()!!.data[0].photoInfo
                            val educationInfo = response.body()!!.data[0].educationInfo
                            val personalInfo = response.body()!!.data[0].personalInfo
                            val skills = response.body()!!.data[0].skills

                            if (photoInfo.equalIgnoreCase("True") &&
                                educationInfo.equalIgnoreCase("True") &&
                                personalInfo.equalIgnoreCase("True") &&
                                skills.equalIgnoreCase("True")
                            ) else {
                                showCategoryDialog(
                                    response.body()!!.data[0].name,
                                    response.body()!!.data[0].category,
                                    response.body()!!.data[0].photoUrl,
                                    personalInfo,
                                    educationInfo,
                                    photoInfo,
                                    response.body()!!.data[0].createdDate,
                                    skills
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        )

    }

    private fun showCategoryDialog(
        name: String,
        category: String,
        photoUrl: String,
        personalInfo: String,
        educationInfo: String,
        photoInfo: String,
        createdDate: String,
        skillInfo: String
    ) {

        val dialog = Dialog(this@MainLandingActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.invite_user_details_layout)


        val nameTV = dialog.findViewById(R.id.nameTV) as TextView
        val categoryTv = dialog.findViewById(R.id.categoryTv) as TextView
        val categoryDateTv = dialog.findViewById(R.id.categoryDateTv) as TextView
        val profilePicIV = dialog.findViewById(R.id.profilePicIMGV) as ImageView
        val personalImageView = dialog.findViewById(R.id.personalIMGV) as ImageView
        val educationaImageView = dialog.findViewById(R.id.educationIMGV) as ImageView
        val skillIMGV = dialog.findViewById(R.id.skillIMGV) as ImageView
        val photInfoImageView = dialog.findViewById(R.id.pictureIMGV) as ImageView
        val cancelIconImgv = dialog.findViewById(R.id.cancelIconImgv) as ImageView


        nameTV.text = name
        categoryTv.text = category
        categoryDateTv.text = createdDate.toBanglaDigit()

        profilePicIV.loadCircularImageFromUrl(photoUrl)

        if (personalInfo.equals("True", ignoreCase = true)) {
            personalImageView.setBackgroundResource(R.drawable.acount_right_icon)
        } else {
            personalImageView.setBackgroundResource(R.drawable.resume_add_icon)
            personalImageView.setOnClickListener {
                setClickListener("personal")
                cancelIconImgv.performClick()
            }

        }

        if (educationInfo.equals("True", ignoreCase = true)) {
            educationaImageView.setBackgroundResource(R.drawable.acount_right_icon)
        } else {
            educationaImageView.setBackgroundResource(R.drawable.resume_add_icon)
            educationaImageView.setOnClickListener {
                setClickListener("education")
                cancelIconImgv.performClick()
            }
        }

        if (photoInfo.equals("True", ignoreCase = true)) {
            photInfoImageView.setBackgroundResource(R.drawable.acount_right_icon)
        } else {
            photInfoImageView.setBackgroundResource(R.drawable.resume_add_icon)
            photInfoImageView.setOnClickListener {
                setClickListener("photo")
                cancelIconImgv.performClick()
            }
        }

        if (skillInfo.equals("True", ignoreCase = true)) {
            skillIMGV.setBackgroundResource(R.drawable.acount_right_icon)
        } else {
            skillIMGV.setBackgroundResource(R.drawable.resume_add_icon)
            skillIMGV.setOnClickListener {
                setClickListener("experience")
                cancelIconImgv.performClick()
            }
        }



        dialog.setCancelable(true)
        dialog.show()

        cancelIconImgv.setOnClickListener {
            try {
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setClickListener(clickItem: String) {

        when (clickItem) {
            "personal" -> {
                startActivity<PersonalInfoActivity>(
                    "name" to "null",
                    "personal_info_edit" to "addDirect"
                )
            }
            "experience" -> {
                startActivity<OtherInfoBaseActivity>(
                    "name" to "null",
                    "other_info_add" to "addDirect"
                )
            }
            "education" -> {
                startActivity<AcademicBaseActivity>(
                    "name" to "null",
                    "education_info_add" to "addDirect"
                )
            }
            "photo" -> {
                startActivity<PhotoUploadActivity>()
            }
        }
    }

    override fun goToMessageByEmployers(from: String) {
        startActivity<EmployersBaseActivity>(
            "from" to from,
            "time" to time
        )
    }

    private fun scheduleMorningNotification() {
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, MorningNotificationReceiver::class.java).apply {
            putExtra("type", "morning")
        }.let {
            PendingIntent.getBroadcast(this, 0, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
        }

        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60 * 60 * 24,
            alarmIntent
        )
    }

    private fun scheduleNightNotification() {
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, NightNotificationReceiver::class.java).apply {
            putExtra("type", "night")
        }.let {
            PendingIntent.getBroadcast(this, 1, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
        }

        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60 * 60 * 24,
            alarmIntent
        )
    }
}
