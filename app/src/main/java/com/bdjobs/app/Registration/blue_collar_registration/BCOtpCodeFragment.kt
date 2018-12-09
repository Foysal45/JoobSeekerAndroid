package com.bdjobs.app.Registration.blue_collar_registration

import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import kotlinx.android.synthetic.main.fragment_bc_otp_code.*


class BCOtpCodeFragment : Fragment() {


    private lateinit var registrationCommunicator: RegistrationCommunicator


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bc_otp_code, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onClick()
        initialization()
    }

    private fun onClick(){

        bcOTPFAButton.setOnClickListener {

            registrationCommunicator.bcGoToStepBirthDate()
        }

    }

    private fun initialization(){

        registrationCommunicator = activity as RegistrationCommunicator

    }



}
