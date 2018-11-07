package com.bdjobs.app.Login

import android.app.Fragment
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_login_otp.*
import android.os.CountDownTimer
import com.bdjobs.app.Utilities.Constants.Companion.counterTimeLimit
import com.bdjobs.app.Utilities.Constants.Companion.timer_countDownInterval


class LoginOTPFragment : Fragment() {

    lateinit var loginCommunicator: LoginCommunicator
    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        rootView = inflater?.inflate(R.layout.fragment_login_otp, container, false)!!
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginCommunicator = activity as LoginCommunicator

    }

    override fun onResume() {
        super.onResume()
        onClicks()
        setData()
        setTime()
    }

    private fun setTime() {
        counterTV.show()
        resendOtpTV.hide()
        object : CountDownTimer(counterTimeLimit.toLong(), timer_countDownInterval.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                val second = millisUntilFinished / 1000 % 60
                val minute = millisUntilFinished / (1000 * 60) % 60
                val hour = millisUntilFinished / (1000 * 60 * 60) % 24
                val time = String.format("%02d:%02d", minute, second)
                counterTV.text = time
            }

            override fun onFinish() {

                counterTV.hide()
                resendOtpTV.show()

            }
        }.start()
    }


    private fun setData() {
        val message = getString(R.string.otp_message) + loginCommunicator.getUserName()
        otpMsgTV.text = message
    }

    private fun onClicks() {
        backBtnIMGV.setOnClickListener {
            loginCommunicator?.backButtonClicked()
        }

        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            try {
                val r = Rect()
                rootView.getWindowVisibleDisplayFrame(r)
                val heightDiff = rootView.rootView.height - (r.bottom - r.top)

                if (heightDiff > 200) { // if more than 100 pixels, its probably a keyboard...
                    footerIMGV.hide()
                } else {
                    //ok now we know the keyboard is down...
                    footerIMGV.show()

                }
            } catch (e: Exception) {
                logException(e)
            }
        }
    }


}