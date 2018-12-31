package com.bdjobs.app.Employers

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.logException
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
    private var companyid = ""
    private var companyname = ""
    private var value = ""
    private val followedEmployersListFragment = FollowedEmployersListFragment()
    private val employerJobListFragment = EmployerJobListFragment()
    private val employerListFragment = EmployerListFragment()

    override fun gotoJobListFragment(companyID: String?, companyName: String?) {
        companyid = companyID!!
        companyname = companyName!!

        transitFragment(employerJobListFragment, R.id.fragmentHolder, true)
    }

    override fun getCompanyID(): String {
        return companyid
    }

    override fun getCompanyName(): String {
        return companyname
    }


    override fun backButtonPressed() {
        onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employers_base)

        try {
            value = intent.getStringExtra("from")
        } catch (e: Exception) {
            logException(e)
        }
        Log.d("value", "value = $value")
        if (value?.equals("follow")) {
            transitFragment(followedEmployersListFragment, R.id.fragmentHolder)
        } else if (value?.equals("emplist")) {
            transitFragment(employerListFragment, R.id.fragmentHolder)
        }
    }

}