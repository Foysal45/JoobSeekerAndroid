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
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationManagerCompat
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.LastSearchCountModel
import com.bdjobs.app.API.ModelClasses.LastUpdateModel
import com.bdjobs.app.Ads.Ads
import com.bdjobs.app.BroadCastReceivers.BackgroundJobBroadcastReceiver
import com.bdjobs.app.databases.External.DataStorage
import com.bdjobs.app.databases.internal.*
import com.bdjobs.app.FavouriteSearch.FavouriteSearchFilterAdapter
import com.bdjobs.app.Notification.NotificationHelper
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.ENCODED_JOBS
import com.bdjobs.app.Utilities.Constants.Companion.favSearchFiltersSynced
import com.bdjobs.app.Utilities.Constants.Companion.followedEmployerSynced
import com.bdjobs.app.Utilities.Constants.Companion.jobInvitationSynced
import com.bdjobs.app.Utilities.Constants.Companion.liveInvitationSynced
import com.bdjobs.app.Utilities.Constants.Companion.videoInvitationSynced

import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import kotlinx.android.synthetic.main.fragment_home_layout.*
import kotlinx.android.synthetic.main.layout_all_interview_invitation.*
import kotlinx.android.synthetic.main.my_assessment_filter_layout.*
import kotlinx.android.synthetic.main.my_favourite_search_filter_layout.*
import kotlinx.android.synthetic.main.my_followed_employers_layout.*
import kotlinx.android.synthetic.main.my_interview_invitation_layout.*
import kotlinx.android.synthetic.main.my_last_search_filter_layout.*
import kotlinx.android.synthetic.main.my_video_interview_invitations_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), BackgroundJobBroadcastReceiver.BackgroundJobListener {

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Timber.d("called onSaveInstanceState")
    }


    private lateinit var bdjobsUserSession: BdjobsUserSession
    private lateinit var bdjobsDB: BdjobsDB
    private lateinit var backgroundJobBroadcastReceiver: BackgroundJobBroadcastReceiver
    private val intentFilter = IntentFilter(Constants.BROADCAST_DATABASE_UPDATE_JOB)
    private var followedEmployerList: List<FollowedEmployer>? = null
    private var jobInvitations: List<JobInvitation>? = null
    private var videoInvitations: List<VideoInvitation>? = null
    private var favouriteSearchFilters: List<FavouriteSearch>? = null
    private var b2CCertificationList: List<B2CCertification>? = null
    private var lastSearch: List<LastSearch>? = null
    private lateinit var homeCommunicator: HomeCommunicator
    private var inviteInterviview: String? = ""
    private var videoInterviview: String? = ""
    private var liveInterview: String? = ""
    lateinit var notificationHelper: NotificationHelper


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsUserSession = BdjobsUserSession(activity)
        bdjobsDB = BdjobsDB.getInstance(activity)
        homeCommunicator = activity as HomeCommunicator
        backgroundJobBroadcastReceiver = BackgroundJobBroadcastReceiver()
        nameTV?.text = bdjobsUserSession.fullName
        emailTV?.text = bdjobsUserSession.email

        profilePicIMGV?.loadCircularImageFromUrl(bdjobsUserSession.userPicUrl)
        onClickListeners()
        getLastUpdateFromServer()
        //showAd()
    }

    private fun showAd() {
        val adLoader = AdLoader.Builder(activity, "ca-app-pub-3940256099942544/2247696110")
                .forUnifiedNativeAd { ad: UnifiedNativeAd ->
                    // Show the ad.
                    val styles = NativeTemplateStyle.Builder().withMainBackgroundColor(ColorDrawable(Color.parseColor("#FFFFFF"))).build()
                    home_small_template?.setStyles(styles)
                    home_small_template?.setNativeAd(ad)

                    home_medium_template?.setStyles(styles)
                    home_medium_template?.setNativeAd(ad)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(errorCode: Int) {
                        // Handle the failure by logging, altering the UI, and so on.
                        //Log.d("adLoader", "error code: $errorCode")
                    }
                })
                .withNativeAdOptions(NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }


    private fun alertAboutShortlistedJobs() {
        try {
            val shortlistedDate = bdjobsUserSession.shortListedDate
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("dd-MMM-yyyy")
            val dtcrnt = df.format(c)
            //Log.d("formattedDate", "dtprev: $shortlistedDate  dtcrnt: $dtcrnt")
            if (shortlistedDate != dtcrnt) {
                showShortListedJobsExpirationPopUP()
            }
        } catch (e: Exception) {
            logException(e)
        }
    }


    private fun onClickListeners() {
        profilePicIMGV?.setOnClickListener {
            homeCommunicator.gotoEditresume()
        }
        searchIMGV?.setOnClickListener {
            homeCommunicator.gotoJobSearch()
        }
        followedEmployerView?.setOnClickListener {
            homeCommunicator.setTime("0")
            homeCommunicator.goToFollowedEmployerList("follow")
        }
        showAllFavIMGV?.setOnClickListener {
            homeCommunicator.goToFavSearchFilters()
        }
        lastSearchView?.setOnClickListener {
            Log.d("rakib", "clicked")
            homeCommunicator.goToJoblistFromLastSearch()
        }
        cl_general_interview?.setOnClickListener {
            homeCommunicator.goToInterviewInvitation("homePage")
            try {
                NotificationManagerCompat.from(activity).cancel(Constants.NOTIFICATION_INTERVIEW_INVITATTION)
            } catch (e: Exception) {
            }
        }
        cl_video_interview?.setOnClickListener {
            homeCommunicator.goToVideoInvitation("homePage")
            try {
                NotificationManagerCompat.from(activity).cancel(Constants.NOTIFICATION_VIDEO_INTERVIEW)
            } catch (e: Exception) {
            }
        }

        cl_live_interview?.setOnClickListener {
            homeCommunicator.goToLiveInvitation("homePage")
        }

        searchBTN?.setOnClickListener {
            homeCommunicator.gotoJobSearch()
        }
        newSearchBTN?.setOnClickListener {
            homeCommunicator.gotoJobSearch()
        }
        notificationIMGV?.setOnClickListener {
            homeCommunicator.goToNotifications()
        }

    }

    private fun showData() {
        Log.i("DatabaseUpdateJob", "Home Fragment Start: ${Calendar.getInstance().time}")
        if (favSearchFiltersSynced)
            showFavouriteSearchFilters()
        if (jobInvitationSynced) {
            Timber.tag("home").d("general complete")
            showAllInvitations()
        }
        //showJobInvitation()
        if (videoInvitationSynced) {
            Timber.tag("home").d("video complete")
            showAllInvitations()
        }
        //showVideoInvitation()

        if (liveInvitationSynced) {
            Timber.tag("home").d("live complete")
            showAllInvitations()
        }

        /* if (certificationSynced)
             showCertificationInfo()*/
        if (followedEmployerSynced)
            showFollowedEmployers()

        showLastSearch()

        if (jobInvitationSynced && videoInvitationSynced && liveInvitationSynced) {
            Timber.tag("home").d("all complete")

            //showAllInvitations()
        }

    }

    private fun showAllInvitations() {
        try {
            blankCL?.hide()
            mainLL?.show()

            if (Constants.liveInvitation == "0" && Constants.videoInvitation == "0" && Constants.generalInvitation == "0"){
                allInterview?.hide()
                blankCL?.show()
                newSearchBTN?.hide()
            } else{
                allInterview?.show()
                tv_live_interview_count.text = Constants.liveInvitation
                tv_video_interview_count.text = Constants.videoInvitation
                tv_general_interview_count.text = Constants.generalInvitation
            }




        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showNotificationCount() {
        try {
            bdjobsUserSession = BdjobsUserSession(activity)
            if (bdjobsUserSession.notificationCount!! <= 0) {
                notificationCountTV?.hide()
            } else {
                notificationCountTV?.show()
                if (bdjobsUserSession.notificationCount!! > 99) {
                    notificationCountTV?.text = "99+"

                } else {
                    notificationCountTV?.text = "${bdjobsUserSession.notificationCount!!}"

                }
            }
        } catch (e: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
//        activity?.registerReceiver(backgroundJobBroadcastReceiver, intentFilter)
        //BackgroundJobBroadcastReceiver.notificationUpdateListener = this
        BackgroundJobBroadcastReceiver.backgroundJobListener = this
        showNotificationCount()
        showData()
        alertAboutShortlistedJobs()

    }

    override fun onPause() {
        super.onPause()
//        activity?.unregisterReceiver(backgroundJobBroadcastReceiver)
    }

    override fun jobInvitationSyncComplete() {
        //Log.d("broadCastCheck", "jobInvitationSyncComplete")
        //showJobInvitation()
        Timber.tag("home").d("general complete override")
        showAllInvitations()

    }

    override fun videoInvitationSyncComplete() {
//        Log.d("broadCastCheck", "videoInvitationSyncComplete")
        //showVideoInvitation()
        Timber.tag("home").d("video complete override")
        showAllInvitations()

    }

    override fun liveInvitationSyncComplete() {
        Timber.tag("home").d("live complete override")
        showAllInvitations()
    }

    override fun certificationSyncComplete() {
        //Log.d("broadCastCheck", "certificationSyncComplete")
        //showCertificationInfo()
    }

    override fun followedEmployerSyncComplete() {
        //Log.d("broadCastCheck", "followedEmployerSyncComplete")
        showFollowedEmployers()
    }

    override fun favSearchFilterSyncComplete() {
        //Log.d("broadCastCheck", "favSearchFilterSyncComplete")
        showFavouriteSearchFilters()
    }

    private fun showFollowedEmployers() {
        doAsync {
            followedEmployerList = bdjobsDB.followedEmployerDao().getAllFollowedEmployer()
            val followedEmployerJobCount = bdjobsDB.followedEmployerDao().getJobCountOfFollowedEmployer()
            uiThread {
//                showBlankLayout()
                followedEmployerView?.hide()
                if (!followedEmployerList.isNullOrEmpty()) {
                    followEmplowercounterTV?.text = followedEmployerJobCount.toString()
                    //Log.d("followEmplowercounterTV", "followEmplowercounterTV: $followedEmployerJobCount")
                    var followedCompanyNames = ""
                    followedEmployerList?.forEach { item ->
                        followedCompanyNames += item.CompanyName + ","
                    }
                    followedCompanyNameTV?.text = followedCompanyNames.removeLastComma()
                    blankCL?.hide()
                    mainLL?.show()
                    newSearchBTN?.show()
                    followedEmployerView?.show()
                }
            }
        }

    }

    private fun showJobInvitation() {
        doAsync {
            jobInvitations = bdjobsDB.jobInvitationDao().getAllJobInvitation()
            uiThread {
                try {
//                    showBlankLayout()
                    jobInvitationView?.hide()
                    if (!jobInvitations.isNullOrEmpty()) {
                        var companyNames = ""
                        jobInvitations?.forEach { item ->
                            companyNames += item.companyName + ","
                        }
                        jobInvitedCompanyNameTV?.text = companyNames.removeLastComma()
                        jobInvitationcounterTV?.text = jobInvitations?.size.toString()
                        blankCL?.hide()
                        mainLL?.show()
                        newSearchBTN?.show()
                        jobInvitationView?.show()
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun showVideoInvitation() {
        doAsync {
            videoInvitations = bdjobsDB.videoInvitationDao().getAllVideoInvitation()
            uiThread {
                try {
                    showBlankLayout()
                    videoInvitationView?.hide()
                    if (!videoInvitations.isNullOrEmpty()) {
                        var companyNames = ""
                        videoInvitations?.forEach { item ->
                            companyNames += item.companyName + ","
                        }

                        videoInvitationCompanyNameTV?.text = companyNames.removeLastComma()
                        videoInvitationCounterTV?.text = videoInvitations?.size.toString()
                        blankCL?.hide()
                        mainLL?.show()
                        newSearchBTN?.show()
                        videoInvitationView?.show()
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun showFavouriteSearchFilters() {
        doAsync {
            favouriteSearchFilters = bdjobsDB.favouriteSearchFilterDao().getLatest2FavouriteSearchFilter()
            val allfavsearch = bdjobsDB.favouriteSearchFilterDao().getAllFavouriteSearchFilter()
            uiThread {
//                showBlankLayout()
                favSearchView?.hide()
                if (!favouriteSearchFilters.isNullOrEmpty()) {
                    try {
                        val favouriteSearchFilterAdapter = FavouriteSearchFilterAdapter(items = (favouriteSearchFilters as MutableList<FavouriteSearch>?)!!, context = activity)
                        favRV?.adapter = favouriteSearchFilterAdapter
                        blankCL?.hide()
                        mainLL?.show()
                        newSearchBTN?.show()
                        myfavSearchTV?.text = "My favourite search filters (${allfavsearch.size})"
                        favSearchView?.show()
                    } catch (e: Exception) {
                        logException(e)
                    }
                }
            }
        }

    }

    private fun showCertificationInfo() {
        doAsync {
            b2CCertificationList = bdjobsDB.b2CCertificationDao().getAllB2CCertification()
            uiThread {
                try {
                    showBlankLayout()
                    assesmentView?.hide()
                    if (!b2CCertificationList.isNullOrEmpty()) {
                        var jobRoles = ""
                        b2CCertificationList?.forEach { item ->
                            jobRoles += item.jobRole + ","
                        }
                        jobRolesTV?.text = jobRoles
                        certificationCounterTV?.text = b2CCertificationList?.size.toString().removeLastComma()
                        blankCL?.hide()
                        mainLL?.show()
                        newSearchBTN?.show()
                        newSearchBTN?.show()
                        assesmentView?.show()
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun showBlankLayout() {
//        if (followedEmployerList.isNullOrEmpty()
//                && jobInvitations.isNullOrEmpty()
//                && favouriteSearchFilters.isNullOrEmpty()
//                && b2CCertificationList.isNullOrEmpty()
//                && lastSearch.isNullOrEmpty()
//                && (Constants.liveInvitation == "0" && Constants.videoInvitation == "0" && Constants.generalInvitation == "0")
//        ) {
//            mainLL?.hide()
//            blankCL?.show()
//            newSearchBTN?.hide()
//        }
    }

    private fun showLastSearch() {
        doAsync {
            lastSearch = bdjobsDB.lastSearchDao().getLastSearch()
            uiThread {
//                showBlankLayout()
                lastSearchView?.hide()
                if (!lastSearch.isNullOrEmpty()) {

                    val searchData = lastSearch?.get(0)
                    lastPrgrs?.show()
                    lastSearchcounterTV?.text = ""
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
                            version = searchData?.version,
                            workPlace = searchData?.workPlace,
                            personWithDisability = searchData?.personWithDisability
                    ).enqueue(object : Callback<LastSearchCountModel> {
                        override fun onFailure(call: Call<LastSearchCountModel>, t: Throwable) {
                            error("onFailure", t)
                        }

                        override fun onResponse(call: Call<LastSearchCountModel>, response: Response<LastSearchCountModel>) {
                            try {
                                //Log.d("jobCount", "jobCount ${response.body()?.data!![0]?.totaljobs}")
                                lastPrgrs?.hide()
                                lastSearchcounterTV?.text = response.body()?.data!![0]?.totaljobs

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
                    if (!searchData.keyword.isNullOrBlank()) {
                        keywordTV?.text = searchData.keyword
                    } else {
                        keywordTV?.text = "-"
                    }
                    srchDateTV?.text = searchData.searchTime?.toSimpleDateString()
                    srchTimeTV?.text = searchData.searchTime?.toSimpleTimeString()
                    lastSearchView?.show()
                    blankCL?.hide()
                    mainLL?.show()
                    newSearchBTN?.show()
                    newSearchBTN?.show()
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
        var workPlace = ""
        var personWithDisability = ""

        search.armyp?.let { string ->
            if (string == "1")
                retiredArmy = "Preferred Retired Army"
        }

        search.workPlace?.let {
            if (it == "1")
                workPlace = "Work From Home"
        }

        search.personWithDisability?.let {
            if (it == "1")
                personWithDisability = "Person With Disability"
        }

        var gender = ""
        search.gender?.let { string ->
            val result: List<String> = string.split(",").map { it.trim() }
            result.forEach {
                gender += dataStorage.getGenderByID(it) + ","
            }
        }

        //Log.d("gender", "gender:  ${search.gender}")

        //Log.d("gender", "genderb: ${search.genderB}")

        var allValues = ("$functionalCat,$organization,$gender,$genderb,$industrialCat,$location,$age,$jobNature,$jobLevel,$experience,$jobtype,$retiredArmy,$newsPaper,$workPlace,$personWithDisability")
        //Log.d("allValuesN", allValues)
        allValues = allValues.replace("Any".toRegex(), "")
        allValues = allValues.replace("null".toRegex(), "")
        //Log.d("allValues", allValues)
        for (i in 0..15) {
            allValues = allValues.replace(",,".toRegex(), ",")
        }
        allValues = allValues.replace(",$".toRegex(), "")
        allValues = if (allValues.startsWith(",")) allValues.substring(1) else allValues

        //Log.d("allValuesN", allValues)

        return allValues.removeLastComma()
    }

    private fun showInterviewInvitationPop() {
        val interviewInvitationDialog = Dialog(activity)

        interviewInvitationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        interviewInvitationDialog?.setCancelable(true)

        interviewInvitationDialog?.setContentView(R.layout.interview_invitation_popup)
        val InterviewCV = interviewInvitationDialog?.findViewById<CardView>(R.id.cardView2)
        val VideoInterviewCV = interviewInvitationDialog?.findViewById<CardView>(R.id.cardView3)
        val LiveInterviewCV = interviewInvitationDialog?.findViewById<CardView>(R.id.cardView4)


        val InterviewTVCount = interviewInvitationDialog?.findViewById<TextView>(R.id.interview_invitation_count_tv)
        val VideoInterviewTVCount = interviewInvitationDialog?.findViewById<TextView>(R.id.interview_invitation_count_tv_3)
        val LiveInterviewTVCount = interviewInvitationDialog?.findViewById<TextView>(R.id.interview_invitation_count_tv_4)

        val cancelBTN = interviewInvitationDialog?.findViewById(R.id.cancel) as ImageView

        InterviewTVCount?.text = inviteInterviview
        VideoInterviewTVCount?.text = videoInterviview
        LiveInterviewTVCount?.text = liveInterview

        if (inviteInterviview.equals("0")) InterviewCV.visibility = View.GONE
        if (videoInterviview.equals("0")) VideoInterviewCV.visibility = View.GONE
        if (liveInterview.equals("0")) LiveInterviewCV.visibility = View.GONE



        cancelBTN?.setOnClickListener {
            interviewInvitationDialog?.dismiss()
        }

        InterviewCV?.setOnClickListener {
            interviewInvitationDialog?.dismiss()
            homeCommunicator.goToInterviewInvitation("popup")
            try {
                NotificationManagerCompat.from(activity).cancel(Constants.NOTIFICATION_INTERVIEW_INVITATTION)
            } catch (e: Exception) {
            }
            interviewInvitationDialog?.cancel()

        }

        VideoInterviewCV?.setOnClickListener {
            interviewInvitationDialog?.dismiss()
            homeCommunicator.goToVideoInvitation("popup")
            try {
                NotificationManagerCompat.from(activity).cancel(Constants.NOTIFICATION_VIDEO_INTERVIEW)
            } catch (e: Exception) {
            }
            interviewInvitationDialog?.cancel()

        }

        LiveInterviewCV?.setOnClickListener {
            interviewInvitationDialog?.dismiss()
            homeCommunicator.goToLiveInvitation("popup")
//            try {
//                NotificationManagerCompat.from(activity).cancel(Constants.NOTIFICATION_VIDEO_INTERVIEW)
//            } catch (e: Exception) {
//            }
            interviewInvitationDialog?.cancel()
        }


        interviewInvitationDialog?.show()

    }

    private fun showVideoInterviewSlider() {

        videoInvitationSliderLayout.show()
        videoInterviewTV.isSelected = true
        videoInterviewTV.text = getString(R.string.homepage_slider_text, videoInterviview)
        videoInvitationSliderLayout.onClick {
            homeCommunicator.goToVideoInvitation("slider")
            try {
                NotificationManagerCompat.from(activity).cancel(Constants.NOTIFICATION_VIDEO_INTERVIEW)
            } catch (e: Exception) {
            }
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

                    val catIds = response.body()?.data?.get(0)?.catId?.split(",")?.toList()

                    if (response.body()?.data?.get(0)?.subscriptionType == "1") {
                        catIds?.let {
                            try {
                                for (item in it) {
                                    if (item.isNotEmpty()) {
                                        activity.subscribeToFCMTopic(item)
                                        //Log.d("rakib", item)
                                    }
                                }
                            } catch (e: Exception) {
                            }
                        }
                    } else if (response.body()?.data?.get(0)?.subscriptionType == "0") {
                        catIds?.let {
                            try {
                                for (item in it) {
                                    if (item.isNotEmpty()) {
                                        activity.unsubscribeFromFCMTopic(item)
                                        //Log.d("rakib", item)
                                    }
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }


                    //Log.d("rakib cat id", "${catIds?.size}")

                    inviteInterviview = response.body()?.data?.get(0)?.inviteInterviview
                    videoInterviview = response.body()?.data?.get(0)?.videoInterviview
                    liveInterview = response.body()?.data?.get(0)?.liveInterview

                    //Log.d("google", "google = $inviteInterviview")

                    if (inviteInterviview?.toInt()!! > 0 || videoInterviview?.toInt()!! > 0 || liveInterview?.toInt()!! > 0) {
                        showInterviewInvitationPop()
                    }

                    try {
                        Constants.changePassword_Eligibility = response.body()?.data?.get(0)?.changePassword_Eligibility!!
                        Constants.isSMSFree = response.body()?.data?.get(0)?.isSmsFree!!
                        bdjobsUserSession.updateIsResumeUpdate(response.body()?.data?.get(0)?.isResumeUpdate!!)
                        bdjobsUserSession.updateIsCvPosted(response.body()?.data?.get(0)?.isCVPosted!!)
                        bdjobsUserSession.updateTrainingId(response.body()?.data?.get(0)?.trainingId!!)
                        bdjobsUserSession.updateEmail(response.body()?.data?.get(0)?.email!!)
                        bdjobsUserSession.updateFullName(response.body()?.data?.get(0)?.name!!)
                        bdjobsUserSession.updateUserName(response.body()?.data?.get(0)?.userName!!)
                        bdjobsUserSession.updateCatagoryId(response.body()?.data?.get(0)?.catId!!)
                        bdjobsUserSession.updateUserPicUrl(response.body()?.data?.get(0)?.userPicUrl?.trim()!!)
                        bdjobsUserSession.updateJobApplyLimit(response.body()?.data?.get(0)?.jobApplyLimit)
                        bdjobsUserSession.updateJobApplyThreshold(response.body()?.data?.get(0)?.appliedJobsThreshold)
                        bdjobsUserSession.updatePostingDate(response.body()?.data?.get(0)?.postingDate!!)
//                        Constants.applyRestrictionStatus = response.body()?.data?.get(0)?.applyRestrictionStatus!!
//                        Constants.appliedJobsThreshold = response.body()?.data?.get(0)?.appliedJobsThreshold!!.toInt()
                        //Log.d("changePassword", "changePassword_Eligibility = ${response.body()?.data?.get(0)?.changePassword_Eligibility!!}")
                    } catch (e: Exception) {
                        logException(e)
                    }

                } catch (e: Exception) {
                    logException(e)
                }
            }

        })
    }

    private fun showShortListedJobsExpirationPopUP() {


        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 2)
        val deadlineNext2Days = calendar.time

        doAsync {
            val shortlistedjobs = bdjobsDB.shortListedJobDao().getShortListedJobsBYDeadline(deadlineNext2Days)
            uiThread {
                //Log.d("ShortListedJobPopup", "Job found: ${shortlistedjobs.size}")

                try {
                    if (shortlistedjobs.isNotEmpty()) {

//                        notificationHelper = NotificationHelper(activity)
//                        notificationHelper.notify(
//                                Constants.NOTIFICATION_ALERT,
//                                notificationHelper.prepareNotification(
//                                        "test title",
//                                        "You have ${shortlistedjobs.size} shortlisted jobs",
//                                        type = NOTIFICATION_TYPE_ALERT_NOTIFICATION
//                                )
//                        )

                        val dialog = Dialog(activity)
                        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog?.setCancelable(true)
                        dialog?.setContentView(R.layout.layout_shortlistedjob_pop_up)
                        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                        val showButton = dialog?.findViewById<Button>(R.id.bcYesTV)
                        val cancelIV = dialog?.findViewById<ImageView>(R.id.deleteIV)
                        val jobCountTV = dialog?.findViewById<TextView>(R.id.textView49)
                        val checkBox = dialog?.findViewById<CheckBox>(R.id.checkBox2)

                        val ad_small_template = dialog?.findViewById<TemplateView>(R.id.ad_small_template)

                        Ads.showNativeAd(ad_small_template, activity)

                        checkBox?.setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                val c = Calendar.getInstance().time
                                val df = SimpleDateFormat("dd-MMM-yyyy")
                                val formattedDate = df.format(c)
                                //Log.d("formattedDate", "formattedDate: $formattedDate")
                                bdjobsUserSession.insertShortlListedPopupDate(formattedDate)
                            } else if (!isChecked) {
                                bdjobsUserSession.insertShortlListedPopupDate("19-Mar-1919")
                            }
                        }

                        var job = "Job"
                        if (shortlistedjobs.size > 1)
                            job = "Jobs"

                        jobCountTV?.text = "${shortlistedjobs.size} $job found"

                        cancelIV?.setOnClickListener {
                            dialog.dismiss()
                        }

                        showButton?.setOnClickListener {
                            homeCommunicator.setShortListFilter("Next 2 days")
                            homeCommunicator.goToShortListedFragment(2)
                            dialog.dismiss()
                        }
                        dialog?.show()
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        }

    }

    fun updateNotificationView(count: Int?) {
        //Log.d("rakib", "in home fragment $count")
        if (count!! > 0) {
            notificationCountTV?.show()
            if (count <= 99)
                notificationCountTV?.text = "$count"
            else
                notificationCountTV?.text = "99+"
        } else {
            notificationCountTV?.hide()
        }


    }



}