package com.bdjobs.app.editResume.educationInfo.fragments.academicInfo


import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AcaDataItem
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.callbacks.EduInfo
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_academic_info_edit.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AcademicInfoEditFragment : Fragment() {

    var isEdit: Boolean = false
    private lateinit var v: View
    private lateinit var eduCB: EduInfo
    private lateinit var session: BdjobsUserSession
    private var hacaID: String = ""
    private lateinit var hID: String
    private var hideRes: String = "0"
    private var foreignInstitute: String = "0"
    private lateinit var ds: DataStorage
    private lateinit var data: AcaDataItem
    private var marks: Int = 0
    private var cgp: Double = 0.0
    private var scale: Double = 0.0
    private var resultValiadtionCode = 0
    private var yearList = ArrayList<String>()
    var validation = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_academic_info_edit, container, false)
        return v


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialization()
        doWork()
    }

    override fun onResume() {
        super.onResume()
        d(isEdit.toString())
        if (isEdit) {
            hID = "1"
            eduCB.setDeleteButton(true)
            preloadedData()
        } else {
            eduCB.setDeleteButton(false)
            hID = "-1"
            clearEditText()
        }
    }

    private fun initialization() {

        session = BdjobsUserSession(activity)
        eduCB = activity as EduInfo
        ds = eduCB.dataStorage()
        debug("checkbox" + cbResHide.isChecked.toString())
        eduCB.setTitle(getString(R.string.title_academic))


    }

    private fun preloadedData() {
        data = eduCB.getData()
        val resID = data.resultId!!
        hacaID = data.acId.toString()
        etLevelEdu.setText(data.levelofEducation)
        etExamTitle.setText(data.examDegreeTitle)
        majorSubACTV.setText(data.concentrationMajorGroup)
        instituteNameACTV.setText(data.instituteName)
        etResults.setText(ds.getResultNameByResultID(resID))
        setView(resID.toInt())
        cgpaTIET.setText(data.marks)
        etScaleTIET.setText(data.scale)
        etPassignYear.setText(data.yearofPAssing)
        etDuration.setText(data.duration)
        etAchievement.setText(data.acievement)
        cbResHide.isChecked = data.showMarks.equals("1")
        cbForInstitute.isChecked = data.instituteType.equals("1")

        levelEduTIL.isErrorEnabled = false
        examTitleTIL.isErrorEnabled = false
        mejorTIL.isErrorEnabled = false
        instituteTIL.isErrorEnabled = false
        resultTIL.isErrorEnabled = false
        acaPassingYearTIL.isErrorEnabled = false


    }

    private fun doWork() {

        addTextChangedListener()
        setDialog()
        cbResHide.setOnCheckedChangeListener { buttonView, isChecked ->

            Log.d("isChecked", "$isChecked")


            if (!TextUtils.isEmpty(etResults.getString())) {


                if (isChecked) {
                    if (etResults.getString().equalIgnoreCase("Grade")) {

                        gradeLayout.visibility = View.GONE

                    } else {
                        marksLayout.visibility = View.GONE

                    }


                } else {

                    if (etResults.getString().equalIgnoreCase("Grade")) {

                        gradeLayout.visibility = View.VISIBLE
                        cGpaTIL.isErrorEnabled = false
                        scaleTIL.isErrorEnabled = false

                    } else {
                        marksLayout.visibility = View.VISIBLE

                    }

                }


            }

        }

        fab_aca_edit.setOnClickListener {

            validation = 0
            checkValidity()
            if (etResults.getString().equalIgnoreCase("First Division/Class")
                    || etResults.getString().equalIgnoreCase("Second Division/Class")
                    || etResults.getString().equalIgnoreCase("Third Division/Class")) {
                if (marksTIET.getString().isEmpty()) {
                    resultValiadtionCode = 1
                } else {

                    marks = marksTIET.getString().toInt()

                    if (marks > 100 || marks < 1) {
                        resultValiadtionCode = 99
                    } else {

                        resultValiadtionCode = 100
                    }


                }

            } else if (etResults.getString().equalIgnoreCase("Grade")) {

                if (cgpaTIET.getString().isEmpty()) {
                    resultValiadtionCode = 4

                    if (cgpaTIET.getString().isEmpty() && etScaleTIET.getString().isEmpty()) {
                        resultValiadtionCode = 7

                    }

                } else if (etScaleTIET.getString().isEmpty()) {

                    resultValiadtionCode = 6
                    if (cgpaTIET.getString().isEmpty() && etScaleTIET.getString().isEmpty()) {
                        resultValiadtionCode = 7

                    }

                } else if (cgpaTIET.getString().isEmpty() && etScaleTIET.getString().isEmpty()) {

                    resultValiadtionCode = 7

                } else if (!cgpaTIET.getString().isEmpty() && !etScaleTIET.getString().isEmpty()) {

                    cgp = cgpaTIET.getString().toDouble()
                    scale = etScaleTIET.getString().toDouble()

                    if (cgp > scale) {

                        resultValiadtionCode = 5


                    } /*else if(cgp < scale) {

                        resultValiadtionCode = 0

                    }*/

                    if (cgp > 5.00 || cgp < 1.00) {

                        resultValiadtionCode = 2
                    } /*else {

                       resultValiadtionCode = 0
                   }*/

                    if (scale > 5.00 || scale < 1.00) {

                        resultValiadtionCode = 3
                    } /*else{

                       resultValiadtionCode = 0
                   }*/

                }

            }



            if (etExamTitle.getString().equalIgnoreCase("Other")) {

                if (etExamOtherTitle.getString().isEmpty()) {

                    resultValiadtionCode = 8

                } else if (etExamOtherTitle.getString().isEmpty())


                    resultValiadtionCode = 8


            }


            Log.d("Validation", " resultValiadtionCode : $resultValiadtionCode")
            Log.d("Validation", " validation :  $validation")

            if (validation >= 4) {

                if (etResults.getString().equalIgnoreCase("First Division/Class")
                        || etResults.getString().equalIgnoreCase("Second Division/Class")
                        || etResults.getString().equalIgnoreCase("Third Division/Class")
                        || etResults.getString().equalIgnoreCase("Grade")) {

                    Log.d("Validation", " else if condition $validation")


                    when (resultValiadtionCode) {
                        1 -> {
                            activity.toast(R.string.toast_marks_blank_texts)
                            marksTIL.requestFocus()
                        }

                        2 -> {

                            activity.toast(R.string.toast_invalide_cgpa_texts)
                            cgpaTIET.requestFocus()

                        }
                        3 -> {

                            activity.toast(R.string.toast_invalide_grade_texts)
                            etScaleTIET.requestFocus()

                        }
                        4 -> {

                            activity.toast(R.string.toast_cgpa_blank_texts)
                            cGpaTIL.requestFocus()
                        }
                        5 -> {

                            activity.toast(R.string.toast_cgpa_scale_michmatch_texts)
                        }
                        6 -> {

                            activity.toast(R.string.toast_scale_blank_texts)
                            scaleTIL.requestFocus()

                        }
                        7 -> {

                            if (!cbResHide.isChecked) {

                                activity.toast(R.string.toast_scale_grade_blank_texts)
                                cGpaTIL.requestFocus()
                            }


                        }
                        8 -> {

                            activity.toast(R.string.toast_exam_degree_title_other_blank)

                            examOtherTIL.requestFocus()
                        }

                        99 -> {

                            activity.toast("Please enter a valid marks")
                            marksTIET.requestFocus()
                        }

                    }

                }


            }


            if (etExamTitle.getString().equalIgnoreCase("Other")) {

                Log.d("Validation", " in Other $validation")

                if (etResults.getString().equalIgnoreCase("Appeared")
                        || etResults.getString().equalIgnoreCase("Enrolled")
                        || etResults.getString().equalIgnoreCase("Awarded")
                        || etResults.getString().equalIgnoreCase("Do not mention")
                        || etResults.getString().equalIgnoreCase("Pass") || cbResHide.isChecked) {


                    if (validation == 7) {

                        updateData()
                    }

                } else if (etResults.getString().equalIgnoreCase("Grade") && !cbResHide.isChecked) {


                    Log.d("Validation", " in Grade $validation")

                    if (validation == 9) {

                        updateData()
                    }

                } else if (etResults.getString().equalIgnoreCase("First Division/Class")
                        || etResults.getString().equalIgnoreCase("Second Division/Class")
                        || etResults.getString().equalIgnoreCase("Third Division/Class")) {


                    Log.d("Validation", " in first division 2nd division $validation")

                    if (!cbResHide.isChecked) {

                        if (validation == 8) {

                            updateData()
                        }

                    }

                }


            } else if (!etExamTitle.getString().equalIgnoreCase("Other")) {

                Log.d("Validation", " in not Other $validation")
                if (etResults.getString().equalIgnoreCase("Appeared")
                        || etResults.getString().equalIgnoreCase("Enrolled")
                        || etResults.getString().equalIgnoreCase("Awarded")
                        || etResults.getString().equalIgnoreCase("Do not mention")
                        || etResults.getString().equalIgnoreCase("Pass") || cbResHide.isChecked) {

                    Log.d("Validation", " result not viewed $validation")

                    if (validation == 6) {

                        updateData()
                    }


                } else if (etResults.getString().equalIgnoreCase("Grade") && !cbResHide.isChecked) {


                    Log.d("Validation", " not other in Grade $validation")

                    if (validation == 8) {

                        updateData()
                    }

                } else if (etResults.getString().equalIgnoreCase("First Division/Class")
                        || etResults.getString().equalIgnoreCase("Second Division/Class")
                        || etResults.getString().equalIgnoreCase("Third Division/Class")) {


                    Log.d("Validation", " not other in first division 2nd division $validation")

                    if (!cbResHide.isChecked) {

                        if (validation == 7) {

                            updateData()
                        }

                    }

                }


            }


            Log.d("Validation", " cbResHide.isChecked ${cbResHide.isChecked}")


            /*       if (cbResHide.isChecked) {

                       Log.d("Validation", " isChecked $validation")
                       if (validation == 8) {

                           updateData()
                       }
                   }*/


            /* if (cbResHide.isVisible) {

                 if (!cbResHide.isChecked) {

                     Log.d("Validation", " not  isChecked $validation")
                     if (etResults.getString().equalIgnoreCase("Grade")) {
                         if (validation == 10 ) {
                             updateData()
                              Log.d("Validation", " dsjkgf $resultValiadtionCode")
                         }
                     } else if (etResults.getString().equalIgnoreCase("First Division/Class")
                             || etResults.getString().equalIgnoreCase("Second Division/Class")
                             || etResults.getString().equalIgnoreCase("Third Division/Class")) {

                         Log.d("Validation", " else if condition $validation")

                         if (validation == 9) {

                             updateData()
                         }
                     }

                 }

             } else {

                 if (validation == 8) {

                     updateData()
                 }
             }*/



        }


    }


    private fun addTextChangedListener() {


        etLevelEdu.easyOnTextChangedListener { charSequence ->
            eduCB.validateField(charSequence.toString(), etLevelEdu, levelEduTIL)
        }

        etExamTitle.easyOnTextChangedListener { charSequence ->

            eduCB.validateField(charSequence.toString(), etExamTitle, examTitleTIL)
            d("etTrInst : ->$charSequence|")

        }

        etExamOtherTitle.easyOnTextChangedListener { charSequence ->

            eduCB.validateField(charSequence.toString(), etExamOtherTitle, examOtherTIL)

        }

        majorSubACTV.easyOnTextChangedListener { charSequence ->

            validateACTV(charSequence.toString(), majorSubACTV, mejorTIL)

        }


        instituteNameACTV.easyOnTextChangedListener { charSequence ->

            validateACTV(charSequence.toString(), instituteNameACTV, instituteTIL)

        }


        etResults.easyOnTextChangedListener { charSequence ->


            eduCB.validateField(charSequence.toString(), etResults, resultTIL)

        }

        etPassignYear.easyOnTextChangedListener { charSequence ->


            eduCB.validateField(charSequence.toString(), etPassignYear, acaPassingYearTIL)

        }

        marksTIET.easyOnTextChangedListener { charSequence ->

            eduCB.validateField(charSequence.toString(), marksTIET, marksTIL)

        }



        cgpaTIET.easyOnTextChangedListener { charSequence ->

            validateGradeEt(charSequence.toString(), cgpaTIET, cGpaTIL)

        }

        etScaleTIET.easyOnTextChangedListener { charSequence ->

            validateGradeEt(charSequence.toString(), etScaleTIET, scaleTIL)

        }


    }


    private fun validateACTV(char: String, et: AutoCompleteTextView, til: TextInputLayout): Boolean {
        when {
            TextUtils.isEmpty(char) -> {
                til.showError(getString(R.string.field_empty_error_message_common))
                requestFocus(et)
                return false
            }
            char.length < 2 -> {
                til.showError("it is too short")
                requestFocus(et)
                return false
            }
            else -> til.hideError()
        }
        return true
    }


    private fun validateGradeEt(char: String, et: TextInputEditText, til: TextInputLayout): Boolean {
        when {
            TextUtils.isEmpty(char) -> {
                til.showError(getString(R.string.field_empty_error_message_common))
                requestFocus(et)
                return false
            }
            char.length < 1 -> {
                til.showError("it is too short")
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

    private fun checkValidity() {


        // Level of education
        if (TextUtils.isEmpty(etLevelEdu.getString())) {

            levelEduTIL.showError("This Field can not be empty")

        } else {

            validation++
            levelEduTIL.isErrorEnabled = false


        }

        // exam degree title

        if (TextUtils.isEmpty(etExamTitle.getString())) {

            examTitleTIL.showError("This Field can not be empty")

        } else {

            validation++
            examTitleTIL.isErrorEnabled = false


        }

        // exam degree title other


        if (etExamOtherTitle.isVisible) {

            if (TextUtils.isEmpty(etExamOtherTitle.getString())) {

                examOtherTIL.showError("This Field can not be empty")

            } else {

                validation++
                examOtherTIL.isErrorEnabled = false


            }
        }


        // concentration major group

        if (TextUtils.isEmpty(majorSubACTV.getString())) {

            mejorTIL.showError("This Field can not be empty")

        } else {

            validation++
            mejorTIL.isErrorEnabled = false


        }

        //institute
        if (TextUtils.isEmpty(instituteNameACTV.getString())) {

            instituteTIL.showError("This Field can not be empty")

        } else {

            validation++
            instituteTIL.isErrorEnabled = false


        }

        // result

        if (TextUtils.isEmpty(etResults.getString())) {

            resultTIL.showError("This Field can not be empty")

        } else {

            validation++
            resultTIL.isErrorEnabled = false


        }

        //passing year
        if (TextUtils.isEmpty(etPassignYear.getString())) {

            acaPassingYearTIL.showError("This Field can not be empty")

        } else {

            validation++
            acaPassingYearTIL.isErrorEnabled = false


        }

        //marks validation

        if (marksLayout.isVisible) {

            if (TextUtils.isEmpty(marksTIET.getString())) {

                marksTIL.showError("This Field can not be empty")

            } else {

                validation++
                marksTIL.isErrorEnabled = false

            }


        }


        //Grade  and scale validation


        if (gradeLayout.isVisible) {

            if (TextUtils.isEmpty(cgpaTIET.getString())) {

                cGpaTIL.showError("This Field can not be empty")

            } else {

                validation++
                cGpaTIL.isErrorEnabled = false

            }

            if (TextUtils.isEmpty(etScaleTIET.getString())) {

                scaleTIL.showError("This Field can not be empty")

            } else {

                validation++
                scaleTIL.isErrorEnabled = false

            }

        }




        Log.d("Validation", "value $validation")

    }


/*private fun validateScale(): Boolean {
    if (gradeLL.getVisibility() == View.VISIBLE) {
        var i = 0f
        try {
            i = java.lang.Float.parseFloat(scaleET.getText().toString())
        } catch (ex: NumberFormatException) { // handle your exception

        }

        if (scaleET.getText().toString().trim({ it <= ' ' }).isEmpty()) {
            scaleTIL.setErrorEnabled(true)
            scaleTIL.setError(getResources().getString(R.string.field_empty_error_message_common))
            requestFocus(scaleET)
            return false
        } else if (i > 5.00 || i < 1.00) {
            scaleTIL.setErrorEnabled(true)
            scaleTIL.setError("CGPA Scale can not be greater than 5.00 or less than 1.00")
            requestFocus(scaleET)
            return false
        } else {
            scaleTIL.setErrorEnabled(false)
        }
        return true
    }
    return false
}

private fun validateCgpa(): Boolean {
    if (gradeLL.getVisibility() == View.VISIBLE) {
        var i = 0f
        try {
            i = java.lang.Float.parseFloat(cgpaET.getText().toString())
        } catch (ex: NumberFormatException) { // handle your exception

        }

        if (cgpaET.getText().toString().trim({ it <= ' ' }).isEmpty()) {
            cgpaTIL.setErrorEnabled(true)
            cgpaTIL.setError(getResources().getString(R.string.field_empty_error_message_common))
            requestFocus(cgpaET)
            return false
        } else if (i > 5.00 || i < 1.00) {
            cgpaTIL.setErrorEnabled(true)
            cgpaTIL.setError("CGPA can not be greater than 5.00 or less than 1.00")
            requestFocus(cgpaET)
            return false
        } else {
            cgpaTIL.setErrorEnabled(false)
        }
        return true
    }
    return false
}*/


    private fun setDialog() {
        val institutes = ds.allInstitutes
        val majorSubjects = ds.allInMajorSubjects


        val universityAdapter = ArrayAdapter<String>(activity,
                android.R.layout.simple_dropdown_item_1line, institutes)
        instituteNameACTV.setAdapter(universityAdapter)
        instituteNameACTV.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT

        val mejorSubjectAdapter = ArrayAdapter<String>(activity,
                android.R.layout.simple_dropdown_item_1line, majorSubjects)
        majorSubACTV.setAdapter(mejorSubjectAdapter)
        majorSubACTV.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT



        etLevelEdu.setOnClickListener {
            val eduLevelList: Array<String> = ds.allEduLevels
            activity.selector("Select level of education", eduLevelList.toList()) { _, i ->
                etLevelEdu.setText(eduLevelList[i])
                levelEduTIL.requestFocus()
                val eduLevel = ds.getEduIDByEduLevel(eduLevelList[i])
                etLevelEdu.setText(ds.getEduLevelByID(eduLevel))
                /*newWorkExperineceID = ",$workExperineceID,"
            exps += ",$workExperineceID,"*/
                //addAsString(workExperineceID)
                Log.d("eduLevel", "eduLevel ID ${ds.getEduIDByEduLevel(eduLevelList[i])}")
            }
        }
        etExamTitle.setOnClickListener {
            var queryValue = etLevelEdu.getString()
            queryValue = queryValue.replace("'", "''")
            val edulevelID = ds.getEduIDByEduLevel(queryValue)
            val examList: Array<String> = ds.getEducationDegreesByEduLevelID(edulevelID)
            activity.selector(getString(R.string.alert_exam_title), examList.toList()) { _, i ->
                etExamTitle.setText(examList[i])
                examTitleTIL.requestFocus()
                Log.d("eduLevel", "ExamTitle ${examList[i]}")
                if (examList[i].equals("Other")) {

                    examOtherTIL.show()
                    examOtherTIL.isErrorEnabled = false

                    if (!isEdit) {
                        etExamOtherTitle.clear()
                        examOtherTIL.isErrorEnabled = false

                    }

                    /* bcEduDegreeOtherTIET.visibility = View.VISIBLE*/
                } else {
                    examOtherTIL.hide()

                }


            }
        }
        etResults.setOnClickListener {
            //val examList: Array<String> = ds.getEducationDegreesByEduLevelID(edulevelID)
            val result: Array<String> = ds.allResults
            activity.selector(getString(R.string.alert_exam_result), result.toList()) { _, i ->
                etResults.setText(result[i])
                resultTIL.requestFocus()
                val examId = ds.getResultIDByResultName(result[i])

                setView(examId.toInt())
                Log.d("eduLevel", "examId $examId")

                etResults.setText(ds.getResultNameByResultID(examId))
                Log.d("eduLevel", "eduLevel ${ds.getResultNameByResultID(examId)}")


            }
        }
        cbResHide.setOnCheckedChangeListener { _, isChecked ->
            hideRes = if (isChecked) "1" else "0"
            Log.d("eduLevel", "hide $hideRes")
        }
        cbForInstitute.setOnCheckedChangeListener { _, isChecked ->
            foreignInstitute = if (isChecked) "1" else "0"
        }


        etPassignYear.setOnClickListener {


            for (item in 1964..2019) {
                yearList.add(item.toString())
            }
            activity.selector("Please Select Passing Year", yearList.toList()) { _, i ->

                etPassignYear.setText(yearList[i])
                acaPassingYearTIL.requestFocus()

            }


        }


    }

    private fun marksValidation(marks: Int): Boolean {

        if (marks > 100 || marks < 1) {

            return true

        }

        return false
    }

    private fun updateData() {

        activity.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().updateAcademicData(session.userId, session.decodId, session.IsResumeUpdate,
                ds.getEduIDByEduLevel(etLevelEdu.getString()), etExamTitle.getString(), instituteNameACTV.getString(),
                etPassignYear.getString(), majorSubACTV.getString(),
                hID, foreignInstitute, "1", ds.getResultIDByResultName(etResults.getString()),
                etScaleTIET.getString(), cgpaTIET.getString(), etDuration.getString(), etAchievement.getString(), hacaID, hideRes)

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
                    activity.stopProgressBar(loadingProgressBar)
                    e.printStackTrace()
                }
            }
        })
    }

    fun dataDelete() {
        activity.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().deleteData("Education", hacaID, session.IsResumeUpdate!!, session.userId!!, session.decodId!!)
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

    private fun clearEditText() {
        etLevelEdu.clear()
        etExamTitle.clear()
        majorSubACTV.setText("")
        instituteNameACTV.setText("")
        etResults.clear()
        cgpaTIET.clear()
        etScaleTIET.clear()
        etPassignYear.clear()
        etDuration.clear()
        etAchievement.clear()
        cbResHide.isChecked = false
        cbForInstitute.isChecked = false


        levelEduTIL.isErrorEnabled = false
        examTitleTIL.isErrorEnabled = false
        mejorTIL.isErrorEnabled = false
        instituteTIL.isErrorEnabled = false
        resultTIL.isErrorEnabled = false
        acaPassingYearTIL.isErrorEnabled = false
        majorSubACTV.clearFocus()
        instituteNameACTV.clearFocus()

    }

    private fun setView(passvalue: Int) {
        val value = if (passvalue == 13 || passvalue == 14 || passvalue == 15)
            100
        else passvalue

        when (value) {
            100 -> {
                llResultFields.show()
                gradeLayout.hide()
                marksLayout.show()
                cbResHide.show()
            }

            11 -> {
                llResultFields.show()
                gradeLayout.show()

                cGpaTIL.isErrorEnabled = false
                scaleTIL.isErrorEnabled = false
                etScaleTIET.clearFocus()

                marksLayout.hide()
                cbResHide.show()

            }
            0 -> {
                cbResHide.isChecked = true
                llResultFields.hide()
            }
            else -> {
                llResultFields.hide()
                cbResHide.hide()

            }
        }
    }
}
