package com.bdjobs.app.assessment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.bdjobs.app.R
import kotlinx.android.synthetic.main.activity_assesment_base.*


class AssesmentBaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assesment_base)

        setSupportActionBar(toolbar)


        toolbar.title = "Employability Certification"

        val navHostFragment = assessmentNavHostFragment as NavHostFragment
        val inflater = navHostFragment.navController.navInflater

        val graph = inflater.inflate(R.navigation.assessment_navigation)

//        graph.startDestination = R.id.testInstructionFragment

        navHostFragment.navController.graph = graph

        val appBarConfiguration =
                AppBarConfiguration.Builder()
                        .setFallbackOnNavigateUpListener { onNavigateUp() }
                        .build()

        val navController = this.findNavController(R.id.assessmentNavHostFragment)

        toolbar.apply {
            setupActionBarWithNavController(navController, appBarConfiguration)
        }

//        NavigationUI.setupActionBarWithNavController(this, navController)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.assessmentNavHostFragment)
        return navController.navigateUp()|| super.onSupportNavigateUp()
    }

    override fun onNavigateUp(): Boolean {
        finish()
        return true
    }


}
