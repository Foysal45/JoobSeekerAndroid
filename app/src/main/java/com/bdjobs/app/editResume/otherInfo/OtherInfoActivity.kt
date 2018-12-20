package com.bdjobs.app.editResume.otherInfo

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.transitFragment
import com.bdjobs.app.editResume.otherInfo.fragments.SpecializationViewFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_other_info_base.*

class OtherInfoActivity : Activity(), ConnectivityReceiver.ConnectivityReceiverListener {
    private var spView = SpecializationViewFragment()

    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_info_base)
        transitFragment(spView, R.id.other_info_container)
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
                    .make(cl_otherInfo_base, getString(R.string.alert_no_internet), Snackbar.LENGTH_INDEFINITE)
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
