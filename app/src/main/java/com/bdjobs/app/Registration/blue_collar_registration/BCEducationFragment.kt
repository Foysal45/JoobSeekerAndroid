package com.bdjobs.app.Registration.blue_collar_registration

import android.app.Fragment
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.callHelpLine
import com.bdjobs.app.Utilities.getString
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

                /*  toast(isChecked.toString())*/

                if (isChecked) {
                    hasEducation = "True"
                    /* nextButton.setEnabled(false)
                     nextButton.setClickable(false)
                     nextButton.setBackgroundResource(R.drawable.next_button_inactive)*/
                  /*  bcEduLevelTIET.isFocusable = true
                    bcEduDegreeTIET.isFocusable = true
                    bcInstituteNameTIET.isFocusable = true
                    bcPassingYearTIET.isFocusable = true


                    bcEduLevelTIET.isEnabled = true
                    bcEduDegreeTIET.isEnabled = true
                    bcInstituteNameTIET.isEnabled = true
                    bcPassingYearTIET.isEnabled = true*/

                    bcEduLevelTIL.isFocusable = true
                    bcEduDegreeTIL.isFocusable = true
                    bcInstituteNameTIL.isFocusable = true
                    bcPassingYearTIL.isFocusable = true

                    bcEduLevelTIL.isEnabled = true
                    bcEduDegreeTIL.isEnabled = true
                    bcInstituteNameTIL.isEnabled = true
                    bcPassingYearTIL.isEnabled = true



                 /*    eduDegreeOtherET.setVisibility(View.GONE)*/


                    bcInstituteNameTIET.clearFocus()
                    bcPassingYearTIET.clearFocus()
                     /*  registrationCommunicator.setEducationType("")*/

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

                   /* bcEduLevelTIL.isFocusable = false
                    bcEduDegreeTIL.isFocusable = false
                    bcInstituteNameTIL.isFocusable = false
                    bcPassingYearTIL.isFocusable = false

                    bcEduLevelTIL.isEnabled = false
                    bcEduDegreeTIL.isEnabled = false
                    bcInstituteNameTIL.isEnabled = false
                    bcPassingYearTIL.isEnabled = false*/
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





    fun callHelpLine(context: Context) {
        try {
            val my_callIntent = Intent(Intent.ACTION_DIAL)
            my_callIntent.data = Uri.parse("tel:" + 16479)
            //here the word 'tel' is important for making a call...
            context.startActivity(my_callIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Error in your phone call" + e.message, Toast.LENGTH_LONG).show()
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

}
