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
import com.bdjobs.app.editResume.adapters.models.*
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import com.bdjobs.app.editResume.personalInfo.fragments.carrerDetails.CareerEditFragment
import com.bdjobs.app.editResume.personalInfo.fragments.carrerDetails.CareerViewFragment
import com.bdjobs.app.editResume.personalInfo.fragments.contactDetails.ContactEditFragment
import com.bdjobs.app.editResume.personalInfo.fragments.contactDetails.ContactViewFragment
import com.bdjobs.app.editResume.personalInfo.fragments.otherRelevantInfo.ORIEditFragment
import com.bdjobs.app.editResume.personalInfo.fragments.otherRelevantInfo.ORIViewFragment
import com.bdjobs.app.editResume.personalInfo.fragments.personalDetails.PersonalDetailsEditFragment
import com.bdjobs.app.editResume.personalInfo.fragments.personalDetails.PersonalDetailsViewFragment
import com.bdjobs.app.editResume.personalInfo.fragments.preferredAreas.PreferredAreasEditFragment
import com.bdjobs.app.editResume.personalInfo.fragments.preferredAreas.PreferredAreasViewFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_personal_info.*

class PersonalInfoActivity : Activity(), ConnectivityReceiver.ConnectivityReceiverListener, PersonalInfo {
    override fun getBackFrom(): String? {
        return this.fragmentFrom
    }

    override fun setBackFrom(from: String?) {
        this.fragmentFrom = from
    }


    override fun setPmThana(thana: String?) {
        pmThana = thana!!
    }

    override fun getPmThana(): String? {
        return pmThana
    }

    override fun setPmPostOffice(po: String?) {
        pmPost = po!!
    }

    override fun getPmPostOffice(): String? {
        return pmPost
    }

    override fun setThana(thana: String?) {
        prThana = thana!!
    }

    override fun getThana(): String? {
        return prThana
    }

    override fun setPostOffice(po: String?) {
        prPost = po!!
    }

    override fun getPostOffice(): String? {
        return prPost
    }

    private val personalEditFragment = PersonalDetailsEditFragment()
    private val personalViewFragment = PersonalDetailsViewFragment()
    private val careerViewFragment = CareerViewFragment()
    private val careerEditFragment = CareerEditFragment()
    private val contactViewFragment = ContactViewFragment()
    private val contactEditFragment = ContactEditFragment()
    private val oriViewFragment = ORIViewFragment()
    private val oriEditFragment = ORIEditFragment()
    private val prefViewFragment = PreferredAreasViewFragment()
    private val prefEditFragment = PreferredAreasEditFragment()
    private lateinit var dataCa: Ca_DataItem
    private lateinit var dataCon: C_DataItem
    private lateinit var dataPer: P_DataItem
    private lateinit var dataOri: ORIdataItem
    private lateinit var dataAreas: PreferredAreasData
    private lateinit var name: String
    private lateinit var gotToAddEmployment: String
    private var prThana: String? = ""
    private var prPost: String? = ""
    private var pmDis: String? = ""
    private var prDis: String? = ""
    private var pmThana: String? = ""
    private var pmPost: String? = ""
    private var fragmentFrom: String? = "first"

    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentValues()
        setContentView(R.layout.activity_personal_info)
        gotToFragment(name)
        val adRequest = AdRequest.Builder().build()
        adViewPersonalInfo?.loadAd(adRequest)
    }

    private fun gotToFragment(name: String) {
        when (name) {
            "personal" -> transitFragment(personalViewFragment, R.id.personalinfo_container, false)
            "contact" -> transitFragment(contactViewFragment, R.id.personalinfo_container, false)
            "career" -> transitFragment(careerViewFragment, R.id.personalinfo_container, false)
            "ori" -> transitFragment(oriViewFragment, R.id.personalinfo_container, false)
            "prefAreas" -> transitFragment(prefViewFragment, R.id.personalinfo_container, false)
        }
    }

    private fun getIntentValues() {
        //gkflhjgh
        name = intent.getStringExtra("name")
        gotToAddEmployment = intent.getStringExtra("personal_info_edit")
        goToEditInfo(gotToAddEmployment)
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

    override fun getPrefAreasData(): PreferredAreasData {
        return dataAreas
    }

    override fun passPrefAreasData(data: PreferredAreasData) {
        this.dataAreas = data
    }

    override fun setEditButton(b: Boolean, type: String) {
        try {
            if (b) {
                iv_edit_data.show()
                iv_edit_data.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_edit_white))
                iv_edit_data.setOnClickListener {
                    goToEditInfo(type)
                }
            } else {
                iv_edit_data.hide()
                //iv_edit_data.setImageDrawable(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }


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
                "editPrefAreas" -> {
                    //contact edit fragment
                    transitFragment(prefEditFragment, R.id.personalinfo_container, true)
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
