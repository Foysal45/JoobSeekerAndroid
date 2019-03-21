package com.bdjobs.app.AppliedJobs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.AppliedJobModelActivity
import com.bdjobs.app.API.ModelClasses.AppliedJobModelData
import com.bdjobs.app.BackgroundJob.CancelAppliedJob
import com.bdjobs.app.BackgroundJob.ExpectedSalaryJob
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.getString
import com.bdjobs.app.Utilities.logException
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AppliedJobsAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val activity = context as Activity
    private var appliedJobsLists: ArrayList<AppliedJobModelData>? = ArrayList()
    private var appliedjobsActitivityLists: ArrayList<AppliedJobModelActivity>? = ArrayList()
    private var isLoadingAdded = false
    private var retryPageLoad = false
    private var errorMsg: String? = null
    private var session: BdjobsUserSession = BdjobsUserSession(context)
    private var communicator: AppliedJobsCommunicator = activity as AppliedJobsCommunicator

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

        try {
            holder?.CompanyName?.text = appliedJobsLists?.get(position)?.companyName
            holder?.PositionName?.text = appliedJobsLists?.get(position)?.title
            holder?.appliedOn?.text = appliedJobsLists?.get(position)?.appliedOn
            holder?.deadline?.text = appliedJobsLists?.get(position)?.deadLine
            holder?.expectedSalary?.text = appliedJobsLists?.get(position)?.expectedSalary

            Log.d("activity", appliedjobsActitivityLists?.toString())

            if (appliedJobsLists?.get(position)?.isUserSeenInvitation == "0") {
                holder?.cardViewAppliedJobs.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FDFFF6")))
            }

            if (appliedJobsLists?.get(position)?.invitaion == "1") {
                holder?.interviewBTN?.visibility = View.VISIBLE
            } else if (appliedJobsLists?.get(position)?.invitaion == "0") {
                holder?.interviewBTN?.visibility = View.GONE
            }


            if (appliedJobsLists?.get(position)?.viewedByEmployer == "No") {
                // holder?.cancelBTN?.visibility = View.VISIBLE
                try {
                    val deadline = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(appliedJobsLists?.get(position)?.deadLine)
                    val todaysDate = Date()

                    /*       Log.d("date", "$deadline - $todaysDate")
                           Log.d("jobtitle", "jobtitle = " + appliedJobsLists!![position].companyName +
                                   "jobid = " + appliedJobsLists!![position].jobId
                                   + "deadline=$deadline + \"todays=$todaysDate ")*/

                    val compare = deadline.compareTo(todaysDate)
                    Log.d("date", "compare = $compare")
                    /*  if (compare.equals("1")){
                          Log.d("date","$compare visible")
                          holder?.cancelBTN?.visibility = View.VISIBLE
                      }*/
                    if (deadline > todaysDate) {
                        holder?.cancelBTN?.visibility = View.VISIBLE
                        holder?.edit_SalaryIcon?.visibility = View.VISIBLE
                    } else if (deadline < todaysDate) {
                        holder?.cancelBTN?.visibility = View.GONE
                        holder?.edit_SalaryIcon?.visibility = View.GONE
                    }


                } catch (e: Exception) {
                    logException(e)
                    Log.d("date", "e")

                }
            } else if (appliedJobsLists?.get(position)?.viewedByEmployer == "Yes" || appliedJobsLists?.get(position)?.status?.isNullOrEmpty()!!) {
                holder?.employerViewIcon?.visibility = View.VISIBLE
                holder?.employerViewIcon?.setBackgroundResource(R.drawable.ic_done_appliedadap)
                holder?.cancelBTN?.visibility = View.GONE
                holder?.edit_SalaryIcon?.visibility = View.GONE
            }

            if (appliedJobsLists?.get(position)?.status == "1") {
                holder?.employerViewIcon?.visibility = View.VISIBLE
                holder?.employerViewIcon?.setBackgroundResource(R.drawable.ic_not_contacted_appliedjobs_adap)
            } else if (appliedJobsLists?.get(position)?.status == "2") {
                holder?.employerViewIcon?.visibility = View.VISIBLE
                holder?.employerViewIcon?.setBackgroundResource(R.drawable.ic_contacted_appliedjobs_adap)
            } else if (appliedJobsLists?.get(position)?.status == "3") {
                holder?.employerViewIcon?.visibility = View.VISIBLE
                holder?.employerViewIcon?.setBackgroundResource(R.drawable.ic_hired_appliedjobs)
            }


            holder?.edit_SalaryIcon?.setOnClickListener {
                try {
                //    Log.d("huhu", "huhu")

                    val saveSearchDialog = Dialog(context)
                    saveSearchDialog?.setContentView(R.layout.expected_salary_popup)
                    saveSearchDialog?.setCancelable(true)
                    saveSearchDialog?.show()
                    val updateBTN = saveSearchDialog?.findViewById(R.id.updateBTN) as Button
                    val cancelBTN = saveSearchDialog?.findViewById(R.id.cancelBTN) as Button
                    val expected_salary_tv = saveSearchDialog?.findViewById(R.id.expected_salary_ET) as TextInputEditText
                    val accountResult_tv = saveSearchDialog?.findViewById(R.id.accountResult_tv) as TextView
                    val position_tv = saveSearchDialog?.findViewById(R.id.position_tv) as TextView
                    val employer_tv = saveSearchDialog?.findViewById(R.id.employer_tv) as TextView
                    val expected_salary_ET = saveSearchDialog?.findViewById(R.id.expected_salary_ET) as TextInputEditText
                    position_tv.text = appliedJobsLists?.get(position)?.title
                    employer_tv.text = appliedJobsLists?.get(position)?.companyName
                    accountResult_tv.text = session.userName
                    var expectedSalary = appliedJobsLists?.get(position)?.expectedSalary
                    expected_salary_ET.setText(expectedSalary.toString())


                    cancelBTN?.setOnClickListener {
                        try {
                            saveSearchDialog?.dismiss()
                        } catch (e: Exception) {
                            logException(e)
                        }
                    }
                    updateBTN.setOnClickListener {
                        if (expected_salary_ET.length() != 0) {
                            try {//update
                                var salary = expected_salary_tv.getString()
                                Log.d("popup", "popup-" + session.userId!! + "de-" + session.decodId!! + "jobid-" + appliedJobsLists!![position].jobId!! + "sal-" + salary)
                                ExpectedSalaryJob.runJobImmediately(session.userId!!, session.decodId!!, appliedJobsLists?.get(position)?.jobId!!, salary)
                                // updateExpectedSalary(appliedJobsLists!![position].jobId!!,salary)
                                saveSearchDialog.dismiss()
                                appliedJobsLists?.get(position)?.expectedSalary = salary
                                notifyItemChanged(position)
                            } catch (e: Exception) {
                                logException(e)
                            }
                        }
                    }
                } catch (e: Exception) {
                    logException(e)
                }


            }
            holder?.cancelBTN?.setOnClickListener {
                try {
                    removeItem(position, it)
                } catch (e: Exception) {
                    logException(e)
                }
            }
            holder?.interviewBTN?.setOnClickListener {
                try {
                    communicator.gotoInterviewInvitationDetails(
                            from = "appliedjobs",
                            jobID = appliedJobsLists?.get(position)?.jobId!!,
                            jobTitle = appliedJobsLists?.get(position)?.title!!,
                            companyName = appliedJobsLists?.get(position)?.companyName!!
                    )
                } catch (e: Exception) {
                    logException(e)
                }
            }
            holder?.interactionBTN?.setOnClickListener {
                try {
                    communicator?.setFrom("employerInteraction")
                    communicator?.setjobID(appliedJobsLists?.get(position)?.jobId!!)
                    communicator?.gotoEmployerInteractionFragment()
                    communicator?.setComapany(appliedJobsLists?.get(position)?.companyName!!)
                    communicator?.setTitle(appliedJobsLists?.get(position)?.title!!)
                } catch (e: Exception) {
                    logException(e)
                }
            }
            holder?.itemView?.setOnClickListener {
                try {
                    val jobids = ArrayList<String>()
                    val lns = ArrayList<String>()
                    val deadline = ArrayList<String>()
                    deadline.add(appliedJobsLists?.get(position)?.deadLine.toString())
                    jobids.add(appliedJobsLists?.get(position)?.jobId.toString())
                    lns.add("0")
                    communicator.setFrom("")
                    activity?.startActivity<JobBaseActivity>("from" to "employer", "jobids" to jobids, "lns" to lns, "position" to 0, "deadline" to deadline)
                } catch (e: Exception) {
                    logException(e)
                }
            }
        } catch (e: Exception) {
            logException(e)
        }

    }

    fun removeItem(position: Int, view: View) {
        try {
            if (appliedJobsLists?.size != 0) {
                val deletedItem = appliedJobsLists?.get(position)
                val jobid = appliedJobsLists?.get(position)?.jobId
                val companyName = appliedJobsLists?.get(position)?.companyName
                Log.d("werywirye", "jobid = $jobid companyname = $companyName")
                appliedJobsLists?.removeAt(position)
                notifyItemRemoved(position)
                try {
                    val deleteJobID = CancelAppliedJob.scheduleAdvancedJob(session.userId!!, session.decodId!!, jobid!!)
                    undoRemove(view, deletedItem, position, deleteJobID)
                    communicator.decrementCounter()
                } catch (e: Exception) {

                }

            } else {
                context.toast("No items left here!")
            }
        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun undoRemove(v: View, deletedItem: AppliedJobModelData?, deletedIndex: Int, deleteJobID: Int) {
        // here we show snackbar and undo option

        try {
            val msg = Html.fromHtml("<font color=\"#ffffff\"> This item has been removed! </font>")
            val snack = Snackbar.make(v, "$msg", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        CancelAppliedJob.cancelJob(deleteJobID)
                        restoreMe(deletedItem!!, deletedIndex)
                        Log.d("jobiiii", "undo = deleted = ${deletedItem} index = ${deletedIndex}")
                        communicator?.scrollToUndoPosition(deletedIndex)
                        Log.d("comid", "comid")
                    }

            snack?.show()
            Log.d("swipe", "dir to LEFT")
        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun restoreMe(item: AppliedJobModelData, pos: Int) {
        try {
            appliedJobsLists?.add(pos, item)
            notifyItemInserted(pos)
            //undoButtonPressed = true
        } catch (e: Exception) {
            logException(e)
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
        for (result in moveResults) {
            add(result)
        }
    }

    fun addActivity(r: AppliedJobModelActivity) {
        appliedjobsActitivityLists?.add(r)
        notifyItemInserted(appliedjobsActitivityLists!!.size - 1)

    }

    fun addAllActivity(moveResults: List<AppliedJobModelActivity>) {
        for (result in moveResults) {
            addActivity(result)
        }
    }

    fun removeAll() {
        appliedJobsLists?.clear()
        appliedjobsActitivityLists?.clear()
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

    val appliedOn = view?.findViewById(R.id.appliedon_tv) as TextView
    val expectedSalary = view?.findViewById(R.id.exSalary_tv) as TextView
    val deadline = view?.findViewById(R.id.deadline_tv) as TextView
    val PositionName = view?.findViewById(R.id.textViewPositionName) as TextView
    val CompanyName = view?.findViewById(R.id.textViewCompanyName) as TextView
    val employerViewIcon = view?.findViewById(R.id.employerView_icon) as ImageView
    val interactionBTN = view?.findViewById(R.id.interactionBTN) as MaterialButton
    val interviewBTN = view?.findViewById(R.id.interviewInvitationBTN) as MaterialButton
    val cancelBTN = view?.findViewById(R.id.CancelBTN) as MaterialButton
    val cardViewAppliedJobs = view?.findViewById(R.id.cardView) as CardView
    val edit_SalaryIcon = view?.findViewById(R.id.edit_SalaryIcon) as ImageView
}

class LoadingVH(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView),
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