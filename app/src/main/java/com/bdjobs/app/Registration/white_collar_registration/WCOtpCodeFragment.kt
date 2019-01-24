package com.bdjobs.app.Registration.white_collar_registration

import android.app.Fragment
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.*
import kotlinx.android.synthetic.main.footer_wc_layout.*
import kotlinx.android.synthetic.main.fragment_wc_otp_code.*


class WCOtpCodeFragment : Fragment() {



    private lateinit var registrationCommunicator : RegistrationCommunicator
    private lateinit var counter: CountDownTimer
    private lateinit var returnView:View
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setTime()
        initialization()
        onClick()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        returnView = inflater.inflate(R.layout.fragment_wc_otp_code, container, false)
        return returnView
    }


    private fun initialization(){

        registrationCommunicator = activity as RegistrationCommunicator
        wcInfoMobileNumberTV.text = "A code has been sent to ${registrationCommunicator.wcGetMobileNumber()} by SMS, please enter the code."

    }

    private fun onClick(){



        wcOTPFAButton.setOnClickListener {

            if (TextUtils.isEmpty(wcOTPCodeTIET.getString())) {

                wcOTPCodeTIL.showError("Please type the code")


            } else {

                registrationCommunicator.wcSetOtp(wcOTPCodeTIET.text.toString())
                registrationCommunicator.wcOtpVerify()

            }


        }



        wcResendOtpTV.setOnClickListener {
            registrationCommunicator.bcResendOtp()
            setTime()
            wcTimerTV?.show()
            wcTimerIconIV?.show()
            wcResendOtpTV?.hide()

        }

        wcSupportTextView.setOnClickListener {

            activity.callHelpLine()

        }

        wcHelplineLayout.setOnClickListener {

            activity.callHelpLine()

        }

    }


    private fun setTime() {
          wcTimerTV.show()
           wcResendOtpTV.hide()
        counter = object : CountDownTimer(Constants.counterTimeLimit.toLong(), Constants.timer_countDownInterval.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                val second = millisUntilFinished / 1000 % 60
                val minute = millisUntilFinished / (1000 * 60) % 60
                val hour = millisUntilFinished / (1000 * 60 * 60) % 24
                val time = String.format("%02d:%02d", minute, second)

               try {
                   wcTimerTV?.text = time
               } catch (e:Exception){


               }
            }

            override fun onFinish() {

                wcTimerTV?.hide()
                wcResendOtpTV?.show()
                wcTimerIconIV?.hide()

            }
        }.start()
    }

}
