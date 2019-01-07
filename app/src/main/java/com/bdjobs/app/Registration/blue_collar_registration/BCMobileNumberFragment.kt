package com.bdjobs.app.Registration.blue_collar_registration

import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.*
import kotlinx.android.synthetic.main.footer_bc_layout.*
import kotlinx.android.synthetic.main.fragment_bc_mobile_number.*
import org.jetbrains.anko.selector


class BCMobileNumberFragment : Fragment() {

    private lateinit var registrationCommunicator: RegistrationCommunicator
    private lateinit var returnView: View
    private lateinit var dataStorage: DataStorage
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialization()
        onClick()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        returnView =  inflater.inflate(R.layout.fragment_bc_mobile_number, container, false)
        return returnView
    }




    private fun initialization(){

        registrationCommunicator = activity as RegistrationCommunicator
        dataStorage = DataStorage(activity)

    }

    private fun onClick(){



        bcMobileNumberTIET.easyOnTextChangedListener { charSequence ->
            mobileNumberValidityCheck(charSequence.toString())
        }



        bcMobileNumberFAButton.setOnClickListener {

            if (validateMobileNumber())  {

                registrationCommunicator.wcMobileNumberSelected(bcMobileNumberTIET.getString())
            registrationCommunicator.wcUserNameTypeSelected("mobile")
                registrationCommunicator.wcUserNameSelected(bcMobileNumberTIET.getString())
            Log.d("CountryCode", "${bcCountryCodeTIET.text}")
            val countryCode: String
                val countryNameAndCountryCode = bcCountryCodeTIET.getString()
            val inputData = countryNameAndCountryCode.split("[\\(||//)]".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            countryCode = inputData[inputData.size - 1].trim({ it <= ' ' })

            registrationCommunicator.wcCountrySeledted(countryCode)

                ///-------------------api------------calling------------------

                registrationCommunicator.wcCreateAccount()
              /*  registrationCommunicator.bcGoToStepOtpCode()*/

        }


        }


        bcCountryCodeTIET.setOnClickListener {

            val countryList: Array<String> = dataStorage.allCountryAndCountryCode

            selector("দেশ নির্বাচন করুন", countryList.toList()) { dialogInterface, i ->

                bcCountryCodeTIET.setText(countryList[i])
                val countryCode: String
                val countryNameAndCountryCode = countryList[i]
                val inputData = countryNameAndCountryCode.split("[\\(||//)]".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                countryCode = inputData[inputData.size - 1].trim({ it <= ' ' })

                registrationCommunicator.wcCountrySeledted(countryCode)


            }


        }

        supportTextView.setOnClickListener {

            activity.callHelpLine()

        }

        bcHelpLineLayout.setOnClickListener {

            activity.callHelpLine()
        }


    }


    private fun mobileNumberValidityCheck(mobileNumber: String): Boolean {

        when {
            TextUtils.isEmpty(mobileNumber) -> {
                bcMobileNumberTIL.showError(getString(R.string.field_empty_error_message_common))
                requestFocus(bcMobileNumberTIET)
                return false
            }
            validateMobileNumber() == false -> {
                bcMobileNumberTIL.showError("Mobile Number is not valid")
                requestFocus(bcMobileNumberTIET)
                return false
            }
            else -> bcMobileNumberTIL.hideError()
        }
        return true
    }


    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }


    private fun validateMobileNumber(): Boolean {
        if (!TextUtils.isEmpty(bcCountryCodeTIET.text.toString()) && !TextUtils.isEmpty(bcMobileNumberTIET.text.toString())) {
            if (android.util.Patterns.PHONE.matcher(bcMobileNumberTIET.text.toString()).matches()) {
                if (bcCountryCodeTIET.text.toString().equals("Bangladesh (88)", ignoreCase = true) && bcMobileNumberTIET.text.toString().length == 11) {
                    return true
                } else if (!bcCountryCodeTIET.text.toString().equals("Bangladesh (88)", ignoreCase = true) && bcMobileNumberTIET.text.toString().length + getCountryCode().length >= 6 && bcMobileNumberTIET.text.toString().length + getCountryCode().length <= 15) {
                    return true
                }
            }
        }
        return false
    }


    private fun getCountryCode(): String {
        val inputData = bcCountryCodeTIET.text.toString().split("[\\(||//)]".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        return inputData[inputData.size - 1].trim({ it <= ' ' })
    }
}
