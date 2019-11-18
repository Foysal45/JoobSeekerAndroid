package com.bdjobs.app.Workmanager

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.FollowUnfollowModelClass
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.logException
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowUnfollowWorker(val appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    private val bdjobsUserSession = BdjobsUserSession(appContext)
    val bdjobsDB: BdjobsDB = BdjobsDB.getInstance(appContext)

    override fun doWork(): Result {

        val companyID: String? = inputData.getString("companyid")
        val companyName: String? = inputData.getString("companyname")

        if (companyID!!.isNotEmpty()) {
            doAsync {
                //  bdjobsDB.favouriteSearchFilterDao().deleteFavouriteSearchByID(companyID)
                bdjobsDB.followedEmployerDao().deleteFollowedEmployerByCompanyID(companyID,companyName!!)
            }
            ApiServiceJobs.create().getUnfollowMessage(
                    id = companyID,
                    name = companyName,
                    userId = bdjobsUserSession.userId,
                    encoded = Constants.ENCODED_JOBS,
                    actType = "fed",
                    decodeId = bdjobsUserSession.decodId
            ).enqueue(object : Callback<FollowUnfollowModelClass> {
                override fun onFailure(call: Call<FollowUnfollowModelClass>, t: Throwable) {
                    error("onFailure", t)
                    Log.d("error", " error = ${ t.message}")
                }

                override fun onResponse(call: Call<FollowUnfollowModelClass>, response: Response<FollowUnfollowModelClass>) {
                    Log.d("error", " error = ${ response?.code()}")
                    try {
                        Log.d("werywirye", "Success API")
                        var statuscode = response.body()?.statuscode
                        var message = response.body()?.data?.get(0)?.message
//                Log.d("msg", message)
                        Toast.makeText(appContext, message, Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        logException(e)
                    }
                }

            })
            Log.d("werywirye", "Hello: $companyID")
            return Result.success()
        }
        return Result.failure()
    }
}