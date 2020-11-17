package com.bdjobs.app.editResume.educationInfo.fragments.academicInfo


import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.acaUpdate
import com.bdjobs.app.editResume.adapters.models.AcaDataItem
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.callbacks.EduInfo
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.whiteelephant.monthpicker.MonthPickerDialog
import kotlinx.android.synthetic.main.fragment_academic_info_edit.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


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
    private var marks: Double = 0.0
    private var yearList = ArrayList<String>()
    var validation = 0
    private var examdegree = ""
    private var instSuggession = true
    private var gradeOrMarks = "0"
    private var scaleORCgpa = ""
    private var boardId = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        // Inflate the layout for this fragment

        d("viewTest onCreateView ")

        v = inflater.inflate(R.layout.fragment_academic_info_edit, container, false)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        d("viewTest onActivityCreated ")
        initialization()
        doWork()
        fragAcaInfoEdit.closeKeyboard(activity)
    }

    override fun onResume() {
        super.onResume()

        d("viewTest onResume ")

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

        cbForInstitute.isChecked = data.instituteType == "1"

        val resID = data.resultId!!
        hacaID = data.acId.toString()

        if (data.levelofEducation!!.equalIgnoreCase("PSC/5 pass") || data.levelofEducation!!.equalIgnoreCase("JSC/JDC/8 pass")) {

            majorSubACTV.hide()
            mejorTIL.hide()
        } else {

            majorSubACTV.show()
            mejorTIL.show()

        }

        etLevelEdu?.setText(ds.getEduLevelByID(data.levelofEducationId.toString()))
        etExamTitle?.setText(data.examDegreeTitle)


        etExamOtherTitle?.hide()
        examOtherTIL?.hide()
        etExamOtherTitle?.clear()



        majorSubACTV?.setText(data.concentrationMajorGroup)
        instituteNameACTV?.setText(data.instituteName)
        etResults?.setText(ds.getResultNameByResultID(resID))
        try {
            setView(resID.toInt())
        } catch (e: Exception) {
            logException(e)
        }
        cgpaTIET?.setText(data.marks)
        etScaleTIET?.setText(data.scale)
        if (data.scale!!.equalIgnoreCase("0")) {

            marksTIET.setText(data.marks)
        }

        etPassignYear?.setText(data.yearofPAssing)
        etDuration?.setText(data.duration)
        etAchievement?.setText(data.acievement)
        cbResHide?.isChecked = data.showMarks.equals("1")
        cbForInstitute?.isChecked = data.instituteType.equals("1")
        levelEduTIL?.isErrorEnabled = false
        examTitleTIL?.isErrorEnabled = false
        mejorTIL?.isErrorEnabled = false
        instituteTIL?.isErrorEnabled = false
        resultTIL?.isErrorEnabled = false
        acaPassingYearTIL?.isErrorEnabled = false

        majorSubACTV?.clearFocus()
        if (etLevelEdu.getString().equalIgnoreCase("Bachelor/Honors") || etLevelEdu.getString().equalIgnoreCase("Masters")) {
            instSuggession = true
            //Log.d("instSuggession", "in suggession condition")
        } else {
            //Log.d("instSuggession", "in  not suggession condition")
            instSuggession = false
        }

        try {

                if (data.boardId == "0" || data.boardId == "" || data.boardId!!.isEmpty()){
                    etBoard.setText("")
                    boardTIL?.hide()
                }
                else{
                    boardTIL?.show()
                    etBoard.setText(ds.getBoardNameByID(data.boardId!!.toInt()))
                }


        } catch (e: Exception) {
        }

    }

    private fun checkValidity(editText: TextInputEditText, textInputLayout: TextInputLayout) {
        if (TextUtils.isEmpty(editText.getString())) {
            textInputLayout.showError("This field can not be empty")
        } else {
            textInputLayout.isErrorEnabled = false
        }


    }

    private fun addValidityCheck() {
        checkValidity(etLevelEdu, levelEduTIL)
        if (examTitleTIL.isVisible) {
            checkValidity(etExamTitle, examTitleTIL)
        }
        // exam degree title other
        if (etExamOtherTitle.isVisible) {
            checkValidity(etExamOtherTitle, examOtherTIL)
        }

        if (etBoard.isVisible) {
            checkValidity(etBoard, boardTIL)
        }

        // concentration major group
        if (mejorTIL.isVisible) {
            if (TextUtils.isEmpty(majorSubACTV.getString())) {
                mejorTIL.showError("This Field can not be empty")
            } else {
                mejorTIL.isErrorEnabled = false

            }

        }
        //institute
        if (TextUtils.isEmpty(instituteNameACTV.getString())) {
            instituteTIL.showError("This Field can not be empty")
        } else {
            instituteTIL.isErrorEnabled = false
        }
        // result
        checkValidity(etResults, resultTIL)
        //passing year
        checkValidity(etPassignYear, acaPassingYearTIL)
        //marks validation
        if (marksLayout.isVisible) {
            checkValidity(marksTIET, marksTIL)

        }
        //Grade  and scale validation
        //Log.d("Validation", " gradeLayout.isVisible ${gradeLayout.isVisible}")
        if (gradeLayout.isVisible) {
            checkValidity(cgpaTIET, cGpaTIL)
            checkValidity(etScaleTIET, scaleTIL)

        }

    }

    private fun doWork() {
        addTextChangedListener()
        setDialog()
        cbResHide?.setOnCheckedChangeListener { buttonView, isChecked ->

            //Log.d("isChecked", "$isChecked")



            if (!TextUtils.isEmpty(etResults.getString())) {
                if (isChecked) {

                    llResultFields?.hide()
                    marksLayout?.hide()
                    gradeLayout?.hide()


                } else {

                    if (etResults.getString().equalIgnoreCase("Grade")) {
                        llResultFields?.show()
                        gradeLayout?.show()
                        marksLayout?.hide()
                        cgpaTIET?.clear()
                        etScaleTIET?.clear()
                        etScaleTIET.clearFocus()
                        cGpaTIL?.isErrorEnabled = false
                        scaleTIL?.isErrorEnabled = false


                    } /*else {
                        marksLayout.visibility = View.VISIBLE

                    }*/

                    if (etResults.getString().equalIgnoreCase("First Division/Class")
                            || etResults.getString().equalIgnoreCase("Second Division/Class")
                            || etResults.getString().equalIgnoreCase("Third Division/Class")) {


                        llResultFields?.show()
                        gradeLayout?.visibility = View.GONE

                        marksTIET?.clear()
                        marksLayout?.show()

                        marksTIL?.isErrorEnabled = false

                    }


                }


            }

        }
        fab_aca_edit?.setOnClickListener {
            fragAcaInfoEdit.closeKeyboard(activity)
            addValidityCheck()
            d("Validation check 1  ${validateLevelofEducation()}")
            if (validateLevelofEducation()) {
                d("Validation check 2  ")
                d("Validation check  ${validateExamDegreeTile()}  ${etExamOtherTitle.isVisible}  ${validateExamDegreeTitleOther()} ")

                if ((validateExamDegreeTile() && !etExamOtherTitle.isVisible) || (etExamOtherTitle.isVisible && validateExamDegreeTitleOther())) {
                    d("Validation check 3 ")
                    if ((majorSubACTV.isVisible && validateMajor()) || !majorSubACTV.isVisible) {

                        d("Validation check 4 ")

                        if (validateInstituteName()) {

                            d("Validation check 5 ")

                            if (validateResult()) {

                                d("Validation check 6 ")
                                if (cbResHide.isVisible) {

                                    d("Validation check 7 ")
                                    if (!cbResHide.isChecked) {


                                        d("Validation check 8 ")

                                        if (validateMarks()) {

                                            d("Validation check 9 ")

                                            if (validatePassingYear()) {

                                                d("Validation check 10 ")
                                                //Log.d("dshjfdg", " in first condition")
                                                updateData()

                                            }
                                        }

                                        if (validateCgpa() && validateScale()) {

                                            d("Validation check 11 ")

                                            if (validatePassingYear()) {


                                                d("Validation check 12 ")

                                                //Log.d("dshjfdg", " in second condition")
                                                updateData()

                                            }

                                        }

                                    } else {

                                        d("Validation check 13 ")
                                        if (validatePassingYear()) {

                                            //Log.d("dshjfdg", " in third condition")
                                            updateData()

                                        }
                                    }

                                } else {

                                    d("Validation check 14 ")

                                    if (validatePassingYear()) {

                                        //Log.d("dshjfdg", " in fourth condition")
                                        updateData()

                                    }
                                }


                            }

                        }

                    }


                }


            }


        }


    }


    private fun addTextChangedListener() {

        addTextChangedListener(etLevelEdu, levelEduTIL)
        addTextChangedListener(etExamTitle, examTitleTIL)
        addTextChangedListener(etBoard, boardTIL)
        addTextChangedListener(etExamOtherTitle, examOtherTIL)
        addTextChangedListener(etResults, resultTIL)
        addTextChangedListener(etPassignYear, acaPassingYearTIL)
        addTextChangedListenerMark(marksTIET, marksTIL)
        addTextChangedListenerMark(cgpaTIET, cGpaTIL)
        addTextChangedListenerMark(etScaleTIET, scaleTIL)
        majorSubACTV?.easyOnTextChangedListener { charSequence ->

            validateACTV(charSequence.toString(), majorSubACTV, mejorTIL)

        }

        instituteNameACTV?.easyOnTextChangedListener { charSequence ->

            validateACTV(charSequence.toString(), instituteNameACTV, instituteTIL)

        }


        //cross icon add


        etLevelEdu?.addTextChangedListener(TW.CrossIconBehave(etLevelEdu))
        etBoard?.addTextChangedListener(TW.CrossIconBehave(etBoard))
        etExamTitle?.addTextChangedListener(TW.CrossIconBehave(etExamTitle))
        etExamOtherTitle?.addTextChangedListener(TW.CrossIconBehave(etExamOtherTitle))
        etResults?.addTextChangedListener(TW.CrossIconBehave(etResults))
        //marksTIET?.addTextChangedListener(TW.CrossIconBehave(marksTIET))
        cgpaTIET?.addTextChangedListener(TW.CrossIconBehave(cgpaTIET))
        etScaleTIET?.addTextChangedListener(TW.CrossIconBehave(etScaleTIET))
        etPassignYear?.addTextChangedListener(TW.CrossIconBehave(etPassignYear))


        majorSubACTV?.addTextChangedListener(TW.CrossIconBehaveACTV(majorSubACTV))
        instituteNameACTV?.addTextChangedListener(TW.CrossIconBehaveACTV(instituteNameACTV))


        etAchievement?.addTextChangedListener(TW.CrossIconBehave(etAchievement))
        //etDuration?.addTextChangedListener(TW.CrossIconBehave(etDuration))


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


    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }


    private fun setDialog() {

        val institutes = ds.allInstitutes
        val majorSubjects = ds.allInMajorSubjects
        val boardNames: Array<String> = ds.allBoards
        val universityAdapter = ArrayAdapter<String>(activity,
                android.R.layout.simple_dropdown_item_1line, institutes)
        val mejorSubjectAdapter = ArrayAdapter<String>(activity,
                android.R.layout.simple_dropdown_item_1line, majorSubjects)


        majorSubACTV?.setAdapter(mejorSubjectAdapter)
        majorSubACTV?.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT
        instituteNameACTV?.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT

        if (instSuggession) {
            instituteNameACTV?.setAdapter(universityAdapter)

        } else {
            instituteNameACTV?.setAdapter(null)

        }

        etLevelEdu?.setOnClickListener {
            val eduLevelList: Array<String> = ds.allEduLevels
            activity.selector("Select level of education", eduLevelList.toList()) { _, i ->
                etLevelEdu.setText(eduLevelList[i])
                levelEduTIL.requestFocus()
                val eduLevel = ds.getEduIDByEduLevel(eduLevelList[i])
                etLevelEdu.setText(ds.getEduLevelByID(eduLevel))

                etExamTitle?.setText("")

                if (eduLevel.equalIgnoreCase("4") || eduLevel.equalIgnoreCase("5")) {

                    instituteNameACTV.setAdapter(universityAdapter)
                } else {

                    instituteNameACTV.setAdapter(null)
                }

                if (eduLevel.equalIgnoreCase("6")) {
                    examTitleTIL?.hide()
                    etExamTitle?.hide()
                    etExamOtherTitle?.clear()
                    examOtherTIL?.isErrorEnabled = false
                    examOtherTIL?.show()
                    etExamOtherTitle?.show()
                } else {
                    examTitleTIL?.show()
                    etExamTitle?.show()
                    examOtherTIL?.hide()
                    etExamOtherTitle?.hide()
                    etExamOtherTitle?.clear()
                }
                if (eduLevel.equalIgnoreCase("3") || eduLevel.equalIgnoreCase("4") || eduLevel.equalIgnoreCase("5") || eduLevel.equalIgnoreCase("6")) {
                    boardTIL?.hide()
                    etBoard.setText("")
                } else {
                    boardTIL?.show()
                }

                if (eduLevel.equalIgnoreCase("-3") || eduLevel.equalIgnoreCase("-2")) {

                    mejorTIL?.hide()
                    majorSubACTV?.hide()
                } else {

                    majorSubACTV?.clearText()
                    mejorTIL?.isErrorEnabled = false
                    majorSubACTV?.clearFocus()
                    mejorTIL?.show()
                    majorSubACTV?.show()

                }



                //Log.d("eduLevel", "eduLevel ID ${ds.getEduIDByEduLevel(eduLevelList[i])}")
            }
        }

        etBoard?.setOnClickListener {

            //Log.d("rakib", "${boardNames.size}")
            selector("Select board", boardNames.toList()) { _, i ->
                etBoard.setText(boardNames[i])
                //Log.d("rakib", "${ds.getBoardIDbyName(etBoard.text.toString())}")
            }


        }

        etExamTitle?.setOnClickListener {
            var queryValue = etLevelEdu.getString()
            queryValue = queryValue.replace("'", "''")
            val edulevelID = ds.getEduIDByEduLevel(queryValue)
            val examList: Array<String> = ds.getEducationDegreesByEduLevelID(edulevelID)
            activity.selector(getString(R.string.alert_exam_title), examList.toList()) { _, i ->
                etExamTitle?.setText(examList[i])
                examTitleTIL?.requestFocus()
                //Log.d("eduLevel", "ExamTitle ${examList[i]}")
                if (examList[i].equalIgnoreCase("Other")) {

                    examOtherTIL?.show()
                    etExamOtherTitle?.show()
                    etExamOtherTitle?.clear()
                    examOtherTIL.isErrorEnabled = false

                    if (!isEdit) {
                        etExamOtherTitle?.clear()
                        examOtherTIL?.isErrorEnabled = false

                    }

                    /* bcEduDegreeOtherTIET.visibility = View.VISIBLE*/
                } else {
                    examOtherTIL?.hide()
                    etExamOtherTitle?.hide()
                    etExamOtherTitle?.clear()

                }


            }
        }
        etResults?.setOnClickListener {
            //val examList: Array<String> = ds.getEducationDegreesByEduLevelID(edulevelID)
            val result: Array<String> = ds.allResults
            activity.selector(getString(R.string.alert_exam_result), result.toList()) { _, i ->
                etResults.setText(result[i])
                resultTIL.requestFocus()
                val examId = ds.getResultIDByResultName(result[i])


                if (examId.equalIgnoreCase("11") ||
                        examId.equalIgnoreCase("13") ||
                        examId.equalIgnoreCase("14") ||
                        examId.equalIgnoreCase("15")) {

                    cbResHide?.isChecked = false

                }


                setView(examId.toInt())
                //Log.d("eduLevel", "examId $examId")

                etResults?.setText(ds.getResultNameByResultID(examId))
                //Log.d("eduLevel", "eduLevel ${ds.getResultNameByResultID(examId)}")


            }
        }
        /*cbResHide.setOnCheckedChangeListener { _, isChecked ->

            //Log.d("eduLevel", "hide $hideRes")
        }*/
        cbForInstitute?.setOnCheckedChangeListener { _, isChecked ->
            foreignInstitute = if (isChecked) "1" else "0"
        }


        etPassignYear?.setOnClickListener {
            //            for (item in 1964..2024) {
//                yearList.add(item.toString())
//            }
//            activity.selector("Select Year of Passing", yearList.toList()) { _, i ->
//                etPassignYear?.setText(yearList[i])
//                acaPassingYearTIL?.requestFocus()
//            }

            try {
                var chosenYear = Calendar.getInstance().get(Calendar.YEAR)
                var currentYear = chosenYear
                if (isEdit) {
                    chosenYear = if (!etPassignYear?.text.toString().isNullOrEmpty())
                        etPassignYear?.text.toString().toInt()
                    else
                        eduCB.getData().yearofPAssing!!.toInt()
                } else {
                    if (!etPassignYear?.text.toString().isNullOrEmpty()) {
                        chosenYear = etPassignYear?.text.toString().toInt()
                    }
                }
                val builder = MonthPickerDialog.Builder(activity, MonthPickerDialog.OnDateSetListener { selectedMonth, selectedYear ->
                    chosenYear = selectedYear
                    etPassignYear?.setText(chosenYear.toString())
                }, chosenYear, 0)

                builder.showYearOnly()
                        .setYearRange(currentYear - 55, currentYear + 5)
                        .build()
                        .show()
            } catch (e: Exception) {

            }

        }


    }


    private fun validateLevelofEducation(): Boolean {
        if (etLevelEdu.getString().trim().isEmpty()) {
            levelEduTIL?.isErrorEnabled = true
            levelEduTIL?.error = resources.getString(R.string.field_empty_error_message_common)
            requestFocus(etLevelEdu)
            return false
        } else {
            levelEduTIL?.isErrorEnabled = false
            return true
        }


    }


    private fun validateBoard(): Boolean {
        if (etBoard.getString().trim().isEmpty()) {
            boardTIL?.isErrorEnabled = true
            boardTIL?.error = resources.getString(R.string.field_empty_error_message_common)
            requestFocus(etBoard)
            return false
        } else {
            boardTIL?.isErrorEnabled = false
            return true
        }


    }

    private fun validateExamDegreeTile(): Boolean {


        if (examTitleTIL.isVisible) {

            if (etExamTitle.getString().trim().isEmpty()) {
                examTitleTIL?.isErrorEnabled = true
                examTitleTIL?.error = resources.getString(R.string.field_empty_error_message_common)
                requestFocus(etExamTitle)
                return false
            } else {
                examTitleTIL?.isErrorEnabled = true
                return true
            }

        }

        return false

    }


    private fun validateExamDegreeTitleOther(): Boolean {
        if (etExamOtherTitle.isVisible) {
            if (etExamOtherTitle.getString().trim().isEmpty()) {
                examOtherTIL?.isErrorEnabled = true
                examOtherTIL?.error = resources.getString(R.string.field_empty_error_message_common)
                requestFocus(etExamOtherTitle)
                return false
            } else {
                examOtherTIL?.isErrorEnabled = false
                return true
            }

        }

        return false
    }


    private fun validateMajor(): Boolean {

        if (majorSubACTV.isVisible) {
            if (majorSubACTV.getString().trim().isEmpty()) {
                mejorTIL?.isErrorEnabled = true
                mejorTIL?.error = resources.getString(R.string.field_empty_error_message_common)
                requestFocus(majorSubACTV)
                return false
            } else {
                mejorTIL?.isErrorEnabled = false
                return true
            }

        }

        return false

    }

    private fun validateInstituteName(): Boolean {
        if (instituteNameACTV.getString().trim().isEmpty()) {
            instituteTIL?.isErrorEnabled = true
            instituteTIL?.error = resources.getString(R.string.field_empty_error_message_common)
            requestFocus(instituteNameACTV)
            return false
        } else {
            instituteTIL?.isErrorEnabled = false
            return true
        }

    }

    private fun validateResult(): Boolean {
        if (etResults.getString().trim().isEmpty()) {
            resultTIL?.isErrorEnabled = true
            resultTIL?.error = resources.getString(R.string.field_empty_error_message_common)
            requestFocus(etResults)
            return false
        } else {
            resultTIL?.isErrorEnabled = false
            return true
        }

    }

    private fun validatePassingYear(): Boolean {
        if (etPassignYear.getString().trim().isEmpty()) {
            acaPassingYearTIL?.isErrorEnabled = true
            acaPassingYearTIL?.error = resources.getString(R.string.field_empty_error_message_common)
            requestFocus(etPassignYear)
            return false
        } else {
            acaPassingYearTIL?.isErrorEnabled = false
            return true
        }

    }

    private fun validateMarks(): Boolean {
        if (marksTIL.isVisible) {
            if (marksTIET.getString().trim().isEmpty()) {
                marksTIL?.isErrorEnabled = true
                marksTIL?.error = resources.getString(R.string.field_empty_error_message_common)
                requestFocus(marksTIET)
                return false
            } else {

                try {
                    marks = marksTIET.getString().toDouble()
                } catch (e: NumberFormatException) {
                    logException(e)
                }

                if (marks > 100 || marks < 1) {

                    marksTIL?.showError("Please enter a valid marks")

                } else {
                    marksTIL?.isErrorEnabled = false
                    return true
                }

            }

        }
        return false
    }


    private fun validateScale(): Boolean {
        if (gradeLayout.isVisible) {
            var scale = 0f
            var cgpa = 0f
            if (!cgpaTIET.getString().trim().isEmpty()) {
                try {
                    cgpa = java.lang.Float.parseFloat(cgpaTIET.getString())
                } catch (ex: NumberFormatException) { // handle your exception
                    logException(ex)
                }

            }

            try {
                scale = java.lang.Float.parseFloat(etScaleTIET.getString())
            } catch (ex: NumberFormatException) { // handle your exception
                logException(ex)
            }

            when {
                etScaleTIET?.getString()!!.trim().isEmpty() -> {
                    scaleTIL?.isErrorEnabled = true
                    scaleTIL?.error = resources.getString(R.string.field_empty_error_message_common)
                    requestFocus(etScaleTIET)
                    return false
                }
                scale > 10.00 || scale < 1.00 -> {
                    scaleTIL?.isErrorEnabled = true
                    scaleTIL?.error = "Please enter a valid scale"
                    requestFocus(etScaleTIET)
                    return false
                }
                cgpa > scale -> {

                    if (!cgpaTIET.getString().trim().isEmpty()) {
                        activity.toast("CGPA can not be greater than Scale")
                        return false
                    }


                }

                else -> return true

            }

        }
        return false
    }

    private fun validateCgpa(): Boolean {
        if (gradeLayout.isVisible) {
            var scale = 0f
            var cgpa = 0f
            try {
                cgpa = java.lang.Float.parseFloat(cgpaTIET.getString())
            } catch (ex: NumberFormatException) { // handle your exception
                logException(ex)
            }
            if (!etScaleTIET.getString().trim().isEmpty()) {
                try {
                    scale = java.lang.Float.parseFloat(etScaleTIET.getString())
                } catch (ex: NumberFormatException) { // handle your exception
                    logException(ex)
                }

            }

            when {
                cgpaTIET.getString().trim().isEmpty() -> {
                    cGpaTIL?.isErrorEnabled = true
                    cGpaTIL?.error = resources.getString(R.string.field_empty_error_message_common)
                    requestFocus(cgpaTIET)
                    return false
                }
                cgpa > 10.00 || cgpa < 1.00 -> {
                    cGpaTIL?.isErrorEnabled = true
                    cGpaTIL?.error = "Please enter valid CGPA"
                    requestFocus(cgpaTIET)
                    return false
                }
                cgpa > scale -> {

                    if (!etScaleTIET.getString().trim().isEmpty()) {

                        activity.toast("CGPA can not be greater than Scale")
                        return false
                    }


                }
                else -> return true
            }
        }

        return false
    }

    private fun addTextChangedListener(editText: TextInputEditText, inputLayout: TextInputLayout) {
        editText.easyOnTextChangedListener { charSequence ->
            eduCB.validateField(charSequence.toString(), editText, inputLayout)
        }
    }

    private fun addTextChangedListenerMark(editText: TextInputEditText, inputLayout: TextInputLayout) {
        editText.easyOnTextChangedListener { charSequence ->
            marksValidation(charSequence.toString(), editText, inputLayout)
        }
    }

    private fun updateData() {

        if (etExamTitle.getString().equalIgnoreCase("Other")) {
            d("dgbdjhgbdg in other")
            examdegree = etExamOtherTitle.getString()
        } else if (etLevelEdu.getString().equalIgnoreCase("Doctoral")) {
            d("dgbdjhgbdg in Doctoral")
            d("dgbdjhgbdg $examdegree")

            if (etExamOtherTitle.isVisible) {

                examdegree = etExamOtherTitle.getString()

            }


            if (etExamTitle.isVisible) {

                examdegree = etExamTitle.getString()

            }


        } else {

            d("dgbdjhgbdg in else ")
            examdegree = etExamTitle.getString()
        }

        d("dgbdjhgbdg $examdegree")


        hideRes = if (cbResHide.isChecked) "1" else "0"
        foreignInstitute = if (cbForInstitute.isChecked) "1" else "0"
        if (cbResHide.isChecked) {
            gradeOrMarks = "0"
            scaleORCgpa = "0"
        }
        if (gradeLayout.isVisible) {
            gradeOrMarks = cgpaTIET.getString()
            scaleORCgpa = etScaleTIET.getString()
        } else if (marksLayout.isVisible) {
            scaleORCgpa = "0"
            gradeOrMarks = marksTIET.getString()
        }


        if (boardTIL.isVisible) {
            activity.showProgressBar(loadingProgressBar)
            if (!etBoard.text.toString().isNullOrEmpty()){
                val call = ApiServiceMyBdjobs.create().updateAcademicData(session.userId, session.decodId, session.IsResumeUpdate,
                        ds.getEduIDByEduLevel(etLevelEdu.getString()), examdegree, instituteNameACTV.getString(),
                        etPassignYear.getString(), majorSubACTV.getString(),
                        hID, foreignInstitute, "1", ds.getResultIDByResultName(etResults.getString()),
                        scaleORCgpa, gradeOrMarks, etDuration.getString(), etAchievement.getString(), hacaID, hideRes, boardId = if (ds.getBoardIDbyName(etBoard.text.toString()) == -1) "" else ds.getBoardIDbyName(etBoard.text.toString()).toString())

                call.enqueue(object : Callback<AddorUpdateModel> {
                    override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                        try {
                            activity?.stopProgressBar(loadingProgressBar)
                            activity?.toast(R.string.message_common_error)
                        } catch (e: Exception) {

                            logException(e)
                        }
                    }

                    override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {

                        try {
                            activity.stopProgressBar(loadingProgressBar)
                            if (response.isSuccessful) {
                                activity?.stopProgressBar(loadingProgressBar)
                                val resp = response.body()
                                activity?.toast(resp?.message.toString())
                                if (resp?.statuscode == "4") {
                                    eduCB.saveButtonClickStatus(true)
                                    eduCB.setBackFrom(acaUpdate)
                                    eduCB.goBack()
                                }
                            }
                        } catch (e: Exception) {
                            //activity.stopProgressBar(loadingProgressBar)
                            e.printStackTrace()
                            logException(e)
                        }
                    }
                })
            } else{
                activity.stopProgressBar(loadingProgressBar)
                toast("Board can not be empty")
            }

        } else {
            activity.showProgressBar(loadingProgressBar)
            val call = ApiServiceMyBdjobs.create().updateAcademicData(session.userId, session.decodId, session.IsResumeUpdate,
                    ds.getEduIDByEduLevel(etLevelEdu.getString()), examdegree, instituteNameACTV.getString(),
                    etPassignYear.getString(), majorSubACTV.getString(),
                    hID, foreignInstitute, "1", ds.getResultIDByResultName(etResults.getString()),
                    scaleORCgpa, gradeOrMarks, etDuration.getString(), etAchievement.getString(), hacaID, hideRes, boardId = if (ds.getBoardIDbyName(etBoard.text.toString()) == -1) "" else ds.getBoardIDbyName(etBoard.text.toString()).toString())

            call.enqueue(object : Callback<AddorUpdateModel> {
                override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                    try {
                        activity?.stopProgressBar(loadingProgressBar)
                        activity?.toast(R.string.message_common_error)
                    } catch (e: Exception) {

                        logException(e)
                    }
                }

                override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {

                    try {
                        activity.stopProgressBar(loadingProgressBar)
                        if (response.isSuccessful) {
                            activity?.stopProgressBar(loadingProgressBar)
                            val resp = response.body()
                            activity?.toast(resp?.message.toString())
                            if (resp?.statuscode == "4") {
                                eduCB.saveButtonClickStatus(true)
                                eduCB.setBackFrom(acaUpdate)
                                eduCB.goBack()
                            }
                        }
                    } catch (e: Exception) {
                        //activity.stopProgressBar(loadingProgressBar)
                        e.printStackTrace()
                        logException(e)
                    }
                }
            })
        }
    }

    fun dataDelete() {
        activity.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().deleteData("Education", hacaID, session.IsResumeUpdate!!, session.userId!!, session.decodId!!)
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                try {
                    activity?.toast(R.string.message_common_error)
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity?.stopProgressBar(loadingProgressBar)
                        val resp = response.body()
                        activity?.toast(resp?.message.toString())
                        clearEditText()
//                        eduCB.getAcademicList()?.let {
//                            for (item in it) {
//                                try {
//                                    if (item.acId!!.equalIgnoreCase(hacaID)) {
//                                        eduCB.getAcademicList()!!.remove(item)
//                                    }
//                                } catch (e: Exception) {
//                                }
//                            }
//                        }
                        eduCB.setBackFrom(acaUpdate)
                        eduCB.goBack()
                        //Log.d("rakib", "go back")
                    }
                } catch (e: Exception) {
                    //activity.stopProgressBar(loadingProgressBar)
                    //activity.toast(response.body()?.message.toString())
                    e.printStackTrace()
                    logException(e)
                }
            }
        })
    }

    private fun clearEditText() {
        etLevelEdu?.clear()
        etExamTitle?.clear()
        majorSubACTV?.setText("")
        instituteNameACTV?.setText("")
        etResults?.clear()
        cgpaTIET?.clear()
        etScaleTIET?.clear()
        etPassignYear?.clear()
        etDuration?.clear()
        etAchievement?.clear()
        cbResHide?.isChecked = false
        cbForInstitute?.isChecked = false


        levelEduTIL?.isErrorEnabled = false
        examTitleTIL?.isErrorEnabled = false
        mejorTIL?.isErrorEnabled = false
        instituteTIL?.isErrorEnabled = false
        resultTIL?.isErrorEnabled = false
        acaPassingYearTIL?.isErrorEnabled = false
        majorSubACTV?.clearFocus()
        instituteNameACTV?.clearFocus()

        etBoard?.clear()
        boardTIL.isErrorEnabled = false

    }

    private fun setView(passvalue: Int) {
        val value = if (passvalue == 13 || passvalue == 14 || passvalue == 15)
            100
        else passvalue

        when (value) {
            100 -> {
                cbResHide?.show()
                if (cbResHide.isChecked) {
                    llResultFields?.hide()
                    marksLayout?.hide()
                    gradeLayout?.hide()
                } else {
                    llResultFields?.show()
                    gradeLayout?.hide()
                    marksLayout?.show()
                    marksTIET?.clear()
                    marksTIL?.isErrorEnabled = false
                }

            }
            11 -> {

                cbResHide?.show()
                if (cbResHide.isChecked) {

                    llResultFields?.hide()
                    gradeLayout?.hide()
                    marksLayout?.hide()

                } else {


                    llResultFields?.show()
                    gradeLayout?.show()
                    marksLayout?.hide()
                    cgpaTIET?.clear()
                    etScaleTIET?.clear()
                    cGpaTIL?.isErrorEnabled = false
                    scaleTIL?.isErrorEnabled = false
                    etScaleTIET?.clearFocus()


                }
            }
            0 -> {
                cbResHide?.hide()
                llResultFields?.hide()
                marksLayout?.hide()
                gradeLayout?.hide()

            }

            else -> {
                llResultFields?.hide()
                marksLayout?.hide()
                gradeLayout?.hide()
                cbResHide?.hide()


            }
        }
    }


}

