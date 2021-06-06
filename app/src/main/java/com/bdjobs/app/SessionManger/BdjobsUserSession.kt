package com.bdjobs.app.SessionManger

import android.app.Activity
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.edit
import com.bdjobs.app.API.ModelClasses.DataLoginPasswordModel
import com.bdjobs.app.BroadCastReceivers.MorningNotificationReceiver
import com.bdjobs.app.BroadCastReceivers.NightNotificationReceiver
import com.bdjobs.app.GuestUserLanding.GuestUserJobSearchActivity
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.Constants.Companion.isDeviceInfromationSent
import com.bdjobs.app.Utilities.Constants.Companion.name_sharedPref
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.databases.internal.BdjobsDB
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber
import kotlin.system.exitProcess


class BdjobsUserSession(val context: Context) {
    private var pref: SharedPreferences? = null

    init {
        pref = context.getSharedPreferences(name_sharedPref, 0)
    }

    fun createSession(isCvPosted: String,
                      name: String,
                      email: String,
                      userId: String,
                      decodId: String,
                      userName: String,
                      AppsDate: String,
                      age: String,
                      exp: String,
                      catagoryId: String,
                      gender: String,
                      resumeUpdateON: String,
                      IsResumeUpdate: String,
                      trainingId: String,
                      userPicUrl: String) {

        pref?.edit {
            putString(Constants.session_key_isCvPosted, isCvPosted)
            putString(Constants.session_key_name, name)
            putString(Constants.session_key_email, email)
            putString(Constants.session_key_userId, userId)
            putString(Constants.session_key_decodId, decodId)
            putString(Constants.session_key_userName, userName)
            putString(Constants.session_key_AppsDate, AppsDate)
            putString(Constants.session_key_age, age)
            putString(Constants.session_key_exp, exp)
            putString(Constants.session_key_catagoryId, catagoryId)
            putString(Constants.session_key_gender, gender)
            putString(Constants.session_key_resumeUpdateON, resumeUpdateON)
            putString(Constants.session_key_IsResumeUpdate, IsResumeUpdate)
            putString(Constants.session_key_trainingId, trainingId)
            putString(Constants.session_key_userPicUrl, userPicUrl)
            putBoolean(Constants.session_key_loggedIn, true)
        }

    }

    fun createSession(sessionData: DataLoginPasswordModel) {

        pref?.edit {
            putString(Constants.session_key_isCvPosted, sessionData.isCvPosted)
            putString(Constants.session_key_name, sessionData.name)
            putString(Constants.session_key_email, sessionData.email)
            putString(Constants.session_key_userId, sessionData.userId)
            putString(Constants.session_key_decodId, sessionData.decodId)
            putString(Constants.session_key_userName, sessionData.userName)
            putString(Constants.session_key_AppsDate, sessionData.appsDate)
            putString(Constants.session_key_PostingDate, sessionData.posingDate)
            putString(Constants.session_key_age, sessionData.age)
            putString(Constants.session_key_exp, sessionData.exp)
            putString(Constants.session_key_catagoryId, sessionData.catagoryId)
            putString(Constants.session_key_gender, sessionData.gender)
            putString(Constants.session_key_resumeUpdateON, sessionData.resumeUpdateON)
            putString(Constants.session_key_IsResumeUpdate, sessionData.isResumeUpdate)
            putString(Constants.session_key_trainingId, sessionData.trainingId)
            putString(Constants.session_key_userPicUrl, sessionData.userPicUrl)
            putBoolean(Constants.session_key_loggedIn, true)
        }

    }

