package com.bdjobs.app.Registration.blue_collar_registration

import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.callHelpLine
import com.bdjobs.app.Utilities.easyOnTextChangedListener
import com.bdjobs.app.Utilities.hideError
import com.bdjobs.app.Utilities.showError
import kotlinx.android.synthetic.main.footer_bc_layout.*
import kotlinx.android.synthetic.main.fragment_bc_name.*


class BCNameFragment : Fragment() {


    private lateinit var registrationCommunicator: RegistrationCommunicator
    private lateinit var returnView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        returnView = inflater.inflate(R.layout.fragment_bc_name, container, false)
        return returnView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialization()
        setName()
        onClick()

    }

    private fun onClick() {

        nameTIET?.easyOnTextChangedListener { charSequence ->
            nameValidityCheck(charSequence.toString())
        }

        bcNameFAButton?.setOnClickListener {


            if (nameTIET.length() == 0 || nameTIET.length() < 1) {

                nameTIL?.showError("নাম খালি রাখা যাবে না")
            } else {

                registrationCommunicator.bcGoToStepGender()
                registrationCommunicator.nameSelected(nameTIET.text.toString())
            }

        }

        supportTextView?.setOnClickListener {

            activity.callHelpLine()

        }

        bcHelpLineLayout?.setOnClickListener {

            activity.callHelpLine()
        }

        bcGoogleButton?.setOnClickListener {

            registrationCommunicator.regWithGoogle()

        }
        bc_facebookButton?.setOnClickListener {

            registrationCommunicator.regWithFacebook()

        }

    }

    private fun initialization() {

        registrationCommunicator = activity as RegistrationCommunicator

    }


    private fun nameValidityCheck(name: String): Boolean {

        when {
            TextUtils.isEmpty(name) -> {
                nameTIL?.showError("নাম খালি রাখা যাবে না")
                requestFocus(nameTIET)
                return false
            }

            else -> nameTIL?.hideError()
        }
        return true
    }

    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }


    fun setName() {

        nameTIET?.setText(registrationCommunicator.getName())
    }


}
