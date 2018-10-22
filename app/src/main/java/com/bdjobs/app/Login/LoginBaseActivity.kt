package com.bdjobs.app.Login

import android.app.Activity
import android.os.Bundle
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.transitFragment

class LoginBaseActivity : Activity(), LoginCommunicator {

    private val loginLandingFragment = LoginLandingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_base)
        transitFragment(loginLandingFragment,R.id.loginFragmentHolderFL)
    }

    override fun backButtonClicked() {
        onBackPressed()
    }
}
