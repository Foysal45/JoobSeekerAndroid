package com.bdjobs.app.videoInterview.ui.interview_details

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.VideoInterviewDetailsModel
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.InterviewInvitation.InterviewInvitationCommunicator
import com.bdjobs.app.Jobs.JobBaseActivity

import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import kotlinx.android.synthetic.main.fragment_video_interview_details.*
import kotlinx.android.synthetic.main.job_video_invitaion_details_item.*
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class VideoInterviewDetailsFragment : androidx.fragment.app.Fragment() {

    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB
    lateinit var interviewInvitationCommunicator: InterviewInvitationCommunicator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        interviewInvitationCommunicator = activity as InterviewInvitationCommunicator
        bdjobsDB = BdjobsDB.getInstance(activity as Activity)
        bdjobsUserSession = BdjobsUserSession(activity as Activity)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_interview_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClicks()
        getVideoInterviewDetails()
    }

    private fun setupClicks() {

        backIMV?.setOnClickListener {
            interviewInvitationCommunicator.backButtonClicked()
        }

        jobDetailTv?.setOnClickListener {
            val jobids = ArrayList<String>()
            val lns = ArrayList<String>()
            val deadline = ArrayList<String>()
            jobids.add(interviewInvitationCommunicator.getCompanyJobID())
            lns.add("0")
            deadline.add("")
            context?.startActivity<JobBaseActivity>("from" to "employer", "jobids" to jobids, "lns" to lns, "position" to 0, "deadline" to deadline)
        }
    }

    private fun getVideoInterviewDetails() {

        shimmer_view_container_JobList?.show()
        shimmer_view_container_JobList?.startShimmer()
        constraintLayout3?.hide()
        details_cl?.hide()

        ApiServiceMyBdjobs.create().getVideoInterviewDetails(
                userID = bdjobsUserSession.userId!!,
                decodeID = bdjobsUserSession.decodId!!,
                jobId = interviewInvitationCommunicator.getCompanyJobID()

        ).enqueue(
                object : Callback<VideoInterviewDetailsModel> {
                    override fun onFailure(call: Call<VideoInterviewDetailsModel>, t: Throwable) {
                        //error("onFailure", t)
//                        followedRV?.show()
//                        shimmer_view_container_JobList?.hide()
//                        shimmer_view_container_JobList?.stopShimmer()
                    }

                    override fun onResponse(call: Call<VideoInterviewDetailsModel>, response: Response<VideoInterviewDetailsModel>) {
                        try {
                            if (response.body()?.statuscode == Constants.api_request_result_code_ok) {

                                shimmer_view_container_JobList?.stopShimmer()
                                shimmer_view_container_JobList?.hide()
                                constraintLayout3?.show()
                                details_cl?.show()

                                jobtitleTV?.text = interviewInvitationCommunicator.getCompanyJobTitle()
                                companyNameTV?.text = response.body()!!.common?.companyName
                                appliedDateTV?.text = "Applied on: " + response.body()!!.common?.applyDate

                                video_invitation_body_text?.text = response.body()!!.data!![0]?.examMessage
                                invitation_date_text?.text = response.body()!!.data!![0]?.vInvitationDate
                                deadline_date_text?.text = response.body()!!.data!![0]?.vInvitationDeadline
                                total_time_text?.text = response.body()!!.data!![0]?.vTotalDuration + " sec"
                                questions_text?.text = response.body()!!.data!![0]?.vTotalQuestion
                                attempts_text?.text = response.body()!!.data!![0]?.vTotalAttempt

                                if (response.body()!!.data!![0]?.vStatus.isNullOrEmpty()) {
                                    status_text_view?.hide()
                                } else {
                                    status_text_view?.show()
                                }

                                if (response.body()!!.data!![0]?.vButtonText.isNullOrEmpty()) {
                                    submit_button?.hide()
                                } else {
                                    submit_button?.show()
                                    submit_button?.text = response.body()!!.data!![0]?.vButtonText
                                    submit_button?.setOnClickListener {
//                                    context?.startActivity<WebActivity>(
//                                            "from" to "videoInterview",
//                                            "url" to interviewInvitationCommunicator.getVideoUrl()
//                                    )
                                        context?.openUrlInBrowser(interviewInvitationCommunicator.getVideoUrl())
                                    }
                                }
                            }
//                            constraintLayout3?.show()
//                            followedRV?.show()
//                            shimmer_view_container_JobList?.hide()
//                            shimmer_view_container_JobList?.stopShimmer()
//
//                            //Log.d("sdofjwioapfgh", "res: ${response.body()}")
//                            if (response.body()?.statuscode == Constants.api_request_result_code_ok) {
//                                appliedDateTV.text = "Applied on: ${response.body()!!.common?.applyDate}"
//                                val interviewInvitationDetailsAdapter = InterviewInvitationDetailsAdapter(activity, (response.body()?.data!!))
//                                followedRV?.setHasFixedSize(true)
//                                followedRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
//                                followedRV?.adapter = interviewInvitationDetailsAdapter
//                                showStickyPopUp(response.body()?.data?.get(0)!!, response.body()?.common!!)
//                            }
                        } catch (e: Exception) {
                            logException(e)
                        }
                    }
                }
        )
    }

}
