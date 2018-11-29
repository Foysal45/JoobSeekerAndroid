package com.bdjobs.app.Registration

import android.app.Activity

import android.os.Bundle
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.transitFragment

class RegistrationBaseActivity : Activity(),RegistrationCommunicator {


    private val registrationLandingFragment = RegistrationLandingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_base)
        transitFragment(registrationLandingFragment, R.id.registrationFragmentHolderFL)
    }



}