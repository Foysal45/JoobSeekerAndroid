package com.bdjobs.app.editResume.educationInfo

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.Utilities.transitFragment
import com.bdjobs.app.editResume.adapters.models.AcaDataItem
import com.bdjobs.app.editResume.callbacks.EduInfo
import com.bdjobs.app.editResume.educationInfo.fragments.AcademicInfoEditFragment
import com.bdjobs.app.editResume.educationInfo.fragments.AcademicInfoViewFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_academic_base.*

class AcademicBaseActivity : AppCompatActivity(), EduInfo, ConnectivityReceiver.ConnectivityReceiverListener {

    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null
    private val acaEditFragment = AcademicInfoEditFragment()
    private val acaViewFragment = AcademicInfoViewFragment()
    private lateinit var datait: AcaDataItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_academic_base)
        transitFragment(acaViewFragment, R.id.edu_info_container, false)
    }

    override fun setDeleteButton(b: Boolean) {
        if (b) {
            iv_delete_data.show()
            iv_delete_data.setOnClickListener {
                /*if (acaEditFragment.isEdit) {
                    editFragment.dataDelete()
                } else if (armyEditFragment.isEdit) {
                    armyEditFragment.dataDelete()
                }*/
            }
        } else {
            iv_delete_data.hide()
        }
    }

    override fun getData(): AcaDataItem {
        return datait
    }

    override fun passData(data: AcaDataItem) {
        this.datait = data
    }

    override fun goToEditInfo(check: String) {
        when (check) {
            "add" -> {
                acaEditFragment.isEdit = false
                transitFragment(acaEditFragment, R.id.edu_info_container, true)
            }
            "edit" -> {
                acaEditFragment.isEdit = true
                transitFragment(acaEditFragment, R.id.edu_info_container, true)
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
        tv_edu_title?.text = title
        tb_edu?.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        tb_edu?.setNavigationOnClickListener {
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
                    .make(educationBaseCL, getString(R.string.alert_no_internet), Snackbar.LENGTH_INDEFINITE)
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
