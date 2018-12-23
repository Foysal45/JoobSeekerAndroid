package com.bdjobs.app.editResume.employmentHistory

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.Utilities.transitFragment
import com.bdjobs.app.editResume.adapters.models.DataItem
import com.bdjobs.app.editResume.callbacks.EmpHisCB
import com.bdjobs.app.editResume.employmentHistory.fragments.ArmyEmpHisViewFragment
import com.bdjobs.app.editResume.employmentHistory.fragments.ArmyEmpHistoryFragment
import com.bdjobs.app.editResume.employmentHistory.fragments.EmpHistoryEditFragment
import com.bdjobs.app.editResume.employmentHistory.fragments.EmpHistoryViewFragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_emplyment_history.*
import org.jetbrains.anko.toast

class EmploymentHistoryActivity : Activity(), ConnectivityReceiver.ConnectivityReceiverListener, EmpHisCB {
    private val editFragment = EmpHistoryEditFragment()
    private val viewFragment = EmpHistoryViewFragment()
    private val armyEditFragment = ArmyEmpHistoryFragment()
    private val armyViewFragment = ArmyEmpHisViewFragment()
    private var json: String? = ""
    private lateinit var listener: EmpHisCB

    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null

    override fun setDeleteButton(b: Boolean) {
        if (b) {
            iv_delete_data.show()
            iv_delete_data.setOnClickListener {
                editFragment.dataDelete()
            }
        } else {
            iv_delete_data.hide()
        }
    }

    fun saveUserInfo(data: DataItem) {
        Log.d("gotJson", "data: ${data.companyName}")
        json = Gson().toJson(data)
    }

    override fun editInfo() {
        transitFragment(editFragment, R.id.emp_his_container)
    }

    override fun setTitle(tit: String?) {
        setupToolbar(tit)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emplyment_history)
        setupToolbar("Employment History")
        transitFragment(viewFragment, R.id.emp_his_container)
        setupOnClicks()
    }

    private fun setupOnClicks() {
        iv_delete_data.setOnClickListener {
            toast("delete button")
        }
    }

    private fun setupToolbar(title: String?) {
        tv_tb_title?.text = title
        tb_emp_his?.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        tb_emp_his?.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        registerReceiver(internetBroadCastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(internetBroadCastReceiver)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            mSnackBar = Snackbar
                    .make(empHisBaseCL, getString(R.string.alert_no_internet), Snackbar.LENGTH_INDEFINITE)
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