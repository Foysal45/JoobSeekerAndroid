package com.bdjobs.app.Employers

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.AppliedJobModelActivity
import com.bdjobs.app.API.ModelClasses.MessageDataModel
import com.bdjobs.app.AppliedJobs.LoadingVH
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.d
import com.bdjobs.app.Utilities.logException
import java.util.*

class EmployerMessageListAdapter(private val context: Context) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    val activity = context as Activity
    private var employerMessageList: ArrayList<MessageDataModel>? = ArrayList()
    private var appliedjobsActitivityLists: ArrayList<AppliedJobModelActivity>? = ArrayList()
    private var isLoadingAdded = false
    private var retryPageLoad = false
    private var errorMsg: String? = null
    private var session: BdjobsUserSession = BdjobsUserSession(context)
    private var employersCommunicator :EmployersCommunicator? = null

    companion object {
        // View Types
        private val ITEM = 0
        private val LOADING = 1
    }

    init {

        if ((context as Activity) is EmployersBaseActivity) {
            employersCommunicator = context as EmployersCommunicator
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RecyclerView.ViewHolder {
        var viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            EmployerMessageListAdapter.ITEM -> {
                val viewItem = inflater.inflate(R.layout.employer_message_list_item, parent, false)
                viewHolder = EmployerMessageViewHolder(viewItem)
            }
            EmployerMessageListAdapter.LOADING -> {
                val viewLoading = inflater.inflate(R.layout.item_progress_1, parent, false)
                viewHolder = LoadingVH(viewLoading)
            }
        }

        return viewHolder!!

    }

    override fun getItemCount(): Int {
        return return if (employerMessageList == null) 0 else employerMessageList!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        d("fdjkghfjkg onBindViewHolder called")

        when (getItemViewType(position)) {
            EmployerMessageListAdapter.ITEM -> {
                val itemHolder = holder as EmployerMessageViewHolder
                bindViews(itemHolder, position)

            }
            EmployerMessageListAdapter.LOADING -> {
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



    private fun bindViews(holder: EmployerMessageViewHolder, position: Int) {

        try {
            holder.jobTitle.text = employerMessageList?.get(position)?.jobTite
            holder.companyName.text = employerMessageList?.get(position)?.companyName
            holder.date.text = employerMessageList?.get(position)?.mailedOn
            holder.dateHeading.text = "Date"

            Log.d("activity", appliedjobsActitivityLists?.toString())

            holder.itemView.setOnClickListener {
                try {

                    employersCommunicator?.setMessageId(employerMessageList?.get(position)?.messageId!!)
                    employersCommunicator?.gotoMessageDetail()
                } catch (e: Exception) {
                    logException(e)
                }
            }
        } catch (e: Exception) {
            logException(e)
        }

    }




    override fun getItemViewType(position: Int): Int {
        return if (position == employerMessageList!!.size - 1 && isLoadingAdded) EmployerMessageListAdapter.LOADING else EmployerMessageListAdapter.ITEM
    }

    fun add(r: MessageDataModel) {
        employerMessageList?.add(r)
        notifyItemInserted(employerMessageList!!.size - 1)
    }


    fun addAll(moveResults: List<MessageDataModel>) {
        for (result in moveResults) {
            add(result)
        }
    }


    fun removeAll() {
        employerMessageList?.clear()
        appliedjobsActitivityLists?.clear()
        notifyDataSetChanged()
    }


    fun addLoadingFooter() {
        isLoadingAdded = true
        // add(EmployerListModelData())
    }


    private fun getItem(position: Int): MessageDataModel? {
        return employerMessageList!![position]
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = employerMessageList!!.size - 1
        val result = getItem(position)


        if (result != null) {
            employerMessageList!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }




}


class EmployerMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val jobTitle = view.findViewById(R.id.jobtitleTV) as TextView
    val companyName = view.findViewById(R.id.companyNameTV) as TextView
    val date = view.findViewById(R.id.appliedDateTV) as TextView
    val dateHeading = view.findViewById(R.id.textView39) as TextView

}


class EmployerMessageLoadingVH(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
    val mProgressBar: ProgressBar = itemView.findViewById(R.id.loadmore_progress_1) as ProgressBar
    val mRetryBtn: ImageButton = itemView.findViewById(R.id.loadmore_retry_1) as ImageButton
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