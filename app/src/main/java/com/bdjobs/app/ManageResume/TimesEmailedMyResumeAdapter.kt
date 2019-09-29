package com.bdjobs.app.ManageResume

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
import com.bdjobs.app.API.ModelClasses.TimesEmailedData
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.logException
import com.google.android.ads.nativetemplates.TemplateView
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
        private val ITEMJOBID = 2
        private val ITEM_WITH_AD = 3
        private val ITEM_JOB_ID_WITH_AD = 4
    }

    override fun getItemViewType(position: Int): Int {

        return if (position % 3 == 0 && position != 0) {
            if (position == timesEmailedList!!.size - 1 && isLoadingAdded) LOADING
            else if (!timesEmailedList?.get(position)?.jobid?.equalIgnoreCase("0")!!) ITEM_JOB_ID_WITH_AD
            else ITEM_WITH_AD
        } else{
            if (position == timesEmailedList!!.size - 1 && isLoadingAdded) LOADING
            else if (!timesEmailedList?.get(position)?.jobid?.equalIgnoreCase("0")!!) ITEMJOBID
            else ITEM
        }


//        return if (position == timesEmailedList!!.size - 1 && isLoadingAdded) LOADING
//        else if (!timesEmailedList?.get(position)?.jobid?.equalIgnoreCase("0")!!) ITEMJOBID
//        else ITEM

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
            ITEMJOBID -> {
                val viewItem = inflater.inflate(R.layout.item_emailed_resume_jobid, parent, false)
                viewHolder = TimesEmailedMyResumeVHJobID(viewItem)
            }
            ITEM_WITH_AD -> {
                val viewItem = inflater.inflate(R.layout.item_emailed_resume_ad, parent, false)
                viewHolder = TimesEmailedMyResumeWithAdViewHolder(viewItem)
            }
            ITEM_JOB_ID_WITH_AD -> {
                val viewItem = inflater.inflate(R.layout.item_emailed_resume_jobid_ad, parent, false)
                viewHolder = TimesEmailedMyResumeWithAdVHJobID(viewItem)
            }
        }

        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return if (timesEmailedList == null) 0 else timesEmailedList!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                val itemHolder = holder as TimesEmailedMyResumeViewHolder
                bindViews(itemHolder, position)

            }

            ITEM_WITH_AD -> {
                val itemHolder = holder as TimesEmailedMyResumeWithAdViewHolder
                Constants.showNativeAd(holder.ad_small_template, context)
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

            ITEMJOBID -> {
                val itemHolderJobID = holder as TimesEmailedMyResumeVHJobID
                bindViewsJOBID(itemHolderJobID, position)
            }

            ITEM_JOB_ID_WITH_AD -> {
                val itemHolderJobID = holder as TimesEmailedMyResumeWithAdVHJobID
                Constants.showNativeAd(holder.ad_small_template, context)
                bindViewsJOBID(itemHolderJobID, position)
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
        add(TimesEmailedData())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = timesEmailedList!!.size - 1
        val result = getItem(position)
        //   notifyItemRemoved(position)

        if (result?.jobid?.isNullOrBlank()!!) {
            timesEmailedList!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): TimesEmailedData? {
        return timesEmailedList!![position]
    }


    private fun bindViews(viewHolder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            ITEM -> {
                val holder = viewHolder as TimesEmailedMyResumeViewHolder
                holder?.subjectTV?.text = timesEmailedList?.get(position)?.subject?.trim()
                holder?.emailTV?.text = timesEmailedList?.get(position)?.emailTo?.trim()
                //  holder?.emailTV?.text = timesEmailedList?.get(position)?.sl?.trim()
                holder?.appliedDateTV?.text = timesEmailedList?.get(position)?.emailedOn?.trim()
            }
            ITEM_WITH_AD -> {
                val holder = viewHolder as TimesEmailedMyResumeWithAdViewHolder
                holder?.subjectTV?.text = timesEmailedList?.get(position)?.subject?.trim()
                holder?.emailTV?.text = timesEmailedList?.get(position)?.emailTo?.trim()
                //  holder?.emailTV?.text = timesEmailedList?.get(position)?.sl?.trim()
                holder?.appliedDateTV?.text = timesEmailedList?.get(position)?.emailedOn?.trim()
            }
        }


        /*     if (!timesEmailedList?.get(position)?.jobid?.equals("0")!!) {
                 holder?.itemView?.setOnClickListener {
                     Log.d("mumu", "mumu ")
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
             } else if (timesEmailedList?.get(position)?.jobid?.equals("0")!!) {
                 //  holder?.emailTV?.setTextColor(Color.parseColor("#767676"))
             }*/


    }

    private fun bindViewsJOBID(viewHolder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {

            ITEMJOBID -> {
                val holder = viewHolder as TimesEmailedMyResumeVHJobID
                holder?.subjectTV?.text = timesEmailedList?.get(position)?.subject?.trim()
                holder?.emailTV?.text = timesEmailedList?.get(position)?.emailTo?.trim()
                //  holder?.emailTV?.text = timesEmailedList?.get(position)?.sl?.trim()
                holder?.appliedDateTV?.text = timesEmailedList?.get(position)?.emailedOn?.trim()

                if (!timesEmailedList?.get(position)?.jobid?.equals("0")!!) {
                    holder?.itemView?.setOnClickListener {
                        Log.d("mumu", "mumu ")
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
                } else if (timesEmailedList?.get(position)?.jobid?.equals("0")!!) {
                    //  holder?.emailTV?.setTextColor(Color.parseColor("#767676"))
                }
            }

            ITEM_JOB_ID_WITH_AD -> {
                val holder = viewHolder as TimesEmailedMyResumeWithAdVHJobID
                holder?.subjectTV?.text = timesEmailedList?.get(position)?.subject?.trim()
                holder?.emailTV?.text = timesEmailedList?.get(position)?.emailTo?.trim()
                //  holder?.emailTV?.text = timesEmailedList?.get(position)?.sl?.trim()
                holder?.appliedDateTV?.text = timesEmailedList?.get(position)?.emailedOn?.trim()

                if (!timesEmailedList?.get(position)?.jobid?.equals("0")!!) {
                    holder?.itemView?.setOnClickListener {
                        Log.d("mumu", "mumu ")
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
                } else if (timesEmailedList?.get(position)?.jobid?.equals("0")!!) {
                    //  holder?.emailTV?.setTextColor(Color.parseColor("#767676"))
                }
            }
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

class TimesEmailedMyResumeWithAdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val subjectTV = view?.findViewById(R.id.subjectTV) as TextView
    val emailTV = view?.findViewById(R.id.emailTV) as TextView
    val appliedDateTV = view?.findViewById(R.id.appliedDateTV) as TextView
    val ad_small_template: TemplateView = view?.findViewById(R.id.ad_small_template) as TemplateView

//    val followUnfollow = view?.findViewById(R.id.follownfollow_BTN) as MaterialButton
//    val employersListCard = view?.findViewById(R.id.empList_cardview) as CardView

}

class TimesEmailedMyResumeVHJobID(view: View) : RecyclerView.ViewHolder(view) {
    val subjectTV = view?.findViewById(R.id.subjectTV) as TextView
    val emailTV = view?.findViewById(R.id.emailTV) as TextView
    val appliedDateTV = view?.findViewById(R.id.appliedDateTV) as TextView

}

class TimesEmailedMyResumeWithAdVHJobID(view: View) : RecyclerView.ViewHolder(view) {
    val subjectTV = view?.findViewById(R.id.subjectTV) as TextView
    val emailTV = view?.findViewById(R.id.emailTV) as TextView
    val appliedDateTV = view?.findViewById(R.id.appliedDateTV) as TextView
    val ad_small_template: TemplateView = view?.findViewById(R.id.ad_small_template) as TemplateView
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