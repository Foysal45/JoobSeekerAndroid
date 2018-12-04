package com.bdjobs.app.Registration.white_collar_registration

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import kotlinx.android.synthetic.main.fragment_wc_social_info.*


class WCSocialInfoFragment : Fragment() {



    private lateinit var returnView : View
    private lateinit var registrationCommunicator : RegistrationCommunicator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       returnView = inflater.inflate(R.layout.fragment_wc_social_info, container, false)
        return returnView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialization()
        onClick()
    }


    private fun onClick(){

        socilaInfoFAButton.setOnClickListener {

            registrationCommunicator.wcGoToStepName()


        }
    }

    private fun initialization(){

        registrationCommunicator = activity as RegistrationCommunicator
    }


}
