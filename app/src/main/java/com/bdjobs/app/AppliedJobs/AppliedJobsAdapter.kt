package com.bdjobs.app.AppliedJobs

//import com.bdjobs.app.BackgroundJob.CancelAppliedJob
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
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.bdjobs.app.API.ModelClasses.AppliedJobModelActivity
import com.bdjobs.app.API.ModelClasses.AppliedJobModelData
import com.bdjobs.app.ads.Ads
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.utilities.*
import com.bdjobs.app.Workmanager.CancelAppliedJobWorker
import com.bdjobs.app.Workmanager.ExpectedSalaryWorker
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.*
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
    private val deadlinePattern = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
    private val todaysPattern = SimpleDateFormat("E MMM dd yyyy", Locale.ENGLISH)


    companion object {
        // View Types
        private val ITEM = 0
        private val LOADING = 1
        private val ITEM_WITH_AD = 2
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
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

            ITEM_WITH_AD -> {
                val viewItem = inflater.inflate(R.layout.applied_jobs_ad, parent, false)
                viewHolder = AppliedjobsAdViewHolder(viewItem)
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

            ITEM_WITH_AD -> {
                val itemHolder = holder as AppliedjobsAdViewHolder
                Ads.showNativeAd(itemHolder.ad_small_template, context)
                bindViews(itemHolder, position)
            }
        }
    }

    private fun bindViews(vHolder: RecyclerView.ViewHolder, position: Int) {


        when (getItemViewType(position)) {
            ITEM -> {
                try {
                    val holder = vHolder as AppliedjobsViewHolder
                    holder.CompanyName.text = appliedJobsLists?.get(position)?.companyName
                    holder.PositionName.text = appliedJobsLists?.get(position)?.title

                    try{
                        holder.appliedOn.text = SimpleDateFormat("M/d/yyyy").parse(appliedJobsLists?.get(position)?.appliedOn).toSimpleDateString()
                        holder.deadline.text = SimpleDateFormat("M/d/yyyy").parse(appliedJobsLists?.get(position)?.deadLine).toSimpleDateString()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                    holder.expectedSalary.text = appliedJobsLists?.get(position)?.expectedSalary

                    //Log.d("activity", appliedjobsActitivityLists?.toString())

                    if (appliedJobsLists?.get(position)?.isUserSeenInvitation == "0") {
                        holder.cardViewAppliedJobs.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FDFFF6")))
                    }

                    if (appliedJobsLists?.get(position)?.invitaion == "1") {
                        holder.interviewBTN.visibility = View.VISIBLE
                    } else if (appliedJobsLists?.get(position)?.invitaion == "0") {
                        holder.interviewBTN.visibility = View.GONE
                    }


                    if (appliedJobsLists?.get(position)?.viewedByEmployer == "No") {
                        // holder?.cancelBTN?.visibility = View.VISIBLE
                        holder.employerViewIcon.visibility = View.GONE
                        try {
                            val deadline = deadlinePattern.parse(appliedJobsLists?.get(position)?.deadLine)
                            val todayDate = todaysPattern.parse(todaysPattern.format(Date()))

//                            Log.d("date", "deadline - $deadline")
//                            Log.d("date", "todays date - $todayDate")

                            if (deadline >= todayDate) {
                                holder.cancelBTN.visibility = View.VISIBLE
                                holder.edit_SalaryIcon.visibility = View.VISIBLE
                            } else if (deadline < todayDate) {
                                holder.cancelBTN.visibility = View.GONE
                                holder.edit_SalaryIcon.visibility = View.GONE
                            }


                        } catch (e: Exception) {
                            logException(e)
                            //Log.d("date", "e")

                        }
                    } else if (appliedJobsLists?.get(position)?.viewedByEmployer == "Yes" || appliedJobsLists?.get(position)?.status?.isNullOrEmpty()!!) {
                        holder.employerViewIcon.visibility = View.VISIBLE
                        holder.employerViewIcon.setBackgroundResource(R.drawable.ic_done_appliedadap)
                        holder.cancelBTN.visibility = View.GONE
                        holder.edit_SalaryIcon.visibility = View.GONE
                    }

                    if (appliedJobsLists?.get(position)?.status == "1") {
                        holder.employerInteractionIcon.visibility = View.VISIBLE
                        holder.employerInteractionIcon.setBackgroundResource(R.drawable.ic_not_contacted_appliedjobs_adap)
                    } else if (appliedJobsLists?.get(position)?.status == "2") {
                        holder.employerInteractionIcon.visibility = View.VISIBLE
                        holder.employerInteractionIcon.setBackgroundResource(R.drawable.ic_contacted_appliedjobs_adap)
                    } else if (appliedJobsLists?.get(position)?.status == "3") {
                        holder.employerInteractionIcon.visibility = View.VISIBLE
                        holder.employerInteractionIcon.setBackgroundResource(R.drawable.ic_hired_appliedjobs)
                    } else {
                        holder.employerInteractionIcon.visibility = View.GONE
                    }


                    holder.edit_SalaryIcon.setOnClickListener {
                        try {
                            //    //Log.d("huhu", "huhu")
                            var salary = ""
                            var canSubmit = false
                            val saveSearchDialog = Dialog(context)
                            saveSearchDialog.setContentView(R.layout.expected_salary_popup)
                            saveSearchDialog.setCancelable(true)
                            saveSearchDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

                            saveSearchDialog.show()
                            val scrollView = saveSearchDialog.findViewById(R.id.scroll) as ScrollView
                            val updateBTN = saveSearchDialog.findViewById(R.id.updateBTN) as Button
                            val updateAnyWayBTN = saveSearchDialog.findViewById(R.id.updateAnywayBTN) as Button
                            val cancelBTN = saveSearchDialog.findViewById(R.id.cancelBTN) as Button
                //                            val expected_salary_tv = saveSearchDialog?.findViewById(R.id.expected_salary_ET) as TextInputEditText
                            val expected_salary_til = saveSearchDialog.findViewById(R.id.monthly_tv) as TextInputLayout
                            val accountResult_tv = saveSearchDialog.findViewById(R.id.accountResult_tv) as TextView
                            val position_tv = saveSearchDialog.findViewById(R.id.position_tv) as TextView
                            val employer_tv = saveSearchDialog.findViewById(R.id.employer_tv) as TextView
                            val salaryLimitExceedTV = saveSearchDialog.findViewById(R.id.salary_limit_exceeded_tv) as TextView
                            val expected_salary_ET = saveSearchDialog.findViewById(R.id.expected_salary_ET) as TextInputEditText
                            position_tv.text = appliedJobsLists?.get(position)?.title
                            employer_tv.text = appliedJobsLists?.get(position)?.companyName
                            accountResult_tv.text = session.userName
                //                    updateBTN?.isEnabled = false
                            var expectedSalary = appliedJobsLists?.get(position)?.expectedSalary
                            expected_salary_ET.setText(expectedSalary.toString())
                            expected_salary_ET.setSelection(expected_salary_ET.text?.length!!)
                            expected_salary_ET.easyOnTextChangedListener {
                                updateBTN.isEnabled = expected_salary_ET.text?.length!! > 0

                            }

                            expected_salary_ET.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                                if (!hasFocus) {
                //                                    salaryLimitExceedTV?.hide()
                //                                    updateBTN?.show()
                //                                    updateAnyWayBTN?.hide()
                //                                    expected_salary_til.boxStrokeColor = ContextCompat.getColor(context, R.color.colorPrimary)
                //                                    scrollView?.post {
                //                                        scrollView.fullScroll(View.FOCUS_DOWN)
                //                                    }
                                } else {
                                    salaryLimitExceedTV.hide()
                                    updateBTN.show()
                                    updateAnyWayBTN.hide()
                                    expected_salary_til.boxStrokeColor = ContextCompat.getColor(context, R.color.colorPrimary)
                                    scrollView.post {
                                        scrollView.fullScroll(View.FOCUS_DOWN)
                                    }
                                }
                            }

                            cancelBTN.setOnClickListener {
                                try {
                                    saveSearchDialog.dismiss()
                                } catch (e: Exception) {
                                    logException(e)
                                }
                            }

                            updateAnyWayBTN.setOnClickListener {
                                val constraints = Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build()

                                val expectedSalaryJobData = workDataOf("userid" to session.userId!!, "decodeid" to session.decodId, "jobid" to appliedJobsLists!![position].jobId!!, "salary" to salary)
                                val expectedSalaryRequest = OneTimeWorkRequestBuilder<ExpectedSalaryWorker>().setInputData(expectedSalaryJobData).setConstraints(constraints).build()
                                WorkManager.getInstance(context).enqueue(expectedSalaryRequest)
                                // updateExpectedSalary(appliedJobsLists!![position].jobId!!,salary)
                                saveSearchDialog.dismiss()
                                appliedJobsLists?.get(position)?.expectedSalary = salary
                                notifyItemChanged(position)
                            }

                            updateBTN.setOnClickListener {
                                if (expected_salary_ET.length() != 0) {
                                    try {//update

                                        expected_salary_ET.clearFocus()

                                        salary = expected_salary_ET.getString()

                                        var minSalary = appliedJobsLists!![position].minSalary!!
                                        var maxSalary = appliedJobsLists!![position].maxSalary!!

                                        if (minSalary != "0" && maxSalary != "0") {
                                            if (salary.toInt() > maxSalary.toInt()) {
                                                //disableSalaryText(salaryTIET,salaryTIL,dialog)
                                                salaryLimitExceedTV.show()
                                                updateAnyWayBTN.show()
                                                expected_salary_ET.hideKeyboard()

                                                updateBTN.hide()
                //                                                    scrollView?.post {
                //                                                        scrollView.fullScroll(View.FOCUS_DOWN)
                //                                                    }
                                                expected_salary_ET.clearFocus()
                                                expected_salary_til.boxStrokeColor = Color.parseColor("#c0392b")
                                            } else {
                                                salaryLimitExceedTV.hide()
                                                updateAnyWayBTN.hide()
                                                updateBTN.show()
                                                canSubmit = true
                                                expected_salary_til.boxStrokeColor = ContextCompat.getColor(context, R.color.colorPrimary)
                                            }
                                        } else {
                                            if (maxSalary != "0" && minSalary == "0") {
                                                if (salary.toInt() > maxSalary.toInt()) {
                                                    salaryLimitExceedTV.show()
                                                    updateBTN.hide()
                                                    updateAnyWayBTN.show()
                                                    expected_salary_ET.hideKeyboard()

                                                    expected_salary_ET.clearFocus()
                                                    expected_salary_til.boxStrokeColor = Color.parseColor("#c0392b")
                //                                                        scrollView?.post {
                //                                                            scrollView.fullScroll(View.FOCUS_DOWN)
                //                                                        }
                                                } else {
                                                    salaryLimitExceedTV.hide()
                                                    updateBTN.show()
                                                    canSubmit = true

                                                    updateAnyWayBTN.hide()
                                                    expected_salary_til.boxStrokeColor = ContextCompat.getColor(context, R.color.colorPrimary)
                                                }
                                            } else if (maxSalary == "0" && minSalary != "0") {
                                                if (salary.toInt() > minSalary.toInt()) {
                                                    salaryLimitExceedTV.show()
                                                    updateBTN.hide()
                                                    updateAnyWayBTN.show()
                                                    expected_salary_ET.clearFocus()
                                                    expected_salary_ET.hideKeyboard()
                                                    expected_salary_til.boxStrokeColor = Color.parseColor("#c0392b")
                //                                                        scrollView?.post {
                //                                                            scrollView.fullScroll(View.FOCUS_DOWN)
                //                                                        }
                                                } else {
                                                    salaryLimitExceedTV.hide()
                                                    updateBTN.show()
                                                    canSubmit = true

                                                    updateAnyWayBTN.hide()
                                                    expected_salary_til.boxStrokeColor = ContextCompat.getColor(context, R.color.colorPrimary)
                                                }
                                            } else {
                                                Log.d("rakib", "came here")
                                                canSubmit = true
                                                salaryLimitExceedTV.hide()
                                                updateBTN.show()
                                                updateAnyWayBTN.hide()
                                            }
                                        }

                                        if (canSubmit) {

                                            //Log.d("popup", "popup-" + session.userId!! + "de-" + session.decodId!! + "jobid-" + appliedJobsLists!![position].jobId!! + "sal-" + salary)
                                            val constraints = Constraints.Builder()
                                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                                .build()
                                            val expectedSalaryJobData = workDataOf("userid" to session.userId!!, "decodeid" to session.decodId, "jobid" to appliedJobsLists!![position].jobId!!, "salary" to salary)
                                            val expectedSalaryRequest = OneTimeWorkRequestBuilder<ExpectedSalaryWorker>().setInputData(expectedSalaryJobData).setConstraints(constraints).build()
                                            WorkManager.getInstance(context).enqueue(expectedSalaryRequest)
                                            // updateExpectedSalary(appliedJobsLists!![position].jobId!!,salary)
                                            saveSearchDialog.dismiss()
                                            appliedJobsLists?.get(position)?.expectedSalary = salary
                                            notifyItemChanged(position)
                                        }

                                    } catch (e: Exception) {
                                        updateAnyWayBTN.hide()
                                        updateBTN.show()
                                        canSubmit = true
                                        salaryLimitExceedTV.hide()
                                        expected_salary_til.boxStrokeColor = ContextCompat.getColor(context, R.color.colorPrimary)

                                        logException(e)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            logException(e)
                        }


                    }
                    holder.cancelBTN.setOnClickListener {

                        activity.alert("Are you sure you want to cancel this job application?", "Confirmation") {
                            yesButton {
                                try {
                                    removeItem(position, holder.cancelBTN)
                                    Constants.appliedJobsCount--
                                    session.decrementJobsApplied()
                                } catch (e: Exception) {
                                    logException(e)
                                }
                            }
                            noButton { dialog ->
                                dialog.dismiss()
                            }
                        }.show()

                    }
                    holder.interviewBTN.setOnClickListener {
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
                    holder.interactionBTN.setOnClickListener {
                        try {
                            communicator.setFrom("employerInteraction")
                            communicator.setjobID(appliedJobsLists?.get(position)?.jobId!!)
                            communicator.gotoEmployerInteractionFragment()
                            communicator.setComapany(appliedJobsLists?.get(position)?.companyName!!)
                            communicator.setTitle(appliedJobsLists?.get(position)?.title!!)
                            communicator.setStatus(appliedJobsLists?.get(position)?.status!!)
                        } catch (e: Exception) {
                            logException(e)
                        }
                    }
                    holder.itemView.setOnClickListener {
                        try {
                            val jobids = ArrayList<String>()
                            val lns = ArrayList<String>()
                            val deadline = ArrayList<String>()
                            deadline.add(appliedJobsLists?.get(position)?.deadLine.toString())
                            jobids.add(appliedJobsLists?.get(position)?.jobId.toString())
                            lns.add(appliedJobsLists?.get(position)?.langType.toString())
                            communicator.setFrom("")
                            //Log.d("rakib deadline", "$deadline")
                            activity.startActivity<JobBaseActivity>("from" to "employer", "jobids" to jobids, "lns" to lns, "position" to 0, "deadline" to deadline)
                        } catch (e: Exception) {
                            logException(e)
                        }
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }

            ITEM_WITH_AD -> {
                try {
                    val holder = vHolder as AppliedjobsAdViewHolder
                    holder.CompanyName.text = appliedJobsLists?.get(position)?.companyName
                    holder.PositionName.text = appliedJobsLists?.get(position)?.title
                    try{
                        holder.appliedOn.text = SimpleDateFormat("M/d/yyyy").parse(appliedJobsLists?.get(position)?.appliedOn).toSimpleDateString()
                        holder.deadline.text = SimpleDateFormat("M/d/yyyy").parse(appliedJobsLists?.get(position)?.deadLine).toSimpleDateString()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                    holder.expectedSalary.text = appliedJobsLists?.get(position)?.expectedSalary

                    //Log.d("activity", appliedjobsActitivityLists?.toString())

                    if (appliedJobsLists?.get(position)?.isUserSeenInvitation == "0") {
                        holder.cardViewAppliedJobs.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FDFFF6")))
                    }

                    if (appliedJobsLists?.get(position)?.invitaion == "1") {
                        holder.interviewBTN.visibility = View.VISIBLE
                    } else if (appliedJobsLists?.get(position)?.invitaion == "0") {
                        holder.interviewBTN.visibility = View.GONE
                    }


                    if (appliedJobsLists?.get(position)?.viewedByEmployer == "No") {
                        // holder?.cancelBTN?.visibility = View.VISIBLE
                        holder.employerViewIcon.visibility = View.GONE
                        try {
                            val deadline = deadlinePattern.parse(appliedJobsLists?.get(position)?.deadLine)
                            val todayDate = todaysPattern.parse(todaysPattern.format(Date()))


                            if (deadline >= todayDate) {
                                holder.cancelBTN.visibility = View.VISIBLE
                                holder.edit_SalaryIcon.visibility = View.VISIBLE
                            } else if (deadline < todayDate) {
                                holder.cancelBTN.visibility = View.GONE
                                holder.edit_SalaryIcon.visibility = View.GONE
                            }


                        } catch (e: Exception) {
                            logException(e)
                            //Log.d("date", "e")

                        }
                    } else if (appliedJobsLists?.get(position)?.viewedByEmployer == "Yes" || appliedJobsLists?.get(position)?.status?.isNullOrEmpty()!!) {
                        holder.employerViewIcon.visibility = View.VISIBLE
                        holder.employerViewIcon.setBackgroundResource(R.drawable.ic_done_appliedadap)
                        holder.cancelBTN.visibility = View.GONE
                        holder.edit_SalaryIcon.visibility = View.GONE
                    }

                    if (appliedJobsLists?.get(position)?.status == "1") {
                        holder.employerInteractionIcon.visibility = View.VISIBLE
                        holder.employerInteractionIcon.setBackgroundResource(R.drawable.ic_not_contacted_appliedjobs_adap)
                    } else if (appliedJobsLists?.get(position)?.status == "2") {
                        holder.employerInteractionIcon.visibility = View.VISIBLE
                        holder.employerInteractionIcon.setBackgroundResource(R.drawable.ic_contacted_appliedjobs_adap)
                    } else if (appliedJobsLists?.get(position)?.status == "3") {
                        holder.employerInteractionIcon.visibility = View.VISIBLE
                        holder.employerInteractionIcon.setBackgroundResource(R.drawable.ic_hired_appliedjobs)
                    } else {
                        holder.employerInteractionIcon.visibility = View.GONE
                    }

                    holder.edit_SalaryIcon.setOnClickListener {
                        try {
                            Log.d("huhu", "huhu")
                            var salary = ""
                            var canSubmit = false
                            val saveSearchDialog = Dialog(context)
                            saveSearchDialog.setContentView(R.layout.expected_salary_popup)
                            saveSearchDialog.setCancelable(true)
                            saveSearchDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

                            saveSearchDialog.show()
                            val scrollView = saveSearchDialog.findViewById(R.id.scroll) as ScrollView
                            val updateBTN = saveSearchDialog.findViewById(R.id.updateBTN) as Button
                            val updateAnyWayBTN = saveSearchDialog.findViewById(R.id.updateAnywayBTN) as Button
                            val cancelBTN = saveSearchDialog.findViewById(R.id.cancelBTN) as Button
                //                            val expected_salary_tv = saveSearchDialog?.findViewById(R.id.expected_salary_ET) as TextInputEditText
                            val expected_salary_til = saveSearchDialog.findViewById(R.id.monthly_tv) as TextInputLayout
                            val accountResult_tv = saveSearchDialog.findViewById(R.id.accountResult_tv) as TextView
                            val position_tv = saveSearchDialog.findViewById(R.id.position_tv) as TextView
                            val employer_tv = saveSearchDialog.findViewById(R.id.employer_tv) as TextView
                            val salaryLimitExceedTV = saveSearchDialog.findViewById(R.id.salary_limit_exceeded_tv) as TextView
                            val expected_salary_ET = saveSearchDialog.findViewById(R.id.expected_salary_ET) as TextInputEditText
                            position_tv.text = appliedJobsLists?.get(position)?.title
                            employer_tv.text = appliedJobsLists?.get(position)?.companyName
                            accountResult_tv.text = session.userName
                //                    updateBTN?.isEnabled = false
                            var expectedSalary = appliedJobsLists?.get(position)?.expectedSalary
                            expected_salary_ET.setText(expectedSalary.toString())
                            expected_salary_ET.setSelection(expected_salary_ET.text?.length!!)
                            expected_salary_ET.easyOnTextChangedListener {
                                updateBTN.isEnabled = expected_salary_ET.text?.length!! > 0

                            }

                            expected_salary_ET.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                                if (!hasFocus) {
                //                                    salaryLimitExceedTV?.hide()
                //                                    updateBTN?.show()
                //                                    updateAnyWayBTN?.hide()
                //                                    expected_salary_til.boxStrokeColor = ContextCompat.getColor(context, R.color.colorPrimary)
                //                                    scrollView?.post {
                //                                        scrollView.fullScroll(View.FOCUS_DOWN)
                //                                    }
                                } else {
                                    salaryLimitExceedTV.hide()
                                    updateBTN.show()
                                    updateAnyWayBTN.hide()
                                    expected_salary_til.boxStrokeColor = ContextCompat.getColor(context, R.color.colorPrimary)
                                    scrollView.post {
                                        scrollView.fullScroll(View.FOCUS_DOWN)
                                    }
                                }
                            }

                            cancelBTN.setOnClickListener {
                                try {
                                    saveSearchDialog.dismiss()
                                } catch (e: Exception) {
                                    logException(e)
                                }
                            }

                            updateAnyWayBTN.setOnClickListener {
                                val constraints = Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build()

                                val expectedSalaryJobData = workDataOf("userid" to session.userId!!, "decodeid" to session.decodId, "jobid" to appliedJobsLists!![position].jobId!!, "salary" to salary)
                                val expectedSalaryRequest = OneTimeWorkRequestBuilder<ExpectedSalaryWorker>().setInputData(expectedSalaryJobData).setConstraints(constraints).build()
                                WorkManager.getInstance(context).enqueue(expectedSalaryRequest)
                                // updateExpectedSalary(appliedJobsLists!![position].jobId!!,salary)
                                saveSearchDialog.dismiss()
                                appliedJobsLists?.get(position)?.expectedSalary = salary
                                notifyItemChanged(position)
                            }

                            updateBTN.setOnClickListener {
                                if (expected_salary_ET.length() != 0) {
                                    try {//update

                                        expected_salary_ET.clearFocus()

                                        salary = expected_salary_ET.getString()

                                        var minSalary = appliedJobsLists!![position].minSalary!!
                                        var maxSalary = appliedJobsLists!![position].maxSalary!!

                                        if (minSalary != "0" && maxSalary != "0") {
                                            if (salary.toInt() > maxSalary.toInt()) {
                                                //disableSalaryText(salaryTIET,salaryTIL,dialog)
                                                salaryLimitExceedTV.show()
                                                updateAnyWayBTN.show()
                                                expected_salary_ET.hideKeyboard()

                                                updateBTN.hide()
                //                                                    scrollView?.post {
                //                                                        scrollView.fullScroll(View.FOCUS_DOWN)
                //                                                    }
                                                expected_salary_ET.clearFocus()
                                                expected_salary_til.boxStrokeColor = Color.parseColor("#c0392b")
                                            } else {
                                                salaryLimitExceedTV.hide()
                                                updateAnyWayBTN.hide()
                                                updateBTN.show()
                                                canSubmit = true
                                                expected_salary_til.boxStrokeColor = ContextCompat.getColor(context, R.color.colorPrimary)
                                            }
                                        } else {
                                            if (maxSalary != "0" && minSalary == "0") {
                                                if (salary.toInt() > maxSalary.toInt()) {
                                                    salaryLimitExceedTV.show()
                                                    updateBTN.hide()
                                                    updateAnyWayBTN.show()
                                                    expected_salary_ET.hideKeyboard()

                                                    expected_salary_ET.clearFocus()
                                                    expected_salary_til.boxStrokeColor = Color.parseColor("#c0392b")
                //                                                        scrollView?.post {
                //                                                            scrollView.fullScroll(View.FOCUS_DOWN)
                //                                                        }
                                                } else {
                                                    salaryLimitExceedTV.hide()
                                                    updateBTN.show()
                                                    canSubmit = true

                                                    updateAnyWayBTN.hide()
                                                    expected_salary_til.boxStrokeColor = ContextCompat.getColor(context, R.color.colorPrimary)
                                                }
                                            } else if (maxSalary == "0" && minSalary != "0") {
                                                if (salary.toInt() > minSalary.toInt()) {
                                                    salaryLimitExceedTV.show()
                                                    updateBTN.hide()
                                                    updateAnyWayBTN.show()
                                                    expected_salary_ET.clearFocus()
                                                    expected_salary_ET.hideKeyboard()
                                                    expected_salary_til.boxStrokeColor = Color.parseColor("#c0392b")
                //                                                        scrollView?.post {
                //                                                            scrollView.fullScroll(View.FOCUS_DOWN)
                //                                                        }
                                                } else {
                                                    salaryLimitExceedTV.hide()
                                                    updateBTN.show()
                                                    canSubmit = true

                                                    updateAnyWayBTN.hide()
                                                    expected_salary_til.boxStrokeColor = ContextCompat.getColor(context, R.color.colorPrimary)
                                                }
                                            } else {
                                                salaryLimitExceedTV.hide()
                                                updateBTN.show()
                                                canSubmit = true

                                                updateAnyWayBTN.hide()
                                                expected_salary_til.boxStrokeColor = ContextCompat.getColor(context, R.color.colorPrimary)
                                            }
                                        }

                                        if (canSubmit) {

                                            //Log.d("popup", "popup-" + session.userId!! + "de-" + session.decodId!! + "jobid-" + appliedJobsLists!![position].jobId!! + "sal-" + salary)
                                            val constraints = Constraints.Builder()
                                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                                .build()
                                            val expectedSalaryJobData = workDataOf("userid" to session.userId!!, "decodeid" to session.decodId, "jobid" to appliedJobsLists!![position].jobId!!, "salary" to salary)
                                            val expectedSalaryRequest = OneTimeWorkRequestBuilder<ExpectedSalaryWorker>().setInputData(expectedSalaryJobData).setConstraints(constraints).build()
                                            WorkManager.getInstance(context).enqueue(expectedSalaryRequest)
                                            // updateExpectedSalary(appliedJobsLists!![position].jobId!!,salary)
                                            saveSearchDialog.dismiss()
                                            appliedJobsLists?.get(position)?.expectedSalary = salary
                                            notifyItemChanged(position)
                                        }

                                    } catch (e: Exception) {
                                        updateAnyWayBTN.hide()
                                        updateBTN.show()
                                        canSubmit = true
                                        salaryLimitExceedTV.hide()
                                        expected_salary_til.boxStrokeColor = ContextCompat.getColor(context, R.color.colorPrimary)

                                        logException(e)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            logException(e)
                        }


                    }

                    holder.cancelBTN.setOnClickListener {

                        activity.alert("Are you sure you want to cancel this job application?", "Confirmation") {
                            yesButton {
                                try {

                                    removeItem(position, holder.cancelBTN)
                                    Constants.appliedJobsCount--
                                    session.decrementJobsApplied()
                                } catch (e: Exception) {
                                    logException(e)
                                }
                            }
                            noButton { dialog ->
                                dialog.dismiss()
                            }
                        }.show()

                    }
                    holder.interviewBTN.setOnClickListener {
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
                    holder.interactionBTN.setOnClickListener {
                        try {
                            communicator.setFrom("employerInteraction")
                            communicator.setjobID(appliedJobsLists?.get(position)?.jobId!!)
                            communicator.gotoEmployerInteractionFragment()
                            communicator.setComapany(appliedJobsLists?.get(position)?.companyName!!)
                            communicator.setTitle(appliedJobsLists?.get(position)?.title!!)
                            communicator.setStatus(appliedJobsLists?.get(position)?.status!!)
                        } catch (e: Exception) {
                            logException(e)
                        }
                    }
                    holder.itemView.setOnClickListener {
                        try {
                            val jobids = ArrayList<String>()
                            val lns = ArrayList<String>()
                            val deadline = ArrayList<String>()
                            deadline.add(appliedJobsLists?.get(position)?.deadLine.toString())
                            jobids.add(appliedJobsLists?.get(position)?.jobId.toString())
                //                            lns.add("0")
                            lns.add(appliedJobsLists?.get(position)?.langType.toString())
                            communicator.setFrom("")
                            activity.startActivity<JobBaseActivity>("from" to "employer", "jobids" to jobids, "lns" to lns, "position" to 0, "deadline" to deadline)
                        } catch (e: Exception) {
                            logException(e)
                        }
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        }


    }

    fun removeItem(position: Int, view: View) {
        try {
            if (appliedJobsLists?.size != 0) {

                Log.d("rakib", "${appliedJobsLists?.get(position)?.title} ${appliedJobsLists?.get(position)?.status}")

                when (appliedJobsLists?.get(position)?.status) {
                    "3" -> Constants.totalHired--
                    "2" -> Constants.totalContacted--
                    "1" -> Constants.totalNotContacted--
                }

                val deletedItem = appliedJobsLists?.get(position)
                val jobid = appliedJobsLists?.get(position)?.jobId
                val companyName = appliedJobsLists?.get(position)?.companyName
                //Log.d("werywirye", "jobid = $jobid companyname = $companyName")
                appliedJobsLists?.removeAt(position)
                notifyItemRemoved(position)
                notifyDataSetChanged()
                try {

                    val constraints = Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()

                    val appliedJobData = workDataOf("userid" to session.userId!!, "decodeid" to session.decodId, "jobid" to jobid)
                    val cancelAppliedJobWorkRequest = OneTimeWorkRequestBuilder<CancelAppliedJobWorker>().setInputData(appliedJobData).setConstraints(constraints).build()
//                    val deleteJobID = CancelAppliedJob.scheduleAdvancedJob(session.userId!!, session.decodId!!, jobid!!)
                    // undoRemove(view, deletedItem, position, deleteJobID)
                    WorkManager.getInstance(context).enqueue(cancelAppliedJobWorkRequest)
                    communicator.decrementCounter()
                    communicator.incrementAvailableJobCounter()
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
                        //                        CancelAppliedJob.cancelJob(deleteJobID)
                        restoreMe(deletedItem!!, deletedIndex)
                        //Log.d("jobiiii", "undo = deleted = ${deletedItem} index = ${deletedIndex}")
                        communicator.scrollToUndoPosition(deletedIndex)
                        //Log.d("comid", "comid")
                    }

            snack.show()
            //Log.d("swipe", "dir to LEFT")
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

        if (position % 3 == 0 && position != 0) {
            return ITEM_WITH_AD
        } else {
            return if (position == appliedJobsLists!!.size - 1 && isLoadingAdded) LOADING else ITEM
        }


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
        add(AppliedJobModelData())
    }


    private fun getItem(position: Int): AppliedJobModelData? {
        return appliedJobsLists!![position]
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = appliedJobsLists!!.size - 1
        val result = getItem(position)
        //notifyItemRemoved(position)

        if (result?.jobId?.isNullOrBlank()!!) {
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
    val employerInteractionIcon = view.findViewById(R.id.employerInteraction_icon) as ImageView
    val interactionBTN = view.findViewById(R.id.interactionBTN) as MaterialButton
    val interviewBTN = view.findViewById(R.id.interviewInvitationBTN) as MaterialButton
    val cancelBTN = view.findViewById(R.id.CancelBTN) as MaterialButton
    val cardViewAppliedJobs = view.findViewById(R.id.cardView) as CardView
    val edit_SalaryIcon = view.findViewById(R.id.edit_SalaryIcon) as ImageView
}

class AppliedjobsAdViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val appliedOn = view.findViewById(R.id.appliedon_tv) as TextView
    val expectedSalary = view.findViewById(R.id.exSalary_tv) as TextView
    val deadline = view.findViewById(R.id.deadline_tv) as TextView
    val PositionName = view.findViewById(R.id.textViewPositionName) as TextView
    val CompanyName = view.findViewById(R.id.textViewCompanyName) as TextView
    val employerViewIcon = view.findViewById(R.id.employerView_icon) as ImageView
    val employerInteractionIcon = view.findViewById(R.id.employerInteraction_icon) as ImageView
    val interactionBTN = view.findViewById(R.id.interactionBTN) as MaterialButton
    val interviewBTN = view.findViewById(R.id.interviewInvitationBTN) as MaterialButton
    val cancelBTN = view.findViewById(R.id.CancelBTN) as MaterialButton
    val cardViewAppliedJobs = view.findViewById(R.id.cardView) as CardView
    val edit_SalaryIcon = view.findViewById(R.id.edit_SalaryIcon) as ImageView
    val ad_small_template: TemplateView = view.findViewById(R.id.ad_small_template) as TemplateView
}

class LoadingVH(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView),
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