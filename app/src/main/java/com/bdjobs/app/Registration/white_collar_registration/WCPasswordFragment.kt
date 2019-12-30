package com.bdjobs.app.Registration.white_collar_registration

import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.*
import kotlinx.android.synthetic.main.footer_wc_layout.*
import kotlinx.android.synthetic.main.fragment_wc_password.*


class WCPasswordFragment : Fragment() {


    private lateinit var returnView: View
    private lateinit var registrationCommunicator: RegistrationCommunicator


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       returnView = inflater.inflate(R.layout.fragment_wc_password, container, false)
        return returnView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        onClick()
        initialization()
        setUserId()
        passwordTIET?.easyOnTextChangedListener { charSequence ->
           passwordValidityCheck(charSequence.toString())
        }
        confirmPassTIET?.easyOnTextChangedListener { charSequence ->
            confirmPassValidityCheck(charSequence.toString())
        }
    }

    private fun onClick(){

        passwordFAButton?.setOnClickListener {
            if (passwordValidityCheck(passwordTIET?.text.toString())) {
                if (confirmPassValidityCheck(confirmPassTIET?.text.toString())) {
                    registrationCommunicator.wcSetPassAndConfirmPassword(passwordTIET?.text.toString(), confirmPassTIET?.text.toString())
                    registrationCommunicator.wcCreateAccount()
                }
            }
        }
        phone_button?.setOnClickListener {
            phone_button.iconTint = resources.getColorStateList(R.color.colorWhite)
            phone_button.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            phone_button.setTextColor(resources.getColor(R.color.colorWhite))
            if (!TextUtils.isEmpty(registrationCommunicator.wcGetEmail())){

                email_button.iconTint = resources.getColorStateList(R.color.colorPrimary)
                email_button.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
                email_button.setTextColor(resources.getColor(R.color.colorPrimary))

            }
            registrationCommunicator.wcUserNameTypeSelected("mobile")

        }
        email_button?.setOnClickListener {

            email_button.iconTint = resources.getColorStateList(R.color.colorWhite)
            email_button.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            email_button.setTextColor(resources.getColor(R.color.colorWhite))
            if (!TextUtils.isEmpty(registrationCommunicator.wcGetMobileNumber())){

                phone_button.iconTint = resources.getColorStateList(R.color.colorPrimary)
                phone_button.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
                phone_button.setTextColor(resources.getColor(R.color.colorPrimary))
            }
            registrationCommunicator.wcUserNameTypeSelected("email")
        }
        wcSupportTextView?.setOnClickListener {
            activity.callHelpLine()
        }
        wcHelplineLayout?.setOnClickListener {
            activity.callHelpLine()

        }
    }

    private fun initialization(){
        registrationCommunicator = activity as RegistrationCommunicator

    }
    private fun setUserId(){

        if (TextUtils.isEmpty(registrationCommunicator.wcGetEmail())){
            //Log.d("ConditionCheck","1 Condition")
            email_button.text = "Email Address"
            email_button.isEnabled = false
            phone_button.text = registrationCommunicator.wcGetMobileNumber()
            phone_button.iconTint = resources.getColorStateList(R.color.colorWhite)
            phone_button.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            phone_button.setTextColor(resources.getColor(R.color.colorWhite))
            email_button.iconTint = resources.getColorStateList(R.color.inactive_text_color)
            email_button.setTextColor(resources.getColor(R.color.inactive_text_color))
            registrationCommunicator.wcUserNameTypeSelected("mobile")

        } else if (TextUtils.isEmpty(registrationCommunicator.wcGetMobileNumber())){

            //Log.d("ConditionCheck","2 Condition")
            phone_button.text = "Mobile Number"
            phone_button.isEnabled = false
            email_button.text = registrationCommunicator.wcGetEmail()
            phone_button.iconTint = resources.getColorStateList(R.color.inactive_text_color)
            phone_button.setTextColor(resources.getColor(R.color.inactive_text_color))
            email_button.iconTint = resources.getColorStateList(R.color.colorWhite)
            email_button.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            email_button.setTextColor(resources.getColor(R.color.colorWhite))
            registrationCommunicator.wcUserNameTypeSelected("email")

        } else {

            //Log.d("ConditionCheck","3 Condition")
            email_button.text = registrationCommunicator.wcGetEmail()
            phone_button.text = registrationCommunicator.wcGetMobileNumber()
            email_button.iconTint = resources.getColorStateList(R.color.colorWhite)
            email_button.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            email_button.setTextColor(resources.getColor(R.color.colorWhite))
            phone_button.iconTint = resources.getColorStateList(R.color.colorPrimary)
            phone_button.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            phone_button.setTextColor(resources.getColor(R.color.colorPrimary))
            registrationCommunicator.wcUserNameTypeSelected("email")

        }
    }

    override fun onResume() {
        super.onResume()

        if (TextUtils.isEmpty(registrationCommunicator.wcGetEmail())){
            //Log.d("ConditionCheck","4 Condition")
            email_button.text = "Email Address"
            email_button.isEnabled = false
            phone_button.text = registrationCommunicator.wcGetMobileNumber()
            phone_button.iconTint = resources.getColorStateList(R.color.colorWhite)
            phone_button.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            phone_button.setTextColor(resources.getColor(R.color.colorWhite))
            email_button.iconTint = resources.getColorStateList(R.color.inactive_text_color)
            email_button.setTextColor(resources.getColor(R.color.inactive_text_color))
            registrationCommunicator.wcUserNameTypeSelected("mobile")
        } else if (TextUtils.isEmpty(registrationCommunicator.wcGetMobileNumber())){
            //Log.d("ConditionCheck","5 Condition")
            phone_button.text = "Mobile Number"
            phone_button.isEnabled = false
            email_button.text = registrationCommunicator.wcGetEmail()
            phone_button.iconTint = resources.getColorStateList(R.color.inactive_text_color)
            phone_button.setTextColor(resources.getColor(R.color.inactive_text_color))
            email_button.iconTint = resources.getColorStateList(R.color.colorWhite)
            email_button.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            email_button.setTextColor(resources.getColor(R.color.colorWhite))
            registrationCommunicator.wcUserNameTypeSelected("email")
        } else {
            //Log.d("ConditionCheck","6 Condition")
            email_button.text = registrationCommunicator.wcGetEmail()
            phone_button.text = registrationCommunicator.wcGetMobileNumber()
            email_button.iconTint = resources.getColorStateList(R.color.colorWhite)
            email_button.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            email_button.setTextColor(resources.getColor(R.color.colorWhite))
            phone_button.iconTint = resources.getColorStateList(R.color.colorPrimary)
            phone_button.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            phone_button.setTextColor(resources.getColor(R.color.colorPrimary))
        }
    }

    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    private fun passwordValidityCheck(password: String): Boolean {
        when {
            TextUtils.isEmpty(password) -> {
                passwordTIL?.showError(getString(R.string.field_empty_error_message_common))
                requestFocus(passwordTIET)
                return false
            }
           password.length < 8 -> {
               passwordTIL?.showError("password too short")
                requestFocus(passwordTIET)
                return false
            }
            else -> passwordTIL?.hideError()
        }
        return true
    }
    private fun confirmPassValidityCheck(password: String): Boolean {
        //Log.d("sdjkgndsg", " pass $password confirmPass ${passwordTIET?.text}")
        //Log.d("sdjkgndsg", "comparision ${password.equals(passwordTIET?.text.toString(), true)}")
        when {
            TextUtils.isEmpty(password) -> {
                confirmPassTIL?.showError(getString(R.string.field_empty_error_message_common))
                requestFocus(confirmPassTIET)
                return false
            }
            !password.equals(passwordTIET?.text.toString(), true) -> {
                confirmPassTIL?.showError("password not matched")
                try {
                    requestFocus(confirmPassTIET)
                } catch (e: Exception) {
                    logException(e)
                }
                return false
            }
            else -> confirmPassTIL?.hideError()
        }
        return true
    }

}
