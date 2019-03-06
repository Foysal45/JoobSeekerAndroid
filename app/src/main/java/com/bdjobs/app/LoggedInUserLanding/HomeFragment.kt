package com.bdjobs.app.LoggedInUserLanding

import android.app.Dialog
import android.app.Fragment
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.LastUpdateModel
import com.bdjobs.app.API.ModelClasses.LastSearchCountModel
import com.bdjobs.app.BroadCastReceivers.BackgroundJobBroadcastReceiver
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.Databases.Internal.*
import com.bdjobs.app.FavouriteSearch.FavouriteSearchFilterAdapter
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.ENCODED_JOBS
import com.bdjobs.app.Utilities.Constants.Companion.favSearchFiltersSynced
import com.bdjobs.app.Utilities.Constants.Companion.followedEmployerSynced
import com.bdjobs.app.Utilities.Constants.Companion.jobInvitationSynced
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_home_layout.*
import kotlinx.android.synthetic.main.my_assessment_filter_layout.*
import kotlinx.android.synthetic.main.my_favourite_search_filter_layout.*
import kotlinx.android.synthetic.main.my_followed_employers_layout.*
import kotlinx.android.synthetic.main.my_interview_invitation_layout.*
import kotlinx.android.synthetic.main.my_last_search_filter_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class HomeFragment : Fragment(), BackgroundJobBroadcastReceiver.BackgroundJobListener {


    private lateinit var bdjobsUserSession: BdjobsUserSession
    private lateinit var bdjobsDB: BdjobsDB
    private lateinit var backgroundJobBroadcastReceiver: BackgroundJobBroadcastReceiver
    private val intentFilter = IntentFilter(Constants.BROADCAST_DATABASE_UPDATE_JOB)
    private var followedEmployerList: List<FollowedEmployer>? = null
    private var jobInvitations: List<JobInvitation>? = null
    private var favouriteSearchFilters: List<FavouriteSearch>? = null
    private var b2CCertificationList: List<B2CCertification>? = null
    private var lastSearch: List<LastSearch>? = null
    private lateinit var homeCommunicator: HomeCommunicator
    private var inviteInterviview: String? = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_home_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsUserSession = BdjobsUserSession(activity)
        bdjobsDB = BdjobsDB.getInstance(activity)
        homeCommunicator = activity as HomeCommunicator
        backgroundJobBroadcastReceiver = BackgroundJobBroadcastReceiver()
        nameTV.text = bdjobsUserSession.fullName
        emailTV.text = bdjobsUserSession.email
        profilePicIMGV.loadCircularImageFromUrl(bdjobsUserSession.userPicUrl)
        onClickListeners()
        getLastUpdateFromServer()
        if(Constants.showShortListedPopUp) {
            showShortListedJobsExpirationPopUP()
        }


    }


    private fun onClickListeners() {
        searchIMGV?.setOnClickListener {
            homeCommunicator.goToKeywordSuggestion()
        }
        followedEmployerView?.setOnClickListener {
            homeCommunicator.setTime("0")
            homeCommunicator.goToFollowedEmployerList("follow")
        }
        showAllFavIMGV?.setOnClickListener {
            homeCommunicator.goToFavSearchFilters()
        }
        lastSearchView?.setOnClickListener {
            homeCommunicator.goToJoblistFromLastSearch()
        }
        jobInvitationView?.setOnClickListener {
            homeCommunicator.goToInterviewInvitation("homePage")
        }
        searchBTN?.setOnClickListener {
            homeCommunicator.goToKeywordSuggestion()
        }


    }

    private fun showData() {
        Log.i("DatabaseUpdateJob", "Home Fragment Start: ${Calendar.getInstance().time}")
        if (favSearchFiltersSynced)
            showFavouriteSearchFilters()
        if (jobInvitationSynced)
            showJobInvitation()
       /* if (certificationSynced)
            showCertificationInfo()*/
        if (followedEmployerSynced)
            showFollowedEmployers()

        showLastSearch()
    }

    override fun onResume() {
        super.onResume()
        activity.registerReceiver(backgroundJobBroadcastReceiver, intentFilter)
        BackgroundJobBroadcastReceiver.backgroundJobListener = this
        showData()

    }

    override fun onPause() {
        super.onPause()
        activity.unregisterReceiver(backgroundJobBroadcastReceiver)
    }

    override fun jobInvitationSyncComplete() {
        Log.d("broadCastCheck", "jobInvitationSyncComplete")
        showJobInvitation()
    }

    override fun certificationSyncComplete() {
        Log.d("broadCastCheck", "certificationSyncComplete")
        //showCertificationInfo()
    }

    override fun followedEmployerSyncComplete() {
        Log.d("broadCastCheck", "followedEmployerSyncComplete")
        showFollowedEmployers()
    }

    override fun favSearchFilterSyncComplete() {
        Log.d("broadCastCheck", "favSearchFilterSyncComplete")
        showFavouriteSearchFilters()
    }

    private fun showFollowedEmployers() {
        doAsync {
            followedEmployerList = bdjobsDB.followedEmployerDao().getAllFollowedEmployer()
            val followedEmployerJobCount = bdjobsDB.followedEmployerDao().getJobCountOfFollowedEmployer()
            uiThread {
                showBlankLayout()
                followedEmployerView?.hide()
                if (!followedEmployerList.isNullOrEmpty()) {
                    followEmplowercounterTV?.text = followedEmployerJobCount?.toString()
                    Log.d("followEmplowercounterTV", "followEmplowercounterTV: $followedEmployerJobCount")
                    var followedCompanyNames = ""
                    followedEmployerList?.forEach { item ->
                        followedCompanyNames += item.CompanyName + ","
                    }
                    followedCompanyNameTV?.text = followedCompanyNames.removeLastComma()
                    blankCL?.hide()
                    mainLL?.show()
                    followedEmployerView?.show()
                }
            }
        }

    }

    private fun showJobInvitation() {
        doAsync {
            jobInvitations = bdjobsDB.jobInvitationDao().getAllJobInvitation()
            uiThread {
                showBlankLayout()
                jobInvitationView?.hide()
                if (!jobInvitations.isNullOrEmpty()) {
                    var companyNames = ""
                    jobInvitations?.forEach { item ->
                        companyNames += item?.companyName + ","
                    }
                    jobInvitedCompanyNameTV?.text = companyNames.removeLastComma()
                    jobInvitationcounterTV?.text = jobInvitations?.size.toString()
                    blankCL?.hide()
                    mainLL?.show()
                    jobInvitationView?.show()
                }
            }
        }
    }

    private fun showFavouriteSearchFilters() {
        doAsync {
            favouriteSearchFilters = bdjobsDB.favouriteSearchFilterDao().getLatest2FavouriteSearchFilter()
            val allfavsearch = bdjobsDB.favouriteSearchFilterDao().getAllFavouriteSearchFilter()
            uiThread {
                showBlankLayout()
                favSearchView?.hide()
                try {
                    if (!favouriteSearchFilters.isNullOrEmpty()) {
                        val favouriteSearchFilterAdapter = FavouriteSearchFilterAdapter(items = (favouriteSearchFilters as MutableList<FavouriteSearch>?)!!, context = activity)
                        favRV?.adapter = favouriteSearchFilterAdapter
                        blankCL?.hide()
                        mainLL?.show()
                        myfavSearchTV?.text = "My favourite search filters (${allfavsearch?.size})"
                        favSearchView?.show()
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        }

    }

    private fun showCertificationInfo() {
        doAsync {
            b2CCertificationList = bdjobsDB.b2CCertificationDao().getAllB2CCertification()
            uiThread {
                showBlankLayout()
                assesmentView?.hide()
                if (!b2CCertificationList.isNullOrEmpty()) {
                    var jobRoles = ""
                    b2CCertificationList?.forEach { item ->
                        jobRoles += item?.jobRole + ","
                    }
                    jobRolesTV?.text = jobRoles
                    certificationCounterTV?.text = b2CCertificationList?.size.toString().removeLastComma()
                    blankCL?.hide()
                    mainLL?.show()
                    assesmentView?.show()
                }
            }
        }
    }

    private fun showBlankLayout() {
        if (followedEmployerList.isNullOrEmpty()
                && jobInvitations.isNullOrEmpty()
                && favouriteSearchFilters.isNullOrEmpty()
                && b2CCertificationList.isNullOrEmpty()
                && lastSearch.isNullOrEmpty()
        ) {
            mainLL?.hide()
            blankCL?.show()
        }
    }

    private fun showLastSearch() {
        doAsync {
            lastSearch = bdjobsDB.lastSearchDao().getLastSearch()
            uiThread {
                showBlankLayout()
                lastSearchView?.hide()
                if (!lastSearch.isNullOrEmpty()) {

                    val searchData = lastSearch?.get(0)
                    lastPrgrs?.show()
                    lastSearchcounterTV?.text=""
                    ApiServiceJobs.create().getLastSearchCount(
                            jobLevel = searchData?.jobLevel,
                            Newspaper = searchData?.newsPaper,
                            armyp = searchData?.armyp,
                            bc = searchData?.blueColur,
                            category = searchData?.category,
                            deadline = searchData?.deadline,
                            encoded = ENCODED_JOBS,
                            experience = searchData?.experince,
                            gender = searchData?.gender,
                            genderB = searchData?.genderB,
                            industry = searchData?.industry,
                            isFirstRequest = searchData?.isFirstRequest,
                            jobNature = searchData?.jobnature,
                            jobType = searchData?.jobType,
                            keyword = searchData?.keyword,
                            lastJPD = searchData?.lastJPD,
                            location = searchData?.location,
                            org = searchData?.organization,
                            pageid = searchData?.pageId,
                            pg = searchData?.pageNumber,
                            postedWithin = searchData?.postedWithIn,
                            qAge = searchData?.age,
                            rpp = searchData?.rpp,
                            slno = searchData?.slno,
                            version = searchData?.version
                    ).enqueue(object : Callback<LastSearchCountModel> {
                        override fun onFailure(call: Call<LastSearchCountModel>, t: Throwable) {
                            error("onFailure", t)
                        }

                        override fun onResponse(call: Call<LastSearchCountModel>, response: Response<LastSearchCountModel>) {
                            try {
                                Log.d("jobCount", "jobCount ${response?.body()?.data!![0]?.totaljobs}")
                                lastPrgrs?.hide()
                                lastSearchcounterTV?.text = response?.body()?.data!![0]?.totaljobs

                                if (response.body()?.data?.get(0)?.totaljobs?.length!! > 3) {
                                    lastSearchcounterTV?.textSize = 14.0F
                                }
                            } catch (e: Exception) {
                                lastPrgrs?.hide()
                                lastSearchcounterTV?.text = "0"
                                logException(e)
                            }
                        }
                    })


                    searchFilterTV?.text = getFilterString(searchData!!)
                    if (!searchData?.keyword?.isBlank()!!) {
                        keywordTV?.text = searchData?.keyword
                    } else {
                        keywordTV?.text = "-"
                    }
                    srchDateTV?.text = searchData?.searchTime?.toSimpleDateString()
                    srchTimeTV?.text = searchData?.searchTime?.toSimpleTimeString()
                    lastSearchView?.show()
                    blankCL?.hide()
                    mainLL?.show()
                }
            }
        }
    }

    private fun getFilterString(search: LastSearch): String? {
        val dataStorage = DataStorage(activity)
        val age = dataStorage.getAgeRangeNameByID(search.age)
        val newsPaper = dataStorage.getNewspaperNameById(search.newsPaper)
        val functionalCat = dataStorage.getCategoryNameByID(search.category)
        val location = dataStorage.getLocationNameByID(search.location)
        val organization = dataStorage.getJobSearcOrgTypeByID(search.organization)
        val jobNature = dataStorage.getJobNatureByID(search.jobnature)
        val jobLevel = dataStorage.getJobLevelByID(search.jobLevel)
        val industrialCat = dataStorage.getJobSearcIndustryNameByID(search.industry)
        val experience = dataStorage.getJobExperineceByID(search.experince)
        val jobtype = dataStorage.getJobTypeByID(search.jobType)
        val genderb = dataStorage.getGenderByID(search.genderB)
        var retiredArmy = ""

        search.armyp?.let { string ->
            if (string == "1")
                retiredArmy = "Preferred Retired Army"
        }

        var gender = ""
        search.gender?.let { string ->
            val result: List<String> = string.split(",").map { it.trim() }
            result.forEach {
                gender += dataStorage.getGenderByID(it) + ","
            }
        }

        Log.d("gender", "gender:  ${search.gender}")

        Log.d("gender", "genderb: ${search.genderB}")

        var allValues = ("$functionalCat,$organization,$gender,$genderb,$industrialCat,$location,$age,$jobNature,$jobLevel,$experience,$jobtype,$retiredArmy,$newsPaper")
        Log.d("allValuesN", allValues)
        allValues = allValues.replace("Any".toRegex(), "")
        allValues = allValues.replace("null".toRegex(), "")
        Log.d("allValues", allValues)
        for (i in 0..15) {
            allValues = allValues.replace(",,".toRegex(), ",")
        }
        allValues = allValues.replace(",$".toRegex(), "")
        allValues = if (allValues.startsWith(",")) allValues.substring(1) else allValues

        Log.d("allValuesN", allValues)

        return allValues.removeLastComma()
    }


    private fun showPop() {
        val interviewInvitationDialog = Dialog(activity)
        interviewInvitationDialog?.setContentView(R.layout.interview_invitation_popup)
        interviewInvitationDialog?.setCancelable(true)
        interviewInvitationDialog?.show()
        val InterviewTVCount = interviewInvitationDialog.findViewById<TextView>(R.id.interview_invitation_count_tv)
        val cancelBTN = interviewInvitationDialog.findViewById(R.id.cancel) as ImageView
        val interviewList_MBTN = interviewInvitationDialog.findViewById(R.id.viewList_MBTN) as MaterialButton

        InterviewTVCount.text = inviteInterviview

        cancelBTN?.setOnClickListener {
            interviewInvitationDialog?.dismiss()
        }
        interviewList_MBTN?.setOnClickListener {
            interviewInvitationDialog?.dismiss()
            homeCommunicator.goToInterviewInvitation("popup")
        }
    }

    private fun getLastUpdateFromServer() {
        ApiServiceMyBdjobs.create().getLastUpdate(
                userId = bdjobsUserSession.userId,
                decodeId = bdjobsUserSession.decodId
        ).enqueue(object : Callback<LastUpdateModel> {
            override fun onFailure(call: Call<LastUpdateModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<LastUpdateModel>, response: Response<LastUpdateModel>) {
                try {
                    inviteInterviview = response.body()?.data?.get(0)?.inviteInterviview
                    Log.d("google", "google = $inviteInterviview")

                    if (inviteInterviview?.toInt()!! > 0) {
                        showPop()
                    }

                    try {
                        Constants.changePassword_Eligibility = response.body()?.data?.get(0)?.changePassword_Eligibility!!
                        bdjobsUserSession.updateIsResumeUpdate(response.body()?.data?.get(0)?.isResumeUpdate!!)
                        bdjobsUserSession.updateIsCvPosted(response.body()?.data?.get(0)?.isCVPosted!!)
                        bdjobsUserSession.updateTrainingId(response.body()?.data?.get(0)?.trainingId!!)
                        bdjobsUserSession.updateEmail(response.body()?.data?.get(0)?.email!!)
                        bdjobsUserSession.updateFullName(response.body()?.data?.get(0)?.name!!)
                        bdjobsUserSession.updateUserName(response.body()?.data?.get(0)?.userName!!)
                        bdjobsUserSession.updateCatagoryId(response.body()?.data?.get(0)?.catId!!)
                        bdjobsUserSession.updateUserPicUrl(response.body()?.data?.get(0)?.userPicUrl?.trim()!!)
                        Log.d("changePassword","changePassword_Eligibility = ${response.body()?.data?.get(0)?.changePassword_Eligibility!!}")
                    } catch (e: Exception) {
                        logException(e)
                    }

                } catch (e: Exception) {
                    logException(e)
                }


            }

        })
    }


    private fun showShortListedJobsExpirationPopUP(){

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 2)
        val deadlineNext2Days = calendar.time

        doAsync {
            val shortlistedjobs = bdjobsDB.shortListedJobDao().getShortListedJobsBYDeadline(deadlineNext2Days)
            uiThread {
                Log.d("ShortListedJobPopup", "Job found: ${shortlistedjobs.size}")

                if (shortlistedjobs.isNotEmpty()) {
                    val dialog = Dialog(activity)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(true)
                    dialog.setContentView(R.layout.layout_shortlistedjob_pop_up)
                    dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    val showButton = dialog.findViewById<Button>(R.id.bcYesTV)
                    val cancelIV = dialog.findViewById<ImageView>(R.id.deleteIV)
                    val jobCountTV = dialog.findViewById<TextView>(R.id.textView49)
                    val checkBox = dialog.findViewById<CheckBox>(R.id.checkBox2)
                    // val deadlineCG = dialog.findViewById<ChipGroup>(R.id.deadlineCG)
                    checkBox.setOnCheckedChangeListener { _, isChecked ->
                        Constants.showShortListedPopUp = !isChecked
                    }


                    var job = "Job"
                    if (shortlistedjobs.size > 1)
                        job = "Jobs"

                    jobCountTV.text = "${shortlistedjobs.size} $job found"

                    //selectChip(deadlineCG,"Next 2 days")

                    cancelIV.setOnClickListener {
                        dialog.dismiss()
                    }

                    showButton.setOnClickListener {
                        homeCommunicator.setShortListFilter("Next 2 days")
                        homeCommunicator.goToShortListedFragment(2)
                        dialog?.dismiss()
                    }
                    dialog.show()
                }
            }
        }

    }


}