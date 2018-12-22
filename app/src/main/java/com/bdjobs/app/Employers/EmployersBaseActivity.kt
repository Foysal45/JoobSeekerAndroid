package com.bdjobs.app.Employers

import android.app.Activity
import android.os.Bundle
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.transitFragment


/*=============================================================================
 |
 |Author :  Firoz Hasan
 |Date : 22 Dec 2018
 |BASIC IDEA : IN ONE MODULE/PACKAGE THERE WILL BE ONE BASE ACTIVITY
 |             WHICH GONNA CONTROL ALL DIFFERENT FRAGMETS. IN THIS CASE WE HAVE
 |             FOLLOWEDEMPLOYERLISTFRAGMENT AND EMPLOYERSCOMMUNICATOR(INTERFACE)
 |             WHICH WILL BE USE AS A BRIDGE BETWEEN activity and fragments
 |Purpose: In FollowedEmployersListFragment we instantiate
 |         BdjobsDB (roomdb) to get followedemployerlist then send that lists
 |         to adapter and performs unfollow operation by deleting it
 |         fm roomdb and calling rest api.
 |
 |
 |
 |
 |
 |
 |
 |
 |
 +-----------------------------------------------------------------------------
 */
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
