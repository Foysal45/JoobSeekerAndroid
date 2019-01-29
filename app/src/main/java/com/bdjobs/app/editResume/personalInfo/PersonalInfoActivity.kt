package com.bdjobs.app.editResume.personalInfo

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.C_DataItem
import com.bdjobs.app.editResume.adapters.models.Ca_DataItem
import com.bdjobs.app.editResume.adapters.models.ORIdataItem
import com.bdjobs.app.editResume.adapters.models.P_DataItem
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import com.bdjobs.app.editResume.personalInfo.fragments.carrerDetails.CareerEditFragment
import com.bdjobs.app.editResume.personalInfo.fragments.carrerDetails.CareerViewFragment
import com.bdjobs.app.editResume.personalInfo.fragments.contactDetails.ContactEditFragment
import com.bdjobs.app.editResume.personalInfo.fragments.contactDetails.ContactViewFragment
import com.bdjobs.app.editResume.personalInfo.fragments.otherRelevantInfo.ORIEditFragment
import com.bdjobs.app.editResume.personalInfo.fragments.otherRelevantInfo.ORIViewFragment
import com.bdjobs.app.editResume.personalInfo.fragments.personalDetails.PersonalDetailsEditFragment
import com.bdjobs.app.editResume.personalInfo.fragments.personalDetails.PersonalDetailsViewFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_personal_info.*

class PersonalInfoActivity : Activity(), ConnectivityReceiver.ConnectivityReceiverListener, PersonalInfo {

    private val personalEditFragment = PersonalDetailsEditFragment()
    private val personalViewFragment = PersonalDetailsViewFragment()
    private val careerViewFragment = CareerViewFragment()
    private val careerEditFragment = CareerEditFragment()
    private val contactViewFragment = ContactViewFragment()
    private val contactEditFragment = ContactEditFragment()
    private val oriViewFragment = ORIViewFragment()
    private val oriEditFragment = ORIEditFragment()
    private lateinit var dataCa: Ca_DataItem
    private lateinit var dataCon: C_DataItem
    private lateinit var dataPer: P_DataItem
    private lateinit var dataOri: ORIdataItem
    private lateinit var name: String

    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentValues()
        setContentView(R.layout.activity_personal_info)
        gotToFragment(name)
    }

    private fun gotToFragment(name: String) {
        when (name) {
            "personal" -> transitFragment(personalViewFragment, R.id.personalinfo_container, false)
            "contact" -> transitFragment(contactViewFragment, R.id.personalinfo_container, false)
            "career" -> transitFragment(careerViewFragment, R.id.personalinfo_container, false)
            "ori" -> transitFragment(oriViewFragment, R.id.personalinfo_container, false)
        }
    }

    private fun getIntentValues() {
        name = intent.getStringExtra("name")
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

    override fun getOriData(): ORIdataItem {
        return dataOri
    }

    override fun passOriData(data: ORIdataItem) {
        this.dataOri = data
    }

    override fun setEditButton(b: Boolean, type: String) {
        if (b) {
            iv_edit_data.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_edit_white))
            iv_edit_data.setOnClickListener {
                goToEditInfo(type)
            }
        } else {
            iv_edit_data.setImageDrawable(null)
        }
    }
/*
    override fun setDeleteButton(b: Boolean) {
        if (b) {
            iv_edit_data.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_delete_white_24dp))
        }
    }*/

    private fun goToEditInfo(check: String) {
        try {
            when (check) {
                "addDirect" -> {
                    //personalEditFragment.isEdit = false
                    transitFragment(personalEditFragment, R.id.personalinfo_container, false)
                    Constants.isDirectCall = true
                }
                "editORI" -> {
                    transitFragment(oriEditFragment, R.id.personalinfo_container, true)
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
                    iv_edit_data.hide()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    override fun goBack() {
        onBackPressed()
        if (Constants.isDirectCall) finish()
        CLpersonalBase.closeKeyboard(this@PersonalInfoActivity)
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

    override fun validateField(char: String, et: TextInputEditText, til: TextInputLayout): Boolean {
        when {
            TextUtils.isEmpty(char) -> {
                til.showError(getString(R.string.field_empty_error_message_common))
                this.requestFocus(et)
                return false
            }
            char.length < 2 -> {
                til.showError(" it is too short")
                this.requestFocus(et)
                return false
            }
            else -> til.hideError()
        }
        return true
    }
}
