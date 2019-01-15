package com.bdjobs.app.BackgroundJob

import android.content.Context
import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator


class BdjobsJobCreator(private val appContext:Context) : JobCreator {

    override fun create(tag: String): Job? {
        return when (tag) {
            DatabaseUpdateJob.TAG -> DatabaseUpdateJob(appContext)
            FavSearchDeleteJob.TAG -> FavSearchDeleteJob(appContext)
            ShortListedJobDeleteJob.TAG->ShortListedJobDeleteJob(appContext)
            FollowUnfollowJob.TAG -> FollowUnfollowJob(appContext)
            ExpectedSalaryJob.TAG -> ExpectedSalaryJob(appContext)
            else -> null
        }
    }
}