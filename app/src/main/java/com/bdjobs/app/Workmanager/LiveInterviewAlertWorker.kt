package com.bdjobs.app.Workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bdjobs.app.Databases.Internal.BdjobsDB
import org.jetbrains.anko.doAsync
import timber.log.Timber
import java.util.*

class LiveInterviewAlertWorker(val appContext: Context, workerParams: WorkerParameters): Worker(appContext,workerParams) {
    override fun doWork(): Result {
        doAsync {
            val bdjobsDB = BdjobsDB.getInstance(appContext)
            val calendar = Calendar.getInstance()
            val live = bdjobsDB.liveInvitationDao().getAllLiveInvitationByDate(Date())
            val all = bdjobsDB.liveInvitationDao().getAllLiveInvitation()
            Timber.tag("rakib").d("live $live")
            Timber.tag("rakib").d("all $all")
        }
        return Result.success()
    }
}