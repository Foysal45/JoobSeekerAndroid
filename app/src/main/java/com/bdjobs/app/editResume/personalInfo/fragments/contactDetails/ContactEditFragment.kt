package com.bdjobs.app.editResume.personalInfo.fragments.contactDetails

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.Databases.External.DataStorage
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
    private var presentInOutBD: String? = ""
    private var permanentInOutBD: String? = ""
    private var sameAddress: String = ""
    private var count: Int = 0
    private lateinit var data: C_DataItem
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dataStorage = DataStorage(activity)
        session = BdjobsUserSession(activity)
        contactInfo = activity as PersonalInfo
        d("onActivityCreated")
    }

    override fun onResume() {
        super.onResume()
        d("onResume")
        contactInfo.setTitle(getString(R.string.title_contact))
        contactInfo.setEditButton(false, "dd")
        initViews()
        doWork()
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
        count = 0
        addTextChangedListener(prContactDivTIET, contactDivTIL)
        addTextChangedListener(prContactDistrictTIET, contactDistrictTIL1)
        addTextChangedListener(prContactThanaTIET, contactThanaTIL1)
        //addTextChangedListener(prContactPostOfficeTIET1, contactPostOfficeTIL1)
        addTextChangedListener(prContactAddressTIETPR, prContactAddressTILPR)
        addTextChangedListener(presentContactCountryTIET, presentContactCountryTIL)
        addTextChangedListener(prContactAddressTIETPR, prContactAddressTILPR)

        if (pmContactDivTIET1.getString() != "" || pmContactAddressTIETPRM.getString() != "") {
            addTextChangedListener(pmContactDivTIET1, contactDivTIL1)
            addTextChangedListener(pmContactDistrictTIET, contactDistrictTIL)
            addTextChangedListener(pmContactThanaTIETP, contactThanaTIL)
            //addTextChangedListener(pmContactPostOfficeTIET, contactPostOfficeTIL)
            addTextChangedListener(pmContactAddressTIETPRM, contactAddressTILPRM)
            addTextChangedListener(permanentContactCountryTIETP, presentContactCountryTILP)
        } else {
            contactDivTIL1.hideError()
            contactDistrictTIL.hideError()
            contactThanaTIL.hideError()
            contactAddressTILPRM.hideError()
            if (permanentInOutBD != "1") presentContactCountryTILP.hideError()
        }

        preloadedData()
        addressCheckbox.setOnCheckedChangeListener { _, isChecked ->
            sameAddress = if (isChecked) "on" else "off"
        }
        fab_contact_update.setOnClickListener {
            d("InOutBD : $presentInOutBD and $permanentInOutBD")

            var validation = 0
            validation = isValidate(prContactDivTIET, contactDivTIL, prContactDivTIET, true, validation)
            validation = isValidate(prContactDistrictTIET, contactDistrictTIL1, prContactDistrictTIET, true, validation)
            validation = isValidate(prContactThanaTIET, contactThanaTIL1, prContactThanaTIET, true, validation)
            validation = isValidate(contactMobileNumberTIET, contactMobileNumberTIL, contactMobileNumberTIET, true, validation)
            validation = isValidate(contactEmailAddressTIET, contactEmailAddressTIL, contactEmailAddressTIET, true, validation)
            if (presentInOutBD == "1") {
                validation = isValidate(presentContactCountryTIET, presentContactCountryTIL, presentContactCountryTIET, true, validation)
            }
            if (permanentInOutBD == "1") {
                validation = isValidate(permanentContactCountryTIETP, presentContactCountryTILP, permanentContactCountryTIETP, true, validation)
            }
            if (validation >= 3) updateData()
        }
        contactAddMobileButton?.setOnClickListener {
            count++
            if (count == 1) contactMobileNumber1TIL?.show() else contactMobileNumber2TIL?.show()
            if (contactMobileNumber2TIL.isVisible)
                contactAddMobileButton?.hide() else contactAddMobileButton?.show()
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

    private fun updateData() {
        activity.showProgressBar(loadingProgressBar)
        val presentAddressID = contactInfo.getContactData().presentAddressID
        val permanentAddressID = contactInfo.getContactData().permanentAddressID
        Log.d("dgjdgh", "PassingValue present in bd : ${presentInOutBD} " + "\n" +
                " district presrent  :  ${getIdByName(prContactDistrictTIET.getString())}" + "\n" +
                " thana parmanent :  ${getIdByName(prContactThanaTIET.getString())} " + "\n" +
                " post office present : ${getIdByName(prContactPostOfficeTIET1.getString())}" + "\n" +
                " addres present : ${prContactAddressTIETPR.getString()}" + "\n" +
                " country present : ${getIdByName(presentContactCountryTIET.getString())}" + "\n" +
                " presentInOutBD : ${presentInOutBD}" + "\n" +
                " permanentInOutBD : ${permanentInOutBD}" + "\n" +
                " district two parmanent : ${getIdByName(pmContactDistrictTIET.getString())}" + "\n" +
                " thana two parmanent : ${getIdByName(pmContactThanaTIETP.getString())}" + "\n" +
                " post office two parmanent : ${getIdByName(pmContactPostOfficeTIET.getString())}" + "\n" +
                " parmanent address parmanent : ${pmContactAddressTIETPRM.getString()}" + "\n" +
                " parmamnt country parmanent : ${getIdByName(permanentContactCountryTIETP.getString())}" + "\n" +
                " same address : $sameAddress" + "\n" +
                "permanentAddressID : $permanentAddressID " + "\n" +
                "presentAddressID :  $presentAddressID" + "\n" +
                "mobile number one : ${contactMobileNumberTIET.getString()}" + "\n" +
                " mobile number two : ${contactMobileNumber1TIET.getString()}" + "\n" +
                " mobile number three : ${contactMobileNumber2TIET.getString()}" + "\n" +
                " email ddree one : ${contactEmailAddressTIET.getString()}" + "\n" +
                " email address another: ${contactEmailAddressTIET1.getString()}")
        val call = ApiServiceMyBdjobs.create().updateContactData(session.userId, session.decodId, session.IsResumeUpdate,
                presentInOutBD, getIdByName(prContactDistrictTIET.getString()), getIdByName(prContactThanaTIET.getString()),
                getIdByName(prContactPostOfficeTIET1.getString()), prContactAddressTIETPR.getString(),
                getIdByName(presentContactCountryTIET.getString()), permanentInOutBD, getIdByName(pmContactDistrictTIET.getString()),
                getIdByName(pmContactThanaTIETP.getString()), getIdByName(pmContactPostOfficeTIET.getString()), pmContactAddressTIETPRM.getString(),
                getIdByName(permanentContactCountryTIETP.getString()), sameAddress, permanentAddressID, presentAddressID,
                contactMobileNumber1TIET.getString(), contactMobileNumberTIET.getString(), contactMobileNumber2TIET.getString(),
                contactEmailAddressTIET.getString(), contactEmailAddressTIET1.getString())
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                activity.stopProgressBar(loadingProgressBar)
                activity.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity.stopProgressBar(loadingProgressBar)
                        response.body()?.message?.let { activity.toast(it) }

                        if (response.body()?.statuscode == "4") {
                            session.updateEmail(contactEmailAddressTIET.getString())
                            contactInfo.goBack()
                        }
                    }
                } catch (e: Exception) {
                    activity.stopProgressBar(loadingProgressBar)
                    e.printStackTrace()
                    logException(e)
                }
            }
        })
    }

    private fun getIdByName(s: String): String {
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

        if (addressType == "3") {
            addressCheckbox.isChecked = true
        }

        prContactDistrictTIET?.setText(dataStorage.getLocationNameByID(data.presentDistrict))
        d("dis : ${data.presentDistrict}")
        prContactThanaTIET?.setText(dataStorage.getLocationNameByID(data.presentThana))
        d("thana : ${data.presentThana}")
        prContactPostOfficeTIET1?.setText(dataStorage.getLocationNameByID(data.presentPostOffice))
        d("thana : ${data.presentPostOffice}")
        prContactAddressTIETPR?.setText(data.presentVillage)
        val prDiv = dataStorage.getDivisionNameByDistrictName(dataStorage.getLocationNameByID(data.presentDistrict).toString())
        prContactDivTIET?.setText(prDiv)
        d("division : ${dataStorage.getDivisionNameByDistrictName(data.presentDistrict.toString())}")
        presentContactCountryTIET?.setText(dataStorage.getLocationNameByID(data.presentCountry))
        // Permenant
        pmContactDistrictTIET?.setText(dataStorage.getLocationNameByID(data.permanentDistrict))
        pmContactThanaTIETP?.setText(dataStorage.getLocationNameByID(data.permanentThana))
        pmContactPostOfficeTIET?.setText(dataStorage.getLocationNameByID(data.permanentPostOffice))
        pmContactAddressTIETPRM?.setText(data.permanentVillage)
        val pmDiv = dataStorage.getDivisionNameByDistrictName(dataStorage.getLocationNameByID(data.permanentDistrict).toString())
        pmContactDivTIET1?.setText(pmDiv)
        d("divisionPm : id : ${data.permanentDistrict} & ${dataStorage.getLocationNameByID(data.permanentDistrict)}")
        permanentContactCountryTIETP?.setText(dataStorage.getLocationNameByID(data.presentCountry))

        contactMobileNumberTIET?.setText(data.mobile)
        contactEmailAddressTIET?.setText(data.email)

        if (!homePhone?.isEmpty()!!) {
            contactMobileNumber2TIL?.show()
            contactAddEmailButton.hide()
            contactMobileNumber2TIET?.setText(data.homePhone)
        } else contactMobileNumber2TIL?.hide()
        if (!officePhone?.isEmpty()!!) {
            contactMobileNumber1TIL?.show()
            contactMobileNumber1TIET?.setText(data.officePhone)
        } else contactMobileNumber1TIL?.hide()
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
            presentInsideBangladeshLayout.hide()
            presentOutsideBangladeshLayoutP.show()
        }
    }

    private fun setupViews() {
        ////Present Address---------------Start

        prContactDivTIET.setOnClickListener {
            val divisionList: Array<String> = dataStorage.allDivision
            activity?.selector("Select Your Division", divisionList.toList()) { _, i ->
                prContactDivTIET.setText(divisionList[i])
                contactDivTIL.requestFocus()
            }
        }

        prContactDistrictTIET.setOnClickListener {
            var queryValue = prContactDivTIET.getString()
            queryValue = queryValue.replace("'", "''")
            val districtList: Array<String> = dataStorage.getDependentLocationByParentName(queryValue)
            activity?.selector("Please Select your District", districtList.toList()) { _, i ->
                prContactDistrictTIET.setText(districtList[i])
                contactDistrictTIL1.requestFocus()
            }
        }
        prContactThanaTIET.setOnClickListener {
            var queryValue = prContactDistrictTIET.getString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentLocationByParentName(queryValue)

            activity?.selector("Please Select your police station", districtList.toList()) { _, i ->

                prContactThanaTIET.setText(districtList[i])
                contactThanaTIL1.requestFocus()
            }
        }

        prContactPostOfficeTIET1.setOnClickListener {

            var queryValue = prContactThanaTIET.text.toString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentPostOfficeByParentNameInEnglish(queryValue)

            activity?.selector("Please Select your post office", districtList.toList()) { _, i ->

                prContactPostOfficeTIET1.setText(districtList[i])
                contactPostOfficeTIL1.requestFocus()

            }

        }
        presentContactCountryTIET.setOnClickListener {

            val countryList: Array<String> = dataStorage.allCountries

            activity?.selector("Please select your country ", countryList.toList()) { _, i ->

                presentContactCountryTIET.setText(countryList[i])
                presentContactCountryTIL.requestFocus()

            }

        }

        ////Parmanent Address---------------Start

        pmContactDivTIET1.setOnClickListener {
            val divisionList: Array<String> = dataStorage.allDivision
            activity?.selector("Select Your division", divisionList.toList()) { _, i ->

                pmContactDivTIET1.setText(divisionList[i])
                contactDivTIL1.requestFocus()
            }
        }

        pmContactDistrictTIET.setOnClickListener {


            var queryValue = pmContactDivTIET1.getString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentLocationByParentName(queryValue)

            activity?.selector("Please Select your district", districtList.toList()) { _, i ->

                pmContactDistrictTIET.setText(districtList[i])

                contactDistrictTIL.requestFocus()
            }
        }

        pmContactThanaTIETP.setOnClickListener {
            var queryValue = pmContactDistrictTIET.getString()
            queryValue = queryValue.replace("'", "''")
            d("thana : $queryValue")

            val districtList: Array<String> = dataStorage.getDependentLocationByParentName(queryValue)

            activity?.selector("Please Select your police station", districtList.toList()) { _, i ->

                pmContactThanaTIETP.setText(districtList[i])
                contactThanaTIL1.requestFocus()
            }
        }

        pmContactPostOfficeTIET.setOnClickListener {
            var queryValue = pmContactThanaTIETP.text.toString()
            queryValue = queryValue.replace("'", "''")
            d("post office : $queryValue")

            val districtList: Array<String> = dataStorage.getDependentPostOfficeByParentNameInEnglish(queryValue)

            activity?.selector("Please Select your post office", districtList.toList()) { _, i ->

                pmContactPostOfficeTIET.setText(districtList[i])
                contactPostOfficeTIL.requestFocus()

            }
        }

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

        ////Parmanent Address---------------Start

        pmContactDivTIET1.setOnClickListener {
            val divisionList: Array<String> = dataStorage.allDivision
            activity?.selector("Select Your division", divisionList.toList()) { _, i ->

                pmContactDivTIET1.setText(divisionList[i])
                contactDivTIL1.requestFocus()
            }
        }

        prContactDistrictTIET.setOnClickListener {
            var queryValue = prContactDivTIET.getString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentLocationByParentName(queryValue)

            activity?.selector("Please Select your district", districtList.toList()) { _, i ->
                prContactDistrictTIET.setText(districtList[i])
                contactDistrictTIL1.requestFocus()
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
            if (i > 0) {
                val chip = chipGroup.findViewById(i) as Chip
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
                        d("valueD : $presentInOutBD and $permanentInOutBD")
                    }
                    R.id.cgPermanent -> {
                        permanentInOutBD = ""
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
            if (data.equalIgnoreCase(chipText)) {
                Log.d("chip_entry", "text:$i")
                chip.isChecked = true
                d("value t/f : ${chip.isChecked}")
            }
        }
    }

    /*private fun disableError() {
        companyNameTIL.hideError()
        companyBusinessTIL.hideError()
        positionTIL.hideError()
        estartDateTIL.hideError()
        endDateTIL.hideError()
        experiencesTIL.hideError()
    }*/

}
