package com.bdjobs.app.Utilities

class Constants {
    companion object {

        const val FB_KEY_EMAIL = "email"
        const val FB_KEY_ID = "id"
        const val SOCIAL_MEDIA_GOOGLE = "G"
        const val SOCIAL_MEDIA_FACEBOOK = "F"
        const val SOCIAL_MEDIA_LINKEDIN = "L"
        const val FACEBOOK_GRAPH_REQUEST_PERMISSION_STRING = "id,name,email,gender,picture.type(large),first_name,last_name"
        const val FACEBOOK_GRAPH_REQUEST_PERMISSION_KEY ="fields"

        const val LINKEDIN_REQUEST_URL = "https://api.linkedin.com/v1/people/~:(email-address,id,first-name,last-name,formatted-name,phone-numbers,public-profile-url,picture-url,picture-urls::(original))"

        const val ENCODED_JOBS ="02041526JSBJ2"

        const val RC_SIGN_IN = 9001
        const val counterTimeLimit = 60000
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
        const val api_request_result_code_ok = "0"
        const val timer_countDownInterval = 1000
        const val baseUrlMyBdjobs = "https://my.bdjobs.com/apps/mybdjobs/v1/"
        const val baseUrlJobs = "https://jobs.bdjobs.com/apps/api/v1/"
        const val base_url_mybdjobs_photo = "https://my.bdjobs.com/photos"
        const val guestUserRequestCode = 1
        const val REQ_CODE_SPEECH_INPUT = 100
        const val session_key_isCvPosted = "isCvPosted"
        const val session_key_name = "name"
        const val session_key_email = "email"
        const val session_key_userId = "userId"
        const val session_key_decodId = "decodId"
        const val session_key_userName = "userName"
        const val session_key_AppsDate = "AppsDate"
        const val session_key_age = "age"
        const val session_key_exp = "exp"
        const val session_key_catagoryId = "catagoryId"
        const val session_key_gender = "gender"
        const val session_key_resumeUpdateON = "resumeUpdateON"
        const val session_key_IsResumeUpdate = "IsResumeUpdate"
        const val session_key_trainingId = "trainingId"
        const val session_key_userPicUrl = "userPicUrl"
        const val session_key_loggedIn= "loggedIn"
        const val api_mybdjobs_app_signinprocess ="app_signinprocess.asp"
        const val api_mybdjobs_app_agent_log ="apps_agent_log.asp"

        const val api_mybdjobs_app_social_agent_log ="app_social_agent_log.asp"

        const val api_jobs_db_update ="dbupdate.asp"
        const val key_recent_search_keyword ="keyword"
        const val internal_database_name ="BdjobsInternal.db"
        const val JOB_SHARE_URL = "http://jobs.bdjobs.com/JobDetails.asp?id="
    }

}