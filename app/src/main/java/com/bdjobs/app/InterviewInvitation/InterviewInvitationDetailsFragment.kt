package com.bdjobs.app.InterviewInvitation

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.InvitationDetailModels
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_interview_invitation_details.*
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InterviewInvitationDetailsFragment : Fragment() {

    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB
    lateinit var interviewInvitationCommunicator: InterviewInvitationCommunicator


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
    }

    override fun onResume() {
        super.onResume()


        companyNameTV?.text = interviewInvitationCommunicator.getCompanyNm()
        jobtitleTV?.text = interviewInvitationCommunicator.getCompanyJobTitle()


        jobDetailTv?.setOnClickListener {
            val jobids = ArrayList<String>()
            val lns = ArrayList<String>()
            jobids.add(interviewInvitationCommunicator.getCompanyJobID())
            lns.add("0")
            startActivity<JobBaseActivity>("from" to "employer", "jobids" to jobids, "lns" to lns, "position" to 0)
        }

        getDetailsFromServer()


    }

    private fun getDetailsFromServer() {
        constraintLayout3.hide()
        followedRV?.hide()
        shimmer_view_container_JobList?.show()
        shimmer_view_container_JobList?.startShimmerAnimation()
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
                        constraintLayout3.show()
                        followedRV?.show()
                        shimmer_view_container_JobList?.hide()
                        shimmer_view_container_JobList?.stopShimmerAnimation()

                        Log.d("sdofjwioapfgh", "res: ${response.body()?.message}")
                        if (response.body()?.statuscode == Constants.api_request_result_code_ok) {
                            appliedDateTV.text = "Applied on: ${response.body()!!.common.applyDate}"
                            val interviewInvitationDetailsAdapter = InterviewInvitationDetailsAdapter(activity, response?.body()?.data!!)
                            followedRV?.setHasFixedSize(true)
                            followedRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                            followedRV?.adapter = interviewInvitationDetailsAdapter
                        }

                    }
                }
        )

    }
}