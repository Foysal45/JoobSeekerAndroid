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
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.api_request_result_code_ok
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.fragment_interview_invitation_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

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
        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)
    }

    override fun onResume() {
        super.onResume()



        if (interviewInvitationCommunicator.getFrom().equalIgnoreCase("popup")) {
            followedRV?.hide()
            favCountTV?.hide()
            shimmer_view_container_JobList?.show()
            shimmer_view_container_JobList?.startShimmerAnimation()
            showDataFromServer()

        } else {
            //homePage
            followedRV?.hide()
            favCountTV?.hide()
            shimmer_view_container_JobList?.show()
            shimmer_view_container_JobList?.startShimmerAnimation()

            showDataFromDB()

        }

        Log.d("from", "from: ${interviewInvitationCommunicator.getFrom()}")
    }

    private fun showDataFromServer() {
        ApiServiceMyBdjobs.create().getJobInvitationList(
                userId = bdjobsUserSession.userId,
                decodeId = bdjobsUserSession.decodId


        ).enqueue(object : Callback<JobInvitationListModel> {
            override fun onFailure(call: Call<JobInvitationListModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<JobInvitationListModel>, response: Response<JobInvitationListModel>) {
                try {
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
                } catch (e: Exception) {
                    logException(e)
                }
            }

        })
    }

    private fun showDataFromDB() {
        doAsync {
            var interviewInvitations: List<JobInvitation>? = null

            interviewInvitations = if (interviewInvitationCommunicator.getFrom().equalIgnoreCase("mybdjobs")) {
                if (interviewInvitationCommunicator.getTime().equalIgnoreCase("0")) {
                    bdjobsDB.jobInvitationDao().getAllJobInvitation()
                } else {
                    val calendar = Calendar.getInstance()
//                    calendar.add(Calendar.DAY_OF_YEAR, - 30)
//                    val lastmonth = calendar.time
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                    val firstDateOfMonth = calendar.time
                    bdjobsDB.jobInvitationDao().getALLJobInvitationByDate(firstDateOfMonth)
                }

            } else {
                bdjobsDB.jobInvitationDao().getAllJobInvitation()

            }

            uiThread {
                try {
                    val interviewInvitationListAdapter = InterviewInvitationListAdapter(activity, interviewInvitations as MutableList<JobInvitation>)
                    followedRV?.setHasFixedSize(true)
                    followedRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                    followedRV?.adapter = interviewInvitationListAdapter
                    var data = "invitation"

                    if (interviewInvitations.size!! > 0) {
                        invitationNoDataLL?.hide()
                        followedRV?.show()
                        Log.d("totalJobs", "data ase")
                    } else {
                        invitationNoDataLL?.show()
                        followedRV?.hide()
                        Log.d("totalJobs", "zero")
                    }

                    if (interviewInvitations.size > 1) {
                        data = "invitations"
                    }
                    val styledText = "<b><font color='#13A10E'>${interviewInvitations.size}</font></b> Interview $data found"
                    favCountTV.text = Html.fromHtml(styledText)

                    favCountTV?.show()
                    shimmer_view_container_JobList?.hide()
                    shimmer_view_container_JobList?.stopShimmerAnimation()
                } catch (e: Exception) {
                    logException(e)
                }
            }
        }
    }


}