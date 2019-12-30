package com.bdjobs.app.BackgroundJob
//
//import android.content.Context
//import android.util.Log
//import android.widget.Toast
//import com.bdjobs.app.API.ApiServiceJobs
//import com.bdjobs.app.API.ApiServiceMyBdjobs
//import com.bdjobs.app.API.ModelClasses.AppliedJobsSalaryEdit
//import com.bdjobs.app.API.ModelClasses.FavouriteSearchCountModel
//import com.bdjobs.app.API.ModelClasses.FollowUnfollowModelClass
//import com.bdjobs.app.Databases.Internal.BdjobsDB
//import com.bdjobs.app.SessionManger.BdjobsUserSession
//import com.bdjobs.app.Utilities.Constants
//import com.bdjobs.app.Utilities.error
//import com.bdjobs.app.Utilities.logException
//import com.evernote.android.job.Job
//import com.evernote.android.job.JobManager
//import com.evernote.android.job.JobRequest
//import com.evernote.android.job.util.support.PersistableBundleCompat
//import org.jetbrains.anko.doAsync
//import org.jetbrains.anko.toast
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.util.concurrent.TimeUnit
//
//
//class ExpectedSalaryJob(private val appContext: Context) : Job() {
//
//    private val bdjobsUserSession = BdjobsUserSession(appContext)
//
//    companion object {
//        const val TAG = "exptected_salary_popup"
//
//        fun runJobImmediately(userId: String, decodeId: String, jobid: String, expectedSalary: String): Int {
//            val extras = PersistableBundleCompat()
//            extras.putString("userid", userId)
//            extras.putString("decodeid", decodeId)
//            extras.putString("jobid", jobid)
//            extras.putString("expectedsalary", expectedSalary)
//            val jobId = JobRequest.Builder(ExpectedSalaryJob.TAG)
//                    .startNow()
//                    .setExtras(extras)
//                    .build()
//                    .schedule()
//            // //Log.d("werywirye", " jobid-android-job: $jobId ")
//            //Log.d("werywirye", "Hello: $jobid - $decodeId - $userId - $expectedSalary ")
//            //Log.d("werywirye", " jobid: $jobId ")
//            return jobId
//        }
//    }
//
//    override fun onRunJob(params: Params): Result {
//        val userID = params.extras.getString("userid", "")
//        val decodeID = params.extras.getString("decodeid", "")
//        val jobID = params.extras.getString("jobid", "")
//        val expectedSalary = params.extras.getString("expectedsalary", "")
//
//        //Log.d("werywirye1", "Hello: $jobID - $decodeID - $userID - $expectedSalary ")
//        if (jobID.isNotEmpty()) {
//            // updateExpectedSalary(jobID, expectedSalary, userID, decodeID)
//            /*//Log.d("werywirye1", "Hello: $jobID - $expectedSalary - $userID - $expectedSalary ")
//            return Result.SUCCESS*/
//
//
//            ApiServiceMyBdjobs.create().getUpdateSalaryMsg(
//                    userId = userID,
//                    decodeId = decodeID,
//                    JobId = jobID,
//                    txtExpectedSalary = expectedSalary
//            ).enqueue(object : Callback<AppliedJobsSalaryEdit> {
//                override fun onFailure(call: Call<AppliedJobsSalaryEdit>, t: Throwable) {
//                    // Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
//                    appContext.toast("${t.message}")
//                }
//
//                override fun onResponse(call: Call<AppliedJobsSalaryEdit>, response: Response<AppliedJobsSalaryEdit>) {
//
//                    if (response.body()?.statuscode == "0") {
//                        // Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
//                        appContext.toast("${response.body()?.message}")
//
//                    }
//                }
//
//            })
//            return Result.SUCCESS
//        }
//
//        //Log.d("werywirye1", "employer: $jobID")
//        return Result.FAILURE
//
//
//    }
//
//
//}