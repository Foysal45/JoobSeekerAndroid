package com.bdjobs.app.Registration.blue_collar_registration

import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.callHelpLine
import kotlinx.android.synthetic.main.footer_bc_layout.*
import kotlinx.android.synthetic.main.fragment_bc_gender.*
import org.jetbrains.anko.makeCall


class BCGenderFragment : Fragment() {



    private lateinit var registrationCommunicator: RegistrationCommunicator
    private lateinit var returnView: View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        returnView = inflater.inflate(R.layout.fragment_bc_gender, container, false)

        return returnView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialization()
        onClick()
    }


    private fun onClick(){

        bcMaleButton.setOnClickListener {
            bcMaleButton.iconTint = resources.getColorStateList(R.color.colorWhite)
            bcMaleButton.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            bcMaleButton.setTextColor(resources.getColor(R.color.colorWhite))


            bcFemaleButton.iconTint = resources.getColorStateList(R.color.colorPrimary)
            bcFemaleButton.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            bcFemaleButton.setTextColor(resources.getColor(R.color.colorPrimary))

            bcOtherButton.iconTint = resources.getColorStateList(R.color.colorPrimary)
            bcOtherButton.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            bcOtherButton.setTextColor(resources.getColor(R.color.colorPrimary))
            registrationCommunicator.bcGenderSelected("M")

        }


        bcFemaleButton.setOnClickListener {
            bcFemaleButton.iconTint = resources.getColorStateList(R.color.colorWhite)
            bcFemaleButton.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            bcFemaleButton.setTextColor(resources.getColor(R.color.colorWhite))


            bcMaleButton.iconTint = resources.getColorStateList(R.color.colorPrimary)
            bcMaleButton.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            bcMaleButton.setTextColor(resources.getColor(R.color.colorPrimary))

            bcOtherButton.iconTint = resources.getColorStateList(R.color.colorPrimary)
            bcOtherButton.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            bcOtherButton.setTextColor(resources.getColor(R.color.colorPrimary))
            registrationCommunicator.bcGenderSelected("F")

        }


        bcOtherButton.setOnClickListener {
            bcOtherButton.iconTint = resources.getColorStateList(R.color.colorWhite)
            bcOtherButton.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            bcOtherButton.setTextColor(resources.getColor(R.color.colorWhite))

            bcFemaleButton.iconTint = resources.getColorStateList(R.color.colorPrimary)
            bcFemaleButton.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            bcFemaleButton.setTextColor(resources.getColor(R.color.colorPrimary))


            bcMaleButton.iconTint = resources.getColorStateList(R.color.colorPrimary)
            bcMaleButton.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            bcMaleButton.setTextColor(resources.getColor(R.color.colorPrimary))

            registrationCommunicator.bcGenderSelected("O")


        }

        supportTextView.setOnClickListener {

           activity.callHelpLine()

        }

        bcHelpLineLayout.setOnClickListener {

            activity.callHelpLine()
        }




    }

    private fun initialization(){

        registrationCommunicator = activity as RegistrationCommunicator

    }

    fun goToNextStep(){

        bcGenderFAButton.setOnClickListener {

            registrationCommunicator.bcGoToStepMobileNumber()
        }

    }

}
