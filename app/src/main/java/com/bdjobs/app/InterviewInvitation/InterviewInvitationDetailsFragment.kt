package com.bdjobs.app.InterviewInvitation

import android.app.Dialog
import android.app.Fragment
import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.InvitationDetailModels
import com.bdjobs.app.API.ModelClasses.InvitationDetailModelsCommon
import com.bdjobs.app.API.ModelClasses.InvitationDetailModelsData
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_interview_invitation_details.*
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InterviewInvitationDetailsFragment : Fragment() {

    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB
    lateinit var interviewInvitationCommunicator: InterviewInvitationCommunicator
    var applyID = ""
    var invitationID = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_interview_invitation_details, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsUserSession = BdjobsUserSession(activity)
        bdjobsDB = BdjobsDB.getInstance(activity)
        interviewInvitationCommunicator = activity as InterviewInvitationCommunicator
        backIMV?.setOnClickListener {
            interviewInvitationCommunicator.backButtonClicked()
        }
        companyNameTV?.text = interviewInvitationCommunicator.getCompanyNm()
        jobtitleTV?.text = interviewInvitationCommunicator.getCompanyJobTitle()
        getDetailsFromServer()
        onClickListeners()
    }


    private fun onClickListeners() {
        jobDetailTv?.setOnClickListener {
            val jobids = ArrayList<String>()
            val lns = ArrayList<String>()
            val deadline = ArrayList<String>()
            jobids.add(interviewInvitationCommunicator.getCompanyJobID())
            lns.add("0")
            deadline.add("")
            startActivity<JobBaseActivity>("from" to "employer", "jobids" to jobids, "lns" to lns, "position" to 0, "deadline" to deadline)
        }
        noBTN?.setOnClickListener { showCancelPopUp() }
        ratingBTN?.setOnClickListener { showRatingPopUp() }
        rescheduleRequestTV?.setOnClickListener { showReschedulePopUp() }
        yesBTN?.setOnClickListener {
            sendInterviewConfirmation(
                    applyID = applyID,
                    userActivity = "3",
                    invitationID = invitationID
            )
        }
    }

    private fun getDetailsFromServer() {
        messageHolderLL.hide()
        constraintLayout3.hide()
        followedRV?.hide()
        shimmer_view_container_JobList?.show()
        shimmer_view_container_JobList?.startShimmerAnimation()
        /*Log.d("sdofjwioapfgh", "userId= ${bdjobsUserSession.userId!!}" +
                "decodId= ${bdjobsUserSession.decodId!!}" +
                "CompanyJobID= ${interviewInvitationCommunicator.getCompanyJobID()}")*/

        ApiServiceMyBdjobs.create().getIterviewInvitationDetails(
                userID = bdjobsUserSession.userId!!,
                decodeID = bdjobsUserSession.decodId!!,
                jobId = interviewInvitationCommunicator.getCompanyJobID()

        ).enqueue(
                object : Callback<InvitationDetailModels> {
                    override fun onFailure(call: Call<InvitationDetailModels>, t: Throwable) {
                        error("onFailure", t)
                        followedRV?.show()
                        shimmer_view_container_JobList?.hide()
                        shimmer_view_container_JobList?.stopShimmerAnimation()
                    }

                    override fun onResponse(call: Call<InvitationDetailModels>, response: Response<InvitationDetailModels>) {
                        try {
                            constraintLayout3?.show()
                            followedRV?.show()
                            shimmer_view_container_JobList?.hide()
                            shimmer_view_container_JobList?.stopShimmerAnimation()

                            //Log.d("sdofjwioapfgh", "res: ${response.body()}")
                            if (response.body()?.statuscode == Constants.api_request_result_code_ok) {
                                appliedDateTV.text = "Applied on: ${response.body()!!.common?.applyDate}"
                                val interviewInvitationDetailsAdapter = InterviewInvitationDetailsAdapter(activity, (response.body()?.data!!))
                                followedRV?.setHasFixedSize(true)
                                followedRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                                followedRV?.adapter = interviewInvitationDetailsAdapter
                                showStickyPopUp(response.body()?.data?.get(0)!!, response.body()?.common!!)
                            }
                        } catch (e: Exception) {
                            logException(e)
                        }
                    }
                }
        )
    }


    private fun showStickyPopUp(latestJobInviteDetails: InvitationDetailModelsData, common: InvitationDetailModelsCommon) {
        messageHolderLL.show()
        val interviewDate = latestJobInviteDetails.examDate
        val interviewTime = latestJobInviteDetails.examTime
        val message = "Do you want to present in the interview on $interviewDate, at $interviewTime in ${interviewInvitationCommunicator.getCompanyNm()}?"
        val companyRating = common.rating
        val ratingMessage = common.ratingMessage
        val ratingDate = common.ratingDate
        val jobClosed = common.jobClosed

        applyID = common.applyId!!
        invitationID = latestJobInviteDetails.invitationId!!

        //Log.d("invitationID", "jobClosed: $jobClosed\napplyID: $applyID")

        if (jobClosed.equals("True", ignoreCase = true)) {
            if (TextUtils.isEmpty(companyRating)) {
                ratingInputRL.visibility = View.VISIBLE
                ratingShowRL.visibility = View.GONE
            } else {
                ratingInputRL.visibility = View.GONE
                ratingShowRL.visibility = View.VISIBLE
                var rating : Float = 0f
                try {
                    rating = java.lang.Float.parseFloat(companyRating!!)
                } catch (e: Exception) {
                }
                companyRatingBar.rating = rating
                ratingMsgTV.text = ratingMessage
                ratingDateTV.text = ratingDate
            }
        } else {
            if (latestJobInviteDetails.confimationStatus?.equalIgnoreCase("0")!!) {
                questionRL.visibility = View.VISIBLE
                messageTV.text = message
                rescheduleRequestTV.visibility = View.VISIBLE
            } else if (latestJobInviteDetails.confimationStatus.equalIgnoreCase("5")) {
                questionRL.visibility = View.VISIBLE
                messageTV.text = message
                rescheduleRequestTV.visibility = View.GONE
            } else {
                questionRL.visibility = View.GONE
            }

            if (common.showUndo?.equalIgnoreCase("1")!!) {
                val snack = Snackbar.make(invDetailsRoot, "Your request has been sent to the employer.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("UNDO") {
                            sendInterviewConfirmation(
                                    applyID = applyID,
                                    userActivity = "6",
                                    invitationID = invitationID
                            )
                        }

                snack.show()
            }
        }
    }

    private fun showReschedulePopUp() {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.job_invitaion_reschedule_pop_up_layout)
        val closeIMGV = dialog.findViewById(R.id.closeIMGV) as ImageView
        val otherReasonET = dialog.findViewById(R.id.otherReasonET) as EditText
        val submitTV = dialog.findViewById(R.id.submitTV) as Button

        closeIMGV.setOnClickListener {
            try {
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        submitTV.setOnClickListener {
            val reason = otherReasonET.text.toString()
            when {
                TextUtils.isEmpty(reason) -> Toast.makeText(activity, "Please write your reason for rescheduling.", Toast.LENGTH_SHORT).show()
                reason.trim { it <= ' ' }.isEmpty() -> Toast.makeText(activity, "Please write your reason for rescheduling.", Toast.LENGTH_SHORT).show()
                else -> {
                    dialog.dismiss()
                    sendInterviewConfirmation(
                            applyID = applyID,
                            userActivity = "4",
                            invitationID = invitationID,
                            rescheduleComment = reason
                    )
                }
            }
        }
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun showRatingPopUp() {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.company_rating_pop_up_layout)

        val closeIMGV = dialog.findViewById(R.id.closeIMGV) as ImageView
        val companyRatingBar = dialog.findViewById<View>(R.id.companyRatingBar) as RatingBar
        val otherReasonET = dialog.findViewById(R.id.otherReasonET) as EditText
        val submitBTN = dialog.findViewById(R.id.submitTV) as Button

        closeIMGV.setOnClickListener {
            try {
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        submitBTN.setOnClickListener {
            val rating = companyRatingBar.rating.toString()
            val ratingMessage = otherReasonET.text.toString()
            if (TextUtils.isEmpty(rating)) {
                Toast.makeText(activity, "Please give a rating", Toast.LENGTH_SHORT).show()
            } else if (ratingMessage.trim { it <= ' ' }.isEmpty()) {

                Toast.makeText(activity, "Please give some feedback", Toast.LENGTH_SHORT).show()

            } else {
                //post request
                dialog.dismiss()
                val progressDialog = ProgressDialog(activity)
                progressDialog.setMessage("Please wait")
                progressDialog.setCancelable(false)
                progressDialog.show()

                ApiServiceMyBdjobs.create().sendCompanyRating(
                        userID = bdjobsUserSession.userId!!,
                        decodeID = bdjobsUserSession.decodId!!,
                        jobId = interviewInvitationCommunicator.getCompanyJobID(),
                        ratting = rating,
                        rattingComment = ratingMessage
                ).enqueue(object : Callback<InvitationDetailModels> {
                    override fun onFailure(call: Call<InvitationDetailModels>, t: Throwable) {
                        error("onFailure", t)
                    }

                    override fun onResponse(call: Call<InvitationDetailModels>, response: Response<InvitationDetailModels>) {
                        try {
                            progressDialog.dismiss()
                            if (response.isSuccessful) {
                                if (response.body()!!.statuscode?.equalIgnoreCase("4")!!) {
                                    Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                                    getDetailsFromServer()
                                } else {
                                    Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                })
            }
        }

        dialog.setCancelable(true)
        dialog.show()

    }

    private fun showCancelPopUp() {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.job_invitaion_cancel_pop_up_layout)

        val reasonRG = dialog.findViewById(R.id.reasonRG) as RadioGroup
        val closeIMGV = dialog.findViewById(R.id.closeIMGV) as ImageView
        val otherReasonET = dialog.findViewById(R.id.otherReasonET) as EditText
        val submitBTN = dialog.findViewById(R.id.submitTV) as Button


        closeIMGV.setOnClickListener {
            try {
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        reasonRG.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.reason1RB) {
                otherReasonET.visibility = View.GONE
            } else if (checkedId == R.id.reason2RB) {
                otherReasonET.visibility = View.GONE
            } else if (checkedId == R.id.reason3RB) {
                otherReasonET.visibility = View.GONE
            } else if (checkedId == R.id.reason4RB) {
                otherReasonET.visibility = View.VISIBLE
            }
        }


        submitBTN.setOnClickListener {
            var otherReason = ""
            var selectedReason = ""
            val checkedId = reasonRG.checkedRadioButtonId
            if (checkedId == R.id.reason1RB) {
                selectedReason = "1"
            } else if (checkedId == R.id.reason2RB) {
                selectedReason = "2"
            } else if (checkedId == R.id.reason3RB) {
                selectedReason = "3"
            } else if (checkedId == R.id.reason4RB) {
                selectedReason = "4"
                otherReason = otherReasonET.text.toString()
            }

            if (TextUtils.isEmpty(selectedReason)) {
                Toast.makeText(activity, "Please select an option from the list.", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(otherReason) && selectedReason.equals("4", ignoreCase = true)) {
                Toast.makeText(activity, "Please write your reason briefly.", Toast.LENGTH_SHORT).show()
            } else if (selectedReason.equals("4", ignoreCase = true) && otherReason.trim { it <= ' ' }.length == 0) {
                Toast.makeText(activity, "Please write your reason briefly.", Toast.LENGTH_SHORT).show()
            } else {
                dialog.dismiss()
                sendInterviewConfirmation(
                        applyID = applyID,
                        userActivity = "2",
                        selectedReason = selectedReason,
                        otherReason = otherReason,
                        invitationID = invitationID
                )
            }
        }

        dialog.setCancelable(true)
        dialog.show()
    }


    private fun sendInterviewConfirmation(applyID: String, userActivity: String, selectedReason: String = "", otherReason: String = "", invitationID: String, rescheduleComment: String = "") {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Please wait")
        progressDialog.setCancelable(false)
        progressDialog.show()

        ApiServiceMyBdjobs.create().sendInterviewConfirmation(
                userID = bdjobsUserSession.userId!!,
                decodeID = bdjobsUserSession.decodId!!,
                applyId = applyID,
                activity = userActivity,
                invitationId = invitationID,
                cancleReason = selectedReason,
                otherComment = otherReason,
                rescheduleComment = rescheduleComment
        ).enqueue(object : Callback<InvitationDetailModels> {
            override fun onFailure(call: Call<InvitationDetailModels>, t: Throwable) {
                error("onFailure", t)
                try {
                    progressDialog.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onResponse(call: Call<InvitationDetailModels>, response: Response<InvitationDetailModels>) {
                try {
                    progressDialog.dismiss()
                    if (response.isSuccessful) {
                        if (response.body()!!.statuscode?.equalIgnoreCase("4")!!) {
                            Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                            getDetailsFromServer()
                        } else {
                            Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

        })
    }


}