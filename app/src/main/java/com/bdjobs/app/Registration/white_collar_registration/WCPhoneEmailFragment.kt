package com.bdjobs.app.Registration.white_collar_registration


import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import kotlinx.android.synthetic.main.fragment_wc_password.*
import kotlinx.android.synthetic.main.fragment_wc_phone_email.*


class WCPhoneEmailFragment : Fragment() {


    private lateinit var registrationCommunicator: RegistrationCommunicator
    private lateinit var returnView : View

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialization()
        onClick()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        returnView = inflater.inflate(R.layout.fragment_wc_phone_email, container, false)
        return returnView
    }



    private fun onClick(){

        phoneEmailFAButton.setOnClickListener {

            registrationCommunicator.wcGoToStepPassword()

        }



    }


    private fun initialization(){

        registrationCommunicator = activity as RegistrationCommunicator

    }




}
