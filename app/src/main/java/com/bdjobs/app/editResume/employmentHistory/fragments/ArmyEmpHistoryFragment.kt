package com.bdjobs.app.editResume.employmentHistory.fragments


import android.app.DatePickerDialog
import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.pickDate
import com.bdjobs.app.editResume.callbacks.EmpHisCB
import kotlinx.android.synthetic.main.fragment_army_emp_history.*
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

class ArmyEmpHistoryFragment : Fragment() {

    private lateinit var call: EmpHisCB
    private lateinit var now: Calendar

    private val commissionDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        now.set(Calendar.YEAR, year)
        now.set(Calendar.MONTH, monthOfYear)
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        updateDateInView(0)
    }
    private val retireDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        now.set(Calendar.YEAR, year)
        now.set(Calendar.MONTH, monthOfYear)
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        updateDateInView(1)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_army_emp_history, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        call = activity as EmpHisCB
        now = Calendar.getInstance()
        doWork()
    }

    private fun doWork() {
        call.setTitle("Employment History (Retired Army Person)")
        et_commission.setOnClickListener { pickDate(activity, now, commissionDateSetListener) }
        et_retire.setOnClickListener { pickDate(activity, now, retireDateSetListener) }
        fab_eh_army.setOnClickListener { activity.toast("save") }
    }

    private fun updateDateInView(c: Int) {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        if (c == 0) {
            et_commission.setText(sdf.format(now.time))
        } else {
            et_retire.setText(sdf.format(now.time))
        }
    }
}
