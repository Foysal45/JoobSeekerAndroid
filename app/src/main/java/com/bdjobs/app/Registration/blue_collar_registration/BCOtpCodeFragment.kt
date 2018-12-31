package com.bdjobs.app.Registration.blue_collar_registration

import android.os.Bundle
import android.app.Fragment
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.callHelpLine
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.footer_bc_layout.*
import kotlinx.android.synthetic.main.fragment_bc_otp_code.*
import kotlinx.android.synthetic.main.fragment_login_otp.*
import kotlinx.android.synthetic.main.fragment_wc_otp_code.*
import org.jetbrains.anko.toast
import java.lang.Exception


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

      /*  bcOTPFAButton.setOnClickListener {

            registrationCommunicator.bcGoToStepBirthDate()
        }*/


        bcOTPFAButton.setOnClickListener {
            registrationCommunicator.wcSetOtp(bcOTPCodeTIET.text.toString())


            ///---------------api---------calling-------------////

            registrationCommunicator.wcOtpVerify()

          /*  registrationCommunicator.bcGoToStepBirthDate()*/
        }

        bctimeLayout.setOnClickListener {

            setTime()

        }

       /* bctimeLayout.setOnClickListener {

              toast("clicked")
              registrationCommunicator.bcResendOtp()

        }*/

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

        bcInfoMobileNumberTV.text = "${registrationCommunicator.wcGetMobileNumber()} নম্বরে এস এম এস এর মাধ্যেমে একটি কোড পাঠানো হয়েছে, দয়াকরে কোডটি লিখুন ।"


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
