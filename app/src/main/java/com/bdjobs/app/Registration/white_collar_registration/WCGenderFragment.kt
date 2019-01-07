package com.bdjobs.app.Registration.white_collar_registration

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.callHelpLine
import com.bdjobs.app.Utilities.equalIgnoreCase
import kotlinx.android.synthetic.main.footer_wc_layout.*
import kotlinx.android.synthetic.main.fragment_wc_gender.*


class WCGenderFragment : Fragment() {


    private lateinit var registrationCommunicator: RegistrationCommunicator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wc_gender, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registrationCommunicator = activity as RegistrationCommunicator
        initialization()
        onClick()
    }


    private fun onClick(){

        MaleButton.setOnClickListener {
            MaleButton.iconTint = resources.getColorStateList(R.color.colorWhite)
            MaleButton.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            MaleButton.setTextColor(resources.getColor(R.color.colorWhite))


            FemaleButton.iconTint = resources.getColorStateList(R.color.colorPrimary)
            FemaleButton.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            FemaleButton.setTextColor(resources.getColor(R.color.colorPrimary))

            otherButton.iconTint = resources.getColorStateList(R.color.colorPrimary)
            otherButton.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            otherButton.setTextColor(resources.getColor(R.color.colorPrimary))

            registrationCommunicator.wcGenderSelected("M")

        }


        FemaleButton.setOnClickListener {
            FemaleButton.iconTint = resources.getColorStateList(R.color.colorWhite)
            FemaleButton.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            FemaleButton.setTextColor(resources.getColor(R.color.colorWhite))


            MaleButton.iconTint = resources.getColorStateList(R.color.colorPrimary)
            MaleButton.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            MaleButton.setTextColor(resources.getColor(R.color.colorPrimary))

            otherButton.iconTint = resources.getColorStateList(R.color.colorPrimary)
            otherButton.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            otherButton.setTextColor(resources.getColor(R.color.colorPrimary))
            registrationCommunicator.wcGenderSelected("F")

        }


        otherButton.setOnClickListener {
            otherButton.iconTint = resources.getColorStateList(R.color.colorWhite)
            otherButton.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            otherButton.setTextColor(resources.getColor(R.color.colorWhite))

            FemaleButton.iconTint = resources.getColorStateList(R.color.colorPrimary)
            FemaleButton.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            FemaleButton.setTextColor(resources.getColor(R.color.colorPrimary))


            MaleButton.iconTint = resources.getColorStateList(R.color.colorPrimary)
            MaleButton.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            MaleButton.setTextColor(resources.getColor(R.color.colorPrimary))
            registrationCommunicator.wcGenderSelected("O")


        }


        wcSupportTextView.setOnClickListener {

            activity.callHelpLine()

        }

        wcHelplineLayout.setOnClickListener {

            activity.callHelpLine()
        }


    }

    private fun initialization(){



    }

    fun goToNextStep(){

        genderFAButton.setOnClickListener {

            registrationCommunicator.wcGoToStepPhoneEmail()
        }


    }


    override fun onResume() {
        super.onResume()

        /* val  gender = registrationCommunicator.getGender()
           if (gender != null) {*/

        if (registrationCommunicator.getGender().equalIgnoreCase("M")) {


            MaleButton.iconTint = resources.getColorStateList(R.color.colorWhite)
            MaleButton.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            MaleButton.setTextColor(resources.getColor(R.color.colorWhite))


            FemaleButton.iconTint = resources.getColorStateList(R.color.colorPrimary)
            FemaleButton.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            FemaleButton.setTextColor(resources.getColor(R.color.colorPrimary))

            otherButton.iconTint = resources.getColorStateList(R.color.colorPrimary)
            otherButton.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            otherButton.setTextColor(resources.getColor(R.color.colorPrimary))

            registrationCommunicator.wcGenderSelected("M")


        } else if (registrationCommunicator.getGender().equalIgnoreCase("F")) {


            FemaleButton.iconTint = resources.getColorStateList(R.color.colorWhite)
            FemaleButton.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            FemaleButton.setTextColor(resources.getColor(R.color.colorWhite))


            MaleButton.iconTint = resources.getColorStateList(R.color.colorPrimary)
            MaleButton.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            MaleButton.setTextColor(resources.getColor(R.color.colorPrimary))

            otherButton.iconTint = resources.getColorStateList(R.color.colorPrimary)
            otherButton.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            otherButton.setTextColor(resources.getColor(R.color.colorPrimary))
            registrationCommunicator.wcGenderSelected("F")


        } else if (registrationCommunicator.getGender().equalIgnoreCase("O")) {

            otherButton.iconTint = resources.getColorStateList(R.color.colorWhite)
            otherButton.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            otherButton.setTextColor(resources.getColor(R.color.colorWhite))

            FemaleButton.iconTint = resources.getColorStateList(R.color.colorPrimary)
            FemaleButton.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            FemaleButton.setTextColor(resources.getColor(R.color.colorPrimary))


            MaleButton.iconTint = resources.getColorStateList(R.color.colorPrimary)
            MaleButton.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            MaleButton.setTextColor(resources.getColor(R.color.colorPrimary))
            registrationCommunicator.wcGenderSelected("O")

        }

        /*   }*/


    }
}
