package com.bdjobs.app.LoggedInUserLanding

import android.app.Dialog
import android.app.Fragment
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ModelClasses.MoreHorizontalData
import com.bdjobs.app.assessment.AssesmentBaseActivity
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.ManageResume.ManageResumeActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Settings.SettingBaseActivity
import com.bdjobs.app.Training.TrainingListAcitivity
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.editResume.EditResLandingActivity
import com.bdjobs.app.editResume.personalInfo.PersonalInfoActivity
import com.bdjobs.app.liveInterview.LiveInterviewActivity
import com.bdjobs.app.sms.BaseActivity
import com.bdjobs.app.transaction.TransactionBaseActivity
import com.bdjobs.app.videoInterview.VideoInterviewActivity
import com.bdjobs.app.videoResume.VideoResumeActivity
import kotlinx.android.synthetic.main.fragment_more_layout.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import timber.log.Timber

class MoreFragment : Fragment() {

    private lateinit var bdjobsUserSession : BdjobsUserSession
    private var horizontalAdapter: HorizontalAdapter? = null
    private var horizontaList: ArrayList<MoreHorizontalData> = ArrayList()
    lateinit var homeCommunicator: HomeCommunicator
    var cvUploadMore: String = ""
    private lateinit var bdjobsDB: BdjobsDB
    private var dialog: Dialog? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_more_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        homeCommunicator = activity as HomeCommunicator
        bdjobsUserSession = BdjobsUserSession(activity)
        bdjobsDB = BdjobsDB.getInstance(activity)
        initializeViews()
        clearAddPopulateData()
        getIfCVuploaded()
        onclick()
    }

    fun getIfCVuploaded() {
        cvUploadMore = homeCommunicator.isGetCvUploaded()
    }

    private fun shakeHorizontaList() {
        //Log.d("horizontaList", "horizontaList: ${horizontaList.size}")
        Handler().postDelayed({
            horizontal_RV?.post {
                horizontal_RV.smoothScrollToPosition(horizontaList.size - 1)
            }
        }, 1000)

        Handler().postDelayed({
            horizontal_RV?.post {
                horizontal_RV.smoothScrollToPosition(0)
            }
        }, 2000)
    }

    private fun onclick() {

        searchIMGV.setOnClickListener {
            homeCommunicator.gotoJobSearch()
        }

        profilePicIMGV.setOnClickListener {
            homeCommunicator.gotoEditresume()
        }

        notificationIMGV?.setOnClickListener {
            homeCommunicator.goToNotifications()
        }

        profilePicIMGV?.loadCircularImageFromUrl(BdjobsUserSession(activity).userPicUrl?.trim())

        versionInfoTV?.text = "v${activity?.getAppVersion()} (${activity?.getAppVersionCode()})"

        videoResume?.setOnClickListener {
            navigateToVideoResumePage()
        }

        ll_video_resume.setOnClickListener{
            navigateToVideoResumePage()
        }

        employerList_MBTN?.setOnClickListener {
            homeCommunicator.goToFollowedEmployerList("employer")
        }

        video_guide_MBTN?.setOnClickListener {
            activity?.openUrlInBrowser("https://mybdjobs.bdjobs.com/mybdjobs/videoHelp.asp")
        }

        generalSearch_MBTN?.setOnClickListener {
            homeCommunicator.gotoJobSearch()
        }
        appGuides_MBTN?.setOnClickListener {
            activity?.openUrlInBrowser("https://bdjobs.com/apps/version2/guide.html")
        }
        rateUs_MBTN?.setOnClickListener {
            goToRateApp()
        }
        feedback_MBTN?.setOnClickListener {
            activity?.openUrlInBrowser("https://jobs.bdjobs.com/feedback.asp")
        }
        privacypolicy_MBTN?.setOnClickListener {
            activity?.openUrlInBrowser("https://bdjobs.com/policy/Privacy_policy.asp")
        }
        terms_MBTN?.setOnClickListener {
            activity?.openUrlInBrowser("https://www.bdjobs.com/tos.asp")
        }
        new_job_MBTN?.setOnClickListener {
            startActivity<JobBaseActivity>("postedWithin" to "1")
        }
        deadline_MBTN?.setOnClickListener {
            startActivity<JobBaseActivity>("deadline" to "2")
        }
        part_time_MBTN?.setOnClickListener {
            startActivity<JobBaseActivity>("jobNature" to "PartTime")
        }
        contractual_MBTN?.setOnClickListener {
            startActivity<JobBaseActivity>("jobNature" to "Contract")
        }
        government_MBTN?.setOnClickListener {
            startActivity<JobBaseActivity>("organization" to "1")
        }
        overseas_MBTN?.setOnClickListener {
            startActivity<JobBaseActivity>(Constants.key_loacationET to "Country")
        }
        interviewinvitation_MBTN?.setOnClickListener {
            homeCommunicator.goToInterviewInvitation("homePage")
        }
        video_interviewinvitation_MBTN?.setOnClickListener {
            startActivity<VideoInterviewActivity>()
        }

        live_interviewinvitation_MBTN?.setOnClickListener {
            startActivity<LiveInterviewActivity>("from" to "homePage")
        }

        training_MBTN?.setOnClickListener {
            startActivity<TrainingListAcitivity>()
        }
        settings_MBTN?.setOnClickListener {
            startActivity<SettingBaseActivity>()
        }
        viewResume_MBTN.setOnClickListener {
            homeCommunicator.setTime("0")
            homeCommunicator.goToEmployerViewedMyResume("vwdMyResume")
        }
        emailResume_MBTN?.setOnClickListener {
            startActivity<ManageResumeActivity>(
                    "from" to "emailResume"
            )
        }
        uploadResumeBTN?.setOnClickListener {
            startActivity<ManageResumeActivity>(
                    "from" to "uploadResume"
            )
        }

        jobApplicationStatus_MBTN.setOnClickListener {
//            activity?.showJobApplicationGuidelineDialog()
            Constants.showJobApplicationGuidelineDialog(activity)
        }

        assessment_MBTN?.setOnClickListener {
            startActivity<AssesmentBaseActivity>()
        }

        sms_package_MBTN?.setOnClickListener {
            startActivity<BaseActivity>()
        }

        transaction_overview_MBTN?.setOnClickListener {
            startActivity<TransactionBaseActivity>()
        }

        messageIMGV?.setOnClickListener {
            homeCommunicator.goToMessages()
        }
    }

    private fun navigateToVideoResumePage() {
        if (!bdjobsUserSession.isCvPosted?.equalIgnoreCase("true")!!) {
            try {
                val alertd = alert("To Access this feature please post your resume") {
                    title = "Your resume is not posted!"
                    positiveButton("Post Bdjobs Resume") { startActivity<PersonalInfoActivity>("name" to "personal", "personal_info_edit" to "null") }
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
            startActivity<VideoResumeActivity>()
        }
    }


    private fun goToRateApp(){
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse("appmarket://details?id=com.bdjobs.app"))
        val otherApps: MutableList<ResolveInfo> = activity?.getPackageManager()!!.queryIntentActivities(intent, 0)
        var agFound = false

        for (app in otherApps) {
            if (app.activityInfo.applicationInfo.packageName.equals("com.huawei.appmarket")) {
                val psComponent = ComponentName(app.activityInfo.applicationInfo.packageName, app.activityInfo.name)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.component = psComponent
                startActivity(intent)
                agFound = true
                break
            }
        }
        if (!agFound) {
            activity?.openUrlInBrowser("https://play.google.com/store/apps/details?id=com.bdjobs.app")
        }
    }

    private fun clearAddPopulateData() {
        /*this  function deletes duplicates data lists  */
        if (horizontaList.isNullOrEmpty()) {
            populateData()
        } else {
            horizontalAdapter?.removeAll()
            horizontaList.clear()
            populateData()
        }
        horizontalAdapter?.addAll(horizontaList)
//        shakeHorizontaList()
    }

    private fun populateData() {

        homeCommunicator.getInviteCodeUserType()?.let { txt ->
            when {
                txt.equalIgnoreCase("o") -> horizontaList.add(MoreHorizontalData(R.drawable.ic_invite_code, "ইনভাইট &\nআর্ন"))
                txt.equalIgnoreCase("u") -> horizontaList.add(MoreHorizontalData(R.drawable.ic_invite_code, "ইনভাইট\nকোড"))
                else -> {
                }
            }
        }
//
        horizontaList.add(MoreHorizontalData(R.drawable.ic_manage_resume_more, "Manage\nResume"))
        horizontaList.add(MoreHorizontalData(R.drawable.ic_applied_jobs_more, "Applied\nJobs"))
        horizontaList.add(MoreHorizontalData(R.drawable.ic_favorite, "Favourite\nSearch"))
        horizontaList.add(MoreHorizontalData(R.drawable.ic_followed_employers_more, "Followed\nEmployers"))
        horizontaList.add(MoreHorizontalData(R.drawable.ic_emplist_ic, "Employer\nList"))

    }

    private fun initializeViews() {
        horizontalAdapter = HorizontalAdapter(activity)
        horizontal_RV?.adapter = horizontalAdapter
        horizontal_RV?.setHasFixedSize(true)
        //Log.d("initPag", "called")

    }

    override fun onResume() {
        super.onResume()
        showNotificationCount()
        showMessageCount()
    }

    private fun showNotificationCount() {
        try {
            bdjobsUserSession = BdjobsUserSession(activity)
            if (bdjobsUserSession.notificationCount!! <= 0) {
                notificationCountTV?.hide()
            } else {
                notificationCountTV?.show()
                if (bdjobsUserSession.notificationCount!! > 99) {
                    notificationCountTV?.text = "99+"

                } else {
                    notificationCountTV?.text = "${bdjobsUserSession.notificationCount!!}"

                }
            }
        } catch (e: Exception) {
        }
    }

    fun updateMessageView(count: Int?) {
        //Log.d("rakib", "in home fragment $count")
        if (count!! > 0) {
            messageCountTV?.show()
            if (count <= 99)
                messageCountTV?.text = "$count"
            else
                messageCountTV?.text = "99+"
        } else {
            messageCountTV?.hide()
        }
    }

    private fun showMessageCount() {
        try {

            doAsync {
                bdjobsUserSession = BdjobsUserSession(activity)
                val count = bdjobsDB.notificationDao().getMessageCount()
                Timber.d("Messages count: $count")
                bdjobsUserSession.updateMessageCount(count)
            }

            if (bdjobsUserSession.messageCount!! <= 0) {
                messageCountTV?.hide()
            } else {
                messageCountTV?.show()
                if (bdjobsUserSession.messageCount!! > 99) {
                    messageCountTV?.text = "99+"

                } else {
                    messageCountTV?.text = "${bdjobsUserSession.messageCount!!}"

                }
            }
        } catch (e: Exception) {
        }
    }

    fun updateNotificationView(count: Int?) {
        //Log.d("rakib", "in home fragment $count")
        if (count!! > 0) {
            notificationCountTV?.show()
            if (count <= 99)
                notificationCountTV?.text = "$count"
            else
                notificationCountTV?.text = "99+"
        } else {
            notificationCountTV?.hide()
        }

//        notificationCountTV?.show()
//        bdjobsUserSession = BdjobsUserSession(activity)
//        if (bdjobsUserSession.notificationCount!! > 99){
//            notificationCountTV?.text = "99+"
//
//        } else{
//            notificationCountTV?.text = "${bdjobsUserSession.notificationCount!!}"
//
//        }
    }



}