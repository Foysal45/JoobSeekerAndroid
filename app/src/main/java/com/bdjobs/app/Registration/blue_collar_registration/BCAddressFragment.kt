package com.bdjobs.app.Registration.blue_collar_registration

import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.footer_bc_layout.*
import kotlinx.android.synthetic.main.fragment_bc_adress.*
import org.jetbrains.anko.selector


class BCAddressFragment : Fragment() {


    private lateinit var registrationCommunicator :RegistrationCommunicator
    private lateinit var dataStorage:DataStorage
    private lateinit var division :String
    private lateinit var district :String
    private lateinit var thana :String
    private lateinit var postOffice :String
    private lateinit var address :String
    private lateinit var returnView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        returnView = inflater.inflate(R.layout.fragment_bc_adress, container, false)
        return returnView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onClick()
        initialization()

    }


    private fun onClick(){

        bcAddressFAButton.setOnClickListener {

            checkValidity()

            thana = bcThanaTIET.getString()
            district = bcDistrictTIET.getString()
            division = bcDivisionTIET.getString()
            address = bcVillageTIET.getString()
            postOffice = bcPostOfficeTIET.getString()
            var locationID = ""

            if (postOffice.equals("অন্যান্য", ignoreCase = true) || TextUtils.isEmpty(postOffice)) {
                locationID = dataStorage.getBanglaLocationIDByName(thana)!!
            } else {
                locationID = dataStorage.getBanglaLocationIDByName(postOffice)!!
            }

            if (validateCondition()){

                registrationCommunicator.bcAddressSelected(division, district, thana, postOffice, address, locationID)
                registrationCommunicator.bcGoToStepExperience()
            }



        }

        bcDivisionTIET.setOnClickListener {
            val divisionList: Array<String> = dataStorage.banglaAllDivision
            selector("বিভাগ নির্বাচন করুন", divisionList.toList()) { dialogInterface, i ->
                bcDivisionTIET.setText(divisionList[i])
                bcDistrictTIL.requestFocus()
            }

        }

        bcDistrictTIET.setOnClickListener {
            var queryValue = bcDivisionTIET.getString()
            queryValue = queryValue.replace("'", "''")
            val districtList: Array<String> = dataStorage.getDependentLocationByParentNameInBangla(queryValue)
            selector("জেলা নির্বাচন করুন", districtList.toList()) { dialogInterface, i ->
                bcDistrictTIET.setText(districtList[i])
                bcDistrictTIL.requestFocus()


            }


        }

        bcThanaTIET.setOnClickListener {
            var queryValue = bcDistrictTIET.getString()
            queryValue = queryValue.replace("'", "''")
            val districtList: Array<String> = dataStorage.getDependentLocationByParentNameInBangla(queryValue)
            selector("উপজেলা / থানা নির্বাচন করুন", districtList.toList()) { dialogInterface, i ->
                bcThanaTIET.setText(districtList[i])
                bcDistrictTIL.requestFocus()



            }


        }

        bcPostOfficeTIET.setOnClickListener {
            var queryValue = bcThanaTIET.getString()
            queryValue = queryValue.replace("'", "''")
            val districtList: Array<String> = dataStorage.getDependentPostOfficeByParentNameInBangla(queryValue)
            selector("উপজেলা / থানা নির্বাচন করুন", districtList.toList()) { dialogInterface, i ->
                bcPostOfficeTIET.setText(districtList[i])
                bcPostOfficeTIL.requestFocus()


            }

        }


        supportTextView.setOnClickListener {

            activity.callHelpLine()

        }

        bcHelpLineLayout.setOnClickListener {

            activity.callHelpLine()
        }

    }

    private fun initialization(){

        registrationCommunicator = activity as RegistrationCommunicator
        dataStorage = DataStorage(activity)
        addTextChangedListener()

    }

    private fun addTextChangedListener() {

        bcDivisionTIET.easyOnTextChangedListener { charSequence ->
            addressValidation(charSequence.toString(), bcDivisionTIET, bcDivisionTIL, "")
        }

        bcDistrictTIET.easyOnTextChangedListener { charSequence ->

            addressValidation(charSequence.toString(), bcDistrictTIET, bcDistrictTIL, "")
            d("etTrInst : ->$charSequence|")

        }


        bcThanaTIET.easyOnTextChangedListener { charSequence ->

            addressValidation(charSequence.toString(), bcThanaTIET, bcThanaTIL, "")

        }


        bcPostOfficeTIET.easyOnTextChangedListener { charSequence ->


            addressValidation(charSequence.toString(), bcPostOfficeTIET, bcPostOfficeTIL, "")

        }


        bcVillageTIET.easyOnTextChangedListener { charSequence ->


            addressValidation(charSequence.toString(), bcVillageTIET, bcVillageTIL, "এলাকার ঠিকানা লিখুন")

        }


    }

    private fun validateCondition(): Boolean {
        return !TextUtils.isEmpty(bcVillageTIET.text.toString()) and !TextUtils.isEmpty(bcDistrictTIET.text.toString()) and !TextUtils.isEmpty(bcDistrictTIET.text.toString()) and !TextUtils.isEmpty(bcThanaTIET.text.toString())
    }


    private fun addressValidation(char: String, et: TextInputEditText, til: TextInputLayout, message: String): Boolean {
        when {
            TextUtils.isEmpty(char) -> {
                til.showError(message)
                requestFocus(et)
                return false
            }
            else -> til.hideError()
        }
        return true
    }


    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }


    private fun checkValidity() {


        if (TextUtils.isEmpty(bcDivisionTIET.getString())) {

            bcDivisionTIL.showError("বিভাগ নির্বাচন করুন")

        } else {
            bcDivisionTIL.isErrorEnabled = false


        }

        if (TextUtils.isEmpty(bcDistrictTIET.getString())) {

            bcDistrictTIL.showError("জেলা নির্বাচন করুন")

        } else {


            bcDistrictTIL.isErrorEnabled = false


        }

        if (TextUtils.isEmpty(bcThanaTIET.getString())) {

            bcThanaTIL.showError("থানা/উপজেলা নির্বাচন করুন")

        } else {


            bcThanaTIL.isErrorEnabled = false


        }


        if (TextUtils.isEmpty(bcPostOfficeTIET.getString())) {

            bcPostOfficeTIL.showError("পোস্ট অফিস নির্বাচন করুন")

        } else {


            bcPostOfficeTIL.isErrorEnabled = false


        }

        if (TextUtils.isEmpty(bcVillageTIET.getString())) {

            bcVillageTIL.showError("এলাকার ঠিকানা লিখুন")

        } else {


            bcVillageTIL.isErrorEnabled = false
            bcVillageTIET.requestFocus()

        }


    }

}
