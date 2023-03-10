package com.bdjobs.app.Registration.white_collar_registration


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import kotlinx.android.synthetic.main.fragment_wc_congratulation.*

class WCCongratulationFragment : Fragment() {


    private lateinit var registrationCommunicator: RegistrationCommunicator


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wc_congratulation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        initialization()
        onClick()
    }
    private fun initialization(){
        registrationCommunicator = activity as RegistrationCommunicator
    }
    private fun onClick(){
        wcJobSearchButton?.setOnClickListener {
            registrationCommunicator.goToHomePage()
        }
    }
}
