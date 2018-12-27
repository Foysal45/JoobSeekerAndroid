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
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.callbacks.EmpHisCB
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_emp_history_edit.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class EmpHistoryEditFragment : Fragment() {

    private lateinit var empHisCB: EmpHisCB
    private lateinit var now: Calendar
    private lateinit var session: BdjobsUserSession
    private var hID: String = ""
    private var hExpID: String? = ""
    private var areaOfexps: String? = ""
    private var currentlyWorking: String = "OFF"
    var isEdit = false
    private lateinit var v: View

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
        v = inflater.inflate(R.layout.fragment_emp_history_edit, container, false)
        return v
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
        session = BdjobsUserSession(activity)
        empHisCB = activity as EmpHisCB
        now = Calendar.getInstance()
    }

    override fun onResume() {
        super.onResume()
        doWork()
        if (isEdit) {
            empHisCB.setDeleteButton(true)
            hID = "4"
            preloadedData()
        } else {
            empHisCB.setDeleteButton(false)
            hID = "-4"
            clearEditText()

        }
    }

    private fun doWork() {
        empHisCB.setTitle("Employment History")
        estartDateET?.setOnClickListener {
            pickDate(activity, now, startDateSetListener)
        }
        et_end_date?.setOnClickListener {
            pickDate(activity, now, endDateSetListener)
        }
        cb_present?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentlyWorking = "ON"
                et_end_date?.setText("")
                et_end_date?.isEnabled = false
            } else {
                currentlyWorking = "OFF"
                updateDateInView(1)
                et_end_date?.isEnabled = true
            }
        }
        experiencesMACTV.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val input = experiencesMACTV.text.toString()
                addChip(input)
                return@OnEditorActionListener true
            }
            false
        })
        fab_eh?.setOnClickListener {
            activity.showProgressBar(loadingProgressBar)
            val call = ApiServiceMyBdjobs.create().updateExpsList(session.userId, session.decodId, companyNameET.getString(),
                    companyBusinessACTV.getString(), companyLocationET.getString(), positionET.getString(),
                    departmentET.getString(), responsibilitiesET.getString(), estartDateET.getString(), et_end_date.getString(),
                    currentlyWorking, experiencesMACTV.getString(), hExpID, hID)
            call.enqueue(object : Callback<AddorUpdateModel> {
                override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                    activity.stopProgressBar(loadingProgressBar)
                    activity.toast(R.string.message_common_error)
                }

                override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                    try {
                        if (response.isSuccessful) {
                            activity.stopProgressBar(loadingProgressBar)
                            val resp = response.body()
                            activity.toast(resp?.message.toString())
                            empHisCB.goBack()
                        }
                    } catch (e: Exception) {
                        activity.stopProgressBar(loadingProgressBar)
                        e.printStackTrace()
                    }
                }
            })
        }
    }

    private fun preloadedData() {
        val data = empHisCB.getData()
        hExpID = data.expId
        areaOfexps = data.areaofExperience
        companyNameET.setText(data.companyName)
        companyBusinessACTV.setText(data.companyBusiness)
        companyLocationET.setText(data.companyLocation)
        positionET.setText(data.positionHeld)
        departmentET.setText(data.departmant)
        responsibilitiesET.setText(data.responsibility)
        estartDateET.setText(data.from)
        if (data.to != "Continuing") {
            et_end_date.setText(data.to)
        } else {
            cb_present.isChecked = true
            et_end_date.isEnabled = false
        }
        experiencesMACTV.setText(data.areaofExperience)
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
        val call = ApiServiceMyBdjobs.create().deleteData("Experience", hExpID!!, session.IsResumeUpdate!!, session.userId!!, session.decodId!!)
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                activity.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        val resp = response.body()
                        activity.toast(resp?.message.toString())
                        clearEditText()
                        empHisCB.goBack()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun clearEditText() {

        companyNameET.clear()
        companyBusinessACTV.clear()
        companyLocationET.clear()
        positionET.clear()
        departmentET.clear()
        responsibilitiesET.clear()
        estartDateET.clear()
        et_end_date.clear()
        cb_present.isChecked = false
        experiencesMACTV.clear()
    }

}
