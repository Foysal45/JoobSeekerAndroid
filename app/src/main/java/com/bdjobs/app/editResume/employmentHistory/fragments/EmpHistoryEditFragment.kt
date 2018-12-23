package com.bdjobs.app.editResume.employmentHistory.fragments


import android.app.DatePickerDialog
import android.app.Fragment
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.clearText
import com.bdjobs.app.Utilities.closeKeyboard
import com.bdjobs.app.Utilities.pickDate
import com.bdjobs.app.editResume.callbacks.EmpHisCB
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_emp_history_edit.*
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*


class EmpHistoryEditFragment : Fragment() {

    private val exp = arrayOf("John Smith", "Kate Eckhart", "Emily Sun", "Frodo Baggins")
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

    private fun addChip(input: String) {
        val c1 = getChip(entry_chip_group, input, R.xml.chip_entry)
        entry_chip_group.addView(c1)
        experiencesMACTV?.clearText()
        experiencesMACTV?.closeKeyboard(activity)
    }

    private fun getChip(entryChipGroup: ChipGroup, text: String, item: Int): Chip {
        val chip = Chip(activity)
        chip.setChipDrawable(ChipDrawable.createFromResource(activity, item))
        val paddingDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10f,
                resources.displayMetrics
        ).toInt()
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.text = text
        chip.setOnCloseIconClickListener { entryChipGroup.removeView(chip) }
        return chip
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        call = activity as EmpHisCB
        call.setDeleteButton(true)
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

        experiencesMACTV.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                /*if (!experiencesMACTV?.text?.isEmpty()!!)
                    experiencesMACTV?.clearText()*/
                val input = experiencesMACTV.text.toString()
                addChip(input)
                return@OnEditorActionListener true
            }
            false
        })

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

    fun dataDelete() {
        activity.toast("data deleted")
    }

}
