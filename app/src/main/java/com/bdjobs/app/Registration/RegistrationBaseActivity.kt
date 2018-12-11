package com.bdjobs.app.Registration

import android.app.Activity

import android.os.Bundle
import android.util.Log
import android.view.View
import com.bdjobs.app.R
import com.bdjobs.app.Registration.blue_collar_registration.*
import com.bdjobs.app.Registration.white_collar_registration.*
import com.bdjobs.app.Utilities.simpleClassName

import com.bdjobs.app.Utilities.transitFragment
import kotlinx.android.synthetic.main.activity_registration_base.*
import org.jetbrains.anko.toast

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
    private val bcPhotoUploadFragment = BCPhotoUploadFragment()
    private val bcCongratulationFragment = BCCongratulationFragment()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_base)
        stepProgressBar.visibility = View.GONE

        transitFragment(registrationLandingFragment, R.id.registrationFragmentHolderFL)

        backIcon.setOnClickListener {

          onBackPressed()

            setProgreesBar()
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        setProgreesBar()
    }

    override fun goToStepBlueCollar() {
        stepProgressBar.progress = 10
        transitFragment(bcCategoryFragment, R.id.registrationFragmentHolderFL,true)
    }

    override fun gotToStepWhiteCollar() {
        setProgreesBar()
        stepProgressBar.progress = 10
        transitFragment(wccategoryFragment, R.id.registrationFragmentHolderFL,true)

    }

    //white Collar

    override fun wcGoToStepSocialInfo() {


        transitFragment(wcSocialInfoFragment, R.id.registrationFragmentHolderFL,true)
        setProgreesBar()


    }


    override fun wcGoToStepName() {

        transitFragment(wcNameFragment, R.id.registrationFragmentHolderFL,true)
        setProgreesBar()
        stepProgressBar.progress = 20


    }

    override fun wcGoToStepGender() {
        transitFragment(wcGenderFragment,R.id.registrationFragmentHolderFL,true)
        setProgreesBar()
        stepProgressBar.progress = 40

    }

    override fun wcGoToStepPhoneEmail() {
        transitFragment(wcPhoneEmailFragment,R.id.registrationFragmentHolderFL,true)
        setProgreesBar()
        stepProgressBar.progress = 60

    }


    override fun wcGoToStepPassword() {
        transitFragment(wcPasswordFragment,R.id.registrationFragmentHolderFL,true)
        setProgreesBar()
        stepProgressBar.progress = 80

    }

    override fun wcGoToStepCongratulation() {
        transitFragment(wcCongratulationFragment,R.id.registrationFragmentHolderFL,true)
    }

    // blue Collar
    override fun bcGoToStepName() {
        transitFragment(bcNameFragment,R.id.registrationFragmentHolderFL,true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 10

    }

    override fun bcGoToStepGender() {
        transitFragment(bcGenderFragment,R.id.registrationFragmentHolderFL,true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 20

    }
    override fun bcGoToStepMobileNumber() {
        transitFragment(bcMobileNumberFragment,R.id.registrationFragmentHolderFL,true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 30

    }

    override fun bcGoToStepOtpCode() {
        transitFragment(bcOtpCodeFragment,R.id.registrationFragmentHolderFL,true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 40

    }

    override fun bcGoToStepBirthDate() {
        transitFragment(bcBirthDateFragment,R.id.registrationFragmentHolderFL,true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 50


    }


    override fun bcGoToStepAdress() {
        transitFragment(bcAdressFragment,R.id.registrationFragmentHolderFL,true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 60

    }

    override fun bcGoToStepExperience() {
        transitFragment(bcExperienceFragment,R.id.registrationFragmentHolderFL,true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 70

    }
    override fun bcGoToStepEducation() {
        transitFragment(bcEducationFragment,R.id.registrationFragmentHolderFL,true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 80

    }


    override fun bcGoToStepPhotoUpload() {
        transitFragment(bcPhotoUploadFragment,R.id.registrationFragmentHolderFL,true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 90

    }


    override fun bcGoToStepCongratulation() {
        transitFragment(bcCongratulationFragment,R.id.registrationFragmentHolderFL,true)
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 100

    }


    override fun onBackPressed() {
        super.onBackPressed()
        setProgreesBar()
    }

    override fun setProgreesBar() {
       toast("stepChange")

        val wcCategoryFragment = fragmentManager.findFragmentByTag(simpleClassName(wccategoryFragment))
        if(wcCategoryFragment!=null && wcCategoryFragment.isVisible){
            Log.d("stepChange","FragmentLive: ${simpleClassName(wcCategoryFragment)} ")
        }


    }

}
