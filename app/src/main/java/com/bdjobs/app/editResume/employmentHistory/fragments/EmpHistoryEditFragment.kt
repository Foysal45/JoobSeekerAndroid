package com.bdjobs.app.editResume.employmentHistory.fragments


import android.app.DatePickerDialog
import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_emp_history_edit.*
import org.jetbrains.anko.act
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EmpHistoryEditFragment : Fragment() {

    private lateinit var empHisCB: EmpHisCB
    private lateinit var now: Calendar
    private lateinit var session: BdjobsUserSession
    private var hID: String = ""
    private var hExpID: String? = ""
    private var currentlyWorking: String = "OFF"
    //private var companyBusinessID = ""
    private var workExperienceID = ""
    private var idArr: ArrayList<String> = ArrayList()
    var isEdit = false
    private lateinit var v: View
    private lateinit var dataStorage: DataStorage
    private var date: Date? = null

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
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_emp_history_edit, container, false)
        Log.i("checking", "onCreateView")
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i("checking", "onActivityCreated")
        session = BdjobsUserSession(activity)
        empHisCB = activity as EmpHisCB
        now = Calendar.getInstance()
        dataStorage = DataStorage(activity)
        empHisCB.setTitle(getString(R.string.title_emp_history))
        initViews()
        if (!isEdit) {
            empHisCB.setDeleteButton(false)
            hID = "-4"
            //idArr.add("")
            clearEditText()
        }
        d("onActivityCreated : ${savedInstanceState?.isEmpty}")
    }

    override fun onResume() {
        super.onResume()
        Log.i("checking", "onResume")
        Log.i("onResume", "onPause called... $idArr")
        /*if (idArr.isNotEmpty()) {
            try {
                //entry_chip_group?.removeAllViews()
                idArr.removeAll(idArr)
            } catch (e: Exception) {
                e.printStackTrace()
                logException(e)
            }
        }*/
        //Log.d("dsgjdhsg", "companyBusinessID $companyBusinessID")
        //exps = ""
        positionTIL.clearFocus()
        companyNameET.requestFocus()
        //ehMailLL.clearFocus()
        if (isEdit) {
            empHisCB.setDeleteButton(true)
            hID = "4"
            if (empHisCB.getIsFirst())
                preloadedData()
        } else if (!isEdit) {
            empHisCB.setDeleteButton(false)
            hID = "-4"
            //isEmpty = true
            //clearEditText()
        }
        doWork()

    }

    private fun addChip(input: String, id: String) {
        Log.i("getInput", "\n// |$input| and |$id|")

        if (input.trim().isBlank() || input.trim().isEmpty()) {
            val data = empHisCB.getExpsArray()
            data.forEach { dt ->
                dt.areaofExperience?.forEach {
                    if (it?.id?.let { it1 -> dataStorage.workDisciplineByWorkDisciplineID(it1) } == "") {
                        addAsString(id)
                        val c1 = getChip(entry_chip_group, it.expsName.toString(), R.xml.chip_entry)
                        entry_chip_group.addView(c1)
                    }
                }
                Log.i("getAreaOfExpsData", "\n ${dt.areaofExperience},// |$input| and |$id|")
            }
        } else {
            when {
                entry_chip_group.childCount < 3 -> {
                    addAsString(id)
                    val c1 = getChip(entry_chip_group, input, R.xml.chip_entry)
                    entry_chip_group.addView(c1)
                }
                else -> {
                    //idArr.remove(workExperienceID)
                    //removeItem(workExperienceID)
                    experiencesMACTV.setText("")
                    experiencesMACTV.clearFocus()
                }
            }
        }


        experiencesMACTV.clearText()
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
            //experiencesMACTV.isEnabled = true
            //experiencesMACTV.requestFocus()
        }
        return chip
    }

    private fun removeItem(s: String) {
        val id = dataStorage.workDisciplineIDByWorkDiscipline(s)
        if (idArr.contains(id))
            idArr.remove("$id")
        d("selected rmv: exps and $idArr")
    }

    private fun initViews() {
        companyNameET?.addTextChangedListener(TW.CrossIconBehave(companyNameET))
        companyBusinessACTV?.addTextChangedListener(TW.CrossIconBehaveACTV(companyBusinessACTV))
        companyLocationET?.addTextChangedListener(TW.CrossIconBehave(companyLocationET))
        positionET?.addTextChangedListener(TW.CrossIconBehave(positionET))
        departmentET?.addTextChangedListener(TW.CrossIconBehave(departmentET))
        experiencesMACTV?.addTextChangedListener(TW.CrossIconBehaveACTV(experiencesMACTV))
        responsibilitiesET?.addTextChangedListener(TW.CrossIconBehave(responsibilitiesET))
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        val comName = outState?.putString("comName", "someval")
        d("putComname : $comName")
        d("onSaveInstanceState : $comName")
        super.onSaveInstanceState(outState)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("onDestroyView", "onDestroyView called")
    }

    private fun doWork() {
        experiencesMACTV.onFocusChange { _, hasFocus ->
            if (hasFocus) {
                val workExperineceList: Array<String> = dataStorage.allWorkDiscipline
                val expsAdapter = ArrayAdapter<String>(activity!!,
                        android.R.layout.simple_dropdown_item_1line, workExperineceList)
                experiencesMACTV.setAdapter(expsAdapter)
                experiencesMACTV.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT
                experiencesMACTV.setOnItemClickListener { _, _, position, id ->
                    d("Array size : pos : $position id : $id")
                    //activity.toast("Selected : ${workExperineceList[position + 1]} and gotStr : ${experiencesMACTV.text}")
                    d("Selected : ${workExperineceList[position + 1]} and gotStr : ${experiencesMACTV.text}")
                    workExperienceID = dataStorage.workDisciplineIDByWorkDiscipline(experiencesMACTV.text.toString())!!

                    if (idArr.size in 0..2) {
                        //experiencesMACTV.isEnabled = true
                        if (idArr.contains(workExperienceID)) {
                            experiencesMACTV.closeKeyboard(activity)
                            activity.toast("Experience already added")
                            experiencesMACTV.setText("")
                            experiencesMACTV.clearFocus()
                        } else {
                            addChip(dataStorage.workDisciplineByWorkDisciplineID(workExperienceID)!!, workExperienceID)
                        }
                        experiencesTIL.hideError()
                    } else if (idArr.size >= 3) {
                        //addChip(dataStorage.workDisciplineByWorkDisciplineID(workExperienceID)!!, workExperienceID)
                        d("Array_size : ${idArr.size} and exps and id : $id")
                        //experiencesMACTV.isEnabled = false
                        experiencesMACTV.setText("")
                        experiencesMACTV.clearFocus()
                        activity?.toast("Maximum 3 experiences can be added.")
                        experiencesTIL.hideError()
                    }
                }
            }
        }
        cb_present?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentlyWorking = "ON"
                et_end_date?.setText("")
                endDateTIL.hideError()
            } else {
                currentlyWorking = "OFF"
                updateDateInView(1)
                endDateTIL?.isEnabled = true
                et_end_date.clear()
                //et_end_date?.error = "Can't be empty"
                //endDateTIL.hideError()
            }
        }
        addTextChangedListener(companyNameET, companyNameTIL)
        //addTextChangedListener(companyBusinessACTV, companyBusinessTIL)
        companyBusinessACTV.easyOnTextChangedListener { charSequence ->
            validateAutoCompleteField(charSequence.toString(), companyBusinessACTV, companyBusinessTIL)
        }
        addTextChangedListener(positionET, positionTIL)
        addTextChangedListener(estartDateET, estartDateTIL)
        if (idArr.isEmpty()) experiencesMACTV.easyOnTextChangedListener { charSequence ->
            activity?.ACTVValidation(charSequence.toString(), experiencesMACTV, experiencesTIL)
        }

        if (cb_present?.isChecked as Boolean) {
            addTextChangedListener(et_end_date, endDateTIL)
        }
        //addTextChangedListener(etTrTrainingYear, trTrainingYearTIL)

        estartDateET?.setOnClickListener {

            val cal = Calendar.getInstance()
            val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)

            if (isEdit) {
                try {
                    date = formatter.parse(estartDateET.text.toString())
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                date?.let {
                    cal.time = date
                    pickDate(activity, cal, startDateSetListener)
                }
            } else {
                if (estartDateET.text.toString().isNullOrEmpty())
                    pickDate(activity, cal, startDateSetListener)
                else {
                    date = formatter.parse(estartDateET.text.toString())
                    cal.time = date
                    pickDate(activity, cal, startDateSetListener)
                }
            }
        }
        et_end_date?.setOnClickListener {

            if (!cb_present.isChecked) {
                val cal = Calendar.getInstance()
                val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)

                if (isEdit) {
                    try {
                        date = if (empHisCB?.getData().to == "Continuing") {
                            if (!et_end_date.text.toString().isNullOrEmpty()) {
                                formatter.parse(et_end_date.text.toString())
                            } else {
                                Date()
                            }
                        } else {
                            formatter.parse(et_end_date.text.toString())
                        }
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    date?.let {
                        cal.time = date
                        pickDate(activity, cal, endDateSetListener)
                    }
                } else {
                    if (!et_end_date.text.toString().isNullOrEmpty()) {
                        date = formatter.parse(et_end_date.text.toString())
                        cal.time = date
                        pickDate(activity, cal, endDateSetListener)
                    } else {
                        pickDate(activity, cal, endDateSetListener)
                    }

                }
            }
        }
        companyBusinessACTV.onFocusChange { _, hasFocus ->
            val organizationList: ArrayList<String> = dataStorage.allOrgTypes

            if (hasFocus) {
                try {
                    val orgsAdapter = ArrayAdapter<String>(activity,
                            android.R.layout.simple_dropdown_item_1line, organizationList)
                    companyBusinessACTV.setAdapter(orgsAdapter)
                    companyBusinessACTV.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
        fab_eh?.setOnClickListener {
            clEmploymentHistory.closeKeyboard(activity)
            var validation = 0
            if (idArr.isEmpty()) {
                experiencesTIL.isErrorEnabled = true
                //experiencesTIL.error = ""
                //isEmpty = false
            } else {
                experiencesTIL.hideError()
                //isEmpty = true
            }
            //exps = TextUtils.join(",", idArr)
            //exps = exps.replace(",,".toRegex(), ",")
            validation = isValidate(companyNameET, companyNameTIL, positionET, true, validation)
            validation = isValidateAutoCompleteTV(companyBusinessACTV, companyBusinessTIL, positionET, true, validation)
            validation = isValidate(positionET, positionTIL, estartDateET, true, validation)
            validation = isValidate(estartDateET, estartDateTIL, et_end_date, true, validation)
            validation = isValidateAutoCompleteTV(experiencesMACTV, experiencesTIL, companyNameET, idArr.isEmpty(), validation)
            if (!cb_present.isChecked) validation = isValidate(et_end_date, endDateTIL, et_end_date, true, validation)
            //validation = isValidate(estartDateET, estartDateTIL, estartDateET, true, validation) // area of experiences
            Log.d("validation", "validation : $validation and isEmpty")

            if (companyBusinessACTV.getString().trim().isEmpty())
                companyBusinessTIL.isErrorEnabled = true
            else
                companyBusinessTIL.hideError()

            if (cb_present.isChecked && validation >= 4) {
                disableError()
                debug("chiIDs: exps, and cbValue $currentlyWorking and ${cb_present.isChecked}")
                if (idArr.size == 0) {
                    activity?.toast("Please select at least one experience")
                    experiencesTIL.isErrorEnabled = true
                    experiencesTIL?.showError("This field can not be empty")
                    endDateTIL.isErrorEnabled = true
                    endDateTIL.error = "This field can not be empty"
                } else {
                    endDateTIL.hideError()
                    experiencesTIL.hideError()
                    updateData()
                }
            } else if (!cb_present.isChecked && validation >= 5) {
                debug("chiIDs: eliffff, and cbValue $currentlyWorking and ${cb_present.isChecked}")
                /*
                endDateTIL.isErrorEnabled = true
                endDateTIL.error = "This field can not be empty"*/
                disableError()
                endDateTIL.hideError()
                updateData()
            }
        }
    }

    private fun updateData() {
        if (!experiencesMACTV.text.toString().isNullOrEmpty()){
            toast("Area of Experience is not available ")
            experiencesMACTV?.requestFocus()
        }

        else {
            activity?.showProgressBar(loadingProgressBar)
            val exps = TextUtils.join(",", idArr)

            Log.d("*+*+allValuesExp", exps.toString())
            //companyBusinessID = dataStorage.getOrgIDByOrgName(companyBusinessACTV.getString())
            val call = ApiServiceMyBdjobs.create().updateExpsList(session.userId, session.decodId, companyNameET.getString(),
                    companyBusinessACTV.getString(), companyLocationET.getString(), positionET.getString(),
                    departmentET.getString(), responsibilitiesET.getString(), estartDateET.getString(), et_end_date.getString(),
                    currentlyWorking, ",$exps,", hExpID, hID)
            call.enqueue(object : Callback<AddorUpdateModel> {
                override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                    activity?.stopProgressBar(loadingProgressBar)
                    activity?.toast(R.string.message_common_error)
                }

                override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                    try {
                        if (response.isSuccessful) {
                            activity?.stopProgressBar(loadingProgressBar)
                            val resp = response.body()
                            activity?.toast(resp?.message.toString())
                            if (resp?.statuscode == "4") {
                                empHisCB.setBackFrom(Constants.empHistoryList)
                                empHisCB.goBack()
                            } else if (resp?.message == "Please select End Date") {
                                endDateTIL.isErrorEnabled = true
                                endDateTIL?.showError("This field can not be empty")
                            }
                        } else {
                            activity?.stopProgressBar(loadingProgressBar)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        }
    }

    private fun addAsString(expID: String) {
        if (!idArr.contains(expID)) {
            idArr.add(expID.trim())
        }
        d("selected exps:exps and ids $idArr")
    }

    private fun preloadedData() {
        idArr.clear()
        val data = empHisCB.getData()
        empHisCB.setIsFirst(false)

        val areaOfexps = data.areaofExperience
        areaOfexps?.forEach {
            addChip(dataStorage.workDisciplineByWorkDisciplineID(it?.id!!).toString(), it.id)
        }

        hExpID = data.expId
        companyNameET.setText(data.companyName)
        experiencesMACTV.setText("")
        companyBusinessACTV.setText(data.companyBusiness)
        companyLocationET.setText(data.companyLocation)
        positionET.setText(data.positionHeld)
        departmentET.setText(data.departmant)
        responsibilitiesET.setText(data.responsibility)
        estartDateET.setText(data.from)

        //experiencesMACTV.setText(data.areaofExperience)
        if (data.to != "Continuing") {
            currentlyWorking = "OFF"
            cb_present.isChecked = false
            et_end_date.setText(data.to)
            //toast("not continuing")
        } else {
            et_end_date.clear()
            currentlyWorking = "ON"
            cb_present.isChecked = true
            et_end_date.isEnabled = false
            endDateTIL.hideError()
        }
        disableError()

    }

    private fun updateDateInView(c: Int) {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        if (c == 0) {

            estartDateET.setText(sdf.format(now.time))
        } else {
            et_end_date.setText(sdf.format(now.time))
        }
    }

    private fun addTextChangedListener(editText: TextInputEditText, inputLayout: TextInputLayout) {
        editText.easyOnTextChangedListener { charSequence ->
            empHisCB.validateField(charSequence.toString(), editText, inputLayout)
        }
    }

    fun dataDelete() {
        activity?.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().deleteData("Experience", hExpID!!, session.IsResumeUpdate!!, session.userId!!, session.decodId!!)
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                activity?.stopProgressBar(loadingProgressBar)
                activity?.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity?.stopProgressBar(loadingProgressBar)
                        val resp = response.body()
//                        empHisCB.getExpsArray()?.let {
//                            for (item in it) {
//                                try {
//                                    if (item.expId!!.equalIgnoreCase(hExpID!!)) {
//                                        empHisCB.getExpsArray()?.remove(item)
//                                    }
//                                } catch (e: Exception) {
//                                }
//                            }
//                        }
                        activity?.toast(resp?.message.toString())
                        empHisCB.setBackFrom(Constants.empHistoryList)
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
        //if (companyBusinessACTV.text.trim().isNotEmpty()) {
        companyBusinessACTV.setText("")
        et_end_date.setText("")
        //}
        companyLocationET.clear()
        positionET.clear()
        departmentET.clear()
        responsibilitiesET.clear()
        estartDateET.clear()
        cb_present.isChecked = false
        experiencesMACTV.setText("")
        //experiencesMACTV.clear()
        positionTIL.clearFocus()
        companyNameET.requestFocus()
        disableError()
    }

    private fun disableError() {
        companyNameTIL.hideError()
        companyBusinessTIL.hideError()
        positionTIL.hideError()
        estartDateTIL.hideError()
        endDateTIL.hideError()
        experiencesTIL.clearFocus()
        experiencesTIL.hideError()
    }

    fun validateAutoCompleteField(char: String, et: AutoCompleteTextView, til: TextInputLayout): Boolean {
        when {
            TextUtils.isEmpty(char) -> {
                til.showError(getString(R.string.field_empty_error_message_common))
                //activity?.requestFocus(et)
                return false
            }
            char.length < 2 -> {
                til.showError(" it is too short")
                //activity?.requestFocus(et)
                return false
            }
            else -> til.hideError()
        }
        return true
    }
}
