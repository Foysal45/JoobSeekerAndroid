package com.bdjobs.app.editResume.educationInfo.fragments.trainingInfo


import android.app.DatePickerDialog
import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.callbacks.EduInfo
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_training_edit.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TrainingEditFragment : Fragment() {

    var isEdit: Boolean = false
    private lateinit var eduCB: EduInfo
    private lateinit var session: BdjobsUserSession
    private lateinit var hTrainingID: String
    private lateinit var hID: String
    private var calendar: Calendar? = null
    private var yearSelected = false
    private var yearList = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_training_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        eduCB = activity as EduInfo
        eduCB.setTitle(getString(R.string.title_training))
        initialization()
        doWork()
        if (!isEdit) {
            eduCB.setDeleteButton(false)
            hID = "-2"
            clearEditText()
            hTrainingID = ""
            d("hid val $isEdit: $hID")
        }
    }

    private fun initialization() {
        etTrTopic?.addTextChangedListener(TW.CrossIconBehave(etTrTopic))
        etTrCountry?.addTextChangedListener(TW.CrossIconBehave(etTrCountry))
        etTrTrainingYear?.addTextChangedListener(TW.CrossIconBehave(etTrTrainingYear))
        etTrInstitute?.addTextChangedListener(TW.CrossIconBehave(etTrInstitute))
        etTrTitle?.addTextChangedListener(TW.CrossIconBehave(etTrTitle))
        etTrTopic?.addTextChangedListener(TW.CrossIconBehave(etTrTopic))
        etTrLoc?.addTextChangedListener(TW.CrossIconBehave(etTrLoc))
        etTrDuration?.addTextChangedListener(TW.CrossIconBehave(etTrDuration))
    }

    override fun onResume() {
        super.onResume()
        d(isEdit.toString())
        etTrTitle.requestFocus()
        etTrDuration.clearFocus()
        if (isEdit) {
            hID = "2"
            eduCB.setDeleteButton(true)
            preloadedData()
            d("hid val $isEdit : $hID")
        } else {
            eduCB.setDeleteButton(false)
            hID = "-2"
            clearEditText()
            hTrainingID = ""
            d("hid val $isEdit: $hID")
        }
    }

    private fun preloadedData() {
        val data = eduCB.getTrainingData()
        hTrainingID = data.trId.toString()
        etTrTitle.setText(data.title)
        etTrTopic.setText(data.topic)
        etTrCountry.setText(data.country)
        etTrTrainingYear.setText(data.year)
        etTrInstitute.setText(data.institute)
        etTrLoc.setText(data.location)
        etTrDuration.setText(data.duration)
        disableError()
        d("values : ${data.country}")
    }
    private fun addTextChangedListener(editText: TextInputEditText, inputLayout: TextInputLayout) {
        editText.easyOnTextChangedListener { charSequence ->
            eduCB.validateField(charSequence.toString(), editText, inputLayout)
        }
    }

    private fun doWork() {
        addTextChangedListener(etTrTitle, trainingTitleTIL)
        addTextChangedListener(etTrInstitute, trInstituteTIL)
        addTextChangedListener(etTrCountry, trCountryTIL)
        addTextChangedListener(etTrTrainingYear, trTrainingYearTIL)
        addTextChangedListener(etTrDuration, trainingTitleTIL)

        etTrTrainingYear.setOnClickListener {

            pickDateA()
            /*for (item in 1964..2024) {
                yearList.add(item.toString())
            }
            activity.selector("Select Year", yearList.toList()) { _, i ->
                etTrTrainingYear.setText(yearList[i])
                trTrainingYearTIL.requestFocus()
            }*/

        }
        fab_tr_update.setOnClickListener {
            clTrainingEdit.closeKeyboard(activity)
            var validation = 0
            validation = isValidate(etTrTitle, trainingTitleTIL, etTrInstitute, true, validation)
            validation = isValidate(etTrInstitute, trInstituteTIL, etTrCountry, true, validation)
            validation = isValidate(etTrCountry, trCountryTIL, etTrTrainingYear, true, validation)
            validation = isValidate(etTrTrainingYear, trTrainingYearTIL, etTrDuration, true, validation)
            validation = isValidate(etTrDuration, trDurTIL, etTrDuration, true, validation)
            Log.d("validation", "validation : $validation")
            if (validation == 5) updateData()
        }
    }

    private fun updateDateInView(year: Int) {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        yearSelected = true
        etTrTrainingYear.setText(year.toString())
    }

    private fun updateData() {
        activity.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().updateTrainingList(session.userId, session.decodId, session.IsResumeUpdate,
                etTrTitle.getString(), etTrInstitute.getString(), etTrCountry.getString(), etTrTrainingYear.getString(),
                etTrDuration.getString(), hID, etTrTopic.getString(), etTrLoc.getString(), hTrainingID)

        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                activity.stopProgressBar(loadingProgressBar)
                activity.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                activity.stopProgressBar(loadingProgressBar)
                try {
                    if (response.isSuccessful) {
                        activity.stopProgressBar(loadingProgressBar)
                        val resp = response.body()
                        activity.toast(resp?.message.toString())
                        if (resp?.statuscode == "4") {
                            eduCB.goBack()
                        }
                    }
                } catch (e: Exception) {
                    assert(activity != null)
                    activity.stopProgressBar(loadingProgressBar)
                    e.printStackTrace()
                }
            }
        })
    }

    private fun clearEditText() {
        etTrTitle.clear()
        etTrTopic.clear()
        etTrCountry.clear()
        etTrTrainingYear.clear()
        etTrInstitute.clear()
        etTrLoc.clear()
        etTrDuration.clear()
        disableError()
    }

    private fun disableError() {
        trainingTitleTIL.hideError()
        trInstituteTIL.hideError()
        trCountryTIL.hideError()
        trTrainingYearTIL.hideError()
        trDurTIL.hideError()
    }

    fun dataDelete() {
        activity.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().deleteData("Training", hTrainingID, session.IsResumeUpdate!!, session.userId!!, session.decodId!!)
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                activity.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity.stopProgressBar(loadingProgressBar)
                        val resp = response.body()
                        activity.toast(resp?.message.toString())
                        clearEditText()
                        eduCB.goBack()
                    }
                } catch (e: Exception) {
                    activity.stopProgressBar(loadingProgressBar)
                    activity.toast(response.body()?.message.toString())
                    e.printStackTrace()
                }
            }
        })
    }

    fun pickDateA() {
        yearSelected = false
        val now = Calendar.getInstance()
        val dateSelectedListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            now?.set(Calendar.YEAR, year)
            now?.set(Calendar.MONTH, monthOfYear)
            now?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView(year)
        }
        val dpd = DatePickerDialog(activity,
                dateSelectedListener,
                // set DatePickerDialog to point to today's date when it loads up
                now.get(Calendar.YEAR),
                now.get(Calendar.YEAR),
                now.get(Calendar.YEAR))

        /*now.add(yearT, -55) // subtract 2 years from now
        dpd.datePicker.minDate = now.timeInMillis
        now.add(yearT, 5) // add 4 years to min date to have 2 years after now
        dpd.datePicker.maxDate = now.timeInMillis*/
        dpd.datePicker.touchables[0].performClick()
        dpd.show()
    }
}
