package com.bdjobs.app.Utilities

import android.app.Activity
import android.content.Context
import android.util.Log
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.FavouritSearchFilterModelClass
import com.bdjobs.app.API.ModelClasses.FollowEmployerListModelClass
import com.bdjobs.app.API.ModelClasses.JobListModel
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.FavouriteSearch
import com.bdjobs.app.Databases.Internal.FollowedEmployer
import com.bdjobs.app.Databases.Internal.ShortListedJobs
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants.Companion.ENCODED_JOBS
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DatabaseSync(private val context: Context, private var goToHome: Boolean = true) {

    private val bdjobsUserSession = BdjobsUserSession(context)
    val bdjobsInternalDB: BdjobsDB = BdjobsDB.getInstance(context)

    fun insertDataAndGoToHomepage() {
        ApiServiceJobs.create().getFavouriteSearchFilters(encoded = ENCODED_JOBS, userID = bdjobsUserSession.userId).enqueue(object : Callback<FavouritSearchFilterModelClass> {
            override fun onFailure(call: Call<FavouritSearchFilterModelClass>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<FavouritSearchFilterModelClass>, response: Response<FavouritSearchFilterModelClass>) {

                try {
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
                                insertFollowedEmployers()
                            }
                        }
                    }
                } catch (e: Exception) {
                    logException(e)
                    insertFollowedEmployers()
                }

            }
        })
    }

    private fun goToHomepage() {
        if (goToHome) {
            val activity = context as Activity
            activity.startActivity<MainLandingActivity>()
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            activity.finish()
        }
    }

    private fun insertFollowedEmployers() {

        ApiServiceJobs.create().getFollowEmployerList(userID = bdjobsUserSession.userId, decodeId = bdjobsUserSession.decodId, encoded = ENCODED_JOBS).enqueue(object : Callback<FollowEmployerListModelClass> {
            override fun onFailure(call: Call<FollowEmployerListModelClass>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<FollowEmployerListModelClass>, response: Response<FollowEmployerListModelClass>) {
                try {
                    Log.d("insertFollowedEmployers", "insertFollowedEmployers Size: ${response.body()?.data?.size}")

                    doAsync {
                        bdjobsInternalDB.followedEmployerDao().deleteAllFollowedEmployer()
                        for (item in response.body()?.data!!) {
                            val followedEmployer = FollowedEmployer(
                                    CompanyID = item?.companyID,
                                    CompanyName = item?.companyName,
                                    JobCount = item?.jobCount)
                            bdjobsInternalDB.followedEmployerDao().insertFollowedEmployer(followedEmployer)
                        }
                        uiThread {
                            insertShortListedJobs()
                        }
                    }
                } catch (e: Exception) {
                    logException(e)
                    insertShortListedJobs()
                }
            }
        }
        )
    }

    private fun insertShortListedJobs() {

        ApiServiceJobs.create().getShortListedJobs(p_id = bdjobsUserSession.userId, encoded = ENCODED_JOBS).enqueue(object : Callback<JobListModel> {
            override fun onFailure(call: Call<JobListModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<JobListModel>, response: Response<JobListModel>) {
                try {
                    Log.d("insertShortListedJobs", "insertShortListedJobs Size: ${response.body()?.data?.size}")

                    doAsync {
                        bdjobsInternalDB.shortListedJobDao().deleteAllShortListedJobs()
                        for (item in response.body()?.data!!) {
                            val shortlistedJob = ShortListedJobs(
                                    jobid= item?.jobid,
                                    jobtitle = item?.jobTitle,
                                    companyname= item?.companyName,
                                    deadline = item?.deadline,
                                    eduRec= item?.eduRec,
                                    experience = item?.experience,
                                    standout= item?.standout,
                                    logo = item?.logo,
                                    lantype= item?.lantype
                            )

                            bdjobsInternalDB.shortListedJobDao().insertShortListedJob(shortlistedJob)
                        }

                        uiThread {
                           goToHomepage()
                        }
                    }
                } catch (e: Exception) {
                    logException(e)
                   goToHomepage()
                }

            }

        })


    }

    private fun insertMyBdJobsStats() {


    }


}