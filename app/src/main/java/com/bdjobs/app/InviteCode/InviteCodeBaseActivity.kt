package com.bdjobs.app.InviteCode

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bdjobs.app.API.ModelClasses.InviteCodeOwnerStatementModel
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.InviteCode.InviteCodeOwner.OwnerStatementFragment
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.transitFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_invite_code_base.*
import kotlinx.android.synthetic.main.no_internet.*

class InviteCodeBaseActivity : FragmentActivity(), InviteCodeCommunicator{



    private var paymentMethod: String = ""
    private var accountNumber: String = ""
    private var inviteCodeuserType: String = ""
    private var pcOwnerID: String = ""
    private var inviteCodeStatus: String = ""
    private var fromInviteCodeSubmitPage = false
    private val internetBroadCastReceiver = ConnectivityReceiver()
    var mSnackBar: Snackbar? = null
    var navHostFragment: NavHostFragment? = null
    var flag = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_code_base)

        navHostFragment = inviteCodeBaseNavFragment as NavHostFragment
        val inflater = navHostFragment?.navController?.navInflater
        val graph = inflater?.inflate(R.navigation.invite_code_base_navigation)


        //registerReceiver(internetBroadCastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))


        try {
            inviteCodeStatus = intent.getStringExtra("inviteCodeStatus").toString()
            pcOwnerID = intent.getStringExtra("pcOwnerID").toString()
            inviteCodeuserType = intent.getStringExtra("userType").toString()

        } catch (e: Exception) {
            logException(e)
        }

        //Log.d("rakib", inviteCodeuserType)

        if (inviteCodeuserType.equalIgnoreCase("o")) {
            graph?.startDestination = R.id.navigation_owner_base

        } else if (inviteCodeuserType.equalIgnoreCase("u")) {
            if (inviteCodeStatus.equalIgnoreCase("0")) {
                graph?.startDestination = R.id.navigation_user_invite_code_submit
            } else if (inviteCodeStatus.equalIgnoreCase("1")) {
                graph?.startDestination = R.id.navigation_user_invite_code_status
            }
        }
        navHostFragment?.navController?.graph = graph!!

        flag = 1
    }

    override fun onResume() {
        super.onResume()

    }

    override fun getPaymentMethod(): String? {
        return paymentMethod
    }

    override fun getAccountNumber(): String? {
        return accountNumber
    }

    override fun getInviteCodeUserType(): String? {
        return inviteCodeuserType
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

    override fun goToPaymentMethod(paymentMethod: String, accountNumber: String, fromInviteCodeSubmitPage: Boolean) {
        val navController = Navigation.findNavController(this@InviteCodeBaseActivity, R.id.inviteCodeBaseNavFragment)
        navController.navigate(R.id.navigation_payment_method)

        this.accountNumber = accountNumber
        this.paymentMethod = paymentMethod
        this.fromInviteCodeSubmitPage = fromInviteCodeSubmitPage
    }

    override fun getfromInviteCodeSubmitPage(): Boolean {
        return fromInviteCodeSubmitPage
    }

    override fun onBackPressed() {
        if (fromInviteCodeSubmitPage) {
            val intent = Intent(this@InviteCodeBaseActivity, MainLandingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }

}
