package com.bdjobs.app.Employers

import android.app.Activity
import android.content.Context
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.bdjobs.app.API.ModelClasses.FollowEmployerListData
import com.bdjobs.app.Ads.Ads
//import com.bdjobs.app.BackgroundJob.FollowUnfollowJob
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Workmanager.FollowUnfollowWorker
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton


class FollowedEmployersAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val activity = context as Activity
    val dataStorage = DataStorage(context)
    val bdjobsUserSession = BdjobsUserSession(context)
    private var followedEmployerList: ArrayList<FollowEmployerListData>? = ArrayList()
    private lateinit var company_ID: String
    private lateinit var company_name: String
    private var undoButtonPressed: Boolean = false
    private val employersCommunicator = activity as EmployersCommunicator

    private var isLoadingAdded = false
    private var retryPageLoad = false
    private var errorMsg: String? = null

    companion object {
        private val ITEM = 0
        private val LOADING = 1
        private val ITEM_WITH_AD = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            ITEM -> {
                val viewItem = inflater.inflate(R.layout.followed_employers, parent, false)
                viewHolder = FollowedEmployerViewHolder(viewItem)
            }
            LOADING -> {
                val viewLoading = inflater.inflate(R.layout.item_progress_1, parent, false)
                viewHolder = FollowedEmployerLoadingViewHolder(viewLoading)
            }
            ITEM_WITH_AD -> {
                val viewItem = inflater.inflate(R.layout.followed_employers_ad, parent, false)
                viewHolder = FollowedEmployerViewHolderWithAd(viewItem)
            }

        }
        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return if (followedEmployerList == null) 0 else followedEmployerList!!.size

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            ITEM -> {
                val itemHolder = holder as FollowedEmployerViewHolder
                bindViews(itemHolder, position)
            }
            LOADING -> {
                val itemHolder = holder as FollowedEmployerLoadingViewHolder
                if (retryPageLoad) {
                    itemHolder?.mErrorLayout?.visibility = View.VISIBLE
                    itemHolder?.mProgressBar?.visibility = View.GONE
                    itemHolder?.mErrorTxt?.text = if (errorMsg != null)
                        errorMsg
                    else
                        context?.getString(R.string.error_msg_unknown)

                } else {
                    itemHolder?.mErrorLayout?.visibility = View.GONE
                    itemHolder?.mProgressBar?.visibility = View.VISIBLE
                }
            }
            ITEM_WITH_AD -> {
                val itemHolder = holder as FollowedEmployerViewHolderWithAd
                Ads.showNativeAd(holder.ad_small_template, context)
                bindViews(itemHolder, position)
            }
        }

    }

    private fun bindViews(viewHolder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            ITEM -> {
                val holder = viewHolder as FollowedEmployerViewHolder
                try {
                    holder?.employerCompany.text = followedEmployerList?.get(position)?.companyName
                    holder?.offeringJobs.text = followedEmployerList?.get(position)?.jobCount
                    company_name = followedEmployerList?.get(position)?.companyName!!
                    company_ID = followedEmployerList?.get(position)?.companyID!!
                    holder.followUunfollow?.setOnClickListener {

                        try {
                            activity?.alert("Are you sure you want to unfollow this company?", "Confirmation") {
                                yesButton {
                                    undoButtonPressed = false
                                    removeItem(holder.adapterPosition)

                                }
                                noButton { dialog ->
                                    dialog.dismiss()
                                }
                            }.show()

                        } catch (e: Exception) {
                            logException(e)
                        }
                    }

                    var jobCount = followedEmployerList?.get(position)?.jobCount
                    if (jobCount == null) {
                        jobCount = "0"
                    }
                    var jobCountint = jobCount?.toInt()
                    //      //Log.d("crash", "crash $jobCount")


                    holder?.itemView?.setOnClickListener {
                        if (jobCountint > 0) {
                            try {
                                if (position < followedEmployerList?.size!!) {
                                    //Log.d("flwd", "position = ${position} list = ${followedEmployerList!!.size}")
                                    var company_name_1 = followedEmployerList?.get(position)?.companyName!!
                                    var company_ID_1 = followedEmployerList?.get(position)?.companyID!!
                                    employersCommunicator?.gotoJobListFragment(company_ID_1, company_name_1)
                                    employersCommunicator?.positionClicked(position)
                                    //Log.d("companyid", company_ID_1)
                                    //Log.d("companyid", company_name_1)
                                }
                            } catch (e: Exception) {
                                logException(e)
                            }
                        }
                    }

                } catch (e: Exception) {
                    logException(e)
                }
            }

            ITEM_WITH_AD -> {
                val holder = viewHolder as FollowedEmployerViewHolderWithAd
                try {
                    holder?.employerCompany.text = followedEmployerList?.get(position)?.companyName
                    holder?.offeringJobs.text = followedEmployerList?.get(position)?.jobCount
                    company_name = followedEmployerList?.get(position)?.companyName!!
                    company_ID = followedEmployerList?.get(position)?.companyID!!
                    holder.followUunfollow?.setOnClickListener {

                        try {
                            activity?.alert("Are you sure you want to unfollow this company?", "Confirmation") {
                                yesButton {
                                    undoButtonPressed = false
                                    removeItem(holder.adapterPosition)

                                }
                                noButton { dialog ->
                                    dialog.dismiss()
                                }
                            }.show()

                        } catch (e: Exception) {
                            logException(e)
                        }
                    }

                    var jobCount = followedEmployerList?.get(position)?.jobCount
                    if (jobCount == null) {
                        jobCount = "0"
                    }
                    var jobCountint = jobCount?.toInt()
                    //      //Log.d("crash", "crash $jobCount")


                    holder?.itemView?.setOnClickListener {
                        if (jobCountint > 0) {
                            try {
                                if (position < followedEmployerList?.size!!) {
                                    //Log.d("flwd", "position = ${position} list = ${followedEmployerList!!.size}")
                                    var company_name_1 = followedEmployerList?.get(position)?.companyName!!
                                    var company_ID_1 = followedEmployerList?.get(position)?.companyID!!
                                    employersCommunicator?.gotoJobListFragment(company_ID_1, company_name_1)
                                    employersCommunicator?.positionClicked(position)
                                    //Log.d("companyid", company_ID_1)
                                    //Log.d("companyid", company_name_1)
                                }
                            } catch (e: Exception) {
                                logException(e)
                            }
                        }
                    }

                } catch (e: Exception) {
                    logException(e)
                }
            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 3 == 0 && position !=0)
            ITEM_WITH_AD
        else
            if (position == followedEmployerList!!.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    private fun getItem(position: Int): FollowEmployerListData? {
        return followedEmployerList!![position]
    }

    fun removeItem(position: Int) {

        try {
            if (followedEmployerList?.size != 0) {
                val deletedItem = followedEmployerList?.get(position)
                val companyid = followedEmployerList?.get(position)?.companyID
                val companyName = followedEmployerList?.get(position)?.companyName
                //Log.d("werywirye", "companyid = $companyid companyname = $companyName")
                followedEmployerList?.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeRemoved(position, followedEmployerList?.size!!)

                try {

                    val constraints = Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()

                    val followUnfollowData = workDataOf("companyid" to companyid, "companyname" to companyName)
                    val followUnfollowRequest = OneTimeWorkRequestBuilder<FollowUnfollowWorker>().setInputData(followUnfollowData).setConstraints(constraints).build()
                    WorkManager.getInstance(context).enqueue(followUnfollowRequest)

//                    val deleteJobID = FollowUnfollowJob.scheduleAdvancedJob(companyid!!, companyName!!)
                    bdjobsUserSession?.deccrementFollowedEmployer()
                    //undoRemove(view, deletedItem, position, deleteJobID)
                    employersCommunicator.decrementCounter(position)
                } catch (e: Exception) {

                }

            } else {
                context.toast("No items left here!")
            }
        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun undoRemove(v: View, deletedItem: FollowEmployerListData?, deletedIndex: Int, deleteJobID: Int) {
        // here we show snackbar and undo option

        val msg = Html.fromHtml("<font color=\"#ffffff\"> This item has been removed! </font>")
        val snack = Snackbar.make(v, "$msg", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
//                    FollowUnfollowJob.cancelJob(deleteJobID)
                    restoreMe(deletedItem!!, deletedIndex)
                    employersCommunicator?.scrollToUndoPosition(deletedIndex)
                    //Log.d("comid", "comid = ${deletedItem} ccc = ${deletedIndex}")
                }

        snack?.show()
        //Log.d("swipe", "dir to LEFT")
    }

    private fun restoreMe(item: FollowEmployerListData, pos: Int) {
        followedEmployerList?.add(pos, item)
        notifyItemInserted(pos)
        undoButtonPressed = true
    }

    fun add(r: FollowEmployerListData) {
        followedEmployerList?.add(r)
        notifyItemInserted(followedEmployerList!!.size - 1)
        employersCommunicator.setFollowedEmployerList(followedEmployerList)
    }

    fun addAll(moveResults: List<FollowEmployerListData>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(FollowEmployerListData())

    }

    fun removeAll() {
        followedEmployerList?.clear()
        notifyDataSetChanged()
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = followedEmployerList!!.size - 1
        val result = getItem(position)
        notifyItemRemoved(position)

        if (result != null) {
            followedEmployerList!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

}

class FollowedEmployerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val employerCompany = view?.findViewById(R.id.employers_company_TV) as TextView
    val offeringJobs = view?.findViewById(R.id.offering_jobs_number_TV) as TextView
    val followUunfollow = view?.findViewById(R.id.follow_unfollow_BTN) as MaterialButton
    val followemployersCard = view?.findViewById(R.id.follwEmp_cardview) as CardView
}

class FollowedEmployerViewHolderWithAd(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val employerCompany = view?.findViewById(R.id.employers_company_TV) as TextView
    val offeringJobs = view?.findViewById(R.id.offering_jobs_number_TV) as TextView
    val followUunfollow = view?.findViewById(R.id.follow_unfollow_BTN) as MaterialButton
    val followemployersCard = view?.findViewById(R.id.follwEmp_cardview) as CardView
    val ad_small_template: TemplateView = view?.findViewById(R.id.ad_small_template) as TemplateView
}

class FollowedEmployerLoadingViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
    val mProgressBar: ProgressBar = itemView?.findViewById(R.id.loadmore_progress_1) as ProgressBar
    val mRetryBtn: ImageButton = itemView?.findViewById(R.id.loadmore_retry_1) as ImageButton
    val mErrorTxt: TextView = itemView?.findViewById(R.id.loadmore_errortxt_1) as TextView
    val mErrorLayout: LinearLayout = itemView?.findViewById(R.id.loadmore_errorlayout_1) as LinearLayout

    init {

        mRetryBtn?.setOnClickListener(this)
        mErrorLayout?.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.loadmore_retry, R.id.loadmore_errorlayout -> {
                /*  adapter?.showRetry(false, null)
                  mCallback?.retryPageLoad()*/
            }
        }
    }
}