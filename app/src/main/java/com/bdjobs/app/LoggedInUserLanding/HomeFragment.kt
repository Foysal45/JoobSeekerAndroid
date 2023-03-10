package com.bdjobs.app.LoggedInUserLanding

import android.app.Dialog
import android.content.Intent
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
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.LastSearchCountModel
import com.bdjobs.app.API.ModelClasses.LastUpdateModel
import com.bdjobs.app.BroadCastReceivers.BackgroundJobBroadcastReceiver
import com.bdjobs.app.FavouriteSearch.FavouriteSearchFilterAdapter
import com.bdjobs.app.Notification.Models.CommonNotificationModel
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.utilities.*
import com.bdjobs.app.utilities.Constants.Companion.ENCODED_JOBS
import com.bdjobs.app.utilities.Constants.Companion.favSearchFiltersSynced
import com.bdjobs.app.utilities.Constants.Companion.followedEmployerSynced
import com.bdjobs.app.utilities.Constants.Companion.jobInvitationSynced
import com.bdjobs.app.utilities.Constants.Companion.liveInvitationSynced
import com.bdjobs.app.utilities.Constants.Companion.videoInvitationSynced
import com.bdjobs.app.ads.Ads
import com.bdjobs.app.databases.External.DataStorage
import com.bdjobs.app.databases.internal.*
import com.bdjobs.app.sms.SmsBaseActivity
import com.bdjobs.app.videoResume.VideoResumeActivity
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_home_layout.*
import kotlinx.android.synthetic.main.layout_all_interview_invitation.*
import kotlinx.android.synthetic.main.layout_sms_job_alert_home.*
import kotlinx.android.synthetic.main.my_favourite_search_filter_layout.*
import kotlinx.android.synthetic.main.my_followed_employers_layout.*
import kotlinx.android.synthetic.main.my_last_search_filter_layout.*
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.textColor
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), BackgroundJobBroadcastReceiver.BackgroundJobListener {

    private lateinit var bdJobsUserSession: BdjobsUserSession
    private lateinit var bdJobsDB: BdjobsDB
    private lateinit var backgroundJobBroadcastReceiver: BackgroundJobBroadcastReceiver
    private var followedEmployerList: List<FollowedEmployer>? = null

    //    private var jobInvitations: List<JobInvitation>? = null
//    private var videoInvitations: List<VideoInvitation>? = null
    private var favouriteSearchFilters: List<FavouriteSearch>? = null

    //    private var b2CCertificationList: List<B2CCertification>? = null
    private var lastSearch: List<LastSearch>? = null
    private lateinit var homeCommunicator: HomeCommunicator
    private var inviteInterview: String? = ""
    private var videoInterview: String? = ""
    private var liveInterview: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdJobsUserSession = BdjobsUserSession(requireContext())
        bdJobsDB = BdjobsDB.getInstance(requireContext())
        homeCommunicator = requireContext() as HomeCommunicator
        backgroundJobBroadcastReceiver = BackgroundJobBroadcastReceiver()
        nameTV?.text = bdJobsUserSession.fullName
        emailTV?.text = bdJobsUserSession.email

        profilePicIMGV?.loadCircularImageFromUrl(bdJobsUserSession.userPicUrl)
        onClickListeners()
        getLastUpdateFromServer()

//        if (!bdJobsUserSession.isExpirationMessageShown) {
        alertAboutShortlistedJobs()

//            bdJobsUserSession.isExpirationMessageShown = true
//        }

        // showGeneralPopUp()
        //showAd()
    }


    private fun showAd() {
        val adLoader = AdLoader.Builder(requireContext(), "ca-app-pub-3940256099942544/2247696110")
            .forUnifiedNativeAd { ad: UnifiedNativeAd ->
                // Show the ad.
                val styles = NativeTemplateStyle.Builder()
                    .withMainBackgroundColor(ColorDrawable(Color.parseColor("#FFFFFF"))).build()
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
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build()
            )
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun alertAboutShortlistedJobs() {
        try {
            val shortlistedDate = bdJobsUserSession.shortListedDate
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("dd-MMM-yyyy")
            val dtcrnt = df.format(c)
            //Log.d("formattedDate", "dtprev: $shortlistedDate  dtcrnt: $dtcrnt")
            if (shortlistedDate != dtcrnt) {
                showShortlistJobExpirationInMessageBox()
//                showShortListedJobsExpirationPopUP()
            }

        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun onClickListeners() {

        cl_video_resume?.setOnClickListener {
            startActivity<VideoResumeActivity>()
        }

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
                NotificationManagerCompat.from(requireContext())
                    .cancel(Constants.NOTIFICATION_INTERVIEW_INVITATTION)
            } catch (e: Exception) {
            }
        }
        cl_video_interview?.setOnClickListener {
            homeCommunicator.goToVideoInvitation("homePage")
            try {
                NotificationManagerCompat.from(requireContext())
                    .cancel(Constants.NOTIFICATION_VIDEO_INTERVIEW)
            } catch (e: Exception) {
            }
        }

        cl_live_interview?.setOnClickListener {
            homeCommunicator.goToLiveInvitation("homePage")
        }

        searchBTN?.setOnClickListener {
            homeCommunicator.gotoAllJobSearch()
        }
        newSearchBTN?.setOnClickListener {
            homeCommunicator.gotoAllJobSearch()
            //test
//            context.startActivity(Intent(context,HomeActivity::class.java))
        }
        notificationIMGV?.setOnClickListener {
            homeCommunicator.goToNotifications()
        }
        messageIMGV?.setOnClickListener {
            homeCommunicator.goToMessages()
        }

        cl_start_sms_alert_view.setOnClickListener {
            requireContext().startActivity(
                Intent(requireContext(), SmsBaseActivity::class.java)
                    .putExtra("from", "settings")
            )
        }

        tv_sms_setting_home.setOnClickListener {
            requireContext().startActivity(
                Intent(requireContext(), SmsBaseActivity::class.java)
                    .putExtra("from", "settings")
            )
        }

        tv_purchase_sms.setOnClickListener {
            requireContext().startActivity(Intent(requireContext(), SmsBaseActivity::class.java))
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
            bdJobsUserSession = BdjobsUserSession(requireContext())
            if (bdJobsUserSession.liveInterviewCount == 0 && bdJobsUserSession.videoInterviewCount == 0 && bdJobsUserSession.generalInterviewCount == 0) {
                Timber.d("Showing blank1")
                allInterview?.hide()
                allInterview?.hide()
                smsAlertView.hide()
                newSearchBTN?.hide()
                blankCL?.show()
            } else {
                bdJobsUserSession = BdjobsUserSession(requireContext())
                allInterview?.show()
                tv_live_interview_count.text = bdJobsUserSession.liveInterviewCount.toString()
                tv_video_interview_count.text = bdJobsUserSession.videoInterviewCount.toString()
                tv_general_interview_count.text = bdJobsUserSession.generalInterviewCount.toString()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showNotificationCount() {
        try {
            bdJobsUserSession = BdjobsUserSession(requireContext())
            if (bdJobsUserSession.notificationCount!! <= 0) {
                notificationCountTV?.hide()
            } else {
                notificationCountTV?.show()
                if (bdJobsUserSession.notificationCount!! > 99) {
                    notificationCountTV?.text = "99+"

                } else {
                    notificationCountTV?.text = "${bdJobsUserSession.notificationCount!!}"

                }
            }
        } catch (e: Exception) {
        }
    }

    private fun showMessageCount() {
        try {

            doAsync {
                bdJobsUserSession = BdjobsUserSession(requireContext())
                val count = bdJobsDB.notificationDao().getMessageCount()
                Timber.d("Messages count: $count")
                bdJobsUserSession.updateMessageCount(count)
            }

            if (bdJobsUserSession.messageCount!! <= 0) {
                messageCountTV?.hide()
            } else {
                messageCountTV?.show()
                if (bdJobsUserSession.messageCount!! > 99) {
                    messageCountTV?.text = "99+"

                } else {
                    messageCountTV?.text = "${bdJobsUserSession.messageCount!!}"

                }
            }
        } catch (e: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()

        Timber.d("onResume Triggered")
        BackgroundJobBroadcastReceiver.backgroundJobListener = this

        showData()
        safeFetchSmsAlertStatus()
        showNotificationCount()
        showMessageCount()


    }

    override fun jobInvitationSyncComplete() {
        Timber.tag("home").d("general complete override")
        showAllInvitations()

    }

    override fun videoInvitationSyncComplete() {
        Timber.tag("home").d("video complete override")
        showAllInvitations()

    }

    override fun liveInvitationSyncComplete() {
        Timber.tag("home").d("live complete override")
        showAllInvitations()
    }

    override fun certificationSyncComplete() {
    }

    override fun followedEmployerSyncComplete() {
        showFollowedEmployers()
    }

    override fun favSearchFilterSyncComplete() {
        //Log.d("broadCastCheck", "favSearchFilterSyncComplete")
        showFavouriteSearchFilters()
    }

    private fun showFollowedEmployers() {
        doAsync {
            followedEmployerList = bdJobsDB.followedEmployerDao().getAllFollowedEmployer()
            val followedEmployerJobCount =
                bdJobsDB.followedEmployerDao().getJobCountOfFollowedEmployer()
            uiThread {
                followedEmployerView?.hide()
                if (!followedEmployerList.isNullOrEmpty()) {
                    followEmplowercounterTV?.text = followedEmployerJobCount.toString()
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

    private fun showFavouriteSearchFilters() {
        doAsync {
            favouriteSearchFilters =
                bdJobsDB.favouriteSearchFilterDao().getLatest2FavouriteSearchFilter()
            val allfavsearch = bdJobsDB.favouriteSearchFilterDao().getAllFavouriteSearchFilter()
            uiThread {
                favSearchView?.hide()
                if (!favouriteSearchFilters.isNullOrEmpty()) {
                    try {
                        val favouriteSearchFilterAdapter = FavouriteSearchFilterAdapter(
                            items = (favouriteSearchFilters as MutableList<FavouriteSearch>?)!!,
                            context = requireContext(),
                            onUpdateCounter = object :
                                FavouriteSearchFilterAdapter.OnUpdateCounter {
                                override fun update(count: Int) {

                                }
                            })
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


    private fun showLastSearch() {
        doAsync {
            lastSearch = bdJobsDB.lastSearchDao().getLastSearch()
            uiThread {
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
                        personWithDisability = searchData?.personWithDisability,
                        facilitiesForPWD = searchData?.facilitiesForPWD
                    ).enqueue(object : Callback<LastSearchCountModel> {
                        override fun onFailure(call: Call<LastSearchCountModel>, t: Throwable) {
                            error("onFailure", t)
                        }

                        override fun onResponse(
                            call: Call<LastSearchCountModel>,
                            response: Response<LastSearchCountModel>
                        ) {
                            try {
                                //Log.d("jobCount", "jobCount ${response.body()?.data!![0]?.totaljobs}")
                                lastPrgrs?.hide()
                                lastSearchcounterTV?.text = response.body()?.data!![0]?.totaljobs

                                if (response.body()?.data?.get(0)?.totaljobs?.length!! > 3) {
                                    lastSearchcounterTV?.textSize = 14.0F
                                }

//                                if (bdJobsUserSession.adTypeLanding=="2") {
//                                    try {
//                                        navHostFragmentAD.visibility = View.VISIBLE
//                                        homeCommunicator.goToAjkerDealLive(R.id.navHostFragmentAD)
//                                    } catch (e: Exception) {
//                                    }
//                                } else {
//                                    navHostFragmentAD.visibility = View.GONE
//                                }


                            } catch (e: Exception) {
                                lastPrgrs?.hide()
                                lastSearchcounterTV?.text = "0"
                                logException(e)
                            }
                        }
                    })


//                    Timber.d("Filter Location: ${searchData?.let { it1 -> getFilterString(it1) }}")
                    searchFilterTV?.text = try {
                        searchData?.let { it1 ->
                            if (activity != null) {
                                getFilterString(it1)
                            } else {
                                ""
                            }
                        }
                    } catch (e: Exception) {
                        ""
                    }
                    if (!searchData?.keyword.isNullOrBlank()) {
                        keywordTV?.text = searchData?.keyword
                    } else {
                        keywordTV?.text = "-"
                    }
                    srchDateTV?.text = searchData?.searchTime?.toSimpleDateString()
                    srchTimeTV?.text = searchData?.searchTime?.toSimpleTimeString()
                    lastSearchView?.show()
                    blankCL?.hide()
                    mainLL?.show()
                    newSearchBTN?.show()
                }
                /*else {
//                    homeCommunicator.goToAjkerDealLive(R.id.navHostFragmentAD)
//                    navHostFragmentAD.visibility = View.VISIBLE
                }*/
            }
        }
    }

    private fun getFilterString(search: LastSearch): String {
        val dataStorage = DataStorage(requireContext())
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
        var facilitiesForPWD = ""

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
                personWithDisability = "Jobs prefer person with disability"
        }

        search.facilitiesForPWD?.let {
            if (it == "1")
                facilitiesForPWD = "Companies provide facilities for person with disability"
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

        var allValues =
            ("$functionalCat,$organization,$gender,$genderb,$industrialCat,$location,$age,$jobNature,$jobLevel,$experience,$jobtype,$retiredArmy,$newsPaper,$workPlace,$personWithDisability,$facilitiesForPWD")
        //Log.d("allValuesN", allValues)
//        allValues = allValues.replace("Any".toRegex(), "")
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
        val interviewInvitationDialog = Dialog(requireContext())

        interviewInvitationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        interviewInvitationDialog?.setCancelable(true)

        interviewInvitationDialog.setContentView(R.layout.interview_invitation_popup)
        val interviewCV = interviewInvitationDialog.findViewById<CardView>(R.id.cardView2)
        val videoInterviewCV = interviewInvitationDialog.findViewById<CardView>(R.id.cardView3)
        val liveInterviewCV = interviewInvitationDialog.findViewById<CardView>(R.id.cardView4)


        val interviewTVCount =
            interviewInvitationDialog.findViewById<TextView>(R.id.interview_invitation_count_tv)
        val videoInterviewTVCount =
            interviewInvitationDialog.findViewById<TextView>(R.id.interview_invitation_count_tv_3)
        val liveInterviewTVCount =
            interviewInvitationDialog.findViewById<TextView>(R.id.interview_invitation_count_tv_4)

        val cancelBTN = interviewInvitationDialog?.findViewById(R.id.cancel) as ImageView

        interviewTVCount?.text = inviteInterview
        videoInterviewTVCount?.text = videoInterview
        liveInterviewTVCount?.text = liveInterview

        if (inviteInterview.equals("0")) interviewCV.visibility = View.GONE
        if (videoInterview.equals("0")) videoInterviewCV.visibility = View.GONE
        if (liveInterview.equals("0")) liveInterviewCV.visibility = View.GONE

        cancelBTN?.setOnClickListener {
            interviewInvitationDialog?.dismiss()
        }

        interviewCV?.setOnClickListener {
            interviewInvitationDialog.dismiss()
            homeCommunicator.goToInterviewInvitation("popup")
            try {
                NotificationManagerCompat.from(requireContext())
                    .cancel(Constants.NOTIFICATION_INTERVIEW_INVITATTION)
            } catch (e: Exception) {
            }
            interviewInvitationDialog?.cancel()

        }

        videoInterviewCV?.setOnClickListener {
            interviewInvitationDialog.dismiss()
            homeCommunicator.goToVideoInvitation("popup")
            try {
                NotificationManagerCompat.from(requireContext())
                    .cancel(Constants.NOTIFICATION_VIDEO_INTERVIEW)
            } catch (e: Exception) {
            }
            interviewInvitationDialog?.cancel()

        }

        liveInterviewCV?.setOnClickListener {
            interviewInvitationDialog.dismiss()
            homeCommunicator.goToLiveInvitation("popup")
            interviewInvitationDialog?.cancel()
        }

        interviewInvitationDialog?.show()

    }

    private fun showGeneralPopUp() {
        val generalDialog = Dialog(requireContext())

        generalDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        generalDialog?.setCancelable(true)

        generalDialog.setContentView(R.layout.home_general_popup)
        val mainCL = generalDialog.findViewById<ConstraintLayout>(R.id.main_cl)
        val cardView = generalDialog.findViewById<CardView>(R.id.cv_video_resume)

        val cancelBTN = generalDialog?.findViewById(R.id.cancel) as ImageView


        cancelBTN?.setOnClickListener {
            generalDialog?.dismiss()
        }

        cardView?.setOnClickListener {
            generalDialog.dismiss()
            homeCommunicator.goToResumeManager()
        }

        mainCL?.setOnClickListener {
            generalDialog?.dismiss()
            homeCommunicator.goToResumeManager()

        }
        generalDialog?.show()

    }

    private fun getLastUpdateFromServer() {
        ApiServiceMyBdjobs.create().getLastUpdate(
            userId = bdJobsUserSession.userId,
            decodeId = bdJobsUserSession.decodId
        ).enqueue(object : Callback<LastUpdateModel> {
            override fun onFailure(call: Call<LastUpdateModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(
                call: Call<LastUpdateModel>,
                response: Response<LastUpdateModel>
            ) {
                try {

                    val catIds = response.body()?.data?.get(0)?.catId?.split(",")?.toList()

                    if (response.body()?.data?.get(0)?.subscriptionType == "1") {
                        catIds?.let {
                            try {
                                for (item in it) {
                                    if (item.isNotEmpty()) {
                                        requireActivity().subscribeToFCMTopic(item)
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
                                        requireActivity().unsubscribeFromFCMTopic(item)
                                    }
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }


                    inviteInterview = response.body()?.data?.get(0)?.inviteInterviview
                    videoInterview = response.body()?.data?.get(0)?.videoInterviview
                    liveInterview = response.body()?.data?.get(0)?.liveInterview

                    try {
                        Constants.changePassword_Eligibility =
                            response.body()?.data?.get(0)?.changePassword_Eligibility!!
                        Constants.isSMSFree = response.body()?.data?.get(0)?.isSmsFree!!
                        bdJobsUserSession.updateIsResumeUpdate(response.body()?.data?.get(0)?.isResumeUpdate!!)
                        bdJobsUserSession.updateIsCvPosted(response.body()?.data?.get(0)?.isCVPosted!!)
                        bdJobsUserSession.updateTrainingId(response.body()?.data?.get(0)?.trainingId!!)
                        bdJobsUserSession.updateEmail(response.body()?.data?.get(0)?.email!!)
                        bdJobsUserSession.updateFullName(response.body()?.data?.get(0)?.name!!)
                        bdJobsUserSession.updateUserName(response.body()?.data?.get(0)?.userName!!)
                        bdJobsUserSession.updateCatagoryId(response.body()?.data?.get(0)?.catId!!)
                        bdJobsUserSession.updateUserPicUrl(response.body()?.data?.get(0)?.userPicUrl?.trim()!!)
                        bdJobsUserSession.updateJobApplyLimit(response.body()?.data?.get(0)?.jobApplyLimit)
                        bdJobsUserSession.updateJobApplyThreshold(response.body()?.data?.get(0)?.appliedJobsThreshold)
                        bdJobsUserSession.updatePostingDate(response.body()?.data?.get(0)?.postingDate!!)
                    } catch (e: Exception) {
                        logException(e)
                    }

                } catch (e: Exception) {
                    logException(e)
                }
            }

        })
    }

    /**
     * fetching SMS Alert Status
     */
    private fun safeFetchSmsAlertStatus() {
        lifecycleScope.launch {
            try {
                val response = ApiServiceMyBdjobs.create().getSMSSetting(
                    bdJobsUserSession.userId,
                    bdJobsUserSession.decodId,
                    Constants.APP_ID
                )

                if (response.statuscode == "0" && response.message == "Success") {

                    blankCL?.hide()
                    newSearchBTN?.show()
                    smsAlertView.visibility = View.VISIBLE

                    val data = response.data!![0]
                    val remainingSms = data.remainingSMSAmount?.toInt() ?: 0
                    val smsLimit = data.dailySmsLimit?.toInt() ?: 0

                    smsAlertView.visibility = View.VISIBLE
                    cl_label_views.visibility = View.VISIBLE
                    tv_label_sms_alert_home.visibility = View.VISIBLE
                    cl_left_views.visibility = View.VISIBLE

                    /**
                     * when SMS alert feature is on
                     * checking count of remainingSMS & dailySMSLimit
                     * and showing them both
                     * else only showing remainingSMS value
                     */
                    if (data.smsAlertOn == "True") {
                        tv_sms_alert_status_off_home.visibility = View.GONE
                        tv_sms_alert_status_on_home.visibility = View.VISIBLE

                        /**
                         * if remainingSMS is greater then zero
                         * then showing SMS setting view
                         * else showing the Buy SMS view
                         */
                        if (remainingSms > 0) {
                            tv_purchase_sms.visibility = View.GONE
                            cl_start_sms_alert_view.visibility = View.GONE
                            tv_sms_setting_home.visibility = View.VISIBLE

                            tv_no_package.visibility = View.GONE
                            ll_daily_limit_sms_home.visibility = View.VISIBLE
                            ll_remaining_sms_home.visibility = View.VISIBLE

                            tv_remaining_sms_count_home.text = "$remainingSms"
                            tv_sms_limit_count_home.text = "$smsLimit"
                        } else {
                            cl_start_sms_alert_view.visibility = View.GONE
                            tv_sms_setting_home.visibility = View.GONE
                            tv_purchase_sms.visibility = View.VISIBLE

                            tv_no_package.visibility = View.GONE
                            ll_daily_limit_sms_home.visibility = View.VISIBLE
                            ll_remaining_sms_home.visibility = View.VISIBLE

                            tv_remaining_sms_count_home.text = "$remainingSms"
                            tv_sms_limit_count_home.text = "$smsLimit"
                        }

                        smsColorFormatting(remainingSms)

                    } else {
                        tv_sms_alert_status_on_home.visibility = View.GONE
                        tv_sms_alert_status_off_home.visibility = View.VISIBLE

                        tv_purchase_sms.visibility = View.GONE
                        tv_sms_setting_home.visibility = View.GONE
                        cl_start_sms_alert_view.visibility = View.VISIBLE

                        tv_no_package.visibility = View.GONE
                        ll_daily_limit_sms_home.visibility = View.GONE
                        ll_remaining_sms_home.visibility = View.VISIBLE

                        tv_remaining_sms_count_home.text = "$remainingSms"

                        smsColorFormatting(remainingSms)
                    }


                } else if (response.statuscode == "3") {
                    /*
                     * Showing the SMS alert buy UI
                     * if no package is available
                     */
                    blankCL?.hide()
                    newSearchBTN?.show()
                    smsAlertView.visibility = View.VISIBLE
                    cl_label_views.visibility = View.VISIBLE
                    tv_label_sms_alert_home.visibility = View.VISIBLE
                    tv_sms_alert_status_on_home.visibility = View.GONE
                    tv_sms_alert_status_off_home.visibility = View.GONE

                    cl_left_views.visibility = View.VISIBLE
                    tv_purchase_sms.visibility = View.VISIBLE
                    tv_sms_setting_home.visibility = View.GONE
                    cl_start_sms_alert_view.visibility = View.GONE

                    tv_no_package.visibility = View.VISIBLE


                } else {
                    /**
                     * not showing the SMS alert view. if status is not 0 or 3
                     */
                    smsAlertView.visibility = View.GONE
                }
            } catch (e: Exception) {
                Timber.e("Exception while fetching SMS Alert Status : ${e.localizedMessage}")
                try {
                    if (isAdded) smsAlertView.visibility = View.GONE
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun smsColorFormatting(remainingSms: Int) {
        when {
            remainingSms >= 10 -> {
                tv_remaining_sms_count_home.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.circle_blue
                    )
                tv_remaining_sms_count_home.textColor = ContextCompat.getColor(
                    requireContext(),
                    R.color.remaining_sms_10_more
                )
                tv_sms_limit_count_home.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.circle_blue
                    )
                tv_sms_limit_count_home.textColor = ContextCompat.getColor(
                    requireContext(),
                    R.color.remaining_sms_10_more
                )
            }
            remainingSms in 1..9 -> {
                tv_remaining_sms_count_home.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.circle_yellow
                    )
                tv_remaining_sms_count_home.textColor = ContextCompat.getColor(
                    requireContext(),
                    R.color.remaining_sms_1_9
                )
                tv_sms_limit_count_home.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.circle_yellow
                    )
                tv_sms_limit_count_home.textColor = ContextCompat.getColor(
                    requireContext(),
                    R.color.remaining_sms_1_9
                )
            }
            else -> {
                tv_remaining_sms_count_home.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.circle_violet
                    )
                tv_remaining_sms_count_home.textColor = ContextCompat.getColor(
                    requireContext(),
                    R.color.remaining_sms_0
                )
                tv_sms_limit_count_home.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.circle_violet
                    )
                tv_sms_limit_count_home.textColor = ContextCompat.getColor(
                    requireContext(),
                    R.color.remaining_sms_0
                )
            }
        }
    }

    private fun showShortlistJobExpirationInMessageBox() {
        Timber.d("here")
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 2)
        val deadlineNext2Days = calendar.time

        doAsync {
            val shortlistedJobs =
                bdJobsDB.shortListedJobDao().getShortListedJobsBYDeadline(deadlineNext2Days)

            uiThread {
                try {
                    if (shortlistedJobs.isNotEmpty()) {

                        Timber.d("here2")

                        var job = "Job"
                        if (shortlistedJobs.size > 1)
                            job = "Jobs"

                        val body = "${shortlistedJobs.size} $job found"

                        val notificationModel = CommonNotificationModel(
                            title = "Expiration of shortlisted job within next 2 days",
                            body = body,
                            type = "exp",
                            activityNode = "com.bdjobs.app.LoggedInUserLanding.MainLandingActivity",
                            notificationId = "1000"
                        )
                        Timber.d("here3: Title: ${notificationModel.title}")


                        doAsync {

                            // checking if the notification contents are same or not
                            val isSameExist =
                                bdJobsDB.notificationDao().checkSameNotificationByMessage(body)

                            if (isSameExist == 0) {
                                bdJobsDB.notificationDao()
                                    .deleteNotificationByNotificationId("1000")

                                val notification = Notification(
                                    title = notificationModel.title,
                                    body = notificationModel.body,
                                    type = notificationModel.type,
                                    imageLink = notificationModel.imageLink,
                                    link = notificationModel.link,
                                    notificationId = notificationModel.notificationId,
                                    arrivalTime = Date(),
                                    payload = Gson().toJson(notificationModel).replace("\\n", "\n")
                                )

                                bdJobsDB.notificationDao().insertNotification(notification)

                                bdJobsUserSession.updateMessageCount(
                                    bdJobsDB.notificationDao().getMessageCount()
                                )
                            }

                        }

                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun showShortListedJobsExpirationPopUP() {


        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 2)
        val deadlineNext2Days = calendar.time

        doAsync {
            val shortlistedjobs =
                bdJobsDB.shortListedJobDao().getShortListedJobsBYDeadline(deadlineNext2Days)
            uiThread {
                try {
                    if (shortlistedjobs.isNotEmpty()) {

                        val dialog = Dialog(requireContext())
                        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog?.setCancelable(true)
                        dialog?.setContentView(R.layout.layout_shortlistedjob_pop_up)
                        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                        val showButton = dialog?.findViewById<Button>(R.id.bcYesTV)
                        val cancelIV = dialog?.findViewById<ImageView>(R.id.deleteIV)
                        val jobCountTV = dialog?.findViewById<TextView>(R.id.textView49)
                        val checkBox = dialog?.findViewById<CheckBox>(R.id.checkBox2)

                        val adSmallTemplate =
                            dialog.findViewById<TemplateView>(R.id.ad_small_template)

                        Ads.showNativeAd(adSmallTemplate, requireContext())

                        checkBox?.setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                val c = Calendar.getInstance().time
                                val df = SimpleDateFormat("dd-MMM-yyyy")
                                val formattedDate = df.format(c)
                                //Log.d("formattedDate", "formattedDate: $formattedDate")
                                bdJobsUserSession.insertShortlListedPopupDate(formattedDate)
                            } else if (!isChecked) {
                                bdJobsUserSession.insertShortlListedPopupDate("19-Mar-1919")
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

    fun updateMessageView(count: Int?) {
        //Log.d("rakib", "in home fragment $count")
        if (count!! > 0) {
            messageCountTV?.show()
            if (count <= 99)
                messageCountTV?.text = "$count"
            else
                messageCountTV?.text = "99+"
        } else {
            messageCountTV?.hide()
        }
    }

    fun updateInvitationCountView() {
        showAllInvitations()
    }


}