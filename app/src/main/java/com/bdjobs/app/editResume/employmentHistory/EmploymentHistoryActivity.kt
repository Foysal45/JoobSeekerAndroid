package com.bdjobs.app.editResume.employmentHistory

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import com.bdjobs.app.BroadCastReceivers.ConnectivityReceiver
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AreaofExperienceItem
import com.bdjobs.app.editResume.adapters.models.ArmydataItem
import com.bdjobs.app.editResume.adapters.models.DataItem
import com.bdjobs.app.editResume.callbacks.EmpHisCB
import com.bdjobs.app.editResume.employmentHistory.fragments.ArmyEmpHisViewFragment
import com.bdjobs.app.editResume.employmentHistory.fragments.ArmyEmpHistoryEditFragment
import com.bdjobs.app.editResume.employmentHistory.fragments.EmpHistoryEditFragment
import com.bdjobs.app.editResume.employmentHistory.fragments.EmpHistoryViewFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_emplyment_history.*

class EmploymentHistoryActivity : Activity(), ConnectivityReceiver.ConnectivityReceiverListener, EmpHisCB {

    private val editFragment = EmpHistoryEditFragment()
    private val viewFragment = EmpHistoryViewFragment()
    private val armyEditFragment = ArmyEmpHistoryEditFragment()
    private val armyViewFragment = ArmyEmpHisViewFragment()
    private var datait: DataItem? = null
    private var dataExps: AreaofExperienceItem? = null
    private var dataitArmy: ArmydataItem? = null
    lateinit var name: String

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
        return datait!!
    }

    override fun getArmyData(): ArmydataItem {
        return dataitArmy!!
    }

    override fun passAreaOfExpsData(data: AreaofExperienceItem) {
        this.dataExps = data
    }

    override fun getAreaOfExpsData(): AreaofExperienceItem {
        return dataExps!!
    }


    override fun passData(data: DataItem) {
        this.datait = data
    }

    override fun passArmyData(data: ArmydataItem) {
        this.dataitArmy = data
    }

    override fun goToEditInfo(check: String) {
        try {
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
                "army_add" -> {
                    armyEditFragment.isEdit = false
                    transitFragment(armyEditFragment, R.id.emp_his_container, true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentValues()
        setContentView(R.layout.activity_emplyment_history)
        setupToolbar(getString(R.string.title_emp_his))
        gotToFragment(name)
    }


    private fun gotToFragment(name: String) {
        when (name) {
            "employ" -> transitFragment(viewFragment, R.id.emp_his_container, false)
            "army" -> transitFragment(armyViewFragment, R.id.emp_his_container, false)
        }
    }

    private fun getIntentValues() {
        name = intent.getStringExtra("name")
    }

    override fun goBack() {
        onBackPressed()
        empHisBaseCL.closeKeyboard(this@EmploymentHistoryActivity)
    }

    override fun setTitle(tit: String?) {
        setupToolbar(tit)
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

    override fun validateField(et: TextInputEditText, til: TextInputLayout): Boolean {
        if (et.getString().isEmpty()) {
            til.isErrorEnabled = true
            til.error = resources.getString(R.string.field_empty_error_message_common)
            requestFocus(et)
            return false
        } else {
            til.isErrorEnabled = false
        }
        return true
    }

    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }
}