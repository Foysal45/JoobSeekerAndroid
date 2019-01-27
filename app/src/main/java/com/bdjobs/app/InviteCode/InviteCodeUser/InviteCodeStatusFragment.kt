package com.bdjobs.app.InviteCode.InviteCodeUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession

class InviteCodeStatusFragment : Fragment(){
    private var bdjobsUserSession: BdjobsUserSession?=null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_invite_code_status, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bdjobsUserSession = BdjobsUserSession(activity!!)


    }
}