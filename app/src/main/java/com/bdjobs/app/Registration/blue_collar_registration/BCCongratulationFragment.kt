package com.bdjobs.app.Registration.blue_collar_registration

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.InviteCode.InviteCodeBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_bc_congratulation.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread


class BCCongratulationFragment : Fragment() {


    private var userType = ""
    private var pcOwnerID = ""
    private var inviteCodeStatus = ""
    private lateinit var registrationCommunicator: RegistrationCommunicator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_bc_congratulation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        initialization()
        onClick()
    }


    private fun onClick(){
        bcJobSearchButton?.setOnClickListener {
            registrationCommunicator.goToHomePage()
        }
        promoCodeBTN?.setOnClickListener {
            startActivity<InviteCodeBaseActivity>(
                    "userType" to userType,
                    "pcOwnerID" to pcOwnerID,
                    "inviteCodeStatus" to inviteCodeStatus)
        }
    }


    private fun initialization() {
        registrationCommunicator = activity as RegistrationCommunicator
        val bdjobsDB = BdjobsDB.getInstance(activity)
        val  bdjobsUserSession = BdjobsUserSession(activity)
        doAsync {
            val inviteCodeUserInfo = bdjobsDB.inviteCodeUserInfoDao().getInviteCodeInformation(bdjobsUserSession.userId!!)
            uiThread {
                if (inviteCodeUserInfo.isNullOrEmpty()) {
                    promoCodeBTN?.hide()
                }else{
                    promoCodeBTN?.show()
                    userType = inviteCodeUserInfo.get(0).userType!!
                    pcOwnerID = inviteCodeUserInfo.get(0).pcOwnerID!!
                    inviteCodeStatus = inviteCodeUserInfo.get(0).inviteCodeStatus!!
                    Log.d("invitereg", "userType = $userType, pcOwnerID= $pcOwnerID, inviteCodeStatus= $inviteCodeStatus")

                }
            }
        }
    }

}
