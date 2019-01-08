package com.bdjobs.app.editResume.contactInfo.fragments.contactDetails

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
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
    private var presentInOutBD: String = ""
    private var permanentInOutBD: String = ""
    private var sameAddress: String = ""
    
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
        doWork()
    }

    private fun doWork() {
        preloadedData()
        fab_contact_update.setOnClickListener {
            updateData()
        }
        addressCheckbox.setOnCheckedChangeListener { _, isChecked ->
            sameAddress = if (isChecked) "ON" else "OFF"
        }
        setupViews()
    }

    private fun updateData() {
        activity.showProgressBar(loadingProgressBar)
        val presentAddressID = contactInfo.getContactData().presentAddressID
        val permanentAddressID = contactInfo.getContactData().permanentAddressID
        Log.d("prePer", "${contactAddressTIETPR.getString()} and ${contactAddressTIETPRM.getString()}")

        val call = ApiServiceMyBdjobs.create().updateContactData(session.userId, session.decodId, session.IsResumeUpdate,
                presentInOutBD, getIdByName(contactDistrictTIET1.getString()), getIdByName(contactThanaTIET.getString()), getIdByName(contactPostOfficeTIET1.getString()),
                contactAddressTIETPR.getString(), getIdByName(presentContactCountryTIET.getString()), permanentInOutBD, getIdByName(contactDistrictTIET.getString()),
                getIdByName(contactThanaTIETP.getString()), getIdByName(contactPostOfficeTIET1.getString()), contactAddressTIETPRM.getString(), getIdByName(permanentContactCountryTIETP.getString()), sameAddress, permanentAddressID, presentAddressID,
                contactMobileNumberTIET.getString(), contactMobileNumber1TIET.getString(), contactMobileNumber2TIET.getString(),
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
        return dataStorage.getLocationIDByName(s).toString()
    }

    private fun preloadedData() {
        val data = contactInfo.getContactData()
        getDataFromChipGroup(cgPermanent)
        getDataFromChipGroup(cgPresent)
        val addressType = data.addressType1

        addressCheckbox.isChecked = addressType == "3"

        contactDistrictTIET1?.setText(dataStorage.getLocationNameByID(data.presentDistrict))
        contactThanaTIET?.setText(dataStorage.getLocationNameByID(data.presentThana))
        contactPostOfficeTIET?.setText(dataStorage.getLocationNameByID(data.presentPostOffice))
        contactAddressTIETPR?.setText(data.presentVillage)
        //contactAddressTIET?.setText(data.presentVillage)
        presentContactCountryTIET?.setText(dataStorage.getLocationNameByID(data.presentCountry))
        // Permenant
        contactDistrictTIET?.setText(dataStorage.getLocationNameByID(data.permanentDistrict))
        contactThanaTIETP?.setText(dataStorage.getLocationNameByID(data.permanentThana))
        contactPostOfficeTIET1?.setText(dataStorage.getLocationNameByID(data.permanentPostOffice))
        contactAddressTIETPRM?.setText(data.permanentVillage)
        //contactAddressTIET?.setText(data.permanentVillage)
        permanentContactCountryTIETP?.setText(dataStorage.getLocationNameByID(data.presentCountry))

        contactMobileNumberTIET?.setText(data.mobile)
        contactMobileNumber1TIET?.setText(data.officePhone)
        contactMobileNumber2TIET?.setText(data.homePhone)
        contactEmailAddressTIET?.setText(data.email)
        contactEmailAddressTIET1?.setText(data.alternativeEmail)

        if (data.presentInsideOutsideBD == "False") {
            selectChip(cgPresent, "Inside Bangladesh")
        } else {
            selectChip(cgPresent, "Outside Bangladesh")
        }
        if (data.permanentInsideOutsideBD == "False") {
            selectChip(cgPermanent, "Inside Bangladesh")
        } else {
            selectChip(cgPermanent, "Outside Bangladesh")
        }
    }

    private fun setupViews() {
        ////Present Address---------------Start

        contactDivTIET.setOnClickListener {
            val divisionList: Array<String> = dataStorage.allDivision
            activity?.selector("Select Your Division", divisionList.toList()) { _, i ->
                contactDivTIET.setText(divisionList[i])
                contactDivTIL.requestFocus()
            }
        }

        contactDistrictTIET1.setOnClickListener {
            var queryValue = contactDivTIET.getString()
            queryValue = queryValue.replace("'", "''")
            val districtList: Array<String> = dataStorage.getDependentLocationByParentName(queryValue)
            activity?.selector("Please Select your District", districtList.toList()) { _, i ->
                contactDistrictTIET1.setText(districtList[i])
                contactDistrictTIL1.requestFocus()
            }
        }
        contactThanaTIET.setOnClickListener {
            var queryValue = contactDistrictTIET1.getString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentLocationByParentName(queryValue)

            activity?.selector("Please Select your police station", districtList.toList()) { _, i ->

                contactThanaTIET.setText(districtList[i])
                contactThanaTIL1.requestFocus()
            }
        }

        contactPostOfficeTIET1.setOnClickListener {

            var queryValue = contactThanaTIET.text.toString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentPostOfficeByParentNameInEnglish(queryValue)

            activity?.selector("Please Select your post office", districtList.toList()) { _, i ->

                contactPostOfficeTIET1.setText(districtList[i])
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

        contactDivTIET1.setOnClickListener {
            val divisionList: Array<String> = dataStorage.allDivision
            activity?.selector("Select Your division", divisionList.toList()) { _, i ->

                contactDivTIET1.setText(divisionList[i])
                contactDivTIL1.requestFocus()
            }
        }

        contactDistrictTIET.setOnClickListener {


            var queryValue = contactDivTIET1.getString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentLocationByParentName(queryValue)

            activity?.selector("Please Select your district", districtList.toList()) { _, i ->

                contactDistrictTIET.setText(districtList[i])

                contactDistrictTIL.requestFocus()
            }
        }

        contactThanaTIETP.setOnClickListener {
            var queryValue = contactDistrictTIET.getString()
            queryValue = queryValue.replace("'", "''")
            d("thana : $queryValue")

            val districtList: Array<String> = dataStorage.getDependentLocationByParentName(queryValue)

            activity?.selector("Please Select your police station", districtList.toList()) { _, i ->

                contactThanaTIETP.setText(districtList[i])
                contactThanaTIL1.requestFocus()
            }
        }

        contactPostOfficeTIET.setOnClickListener {
            var queryValue = contactThanaTIETP.text.toString()
            queryValue = queryValue.replace("'", "''")
            d("post office : $queryValue")

            val districtList: Array<String> = dataStorage.getDependentPostOfficeByParentNameInEnglish(queryValue)

            activity?.selector("Please Select your post office", districtList.toList()) { _, i ->

                contactPostOfficeTIET.setText(districtList[i])
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

        contactThanaTIET.setOnClickListener {
            var queryValue = contactDistrictTIET1.getString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentLocationByParentName(queryValue)

            activity?.selector("Please Select your police station", districtList.toList()) { _, i ->

                contactThanaTIET.setText(districtList[i])
                contactThanaTIL1.requestFocus()
            }
        }

        contactPostOfficeTIET1.setOnClickListener {
            var queryValue = contactThanaTIET.text.toString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentPostOfficeByParentNameInEnglish(queryValue)

            activity?.selector("Please Select your post office", districtList.toList()) { _, i ->

                contactPostOfficeTIET1.setText(districtList[i])
                contactPostOfficeTIL1.requestFocus()
            }
        }

        presentContactCountryTIET.setOnClickListener {


            val countryList: Array<String> = dataStorage.allCountries

            activity?.selector("Please select your country ", countryList.toList()) { dialogInterface, i ->

                presentContactCountryTIET.setText(countryList[i])
                presentContactCountryTIL.requestFocus()


            }


        }

        ////Parmanent Address---------------Start

        contactDivTIET1.setOnClickListener {
            val divisionList: Array<String> = dataStorage.allDivision
            activity?.selector("Select Your division", divisionList.toList()) { dialogInterface, i ->

                contactDivTIET1.setText(divisionList[i])
                contactDivTIL1.requestFocus()
            }
        }

        contactDistrictTIET1.setOnClickListener {
            var queryValue = contactDivTIET.getString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentLocationByParentName(queryValue)

            activity?.selector("Please Select your district", districtList.toList()) { dialogInterface, i ->
                contactDistrictTIET1.setText(districtList[i])
                contactDistrictTIL1.requestFocus()
            }
        }

        contactThanaTIET.setOnClickListener {

            var queryValue = contactDistrictTIET1.getString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentLocationByParentName(queryValue)

            activity?.selector("Please Select your police station", districtList.toList()) { dialogInterface, i ->
                contactThanaTIET.setText(districtList[i])
                contactThanaTIL1.requestFocus()
            }
        }
        contactPostOfficeTIET1.setOnClickListener {
            var queryValue = contactThanaTIET.text.toString()
            queryValue = queryValue.replace("'", "''")

            val districtList: Array<String> = dataStorage.getDependentPostOfficeByParentNameInEnglish(queryValue)

            activity?.selector("Please Select your post office", districtList.toList()) { _, i ->
                contactPostOfficeTIET1.setText(districtList[i])
                contactPostOfficeTIL1.requestFocus()
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
                val data = chip.text.toString()
                when (chipGroup.id) {
                    R.id.cgPresent -> {
                        when (data) {
                            "Inside Bangladesh" -> {
                                presentInOutBD = "1"
                                presentInsideBangladeshLayout1.show()
                                presentOutsideBangladeshLayout.hide()

                            }
                            "Outside Bangladesh" -> {
                                presentInOutBD = "0"
                                presentInsideBangladeshLayout1.hide()
                                presentOutsideBangladeshLayout.show()

                            }
                        }
                        d("value : $presentInOutBD and $permanentInOutBD")

                    }
                    R.id.cgPermanent -> {
                        when (data) {
                            "Inside Bangladesh" -> {
                                permanentInOutBD = "0"

                                presentInsideBangladeshLayout.show()
                                presentOutsideBangladeshLayoutP.hide()

                            }
                            "Outside Bangladesh" -> {
                                permanentInOutBD = "1"

                                presentInsideBangladeshLayout.hide()
                                presentOutsideBangladeshLayoutP.show()

                            }
                        }

                        d("valuep : $presentInOutBD and $permanentInOutBD")
                    }
                }
            } else {
                when (chipGroup.id) {
                    R.id.cgPresent -> {
                    }
                    R.id.cgPermanent -> {
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

}
