package com.bdjobs.app.Registration


import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.Login.LoginCommunicator

import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_registration_landing.*
import org.jetbrains.anko.toast


class RegistrationLandingFragment : Fragment() {

    private lateinit var  registrationCommunicator : RegistrationCommunicator
    private lateinit var returnView : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        returnView = inflater.inflate(R.layout.fragment_registration_landing, container, false)
        return returnView
    }





    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registrationCommunicator = activity as RegistrationCommunicator
        onClick()
    }

    fun onClick(){


       whiteCollar.setOnClickListener {

           registrationCommunicator.gotToStepWhiteCollar()
           toast("clicked")

       }

    }

}
