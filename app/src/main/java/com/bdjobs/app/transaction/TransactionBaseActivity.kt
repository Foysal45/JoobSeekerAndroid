package com.bdjobs.app.transaction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.Registration.blue_collar_registration.BCPhotoUploadFragment
import com.bdjobs.app.transaction.ui.TransactionListFragment
import kotlinx.android.synthetic.main.activity_base.*

class TransactionBaseActivity : AppCompatActivity() {

    private val transactionListFragment = TransactionListFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_base)

        val appBarConfiguration = AppBarConfiguration.Builder()
                .setFallbackOnNavigateUpListener { onNavigateUp() }
                .build()


        val navController = this.findNavController(R.id.transactionNavHostFragment)

        tool_bar?.setupWithNavController(navController,appBarConfiguration)
       /* navController.popBackStack(R.id.transactionListFragment, true)*/
    }

    override fun onNavigateUp(): Boolean {
        finish()
        return true
    }

    /*override fun onBackPressed() {

        finish()

    }*/
}