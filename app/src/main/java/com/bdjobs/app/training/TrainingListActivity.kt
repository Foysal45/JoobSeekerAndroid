package com.bdjobs.app.training

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.R
import com.bdjobs.app.databinding.ActivityTrainingListAcitivityBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar

class TrainingListActivity : AppCompatActivity(),
    ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var mBinding: ActivityTrainingListAcitivityBinding
    private var mSnackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_training_list_acitivity)

        setSupportActionBar(mBinding.toolbarTraining)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val hostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentHolder) as NavHostFragment
        val navController = hostFragment.navController
        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.training_nav_graph)

        navController.graph = graph


        val adRequest = AdRequest.Builder().build()
        mBinding.adView.loadAd(adRequest)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
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
                .make(
                    mBinding.root,
                    getString(R.string.alert_no_internet),
                    Snackbar.LENGTH_INDEFINITE
                )
                .setAction(getString(R.string.turn_on_wifi)) {
                    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                }
                .setActionTextColor(ContextCompat.getColor(this, R.color.colorWhite))

            mSnackBar?.show()

        } else {
            mSnackBar?.dismiss()
        }
    }

}
