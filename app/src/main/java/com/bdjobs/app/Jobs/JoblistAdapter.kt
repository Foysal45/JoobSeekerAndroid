package com.bdjobs.app.Jobs

import android.content.Context
import android.os.Build


import android.text.Layout.JUSTIFICATION_MODE_INTER_WORD
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.squareup.picasso.Picasso

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
    private var jobList: MutableList<DataItem>? = null
    var call: JobCommunicator? = null
    private var isLoadingAdded = false
    private var retryPageLoad = false
    private var errorMsg: String? = null


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

        /* Log.d("Check"," on Bind View holder Position " + position)
         Log.d("Check"," getItemViewType(position) " + getItemViewType(position))*/

        var result = jobList?.get(position) // jobs


        when (getItemViewType(position)) {
            BASIC -> {

                /*   Log.d("Check", " BASIC $BASIC")*/

                val jobsVH = holder as JobsListVH

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    jobsVH.tvPosName.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                    jobsVH.tvComName.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                    jobsVH.tvExperience.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                }

                jobsVH.tvPosName.text = result?.jobTitle
                jobsVH.tvComName.text = result?.companyName
                jobsVH.tvDeadline.text = result?.deadline
                jobsVH.tvEducation.text = result?.eduRec
                jobsVH.tvExperience.text = result?.experience

                jobsVH.itemView.setOnClickListener {

                    call?.onItemClicked(position)

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

                /*    Log.d("Check", " BASIC " + BASIC)*/

                val jobsVH = holder as StandOutListVH

                jobsVH.tvPosName.text = result?.jobTitle
                jobsVH.tvComName.text = result?.companyName
                jobsVH.tvDeadline.text = result?.deadline
                jobsVH.tvEducation.text = result?.eduRec
                jobsVH.tvExperience.text = result?.experience


                jobsVH.itemView.setOnClickListener {
                    call?.onItemClicked(position)
                }


                if (result?.logo != null) {
                    jobsVH.logoImageView.visibility = View.VISIBLE
                    Picasso.get().load(result.logo).into(jobsVH.logoImageView)


                }
            }

            STANDOUT_AD -> {
                val jobsVH = holder as StandOutAdJobListVH
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    jobsVH.tvPosName.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                    jobsVH.tvComName.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                    jobsVH.tvExperience.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                }

                jobsVH.tvPosName.text = result?.jobTitle
                jobsVH.tvComName.text = result?.companyName
                jobsVH.tvDeadline.text = result?.deadline
                jobsVH.tvEducation.text = result?.eduRec
                jobsVH.tvExperience.text = result?.experience

                jobsVH.itemView.setOnClickListener {

                    call?.onItemClicked(position)

                }
            }

            BASIC_AD -> {

                val jobsVH = holder as BasicAdobListVH
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    jobsVH.tvPosName.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                    jobsVH.tvComName.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                    jobsVH.tvExperience.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                }

                jobsVH.tvPosName.text = result?.jobTitle
                jobsVH.tvComName.text = result?.companyName
                jobsVH.tvDeadline.text = result?.deadline
                jobsVH.tvEducation.text = result?.eduRec
                jobsVH.tvExperience.text = result?.experience

                jobsVH.itemView.setOnClickListener {

                    call?.onItemClicked(position)

                }

            }
        }


    }


    override fun getItemCount(): Int {
        return if (jobList == null) 0 else jobList!!.size
    }

    override fun getItemViewType(position: Int): Int {


        /*   Log.d("Hello","Position: $position")*/




       /* if (showAD && (position % 3 == 0)) {
            *//*   Log.d("Hello","Position: AD= $position")*//*
            if (position == jobList!!.size - 1 && isLoadingAdded) {

                return LOADING

            } else if (jobList!![position].standout.equals("1")) {

                return STANDOUT_AD

            } else if (jobList!![position].standout.equals("0")) {

                return BASIC_AD
            }

        } else {
            if (position == jobList!!.size - 1 && isLoadingAdded) {

                return LOADING

            } else if (jobList!![position].standout.equals("1")) {

                return STANDOUT

            } else if (jobList!![position].standout.equals("0")) {

                return BASIC
            }
        }*/

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

                return STANDOUT

            } else if (jobList!![position].standout.equals("0")) {

                return BASIC
            }
        }
        return LOADING


    }


    fun add(r: DataItem) {
        jobList?.add(r)
        notifyItemInserted(jobList!!.size - 1)

    }

    fun addAll(moveResults: List<DataItem>) {
        for (result in moveResults) {
            add(result)
        }
        jobCommunicator?.setJobList(jobList)
    }

    private fun remove(r: DataItem?) {
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
        add(DataItem())

    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = jobList!!.size - 1
        val result = getItem(position)

        if (result != null) {
            jobList!!.removeAt(position)
            notifyItemRemoved(position)
        }

    }

    private fun getItem(position: Int): DataItem? {
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
    private class StandOutAdJobListVH(viewItem: View?) : RecyclerView.ViewHolder(viewItem!!) {
        val tvPosName: TextView = viewItem?.findViewById(R.id.textViewPositionNameNewJob) as TextView
        val tvComName: TextView = viewItem?.findViewById(R.id.textViewCompanyNameNewJob) as TextView
        val tvDeadline: TextView = viewItem?.findViewById(R.id.textViewDeadlineDateNewJob) as TextView
        val tvExperience: TextView = viewItem?.findViewById(R.id.textViewExperienceYearNewJob) as TextView
        val tvEducation: TextView = viewItem?.findViewById(R.id.textViewEducationNameNewJob) as TextView
        /*  val constraintLayout : ConstraintLayout = itemView.findViewById(R.id.constraintLayoutStandOut) as ConstraintLayout*/
        val logoImageView: ImageView = itemView.findViewById(R.id.imageViewCompanyLogoNewJob) as ImageView
        /* var cardView : CardView  = itemView.findViewById(R.id.cardViewStandOutN) as CardView*/


    }

    private class BasicAdobListVH(viewItem: View?) : RecyclerView.ViewHolder(viewItem!!) {
        val tvPosName: TextView = viewItem?.findViewById(R.id.textViewPositionNameNewJob) as TextView
        val tvComName: TextView = viewItem?.findViewById(R.id.textViewCompanyNameNewJob) as TextView
        val tvDeadline: TextView = viewItem?.findViewById(R.id.textViewDeadlineDateNewJob) as TextView
        val tvExperience: TextView = viewItem?.findViewById(R.id.textViewExperienceYearNewJob) as TextView
        val tvEducation: TextView = viewItem?.findViewById(R.id.textViewEducationNameNewJob) as TextView
        /*  val constraintLayout : ConstraintLayout = itemView.findViewById(R.id.constraintLayoutStandOut) as ConstraintLayout*/
        val logoImageView: ImageView = itemView.findViewById(R.id.imageViewCompanyLogoNewJob) as ImageView
        /* var cardView : CardView  = itemView.findViewById(R.id.cardViewStandOutN) as CardView*/


    }

    private class JobsListVH(viewItem: View?) : RecyclerView.ViewHolder(viewItem!!) {
        val tvPosName: TextView = viewItem?.findViewById(R.id.textViewPositionName) as TextView
        val tvComName: TextView = viewItem?.findViewById(R.id.textViewCompanyName) as TextView
        val tvDeadline: TextView = viewItem?.findViewById(R.id.textViewDeadlineDate) as TextView
        val tvExperience: TextView = viewItem?.findViewById(R.id.textViewExperienceYear) as TextView
        val tvEducation: TextView = viewItem?.findViewById(R.id.textViewEducationName) as TextView
        val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraintLayout) as ConstraintLayout
        val logoImageView: ImageView = itemView.findViewById(R.id.imageViewCompanyLogo) as ImageView
        var cardView: CardView = itemView.findViewById(R.id.cardView) as CardView


    }

    private class StandOutListVH(viewItem: View?) : RecyclerView.ViewHolder(viewItem!!) {
        val tvPosName: TextView = viewItem?.findViewById(R.id.textViewPositionNameStandOut) as TextView
        val tvComName: TextView = viewItem?.findViewById(R.id.textViewCompanyNameStandOut) as TextView
        val tvDeadline: TextView = viewItem?.findViewById(R.id.textViewDeadlineDateStandOut) as TextView
        val tvExperience: TextView = viewItem?.findViewById(R.id.textViewExperienceYearStandOut) as TextView
        val tvEducation: TextView = viewItem?.findViewById(R.id.textViewEducationNameStandOut) as TextView
        val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraintLayoutStandOut) as ConstraintLayout
        val logoImageView: ImageView = itemView.findViewById(R.id.imageViewCompanyLogoStandOut) as ImageView
        var cardView: CardView = itemView.findViewById(R.id.cardViewStandOut) as CardView


    }

    private class LoadingVH(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var mProgressBar: ProgressBar? = itemView.findViewById(R.id.loadmore_progress) as ProgressBar?
        private var mRetryBtn: ImageButton? = itemView.findViewById(R.id.loadmore_retry) as ImageButton?
        internal var mErrorTxt: TextView? = itemView.findViewById(R.id.loadmore_errortxt) as TextView?
        internal var mErrorLayout: LinearLayout? = itemView.findViewById(R.id.loadmore_errorlayout) as LinearLayout?
        /*private var mCallback: FragmentCallbacks? = null
        private val adapter: ShortListAdapter? = null*/


        /* mRetryBtn.setOnClickListener(this)
         mErrorLayout.setOnClickListener(this)*/


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