    val isCvPosted = pref?.getString(Constants.session_key_isCvPosted, null)
    val fullName = pref?.getString(Constants.session_key_name, null)
    val email = pref?.getString(Constants.session_key_email, null)
    val userId = pref?.getString(Constants.session_key_userId, null)
    val decodId = pref?.getString(Constants.session_key_decodId, null)
    val userName = pref?.getString(Constants.session_key_userName, null)
    val AppsDate = pref?.getString(Constants.session_key_AppsDate, null)
    val postingDate = pref?.getString(Constants.session_key_PostingDate, null)
    val age = pref?.getString(Constants.session_key_age, null)
    val exp = pref?.getString(Constants.session_key_exp, null)
    val catagoryId = pref?.getString(Constants.session_key_catagoryId, null)
    val gender = pref?.getString(Constants.session_key_gender, null)
    val resumeUpdateON = pref?.getString(Constants.session_key_resumeUpdateON, null)
    val IsResumeUpdate = pref?.getString(Constants.session_key_IsResumeUpdate, null)
    val trainingId = pref?.getString(Constants.session_key_trainingId, null)
    val userPicUrl = pref?.getString(Constants.session_key_userPicUrl, null)
    val isLoggedIn = pref?.getBoolean(Constants.session_key_loggedIn, false)
    val cvUploadStatus = pref?.getString(Constants.session_key_cvuploadstatus, "1")
    val shortListedDate = pref?.getString(Constants.KEY_SHORTLISTED_DATE, "19-Mar-1919")
    val videoResumeThreshold = pref?.getString(Constants.VIDEO_RESUME_THRESHOLD, "0")
    val videoResumeTotalAnswered = pref?.getString(Constants.VIDEO_RESUME_TOTAL_ANSWERED, "0")

    //val applyJobCount = pref?.getString(Constants.session_key_job_apply_count,"0")
    //val availableJobsCount = pref?.getString(Constants.session_key_available_job_count,"0")
    val jobApplyLimit = pref?.getString(Constants.session_job_apply_limit, "50")
    val jobApplyThreshold = pref?.getString(Constants.session_job_apply_threshold, "25")
    var notificationCount = pref?.getInt(Constants.notification_count, 0)
    var messageCount = pref?.getInt(Constants.message_count, 0)

    var generalInterviewCount = pref?.getInt(Constants.GENERAL_INTERVIEW_COUNT, 0)
    var videoInterviewCount = pref?.getInt(Constants.VIDEO_INTERVIEW_COUNT, 0)
    var liveInterviewCount = pref?.getInt(Constants.LIVE_INTERVIEW_COUNT, 0)

