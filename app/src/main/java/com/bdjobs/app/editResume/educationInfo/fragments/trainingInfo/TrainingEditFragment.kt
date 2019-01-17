package com.bdjobs.app.editResume.educationInfo.fragments.trainingInfo


import android.app.DatePickerDialog
import android.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.callbacks.EduInfo
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_training_edit.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class TrainingEditFragment : Fragment() {

    var isEdit: Boolean = false
    private lateinit var eduCB: EduInfo
    private lateinit var session: BdjobsUserSession
    private lateinit var hTrainingID: String
    private lateinit var hID: String
    private var calendar: Calendar? = null
    lateinit var datePickerDialog: DatePickerDialog
    private var yearList = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_training_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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

        /* etTrTopic.isFocusable = false*/
        /*  etTrCountry.isFocusable = false*/
        /* etTrTrainingYear.isFocusable = false
         etTrInstitute.isFocusable = false*/
        /*  etTrTitle.isFocusable = false*/


        etTrTopic?.addTextChangedListener(trainingTextWatcher(etTrTopic))
        etTrCountry?.addTextChangedListener(trainingTextWatcher(etTrCountry))
        etTrTrainingYear?.addTextChangedListener(trainingTextWatcher(etTrTrainingYear))
        etTrInstitute?.addTextChangedListener(trainingTextWatcher(etTrInstitute))
        etTrTitle?.addTextChangedListener(trainingTextWatcher(etTrTitle))
        etTrTopic?.addTextChangedListener(trainingTextWatcher(etTrTopic))
        etTrLoc?.addTextChangedListener(trainingTextWatcher(etTrLoc))

        session = BdjobsUserSession(activity)
        eduCB = activity as EduInfo
        calendar = Calendar.getInstance()


    }

    override fun onResume() {
        super.onResume()
        ehMailLL.clearFocus()
        d(isEdit.toString())
        if (isEdit) {
            hID = "2"
            eduCB.setDeleteButton(true)
            preloadedData()
            d("hid val $isEdit : $hID")
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


        trainingTitleTIL.isErrorEnabled = false
        trInstituteTIL.isErrorEnabled = false
        trCountryTIL.isErrorEnabled = false

        trTrainingYearTIL.isErrorEnabled = false
        trDurTIL.isErrorEnabled = false

        d("values : ${data.country}")


    }

    private fun doWork() {
        eduCB.setTitle(getString(R.string.title_training))
        addTextChangedListener()

        etTrTrainingYear.setOnClickListener {


            for (item in 1964..2019) {
                yearList.add(item.toString())
            }
            activity.selector("Please Select Training Year", yearList.toList()) { _, i ->

                etTrTrainingYear.setText(yearList[i])
                trTrainingYearTIL.requestFocus()

            }



        }




        fab_tr_update.setOnClickListener {


            var validation = 0

            if (TextUtils.isEmpty(etTrTitle.getString())) {

                trainingTitleTIL.showError("This Field can not be empty")

            } else {

                validation++
                trainingTitleTIL.isErrorEnabled = false
                etTrInstitute.requestFocus()

            }

            if (TextUtils.isEmpty(etTrInstitute.getString())) {

                trInstituteTIL.showError("This Field can not be empty")

            } else {

                trInstituteTIL.isErrorEnabled = false
                validation++
                etTrCountry.requestFocus()

            }

            if (TextUtils.isEmpty(etTrCountry.getString())) {

                trCountryTIL.showError("This Field can not be empty")

            } else {
                trCountryTIL.isErrorEnabled = false
                validation++
                etTrTrainingYear.requestFocus()

            }

            if (TextUtils.isEmpty(etTrTrainingYear.getString())) {

                trTrainingYearTIL.showError("This Field can not be empty")
            } else {
                trTrainingYearTIL.isErrorEnabled = false
                validation++
                etTrDuration.requestFocus()

            }


            if (TextUtils.isEmpty(etTrDuration.getString())) {

                trDurTIL.showError("This Field can not be empty")

            } else {
                trDurTIL.isErrorEnabled = false
                validation++
                etTrDuration.requestFocus()
            }



            Log.d("validation", "validation : $validation")


            setFocus(validation)


        }


    }


    private fun addTextChangedListener() {


        etTrTitle.easyOnTextChangedListener { charSequence ->
            eduCB.validateField(charSequence.toString(), etTrTitle, trainingTitleTIL)
        }

        etTrInstitute.easyOnTextChangedListener { charSequence ->

            eduCB.validateField(charSequence.toString(), etTrInstitute, trInstituteTIL)
            d("etTrInst : ->$charSequence|")

        }


        etTrCountry.easyOnTextChangedListener { charSequence ->

            eduCB.validateField(charSequence.toString(), etTrCountry, trCountryTIL)

        }


        etTrTrainingYear.easyOnTextChangedListener { charSequence ->


            eduCB.validateField(charSequence.toString(), etTrTrainingYear, trainingTitleTIL)

        }


        etTrDuration.easyOnTextChangedListener { charSequence ->


            durationValidation(charSequence.toString(), etTrDuration, trDurTIL)

        }


    }


    private fun durationValidation(char: String, et: TextInputEditText, til: TextInputLayout): Boolean {
        when {
            TextUtils.isEmpty(char) -> {
                til.showError(getString(R.string.field_empty_error_message_common))
                requestFocus(et)
                return false
            }
            else -> til.hideError()
        }
        return true
    }

    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }


    private fun updateData() {
        activity.showProgressBar(loadingProgressBarTraining)
        val call = ApiServiceMyBdjobs.create().updateTrainingList(session.userId, session.decodId, session.IsResumeUpdate,
                etTrTitle.getString(), etTrInstitute.getString(), etTrCountry.getString(), etTrTrainingYear.getString(),
                etTrDuration.getString(), hID, etTrTopic.getString(), etTrLoc.getString(), hTrainingID)

        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                activity.stopProgressBar(loadingProgressBarTraining)
                activity.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                activity.stopProgressBar(loadingProgressBarTraining)
                try {
                    if (response.isSuccessful) {
                        activity.stopProgressBar(loadingProgressBarTraining)
                        val resp = response.body()
                        activity.toast(resp?.message.toString())
                        if (resp?.statuscode == "4") {
                            eduCB.goBack()
                        }
                    }
                } catch (e: Exception) {
                    activity.stopProgressBar(loadingProgressBarTraining)
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

        ehMailLL.clearFocus()

        trainingTitleTIL.isErrorEnabled = false
        trInstituteTIL.isErrorEnabled = false
        trCountryTIL.isErrorEnabled = false
        trTrainingYearTIL.isErrorEnabled = false
        trDurTIL.isErrorEnabled = false



    }

    private fun showHideCrossButton(editText: EditText) {
        if (editText.text.isNullOrBlank()) {
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_ash, 0)
            editText.clearTextOnDrawableRightClick()
        }
    }


    private inner class trainingTextWatcher(private val editText: EditText) : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            showHideCrossButton(editText)
            debug(editText.getString())
        }
    }



    fun dataDelete() {
        activity.showProgressBar(loadingProgressBarTraining)
        val call = ApiServiceMyBdjobs.create().deleteData("Training", hTrainingID, session.IsResumeUpdate!!, session.userId!!, session.decodId!!)
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                activity.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity.stopProgressBar(loadingProgressBarTraining)
                        val resp = response.body()
                        activity.toast(resp?.message.toString())
                        clearEditText()
                        eduCB.goBack()
                    }
                } catch (e: Exception) {
                    activity.stopProgressBar(loadingProgressBarTraining)
                    activity.toast(response.body()?.message.toString())
                    e.printStackTrace()
                }
            }
        })
    }


    private fun setFocus(passvalue: Int) {


        when (passvalue) {
            5 -> {

                updateData()

            }

            11 -> {


            }
            0 -> {

            }
            else -> {


            }
        }
    }





}
