package com.bdjobs.app.Workmanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.UnshorlistJobModel
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.error
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShortlistedJobDeleteWorker(val appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    private val bdjobsUserSession = BdjobsUserSession(appContext)
    val bdjobsDB: BdjobsDB = BdjobsDB.getInstance(appContext)

    override fun doWork(): Result {

        val jobid = inputData.getString("jobId")

        Log.d("rakib workmanager", jobid)

        if (jobid!!.isNotEmpty()) {
            doAsync {
                bdjobsDB.shortListedJobDao().deleteShortListedJobsByJobID(jobid)
            }
            ApiServiceMyBdjobs.create().unShortlistJob(
                    userId = bdjobsUserSession.userId,
                    decodeId = bdjobsUserSession.decodId,
                    strJobId = jobid
            ).enqueue(object : Callback<UnshorlistJobModel> {
                override fun onFailure(call: Call<UnshorlistJobModel>, t: Throwable) {
                    error("onFailure", t)
                }

                override fun onResponse(call: Call<UnshorlistJobModel>, response: Response<UnshorlistJobModel>) {
                    try {
                         val output = workDataOf("jobid" to jobid)
                        appContext?.toast("${response.body()?.message}")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })

            return Result.success()
        }


        return Result.failure()
    }

}