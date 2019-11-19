package com.bdjobs.app.BackgroundJob

import android.app.Application
//import com.evernote.android.job.JobApi
//import com.evernote.android.job.JobConfig
//import com.evernote.android.job.JobManager


class App :Application() {

    override fun onCreate() {
        super.onCreate()

//        JobConfig.setForceAllowApi14(true)
//        JobConfig.setApiEnabled(JobApi.GCM, false) // is only important for Android 4.X
//        JobManager.create(this).addJobCreator(BdjobsJobCreator(applicationContext))
    }


}