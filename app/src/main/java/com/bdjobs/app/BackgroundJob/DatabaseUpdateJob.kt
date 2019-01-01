package com.bdjobs.app.BackgroundJob

import android.content.Context
import android.util.Log
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.*
import com.bdjobs.app.Databases.Internal.*
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.Constants.Companion.api_request_result_code_ok
import com.bdjobs.app.Utilities.error
import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import com.bdjobs.app.Utilities.Constants.Companion.BROADCAST_DATABASE_UPDATE_JOB
import com.bdjobs.app.Utilities.Constants.Companion.certificationSynced
import com.bdjobs.app.Utilities.Constants.Companion.favSearchFiltersSynced
import com.bdjobs.app.Utilities.Constants.Companion.followedEmployerSynced
import com.bdjobs.app.Utilities.Constants.Companion.jobInvitationSynced


class DatabaseUpdateJob(private val appContext: Context) : Job() {
    private val bdjobsUserSession = BdjobsUserSession(appContext)
    val bdjobsInternalDB: BdjobsDB = BdjobsDB.getInstance(appContext)


    companion object {
        const val TAG = "database_update_job"
        fun runJobImmediately() {
            val jobId = JobRequest.Builder(DatabaseUpdateJob.TAG)
                    .startNow()
                    .build()
                    .schedule()
        }
    }


    override fun onRunJob(params: Params): Result {
        Log.d("DatabaseUpdateJob", "DatabaseUpdateJob Start : ${Calendar.getInstance().time}")
        insertFavouriteSearchFilter()
        insertJobInvitation()
        insertCertificationList()
        insertFollowedEmployers()
        insertShortListedJobs()
        return Result.SUCCESS
    }



    private fun insertCertificationList() {
        Log.d("XZXfg", "insertCertificationList")
        ApiServiceMyBdjobs.create().getAssesmentCompleteList(userId = bdjobsUserSession.userId, decodeId = bdjobsUserSession.decodId).enqueue(object : Callback<AssesmentCompleteModel> {
            override fun onFailure(call: Call<AssesmentCompleteModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<AssesmentCompleteModel>, response: Response<AssesmentCompleteModel>) {
                response.body()?.statuscode?.let { status ->
                    if (status == api_request_result_code_ok) {
                        response.body()?.data?.let { items ->
                            doAsync {
                                for (item in items) {
                                    val b2CCertification = B2CCertification(
                                            jobRole = item?.jobRole,
                                            testDate = item?.testDate,
                                            sid = item?.sid,
                                            jid = item?.jid,
                                            jrid = item?.jrid,
                                            aid = item?.aid,
                                            sType = item?.sType,
                                            ajid = item?.ajid,
                                            res = item?.res)
                                    bdjobsInternalDB.b2CCertificationDao().insertB2CCertification(b2CCertification)
                                }
                                uiThread {
                                    val intent = Intent(BROADCAST_DATABASE_UPDATE_JOB)
                                    intent.putExtra("job","insertCertificationList")
                                    appContext.sendBroadcast(intent)
                                    certificationSynced = true
                                    Log.d("DatabaseUpdateJob", "insertCertificationList Finish : ${Calendar.getInstance().time}")
                                }
                            }
                        }
                    }
                }
            }

        })
    }

