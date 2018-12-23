package com.bdjobs.app.Registration.blue_collar_registration

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.Databases.External.DataStorage

import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.easyOnTextChangedListener
import com.bdjobs.app.Utilities.hideError
import com.bdjobs.app.Utilities.showError
import kotlinx.android.synthetic.main.fragment_bc_mobile_number.*
import kotlinx.android.synthetic.main.fragment_wc_phone_email.*
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
        // Inflate the layout for this fragment
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

            registrationCommunicator.wcMobileNumberSelected(bcMobileNumberTIET.text.toString())
            registrationCommunicator.wcUserNameTypeSelected("mobile")
            registrationCommunicator.wcUserNameSelected(bcMobileNumberTIET.text.toString())
            Log.d("CountryCode", "${bcCountryCodeTIET.text}")
            val countryCode: String
            val countryNameAndCountryCode = bcCountryCodeTIET.text.toString()
            val inputData = countryNameAndCountryCode.split("[\\(||//)]".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            countryCode = inputData[inputData.size - 1].trim({ it <= ' ' })

            registrationCommunicator.wcCountrySeledted(countryCode)

                ///-------------------api------------calling------------------

             /*   registrationCommunicator.wcCreateAccount()*/
                registrationCommunicator.bcGoToStepOtpCode()

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


    }


    private fun mobileNumberValidityCheck(mobileNumber: String): Boolean {

        when {
            TextUtils.isEmpty(mobileNumber) -> {
                bcMobileNumberTIL.showError(getString(R.string.field_empty_error_message_common))
                requestFocus(bcMobileNumberTIET)
                return false
            }
            validateMobileNumber() == false -> {
                bcMobileNumberTIL.showError("Mobile Number Not valid")
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
        if (!TextUtils.isEmpty(bcCountryCodeTIET.getText().toString()) && !TextUtils.isEmpty(bcMobileNumberTIET.getText().toString())) {
            if (android.util.Patterns.PHONE.matcher(bcMobileNumberTIET.getText().toString()).matches()) {
                if (bcCountryCodeTIET.getText().toString().equals("Bangladesh (88)", ignoreCase = true) && bcMobileNumberTIET.getText().toString().length == 11) {
                    return true
                } else if (!bcCountryCodeTIET.getText().toString().equals("Bangladesh (88)", ignoreCase = true) && bcMobileNumberTIET.getText().toString().length + getCountryCode().length >= 6 && bcMobileNumberTIET.getText().toString().length + getCountryCode().length <= 15) {
                    return true
                }
            }
        }
        return false
    }


    private fun getCountryCode(): String {
        val inputData = bcCountryCodeTIET.getText().toString().split("[\\(||//)]".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        return inputData[inputData.size - 1].trim({ it <= ' ' })
    }
}
