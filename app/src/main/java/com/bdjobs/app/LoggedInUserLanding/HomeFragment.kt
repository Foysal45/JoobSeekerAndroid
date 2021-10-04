package com.bdjobs.app.LoggedInUserLanding

import android.app.Dialog
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.LastSearchCountModel
import com.bdjobs.app.API.ModelClasses.LastUpdateModel
import com.bdjobs.app.Ads.Ads
import com.bdjobs.app.BroadCastReceivers.BackgroundJobBroadcastReceiver
import com.bdjobs.app.FavouriteSearch.FavouriteSearchFilterAdapter
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.ENCODED_JOBS
import com.bdjobs.app.Utilities.Constants.Companion.favSearchFiltersSynced
import com.bdjobs.app.Utilities.Constants.Companion.followedEmployerSynced
import com.bdjobs.app.Utilities.Constants.Companion.jobInvitationSynced
import com.bdjobs.app.Utilities.Constants.Companion.liveInvitationSynced
import com.bdjobs.app.Utilities.Constants.Companion.videoInvitationSynced
import com.bdjobs.app.databases.External.DataStorage
import com.bdjobs.app.databases.internal.*
import com.bdjobs.app.videoResume.VideoResumeActivity
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
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), BackgroundJobBroadcastReceiver.BackgroundJobListener {

    private lateinit var bdjobsUserSession: BdjobsUserSession
    private lateinit var bdjobsDB: BdjobsDB
    private lateinit var backgroundJobBroadcastReceiver: BackgroundJobBroadcastReceiver
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsUserSession = BdjobsUserSession(requireContext())
        bdjobsDB = BdjobsDB.getInstance(requireContext())
        homeCommunicator = requireContext() as HomeCommunicator
        backgroundJobBroadcastReceiver = BackgroundJobBroadcastReceiver()
        nameTV?.text = bdjobsUserSession.fullName
        emailTV?.text = bdjobsUserSession.email

        profilePicIMGV?.loadCircularImageFromUrl(bdjobsUserSession.userPicUrl)
        onClickListeners()
        getLastUpdateFromServer()


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
            bdjobsUserSession = BdjobsUserSession(requireContext())
            if (bdjobsUserSession.liveInterviewCount == 0 && bdjobsUserSession.videoInterviewCount == 0 && bdjobsUserSession.generalInterviewCount == 0) {
                Timber.d("Showing blank")
                allInterview?.hide()
                allInterview?.hide()
                blankCL?.show()
                newSearchBTN?.hide()
            } else {
                bdjobsUserSession = BdjobsUserSession(requireContext())
                allInterview?.show()
                tv_live_interview_count.text = bdjobsUserSession.liveInterviewCount.toString()
                tv_video_interview_count.text = bdjobsUserSession.videoInterviewCount.toString()
                tv_general_interview_count.text = bdjobsUserSession.generalInterviewCount.toString()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showNotificationCount() {
        try {
            bdjobsUserSession = BdjobsUserSession(requireContext())
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

    private fun showMessageCount() {
        try {

            doAsync {
                bdjobsUserSession = BdjobsUserSession(requireContext())
                val count = bdjobsDB.notificationDao().getMessageCount()
                Timber.d("Messages count: $count")
                bdjobsUserSession.updateMessageCount(count)
            }

            if (bdjobsUserSession.messageCount!! <= 0) {
                messageCountTV?.hide()
            } else {
                messageCountTV?.show()
                if (bdjobsUserSession.messageCount!! > 99) {
                    messageCountTV?.text = "99+"

                } else {
                    messageCountTV?.text = "${bdjobsUserSession.messageCount!!}"

                }
            }
        } catch (e: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()

        Timber.d("onResume Triggered")
        BackgroundJobBroadcastReceiver.backgroundJobListener = this
        showNotificationCount()
        showMessageCount()
        showData()
        alertAboutShortlistedJobs()

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
            followedEmployerList = bdjobsDB.followedEmployerDao().getAllFollowedEmployer()
            val followedEmployerJobCount =
                bdjobsDB.followedEmployerDao().getJobCountOfFollowedEmployer()
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
                bdjobsDB.favouriteSearchFilterDao().getLatest2FavouriteSearchFilter()
            val allfavsearch = bdjobsDB.favouriteSearchFilterDao().getAllFavouriteSearchFilter()
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
            lastSearch = bdjobsDB.lastSearchDao().getLastSearch()
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

//                                homeCommunicator.goToAjkerDealLive(R.id.navHostFragmentAD)
//                                navHostFragmentAD.visibility = View.VISIBLE

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
                    newSearchBTN?.show()
                } else {
//                    homeCommunicator.goToAjkerDealLive(R.id.navHostFragmentAD)
//                    navHostFragmentAD.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getFilterString(search: LastSearch): String? {
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

        interviewInvitationDialog?.setContentView(R.layout.interview_invitation_popup)
        val InterviewCV = interviewInvitationDialog?.findViewById<CardView>(R.id.cardView2)
        val VideoInterviewCV = interviewInvitationDialog?.findViewById<CardView>(R.id.cardView3)
        val LiveInterviewCV = interviewInvitationDialog?.findViewById<CardView>(R.id.cardView4)


        val InterviewTVCount =
            interviewInvitationDialog?.findViewById<TextView>(R.id.interview_invitation_count_tv)
        val VideoInterviewTVCount =
            interviewInvitationDialog?.findViewById<TextView>(R.id.interview_invitation_count_tv_3)
        val LiveInterviewTVCount =
            interviewInvitationDialog?.findViewById<TextView>(R.id.interview_invitation_count_tv_4)

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
                NotificationManagerCompat.from(requireContext())
                    .cancel(Constants.NOTIFICATION_INTERVIEW_INVITATTION)
            } catch (e: Exception) {
            }
            interviewInvitationDialog?.cancel()

        }

        VideoInterviewCV?.setOnClickListener {
            interviewInvitationDialog?.dismiss()
            homeCommunicator.goToVideoInvitation("popup")
            try {
                NotificationManagerCompat.from(requireContext())
                    .cancel(Constants.NOTIFICATION_VIDEO_INTERVIEW)
            } catch (e: Exception) {
            }
            interviewInvitationDialog?.cancel()

        }

        LiveInterviewCV?.setOnClickListener {
            interviewInvitationDialog?.dismiss()
            homeCommunicator.goToLiveInvitation("popup")
            interviewInvitationDialog?.cancel()
        }

        interviewInvitationDialog?.show()

    }

    private fun showGeneralPopUp() {
        val generalDialog = Dialog(requireContext())

        generalDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        generalDialog?.setCancelable(true)

        generalDialog?.setContentView(R.layout.home_general_popup)
        val mainCL = generalDialog?.findViewById<ConstraintLayout>(R.id.main_cl)
        val cardview = generalDialog?.findViewById<CardView>(R.id.cv_video_resume)

        val cancelBTN = generalDialog?.findViewById(R.id.cancel) as ImageView


        cancelBTN?.setOnClickListener {
            generalDialog?.dismiss()
        }

        cardview?.setOnClickListener {
            generalDialog?.dismiss()
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
            userId = bdjobsUserSession.userId,
            decodeId = bdjobsUserSession.decodId
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


                    inviteInterviview = response.body()?.data?.get(0)?.inviteInterviview
                    videoInterviview = response.body()?.data?.get(0)?.videoInterviview
                    liveInterview = response.body()?.data?.get(0)?.liveInterview

                    try {
                        Constants.changePassword_Eligibility =
                            response.body()?.data?.get(0)?.changePassword_Eligibility!!
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
            val shortlistedjobs =
                bdjobsDB.shortListedJobDao().getShortListedJobsBYDeadline(deadlineNext2Days)
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

                        val ad_small_template =
                            dialog?.findViewById<TemplateView>(R.id.ad_small_template)

                        Ads.showNativeAd(ad_small_template, requireContext())

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