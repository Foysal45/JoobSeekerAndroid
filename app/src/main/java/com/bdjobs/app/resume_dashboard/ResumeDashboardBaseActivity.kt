package com.bdjobs.app.resume_dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import com.bdjobs.app.R
import com.bdjobs.app.databinding.ActivityResumeDashboardBaseBinding
import com.google.android.material.tabs.TabLayout

class ResumeDashboardBaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResumeDashboardBaseBinding
    private lateinit var graph : NavGraph

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
}