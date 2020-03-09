package com.bdjobs.app.editResume.educationInfo.fragments.professionalQualification

import android.app.DatePickerDialog
import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.adapters.models.ProfessionalModel
import com.bdjobs.app.editResume.callbacks.EduInfo
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_professional_ql_edit.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ProfessionalQLEditFragment : Fragment() {


    var isEdit: Boolean = false
    private lateinit var eduCB: EduInfo
    private lateinit var session: BdjobsUserSession
    private lateinit var hPqualificationID: String
    private lateinit var hID: String
    lateinit var datePickerDialog: DatePickerDialog
    private lateinit var calendar: Calendar
    private var date: Date? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        return inflater.inflate(R.layout.fragment_professional_ql_edit, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialization()
        doWork()
        if (!isEdit) {
            eduCB.setDeleteButton(false)
            hID = "-3"
            clearEditText()
            hPqualificationID = ""
            d("hid val $isEdit: $hID")
        }

    }


    private fun initialization() {

        session = BdjobsUserSession(activity)
        eduCB = activity as EduInfo
        calendar = Calendar.getInstance()
        eduCB.setTitle(getString(R.string.title_professional_qualification))
        addTextChangedListener(etPqCertification, certificationTIL)
        addTextChangedListener(etPqInstitute, prfInstituteTIL)
        addTextChangedListener(etPqStartDate, pqStartDateTIL)
        addTextChangedListener(etPqEndDate, pqEndDateTIL)



        etPqCertification?.addTextChangedListener(TW.CrossIconBehave(etPqCertification))
        etPqInstitute?.addTextChangedListener(TW.CrossIconBehave(etPqInstitute))
        etPqLocation?.addTextChangedListener(TW.CrossIconBehave(etPqLocation))


    }

    private val startDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        updateDateInView(0)
    }
    private val endDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        calendar?.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        updateDateInView(1)
    }

    private fun updateDateInView(c: Int) {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        if (c == 0) {

            etPqStartDate.setText(sdf.format(calendar.time))
        } else {
            etPqEndDate.setText(sdf.format(calendar.time))
        }
    }


    private fun doWork() {


        fab_Professional_update?.setOnClickListener {
            var validation = 0
            validation = isValidate(etPqCertification, certificationTIL, etPqInstitute, true, validation)
            validation = isValidate(etPqInstitute, prfInstituteTIL, etPqInstitute, true, validation)
            validation = isValidate(etPqStartDate, pqStartDateTIL, etPqEndDate, true, validation)
            validation = isValidate(etPqEndDate, pqEndDateTIL, etPqEndDate, true, validation)

            //Log.d("validation", "validation : $validation")
            if (validation == 4) {

                if (dateValidationCheck()) {
                    updateData()
                }

            }


        }



        etPqStartDate?.setOnClickListener {

            val cal = Calendar.getInstance()
            val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)

            if (isEdit) {
                try {
                    date = formatter.parse(etPqStartDate.text.toString())
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                date?.let {
                    cal.time = date
                    pickDate(activity, cal, startDateSetListener)
                }
            } else {
                if (etPqStartDate.text.toString().isNullOrEmpty())
                    pickDate(activity, cal, startDateSetListener)
                else {
                    date = formatter.parse(etPqStartDate.text.toString())
                    cal.time = date
                    pickDate(activity, cal, startDateSetListener)
                }
            }

//            val mYear = calendar!!.get(Calendar.YEAR)
//            val mMonth = calendar!!.get(Calendar.MONTH)
//            val mDay = calendar!!.get(Calendar.DAY_OF_MONTH)
//            datePickerDialog = DatePickerDialog(activity,
//                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//
//                        val date = "${monthOfYear + 1}/$dayOfMonth/$year"
//                        //Log.d("Test", " date default ${date} ")
//                        etPqStartDate.setText(date)
//
//
//                    }, mYear, mMonth, mDay)
//
//            datePickerDialog.show()


        }
        etPqEndDate?.setOnClickListener {

            val cal = Calendar.getInstance()
            val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)

            if (isEdit) {
                try {
                    date = if (eduCB?.getProfessionalData().to == "Continuing") {
                        if (!etPqEndDate.text.toString().isNullOrEmpty()) {
                            formatter.parse(etPqEndDate.text.toString())
                        } else {
                            Date()
                        }
                    } else {
                        formatter.parse(etPqEndDate.text.toString())
                    }
                    date?.let {
                        cal.time = date
                        pickDate(activity, cal, endDateSetListener)
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

            } else {
                try {
                    if (!etPqEndDate.text.toString().isNullOrEmpty()){
                        date = formatter.parse(etPqEndDate.text.toString())
                        cal.time = date
                        pickDate(activity, cal, endDateSetListener)
                    } else {
                        pickDate(activity, cal, endDateSetListener)
                    }
                } catch (e: Exception) {
                }

            }

//            val mYear = calendar!!.get(Calendar.YEAR)
//            val mMonth = calendar!!.get(Calendar.MONTH)
//            val mDay = calendar!!.get(Calendar.DAY_OF_MONTH)
//            // date picker dialog
//            datePickerDialog = DatePickerDialog(activity,
//                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//
//                        val date = "${monthOfYear + 1}/$dayOfMonth/$year"
//                        //Log.d("Test", " date default ${date} ")
//                        etPqEndDate.setText(date)
//
//                    }, mYear, mMonth, mDay)
//
//            datePickerDialog.show()


        }
    }


    private fun addTextChangedListener(editText: TextInputEditText, inputLayout: TextInputLayout) {
        editText.easyOnTextChangedListener { charSequence ->
            eduCB.validateField(charSequence.toString(), editText, inputLayout)
        }
    }


    private fun updateData() {
        activity.showProgressBar(professionalLoadingProgressBar)
        val call = ApiServiceMyBdjobs.create().updatePQualificationList(session.userId, session.decodId, etPqCertification.getString(),
                etPqInstitute.getString(), etPqStartDate.getString(), etPqEndDate.getString(), hID,
                session.IsResumeUpdate, etPqLocation.getString(), hPqualificationID)

        call.enqueue(object : Callback<ProfessionalModel> {
            override fun onFailure(call: Call<ProfessionalModel>, t: Throwable) {
                activity?.stopProgressBar(professionalLoadingProgressBar)
                activity?.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<ProfessionalModel>, response: Response<ProfessionalModel>) {

                try {
                    activity.stopProgressBar(professionalLoadingProgressBar)
                    if (response.isSuccessful) {
                        activity.stopProgressBar(professionalLoadingProgressBar)
                        val resp = response.body()
                        activity.toast(resp?.message.toString())
                        if (resp?.statuscode == "4") {
                            eduCB.setBackFrom(Constants.profUpdate)
                            eduCB.goBack()
                        }
                    }
                } catch (e: Exception) {
                    assert(activity != null)
                    activity?.stopProgressBar(professionalLoadingProgressBar)
                    e.printStackTrace()
                }
            }
        })
    }


    private fun dateValidationCheck(): Boolean {
        val sdf1 = SimpleDateFormat("MM/dd/yyyy")
        try {
            val date1 = sdf1.parse(etPqStartDate.getString())
            val date2 = sdf1.parse(etPqEndDate.getString())

            if (date1.after(date2)) {
                activity.toast("Start Date cannot be greater than End Date!")
            } else {

                if (date1 == date2) {

                    activity.toast("Start Date and End Date cannot be equal!")
                    return false

                } else {

                    return true
                }


            }


        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return false

    }


    override fun onResume() {
        super.onResume()

        d(isEdit.toString())
        /* etTrTitle.requestFocus()
         etTrDuration.clearFocus()*/
        if (isEdit) {
            hID = "3"
            eduCB.setDeleteButton(true)
            preloadedData()
            d("hid val $isEdit : $hID")
        } else {
            eduCB.setDeleteButton(false)
            hID = "-3"
            clearEditText()
            hPqualificationID = ""
            d("hid val $isEdit: $hID")
        }


    }


    private fun preloadedData() {
        val data = eduCB.getProfessionalData()

        hPqualificationID = data.prId.toString()
        etPqCertification?.setText(data.certification)
        etPqInstitute?.setText(data.institute)
        etPqLocation?.setText(data.location)
        etPqStartDate?.setText(data.from)
        etPqEndDate?.setText(data.to)


    }

    private fun clearEditText() {
        etPqCertification?.clear()
        etPqInstitute?.clear()
        etPqLocation?.clear()
        etPqStartDate?.clear()
        etPqEndDate?.clear()

        etPqCertification?.clearFocus()
        etPqInstitute?.clearFocus()
        etPqLocation?.clearFocus()
        etPqStartDate?.clearFocus()
        etPqEndDate?.clearFocus()

        disableError()
    }


    private fun disableError() {
        certificationTIL?.hideError()
        prfInstituteTIL?.hideError()
        prqLocationTIL?.hideError()
        pqEndDateTIL?.hideError()
        pqStartDateTIL?.hideError()
    }


    fun dataDelete() {
        activity.showProgressBar(professionalLoadingProgressBar)
        val call = ApiServiceMyBdjobs.create().deleteData("Profession", hPqualificationID, session.IsResumeUpdate!!, session.userId!!, session.decodId!!)
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                activity.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity.stopProgressBar(professionalLoadingProgressBar)
                        val resp = response.body()
                        activity.toast(resp?.message.toString())
                        clearEditText()
//                        eduCB.getProfessionalList()?.let {
//                            for (item in it) {
//                                try {
//                                    if (item.prId!!.equalIgnoreCase(hPqualificationID)) {
//                                        eduCB.getProfessionalList()!!.remove(item)
//                                    }
//                                } catch (e: Exception) {
//                                }
//                            }
//                        }
                        eduCB.setBackFrom(Constants.profUpdate)
                        eduCB.goBack()
                    }
                } catch (e: Exception) {
                    activity.stopProgressBar(professionalLoadingProgressBar)
                    activity.toast(response.body()?.message.toString())
                    e.printStackTrace()
                }
            }
        })
    }


}
