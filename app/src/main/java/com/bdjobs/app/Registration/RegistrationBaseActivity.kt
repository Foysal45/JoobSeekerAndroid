package com.bdjobs.app.Registration

import android.app.Activity

import android.os.Bundle
import com.bdjobs.app.R
import com.bdjobs.app.Registration.blue_collar_registration.*
import com.bdjobs.app.Registration.white_collar_registration.*

import com.bdjobs.app.Utilities.transitFragment
import kotlinx.android.synthetic.main.activity_registration_base.*

class RegistrationBaseActivity : Activity(),RegistrationCommunicator {



    //white Collar
    private val registrationLandingFragment = RegistrationLandingFragment()
    private val wccategoryFragment = WCCategoryFragment()
    private val wcSocialInfoFragment = WCSocialInfoFragment()
    private val wcNameFragment = WCNameFragment()
    private val wcGenderFragment = WCGenderFragment()
    private val wcPhoneEmailFragment = WCPhoneEmailFragment()
    private val wcPasswordFragment = WCPasswordFragment()
    private val wcCongratulationFragment = WCCongratulationFragment()

    // blue Collar
    private val bcCategoryFragment = BCCategoryFragment()
    private val bcNameFragment = BCNameFragment()
    private val bcGenderFragment = BCGenderFragment()
    private val bcMobileNumberFragment = BCMobileNumberFragment()
    private val bcOtpCodeFragment = BCOtpCodeFragment()
    private val bcBirthDateFragment = BCBirthDateFragment()
    private val bcAdressFragment = BCAdressFragment()
    private val bcExperienceFragment = BCExperienceFragment()
    private val bcEducationFragment = BCEducationFragment()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_base)
        transitFragment(registrationLandingFragment, R.id.registrationFragmentHolderFL)
        backIcon.setOnClickListener {

          onBackPressed()

        }
    }


    override fun goToStepBlueCollar() {
        transitFragment(bcCategoryFragment, R.id.registrationFragmentHolderFL,true)
    }

    override fun gotToStepWhiteCollar() {
        transitFragment(wccategoryFragment, R.id.registrationFragmentHolderFL,true)
    }


    override fun wcGoToStepSocialInfo() {
        transitFragment(wcSocialInfoFragment, R.id.registrationFragmentHolderFL,true)
    }


    override fun wcGoToStepName() {
        transitFragment(wcNameFragment, R.id.registrationFragmentHolderFL,true)
    }

    override fun wcGoToStepGender() {
        transitFragment(wcGenderFragment,R.id.registrationFragmentHolderFL,true)
    }

    override fun wcGoToStepPhoneEmail() {
        transitFragment(wcPhoneEmailFragment,R.id.registrationFragmentHolderFL,true)
    }


    override fun wcGoToStepPassword() {
        transitFragment(wcPasswordFragment,R.id.registrationFragmentHolderFL,true)
    }

    override fun wcGoToStepCongratulation() {
        transitFragment(wcCongratulationFragment,R.id.registrationFragmentHolderFL,true)
    }

    override fun bcGoToStepName() {
        transitFragment(bcNameFragment,R.id.registrationFragmentHolderFL,true)
    }

    override fun bcGoToStepGender() {
        transitFragment(bcGenderFragment,R.id.registrationFragmentHolderFL,true)
    }
    override fun bcGoToStepMobileNumber() {
        transitFragment(bcMobileNumberFragment,R.id.registrationFragmentHolderFL,true)
    }

    override fun bcGoToStepOtpCode() {
        transitFragment(bcOtpCodeFragment,R.id.registrationFragmentHolderFL,true)
    }

    override fun bcGoToStepBirthDate() {

        transitFragment(bcBirthDateFragment,R.id.registrationFragmentHolderFL,true)

    }


    override fun bcGoToStepAdress() {
        transitFragment(bcAdressFragment,R.id.registrationFragmentHolderFL,true)
    }

    override fun bcGoToStepExperience() {
        transitFragment(bcExperienceFragment,R.id.registrationFragmentHolderFL,true)
    }
    override fun bcGoToStepEducation() {
        transitFragment(bcEducationFragment,R.id.registrationFragmentHolderFL,true)
    }
}
