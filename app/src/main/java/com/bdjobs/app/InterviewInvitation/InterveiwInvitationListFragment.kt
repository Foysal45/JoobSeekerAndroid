package com.bdjobs.app.InterviewInvitation

import android.app.Fragment
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.JobInvitationListModel
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.JobInvitation
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants.Companion.api_request_result_code_ok
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_interview_invitation_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InterveiwInvitationListFragment : Fragment() {

    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB
    lateinit var interviewInvitationCommunicator: InterviewInvitationCommunicator


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_interview_invitation_list, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsUserSession = BdjobsUserSession(activity)
        bdjobsDB = BdjobsDB.getInstance(activity)
        interviewInvitationCommunicator = activity as InterviewInvitationCommunicator
        backIMV.setOnClickListener {
            interviewInvitationCommunicator.backButtonClicked()
        }
    }

    override fun onResume() {
        super.onResume()

        if (interviewInvitationCommunicator.getFrom().equalIgnoreCase("popup")) {
            followedRV.hide()
            favCountTV.hide()
            shimmer_view_container_JobList.show()
            shimmer_view_container_JobList.startShimmerAnimation()

            showDataFromServer()

        } else {
            //homePage
            followedRV.hide()
            favCountTV.hide()
            shimmer_view_container_JobList.show()
            shimmer_view_container_JobList.startShimmerAnimation()

            showDataFromDB()

        }

        Log.d("from", "from: ${interviewInvitationCommunicator.getFrom()}")
    }

    private fun showDataFromServer() {
        ApiServiceMyBdjobs.create().getJobInvitationList(userId = bdjobsUserSession.userId, decodeId = bdjobsUserSession.decodId).enqueue(object : Callback<JobInvitationListModel> {
            override fun onFailure(call: Call<JobInvitationListModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<JobInvitationListModel>, response: Response<JobInvitationListModel>) {
                response.body()?.statuscode?.let { status ->
                    if (status == api_request_result_code_ok) {
                        response.body()?.data?.let { items ->
                            doAsync {
                                for (item in items) {
                                    val jobInvitation = JobInvitation(companyName = item?.companyName,
                                            inviteDate = item?.inviteDate,
                                            jobId = item?.jobId,
                                            jobTitle = item?.jobTitle,
                                            seen = item?.seen)
                                    bdjobsDB.jobInvitationDao().insertJobInvitation(jobInvitation)
                                }
                                uiThread {
                                    showDataFromDB()
                                }
                            }
                        }
                    }
                }
            }

        })
    }

    private fun showDataFromDB() {
        doAsync {
            val interviewInvitations = bdjobsDB.jobInvitationDao().getAllJobInvitation()
            uiThread {
                val interviewInvitationListAdapter = InterviewInvitationListAdapter(activity, interviewInvitations as MutableList<JobInvitation>)
                followedRV?.setHasFixedSize(true)
                followedRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                followedRV?.adapter = interviewInvitationListAdapter
                val styledText = "<b><font color='#13A10E'>${interviewInvitations.size}</font></b> Interview invitations found"
                favCountTV.text = Html.fromHtml(styledText)
                followedRV.show()
                favCountTV.show()
                shimmer_view_container_JobList.hide()
                shimmer_view_container_JobList.stopShimmerAnimation()
            }
        }
    }


}