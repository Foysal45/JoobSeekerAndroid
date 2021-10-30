package com.bdjobs.app.Jobs


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.bdjobs.app.API.ApiServiceJobs
import com.bdjobs.app.API.ModelClasses.JobListModelData
import com.bdjobs.app.API.ModelClasses.ShortlistJobModel
import com.bdjobs.app.Ads.Ads
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.databases.internal.ShortListedJobs
import com.bdjobs.app.LoggedInUserLanding.HomeCommunicator
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Workmanager.ShortlistedJobDeleteWorker
import com.bdjobs.app.ajkerDeal.ui.home.page_home.HomeNewFragment
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@Suppress("SpellCheckingInspection")
class JobListAdapter(val context: Context, var onUpdateCounter: OnUpdateCounter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        // View Types
        private const val BASIC = 0
        private const val LOADING = 1
        private const val STANDOUT = 2
        private const val BASIC_AD = 3
        private const val STANDOUT_AD = 4
        private const val FEATURED = 5
        private const val FEATURED_AD = 6
        private const val AJKER_DEAL_LIVE = 7
        private var showAD = true
    }

    private var jobCommunicator: JobCommunicator? = null
    private var homeCommunicator: HomeCommunicator? = null
    private var jobList: MutableList<JobListModelData>? = null
    private var isLoadingAdded = false
    private var retryPageLoad = false
    private var errorMsg: String? = null
    private val bdJobsDB = BdjobsDB.getInstance(context)


    init {
        this.jobList = ArrayList()

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

            FEATURED -> {
                val viewLoading = inflater.inflate(R.layout.featured_joblist_item, parent, false)
                viewHolder = FeaturedListVH(viewLoading)
            }

            AJKER_DEAL_LIVE -> {
                val viewLive = inflater.inflate(R.layout.item_ajker_deal_live,parent,false)
                viewHolder = AjkerDealLiveVH(viewLive)
            }


            //------------------------------------------------------------------------------------------------------------------------------//

            FEATURED_AD -> {
                val viewLoading = inflater.inflate(R.layout.featured_joblist_item_ad, parent, false)
                viewHolder = FeaturedAdListVH(viewLoading)
            }

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
        if (position<itemCount-1) {
            val result =  this.jobList?.get(position) // jobs

            when (getItemViewType(position)) {
                BASIC -> {

                    val jobsVH = holder as JobsListVH

                    jobsVH.tvPosName.text = result?.jobTitle
                    jobsVH.tvComName.text = result?.companyName
                    jobsVH.tvDeadline.text = result?.deadline
                    jobsVH.tvEducation.text = result?.eduRec
                    jobsVH.tvExperience.text = result?.experience

                    doAsync {
                        val shortListed = bdJobsDB.shortListedJobDao().isItShortListed(result?.jobid)
                        val appliedJobs = bdJobsDB.appliedJobDao().getAppliedJobsById(result?.jobid)
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

                    /*   //Log.d("Check", " LOADING " + LOADING)*/
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

                    //Log.d("ouiouii", " STANDOUT ${result?.jobTitle}")

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
                        shorlistAndUnshortlistJob(position)

                    }
                    if (result?.logo != null) {
                        jobsVH.logoImageView.visibility = View.VISIBLE
                        Picasso.get()?.load(result.logo)?.into(jobsVH.logoImageView)

                    }

                    doAsync {
                        val shortListed = bdJobsDB.shortListedJobDao().isItShortListed(result?.jobid)
                        val appliedJobs = bdJobsDB.appliedJobDao().getAppliedJobsById(result?.jobid)
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


                }

                FEATURED -> {

                    val jobsVH = holder as FeaturedListVH
                    jobsVH.tvPosName.text = result?.jobTitle
                    jobsVH.tvComName.text = result?.companyName
                    jobsVH.tvDeadline.text = result?.deadline
                    jobsVH.tvEducation.text = result?.eduRec
                    jobsVH.tvExperience.text = result?.experience


                    jobsVH.cardView.setOnClickListener {
                        jobCommunicator?.onItemClicked(position)
                        val jobids = ArrayList<String>()
                        val lns = ArrayList<String>()
                        val deadline = ArrayList<String>()
                        jobids.add(jobList?.get(position)?.jobid!!)
                        lns.add(jobList?.get(position)?.lantype!!)
                        deadline.add(jobList?.get(position)?.deadlineDB!!)
                        homeCommunicator?.shortListedClicked(jobids = jobids, lns = lns, deadline = deadline)
                    }


                    jobsVH.ivDropArrow.setOnClickListener {
                        jobsVH.ivDropArrow.visibility = View.GONE
                        jobsVH.clHiddenLayout.visibility = View.VISIBLE
//                    ExpandAndCollapseViewUtil.expand(jobsVH.clHiddenLayout, 300)
                    }

                    jobsVH.shortListIconIV.setOnClickListener {
                        shorlistAndUnshortlistJob(position)

                    }
                    if (result?.logo != null) {
                        jobsVH.logoImageView.visibility = View.VISIBLE
                        Picasso.get()?.load(result.logo)?.into(jobsVH.logoImageView)

                    }

                    doAsync {
                        val shortListed = bdJobsDB.shortListedJobDao().isItShortListed(result?.jobid)
                        val appliedJobs = bdJobsDB.appliedJobDao().getAppliedJobsById(result?.jobid)
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
                                //jobsVH.appliedBadge.hide()
                            } else {
                                //jobsVH.appliedBadge.show()
                            }
                        }
                    }


                }

                FEATURED_AD -> {

                    val jobsVH = holder as FeaturedAdListVH
                    Ads.showNativeAd(jobsVH.adSmallTemplate, context)
                    jobsVH.tvPosName.text = result?.jobTitle
                    jobsVH.tvComName.text = result?.companyName
                    jobsVH.tvDeadline.text = result?.deadline
                    jobsVH.tvEducation.text = result?.eduRec
                    jobsVH.tvExperience.text = result?.experience


                    jobsVH.cardView.setOnClickListener {
                        jobCommunicator?.onItemClicked(position)
                        val jobids = ArrayList<String>()
                        val lns = ArrayList<String>()
                        val deadline = ArrayList<String>()
                        jobids.add(jobList?.get(position)?.jobid!!)
                        lns.add(jobList?.get(position)?.lantype!!)
                        deadline.add(jobList?.get(position)?.deadlineDB!!)
                        homeCommunicator?.shortListedClicked(jobids = jobids, lns = lns, deadline = deadline)
                    }


                    jobsVH.ivDropArrow.setOnClickListener {
                        jobsVH.ivDropArrow.visibility = View.GONE
                        jobsVH.clHiddenLayout.visibility = View.VISIBLE
//                    ExpandAndCollapseViewUtil.expand(jobsVH.clHiddenLayout, 300)
                    }

                    jobsVH.shortListIconIV.setOnClickListener {
                        shorlistAndUnshortlistJob(position)

                    }
                    if (result?.logo != null) {
                        jobsVH.logoImageView.visibility = View.VISIBLE
                        Picasso.get()?.load(result.logo)?.into(jobsVH.logoImageView)

                    }

                    doAsync {
                        val shortListed = bdJobsDB.shortListedJobDao().isItShortListed(result?.jobid)
                        val appliedJobs = bdJobsDB.appliedJobDao().getAppliedJobsById(result?.jobid)
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
                                //jobsVH.appliedBadge.hide()
                            } else {
                                //jobsVH.appliedBadge.show()
                            }
                        }
                    }


                }

                STANDOUT_AD -> {
                    //Log.d("ouiouii", " STANDOUT_AD ${result?.jobTitle}")

                    val jobsVH = holder as StandOutAdJobListVH

                    Ads.showNativeAd(jobsVH.adSmallTemplate, context)

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
                        shorlistAndUnshortlistJob(position)
                    }

                    if (result?.logo != null) {
                        jobsVH.logoImageView.visibility = View.VISIBLE
                        Picasso.get()?.load(result.logo)?.into(jobsVH.logoImageView)

                    }

                    doAsync {
                        val shortListed = bdJobsDB.shortListedJobDao().isItShortListed(result?.jobid)
                        val appliedJobs = bdJobsDB.appliedJobDao().getAppliedJobsById(result?.jobid)
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


                }

                BASIC_AD -> {
                    //Log.d("ouiouii", " BASIC_AD ${result?.jobTitle}")

                    val jobsVH = holder as BasicAdobListVH

                    Ads.showNativeAd(jobsVH.adSmallTemplate, context)

                    jobsVH.tvPosName.text = result?.jobTitle
                    jobsVH.tvComName.text = result?.companyName
                    jobsVH.tvDeadline.text = result?.deadline
                    jobsVH.tvEducation.text = result?.eduRec
                    jobsVH.tvExperience.text = result?.experience

                    doAsync {
                        val shortListed = bdJobsDB.shortListedJobDao().isItShortListed(result?.jobid)
                        val appliedJobs = bdJobsDB.appliedJobDao().getAppliedJobsById(result?.jobid)
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

    }

    private fun deleteShortListedJobwithUndo(position: Int) {
        //Log.d("czcx", "position: $position")

        try {
            if (jobList?.size != 0) {
                val deletedItem = jobList?.get(position)
                jobList?.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeRemoved(position, jobList?.size!!)
                val actv = context as Activity

                val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()

                val shortlistedJobDeleteData = workDataOf("jobId" to deletedItem?.jobid)
                val shortlistedJobDeleteRequest = OneTimeWorkRequestBuilder<ShortlistedJobDeleteWorker>().setInputData(shortlistedJobDeleteData).setConstraints(constraints).build()
                WorkManager.getInstance(context).enqueue(shortlistedJobDeleteRequest)

//                val deleteJobID = ShortListedJobDeleteJob.scheduleAdvancedJob(deletedItem?.jobid!!)
                //undoRemove(actv.mainCL, deletedItem, position, deleteJobID)
//                homeCommunicator?.decrementCounter()
                homeCommunicator?.getTotalShortlistedJobCounter()?.minus(1)?.let { onUpdateCounter.update(it) }
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
//                    ShortListedJobDeleteJob.cancelJob(deleteJobID)
                    restoreMe(deletedItem!!, deletedIndex)
                    homeCommunicator?.scrollToUndoPosition(deletedIndex)
                    //Log.d("comid", "comid = ${deletedIndex}")
                }

        snack.show()
        //Log.d("swipe", "dir to LEFT")
    }

    private fun restoreMe(item: JobListModelData, pos: Int) {
        jobList?.add(pos, item)
        notifyItemInserted(pos)
    }

    @SuppressLint("NotifyDataSetChanged", "SimpleDateFormat")
    private fun shorlistAndUnshortlistJob(position: Int) {
        val bdjobsUserSession = BdjobsUserSession(context)
        if (!bdjobsUserSession.isLoggedIn!!) {
            jobCommunicator?.goToLoginPage()
            jobCommunicator?.setBackFrom("jobdetail")
        } else {
            doAsync {
                val shortListed = bdJobsDB.shortListedJobDao().isItShortListed(jobList?.get(position)?.jobid)
                uiThread {
                    if (shortListed || homeCommunicator != null) {
                        context.alert("Are you sure you want to remove this job from shortlisted jobs?", "Confirmation") {
                            yesButton {
                                if (homeCommunicator != null) {
                                    deleteShortListedJobwithUndo(position)
                                } else {
                                    val constraints = Constraints.Builder()
                                        .setRequiredNetworkType(NetworkType.CONNECTED)
                                        .build()

                                    val shortlistedJobDeleteData = workDataOf("jobId" to jobList?.get(position)?.jobid)
                                    val shortlistedJobDeleteRequest = OneTimeWorkRequestBuilder<ShortlistedJobDeleteWorker>().setInputData(shortlistedJobDeleteData).setConstraints(constraints).build()
                                    WorkManager.getInstance(context).enqueue(shortlistedJobDeleteRequest)
                    //                                    ShortListedJobDeleteJob.runJobImmediately(jobList?.get(position)?.jobid!!)
                                    doAsync {
                                        bdJobsDB.shortListedJobDao().deleteShortListedJobsByJobID(jobList?.get(position)?.jobid!!)
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
                            val deadlineDate = SimpleDateFormat("MM/dd/yyyy").parse(deadline!!)

                            //Log.d("fphwrpeqspm", "todayDate: $todayDate deadlineDate:$deadlineDate")

                            if (todayDate!! > deadlineDate) {
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
                                        deadline = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(jobList?.get(position)?.deadlineDB!!)
                                    } catch (e: Exception) {
                                        logException(e)
                                    }

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
        return if (jobList!=null && jobList!!.size>0) jobList!!.size+1 else 0
    }

    override fun getItemViewType(position: Int): Int {

        val totalJobCount = if (homeCommunicator==null) jobCommunicator?.getTotalJobCount() else homeCommunicator?.getTotalShortlistedJobCounter()

        Timber.d("Position: $position .. ItemCount: $itemCount Total: $totalJobCount")

        if ( position<itemCount-1) {
            if (showAD && (position % 3 == 0) && position != 0 && position < 21) {
                return when {
                    this.jobList?.get(position)?.standout?.equalIgnoreCase("2")!! -> {

                        FEATURED_AD

                    }
                    this.jobList?.get(position)?.standout?.equalIgnoreCase("1")!! -> {

                        STANDOUT_AD

                    }
                    this.jobList?.get(position)?.standout?.equalIgnoreCase("0")!! -> {

                        BASIC_AD
                    }
                    else -> {
                        BASIC_AD
                    }
                }
            }
            else {
                return when {
                    this.jobList?.get(position)?.standout?.equalIgnoreCase("2")!! -> {

                        FEATURED

                    }
                    this.jobList?.get(position)?.standout?.equalIgnoreCase("1")!! -> {

                        STANDOUT

                    }
                    this.jobList?.get(position)?.standout?.equalIgnoreCase("0")!! -> {

                        BASIC
                    }
                    else -> {
                        BASIC
                    }
                }
            }
        } else if (totalJobCount!! <= position) {
            return AJKER_DEAL_LIVE
        } else {
            return LOADING
        }
    }


    fun add(item: JobListModelData) {
        this.jobList?.add(item)
        notifyItemInserted(this.jobList!!.size - 1)

    }

    fun addAll(moveResults: List<JobListModelData>) {
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

        if (result?.jobid.isNullOrBlank()) {
            this.jobList!!.removeAt(position)
            notifyItemRemoved(position)
        }

    }

    private fun getItem(position: Int): JobListModelData? {
        return this.jobList!![position]
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        if (holder is AjkerDealLiveVH) {
            if (BdjobsUserSession(context).adTypeJobList=="2") {
                holder.container.visibility = View.VISIBLE
                holder.fragment(HomeNewFragment())
            } else {
                holder.container.visibility = View.GONE
            }
        }
        super.onViewAttachedToWindow(holder)


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
        val adSmallTemplate: TemplateView = viewItem?.findViewById(R.id.ad_small_template) as TemplateView
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
        val adSmallTemplate: TemplateView = viewItem?.findViewById(R.id.ad_small_template) as TemplateView
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

    private class FeaturedListVH(viewItem: View?) : RecyclerView.ViewHolder(viewItem!!) {
        val tvPosName: TextView = viewItem?.findViewById(R.id.textViewPositionNameFeatured) as TextView
        val tvComName: TextView = viewItem?.findViewById(R.id.textViewCompanyNameStandOut) as TextView
        val tvDeadline: TextView = viewItem?.findViewById(R.id.textViewDeadlineDateStandOut) as TextView
        val tvExperience: TextView = viewItem?.findViewById(R.id.textViewExperienceYearStandOut) as TextView
        val tvEducation: TextView = viewItem?.findViewById(R.id.textViewEducationNameStandOut) as TextView
        val logoImageView: ImageView = viewItem?.findViewById(R.id.imageViewCompanyLogoStandOut) as ImageView
        var shortListIconIV: ImageView = viewItem?.findViewById(R.id.shortlist_icon) as ImageView
        var ivDropArrow: ImageView = viewItem?.findViewById(R.id.img_drop_arrow) as ImageView
        var clHiddenLayout: ConstraintLayout = viewItem?.findViewById(R.id.hidden_cl) as ConstraintLayout
//        var linearLayout: LinearLayout = viewItem?.findViewById(R.id.linearLayout) as LinearLayout
        var cardView : MaterialCardView = viewItem?.findViewById(R.id.cardViewFeatured) as MaterialCardView

    }

    private class FeaturedAdListVH(viewItem: View?) : RecyclerView.ViewHolder(viewItem!!) {
        val tvPosName: TextView = viewItem?.findViewById(R.id.textViewPositionNameFeatured) as TextView
        val tvComName: TextView = viewItem?.findViewById(R.id.textViewCompanyNameStandOut) as TextView
        val tvDeadline: TextView = viewItem?.findViewById(R.id.textViewDeadlineDateStandOut) as TextView
        val tvExperience: TextView = viewItem?.findViewById(R.id.textViewExperienceYearStandOut) as TextView
        val tvEducation: TextView = viewItem?.findViewById(R.id.textViewEducationNameStandOut) as TextView
        val logoImageView: ImageView = viewItem?.findViewById(R.id.imageViewCompanyLogoStandOut) as ImageView
        var shortListIconIV: ImageView = viewItem?.findViewById(R.id.shortlist_icon) as ImageView
        var ivDropArrow: ImageView = viewItem?.findViewById(R.id.img_drop_arrow) as ImageView
        var clHiddenLayout: ConstraintLayout = viewItem?.findViewById(R.id.hidden_cl) as ConstraintLayout
//        var linearLayout: LinearLayout = viewItem?.findViewById(R.id.linearLayout) as LinearLayout
        val adSmallTemplate: TemplateView = viewItem?.findViewById(R.id.ad_small_template) as TemplateView
        var cardView : MaterialCardView = viewItem?.findViewById(R.id.cardViewFeatured) as MaterialCardView


    }

    private class LoadingVH(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var mProgressBar: ProgressBar? = itemView.findViewById(R.id.loadmore_progress) as ProgressBar?
        private var mRetryBtn: ImageButton? = itemView.findViewById(R.id.loadmore_retry) as ImageButton?
        var mErrorTxt: TextView? = itemView.findViewById(R.id.loadmore_errortxt) as TextView?
        var mErrorLayout: LinearLayout? = itemView.findViewById(R.id.loadmore_errorlayout) as LinearLayout?
        override fun onClick(view: View) {
            when (view.id) {
                R.id.loadmore_retry, R.id.loadmore_errorlayout -> {
                    /* adapter?.showRetry(false, null)
                     mCallback?.retryPageLoad()*/
                }
            }
        }

    }

    inner class AjkerDealLiveVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var container: FrameLayout = itemView.findViewById(R.id.live_container)

        fun fragment(fragment: Fragment) {

            (context as AppCompatActivity)
                .supportFragmentManager
                .beginTransaction()
                .replace(container.id, fragment)
                .commitNowAllowingStateLoss()
        }
    }

    interface OnUpdateCounter {
        fun update(count : Int)
    }
}