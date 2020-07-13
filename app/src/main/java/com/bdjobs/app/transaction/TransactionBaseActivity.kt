package com.bdjobs.app.transaction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import kotlinx.android.synthetic.main.activity_base.*

class TransactionBaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_base)

        val appBarConfiguration = AppBarConfiguration.Builder()
                .setFallbackOnNavigateUpListener { onNavigateUp() }
                .build()

        val navController = this.findNavController(R.id.transactionNavHostFragment)

        tool_bar?.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onNavigateUp(): Boolean {
        finish()
        return true
    }
}