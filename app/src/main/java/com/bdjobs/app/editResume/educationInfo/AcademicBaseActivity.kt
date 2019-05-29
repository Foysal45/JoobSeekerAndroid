package com.bdjobs.app.editResume.educationInfo

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AcaDataItem
import com.bdjobs.app.editResume.adapters.models.ProfessionalDataModel
import com.bdjobs.app.editResume.adapters.models.Tr_DataItem
import com.bdjobs.app.editResume.callbacks.EduInfo
import com.bdjobs.app.editResume.educationInfo.fragments.academicInfo.AcademicInfoEditFragment
import com.bdjobs.app.editResume.educationInfo.fragments.academicInfo.AcademicInfoViewFragment
import com.bdjobs.app.editResume.educationInfo.fragments.professionalQualification.ProfessionalQLEditFragment
import com.bdjobs.app.editResume.educationInfo.fragments.professionalQualification.ProfessionalQLViewFragment
import com.bdjobs.app.editResume.educationInfo.fragments.trainingInfo.TrainingEditFragment
import com.bdjobs.app.editResume.educationInfo.fragments.trainingInfo.TrainingViewFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_academic_base.*

class AcademicBaseActivity : AppCompatActivity(), EduInfo, ConnectivityReceiver.ConnectivityReceiverListener {
    override fun saveButtonClickStatus(value: Boolean) {

    }

    override fun getClickStatus(): Boolean {
        return true
    }

    override fun setAcademicList(aca: ArrayList<AcaDataItem>) {
        this.acaList = aca
    }

    override fun setTrainingList(aca: ArrayList<Tr_DataItem>) {
        this.trList = aca
    }

    override fun setProfessionalList(aca: ArrayList<ProfessionalDataModel>) {
        this.profList = aca
    }

    override fun getAcademicList(): ArrayList<AcaDataItem>? {
        return this.acaList
    }

    override fun getTrainingList(): ArrayList<Tr_DataItem>? {
        return this.trList
    }

    override fun getProfessionalList(): ArrayList<ProfessionalDataModel>? {
        return this.profList
    }

    override fun getBackFrom(): String? {
        return this.fragmentFrom
    }

    override fun setBackFrom(from: String?) {
        this.fragmentFrom = from
    }


    private var fragmentFrom: String? = "first"
    private var acaList: ArrayList<AcaDataItem>? = null
    private var trList: ArrayList<Tr_DataItem>? = null
    private var profList: ArrayList<ProfessionalDataModel>? = null
    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null
    private val acaEditFragment = AcademicInfoEditFragment()
    private val acaViewFragment = AcademicInfoViewFragment()
    private val trainingEditFragment = TrainingEditFragment()
    private val trainingViewFragment = TrainingViewFragment()
    private val professionalQLEditFragment = ProfessionalQLEditFragment()
    private val professionalQLViewFragment = ProfessionalQLViewFragment()
    private lateinit var datait: AcaDataItem
    private lateinit var dataTr: Tr_DataItem
    private lateinit var dataPrq: ProfessionalDataModel
    private lateinit var dataStorage: DataStorage
    lateinit var name: String
    lateinit var gotToAddEmployment: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentValues()
        setContentView(R.layout.activity_academic_base)
        gotToFragment(name)
        val adRequest = AdRequest.Builder().build()
        adViewAcademicInfo?.loadAd(adRequest)
    }

    private fun gotToFragment(name: String) {
        when (name) {
            "academic" -> transitFragment(acaViewFragment, R.id.edu_info_container, false)
            "training" -> transitFragment(trainingViewFragment, R.id.edu_info_container, false)
            "professional" -> transitFragment(professionalQLViewFragment, R.id.edu_info_container, false)
        }
    }

    private fun getIntentValues() {
        name = intent.getStringExtra("name")
        gotToAddEmployment = intent.getStringExtra("education_info_add")
        goToEditInfo(gotToAddEmployment)
    }

    override fun setDeleteButton(b: Boolean) {
        if (b) {
            iv_delete_data?.show()
            iv_delete_data?.setOnClickListener {
                if (acaEditFragment.isEdit) {
                    acaEditFragment.dataDelete()
                }
                if (trainingEditFragment.isEdit) {
                    trainingEditFragment.dataDelete()
                }
                if (professionalQLEditFragment.isEdit) {
                    professionalQLEditFragment.dataDelete()
                }
            }
        } else {
            iv_delete_data?.hide()
        }
    }

    override fun getData(): AcaDataItem {
        return datait
    }

    override fun passData(data: AcaDataItem) {
        this.datait = data
    }

    override fun getTrainingData(): Tr_DataItem {
        return dataTr
    }

    override fun passTrainingData(data: Tr_DataItem) {
        this.dataTr = data
    }

    override fun goToEditInfo(check: String) {
        try {
            when (check) {
                "addDirect" -> {
                    acaEditFragment.isEdit = false
                    transitFragment(acaEditFragment, R.id.edu_info_container, true)
                    Constants.isDirectCall = true
                }
                "add" -> {
                    acaEditFragment.isEdit = false
                    transitFragment(acaEditFragment, R.id.edu_info_container, true)
                }
                "addTr" -> {
                    trainingEditFragment.isEdit = false
                    transitFragment(trainingEditFragment, R.id.edu_info_container, true)
                }
                "edit" -> {
                    acaEditFragment.isEdit = true
                    transitFragment(acaEditFragment, R.id.edu_info_container, true)
                }
                "editTr" -> {
                    trainingEditFragment.isEdit = true
                    transitFragment(trainingEditFragment, R.id.edu_info_container, true)
                }
                "addProfessional" -> {
                    professionalQLEditFragment.isEdit = false
                    transitFragment(professionalQLEditFragment, R.id.edu_info_container, true)
                }
                "editProfessional" -> {
                    professionalQLEditFragment.isEdit = true
                    transitFragment(professionalQLEditFragment, R.id.edu_info_container, true)
                }
                else -> {

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    override fun goBack() {

        if (Constants.isDirectCall) finish()
        educationBaseCL.closeKeyboard(this@AcademicBaseActivity)
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

    override fun validateField(char: String, et: TextInputEditText, til: TextInputLayout): Boolean {
        when {
            TextUtils.isEmpty(char) -> {
                til.showError(getString(R.string.field_empty_error_message_common))
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

    override fun dataStorage(): DataStorage {
        dataStorage = DataStorage(this@AcademicBaseActivity)
        return dataStorage
    }


    override fun passProfessionalData(data: ProfessionalDataModel) {

        this.dataPrq = data

    }

    override fun getProfessionalData(): ProfessionalDataModel {
        return dataPrq
    }



}
