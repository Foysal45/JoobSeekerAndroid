package com.bdjobs.app.Utilities

import android.R
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.FavouriteSearchCountDataModelWithID
import com.bdjobs.app.API.ModelClasses.HotJobsData
import com.bdjobs.app.API.ModelClasses.UploadResume
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.videoInterview.data.models.VideoManager
import com.google.android.gms.ads.formats.UnifiedNativeAd
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Constants {
    companion object {

        val ADMOB_NATIVE_AD_UNIT_ID = "ca-app-pub-5130888087776673/8613851148"
        val ADMOB_APP_ID = "ca-app-pub-5130888087776673~6094744346"
        val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-5130888087776673/3622884741"
        var nativeAdvertisement : UnifiedNativeAd? = null







        var hotjobs: List<HotJobsData?>? = listOf()


        fun deleteFavCount(filterId:String?){
            try {
                Favcounts.forEachIndexed { index, favouriteSearchCountDataModelWithID ->
                    if(favouriteSearchCountDataModelWithID.id == filterId){
                        Favcounts.removeAt(index)
                    }
                }
            } catch (e: Exception) {
            }
        }


        val Favcounts:ArrayList<FavouriteSearchCountDataModelWithID> = ArrayList<FavouriteSearchCountDataModelWithID>()

        const val GOOGLE_SIGN_IN_CLIENT_ID ="538810570838-2u4ecb19a0kl7789girooesoq9rsfhdn.apps.googleusercontent.com"

        var isDeviceInfromationSent = false
        var changePassword_Eligibility = "0"
        var myBdjobsStatsLastMonth = true
        var matchedTraining = true
        var timesEmailedResumeLast = true
        var cvUploadStatus = ""
        var favSearchFiltersSynced = false
        var jobInvitationSynced = false
        var certificationSynced = false
        var followedEmployerSynced = false
        var isDirectCall = false
        var appliedJobsCount = 0
        var appliedJobsThreshold = 25
        var appliedJobLimit = 50

        var totalContacted = 0
        var totalNotContacted = 0
        var totalHired = 0

        var calendar = Calendar.getInstance()
        var daysAvailable = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH)
//        val warningmsgThrsldDate = "31/10/2019" //dd/mm/yyyy
//        var applyRestrictionStatus = false

        // set and get from fragment
        //personal
        val personalUpdate = "personalUpdate"
        val contactUpdate = "contactUpdate"
        val caiUpdate = "caiUpdate"
        val prefAreasUpdate = "prefAreasUpdate"
        val oriUpdate = "oriUpdate"

        //Education
        val acaUpdate = "acaUpdate"
        val trUpdate = "trUpdate"
        val profUpdate = "profUpdate"

        // Employment History
        val empHistoryList = "empHistoryList"
        val armyEmpHistoryList = "armyEmpHistoryList"

        //Other infos
        val langUpdate = "langUpdate"
        val referUpdate = "referUpdate"
        val specUpdate = "specUpdate"


        const val KEY_SHORTLISTED_DATE = "shortlistedDate"

        const val HOTJOBS_WEB_LINK = "http://bdjobs.com/upcoming/files/hotjob/apphotjobs.asp"
        const val FB_KEY_EMAIL = "email"
        const val FB_KEY_ID = "id"
        const val SOCIAL_MEDIA_GOOGLE = "G"
        const val SOCIAL_MEDIA_FACEBOOK = "F"
        const val FACEBOOK_GRAPH_REQUEST_PERMISSION_STRING = "id,name,email,gender,picture.type(large),first_name,last_name"
        const val FACEBOOK_GRAPH_REQUEST_PERMISSION_KEY = "fields"

        const val ENCODED_JOBS = "02041526JSBJ2"

        const val RC_SIGN_IN = 9001
        const val counterTimeLimit = 30000
        const val key_true = "true"
        const val key_false = "false"
        const val name_sharedPref = "bdjobs"
        const val key_db_update = "dbupdatedate"
        const val dfault_date_db_update = "3/09/2016 5:08:00 PM"
        const val key_typedData = "typedData"
        const val key_from = "from"
        const val key_jobtitleET = "jobtitleET"
        const val key_loacationET = "loacationET"
        const val key_categoryET = "categoryET"
        const val key_special_categoryET = "specialCategoryET"

        const val key_industryET = "industryET"
        const val key_newspaperET = "newspaperET"

        const val api_request_result_code_ok = "0"
        const val timer_countDownInterval = 1000
        const val baseUrlMyBdjobs = "https://my.bdjobs.com/apps/mybdjobs/v1/"
        const val baseUrlJobs = "https://jobs.bdjobs.com/apps/api/v1/"
        const val base_url_mybdjobs_photo = "https://my.bdjobs.com/photos"
        const val base_url_module_sample = "http://mybdjobs.bdjobs.com/mybdjobs/assessment/samples/"
        const val base_url_assessment_web = "https://mybdjobs.bdjobs.com/mybdjobs/assessment/smnt_certification_home.asp?device=app"
        const val url_assessment_help = "https://mybdjobs.bdjobs.com/mybdjobs/assessment/smnt_certification_help.asp?r=lm"

        const val key_go_to_home = "goToHome"
        const val BdjobsUserRequestCode = 1
        const val REQ_CODE_SPEECH_INPUT = 100
        const val session_key_isCvPosted = "isCvPosted"
        const val session_key_name = "name"
        const val session_key_email = "email"
        const val session_key_userId = "userId"
        const val session_key_decodId = "decodId"
        const val session_key_userName = "userName"
        const val session_key_AppsDate = "AppsDate"
        const val session_key_PostingDate = "postingDate"
        const val session_key_age = "age"
        const val session_key_exp = "exp"
        const val session_key_catagoryId = "catagoryId"
        const val session_key_gender = "gender"
        const val session_key_resumeUpdateON = "resumeUpdateON"
        const val session_key_IsResumeUpdate = "IsResumeUpdate"
        const val session_key_trainingId = "trainingId"
        const val session_key_userPicUrl = "userPicUrl"
        const val session_key_loggedIn = "loggedIn"
        const val api_mybdjobs_app_signinprocess = "app_signinprocess.asp"
        const val api_mybdjobs_app_agent_log = "apps_agent_log.asp"
        const val api_mybdjobs_app_social_agent_log = "app_social_agent_log.asp"
        const val api_jobs_db_update = "dbupdate.asp"
        const val api_mybdjobs_app_favouritejob_count = "app_favouritejob_count.asp"
        const val session_key_cvuploadstatus = "cvuploadstatus"
        //const val session_key_job_apply_count = "jobApplyCount"
        //const val session_key_available_job_count = "availableJobCount"
        const val session_job_apply_limit = "jobApplyLimit"
        const val session_job_apply_threshold = "jobApplyThreshold"
        const val notification_count = "notificationCount"


        const val session_key_mybdjobscount_jobs_applied_thismonth = "jobs_applied_thismonth"
        const val session_key_mybdjobscount_times_emailed_resume_lastmonth = "times_emailed_resume_lastmonth"
        const val session_key_mybdjobscount_employers_viwed_resume_lastmonth= "employers_viwed_resume_lastmonth"
        const val session_key_mybdjobscount_employers_followed_lastmonth = "employers_followed_lastmonth"
        const val session_key_mybdjobscount_interview_invitation_lastmonth = "interview_invitation_lastmonth"
        const val session_key_mybdjobscount_message_by_employers_lastmonth = "message_by_employers_lastmonth"
        const val session_key_mybdjobscount_video_invitation_lastmonth = "video_invitation_lastmonth"



        const val session_key_mybdjobscount_jobs_applied_alltime = "jobs_applied_alltime"
        const val session_key_mybdjobscount_times_emailed_resume_alltime = "times_emailed_resume_alltime"
        const val session_key_mybdjobscount_employers_viwed_resume_alltime= "employers_viwed_resume_alltime"
        const val session_key_mybdjobscount_employers_followed_alltime = "employers_followed_alltime"
        const val session_key_mybdjobscount_interview_invitation_alltime = "interview_invitation_alltime"
        const val session_key_mybdjobscount_message_by_employers_alltime = "message_by_employers_alltime"
        const val session_key_mybdjobscount_video_invitation_alltime = "video_invitation_alltime"

        const val session_key_mybdjobscount_jobs_applied = "Jobs\nApplied"
        const val session_key_mybdjobscount_times_emailed_resume = "Times Emailed\nResume"
        const val session_key_mybdjobscount_employers_viwed_resume= "Employers Viewed\nResume"
        const val session_key_mybdjobscount_employers_followed = "Employers\nFollowed"
        const val session_key_mybdjobscount_interview_invitation = "Interview\nInvitations"
        const val session_key_mybdjobscount_message_by_employers = "Messages by\nEmployers"
        const val session_key_mybdjobscount_video_invitation = "Video Interview\nInvitations"


        const val NOTIFICATION_INTERVIEW_INVITATTION = 100
        const val NOTIFICATION_CV_VIEWED = 101
        const val NOTIFICATION_PROMOTIONAL_MESSAGE = 102
        const val NOTIFICATION_MATCHED_JOB = 104
        const val NOTIFICATION_VIDEO_INTERVIEW = 105


        const val internal_database_name = "BdjobsInternal.db"
        const val JOB_SHARE_URL = "https://jobs.bdjobs.com/JobDetails.asp?id="
        const val BROADCAST_DATABASE_UPDATE_JOB = "com.bdjobs.dataBaseUpdateJob.jobComplete"

        const val KEY_MANUFACTURER = "Manufacturer"
        const val KEY_OS_VERSION = "OS_version"
        const val KEY_OS_API_LEVEL = "OS_Api_Level"
        const val KEY_DEVICE = "Device"
        const val KEY_MODEL_PRODUCT = "Model"
        const val KEY_TOTAL_INTERNAL_STORAGE = "Internal_Storage"
        const val KEY_FREE_INTERNAL_STORAGE = "Free_Internal_Storage"
        const val KEY_MOBILE_NETWORK = "Mobile_Network"
        const val KEY_CONNECTED_WIFI = "Connected Wifi"
        const val KEY_DEVICE_RAM_SIZE = "Ram_Size"
        const val KEY_DEVICE_FREE_RAM_SIZE = "Ram_Free_Size"
        const val KEY_DATE_AND_TIME = "dateAndTime"
        const val KEY_APP_VERSION = "appVersion"
        const val KEY_SCREEN_SIZE = "screensize"
        const val APP_ID = "1"

        const val NOTIFICATION_TYPE_INTERVIEW_INVITATION = "ii"
        const val NOTIFICATION_TYPE_CV_VIEWED = "cv"
        const val NOTIFICATION_TYPE_PROMOTIONAL_MESSAGE = "pm"
        const val NOTIFICATION_TYPE_MATCHED_JOB = "mj"
        const val NOTIFICATION_TYPE_FORCE_LOGOUT = "fl"
        const val NOTIFICATION_TYPE_GENERAL = "gn"
        const val NOTIFICATION_TYPE_REMOVE_NOTIFICATION = "rn"
        const val NOTIFICATION_TYPE_REMOVE_MESSAGE = "rm"
        const val NOTIFICATION_TYPE_VIDEO_INTERVIEW = "vi"

        fun sendDeviceInformation(token: String? = "", context: Context) {
            val session = BdjobsUserSession(context)

            val deviceInfo = context.getDeviceInformation()
            val OS_API_Level = deviceInfo[Constants.KEY_OS_API_LEVEL]
            val Internal_storage = deviceInfo[Constants.KEY_TOTAL_INTERNAL_STORAGE]
            val Free_storage = deviceInfo[Constants.KEY_FREE_INTERNAL_STORAGE]
            val Mobile_Network = deviceInfo[Constants.KEY_MOBILE_NETWORK]
            val RAM_Size = deviceInfo[Constants.KEY_DEVICE_RAM_SIZE]
            val RAM_Free = deviceInfo[Constants.KEY_DEVICE_FREE_RAM_SIZE]
            val dateAndTime = deviceInfo[Constants.KEY_DATE_AND_TIME]
            val ScreenSize = deviceInfo[Constants.KEY_SCREEN_SIZE]
            val Manufacturer = deviceInfo[Constants.KEY_MANUFACTURER]
            val AppVersion = deviceInfo[Constants.KEY_APP_VERSION]
            val userID = session.userId
            val decodeID = session.decodId
            val deviceID = context.getDeviceID()

            /*Log.d("DeviceInformation", "OS_API_Level=$OS_API_Level\n" +
                    "Internal_storage=$Internal_storage\n" +
                    "Free_storage=$Free_storage\n" +
                    "Mobile_Network=$Mobile_Network\n" +
                    "RAM_Size=$RAM_Size\n" +
                    "RAM_Free=$RAM_Free\n" +
                    "dateAndTime=$dateAndTime\n" +
                    "ScreenSize=$ScreenSize\n" +
                    "Manufacturer=$Manufacturer\n" +
                    "AppVersion=$AppVersion\n" +
                    "deviceID=$deviceID\n" +
                    "FCMToken=$token")*/

            ApiServiceMyBdjobs.create().sendDeviceInformation(
                    OS_API_Level = OS_API_Level,
                    Internal_storage = Internal_storage,
                    Free_storage = Free_storage,
                    Mobile_Network = Mobile_Network,
                    RAM_Size = RAM_Size,
                    RAM_Free = RAM_Free,
                    ScreenSize = ScreenSize,
                    dateTime = dateAndTime,
                    FCMToken = token,
                    Manufecturer = Manufacturer,
                    AppVersion = AppVersion,
                    userId = userID,
                    decodeId = decodeID,
                    deviceID = deviceID
            ).enqueue(
                    object : Callback<UploadResume> {
                        override fun onFailure(call: Call<UploadResume>, t: Throwable) {
                            error("onFailure", t)
                        }

                        override fun onResponse(call: Call<UploadResume>, response: Response<UploadResume>) {
                            //Log.d("DeviceInformation", "send data: ${response.body()}")
                            try {
                                if (response.body()?.statuscode == api_request_result_code_ok)
                                    Constants.isDeviceInfromationSent = true
                            } catch (e: Exception) {
                                logException(e)
                            }
                        }
                    }
            )
        }

        fun showJobApplicationGuidelineDialog(context: Context) {

            val dialog = Dialog(context, R.style.Theme_Translucent_NoTitleBar)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(true)
            dialog?.setContentView(com.bdjobs.app.R.layout.job_application_guideline)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val backImageView = dialog?.findViewById<ImageView>(com.bdjobs.app.R.id.job_application_guideline_back_arrow)
            val dropArrowImageView = dialog?.findViewById<ImageView>(com.bdjobs.app.R.id.job_application_guideline_drop_arrow)
            val scrollView = dialog?.findViewById<ScrollView>(com.bdjobs.app.R.id.scroll_view)
            val titleView = dialog?.findViewById<TextView>(com.bdjobs.app.R.id.job_application_guideline_how_it_works_title)
            val howItWorksMessageView = dialog?.findViewById<TextView>(com.bdjobs.app.R.id.job_application_guideline_how_it_works_message)

            val bdjobsUserSession = BdjobsUserSession(context)

            howItWorksMessageView.text = "• Job seekers of Bdjobs can apply for a limited number of circulars every month.\n• Validity period of application limit is ${bdjobsUserSession.jobApplyLimit} jobs per month.\n• It is set to be renewed every month.\n• Unused “Application Volume” will not be carried forward to the next month."


            dropArrowImageView?.setOnClickListener {
                scrollView?.post { scrollView.smoothScrollTo(0, titleView!!.bottom) }
            }

            backImageView?.setOnClickListener {
                dialog?.dismiss()
            }

            dialog?.show()
        }

        fun getDateTimeAsAgo(date: Date?) : HashMap<String,Long>{

           val hashMap = HashMap<String,Long>()


            val seconds = TimeUnit.MILLISECONDS.toSeconds(Date().time - date!!.time)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(Date().time - date!!.time)
            val hours = TimeUnit.MILLISECONDS.toHours(Date().time - date!!.time)
            val days = TimeUnit.MILLISECONDS.toDays(Date().time - date!!.time)

            when {
                seconds < 60 -> {
                    hashMap["seconds"] = seconds
                    return  hashMap
                }
                minutes < 60 -> {
                    hashMap["minutes"] = minutes
                    return hashMap
                }
                hours < 24 -> {
                    hashMap["hours"] = hours
                    return hashMap
                }
                else -> {
                    hashMap["days"] = days
                    return hashMap
                }
            }
        }


        var jobId : String? = ""
        var applyId :String? = ""
        var quesId : String? = ""
        var quesSerialNo : String? = ""
        var duration : String? = ""
        var file : File? = null

        fun createVideoManagerDataForUpload(videoManager: VideoManager?){
            jobId = videoManager?.jobId
            applyId = videoManager?.applyId
            quesId = videoManager?.questionId
            quesSerialNo = videoManager?.questionSerial
            duration = videoManager?.questionDuration
            file = videoManager?.file
        }
    }
}