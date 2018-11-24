package com.bdjobs.app.Utilities

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.ProgressBar
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
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent



class DatabaseSync(private val context: Context, private var goToHome: Boolean = true, private val progressBar: ProgressBar?=null) {

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
                doAsync {
                    response.body()?.data?.let { items ->
                        Log.d("XZXfg", "insertFavourite Size: ${items.size}")

                        bdjobsInternalDB.favouriteSearchFilterDao().deleteAllFavouriteSearch()
                        for (item in items) {


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

                    }
                    uiThread { insertFollowedEmployers() }
                }


            }
        })
    }

    private fun goToHomepage() {

        Log.d("XZXfg", "goToHomepage :${goToHome}")
        if (goToHome) {
            val activity = context as Activity
            progressBar?.let {pbar->
               activity.stopProgressBar(pbar)
            }
            val intent = Intent(activity, MainLandingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
            activity.finishAffinity()
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

                doAsync {
                    response.body()?.data?.let { items ->
                        Log.d("XZXfg", "insertFollowedEmployers Size: ${items.size}")

                        bdjobsInternalDB.followedEmployerDao().deleteAllFollowedEmployer()
                        for (item in items) {
                            val followedEmployer = FollowedEmployer(
                                    CompanyID = item?.companyID,
                                    CompanyName = item?.companyName,
                                    JobCount = item?.jobCount)
                            bdjobsInternalDB.followedEmployerDao().insertFollowedEmployer(followedEmployer)
                        }

                    }
                    uiThread { insertShortListedJobs() }
                }


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

                doAsync {
                    response.body()?.data?.let {items ->
                        Log.d("XZXfg", "insertShortListedJobs Size: ${items.size}")
                        bdjobsInternalDB.shortListedJobDao().deleteAllShortListedJobs()
                        for (item in items) {
                            val deadline = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(item?.deadline)
                            Log.d("deadline", "deadline: $deadline")
                            val shortlistedJob = ShortListedJobs(
                                    jobid = item.jobid,
                                    jobtitle = item.jobTitle,
                                    companyname = item.companyName,
                                    deadline = deadline,
                                    eduRec = item.eduRec,
                                    experience = item.experience,
                                    standout = item.standout,
                                    logo = item.logo,
                                    lantype = item.lantype
                            )

                            bdjobsInternalDB.shortListedJobDao().insertShortListedJob(shortlistedJob)
                        }
                    }
                    response.body()?.common?.appliedid?.let { items ->
                        Log.d("XZXfg", "appliedid Size: ${items.size}")

                        bdjobsInternalDB.appliedJobDao().deleteAllAppliedJobs()
                        for (item in items) {
                            val appliedJobs = AppliedJobs(
                                    appliedid = item
                            )

                            bdjobsInternalDB.appliedJobDao().insertAppliedJobs(appliedJobs)
                        }
                    }

                    uiThread { goToHomepage() }
                }

            }

        })


    }

    private fun insertMyBdJobsStats() {


    }


}