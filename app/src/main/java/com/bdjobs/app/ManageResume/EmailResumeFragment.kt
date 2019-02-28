package com.bdjobs.app.ManageResume


import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.EmailResume
import com.bdjobs.app.API.ModelClasses.SendEmailCV
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import kotlinx.android.synthetic.main.fragment_email_resume.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


class EmailResumeFragment : Fragment() {

    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var symbol: String
    lateinit var communicator: ManageResumeCommunicator
    private var isResumeUpdate : String = "0"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.bdjobs.app.R.layout.fragment_email_resume, container, false)
    }

    override fun onResume() {
        super.onResume()
        bdjobsUserSession = BdjobsUserSession(activity)
        communicator = activity as ManageResumeCommunicator
        et_from.setText(bdjobsUserSession.email)
        backIV.setOnClickListener {
            communicator.backButtonPressed()
        }

        uploadResume.isEnabled = Constants.cvUploadStatus == "0" || Constants.cvUploadStatus == "4"

        mybdjobsResume.performClick()

        cbFAB.setOnClickListener {
            validation()
        }

        et_from.easyOnTextChangedListener { charSequence ->
            validateEmail()
        }
        et_to.easyOnTextChangedListener { charSequence ->
            validateEmpEmail()

        }
        et_Subject.easyOnTextChangedListener { charSequence ->
            validateSubj()

        }
    }

    private fun validation() {
        if (!validateEmail()) {
            return
        }
        if (!validateEmpEmail()) {
            return
        }

        if (!validateSubj()) {
            return
        }

        if(mybdjobsResume.isChecked){

            isResumeUpdate = "0"
            //Toast.makeText(getApplicationContext(), "mybdjobs", Toast.LENGTH_SHORT).show()

        }
        else if(uploadResume.isChecked){
            isResumeUpdate = "1"
         //   Toast.makeText(getApplicationContext(), "uploadResume", Toast.LENGTH_SHORT).show()
        }
        //
       // Toast.makeText(getApplicationContext(), "${isResumeUpdate}", Toast.LENGTH_SHORT).show()
        callSendEmailCV(isResumeUpdate)
    }

    private fun callApiMyBdjobsResume() {
        ApiServiceMyBdjobs.create().getEmailResumeMsg(
                /*   userID = bdjobsUserSession.userId,
                   decodeID = bdjobsUserSession.decodId,
                   isResumeUpdate = bdjobsUserSession.IsResumeUpdate,
                   userEmail = "a[elpoalpl@jhjnhkj.coijoi",
                   companyEmail = "rumana.cse7@gmail.com",
                   mailSubject = "ewaerwrwerwer",
                   application = "",
                   fullName = "stertert",
                   uploadedCv = "0",
                   Jobid = "0"*/

                userID = bdjobsUserSession.userId,
                decodeID = bdjobsUserSession.decodId,
                isResumeUpdate = bdjobsUserSession.IsResumeUpdate,
                userEmail = et_from.text?.toString(),
                companyEmail = et_to.text?.toString(),
                mailSubject = et_Subject.text?.toString(),
                application = "",
                fullName = bdjobsUserSession.fullName,
                uploadedCv = "0",
                Jobid = "0"


        ).enqueue(object : Callback<EmailResume> {
            override fun onFailure(call: Call<EmailResume>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<EmailResume>, response: Response<EmailResume>) {

                toast(response.body()?.message!!)
            }

        })
    }

    private fun callSendEmailCV(isResumeUpdate: String) {
        activity.showProgressBar(EmailResumeLoadingProgressBar)
        ApiServiceMyBdjobs.create().sendEmailCV(
                userID = bdjobsUserSession.userId,
                decodeID = bdjobsUserSession.decodId,
                uploadedCv = "0",
                application = et_Message.text?.toString(),
                isResumeUpdate = isResumeUpdate,
                fullName = bdjobsUserSession.fullName,
                Jobid = "0",
                userEmail = et_from.text?.toString(),
                companyEmail = et_to.text?.toString(),
                mailSubject = et_Subject.text?.toString()


        ).enqueue(object : Callback<SendEmailCV> {
            override fun onFailure(call: Call<SendEmailCV>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<SendEmailCV>, response: Response<SendEmailCV>) {
           try {
               if (response.isSuccessful) {
                   activity.stopProgressBar(EmailResumeLoadingProgressBar)
                   Log.d("isresume", "value = $isResumeUpdate full = ${bdjobsUserSession.fullName}")
                   toast(response.body()?.message!!)
                   communicator.backButtonPressed()
               }
           }
           catch (e : Exception){
               activity.stopProgressBar(EmailResumeLoadingProgressBar)
               e.printStackTrace()
           }
            }

        })
    }

    private fun validateEmail(): Boolean {
        val email = et_from.getText().toString()
        if (et_from.getText().toString().trim({ it <= ' ' }).isEmpty()) {
            from_TIL.setErrorEnabled(true)
            from_TIL.setError(getString(com.bdjobs.app.R.string.type_email))
            requestFocus(et_from)
            return false
        } else if (!isValidEmail(email)) {
            from_TIL.setErrorEnabled(true)
            from_TIL.setError(getString(com.bdjobs.app.R.string.valid_email))
            requestFocus(et_from)
            return false
        } else {
            from_TIL.setErrorEnabled(false)
        }

        return true
    }

    private fun validateEmpEmail(): Boolean {
        val email = et_to.getText().toString()
        if (et_to.getText().toString().trim({ it <= ' ' }).isEmpty()) {
            to_TIL.setErrorEnabled(true)
            to_TIL.setError(getString(com.bdjobs.app.R.string.type_email))
            requestFocus(et_to)
            return false
        } else if (!isValidEmail(email)) {
            to_TIL.setErrorEnabled(true)
            to_TIL.setError(getString(com.bdjobs.app.R.string.valid_email))
            requestFocus(et_to)
            return false
        } else {
            to_TIL.setErrorEnabled(false)
        }

        return true
    }

    private fun validateSubj(): Boolean {
        val email = et_Subject.getText().toString()
        if (et_Subject.getText().toString().trim({ it <= ' ' }).isEmpty()) {
            Subject_TIL.setErrorEnabled(true)
            Subject_TIL.setError(getString(com.bdjobs.app.R.string.email_subj))
            requestFocus(et_Subject)
            return false
        } else {
            Subject_TIL.setErrorEnabled(false)
        }

        return true
    }


    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    private fun checkStringHasSymbol(s: String): Boolean {

        val p = Pattern.compile("[:`!@#$%&*()_ +/=;|'\"<>?{}\\[\\]~-]")

        var b = false
        for (i in 0 until s.length) {
            symbol = s[i].toString()
            val m = p.matcher(symbol)
            b = m.matches()
            if (b) {
                return true
            }
        }
        return false
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

}
