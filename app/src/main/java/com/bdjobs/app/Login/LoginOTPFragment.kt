package com.bdjobs.app.Login

import android.app.Fragment
import android.graphics.Rect
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.LoginSessionModel
import com.bdjobs.app.API.ModelClasses.ResendOtpModel
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.api_request_result_code_ok
import com.bdjobs.app.Utilities.Constants.Companion.counterTimeLimit
import com.bdjobs.app.Utilities.Constants.Companion.timer_countDownInterval
import kotlinx.android.synthetic.main.fragment_login_otp.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginOTPFragment : Fragment() {

    lateinit var loginCommunicator: LoginCommunicator
    private lateinit var rootView: View
    private lateinit var counter: CountDownTimer

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater?.inflate(R.layout.fragment_login_otp, container, false)!!
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginCommunicator = activity as LoginCommunicator
        setTime()
    }

    override fun onResume() {
        super.onResume()
        onClicks()
        setData()
    }

    private fun setTime() {
        counterTV?.show()
        resendOtpTV?.hide()
        counter = object : CountDownTimer(counterTimeLimit.toLong(), timer_countDownInterval.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                val second = millisUntilFinished / 1000 % 60
                val minute = millisUntilFinished / (1000 * 60) % 60
                val hour = millisUntilFinished / (1000 * 60 * 60) % 24
                val time = String.format("%02d:%02d", minute, second)
                counterTV?.text = time
            }

            override fun onFinish() {
                counterTV?.hide()
                resendOtpTV?.show()
            }
        }.start()
    }

    private fun setData() {
        val message = getString(R.string.otp_message) + loginCommunicator.getUserName()
        otpMsgTV?.text = message
    }

    private fun onClicks() {
        backBtnIMGV?.setOnClickListener {
            loginCommunicator?.backButtonClicked()
        }

        otpTIET?.easyOnTextChangedListener { charSequence ->
            validateOtpCode(charSequence.toString())
        }


        resendOtpTV?.setOnClickListener {
            resendOTP()
        }

        rootView?.viewTreeObserver.addOnGlobalLayoutListener {
            try {
                val r = Rect()
                rootView.getWindowVisibleDisplayFrame(r)
                val heightDiff = rootView.rootView.height - (r.bottom - r.top)

                if (heightDiff > 200) { // if more than 100 pixels, its probably a keyboard...
                    footerIMGV?.hide()
                } else {
                    //ok now we know the keyboard is down...
                    footerIMGV?.show()

                }
            } catch (e: Exception) {
                logException(e)
            }
        }

        nextButtonFAB?.setOnClickListener {
            doLogin()
        }

    }

    private fun resendOTP()
    {
//        toast("clicked")
        setTime()
        counterTV?.show()
        resendOtpTV?.hide()

        loadingProgressBar.visibility = View.VISIBLE

        ApiServiceMyBdjobs.create().resendOtp(loginCommunicator.getUserId(), loginCommunicator.getUserName(), "2").enqueue(object : Callback<ResendOtpModel> {
            override fun onFailure(call: Call<ResendOtpModel>, t: Throwable) {

                //Log.d("resendOtp", " sjkafhsakfljh failuere")
            }

            override fun onResponse(call: Call<ResendOtpModel>, response: Response<ResendOtpModel>) {

                try {
                    //Log.d("resendOtp", " sjkafhsakfljh ${response.message()}")
                    /*  toast(response.message())*/
                    loadingProgressBar.visibility = View.GONE

                } catch (e: Exception) {

                    logException(e)
                }


            }


        })
    }

    private fun doLogin() {
        val otpCode = otpTIET.getString()
        if (validateOtpCode(otpCode)) {
            activity?.showProgressBar(progressBar)
            ApiServiceMyBdjobs.create().doLogin(username = loginCommunicator.getUserName(), otpCode = otpCode, userId = loginCommunicator.getUserId(), fullName = loginCommunicator.getFullName()).enqueue(object : Callback<LoginSessionModel> {
                override fun onFailure(call: Call<LoginSessionModel>, t: Throwable) {
                    activity?.stopProgressBar(progressBar)
                    error("onFailure", t)
                }

                override fun onResponse(call: Call<LoginSessionModel>, response: Response<LoginSessionModel>) {


                    try {
                        if (response.isSuccessful) {
                           if(response?.body()?.statuscode!!.equalIgnoreCase(api_request_result_code_ok)){
                               otpTIL?.hideError()
                               val bdjobsUserSession = BdjobsUserSession(activity)
                               bdjobsUserSession.createSession(response?.body()?.data?.get(0)!!)
                               loginCommunicator.goToHomePage()
                           }else{
                               activity?.stopProgressBar(progressBar)
                               otpTIL?.showError("The code is not correct or has been expired. Resend code")
                           }
                        }
                    } catch (e: Exception) {
                        logException(e)
                    }
                }
            })

        } else {
            return
        }
    }

    private fun validateOtpCode(otpCode: String?): Boolean {
        when {
            otpCode.isNullOrBlank() -> {
                otpTIL?.showError("Please type the code")
                return false
            }

        }
        otpTIL?.hideError()
        return true
    }

    override fun onStop() {
        counter?.cancel()
        super.onStop()
    }
}