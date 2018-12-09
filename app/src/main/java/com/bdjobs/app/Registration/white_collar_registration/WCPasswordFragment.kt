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
import kotlinx.android.synthetic.main.fragment_wc_password.*


class WCPasswordFragment : Fragment() {


    private lateinit var returnView: View
    private lateinit var registrationCommunicator: RegistrationCommunicator


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       returnView = inflater.inflate(R.layout.fragment_wc_password, container, false)
        return returnView
    }

    // TODO: Rename method, update argument and hook method into UI event


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        onClick()
        initialization()


    }

    private fun onClick(){

        passwordFAButton.setOnClickListener {

                registrationCommunicator.wcGoToStepCongratulation()

        }


        phone_button.setOnClickListener {


            phone_button.iconTint = resources.getColorStateList(R.color.colorWhite)
            phone_button.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            phone_button.setTextColor(resources.getColor(R.color.colorWhite))



            email_button.iconTint = resources.getColorStateList(R.color.colorPrimary)
            email_button.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            email_button.setTextColor(resources.getColor(R.color.colorPrimary))
        }


        email_button.setOnClickListener {

            email_button.iconTint = resources.getColorStateList(R.color.colorWhite)
            email_button.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            email_button.setTextColor(resources.getColor(R.color.colorWhite))

            phone_button.iconTint = resources.getColorStateList(R.color.colorPrimary)
            phone_button.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            phone_button.setTextColor(resources.getColor(R.color.colorPrimary))
        }

    }

    private fun initialization(){

        registrationCommunicator = activity as RegistrationCommunicator

    }
}
