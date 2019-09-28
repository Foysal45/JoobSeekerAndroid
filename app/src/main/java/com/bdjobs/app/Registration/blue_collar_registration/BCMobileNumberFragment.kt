package com.bdjobs.app.Registration.blue_collar_registration

import android.app.Activity
import android.app.Dialog
import android.app.Fragment
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
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
    private lateinit var dialog: Dialog
    private var countryCode = ""
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialization()
        onClick()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        returnView = inflater.inflate(R.layout.fragment_bc_mobile_number, container, false)
        return returnView
    }


    private fun initialization() {
        registrationCommunicator = activity as RegistrationCommunicator
        dataStorage = DataStorage(activity)
    }

    private fun onClick() {
        bcMobileNumberTIET?.easyOnTextChangedListener { charSequence ->
            mobileNumberValidityCheck(charSequence.toString())
        }
        bcMobileNumberFAButton?.setOnClickListener {
            if (validateMobileNumber()) {
                showDialog(activity)
            } else {
                if (TextUtils.isEmpty(bcMobileNumberTIET?.getString())) {
                    bcMobileNumberTIL?.showError("মোবাইল নাম্বার খালি রাখা যাবে না")
                }
            }
        }
        bcCountryCodeTIET?.setOnClickListener {
            val countryList: Array<String> = dataStorage.allCountryAndCountryCode
            activity.selector("দেশ নির্বাচন করুন", countryList.toList()) { dialogInterface, i ->
                bcCountryCodeTIET?.setText(countryList[i])
                val countryCode: String
                val countryNameAndCountryCode = countryList[i]
                val inputData = countryNameAndCountryCode.split("[\\(||//)]".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                countryCode = inputData[inputData.size - 1].trim({ it <= ' ' })
                registrationCommunicator.wcCountrySeledted(countryCode)
            }
        }
        supportTextView?.setOnClickListener {
            activity.callHelpLine()
        }
        bcHelpLineLayout?.setOnClickListener {
            activity.callHelpLine()
        }
    }


    private fun showDialog(activity: Activity) {
        dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_verify_number_layout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val yesButton = dialog.findViewById<TextView>(R.id.bcYesTV)
        val noButton = dialog.findViewById<TextView>(R.id.bcNoTV)
        val mobileNumberTV = dialog.findViewById<TextView>(R.id.mobileNumberTV)
        val crossIV = dialog.findViewById<ImageView>(R.id.deleteIV)
        mobileNumberTV?.text = "${bcMobileNumberTIET.getString()} এই নাম্বারটিই কি আপনার?"
        crossIV?.setOnClickListener {
            dialog.dismiss()
        }
        noButton?.setOnClickListener {
            dialog.dismiss()
        }
        yesButton?.setOnClickListener {
            try {
                registrationCommunicator.wcMobileNumberSelected(bcMobileNumberTIET?.getString()!!)
                registrationCommunicator.wcUserNameTypeSelected("mobile")
                registrationCommunicator.wcUserNameSelected(bcMobileNumberTIET?.getString()!!)
                Log.d("CountryCode", "${bcCountryCodeTIET?.text}")

                val countryNameAndCountryCode = bcCountryCodeTIET?.getString()!!
                val inputData = countryNameAndCountryCode.split("[\\(||//)]".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                countryCode = inputData[inputData.size - 1].trim({ it <= ' ' })

                registrationCommunicator.wcCountrySeledted(countryCode)
                ///-------------------api------------calling------------------

                registrationCommunicator.wcCreateAccount()
                /* registrationCommunicator.bcGoToStepOtpCode()*/

                dialog.dismiss()
            } catch (e: Exception) {
                logException(e)
            }

        }
        dialog.show()

    }

    private fun mobileNumberValidityCheck(mobileNumber: String): Boolean {
        when {
            TextUtils.isEmpty(mobileNumber) -> {
                bcMobileNumberTIL?.showError("মোবাইল নাম্বার খালি রাখা যাবে না")
                requestFocus(bcMobileNumberTIET)
                return false
            }
            !validateMobileNumber() -> {
                bcMobileNumberTIL?.showError("মোবাইল নাম্বারটি সঠিক নয়")
                requestFocus(bcMobileNumberTIET)
                return false
            }
            else -> bcMobileNumberTIL?.hideError()
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
        if (!TextUtils.isEmpty(bcCountryCodeTIET?.text.toString()) && !TextUtils.isEmpty(bcMobileNumberTIET?.text.toString())) {
            if (android.util.Patterns.PHONE.matcher(bcMobileNumberTIET?.text.toString()).matches()) {
                if (bcCountryCodeTIET?.text.toString().equals("Bangladesh (88)", ignoreCase = true) && bcMobileNumberTIET?.text.toString().length == 11) {
                    return true
                } else if (!bcCountryCodeTIET?.text.toString().equals("Bangladesh (88)", ignoreCase = true) && bcMobileNumberTIET?.text.toString().length + getCountryCode().length >= 6 && bcMobileNumberTIET?.text.toString().length + getCountryCode().length <= 15) {
                    return true
                }
            }
        }
        return false
    }

    private fun getCountryCode(): String {
        val inputData = bcCountryCodeTIET?.text.toString().split("[\\(||//)]".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        return inputData[inputData.size - 1].trim({ it <= ' ' })
    }
}
