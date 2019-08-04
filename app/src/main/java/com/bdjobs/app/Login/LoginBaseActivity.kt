package com.bdjobs.app.Login

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ProgressBar
import com.bdjobs.app.API.ModelClasses.SocialLoginAccountListData
import com.bdjobs.app.BackgroundJob.DatabaseUpdateJob
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationBaseActivity
import com.bdjobs.app.Utilities.Constants.Companion.key_go_to_home
import com.bdjobs.app.Utilities.debug
import com.bdjobs.app.Utilities.transitFragment
import com.bdjobs.app.Web.WebActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login_base.*
import org.jetbrains.anko.startActivity

class LoginBaseActivity : Activity(), LoginCommunicator, ConnectivityReceiver.ConnectivityReceiverListener {


    private val loginUserNameFragment = LoginUserNameFragment()
    private val loginPasswordFragment = LoginPasswordFragment()
    private val loginOTPFragment = LoginOTPFragment()
    private val socialAccountListFragment = SocialAccountListFragment()
    private var mSnackBar: Snackbar? = null
    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var userId: String? = null
    private var fullName: String? = null
    private var imageUrl: String? = null
    private var userName: String? = null
    private var socialLoginAccountDataList: List<SocialLoginAccountListData?>? = null
    private var goToHome = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_base)
        transitFragment(loginUserNameFragment, R.id.loginFragmentHolderFL)
        intent?.extras?.getBoolean(key_go_to_home)?.let { goHome ->
            goToHome = goHome
            Log.d("goToHome", "goToHome: ${goToHome}")
        }
    }

    override fun backButtonClicked() {
        onBackPressed()
    }

    override fun goToWebActivity(url: String, from: String) {
        startActivity<WebActivity>("url" to url, "from" to from)
    }


    override fun goToPasswordFragment(userName: String?, userId: String?, fullName: String?, imageUrl: String?) {
        this.userName = userName
        this.fullName = fullName
        this.userId = userId
        this.imageUrl = imageUrl
        debug("goToPasswordFragment, userName:$userName \n fullName:$fullName \n userId:$userId \n imageUrl:$imageUrl \n")
        transitFragment(loginPasswordFragment, R.id.loginFragmentHolderFL, true)
    }

    override fun goToHomePage() {
        DatabaseUpdateJob.runJobImmediately()
        if (goToHome) {
            val intent = Intent(this@LoginBaseActivity, MainLandingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finishAffinity()
        } else {
            finish()
        }
    }

    override fun getSocialLoginAccountDataList(): List<SocialLoginAccountListData?>? {
        return socialLoginAccountDataList
    }

    override fun getGoToHome(): Boolean? {
        return goToHome
    }

    override fun goToSocialAccountListFragment(socialLoginAccountDataList: List<SocialLoginAccountListData?>?) {
        this.socialLoginAccountDataList = socialLoginAccountDataList
        transitFragment(socialAccountListFragment, R.id.loginFragmentHolderFL, true)
    }


    override fun goToOtpFragment(userName: String?, userId: String?, fullName: String?, imageUrl: String?) {
        this.userName = userName
        this.fullName = fullName
        this.userId = userId
        this.imageUrl = imageUrl
        debug("goToOtpFragment, userName:$userName \n fullName:$fullName \n userId:$userId \n imageUrl:$imageUrl \n")
        transitFragment(loginOTPFragment, R.id.loginFragmentHolderFL, true)
    }

    override fun goToRegistrationActivity() {
        startActivity<RegistrationBaseActivity>()
    }

    override fun onPostResume() {
        super.onPostResume()
        registerReceiver(internetBroadCastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ConnectivityReceiver.connectivityReceiverListener = this
    }


    override fun getFullName(): String? {
        return fullName
    }

    override fun getUserId(): String? {
        return userId
    }

    override fun getImageUrl(): String? {
        return imageUrl
    }

    override fun getUserName(): String? {
        return userName
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            mSnackBar = Snackbar
                    .make(loginBaseCL, getString(R.string.alert_no_internet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.turn_on_wifi)) {
                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    }
                    .setActionTextColor(resources.getColor(R.color.colorWhite))

            mSnackBar?.show()

        } else {
            mSnackBar?.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(internetBroadCastReceiver)
    }
}
