package com.bdjobs.app.editResume.otherInfo

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import com.bdjobs.app.API.ModelClasses.AddExpModel
import com.bdjobs.app.ads.Ads
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.utilities.*
import com.bdjobs.app.editResume.adapters.models.LanguageDataModel
import com.bdjobs.app.editResume.adapters.models.ReferenceDataModel
import com.bdjobs.app.editResume.adapters.models.Skill
import com.bdjobs.app.editResume.adapters.models.SpecializationDataModel
import com.bdjobs.app.editResume.callbacks.OtherInfo
import com.bdjobs.app.editResume.otherInfo.fragments.languagePref.LangPrViewFragment
import com.bdjobs.app.editResume.otherInfo.fragments.languagePref.LangProficiencyEditFragment
import com.bdjobs.app.editResume.otherInfo.fragments.referances.ReferenceEditFragment
import com.bdjobs.app.editResume.otherInfo.fragments.referances.ReferencesViewFragment
import com.bdjobs.app.editResume.otherInfo.fragments.specializations.SpecializationNewEditFragment
import com.bdjobs.app.editResume.otherInfo.fragments.specializations.SpecializationNewViewFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_other_info_base.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

class OtherInfoBaseActivity : Activity(), OtherInfo,
    ConnectivityReceiver.ConnectivityReceiverListener {
    override fun setExtraCurricularActivity(extra: String) {
        extraCuri = extra
    }

    override fun getExtraCurricularActivity(): String? {
        return extraCuri
    }

    override fun setSkillDescription(desc: String) {
        skillDescription = desc
    }

    override fun getSkillDescription(): String? {
        return  skillDescription
    }

    override fun setSkills(skills: ArrayList<Skill?>) {
        this.skills = skills
    }

    override fun getSkills(): ArrayList<Skill?>? {
        return skills
    }

    override fun getReferenceList(): ArrayList<ReferenceDataModel>? {
        return this.referenceList
    }

    override fun setReferenceList(ref: ArrayList<ReferenceDataModel>) {
        this.referenceList = ref
    }

    override fun setLanguageList(lan: ArrayList<LanguageDataModel>) {
        this.languageList = lan
    }

    override fun getLanguageList(): ArrayList<LanguageDataModel>? {
        return this.languageList
    }


    private var skills = ArrayList<Skill?>()

    private var skillList = ArrayList<AddExpModel>()
    private var skillDescription = ""
    private var extraCuri = ""

    override fun passSpecializationDataNew(
        data: ArrayList<AddExpModel>?,
        SkillDes: String,
        extraCuricular: String
    ) {
        skillList = data!!
        skillDescription = SkillDes
        extraCuri = extraCuricular
    }


    override fun getSpecializationDataNew(): ArrayList<AddExpModel>? {

        return skillList
    }

    override fun getSkillDes(): String? {
        return skillDescription
    }

    override fun getExtraCuri(): String? {
        return extraCuri
    }


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

    private val specializationNewEditFragment = SpecializationNewEditFragment()
    private val specializationNewViewFragment = SpecializationNewViewFragment()

    private var dataReference: ReferenceDataModel = ReferenceDataModel()
    private var dataLanguage: LanguageDataModel = LanguageDataModel()
    private var dataSpecialization: SpecializationDataModel = SpecializationDataModel()
    private lateinit var dataStorage: DataStorage
    lateinit var name: String
    lateinit var gotToAddOtherInfo: String

    private var clickPosition = -1

    private var languageList: ArrayList<LanguageDataModel>? = null
    private var referenceList: ArrayList<ReferenceDataModel>? = null

//    private lateinit var extraCuricularActivity : String
//    private lateinit var skillDescription : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentValues()
        setContentView(R.layout.activity_other_info_base)
        gotToFragment(name)
//        val adRequest = AdRequest.Builder().build()
//        adViewOtherInfo?.loadAd(adRequest)
        //fjkhgfhfh
        Ads.loadAdaptiveBanner(this@OtherInfoBaseActivity,adViewOtherInfo)


    }

    override fun onPostResume() {
        super.onPostResume()
        registerReceiver(
            internetBroadCastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(internetBroadCastReceiver)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            mSnackBar = Snackbar
                .make(
                    cl_otherInfo_base,
                    getString(R.string.alert_no_internet),
                    Snackbar.LENGTH_INDEFINITE
                )
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
            "specialization" -> transitFragment(
                specializationNewViewFragment,
                R.id.other_info_container,
                false
            )
            "language" -> {
                iv_OI_delete_data.hide()
                transitFragment(languageViewFragment, R.id.other_info_container, false)
            }
            "reference" -> transitFragment(referenceViewFrgament, R.id.other_info_container, false)
        }
    }


    private fun getIntentValues() {
        try {
            name = intent.getStringExtra("name").toString()
            gotToAddOtherInfo = intent.getStringExtra("other_info_add").toString()
            goToEditInfo(gotToAddOtherInfo)
        } catch (e: Exception) {
            logException(e)
        }
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
                    alert("Are you sure you want to delete?", "Delete") {
                        yesButton { languageEditFrgamnet.dataDelete()}
                        noButton {}
                    }.show()

                }
                if (refernceEditFragment.isEdit) {
                    alert("Are you sure you want to delete?", "Delete") {
                        yesButton {  refernceEditFragment.dataDelete()}
                        noButton {}
                    }.show()
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
                    specializationNewEditFragment.isEdit = false
                    transitFragment(specializationNewViewFragment, R.id.other_info_container, false)
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
                    specializationNewEditFragment.isEdit = false
                    transitFragment(specializationNewEditFragment, R.id.other_info_container, true)

                }
                "editSpecialization" -> {
                    specializationNewEditFragment.isEdit = true
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

        if (Constants.isDirectCall) finish()
        cl_otherInfo_base.closeKeyboard(this@OtherInfoBaseActivity)
        onBackPressed()
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

    override fun showEditDialog(item: Skill?) {
        specializationNewViewFragment.showEditDialog(item)
    }

    override fun confirmationPopup(deleteItem: String) {
        specializationNewViewFragment.confirmationPopUp(deleteItem)
    }


}
