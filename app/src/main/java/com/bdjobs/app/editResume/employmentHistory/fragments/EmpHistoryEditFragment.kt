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
import kotlinx.android.synthetic.main.fragment_emp_history_edit.*
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

class EmpHistoryEditFragment : Fragment() {

    private lateinit var call: EmpHisCB
    private lateinit var now: Calendar

    private val startDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        now.set(Calendar.YEAR, year)
        now.set(Calendar.MONTH, monthOfYear)
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        updateDateInView(0)
    }
    private val endDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        now.set(Calendar.YEAR, year)
        now.set(Calendar.MONTH, monthOfYear)
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        updateDateInView(1)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emp_history_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        call = activity as EmpHisCB
        now = Calendar.getInstance()
        doWork()
    }

    private fun doWork() {
        call.setTitle("Employment History")
        estartDateET?.setOnClickListener {
            pickDate(activity, now, startDateSetListener)
        }
        et_end_date?.setOnClickListener {
            pickDate(activity, now, endDateSetListener)
        }

        cb_present?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                et_end_date?.setText("")
                et_end_date?.isEnabled = false
            } else {
                updateDateInView(1)
                et_end_date?.isEnabled = true
            }
        }

        fab_eh?.setOnClickListener {
            activity.toast("Save button")
        }
    }

    private fun updateDateInView(c: Int) {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        if (c == 0) {
            estartDateET.setText(sdf.format(now.time))
        } else {
            et_end_date.setText(sdf.format(now.time))
        }
    }

}
