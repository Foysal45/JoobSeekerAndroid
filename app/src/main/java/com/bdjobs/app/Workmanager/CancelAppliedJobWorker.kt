package com.bdjobs.app.Workmanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.CancelAppliedJobs
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.SessionManger.BdjobsUserSession
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CancelAppliedJobWorker(val appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    private val bdjobsUserSession = BdjobsUserSession(appContext)
    val bdjobsDB: BdjobsDB = BdjobsDB.getInstance(appContext)

    override fun doWork(): Result {

        val userid: String? = inputData.getString("userid")
        val decodeid: String? = inputData.getString("decodeid")
        val jobid : String? = inputData.getString("jobid")


        if (userid!!.isNotEmpty()) {


            ApiServiceMyBdjobs.create().getAppliedCancelMsg(
                    userId = userid,
                    decodeId = decodeid,
                    JobId = jobid
            ).enqueue(object : Callback<CancelAppliedJobs> {
                override fun onFailure(call: Call<CancelAppliedJobs>, t: Throwable) {
                    appContext.toast("${t.message}")
                }

                override fun onResponse(call: Call<CancelAppliedJobs>, response: Response<CancelAppliedJobs>) {
                    if (response.body()?.statuscode == "0" || response.body()?.statuscode == "4") {
                        // Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
                        appContext.toast("${response.body()?.message}")
                    }
                    doAsync {
                        bdjobsDB.appliedJobDao().deleteAppliedJobsByJobID(jobid!!)
                    }
                }
            })

            Log.d("werywirye", "Hello: $userid")
            return Result.success()
        }

        return Result.failure()
    }

}