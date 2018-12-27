package com.bdjobs.app.Registration.white_collar_registration

import android.os.Bundle
import android.app.Fragment
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.footer_wc_layout.*
import kotlinx.android.synthetic.main.fragment_bc_otp_code.*
import kotlinx.android.synthetic.main.fragment_wc_otp_code.*
import org.jetbrains.anko.makeCall
import org.jetbrains.anko.toast
import java.lang.Exception


class WCOtpCodeFragment : Fragment() {



    private lateinit var registrationCommunicator : RegistrationCommunicator
    private lateinit var counter: CountDownTimer
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setTime()
        initialization()
        onClick()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wc_otp_code, container, false)
    }


    private fun initialization(){

        registrationCommunicator = activity as RegistrationCommunicator
        wcInfoMobileNumberTV.text = " Please type the verification code sent to ${registrationCommunicator.wcGetMobileNumber()} "

    }

    private fun onClick(){



        wcOTPFAButton.setOnClickListener {
            registrationCommunicator.wcSetOtp(wcOTPCodeTIET.text.toString())
            registrationCommunicator.wcOtpVerify()
        }

       /* timeLayout.setOnClickListener {

           *//* activity.toast("time clicked")*//*


            setTime()
        }*/

        wcResendOtpTV.setOnClickListener {
            registrationCommunicator.bcResendOtp()
            setTime()
            wcTimerTV.show()
            wcTimerIconIV.show()
            wcResendOtpTV.hide()

        }

       /* wcSupportTextView.setOnClickListener {

           activity.makeCall("16479")

        }

        wcHelplineLayout.setOnClickListener {

           activity.makeCall("16479")

        }*/

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
                   wcTimerTV.text = time
               } catch (e:Exception){


               }
            }

            override fun onFinish() {

                wcTimerTV.hide()
                wcResendOtpTV.show()
                wcTimerIconIV.hide()

            }
        }.start()
    }

}
