package com.bdjobs.app.InviteCode

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.logException
import kotlinx.android.synthetic.main.activity_invite_code_base.*

class InviteCodeBaseActivity : FragmentActivity(),InviteCodeCommunicator {


    private var paymentMethod: String = ""
    private var accountNumber: String = ""
    private var inviteCodeuserType: String = ""
    private var pcOwnerID: String = ""
    private var inviteCodeStatus: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_code_base)

        val navHostFragment = inviteCodeBaseNavFragment as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.invite_code_base_navigation)

        try {
            inviteCodeStatus = intent.getStringExtra("inviteCodeStatus")
            pcOwnerID = intent.getStringExtra("pcOwnerID")
            inviteCodeuserType = intent.getStringExtra("userType")

        } catch (e: Exception) {
            logException(e)
        }

        if (inviteCodeuserType.equalIgnoreCase("o")) {
            graph.startDestination = R.id.navigation_owner_base

        } else if (inviteCodeuserType.equalIgnoreCase("u")) {
            if (inviteCodeStatus.equalIgnoreCase("0")) {
                graph.startDestination = R.id.navigation_user_invite_code_submit
            } else if (inviteCodeStatus.equalIgnoreCase("1")) {
                graph.startDestination = R.id.navigation_user_invite_code_status
            }
        }
        navHostFragment.navController.graph = graph

    }
    override fun getPaymentMethod(): String? {
        return paymentMethod
    }

    override fun getAccountNumber(): String? {
        return accountNumber
    }

    override fun getInviteCodeUserType(): String? {
       return  inviteCodeuserType
    }

    override fun getInviteCodepcOwnerID(): String? {
        return pcOwnerID
    }

    override fun getInviteCodeStatus(): String? {
        return inviteCodeStatus
    }
    override fun backButtonClicked() {
       onBackPressed()
    }

    override fun goToPaymentMethod(paymentMethod: String, accountNumber: String) {

        val navController = Navigation.findNavController(this@InviteCodeBaseActivity, R.id.inviteCodeBaseNavFragment)
        navController.navigate(R.id.navigation_payment_method)

        this.accountNumber=accountNumber
        this.paymentMethod=paymentMethod
    }
}