    private fun killCurrentApp(context: Context) {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val appTasks = am!!.getAppTasks()
            if (appTasks.size > 0) {
                val appTask = appTasks[0]
                appTask.finishAndRemoveTask()
            }
        }
    }

    fun cancelAlarms(){
        try {
            Timber.d("cancel method called")
            //morning alarms
            val morningIntent = Intent(context, MorningNotificationReceiver::class.java)
            val morningPendingIntent = PendingIntent.getBroadcast(context, 0, morningIntent, PendingIntent.FLAG_NO_CREATE)
            val morningAlarmManager: AlarmManager? = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
            if (morningPendingIntent != null) {
                morningAlarmManager?.cancel(morningPendingIntent)
            }

            //night alarms
            val nightIntent = Intent(context, NightNotificationReceiver::class.java)
            val nightPendingIntent = PendingIntent.getBroadcast(context, 0, nightIntent, PendingIntent.FLAG_NO_CREATE)
            val nightAlarmManager: AlarmManager? = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
            if (nightPendingIntent != null) {
                nightAlarmManager?.cancel(nightPendingIntent)
            }
        } catch (e:Exception){
            e.printStackTrace()
        }

    }

    fun logoutUser(exitApp: Boolean = false) {

        pref?.edit()?.clear()?.apply()
        val bdjobsDB = BdjobsDB.getInstance(context = context)
        isDeviceInfromationSent = false
        doAsync {
            bdjobsDB.followedEmployerDao().deleteAllFollowedEmployer()
            bdjobsDB.shortListedJobDao().deleteAllShortListedJobs()
            bdjobsDB.favouriteSearchFilterDao().deleteAllFavouriteSearch()
            bdjobsDB.appliedJobDao().deleteAllAppliedJobs()
            bdjobsDB.b2CCertificationDao().deleteAllB2CCertification()
            bdjobsDB.jobInvitationDao().deleteAllJobInvitation()
            bdjobsDB.videoInvitationDao().deleteAllVideoInvitation()
            bdjobsDB.liveInvitationDao().deleteAllLiveInvitation()
            bdjobsDB.lastSearchDao().deleteAllLastSearch()
            bdjobsDB.suggestionDAO().deleteAllSuggestion()
            bdjobsDB.notificationDao().deleteAllNotifications()

            Constants.generalInvitation = "0"
            Constants.videoInvitation = "0"
            Constants.liveInvitation = "0"

            uiThread {
                // loadingDialog.dismiss()
                if (!exitApp) {
                    val intent = Intent(context, GuestUserJobSearchActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    (context as Activity).finishAffinity()
                } else {
//                    val id = android.os.Process.myPid()
//                    android.os.Process.killProcess(id)
                    killCurrentApp(context)
                    exitProcess(1)
                }


            }
        }
    }

    fun insertVideoResumeTotalAnswered(count: String){
        pref?.edit {
            putString(Constants.VIDEO_RESUME_TOTAL_ANSWERED, count)
        }
        pref?.edit()?.apply()
    }

    fun insertVideoResumeThresholdValue(count: String){
        pref?.edit {
            putString(Constants.VIDEO_RESUME_THRESHOLD, count)
        }
        pref?.edit()?.apply()
    }

    fun insertGeneralInterviewCount(count: Int){
        pref?.edit {
            putInt(Constants.GENERAL_INTERVIEW_COUNT, count)
        }
        pref?.edit()?.apply()
    }

    fun insertVideoInterviewCount(count: Int){
        pref?.edit {
            putInt(Constants.VIDEO_INTERVIEW_COUNT, count)
        }
        pref?.edit()?.apply()
    }

    fun insertLiveInterviewCount(count: Int){
        pref?.edit {
            putInt(Constants.LIVE_INTERVIEW_COUNT, count)
        }
        pref?.edit()?.apply()
    }

    fun insertShortlListedPopupDate(dt: String) {
        pref?.edit {
            putString(Constants.KEY_SHORTLISTED_DATE, dt)
        }
    }

    fun updateIsCvPosted(isCvPosted: String) {
        pref?.edit {
            putString(Constants.session_key_isCvPosted, isCvPosted)
        }
        pref?.edit()?.apply()
    }

    fun updateFullName(name: String) {
        pref?.edit {
            putString(Constants.session_key_name, name)
        }
    }

    fun updateEmail(email: String) {
        pref?.edit {
            putString(Constants.session_key_email, email)
        }
    }

    fun updateUserId(userId: String) {
        pref?.edit {
            putString(Constants.session_key_userId, userId)
        }
    }

    fun updateDecodId(decodId: String) {
        pref?.edit {
            putString(Constants.session_key_decodId, decodId)
        }
    }

    fun updateUserName(userName: String) {
        pref?.edit {
            putString(Constants.session_key_userName, userName)
        }
    }

    fun updateAppsDate(AppsDate: String) {
        pref?.edit {
            putString(Constants.session_key_AppsDate, AppsDate)
        }
    }

    fun updatePostingDate(postingDate: String) {
        pref?.edit {
            putString(Constants.session_key_PostingDate, postingDate)
        }
    }

    fun updateAge(age: String) {
        pref?.edit {
            putString(Constants.session_key_age, age)
        }
    }

    fun updateExperience(exp: String) {
        pref?.edit {
            putString(Constants.session_key_exp, exp)
        }
    }

    fun updateCatagoryId(catagoryId: String) {
        pref?.edit {
            putString(Constants.session_key_catagoryId, catagoryId)
        }
    }

    fun updateGender(gender: String) {
        pref?.edit {
            putString(Constants.session_key_gender, gender)
        }
    }

    fun updateResumeUpdateON(resumeUpdateON: String) {
        pref?.edit {
            putString(Constants.session_key_resumeUpdateON, resumeUpdateON)
        }
    }

    fun updateIsResumeUpdate(IsResumeUpdate: String) {
        pref?.edit {
            putString(Constants.session_key_IsResumeUpdate, IsResumeUpdate)
        }
    }

    fun updateTrainingId(trainingId: String) {
        pref?.edit {
            putString(Constants.session_key_trainingId, trainingId)

        }
    }

    fun updateUserPicUrl(userPicUrl: String) {
        pref?.edit {
            putString(Constants.session_key_userPicUrl, userPicUrl)
        }
    }

    fun updateUserCVUploadStatus(status: String?) {
        pref?.edit {
            putString(Constants.session_key_cvuploadstatus, status)
        }
    }

//    fun updateJobApplyCount(count : String?){
//        pref?.edit {
//            putString(Constants.session_key_job_apply_count, count)
//            putString(Constants.session_key_available_job_count, (50-count!!.toInt()).toString())
//        }
//    }

    fun updateJobApplyLimit(count: String?) {
        pref?.edit {
            putString(Constants.session_job_apply_limit, count)
        }
    }

    fun updateJobApplyThreshold(count: String?) {
        pref?.edit {
            putString(Constants.session_job_apply_threshold, count)
        }
    }

    fun updateNotificationCount(count: Int?) {
        pref?.edit {
            putInt(Constants.notification_count, count!!)
        }
    }

    fun updateMessageCount(count: Int?) {
        pref?.edit {
            putInt(Constants.message_count, count!!)
        }
    }

    fun insertMybdjobsLastMonthCountData(
            jobsApplied: String?,
            emailResume: String?,
            employerViewdResume: String?,
            followedEmployers: String?,
            interviewInvitation: String?,
            messageByEmployers: String?,
            videoInvitation: String?,
            liveInvitation: String?


    ) {

        pref?.edit {
            putString(Constants.session_key_mybdjobscount_jobs_applied_thismonth, jobsApplied)
            putString(Constants.session_key_mybdjobscount_times_emailed_resume_lastmonth, emailResume)
            putString(Constants.session_key_mybdjobscount_employers_viwed_resume_lastmonth, employerViewdResume)
            putString(Constants.session_key_mybdjobscount_employers_followed_lastmonth, followedEmployers)
            putString(Constants.session_key_mybdjobscount_interview_invitation_lastmonth, interviewInvitation)
            putString(Constants.session_key_mybdjobscount_message_by_employers_lastmonth, messageByEmployers)
            putString(Constants.session_key_mybdjobscount_video_invitation_lastmonth, videoInvitation)
            putString(Constants.session_key_mybdjobscount_live_invitation_lastmonth, liveInvitation)


        }

    }

    fun insertMybdjobsAlltimeCountData(jobsApplied: String?,
                                       emailResume: String?,
                                       employerViewdResume: String?,
                                       followedEmployers: String?,
                                       interviewInvitation: String?,
                                       messageByEmployers: String?,
                                       videoInvitation: String?,
                                       liveInvitation: String?

    ) {

        pref?.edit {
            putString(Constants.session_key_mybdjobscount_jobs_applied_alltime, jobsApplied)
            putString(Constants.session_key_mybdjobscount_times_emailed_resume_alltime, emailResume)
            putString(Constants.session_key_mybdjobscount_employers_viwed_resume_alltime, employerViewdResume)
            putString(Constants.session_key_mybdjobscount_employers_followed_alltime, followedEmployers)
            putString(Constants.session_key_mybdjobscount_interview_invitation_alltime, interviewInvitation)
            putString(Constants.session_key_mybdjobscount_message_by_employers_alltime, messageByEmployers)
            putString(Constants.session_key_mybdjobscount_video_invitation_alltime, videoInvitation)
            putString(Constants.session_key_mybdjobscount_live_invitation_alltime, liveInvitation)


        }

    }

    val mybdjobscount_jobs_applied_lastmonth = pref?.getString(Constants.session_key_mybdjobscount_jobs_applied_thismonth, "0")
    val mybdjobscount_times_emailed_resume_lastmonth = pref?.getString(Constants.session_key_mybdjobscount_times_emailed_resume_lastmonth, "0")
    val mybdjobscount_employers_viwed_resume_lastmonth = pref?.getString(Constants.session_key_mybdjobscount_employers_viwed_resume_lastmonth, "0")
    val mybdjobscount_employers_followed_lastmonth = pref?.getString(Constants.session_key_mybdjobscount_employers_followed_lastmonth, "0")
    val mybdjobscount_interview_invitation_lastmonth = pref?.getString(Constants.session_key_mybdjobscount_interview_invitation_lastmonth, "0")
    val mybdjobscount_message_by_employers_lastmonth = pref?.getString(Constants.session_key_mybdjobscount_message_by_employers_lastmonth, "0")
    val mybdjobscount_video_invitation_lastmonth = pref?.getString(Constants.session_key_mybdjobscount_video_invitation_lastmonth, "0")
    val mybdjobscount_live_invitation_lastmonth = pref?.getString(Constants.session_key_mybdjobscount_live_invitation_lastmonth, "0")


    val mybdjobscount_jobs_applied_alltime = pref?.getString(Constants.session_key_mybdjobscount_jobs_applied_alltime, "0")
    val mybdjobscount_times_emailed_resume_alltime = pref?.getString(Constants.session_key_mybdjobscount_times_emailed_resume_alltime, "0")
    val mybdjobscount_employers_viwed_resume_alltime = pref?.getString(Constants.session_key_mybdjobscount_employers_viwed_resume_alltime, "0")
    val mybdjobscount_employers_followed_alltime = pref?.getString(Constants.session_key_mybdjobscount_employers_followed_alltime, "0")
    val mybdjobscount_interview_invitation_alltime = pref?.getString(Constants.session_key_mybdjobscount_interview_invitation_alltime, "0")
    val mybdjobscount_message_by_employers_alltime = pref?.getString(Constants.session_key_mybdjobscount_message_by_employers_alltime, "0")
    val mybdjobscount_video_invitation_alltime = pref?.getString(Constants.session_key_mybdjobscount_video_invitation_alltime, "0")
    val mybdjobscount_live_invitation_alltime = pref?.getString(Constants.session_key_mybdjobscount_live_invitation_alltime, "0")


    protected fun incrementCount(key: String) {
        try {
            val value = pref?.getString(key, "0")
            val count = (value?.toInt()!! + 1).toString()
            //Log.d("rakib", "$key -> $count")
            pref?.edit() {
                putString(key, count)
            }
        } catch (e: Exception) {
            logException(e)
        }
    }

    protected fun decrementCount(key: String) {
        try {
            val value = pref?.getString(key, "0")
            if (value?.toInt()!! > 0) {
                val count = (value.toInt() - 1).toString()
                //Log.d("rakib", "$key -> $count")
                pref?.edit() {
                    putString(key, count)
                }
            }
        } catch (e: Exception) {
            logException(e)
        }
    }

    fun incrementJobsApplied() {
        incrementCount(Constants.session_key_mybdjobscount_jobs_applied_thismonth)
        incrementCount(Constants.session_key_mybdjobscount_jobs_applied_alltime)
        //incrementCount(Constants.session_key_job_apply_count)

    }

    fun decrementJobsApplied() {
        decrementCount(Constants.session_key_mybdjobscount_jobs_applied_thismonth)
        decrementCount(Constants.session_key_mybdjobscount_jobs_applied_alltime)
        //decrementCount(Constants.session_key_job_apply_count)

    }

    fun incrementAvailableJobs() {
        //incrementCount(Constants.session_key_available_job_count)
    }

    fun decrementAvailableJobs() {
        //decrementCount(Constants.session_key_available_job_count)
    }

    fun incrementTimesEmailedRessume() {
        incrementCount(Constants.session_key_mybdjobscount_times_emailed_resume_lastmonth)
        incrementCount(Constants.session_key_mybdjobscount_times_emailed_resume_alltime)
    }

    fun incrementFollowedEmployer() {
        incrementCount(Constants.session_key_mybdjobscount_employers_followed_lastmonth)
        incrementCount(Constants.session_key_mybdjobscount_employers_followed_alltime)
    }

    fun deccrementFollowedEmployer() {
        decrementCount(Constants.session_key_mybdjobscount_employers_followed_lastmonth)
        decrementCount(Constants.session_key_mybdjobscount_employers_followed_alltime)
    }

    var isRemoteViewInitialized:Boolean
    get() = pref!!.getBoolean(Constants.session_key_remoteview_initiliaze,false)
    set(value) {
        pref!!.edit().putBoolean(Constants.session_key_remoteview_initiliaze,value).apply()
    }

    var isSessionAlreadyStarted : Boolean
    get() = pref!!.getBoolean(Constants.session_key_socket_session_started,false)
    set(value) {
        pref!!.edit().putBoolean(Constants.session_key_socket_session_started,value).apply()
    }

}