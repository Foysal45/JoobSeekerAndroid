package com.bdjobs.app.Registration.blue_collar_registration

import android.app.DatePickerDialog
import android.os.Bundle
import android.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker

import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import kotlinx.android.synthetic.main.footer_bc_layout.*
import kotlinx.android.synthetic.main.fragment_bc_birth_date.*
import org.jetbrains.anko.makeCall
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class BCBirthDateFragment : Fragment() {

    lateinit var datePickerDialog: DatePickerDialog
    private var calendar: Calendar? = null
    private lateinit var registrationCommunicator :RegistrationCommunicator
    internal var age = 0
    internal var birthdate: String? = null
    private var ageLimit = false
    private lateinit var returnView:View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        returnView = inflater.inflate(R.layout.fragment_bc_birth_date, container, false)
        return returnView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        intialization()
        onClick()

    }

    override fun onResume() {
        super.onResume()

        Log.d("birthd", "onResumeCalled")
        if (!TextUtils.isEmpty(birthdate)) {
            bcBirthDateTIET.setText(birthdate)
            bcAgeTIET.text!!.clear()
            ageLimit = true

        } else if (!TextUtils.isEmpty(registrationCommunicator.bcGetAge())) {
            Log.d("birthd", "age: " + registrationCommunicator.bcGetAge())
            age = Integer.parseInt(registrationCommunicator.bcGetAge())
            if (age >= 12) {
                bcAgeTIET.setText(age.toString())
                bcBirthDateTIET.text!!.clear()
               ageLimit = true

            }
        }

    }

    private fun onClick() {

        bcBirthDateFAButton.setOnClickListener {


            if (!TextUtils.isEmpty(bcBirthDateTIET.text.toString())) {

                birthdate = bcBirthDateTIET.text.toString()


                Log.d("Test"," birthDtae ${birthdate}")

                    val sdf = SimpleDateFormat("dd/MM/yyyy")
                    try {
                        val birthDate = sdf.parse(birthdate)
                        age = calculateAge(birthDate)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }


            } else {


                    birthdate = ""
                    try {
                        age = Integer.parseInt(bcAgeTIET.text.toString())
                    }catch ( e  :Exception){


                    }
                    Log.d("Test"," age ${age}")
                if (age >= 12){

                    ageLimit = true
                }


            }

            registrationCommunicator.bcBirthDateAndAgeSelected(birthdate!!, age.toString())

            if (ageLimit){

                registrationCommunicator.bcGoToStepAdress()

            }


        }


        bcBirthDateTIET.setOnClickListener {

            // calender class's instance and get current date , month and year from calender

            val mYear = calendar!!.get(Calendar.YEAR) // current year
            val mMonth = calendar!!.get(Calendar.MONTH) // current month
            val mDay = calendar!!.get(Calendar.DAY_OF_MONTH) // current day
            // date picker dialog
            datePickerDialog = DatePickerDialog(activity,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        // set day of month , month and year value in the edit text

                        val date = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                        bcAgeTIET.text!!.clear()
                        bcBirthDateTIET.setText(date)
                        var ageTemp = 0
                        val sdf = SimpleDateFormat("dd/MM/yyyy")
                        try {
                            val birthDate = sdf.parse(date)
                            ageTemp = calculateAge(birthDate)
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }

                        if (ageTemp > 11) {
                            age = ageTemp
                            ageLimit = true
                           /* nextButton.setEnabled(true)
                            nextButton.setClickable(true)
                            nextButton.setBackgroundResource(R.drawable.next_button_active)*/
                        } else {
                            ageLimit = false
                           /* nextButton.setEnabled(false)
                            nextButton.setClickable(false)
                            nextButton.setBackgroundResource(R.drawable.next_button_inactive)*/
                        }
                    }, mYear, mMonth, mDay)

            val calendarMin = Calendar.getInstance()
            calendarMin.set(Calendar.DAY_OF_MONTH, mDay)
            calendarMin.set(Calendar.MONTH, mMonth)
            calendarMin.set(Calendar.YEAR, mYear - 85)

            val calendarMax = Calendar.getInstance()
            calendarMax.set(Calendar.DAY_OF_MONTH, mDay)
            calendarMax.set(Calendar.MONTH, mMonth)
            calendarMax.set(Calendar.YEAR, mYear - 12)


            datePickerDialog.getDatePicker().setMaxDate(calendarMax.timeInMillis)
            datePickerDialog.getDatePicker().setMinDate(calendarMin.timeInMillis)
            datePickerDialog.show()
        }



        supportTextView.setOnClickListener {

            makeCall("16479")

        }

        bcHelpLineLayout.setOnClickListener {

            makeCall("16479")
        }



    }

    private fun intialization(){

        registrationCommunicator = activity as RegistrationCommunicator
        calendar =  Calendar.getInstance()
    }


    private fun calculateAge(birthDate: Date): Int {
        var years = 0
        var months = 0
        var days = 0
        //create calendar object for birth day
        val birthDay = Calendar.getInstance()
        birthDay.timeInMillis = birthDate.time
        //create calendar object for current day
        val currentTime = System.currentTimeMillis()
        val now = Calendar.getInstance()
        now.timeInMillis = currentTime
        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR)
        val currMonth = now.get(Calendar.MONTH) + 1
        val birthMonth = birthDay.get(Calendar.MONTH) + 1
        //Get difference between months
        months = currMonth - birthMonth
        //if month difference is in negative then reduce years by one and calculate the number of months.
        if (months < 0) {
            years--
            months = 12 - birthMonth + currMonth
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
            years--
            months = 11
        }
        //Calculate the days
        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE)
        else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
            val today = now.get(Calendar.DAY_OF_MONTH)
            now.add(Calendar.MONTH, -1)
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today
        } else {
            days = 0
            if (months == 12) {
                years++
                months = 0
            }
        }
        //Create new Age object
        return years
    }

}
