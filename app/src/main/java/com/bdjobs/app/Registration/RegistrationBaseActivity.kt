package com.bdjobs.app.Registration

import android.app.Activity

import android.os.Bundle
import com.bdjobs.app.R

class RegistrationBaseActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_base)
    }
}
