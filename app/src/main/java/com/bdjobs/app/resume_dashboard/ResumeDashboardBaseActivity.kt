package com.bdjobs.app.resume_dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.R
import com.bdjobs.app.databinding.ActivityResumeDashboardBaseBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_login_base.*
import timber.log.Timber

class ResumeDashboardBaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var binding: ActivityResumeDashboardBaseBinding
    private lateinit var graph : NavGraph
    private var mSnackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_resume_dashboard_base)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val hostFragment = findNavController(R.id.resumeDashboardHostFragment)
        val inflater = hostFragment.navInflater
        graph = inflater.inflate(R.navigation.resume_dashboard_nav_graph)

        tabListener()

        hostFragment.graph = graph
    }




    private fun tabListener() {

        val navController = findNavController(R.id.resumeDashboardHostFragment)
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (binding.tabs.selectedTabPosition == 0) {
                    navController.navigate(R.id.dashboardFragment)
                } else navController.navigate(R.id.viewEditResumeFragment)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(event?.action == KeyEvent.ACTION_DOWN) {
            onBackPressed()
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        showSnackBar(isConnected)
    }

    private fun showSnackBar(isConnected: Boolean) {

        if (!isConnected) {
            mSnackBar = Snackbar
                .make(binding.clParent, getString(R.string.alert_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.turn_on_wifi)) {
                    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                }
                .setActionTextColor(resources.getColor(R.color.colorWhite))

            mSnackBar?.show()

        } else {
            mSnackBar?.dismiss()
        }
    }

}