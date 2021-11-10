package com.bdjobs.app.Jobs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Parcelable
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.text.style.UnderlineSpan
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.*
import com.bdjobs.app.ads.Ads
import com.bdjobs.app.AppliedJobs.AppliedJobsActivity
import com.bdjobs.app.BuildConfig
import com.bdjobs.app.Employers.EmployersBaseActivity
import com.bdjobs.app.ManageResume.ManageResumeActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.appliedJobsCount
import com.bdjobs.app.Web.WebActivity
import com.bdjobs.app.ajkerDeal.api.ApiInterfaceAPI
import com.bdjobs.app.ajkerDeal.api.ResponseHeader
import com.bdjobs.app.ajkerDeal.api.RetrofitUtils
import com.bdjobs.app.ajkerDeal.api.models.catalog.CatalogData
import com.bdjobs.app.ajkerDeal.api.models.live_list.LiveListData
import com.bdjobs.app.ajkerDeal.api.models.live_list.LiveListRequest
import com.bdjobs.app.ajkerDeal.ui.home.page_home.HomeNewAdapter
import com.bdjobs.app.ajkerDeal.ui.video_shopping.video_pager.VideoPagerActivity
import com.bdjobs.app.ajkerDeal.utilities.AppConstant
import com.bdjobs.app.ajkerDeal.utilities.DigitConverter
import com.bdjobs.app.ajkerDeal.utilities.alertAd
import com.bdjobs.app.databases.internal.AppliedJobs
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.databases.internal.FollowedEmployer
import com.bdjobs.app.databases.internal.ShortListedJobs
import com.bdjobs.app.editResume.EditResLandingActivity
import com.bdjobs.app.editResume.personalInfo.PersonalInfoActivity
import com.bdjobs.app.videoResume.VideoResumeActivity
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.squareup.picasso.Picasso
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@SuppressWarnings("deprecation")
class JobDetailAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        // View Types
        private const val BASIC = 0
        private const val LOADING = 1

        var appliedJobCount = 0
        var jobApplyThreshold = 25
        var jobApplyLimit = 50
        var availableJobs = 0

    }

    private val bdJobsDB = BdjobsDB.getInstance(context)
    private var bdJobsUserSession = BdjobsUserSession(context)
    private var jobCommunicator: JobCommunicator? = null
    private var jobList: MutableList<JobListModelData>? = null
    var call: JobCommunicator? = null
    private var isLoadingAdded = false
    private var retryPageLoad = false
    private var errorMsg: String? = null
    var jobKeyPointsData = ""
    var jobContextData = ""
    var jobDescriptionData = ""
    var jobTitle = ""
    var jobNatureData = ""
    var educationData = ""
    var experienceData = ""
    var requirementsData = ""
    var salaryData = ""
    var salaryDataText = ""
    var otherBenefitsData = ""
    var jobSourceData = ""
    var readApplyData = ""
    var companyName = ""
    var companyAddress = ""
    var companyLogoUrl = ""
    var companyOtherJobs = ""
    var applyOnline = ""
    var postedDate = ""
    var applyStatus = false
    var workPlace = ""
    var minSalary = ""
    var maxSalary = ""
    var showSalary = ""
    var preferVideoResume = 0
    private lateinit var dialog: Dialog
    private val applyOnlinePositions = ArrayList<Int>()
    private var language = ""
    private var remoteConfig: FirebaseRemoteConfig
    private var isShowingApplyButton = false

    private lateinit var dataAdapter: HomeNewAdapter
    private val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US)

    private val classifiedHashMap: HashMap<String, Int> = HashMap()
    private val liveList: MutableList<LiveListData> = mutableListOf()

    private val totalItemToBeViewed = 20

    var customLayoutManager: CustomLayoutManager ? = null

    init {
        jobList = java.util.ArrayList()
        jobCommunicator = context as JobCommunicator
        language = "bangla"
        //Log.d("JobDetailFragment", "${jobList?.size}")
        try {
            appliedJobsCount =
                bdJobsUserSession.mybdjobscount_jobs_applied_lastmonth!!.toInt()
            jobApplyLimit = bdJobsUserSession.jobApplyLimit!!.toInt()
        } catch (e: Exception) {
        }
        remoteConfig = FirebaseRemoteConfig.getInstance()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reload() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //Log.d("rakib", "created")
        var viewHolder: RecyclerView.ViewHolder? = null
        var inflater = LayoutInflater.from(parent.context)

        call = context as JobCommunicator
        when (viewType) {
            BASIC -> {
                val viewItem = inflater.inflate(R.layout.jobdetail_item_list, parent, false)
                viewHolder = JobsListVH(viewItem)

            }
            LOADING -> {
                val viewLoading = inflater.inflate(R.layout.item_progress, parent, false)
                viewHolder = LoadingVH(viewLoading)
            }
        }

        return viewHolder!!
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        applyStatus = false

        when (getItemViewType(position)) {
            BASIC -> {


                // holder.setIsRecyclable(false)

                val jobsVH = holder as JobsListVH

                val ss =
                    SpannableString("Applicants are encouraged to submit Video Resume. Learn more about Video Resume    ")
                val d = ContextCompat.getDrawable(context, R.drawable.ic_external_link)
                d?.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
                val span = ImageSpan(d!!, ImageSpan.ALIGN_BASELINE)
                ss.setSpan(span, ss.length - 1, ss.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                ss.setSpan(UnderlineSpan(), 50, 79, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)


//                jobsVH.videoResumeEncouragementTV.text = ss

                jobsVH.itemView.setOnClickListener {
                    call?.onItemClicked(position)
                }

                jobsVH.callBTN.setOnClickListener {
                    try {
                        (context as Activity).callHelpLine()
                    } catch (e: Exception) {
                        logException(e)
                    }
                }

                jobsVH.emailBTN.setOnClickListener {
                    sendEmail()
                }

                jobsVH.reportBTN.setOnClickListener {
                    reportThisJob(position)
                }


                jobsVH.shimmerViewContainer.show()

                jobsVH.applyFab.visibility = View.GONE
                isShowingApplyButton = false

                jobsVH.shimmerViewContainer.startShimmer()
                jobsVH.constraintLayout.hide()
                jobsVH.liveViewRoot.hide()
                jobCommunicator?.hideShortListIcon()


                ApiServiceJobs.create().getJobdetailData(
                    Constants.ENCODED_JOBS,
                    jobList?.get(position)?.jobid!!,
                    jobList?.get(position)?.lantype!!,
                    "",
                    "0",
                    bdJobsUserSession.userId,
                    "EN"
                ).enqueue(object : Callback<JobDetailJsonModel> {
                    override fun onFailure(call: Call<JobDetailJsonModel>, t: Throwable) {
                        //Log.d("ApiServiceJobs", "onFailure: fisrt time ${t.message}")
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onResponse(
                        call: Call<JobDetailJsonModel>,
                        response: Response<JobDetailJsonModel>
                    ) {

                        try {

                            //Log.d("ApiServiceJobs", "onResponse: ${response.body()?.data?.get(0)?.jobTitle}")
                            //Log.d("ApiServiceJobs", "onResponse: " + response.body())
                            jobsVH.shimmerViewContainer.hide()
                            jobsVH.shimmerViewContainer.stopShimmer()
                            jobsVH.constraintLayout.show()
                            jobsVH.liveViewRoot.show()
                            Ads.showNativeAd(jobsVH.adSmallTemplate, context)
                            val jobDetailResponseAll = response.body()?.data?.get(0)

                            jobKeyPointsData = jobDetailResponseAll!!.jobKeyPoints!!
                            jobContextData = jobDetailResponseAll.context!!
                            jobDescriptionData = jobDetailResponseAll.jobDescription!!
                            jobNatureData = jobDetailResponseAll.jobNature!!
                            educationData = jobDetailResponseAll.educationRequirements!!
                            experienceData = jobDetailResponseAll.experience!!
                            requirementsData = jobDetailResponseAll.additionJobRequirements!!
                            salaryData = jobDetailResponseAll.jobSalaryRange!!
                            salaryDataText = jobDetailResponseAll.jobSalaryRangeText!!
                            otherBenefitsData = jobDetailResponseAll.jobOtherBenifits!!
                            jobSourceData = jobDetailResponseAll.jobSource!!
                            readApplyData = jobDetailResponseAll.applyInstruction!!
                            companyName = jobDetailResponseAll.compnayName!!
                            jobTitle = jobDetailResponseAll.jobTitle!!
                            companyAddress = jobDetailResponseAll.companyAddress!!
                            companyLogoUrl = jobDetailResponseAll.jobLOgoName!!
                            companyOtherJobs = jobDetailResponseAll.companyOtherJ0bs!!
                            applyOnline = jobDetailResponseAll.onlineApply!!
                            postedDate = jobDetailResponseAll.postedOn!!
                            workPlace = jobDetailResponseAll.jobWorkPlace!!
                            minSalary = jobDetailResponseAll.minSalary!!
                            maxSalary = jobDetailResponseAll.maxSalary!!
                            showSalary = jobDetailResponseAll.jobShowSalary!!
                            preferVideoResume = jobDetailResponseAll.preferVideoResume!!

                            if (applyOnline.equalIgnoreCase("True")) {

                                bdJobsUserSession = BdjobsUserSession(context)

                                appliedJobCount =
                                    bdJobsUserSession.mybdjobscount_jobs_applied_lastmonth!!.toInt()
                                jobApplyThreshold = bdJobsUserSession.jobApplyThreshold!!.toInt()
                                jobApplyLimit = bdJobsUserSession.jobApplyLimit!!.toInt()
                                availableJobs = jobApplyLimit - appliedJobCount

                                if (appliedJobCount >= jobApplyThreshold) {
                                    jobsVH.jobApplicationStatusTitle.show()
                                    jobsVH.jobApplicationStatusCard.show()
                                    jobsVH.jobApplicationCountTV.text =
                                        "You have already applied for $appliedJobCount jobs in the current month."
                                    jobsVH.jobApplicationRemainingTV.text =
                                        if (availableJobs <= 0) "Only 0 remaining" else "Only $availableJobs remaining"
                                } else {
                                    jobsVH.jobApplicationStatusTitle.hide()
                                    jobsVH.jobApplicationStatusCard.hide()

                                }
                                jobsVH.whyIAmSeeingThisTV.setOnClickListener {
                                    Constants.showJobApplicationGuidelineDialog(context)
                                }
                            }
                            else {
                                jobsVH.jobApplicationStatusTitle.hide()
                                jobsVH.jobApplicationStatusCard.hide()
                            }

                            /**
                             * checking if employer prefer applicant with Video Resume
                             * 1 -> yes ; 0 -> no
                             */
                            if (preferVideoResume == 1) {
                                jobsVH.videoResumeEncouragementTV.visibility = View.VISIBLE
                                jobsVH.viewDivider.visibility = View.VISIBLE

                                jobsVH.videoResumeEncouragementTV.text = ss

                            } else {
                                jobsVH.videoResumeEncouragementTV.visibility = View.GONE
                                jobsVH.viewDivider.visibility = View.GONE
                            }

                            try {

                                jobsVH.videoResumeEncouragementTV.setOnClickListener {
                                    context.startActivity(
                                        Intent(
                                            context,
                                            VideoResumeActivity::class.java
                                        )
                                    )
                                }

                                val date = Date()
                                val formatter = SimpleDateFormat("MM/dd/yyyy")
                                val today: String = formatter.format(date)
                                val todayDate = SimpleDateFormat("MM/dd/yyyy").parse(today)

                                val deadline = jobDetailResponseAll.DeadlineDB!!
                                val deadlineDate = SimpleDateFormat("MM/dd/yyyy").parse(deadline)

                                //Log.d("fphwrpeqspm", "todayDate: $todayDate deadlineDate:$deadlineDate")

                                if (todayDate > deadlineDate) {
                                    jobsVH.jobExpirationBtn.show()
                                    jobCommunicator?.hideShortListIcon()
                                } else {
                                    jobsVH.jobExpirationBtn.hide()
                                    jobCommunicator?.showShortListIcon()
                                }

                            } catch (e: Exception) {

                            }


                            if (jobDetailResponseAll.companyWeb.isNullOrBlank()) {
                                jobsVH.websiteTV.hide()
                                jobsVH.websiteHeadingTV.hide()
                            } else {
                                jobsVH.websiteTV.show()
                                jobsVH.websiteHeadingTV.show()

                                if (jobDetailResponseAll.companyWeb.startsWith("http") || jobDetailResponseAll.companyWeb.startsWith(
                                        "Http"
                                    )
                                ) {
                                    jobsVH.websiteTV.text =
                                        Html.fromHtml("<a href='" + jobDetailResponseAll.companyWeb + "'>" + jobDetailResponseAll.companyWeb + "</a>")
                                    jobsVH.websiteTV.movementMethod = MovementCheck()
                                } else {
                                    jobsVH.websiteTV.text =
                                        Html.fromHtml("<a href='https://" + jobDetailResponseAll.companyWeb + "'>" + jobDetailResponseAll.companyWeb + "</a>")
                                    jobsVH.websiteTV.movementMethod = MovementCheck()
                                }
                            }


                            if (jobDetailResponseAll.companyBusiness.isNullOrBlank()) {
                                jobsVH.businessHeadingTV.hide()
                                jobsVH.businessTV.hide()
                            } else {
                                jobsVH.businessHeadingTV.show()
                                jobsVH.businessTV.show()
                                jobsVH.businessTV.text = jobDetailResponseAll.companyBusiness
                            }



                            if (applyOnline.equalIgnoreCase("True")) {
                                applyOnlinePositions.add(position)
                            }

                            if (jobList?.get(position)?.lantype!!.equalIgnoreCase("2")) {
                                jobsVH.followTV.hide()
                            } else {
                                jobsVH.followTV.show()
                            }

                            jobsVH.tvPosName.text = jobDetailResponseAll.jobTitle
                            jobsVH.tvComName.text = jobDetailResponseAll.compnayName

                            if (bdJobsUserSession.isFirstInstall!!) {
                                // this is installed first
                                val daysDiff = getDifferenceBetweenDates(currentDate,bdJobsUserSession.firstInstallAt!!)
                                Timber.d("Days Diff: $daysDiff")

                                if (daysDiff.toInt() < 30) {
                                    // show both
                                    jobsVH.rootBothView.visibility = View.VISIBLE
                                    jobsVH.rootOnlyValueView.visibility = View.GONE
                                }
                                else {
                                    // only show value
                                    jobsVH.rootBothView.visibility = View.GONE
                                    jobsVH.rootOnlyValueView.visibility = View.VISIBLE
                                }

                            }
                            else {
                                //show both
                                jobsVH.rootBothView.visibility = View.VISIBLE
                                jobsVH.rootOnlyValueView.visibility = View.GONE
                            }

                            jobsVH.tvSalary.text = jobDetailResponseAll.jobSalaryRange
                            jobsVH.tvSalary2.text = jobDetailResponseAll.jobSalaryRange
                            jobsVH.tvDeadline.text = jobDetailResponseAll.deadline
                            jobsVH.tvDeadline2.text = jobDetailResponseAll.deadline
                            jobsVH.tvLocation.text = jobDetailResponseAll.jobLocation
                            jobsVH.tvLocation2.text = jobDetailResponseAll.jobLocation
                            jobsVH.tvVacancies.text = jobDetailResponseAll.jobVacancies
                            jobsVH.tvVacancies2.text = jobDetailResponseAll.jobVacancies


//                            if (remoteConfig.getBoolean("Apply_Button_Type"))
//                                jobsVH.applyButton.hide()
//                            else
                            jobsVH.applyFab.visibility = View.GONE
                            isShowingApplyButton = false

                            jobsVH.appliedBadge.hide()

                            jobsVH.followTV.setOnClickListener {
                                val bdjobsUserSession = BdjobsUserSession(context)
                                if (!bdjobsUserSession.isLoggedIn!!) {
                                    jobCommunicator?.setBackFrom("jobdetail")
                                    jobCommunicator?.goToLoginPage()
                                } else {
                                    doAsync {
                                        val isItFollowed = bdJobsDB.followedEmployerDao()
                                            .isItFollowed(
                                                jobDetailResponseAll.companyID!!,
                                                jobDetailResponseAll.companyNameENG!!
                                            )
                                        uiThread {
                                            if (isItFollowed) {
                                                jobsVH.followTV.setTextColor(Color.parseColor("#13A10E"))
                                                jobsVH.followTV.text = "Follow"
                                                jobsVH.followTV.backgroundTintList =
                                                    ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                                                callUnFollowApi(
                                                    jobDetailResponseAll.companyID,
                                                    jobDetailResponseAll.companyNameENG
                                                )
                                            } else {
                                                jobsVH.followTV.text = "Unfollow"
                                                jobsVH.followTV.backgroundTintList =
                                                    ColorStateList.valueOf(Color.parseColor("#E6E5EB"))
                                                jobsVH.followTV.setTextColor(Color.parseColor("#767676"))
                                                callFollowApi(
                                                    jobDetailResponseAll.companyID,
                                                    jobDetailResponseAll.companyNameENG
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            //Log.d("applyPostion", "online: $applyonlinePostions")
                            if (applyOnline.equalIgnoreCase("True")) {

                                bdJobsUserSession = BdjobsUserSession(context)

                                appliedJobCount =
                                    bdJobsUserSession.mybdjobscount_jobs_applied_lastmonth!!.toInt()
                                jobApplyThreshold = bdJobsUserSession.jobApplyThreshold!!.toInt()
                                jobApplyLimit = bdJobsUserSession.jobApplyLimit!!.toInt()
                                availableJobs = jobApplyLimit - appliedJobCount


                                if (bdJobsUserSession.isLoggedIn!!) {

                                    if (appliedJobCount >= jobApplyLimit) {
                                        jobsVH.applyLimitOverButton.visibility = View.VISIBLE

//                                        if (remoteConfig.getBoolean("Apply_Button_Type"))
//                                            jobsVH.applyButton.visibility = View.GONE
//                                        else
                                        jobsVH.applyFab.visibility = View.GONE
                                        isShowingApplyButton = false

                                        jobsVH.applyLimitOverButton.setOnClickListener {
                                            showApplyLimitOverPopup(context, position)
                                        }
                                    } else {
//                                        if (remoteConfig.getBoolean("Apply_Button_Type"))
//                                            jobsVH.applyButton.visibility = View.VISIBLE
//                                        else
                                        jobsVH.applyFab.visibility = View.VISIBLE
                                        isShowingApplyButton = true
                                        jobsVH.applyLimitOverButton.visibility = View.GONE
                                    }


                                } else {
//                                    if (remoteConfig.getBoolean("Apply_Button_Type"))
//                                        jobsVH.applyButton.visibility = View.VISIBLE
//                                    else
                                    jobsVH.applyFab.visibility = View.VISIBLE
                                    isShowingApplyButton = true
                                }
                            } else {
//                                jobsVH.applyButton.visibility = View.GONE
                                jobsVH.applyFab.visibility = View.GONE
                                isShowingApplyButton = false
                            }

                            jobsVH.applyFab.setOnClickListener {
                                val bdjobsUserSession = BdjobsUserSession(context)
                                if (!bdjobsUserSession.isLoggedIn!!) {
                                    jobCommunicator?.setBackFrom("jobdetail")
                                    jobCommunicator?.goToLoginPage()
                                } else {
                                    if (!bdjobsUserSession.isCvPosted?.equalIgnoreCase("true")!!) {
                                        try {
                                            val alertd =
                                                context.alert("To Access this feature please post your resume") {
                                                    title = "Your resume is not posted!"
                                                    positiveButton("Post Resume") { context.startActivity<EditResLandingActivity>() }
                                                    negativeButton("Cancel") { dd ->
                                                        dd.dismiss()
                                                    }
                                                }
                                            alertd.isCancelable = false
                                            alertd.show()
                                        } catch (e: Exception) {
                                            logException(e)
                                        }
                                    } else {
                                        showWarningPopup(
                                            context,
                                            position,
                                            jobDetailResponseAll.gender!!,
                                            jobDetailResponseAll.photograph!!,
                                            jobDetailResponseAll.minSalary!!,
                                            jobDetailResponseAll.maxSalary!!
                                        )
                                        //checkApplyEligibility(context, position, jobDetailResponseAll.gender!!, jobDetailResponseAll.photograph!!)
                                    }
                                }
                            }

                            doAsync {
                                val appliedJobs = bdJobsDB.appliedJobDao()
                                    .getAppliedJobsById(jobList?.get(position)?.jobid!!)
                                val isItFollowed = bdJobsDB.followedEmployerDao().isItFollowed(
                                    jobDetailResponseAll.companyID!!,
                                    jobDetailResponseAll.companyNameENG!!
                                )

                                uiThread {
                                    if (appliedJobs.isEmpty()) {
                                        jobsVH.appliedBadge.hide()
                                    } else {
                                        jobsVH.appliedBadge.show()
                                        jobsVH.videoResumeEncouragementTV.visibility = View.GONE
                                        jobsVH.viewDivider.visibility = View.GONE

                                        if (preferVideoResume==1) {
                                            jobsVH.videoResumeEncBottomRoot.visibility = View.VISIBLE
                                            jobsVH.alertTv.visibility = View.GONE
                                        }
//                                        if (remoteConfig.getBoolean("Apply_Button_Type"))
//                                        jobsVH.applyButton.visibility = View.GONE
//                                        else
                                        jobsVH.applyFab.visibility = View.GONE
                                    }

                                    if (isItFollowed) {
                                        jobsVH.followTV.text = "Unfollow"
                                        jobsVH.followTV.backgroundTintList =
                                            ColorStateList.valueOf(Color.parseColor("#E6E5EB"))
                                        jobsVH.followTV.setTextColor(Color.parseColor("#767676"))
                                    } else {
                                        jobsVH.followTV.setTextColor(Color.parseColor("#13A10E"))
                                        jobsVH.followTV.text = "Follow"
                                        jobsVH.followTV.backgroundTintList =
                                            ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                                    }
                                }
                            }

                            jobsVH.closeIcon.setOnClickListener {
                                jobsVH.videoResumeEncBottomRoot.visibility = View.GONE
                                jobsVH.alertTv.visibility = View.VISIBLE
                            }

                            jobsVH.videoResumeEncBottomRoot.setOnClickListener {
                                context.startActivity(Intent(context,VideoResumeActivity::class.java))
                            }

                            //Job Information Checking Start

                            if (jobDetailResponseAll.jObIMage.isNullOrBlank()) {
                                jobsVH.govtJobsIMGV.hide()
                                jobsVH.horizontalView.show()
                                jobsVH.horizontalViewTwo.show()
                                jobsVH.horizontalView.show()

                                if (jobKeyPointsData.isBlank()) {

                                    jobsVH.tvKeyPoints.visibility = View.GONE
                                    jobsVH.keyPoints.visibility = View.GONE

                                } else {
                                    jobsVH.tvKeyPoints.visibility = View.VISIBLE
                                    jobsVH.keyPoints.visibility = View.VISIBLE
                                    jobsVH.tvKeyPoints.text =
                                        response.body()?.data?.get(0)?.jobKeyPoints
                                }

                                if (jobContextData.isBlank()) {

                                    jobsVH.tvJobContext.visibility = View.GONE
                                    jobsVH.tvJobContextValue.visibility = View.GONE

                                } else {

                                    jobsVH.tvJobContext.visibility = View.VISIBLE
                                    jobsVH.tvJobContextValue.visibility = View.VISIBLE
                                    jobsVH.tvJobContextValue.text = jobContextData
                                }

                                if (jobDescriptionData.isBlank()) {

                                    jobsVH.tvJobResponsibility.visibility = View.GONE
                                    jobsVH.tvJobResponsibilityValue.visibility = View.GONE

                                } else {
                                    jobsVH.tvJobResponsibilityValue.text = jobDescriptionData
                                    jobsVH.tvJobResponsibility.visibility = View.VISIBLE
                                    jobsVH.tvJobResponsibilityValue.visibility = View.VISIBLE

                                }

                                if (jobNatureData.isBlank()) {

                                    jobsVH.tvJobNature.visibility = View.GONE
                                    jobsVH.tvJobNatureValue.visibility = View.GONE

                                } else {
                                    jobsVH.tvJobNatureValue.text = jobNatureData
                                    jobsVH.tvJobNature.visibility = View.VISIBLE
                                    jobsVH.tvJobNatureValue.visibility = View.VISIBLE

                                }

                                if (workPlace.isBlank()) {
                                    Timber.d("workplace if")
                                    jobsVH.workingPlaceTV.visibility = View.GONE
                                    jobsVH.workingPlaceValueTV.visibility = View.GONE
                                } else {
                                    Timber.d("workplace else")
                                    jobsVH.workingPlaceValueTV.text = workPlace
                                    jobsVH.workingPlaceTV.visibility = View.VISIBLE
                                    jobsVH.workingPlaceValueTV.visibility = View.VISIBLE
                                }


                                if (educationData.isBlank() && experienceData.isBlank() && requirementsData.isBlank()) {

                                    jobsVH.tvEducationalRequirementsValue.visibility = View.GONE
                                    jobsVH.tvEducationalRequirements.visibility = View.GONE
                                    jobsVH.tvExperienceReq.visibility = View.GONE
                                    jobsVH.tvExperienceReqValue.visibility = View.GONE
                                    jobsVH.tvJobReqValue.visibility = View.GONE
                                    jobsVH.tvJobReq.visibility = View.GONE
                                    jobsVH.tvRequirementsHead.visibility = View.GONE

                                } else {

                                    if (educationData.isBlank()) {

                                        jobsVH.tvEducationalRequirementsValue.visibility = View.GONE
                                        jobsVH.tvEducationalRequirements.visibility = View.GONE

                                    } else {
                                        jobsVH.tvEducationalRequirementsValue.text = educationData
                                        jobsVH.tvEducationalRequirementsValue.visibility =
                                            View.VISIBLE
                                        jobsVH.tvEducationalRequirements.visibility = View.VISIBLE

                                    }

                                    if (experienceData.isBlank()) {

                                        jobsVH.tvExperienceReq.visibility = View.GONE
                                        jobsVH.tvExperienceReqValue.visibility = View.GONE

                                    } else {
                                        jobsVH.tvExperienceReqValue.text = experienceData
                                        jobsVH.tvExperienceReq.visibility = View.VISIBLE
                                        jobsVH.tvExperienceReqValue.visibility = View.VISIBLE
                                    }

                                    if (requirementsData.isBlank()) {
                                        jobsVH.tvJobReqValue.visibility = View.GONE
                                        jobsVH.tvJobReq.visibility = View.GONE
                                    } else {
                                        jobsVH.tvJobReqValue.text = requirementsData
                                        jobsVH.tvJobReqValue.visibility = View.VISIBLE
                                        jobsVH.tvJobReq.visibility = View.VISIBLE
                                    }

                                }

                                if (salaryData.equals("--") && otherBenefitsData.isBlank()) {

                                    jobsVH.tvSalaryRange.visibility = View.GONE
                                    jobsVH.tvSalaryRangeData.visibility = View.GONE
                                    jobsVH.tvOtherBenefits.visibility = View.GONE
                                    jobsVH.tvOtherBenefitsData.visibility = View.GONE
                                    jobsVH.tvSalaryAndCompensation.visibility = View.GONE


                                } else {

                                    if (salaryData.equals("--")) {

                                        jobsVH.tvSalaryRange.visibility = View.GONE
                                        jobsVH.tvSalaryRangeData.visibility = View.GONE

                                        if (!salaryDataText.isBlank()) {
                                            jobsVH.tvSalaryRangeData.visibility = View.VISIBLE
                                            jobsVH.tvSalaryRangeData.text = salaryDataText
                                        }

                                    } else {

                                        jobsVH.tvSalaryRange.visibility = View.VISIBLE
                                        jobsVH.tvSalaryRangeData.visibility = View.VISIBLE
                                        jobsVH.tvSalaryRangeData.text =
                                            "\u2022 $salaryData" + "\n\n$salaryDataText"
                                    }

                                    if (otherBenefitsData.isBlank()) {

                                        jobsVH.tvOtherBenefits.visibility = View.GONE
                                        jobsVH.tvOtherBenefitsData.visibility = View.GONE

                                    } else {

                                        jobsVH.tvOtherBenefitsData.text = otherBenefitsData
                                        jobsVH.tvOtherBenefits.visibility = View.VISIBLE
                                        jobsVH.tvOtherBenefitsData.visibility = View.VISIBLE

                                    }

                                }

                                if (showSalary.equals("0")) {
                                    jobsVH.tvSalaryRangeData.visibility = View.GONE
                                    jobsVH.tvSalaryRange.visibility = View.GONE
                                }

                                if (readApplyData.isNullOrBlank()) {
                                    jobsVH.tvReadBefApply.visibility = View.GONE
                                    jobsVH.tvReadBefApplyData.visibility = View.GONE

                                } else {
                                    jobsVH.tvReadBefApplyData.text = Html.fromHtml(readApplyData)
                                    jobsVH.tvReadBefApply.visibility = View.VISIBLE
                                    jobsVH.tvReadBefApplyData.visibility = View.VISIBLE
                                    jobsVH.tvReadBefApplyData.movementMethod = MovementCheck()

                                    if (jobDetailResponseAll.jobAppliedEmail.isNullOrBlank() || jobDetailResponseAll.jobAppliedEmail.isNullOrEmpty()) {
                                        jobsVH.emailApplyTV.hide()
                                        jobsVH.emailApplyMsgTV.hide()
                                    } else {
                                        jobsVH.emailApplyTV.show()
                                        jobsVH.emailApplyMsgTV.show()
                                        jobsVH.emailApplyMsgTV.text =
                                            "or to Email CV from MY BDJOBS account please "
                                        jobsVH.emailApplyTV.text =
                                            Html.fromHtml("<a href='" + "CLICK HERE" + "'>" + "CLICK HERE" + "</a>")

                                        jobsVH.emailApplyTV.setOnClickListener {
                                            val bdjobsUserSession = BdjobsUserSession(context)
                                            if (bdjobsUserSession.isLoggedIn!!) {
                                                context.startActivity<ManageResumeActivity>(
                                                    "from" to "emailResumeCompose",
                                                    "subject" to jobDetailResponseAll.jobTitle,
                                                    "emailAddress" to jobDetailResponseAll.jobAppliedEmail,
                                                    "jobid" to jobDetailResponseAll.jobId
                                                )
                                            } else {
                                                jobCommunicator?.setBackFrom("jobdetail")
                                                jobCommunicator?.goToLoginPage()
                                            }
                                        }
                                    }

                                }

                                if (companyLogoUrl.isBlank()) {
                                    jobsVH.companyLogo.visibility = View.GONE
                                } else {
                                    jobsVH.companyLogo.visibility = View.VISIBLE
                                    Picasso.get()?.load(companyLogoUrl)?.into(jobsVH.companyLogo)
                                }



                                if (jobSourceData.isNullOrBlank() || jobSourceData.isNullOrEmpty()) {
                                    jobsVH.tvJobSource.hide()
                                    jobsVH.tvJobSourceHeading.hide()
                                } else {
                                    jobsVH.tvJobSource.show()
                                    jobsVH.tvJobSourceHeading.show()
                                    jobsVH.tvJobSource.text = jobSourceData
                                }


                                if (companyAddress.isNullOrBlank() || companyAddress.isNullOrEmpty()) {
                                    jobsVH.tvCompanyAddress.hide()
                                    jobsVH.addressHeadingTV.hide()
                                } else {
                                    jobsVH.addressHeadingTV.show()
                                    jobsVH.tvCompanyAddress.show()
                                    jobsVH.tvCompanyAddress.text = companyAddress
                                }

                                jobsVH.tvCompanyName.text = companyName
                                jobsVH.tvPostedDate.text = postedDate

                                if (jobDetailResponseAll.companyOtherJ0bs.equalIgnoreCase("0")) {
                                    jobsVH.allJobsButtonLayout.hide()
                                } else {
                                    jobsVH.allJobsButtonLayout.show()
                                }

                                var job = "job"
                                try {
                                    if (jobDetailResponseAll.companyOtherJ0bs.toInt() > 0) {
                                        job = "jobs"
                                    }
                                } catch (e: Exception) {
                                    logException(e)
                                }

                                jobsVH.viewAllJobsTV.text =
                                    "View ${jobDetailResponseAll.companyOtherJ0bs} more $job of this company"

                                try {
                                    if (jobDetailResponseAll.companyOtherJ0bs.toInt() > 0) {
                                        jobsVH.allJobsButtonLayout.setOnClickListener {
                                            context.startActivity<EmployersBaseActivity>(
                                                "from" to "joblist",
                                                "companyid" to jobDetailResponseAll.companyID,
                                                "companyname" to jobDetailResponseAll.companyNameENG,
                                                "jobId" to jobDetailResponseAll.jobId
                                            )
                                        }
                                    }
                                } catch (e: Exception) {
                                    logException(e)
                                }

                            }
                            else {


                                if (jobSourceData.isNullOrBlank() || jobSourceData.isNullOrEmpty()) {
                                    jobsVH.tvJobSource.hide()
                                    jobsVH.tvJobSourceHeading.hide()
                                } else {
                                    jobsVH.tvJobSource.show()
                                    jobsVH.tvJobSourceHeading.show()
                                    jobsVH.tvJobSource.text = jobSourceData
                                }



                                if (companyAddress.isNullOrBlank() || companyAddress.isNullOrEmpty()) {
                                    jobsVH.tvCompanyAddress.hide()
                                    jobsVH.addressHeadingTV.hide()
                                } else {
                                    jobsVH.addressHeadingTV.show()
                                    jobsVH.tvCompanyAddress.show()
                                    jobsVH.tvCompanyAddress.text = companyAddress
                                }
                                jobsVH.tvCompanyName.text = companyName

                                if (jobDetailResponseAll.companyOtherJ0bs.equalIgnoreCase("0")) {
                                    jobsVH.allJobsButtonLayout.hide()
                                } else {
                                    jobsVH.allJobsButtonLayout.show()
                                }

                                var job = "job"
                                try {
                                    if (jobDetailResponseAll.companyOtherJ0bs.toInt() > 0) {
                                        job = "jobs"
                                    }
                                } catch (e: Exception) {
                                    logException(e)
                                }

                                jobsVH.viewAllJobsTV.text =
                                    "View ${jobDetailResponseAll.companyOtherJ0bs} more $job of this company"

                                try {
                                    if (jobDetailResponseAll.companyOtherJ0bs.toInt() > 0) {
                                        jobsVH.allJobsButtonLayout.setOnClickListener {
                                            context.startActivity<EmployersBaseActivity>(
                                                "from" to "joblist",
                                                "companyid" to jobDetailResponseAll.companyID,
                                                "companyname" to jobDetailResponseAll.companyNameENG,
                                                "jobId" to jobDetailResponseAll.jobId
                                            )
                                        }
                                    }
                                } catch (e: Exception) {
                                    logException(e)
                                }



                                jobsVH.govtJobsIMGV.loadImageFromUrl(jobDetailResponseAll.jObIMage)
                                jobsVH.govtJobsIMGV.setOnClickListener {
                                    context.openUrlInBrowser(jobDetailResponseAll.jObIMage)
                                }
                                jobsVH.horizontalView.hide()
                                jobsVH.horizontalViewTwo.hide()
                                jobsVH.horizontalView.hide()
                                jobsVH.govtJobsIMGV.show()
                                jobsVH.jobInfo.hide()
                                jobsVH.appliedBadge.hide()
                                jobsVH.keyPoints.hide()
                                jobsVH.tvKeyPoints.hide()
                                jobsVH.tvJobContext.hide()
                                jobsVH.tvJobContextValue.hide()
                                jobsVH.tvJobResponsibility.hide()
                                jobsVH.tvJobResponsibilityValue.hide()
                                jobsVH.tvJobNature.hide()
                                jobsVH.tvJobNatureValue.hide()
                                jobsVH.tvRequirementsHead.hide()
                                jobsVH.tvJobReqValue.hide()
                                jobsVH.tvJobReq.hide()
                                jobsVH.tvEducationalRequirements.hide()
                                jobsVH.tvEducationalRequirementsValue.hide()
                                jobsVH.tvExperienceReq.hide()
                                jobsVH.tvExperienceReqValue.hide()
                                jobsVH.tvEducationalRequirementsValue.hide()
                                jobsVH.tvSalaryAndCompensation.hide()
                                jobsVH.tvSalary.hide()
                                jobsVH.tvSalaryRange.hide()
                                jobsVH.tvSalaryRangeData.hide()
                                jobsVH.tvOtherBenefits.hide()
                                jobsVH.tvOtherBenefitsData.hide()
                                jobsVH.tvReadBefApply.hide()
                                jobsVH.tvReadBefApplyData.hide()

                            }


                            // ajker deal live part
                            if (bdJobsUserSession.isLoggedIn!!) {
                                if (applyOnline.equalIgnoreCase("True")) {
                                    holder.liveProgressBar.visibility =View.GONE
                                    holder.liveViewContainer.visibility =View.GONE

                                } else {
                                    fetchLiveShowHandPicked(totalItemToBeViewed,jobsVH)
                                    manageItemClickListener()
                                }
                            }
                            else {
                                fetchLiveShowHandPicked(totalItemToBeViewed,jobsVH)
                                manageItemClickListener()
                            }

                        } catch (e: Exception) {
                            logException(e)
                        }

                    }
                })

                jobsVH.liveRecyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                        when(e.action) {
                            MotionEvent.ACTION_MOVE -> {
                                customLayoutManager?.setScrollingEnable(false)
                            } else -> customLayoutManager?.setScrollingEnable(true)
                        }
                        return false
                    }

                    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

                    }

                    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

                    }
                })
            }

            LOADING -> {

                val loadingVH = holder as LoadingVH

                if (retryPageLoad) {
                    loadingVH.mErrorLayout?.visibility = View.VISIBLE

                    loadingVH.mErrorTxt?.text = if (errorMsg != null)
                        errorMsg
                    else
                        context.getString(R.string.hint_keyword)

                } else {
                    loadingVH.mErrorLayout?.visibility = View.GONE

                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        customLayoutManager = recyclerView.layoutManager as CustomLayoutManager

    }

    private fun fetchLiveShowHandPicked(count: Int,holder: JobDetailAdapter.JobsListVH) {

        holder.liveProgressBar.visibility =View.VISIBLE
        holder.liveTitleTV.visibility = View.GONE

        dataAdapter = HomeNewAdapter()

        val gridLayoutManager =
            GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        with(holder.liveRecyclerView) {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
            adapter = dataAdapter
            isNestedScrollingEnabled = false
            //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        try {
            ApiInterfaceAPI.invoke(
                RetrofitUtils.retrofitInstance(
                    AppConstant.BASE_URL_API, RetrofitUtils.getGson(),
                    RetrofitUtils.createOkHttpClient(RetrofitUtils.createCache((context as Activity).application))
                ),

                ).fetchHandPickLivesFromJobDetails(
                LiveListRequest(
                    index = 0,
                    count = count,
                    liveId = 0,
                    gender = bdJobsUserSession.gender,
                    currentSalary = bdJobsUserSession.presentSalary,
                    expectedSalary = bdJobsUserSession.expectedSalary,
                    location = bdJobsUserSession.userPresentAddress
                )
            ).enqueue(object : Callback<ResponseHeader<List<LiveListData>>>{
                override fun onResponse(
                    call: Call<ResponseHeader<List<LiveListData>>>,
                    response: Response<ResponseHeader<List<LiveListData>>>
                ) {

                    if (response.isSuccessful) {
                        holder.liveProgressBar.visibility =View.GONE
                        holder.liveViewContainer.visibility =View.VISIBLE
                        if (response.code()==200) {
                            holder.liveTitleTV.visibility = View.VISIBLE
                            val list = response.body()?.data as MutableList<LiveListData>

                            try {
                                Timber.d("Here")
                                if (BuildConfig.DEBUG) {
                                    list.add(
                                        0,
                                        LiveListData(
                                            id=5789,
                                            liveDate="14/09/2021",
                                            fromTime="17:53:00",
                                            toTime="18:53:00",
                                            channelId=1696416,
                                            channelType="customer",
                                            isActive=1,
                                            insertedBy=746,
                                            scheduleId=0,
                                            coverPhoto="https://static.ajkerdeal.com/LiveVideoImage/LiveVideoCoverPhoto/5789/livecoverphoto.jpg",
                                            videoTitle="Test Live",
                                            customerName=null,
                                            profileID=null,
                                            compStringName="",
                                            channelLogo="https://static.ajkerdeal.com/images/banners/1696416/logo.jpg",
                                            channelName="Flash",
                                            liveChannelName=null,
                                            videoChannelLink="",
                                            customerId=0,
                                            merchantId=0,
                                            statusName="replay",
                                            paymentMode="both",
                                            facebookPageUrl="https://www.facebook.com/flashfashionhouse/videos/557937342187835",
                                            mobile="01853165356", alternativeMobile="",
                                            redirectToFB=false, isShowMobile=true,
                                            isShowComment=true, isShowProductCart=false,
                                            facebookVideoUrl="https://www.facebook.com/flashfashionhouse/videos/557937342187835",
                                            orderPlaceFlag=1, categoryId=7, subCategoryId=111, subSubCategoryId=0,
                                            isThirdPartyProductUrl=0, isNotificationSended=true, videoId="557937342187835",
                                        )
                                    )
                                    list.add(
                                        0,
                                        LiveListData(
                                            id=5928,
                                            liveDate="19/09/2021",
                                            fromTime="22:27:21",
                                            toTime="23:00:21",
                                            channelId=1412606,
                                            channelType="customer",
                                            isActive=1,
                                            insertedBy=1412606,
                                            scheduleId=0,
                                            coverPhoto="https://static.ajkerdeal.com/LiveVideoImage/LiveVideoCoverPhoto/5928/livecoverphoto.jpg",
                                            videoTitle="Test Live LP",
                                            customerName=null,
                                            profileID=null,
                                            compStringName="",
                                            channelLogo="https://static.ajkerdeal.com/images/banners/1412606/logo.jpg",
                                            channelName="Gm - Gents Mart",
                                            liveChannelName=null,
                                            videoChannelLink="https://ad-live-streaming.s3-ap-southeast-1.amazonaws.com/live_show/1412606/5928/live_hls.m3u8",
                                            customerId=0,
                                            merchantId=0,
                                            statusName="replay",
                                            paymentMode="both",
                                            facebookPageUrl="",
                                            mobile="", alternativeMobile="",
                                            redirectToFB=false, isShowMobile=false,
                                            isShowComment=true, isShowProductCart=false,
                                            facebookVideoUrl="",
                                            orderPlaceFlag=0, categoryId=0, subCategoryId=0, subSubCategoryId=0,
                                            isThirdPartyProductUrl=0, isNotificationSended=false, videoId="",
                                        )
                                    )
                                }

                                liveList.clear()
                                liveList.addAll(list)
                                dataAdapter.initList(list)
                                Timber.d("requestBody ${dataAdapter.itemCount}, ${list.size}, ${liveList.size}, $list")
                                //val position = mHomePageDataList.indexOfFirst { it.homeViewType == HomeViewType.TYPE_LIVE }
                                val position = classifiedHashMap["1"] ?: -1
                                if (position == -1) return
                                Timber.d("LiveHandPickDebug api call actionType1 $position")
                            } catch (e: Exception) {
                                Timber.e("Exception showing: ${e.localizedMessage}")
                            }

                        } else {
                            holder.liveProgressBar.visibility =View.GONE
                            holder.liveViewContainer.visibility =View.GONE
                        }
                    } else {
                        holder.liveProgressBar.visibility =View.GONE
                        holder.liveViewContainer.visibility =View.GONE
                    }
                }

                override fun onFailure(call: Call<ResponseHeader<List<LiveListData>>>, t: Throwable) {

                    Timber.e("$t")
                    holder.liveProgressBar.visibility =View.GONE
                    holder.liveViewContainer.visibility =View.GONE
                }

            })
        } catch (e: Exception) {
            holder.liveProgressBar.visibility=View.GONE
            holder.liveViewContainer.visibility =View.GONE
            Timber.e("$e")
        }


    }

    private fun manageItemClickListener() {

        dataAdapter.onItemClick = { model, parentPosition ->
            Timber.d("requestBody $model")

            when (model.statusName) {
                "live" -> {
                    goToViewShow(model, model.statusName, parentPosition)
                }
                "replay" -> {
                    goToViewShow(model, model.statusName, parentPosition)
                }
                "upcoming" -> {
                    val formatDate = "${model.liveDate} ${model.fromTime}"
                    val date = sdf.parse(formatDate)
                    context.alertAd("আপকামিং", "এই লাইভটি শুরু হবে ${DigitConverter.relativeWeekday(date!!)}।", false, "ঠিক আছে") {

                    }.show()
                }
            }
        }
    }

    private fun goToViewShow(model: LiveListData, statusName: String?, position: Int) {
        val videoList: MutableList<CatalogData> = mutableListOf()
        var playIndex = 0
        Timber.d("requestBody $position, $statusName, ${liveList.size}, $model")
        when (statusName) {
            "live" -> {
                generateLiveVideoList(model, videoList)
            }
            "replay" -> {

                Timber.d("requestBody ${liveList.size}")
                val replayList = liveList.filter { it.statusName == "replay" }
                playIndex = replayList.indexOf(model) ?: -1
                replayList.forEach { model1 ->
                    generateLiveVideoList(model1, videoList)
                }
            }
        }
        Intent(context, VideoPagerActivity::class.java).apply {
            putExtra("playIndex", if (playIndex > -1) playIndex else 0)
            putParcelableArrayListExtra("videoList", videoList as java.util.ArrayList<out Parcelable>)
            putExtra("noCache", true)
            putExtra("isLiveShow", true)
        }.also {
            context.startActivity(it)
        }
    }

    private fun generateLiveVideoList(model: LiveListData, list: MutableList<CatalogData>): MutableList<CatalogData> {
        list.add(
            CatalogData(
                model.id,
                model.videoTitle,
                model.coverPhoto,
                "",
                model.videoChannelLink ?: "",
                channelLogo = model.channelLogo ?: "",
                customerName = model.channelName,
                customerId = model.channelId,
                sellingTag = model.paymentMode,
                sellingText = model.statusName,
                facebookPageUrl = model.facebookPageUrl ?: "",
                mobile = model.mobile ?: "",
                alternativeMobile = model.alternativeMobile ?: "",
                redirectToFB = model.redirectToFB,
                channelType = model.channelType ?: "",
                isShowMobile = model.isShowMobile,
                isShowComment = model.isShowComment,
                isShowProductCart = model.isShowProductCart,
                facebookVideoUrl = model.facebookVideoUrl,
                liveDate = model.liveDate,
                orderPlaceFlag = model.orderPlaceFlag,
                isYoutubeVideo = model.isYoutubeVideo == 1,
                videoId = model.videoId
            )
        )
        return list
    }

    private fun sendEmail() {
        context.email("complain@bdjobs.com", "", "")
    }

    private fun checkApplyEligibility(
        activity: Context,
        position: Int,
        gender: String,
        jobPhotograph: String,
        minSalary: String,
        maxSalary: String
    ) {

        val bdjobsUserSession = BdjobsUserSession(context)
        val loadingDialog = activity.indeterminateProgressDialog("Applying")
        loadingDialog.setCancelable(false)
        loadingDialog.show()

        ApiServiceJobs.create().applyEligibilityCheck(
            userID = bdjobsUserSession.userId,
            decodeID = bdjobsUserSession.decodId,
            jobID = jobList?.get(position)?.jobid!!,
            JobSex = gender,
            JobPhotograph = jobPhotograph,
            encoded = Constants.ENCODED_JOBS
        ).enqueue(
            object : Callback<ApplyEligibilityModel> {
                override fun onFailure(call: Call<ApplyEligibilityModel>, t: Throwable) {
                    loadingDialog.dismiss()
                }

                override fun onResponse(
                    call: Call<ApplyEligibilityModel>,
                    response: Response<ApplyEligibilityModel>
                ) {

                    loadingDialog.dismiss()

                    try {
                        if (response.isSuccessful) {
                            if (response.body()?.data?.get(0)?.applyEligibility?.equalIgnoreCase("true")!!) {
                                showSalaryDialog(
                                    activity,
                                    position,
                                    gender,
                                    jobPhotograph,
                                    minSalary,
                                    maxSalary,
                                    "0"
                                )
                            } else {

                                val plainMessage = response.body()?.data?.get(0)?.message
                                val plainHeading = response.body()?.data?.get(0)?.title

                                val message = Html.fromHtml(plainMessage)
                                val heading = Html.fromHtml(plainHeading)

                                if (response.body()?.data?.get(0)?.UpdateResume?.equalIgnoreCase("False")!!) {

                                    val alertd = context.alert(message) {
                                        title = heading
                                        //positiveButton("Post Resume") { context.startActivity<EditResLandingActivity>() }
                                        negativeButton("OK") { dd ->
                                            dd.dismiss()
                                        }
                                    }
                                    alertd.isCancelable = false
                                    alertd.show()
                                } else {
                                    showCvUpdateDialog(
                                        activity,
                                        position,
                                        gender,
                                        jobPhotograph,
                                        minSalary,
                                        maxSalary,
                                        heading,
                                        message
                                    )
                                }


                            }
                        }
                    } catch (e: Exception) {
                        logException(e)
                    }
                }
            }
        )
    }

    private fun showCvUpdateDialog(
        activity: Context, position: Int, gender: String, jobphotograph: String,
        minSalary: String, maxSalary: String, title: CharSequence, message: CharSequence
    ) {
        val dialog = Dialog(context)

        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
//            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(true)
            setContentView(R.layout.dialog_update_cv)
        }

        val titleTV = dialog.findViewById<MaterialTextView>(R.id.tv_title)
        val messageTV = dialog.findViewById<MaterialTextView>(R.id.tv_message)
        val updateCVBtn = dialog.findViewById<MaterialButton>(R.id.btn_update_cv)
        val upToDateBtn = dialog.findViewById<MaterialButton>(R.id.btn_up_to_date)
        val laterBtn = dialog.findViewById<MaterialButton>(R.id.btn_later)


        titleTV.text = title
        messageTV.text = message

        updateCVBtn.setOnClickListener {
            context.startActivity(Intent(context, EditResLandingActivity::class.java))
            dialog.dismiss()
        }
        upToDateBtn.setOnClickListener {
            updateCV(
                context,
                position,
                gender,
                jobphotograph,
                minSalary,
                maxSalary,
                dialog,
                "0",
                "0"
            )
        }
        laterBtn.setOnClickListener {
            updateCV(
                context,
                position,
                gender,
                jobphotograph,
                minSalary,
                maxSalary,
                dialog,
                "1",
                "1"
            )
        }

        dialog.show()
    }


    private fun updateCV(
        activity: Context,
        position: Int,
        gender: String,
        jobPhotograph: String,
        minSalary: String,
        maxSalary: String,
        dialog: Dialog,
        updateLater: String,
        cvUpdateLater: String
    ) {
        ApiServiceJobs.create()
            .updateCV(bdJobsUserSession.userId, bdJobsUserSession.decodId, updateLater)
            .enqueue(object : Callback<CvUpdateLaterModel> {
                override fun onResponse(
                    call: Call<CvUpdateLaterModel>,
                    response: Response<CvUpdateLaterModel>
                ) {
                    Timber.d("update CV onResponse")

                    if (response.isSuccessful && response.code() == 200) {

                        val body = response.body()

                        if (body?.message == "success") {
//                            if (body.isUpdated == 1) {
                            // show popup
                            showSalaryDialog(
                                activity,
                                position,
                                gender,
                                jobPhotograph,
                                minSalary,
                                maxSalary,
                                cvUpdateLater
                            )
                            dialog.dismiss()
//                            } else {
//                                // don't show popup
//                                dialog.dismiss()
//                            }
                        } else {
                            dialog.dismiss()
                        }

                    } else Timber.d("response unsuccessful: ${response.code()}")
                }

                override fun onFailure(call: Call<CvUpdateLaterModel>, t: Throwable) {
                    Timber.d("Failure: ${t.localizedMessage}")
                    dialog.dismiss()
                }

            })
    }

    @SuppressLint("SetTextI18n")
    private fun showApplyLimitOverPopup(
        context: Context, position: Int
    ) {

        try {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.job_apply_limit_reached_popup)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val titleTV = dialog.findViewById<TextView>(R.id.job_apply_limit_reached_title_tv)
            titleTV.text = "Job seekers can apply for $jobApplyLimit jobs every month."

            val applyCountTV = dialog.findViewById<TextView>(R.id.apply_count_tv)
            applyCountTV.text = "$appliedJobsCount"

            val remainingDaysTV = dialog.findViewById<TextView>(R.id.remaining_days_tv)
            // TODO if remainingDaysCount = 0 convert to hours & minutes
            val remainingDaysCount = if (Constants.daysAvailable > 0) Constants.daysAvailable else 1
            remainingDaysTV.text = "$remainingDaysCount days"

            val okBtn = dialog.findViewById<MaterialButton>(R.id.job_apply_limit_reached_ok_button)
            okBtn.setOnClickListener {
                dialog.dismiss()
            }

            val appliedJobsBtn =
                dialog.findViewById<MaterialButton>(R.id.job_apply_limit_reached_applied_jobs_button)
            appliedJobsBtn.setOnClickListener {
                dialog.dismiss()
                context.startActivity<AppliedJobsActivity>("time" to "1")
            }

            dialog.show()
        } catch (e: Exception) {
        }
    }

    private fun showWarningPopup(
        context: Context,
        position: Int,
        gender: String,
        jobPhotograph: String,
        minSalary: String,
        maxSalary: String
    ) {
        try {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.layout_warning_popup)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val warningTitleTV = dialog.findViewById<TextView>(R.id.txt_warning_title)
            val warningMessageTV = dialog.findViewById<TextView>(R.id.txt_warning_message)
            val translateIV = dialog.findViewById<ImageView>(R.id.img_translate)
            val cancelBtn = dialog.findViewById<Button>(R.id.btn_cancel)
            val agreedBtn = dialog.findViewById<Button>(R.id.btn_agreed)
            val agreedCheckBox = dialog.findViewById<CheckBox>(R.id.chk_bx_agreed)
            val adSmallTemplate = dialog.findViewById<TemplateView>(R.id.ad_small_template)
            Ads.showNativeAd(adSmallTemplate, context)

            translateIV?.setOnClickListener {
                when (language) {
                    "bangla" -> {
                        language = "english"
                        translateIV.setImageResource(R.drawable.ic_translate_color)
                        warningTitleTV?.text = context.getString(R.string.warning_title)
                        warningMessageTV?.text = context.getString(R.string.warning_message)
                        agreedCheckBox?.text = context.getString(R.string.warning_message_agreement)
                        agreedBtn?.text = context.getString(R.string.warning_agree_button)
                        cancelBtn?.text = context.getString(R.string.warning_cancel_button)
                    }
                    "english" -> {
                        language = "bangla"
                        translateIV.setImageResource(R.drawable.ic_translate)
                        warningTitleTV?.text = context.getString(R.string.warning_title_bangla)
                        warningMessageTV?.text = context.getString(R.string.warning_message_bangla)
                        agreedCheckBox?.text =
                            context.getString(R.string.warning_message_agreement_bangla)
                        agreedBtn?.text = context.getString(R.string.warning_agree_button_bangla)
                        cancelBtn?.text = context.getString(R.string.warning_cancel_button_bangla)
                    }
                }
            }
            agreedCheckBox?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    agreedBtn?.isEnabled = true
                    agreedBtn?.isClickable = true
                    agreedBtn?.setTextColor(Color.parseColor("#1565C0"))
                } else {
                    agreedBtn?.isEnabled = false
                    agreedBtn?.isClickable = false
                    agreedBtn?.setTextColor(Color.parseColor("#9E9E9E"))
                }

            }
            cancelBtn?.setOnClickListener { dialog.dismiss() }
            agreedBtn?.setOnClickListener {
                dialog.dismiss()
                checkApplyEligibility(
                    context,
                    position,
                    gender,
                    jobPhotograph,
                    minSalary,
                    maxSalary
                )
            }
            dialog.show()
        } catch (e: Exception) {
        }

//        val showButton = dialog.findViewById<Button>(R.id.bcYesTV)
//        val cancelIV = dialog.findViewById<ImageView>(R.id.deleteIV)
//        val jobCountTV = dialog.findViewById<TextView>(R.id.textView49)
//        val checkBox = dialog.findViewById<CheckBox>(R.id.checkBox2)
    }


    @SuppressLint("SetTextI18n")
    private fun showSalaryDialog(
        activity: Context,
        position: Int,
        gender: String,
        jobPhotograph: String,
        minSalary: String,
        maxSalary: String,
        cvUpdateLater: String
    ) {
        dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
//        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.online_apply_dialog_layout)
        val cancelButton = dialog.findViewById<Button>(R.id.onlineApplyCancelBTN)
        val okButton = dialog.findViewById<Button>(R.id.onlineApplyOkBTN)
        val applyAnywayButton = dialog.findViewById<Button>(R.id.applyAnywayBTN)
        val salaryTIET = dialog.findViewById<TextInputEditText>(R.id.salaryAmountTIET)
        val salaryTIL = dialog.findViewById<TextInputLayout>(R.id.salaryAmountTIL)
        val adSmallTemplate = dialog.findViewById<TemplateView>(R.id.ad_small_template)
        val salaryExceededTextView: TextView =
            dialog.findViewById(R.id.salary_limit_exceeded_tv) as TextView
//        val scrollView = dialog.findViewById(R.id.scroll) as ScrollView

        val jobApplicationStatusCard =
            dialog.findViewById<ConstraintLayout>(R.id.job_detail_job_application_status_card)
        val appliedJobsCountTV =
            dialog.findViewById<TextView>(R.id.job_detail_job_application_count_tv)
        val remainingJobsCountTV =
            dialog.findViewById<TextView>(R.id.job_detail_job_application_remaining_tv)
        val whyIAmSeeingThisTV = dialog.findViewById<TextView>(R.id.why_i_am_seeing_this_text)

        Ads.showNativeAd(adSmallTemplate, context)

        bdJobsUserSession = BdjobsUserSession(context)

        appliedJobCount = bdJobsUserSession.mybdjobscount_jobs_applied_lastmonth!!.toInt()
        jobApplyThreshold = bdJobsUserSession.jobApplyThreshold!!.toInt()
        jobApplyLimit = bdJobsUserSession.jobApplyLimit!!.toInt()
        availableJobs = jobApplyLimit - appliedJobCount

        if (appliedJobCount >= jobApplyThreshold) {

            jobApplicationStatusCard.show()


            if (appliedJobsCount != Constants.appliedJobLimit) {
                appliedJobsCountTV.text =
                    "You have already applied for $appliedJobCount jobs in the current month."
            } else {
                appliedJobsCountTV.text =
                    "You have already applied to $appliedJobCount jobs this month! To continue applying to jobs, purchase additional online application package from web."

            }

            remainingJobsCountTV.text =
                if (availableJobs <= 0) "Only 0 remaining" else "Only $availableJobs remaining"

            whyIAmSeeingThisTV.setOnClickListener {
                Constants.showJobApplicationGuidelineDialog(context)
            }
        } else {
            jobApplicationStatusCard?.hide()
        }

        salaryTIET.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                salaryExceededTextView.hide()
                okButton?.show()
                applyAnywayButton?.hide()
                salaryTIL.boxStrokeColor = ContextCompat.getColor(context, R.color.colorPrimary)
            }
        }

        applyAnywayButton?.setOnClickListener {
            applyOnlineJob(
                position,
                salaryTIET.text.toString(),
                gender,
                jobPhotograph,
                cvUpdateLater
            )
        }

        salaryTIET?.easyOnTextChangedListener { text ->
            validateFilterName(text.toString(), salaryTIL)
            okButton?.isEnabled = text.isNotEmpty()
        }

        cancelButton?.setOnClickListener {
            dialog?.dismiss()
        }

        okButton?.setOnClickListener {

            d("applyTest in ok button $applyStatus")

            try {
                salaryTIET?.hideKeyboard()
            } catch (e: Exception) {

            }

            try {
                if (minSalary != "0" && maxSalary != "0") {
                    if (salaryTIET.text.toString().toInt() > maxSalary.toInt()) {
                        salaryExceededTextView.show()
                        applyAnywayButton?.show()
                        okButton.hide()
                        salaryTIET.clearFocus()
                        salaryTIL.boxStrokeColor = Color.parseColor("#c0392b")
                    } else {
                        salaryExceededTextView.hide()
                        applyAnywayButton?.hide()
                        okButton.show()
                        salaryTIL.boxStrokeColor =
                            ContextCompat.getColor(context, R.color.colorPrimary)
                    }
                } else {
                    if (maxSalary != "0" && minSalary == "0") {
                        if (salaryTIET.text.toString().toInt() > maxSalary.toInt()) {
                            salaryExceededTextView.show()
                            okButton.hide()
                            applyAnywayButton?.show()
                            salaryTIET.clearFocus()
                            salaryTIL.boxStrokeColor = Color.parseColor("#c0392b")
                        } else {
                            salaryExceededTextView.hide()
                            okButton.show()
                            applyAnywayButton?.hide()
                            salaryTIL.boxStrokeColor =
                                ContextCompat.getColor(context, R.color.colorPrimary)
                        }
                    } else if (maxSalary == "0" && minSalary != "0") {
                        if (salaryTIET.text.toString().toInt() > minSalary.toInt()) {
                            salaryExceededTextView.show()
                            okButton.hide()
                            applyAnywayButton?.show()
                            salaryTIET.clearFocus()
                            salaryTIL.boxStrokeColor = Color.parseColor("#c0392b")
                        } else {
                            salaryExceededTextView.hide()
                            okButton.show()
                            applyAnywayButton?.hide()
                            salaryTIL.boxStrokeColor =
                                ContextCompat.getColor(context, R.color.colorPrimary)
                        }
                    }
                }
            } catch (e: Exception) {
                applyAnywayButton?.hide()
                okButton.show()
                salaryExceededTextView.hide()
                salaryTIL.boxStrokeColor = ContextCompat.getColor(context, R.color.colorPrimary)

            }

            if (validateFilterName(salaryTIET.getString(), salaryTIL) && !applyStatus) {
                applyStatus = true

                if (okButton.isVisible) {
                    bdJobsUserSession.lastExpectedSalary = salaryTIET.text.toString()
                    applyOnlineJob(
                        position,
                        salaryTIET.text.toString(),
                        gender,
                        jobPhotograph,
                        cvUpdateLater
                    )
                    dialog.dismiss()
                }

                d("applyTest validate $applyStatus")

            } else {
                if (okButton.isVisible) {
                    bdJobsUserSession.lastExpectedSalary = salaryTIET.text.toString()
                    applyOnlineJob(
                        position,
                        salaryTIET.text.toString(),
                        gender,
                        jobPhotograph,
                        cvUpdateLater
                    )
                    dialog.dismiss()
                }
            }

        }
        dialog.show()
    }

    private fun showConfirmationDialog(message: String) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_job_confirmation)

