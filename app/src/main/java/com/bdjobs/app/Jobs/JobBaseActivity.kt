package com.bdjobs.app.Jobs

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import com.bdjobs.app.ConnectivityCheck.ConnectivityReceiver
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.transitFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_job_landing.*

class JobBaseActivity : Activity(), ConnectivityReceiver.ConnectivityReceiverListener {


    private val joblistFragment = JoblistFragment()
    private val jobDetailsFragment = JobDetailsFragment()
    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_landing)
        transitFragment(joblistFragment, R.id.jobFragmentHolder)
    }

    override fun onPostResume() {
        super.onPostResume()
        registerReceiver(internetBroadCastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(internetBroadCastReceiver)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            mSnackBar = Snackbar
                    .make(jobsBaseCL, getString(R.string.alert_no_internet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.turn_on_wifi)) {
                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    }
                    .setActionTextColor(resources.getColor(R.color.colorWhite))
            mSnackBar?.show()
        } else {
            mSnackBar?.dismiss()
        }
    }
}
