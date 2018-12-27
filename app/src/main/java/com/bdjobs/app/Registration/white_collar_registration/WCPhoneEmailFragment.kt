package com.bdjobs.app.Registration.white_collar_registration


import android.app.AlertDialog
import android.os.Bundle
import android.app.Fragment
import android.content.DialogInterface
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
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
import kotlinx.android.synthetic.main.footer_wc_layout.*
import kotlinx.android.synthetic.main.fragment_login_username.*
import kotlinx.android.synthetic.main.fragment_wc_password.*
import kotlinx.android.synthetic.main.fragment_wc_phone_email.*
import org.jetbrains.anko.makeCall
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast


class WCPhoneEmailFragment : Fragment() {


    private lateinit var registrationCommunicator: RegistrationCommunicator
    private lateinit var returnView: View
    private lateinit var dataStorage: DataStorage
    private var mobileValidity = false
    private var emailValidity = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialization()
        setEmail()
        onClick()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        returnView = inflater.inflate(R.layout.fragment_wc_phone_email, container, false)
        return returnView
    }


    private fun onClick() {


        mobileNumberTIET.easyOnTextChangedListener { charSequence ->

            mobileNumberValidityCheck(charSequence.toString())

        }

        emailTIET.easyOnTextChangedListener { charSequence ->

            emailValidityCheck(charSequence.toString())

        }

        phoneEmailFAButton.setOnClickListener {

            when {
                validateMobileNumber() -> {


                    registrationCommunicator.wcGoToStepPassword()
                    registrationCommunicator.wcMobileNumberSelected(mobileNumberTIET.text.toString())
                    registrationCommunicator.wcUserNameSelected(mobileNumberTIET.text.toString())


                    Log.d("CountryCode", "${countryCodeTIET.text}")
                    val countryCode: String
                    val countryNameAndCountryCode = countryCodeTIET.text.toString()
                    val inputData = countryNameAndCountryCode.split("[\\(||//)]".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                    countryCode = inputData[inputData.size - 1].trim({ it <= ' ' })

                    registrationCommunicator.wcCountrySeledted(countryCode)


                    if (isValidEmail(emailTIET.text.toString())) {

                        registrationCommunicator.wcEmailSelected(emailTIET.text.toString())
                        registrationCommunicator.wcUserNameSelected(emailTIET.text.toString())
                        registrationCommunicator.wcUserNameTypeSelected("email")

                    }

                }
                isValidEmail(emailTIET.text.toString()) -> {
                    Log.d("dsklgj", "2")
                    registrationCommunicator.wcGoToStepPassword()
                    registrationCommunicator.wcEmailSelected(emailTIET.text.toString())
                    registrationCommunicator.wcUserNameSelected(emailTIET.text.toString())
                    registrationCommunicator.wcUserNameTypeSelected("email")
                }


            }


        }

        countryCodeTIET.setOnClickListener {

            val countryList: Array<String> = dataStorage.allCountryAndCountryCode

            selector("Select Your Country", countryList.toList()) { dialogInterface, i ->

                countryCodeTIET.setText(countryList[i])
                val countryCode: String
                val countryNameAndCountryCode = countryList[i]
                val inputData = countryNameAndCountryCode.split("[\\(||//)]".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                countryCode = inputData[inputData.size - 1].trim({ it <= ' ' })

                registrationCommunicator.wcCountrySeledted(countryCode)


            }


        }


        wcSupportTextView.setOnClickListener {

            activity.makeCall("16479")

        }

        wcHelplineLayout.setOnClickListener {

            activity.makeCall("16479")

        }

    }


    private fun initialization() {

        registrationCommunicator = activity as RegistrationCommunicator
        dataStorage = DataStorage(activity)


    }

    private fun validateMobileNumber(): Boolean {
        if (!TextUtils.isEmpty(countryCodeTIET.getText().toString()) && !TextUtils.isEmpty(mobileNumberTIET.getText().toString())) {
            if (android.util.Patterns.PHONE.matcher(mobileNumberTIET.getText().toString()).matches()) {
                if (countryCodeTIET.getText().toString().equals("Bangladesh (88)", ignoreCase = true) && mobileNumberTIET.getText().toString().length == 11) {
                    return true
                } else if (!countryCodeTIET.getText().toString().equals("Bangladesh (88)", ignoreCase = true) && mobileNumberTIET.getText().toString().length + getCountryCode().length >= 6 && mobileNumberTIET.getText().toString().length + getCountryCode().length <= 15) {
                    return true
                }
            }
        }
        return false
    }


    private fun mobileNumberValidityCheck(mobileNumber: String): Boolean {

        when {
            TextUtils.isEmpty(mobileNumber) -> {
                mobileNumberTIL.showError(getString(R.string.field_empty_error_message_common))
                requestFocus(mobileNumberTIET)
                return false
            }
            validateMobileNumber() == false -> {
                mobileNumberTIL.showError("Mobile Number Not valid")
                requestFocus(mobileNumberTIET)
                return false
            }
            else -> mobileNumberTIL.hideError()
        }
        return true
    }


    private fun emailValidityCheck(email: String): Boolean {

        when {
            TextUtils.isEmpty(email) -> {
                emailTIL.showError(getString(R.string.field_empty_error_message_common))
                requestFocus(emailTIET)
                return false
            }
            isValidEmail(email) == false -> {
                emailTIL.showError("email Not valid")
                requestFocus(emailTIET)
                return false
            }
            else -> emailTIL.hideError()
        }
        return true


    }


    private fun getCountryCode(): String {
        val inputData = countryCodeTIET.getText().toString().split("[\\(||//)]".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        return inputData[inputData.size - 1].trim({ it <= ' ' })
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }


    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    fun setEmail(){

        emailTIET.setText(registrationCommunicator.getEmail())
    }

}
