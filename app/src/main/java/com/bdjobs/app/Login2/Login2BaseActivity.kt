package com.bdjobs.app.Login2

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import com.google.android.material.snackbar.Snackbar
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
//import com.bdjobs.app.BackgroundJob.DatabaseUpdateJob
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.Constants.Companion.key_go_to_home
import com.bdjobs.app.Utilities.transitFragment
import com.bdjobs.app.Workmanager.DatabaseUpdateWorker

import kotlinx.android.synthetic.main.activity_login2_base.*

class Login2BaseActivity : Activity(), Login2Communicator, ConnectivityReceiver.ConnectivityReceiverListener {

    private val login2UserNameFragment = Login2UserNameFragment()
    private val login2PasswordFragment = Login2PasswordFragment()
    private val login2OTPFragment = Login2OTPFragment()
    private var mSnackBar: Snackbar? = null
    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var userId: String? = null
    private var fullName: String? = null
    private var imageUrl: String? = null
    private var userName: String? = null
    private var goToHome = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_base)
        transitFragment(login2UserNameFragment, R.id.loginFragmentHolderFL)
        intent?.extras?.getBoolean(key_go_to_home)?.let { goHome ->
            goToHome = goHome
            //Log.d("goToHome", "goToHome: ${goToHome}")
        }
    }

    override fun backButtonClicked() {
        onBackPressed()
    }




    override fun goToPasswordFragment(userName: String?, userId: String?, fullName: String?, imageUrl: String?) {
        this.userName = userName
        this.fullName = fullName
        this.userId = userId
        this.imageUrl = imageUrl
        transitFragment(login2PasswordFragment, R.id.loginFragmentHolderFL, true)
    }

    override fun goToOtpFragment(userName: String?, userId: String?, fullName: String?, imageUrl: String?) {
        this.userName = userName
        this.fullName = fullName
        this.userId = userId
        this.imageUrl = imageUrl
        transitFragment(login2OTPFragment, R.id.loginFragmentHolderFL, true)
    }

    override fun goToHomePage() {
//        DatabaseUpdateJob.runJobImmediately()

        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val databaseUpdateRequest = OneTimeWorkRequestBuilder<DatabaseUpdateWorker>()
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(applicationContext).enqueue(databaseUpdateRequest)

        if (goToHome) {
            val intent = Intent(this@Login2BaseActivity, MainLandingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finishAffinity()
        } else {
            finish()
        }
    }

    override fun getGoToHome(): Boolean? {
        return goToHome
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
