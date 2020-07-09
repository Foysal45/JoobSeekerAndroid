package com.bdjobs.app.Registration.blue_collar_registration

import android.app.DatePickerDialog
import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.footer_bc_layout.*
import kotlinx.android.synthetic.main.fragment_bc_birth_date.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class BCBirthDateFragment : Fragment() {

    lateinit var datePickerDialog: DatePickerDialog
    private var calendar: Calendar? = null
    private lateinit var registrationCommunicator: RegistrationCommunicator
    internal var age = 0
    internal var birthdate: String? = null
    private var ageLimit = false
    private lateinit var returnView: View
    private var date = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        returnView = inflater.inflate(R.layout.fragment_bc_birth_date, container, false)
        return returnView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        intialization()
        onClick()
        registrationCommunicator.checkInviteCodeEligibility()
    }

    override fun onResume() {
        super.onResume()
        //Log.d("birthd", "onResumeCalled")
        if (!TextUtils.isEmpty(birthdate)) {
            bcBirthDateTIET?.setText(birthdate)
            bcAgeTIET?.text!!.clear()
            ageLimit = true

        } else if (!TextUtils.isEmpty(registrationCommunicator.bcGetAge())) {
            //Log.d("birthd", "age: " + registrationCommunicator.bcGetAge())
            age = Integer.parseInt(registrationCommunicator.bcGetAge())
            if (age >= 12) {
                bcAgeTIET?.setText(age.toString())
                bcBirthDateTIET?.text!!.clear()
                ageLimit = true

            }
        }

    }

    private fun onClick() {
        bcBirthDateFAButton.setOnClickListener {
            if (!TextUtils.isEmpty(bcBirthDateTIET?.text.toString())) {
                birthdate = bcBirthDateTIET?.text.toString()
                //Log.d("Test", " birthDtae ${birthdate}")
                val sdf = SimpleDateFormat("dd/MM/yyyy")
                try {
                    val birthDateCal = sdf.parse(birthdate)
                    //Log.d("Test", " birthDate after parse $birthDateCal ")
                    val age = calculateAge(birthDateCal)
                    //Log.d("Test", " age in calculationn ${calculateAge(birthDateCal)} age ${age}")

                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                age = 0
                //Log.d("Test", " age in birth condition ${age} ageLimit ${ageLimit}")

            } else {
                birthdate = ""
                age = 0
                try {
                    age = Integer.parseInt(bcAgeTIET?.text.toString())
                } catch (e: Exception) {
                }
                //Log.d("Test", " age $age")
                ageLimit = age in 12..85
                //Log.d("Test", " ageLimit ${ageLimit} and age $age ")
            }
            if (TextUtils.isEmpty(bcBirthDateTIET?.text.toString()) && TextUtils.isEmpty(bcAgeTIET?.text.toString())) {
                bcAgeTIL?.showError("জন্ম তারিখ অথবা বয়স যেকোনো একটির তথ্য দিন")
                bcBirthDateTIL?.showError("জন্ম তারিখ অথবা বয়স যেকোনো একটির তথ্য দিন")
            }

            if (ageLimit) {
                registrationCommunicator.bcBirthDateAndAgeSelected(birthdate.toString(), age.toString())
                registrationCommunicator.bcGoToStepAdress()
            }

        }


        bcBirthDateTIET?.setOnClickListener {
            // calender class's instance and get current date , month and year from calender
            val mYear = calendar!!.get(Calendar.YEAR) // current year
            val mMonth = calendar!!.get(Calendar.MONTH) // current month
            val mDay = calendar!!.get(Calendar.DAY_OF_MONTH) // current day
            // date picker dialog
            datePickerDialog = DatePickerDialog(activity,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        // set day of month , month and year value in the edit text

                        date = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                        bcAgeTIET.text!!.clear()
                        //Log.d("Test", " date default ${date} ")
                        bcBirthDateTIET.setText(date)
                        var ageTemp = 0
                        val sdf = SimpleDateFormat("dd/MM/yyyy")
                        try {
                            val birthDate = sdf.parse(date)
                            //Log.d("Test", " date after formate ${birthDate} ")
                            ageTemp = calculateAge(birthDate)
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }

                        //Log.d("Test", " ageTemp ${ageTemp} ageLimit $ageLimit")
                        val age = ageTemp
                        if (age in 12..85) {
                            ageLimit = true
                            bcBirthDateTIL?.hideError()
                            bcAgeTIL?.hideError()
                            bcAgeTIET?.isFocusableInTouchMode = true

                        } else {
                            ageLimit = false
                            bcBirthDateTIL?.showError("বয়স অবশ্যই (12-85) একটি সীমার মধ্যে হতে হবে")
                            bcAgeTIET?.isFocusableInTouchMode = true

                        }

                        //Log.d("Test", " age in birth ${age} ageLimit $ageLimit")

                    }, mYear, mMonth, mDay)

            val calendarMin = Calendar.getInstance()
            calendarMin.set(Calendar.DAY_OF_MONTH, mDay)
            calendarMin.set(Calendar.MONTH, mMonth)
            calendarMin.set(Calendar.YEAR, mYear - 85)

            val calendarMax = Calendar.getInstance()
            calendarMax.set(Calendar.DAY_OF_MONTH, mDay)
            calendarMax.set(Calendar.MONTH, mMonth)
            calendarMax.set(Calendar.YEAR, mYear - 12)


            datePickerDialog.datePicker.maxDate = calendarMax.timeInMillis
            datePickerDialog.datePicker.minDate = calendarMin.timeInMillis
            datePickerDialog.show()
        }

        supportTextView?.setOnClickListener {
            activity.callHelpLine()
        }
        bcHelpLineLayout?.setOnClickListener {
            activity.callHelpLine()
        }
    }

    private fun intialization() {
        registrationCommunicator = activity as RegistrationCommunicator
        calendar = Calendar.getInstance()
        addTextChangedListener()
    }


    private fun addTextChangedListener() {
        bcBirthDateTIET?.easyOnTextChangedListener { charSequence ->
            birthdateValidation(charSequence.toString(), bcBirthDateTIET, bcBirthDateTIL, "জন্ম তারিখ অথবা বয়স যেকোনো একটির তথ্য দিন")
        }
        bcAgeTIET?.easyOnTextChangedListener { charSequence ->
            ageValidation(charSequence.toString(), bcAgeTIET, bcAgeTIL, "জন্ম তারিখ অথবা বয়স যেকোনো একটির তথ্য দিন")
        }

    }

    private fun ageValidation(char: String, et: TextInputEditText, til: TextInputLayout, message: String): Boolean {
        when {
            TextUtils.isEmpty(char) -> {
                til.showError(message)
                requestFocus(et)
                return false
            }
            else -> {
                try {
                    age = Integer.parseInt(bcAgeTIET?.text.toString())
                } catch (e: Exception) {
                }
                //Log.d("Test", " age in age ${age}")
                if (age in 12..85) {
                    //Log.d("Test", " true ")
                    ageLimit = true
                } else {
                    //Log.d("Test", " false ")
                    ageLimit = false
                }
                if (ageLimit) {
                    til.hideError()
                    bcBirthDateTIET?.clear()
                    bcBirthDateTIL?.hideError()
                } else {
                    til.showError("বয়স অবশ্যই (12-85) একটি সীমার মধ্যে হতে হবে")
                }
            }
        }
        return true
    }

    private fun birthdateValidation(char: String, et: TextInputEditText, til: TextInputLayout, message: String): Boolean {
        when {
            TextUtils.isEmpty(char) -> {
                til.showError(message)
                requestFocus(et)
                return false
            }
            else -> {
                if (ageLimit) {
                    til.hideError()
                    bcAgeTIL?.hideError()
                } else {
                    til.showError("বয়স অবশ্যই (12-85) একটি সীমার মধ্যে হতে হবে")
                }
            }
        }
        return true
    }


    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
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

    override fun onPause() {
        super.onPause()
        bcBirthDateTIL?.hideKeyboard()
    }

}
