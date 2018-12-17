package com.bdjobs.app.Employers

import android.app.Activity
import android.os.Bundle
import com.bdjobs.app.R

class EmployersBaseActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employers_base)
    }
}
