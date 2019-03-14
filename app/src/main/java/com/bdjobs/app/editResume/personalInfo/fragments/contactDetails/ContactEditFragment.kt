package com.bdjobs.app.editResume.personalInfo.fragments.contactDetails

import android.app.AlertDialog
import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.trimmedLength
import androidx.core.view.isVisible
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.Databases.External.LocationModel
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.adapters.models.C_DataItem
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_contact_edit.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ContactEditFragment : Fragment() {

    private lateinit var contactInfo: PersonalInfo
    private lateinit var session: BdjobsUserSession
    private lateinit var dataStorage: DataStorage
    private lateinit var data: C_DataItem
    private var presentInOutBD: String? = ""
    private var permanentInOutBD: String? = ""
    private var sameAddress: String = ""

    private lateinit var district: String
    private lateinit var thana: String
    private lateinit var postOffice: String
    private lateinit var address: String
    var districtList: ArrayList<LocationModel>? = null
    var thanaList: ArrayList<LocationModel>? = null
    var postOfficeList: ArrayList<LocationModel>? = null
    var thanaListPm: ArrayList<LocationModel>? = null
    var postOfficeListPm: ArrayList<LocationModel>? = null
    var locationID = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contact_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dataStorage = DataStorage(activity)
        session = BdjobsUserSession(activity)
        contactInfo = activity as PersonalInfo
        d("onActivityCreated")
        contactInfo.setTitle(getString(R.string.title_contact))
        contactInfo.setEditButton(false, "dd")
        initViews()
        doWork()
    }

    override fun onResume() {
        super.onResume()
        d("onResume")
        updateViewsData()
        preloadedData()
    }

    private fun updateViewsData() {

        districtList = dataStorage.getAllEnglishDistrictList()
        d("districtList ${districtList?.size} list ${districtList.toString()}")
        val districtNameList = arrayListOf<String>()
        districtList?.forEach { dt ->
            districtNameList.add(dt.locationName)
        }
        setDialog("Please Select your district", prContactDistrictTIET, districtNameList.toTypedArray())
        setDialog("Please Select your district", pmContactDistrictTIET, districtNameList.toTypedArray())
    }

    private fun initViews() {
        prContactDivTIET.addTextChangedListener(TW.CrossIconBehave(prContactDivTIET))
        prContactDistrictTIET.addTextChangedListener(TW.CrossIconBehave(prContactDistrictTIET))
        prContactThanaTIET.addTextChangedListener(TW.CrossIconBehave(prContactThanaTIET))
        prContactPostOfficeTIET1.addTextChangedListener(TW.CrossIconBehave(prContactPostOfficeTIET1))
        prContactAddressTIETPR.addTextChangedListener(TW.CrossIconBehave(prContactAddressTIETPR))
        presentContactCountryTIET.addTextChangedListener(TW.CrossIconBehave(presentContactCountryTIET))

        pmContactDivTIET1.addTextChangedListener(TW.CrossIconBehave(pmContactDivTIET1))
        pmContactDistrictTIET.addTextChangedListener(TW.CrossIconBehave(pmContactDistrictTIET))
        pmContactThanaTIETP.addTextChangedListener(TW.CrossIconBehave(pmContactThanaTIETP))
        pmContactPostOfficeTIET.addTextChangedListener(TW.CrossIconBehave(pmContactPostOfficeTIET))
        pmContactAddressTIETPRM.addTextChangedListener(TW.CrossIconBehave(pmContactAddressTIETPRM))
        permanentContactCountryTIETP.addTextChangedListener(TW.CrossIconBehave(permanentContactCountryTIETP))

        contactMobileNumberTIET.addTextChangedListener(TW.CrossIconBehave(contactMobileNumberTIET))
        contactMobileNumber1TIET.addTextChangedListener(TW.CrossIconBehave(contactMobileNumber1TIET))
        contactMobileNumber2TIET.addTextChangedListener(TW.CrossIconBehave(contactMobileNumber2TIET))
        contactEmailAddressTIET.addTextChangedListener(TW.CrossIconBehave(contactEmailAddressTIET))
        contactEmailAddressTIET1.addTextChangedListener(TW.CrossIconBehave(contactEmailAddressTIET1))
    }

    private fun doWork() {
        addTextChangedListener(prContactDivTIET, contactDivTIL)
        addTextChangedListener(prContactDistrictTIET, contactDistrictTIL1)
        addTextChangedListener(prContactThanaTIET, contactThanaTIL1)
        //addTextChangedListener(prContactPostOfficeTIET1, contactPostOfficeTIL1)
        addTextChangedListener(prContactAddressTIETPR, prContactAddressTILPR)
        addTextChangedListener(presentContactCountryTIET, presentContactCountryTIL)
        addTextChangedListener(prContactAddressTIETPR, prContactAddressTILPR)

        contactMobileNumberTIET.easyOnTextChangedListener {
            if (it.trimmedLength() >= 2)
                contactEmailAddressTIL.hideError() else contactEmailAddressTIL.isErrorEnabled = true
        }
        contactEmailAddressTIET.easyOnTextChangedListener {
            if (it.trimmedLength() >= 2)
                contactMobileNumberTIL.hideError() else contactMobileNumberTIL.isErrorEnabled = true
        }

        if (pmContactDivTIET1.getString() != "" || pmContactAddressTIETPRM.getString() != "") {
            addTextChangedListener(pmContactDivTIET1, contactDivTIL1)
            addTextChangedListener(pmContactDistrictTIET, contactDistrictTIL)
            addTextChangedListener(pmContactThanaTIETP, contactThanaTIL)
            //addTextChangedListener(pmContactPostOfficeTIET, contactPostOfficeTIL)
            addTextChangedListener(pmContactAddressTIETPRM, contactAddressTILPRM)
            addTextChangedListener(permanentContactCountryTIETP, presentContactCountryTILP)
            addTextChangedListener(contactMobileNumberTIET, contactMobileNumberTIL)
            addTextChangedListener(contactEmailAddressTIET, contactEmailAddressTIL)
        } else {
            contactDivTIL1.hideError()
            contactDistrictTIL.hideError()
            contactThanaTIL.hideError()
            contactAddressTILPRM.hideError()
            if (permanentInOutBD != "1") presentContactCountryTILP.hideError()
        }

        addressCheckbox.setOnCheckedChangeListener { _, isChecked ->
            sameAddress = if (isChecked) "on" else "off"
            if (isChecked) {
                //cgPermanent.clearCheck()
                llPermenantPortion.hide()
                cgPermanent.hide()
                pmContactDivTIET1.enableOrdisableEdit(false)
                pmContactDistrictTIET.enableOrdisableEdit(false)
                pmContactThanaTIETP.enableOrdisableEdit(false)
                pmContactPostOfficeTIET.enableOrdisableEdit(false)
                pmContactAddressTIETPRM.enableOrdisableEdit(false)
                pmContactDivTIET1.clear()
                pmContactDistrictTIET.clear()
                pmContactThanaTIETP.clear()
                pmContactPostOfficeTIET.clear()
                pmContactAddressTIETPRM.clear()
                permanentContactCountryTIETP.clear()
            } else {
                llPermenantPortion.show()
                cgPermanent.show()
                pmContactDivTIET1.enableOrdisableEdit(true)
                pmContactDistrictTIET.enableOrdisableEdit(true)
                pmContactThanaTIETP.enableOrdisableEdit(true)
                pmContactPostOfficeTIET.enableOrdisableEdit(true)
                pmContactAddressTIETPRM.enableOrdisableEdit(true)
            }
        }
        fab_contact_update.setOnClickListener {
            d("InOutBD : $presentInOutBD and $permanentInOutBD")
            clContactEdit.closeKeyboard(activity!!)

            var validation = 0
            when (presentInOutBD) {
                "0" -> {
                    validation = isValidate(prContactDivTIET, contactDivTIL, prContactDivTIET, true, validation)
                    validation = isValidate(prContactDistrictTIET, contactDistrictTIL1, prContactDistrictTIET, true, validation)
                    validation = isValidate(prContactThanaTIET, contactThanaTIL1, prContactThanaTIET, true, validation)
                    validation = isValidate(prContactAddressTIETPR, prContactAddressTILPR, prContactAddressTIETPR, true, validation)
                    Log.d("CValidaiton", "(out 1.1) value : $validation")
                }
                "1" -> {
                    validation = isValidate(presentContactCountryTIET, presentContactCountryTIL, presentContactCountryTIET, true, validation)
                    validation = isValidate(prContactAddressTIETPR, prContactAddressTILPR, prContactAddressTIETPR, true, validation)
                    Log.d("CValidaiton", "(out 1.2) value : $validation")
                }
            }

            when (permanentInOutBD) {
                "1" -> {
                    validation = isValidate(permanentContactCountryTIETP, presentContactCountryTILP, permanentContactCountryTIETP, true, validation)
                    validation = isValidate(pmContactAddressTIETPRM, contactAddressTILPRM, pmContactAddressTIETPRM, true, validation)
                    Log.d("CValidaiton", "(out 2.2) value : $validation")
                }
                "0" -> {
                    validation = isValidate(pmContactDivTIET1, contactDivTIL1, pmContactDivTIET1, true, validation)
                    validation = isValidate(pmContactDistrictTIET, contactDistrictTIL, pmContactDistrictTIET, true, validation)
                    validation = isValidate(pmContactThanaTIETP, contactThanaTIL, pmContactThanaTIETP, true, validation)
                    validation = isValidate(pmContactAddressTIETPRM, contactAddressTILPRM, pmContactAddressTIETPRM, true, validation)
                    Log.d("CValidaiton", "(out 2.1) value : $validation")
                }
            }

            if (contactEmailAddressTIET.getString().trim() == "") {
                validation = isValidate(contactMobileNumberTIET, contactMobileNumberTIL, contactMobileNumberTIET, true, validation)
            }
            if (contactMobileNumberTIET.getString().trim() == "") {
                validation = isValidate(contactEmailAddressTIET, contactEmailAddressTIL, contactEmailAddressTIET, true, validation)
            }
            if (presentInOutBD == "1") {
                /*prContactDivTIET.clear()
                prContactDistrictTIET.clear()
                prContactThanaTIET.clear()*/
                validation = isValidate(presentContactCountryTIET, presentContactCountryTIL, presentContactCountryTIET, true, validation)
            } /* else {
                toast("Please select Inside or Outside Bangladesh")
            }*/
            if (permanentInOutBD == "1") {
                /*pmContactDivTIET1.clear()
                pmContactDistrictTIET.clear()
                pmContactThanaTIETP.clear()*/
                validation = isValidate(permanentContactCountryTIETP, presentContactCountryTILP, permanentContactCountryTIETP, true, validation)
            } /*else if (permanentInOutBD == "" && pmContactAddressTIETPRM.getString().isBlank()) {
                toast("Please select Inside or Outside Bangladesh")
            }*/
            Log.d("checkValid", " val : $validation ")
            Log.d("checkValid", " val : $validation ")
            if (validation >= 2) updateData()
        }

        contactAddMobileButton?.setOnClickListener {
            checkAddMobileButtonState()
        }

        contactAddEmailButton?.setOnClickListener {
            contactEmailAddressTIL1.show()
            if (contactEmailAddressTIL1.isVisible)
                contactAddEmailButton.invisible()
            else {
                contactAddEmailButton.show()
            }
        }
        setupViews()
    }

    private fun checkAddMobileButtonState() {
        val mobile1 = contactMobileNumber1TIL.isVisible
        val mobile2 = contactMobileNumber2TIL.isVisible

        //if (count == 1) contactMobileNumber1TIL?.show() else contactMobileNumber2TIL?.show()
        if (mobile1 && mobile2) {
            Log.d("mobile1", "called")
            contactAddMobileButton?.hide()
        } else if (mobile1 && !mobile2) {
            contactMobileNumber2TIL?.show()
            contactAddMobileButton?.hide()
            Log.d("mobile2", "called")
        } else if (!mobile1 && mobile2) {
            Log.d("mobile3", "called")
            contactMobileNumber1TIL?.show()
            contactAddMobileButton?.hide()
        } else {
            Log.d("mobile4", "called")
            contactMobileNumber1TIL?.show()
            contactAddMobileButton?.show()
        }
    }

    private fun updateData() {

        activity.showProgressBar(loadingProgressBar)
        /*if (presentInOutBD == "" && prContactAddressTIETPR.getString().isNotBlank()) {
            activity?.stopProgressBar(loadingProgressBar)
            activity?.toast("Please select Inside or Outside Bangladesh")
        }
        if (pmContactAddressTIETPRM.getString().isNotBlank() && permanentInOutBD == "") {
            activity?.toast("Please select Inside or Outside Bangladesh")
            activity?.stopProgressBar(loadingProgressBar)
        }*/

        val presentAddressID = contactInfo.getContactData().presentAddressID
        val permanentAddressID = contactInfo.getContactData().permanentAddressID
        Log.d("ContactDetails", "PassingValue present in bd : $presentInOutBD " + "\n" +
                " district presrent  :  ${getIdByName(prContactDistrictTIET.getString(), districtList, "d")}" + "\n" +
                " thana parmanent :  ${getIdByName(prContactThanaTIET.getString(), thanaList, "t")} " + "\n" +
                " post office present : ${getIdByName(prContactPostOfficeTIET1.getString(), postOfficeList, "p")}" + "\n" +
                " addres present : ${prContactAddressTIETPR.getString()}" + "\n" +
                " country present : ${presentContactCountryTIET.getString()}" + "\n" +
                " presentInOutBD : $presentInOutBD" + "\n" +
                " permanentInOutBD : $permanentInOutBD" + "\n" +
                " district two parmanent : ${getIdByName(pmContactDistrictTIET.getString(), districtList, "dpm")}" + "\n" +
                " thana two parmanent : ${getIdByName(pmContactThanaTIETP.getString(), thanaListPm, "tpm")}" + "\n" +
                " post office two parmanent : ${getIdByName(pmContactPostOfficeTIET.getString(), postOfficeListPm, "ppm")}" + "\n" +
                " parmanent address parmanent : ${pmContactAddressTIETPRM.getString()}" + "\n" +
                " parmamnt country parmanent : ${permanentContactCountryTIETP.getString()}" + "\n" +
                " same address : $sameAddress" + "\n" +
                " permanentAddressID : $permanentAddressID " + "\n" +
                " presentAddressID :  $presentAddressID" + "\n" +
                " mobile number one : ${contactMobileNumberTIET.getString()}" + "\n" +
                " mobile number two : ${contactMobileNumber1TIET.getString()}" + "\n" +
                " mobile number three : ${contactMobileNumber2TIET.getString()}" + "\n" +
                " email ddree one : ${contactEmailAddressTIET.getString()}" + "\n" +
                " email address another: ${contactEmailAddressTIET1.getString()}")
        val call = ApiServiceMyBdjobs.create().updateContactData(userId = session.userId, decodeId = session.decodId, isResumeUpdate = session.IsResumeUpdate,
                inOut = presentInOutBD, present_district = getIdByName(prContactDistrictTIET.getString(), districtList, "d"), present_thana = getIdByName(prContactThanaTIET.getString(), thanaList, "t"),
                present_p_office = getIdByName(prContactPostOfficeTIET1.getString(), postOfficeList, "p"), present_Village = prContactAddressTIETPR.getString(),
                present_country_list = getIdByCountryName(presentContactCountryTIET.getString()), permInOut = permanentInOutBD, permanent_district = getIdByName(pmContactDistrictTIET.getString(), districtList, "dpm"),
                permanent_thana = getIdByName(pmContactThanaTIETP.getString(), thanaListPm, "tpm"), permanent_p_office = getIdByName(pmContactPostOfficeTIET.getString(), postOfficeListPm, "ppm"), permanent_Village = pmContactAddressTIETPRM.getString(),
                permanent_country_list = getIdByCountryName(permanentContactCountryTIETP.getString()), same_address = sameAddress, permanent_adrsID = permanentAddressID, present_adrsID = presentAddressID,
                officePhone = contactMobileNumber1TIET.getString(), mobile = contactMobileNumberTIET.getString(), homePhone = contactMobileNumber2TIET.getString(),
                email = contactEmailAddressTIET.getString(), alternativeEmail = contactEmailAddressTIET1.getString())
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                activity.stopProgressBar(loadingProgressBar)
                activity.toast(R.string.message_common_error)
                Log.d("contact_details", "msg: ${t.message}")

            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity.stopProgressBar(loadingProgressBar)

                        //if (response.body()?.statuscode != "2")
                        response.body()?.message?.let { activity.toast(it) }

                        if (response.body()?.statuscode == "4") {
                            session.updateEmail(contactEmailAddressTIET.getString())
                            contactInfo.goBack()
                            onDestroy()
                        }
                    } else {
                        activity.stopProgressBar(loadingProgressBar)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    logException(e)
                }
            }
        })
    }

    private fun getIdByName(s: String, list: ArrayList<LocationModel>?, tag: String): String {
        var id = ""
        if (!list.isNullOrEmpty()) {
            for ((_, value) in list.withIndex()) {
                if (value.locationName == s) {
                    id = value.locationId
                    Log.d("getIdByName", ",// ${value.locationId},//")
                }
            }
        } else {
            Log.d("getIdByNameElse", "$list,// $tag,//")
            when (tag) {
                "t" -> id = contactInfo.getThana().toString()
                "p" -> id = contactInfo.getPostOffice().toString()
                "tpm" -> id = contactInfo.getPmThana().toString()
                "ppm" -> id = contactInfo.getPmPostOffice().toString()
                else -> {
                }
            }
        }
        Log.d("locationID", "value: $id")
        return id
    }

    private fun getIdByCountryName(s: String): String {
        Log.d("locationID", "value: ${dataStorage.getLocationIDByName(s).toString().trim()}")
        return dataStorage.getLocationIDByName(s).toString().trim()
    }

    private fun addTextChangedListener(editText: TextInputEditText, inputLayout: TextInputLayout) {
        editText.easyOnTextChangedListener { charSequence ->
            contactInfo.validateField(charSequence.toString(), editText, inputLayout)
        }
    }

    private fun preloadedData() {
        data = contactInfo.getContactData()
        getDataFromChipGroup(cgPermanent)
        getDataFromChipGroup(cgPresent)
        val addressType = data.addressType1
        val homePhone = data.homePhone
        val officePhone = data.officePhone

        this.addressCheckbox.isChecked = addressType == "3"

        //if (data.permanentInsideOutsideBD == "") cgPermanent.clearCheck()

        if (officePhone.isNullOrBlank()) contactAddMobileButton.show() else contactAddMobileButton.hide()

        prContactDistrictTIET?.setText(dataStorage.getLocationNameByID(data.presentDistrict))
        d("dis : ${data.presentDistrict}")
        prContactThanaTIET?.setText(dataStorage.getLocationNameByID(data.presentThana))
        d("thana : ${data.presentThana}")
        contactInfo.setThana(data.presentThana)
        when {
            data.presentPostOffice != "0" -> {
                prContactPostOfficeTIET1?.setText(dataStorage.getLocationNameByID(data.presentPostOffice))
                contactInfo.setPostOffice(data.presentPostOffice)
            }
            data.permanentPostOffice == "-2" -> prContactPostOfficeTIET1?.setText(getString(R.string.hint_post_office_other))
            else -> prContactPostOfficeTIET1?.setText("")
        }
        d("postOffice : ${data.presentPostOffice}")
        prContactAddressTIETPR?.setText(data.presentVillage)
        val prDiv = dataStorage.getDivisionNameByDistrictName(dataStorage.getLocationNameByID(data.presentDistrict).toString())
        d("division : $prDiv")

        prContactDivTIET?.setText(prDiv)
        d("division : ${dataStorage.getDivisionNameByDistrictName(data.presentDistrict.toString())}")
        if (data.presentCountry != "118") presentContactCountryTIET?.setText(dataStorage.getLocationNameByID(data.presentCountry))
        // Permenant
        pmContactDistrictTIET?.setText(dataStorage.getLocationNameByID(data.permanentDistrict))
        pmContactThanaTIETP?.setText(dataStorage.getLocationNameByID(data.permanentThana))
        contactInfo.setPmThana(data.permanentThana)
        when {
            data.permanentPostOffice != "0" -> {
                pmContactPostOfficeTIET?.setText(dataStorage.getLocationNameByID(data.permanentPostOffice))
                contactInfo.setPmPostOffice(data.permanentPostOffice)
            }
            data.permanentPostOffice == "-2" -> pmContactPostOfficeTIET?.setText(getString(R.string.hint_post_office_other))
            else -> pmContactPostOfficeTIET?.setText("")
        }
        pmContactAddressTIETPRM?.setText(data.permanentVillage)
        if (data.permanentCountry != "118") permanentContactCountryTIETP?.setText(dataStorage.getLocationNameByID(data.permanentCountry))

        contactMobileNumberTIET?.setText(data.mobile)
        contactEmailAddressTIET?.setText(data.email)

        if (!homePhone?.isEmpty()!!) {
            contactMobileNumber2TIET?.setText(data.homePhone)
            contactMobileNumber2TIL?.show()
        } else {
            contactMobileNumber2TIET?.clear()
            contactMobileNumber2TIL?.hide()
        }
        if (!officePhone?.isEmpty()!!) {
            contactMobileNumber1TIL?.show()
            contactMobileNumber1TIET?.setText(data.officePhone)
        } else {
            contactMobileNumber1TIET?.clear()
            contactMobileNumber1TIL?.hide()
        }

        if (homePhone.isEmpty() || officePhone.isEmpty()) {
            contactAddMobileButton.show()
        } else
            contactAddMobileButton.hide()

        if (!data.alternativeEmail?.isEmpty()!!) {
            contactEmailAddressTIL1?.show()
            contactAddEmailButton.invisible()
            contactEmailAddressTIET1?.setText(data.alternativeEmail)
        } else {
            contactEmailAddressTIL1?.hide()
            contactAddEmailButton.show()
        }
        if (data.presentInsideOutsideBD == "False") {
            selectChip(cgPresent, "Inside Bangladesh")
            presentInOutBD = "0"
            presentInsideBangladeshLayout1.show()
            presentOutsideBangladeshLayout.hide()
        } else if (data.presentInsideOutsideBD == "True") {
            selectChip(cgPresent, "Outside Bangladesh")
            presentInOutBD = "1"
            //presentContactCountryTIET.clear()
            presentInsideBangladeshLayout1.hide()
            presentOutsideBangladeshLayout.show()
        }
        if (data.permanentInsideOutsideBD == "False") {
            selectChip(cgPermanent, "Inside Bangladesh")
            permanentInOutBD = "0"
            presentInsideBangladeshLayout.show()
            presentOutsideBangladeshLayoutP.hide()
        } else if (data.permanentInsideOutsideBD == "True") {
            selectChip(cgPermanent, "Outside Bangladesh")
            permanentInOutBD = "1"
            //permanentContactCountryTIETP.clear()
            presentInsideBangladeshLayout.hide()
            presentOutsideBangladeshLayoutP.show()
        }
    }

    private fun setupViews() {
        ////Present Address---------------Start
        presentContactCountryTIET.setOnClickListener {

            val countryList: Array<String> = dataStorage.allCountries

            activity?.selector("Please select your country ", countryList.toList()) { _, i ->

                presentContactCountryTIET.setText(countryList[i])
                presentContactCountryTIL.requestFocus()

            }

        }

        ////Parmanent Address---------------Start
        permanentContactCountryTIETP.setOnClickListener {
            val countryList: Array<String> = dataStorage.allCountries
            activity?.selector("Please select your country ", countryList.toList()) { _, i ->
                permanentContactCountryTIETP.setText(countryList[i])
                presentContactCountryTILP.requestFocus()
            }
        }
        presentContactCountryTIET.setOnClickListener {


            val countryList: Array<String> = dataStorage.allCountries

            activity?.selector("Please select your country ", countryList.toList()) { _, i ->

                presentContactCountryTIET.setText(countryList[i])
                presentContactCountryTIL.requestFocus()
            }
        }
        permanentContactCountryTIETP.setOnClickListener {
            val countryList: Array<String> = dataStorage.allCountries
            activity?.selector("Please select your country ", countryList.toList()) { _, i ->
                permanentContactCountryTIETP.setText(countryList[i])
                presentContactCountryTILP.requestFocus()
            }
        }
    }

    private fun getDataFromChipGroup(cg: ChipGroup) {
        cg.setOnCheckedChangeListener { chipGroup, i ->
            //val chip = chipGroup.getChildAt(chipGroup.checkedChipId) as Chip
            val chip = chipGroup.findViewById(i) as Chip
            cg.radioCheckableChip(chip)
            if (i > 0) {
                //val chip = chipGroup.findViewById(i) as Chip
                Log.d("chip_entry", "text: ${chip.text}")
                val dataC = chip.text.toString()
                when (chipGroup.id) {
                    R.id.cgPresent -> {
                        val chips = when (dataC) {
                            "Inside Bangladesh" -> {
                                presentInsideBangladeshLayout1.show()
                                presentOutsideBangladeshLayout.hide()
                                "0"
                            }
                            "Outside Bangladesh" -> {
                                //presentContactCountryTIET.clear()
                                presentInsideBangladeshLayout1.hide()
                                presentOutsideBangladeshLayout.show()
                                "1"
                            }
                            else -> ""
                        }
                        debug("value : $chips")
                        presentInOutBD = chips
                        d("value : $presentInOutBD and $permanentInOutBD")

                    }
                    R.id.cgPermanent -> {
                        val chips = when (dataC) {
                            "Inside Bangladesh" -> {
                                presentInsideBangladeshLayout.show()
                                presentOutsideBangladeshLayoutP.hide()
                                "0"
                            }
                            "Outside Bangladesh" -> {
                                //permanentContactCountryTIETP.clear()
                                presentInsideBangladeshLayout.hide()
                                presentOutsideBangladeshLayoutP.show()
                                "1"
                            }
                            else -> ""
                        }
                        debug("valuep : $chips")
                        permanentInOutBD = chips
                        d("value : $presentInOutBD and $permanentInOutBD")
                    }
                }
            } else {
                when (chipGroup.id) {
                    R.id.cgPresent -> {
                        presentInOutBD = ""
                        activity?.toast("Please select Inside Bangladesh or Outside Bangladesh!")
                        d("valueD : $presentInOutBD and $permanentInOutBD")
                    }
                    R.id.cgPermanent -> {
                        permanentInOutBD = ""
                        //activity?.toast("Please select Inside Bangladesh or Outside Bangladesh!")
                        d("valuepD : $presentInOutBD and $permanentInOutBD")
                    }
                }
            }
        }
    }

    private fun selectChip(chipGroup: ChipGroup, data: String) {
        val count = chipGroup.childCount
        for (i in 0 until count) {
            val chip = chipGroup.getChildAt(i) as Chip
            val chipText = chip.text.toString()
            chip.isClickable = true
            if (data.equalIgnoreCase(chipText)) {
                Log.d("chip_entry", "text:$i")
                chip.isChecked = true
                d("value t/f : ${chip.isChecked}")
            }
        }
    }

    private fun setDialog(title: String, editText: TextInputEditText, data: Array<String>) {
        editText.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(title)
                    .setItems(data
                    ) { dialog, which ->
                        editText.setText(data[which])

                        var thanaId = ""
                        var postOfficeId = ""

                        if (editText.id == R.id.prContactDistrictTIET) {
                            prContactThanaTIET?.clear()
                            prContactPostOfficeTIET1?.clear()
                            prContactThanaTIET?.setOnClickListener(null)
                            prContactPostOfficeTIET1?.setOnClickListener(null)

                            thanaList = dataStorage.getDependentEnglishLocationByParentId(districtList?.get(which)?.locationId!!)
                            val thanaNameList = arrayListOf<String>()
                            thanaList?.forEach { dt ->
                                thanaNameList.add(dt.locationName)
                            }
                            setDialog("Please Select your post office", prContactThanaTIET, thanaNameList.toTypedArray())
                        }
                        if (editText.id == R.id.prContactThanaTIET) {
                            prContactPostOfficeTIET1?.clear()
                            prContactPostOfficeTIET1?.setOnClickListener(null)

                            thanaId = thanaList?.get(which)?.locationId!!
                            contactInfo.setThana(thanaId)

                            postOfficeList = dataStorage.getDependentEnglishLocationByParentId(thanaList?.get(which)?.locationId!!)

                            val pstOfficeNameList = arrayListOf<String>()
                            if (pstOfficeNameList.isNullOrEmpty()) {
                                val otherLocation = LocationModel("Other", "-2")
                                postOfficeList?.add(otherLocation)
                            }
                            postOfficeList?.forEach { dt ->
                                pstOfficeNameList.add(dt.locationName)
                            }
                            setDialog("Please Select your police station", prContactPostOfficeTIET1, pstOfficeNameList.toTypedArray())

                        }
                        if (editText.id == R.id.prContactPostOfficeTIET1) {

                            postOfficeId = postOfficeList?.get(which)?.locationId!!
                            contactInfo.setPostOffice(postOfficeId)
                            postOffice = prContactPostOfficeTIET1.getString()
                            locationID = if (postOffice.equals("Other", ignoreCase = true) || TextUtils.isEmpty(postOffice)) {
                                thanaId
                            } else {
                                postOfficeId
                            }
                        }

                        if (editText.id == R.id.pmContactDistrictTIET) {
                            pmContactThanaTIETP?.clear()
                            pmContactPostOfficeTIET?.clear()
                            pmContactThanaTIETP?.setOnClickListener(null)
                            pmContactPostOfficeTIET?.setOnClickListener(null)

                            thanaListPm = dataStorage.getDependentEnglishLocationByParentId(districtList?.get(which)?.locationId!!)
                            val thanaNameList = arrayListOf<String>()

                            thanaListPm?.forEach { dt ->
                                thanaNameList.add(dt.locationName)
                            }
                            setDialog("Please Select your post office", pmContactThanaTIETP, thanaNameList.toTypedArray())
                        }
                        if (editText.id == R.id.pmContactThanaTIETP) {
                            pmContactPostOfficeTIET?.clear()
                            pmContactPostOfficeTIET?.setOnClickListener(null)

                            thanaId = thanaListPm?.get(which)?.locationId!!
                            contactInfo.setPmThana(thanaId)
                            postOfficeListPm = dataStorage.getDependentEnglishLocationByParentId(thanaListPm?.get(which)?.locationId!!)
                            val pstOfficeNameList = arrayListOf<String>()
                            if (pstOfficeNameList.isNullOrEmpty()) {
                                val otherLocation = LocationModel("Other", "-2")
                                postOfficeListPm?.add(otherLocation)
                            }
                            postOfficeListPm?.forEach { dt ->
                                pstOfficeNameList.add(dt.locationName)
                            }
                            setDialog("Please Select your police station", pmContactPostOfficeTIET, pstOfficeNameList.toTypedArray())
                        }
                        if (editText.id == R.id.pmContactPostOfficeTIET) {

                            postOfficeId = postOfficeListPm?.get(which)?.locationId!!
                            contactInfo.setPmPostOffice(postOfficeId)
                            postOffice = pmContactPostOfficeTIET.getString()
                            locationID = if (postOffice.equals("Other", ignoreCase = true) || TextUtils.isEmpty(postOffice)) {
                                thanaId
                            } else {
                                postOfficeId
                            }
                        }
                    }
            val dialog = builder.create()
            dialog.show()
        }
    }
}
