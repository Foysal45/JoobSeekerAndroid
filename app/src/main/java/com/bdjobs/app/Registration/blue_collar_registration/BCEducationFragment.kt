package com.bdjobs.app.Registration.blue_collar_registration

import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CompoundButton
import androidx.core.view.isVisible
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.footer_bc_layout.*
import kotlinx.android.synthetic.main.fragment_bc_education.*
import org.jetbrains.anko.selector


class BCEducationFragment : Fragment() {


    private lateinit var registrationCommunicator: RegistrationCommunicator
    private var hasEducation = "False"
    private lateinit var dataStorage: DataStorage
    private lateinit var eduDegree: String
    private lateinit var levelOfEducation: String
    private lateinit var passingYear: String
    private lateinit var instituteName: String
    private lateinit var educationType: String
    private lateinit var returnView:View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

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


            val eduLevel = bcEduLevelTIET.text.toString()
            eduDegree = bcEduDegreeTIET.text.toString()

            instituteName = bcInstituteNameTIET.text.toString()
            passingYear = bcPassingYearTIET.text.toString()
            educationType = dataStorage.getEducationTypeByEducationDegreeName(eduDegree)
            levelOfEducation = dataStorage.getEduIDByEduLevel(eduLevel)
            Log.d("MobileNumberVer2", " EducationType in database $educationType")
            Log.d("MobileNumberVer2", " levelOfEducation in database $levelOfEducation")

            if (eduDegree.equals("Other", ignoreCase = true)) {

                eduDegree = bcEduDegreeOtherTIET.text.toString()
                educationType = "5"


            }
            Log.d("ConditionCheck"," hasEducation $hasEducation ")

