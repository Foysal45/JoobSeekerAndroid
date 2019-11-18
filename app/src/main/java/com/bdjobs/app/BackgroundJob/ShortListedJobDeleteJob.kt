package com.bdjobs.app.BackgroundJob
//
//import android.content.Context
//import android.util.Log
//import com.bdjobs.app.API.ApiServiceMyBdjobs
//import com.bdjobs.app.API.ModelClasses.UnshorlistJobModel
//import com.bdjobs.app.Databases.Internal.BdjobsDB
//import com.bdjobs.app.SessionManger.BdjobsUserSession
//import com.bdjobs.app.Utilities.error
//import com.evernote.android.job.Job
//import com.evernote.android.job.JobManager
//import com.evernote.android.job.JobRequest
//import com.evernote.android.job.util.support.PersistableBundleCompat
//import org.jetbrains.anko.doAsync
//import org.jetbrains.anko.toast
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class ShortListedJobDeleteJob(private val appContext: Context) : Job() {
//
//    private val bdjobsUserSession = BdjobsUserSession(appContext)
//    val bdjobsDB: BdjobsDB = BdjobsDB.getInstance(appContext)
//
//    companion object {
//        const val TAG = "shortlist_delete_job"
//        fun scheduleAdvancedJob(jobID: String): Int {
//            val extras = PersistableBundleCompat()
//            extras.putString("jobid", jobID)
//
//            /*val jobId = JobRequest.Builder(ShortListedJobDeleteJob.TAG)
//                    .setExecutionWindow(TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(10))
//                    .setRequiredNetworkType(JobRequest.NetworkType.UNMETERED)
//                    .setRequirementsEnforced(true)
//                    .setExtras(extras)
//                    .build()
//                    .schedule()*/
//
//            val jobId = JobRequest.Builder(ShortListedJobDeleteJob.TAG)
//                    .startNow()
//                    .setExtras(extras)
//                    .build()
//                    .schedule()
//
//            Log.d(TAG, " jobid: $jobId")
//            return jobId
//        }
//
//        fun runJobImmediately(jobID: String): Int {
//            val extras = PersistableBundleCompat()
//            extras.putString("jobid", jobID)
//            val jobId = JobRequest.Builder(ShortListedJobDeleteJob.TAG)
//                    .startNow()
//                    .setExtras(extras)
//                    .build()
//                    .schedule()
//            Log.d("werywirye", " jobid: $jobId")
//            return jobId
//        }
//
//        fun cancelJob(jobId: Int) {
//            JobManager.instance().cancel(jobId)
//        }
//    }
//
//    override fun onRunJob(params: Params): Result {
//
//        val jobid = params.extras.getString("jobid", "")
//        Log.d(TAG, "Shortlised jobID: $jobid")
//        if (jobid.isNotEmpty()) {
//            doAsync {
//                bdjobsDB.shortListedJobDao().deleteShortListedJobsByJobID(jobid)
//            }
//            ApiServiceMyBdjobs.create().unShortlistJob(
//                    userId = bdjobsUserSession.userId,
//                    decodeId = bdjobsUserSession.decodId,
//                    strJobId = jobid
//            ).enqueue(object : Callback<UnshorlistJobModel> {
//                override fun onFailure(call: Call<UnshorlistJobModel>, t: Throwable) {
//                    error("onFailure", t)
//                }
//
//                override fun onResponse(call: Call<UnshorlistJobModel>, response: Response<UnshorlistJobModel>) {
//                    Log.d(TAG, "Shortlised jobID: $jobid")
//                    try {
//                        appContext?.toast("${response.body()?.message}")
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            })
//
//            return Result.SUCCESS
//        }
//
//        Log.d("werywirye", "Shortlised jobID: $jobid")
//        return Result.FAILURE
//
//    }
//}