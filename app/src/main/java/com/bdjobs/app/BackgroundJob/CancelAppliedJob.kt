package com.bdjobs.app.BackgroundJob
//
//import android.content.Context
//import android.util.Log
//import com.bdjobs.app.API.ApiServiceMyBdjobs
//import com.bdjobs.app.API.ModelClasses.CancelAppliedJobs
//import com.bdjobs.app.Databases.Internal.AppliedJobDao
//import com.bdjobs.app.Databases.Internal.BdjobsDB
//import com.bdjobs.app.SessionManger.BdjobsUserSession
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
//
//class CancelAppliedJob(private val appContext: Context) : Job() {
//
//    private val bdjobsUserSession = BdjobsUserSession(appContext)
//    val bdjobsDB: BdjobsDB = BdjobsDB.getInstance(appContext)
//
//    companion object {
//        const val TAG = "cancel_applied_job"
//        fun scheduleAdvancedJob(userid: String, decodeid: String, jobid: String): Int {
//            val extras = PersistableBundleCompat()
//            extras.putString("userid", userid)
//            extras.putString("decodeid", decodeid)
//            extras.putString("jobid", jobid)
//
//           /* val jobId = JobRequest.Builder(CancelAppliedJob.TAG)
//                    .setExecutionWindow(TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(10))
//                    .setRequiredNetworkType(JobRequest.NetworkType.UNMETERED)
//                    .setRequirementsEnforced(true)
//                    .setExtras(extras)
//                    .build()
//                    .schedule()*/
//
//            val jobId = JobRequest.Builder(CancelAppliedJob.TAG)
//                    .startNow()
//                    .setExtras(extras)
//                    .build()
//                    .schedule()
//
//            Log.d("werywirye", " jobid: $jobId ")
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
//        val userid = params.extras.getString("userid", "")
//        val decodeid = params.extras.getString("decodeid", "")
//        val jobid = params.extras.getString("jobid", "")
//
//        if (userid.isNotEmpty()) {
//
//
//            ApiServiceMyBdjobs.create().getAppliedCancelMsg(
//                    userId = userid,
//                    decodeId = decodeid,
//                    JobId = jobid
//            ).enqueue(object : Callback<CancelAppliedJobs> {
//                override fun onFailure(call: Call<CancelAppliedJobs>, t: Throwable) {
//                    appContext.toast("${t.message}")
//                }
//
//                override fun onResponse(call: Call<CancelAppliedJobs>, response: Response<CancelAppliedJobs>) {
//                    if (response.body()?.statuscode == "0" || response.body()?.statuscode == "4") {
//                        // Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
//                        appContext.toast("${response.body()?.message}")
//                    }
//                    doAsync {
//                        bdjobsDB.appliedJobDao().deleteAppliedJobsByJobID(jobid)
//                    }
//                }
//            })
//
//            Log.d("werywirye", "Hello: $userid")
//            return Result.SUCCESS
//        }
//
//        Log.d("werywirye", "Hello: $userid")
//        return Result.FAILURE
//
//    }
//
//}