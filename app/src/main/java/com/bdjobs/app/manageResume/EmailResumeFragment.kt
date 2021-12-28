package com.bdjobs.app.manageResume


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.SendEmailCV
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.ads.Ads
import com.bdjobs.app.editResume.EditResLandingActivity
import com.bdjobs.app.utilities.*
import kotlinx.android.synthetic.main.fragment_email_resume.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


class EmailResumeFragment : Fragment() {

    lateinit var bdJobsUserSession: BdjobsUserSession
    lateinit var symbol: String
    lateinit var communicator: ManageResumeCommunicator
    private var subject = ""
    private var toEmail = ""
    private var jobID = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.bdjobs.app.R.layout.fragment_email_resume, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        bdJobsUserSession = BdjobsUserSession(requireContext())
        communicator = activity as ManageResumeCommunicator
        subject = communicator.getSubject()
        toEmail = communicator.getEmailTo()
        jobID = communicator.getjobID()

//        val adRequest = AdRequest.Builder().build()
//        adView?.loadAd(adRequest)
        Ads.loadAdaptiveBanner(requireContext(),adView)


        if (!bdJobsUserSession.isCvPosted?.equalIgnoreCase("true")!!) {
            try {
                val alertDialog = alert("To access this feature post Bdjobs Resume.") {
                    title = "Your Bdjobs Resume is not posted."
                    positiveButton("Post Resume") { requireContext().startActivity<EditResLandingActivity>() }
                    negativeButton("Cancel") {
                        communicator.backButtonPressed()
                    }
                }
                alertDialog.isCancelable = false
                alertDialog.show()
            } catch (e: Exception) {
                logException(e)
            }
        } else {
            val emailText = "${bdJobsUserSession.fullName} <${bdJobsUserSession.email}>"
            et_from.setText(emailText)
            et_Subject?.setText("Application for the post of $subject")
            et_to?.setText(toEmail)

            backIV.setOnClickListener {
                communicator.backButtonPressed()
            }

            //Log.d("manage", "===${communicator.getEmailTo()}")

            uploadResume.isEnabled = bdJobsUserSession.cvUploadStatus == "0" || bdJobsUserSession.cvUploadStatus == "4"

            mybdjobsResume.performClick()

            cbFAB.setOnClickListener {
                validation()
            }

            et_from.easyOnTextChangedListener {
                validateEmail()
            }
            et_to.easyOnTextChangedListener {
                validateEmpEmail()
            }
            et_Subject.easyOnTextChangedListener {
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
        callSendEmailCV(uploaded)
    }


    private fun callSendEmailCV(uploadedCV: String) {
        activity?.showProgressBar(EmailResumeLoadingProgressBar)
        ApiServiceMyBdjobs.create().sendEmailCV(
                userID = bdJobsUserSession.userId,
                decodeID = bdJobsUserSession.decodId,
                uploadedCv = uploadedCV,
                application = et_Message?.getString(),
                isResumeUpdate = bdJobsUserSession.IsResumeUpdate,
                fullName = bdJobsUserSession.fullName,
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
                        if (isAdded) {
                            requireActivity().stopProgressBar(EmailResumeLoadingProgressBar)
                            requireActivity().toast(response.body()?.message!!)
                            bdJobsUserSession.incrementTimesEmailedRessume()
                            communicator.backButtonPressed()
                        }

                    }
                } catch (e: Exception) {
                    if (isAdded) {
                        requireActivity().stopProgressBar(EmailResumeLoadingProgressBar)
                        e.printStackTrace()
                    }

                }
            }

        })
    }

    private fun validateEmail(): Boolean {
        val email = et_from?.text.toString()
        if (et_from?.text.toString().trim { it <= ' ' }.isEmpty()) {
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
        if (et_to?.text.toString().trim { it <= ' ' }.isEmpty()) {
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

        if (et_Subject?.text.toString().trim { it <= ' ' }.isEmpty()) {
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

    private fun isValidEmail(target: CharSequence): Boolean {
        val email = target.toString().substringAfter("<").substringBefore(">")
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}
