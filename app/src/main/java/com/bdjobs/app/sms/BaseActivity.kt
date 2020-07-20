package com.bdjobs.app.sms

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.activity_base.*

class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        val from = intent.getStringExtra("from")

        val navHostFragment = smsNavHostFragment as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.sms_nav_graph)

        when (from) {
            "employer", "favourite" -> {
                graph.startDestination = R.id.settingsFragment
            }
            else -> {
                if (Constants.isSMSFree.equalIgnoreCase("True"))
                    graph.startDestination = R.id.smsFreeTrialHomeFragment
                else
                    graph.startDestination = R.id.smsHomeFragment
            }
        }

        navHostFragment.navController.graph = graph

        val appBarConfiguration = AppBarConfiguration.Builder()
                .setFallbackOnNavigateUpListener { onNavigateUp() }
                .build()
        tool_bar?.setupWithNavController(navHostFragment.navController, appBarConfiguration)

    }

    override fun onNavigateUp(): Boolean {
        finish()
        return true
    }


}