//        val personalInfoRoot = dialog.findViewById<ConstraintLayout>(R.id.cl_personal_info)
        val btnOk = dialog.findViewById<MaterialButton>(R.id.ok_btn_cnf)
        val messageTV = dialog.findViewById<MaterialTextView>(R.id.tv_message)

        val applicantName = dialog.findViewById<MaterialTextView>(R.id.tv_applicant_name)
        val applicantNameLabel = dialog.findViewById<MaterialTextView>(R.id.tv_applicant_name_label)
        val applicantEmail = dialog.findViewById<MaterialTextView>(R.id.tv_applicant_email)
        val applicantEmailLabel =
            dialog.findViewById<MaterialTextView>(R.id.tv_applicant_email_label)
        val applicantPresentAddress =
            dialog.findViewById<MaterialTextView>(R.id.tv_applicant_present_address)
        val applicantPresentAddressLabel =
            dialog.findViewById<MaterialTextView>(R.id.tv_applicant_present_address_label)
        val applicantPermanentAddress =
            dialog.findViewById<MaterialTextView>(R.id.tv_applicant_permanent_address)
        val applicantPermanentAddressLabel =
            dialog.findViewById<MaterialTextView>(R.id.tv_applicant_permanent_address_label)
        val applicantMobile = dialog.findViewById<MaterialTextView>(R.id.tv_applicant_mobile)
        val applicantMobileLabel =
            dialog.findViewById<MaterialTextView>(R.id.tv_applicant_mobile_label)

        applicantName.text = bdJobsUserSession.fullName
        applicantEmail.text = bdJobsUserSession.email

        if (bdJobsUserSession.userMobileNumber != "") {
            applicantMobile.visibility = View.VISIBLE
            applicantMobileLabel.visibility = View.VISIBLE
            applicantMobile.text = bdJobsUserSession.userMobileNumber
        } else {
            applicantMobile.visibility = View.GONE
            applicantMobileLabel.visibility = View.GONE
        }

        if (bdJobsUserSession.userPresentAddress != "") {
            applicantPresentAddress.visibility = View.VISIBLE
            applicantPresentAddressLabel.visibility = View.VISIBLE
            applicantPresentAddress.text = bdJobsUserSession.userPresentAddress
        } else {
            applicantPresentAddress.visibility = View.GONE
            applicantPresentAddressLabel.visibility = View.GONE
        }

        if (bdJobsUserSession.userPermanentAddress != "") {
            applicantPermanentAddress.visibility = View.VISIBLE
            applicantPermanentAddressLabel.visibility = View.VISIBLE
            applicantPermanentAddress.text = bdJobsUserSession.userPermanentAddress
        } else {
            applicantPermanentAddress.visibility = View.GONE
            applicantPermanentAddressLabel.visibility = View.GONE
        }

        messageTV.text = message

        applicantNameLabel.setOnClickListener {
            goToFragment("personal")
            dialog.dismiss()
        }
        applicantEmailLabel.setOnClickListener {
            goToFragment("contact")
            dialog.dismiss()
        }
        applicantMobileLabel.setOnClickListener {
            goToFragment("contact")
            dialog.dismiss()
        }
        applicantPresentAddressLabel.setOnClickListener {
            goToFragment("contact")
            dialog.dismiss()
        }
        applicantPermanentAddressLabel.setOnClickListener {
            goToFragment("contactJD")
            dialog.dismiss()
        }
