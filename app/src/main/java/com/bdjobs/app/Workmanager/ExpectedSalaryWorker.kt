package com.bdjobs.app.Workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.AppliedJobsSalaryEdit
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExpectedSalaryWorker(val appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {
    override fun doWork(): Result {

        val userid: String? = inputData.getString("userid")
        val decodeid: String? = inputData.getString("decodeid")
        val jobid: String? = inputData.getString("jobid")
        val expectedSalary: String? = inputData.getString("salary")

        if (jobid!!.isNotEmpty()) {
            // updateExpectedSalary(jobID, expectedSalary, userID, decodeID)
            /*Log.d("werywirye1", "Hello: $jobID - $expectedSalary - $userID - $expectedSalary ")
            return Result.SUCCESS*/


            ApiServiceMyBdjobs.create().getUpdateSalaryMsg(
                    userId = userid,
                    decodeId = decodeid,
                    JobId = jobid,
                    txtExpectedSalary = expectedSalary
            ).enqueue(object : Callback<AppliedJobsSalaryEdit> {
                override fun onFailure(call: Call<AppliedJobsSalaryEdit>, t: Throwable) {
                    // Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                    appContext.toast("${t.message}")
                }

                override fun onResponse(call: Call<AppliedJobsSalaryEdit>, response: Response<AppliedJobsSalaryEdit>) {

                    if (response.body()?.statuscode == "0") {
                        // Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
                        appContext.toast("${response.body()?.message}")

                    }
                }

            })
            return Result.success()
        }

        return Result.failure()
    }

}