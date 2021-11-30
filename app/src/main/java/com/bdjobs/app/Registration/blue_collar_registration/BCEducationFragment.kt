package com.bdjobs.app.Registration.blue_collar_registration

import android.app.AlertDialog
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import androidx.core.view.isVisible
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.databases.External.DataStorage
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.footer_bc_layout.*
import kotlinx.android.synthetic.main.fragment_bc_education.*
import org.jetbrains.anko.selector
import java.util.*
import kotlin.collections.ArrayList


class BCEducationFragment : Fragment() {


    private lateinit var registrationCommunicator: RegistrationCommunicator
    private var hasEducation = "True"
    private lateinit var dataStorage: DataStorage
    private lateinit var eduDegree: String
    private lateinit var levelOfEducation: String
    private lateinit var passingYear: String
    private lateinit var instituteName: String
    private lateinit var board: String
    private var educationType = ""
    private lateinit var returnView: View
    private var yearList = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        returnView = inflater.inflate(R.layout.fragment_bc_education, container, false)
        return returnView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialization()
        onClick()
    }


    private fun initialization() {

        registrationCommunicator = activity as RegistrationCommunicator
        dataStorage = DataStorage(activity)
        addTextChangedListener()

    }

    private fun onClick() {
        bcEducationFAButton.setOnClickListener {
            val eduLevel = bcEduLevelTIET?.text.toString()
            eduDegree = bcEduDegreeTIET?.text.toString()
            instituteName = bcInstituteNameTIET?.text.toString()
            passingYear = bcPassingYearTIET?.text.toString()
            educationType = dataStorage.getEducationTypeByEducationDegreeName(eduDegree)
            levelOfEducation = dataStorage.getEduIDByEduLevel(eduLevel)
            board = dataStorage.getBoardIDbyName(bcEduBoardTIET.text.toString()).toString()


            if (board == "-1") board = "0" else board

            //Log.d("rakib", "click $board")

            //Log.d("rakib", "check $levelOfEducation $eduDegree $instituteName $passingYear 1 board $board")
            //Log.d("MobileNumberVer2", " EducationType in database $educationType")
            //Log.d("MobileNumberVer2", " levelOfEducation in database $levelOfEducation")
            if (eduDegree.equals("Other", ignoreCase = true)) {

                eduDegree = bcEduDegreeOtherTIET.text.toString()
                educationType = "5"
            }
            //Log.d("ConditionCheck", " hasEducation $hasEducation ")
            if (hasEducation.equals("False", true)) {

                //Log.d("ConditionCheck", " First Condition ")
                registrationCommunicator.bcEducationSelected("0", eduDegree, instituteName, "0", "0", board)
                registrationCommunicator.bcGoToStepPhotoUpload(hasEducation)
            } else {
                bcEducationFAButton.hideKeyboard()
                checkValidity()
                //Log.d("ConditionCheck", " validateCondition ${validateCondition()} 2nd ${validateConditionTwo()}")
                if (validateCondition() || validateConditionTwo()) {
                    //Log.d("ConditionCheck", " second Condition ")
                    registrationCommunicator.bcEducationSelected(levelOfEducation, eduDegree, instituteName, passingYear, "1", board)
                    registrationCommunicator.bcGoToStepPhotoUpload(hasEducation)
//                    Log.d("rakib","$board")
                }
            }
        }

        bcPassingYearTIET?.setOnClickListener {
            for (item in 1964..2024) {
                yearList.add(item.toString())
            }
            activity.selector("পাশ করার বছর ", yearList.toList()) { dialogInterface, i ->
                try {
                    bcPassingYearTIET?.setText(yearList[i])
                    bcPassingYearTIL?.requestFocus()
                } catch (e: Exception) {
                    logException(e)
                }
            }
        }

        checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    hasEducation = "True"
                    bcEduLevelTIET.isEnabled = true
                    bcEduDegreeTIET.isEnabled = true
                    bcInstituteNameTIET.isEnabled = true
                    bcPassingYearTIET.isEnabled = true
                    bcEduBoardTIET.isEnabled = true
                    bcEduDegreeOtherTIL.visibility = View.GONE
                    bcEduDegreeOtherTIET.visibility = View.GONE
                    bcInstituteNameTIET.clearFocus()
                    bcInstituteNameTIL.isErrorEnabled = false
                    bcPassingYearTIL.isErrorEnabled = false
                    educationType = ""

                } else {
                    hasEducation = "False"
                    bcEduLevelTIET.clear()
                    bcEduDegreeTIET.clear()
                    bcInstituteNameTIET.clear()
                    bcPassingYearTIET.clear()
                    bcEduBoardTIET?.clear()
                    bcEduLevelTIET.isEnabled = false
                    bcEduDegreeTIET.isEnabled = false
                    bcInstituteNameTIET.isEnabled = false
                    bcPassingYearTIET.isEnabled = false
                    bcEduDegreeOtherTIL.visibility = View.GONE
                    bcEduDegreeOtherTIET.visibility = View.GONE
                    bcEduBoardTIET?.isEnabled = false
                    bcEduDegreeTIL.isErrorEnabled = false
                    bcEduLevelTIL.isErrorEnabled = false
                    bcInstituteNameTIL.isErrorEnabled = false
                    bcPassingYearTIL.isErrorEnabled = false
                    bcEduBoardTIET.clear()
                    bcEduBoardTIL.isErrorEnabled = false
                }


            }

        })
        supportTextView.setOnClickListener {
            activity.callHelpLine()
        }
        bcHelpLineLayout.setOnClickListener {
            activity.callHelpLine()
        }
    }


    private fun addTextChangedListener() {
        bcEduLevelTIET?.easyOnTextChangedListener { charSequence ->
            educationValidation(charSequence.toString(), bcEduLevelTIET, bcEduLevelTIL, "")
        }
        bcEduDegreeTIET?.easyOnTextChangedListener { charSequence ->
            educationValidation(charSequence.toString(), bcEduDegreeTIET, bcEduDegreeTIL, "")
        }
        bcInstituteNameTIET?.easyOnTextChangedListener { charSequence ->
            educationValidation(charSequence.toString(), bcInstituteNameTIET, bcInstituteNameTIL, "শিক্ষা প্রতিষ্ঠানের  নাম লিখুন")
        }
        bcPassingYearTIET?.easyOnTextChangedListener { charSequence ->
            educationValidation(charSequence.toString(), bcPassingYearTIET, bcPassingYearTIL, "")
        }
        bcEduDegreeOtherTIET?.easyOnTextChangedListener { charSequence ->
            educationValidation(charSequence.toString(), bcEduDegreeOtherTIET, bcEduDegreeOtherTIL, "পরীক্ষা/ডিগ্রীর নাম লিখুন")

        }

        bcEduBoardTIET?.easyOnTextChangedListener { charSequence ->
            educationValidation(charSequence.toString(), bcEduBoardTIET, bcEduBoardTIL, "বোর্ডের নাম লিখুন")
        }


    }


    private fun educationValidation(char: String, et: TextInputEditText, til: TextInputLayout, message: String): Boolean {
        when {
            TextUtils.isEmpty(char) -> {
                til.showError(message)
                requestFocus(et)
                return false
            }
            else -> til.hideError()
        }
        return true
    }


    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }


    private fun validateCondition(): Boolean {
        if (!bcEduDegreeOtherTIET.isVisible && !bcEduDegreeOtherTIL.isVisible) {
            val passingYear = bcPassingYearTIET.getString()
            var passingYearInt = 0
            try {
                passingYearInt = Integer.parseInt(passingYear)
            } catch (e: Exception) {
                logException(e)
            }
            return !TextUtils.isEmpty(bcEduLevelTIET.getString()) and
                    !TextUtils.isEmpty(bcEduDegreeTIET.getString()) and
                    !TextUtils.isEmpty(bcInstituteNameTIET.getString()) and
                    ((bcPassingYearTIET.getString().length > 3) and (
                            passingYearInt <= 2024) and (passingYearInt >= 1964))
        } else return false
    }


    private fun validateConditionTwo(): Boolean {
        val passingYear = bcPassingYearTIET.getString()
        var passingYearInt = 0
        try {
            passingYearInt = Integer.parseInt(passingYear)
        } catch (e: Exception) {
            logException(e)
        }
        return !TextUtils.isEmpty(bcEduLevelTIET.getString()) and
                !TextUtils.isEmpty(bcEduDegreeTIET.getString()) and
                !TextUtils.isEmpty(bcInstituteNameTIET.getString()) and
                !TextUtils.isEmpty(bcEduDegreeOtherTIET.getString()) and
                ((bcPassingYearTIET.getString().length > 3) and (
                        passingYearInt <= 2024) and (passingYearInt >= 1964))
    }

    private fun checkValidity() {

        if (TextUtils.isEmpty(bcEduLevelTIET.getString())){
            bcEduLevelTIL?.showError("সর্বশেষ শিক্ষা পর্যায় নির্বাচন করুন")

        }else if (TextUtils.isEmpty(bcEduDegreeTIET.getString())){
            bcEduDegreeTIL?.showError("পরীক্ষা/ডিগ্রীর নাম নির্বাচন করুন")

        }else if(bcEduDegreeOtherTIL.isVisible && TextUtils.isEmpty(bcEduDegreeOtherTIET.getString())){
            if (TextUtils.isEmpty(bcEduDegreeOtherTIET.getString())) {
                bcEduDegreeOtherTIL?.showError("পরীক্ষা/ডিগ্রীর নাম লিখুন")
                requestFocus(bcEduDegreeOtherTIET)
            } else {
                bcEduDegreeOtherTIL?.isErrorEnabled = false
            }
        }else if (bcEduBoardTIL.isVisible && TextUtils.isEmpty(bcEduBoardTIET.getString())){
            if (TextUtils.isEmpty(bcEduBoardTIET.getString())) {
                bcEduBoardTIL?.showError("বোর্ডের নাম লিখুন")
                requestFocus(bcEduBoardTIET)
            } else {
                bcEduBoardTIL?.isErrorEnabled = false
            }
        }else if (TextUtils.isEmpty(bcInstituteNameTIET.getString())){

            bcInstituteNameTIL?.showError("শিক্ষা প্রতিষ্ঠানের  নাম লিখুন")
            requestFocus(bcInstituteNameTIET)

        }else if (TextUtils.isEmpty(bcPassingYearTIET.getString())){

            bcPassingYearTIL?.showError("পাশ করার বছর লিখুন")
            requestFocus(bcPassingYearTIET)

        }
        else{
            bcEduLevelTIL?.isErrorEnabled = false
            bcEduDegreeTIL?.isErrorEnabled = false
            bcInstituteNameTIL?.isErrorEnabled = false
            bcPassingYearTIL?.isErrorEnabled = false
        }


    }


    override fun onResume() {
        super.onResume()

        bcInstituteNameTIL?.isErrorEnabled = false
        val eduLevels = dataStorage.allEduLevels
        setDialog("সর্বশেষ শিক্ষা পর্যায়", bcEduLevelTIET, Arrays.copyOf<String>(eduLevels, eduLevels.size))

        val allBoards =  dataStorage.allBoards.apply {
            this.forEach {
                it.trim()
                Log.d("rakib","$it\n")
            }
        }


        try {
            setDialog("বোর্ড", bcEduBoardTIET, Arrays.copyOf(allBoards, allBoards.size))
        } catch (e: Exception) {
        }

        if (!TextUtils.isEmpty(bcEduLevelTIET.getString())) {
            var queryValue = bcEduLevelTIET.getString()
            queryValue = queryValue.replace("'", "''")
            val edulevelID = dataStorage.getEduIDByEduLevel(queryValue)
            //Log.d("rakib", "edu level id $edulevelID")
            setDialog("পরীক্ষা/ডিগ্রীর নাম", bcEduDegreeTIET, dataStorage.getEducationDegreesByEduLevelID(edulevelID))

            if (edulevelID == "3" || edulevelID == "4" || edulevelID == "5" || edulevelID == "6") {
                bcEduBoardTIET?.clear()
                bcEduBoardTIL?.visibility = View.GONE
                bcEduDegreeTIL.isErrorEnabled = false
                board = "0"
                //Log.d("rakib", "onresume if $board")

            } else {
                board = dataStorage.getBoardIDbyName(bcEduBoardTIET?.text?.toString()).toString()
                bcEduBoardTIL?.visibility = View.VISIBLE
                bcEduDegreeTIL.isErrorEnabled = false
                //Log.d("rakib", "onresume else $board")

            }
        }
        try {
            if (bcEduDegreeTIET?.text.toString().equalIgnoreCase("Other")) {
                bcEduDegreeOtherTIET?.show()
            }
            if (educationType == "5") {
                //Log.d("ExceptionTest", " In If Condition ")
                bcEduDegreeOtherTIET?.show()
            }
        } catch (e: Exception) {
            //Log.d("ExceptionTest", " Exception " + e.message)
        }

    }

    private fun setDialog(title: String, editText: TextInputEditText, data: Array<String>) {
        editText.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(title)
                    .setItems(data
                    ) { dialog, which ->
                        editText.setText(data[which].trim())

                        if (title.equals("পরীক্ষা/ডিগ্রীর নাম")){
                            if (data[which].equals("Other", ignoreCase = true)) {
                              //  Toast.makeText(activity, "other = show", Toast.LENGTH_SHORT).show()
                                bcEduDegreeOtherTIET?.show()
                                bcEduDegreeOtherTIL?.show()
                                bcEduDegreeOtherTIET?.clear()
                                bcEduDegreeTIL?.isErrorEnabled = false
                                bcEduDegreeOtherTIL?.isErrorEnabled = false
                                /* registrationCommunicator.setEducationType("5")*/
                            }
                            else {
                                bcEduDegreeOtherTIET?.hide()
                                bcEduDegreeOtherTIL?.hide()
                            }
                        }


                        if (editText.id == R.id.bcEduLevelTIET) {

                            bcInstituteNameTIL?.isErrorEnabled = false
                            bcPassingYearTIL?.isErrorEnabled = false


                            bcEduDegreeTIET?.clear()
                            bcPassingYearTIET?.clear()
                            bcEduDegreeTIET?.setOnClickListener(null)
                            var queryValue = editText.text.toString()
                            queryValue = queryValue.replace("'", "''")
                            val edulevelID = dataStorage.getEduIDByEduLevel(queryValue)
                            if (edulevelID == "3" || edulevelID == "4" || edulevelID == "5" || edulevelID == "6") {
                                bcEduBoardTIL?.visibility = View.GONE
                                bcEduDegreeTIL.isErrorEnabled = false
                                bcEduBoardTIET?.clear()
                                board = "0"
                                //Log.d("rakib", "set dialog if $board")

                            } else {
                                board = dataStorage.getBoardIDbyName(bcEduBoardTIET?.text?.toString()).toString()
                                bcEduBoardTIL?.visibility = View.VISIBLE
                                bcEduDegreeTIL.isErrorEnabled = false
                                //Log.d("rakib", "set dialog else $board")



                            }
                            setDialog("পরীক্ষা/ডিগ্রীর নাম", bcEduDegreeTIET, dataStorage.getEducationDegreesByEduLevelID(edulevelID))

                        }


                    }
            val dialog = builder.create()
            dialog.show()
        }
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}
