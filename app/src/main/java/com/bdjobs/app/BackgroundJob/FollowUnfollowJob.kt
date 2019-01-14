package com.bdjobs.app.BackgroundJob

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.FavouriteSearchCountModel
import com.bdjobs.app.API.ModelClasses.FollowUnfollowModelClass
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.logException
import com.evernote.android.job.Job
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.evernote.android.job.util.support.PersistableBundleCompat
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit


class FollowUnfollowJob(private val appContext: Context) : Job() {

    private val bdjobsUserSession = BdjobsUserSession(appContext)
    val bdjobsDB: BdjobsDB = BdjobsDB.getInstance(appContext)

    companion object {
        const val TAG = "follow_unfollow_job"
        fun scheduleAdvancedJob(companyId: String, companyName : String): Int {
            val extras = PersistableBundleCompat()
            extras.putString("companyid", companyId)
            extras.putString("companyname", companyName)

            val jobId = JobRequest.Builder(FollowUnfollowJob.TAG)
                    .setExecutionWindow(TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(10))
                    .setRequiredNetworkType(JobRequest.NetworkType.UNMETERED)
                    .setRequirementsEnforced(true)
                    .setExtras(extras)
                    .build()
                    .schedule()

            Log.d("werywirye", " jobid: $jobId ")
            return jobId
        }

        fun cancelJob(jobId: Int) {
            JobManager.instance().cancel(jobId)
        }
    }

    override fun onRunJob(params: Params): Result {

        val companyID = params.extras.getString("companyid", "")
        val companyName = params.extras.getString("companyname", "")

        if (companyID.isNotEmpty()) {
            doAsync {
              //  bdjobsDB.favouriteSearchFilterDao().deleteFavouriteSearchByID(companyID)
                bdjobsDB.followedEmployerDao().deleteFollowedEmployerByCompanyID(companyID)
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
                }

                override fun onResponse(call: Call<FollowUnfollowModelClass>, response: Response<FollowUnfollowModelClass>) {

                    try {
                        Log.d("werywirye", "Success API")
                        var statuscode = response.body()?.statuscode
                        var message = response.body()?.data?.get(0)?.message
//                Log.d("msg", message)
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        logException(e)
                    }
                }

            })

            Log.d("werywirye", "Hello: $companyID")
            return Result.SUCCESS
        }

        Log.d("werywirye", "Hello: $companyID")
        return Result.FAILURE

    }

}