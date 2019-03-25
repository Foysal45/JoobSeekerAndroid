package com.bdjobs.app.BackgroundJob

import android.content.Context
import android.util.Log
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.FavouriteSearchCountModel
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.SessionManger.BdjobsUserSession
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


class FavSearchDeleteJob(private val appContext: Context) : Job() {

    private val bdjobsUserSession = BdjobsUserSession(appContext)
    val bdjobsDB: BdjobsDB = BdjobsDB.getInstance(appContext)

    companion object {
        const val TAG = "favsearch_delete_job"
        fun scheduleAdvancedJob(favId: String): Int {
            val extras = PersistableBundleCompat()
            extras.putString("favid", favId)

           /* val jobId = JobRequest.Builder(FavSearchDeleteJob.TAG)
                    .setExecutionWindow(TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(10))
                    .setRequiredNetworkType(JobRequest.NetworkType.UNMETERED)
                    .setRequirementsEnforced(true)
                    .setExtras(extras)
                    .build()
                    .schedule()*/
            val jobId = JobRequest.Builder(FavSearchDeleteJob.TAG)
                    .startNow()
                    .setExtras(extras)
                    .build()
                    .schedule()

            Log.d("werywirye", " jobid: $jobId")
            return jobId
        }

        fun cancelJob(jobId: Int) {
            JobManager.instance().cancel(jobId)
        }
    }

    override fun onRunJob(params: Params): Result {

        val favID = params.extras.getString("favid", "")

        if (favID.isNotEmpty()) {
            doAsync {
                bdjobsDB.favouriteSearchFilterDao().deleteFavouriteSearchByID(favID)
            }
            ApiServiceMyBdjobs.create().deleteFavSearch(userId = bdjobsUserSession.userId, decodeId = bdjobsUserSession.decodId, intSfID = favID).enqueue(object : Callback<FavouriteSearchCountModel> {
                override fun onFailure(call: Call<FavouriteSearchCountModel>, t: Throwable) {
                    error("onFailure", t)
                }

                override fun onResponse(call: Call<FavouriteSearchCountModel>, response: Response<FavouriteSearchCountModel>) {
                    try {
                        Log.d("werywirye", "Success API")
                    } catch (e: Exception) {
                        logException(e)
                    }
                }
            })
            Log.d("werywirye", "Hello: $favID")
            return Result.SUCCESS
        }

        Log.d("werywirye", "Hello: $favID")
        return Result.FAILURE

    }


}