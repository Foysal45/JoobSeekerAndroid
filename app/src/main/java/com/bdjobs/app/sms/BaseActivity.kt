package com.bdjobs.app.sms

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_invite_code_base.*

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