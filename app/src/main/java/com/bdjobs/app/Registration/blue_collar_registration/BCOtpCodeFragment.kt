package com.bdjobs.app.Registration.blue_collar_registration

import android.app.Fragment
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.*
import kotlinx.android.synthetic.main.footer_bc_layout.*
import kotlinx.android.synthetic.main.fragment_bc_otp_code.*


class BCOtpCodeFragment : Fragment() {


    private lateinit var registrationCommunicator: RegistrationCommunicator
    private lateinit var counter: CountDownTimer
    private lateinit var returnView : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        returnView = inflater.inflate(R.layout.fragment_bc_otp_code, container, false)
        return returnView

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        onClick()
        initialization()
        setTime()

    }

    private fun onClick(){

        bcOTPFAButton.setOnClickListener {

            if (bcOTPCodeTIET?.text.toString().isNullOrEmpty()) {

                bcOTPCodeTIL?.showError("কোডটি লিখুন")


            } else {

                registrationCommunicator.wcSetOtp(bcOTPCodeTIET.getString())
                registrationCommunicator.wcOtpVerify()

            }


            ///---------------api---------calling-------------////




        }

        bctimeLayout.setOnClickListener {

            setTime()

        }



        bcResendOtpTV.setOnClickListener {


            registrationCommunicator.bcResendOtp()
            setTime()
            bcTimerTV?.show()
            bcTimerIconIV?.show()
            bcResendOtpTV?.hide()

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

        bcInfoMobileNumberTV.text = "${registrationCommunicator.wcGetMobileNumber()} নাম্বারে এস এম এস এর মাধ্যমে একটি কোড পাঠানো হয়েছে, অনুগ্রহ করে কোডটি লিখুন।"
        bcOTPCodeTIET.easyOnTextChangedListener { charSequence ->
            otpValidityCheck(charSequence.toString())
        }

    }


    private fun otpValidityCheck(code: String): Boolean {

        when {
            TextUtils.isEmpty(code) -> {
                bcOTPCodeTIL?.showError("কোডটি লিখুন")
                requestFocus(bcOTPCodeTIET)
                return false
            }
            code.length < 6 -> {
                bcOTPCodeTIL?.showError("সঠিক কোডটি টাইপ করুন")
                requestFocus(bcOTPCodeTIET)
                return false
            }
            else -> bcOTPCodeTIL?.hideError()
        }
        return true
    }


    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }


    private fun setTime() {
        bcTimerTV?.show()
        bcResendOtpTV?.hide()
        counter = object : CountDownTimer(Constants.counterTimeLimit.toLong(), Constants.timer_countDownInterval.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                val second = millisUntilFinished / 1000 % 60
                val minute = millisUntilFinished / (1000 * 60) % 60
                val hour = millisUntilFinished / (1000 * 60 * 60) % 24
                val time = String.format("%02d:%02d", minute, second)
                try {

                    bcTimerTV?.text = time
                }catch (e:Exception){

                }
            }

            override fun onFinish() {

                bcTimerTV?.hide()
                bcResendOtpTV?.show()
                bcTimerIconIV?.hide()

            }
        }.start()
    }


}
