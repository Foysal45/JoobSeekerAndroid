package com.bdjobs.app.Jobs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Html
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.*
import com.bdjobs.app.Databases.Internal.AppliedJobs
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.FollowedEmployer
import com.bdjobs.app.Databases.Internal.ShortListedJobs
import com.bdjobs.app.Employers.EmployersBaseActivity
import com.bdjobs.app.ManageResume.ManageResumeActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Web.WebActivity
import com.bdjobs.app.editResume.EditResLandingActivity
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class JobDetailAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        // View Types
        private val BASIC = 0
        private val LOADING = 1
        private var appliedJobsCount = 0

    }

    private val bdjobsDB = BdjobsDB.getInstance(context)
    private val bdjobsUserSession = BdjobsUserSession(context)
    private var jobCommunicator: JobCommunicator? = null
    private var jobList: MutableList<JobListModelData>? = null
    var call: JobCommunicator? = null
    private var isLoadingAdded = false
    private var retryPageLoad = false
    private var errorMsg: String? = null
    var jobKeyPointsData = ""
    var jobContextData = ""
    var jobDescriptionData = ""
    var jobNatureData = ""
    var educationData = ""
    var experienceData = ""
    var requirmentsData = ""
    var salaryData = ""
    var otherBenifitsData = ""
    var jobSourceData = ""
    var readApplyData = ""
    var companyName = ""
    var companyAddress = ""
    var companyLogoUrl = ""
    var companyOtherJobs = ""
    var applyOnline = ""
    var applyStatus = false
    private lateinit var dialog: Dialog
    private val applyonlinePostions = ArrayList<Int>()

    private var language = ""


    init {
        jobList = java.util.ArrayList()
        jobCommunicator = context as JobCommunicator
        language = "bangla"
        Log.d("JobDetailFragment", "${jobList?.size}")
        appliedJobsCount = bdjobsUserSession.applyJobCount!!.toInt()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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
                jobsVH.itemView.setOnClickListener {
                    call?.onItemClicked(position)
                }

                jobsVH.callBTN.setOnClickListener {
                    try {
                        (context as Activity)?.callHelpLine()
                    } catch (e: Exception) {
                        logException(e)
                    }
                }

                jobsVH.emailBTN.setOnClickListener {
                    sendEmail()
                }

                jobsVH.reportBTN.setOnClickListener {
                    reportthisJob(position)
                }

                Log.d("JobId", "onResponse: ${jobList?.get(position)?.jobid!!}")

                jobsVH.shimmer_view_container.show()
                jobsVH.applyButton.visibility = View.GONE
                jobsVH.shimmer_view_container.startShimmerAnimation()
                jobCommunicator?.hideShortListIcon()


                ApiServiceJobs.create().getJobdetailData(Constants.ENCODED_JOBS, jobList?.get(position)?.jobid!!, jobList?.get(position)?.lantype!!, "", "0", bdjobsUserSession.userId, "EN").enqueue(object : Callback<JobDetailJsonModel> {
                    override fun onFailure(call: Call<JobDetailJsonModel>, t: Throwable) {
                        Log.d("ApiServiceJobs", "onFailure: fisrt time ${t.message}")
                    }

                    override fun onResponse(call: Call<JobDetailJsonModel>, response: Response<JobDetailJsonModel>) {

                        try {

                            Log.d("ApiServiceJobs", "onResponse: ${response.body()?.data?.get(0)?.jobTitle}")
                            Log.d("ApiServiceJobs", "onResponse: " + response.body())
                            jobsVH.shimmer_view_container.hide()
                            jobsVH.shimmer_view_container.stopShimmerAnimation()
                            Constants.showNativeAd(jobsVH.ad_small_template, context)
                            val jobDetailResponseAll = response.body()?.data?.get(0)

                            jobKeyPointsData = jobDetailResponseAll!!.jobKeyPoints!!
                            jobContextData = jobDetailResponseAll.context!!
                            jobDescriptionData = jobDetailResponseAll.jobDescription!!
                            jobNatureData = jobDetailResponseAll.jobNature!!
                            educationData = jobDetailResponseAll.educationRequirements!!
                            experienceData = jobDetailResponseAll.experience!!
                            requirmentsData = jobDetailResponseAll.additionJobRequirements!!
                            salaryData = jobDetailResponseAll.jobSalaryRange!!
                            otherBenifitsData = jobDetailResponseAll.jobOtherBenifits!!
                            jobSourceData = jobDetailResponseAll.jobSource!!
                            readApplyData = jobDetailResponseAll.applyInstruction!!
                            companyName = jobDetailResponseAll.compnayName!!
                            companyAddress = jobDetailResponseAll.companyAddress!!
                            companyLogoUrl = jobDetailResponseAll.jobLOgoName!!
                            companyOtherJobs = jobDetailResponseAll.companyOtherJ0bs!!
                            applyOnline = jobDetailResponseAll.onlineApply!!

                            if (appliedJobsCount >= 5) {
                                jobsVH.jobApplicationStatusTitle.show()
                                jobsVH.jobApplicationStatusCard.show()
                                jobsVH.jobApplicationCountTV.text = "You have already applied to ${appliedJobsCount} Job Application current month"
                                jobsVH.jobApplicationRemainingTV.text = "Only ${50 - appliedJobsCount} remaining"
                            } else {
                                jobsVH.jobApplicationStatusTitle.hide()
                                jobsVH.jobApplicationStatusCard.hide()

                            }

                            try {
                                val date = Date()
                                val formatter = SimpleDateFormat("MM/dd/yyyy")
                                val today: String = formatter.format(date)
                                val todayDate = SimpleDateFormat("MM/dd/yyyy").parse(today)

                                val deadline = jobDetailResponseAll.DeadlineDB!!
                                val deadlineDate = SimpleDateFormat("MM/dd/yyyy").parse(deadline)

                                Log.d("fphwrpeqspm", "todayDate: $todayDate deadlineDate:$deadlineDate")

                                if (todayDate > deadlineDate) {
                                    jobsVH.jobexpirationBtn.show()
                                    jobCommunicator?.hideShortListIcon()
                                } else {
                                    jobsVH.jobexpirationBtn.hide()
                                    jobCommunicator?.showShortListIcon()
                                }

                            } catch (e: Exception) {

                            }


                            if (jobDetailResponseAll.companyWeb.isNullOrBlank()) {
                                jobsVH.websiteTV.hide()
                                jobsVH.wbsiteHeadingTV.hide()
                            } else {
                                jobsVH.websiteTV.show()
                                jobsVH.wbsiteHeadingTV.show()

                                if (jobDetailResponseAll.companyWeb.startsWith("http") || jobDetailResponseAll.companyWeb.startsWith("Http")) {
                                    jobsVH.websiteTV.text = Html.fromHtml("<a href='" + jobDetailResponseAll.companyWeb + "'>" + jobDetailResponseAll.companyWeb + "</a>")
                                    jobsVH.websiteTV.movementMethod = MovementCheck()
                                } else {
                                    jobsVH.websiteTV.text = Html.fromHtml("<a href='https://" + jobDetailResponseAll.companyWeb + "'>" + jobDetailResponseAll.companyWeb + "</a>")
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
                                applyonlinePostions.add(position)
                            }

                            if (jobList?.get(position)?.lantype!!.equalIgnoreCase("2")) {
                                jobsVH.followTV.hide()
                            } else {
                                jobsVH.followTV.show()
                            }

                            jobsVH.tvPosName.text = jobDetailResponseAll.jobTitle
                            jobsVH.tvComName.text = jobDetailResponseAll.compnayName
                            jobsVH.tvSalary.text = jobDetailResponseAll.jobSalaryRange
                            jobsVH.tvDeadline.text = jobDetailResponseAll.deadline
                            jobsVH.tvLocation.text = jobDetailResponseAll.jobLocation
                            jobsVH.tvVacancies.text = jobDetailResponseAll.jobVacancies

                            jobsVH.whyIAmSeeingThisTV.setOnClickListener {
                                context.showJobApplicationGuidelineDialog()
                            }

                            jobsVH.applyButton.hide()
                            jobsVH.appliedBadge.hide()

                            jobsVH.followTV.setOnClickListener {
                                val bdjobsUserSession = BdjobsUserSession(context)
                                if (!bdjobsUserSession.isLoggedIn!!) {
                                    jobCommunicator?.setBackFrom("jobdetail")
                                    jobCommunicator?.goToLoginPage()
                                } else {
                                    doAsync {
                                        val isItFollowed = bdjobsDB.followedEmployerDao().isItFollowed(jobDetailResponseAll.companyID!!, jobDetailResponseAll.companyNameENG!!)
                                        uiThread {
                                            if (isItFollowed) {
                                                jobsVH.followTV.setTextColor(Color.parseColor("#13A10E"))
                                                jobsVH.followTV.text = "Follow"
                                                jobsVH.followTV.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                                                callUnFollowApi(jobDetailResponseAll.companyID, jobDetailResponseAll.companyNameENG)
                                            } else {
                                                jobsVH.followTV.text = "Unfollow"
                                                jobsVH.followTV.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E6E5EB"))
                                                jobsVH.followTV.setTextColor(Color.parseColor("#767676"))
                                                callFollowApi(jobDetailResponseAll.companyID, jobDetailResponseAll.companyNameENG)
                                            }
                                        }
                                    }
                                }
                            }

                            Log.d("applyPostion", "online: $applyonlinePostions")
                            if (applyOnline.equalIgnoreCase("True")) {
                                jobsVH.applyButton.visibility = View.VISIBLE
                                jobsVH.applyButton.setOnClickListener {
                                    val bdjobsUserSession = BdjobsUserSession(context)
                                    if (!bdjobsUserSession.isLoggedIn!!) {
                                        jobCommunicator?.setBackFrom("jobdetail")
                                        jobCommunicator?.goToLoginPage()
                                    } else {
                                        if (!bdjobsUserSession.isCvPosted?.equalIgnoreCase("true")!!) {
                                            try {
                                                val alertd = context.alert("To Access this feature please post your resume") {
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
                                            showWarningPopup(context, position, jobDetailResponseAll.gender!!, jobDetailResponseAll.photograph!!)
                                            //checkApplyEligibility(context, position, jobDetailResponseAll.gender!!, jobDetailResponseAll.photograph!!)
                                        }
                                    }
                                }
                            } else {
                                jobsVH.applyButton.visibility = View.GONE
                            }


                            doAsync {
                                val appliedJobs = bdjobsDB.appliedJobDao().getAppliedJobsById(jobList?.get(position)?.jobid!!)
                                val isItFollowed = bdjobsDB.followedEmployerDao().isItFollowed(jobDetailResponseAll.companyID!!, jobDetailResponseAll.companyNameENG!!)

                                uiThread {
                                    if (appliedJobs.isEmpty()) {
                                        jobsVH.appliedBadge.hide()
                                    } else {
                                        jobsVH.appliedBadge.show()
                                        jobsVH.applyButton.visibility = View.GONE
                                    }

                                    if (isItFollowed) {
                                        jobsVH.followTV.text = "Unfollow"
                                        jobsVH.followTV.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E6E5EB"))
                                        jobsVH.followTV.setTextColor(Color.parseColor("#767676"))
                                    } else {
                                        jobsVH.followTV.setTextColor(Color.parseColor("#13A10E"))
                                        jobsVH.followTV.text = "Follow"
                                        jobsVH.followTV.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                                    }
                                }
                            }
                            //Job Information Checking Start

                            if (jobDetailResponseAll.jObIMage.isNullOrBlank()) {
                                jobsVH.govtJobsIMGV.hide()
                                jobsVH.horizontalView.show()
                                jobsVH.horizontalViewTwo.show()
                                jobsVH.horizontalView.show()

                                if (jobKeyPointsData.isBlank()) {

                                    jobsVH.tvKeyPoints.visibility = View.GONE
                                    jobsVH.keyPonits.visibility = View.GONE

                                } else {
                                    jobsVH.tvKeyPoints.visibility = View.VISIBLE
                                    jobsVH.keyPonits.visibility = View.VISIBLE
                                    jobsVH.tvKeyPoints.text = response.body()?.data?.get(0)?.jobKeyPoints
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


                                if (educationData.isBlank() && experienceData.isBlank() && requirmentsData.isBlank()) {

                                    jobsVH.tvEducationalRequirmentsValue.visibility = View.GONE
                                    jobsVH.tvEducationalRequirments.visibility = View.GONE
                                    jobsVH.tvExperienceReq.visibility = View.GONE
                                    jobsVH.tvExperienceReqValue.visibility = View.GONE
                                    jobsVH.tvJobReqValue.visibility = View.GONE
                                    jobsVH.tvJobReq.visibility = View.GONE
                                    jobsVH.tvRequirementsHead.visibility = View.GONE

                                } else {

                                    if (educationData.isBlank()) {

                                        jobsVH.tvEducationalRequirmentsValue.visibility = View.GONE
                                        jobsVH.tvEducationalRequirments.visibility = View.GONE

                                    } else {
                                        jobsVH.tvEducationalRequirmentsValue.text = educationData
                                        jobsVH.tvEducationalRequirmentsValue.visibility = View.VISIBLE
                                        jobsVH.tvEducationalRequirments.visibility = View.VISIBLE

                                    }

                                    if (experienceData.isBlank()) {

                                        jobsVH.tvExperienceReq.visibility = View.GONE
                                        jobsVH.tvExperienceReqValue.visibility = View.GONE

                                    } else {
                                        jobsVH.tvExperienceReqValue.text = experienceData
                                        jobsVH.tvExperienceReq.visibility = View.VISIBLE
                                        jobsVH.tvExperienceReqValue.visibility = View.VISIBLE
                                    }

                                    if (requirmentsData.isBlank()) {
                                        jobsVH.tvJobReqValue.visibility = View.GONE
                                        jobsVH.tvJobReq.visibility = View.GONE
                                    } else {
                                        jobsVH.tvJobReqValue.text = requirmentsData
                                        jobsVH.tvJobReqValue.visibility = View.VISIBLE
                                        jobsVH.tvJobReq.visibility = View.VISIBLE
                                    }

                                }

                                if (salaryData.isBlank() && otherBenifitsData.isBlank()) {

                                    jobsVH.tvSalaryRange.visibility = View.GONE
                                    jobsVH.tvSalaryRangeData.visibility = View.GONE
                                    jobsVH.tvOtherBenifits.visibility = View.GONE
                                    jobsVH.tvOtherBenifitsData.visibility = View.GONE
                                    jobsVH.tvSalaryAndCompensation.visibility = View.GONE


                                } else {

                                    if (salaryData.isBlank()) {

                                        jobsVH.tvSalaryRange.visibility = View.GONE
                                        jobsVH.tvSalaryRangeData.visibility = View.GONE

                                    } else {

                                        jobsVH.tvSalaryRange.visibility = View.VISIBLE
                                        jobsVH.tvSalaryRangeData.visibility = View.VISIBLE
                                        jobsVH.tvSalaryRangeData.text = salaryData
                                    }

                                    if (otherBenifitsData.isBlank()) {

                                        jobsVH.tvOtherBenifits.visibility = View.GONE
                                        jobsVH.tvOtherBenifitsData.visibility = View.GONE

                                    } else {

                                        jobsVH.tvOtherBenifitsData.text = otherBenifitsData
                                        jobsVH.tvOtherBenifits.visibility = View.VISIBLE
                                        jobsVH.tvOtherBenifitsData.visibility = View.VISIBLE

                                    }

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
                                        jobsVH.emailApplyMsgTV.text = "or to Email CV from MY BDJOBS account please "
                                        jobsVH.emailApplyTV.text = Html.fromHtml("<a href='" + "CLICK HERE" + "'>" + "CLICK HERE" + "</a>")

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

                                jobsVH.viewAllJobsTV.text = "View ${jobDetailResponseAll.companyOtherJ0bs} more $job of this company"

                                try {
                                    if (jobDetailResponseAll.companyOtherJ0bs.toInt() > 0) {
                                        jobsVH.allJobsButtonLayout.setOnClickListener {
                                            context.startActivity<EmployersBaseActivity>("from" to "joblist",
                                                    "companyid" to jobDetailResponseAll.companyID,
                                                    "companyname" to jobDetailResponseAll.companyNameENG,
                                                    "jobId" to jobDetailResponseAll.jobId
                                            )
                                        }
                                    }
                                } catch (e: Exception) {
                                    logException(e)
                                }

                            } else {


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

                                jobsVH.viewAllJobsTV.text = "View ${jobDetailResponseAll.companyOtherJ0bs} more $job of this company"

                                try {
                                    if (jobDetailResponseAll.companyOtherJ0bs.toInt() > 0) {
                                        jobsVH.allJobsButtonLayout.setOnClickListener {
                                            context.startActivity<EmployersBaseActivity>("from" to "joblist",
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
                                jobsVH.keyPonits.hide()
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
                                jobsVH.tvEducationalRequirments.hide()
                                jobsVH.tvEducationalRequirmentsValue.hide()
                                jobsVH.tvExperienceReq.hide()
                                jobsVH.tvExperienceReqValue.hide()
                                jobsVH.tvEducationalRequirmentsValue.hide()
                                jobsVH.tvSalaryAndCompensation.hide()
                                jobsVH.tvSalary.hide()
                                jobsVH.tvSalaryRange.hide()
                                jobsVH.tvSalaryRangeData.hide()
                                jobsVH.tvOtherBenifits.hide()
                                jobsVH.tvOtherBenifitsData.hide()
                                jobsVH.tvReadBefApply.hide()
                                jobsVH.tvReadBefApplyData.hide()


                            }
                        } catch (e: Exception) {
                            logException(e)
                        }

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

    private fun sendEmail() {
        context.email("complain@bdjobs.com", "", "")
    }

    private fun checkApplyEligibility(activity: Context, position: Int, gender: String, jobphotograph: String) {

        val bdjobsUserSession = BdjobsUserSession(context)
        val loadingDialog = activity.indeterminateProgressDialog("Applying")
        loadingDialog?.setCancelable(false)
        loadingDialog?.show()

        ApiServiceJobs.create().applyEligibilityCheck(
                userID = bdjobsUserSession.userId, decodeID = bdjobsUserSession.decodId, jobID = jobList?.get(position)?.jobid!!, JobSex = gender, JobPhotograph = jobphotograph, encoded = Constants.ENCODED_JOBS
        ).enqueue(
                object : Callback<ApplyEligibilityModel> {
                    override fun onFailure(call: Call<ApplyEligibilityModel>, t: Throwable) {
                        loadingDialog?.dismiss()
                    }

                    override fun onResponse(call: Call<ApplyEligibilityModel>, response: Response<ApplyEligibilityModel>) {

                        loadingDialog?.dismiss()

                        try {
                            if (response.isSuccessful) {
                                if (response.body()?.data?.get(0)?.applyEligibility?.equalIgnoreCase("true")!!) {
                                    showSalaryDialog(activity, position, gender, jobphotograph)
                                } else {
                                    val plainMessage = response.body()?.data?.get(0)?.message
                                    val plainHeading = response.body()?.data?.get(0)?.title

                                    val message = Html.fromHtml(plainMessage)
                                    val heading = Html.fromHtml(plainHeading)

                                    val alertd = context.alert(message) {
                                        title = heading
                                        //positiveButton("Post Resume") { context.startActivity<EditResLandingActivity>() }
                                        negativeButton("OK") { dd ->
                                            dd.dismiss()
                                        }
                                    }
                                    alertd.isCancelable = false
                                    alertd.show()
                                }
                            }
                        } catch (e: Exception) {
                            logException(e)
                        }
                    }
                }
        )
    }


    private fun showWarningPopup(context: Context, position: Int, gender: String, jobphotograph: String) {
        val dialog = Dialog(context)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.layout_warning_popup)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var warningTitleTV = dialog?.findViewById<TextView>(R.id.txt_warning_title)
        var warningMessageTV = dialog?.findViewById<TextView>(R.id.txt_warning_message)
        val translateIV = dialog?.findViewById<ImageView>(R.id.img_translate)
        val cancelBtn = dialog?.findViewById<Button>(R.id.btn_cancel)
        val agreedBtn = dialog?.findViewById<Button>(R.id.btn_agreed)
        val agreedCheckBox = dialog?.findViewById<CheckBox>(R.id.chk_bx_agreed)
        val ad_small_template = dialog?.findViewById<TemplateView>(R.id.ad_small_template)
        Constants.showNativeAd(ad_small_template, context)

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
                    agreedCheckBox?.text = context.getString(R.string.warning_message_agreement_bangla)
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
            dialog?.dismiss()
            checkApplyEligibility(context, position, gender, jobphotograph)
        }
        dialog?.show()

//        val showButton = dialog.findViewById<Button>(R.id.bcYesTV)
//        val cancelIV = dialog.findViewById<ImageView>(R.id.deleteIV)
//        val jobCountTV = dialog.findViewById<TextView>(R.id.textView49)
//        val checkBox = dialog.findViewById<CheckBox>(R.id.checkBox2)
    }


    private fun showSalaryDialog(activity: Context, position: Int, gender: String, jobphotograph: String) {
        dialog = Dialog(activity)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.online_apply_dialog_layout)
        val cancelButton = dialog?.findViewById<Button>(R.id.onlineApplyCancelBTN)
        val okButton = dialog?.findViewById<Button>(R.id.onlineApplyOkBTN)
        val salaryTIET = dialog?.findViewById<TextInputEditText>(R.id.salaryAmountTIET)
        val salaryTIL = dialog?.findViewById<TextInputLayout>(R.id.salaryAmountTIL)
        val ad_small_template = dialog?.findViewById<TemplateView>(R.id.ad_small_template)

        val jobApplicationStatusCard = dialog?.findViewById<ConstraintLayout>(R.id.job_detail_job_application_status_card)
        val appliedJobsCountTV = dialog?.findViewById<TextView>(R.id.job_detail_job_application_count_tv)
        val remainingJobsCountTV = dialog?.findViewById<TextView>(R.id.job_detail_job_application_remaining_tv)
        val whyIAmSeeingThisTV = dialog?.findViewById<TextView>(R.id.why_i_am_seeing_this_text)

        Constants.showNativeAd(ad_small_template, context)

        if (appliedJobsCount >= 5) {
            jobApplicationStatusCard.show()
            appliedJobsCountTV.text = "You have already applied to ${appliedJobsCount} Job Application current month"
            remainingJobsCountTV.text = "Only ${50 - appliedJobsCount} remaining"
            whyIAmSeeingThisTV.setOnClickListener {
                context.showJobApplicationGuidelineDialog()
            }
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

            if (validateFilterName(salaryTIET.getString(), salaryTIL) && !applyStatus) {
                applyStatus = true
                applyOnlineJob(position, salaryTIET.text.toString(), gender, jobphotograph)
                d("applyTest validate $applyStatus")

            }


        }
        dialog?.show()
    }


    private fun validateFilterName(typedData: String, textInputLayout: TextInputLayout): Boolean {

        if (typedData.isNullOrBlank()) {
            textInputLayout.showError(context.getString(R.string.field_empty_error_message_common))
            return false
        }
        textInputLayout.hideError()
        return true
    }

    private fun applyOnlineJob(position: Int, salary: String, gender: String, jobphotograph: String) {
        Log.d("dlkgj", "gender $gender jobid:${jobList?.get(position)?.jobid!!}")
        val bdjobsUserSession = BdjobsUserSession(context)
        val loadingDialog = context.indeterminateProgressDialog("Applying")
        loadingDialog?.setCancelable(false)
        loadingDialog?.show()
        ApiServiceJobs.create().applyJob(bdjobsUserSession.userId, bdjobsUserSession.decodId, jobList?.get(position)?.jobid!!, salary, gender, jobphotograph, Constants.ENCODED_JOBS).enqueue(object : Callback<ApplyOnlineModel> {
            override fun onFailure(call: Call<ApplyOnlineModel>, t: Throwable) {

                Log.d("dlkgj", "respone ${t.message}")
                loadingDialog?.dismiss()
                dialog?.dismiss()
                applyStatus = false
                d("applyTest onFailure ")

            }

            override fun onResponse(call: Call<ApplyOnlineModel>, response: Response<ApplyOnlineModel>) {


                try {

                    d("applyTest onResponse ")
                    dialog?.dismiss()
                    loadingDialog?.dismiss()
                    context.longToast(response.body()!!.data[0].message)
                    if (response.body()!!.data[0].status.equalIgnoreCase("ok")) {
                        bdjobsUserSession.incrementJobsApplied()
                        bdjobsUserSession.decrementAvailableJobs()
                        applyStatus = true
                        doAsync {
                            val appliedJobs = AppliedJobs(appliedid = jobList?.get(position)?.jobid!!)
                            bdjobsDB.appliedJobDao().insertAppliedJobs(appliedJobs)
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

        if (position == jobList!!.size - 1 && isLoadingAdded) {
            return LOADING
        } else if (jobList!![position].standout.equals("1")) {
            return BASIC
        } else if (jobList!![position].standout.equals("0")) {
            return BASIC
        }
        return LOADING
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

        Log.d("riuhghugr", "getItemViewType" + getItemViewType(position))

        Log.d("riuhghugr", " result: $result")
        if (result?.jobid.isNullOrBlank()) {
            this.jobList!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): JobListModelData? {
        return jobList!![position]
    }


    fun showRetry(show: Boolean, errorMsg: String?) {
        retryPageLoad = show
        jobList?.size?.minus(1)?.let { notifyItemChanged(it) }

        if (errorMsg != null) this.errorMsg = errorMsg
    }

    /**
     * Main list's content ViewHolder
     */

    private class JobsListVH(viewItem: View?) : RecyclerView.ViewHolder(viewItem!!) {

        val horizontalViewTwo: View = viewItem?.findViewById(R.id.horizontalViewTwo) as View
        val horizontalView: View = viewItem?.findViewById(R.id.horizontalView) as View
        val horizontalViewfour: View = viewItem?.findViewById(R.id.horizontalViewfour) as View

        val jobInfo: TextView = viewItem?.findViewById(R.id.jobInfo) as TextView
        val govtJobsIMGV: ImageView = viewItem?.findViewById(R.id.govtJobsIMGV) as ImageView

        val shimmer_view_container: ShimmerFrameLayout = viewItem?.findViewById(R.id.shimmer_view_container) as ShimmerFrameLayout
        val appliedBadge: TextView = viewItem?.findViewById(R.id.appliedBadge) as TextView
        val tvPosName: TextView = viewItem?.findViewById(R.id.positionName) as TextView
        val tvComName: TextView = viewItem?.findViewById(R.id.companyName) as TextView
        val tvLocation: TextView = viewItem?.findViewById(R.id.locationValue) as TextView
        val tvDeadline: TextView = viewItem?.findViewById(R.id.deadlineValue) as TextView
        val tvVacancies: TextView = viewItem?.findViewById(R.id.vacancyValue) as TextView
        val tvSalary: TextView = viewItem?.findViewById(R.id.salaryValue) as TextView
        val tvKeyPoints: TextView = viewItem?.findViewById(R.id.keyPointsTv) as TextView
        val tvJobContextValue: TextView = viewItem?.findViewById(R.id.jobContextTV) as TextView
        val tvJobContext: TextView = viewItem?.findViewById(R.id.jobContext) as TextView
        val tvJobResponsibilityValue: TextView = viewItem?.findViewById(R.id.responsibilityTV) as TextView
        val tvJobResponsibility: TextView = viewItem?.findViewById(R.id.responsibility) as TextView
        val tvJobNatureValue: TextView = viewItem?.findViewById(R.id.jobNatureTv) as TextView
        val tvJobNature: TextView = viewItem?.findViewById(R.id.jobNature) as TextView
        val tvEducationalRequirmentsValue: TextView = viewItem?.findViewById(R.id.educationTV) as TextView
        val tvEducationalRequirments: TextView = viewItem?.findViewById(R.id.education) as TextView
        val tvExperienceReqValue: TextView = viewItem?.findViewById(R.id.experienceTV) as TextView
        val tvExperienceReq: TextView = viewItem?.findViewById(R.id.Experience) as TextView
        val tvJobReqValue: TextView = viewItem?.findViewById(R.id.jobRequirementsTV) as TextView
        val tvJobReq: TextView = viewItem?.findViewById(R.id.jobRequirements) as TextView
        val tvRequirementsHead: TextView = viewItem?.findViewById(R.id.requirements) as TextView
        val tvSalaryRangeData: TextView = viewItem?.findViewById(R.id.salaryRangeTV) as TextView
        val tvSalaryRange: TextView = viewItem?.findViewById(R.id.salaryRange) as TextView
        val tvOtherBenifitsData: TextView = viewItem?.findViewById(R.id.otherBenefitsTV) as TextView
        val tvOtherBenifits: TextView = viewItem?.findViewById(R.id.otherBenefits) as TextView
        val tvSalaryAndCompensation: TextView = viewItem?.findViewById(R.id.salaryAndCompensation) as TextView
        val tvJobSource: TextView = viewItem?.findViewById(R.id.jobSourceTV) as TextView
        val tvJobSourceHeading: TextView = viewItem?.findViewById(R.id.JobSource) as TextView
        val tvReadBefApplyData: TextView = viewItem?.findViewById(R.id.readAndApplyTV) as TextView
        val tvReadBefApply: TextView = viewItem?.findViewById(R.id.readAndApply) as TextView
        val tvCompanyName: TextView = viewItem?.findViewById(R.id.companyAddressNameTV) as TextView
        val tvCompanyAddress: TextView = viewItem?.findViewById(R.id.companyAddressTV) as TextView
        val keyPonits: TextView = viewItem?.findViewById(R.id.keyPoints) as TextView
        val companyLogo: ImageView = viewItem?.findViewById(R.id.company_icon) as ImageView
        val allJobsButtonLayout: RelativeLayout = viewItem?.findViewById(R.id.buttonLayout) as RelativeLayout
        val followTV: TextView = viewItem?.findViewById(R.id.followTV) as MaterialButton
        val viewAllJobsTV: TextView = viewItem?.findViewById(R.id.viewAllJobs) as TextView
        val applyButton: MaterialButton = viewItem?.findViewById(R.id.applyButton) as MaterialButton

        val wbsiteHeadingTV: TextView = viewItem?.findViewById(R.id.wbsiteHeadingTV) as TextView
        val websiteTV: TextView = viewItem?.findViewById(R.id.websiteTV) as TextView
        val businessHeadingTV: TextView = viewItem?.findViewById(R.id.businessHeadingTV) as TextView
        val businessTV: TextView = viewItem?.findViewById(R.id.businessTV) as TextView
        val emailApplyTV: TextView = viewItem?.findViewById(R.id.emailApplyTV) as TextView
        val emailApplyMsgTV: TextView = viewItem?.findViewById(R.id.emailApplyMsgTV) as TextView
        val addressHeadingTV: TextView = viewItem?.findViewById(R.id.address) as TextView
        val jobexpirationBtn: Button = viewItem?.findViewById(R.id.jobexpirationBtn) as Button

        val ad_small_template: TemplateView = viewItem?.findViewById(R.id.ad_small_template) as TemplateView


        val reportBTN: Button = viewItem?.findViewById(R.id.reportBTN) as Button
        val callBTN: Button = viewItem?.findViewById(R.id.callBTN) as Button
        val emailBTN: Button = viewItem?.findViewById(R.id.emailBTN) as Button

        val whyIAmSeeingThisTV: TextView = viewItem?.findViewById(R.id.why_i_am_seeing_this_text) as TextView

        val jobApplicationStatusTitle: TextView = viewItem?.findViewById(R.id.job_detail_job_application_status_title) as TextView
        val jobApplicationStatusCard: ConstraintLayout = viewItem?.findViewById(R.id.job_detail_job_application_status_card) as ConstraintLayout
        val jobApplicationCountTV: TextView = viewItem?.findViewById(R.id.job_detail_job_application_count_tv) as TextView
        val jobApplicationRemainingTV: TextView = viewItem?.findViewById(R.id.job_detail_job_application_remaining_tv) as TextView


    }


    private class LoadingVH(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var mProgressBar: ProgressBar? = itemView.findViewById(R.id.loadmore_progress) as ProgressBar?
        private var mRetryBtn: ImageButton? = itemView.findViewById(R.id.loadmore_retry) as ImageButton?
        internal var mErrorTxt: TextView? = itemView.findViewById(R.id.loadmore_errortxt) as TextView?
        internal var mErrorLayout: LinearLayout? = itemView.findViewById(R.id.loadmore_errorlayout) as LinearLayout?


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

        Log.d("ShareJob", "position $position")

        var shareBody = ""
        try {
            if (jobList!!.get(position).lantype.equals("2")) {
                shareBody = "${Constants.JOB_SHARE_URL}${jobList!!.get(position).jobid}&ln=${jobList!!.get(position).lantype}"
            } else {
                shareBody = "${Constants.JOB_SHARE_URL}${jobList!!.get(position).jobid}"
            }
        } catch (e: Exception) {
            logException(e)
        }


        val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "${jobList!!.get(position).jobTitle}")
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
        context.startActivity(Intent.createChooser(sharingIntent, "Share"))

    }

    fun shorlistAndUnshortlistJob(position: Int) {
        val bdjobsUserSession = BdjobsUserSession(context)
        if (!bdjobsUserSession.isLoggedIn!!) {
            jobCommunicator?.setBackFrom("jobdetail")
            jobCommunicator?.goToLoginPage()
        } else {
            doAsync {
                val shortListed = bdjobsDB.shortListedJobDao().isItShortListed(jobList?.get(position)?.jobid)
                uiThread {
                    if (shortListed) {
                        ApiServiceMyBdjobs.create().unShortlistJob(
                                userId = bdjobsUserSession.userId,
                                decodeId = bdjobsUserSession.decodId,
                                strJobId = jobList?.get(position)?.jobid!!
                        ).enqueue(object : Callback<UnshorlistJobModel> {
                            override fun onFailure(call: Call<UnshorlistJobModel>, t: Throwable) {
                                error("onFailure", t)
                            }

                            override fun onResponse(call: Call<UnshorlistJobModel>, response: Response<UnshorlistJobModel>) {
                                try {
                                    context.toast(response.body()?.message!!)
                                } catch (e: Exception) {
                                    logException(e)
                                }
                            }
                        })

                        doAsync {
                            bdjobsDB.shortListedJobDao().deleteShortListedJobsByJobID(jobList?.get(position)?.jobid!!)
                        }
                        uiThread {
                            showHideShortListedIcon(position)
                        }

                    } else {

                        ApiServiceJobs.create().insertShortListJob(
                                userID = bdjobsUserSession.userId,
                                encoded = Constants.ENCODED_JOBS,
                                jobID = jobList?.get(position)?.jobid!!
                        ).enqueue(object : Callback<ShortlistJobModel> {
                            override fun onFailure(call: Call<ShortlistJobModel>, t: Throwable) {
                                error("onFailure", t)
                            }

                            override fun onResponse(call: Call<ShortlistJobModel>, response: Response<ShortlistJobModel>) {
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
                                deadline = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(jobList?.get(position)?.deadlineDB)
                            } catch (e: Exception) {
                                logException(e)
                            }
                            Log.d("DeadLine", "DeadLine: $deadline")
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

                            bdjobsDB.shortListedJobDao().insertShortListedJob(shortlistedJob)
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
            val shortListed = bdjobsDB.shortListedJobDao().isItShortListed(jobList?.get(position)?.jobid)
            uiThread {
                if (shortListed) {
                    jobCommunicator?.showShortListedIcon()
                } else {
                    jobCommunicator?.showUnShortListedIcon()
                }

            }
        }
    }

    fun reportthisJob(position: Int) {
        try {
            val jobid = jobList?.get(position)?.jobid
            context.startActivity<WebActivity>("url" to "https://jobs.bdjobs.com/reportthisjob.asp?id=$jobid", "from" to "reportJob")
        } catch (e: Exception) {
            logException(e)
        }
    }


    private fun callFollowApi(companyid: String, companyname: String) {
        val bdjobsUserSession = BdjobsUserSession(context)
        ApiServiceJobs.create().getUnfollowMessage(id = companyid, name = companyname, userId = bdjobsUserSession.userId, encoded = Constants.ENCODED_JOBS, actType = "fei", decodeId = bdjobsUserSession.decodId).enqueue(object : Callback<FollowUnfollowModelClass> {
            override fun onFailure(call: Call<FollowUnfollowModelClass>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<FollowUnfollowModelClass>, response: Response<FollowUnfollowModelClass>) {
                try {
                    val statuscode = response.body()?.statuscode
                    val message = response.body()?.data?.get(0)?.message
                    Log.d("jobCount", "jobCount: ${response.body()?.data?.get(0)?.jobcount}")

                    if (statuscode?.equalIgnoreCase(Constants.api_request_result_code_ok)!!) {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        bdjobsUserSession.incrementFollowedEmployer()
                        doAsync {
                            val followedEmployer = FollowedEmployer(
                                    CompanyID = companyid,
                                    CompanyName = companyname,
                                    JobCount = response.body()?.data?.get(0)?.jobcount,
                                    FollowedOn = Date()
                            )
                            bdjobsDB.followedEmployerDao().insertFollowedEmployer(followedEmployer)
                        }
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }

        })
    }

    private fun callUnFollowApi(companyid: String, companyName: String) {
        val bdjobsUserSession = BdjobsUserSession(context)
        ApiServiceJobs.create().getUnfollowMessage(id = companyid, name = companyName, userId = bdjobsUserSession.userId, encoded = Constants.ENCODED_JOBS, actType = "fed", decodeId = bdjobsUserSession.decodId).enqueue(object : Callback<FollowUnfollowModelClass> {
            override fun onFailure(call: Call<FollowUnfollowModelClass>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<FollowUnfollowModelClass>, response: Response<FollowUnfollowModelClass>) {

                try {
                    var statuscode = response.body()?.statuscode
                    var message = response.body()?.data?.get(0)?.message
                    Log.d("msg", message)
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    if (statuscode?.equalIgnoreCase(Constants.api_request_result_code_ok)!!) {
                        doAsync {
                            bdjobsDB.followedEmployerDao().deleteFollowedEmployerByCompanyID(companyid, companyName)
                        }
                        bdjobsUserSession.deccrementFollowedEmployer()
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }

        })
    }

    private inner class MovementCheck : LinkMovementMethod() {

        override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
            try {
                return super.onTouchEvent(widget, buffer, event)
            } catch (ex: Exception) {
                return true
            }
        }
    }


}