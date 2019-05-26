package com.bdjobs.app.editResume.otherInfo

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import com.bdjobs.app.API.ModelClasses.AddExpModel
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.LanguageDataModel
import com.bdjobs.app.editResume.adapters.models.ReferenceDataModel
import com.bdjobs.app.editResume.adapters.models.SpecializationDataModel
import com.bdjobs.app.editResume.callbacks.OtherInfo
import com.bdjobs.app.editResume.otherInfo.fragments.languagePref.LangPrViewFragment
import com.bdjobs.app.editResume.otherInfo.fragments.languagePref.LangProficiencyEditFragment
import com.bdjobs.app.editResume.otherInfo.fragments.referances.ReferenceEditFragment
import com.bdjobs.app.editResume.otherInfo.fragments.referances.ReferencesViewFragment
import com.bdjobs.app.editResume.otherInfo.fragments.specializations.SpecializationEditFragment
import com.bdjobs.app.editResume.otherInfo.fragments.specializations.SpecializationNewEditFragment
import com.bdjobs.app.editResume.otherInfo.fragments.specializations.SpecializationNewViewFragment
import com.bdjobs.app.editResume.otherInfo.fragments.specializations.SpecializationViewFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_other_info_base.*

class OtherInfoBaseActivity : Activity(), OtherInfo, ConnectivityReceiver.ConnectivityReceiverListener {


    override fun getBackFrom(): String? {
        return this.fragmentFrom
    }

    override fun setBackFrom(from: String?) {
        this.fragmentFrom = from
    }

    private var fragmentFrom: String? = "first"
    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null
    private val languageEditFrgamnet = LangProficiencyEditFragment()
    private val languageViewFragment = LangPrViewFragment()
    private val refernceEditFragment = ReferenceEditFragment()
    private val referenceViewFrgament = ReferencesViewFragment()

    private val specializationEditFragment = SpecializationEditFragment()
    private val specializationViewFragment = SpecializationViewFragment()

    private val specializationNewEditFragment = SpecializationNewEditFragment()
    private val specializationNewViewFragment = SpecializationNewViewFragment()

    private lateinit var dataReference: ReferenceDataModel
    private lateinit var dataLanguage: LanguageDataModel
    private lateinit var dataSpecialization: SpecializationDataModel
    private lateinit var dataStorage: DataStorage
    lateinit var name: String
    lateinit var gotToAddOtherInfo: String

    private var clickPosition = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentValues()
        setContentView(R.layout.activity_other_info_base)
        gotToFragment(name)
        //fjkhgfhfh

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


    private fun gotToFragment(name: String) {
        when (name) {
            "specialization" -> transitFragment(specializationNewViewFragment, R.id.other_info_container, false)
            "language" -> {
                iv_OI_delete_data.hide()
                transitFragment(languageViewFragment, R.id.other_info_container, false)
            }
            "reference" -> transitFragment(referenceViewFrgament, R.id.other_info_container, false)
        }
    }


    private fun getIntentValues() {
        name = intent.getStringExtra("name")
        gotToAddOtherInfo = intent.getStringExtra("other_info_add")
        goToEditInfo(gotToAddOtherInfo)
    }


    override fun dataStorage(): DataStorage {
        dataStorage = DataStorage(this@OtherInfoBaseActivity)
        return dataStorage
    }

    private fun setupToolbar(title: String?) {
        tv_otherInfo_title?.text = title
        tb_otherInfo?.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        tb_otherInfo?.setNavigationOnClickListener {
            goBack()
        }
    }


    override fun setTitle(tit: String?) {
        setupToolbar(tit)
    }

    override fun setDeleteButton(b: Boolean) {
        if (b) {
            iv_OI_delete_data?.show()
            iv_OI_delete_data?.setOnClickListener {
                if (languageEditFrgamnet.isEdit) {
                    languageEditFrgamnet.dataDelete()
                }
                if (refernceEditFragment.isEdit) {
                    refernceEditFragment.dataDelete()
                }
            }
        } else {
            iv_OI_delete_data?.hide()
        }

    }


    override fun setEditButton(b: Boolean) {

        if (b) {
            iv_OI_delete_data?.setImageResource(R.drawable.specialization_edit_icon)
            iv_OI_delete_data?.show()
            iv_OI_delete_data?.setOnClickListener {
                goToEditInfo("editSpecialization")
                setEditButton(false)

            }
        } else {
            iv_OI_delete_data?.hide()

        }


    }

    override fun goToEditInfo(check: String) {

        try {
            when (check) {
                "addDirect" -> {
                    specializationEditFragment.isEdit = false
                    transitFragment(specializationNewEditFragment, R.id.other_info_container, false)
                    Constants.isDirectCall = true
                }
                "addLanguage" -> {
                    languageEditFrgamnet.isEdit = false
                    transitFragment(languageEditFrgamnet, R.id.other_info_container, true)
                     
                }
                "editLanguage" -> {
                    languageEditFrgamnet.isEdit = true
                    transitFragment(languageEditFrgamnet, R.id.other_info_container, true)

                }
                "addSpecialization" -> {
                    specializationEditFragment.isEdit = false
                    transitFragment(specializationNewEditFragment, R.id.other_info_container, true)

                }
                "editSpecialization" -> {
                    specializationEditFragment.isEdit = true
                    transitFragment(specializationNewEditFragment, R.id.other_info_container, true)

                }
                "addReference" -> {
                    refernceEditFragment.isEdit = false
                    transitFragment(refernceEditFragment, R.id.other_info_container, true)

                }
                "editReference" -> {
                    refernceEditFragment.isEdit = true
                    transitFragment(refernceEditFragment, R.id.other_info_container, true)

                }
                else -> {

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }


    }


    override fun passSpecializationData(data: SpecializationDataModel) {

        this.dataSpecialization = data

    }

    override fun getSpecializationData(): SpecializationDataModel {

        return dataSpecialization
    }


    override fun passReferenceData(data: ReferenceDataModel) {

        this.dataReference = data

    }


    override fun validateField(char: String, et: TextInputEditText, til: TextInputLayout): Boolean {
        when {
            TextUtils.isEmpty(char) -> {
                til.showError(getString(R.string.field_empty_error_message_common))
                return false
            }
            else -> til.hideError()
        }
        return true
    }

    override fun goBack() {
        onBackPressed()
        if (Constants.isDirectCall) finish()
        cl_otherInfo_base.closeKeyboard(this@OtherInfoBaseActivity)

    }

    override fun passLanguageData(data: LanguageDataModel) {

        this.dataLanguage = data

    }


    override fun getLanguageData(): LanguageDataModel {
        return dataLanguage
    }


    override fun getReferenceData(): ReferenceDataModel {
        return dataReference
    }




    override fun setItemClick(position: Int) {
       clickPosition = position
    }

    override fun getItemClick(): Int {
       return clickPosition
    }

    override fun showEditDialog(item: AddExpModel) {
        specializationNewEditFragment.showEditDialog(item)
    }

}
