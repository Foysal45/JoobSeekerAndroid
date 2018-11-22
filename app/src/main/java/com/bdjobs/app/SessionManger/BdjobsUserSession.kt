package com.bdjobs.app.SessionManger

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.bdjobs.app.API.ModelClasses.DataLoginPasswordModel
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.Constants.Companion.name_sharedPref

class BdjobsUserSession(context: Context) {
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
    val age = pref?.getString(Constants.session_key_age, null)
    val exp = pref?.getString(Constants.session_key_exp, null)
    val catagoryId = pref?.getString(Constants.session_key_catagoryId, null)
    val gender = pref?.getString(Constants.session_key_gender, null)
    val resumeUpdateON = pref?.getString(Constants.session_key_resumeUpdateON, null)
    val IsResumeUpdate = pref?.getString(Constants.session_key_IsResumeUpdate, null)
    val trainingId = pref?.getString(Constants.session_key_trainingId, null)
    val userPicUrl = pref?.getString(Constants.session_key_userPicUrl, null)
    val isLoggedIn = pref?.getBoolean(Constants.session_key_loggedIn, false)

    fun logoutUser() {
        pref?.edit()?.clear()?.apply()
    }

    fun updateIsCvPosted(isCvPosted: String) {
        pref?.edit {
            putString(Constants.session_key_isCvPosted, isCvPosted)
        }
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

}