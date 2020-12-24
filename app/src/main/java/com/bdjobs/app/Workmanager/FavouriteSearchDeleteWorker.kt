package com.bdjobs.app.Workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.FavouriteSearchCountModel
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.logException
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavouriteSearchDeleteWorker(val appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    private val bdjobsUserSession = BdjobsUserSession(appContext)
    val bdjobsDB: BdjobsDB = BdjobsDB.getInstance(appContext)

    override fun doWork(): Result {

        val favID: String? = inputData.getString("favId")


        if (favID!!.isNotEmpty()) {
            doAsync {
                bdjobsDB.favouriteSearchFilterDao().deleteFavouriteSearchByID(favID)
            }
            ApiServiceMyBdjobs.create().deleteFavSearch(userId = bdjobsUserSession.userId, decodeId = bdjobsUserSession.decodId, intSfID = favID).enqueue(object : Callback<FavouriteSearchCountModel> {
                override fun onFailure(call: Call<FavouriteSearchCountModel>, t: Throwable) {
                    error("onFailure", t)
                }

                override fun onResponse(call: Call<FavouriteSearchCountModel>, response: Response<FavouriteSearchCountModel>) {
                    try {
                        //Log.d("werywirye", "Success API")
                    } catch (e: Exception) {
                        logException(e)
                    }
                }
            })
            //Log.d("werywirye", "Hello: $favID")
            return Result.success()
        }
        return Result.failure()
    }
}