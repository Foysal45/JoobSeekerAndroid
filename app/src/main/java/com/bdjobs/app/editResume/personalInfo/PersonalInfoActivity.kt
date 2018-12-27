package com.bdjobs.app.editResume.personalInfo

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.transitFragment
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import com.bdjobs.app.editResume.personalInfo.fragments.contactDetails.ContactViewFragment
import com.bdjobs.app.editResume.personalInfo.fragments.personalDetails.PersonalDetailsEditFragment
import com.bdjobs.app.editResume.personalInfo.fragments.personalDetails.PersonalDetailsViewFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_personal_info.*

class PersonalInfoActivity : Activity(), ConnectivityReceiver.ConnectivityReceiverListener, PersonalInfo {

    private val editFragment = PersonalDetailsEditFragment()
    private val viewFragment = PersonalDetailsViewFragment()
    private val contactViewFragment = ContactViewFragment()

    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_info)
        transitFragment(contactViewFragment, R.id.CLpersonalBase, false)
    }

    override fun setEditButton(b: Boolean) {
        if (b) {
        } else {
        }
    }

    override fun goToEditInfo(check: String) {
        when (check) {
            "add" -> {
                //editFragment.isEdit = false
                transitFragment(editFragment, R.id.personalinfo_container, true)
            }
            "edit" -> {
            }
            else -> {
            }
        }
    }

    override fun goBack() {
        onBackPressed()
    }

    override fun setTitle(tit: String?) {
        setupToolbar(tit)
    }

    private fun setupToolbar(title: String?) {
        tv_tb_title?.text = title
        tbPersonalInfo?.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        tbPersonalInfo?.setNavigationOnClickListener {
            goBack()
        }
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
                    .make(CLpersonalBase, getString(R.string.alert_no_internet), Snackbar.LENGTH_INDEFINITE)
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
