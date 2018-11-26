package com.bdjobs.app.Jobs

import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Layout.JUSTIFICATION_MODE_INTER_WORD
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.JobDetailJsonModel
import com.bdjobs.app.API.ModelClasses.JobListModelData
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.bdjobs.app.Login.LoginBaseActivity
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import org.jetbrains.anko.intentFor


class JobDetailAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        // View Types
        private val BASIC = 0
        private val LOADING = 1
        private val STANDOUT = 2
        private val BASIC_AD = 3
        private val STANDOUT_AD = 4
        private var showAD = true
    }

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


    init {
        jobList = java.util.ArrayList()
        jobCommunicator = context as JobCommunicator
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

        /* Log.d("Check"," on Bind View holder Position " + position)
         Log.d("Check"," getItemViewType(position) " + getItemViewType(position))*/

        var result = jobList?.get(position) // jobs


        when (getItemViewType(position)) {
            BASIC -> {

                /*   Log.d("Check", " BASIC $BASIC")*/

                val jobsVH = holder as JobsListVH






                jobsVH.itemView.setOnClickListener {

                    call?.onItemClicked(position)

                }


                jobsVH.allJobsButtonLayout.setOnClickListener {

                   Toast.makeText(context," View all jobs Button clicked ",Toast.LENGTH_LONG).show()

                }


                jobsVH.followTV.setOnClickListener {

                    Toast.makeText(context," follow Button clicked ",Toast.LENGTH_LONG).show()

                }
                Log.d("JobId", "onResponse: ${jobList?.get(position)?.jobid!!}")



                jobsVH.shimmer_view_container.show()
                jobsVH.applyButton.visibility = View.GONE
                jobsVH.shimmer_view_container.startShimmerAnimation()




                ApiServiceJobs.create().getJobdetailData("11JBSJ6251402", jobList?.get(position)?.jobid!!, jobList?.get(position)?.lantype!!, "", "0", "2335238", "EN").enqueue(object : Callback<JobDetailJsonModel> {
                    override fun onFailure(call: Call<JobDetailJsonModel>, t: Throwable) {
                        Log.d("ApiServiceJobs", "onFailure: fisrt time ${t.message}")
                    }

                    override fun onResponse(call: Call<JobDetailJsonModel>, response: Response<JobDetailJsonModel>) {
                        Log.d("ApiServiceJobs", "onResponse: ${response?.body()?.data?.get(0)?.jobTitle}")
                        Log.d("ApiServiceJobs", "onResponse: " + response?.body())
                        jobsVH.shimmer_view_container.hide()
                        jobsVH.shimmer_view_container.stopShimmerAnimation()

                        val jobDetailResponseAll = response.body()?.data?.get(0)

                        jobKeyPointsData = jobDetailResponseAll!!.jobKeyPoints!!
                        jobContextData =  jobDetailResponseAll.context!!
                        jobDescriptionData =  jobDetailResponseAll.jobDescription!!
                        jobNatureData = jobDetailResponseAll.jobNature!!
                        educationData = jobDetailResponseAll.educationRequirements!!
                        experienceData =jobDetailResponseAll.experience!!
                        requirmentsData = jobDetailResponseAll.additionJobRequirements!!
                        salaryData = jobDetailResponseAll.jobSalaryRange!!
                        otherBenifitsData = jobDetailResponseAll.jobOtherBenifits!!
                        jobSourceData = jobDetailResponseAll.jobSource!!
                        readApplyData = jobDetailResponseAll.applyInstruction!!
                        companyName = jobDetailResponseAll.compnayName!!
                        companyAddress = jobDetailResponseAll.companyAddress!!
                        companyLogoUrl =jobDetailResponseAll.jobLOgoName!!
                        companyOtherJobs = jobDetailResponseAll.companyOtherJ0bs!!
                        applyOnline = jobDetailResponseAll.onlineApply!!

                        jobsVH.tvPosName.text =jobDetailResponseAll.jobTitle
                        jobsVH.tvComName.text = jobDetailResponseAll.compnayName
                        jobsVH.tvSalary.text = jobDetailResponseAll.jobSalaryRange
                        jobsVH.tvDeadline.text = jobDetailResponseAll.deadline
                        jobsVH.tvLocation.text = jobDetailResponseAll.jobLocation
                        jobsVH.tvVacancies.text = jobDetailResponseAll.jobVacancies


                        if (applyOnline.equalIgnoreCase("True")){

                            jobsVH.applyButton.visibility = View.VISIBLE
                            jobsVH.applyButton.setOnClickListener {
                                val bdjobsUserSession = BdjobsUserSession(context)
                                if(!bdjobsUserSession.isLoggedIn!!){
                                    /*val activity = context as Activity
                                    val intent = Intent(activity, LoginBaseActivity::class.java)
                                    intent.putExtra("goToHome",false)
                                    activity.startActivity(intent)*/
                                    jobCommunicator?.goToLoginPage()
                                }
                            }

                        } else {

                            jobsVH.applyButton.visibility = View.GONE
                        }

                        //Job Information Checking Start

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

                        //Job Information Checking End


                        // Job Requirements Checking Start

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

                        // Job Requirements Checking End

                        // Salary Compensation Checking Start

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


                        // Salary Compensation Checking End

                        //read&apply checking start
                        if (readApplyData.isBlank()) {

                            jobsVH.tvReadBefApply.visibility = View.GONE
                            jobsVH.tvReadBefApplyData.visibility = View.GONE

                        } else {

                            jobsVH.tvReadBefApplyData.text = Html.fromHtml(readApplyData)
                            jobsVH.tvReadBefApply.visibility = View.VISIBLE
                            jobsVH.tvReadBefApplyData.visibility = View.VISIBLE

                        }


                        //companyLogo checking start

                        if (companyLogoUrl.isBlank()) {

                              jobsVH.companyLogo.visibility = View.GONE


                        } else {
                            jobsVH.companyLogo.visibility = View.VISIBLE
                            Picasso.get().load(companyLogoUrl).into(jobsVH.companyLogo)

                        }

                        jobsVH.tvJobSource.text = jobSourceData
                        jobsVH.tvCompanyAddress.text = companyAddress
                        jobsVH.tvCompanyName.text = companyName
                        jobsVH.viewAllJobsTV.text = "View all jobs of this Company ($companyOtherJobs)"


                    }

                })


            }

            LOADING -> {

                /*   Log.d("Check", " LOADING " + LOADING)*/
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


    override fun getItemCount(): Int {
        return if (jobList == null) 0 else jobList!!.size
    }

    override fun getItemViewType(position: Int): Int {


        /*   Log.d("Hello","Position: $position")*/

        if (showAD && (position % 3 == 0)) {
            /*   Log.d("Hello","Position: AD= $position")*/
            if (position == jobList!!.size - 1 && isLoadingAdded) {

                return LOADING

            } else if (jobList!![position].standout.equals("1")) {

                return BASIC

            } else if (jobList!![position].standout.equals("0")) {

                return BASIC
            }

        } else {
            if (position == jobList!!.size - 1 && isLoadingAdded) {

                return LOADING

            } else if (jobList!![position].standout.equals("1")) {

                return BASIC

            } else if (jobList!![position].standout.equals("0")) {

                return BASIC
            }
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

        Log.d("riuhghugr","getItemViewType" + getItemViewType(position))

        Log.d("riuhghugr"," result: $result")
        if (result?.jobid.isNullOrBlank() ) {
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

        val shimmer_view_container: ShimmerFrameLayout = viewItem?.findViewById(R.id.shimmer_view_container) as ShimmerFrameLayout


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
        val tvReadBefApplyData: TextView = viewItem?.findViewById(R.id.readAndApplyTV) as TextView
        val tvReadBefApply: TextView = viewItem?.findViewById(R.id.readAndApply) as TextView
        val tvCompanyName: TextView = viewItem?.findViewById(R.id.companyAddressNameTV) as TextView
        val tvCompanyAddress: TextView = viewItem?.findViewById(R.id.companyAddressTV) as TextView
        val keyPonits: TextView = viewItem?.findViewById(R.id.keyPoints) as TextView
        val companyLogo: ImageView = viewItem?.findViewById(R.id.company_icon) as ImageView
        val allJobsButtonLayout: ConstraintLayout = viewItem?.findViewById(R.id.buttonLayout) as ConstraintLayout
        val followTV: TextView = viewItem?.findViewById(R.id.followTV) as TextView
        val viewAllJobsTV: TextView = viewItem?.findViewById(R.id.viewAllJobs) as TextView
        val applyButton: Button = viewItem?.findViewById(R.id.applyButton) as Button
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


    fun shareJobs(position: Int){

        Log.d("ShareJob","position $position")

        var shareBody = ""
        if (jobList!!.get(position).lantype.equals("2")){
            shareBody = "${Constants.JOB_SHARE_URL}${jobList!!.get(position).jobid}&ln=${jobList!!.get(position).lantype}"
        } else {
            shareBody = "${Constants.JOB_SHARE_URL}${jobList!!.get(position).jobid}"
        }



        val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "${jobList!!.get(position).jobTitle}")
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
        context.startActivity(Intent.createChooser(sharingIntent, "Share"))

    }

}