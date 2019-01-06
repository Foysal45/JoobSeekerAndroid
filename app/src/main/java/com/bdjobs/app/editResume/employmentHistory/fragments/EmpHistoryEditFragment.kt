package com.bdjobs.app.editResume.employmentHistory.fragments


import android.app.DatePickerDialog
import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.callbacks.EmpHisCB
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_emp_history_edit.*
import org.jetbrains.anko.selector
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
    private var currentlyWorking: String = "OFF"
    private var companyBusinessID = ""
    private var workExperineceID = ""
    private var newWorkExperineceID = ""
    private var exps: String = ""
    private var expIDs: MutableList<String> = MutableList(3) { "" }
    var isEdit = false
    private lateinit var v: View
    private lateinit var dataStorage: DataStorage

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
        if (entry_chip_group.childCount <= 2) {
            addAsString(workExperineceID)
            val c1 = getChip(entry_chip_group, input, R.xml.chip_entry)
            entry_chip_group.addView(c1)
            experiencesMACTV?.clearText()
        } else activity.toast("Maximum 3 experiences can be added.")
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
        chip.setOnCloseIconClickListener {
            entryChipGroup.removeView(chip)
            removeItem(chip.text.toString())
        }
        return chip
    }

    private fun removeItem(s: String) {
        val id = dataStorage.workDisciplineIDByWorkDiscipline(s)
        //debug("chipsIDs: $id and $s")
        for (i in 0 until expIDs.size) {
            if (id == expIDs[i]) {
                //exps.replace(",$id", "", true)
                expIDs.remove(id)
                exps = exps.replace(id, "")
                break
            }
            debug("newChipsIDsss: $expIDs")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        empHisCB = activity as EmpHisCB
        now = Calendar.getInstance()
        dataStorage = DataStorage(activity)
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
        empHisCB.setTitle(getString(R.string.title_emp_history))
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
        companyBusinessACTV.setOnClickListener {
            val organizationList: ArrayList<String> = dataStorage.allOrgTypes
            activity.selector("Select your area of company business", organizationList.toList()) { _, i ->

                companyBusinessACTV.setText(organizationList[i])
                companyBusinessTIL.requestFocus()

                companyBusinessID = dataStorage.getOrgIDByOrgName(organizationList[i])

                Log.d("dsgjdhsg", "companyBusinessID $companyBusinessID")
            }
        }

        experiencesMACTV.setOnClickListener {

            val workExperineceList: Array<String> = dataStorage.allWorkDiscipline

            activity.selector("Select your area of work experience", workExperineceList.toList()) { dialogInterface, i ->
                experiencesMACTV.setText(workExperineceList[i])
                experiencesTIL.requestFocus()
                workExperineceID = dataStorage.workDisciplineIDByWorkDiscipline(workExperineceList[i])!!
                /*newWorkExperineceID = ",$workExperineceID,"
                exps += ",$workExperineceID,"*/
                addChip(dataStorage.workDisciplineByWorkDisciplineID(workExperineceID)!!)
                expIDs.add(workExperineceID)
                Log.d("dsgjdhsg", "workExperineceID $newWorkExperineceID")
            }
        }

        fab_eh?.setOnClickListener {
            updateData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exps = ""
    }

    private fun updateData() {
        activity.showProgressBar(loadingProgressBar)
        debug("chiIDs: $exps")
        for (i in 0 until expIDs.size) {
            exps = exps.replace(",,".toRegex(), ",")
        }
        exps = exps.replace(",$".toRegex(), "")
        exps = if (exps.startsWith(",")) exps.substring(1) else exps

        Log.d("allValuesN", exps)
        val call = ApiServiceMyBdjobs.create().updateExpsList(session.userId, session.decodId, companyNameET.getString(),
                companyBusinessID, companyLocationET.getString(), positionET.getString(),
                departmentET.getString(), responsibilitiesET.getString(), estartDateET.getString(), et_end_date.getString(),
                currentlyWorking, ",$exps,", hExpID, hID)
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
                        if (resp?.statuscode == "4") {
                            empHisCB.goBack()
                        }
                    }
                } catch (e: Exception) {
                    activity.stopProgressBar(loadingProgressBar)
                    e.printStackTrace()
                }
            }
        })
    }

    private fun addAsString(expID: String) {
        exps += ",$expID"
        /*expIDs = exps.split(",") as ArrayList<String>
        for (i in 0 until expIDs.size) {
            debug("chipsIDs: ${expIDs[i]}")
        }*/
        /*val stringArray = exps.split(",")
        debug("chipsIDs: $stringArray")*/
    }

    private fun preloadedData() {
        val data = empHisCB.getData()
        val areaOfexps = data.areaofExperience
        //for ((i, value) in areaOfexps?.withIndex()!!)
        areaOfexps?.forEach {
            addChip(dataStorage.workDisciplineByWorkDisciplineID(it?.id!!).toString())
            addAsString(it.id)
        }
        expIDs = exps.split(",").toMutableList()

        hExpID = data.expId
        companyNameET.setText(data.companyName)

        companyBusinessACTV.setText(dataStorage.getOrgNameByID(data.companyBusiness!!))
        companyLocationET.setText(data.companyLocation)
        positionET.setText(data.positionHeld)
        departmentET.setText(data.departmant)
        responsibilitiesET.setText(data.responsibility)
        estartDateET.setText(data.from)

        //experiencesMACTV.setText(data.areaofExperience)
        if (data.to != "Continuing") {
            et_end_date.setText(data.to)
        } else {
            cb_present.isChecked = true
            et_end_date.isEnabled = false
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
