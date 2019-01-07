package com.bdjobs.app.InterviewInvitation

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import kotlinx.android.synthetic.main.fragment_interview_invitation_details.*

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

        }

    }
}