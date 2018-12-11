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
      /*  transitFragment(registrationLandingFragment, R.id.registrationFragmentHolderFL)*/


        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.registrationFragmentHolderFL, registrationLandingFragment, "registrationLandingFragment")
        transaction.commit()




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

        setProgreesBar()
        transitFragment(wcSocialInfoFragment, R.id.registrationFragmentHolderFL,true)



    }


    override fun wcGoToStepName() {
        setProgreesBar()
        stepProgressBar.progress = 20
        transitFragment(wcNameFragment, R.id.registrationFragmentHolderFL,true)


    }

    override fun wcGoToStepGender() {
        setProgreesBar()
        stepProgressBar.progress = 40
        transitFragment(wcGenderFragment,R.id.registrationFragmentHolderFL,true)
    }

    override fun wcGoToStepPhoneEmail() {
        setProgreesBar()
        stepProgressBar.progress = 60
        transitFragment(wcPhoneEmailFragment,R.id.registrationFragmentHolderFL,true)
    }


    override fun wcGoToStepPassword() {
        setProgreesBar()
        stepProgressBar.progress = 80
        transitFragment(wcPasswordFragment,R.id.registrationFragmentHolderFL,true)
    }

    override fun wcGoToStepCongratulation() {
        transitFragment(wcCongratulationFragment,R.id.registrationFragmentHolderFL,true)
    }

    // blue Collar
    override fun bcGoToStepName() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 10
        transitFragment(bcNameFragment,R.id.registrationFragmentHolderFL,true)
    }

    override fun bcGoToStepGender() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 20
        transitFragment(bcGenderFragment,R.id.registrationFragmentHolderFL,true)
    }
    override fun bcGoToStepMobileNumber() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 30
        transitFragment(bcMobileNumberFragment,R.id.registrationFragmentHolderFL,true)
    }

    override fun bcGoToStepOtpCode() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 40
        transitFragment(bcOtpCodeFragment,R.id.registrationFragmentHolderFL,true)
    }

    override fun bcGoToStepBirthDate() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 50
        transitFragment(bcBirthDateFragment,R.id.registrationFragmentHolderFL,true)

    }


    override fun bcGoToStepAdress() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 60
        transitFragment(bcAdressFragment,R.id.registrationFragmentHolderFL,true)
    }

    override fun bcGoToStepExperience() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 70
        transitFragment(bcExperienceFragment,R.id.registrationFragmentHolderFL,true)
    }
    override fun bcGoToStepEducation() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 80
        transitFragment(bcEducationFragment,R.id.registrationFragmentHolderFL,true)
    }


    override fun bcGoToStepPhotoUpload() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 90
        transitFragment(bcPhotoUploadFragment,R.id.registrationFragmentHolderFL,true)
    }


    override fun bcGoToStepCongratulation() {
        stepProgressBar.visibility = View.VISIBLE
        stepProgressBar.progress = 100
        transitFragment(bcCongratulationFragment,R.id.registrationFragmentHolderFL,true)
    }

    override fun setProgreesBar() {
       toast("stepChange")
       if (wcNameFragment.isVisible && wcNameFragment.isResumed){

           Log.d("FragmentTest","current fragment name fragment")

       }


    }

}
