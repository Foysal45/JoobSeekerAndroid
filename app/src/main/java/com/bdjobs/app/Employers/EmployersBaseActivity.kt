package com.bdjobs.app.Employers

import android.app.Activity
import android.os.Bundle
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.transitFragment

class EmployersBaseActivity : Activity(), EmployersCommunicator {
    override fun backButtonPressed() {
        onBackPressed()
    }

    private val  followedEmployersListFragment = FollowedEmployersListFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employers_base)
        transitFragment(followedEmployersListFragment,R.id.fragmentHolder)
    }

}
