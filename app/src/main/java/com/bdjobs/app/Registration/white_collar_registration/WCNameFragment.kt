package com.bdjobs.app.Registration.white_collar_registration


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
import kotlinx.android.synthetic.main.footer_wc_layout.*
import kotlinx.android.synthetic.main.fragment_wc_name.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.makeCall


class WCNameFragment : Fragment() {

   private lateinit var registrationCommunicator: RegistrationCommunicator

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        registrationCommunicator = activity as RegistrationCommunicator

        setName()



        onClick()


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wc_name, container, false)
    }



    private fun onClick(){




        nameFAButton.setOnClickListener {


                if(usernameTIET.length() == 0 || usernameTIET.length() < 2 ){

                    userNameTIL.showError("Name can not be empty")
                } else {

                    registrationCommunicator.wcGoToStepGender()
                    registrationCommunicator.nameSelected(usernameTIET.text.toString())
                }


            }



        wcSupportTextView.setOnClickListener {

            makeCall("16479")

        }

        wcHelplineLayout.setOnClickListener {

            makeCall("16479")
        }



        usernameTIET.easyOnTextChangedListener { charSequence ->
            nameValidityCheck(charSequence.toString())
        }

    }


    private fun nameValidityCheck(mobileNumber: String): Boolean {

        when {
            TextUtils.isEmpty(mobileNumber) -> {
                userNameTIL.showError(getString(R.string.field_empty_error_message_common))
                requestFocus(usernameTIET)
                return false
            }
            mobileNumber.length < 2  -> {
                userNameTIL.showError("Your name is too short")
                requestFocus(usernameTIET)
                return false
            }
            else -> userNameTIL.hideError()
        }
        return true
    }

    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    private fun setName(){

        if (!TextUtils.isEmpty(registrationCommunicator.getName())){

            usernameTIET.setText(registrationCommunicator.getName())
        }




    }


    override fun onResume() {
        super.onResume()



    }
}
