package com.bdjobs.app.Assessment

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.bdjobs.app.R

import kotlinx.android.synthetic.main.activity_assesment_base.*

class AssesmentBaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assesment_base)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.title = "Employability Certification"

        val navController = this.findNavController(R.id.assessmentNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController)


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.assessmentNavHostFragment)
        return navController.navigateUp()
    }

}
