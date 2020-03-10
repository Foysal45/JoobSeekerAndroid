package com.bdjobs.app.Workmanager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.*
import com.bdjobs.app.Databases.External.DBHelper
import com.bdjobs.app.Databases.Internal.*
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.Constants.Companion.BROADCAST_DATABASE_UPDATE_JOB
import com.bdjobs.app.Utilities.Constants.Companion.api_request_result_code_ok
import com.bdjobs.app.Utilities.Constants.Companion.certificationSynced
import com.bdjobs.app.Utilities.Constants.Companion.favSearchFiltersSynced
import com.bdjobs.app.Utilities.Constants.Companion.followedEmployerSynced
import com.bdjobs.app.Utilities.Constants.Companion.jobInvitationSynced
import com.bdjobs.app.Utilities.debug
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.logException
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class DatabaseUpdateWorker(val appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    var bdjobsUserSession: BdjobsUserSession = BdjobsUserSession(appContext)
    var bdjobsInternalDB: BdjobsDB = BdjobsDB.getInstance(appContext)
    lateinit var pref: SharedPreferences
    var dbUpdateDate: String? = ""


    override fun doWork(): Result {

        insertFavouriteSearchFilter()
        insertJobInvitation()
        //insertCertificationList()
        insertFollowedEmployers()
        insertShortListedJobs()
        getMybdjobsCountData("0")
        getMybdjobsCountData("1")
        getIsCvUploaded()
        updateExternalDatabase()
        getUnSeenNotificationsCount()

        return Result.success()
    }

    private fun insertFavouriteSearchFilter() {
        ApiServiceJobs.create().getFavouriteSearchFilters(encoded = Constants.ENCODED_JOBS, userID = bdjobsUserSession.userId).enqueue(object : Callback<FavouritSearchFilterModelClass> {
            override fun onFailure(call: Call<FavouritSearchFilterModelClass>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<FavouritSearchFilterModelClass>, response: Response<FavouritSearchFilterModelClass>) {
                doAsync {
                    bdjobsInternalDB.favouriteSearchFilterDao().deleteAllFavouriteSearch()
                    response.body()?.data?.let { items ->
                        //Log.d("XZXfg", "insertFavourite Size: ${items.size}")
                        for (item in items) {

                            //Log.d("createdonF", "created onF: ${item.createdon} \n updatedOn onF: ${item.updatedon}")

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

                            //Log.d("createdon", "created on: $cratedOn \n updatedOn on: $updatedOn")

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
                        intent.putExtra("job", "insertFavouriteSearchFilter")
                        appContext.sendBroadcast(intent)
                        favSearchFiltersSynced = true
                        //Log.d("DatabaseUpdateJob", "insertFavouriteSearchFilter Finish : ${Calendar.getInstance().time}")
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
                                    var inviteDate: Date? = null
                                    try {
                                        inviteDate = SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse(item?.inviteDate)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    val jobInvitation = JobInvitation(companyName = item?.companyName,
                                            inviteDate = inviteDate,
                                            jobId = item?.jobId,
                                            jobTitle = item?.jobTitle,
                                            seen = item?.seen)

                                    bdjobsInternalDB.jobInvitationDao().insertJobInvitation(jobInvitation)

                                }
                                uiThread {
                                    val intent = Intent(BROADCAST_DATABASE_UPDATE_JOB)
                                    intent.putExtra("job", "insertJobInvitation")
                                    appContext.sendBroadcast(intent)
                                    //Log.d("DatabaseUpdateJob", "insertJobInvitation Finish : ${Calendar.getInstance().time}")
                                    jobInvitationSynced = true
                                }
                            }
                        }
                    }
                }
            }

        })

    }

    fun updateExternalDatabase() {

        pref = appContext.getSharedPreferences(Constants.name_sharedPref, Context.MODE_PRIVATE)

        dbUpdateDate = pref.getString(Constants.key_db_update, Constants.dfault_date_db_update)

        ApiServiceJobs.create().getDbInfo(dbUpdateDate!!).enqueue(object : Callback<DatabaseUpdateModel> {
            override fun onFailure(call: Call<DatabaseUpdateModel>?, t: Throwable?) {
                try {
                    debug("getDbInfo: ${t?.message!!}")
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<DatabaseUpdateModel>?, response: Response<DatabaseUpdateModel>?) {

                try {
                    if (response?.body()?.messageType == "1") {

                        if (response.body()?.update == "1") {
                            //Log.d("Rakib", response.body()?.dblink)
                            downloadDatabase(response.body()?.dblink!!, response.body()?.lastupdate!!)
                        } else {
                        }
                    } else {
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        })

    }

    fun downloadDatabase(dbDownloadLink: String, updateDate: String) {

        ApiServiceJobs.create().downloadDatabaseFile(dbDownloadLink).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                try {
                    if (response?.isSuccessful!!) {
                        //debug("getDbInfo: server contacted and has file")
                        val writtenToDisk = writeResponseBodyToDisk(response.body()!!)
                        // debug("getDbInfo: file download was a success? $writtenToDisk")

                        if (writtenToDisk) {
                            pref.edit {
                                putString(Constants.key_db_update, updateDate)
                            }
                        }
                    } else {
                        debug("getDbInfo: server contact failed")
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        })
    }

    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        try {

            val dbFile = File(DBHelper.DB_PATH + DBHelper.DB_NAME)

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val fileReader = ByteArray(4096)

                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0

                inputStream = body.byteStream()
                outputStream = FileOutputStream(dbFile)

                while (true) {
                    val read = inputStream!!.read(fileReader)

                    if (read == -1) {
                        break
                    }

                    outputStream.write(fileReader, 0, read)

                    fileSizeDownloaded += read.toLong()

                    debug("dbFile download: $fileSizeDownloaded of $fileSize")
                }

                outputStream.flush()

                return true
            } catch (e: IOException) {
                logException(e)
                return false
            } finally {
                if (inputStream != null) {
                    inputStream.close()
                }

                if (outputStream != null) {
                    outputStream.close()
                }
            }
        } catch (e: IOException) {
            logException(e)
            return false
        }
    }

    private fun getUnSeenNotificationsCount() {
        doAsync {
            val count = bdjobsInternalDB.notificationDao().getNotificationCount()
            //Log.d("rakib", "notification count $count")
            bdjobsUserSession.updateNotificationCount(count)
        }
    }

    private fun getMybdjobsCountData(activityDate: String) {
        ApiServiceMyBdjobs.create().mybdjobStats(
                userId = bdjobsUserSession.userId,
                decodeId = bdjobsUserSession.decodId,
                isActivityDate = activityDate,
                trainingId = bdjobsUserSession.trainingId,
                isResumeUpdate = bdjobsUserSession.IsResumeUpdate
        ).enqueue(object : Callback<StatsModelClass> {
            override fun onFailure(call: Call<StatsModelClass>, t: Throwable) {

            }

            override fun onResponse(call: Call<StatsModelClass>, response: Response<StatsModelClass>) {

                try {
                    var jobsApplied: String? = ""
                    var emailResume: String? = ""
                    var viewdResume: String? = ""
                    var followedEmployers: String? = ""
                    var interviewInvitation: String? = ""
                    var employerMessage: String? = ""

                    response?.body()?.data?.forEach { itt ->
                        when (itt?.title) {
                            Constants.session_key_mybdjobscount_jobs_applied -> {
                                jobsApplied = itt?.count
                            }
                            Constants.session_key_mybdjobscount_times_emailed_resume -> {
                                emailResume = itt?.count
                            }
                            Constants.session_key_mybdjobscount_employers_viwed_resume -> {
                                viewdResume = itt?.count
                            }
                            Constants.session_key_mybdjobscount_employers_followed -> {
                                followedEmployers = itt?.count
                            }
                            Constants.session_key_mybdjobscount_interview_invitation -> {
                                interviewInvitation = itt?.count
                            }
                            Constants.session_key_mybdjobscount_message_by_employers -> {
                                employerMessage = itt?.count
                            }
                        }

                    }

                    if (activityDate == "0") {
                        //alltime
                        bdjobsUserSession.insertMybdjobsAlltimeCountData(
                                jobsApplied = jobsApplied,
                                emailResume = emailResume,
                                employerViewdResume = viewdResume,
                                followedEmployers = followedEmployers,
                                interviewInvitation = interviewInvitation,
                                messageByEmployers = employerMessage
                        )
                    } else if (activityDate == "1") {
                        //last_moth
                        bdjobsUserSession.insertMybdjobsLastMonthCountData(
                                jobsApplied = jobsApplied,
                                emailResume = emailResume,
                                employerViewdResume = viewdResume,
                                followedEmployers = followedEmployers,
                                interviewInvitation = interviewInvitation,
                                messageByEmployers = employerMessage
                        )
                    }

                } catch (e: Exception) {
                    logException(e)
                }
            }

        })
    }

    private fun getIsCvUploaded() {
        ApiServiceMyBdjobs.create().getCvFileAvailable(
                userID = bdjobsUserSession.userId,
                decodeID = bdjobsUserSession.decodId

        ).enqueue(object : Callback<FileInfo> {
            override fun onFailure(call: Call<FileInfo>, t: Throwable) {
                error("onFailure", t)

            }

            override fun onResponse(call: Call<FileInfo>, response: Response<FileInfo>) {
                try {
                    if (response.isSuccessful) {
                        bdjobsUserSession.updateUserCVUploadStatus(response.body()?.statuscode)
                    }

                } catch (e: Exception) {
                }
            }
        })
    }

    private fun insertCertificationList() {
        //Log.d("XZXfg", "insertCertificationList")
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
                                    intent.putExtra("job", "insertCertificationList")
                                    appContext.sendBroadcast(intent)
                                    certificationSynced = true
                                    //Log.d("DatabaseUpdateJob", "insertCertificationList Finish : ${Calendar.getInstance().time}")
                                }
                            }
                        }
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
                //Log.d("XZXfg", "insertFollowedEmployers")
                doAsync {
                    bdjobsInternalDB.followedEmployerDao().deleteAllFollowedEmployer()
                    response.body()?.data?.let { items ->
                        //Log.d("XZXfg", "insertFollowedEmployers Size: ${items.size}")

                        for (item in items) {
                            var followedOn: Date? = null
                            try {
                                followedOn = SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse(item?.followedOn)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            //Log.d("followedOn", "followedOn on: $followedOn")

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
                        intent.putExtra("job", "insertFollowedEmployers")
                        appContext.sendBroadcast(intent)
                        followedEmployerSynced = true
                        //Log.d("DatabaseUpdateJob", "insertFollowedEmployers Finish : ${Calendar.getInstance().time}")
                    }
                }


            }
        }
        )
    }

    private fun insertShortListedJobs() {
        //Log.d("XZXfg", "insertShortListedJobs")
        ApiServiceJobs.create().getShortListedJobs(p_id = bdjobsUserSession.userId, encoded = Constants.ENCODED_JOBS).enqueue(object : Callback<ShortListedJobModel> {
            override fun onFailure(call: Call<ShortListedJobModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<ShortListedJobModel>, response: Response<ShortListedJobModel>) {

                doAsync {
                    bdjobsInternalDB.shortListedJobDao().deleteAllShortListedJobs()
                    response.body()?.data?.let { items ->
                        //Log.d("XZXfg", "insertShortListedJobs Size: ${items.size}")

                        for (item in items) {
                            val deadline = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(item?.deadline)
                            //Log.d("deadline", "deadline: $deadline")
                            val shortlistedJob = ShortListedJobs(
                                    jobid = item?.jobid,
                                    jobtitle = item?.jobtitle,
                                    companyname = item?.companyname,
                                    deadline = deadline,
                                    eduRec = item?.eduRec,
                                    experience = item?.experience,
                                    standout = item?.standout,
                                    logo = item?.logo,
                                    lantype = item?.lantype
                            )

                            //Log.d("item.jobTitle", "item.jobTitle: ${item?.jobtitle}")
                            //Log.d("item.jobTitle", "item.companyName: ${item?.companyname}")
                            bdjobsInternalDB.shortListedJobDao().insertShortListedJob(shortlistedJob)
                        }
                    }
                    response.body()?.common?.appliedid?.let { items ->
                        //Log.d("XZXfg", "appliedid Size: ${items.size}")

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
                        intent.putExtra("job", "insertShortListedJobs")
                        appContext.sendBroadcast(intent)
                        //Log.d("DatabaseUpdateJob", "insertShortListedJobs Finish : ${Calendar.getInstance().time}")
                    }
                }

            }

        })


    }


}