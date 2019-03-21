package com.bdjobs.app.ManageResume

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.TimesEmailedData
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.logException
import org.jetbrains.anko.startActivity

class TimesEmailedMyResumeAdapter(private var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var timesEmailedList: ArrayList<TimesEmailedData>? = ArrayList()
    private var isLoadingAdded = false
    private var retryPageLoad = false
    private var errorMsg: String? = null
    val activity = context as Activity
    companion object {
        // View Types
        private val ITEM = 0
        private val LOADING = 1
    }

    override fun getItemViewType(position: Int): Int {
//        return super.getItemViewType(position)
        return if (position == timesEmailedList!!.size - 1 && isLoadingAdded) LOADING else ITEM
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            ITEM -> {
                val viewItem = inflater.inflate(R.layout.item_emailed_resume, parent, false)
                viewHolder = TimesEmailedMyResumeViewHolder(viewItem)
            }
            LOADING -> {
                val viewLoading = inflater.inflate(R.layout.item_progress_1, parent, false)
                viewHolder = TimesEmailedMyResumeLoadingVH(viewLoading)
            }
        }

        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return return if (timesEmailedList == null) 0 else timesEmailedList!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                val itemHolder = holder as TimesEmailedMyResumeViewHolder
                bindViews(itemHolder, position)

            }
            LOADING -> {
                val loadingVH = holder as TimesEmailedMyResumeLoadingVH

                if (retryPageLoad) {
                    loadingVH?.mErrorLayout?.visibility = View.VISIBLE
                    loadingVH?.mProgressBar?.visibility = View.GONE

                    loadingVH?.mErrorTxt?.text = if (errorMsg != null)
                        errorMsg
                    else
                        context?.getString(R.string.error_msg_unknown)

                } else {
                    loadingVH?.mErrorLayout?.visibility = View.GONE
                    loadingVH?.mProgressBar?.visibility = View.VISIBLE
                }
            }
        }



    }


    fun add(r: TimesEmailedData) {
        timesEmailedList?.add(r)
        notifyItemInserted(timesEmailedList!!.size - 1)
    }

    fun addAll(moveResults: List<TimesEmailedData>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun removeAll() {
        timesEmailedList?.clear()
        notifyDataSetChanged()
    }


    fun addLoadingFooter() {
        isLoadingAdded = true
        // add(timesEmailedListModelData())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = timesEmailedList!!.size - 1
        val result = getItem(position)
        notifyItemRemoved(position)

        if (result != null) {
            timesEmailedList!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    private fun getItem(position: Int): TimesEmailedData? {
        return timesEmailedList!![position]
    }


    private fun bindViews(holder: TimesEmailedMyResumeViewHolder, position: Int) {

        holder?.subjectTV?.text = timesEmailedList?.get(position)?.subject
        holder?.emailTV?.text = timesEmailedList?.get(position)?.emailTo
        holder?.appliedDateTV?.text = timesEmailedList?.get(position)?.emailedOn

        if (!timesEmailedList?.get(position)?.jobid?.equals("0")!!){
            holder?.itemView?.setOnClickListener {
                Log.d("mumu", "mumu")
                try {
                    val jobids = ArrayList<String>()
                    val lns = ArrayList<String>()
                    jobids.add(timesEmailedList?.get(position)?.jobid.toString())
                    lns.add("0")
                   // communicator.setFrom("")
                    activity?.startActivity<JobBaseActivity>("from" to "employer", "jobids" to jobids, "lns" to lns, "position" to 0)
                } catch (e: Exception) {
                    logException(e)
                }
            }
        }
        else if (timesEmailedList?.get(position)?.jobid?.equals("0")!!){
          //  holder?.emailTV?.setTextColor(Color.parseColor("#767676"))
        }


    }
}

class TimesEmailedMyResumeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val subjectTV = view?.findViewById(R.id.subjectTV) as TextView
    val emailTV = view?.findViewById(R.id.emailTV) as TextView
    val appliedDateTV = view?.findViewById(R.id.appliedDateTV) as TextView

//    val followUnfollow = view?.findViewById(R.id.follownfollow_BTN) as MaterialButton
//    val employersListCard = view?.findViewById(R.id.empList_cardview) as CardView

}

class TimesEmailedMyResumeLoadingVH(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView),
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