package com.bdjobs.app.Registration.white_collar_registration


import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.databases.External.DataStorage
import kotlinx.android.synthetic.main.footer_wc_layout.*
import kotlinx.android.synthetic.main.fragment_wc_phone_email.*
import org.jetbrains.anko.selector


class WCPhoneEmailFragment : Fragment() {


    private lateinit var registrationCommunicator: RegistrationCommunicator
    private lateinit var returnView: View
    private lateinit var dataStorage: DataStorage


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
        mobileNumberTIET?.easyOnTextChangedListener { charSequence ->
            mobileNumberValidityCheck(charSequence.toString())
            if (mobileNumberValidityCheck(charSequence.toString())) {
                emailTIL?.hideError()
            }
        }
        emailTIET?.easyOnTextChangedListener { charSequence ->
            emailValidityCheck(charSequence.toString())
            if (emailValidityCheck(charSequence.toString())) {
                mobileNumberTIL?.hideError()
            }
        }

        phoneEmailFAButton?.setOnClickListener {
            if (mobileNumberTIET?.getString().isNullOrEmpty() && emailTIET?.getString().isNullOrEmpty()) {
                mobileNumberTIL?.showError("Please fill at least a Mobile Number or Email Address")
                emailTIL?.showError("Please fill at least a Mobile Number or Email Address")
            }
            when {
                validateMobileNumber() -> {
                    registrationCommunicator.wcGoToStepPassword()
                    registrationCommunicator.wcMobileNumberSelected(mobileNumberTIET.getString())
                    registrationCommunicator.wcUserNameSelected(mobileNumberTIET.getString())
                    //Log.d("CountryCode", "${countryCodeTIET?.text}")
                    val countryCode: String
                    val countryNameAndCountryCode = countryCodeTIET?.getString()
                    val inputData = countryNameAndCountryCode!!.split("[\\(||//)]".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                    countryCode = inputData[inputData.size - 1].trim({ it <= ' ' })
                    registrationCommunicator.wcCountrySeledted(countryCode)
                    if (isValidEmail(emailTIET?.text.toString())) {
                        registrationCommunicator.wcEmailSelected(emailTIET.getString())
                        registrationCommunicator.wcUserNameSelected(emailTIET.getString())
                        registrationCommunicator.wcUserNameTypeSelected("email")
                    }
                }
                isValidEmail(emailTIET.text.toString()) -> {
                    //Log.d("dsklgj", "2")
                    registrationCommunicator.wcGoToStepPassword()
                    registrationCommunicator.wcEmailSelected(emailTIET.getString())
                    registrationCommunicator.wcUserNameSelected(emailTIET.getString())
                    registrationCommunicator.wcUserNameTypeSelected("email")
                }
            }
        }
        countryCodeTIET?.setOnClickListener {
            val countryList: Array<String> = dataStorage.allCountryAndCountryCode
            activity?.selector("Select your country/region", countryList.toList()) { dialogInterface, i ->
                countryCodeTIET.setText(countryList[i])
                val countryCode: String
                val countryNameAndCountryCode = countryList[i]
                val inputData = countryNameAndCountryCode.split("[\\(||//)]".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                countryCode = inputData[inputData.size - 1].trim({ it <= ' ' })
                registrationCommunicator.wcCountrySeledted(countryCode)
            }
        }

        wcSupportTextView?.setOnClickListener {
            activity?.callHelpLine()
        }
        wcHelplineLayout?.setOnClickListener {
            activity?.callHelpLine()
        }

    }


    private fun initialization() {
        registrationCommunicator = activity as RegistrationCommunicator
        dataStorage = DataStorage(activity)
    }

    private fun validateMobileNumber(): Boolean {
        if (!TextUtils.isEmpty(countryCodeTIET?.getString()) && !TextUtils.isEmpty(mobileNumberTIET?.getString())) {
            if (android.util.Patterns.PHONE.matcher(mobileNumberTIET?.getString()).matches()) {
                if (countryCodeTIET?.getString().equals("Bangladesh (88)", ignoreCase = true) && mobileNumberTIET?.getString()!!.length == 11) {
                    return true
                } else if (!countryCodeTIET?.getString().equals("Bangladesh (88)", ignoreCase = true) && mobileNumberTIET?.getString()!!.length + getCountryCode().length >= 6 && mobileNumberTIET?.getString()!!.length + getCountryCode().length <= 15) {
                    return true
                }
            }
        }
        return false
    }


    private fun mobileNumberValidityCheck(mobileNumber: String): Boolean {
        when {
            TextUtils.isEmpty(mobileNumber) -> {
                mobileNumberTIL?.showError(getString(R.string.field_empty_error_message_common))
                try {
                    requestFocus(mobileNumberTIET)
                } catch (e: Exception) {
                    logException(e)
                }
                return false
            }
            validateMobileNumber() == false -> {
                mobileNumberTIL?.showError("Mobile Number is not valid")
                try {
                    requestFocus(mobileNumberTIET)
                } catch (e: Exception) {
                    logException(e)
                }
                return false
            }
            else -> mobileNumberTIL?.hideError()
        }
        return true
    }


    private fun emailValidityCheck(email: String): Boolean {
        if (!validateMobileNumber()) {
            when {
                TextUtils.isEmpty(email) -> {
                    emailTIL?.showError(getString(R.string.field_empty_error_message_common))
                    try {
                        requestFocus(emailTIET)
                    } catch (e: Exception) {
                        logException(e)
                    }
                    return false
                }
                isValidEmail(email) == false -> {
                    emailTIL?.showError("Email not valid")
                    try {
                        requestFocus(emailTIET)
                    } catch (e: Exception) {
                        logException(e)
                    }
                    return false
                }
                else -> emailTIL?.hideError()
            }
            return true
        }

        return true


    }


    private fun getCountryCode(): String {
        val inputData = countryCodeTIET?.text.toString().split("[\\(||//)]".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        return inputData[inputData.size - 1].trim({ it <= ' ' })
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
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


    fun setEmail() {
        val mail = registrationCommunicator.wcGetEmail()
        emailTIL.editText?.text?.clear()
        Log.e("email", "p+E set = "+mail)
        if(mail != ""){
            emailTIET?.setText(mail)
        }
//        else{
//            emailTIET.text = null
//        }
//        emailTIET?.setText(registrationCommunicator.wcGetEmail())//getEmail())
    }

}
