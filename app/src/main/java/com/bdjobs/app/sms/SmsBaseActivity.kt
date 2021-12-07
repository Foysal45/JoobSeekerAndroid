package com.bdjobs.app.sms

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.utilities.hide
import com.bdjobs.app.utilities.show
import kotlinx.android.synthetic.main.activity_sms_base.*
import kotlinx.android.synthetic.main.activity_sms_base.view.*

class SmsBaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms_base)

        val from = intent.getStringExtra("from")

        val navHostFragment = smsNavHostFragment as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.sms_nav_graph)

        when (from) {
            "employer", "favourite", "settings" -> {
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


        navHostFragment.navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.smsHomeFragment){
                tool_bar?.sms_settings?.show()
                tool_bar?.sms_settings?.setOnClickListener {
                    navHostFragment.navController.navigate(com.bdjobs.app.sms.ui.home.HomeFragmentDirections.actionSmsHomeFragmentToSettingsFragment())
                }
            } else {
                tool_bar?.sms_settings?.hide()
            }
        }
    }

    override fun onNavigateUp(): Boolean {
        super.finish()
        return true
    }
}