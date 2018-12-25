package com.bdjobs.app.editResume.employmentHistory

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.Utilities.transitFragment
import com.bdjobs.app.editResume.adapters.models.ArmydataItem
import com.bdjobs.app.editResume.adapters.models.DataItem
import com.bdjobs.app.editResume.callbacks.EmpHisCB
import com.bdjobs.app.editResume.employmentHistory.fragments.ArmyEmpHisViewFragment
import com.bdjobs.app.editResume.employmentHistory.fragments.ArmyEmpHistoryEditFragment
import com.bdjobs.app.editResume.employmentHistory.fragments.EmpHistoryEditFragment
import com.bdjobs.app.editResume.employmentHistory.fragments.EmpHistoryViewFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_emplyment_history.*

class EmploymentHistoryActivity : Activity(), ConnectivityReceiver.ConnectivityReceiverListener, EmpHisCB {

    private val editFragment = EmpHistoryEditFragment()
    private val viewFragment = EmpHistoryViewFragment()
    private val armyEditFragment = ArmyEmpHistoryEditFragment()
    private val armyViewFragment = ArmyEmpHisViewFragment()
    private lateinit var datait: DataItem
    private lateinit var dataitArmy: ArmydataItem

    private val internetBroadCastReceiver = ConnectivityReceiver()
    private var mSnackBar: Snackbar? = null

    override fun setDeleteButton(b: Boolean) {
        if (b) {
            iv_delete_data.show()
            iv_delete_data.setOnClickListener {
                if (editFragment.isEdit) {
                    editFragment.dataDelete()
                } else if (armyEditFragment.isEdit) {
                    armyEditFragment.dataDelete()
                }
            }
        } else {
            iv_delete_data.hide()
        }
    }

    override fun getData(): DataItem {
        return datait
    }

    override fun getArmyData(): ArmydataItem {
        return dataitArmy
    }

    override fun passData(data: DataItem) {
        this.datait = data
    }

    override fun passArmyData(data: ArmydataItem) {
        this.dataitArmy = data
    }

    override fun goToEditInfo(check: String) {
        when (check) {
            "add" -> {
                editFragment.isEdit = false
                transitFragment(editFragment, R.id.emp_his_container, true)
            }
            "edit" -> {
                editFragment.isEdit = true
                transitFragment(editFragment, R.id.emp_his_container, true)
            }
            "army_edit" -> {
                armyEditFragment.isEdit = true
                transitFragment(armyEditFragment, R.id.emp_his_container, true)
            }
            else -> {

            }
        }
    }

    override fun goBack() {
        onBackPressed()
    }

    override fun setTitle(tit: String?) {
        setupToolbar(tit)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emplyment_history)
        setupToolbar("Employment History")
        transitFragment(armyViewFragment, R.id.emp_his_container, false)
    }


    private fun setupToolbar(title: String?) {
        tv_tb_title?.text = title
        tb_emp_his?.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        tb_emp_his?.setNavigationOnClickListener {
            goBack()
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