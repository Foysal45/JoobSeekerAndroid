package com.bdjobs.app.Utilities

import android.app.Activity
import android.content.Context
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.FavouritSearchFilterModelClass
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.FavouriteSearch
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants.Companion.ENCODED_JOBS
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DatabaseSync(private val context: Context) {

    private val bdjobsUserSession = BdjobsUserSession(context)


    fun insertDataAndGoToHomepage(goToHome:Boolean = true) {
        ApiServiceJobs.create().getFavouriteSearchFilters(encoded = ENCODED_JOBS, user = bdjobsUserSession.userId).enqueue(object : Callback<FavouritSearchFilterModelClass> {
            override fun onFailure(call: Call<FavouritSearchFilterModelClass>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<FavouritSearchFilterModelClass>, response: Response<FavouritSearchFilterModelClass>) {

                try {

                    val bdjobsInternalDB: BdjobsDB = BdjobsDB.getInstance(context)
                    bdjobsInternalDB.favouriteSearchFilterDao().deleteAllFavouriteSearch()
                    if (response.body()?.data?.size!! > 0) {
                        doAsync {
                            for (item in response.body()?.data!!) {
                                val favouriteSearch = FavouriteSearch(
                                        filterid = item?.filterid,
                                        filtername = item?.filtername,
                                        industrialCat = item?.industrialCat,
                                        functionalCat = item?.functionalCat,
                                        location = item?.location,
                                        organization = item?.organization,
                                        jobnature = item?.jobnature,
                                        joblevel = item?.joblevel,
                                        postedon = item?.postedon,
                                        deadline = item?.deadline,
                                        keyword = item?.keyword,
                                        newspaper = item?.newspaper,
                                        gender = item?.gender,
                                        genderb = item?.genderb,
                                        experience = item?.experience,
                                        age = item?.age,
                                        jobtype = item?.jobtype,
                                        retiredarmy = item?.retiredarmy,
                                        createdon = item?.createdon,
                                        updatedon = item?.updatedon,
                                        totaljobs = item?.totaljobs
                                )
                                bdjobsInternalDB.favouriteSearchFilterDao().insertFavouriteSearchFilter(favouriteSearch)
                            }

                            uiThread {
                               goToHomepage(goToHome)
                            }
                        }
                    }
                } catch (e: Exception) {
                    logException(e)
                    goToHomepage(goToHome)
                }

            }
        })
    }

     private fun goToHomepage(hasToGo:Boolean){
         if(hasToGo) {
             val activity = context as Activity
             activity.startActivity<MainLandingActivity>()
             activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
             activity.finish()
         }
    }


    private fun insertFollowedEmployers(){


    }
}