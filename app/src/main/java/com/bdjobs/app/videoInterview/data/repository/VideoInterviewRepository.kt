package com.bdjobs.app.videoInterview.data.repository

import android.app.Application
import com.bdjobs.app.SessionManger.BdjobsUserSession

class VideoInterviewRepository(val application: Application)  {

    val session = BdjobsUserSession(application)
}