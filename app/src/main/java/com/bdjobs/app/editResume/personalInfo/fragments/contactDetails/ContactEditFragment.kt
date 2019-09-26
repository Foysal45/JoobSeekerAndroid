package com.bdjobs.app.editResume.personalInfo.fragments.contactDetails

import android.app.AlertDialog
import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.text.trimmedLength
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
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
import kotlinx.android.synthetic.main.jobdetail_item_list.*
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
    private lateinit var postOffice: String
    var districtList: ArrayList<LocationModel>? = null
    var thanaList: ArrayList<LocationModel>? = null
    var postOfficeList: ArrayList<LocationModel>? = null
    var thanaListPm: ArrayList<LocationModel>? = null
    var postOfficeListPm: ArrayList<LocationModel>? = null
    var locationID = ""

    var countryNameAndCode = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
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
        hideAllError()
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

        thanaList = dataStorage.getDependentEnglishLocationByParentId(contactInfo.getPresentDistrict()!!)
        val thanaNameList = arrayListOf<String>()
        thanaList?.forEach { dt ->
            thanaNameList.add(dt.locationName)
        }


        postOfficeList = dataStorage.getDependentEnglishLocationByParentId(contactInfo.getThana()!!)
        val pstOfficeNameList = arrayListOf<String>()
        if (pstOfficeNameList.isNullOrEmpty()) {
            val otherLocation = LocationModel("Other", "-2")
            postOfficeList?.add(otherLocation)
        }
        postOfficeList?.forEach { dt ->
            pstOfficeNameList.add(dt.locationName)
        }


        postOfficeListPm = dataStorage.getDependentEnglishLocationByParentId(contactInfo.getPmThana()!!)
        val permanentPostOfficeList = arrayListOf<String>()
        if (permanentPostOfficeList.isNullOrEmpty()) {
            val otherLocation = LocationModel("Other", "-2")
            postOfficeListPm?.add(otherLocation)
        }
        postOfficeListPm?.forEach { dt ->
            permanentPostOfficeList.add(dt.locationName)
        }


        pmContactPostOfficeTIET?.clear()
        pmContactPostOfficeTIET?.setOnClickListener(null)


        thanaListPm = dataStorage.getDependentEnglishLocationByParentId(contactInfo?.getPermanentDistrict()!!)
        val permanentThanaList = arrayListOf<String>()
        if (permanentThanaList.isNullOrEmpty()) {
            val otherLocation = LocationModel("Other", "-2")
            thanaListPm?.add(otherLocation)
        }
        thanaListPm?.forEach { dt ->
            permanentThanaList.add(dt.locationName)
        }


        setDialog("Please select your district", prContactDistrictTIET, districtNameList.toTypedArray())
        setDialog("Please select your district", pmContactDistrictTIET, districtNameList.toTypedArray())
        setDialog("Please select your thana", prContactThanaTIET, thanaNameList.toTypedArray())
        setDialog("Please select your thana", pmContactThanaTIETP, permanentThanaList.toTypedArray())
        setDialog("Please select your post office", prContactPostOfficeTIET1, pstOfficeNameList.toTypedArray())
        setDialog("Please select your post office", pmContactPostOfficeTIET, permanentPostOfficeList.toTypedArray())


    }

    private fun initViews() {
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

        val countryList: Array<String> = dataStorage.allCountryAndCountryCode

        try {
            countryList?.let {
                for (item in it) {
                    if (item.substringAfter("(").substringBefore(")") == contactInfo.getContactData().countryCode) {
                        countryNameAndCode = item
                        break
                    }
                }
            }
        } catch (e: Exception) {
            logException(e)
        }

        countryCodeTIET?.setOnClickListener {

            Log.d("rakb", contactInfo.getContactData().countryCode)
            activity.selector("Select your country", countryList.toList()) { dialogInterface, i ->
                countryCodeTIET?.setText(countryList[i])
                mobileNumberValidityCheck(contactMobileNumberTIET.text.toString())
                val countryCode: String
                val countryNameAndCountryCode = countryList[i]
                val inputData = countryNameAndCountryCode.split("[\\(||//)]".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                countryCode = inputData[inputData.size - 1].trim({ it <= ' ' })
            }
        }

    }

    private fun doWork() {
        addTextChangedListener(prContactDistrictTIET, contactDistrictTIL1)
        addTextChangedListener(prContactThanaTIET, contactThanaTIL1)
        //addTextChangedListener(prContactPostOfficeTIET1, contactPostOfficeTIL1)


        addTextChangedListener(prContactAddressTIETPR, prContactAddressTILPR)

        addTextChangedListener(presentContactCountryTIET, presentContactCountryTIL)

        addTextChangedListener(pmContactDistrictTIET, contactDistrictTIL)
        addTextChangedListener(pmContactThanaTIETP, contactThanaTIL)
        addTextChangedListener(pmContactAddressTIETPRM, contactAddressTILPRM)
        addTextChangedListener(permanentContactCountryTIETP, permanentContactCountryTILP)

        //addTextChangedListener(contactMobileNumber1TIET, contactEmailAddressTIL1)

        contactMobileNumberTIET.easyOnTextChangedListener {
            mobileNumberValidityCheck(it.toString())
            if (it.trimmedLength() >= 2)
                contactEmailAddressTIL.hideError()
            else
                contactEmailAddressTIL.isErrorEnabled = true
        }
        contactEmailAddressTIET.easyOnTextChangedListener {
            emailValidityCheck(it.toString(), contactEmailAddressTIET, contactEmailAddressTIL)
            if (it.trimmedLength() >= 2)
                contactMobileNumberTIL.hideError()
            else
                contactMobileNumberTIL.isErrorEnabled = true
        }

//        contactEmailAddressTIET?.easyOnTextChangedListener { charSequence ->
//            emailValidityCheck(charSequence.toString())
//        }

        contactEmailAddressTIET1.addTextChangedListener {
            emailValidityCheck(it.toString(), contactEmailAddressTIET1, contactEmailAddressTIL1)

        }

        if (pmContactAddressTIETPRM.getString().isNotEmpty()) {
            Log.d("rakib", "ulta palta if")
            addTextChangedListener(pmContactDistrictTIET, contactDistrictTIL)
            addTextChangedListener(pmContactThanaTIETP, contactThanaTIL)
            //addTextChangedListener(pmContactPostOfficeTIET, contactPostOfficeTIL)
            addMobileValidation(pmContactAddressTIETPRM, contactAddressTILPRM)
            addTextChangedListener(permanentContactCountryTIETP, permanentContactCountryTILP)
//            addTextChangedListener(contactMobileNumberTIET, contactMobileNumberTIL)
//            addTextChangedListener(contactEmailAddressTIET, contactEmailAddressTIL)
        } else {
            Log.d("rakib", "ulta palta else")
            contactDistrictTIL.hideError()
            contactThanaTIL.hideError()
            contactAddressTILPRM.hideError()
            if (permanentInOutBD != "1" || permanentContactCountryTIETP.getString().isNotEmpty())
                permanentContactCountryTILP.hideError()
        }

        if (contactMobileNumberTIET.getString().isNotEmpty()) {
            Log.d("rakib", "mobile not empty")
            contactEmailAddressTIL.hideError()
        } else {
            Log.d("rakib", "mobile empty")
            contactEmailAddressTIL.isErrorEnabled = true
        }
        if (contactEmailAddressTIET.getString().isNotEmpty()) {
            Log.d("rakib", "email not empty")
            contactMobileNumberTIL.hideError()
        } else {
            Log.d("rakib", "email empty")
            contactMobileNumberTIL.isErrorEnabled = true
        }

        addressCheckbox.setOnCheckedChangeListener { _, isChecked ->
            sameAddress = if (isChecked) "on" else "off"
            if (isChecked) {
//                cgPermanent.clearCheck()
                llPermenantPortion.hide()
                cgPermanent.hide()
                // hideAllError()
                pmContactDivTIET1.enableOrdisableEdit(false)
                pmContactDistrictTIET.enableOrdisableEdit(false)
                pmContactThanaTIETP.enableOrdisableEdit(false)
                pmContactPostOfficeTIET.enableOrdisableEdit(false)
                pmContactAddressTIETPRM.enableOrdisableEdit(false)
                /*pmContactDivTIET1.clear()
                pmContactDistrictTIET.clear()
                pmContactThanaTIETP.clear()
                pmContactPostOfficeTIET.clear()
                pmContactAddressTIETPRM.clear()
                permanentContactCountryTIETP.clear()*/
            } else {
                cgPermanent.check(R.id.insideP)
                llPermenantPortion.show()
                cgPermanent.show()
                hideAllError()
                //pmContactDivTIET1.enableOrdisableEdit(true)
                pmContactDistrictTIET.enableOrdisableEdit(true)
                pmContactThanaTIETP.enableOrdisableEdit(true)
                pmContactPostOfficeTIET.enableOrdisableEdit(true)
                pmContactAddressTIETPRM.enableOrdisableEdit(true)
            }
        }
        fab_contact_update.setOnClickListener {
            d("InOutBD : $presentInOutBD and $permanentInOutBD")
            clContactEdit.closeKeyboard(activity!!)


//            if (presentInOutBD == "0" && permanentInOutBD == "0") {
//                if (prContactAddressTIETPR.text.toString().isNullOrEmpty() || pmContactAddressTIETPRM.text.toString().isNullOrEmpty()){
//                    validationErrors++
//                    toast("House No / Road / Village cannot be empty can not be empty")
//                    if (prContactAddressTIETPR.text.toString().isNullOrEmpty())
//                        requestFocus(prContactAddressTIETPR)
//                    else
//                        requestFocus(pmContactAddressTIETPRM)
//
//                }
//                if (prContactDistrictTIET.text.toString().isNullOrEmpty() || pmContactDistrictTIET.text.toString().isNullOrEmpty()){
//                    validationErrors++
//                    toast("Please select District")
//                    if (prContactDistrictTIET.text.toString().isNullOrEmpty())
//                        requestFocus(prContactDistrictTIET)
//                    else
//                        requestFocus(pmContactDistrictTIET)
//                }
//
//                if (prContactThanaTIET.text.toString().isNullOrEmpty() || pmContactThanaTIETP.text.toString().isNullOrEmpty()){
//                    validationErrors++
//                    toast("Please select Thana")
//                    if (prContactThanaTIET.text.toString().isNullOrEmpty())
//                        requestFocus(prContactThanaTIET)
//                    else
//                        requestFocus(pmContactThanaTIETP)
//                }
//            } else if (presentInOutBD == "1" && permanentInOutBD == "1") {
//                if (presentContactCountryTIET.text.toString().isNullOrEmpty()){
//                    validationErrors++
//                    toast("Please select present Country")
//                    requestFocus(presentContactCountryTIET)
//                } else if (permanentContactCountryTIETP.text.toString().isNullOrEmpty()) {
//                    validationErrors++
//                    toast("Please select permanent Country")
//                    requestFocus(permanentContactCountryTIETP)
//                }
//            }
            var validation = 0
            when (presentInOutBD) {
                "0" -> {
//                        validation = isValidate(prContactDivTIET, contactDivTIL, prContactDivTIET, true, validation)
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
                    if (!addressCheckbox.isChecked) {
                        validation = isValidate(permanentContactCountryTIETP, permanentContactCountryTILP, permanentContactCountryTIETP, true, validation)
                        validation = isValidate(pmContactAddressTIETPRM, contactAddressTILPRM, pmContactAddressTIETPRM, true, validation)
                        Log.d("CValidaiton", "(out 2.2) value : $validation")
                    }
                }
                "0" -> {
                    if (!addressCheckbox.isChecked) {
                        validation = isValidate(pmContactDivTIET1, contactDivTIL1, pmContactDivTIET1, true, validation)
                        validation = isValidate(pmContactDistrictTIET, contactDistrictTIL, pmContactDistrictTIET, true, validation)
                        validation = isValidate(pmContactThanaTIETP, contactThanaTIL, pmContactThanaTIETP, true, validation)
                        validation = isValidate(pmContactAddressTIETPRM, contactAddressTILPRM, pmContactAddressTIETPRM, true, validation)
                        Log.d("CValidaiton", "(out 2.1) value : $validation")
                    }
                }
            }

            if (contactEmailAddressTIET.getString().trim() == "") {
                validation = isValidate(contactMobileNumberTIET, contactMobileNumberTIL, contactMobileNumberTIET, true, validation)
                Log.d("CValidaiton", "email empty $validation")
            }


            if (contactMobileNumberTIET.getString().trim() == "") {
                validation = isValidate(contactEmailAddressTIET, contactEmailAddressTIL, contactMobileNumberTIET, true, validation)
                Log.d("CValidaiton", "mobile empty $validation")
            }

            if (contactMobileNumberTIET.getString().trim() != "") {
                if (mobileNumberValidityCheck(contactMobileNumberTIET.text.toString())) {
//                        validation = isValidate(contactMobileNumberTIET, contactMobileNumberTIL, contactMobileNumberTIET, false, validation)
                    validation++
                }
                Log.d("CValidaiton", "mobile not empty $validation")
            }

            if (contactEmailAddressTIET.getString().trim() != "") {
                if (emailValidityCheck(contactEmailAddressTIET.text.toString(), contactEmailAddressTIET, contactEmailAddressTIL)) {
//                        validation = isValidate(contactEmailAddressTIET, contactEmailAddressTIL, contactEmailAddressTIET, false, validation)
                    validation++
                }
                Log.d("CValidaiton", "email not empty $validation")
            }

            if (presentInOutBD == "1") {
                validation = isValidate(presentContactCountryTIET, presentContactCountryTIL, presentContactCountryTIET, true, validation)
            }
            if (permanentInOutBD == "1") {
                validation = isValidate(permanentContactCountryTIETP, permanentContactCountryTILP, permanentContactCountryTIETP, true, validation)
            }
            if (presentInOutBD == "") {
                activity?.toast("Please select Inside Bangladesh or Outside Bangladesh")
            }
            if (pmContactAddressTIETPRM.getString().isNotEmpty() && permanentInOutBD == "") {
                //activity?.toast("Please select Inside Bangladesh or Outside Bangladesh")
                activity?.stopProgressBar(loadingProgressBar)
            }
            if (pmContactAddressTIETPRM.getString().isEmpty() && (permanentInOutBD == "1" || permanentInOutBD == "0")) {
                if (pmContactAddressTIETPRM.getString().trimmedLength() < 2)
                    contactAddressTILPRM.setError()
                else
                    contactAddressTILPRM.hideError()
            }

            clContactEdit.clearFocus()
            clContactEdit.closeKeyboard(activity)
            Log.d("checkValid", " val : $validation ")
//                if (addressCheckbox.isChecked) {
//                    if (validation >= 3) {
//                        updateData()
//                    }
//                } else {
//                    if (validation >= 7)
//                        updateData()
//                    else if (contactMobileNumberTIET.text.toString().isNullOrEmpty() || contactEmailAddressTIET.text.toString().isNullOrEmpty()) {
//                        if (validation >= 4)
//                            updateData()
//                    }
//                }

            if ((contactMobileNumberTIET?.text.toString().isNullOrEmpty() || contactEmailAddressTIET?.text.toString().isNullOrEmpty()) || (!contactMobileNumberTIET?.text.toString().isNullOrEmpty() && !contactEmailAddressTIET?.text.toString().isNullOrEmpty())) {
                var valid: Boolean
                var numberOfValidations = 0

                Log.d("check", "entered")



                if (contactEmailAddressTIET?.text.toString().isNullOrEmpty() && contactMobileNumberTIET.getString().trim() != "") {
                    valid = mobileNumberValidityCheck(contactMobileNumberTIET?.text.toString())
                    if (valid) numberOfValidations++
                }

                if (contactMobileNumberTIET?.text.toString().isNullOrEmpty() && contactEmailAddressTIET.getString().trim() != "") {
                    valid = emailValidityCheck(contactEmailAddressTIET.text.toString(), contactEmailAddressTIET, contactEmailAddressTIL)
                    if (valid) numberOfValidations++
                }



                if (contactMobileNumberTIET.getString().trim() != "") {
                    valid = mobileNumberValidityCheck(contactMobileNumberTIET?.text.toString())
                    if (valid) numberOfValidations++

                }

                if (contactEmailAddressTIET.getString().trim() != "" && contactEmailAddressTIET1.getString().trim() == ""){
                    valid = emailValidityCheck(contactEmailAddressTIET.text.toString(), contactEmailAddressTIET, contactEmailAddressTIL)
                    if (valid) numberOfValidations++
                }

                if (!contactEmailAddressTIET1.text.toString().isNullOrEmpty()) {
                    valid = emailValidityCheck(contactEmailAddressTIET1.text.toString(), contactEmailAddressTIET1, contactEmailAddressTIL1)
                    if (valid) numberOfValidations++
                }

                if (numberOfValidations > 1) {
                    Log.d("check", "valid")
                    if (addressCheckbox.isChecked) {
                        if (validation >= 4) {
                            Log.d("rakib", "came 4")
                            updateData()
                        }
                    } else {
                        if (validation >= 7) {
                            Log.d("rakib", "came 7")
                            updateData()
                        }
                    }
                }

            }


            //if ()
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

    private fun addMobileValidation(editText: TextInputEditText, inputLayout: TextInputLayout) {
        editText.easyOnTextChangedListener { charSequence ->
            mobileValidation(charSequence.toString(), editText, inputLayout)
        }
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

        activity?.showProgressBar(loadingProgressBar)
        /*if (presentInOutBD == "") {
            activity?.stopProgressBar(loadingProgressBar)
            activity?.toast("Please select Inside or Outside Bangladesh")
        }
        if (pmContactAddressTIETPRM.getString().isNotBlank() && permanentInOutBD == "") {
            activity?.toast("Please select Inside or Outside Bangladesh")
            activity?.stopProgressBar(loadingProgressBar)
        }*/

        val presentAddressID = data.presentAddressID
        val permanentAddressID = data.permanentAddressID
//        Log.d("ContactDetails", "PassingValue present in bd : $presentInOutBD " + "\n" +
//                " district presrent  :  ${getIdByName(prContactDistrictTIET.getString(), districtList, "d")}" + "\n" +
//                " thana parmanent :  ${getIdByName(prContactThanaTIET.getString(), thanaList, "t")} " + "\n" +
//                " post office present : ${getIdByName(prContactPostOfficeTIET1.getString(), postOfficeList, "p")}" + "\n" +
//                " addres present : ${prContactAddressTIETPR.getString()}" + "\n" +
//                " country present : ${presentContactCountryTIET.getString()}" + "\n" +
//                " presentInOutBD : $presentInOutBD" + "\n" +
//                " permanentInOutBD : $permanentInOutBD" + "\n" +
//                " district two parmanent : ${getIdByName(pmContactDistrictTIET.getString(), districtList, "dpm")}" + "\n" +
//                " thana two parmanent : ${getIdByName(pmContactThanaTIETP.getString(), thanaListPm, "tpm")}" + "\n" +
//                " post office two parmanent : ${getIdByName(pmContactPostOfficeTIET.getString(), postOfficeListPm, "ppm")}" + "\n" +
//                " parmanent address parmanent : ${pmContactAddressTIETPRM.getString()}" + "\n" +
//                " parmamnt country parmanent : ${permanentContactCountryTIETP.getString()}" + "\n" +
//                " same address : $sameAddress" + "\n" +
//                " permanentAddressID : $permanentAddressID " + "\n" +
//                " presentAddressID :  $presentAddressID" + "\n" +
//                " countryCode : ${countryCodeTIET.text.toString().substringAfter("(").substringBefore(")")} " + "\n" +
//                " mobile number one : ${contactMobileNumberTIET.getString()}" + "\n" +
//                " mobile number two : ${contactMobileNumber1TIET.getString()}" + "\n" +
//                " mobile number three : ${contactMobileNumber2TIET.getString()}" + "\n" +
//                " email ddree one : ${contactEmailAddressTIET.getString()}" + "\n" +
//                " email address another: ${contactEmailAddressTIET1.getString()}")

        Log.d("contactDetails", "\n" +
                "userid:${session.userId} \n" +
                "decodeid:${session.decodId} \n" +
                "isResumeUpdate:${session.IsResumeUpdate} \n" +
                "inOut:$presentInOutBD \n" +
                "present_district:${getIdByName(prContactDistrictTIET.getString(), districtList, "d")} \n" +
                "present_thana:${getIdByName(prContactThanaTIET.getString(), thanaList, "t")} \n" +
                "present_p_office:${getIdByName(prContactPostOfficeTIET1.getString(), postOfficeList, "p")} \n" +
                "present_Village:${prContactAddressTIETPR.getString()} \n" +
                "present_country_list:${getIdByCountryName(presentContactCountryTIET.getString())} \n" +
                "permInOut:${permanentInOutBD} \n" +
                "permanent_district:${getIdByName(pmContactDistrictTIET.getString(), districtList, "dpm")} \n" +
                "permanent_thana:${getIdByName(pmContactThanaTIETP.getString(), thanaListPm, "tpm")} \n" +
                "permanent_p_office:${getIdByName(pmContactPostOfficeTIET.getString(), postOfficeListPm, "ppm")} \n" +
                "permanent_Village:${pmContactAddressTIETPRM.getString()} \n" +
                "permanent_country_list:${getIdByCountryName(permanentContactCountryTIETP.getString())} \n" +
                "same_address:${sameAddress} \n" +
                "permanent_adrsID:${permanentAddressID} \n" +
                "present_adrsID:${presentAddressID} \n" +
                "officePhone: ${contactMobileNumber1TIET.getString()} \n" +
                "mobile:${contactMobileNumberTIET.getString()} \n" +
                "countryCode:${countryCodeTIET.text.toString().substringAfter("(").substringBefore(")")} \n" +
                "homePhone:${contactMobileNumber2TIET.getString()} \n" +
                "email:${contactEmailAddressTIET.getString()} \n" +
                "alternativeEmail:${contactEmailAddressTIET1.getString()}"

        )

        val call = ApiServiceMyBdjobs.create().updateContactData(userId = session.userId, decodeId = session.decodId, isResumeUpdate = session.IsResumeUpdate,
                inOut = presentInOutBD, present_district = getIdByName(prContactDistrictTIET.getString(), districtList, "d"), present_thana = getIdByName(prContactThanaTIET.getString(), thanaList, "t"),
                present_p_office = getIdByName(prContactPostOfficeTIET1.getString(), postOfficeList, "p"), present_Village = prContactAddressTIETPR.getString(),
                present_country_list = getIdByCountryName(presentContactCountryTIET.getString()), permInOut = permanentInOutBD, permanent_district = getIdByName(pmContactDistrictTIET.getString(), districtList, "dpm"),
                permanent_thana = getIdByName(pmContactThanaTIETP.getString(), thanaListPm, "tpm"), permanent_p_office = getIdByName(pmContactPostOfficeTIET.getString(), postOfficeListPm, "ppm"), permanent_Village = pmContactAddressTIETPRM.getString(),
                permanent_country_list = getIdByCountryName(permanentContactCountryTIETP.getString()), same_address = sameAddress, permanent_adrsID = permanentAddressID, present_adrsID = presentAddressID,
                officePhone = contactMobileNumber1TIET.getString(), mobile = contactMobileNumberTIET.getString(), countryCode = countryCodeTIET.text.toString().substringAfter("(").substringBefore(")"), homePhone = contactMobileNumber2TIET.getString(),
                email = contactEmailAddressTIET.getString(), alternativeEmail = contactEmailAddressTIET1.getString())
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                try {
                    activity?.stopProgressBar(loadingProgressBar)
                    activity?.toast(R.string.message_common_error)
                    Log.d("contact_details", "msg: ${t.message}")
                } catch (e: Exception) {
                    logException(e)
                }

            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity?.stopProgressBar(loadingProgressBar)

                        //if (response.body()?.statuscode != "2")
                        response.body()?.message?.let { activity?.toast(it) }

                        if (response.body()?.statuscode == "4") {
                            session.updateEmail(contactEmailAddressTIET.getString())
                            contactInfo.setBackFrom(Constants.contactUpdate)
                            contactInfo.goBack()
                            //onDestroy()
                        }
                    } else {
                        activity?.stopProgressBar(loadingProgressBar)
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
        clContactEdit.clearFocus()
        permanentInOutBD = ""
        cgPermanent.clearCheck()
        presentInOutBD = ""
        cgPresent.clearCheck()
        try {
            data = contactInfo.getContactData()
            Log.d("rakib", data.presentThana + " " + data.presentPostOffice)
        } catch (e: Exception) {
            logException(e)
            d("++${e.message}")
        }
        getDataFromChipGroup(cgPermanent)
        getDataFromChipGroup(cgPresent)
        val addressType = data.addressType1
        val homePhone = data.homePhone
        val officePhone = data.officePhone

        addressCheckbox.isChecked = addressType == "3"

        if (addressType == "3") {
            cgPermanent.hide()
            llPermenantPortion.hide()
        } else {
            cgPermanent.show()
            llPermenantPortion.show()
        }

        if (officePhone.isNullOrBlank()) contactAddMobileButton.show() else contactAddMobileButton.hide()

        prContactDistrictTIET?.setText(dataStorage.getLocationNameByID(data.presentDistrict))
        d("dis : ${data.presentDistrict}")
        prContactThanaTIET?.setText(dataStorage.getLocationNameByID(data.presentThana))
        d("thana : ${data.presentThana}")
        contactInfo.setThana(data.presentThana)
        if (data.presentPostOffice != "0") {
            prContactPostOfficeTIET1?.setText(dataStorage.getLocationNameByID(data.presentPostOffice))
            contactInfo.setPostOffice(data.presentPostOffice)
        }
        if (data.presentPostOffice == "-2") {
            prContactPostOfficeTIET1?.setText(getString(R.string.hint_post_office_other))
            contactInfo.setPostOffice(data.presentPostOffice)
        }
        if (data.presentPostOffice == "0" || data.presentPostOffice == "") prContactPostOfficeTIET1?.setText("")
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
        if (data.permanentPostOffice != "0") {
            pmContactPostOfficeTIET?.setText(dataStorage.getLocationNameByID(data.permanentPostOffice))
            contactInfo.setPmPostOffice(data.permanentPostOffice)
        }
        if (data.permanentPostOffice == "-2") {
            pmContactPostOfficeTIET?.setText(getString(R.string.hint_post_office_other))
            contactInfo.setPmPostOffice(data.permanentPostOffice)
        }
        if (data.permanentPostOffice == "0" || data.permanentPostOffice == "") pmContactPostOfficeTIET?.setText("")
        pmContactAddressTIETPRM?.setText(data.permanentVillage)
        if (data.permanentCountry != "118") permanentContactCountryTIETP?.setText(dataStorage.getLocationNameByID(data.permanentCountry))

        contactMobileNumberTIET?.setText(data.mobile)
        contactEmailAddressTIET?.setText(data.email)

        if (data.mobile.isNullOrEmpty())
            contactEmailAddressTIL.hideError() else contactEmailAddressTIL.isErrorEnabled = true
        if (data.email.isNullOrEmpty())
            contactMobileNumberTIL.hideError() else contactMobileNumberTIL.isErrorEnabled = true

        contactEmailAddressTIET?.easyOnTextChangedListener { charSequence ->
            emailValidityCheck(charSequence.toString(), contactEmailAddressTIET, contactEmailAddressTIL)
        }

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
        when {
            data.presentInsideOutsideBD == "False" -> {
                selectChip(cgPresent, "Inside Bangladesh")
                presentInOutBD = "0"
                presentContactCountryTIET.clear()
                presentInsideBangladeshLayout1.show()
                presentOutsideBangladeshLayout.hide()
            }
            data.presentInsideOutsideBD == "True" -> {
                selectChip(cgPresent, "Outside Bangladesh")
                presentInOutBD = "1"
                prContactDivTIET.clear()
                prContactDistrictTIET.clear()
                prContactThanaTIET.clear()
                //presentContactCountryTIET.clear()
                presentInsideBangladeshLayout1.hide()
                presentOutsideBangladeshLayout.show()
            }
            else -> cgPresent.clearCheck()
        }

        if (data.permanentInsideOutsideBD == "False") {
            selectChip(cgPermanent, "Inside Bangladesh")
            permanentInOutBD = "0"
            permanentContactCountryTIETP.clear()
            presentInsideBangladeshLayout.show()
            presentOutsideBangladeshLayoutP.hide()
            pmContactAddressTIETPRM.easyOnTextChangedListener {
                if (it.trimmedLength() >= 2)
                    contactAddressTILPRM.hideError() else contactAddressTILPRM.setError()
            }
        } else if (data.permanentInsideOutsideBD == "True") {
            selectChip(cgPermanent, "Outside Bangladesh")
            permanentInOutBD = "1"
            pmContactDivTIET1.clear()
            pmContactDistrictTIET.clear()
            pmContactThanaTIETP.clear()
            //permanentContactCountryTIETP.clear()
            presentInsideBangladeshLayout.hide()
            presentOutsideBangladeshLayoutP.show()
            permanentContactCountryTIETP.easyOnTextChangedListener {
                if (it.trimmedLength() >= 2)
                    permanentContactCountryTILP.hideError() else permanentContactCountryTILP.setError()
            }
            pmContactAddressTIETPRM.easyOnTextChangedListener {
                if (it.trimmedLength() >= 2)
                    contactAddressTILPRM.hideError() else contactAddressTILPRM.setError()
            }
        } else {
            cgPermanent.clearCheck()
        }

        countryCodeTIET?.setText(countryNameAndCode)

        //hideAllError()
    }

    private fun hideAllError() {
        Log.d("rakib error", "called main")

//        if (permanentInOutBD == "" && pmContactDistrictTIET.getString().isEmpty() && pmContactThanaTIETP.getString().isEmpty() && pmContactAddressTIETPRM.getString().isEmpty()) {
//            contactDistrictTIL.hideError()
//            contactThanaTIL.hideError()
//            Log.d("rakib error", "called if")
//            contactAddressTILPRM.hideError()
//            permanentContactCountryTILP.hideError()
//        } else {
//            contactDistrictTIL.setError()
//            contactThanaTIL.setError()
//            contactAddressTILPRM.setError()
//            Log.d("rakib error", "called else ")
//
//            permanentContactCountryTILP.setError()
//        }
        if (permanentInOutBD == "0" && pmContactDistrictTIET.getString().isNotEmpty() && pmContactThanaTIETP.getString().isNotEmpty() && pmContactAddressTIETPRM.getString().isNotEmpty()) {
            contactDistrictTIL.hideError()
            contactThanaTIL.hideError()
            contactAddressTILPRM.hideError()
            Log.d("rakib error", "called 2nd if")

        }
        if (permanentInOutBD == "1" && permanentContactCountryTIETP.getString().isEmpty() && pmContactAddressTIETPRM.getString().isEmpty()) {
            permanentContactCountryTILP.hideError()
            contactAddressTILPRM.hideError()
            Log.d("rakib error", "called 3rd if")

        }
    }

    private fun setupViews() {
        ////Present Address---------------Start
        presentContactCountryTIET.setOnClickListener {

            val countryList: Array<String> = dataStorage.allCountries

            activity?.selector("Please select your country ", countryList.toList()) { _, i ->

                presentContactCountryTIET.setText(countryList[i])
                presentContactCountryTIL.requestFocus()
                //presentContactCountryTIL.hideError()

            }

        }

        ////Parmanent Address---------------Start
        permanentContactCountryTIETP.setOnClickListener {
            val countryList: Array<String> = dataStorage.allCountries
            activity?.selector("Please select your country ", countryList.toList()) { _, i ->
                permanentContactCountryTIETP.setText(countryList[i])
                permanentContactCountryTILP.requestFocus()
               // permanentContactCountryTILP.hideError()
            }
        }
        presentContactCountryTIET.setOnClickListener {


            val countryList: Array<String> = dataStorage.allCountriesWithOutBangladesh

            activity?.selector("Please select your country ", countryList.toList()) { _, i ->

                presentContactCountryTIET.setText(countryList[i])
                presentContactCountryTIL.requestFocus()
                //presentContactCountryTIL.hideError()
            }
        }
        permanentContactCountryTIETP.setOnClickListener {
            val countryList: Array<String> = dataStorage.allCountriesWithOutBangladesh
            activity?.selector("Please select your country ", countryList.toList()) { _, i ->
                permanentContactCountryTIETP.setText(countryList[i])
                permanentContactCountryTILP.requestFocus()
                //permanentContactCountryTILP.hideError()
            }
        }
    }

    private fun getDataFromChipGroup(cg: ChipGroup) {
        cg.setOnCheckedChangeListener { chipGroup, i ->
            if (i > 0) {
                val chip = chipGroup.findViewById(i) as Chip
                cg.radioCheckableChip(chip)
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
                                pmContactAddressTIETPRM.easyOnTextChangedListener {
                                    if (it.trimmedLength() >= 2)
                                        contactAddressTILPRM.hideError() else contactAddressTILPRM.setError()
                                }
                                "0"
                            }
                            "Outside Bangladesh" -> {
                                presentInsideBangladeshLayout.hide()
                                presentOutsideBangladeshLayoutP.show()
                                permanentContactCountryTIETP.easyOnTextChangedListener {
                                    if (it.trimmedLength() >= 2)
                                        permanentContactCountryTILP.hideError() else permanentContactCountryTILP.setError()
                                }
                                pmContactAddressTIETPRM.easyOnTextChangedListener {
                                    if (it.trimmedLength() >= 2)
                                        contactAddressTILPRM.hideError() else contactAddressTILPRM.setError()
                                }
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
                        //activity?.toast("Please select Inside Bangladesh or Outside Bangladesh!")
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


            if ((editText?.id == R.id.prContactThanaTIET && prContactDistrictTIET?.text.toString().isNullOrEmpty()) || (editText.id == R.id.pmContactThanaTIETP && pmContactDistrictTIET?.text.toString().isNullOrEmpty())) {
                toast("Please select District")
            } else if ((editText?.id == R.id.prContactPostOfficeTIET1 && prContactThanaTIET?.text.toString().isNullOrEmpty()) || (editText.id == R.id.pmContactPostOfficeTIET && pmContactThanaTIETP?.text.toString().isNullOrEmpty())) {
                toast("Please select Thana")
            } else {
                val builder = AlertDialog.Builder(activity)
                builder.setTitle(title)
                        .setItems(data
                        ) { dialog, which ->
                            editText.setText(data[which])

                            var thanaId = contactInfo?.getPresentDistrict()!!
                            var postOfficeId = ""

                            if (editText.id == R.id.prContactDistrictTIET) {
                                prContactThanaTIET?.clear()
                                prContactPostOfficeTIET1?.clear()
                                prContactThanaTIET?.setOnClickListener(null)
                                prContactPostOfficeTIET1?.setOnClickListener(null)

                                Log.d("rakib district", districtList?.get(which)?.locationId)

                                contactInfo.setPresentDistrict(districtList?.get(which)?.locationId)
                                thanaList = dataStorage.getDependentEnglishLocationByParentId(districtList?.get(which)?.locationId!!)
                                val thanaNameList = arrayListOf<String>()
                                thanaList?.forEach { dt ->
                                    thanaNameList.add(dt.locationName)
                                }

                                setDialog("Please select your thana", prContactThanaTIET, thanaNameList.toTypedArray())
                            }
                            if (editText.id == R.id.prContactThanaTIET) {
                                if (prContactDistrictTIET?.text.toString().isNullOrEmpty()) {
                                    toast("Select a district first")
                                } else {
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
                                    setDialog("Please select your post office", prContactPostOfficeTIET1, pstOfficeNameList.toTypedArray())

                                }
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
                                setDialog("Please select your thana", pmContactThanaTIETP, thanaNameList.toTypedArray())
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
                                setDialog("Please select your post office", pmContactPostOfficeTIET, pstOfficeNameList.toTypedArray())
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

    private fun emailValidityCheck(email: String, emailTextInputEditText: TextInputEditText, emailTextInputLayout: TextInputLayout): Boolean {
//        if (true) {
        when {
            TextUtils.isEmpty(email) -> {
                if (emailTextInputEditText.id != R.id.contactEmailAddressTIET1) {
                    emailTextInputLayout?.showError(getString(R.string.field_empty_error_message_common))
                    try {
//                        requestFocus(emailTextInputEditText)
                    } catch (e: Exception) {
                        logException(e)
                    }
                    if (!contactMobileNumberTIET.text.toString().isNullOrEmpty()) {
                        emailTextInputLayout?.hideError()
                    }

                } else {
                    emailTextInputLayout?.hideError()
                }
                return false
            }
            !isValidEmail(email) -> {
                emailTextInputLayout?.showError("Email Address not valid")
                try {
                    requestFocus(emailTextInputEditText)
                } catch (e: Exception) {
                    logException(e)
                }
                return false
            }
            else -> {
                emailTextInputLayout?.hideError()
                Log.d("rakib", "email valid true")
            }
        }
        return true


    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun mobileNumberValidityCheck(mobileNumber: String): Boolean {
        when {
            TextUtils.isEmpty(mobileNumber) -> {
                contactMobileNumberTIL?.showError("Primary Mobile No can not be empty")
                requestFocus(contactMobileNumberTIET)
                return false
            }
            !validateMobileNumber() -> {
                contactMobileNumberTIL?.showError("Primary Mobile No is not valid")
                requestFocus(contactMobileNumberTIET)
                return false
            }
            else -> contactMobileNumberTIL?.hideError()
        }
        return true
    }

    private fun requestFocus(view: View?) {
        try {
            if (view != null) {
                try {
                    if (view.requestFocus()) {
                        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                    }
                } catch (e: Exception) {
                    logException(e)
                }

            }
        } catch (e: Exception) {
            logException(e)

        }
    }


    private fun validateMobileNumber(): Boolean {
        Log.d("rakib", "country code ${countryCodeTIET.text.toString()}")
        if (!TextUtils.isEmpty(countryCodeTIET?.text.toString()) && !TextUtils.isEmpty(contactMobileNumberTIET?.text.toString())) {
            if (Patterns.PHONE.matcher(contactMobileNumberTIET?.text.toString()).matches()) {
                if (countryCodeTIET?.text.toString().equals("Bangladesh (88)", ignoreCase = true) && contactMobileNumberTIET?.text.toString().length == 11) {
                    Log.d("rakib", "mobile validate length ${contactMobileNumberTIET.text.toString().length}")
                    return true
                } else if (!countryCodeTIET?.text.toString().equals("Bangladesh (88)", ignoreCase = true) && contactMobileNumberTIET?.text.toString().length + getCountryCode().length >= 6 && contactMobileNumberTIET?.text.toString().length + getCountryCode().length <= 15) {
                    Log.d("rakib", "mobile validate length ${contactMobileNumberTIET.text.toString().length}")
                    return true
                }
            }
        }
        Log.d("rakib", "validate number function false")
        return false
    }

    private fun getCountryCode(): String {
        val inputData = countryCodeTIET?.text.toString().split("[\\(||//)]".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        return inputData[inputData.size - 1].trim({ it <= ' ' })
    }

}
