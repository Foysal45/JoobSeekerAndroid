package com.bdjobs.app.Registration.white_collar_registration

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.callHelpLine
import kotlinx.android.synthetic.main.footer_wc_layout.*
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

        enterInfoButton?.setOnClickListener {
            registrationCommunicator.wcGoToStepName()
            val mail1 =  registrationCommunicator.wcGetEmail()
            Log.e("email", "before = "+mail1)


            registrationCommunicator.wcSetEmail("")
            registrationCommunicator.wcSetOther()



            val mail =  registrationCommunicator.wcGetEmail()
            Log.e("email", "after = "+mail)
        }
        wcSupportTextView?.setOnClickListener {
            activity.callHelpLine()
        }
        wcHelplineLayout?.setOnClickListener {
            activity.callHelpLine()
        }

        google_button?.setOnClickListener {
            /*   registrationCommunicator.clearData()*/
            registrationCommunicator.regWithGoogle()

        }
        facebook_button?.setOnClickListener {
            /*  registrationCommunicator.clearData()*/
            registrationCommunicator.regWithFacebook()

        }
        linkedIn_button?.setOnClickListener {
            /*  registrationCommunicator.clearData()*/
            registrationCommunicator.regWithLinkedIn()

        }

    }

    private fun initialization(){
         registrationCommunicator = activity as RegistrationCommunicator
    }
}
