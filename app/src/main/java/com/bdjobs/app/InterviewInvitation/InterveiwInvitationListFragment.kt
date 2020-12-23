package com.bdjobs.app.InterviewInvitation

import android.app.Fragment
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.JobInvitationListModel
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.databases.internal.JobInvitation
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.api_request_result_code_ok
//import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.fragment_interview_invitation_list.*
//import kotlinx.android.synthetic.main.fragment_interview_invitation_list.adView
import kotlinx.android.synthetic.main.fragment_interview_invitation_list.backIMV
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
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
//        val adRequest = AdRequest.Builder().build()
//        adView?.loadAd(adRequest)

    }

    override fun onResume() {
        super.onResume()



        if (interviewInvitationCommunicator.getFrom().equalIgnoreCase("popup")) {
            followedRV?.hide()
            favCountTV?.hide()
            shimmer_view_container_JobList?.show()
            shimmer_view_container_JobList?.startShimmer()
            showDataFromServer()

        } else {
            //homePage
            followedRV?.hide()
            favCountTV?.hide()
            shimmer_view_container_JobList?.show()
            shimmer_view_container_JobList?.startShimmer()

            showDataFromDB()

        }

        //Log.d("from", "from: ${interviewInvitationCommunicator.getFrom()}")
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
                                        var inviteDate : Date? = null
                                        try {
                                            inviteDate = SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse(item?.inviteDate)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                        var interviewDate : Date? = null
                                        try {
                                            interviewDate = SimpleDateFormat("d MMM yyyy hh:mm:ss").parse("${item?.inviterviewDate} ${item?.inviterviewTime}")
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                        val jobInvitation = JobInvitation(companyName = item?.companyName,
                                                inviteDate = inviteDate,
                                                jobId = item?.jobId,
                                                jobTitle = item?.jobTitle,
                                                seen = item?.seen,
                                                interviewDate = interviewDate,
                                                interviewDateString = item?.inviterviewDate,
                                                interviewTimeString = item?.inviterviewTime
                                        )
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
        //Log.d("rakib","came here")
        doAsync {
            var interviewInvitations: List<JobInvitation>? = null

            interviewInvitations = if (interviewInvitationCommunicator.getFrom().equalIgnoreCase("mybdjobs")) {
                if (interviewInvitationCommunicator.getTime().equalIgnoreCase("0")) {
                    bdjobsDB.jobInvitationDao().getAllJobInvitation()
                } else {
                    val calendar = Calendar.getInstance()

                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                    val firstDateOfMonth = calendar.time

                    bdjobsDB.jobInvitationDao().getALLJobInvitationByDate(firstDateOfMonth)
                }

            } else {
                bdjobsDB.jobInvitationDao().getAllJobInvitation()

            }

            //Log.d("rakib",interviewInvitations.size.toString())

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
                        //Log.d("totalJobs", "data ase")
                    } else {
                        invitationNoDataLL?.show()
                        followedRV?.hide()
                        //Log.d("totalJobs", "zero")
                    }

                    if (interviewInvitations.size > 1) {
                        data = "invitations"
                    }
                    val styledText = "<b><font color='#13A10E'>${interviewInvitations.size}</font></b> Interview $data found"
                    favCountTV.text = Html.fromHtml(styledText)

                    favCountTV?.show()
                    shimmer_view_container_JobList?.hide()
                    shimmer_view_container_JobList?.stopShimmer()
                } catch (e: Exception) {
                    logException(e)
                }
            }
        }
    }


}