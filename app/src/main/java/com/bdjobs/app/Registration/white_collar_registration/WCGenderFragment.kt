package com.bdjobs.app.Registration.white_collar_registration

import android.os.Bundle
import android.app.Fragment
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import kotlinx.android.synthetic.main.fragment_wc_gender.*
import org.jetbrains.anko.textColor


class WCGenderFragment : Fragment() {


    private lateinit var registrationCommunicator: RegistrationCommunicator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wc_gender, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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




    }

    private fun initialization(){

        registrationCommunicator = activity as RegistrationCommunicator

    }

    fun goToNextStep(){

        genderFAButton.setOnClickListener {

            registrationCommunicator.wcGoToStepPhoneEmail()
        }


    }

}
