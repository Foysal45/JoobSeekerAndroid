package com.bdjobs.app.editResume.personalInfo.fragments.personalDetails


import android.app.DatePickerDialog
import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_personal_details_edit.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class PersonalDetailsEditFragment : Fragment() {

    private lateinit var personalInfo: PersonalInfo
    private lateinit var session: BdjobsUserSession
    lateinit var dataStorage: DataStorage
    private lateinit var now: Calendar
    private var gender = ""
    private var marital = ""
    private var dob = ""
    private var date: Date? = null
    private var nationality = "Bangladeshi"

    private val birthDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        now.set(Calendar.YEAR, year)
        now.set(Calendar.MONTH, monthOfYear)
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateDateInView()
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etPerDob.setText(sdf.format(now.time))
        dob = etPerDob.getString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_details_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dataStorage = DataStorage(activity)
        session = BdjobsUserSession(activity)
        personalInfo = activity as PersonalInfo
        now = Calendar.getInstance()
        personalInfo.setTitle(getString(R.string.title_personal))
        doWork()
    }

    override fun onResume() {
        super.onResume()
        personalInfo.setEditButton(false, "dd")
    }

    private fun doWork() {
        preloadedData()
        cbPerIsBd.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                nidTIL.hide()
                nationalityTIL.show()
            } else {
                etPerNationality.setText(nationality)
                nidTIL.show()
                nationalityTIL.hide()
            }
        }
        etPerDob.setOnClickListener {
            pickDateOfBirth(birthDateSetListener)
        }
        fab_per_update.setOnClickListener {
            updateData()
        }
    }

    private fun pickDateOfBirth(listener: DatePickerDialog.OnDateSetListener) {
        val cal = Calendar.getInstance()
        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        try {
            date = formatter.parse(dob)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        d("day: $date")
        val dpd: DatePickerDialog
        val mYear = now.get(Calendar.YEAR) // current year
        val mMonth = now.get(Calendar.MONTH) // current month
        val mDay = now.get(Calendar.DAY_OF_MONTH) // current day
        if (date != null) {
            cal.time = date
            dpd = DatePickerDialog(activity,
                    listener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH))
        } else {
            dpd = DatePickerDialog(activity,
                    listener,
                    // set DatePickerDialog to point to today's date when it loads up
                    mYear,
                    mMonth,
                    mDay)
        }

        val calendarMin = Calendar.getInstance()
        calendarMin.set(Calendar.DAY_OF_MONTH, mDay)
        calendarMin.set(Calendar.MONTH, mMonth)
        calendarMin.set(Calendar.YEAR, mYear - 85)

        val calendarMax = Calendar.getInstance()
        calendarMax.set(Calendar.DAY_OF_MONTH, mDay)
        calendarMax.set(Calendar.MONTH, mMonth)
        calendarMax.set(Calendar.YEAR, mYear - 12)


        dpd.datePicker.maxDate = calendarMax.timeInMillis
        dpd.datePicker.minDate = calendarMin.timeInMillis
        dpd.show()
    }

    private fun preloadedData() {
        getDataFromChipGroup(cgGender)
        getDataFromChipGroup(cgMarital)
        val data = personalInfo.getPersonalData()
        etPerFirstName.setText(data.firstName)
        etPerLastName.setText(data.lastName)
        etPerFName.setText(data.fatherName)
        etPerMName.setText(data.motherName)
        etPerReligion.setText(data.religion)
        dob = data.dateofBirth.toString()
        etPerDob.setText(data.dateofBirth)
        etPerNid.setText(data.nationalIdNo)
        etPerNationality.setText(data.nationality)
        selectChip(cgGender, data.gender!!)
        selectChip(cgMarital, data.maritalStatus!!)

        if (data.nationality == "Bangladeshi") {
            cbPerIsBd.isChecked = true
            nidTIL.show()
            nationalityTIL.hide()
        } else {
            cbPerIsBd.isChecked = false
            nidTIL.hide()
            nationalityTIL.show()
        }

    }

    private fun updateData() {
        Log.d("nation", "val : $nationality and ${etPerNationality.getString()}")
        activity.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().updatePersonalData(session.userId, session.decodId, session.IsResumeUpdate,
                etPerFirstName.getString(), etPerLastName.getString(), etPerFName.getString(), etPerMName.getString(),
                etPerDob.getString(), etPerNationality.getString(), marital, gender, etPerNid.getString(), etPerReligion.getString())
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
                            session.updateIsCvPosted("true")
                            session.updateFullName(etPerFirstName.getString() + " " + etPerLastName.getString())
                            personalInfo.goBack()
                        }
                    }
                } catch (e: Exception) {
                    activity.stopProgressBar(loadingProgressBar)
                    e.printStackTrace()
                }
            }
        })
    }

    private fun getDataFromChipGroup(cg: ChipGroup) {
        cg.setOnCheckedChangeListener { chipGroup, i ->
            if (i > 0) {
                val chip = chipGroup.findViewById(i) as Chip
                Log.d("chip_entry", "text: ${chip.text}")
                val data = chip.text.toString()
                when (chipGroup.id) {
                    R.id.cgGender -> {
                        val chips = when (data) {
                            "Male" -> "M"
                            "Female" -> "F"
                            else -> "O"
                        }
                        debug("value : $chips")
                        gender = chips
                    }
                    R.id.cgMarital -> {
                        val chips = dataStorage.getMaritalIDByMaritalStatus(data)
                        marital = chips
                        debug("value : $chips")
                    }
                }
            } else {
                when (chipGroup.id) {
                    R.id.cgGender -> {
                    }
                    R.id.cgMarital -> {
                    }
                }
            }
        }
    }

    private fun selectChip(chipGroup: ChipGroup, data: String) {
        val count = chipGroup.childCount
        for (i in 0 until count) {
            val chip = chipGroup.getChildAt(i) as Chip
            val chipText = chip.text.toString()
            if (data.equalIgnoreCase(chipText)) {
                Log.d("chip_entry", "text:$i")
                chip.isChecked = true
            }
        }
    }

}
