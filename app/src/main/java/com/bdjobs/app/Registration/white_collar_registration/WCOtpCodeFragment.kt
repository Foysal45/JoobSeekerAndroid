package com.bdjobs.app.Registration.white_collar_registration

import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import kotlinx.android.synthetic.main.fragment_wc_otp_code.*
import org.jetbrains.anko.toast


class WCOtpCodeFragment : Fragment() {



    private lateinit var registrationCommunicator : RegistrationCommunicator

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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

        timeLayout.setOnClickListener {

            toast("time clicked")

        }

    }

}
