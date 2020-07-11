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
import com.bdjobs.app.API.ModelClasses.SendEmailCV
import com.bdjobs.app.Ads.Ads
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.EditResLandingActivity
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.fragment_email_resume.*
import org.jetbrains.anko.act
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


class EmailResumeFragment : Fragment() {

    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var symbol: String
    lateinit var communicator: ManageResumeCommunicator
    private var isResumeUpdate: String = "0"
    private var subject = ""
    private var toEmail = ""
    private var jobID = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.bdjobs.app.R.layout.fragment_email_resume, container, false)
    }

    override fun onResume() {
        super.onResume()
        bdjobsUserSession = BdjobsUserSession(activity)
        communicator = activity as ManageResumeCommunicator
        subject = communicator.getSubject()
        toEmail = communicator.getEmailTo()
        jobID = communicator.getjobID()

//        val adRequest = AdRequest.Builder().build()
//        adView?.loadAd(adRequest)
        Ads.loadAdaptiveBanner(activity,adView)


        if (!bdjobsUserSession.isCvPosted?.equalIgnoreCase("true")!!) {
            try {
                val alertd = alert("To Access this feature please post your resume") {
                    title = "Your resume is not posted!"
                    positiveButton("Post Resume") { startActivity<EditResLandingActivity>() }
                    negativeButton("Cancel") {
                        communicator.backButtonPressed()
                    }
                }
                alertd.isCancelable = false
                alertd.show()
            } catch (e: Exception) {
                logException(e)
            }
        } else {
            val emailText = "${bdjobsUserSession.fullName} <${bdjobsUserSession.email}>"
            et_from.setText(emailText)
            et_Subject?.setText("Application for the post of $subject")
            et_to?.setText(toEmail)

            backIV.setOnClickListener {
                communicator.backButtonPressed()
            }

            //Log.d("manage", "===${communicator.getEmailTo()}")

            uploadResume.isEnabled = bdjobsUserSession.cvUploadStatus == "0" || bdjobsUserSession.cvUploadStatus == "4"

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

        var uploaded = "0"

        if (mybdjobsResume.isChecked) {
            uploaded = "0"
        } else if (uploadResume.isChecked) {
            uploaded = "1"
        }
        //Log.d("isresumeUpdate", "is = $isResumeUpdate")
        callSendEmailCV(uploaded)
    }


    private fun callSendEmailCV(uploadedCV: String) {
        activity?.showProgressBar(EmailResumeLoadingProgressBar)
        ApiServiceMyBdjobs.create().sendEmailCV(
                userID = bdjobsUserSession.userId,
                decodeID = bdjobsUserSession.decodId,
                uploadedCv = uploadedCV,
                application = et_Message?.getString(),
                isResumeUpdate = bdjobsUserSession.IsResumeUpdate,
                fullName = bdjobsUserSession.fullName,
                Jobid = jobID,
                userEmail = et_from?.getString(),
                companyEmail = et_to?.getString(),
                mailSubject = et_Subject?.getString()


        ).enqueue(object : Callback<SendEmailCV> {
            override fun onFailure(call: Call<SendEmailCV>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<SendEmailCV>, response: Response<SendEmailCV>) {
                try {
                    if (response.isSuccessful) {
                        activity?.stopProgressBar(EmailResumeLoadingProgressBar)
                        //Log.d("isresume", "value = $isResumeUpdate full = ${bdjobsUserSession.fullName}")
                        activity?.toast(response.body()?.message!!)
                        bdjobsUserSession.incrementTimesEmailedRessume()
                        communicator.backButtonPressed()
                    }
                } catch (e: Exception) {
                    activity?.stopProgressBar(EmailResumeLoadingProgressBar)
                    e.printStackTrace()
                }
            }

        })
    }

    private fun validateEmail(): Boolean {
        val email = et_from?.text.toString()
        if (et_from?.text.toString().trim({ it <= ' ' }).isEmpty()) {
            from_TIL?.isErrorEnabled = true
            from_TIL?.error = getString(com.bdjobs.app.R.string.type_email)
            requestFocus(et_from)
            return false
        } else if (!isValidEmail(email)) {
            from_TIL?.isErrorEnabled = true
            from_TIL?.error = getString(com.bdjobs.app.R.string.valid_email)
            requestFocus(et_from)
            return false
        } else {
            from_TIL?.isErrorEnabled = false
        }

        return true
    }

    private fun validateEmpEmail(): Boolean {
        val email = et_to?.text.toString()
        if (et_to?.text.toString().trim({ it <= ' ' }).isEmpty()) {
            to_TIL?.isErrorEnabled = true
            to_TIL?.error = getString(com.bdjobs.app.R.string.type_email)
            requestFocus(et_to)
            return false
        } else if (!isValidEmail(email)) {
            to_TIL?.isErrorEnabled = true
            to_TIL?.error = getString(com.bdjobs.app.R.string.valid_email)
            requestFocus(et_to)
            return false
        } else {
            to_TIL?.isErrorEnabled = false
        }

        return true
    }

    private fun validateSubj(): Boolean {

        if (et_Subject?.text.toString().trim({ it <= ' ' }).isEmpty()) {
            Subject_TIL?.isErrorEnabled = true
            Subject_TIL?.error = getString(com.bdjobs.app.R.string.email_subj)
            requestFocus(et_Subject)
            return false
        } else {
            Subject_TIL?.isErrorEnabled = false
        }

        return true
    }


    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
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
        Log.d("rakib","$target")
        val startIndex = target.toString().indexOf("<")
        val endIndex = target.toString().indexOf(">")
        val email = target.toString().substringAfter("<").substringBefore(">")
        Log.d("rakib","$target $startIndex $endIndex $email")
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}
