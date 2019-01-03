package com.bdjobs.app.editResume.personalInfo

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.transitFragment
import com.bdjobs.app.editResume.adapters.models.C_DataItem
import com.bdjobs.app.editResume.adapters.models.Ca_DataItem
import com.bdjobs.app.editResume.adapters.models.P_DataItem
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import com.bdjobs.app.editResume.personalInfo.fragments.carrerDetails.CareerEditFragment
import com.bdjobs.app.editResume.personalInfo.fragments.carrerDetails.CareerViewFragment
import com.bdjobs.app.editResume.personalInfo.fragments.contactDetails.ContactEditFragment
import com.bdjobs.app.editResume.personalInfo.fragments.contactDetails.ContactViewFragment
import com.bdjobs.app.editResume.personalInfo.fragments.personalDetails.PersonalDetailsEditFragment
import com.bdjobs.app.editResume.personalInfo.fragments.personalDetails.PersonalDetailsViewFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_personal_info.*

class PersonalInfoActivity : Activity(), ConnectivityReceiver.ConnectivityReceiverListener, PersonalInfo {

    private val personalEditFragment = PersonalDetailsEditFragment()
    private val personalViewFragment = PersonalDetailsViewFragment()
    private val careerViewFragment = CareerViewFragment()
    private val careerEditFragment = CareerEditFragment()
    private val contactViewFragment = ContactViewFragment()
    private val contactEditFragment = ContactEditFragment()
    private lateinit var dataCa: Ca_DataItem
    private lateinit var dataCon: C_DataItem
    private lateinit var dataPer: P_DataItem
    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_info)
        transitFragment(contactViewFragment, R.id.personalinfo_container, false)
    }

    override fun getPersonalData(): P_DataItem {
        return dataPer
    }

    override fun passPersonalData(data: P_DataItem) {
        this.dataPer = data
    }


    override fun getCareerData(): Ca_DataItem {
        return dataCa
    }

    override fun passCareerData(data: Ca_DataItem) {
        this.dataCa = data
    }

    override fun getContactData(): C_DataItem {
        return dataCon
    }

    override fun passContactData(data: C_DataItem) {
        this.dataCon = data
    }

    override fun setEditButton(b: Boolean, type: String) {
        if (b) {
            iv_edit_data.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_edit_white))
            iv_edit_data.setOnClickListener {
                goToEditInfo(type)
            }
        } else {
            //iv_edit_data.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_delete_white_24dp))
        }
    }

    override fun setDeleteButton(b: Boolean) {
        if (b) {
            iv_edit_data.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_delete_white_24dp))
        } else {
            //iv_edit_data.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_edit_white))
        }
    }


    override fun goToEditInfo(check: String) {
        when (check) {
            "add" -> {

            }
            "editPersonal" -> {
                transitFragment(personalEditFragment, R.id.personalinfo_container, true)
            }
            "editCareer" -> {
                transitFragment(careerEditFragment, R.id.personalinfo_container, true)
            }
            "editContact" -> {
                //contact edit fragment
                transitFragment(contactEditFragment, R.id.personalinfo_container, true)
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