    private fun insertFavouriteSearchFilter() {
        Log.d("XZXfg", "insertFavourite")
        ApiServiceJobs.create().getFavouriteSearchFilters(encoded = Constants.ENCODED_JOBS, userID = bdjobsUserSession.userId).enqueue(object : Callback<FavouritSearchFilterModelClass> {
            override fun onFailure(call: Call<FavouritSearchFilterModelClass>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<FavouritSearchFilterModelClass>, response: Response<FavouritSearchFilterModelClass>) {
                doAsync {
                    bdjobsInternalDB.favouriteSearchFilterDao().deleteAllFavouriteSearch()
                    response.body()?.data?.let { items ->
                        Log.d("XZXfg", "insertFavourite Size: ${items.size}")
                        for (item in items) {

                            Log.d("createdonF", "created onF: ${item.createdon} \n updatedOn onF: ${item.updatedon}")

                            var cratedOn: Date? = null
                            try {
                                cratedOn = SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse(item.createdon)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            var updatedOn: Date? = null
                            try {
                                updatedOn = SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse(item.updatedon)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            Log.d("createdon", "created on: $cratedOn \n updatedOn on: $updatedOn")

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
                                    createdon = cratedOn,
                                    updatedon = updatedOn,
                                    totaljobs = item?.totaljobs
                            )
                            bdjobsInternalDB.favouriteSearchFilterDao().insertFavouriteSearchFilter(favouriteSearch)
                        }

                    }

                    uiThread {
                        val intent = Intent(BROADCAST_DATABASE_UPDATE_JOB)
                        intent.putExtra("job","insertFavouriteSearchFilter")
                        appContext.sendBroadcast(intent)
                        favSearchFiltersSynced = true
                        Log.d("DatabaseUpdateJob", "insertFavouriteSearchFilter Finish : ${Calendar.getInstance().time}")
                    }
                }


            }
        })
    }

    private fun insertFollowedEmployers() {
        ApiServiceJobs.create().getFollowEmployerList(userID = bdjobsUserSession.userId, decodeId = bdjobsUserSession.decodId, encoded = Constants.ENCODED_JOBS).enqueue(object : Callback<FollowEmployerListModelClass> {
            override fun onFailure(call: Call<FollowEmployerListModelClass>, t: Throwable) {
                error("onFailure", t)

            }

            override fun onResponse(call: Call<FollowEmployerListModelClass>, response: Response<FollowEmployerListModelClass>) {
                Log.d("XZXfg", "insertFollowedEmployers")
                doAsync {
                    bdjobsInternalDB.followedEmployerDao().deleteAllFollowedEmployer()
                    response.body()?.data?.let { items ->
                        Log.d("XZXfg", "insertFollowedEmployers Size: ${items.size}")

                        for (item in items) {
                            var followedOn: Date? = null
                            try {
                                followedOn = SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse(item?.followedOn)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            Log.d("followedOn", "followedOn on: $followedOn")

                            val followedEmployer = FollowedEmployer(
                                    CompanyID = item?.companyID,
                                    CompanyName = item?.companyName,
                                    FollowedOn = followedOn,
                                    JobCount = item?.jobCount)
                            bdjobsInternalDB.followedEmployerDao().insertFollowedEmployer(followedEmployer)
                        }

                    }

                    uiThread {
                        val intent = Intent(BROADCAST_DATABASE_UPDATE_JOB)
                        intent.putExtra("job","insertFollowedEmployers")
                        appContext.sendBroadcast(intent)
                        followedEmployerSynced = true
                        Log.d("DatabaseUpdateJob", "insertFollowedEmployers Finish : ${Calendar.getInstance().time}")
                    }
                }


            }
        }
        )
    }

    private fun insertShortListedJobs() {
        Log.d("XZXfg", "insertShortListedJobs")
        ApiServiceJobs.create().getShortListedJobs(p_id = bdjobsUserSession.userId, encoded = Constants.ENCODED_JOBS).enqueue(object : Callback<ShortListedJobModel> {
            override fun onFailure(call: Call<ShortListedJobModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<ShortListedJobModel>, response: Response<ShortListedJobModel>) {

                doAsync {
                    bdjobsInternalDB.shortListedJobDao().deleteAllShortListedJobs()
                    response.body()?.data?.let { items ->
                        Log.d("XZXfg", "insertShortListedJobs Size: ${items.size}")

                        for (item in items) {
                            val deadline = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(item?.deadline)
                            Log.d("deadline", "deadline: $deadline")
                            val shortlistedJob = ShortListedJobs(
                                    jobid = item.jobid,
                                    jobtitle = item.jobtitle,
                                    companyname = item.companyname,
                                    deadline = deadline,
                                    eduRec = item.eduRec,
                                    experience = item.experience,
                                    standout = item.standout,
                                    logo = item.logo,
                                    lantype = item.lantype
                            )

                            Log.d("item.jobTitle", "item.jobTitle: ${item.jobtitle}")
                            Log.d("item.jobTitle", "item.companyName: ${item.companyname}")
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

                    uiThread {
                        val intent = Intent(BROADCAST_DATABASE_UPDATE_JOB)
                        intent.putExtra("job","insertShortListedJobs")
                        appContext.sendBroadcast(intent)
                        Log.d("DatabaseUpdateJob", "insertShortListedJobs Finish : ${Calendar.getInstance().time}")
                    }
                }

            }

        })


    }

    private fun insertJobInvitation() {

        ApiServiceMyBdjobs.create().getJobInvitationList(userId = bdjobsUserSession.userId, decodeId = bdjobsUserSession.decodId).enqueue(object : Callback<JobInvitationListModel> {
            override fun onFailure(call: Call<JobInvitationListModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<JobInvitationListModel>, response: Response<JobInvitationListModel>) {
                response.body()?.statuscode?.let { status ->
                    if (status == api_request_result_code_ok) {
                        response.body()?.data?.let { items ->
                            doAsync {
                                for (item in items) {
                                    val jobInvitation = JobInvitation(companyName = item?.companyName,
                                            inviteDate = item?.inviteDate,
                                            jobId = item?.jobId,
                                            jobTitle = item?.jobTitle,
                                            seen = item?.seen)

                                    bdjobsInternalDB.jobInvitationDao().insertJobInvitation(jobInvitation)

                                }
                                uiThread {
                                    val intent = Intent(BROADCAST_DATABASE_UPDATE_JOB)
                                    intent.putExtra("job","insertJobInvitation")
                                    appContext.sendBroadcast(intent)
                                    Log.d("DatabaseUpdateJob", "insertJobInvitation Finish : ${Calendar.getInstance().time}")
                                    jobInvitationSynced = true
                                }
                            }
                        }
                    }
                }
            }

        })

    }


}