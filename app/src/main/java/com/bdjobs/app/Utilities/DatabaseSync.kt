package com.bdjobs.app.Utilities

import android.app.Activity
import android.content.Context
import android.util.Log
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.FavouritSearchFilterModelClass
import com.bdjobs.app.API.ModelClasses.FollowEmployerListModelClass
import com.bdjobs.app.API.ModelClasses.JobListModel
import com.bdjobs.app.Databases.Internal.*
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants.Companion.ENCODED_JOBS
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class DatabaseSync(private val context: Context, private var goToHome: Boolean = true) {

    private val bdjobsUserSession = BdjobsUserSession(context)
    val bdjobsInternalDB: BdjobsDB = BdjobsDB.getInstance(context)

    fun insertDataAndGoToHomepage() {
        Log.d("XZXfg", "insertFavouriteSearchFilters :${goToHome}")

        ApiServiceJobs.create().getFavouriteSearchFilters(encoded = ENCODED_JOBS, userID = bdjobsUserSession.userId).enqueue(object : Callback<FavouritSearchFilterModelClass> {
            override fun onFailure(call: Call<FavouritSearchFilterModelClass>, t: Throwable) {
                error("onFailure", t)
                insertFollowedEmployers()
            }

            override fun onResponse(call: Call<FavouritSearchFilterModelClass>, response: Response<FavouritSearchFilterModelClass>) {

                response.body()?.data?.size?.let {
                    doAsync {
                        bdjobsInternalDB.favouriteSearchFilterDao().deleteAllFavouriteSearch()
                        for (item in response.body()?.data!!) {
                            Log.d("insertFavourite", "insertFavourite Size: ${item?.filtername}")

                            val createdOn = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH).parse(item?.createdon)
                            val updatedOn = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH).parse(item?.updatedon)

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
                                    createdon = createdOn,
                                    updatedon = updatedOn,
                                    totaljobs = item?.totaljobs
                            )

                            val a = bdjobsInternalDB.favouriteSearchFilterDao().insertFavouriteSearchFilter(favouriteSearch)
                            Log.d("insertFavourite", "insert: $a")

                        }

                    }
                }
                insertFollowedEmployers()

            }
        })
    }

    private fun goToHomepage() {

        Log.d("XZXfg", "goToHomepage :${goToHome}")
        if (goToHome) {
            val activity = context as Activity
            activity.startActivity<MainLandingActivity>()
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            activity.finish()
        }
    }

    private fun insertFollowedEmployers() {

        Log.d("XZXfg", "insertFollowedEmployers :${goToHome}")

        ApiServiceJobs.create().getFollowEmployerList(userID = bdjobsUserSession.userId, decodeId = bdjobsUserSession.decodId, encoded = ENCODED_JOBS).enqueue(object : Callback<FollowEmployerListModelClass> {
            override fun onFailure(call: Call<FollowEmployerListModelClass>, t: Throwable) {
                error("onFailure", t)
                insertShortListedJobs()
            }

            override fun onResponse(call: Call<FollowEmployerListModelClass>, response: Response<FollowEmployerListModelClass>) {
                response.body()?.data?.size.let {
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

                    }
                }
                insertShortListedJobs()

            }
        }
        )
    }

    private fun insertShortListedJobs() {
        Log.d("XZXfg", "insertShortListedJobs :${goToHome}")
        ApiServiceJobs.create().getShortListedJobs(p_id = bdjobsUserSession.userId, encoded = ENCODED_JOBS).enqueue(object : Callback<JobListModel> {
            override fun onFailure(call: Call<JobListModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<JobListModel>, response: Response<JobListModel>) {
                response.body()?.data?.size?.let {
                    Log.d("insertShortListedJobs", "insertShortListedJobs Size: ${response.body()?.data?.size}")

                    doAsync {
                        bdjobsInternalDB.shortListedJobDao().deleteAllShortListedJobs()
                        for (item in response.body()?.data!!) {
                            val deadline = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(item?.deadline)
                            Log.d("deadline", "deadline: $deadline")
                            val shortlistedJob = ShortListedJobs(
                                    jobid = item?.jobid,
                                    jobtitle = item?.jobTitle,
                                    companyname = item?.companyName,
                                    deadline = deadline,
                                    eduRec = item?.eduRec,
                                    experience = item?.experience,
                                    standout = item?.standout,
                                    logo = item?.logo,
                                    lantype = item?.lantype
                            )

                            bdjobsInternalDB.shortListedJobDao().insertShortListedJob(shortlistedJob)
                        }
                    }
                }

                response.body()?.common?.appliedid?.size.let {

                    doAsync {
                        bdjobsInternalDB.appliedJobDao().deleteAllAppliedJobs()
                        for (item in response.body()?.common?.appliedid!!) {
                            val appliedJobs = AppliedJobs(
                                    appliedid = item
                            )

                            bdjobsInternalDB.appliedJobDao().insertAppliedJobs(appliedJobs)
                        }
                    }
                }
                goToHomepage()
            }

        })


    }

    private fun insertMyBdJobsStats() {


    }


}