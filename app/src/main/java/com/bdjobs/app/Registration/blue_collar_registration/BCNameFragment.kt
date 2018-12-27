package com.bdjobs.app.Registration.blue_collar_registration

import android.os.Bundle
import android.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.easyOnTextChangedListener
import com.bdjobs.app.Utilities.hideError
import com.bdjobs.app.Utilities.showError
import kotlinx.android.synthetic.main.footer_bc_layout.*
import kotlinx.android.synthetic.main.fragment_bc_name.*
import kotlinx.android.synthetic.main.fragment_wc_name.*
import kotlinx.android.synthetic.main.fragment_wc_social_info.*
import org.jetbrains.anko.makeCall


class BCNameFragment : Fragment() {


    private lateinit var registrationCommunicator: RegistrationCommunicator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bc_name, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialization()
        setName()
        onClick()

    }

    private fun onClick(){

        nameTIET.easyOnTextChangedListener { charSequence ->
            nameValidityCheck(charSequence.toString())
        }

        bcNameFAButton.setOnClickListener {


            if(nameTIET.length() == 0 || nameTIET.length() < 2 ){

                nameTIL.showError("Name can not be empty")
            } else {

                registrationCommunicator.bcGoToStepGender()
                registrationCommunicator.nameSelected(nameTIET.text.toString())
            }

        }

        supportTextView.setOnClickListener {

            makeCall("16479")

        }

        bcHelpLineLayout.setOnClickListener {

            makeCall("16479")
        }

        bcGoogleButton.setOnClickListener {

            registrationCommunicator.regWithGoogle()

        }
        bc_facebookButton.setOnClickListener {


            registrationCommunicator.regWithFacebook()

        }

    }

    private fun initialization(){

        registrationCommunicator = activity as RegistrationCommunicator

    }



    private fun nameValidityCheck(name: String): Boolean {

        when {
            TextUtils.isEmpty(name) -> {
                nameTIL.showError(getString(R.string.field_empty_error_message_common))
                requestFocus(nameTIET)
                return false
            }
            name.length < 2  -> {
                nameTIL.showError("Your name is too short")
                requestFocus(nameTIET)
                return false
            }
            else -> nameTIL.hideError()
        }
        return true
    }

    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }


    fun setName(){

        nameTIET.setText(registrationCommunicator.getName())
    }


}
