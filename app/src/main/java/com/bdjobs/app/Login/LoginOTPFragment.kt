package com.bdjobs.app.Login

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_login_username.*

class LoginOTPFragment : Fragment() {

    lateinit var loginCommunicator: LoginCommunicator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_login_username, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginCommunicator = activity as LoginCommunicator
        onClicks()
    }

    private fun onClicks() {
        backBtnIMGV.setOnClickListener {
            loginCommunicator?.backButtonClicked()
        }
    }




}