package com.bdjobs.app.Registration.white_collar_registration


import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import kotlinx.android.synthetic.main.fragment_wc_name.*


class WCNameFragment : Fragment() {

   private lateinit var registrationCommunicator: RegistrationCommunicator
    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        registrationCommunicator = activity as RegistrationCommunicator
        onClick()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wc_name, container, false)
    }



    private fun onClick(){

        nameFAButton.setOnClickListener {

            registrationCommunicator.wcGoToStepGender()
            registrationCommunicator.setProgreesBar()

        }

    }




}