//
//        cName.text = companyName
//        cAddress.text = companyAddress
//        appliedPosition.text = jobTitle
//        applicationDate.text = postedDate

        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun goToFragment(check: String) {
        context.startActivity<PersonalInfoActivity>("name" to check, "personal_info_edit" to "null")
    }

    private fun validateFilterName(typedData: String?, textInputLayout: TextInputLayout): Boolean {

        if (typedData.isNullOrBlank()) {
            textInputLayout.showError(context.getString(R.string.field_empty_error_message_common))
            return false
        }
        textInputLayout.hideError()
        return true
    }

    @SuppressWarnings("deprecation")
    private fun applyOnlineJob(
        position: Int,
        salary: String,
        gender: String,
        jobPhotograph: String,
        cvUpdateLater: String
    ) {
        val bdJobsUserSession = BdjobsUserSession(context)
        val loadingDialog = context.indeterminateProgressDialog("Applying")
        loadingDialog.setCancelable(false)
        loadingDialog.show()
        ApiServiceJobs.create().applyJob(
            bdJobsUserSession.userId,
            bdJobsUserSession.decodId,
            jobList?.get(position)?.jobid!!,
            salary,
            gender,
            jobPhotograph,
            Constants.ENCODED_JOBS,
            cvUpdateLater
        ).enqueue(object : Callback<ApplyOnlineModel> {
            override fun onFailure(call: Call<ApplyOnlineModel>, t: Throwable) {

                loadingDialog.dismiss()
                dialog.dismiss()
                applyStatus = false
                d("applyTest onFailure ")

            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ApplyOnlineModel>,
                response: Response<ApplyOnlineModel>
            ) {


                try {

                    d("applyTest onResponse ")
                    dialog.dismiss()
                    loadingDialog.dismiss()

                    val message =
                        if (response.body()!!.data[0].message.endsWith(".")) response.body()!!.data[0].message
                        else "${response.body()!!.data[0].message}."

                    showConfirmationDialog(message)
                    if (response.body()!!.data[0].status.equalIgnoreCase("ok")) {
                        bdJobsUserSession.incrementJobsApplied()
                        bdJobsUserSession.decrementAvailableJobs()
                        applyStatus = true

                        doAsync {
                            val appliedJobs =
                                AppliedJobs(appliedid = jobList?.get(position)?.jobid!!)
                            bdJobsDB.appliedJobDao().insertAppliedJobs(appliedJobs)
                            uiThread {
                                notifyDataSetChanged()
                            }
                        }


                        appliedJobsCount++
                        d("applyTest success $applyStatus")
                    } else {
                        applyStatus = false
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return if (jobList == null) 0 else jobList!!.size
    }

    override fun getItemViewType(position: Int): Int {

        return if (position == jobList!!.size - 1 && isLoadingAdded) {
            LOADING
        } else if (jobList!![position].standout.equals("0")) {
            BASIC
        } else if (jobList!![position].standout.equals("1")) {
            BASIC
        } else if (jobList!![position].standout.equals("2")) {
            BASIC
        } else {
            BASIC
        }
    }


    fun add(r: JobListModelData) {
        jobList?.add(r)
        notifyItemInserted(jobList!!.size - 1)
    }

    fun addAll(moveResults: List<JobListModelData>) {
        for (result in moveResults) {
            add(result)
        }
        jobCommunicator?.setJobList(jobList)
    }

    private fun remove(r: JobListModelData?) {
        val position = jobList!!.indexOf(r)
        if (position > -1) {
            jobList!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        isLoadingAdded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(JobListModelData())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = this.jobList!!.size - 1
        val result = getItem(position)

        if (result.jobid.isNullOrBlank()) {
            this.jobList!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): JobListModelData {
        return jobList!![position]
    }


  /*  fun showRetry(show: Boolean, errorMsg: String?) {
        retryPageLoad = show
        jobList?.size?.minus(1)?.let { notifyItemChanged(it) }

        if (errorMsg != null) this.errorMsg = errorMsg
    }*/

    /**
     * Main list's content ViewHolder
     */

    inner class JobsListVH(viewItem: View?) : RecyclerView.ViewHolder(viewItem!!) {

        val horizontalViewTwo: View = viewItem?.findViewById(R.id.horizontalViewTwo) as View
        val horizontalView: View = viewItem?.findViewById(R.id.horizontalView) as View
        val jobInfo: TextView = viewItem?.findViewById(R.id.jobInfo) as TextView
        val govtJobsIMGV: ImageView = viewItem?.findViewById(R.id.govtJobsIMGV) as ImageView

        val shimmerViewContainer: ShimmerFrameLayout =
            viewItem?.findViewById(R.id.shimmer_view_container) as ShimmerFrameLayout

        val constraintLayout: ConstraintLayout =
            viewItem?.findViewById(R.id.constraintLayout) as ConstraintLayout
        val appliedBadge: TextView = viewItem?.findViewById(R.id.appliedBadge) as TextView
        val tvPosName: TextView = viewItem?.findViewById(R.id.positionName) as TextView
        val tvComName: TextView = viewItem?.findViewById(R.id.companyName) as TextView
        val rootBothView: ConstraintLayout = viewItem?.findViewById(R.id.cl_both_view) as ConstraintLayout
        val rootOnlyValueView: ConstraintLayout = viewItem?.findViewById(R.id.cl_only_value_view) as ConstraintLayout
        val tvLocation: TextView = viewItem?.findViewById(R.id.locationValue) as TextView
        val tvLocation2: TextView = viewItem?.findViewById(R.id.locationValue2) as TextView
        val tvLocationTitle: TextView = viewItem?.findViewById(R.id.locationText) as TextView
        val tvDeadline: TextView = viewItem?.findViewById(R.id.deadlineValue) as TextView
        val tvDeadline2: TextView = viewItem?.findViewById(R.id.deadlineValue2) as TextView
        val tvDeadlineTitle: TextView = viewItem?.findViewById(R.id.deadlineText) as TextView
        val tvVacancies: TextView = viewItem?.findViewById(R.id.vacancyValue) as TextView
        val tvVacancies2: TextView = viewItem?.findViewById(R.id.vacancyValue2) as TextView
        val tvVacanciesTitle: TextView = viewItem?.findViewById(R.id.vacancyText) as TextView
        val tvSalary: TextView = viewItem?.findViewById(R.id.salaryValue) as TextView
        val tvSalary2: TextView = viewItem?.findViewById(R.id.salaryValue2) as TextView
        val tvSalaryTitle: TextView = viewItem?.findViewById(R.id.salaryText) as TextView
        val tvKeyPoints: TextView = viewItem?.findViewById(R.id.keyPointsTv) as TextView
        val tvJobContextValue: TextView = viewItem?.findViewById(R.id.jobContextTV) as TextView
        val tvJobContext: TextView = viewItem?.findViewById(R.id.jobContext) as TextView
        val tvJobResponsibilityValue: TextView =
            viewItem?.findViewById(R.id.responsibilityTV) as TextView
        val tvJobResponsibility: TextView = viewItem?.findViewById(R.id.responsibility) as TextView
        val tvJobNatureValue: TextView = viewItem?.findViewById(R.id.jobNatureTv) as TextView
        val tvJobNature: TextView = viewItem?.findViewById(R.id.jobNature) as TextView
        val tvEducationalRequirementsValue: TextView =
            viewItem?.findViewById(R.id.educationTV) as TextView
        val tvEducationalRequirements: TextView = viewItem?.findViewById(R.id.education) as TextView
        val tvExperienceReqValue: TextView = viewItem?.findViewById(R.id.experienceTV) as TextView
        val tvExperienceReq: TextView = viewItem?.findViewById(R.id.Experience) as TextView
        val tvJobReqValue: TextView = viewItem?.findViewById(R.id.jobRequirementsTV) as TextView
        val tvJobReq: TextView = viewItem?.findViewById(R.id.jobRequirements) as TextView
        val tvRequirementsHead: TextView = viewItem?.findViewById(R.id.requirements) as TextView
        val tvSalaryRangeData: TextView = viewItem?.findViewById(R.id.salaryRangeTV) as TextView
        val tvSalaryRange: TextView = viewItem?.findViewById(R.id.salaryRange) as TextView
        val tvOtherBenefitsData: TextView = viewItem?.findViewById(R.id.otherBenefitsTV) as TextView
        val tvOtherBenefits: TextView = viewItem?.findViewById(R.id.otherBenefits) as TextView
        val tvSalaryAndCompensation: TextView =
            viewItem?.findViewById(R.id.salaryAndCompensation) as TextView
        val tvJobSource: TextView = viewItem?.findViewById(R.id.jobSourceTV) as TextView
        val tvJobSourceHeading: TextView = viewItem?.findViewById(R.id.JobSource) as TextView
        val tvReadBefApplyData: TextView = viewItem?.findViewById(R.id.readAndApplyTV) as TextView
        val tvReadBefApply: TextView = viewItem?.findViewById(R.id.readAndApply) as TextView
        val tvCompanyName: TextView = viewItem?.findViewById(R.id.companyAddressNameTV) as TextView
        val tvPostedDate: TextView = viewItem?.findViewById(R.id.postedDateTV) as TextView
        val tvCompanyAddress: TextView = viewItem?.findViewById(R.id.companyAddressTV) as TextView
        val keyPoints: TextView = viewItem?.findViewById(R.id.keyPoints) as TextView
        val companyLogo: ImageView = viewItem?.findViewById(R.id.company_icon) as ImageView
        val allJobsButtonLayout: RelativeLayout =
            viewItem?.findViewById(R.id.buttonLayout) as RelativeLayout
        val followTV: TextView = viewItem?.findViewById(R.id.followTV) as MaterialButton
        val viewAllJobsTV: TextView = viewItem?.findViewById(R.id.viewAllJobs) as TextView

        //        val applyButton: MaterialButton = viewItem?.findViewById(R.id.applyButton) as MaterialButton
        val applyLimitOverButton: MaterialButton =
            viewItem?.findViewById(R.id.applyLimitBtn) as MaterialButton

        val websiteHeadingTV: TextView = viewItem?.findViewById(R.id.wbsiteHeadingTV) as TextView
        val websiteTV: TextView = viewItem?.findViewById(R.id.websiteTV) as TextView
        val businessHeadingTV: TextView = viewItem?.findViewById(R.id.businessHeadingTV) as TextView
        val businessTV: TextView = viewItem?.findViewById(R.id.businessTV) as TextView
        val emailApplyTV: TextView = viewItem?.findViewById(R.id.emailApplyTV) as TextView
        val emailApplyMsgTV: TextView = viewItem?.findViewById(R.id.emailApplyMsgTV) as TextView
        val addressHeadingTV: TextView = viewItem?.findViewById(R.id.address) as TextView
        val jobExpirationBtn: Button = viewItem?.findViewById(R.id.jobexpirationBtn) as Button

        val adSmallTemplate: TemplateView =
            viewItem?.findViewById(R.id.ad_small_template) as TemplateView


        val reportBTN: Button = viewItem?.findViewById(R.id.reportBTN) as Button
        val callBTN: Button = viewItem?.findViewById(R.id.callBTN) as Button
        val emailBTN: Button = viewItem?.findViewById(R.id.emailBTN) as Button

        val whyIAmSeeingThisTV: TextView =
            viewItem?.findViewById(R.id.why_i_am_seeing_this_text) as TextView

        val jobApplicationStatusTitle: TextView =
            viewItem?.findViewById(R.id.job_detail_job_application_status_title) as TextView
        val jobApplicationStatusCard: ConstraintLayout =
            viewItem?.findViewById(R.id.job_detail_job_application_status_card) as ConstraintLayout
        val jobApplicationCountTV: TextView =
            viewItem?.findViewById(R.id.job_detail_job_application_count_tv) as TextView
        val jobApplicationRemainingTV: TextView =
            viewItem?.findViewById(R.id.job_detail_job_application_remaining_tv) as TextView

        val applyFab: ExtendedFloatingActionButton =
            viewItem?.findViewById(R.id.apply_now_fab) as ExtendedFloatingActionButton

        val workingPlaceTV: TextView = viewItem?.findViewById(R.id.tv_working_place) as TextView
        val workingPlaceValueTV: TextView =
            viewItem?.findViewById(R.id.tv_working_place_value) as TextView

        val liveViewRoot : RelativeLayout = viewItem?.findViewById(R.id.live_parent)!!
        val container: FrameLayout = viewItem?.findViewById(R.id.navHostFragment)!!
        val liveViewContainer: ConstraintLayout = viewItem?.findViewById(R.id.parent)!!
        val liveRecyclerView: RecyclerView = viewItem?.findViewById(R.id.recyclerView)!!
        val liveProgressBar: ProgressBar = viewItem?.findViewById(R.id.progressBar)!!
        val liveTitleTV: TextView = viewItem?.findViewById(R.id.titleTV)!!

        val videoResumeEncouragementTV: MaterialTextView =
            viewItem?.findViewById(R.id.tv_video_resume_encouragement_text)!!
        val viewDivider: View = viewItem?.findViewById(R.id.view_divider)!!

        val videoResumeEncBottomRoot : ConstraintLayout = viewItem?.findViewById(R.id.cv_video_encouragement)!!
        val closeIcon : ImageView = viewItem?.findViewById(R.id.iv_close_enc)!!

        val alertTv : TextView = viewItem?.findViewById(R.id.alertTVJD)!!


        fun fragment(fragment: Fragment) {

            (context as AppCompatActivity)
                .supportFragmentManager
                .beginTransaction()
                .add(container.id, fragment)
                .commitNow()
        }


    }


    private class LoadingVH(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var mErrorTxt: TextView? =
            itemView.findViewById(R.id.loadmore_errortxt) as TextView?
        var mErrorLayout: LinearLayout? =
            itemView.findViewById(R.id.loadmore_errorlayout) as LinearLayout?


        override fun onClick(view: View) {
            when (view.id) {
                R.id.loadmore_retry, R.id.loadmore_errorlayout -> {
                    /* adapter?.showRetry(false, null)
                     mCallback?.retryPageLoad()*/
                }
            }
        }

    }


    fun shareJobs(position: Int) {

        //Log.d("ShareJob", "position $position")

        var shareBody = ""
        try {
            shareBody = if (jobList!![position].lantype.equals("2")) {
                "${Constants.JOB_SHARE_URL}${jobList!![position].jobid}&ln=${
                    jobList!![position].lantype
                }"
            } else {
                "${Constants.JOB_SHARE_URL}${jobList!![position].jobid}&ln=${
                    jobList!![position].lantype
                }"
            }
        } catch (e: Exception) {
            logException(e)
        }


        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            "${jobList!![position].jobTitle}"
        )
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        context.startActivity(Intent.createChooser(sharingIntent, "Share"))

    }

    fun shortlistAndNoShortlistedJobs(position: Int) {
        val bdJobsUserSession = BdjobsUserSession(context)
        if (!bdJobsUserSession.isLoggedIn!!) {
            jobCommunicator?.setBackFrom("jobdetail")
            jobCommunicator?.goToLoginPage()
        } else {
            doAsync {
                val shortListed =
                    bdJobsDB.shortListedJobDao().isItShortListed(jobList?.get(position)?.jobid)
                uiThread {
                    if (shortListed) {
                        ApiServiceMyBdjobs.create().unShortlistJob(
                            userId = bdJobsUserSession.userId,
                            decodeId = bdJobsUserSession.decodId,
                            strJobId = jobList?.get(position)?.jobid!!
                        ).enqueue(object : Callback<UnshorlistJobModel> {
                            override fun onFailure(call: Call<UnshorlistJobModel>, t: Throwable) {
                                error("onFailure", t)
                            }

                            override fun onResponse(
                                call: Call<UnshorlistJobModel>,
                                response: Response<UnshorlistJobModel>
                            ) {
                                try {
                                    context.toast(response.body()?.message!!)
                                } catch (e: Exception) {
                                    logException(e)
                                }
                            }
                        })

                        doAsync {
                            bdJobsDB.shortListedJobDao()
                                .deleteShortListedJobsByJobID(jobList?.get(position)?.jobid!!)
                        }
                        uiThread {
                            showHideShortListedIcon(position)
                        }

                    } else {

                        ApiServiceJobs.create().insertShortListJob(
                            userID = bdJobsUserSession.userId,
                            encoded = Constants.ENCODED_JOBS,
                            jobID = jobList?.get(position)?.jobid!!
                        ).enqueue(object : Callback<ShortlistJobModel> {
                            override fun onFailure(call: Call<ShortlistJobModel>, t: Throwable) {
                                error("onFailure", t)
                            }

                            override fun onResponse(
                                call: Call<ShortlistJobModel>,
                                response: Response<ShortlistJobModel>
                            ) {
                                try {
                                    context.toast(response.body()?.data?.get(0)?.message!!)
                                } catch (e: Exception) {
                                    logException(e)
                                }
                            }
                        })

                        doAsync {
                            var deadline: Date? = null
                            try {
                                deadline = SimpleDateFormat(
                                    "MM/dd/yyyy",
                                    Locale.ENGLISH
                                ).parse(jobList?.get(position)?.deadlineDB!!)
                            } catch (e: Exception) {
                                logException(e)
                            }
                            //Log.d("DeadLine", "DeadLine: $deadline")
                            val shortlistedJob = ShortListedJobs(
                                jobid = jobList?.get(position)?.jobid!!,
                                jobtitle = jobList?.get(position)?.jobTitle!!,
                                companyname = jobList?.get(position)?.companyName!!,
                                deadline = deadline,
                                eduRec = jobList?.get(position)?.eduRec!!,
                                experience = jobList?.get(position)?.experience!!,
                                standout = jobList?.get(position)?.standout!!,
                                logo = jobList?.get(position)?.logo!!,
                                lantype = jobList?.get(position)?.lantype!!
                            )

                            bdJobsDB.shortListedJobDao().insertShortListedJob(shortlistedJob)
                            uiThread {
                                showHideShortListedIcon(position)
                            }
                        }

                    }
                }
            }
        }
    }

    fun showHideShortListedIcon(position: Int) {
        doAsync {
            val shortListed =
                bdJobsDB.shortListedJobDao().isItShortListed(jobList?.get(position)?.jobid)
            uiThread {
                if (shortListed) {
                    jobCommunicator?.showShortListedIcon()
                } else {
                    jobCommunicator?.showUnShortListedIcon()
                }

            }
        }
    }

    fun reportThisJob(position: Int) {
        try {
            val jobid = jobList?.get(position)?.jobid
            context.startActivity<WebActivity>(
                "url" to "https://jobs.bdjobs.com/reportthisjob.asp?id=$jobid",
                "from" to "reportJob"
            )
        } catch (e: Exception) {
            logException(e)
        }
    }


    private fun callFollowApi(companyId: String, companyName: String) {
        val bdjobsUserSession = BdjobsUserSession(context)
        ApiServiceJobs.create().getUnfollowMessage(
            id = companyId,
            name = companyName,
            userId = bdjobsUserSession.userId,
            encoded = Constants.ENCODED_JOBS,
            actType = "fei",
            decodeId = bdjobsUserSession.decodId
        ).enqueue(object : Callback<FollowUnfollowModelClass> {
            override fun onFailure(call: Call<FollowUnfollowModelClass>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(
                call: Call<FollowUnfollowModelClass>,
                response: Response<FollowUnfollowModelClass>
            ) {
                try {
                    val statuscode = response.body()?.statuscode
                    val message = response.body()?.data?.get(0)?.message
                    //Log.d("jobCount", "jobCount: ${response.body()?.data?.get(0)?.jobcount}")

                    if (statuscode?.equalIgnoreCase(Constants.api_request_result_code_ok)!!) {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        bdjobsUserSession.incrementFollowedEmployer()
                        doAsync {
                            val followedEmployer = FollowedEmployer(
                                CompanyID = companyId,
                                CompanyName = companyName,
                                JobCount = response.body()?.data?.get(0)?.jobcount,
                                FollowedOn = Date()
                            )
                            bdJobsDB.followedEmployerDao().insertFollowedEmployer(followedEmployer)
                        }
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }

        })
    }

    private fun callUnFollowApi(companyId: String, companyName: String) {
        val bdJobsUserSession = BdjobsUserSession(context)
        ApiServiceJobs.create().getUnfollowMessage(
            id = companyId,
            name = companyName,
            userId = bdJobsUserSession.userId,
            encoded = Constants.ENCODED_JOBS,
            actType = "fed",
            decodeId = bdJobsUserSession.decodId
        ).enqueue(object : Callback<FollowUnfollowModelClass> {
            override fun onFailure(call: Call<FollowUnfollowModelClass>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(
                call: Call<FollowUnfollowModelClass>,
                response: Response<FollowUnfollowModelClass>
            ) {

                try {
                    val statusCode = response.body()?.statuscode
                    val message = response.body()?.data?.get(0)?.message
                    //Log.d("msg", message)
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    if (statusCode?.equalIgnoreCase(Constants.api_request_result_code_ok)!!) {
                        doAsync {
                            bdJobsDB.followedEmployerDao()
                                .deleteFollowedEmployerByCompanyID(companyId, companyName)
                        }
                        bdJobsUserSession.deccrementFollowedEmployer()
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }

        })
    }

    private inner class MovementCheck : LinkMovementMethod() {

        override fun onTouchEvent(
            widget: TextView,
            buffer: Spannable,
            event: MotionEvent
        ): Boolean {
            return try {
                super.onTouchEvent(widget, buffer, event)
            } catch (ex: Exception) {
                true
            }
        }
    }


}