package com.bdjobs.app.AppliedJobs

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.AppliedJobModelData
import com.bdjobs.app.API.ModelClasses.AppliedJobsSalaryEdit
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.getString
import com.bdjobs.app.Utilities.logException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast
import android.R.attr.data



class AppliedJobsAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var appliedJobsLists: ArrayList<AppliedJobModelData>? = ArrayList()
    private var isLoadingAdded = false
    private var retryPageLoad = false
    private var errorMsg: String? = null
    private var session: BdjobsUserSession = BdjobsUserSession(context)

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
        holder?.CompanyName?.text = appliedJobsLists!![position].companyName
        holder?.PositionName?.text = appliedJobsLists!![position].title
        holder?.appliedOn?.text = appliedJobsLists!![position].appliedOn
        holder?.deadline?.text = appliedJobsLists!![position].deadLine
        holder?.expectedSalary?.text = appliedJobsLists!![position].expectedSalary

/*        if (appliedJobsLists!![position].viewedByEmployer == "Yes") {

            holder?.employerViewIcon?.visibility = View.VISIBLE


        } else if (appliedJobsLists!![position].viewedByEmployer == "No") {

            holder?.employerViewIcon?.visibility = View.GONE

        }

        if (appliedJobsLists!![position].invitaion == "1") {
            holder?.interviewInvitationBTN?.visibility = View.VISIBLE

        }
        */
        if (appliedJobsLists!![position].isUserSeenInvitation == "1") {

            holder?.cardViewAppliedJobs.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FDFFF6")))
        }

        if (appliedJobsLists!![position].invitaion == "1") {
            holder?.interviewCancelBTN?.visibility = View.VISIBLE
            holder?.interviewCancelBTN?.text = "Interview Invitation"
            holder?.interviewCancelBTN?.setTextColor(ColorStateList.valueOf(Color.parseColor("#AC016D")))


        } else if (appliedJobsLists!![position].viewedByEmployer == "No") {

            try {
                val deadline = SimpleDateFormat("mm/dd/yyyy", Locale.ENGLISH).parse(appliedJobsLists!![position].deadLine)
                val todaysDate = Date()
                if (deadline > todaysDate) {
                    holder?.interviewCancelBTN?.visibility = View.VISIBLE
                    holder?.edit_SalaryIcon?.visibility = View.VISIBLE
                    holder?.interviewCancelBTN?.text = "Cancel Application"
                    holder?.interviewCancelBTN?.setTextColor(ColorStateList.valueOf(Color.parseColor("#767676")))

                }
            } catch (e: Exception) {
                logException(e)

            }
            holder?.edit_SalaryIcon?.setOnClickListener {
                Log.d("huhu", "huhu")

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
                position_tv.text = appliedJobsLists!![position].title
                employer_tv.text = appliedJobsLists!![position].companyName
                accountResult_tv.text = session.userName


                cancelBTN?.setOnClickListener {
                    saveSearchDialog?.dismiss()
                }
                updateBTN.setOnClickListener {
                    var salary = expected_salary_tv.getString()
                    updateExpectedSalary(appliedJobsLists!![position].jobId!!, salary)
                    saveSearchDialog?.dismiss()
                }

            }

        }


    }

    private fun updateExpectedSalary(jobid : String, expectedSalary : String){
        ApiServiceMyBdjobs.create().getUpdateSalaryMsg(
                userId = session.userId,
                decodeId = session.decodId,
                JobId = jobid,
                txtExpectedSalary =  expectedSalary
        ).enqueue(object : Callback<AppliedJobsSalaryEdit>{
            override fun onFailure(call: Call<AppliedJobsSalaryEdit>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<AppliedJobsSalaryEdit>, response: Response<AppliedJobsSalaryEdit>) {

                if (response.body()?.statuscode == "0"){
                    Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
                }
                  }

        })

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

    val appliedOn = view?.findViewById(R.id.appliedon_tv) as TextView
    val expectedSalary = view?.findViewById(R.id.exSalary_tv) as TextView
    val deadline = view?.findViewById(R.id.deadline_tv) as TextView
    val PositionName = view?.findViewById(R.id.textViewPositionName) as TextView
    val CompanyName = view?.findViewById(R.id.textViewCompanyName) as TextView
    val employerViewIcon = view?.findViewById(R.id.employerView_icon) as ImageView
    val interactionBTN = view?.findViewById(R.id.interactionBTN) as MaterialButton
    val interviewCancelBTN = view?.findViewById(R.id.interviewCancelBTN) as MaterialButton
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