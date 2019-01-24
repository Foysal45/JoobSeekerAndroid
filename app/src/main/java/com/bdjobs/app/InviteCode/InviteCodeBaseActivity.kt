package com.bdjobs.app.InviteCode

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import com.bdjobs.app.R
import kotlinx.android.synthetic.main.activity_invite_code_base.*

class InviteCodeBaseActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_code_base)



        val navHostFragment = inviteCodeBaseNavFragment as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.invite_code_base_navigation)

       // graph.startDestination = R.id.navigation_user_invite_code_submit

       // graph.startDestination = R.id.navigation_user_invite_code_status

        graph.startDestination = R.id.navigation_owner_base


        navHostFragment.navController.graph = graph

    }
}
