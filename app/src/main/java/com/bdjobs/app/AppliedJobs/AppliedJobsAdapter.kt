package com.bdjobs.app.AppliedJobs

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.AppliedJobModelData
import com.bdjobs.app.R
import com.google.android.material.button.MaterialButton
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class AppliedJobsAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var appliedJobsLists: ArrayList<AppliedJobModelData>? = ArrayList()
    private var isLoadingAdded = false
    private var retryPageLoad = false
    private var errorMsg: String? = null

    companion object {
        // View Types
        private val ITEM = 0
        private val LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // return AppliedjobsViewHolder(LayoutInflater.from(context).inflate(R.layout.applied_jobs, parent, false))

        var viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            ITEM -> {
                val viewItem = inflater.inflate(R.layout.applied_jobs, parent, false)
                viewHolder = AppliedjobsViewHolder(viewItem)
            }
            LOADING -> {
                val viewLoading = inflater.inflate(R.layout.item_progress_1, parent, false)
                viewHolder = LoadingVH(viewLoading)
            }
        }

        return viewHolder!!


    }

    override fun getItemCount(): Int {
        return return if (appliedJobsLists == null) 0 else appliedJobsLists!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                val itemHolder = holder as AppliedjobsViewHolder
                bindViews(itemHolder, position)

            }
            LOADING -> {
                val loadingVH = holder as LoadingVH

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.visibility = View.VISIBLE
                    loadingVH.mProgressBar.visibility = View.GONE

                    loadingVH.mErrorTxt.text = if (errorMsg != null)
                        errorMsg
                    else
                        context.getString(R.string.error_msg_unknown)

                } else {
                    loadingVH.mErrorLayout.visibility = View.GONE
                    loadingVH.mProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun bindViews(holder: AppliedjobsViewHolder, position: Int) {
        holder.CompanyName.text = appliedJobsLists!![position].companyName
        holder.PositionName.text = appliedJobsLists!![position].title
        holder.appliedOn.text = appliedJobsLists!![position].appliedOn
        holder.deadline.text = appliedJobsLists!![position].deadLine
        holder.expectedSalary.text = appliedJobsLists!![position].expectedSalary

        if (appliedJobsLists!![position].viewedByEmployer == "Yes") {

            holder.employerViewIcon.visibility = View.VISIBLE

        } else if (appliedJobsLists!![position].viewedByEmployer == "No") {

            holder.employerViewIcon.visibility = View.GONE

        }
        if (appliedJobsLists!![position].invitaion == "1") {
            holder.applicationBTN.visibility = View.VISIBLE
            holder.applicationBTN.text = "Interview Invitation"
            holder.applicationBTN.setTextColor(ColorStateList.valueOf(Color.parseColor("#AC016D")))

        }
        if (appliedJobsLists!![position].viewedByEmployer == "No") {

            try {
                val deadline = SimpleDateFormat("mm/dd/yyyy", Locale.ENGLISH).parse(appliedJobsLists!![position].deadLine)
                val todaysDate = Date()
                if (deadline > todaysDate) {
                    holder.applicationBTN.visibility = View.VISIBLE
                    holder.applicationBTN.text = "Cancel Application"
                    holder.applicationBTN.setTextColor(ColorStateList.valueOf(Color.parseColor("#767676")))
                }
            } catch (e: Exception) {

            }

        }


    }

    override fun getItemViewType(position: Int): Int {
        return if (position == appliedJobsLists!!.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    fun add(r: AppliedJobModelData) {
        appliedJobsLists?.add(r)
        notifyItemInserted(appliedJobsLists!!.size - 1)
    }


    fun addAll(moveResults: List<AppliedJobModelData>) {
        for (result in moveResults!!) {
            add(result)
        }
    }

    fun removeAll() {
        appliedJobsLists?.clear()
        notifyDataSetChanged()
    }


    fun addLoadingFooter() {
        isLoadingAdded = true
        // add(EmployerListModelData())
    }


    private fun getItem(position: Int): AppliedJobModelData? {
        return appliedJobsLists!![position]
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = appliedJobsLists!!.size - 1
        val result = getItem(position)
        notifyItemRemoved(position)

        if (result != null) {
            appliedJobsLists!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}

class AppliedjobsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val appliedOn = view.findViewById(R.id.appliedon_tv) as TextView
    val expectedSalary = view.findViewById(R.id.exSalary_tv) as TextView
    val deadline = view.findViewById(R.id.deadline_tv) as TextView
    val PositionName = view.findViewById(R.id.textViewPositionName) as TextView
    val CompanyName = view.findViewById(R.id.textViewCompanyName) as TextView
    val employerViewIcon = view.findViewById(R.id.employerView_icon) as ImageView
    val applicationBTN = view.findViewById(R.id.applicationBTN) as MaterialButton


}

class LoadingVH(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
    val mProgressBar: ProgressBar = itemView.findViewById(R.id.loadmore_progress_1) as ProgressBar
    val mRetryBtn: ImageButton = itemView.findViewById(R.id.loadmore_retry_1) as ImageButton
    val mErrorTxt: TextView = itemView.findViewById(R.id.loadmore_errortxt_1) as TextView
    val mErrorLayout: LinearLayout = itemView.findViewById(R.id.loadmore_errorlayout_1) as LinearLayout
    /* private var mCallback: FragmentCallbacks? = null
     private val adapter: PostedJobsAdapter? = null*/

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