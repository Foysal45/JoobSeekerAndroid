package com.bdjobs.app.Jobs


import android.app.Activity
import android.content.Context
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.JobListModelData
import com.bdjobs.app.API.ModelClasses.ShortlistJobModel
import com.bdjobs.app.BackgroundJob.ShortListedJobDeleteJob
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.ShortListedJobs
import com.bdjobs.app.LoggedInUserLanding.HomeCommunicator
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class JoblistAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
    private var homeCommunicator: HomeCommunicator? = null
    private var jobList: MutableList<JobListModelData>? = null
    private var isLoadingAdded = false
    private var retryPageLoad = false
    private var errorMsg: String? = null
    private val bdjobsDB = BdjobsDB.getInstance(context)
    private val bdjobsUserSession = BdjobsUserSession(context)


    init {
        this.jobList = java.util.ArrayList()

        if ((context as Activity) is JobBaseActivity) {
            jobCommunicator = context as JobCommunicator
        }

        if (context is MainLandingActivity) {
            homeCommunicator = context
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            BASIC -> {
                val viewItem = inflater.inflate(R.layout.joblist_item_new_layout, parent, false)
                viewHolder = JobsListVH(viewItem)

            }
            LOADING -> {
                val viewLoading = inflater.inflate(R.layout.item_progress, parent, false)
                viewHolder = LoadingVH(viewLoading)
            }

            STANDOUT -> {
                val viewLoading = inflater.inflate(R.layout.standoutjoblist, parent, false)
                viewHolder = StandOutListVH(viewLoading)
            }
    //------------------------------------------------------------------------------------------------------------------------------//
            STANDOUT_AD -> {
                val viewLoading = inflater.inflate(R.layout.standout_add_raw_item_layout, parent, false)
                viewHolder = StandOutAdJobListVH(viewLoading)
            }

            BASIC_AD -> {
                val viewLoading = inflater.inflate(R.layout.normal_add_raw_item_layout, parent, false)
                viewHolder = BasicAdobListVH(viewLoading)
            }
        }

        return viewHolder!!
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val result = this.jobList?.get(position) // jobs

        when (getItemViewType(position)) {
            BASIC -> {

                Log.d("ouiouii", " BASIC ${result?.jobTitle}")

                val jobsVH = holder as JobsListVH

                jobsVH.tvPosName.text = result?.jobTitle
                jobsVH.tvComName.text = result?.companyName
                jobsVH.tvDeadline.text = result?.deadline
                jobsVH.tvEducation.text = result?.eduRec
                jobsVH.tvExperience.text = result?.experience

                doAsync {
                    val shortListed = bdjobsDB.shortListedJobDao().isItShortListed(result?.jobid)
                    val appliedJobs = bdjobsDB.appliedJobDao().getAppliedJobsById(result?.jobid)
                    uiThread {
                        if (homeCommunicator == null) {
                            if (shortListed) {
                                jobsVH.shortListIconIV.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled))
                            } else {
                                jobsVH.shortListIconIV.setImageDrawable(context.getDrawable(R.drawable.ic_star))
                            }
                        } else {
                            jobsVH.shortListIconIV.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled))
                        }

                        if (appliedJobs.isEmpty()) {
                            jobsVH.appliedBadge.hide()
                        } else {
                            jobsVH.appliedBadge.show()
                        }
                    }
                }

                jobsVH.shortListIconIV.setOnClickListener {
                    shorlistAndUnshortlistJob(position)
                }

                jobsVH.linearLayout.setOnClickListener {
                    try {
                        jobCommunicator?.onItemClicked(position)
                        val jobids = ArrayList<String>()
                        val lns = ArrayList<String>()
                        val deadline = ArrayList<String>()
                        jobids.add(jobList?.get(position)?.jobid!!)
                        lns.add(jobList?.get(position)?.lantype!!)
                        deadline.add(jobList?.get(position)?.deadlineDB!!)
                        homeCommunicator?.shortListedClicked(jobids = jobids, lns = lns, deadline = deadline)
                    } catch (e: Exception) {
                        logException(e)
                    }
                }
            }

            LOADING -> {

                /*   Log.d("Check", " LOADING " + LOADING)*/
                val loadingVH = holder as LoadingVH

                if (retryPageLoad) {
                    loadingVH.mErrorLayout?.visibility = View.VISIBLE
                    /*loadingVH.mProgressBar!!.visibility = View.GONE*/

                    loadingVH.mErrorTxt?.text = if (errorMsg != null)
                        errorMsg
                    else
                        context.getString(R.string.app_name)

                } else {
                    loadingVH.mErrorLayout?.visibility = View.GONE
                    /*loadingVH.mProgressBar.visibility = View.VISIBLE*/
                }
            }

            STANDOUT -> {

                Log.d("ouiouii", " STANDOUT ${result?.jobTitle}")

                val jobsVH = holder as StandOutListVH

                jobsVH.tvPosName.text = result?.jobTitle
                jobsVH.tvComName.text = result?.companyName
                jobsVH.tvDeadline.text = result?.deadline
                jobsVH.tvEducation.text = result?.eduRec
                jobsVH.tvExperience.text = result?.experience


                jobsVH.linearLayout.setOnClickListener {
                    jobCommunicator?.onItemClicked(position)
                    val jobids = ArrayList<String>()
                    val lns = ArrayList<String>()
                    val deadline = ArrayList<String>()
                    jobids.add(jobList?.get(position)?.jobid!!)
                    lns.add(jobList?.get(position)?.lantype!!)
                    deadline.add(jobList?.get(position)?.deadlineDB!!)
                    homeCommunicator?.shortListedClicked(jobids = jobids, lns = lns, deadline = deadline)
                }

                jobsVH.shortListIconIV.setOnClickListener {

                    Toast.makeText(context, "Shortlist Standout Clicked ", Toast.LENGTH_LONG).show()


                }
                if (result?.logo != null) {
                    jobsVH.logoImageView.visibility = View.VISIBLE
                    Picasso.get()?.load(result.logo)?.into(jobsVH.logoImageView)

                }

                doAsync {
                    val shortListed = bdjobsDB.shortListedJobDao().isItShortListed(result?.jobid)
                    val appliedJobs = bdjobsDB.appliedJobDao().getAppliedJobsById(result?.jobid)
                    uiThread {

                        if (homeCommunicator == null) {
                            if (shortListed) {
                                jobsVH.shortListIconIV.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled))
                            } else {
                                jobsVH.shortListIconIV.setImageDrawable(context.getDrawable(R.drawable.ic_star))
                            }
                        } else {
                            jobsVH.shortListIconIV.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled))
                        }

                        if (appliedJobs.isEmpty()) {
                            jobsVH.appliedBadge.hide()
                        } else {
                            jobsVH.appliedBadge.show()
                        }
                    }
                }

                jobsVH.shortListIconIV.setOnClickListener {
                    shorlistAndUnshortlistJob(position)
                }

            }

            STANDOUT_AD -> {
                Log.d("ouiouii", " STANDOUT_AD ${result?.jobTitle}")

                val jobsVH = holder as StandOutAdJobListVH

                Constants.showNativeAd(jobsVH.ad_small_template,context)

                jobsVH.tvPosName.text = result?.jobTitle
                jobsVH.tvComName.text = result?.companyName
                jobsVH.tvDeadline.text = result?.deadline
                jobsVH.tvEducation.text = result?.eduRec
                jobsVH.tvExperience.text = result?.experience


                jobsVH.linearLayout.setOnClickListener {
                    jobCommunicator?.onItemClicked(position)
                    val jobids = ArrayList<String>()
                    val lns = ArrayList<String>()
                    val deadline = ArrayList<String>()
                    jobids.add(jobList?.get(position)?.jobid!!)
                    lns.add(jobList?.get(position)?.lantype!!)
                    deadline.add(jobList?.get(position)?.deadlineDB!!)
                    homeCommunicator?.shortListedClicked(jobids = jobids, lns = lns, deadline = deadline)
                }

                jobsVH.shortListIconIV.setOnClickListener {

                    Toast.makeText(context, "Shortlist Standout Clicked ", Toast.LENGTH_LONG).show()


                }
                if (result?.logo != null) {
                    jobsVH.logoImageView.visibility = View.VISIBLE
                    Picasso.get()?.load(result.logo)?.into(jobsVH.logoImageView)

                }

                doAsync {
                    val shortListed = bdjobsDB.shortListedJobDao().isItShortListed(result?.jobid)
                    val appliedJobs = bdjobsDB.appliedJobDao().getAppliedJobsById(result?.jobid)
                    uiThread {

                        if (homeCommunicator == null) {
                            if (shortListed) {
                                jobsVH.shortListIconIV.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled))
                            } else {
                                jobsVH.shortListIconIV.setImageDrawable(context.getDrawable(R.drawable.ic_star))
                            }
                        } else {
                            jobsVH.shortListIconIV.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled))
                        }

                        if (appliedJobs.isEmpty()) {
                            jobsVH.appliedBadge.hide()
                        } else {
                            jobsVH.appliedBadge.show()
                        }
                    }
                }

                jobsVH.shortListIconIV.setOnClickListener {
                    shorlistAndUnshortlistJob(position)
                }

            }

            BASIC_AD -> {
                Log.d("ouiouii", " BASIC_AD ${result?.jobTitle}")

                val jobsVH = holder as BasicAdobListVH

                Constants.showNativeAd(jobsVH.ad_small_template,context)

                jobsVH.tvPosName.text = result?.jobTitle
                jobsVH.tvComName.text = result?.companyName
                jobsVH.tvDeadline.text = result?.deadline
                jobsVH.tvEducation.text = result?.eduRec
                jobsVH.tvExperience.text = result?.experience

                doAsync {
                    val shortListed = bdjobsDB.shortListedJobDao().isItShortListed(result?.jobid)
                    val appliedJobs = bdjobsDB.appliedJobDao().getAppliedJobsById(result?.jobid)
                    uiThread {
                        if (homeCommunicator == null) {
                            if (shortListed) {
                                jobsVH.shortListIconIV.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled))
                            } else {
                                jobsVH.shortListIconIV.setImageDrawable(context.getDrawable(R.drawable.ic_star))
                            }
                        } else {
                            jobsVH.shortListIconIV.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled))
                        }

                        if (appliedJobs.isEmpty()) {
                            jobsVH.appliedBadge.hide()
                        } else {
                            jobsVH.appliedBadge.show()
                        }
                    }
                }

                jobsVH.shortListIconIV.setOnClickListener {
                    shorlistAndUnshortlistJob(position)
                }

                jobsVH.linearLayout.setOnClickListener {
                    try {
                        jobCommunicator?.onItemClicked(position)
                        val jobids = ArrayList<String>()
                        val lns = ArrayList<String>()
                        val deadline = ArrayList<String>()
                        jobids.add(jobList?.get(position)?.jobid!!)
                        lns.add(jobList?.get(position)?.lantype!!)
                        deadline.add(jobList?.get(position)?.deadlineDB!!)
                        homeCommunicator?.shortListedClicked(jobids = jobids, lns = lns, deadline = deadline)
                    } catch (e: Exception) {
                        logException(e)
                    }
                }
            }
        }
    }

    private fun deleteShortListedJobwithUndo(position: Int) {
        Log.d("czcx", "position: $position")

        try {
            if (jobList?.size != 0) {
                val deletedItem = jobList?.get(position)
                jobList?.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeRemoved(position, jobList?.size!!)
                val actv = context as Activity
                val deleteJobID = ShortListedJobDeleteJob.scheduleAdvancedJob(deletedItem?.jobid!!)
                //undoRemove(actv.mainCL, deletedItem, position, deleteJobID)
                homeCommunicator?.decrementCounter()
            } else {
                context.toast("No items left here!")
            }
        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun undoRemove(v: View, deletedItem: JobListModelData?, deletedIndex: Int, deleteJobID: Int) {
        val msg = Html.fromHtml("<font color=\"#ffffff\">The information has been deleted successfully</font>")
        val snack = Snackbar.make(v, "$msg", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    ShortListedJobDeleteJob.cancelJob(deleteJobID)
                    restoreMe(deletedItem!!, deletedIndex)
                    homeCommunicator?.scrollToUndoPosition(deletedIndex)
                    Log.d("comid", "comid = ${deletedIndex}")
                }

        snack.show()
        Log.d("swipe", "dir to LEFT")
    }

    private fun restoreMe(item: JobListModelData, pos: Int) {
        jobList?.add(pos, item)
        notifyItemInserted(pos)
    }

    private fun shorlistAndUnshortlistJob(position: Int) {
        val bdjobsUserSession = BdjobsUserSession(context)
        if (!bdjobsUserSession.isLoggedIn!!) {
            jobCommunicator?.goToLoginPage()
            jobCommunicator?.setBackFrom("jobdetail")
        } else {
            doAsync {
                val shortListed = bdjobsDB.shortListedJobDao().isItShortListed(jobList?.get(position)?.jobid)
                uiThread {
                    if (shortListed || homeCommunicator != null) {
                        context?.alert("Are you sure you want to remove this job from shortlisted jobs?", "Confirmation") {
                            yesButton {
                                if (homeCommunicator != null) {
                                    deleteShortListedJobwithUndo(position)
                                } else {
                                    ShortListedJobDeleteJob.runJobImmediately(jobList?.get(position)?.jobid!!)
                                    doAsync {
                                        bdjobsDB.shortListedJobDao().deleteShortListedJobsByJobID(jobList?.get(position)?.jobid!!)
                                    }
                                    uiThread { notifyDataSetChanged() }
                                }
                            }
                            noButton { dialog ->
                                dialog.dismiss()
                            }
                        }.show()
                    } else if (homeCommunicator == null) {
                        try {
                            val date = Date()
                            val formatter = SimpleDateFormat("MM/dd/yyyy")
                            val today: String = formatter.format(date)
                            val todayDate = SimpleDateFormat("MM/dd/yyyy").parse(today)

                            val deadline = jobList?.get(position)?.deadlineDB
                            val deadlineDate = SimpleDateFormat("MM/dd/yyyy").parse(deadline)

                            Log.d("fphwrpeqspm", "todayDate: $todayDate deadlineDate:$deadlineDate")

                            if (todayDate > deadlineDate) {
                                context.toast("This job's deadline has been expired. You can not shortlist this job")
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
                                    Log.d("DeadLine", "DeadLineParsed: $deadline \n DeadLine: ${jobList?.get(position)?.deadlineDB}")
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
                                    uiThread { notifyDataSetChanged() }
                                }
                            }
                        } catch (e: Exception) {
                            logException(e)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (this.jobList == null) 0 else this.jobList!!.size
    }

    override fun getItemViewType(position: Int): Int {

        if (showAD && (position % 3 == 0) && position!=0) {
            if (position == this.jobList!!.size - 1 && isLoadingAdded) {

                return LOADING

            } else if (this.jobList?.get(position)?.standout?.equalIgnoreCase("1")!!) {

                return STANDOUT_AD

            } else if (this.jobList?.get(position)?.standout?.equalIgnoreCase("0")!!) {

                return BASIC_AD
            }
        } else {
            if (position == this.jobList!!.size - 1 && isLoadingAdded) {

                return LOADING

            } else if (this.jobList?.get(position)?.standout?.equalIgnoreCase("1")!!) {

                return STANDOUT

            } else if (this.jobList?.get(position)?.standout?.equalIgnoreCase("0")!!) {

                    return BASIC
            }
        }
        return LOADING

    }


    fun add(item: JobListModelData) {
        this.jobList?.add(item)
        notifyItemInserted(this.jobList!!.size - 1)

    }


    fun addAllTest(moveResults: List<JobListModelData>) {
        for (result in moveResults) {
            add(result)
        }
        jobCommunicator?.setJobList(this.jobList)
    }

    private fun remove(item: JobListModelData?) {
        val position = this.jobList!!.indexOf(item)
        if (position > -1) {
            this.jobList!!.removeAt(position)
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
        return this.jobList!![position]
    }


    /**
     * Main list's content ViewHolder
     */
    private class StandOutAdJobListVH(viewItem: View?) : RecyclerView.ViewHolder(viewItem!!) {
        val appliedBadge: TextView = viewItem?.findViewById(R.id.appliedBadge) as TextView
        val tvPosName: TextView = viewItem?.findViewById(R.id.textViewPositionNameStandOut) as TextView
        val tvComName: TextView = viewItem?.findViewById(R.id.textViewCompanyNameStandOut) as TextView
        val tvDeadline: TextView = viewItem?.findViewById(R.id.textViewDeadlineDateStandOut) as TextView
        val tvExperience: TextView = viewItem?.findViewById(R.id.textViewExperienceYearStandOut) as TextView
        val tvEducation: TextView = viewItem?.findViewById(R.id.textViewEducationNameStandOut) as TextView
        val logoImageView: ImageView = viewItem?.findViewById(R.id.imageViewCompanyLogoStandOut) as ImageView
        var shortListIconIV: ImageView = viewItem?.findViewById(R.id.shortlist_icon) as ImageView
        var linearLayout: LinearLayout = viewItem?.findViewById(R.id.linearLayout) as LinearLayout
        val ad_small_template: TemplateView = viewItem?.findViewById(R.id.ad_small_template) as TemplateView
    }

    private class BasicAdobListVH(viewItem: View?) : RecyclerView.ViewHolder(viewItem!!) {
        val appliedBadge: TextView = viewItem?.findViewById(R.id.appliedBadge) as TextView
        val tvPosName: TextView = viewItem?.findViewById(R.id.textViewPositionName) as TextView
        val tvComName: TextView = viewItem?.findViewById(R.id.textViewCompanyName) as TextView
        val tvDeadline: TextView = viewItem?.findViewById(R.id.textViewDeadlineDate) as TextView
        val tvExperience: TextView = viewItem?.findViewById(R.id.textViewExperienceYear) as TextView
        val tvEducation: TextView = viewItem?.findViewById(R.id.textViewEducationName) as TextView
        var shortListIconIV: ImageView = viewItem?.findViewById(R.id.shortlist_icon) as ImageView
        var linearLayout: LinearLayout = viewItem?.findViewById(R.id.linearLayout) as LinearLayout
        val ad_small_template: TemplateView = viewItem?.findViewById(R.id.ad_small_template) as TemplateView
    }

    private class JobsListVH(viewItem: View?) : RecyclerView.ViewHolder(viewItem!!) {
        val appliedBadge: TextView = viewItem?.findViewById(R.id.appliedBadge) as TextView
        val tvPosName: TextView = viewItem?.findViewById(R.id.textViewPositionName) as TextView
        val tvComName: TextView = viewItem?.findViewById(R.id.textViewCompanyName) as TextView
        val tvDeadline: TextView = viewItem?.findViewById(R.id.textViewDeadlineDate) as TextView
        val tvExperience: TextView = viewItem?.findViewById(R.id.textViewExperienceYear) as TextView
        val tvEducation: TextView = viewItem?.findViewById(R.id.textViewEducationName) as TextView
        var shortListIconIV: ImageView = viewItem?.findViewById(R.id.shortlist_icon) as ImageView
        var linearLayout: LinearLayout = viewItem?.findViewById(R.id.linearLayout) as LinearLayout
    }

    private class StandOutListVH(viewItem: View?) : RecyclerView.ViewHolder(viewItem!!) {
        val appliedBadge: TextView = viewItem?.findViewById(R.id.appliedBadge) as TextView
        val tvPosName: TextView = viewItem?.findViewById(R.id.textViewPositionNameStandOut) as TextView
        val tvComName: TextView = viewItem?.findViewById(R.id.textViewCompanyNameStandOut) as TextView
        val tvDeadline: TextView = viewItem?.findViewById(R.id.textViewDeadlineDateStandOut) as TextView
        val tvExperience: TextView = viewItem?.findViewById(R.id.textViewExperienceYearStandOut) as TextView
        val tvEducation: TextView = viewItem?.findViewById(R.id.textViewEducationNameStandOut) as TextView
        val logoImageView: ImageView = viewItem?.findViewById(R.id.imageViewCompanyLogoStandOut) as ImageView
        var shortListIconIV: ImageView = viewItem?.findViewById(R.id.shortlist_icon) as ImageView
        var linearLayout: LinearLayout = viewItem?.findViewById(R.id.linearLayout) as LinearLayout

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


}