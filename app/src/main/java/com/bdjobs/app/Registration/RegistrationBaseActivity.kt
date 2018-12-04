package com.bdjobs.app.Registration

import android.app.Activity

import android.os.Bundle
import com.bdjobs.app.R
import com.bdjobs.app.Registration.white_collar_registration.WCCategoryFragment
import com.bdjobs.app.Registration.white_collar_registration.WCNameFragment
import com.bdjobs.app.Registration.white_collar_registration.WCSocialInfoFragment

import com.bdjobs.app.Utilities.transitFragment

class RegistrationBaseActivity : Activity(),RegistrationCommunicator {


    //white Collar
    private val registrationLandingFragment = RegistrationLandingFragment()
    private val wccategoryFragment = WCCategoryFragment()
    private val wcSocialInfoFragment = WCSocialInfoFragment()
    private val wcNameFragment = WCNameFragment()

    // blue Collar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_base)
        transitFragment(registrationLandingFragment, R.id.registrationFragmentHolderFL)
    }


    override fun goToStepBlueCollar() {

    }

    override fun gotToStepWhiteCollar() {
        transitFragment(wccategoryFragment, R.id.registrationFragmentHolderFL,true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun backButtonClicked() {
       onBackPressed()
    }


    override fun wcGoToStepSocialInfo() {
        transitFragment(wcSocialInfoFragment, R.id.registrationFragmentHolderFL,true)
    }


    override fun wcGoToStepName() {

        transitFragment(wcNameFragment, R.id.registrationFragmentHolderFL,true)

    }

}
