package com.bdjobs.app.Employers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.EmpVwdResumeData
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.logException

class EmployerViewedMyResumeAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var isLoadingAdded = false
    private var retryPageLoad = false
    private var errorMsg: String? = null
    private var vwdResumeList: ArrayList<EmpVwdResumeData> = ArrayList()

    companion object {
        // View Types
        private val ITEM = 0
        private val LOADING = 1
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            ITEM -> {
                val viewItem = inflater.inflate(R.layout.employer_viewed_my_resume, parent, false)
                viewHolder = EmployerViewedMyResumeVH(viewItem)
            }
            LOADING -> {
                val viewLoading = inflater.inflate(R.layout.item_progress_1, parent, false)
                viewHolder = ResumeLoadingVH(viewLoading)
            }
        }

        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return return if (vwdResumeList == null) 0 else vwdResumeList!!.size

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                val itemHolder = holder as EmployerViewedMyResumeVH
                bindViews(itemHolder, position)

            }
            LOADING -> {
                val ResumeLoadingVH = holder as ResumeLoadingVH

                if (retryPageLoad) {
                    ResumeLoadingVH?.mErrorLayout?.visibility = View.VISIBLE
                    ResumeLoadingVH?.mProgressBar?.visibility = View.GONE

                    ResumeLoadingVH?.mErrorTxt?.text = if (errorMsg != null)
                        errorMsg
                    else
                        context?.getString(R.string.error_msg_unknown)

                } else {
                    ResumeLoadingVH?.mErrorLayout?.visibility = View.GONE
                    ResumeLoadingVH?.mProgressBar?.visibility = View.VISIBLE
                }
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (position == vwdResumeList!!.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    private fun getItem(position: Int): EmpVwdResumeData? {
        return vwdResumeList!![position]
    }

    private fun bindViews(holder: EmployerViewedMyResumeVH, position: Int) {
        try {
            holder?.companyName?.text = vwdResumeList?.get(position)?.companyName
            holder?.appliedOn?.text = vwdResumeList?.get(position)?.viewedOn
            //  Log.d("hellohello", "hello= " + vwdResumeList?.get(position)?.summaryView)

            if (vwdResumeList?.get(position)?.summaryView == "yes") {
                holder?.summaryView.visibility = View.VISIBLE
                holder?.summaryView.setImageResource(R.drawable.ic_done_20dp)
            }
            else if(vwdResumeList?.get(position)?.detailView == "yes"){
                holder?.summaryView.visibility = View.VISIBLE
                holder?.summaryView.setImageResource(R.drawable.ic_done_double)
            }
        } catch (e: Exception) {
            logException(e)
        }


    }

    //----------------

    fun add(r: EmpVwdResumeData) {
        vwdResumeList?.add(r)
        notifyItemInserted(vwdResumeList!!.size - 1)
    }


    fun addAll(moveResults: List<EmpVwdResumeData>) {
        for (result in moveResults!!) {
            add(result)
        }
    }

    fun removeAll() {
        vwdResumeList?.clear()
        notifyDataSetChanged()
    }


    fun addLoadingFooter() {
        isLoadingAdded = true
         add(EmpVwdResumeData())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = vwdResumeList!!.size - 1
        val result = getItem(position)
        notifyItemRemoved(position)

        if (result != null) {
            vwdResumeList!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

}

class EmployerViewedMyResumeVH(view: View) : RecyclerView.ViewHolder(view) {
    val appliedOn = view?.findViewById(R.id.viewedOn_TV) as TextView
    val companyName = view?.findViewById(R.id.employers_companyName_TV) as TextView
    val summaryView = view?.findViewById(R.id.summaryView_IV) as ImageView
}

class ResumeLoadingVH(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView),
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
