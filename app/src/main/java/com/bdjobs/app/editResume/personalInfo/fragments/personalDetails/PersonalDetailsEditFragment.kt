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
import java.text.SimpleDateFormat
import java.util.*

class PersonalDetailsEditFragment : Fragment() {


    private lateinit var personalInfo: PersonalInfo
    private lateinit var session: BdjobsUserSession
    lateinit var dataStorage: DataStorage
    private lateinit var now: Calendar
    private var gender = ""
    private var marital = ""
    private var nationality = "Bangladeshi"

    private val birthDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        now.set(Calendar.YEAR, year)
        now.set(Calendar.MONTH, monthOfYear)
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateDateInView()
    }

    private fun updateDateInView() {
        val myFormat = "MMM dd, yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etPerDob.setText(sdf.format(now.time))
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
    }

    override fun onResume() {
        super.onResume()
        personalInfo.setTitle(getString(R.string.title_personal))
        doWork()
    }

    private fun doWork() {
        preloadedData()
        cbPerIsBd.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                nationality = etPerNationality.getString()
                nidTIL.hide()
                nationalityTIL.show()
            } else {
                nationality = "Bangladeshi"
                etPerNationality.setText(nationality)
                nidTIL.show()
                nationalityTIL.hide()
            }
        }
        etPerDob.setOnClickListener { pickDate(activity, now, birthDateSetListener) }
        fab_per_update.setOnClickListener {
            updateData()
        }
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
        etPerDob.setText(data.dateofBirth)
        etPerNid.setText(data.nationalIdNo)
        selectChip(cgGender, data.gender!!)
        selectChip(cgMarital, data.maritalStatus!!)
        cbPerIsBd.isChecked = !data.maritalStatus.isNullOrBlank()
    }

    private fun updateData() {
        activity.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().updatePersonalData(session.userId, session.decodId, session.IsResumeUpdate,
                etPerFirstName.getString(), etPerLastName.getString(), etPerFName.getString(), etPerMName.getString(),
                etPerDob.getString(), nationality, marital, gender, etPerNid.getString(), etPerReligion.getString())
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
