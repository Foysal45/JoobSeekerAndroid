package com.bdjobs.app.sms.data.repository

import android.app.Application
import com.bdjobs.app.SessionManger.BdjobsUserSession

class SMSRepository(private val application : Application) {

    val session = BdjobsUserSession(application)

    fun getFullName() : String?{
        return session.fullName
    }

}