            if (hasEducation.equals("False",true)){

                Log.d("ConditionCheck"," First Condition ")

                registrationCommunicator.bcGoToStepPhotoUpload(hasEducation)


            } else {

                checkValidity()

                if (validateCondition() || validateConditionTwo()) {
                    Log.d("ConditionCheck"," second Condition ")
                    registrationCommunicator.bcEducationSelected(levelOfEducation, eduDegree, instituteName, passingYear, educationType)
                    registrationCommunicator.bcGoToStepPhotoUpload(hasEducation)
                }


            }




        }

        bcEduLevelTIET.setOnClickListener {
            val eduLevelList: Array<String> = dataStorage.allEduLevels

            selector("সর্বশেষ শিক্ষা পর্যায়", eduLevelList.toList()) { dialogInterface, i ->

               bcEduLevelTIET.setText(eduLevelList[i])
               /* bcDistrictTIL.requestFocus()*/


            }


        }

        bcEduDegreeTIET.setOnClickListener {

            var queryValue = bcEduLevelTIET.text.toString()
            queryValue = queryValue.replace("'", "''")
            val edulevelID = dataStorage.getEduIDByEduLevel(queryValue)
            val eduDegreeList: Array<String> = dataStorage.getEducationDegreesByEduLevelID(edulevelID)

            selector("পরীক্ষা/ডিগ্রীর নাম", eduDegreeList.toList()) { dialogInterface, i ->

               bcEduDegreeTIET.setText(eduDegreeList[i])


                if (eduDegreeList[i].equals("Other")){

                    bcEduDegreeOtherTIL.visibility = View.VISIBLE
                    bcEduDegreeOtherTIET.visibility = View.VISIBLE
                }


                /* bcDistrictTIL.requestFocus()*/


            }


        }

        checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

                if (isChecked) {
                    hasEducation = "True"


                    bcEduLevelTIL.isEnabled = true
                    bcEduDegreeTIL.isEnabled = true
                    bcInstituteNameTIL.isEnabled = true
                    bcPassingYearTIL.isEnabled = true


                    bcInstituteNameTIET.clearFocus()
                    bcPassingYearTIET.clearFocus()


                } else {
                    hasEducation = "False"

                    bcEduLevelTIET.text!!.clear()
                    bcEduDegreeTIET.text!!.clear()
                    bcInstituteNameTIET.text!!.clear()
                    bcPassingYearTIET.text!!.clear()

                    bcEduLevelTIET.isFocusable = false
                    bcEduDegreeTIET.isFocusable = false
                    bcInstituteNameTIET.isFocusable = false
                    bcPassingYearTIET.isFocusable = false


                    bcEduLevelTIET.isEnabled = false
                    bcEduDegreeTIET.isEnabled = false
                    bcInstituteNameTIET.isEnabled = false
                    bcPassingYearTIET.isEnabled = false

                    bcEduDegreeTIL.visibility = View.GONE
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

        bcEduLevelTIET.easyOnTextChangedListener { charSequence ->
            educationValidation(charSequence.toString(), bcEduLevelTIET, bcEduLevelTIL, "")
        }

        bcEduDegreeTIET.easyOnTextChangedListener { charSequence ->

            educationValidation(charSequence.toString(), bcEduDegreeTIET, bcEduDegreeTIL, "")


        }


        bcInstituteNameTIET.easyOnTextChangedListener { charSequence ->

            educationValidation(charSequence.toString(), bcInstituteNameTIET, bcInstituteNameTIL, "it is too short")

        }


        bcPassingYearTIET.easyOnTextChangedListener { charSequence ->


            educationValidation(charSequence.toString(), bcPassingYearTIET, bcPassingYearTIL, "it is too short")

        }


        bcEduDegreeOtherTIET.easyOnTextChangedListener { charSequence ->


            educationValidation(charSequence.toString(), bcEduDegreeOtherTIET, bcEduDegreeOtherTIL, "it is too short")

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
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }




    private fun validateCondition(): Boolean {


        val passingYear = bcPassingYearTIET.getString()
        var passingYearInt = 0
        try {

            passingYearInt = Integer.parseInt(passingYear)

        } catch (e: Exception) {


        }

        return !TextUtils.isEmpty(bcEduLevelTIET.getString()) and
                !TextUtils.isEmpty(bcEduDegreeTIET.getString()) and
                !TextUtils.isEmpty(bcInstituteNameTIET.getString()) and
                ((bcPassingYearTIET.getString().length > 3) and (
                        passingYearInt <= 2023) and (passingYearInt >= 1963))
    }


    private fun validateConditionTwo(): Boolean {


        val passingYear = bcPassingYearTIET.getString()
        var passingYearInt = 0
        try {

            passingYearInt = Integer.parseInt(passingYear)

        } catch (e: Exception) {


        }

        return !TextUtils.isEmpty(bcEduLevelTIET.getString()) and
                !TextUtils.isEmpty(bcEduDegreeTIET.getString()) and
                !TextUtils.isEmpty(bcInstituteNameTIET.getString()) and
                !TextUtils.isEmpty(bcEduDegreeOtherTIET.getString()) and
                ((bcPassingYearTIET.getString().length > 3) and (
                        passingYearInt <= 2023) and (passingYearInt >= 1963))
    }


    private fun checkValidity() {


        if (TextUtils.isEmpty(bcEduLevelTIET.getString())) {

            bcEduLevelTIL.showError("সর্বশেষ শিক্ষা পর্যায় নির্বাচন করুন")

        } else {
            bcEduLevelTIL.isErrorEnabled = false


        }

        if (TextUtils.isEmpty(bcEduDegreeTIET.getString())) {

            bcEduDegreeTIL.showError("পরীক্ষা/ডিগ্রীর নাম নির্বাচন করুন")

        } else {

            bcEduDegreeTIL.isErrorEnabled = false


        }

        if (TextUtils.isEmpty(bcInstituteNameTIET.getString())) {

            bcInstituteNameTIL.showError("শিক্ষা প্রতিষ্ঠানের  নাম লিখুন")

        } else {
            bcInstituteNameTIL.isErrorEnabled = false


        }


        if (TextUtils.isEmpty(bcPassingYearTIET.getString())) {

            bcPassingYearTIL.showError("পাশ করার বছর িখুন")

        } else {

            bcPassingYearTIL.isErrorEnabled = false


        }





        if (bcEduDegreeOtherTIL.isVisible) {

            if (TextUtils.isEmpty(bcEduDegreeOtherTIET.getString())) {

                bcEduDegreeOtherTIL.showError("পরীক্ষা/ডিগ্রীর নাম লিখুন")

            } else {

                bcEduDegreeOtherTIL.isErrorEnabled = false


            }

        }


    }


}
