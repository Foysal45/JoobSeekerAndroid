package com.bdjobs.app.Registration.blue_collar_registration

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import com.bdjobs.app.Databases.External.DataStorage

import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import kotlinx.android.synthetic.main.fragment_bc_education.*
import kotlinx.android.synthetic.main.fragment_bc_mobile_number.*
import org.jetbrains.anko.sdk27.coroutines.textChangedListener
import com.bdjobs.app.R.id.checkBox
import org.jetbrains.anko.toast
import java.util.*


class BCEducationFragment : Fragment() {


    private lateinit var registrationCommunicator: RegistrationCommunicator
    private var hasEducation = true
    private lateinit var dataStorage: DataStorage

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bc_education, container, false)
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

            registrationCommunicator.bcGoToStepPhotoUpload()


        }



        checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

                toast(isChecked.toString())

                if (isChecked) {
                    hasEducation = true
                    /* nextButton.setEnabled(false)
                     nextButton.setClickable(false)
                     nextButton.setBackgroundResource(R.drawable.next_button_inactive)*/
                    bcLastEducationTIET.isFocusable = true
                    bcExamNameTIET.isFocusable = true
                    bcInstituteNameTIET.isFocusable = true
                    bcPassingYearTIET.isFocusable = true


                    bcLastEducationTIET.isEnabled = true
                    bcExamNameTIET.isEnabled = true
                    bcInstituteNameTIET.isEnabled = true
                    bcPassingYearTIET.isEnabled = true

                    bcLastEducationTIL.isFocusable = true
                    bcExamNameTIL.isFocusable = true
                    bcInstituteNameTIL.isFocusable = true
                    bcPassingYearTIL.isFocusable = true

                    bcLastEducationTIL.isEnabled = true
                    bcExamNameTIL.isEnabled = true
                    bcInstituteNameTIL.isEnabled = true
                    bcPassingYearTIL.isEnabled = true



                 /*    eduDegreeOtherET.setVisibility(View.GONE)*/


                    bcInstituteNameTIET.clearFocus()
                    bcPassingYearTIET.clearFocus()
                     /*  registrationCommunicator.setEducationType("")*/

                } else {
                    hasEducation = false
                   /*  nextButton.setEnabled(true)
                     nextButton.setClickable(true)
                     nextButton.setBackgroundResource(R.drawable.next_button_active)*/
                    bcLastEducationTIET.text!!.clear()
                    bcExamNameTIET.text!!.clear()
                    bcInstituteNameTIET.text!!.clear()
                    bcPassingYearTIET.text!!.clear()

                    bcLastEducationTIET.isFocusable = false
                    bcExamNameTIET.isFocusable = false
                    bcInstituteNameTIET.isFocusable = false
                    bcPassingYearTIET.isFocusable = false


                    bcLastEducationTIET.isEnabled = false
                    bcExamNameTIET.isEnabled = false
                    bcInstituteNameTIET.isEnabled = false
                    bcPassingYearTIET.isEnabled = false

                    bcLastEducationTIL.isFocusable = false
                    bcExamNameTIL.isFocusable = false
                    bcInstituteNameTIL.isFocusable = false
                    bcPassingYearTIL.isFocusable = false

                    bcLastEducationTIL.isEnabled = false
                    bcExamNameTIL.isEnabled = false
                    bcInstituteNameTIL.isEnabled = false
                    bcPassingYearTIL.isEnabled = false
                    /*  eduDegreeOtherET.setVisibility(View.GONE)*/
                }


            }

        })


    }


    private fun setDialog(title: String, editText: EditText, data: Array<String>) {

        editText.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(title)
                    .setItems(data
                    ) { dialog, which ->
                        editText.setText(data[which])


                        if (data[which].equals("Other", ignoreCase = true)) {

                          /*  eduDegreeOtherET.setVisibility(View.VISIBLE)
                            eduDegreeOtherET.getText().clear()
                            registrationCommunicator.setEducationType("5")*/

                        } else {


                           /* eduDegreeOtherET.setVisibility(View.GONE)*/
                        }

                        if (editText.id == R.id.bcLastEducationTIET) {
                            bcExamNameTIET.text!!.clear()
                            bcLastEducationTIET.setOnClickListener(null)
                            var queryValue = editText.text.toString()
                            queryValue = queryValue.replace("'", "''")
                            val edulevelID = dataStorage.getEduIDByEduLevel(queryValue)
                            setDialog("পরীক্ষা/ডিগ্রীর নাম", bcExamNameTIET, dataStorage.getEducationDegreesByEduLevelID(edulevelID))
                        }
                    }
            val dialog = builder.create()
            dialog.show()


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


    override fun onResume() {
        super.onResume()

       /* val eduLevels = dataStorage.allEduLevels()
        setDialog("সর্বশেষ শিক্ষা পর্যায়", eduLevelET, Arrays.copyOf<String>(eduLevels, eduLevels.size - 1))

        if (!TextUtils.isEmpty(eduLevelET.getText().toString())) {
            var queryValue = eduLevelET.getText().toString()
            queryValue = queryValue.replace("'", "''")
            val edulevelID = dataStorage.getEduIDByEduLevel(queryValue)
            setDialog("পরীক্ষা/ডিগ্রীর নাম", eduDegreeET, dataStorage.getEducationDegreesByEduLevelID(edulevelID))
        }

        Log.d("ExceptionTest", " Education Type " + registrationCommunicator.getEducationType())

        try {

            if (registrationCommunicator.getEducationType().equals("5")) {

                Log.d("ExceptionTest", " In If Condition ")

                eduDegreeOtherET.setVisibility(View.VISIBLE)
            }

        } catch (e: Exception) {

            Log.d("ExceptionTest", " Exception " + e.message)


        }*/

    }
}
