package com.bdjobs.app.Employers

//import com.bdjobs.app.BackgroundJob.FollowUnfollowJob
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.EmployerSubscribeModel
import com.bdjobs.app.API.ModelClasses.FollowEmployerListData
import com.bdjobs.app.ads.Ads
import com.bdjobs.app.LoggedInUserLanding.HomeCommunicator
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Workmanager.FollowUnfollowWorker
import com.bdjobs.app.databases.External.DataStorage
import com.bdjobs.app.sms.SmsBaseActivity
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.material.button.MaterialButton
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FollowedEmployersAdapter(private val context: Context,var onUpdateCounter: OnUpdateCounter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val activity = context as Activity
    val dataStorage = DataStorage(context)
    val bdJobsUserSession = BdjobsUserSession(context)
    private var followedEmployerList: ArrayList<FollowEmployerListData>? = ArrayList()
    private lateinit var companyId1: String
    private lateinit var companyName1: String
    private var undoButtonPressed: Boolean = false
    private var employersCommunicator : EmployersCommunicator? = null
    private var homeCommunicator : HomeCommunicator ? = null

    private var isLoadingAdded = false
    private var retryPageLoad = false
    private var errorMsg: String? = null
    var isNewPurchaseNeeded: String? = ""

    companion object {
        private const val ITEM = 0
        private const val LOADING = 1
        private const val ITEM_WITH_AD = 2
    }

    init {
        if (context is EmployersBaseActivity) {
            employersCommunicator = context
        }

        if (context is MainLandingActivity) {
            homeCommunicator = context
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
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
                    itemHolder.mErrorLayout.visibility = View.VISIBLE
                    itemHolder.mProgressBar.visibility = View.GONE
                    itemHolder.mErrorTxt.text = if (errorMsg != null)
                        errorMsg
                    else
                        context.getString(R.string.error_msg_unknown)

                } else {
                    itemHolder.mErrorLayout.visibility = View.GONE
                    itemHolder.mProgressBar.visibility = View.VISIBLE
                }
            }
            ITEM_WITH_AD -> {
                val itemHolder = holder as FollowedEmployerViewHolderWithAd
                Ads.showNativeAd(holder.adSmallTemplate, context)
                bindViews(itemHolder, position)
            }
        }

    }

    private fun bindViews(viewHolder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            ITEM -> {
                val holder = viewHolder as FollowedEmployerViewHolder
                val item = followedEmployerList?.get(position)
                try {
                    holder.employerCompany.text = item!!.companyName
                    holder.offeringJobs.text = item.jobCount
                    companyName1 = item.companyName!!
                    companyId1 = item.companyID!!
                    holder.followUnfollow.setOnClickListener {
                        try {
                            activity.alert("Are you sure you want to unfollow this company?", "Confirmation") {
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

                    var jobCount = item.jobCount
                    if (jobCount == null) {
                        jobCount = "0"
                    }
                    val jobCountInt = jobCount.toInt()

                    holder.itemView.setOnClickListener {
                        if (jobCountInt > 0) {
                            try {
                                if (position < followedEmployerList?.size!!) {
                                    if (employersCommunicator!=null) employersCommunicator?.gotoJobListFragment(item.companyID, item.companyName)
                                    else homeCommunicator?.gotoJobListFragment(item.companyID, item.companyName)
                                    if (employersCommunicator!=null) employersCommunicator?.positionClicked(position)
                                    else homeCommunicator?.positionClicked(position)

                                }
                            } catch (e: Exception) {
                                logException(e)
                            }
                        }
                    }


                    if (item.isSubscribed!!.equalIgnoreCase("True"))
                    {
                        holder.buttonSubscribe.hide()
                        holder.buttonUnsubscribe.show()
                    } else{
                        holder.buttonSubscribe.show()
                        holder.buttonUnsubscribe.hide()
                    }



                    holder.buttonSubscribe.setOnClickListener {

                        makeSubscribeUnsubscribeApiCallEmployer(item,1)

                        holder.buttonUnsubscribe.show()
                        it.hide()
                    }

                    holder.buttonUnsubscribe.setOnClickListener {

                        makeSubscribeUnsubscribeApiCallEmployer(item,0)

                        holder.buttonSubscribe.show()
                        it.hide()
                    }

                } catch (e: Exception) {
                    logException(e)
                }
            }
            ITEM_WITH_AD -> {
                val holder = viewHolder as FollowedEmployerViewHolderWithAd
                val item = followedEmployerList?.get(position)
                try {
                    holder.employerCompany.text = item!!.companyName
                    holder.offeringJobs.text = item.jobCount

                    holder.followUnfollow.setOnClickListener {

                        try {
                            activity.alert("Are you sure you want to unfollow this company?", "Confirmation") {
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

                    var jobCount = item.jobCount
                    if (jobCount == null) {
                        jobCount = "0"
                    }
                    val jobCountInt = jobCount.toInt()
                    holder.itemView.setOnClickListener {
                        if (jobCountInt > 0) {
                            try {
                                if (position < followedEmployerList?.size!!) {

                                    if (employersCommunicator!=null) employersCommunicator?.gotoJobListFragment(item.companyID!!, item.companyName!!)
                                    else homeCommunicator?.gotoJobListFragment(item.companyID,item.companyName)
                                    if (employersCommunicator!=null) employersCommunicator?.positionClicked(position)

                                }
                            } catch (e: Exception) {
                                logException(e)
                            }
                        }
                    }


                    if (item.isSubscribed!!.equalIgnoreCase("True"))
                    {
                        holder.buttonSubscribe.hide()
                        holder.buttonUnsubscribe.show()
                    } else{
                        holder.buttonSubscribe.show()
                        holder.buttonUnsubscribe.hide()
                    }



                    holder.buttonSubscribe.setOnClickListener {

                        makeSubscribeUnsubscribeApiCallEmployer(item,1)

                        holder.buttonUnsubscribe.show()
                        it.hide()
                    }

                    holder.buttonUnsubscribe.setOnClickListener {

                        makeSubscribeUnsubscribeApiCallEmployer(item,0)

                        holder.buttonSubscribe.show()
                        it.hide()
                    }

                } catch (e: Exception) {
                    logException(e)
                }
            }
        }


    }

    private fun makeSubscribeUnsubscribeApiCallEmployer(item: FollowEmployerListData, type: Int) {
       /* try {*/
            //0 -> unsubscribe
            //1 -> subscribe
            ApiServiceMyBdjobs.create().subscribeOrUnsubscribeSMSFromFollowedEmployers(
                    userId = bdJobsUserSession.userId,
                    decodeId = bdJobsUserSession.decodId,
                    companyId = item.companyID,
                    followId = item.employerId,
                    action = type,
                    appId = Constants.APP_ID

            ).enqueue(object : Callback<EmployerSubscribeModel> {
                override fun onFailure(call: Call<EmployerSubscribeModel>, t: Throwable) {
                    error("onFailure", t)
                }

                override fun onResponse(call: Call<EmployerSubscribeModel>, response: Response<EmployerSubscribeModel>) {
                    try {
                        response.body()?.statuscode?.let { status ->
                            if (status.equalIgnoreCase(Constants.api_request_result_code_ok)) {
                                item.isSubscribed = if (type == 1) "True" else "False"
                                isNewPurchaseNeeded = response.body()?.data?.get(0)?.isNewSMSPurchaseNeeded
                                if (type == 1)
                                    openSubscribeInfoDialog()
                                else
                                    Toast.makeText(context, "Successfully unsubscribed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        logException(e)
                    }
                }
            })
        /*} catch (e: Exception) {
            e.printStackTrace()
        }*/
    }

    @SuppressLint("SetTextI18n")
    private fun openSubscribeInfoDialog() {
        val builder = AlertDialog.Builder(context)
        val inflater = context.layoutInflater
        builder.setView(inflater.inflate(R.layout.dialog_subscribe_sms, null))
        builder.create().apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
            findViewById<ImageView>(R.id.img_close).setOnClickListener {
                this.cancel()
            }
            findViewById<MaterialButton>(R.id.btn_purchase).apply {
                if (isNewPurchaseNeeded!!.equalIgnoreCase("True")) {
                    text = "Buy SMS"
                    setBackgroundColor(ContextCompat.getColor(context,R.color.green))
                    strokeColor = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.border_grey))
                    textColor = ContextCompat.getColor(context,R.color.white)
                } else {
                    text =  "SMS Settings"
                    setBackgroundColor(ContextCompat.getColor(context,R.color.btn_light_blue))
                    strokeColor = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.border_blue))
                    textColor = ContextCompat.getColor(context,R.color.text_blue)
                }
            }.setOnClickListener {
                if (isNewPurchaseNeeded!!.equalIgnoreCase("False")) {
                    context.startActivity<SmsBaseActivity>("from" to "employer")
                    this.cancel()
                } else {
                    context.startActivity<SmsBaseActivity>()
                    this.cancel()
                }
            }
            findViewById<TextView>(R.id.tv_body).text =
                    if (isNewPurchaseNeeded!!.equalIgnoreCase("True"))
                        "You have successfully subscribed to get SMS job alert for this employer. You will get SMS alert based on subscription."
                    else
                        "You have successfully subscribed to get SMS job alert for this employer. You will get SMS alert based on subscription."
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 3 == 0 && position !=0)
            ITEM_WITH_AD
        else
            if (position == followedEmployerList!!.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    private fun getItem(position: Int): FollowEmployerListData {
        return followedEmployerList!![position]
    }

   private  fun removeItem(position: Int) {

        try {
            if (followedEmployerList?.size != 0) {
                val deletedItem = followedEmployerList?.get(position)
                val companyId = followedEmployerList?.get(position)?.companyID
                val companyName = followedEmployerList?.get(position)?.companyName
                followedEmployerList?.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeRemoved(position, followedEmployerList?.size!!)

                try {

                    val constraints = Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()

                    val followUnfollowData = workDataOf("companyid" to companyId, "companyname" to companyName)
                    val followUnfollowRequest = OneTimeWorkRequestBuilder<FollowUnfollowWorker>().setInputData(followUnfollowData).setConstraints(constraints).build()
                    WorkManager.getInstance(context).enqueue(followUnfollowRequest)

                    bdJobsUserSession.deccrementFollowedEmployer()

                    if (employersCommunicator!=null) {
                        employersCommunicator?.decrementCounter(position)
                        employersCommunicator?.getTotalFollowedEmployersCount()?.minus(1)?.let { onUpdateCounter.update(it) }
                    }
                    else {
                        homeCommunicator?.getTotalFollowedEmployersCount()?.minus(1)?.let { onUpdateCounter.update(it) }
                    }
                } catch (e: Exception) {

                }

            } else {
                context.toast("No items left here!")
            }
        } catch (e: Exception) {
            logException(e)
        }
    }

    fun add(r: FollowEmployerListData) {
        followedEmployerList?.add(r)
        notifyItemInserted(followedEmployerList!!.size - 1)
        if (employersCommunicator!=null) employersCommunicator?.setFollowedEmployerList(followedEmployerList)
        else homeCommunicator?.setFollowedEmployerList(followedEmployerList)
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


    interface OnUpdateCounter {
        fun update(count : Int)
    }

}

class FollowedEmployerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val employerCompany = view.findViewById(R.id.employers_company_TV) as TextView
    val offeringJobs = view.findViewById(R.id.offering_jobs_number_TV) as TextView
    val followUnfollow = view.findViewById(R.id.follow_unfollow_BTN) as MaterialButton
    val buttonSubscribe = view.findViewById(R.id.btn_subscribe_sms) as MaterialButton
    val buttonUnsubscribe = view.findViewById(R.id.btn_unsubscribe_sms) as MaterialButton
    val followedEmployersCard = view.findViewById(R.id.follwEmp_cardview) as CardView
}

class FollowedEmployerViewHolderWithAd(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val employerCompany = view.findViewById(R.id.employers_company_TV) as TextView
    val offeringJobs = view.findViewById(R.id.offering_jobs_number_TV) as TextView
    val followUnfollow = view.findViewById(R.id.follow_unfollow_BTN) as MaterialButton
    val buttonSubscribe = view.findViewById(R.id.btn_subscribe_sms) as MaterialButton
    val buttonUnsubscribe = view.findViewById(R.id.btn_unsubscribe_sms) as MaterialButton
    val followedEmployersCard = view.findViewById(R.id.follwEmp_cardview) as CardView
    val adSmallTemplate: TemplateView = view.findViewById(R.id.ad_small_template) as TemplateView
}

class FollowedEmployerLoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
    val mProgressBar: ProgressBar = itemView.findViewById(R.id.loadmore_progress_1) as ProgressBar
    private val mRetryBtn: ImageButton = itemView.findViewById(R.id.loadmore_retry_1) as ImageButton
    val mErrorTxt: TextView = itemView.findViewById(R.id.loadmore_errortxt_1) as TextView
    val mErrorLayout: LinearLayout = itemView.findViewById(R.id.loadmore_errorlayout_1) as LinearLayout

    init {

        mRetryBtn.setOnClickListener(this)
        mErrorLayout.setOnClickListener(this)
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