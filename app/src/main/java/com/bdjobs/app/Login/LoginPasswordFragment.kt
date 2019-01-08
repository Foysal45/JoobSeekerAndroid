package com.bdjobs.app.Login

import android.app.Fragment
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.LoginSessionModel
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.api_request_result_code_ok
import kotlinx.android.synthetic.main.fragment_login_password.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


class LoginPasswordFragment : Fragment() {

    private lateinit var loginCommunicator: LoginCommunicator
    private lateinit var symbol: String


    private lateinit var rootView: View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater?.inflate(R.layout.fragment_login_password, container, false)!!
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
    }

    private fun setData() {
        profilePicIMGV?.loadCircularImageFromUrl(loginCommunicator.getImageUrl())
        nameTV?.text=loginCommunicator.getFullName()
    }

    private fun onClicks() {
        backBtnIMGV?.setOnClickListener {
            loginCommunicator?.backButtonClicked()
        }

        passwordTIET.easyOnTextChangedListener { charSequence ->
            validatePassword(charSequence.toString())
        }

        nextButtonFAB?.setOnClickListener {
            doLogin()
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

    private fun doLogin() {
        val password = passwordTIET.getString()
        if (!validatePassword(password)) {
            return
        } else {
            activity.showProgressBar(progressBar)
            ApiServiceMyBdjobs.create().doLogin(username = loginCommunicator.getUserName(), password = password, userId = loginCommunicator.getUserId(), fullName = loginCommunicator.getFullName()).enqueue(object : Callback<LoginSessionModel> {
                override fun onFailure(call: Call<LoginSessionModel>, t: Throwable) {
                    activity.stopProgressBar(progressBar)
                    error("onFailure", t)
                }

                override fun onResponse(call: Call<LoginSessionModel>, response: Response<LoginSessionModel>) {

                    try {
                        if (response.isSuccessful) {

                            if (response?.body()?.statuscode!!.equalIgnoreCase(api_request_result_code_ok)) {
                                passwordTIL.hideError()
                                val bdjobsUserSession = BdjobsUserSession(activity)
                                bdjobsUserSession.createSession(response?.body()?.data?.get(0)!!)
                                loginCommunicator.goToHomePage()

                            } else {
                                activity.stopProgressBar(progressBar)
                                passwordTIL.showError("Wrong password. Try again or click Forgot password to reset it")
                            }

                        }
                    } catch (e: Exception) {
                        logException(e)
                    }

                }

            })
        }
    }

    private fun validatePassword(password: String): Boolean {

        when {
            TextUtils.isEmpty(password) -> {
                passwordTIL?.showError(getString(R.string.field_empty_error_message_common))
                requestFocus(passwordTIET)
                return false
            }
           /* checkStringHasSymbol(password) -> {
                passwordTIL?.showError("Password can not contain $symbol")
                requestFocus(passwordTIET)
                return false
            }*/
            password.trim { it <= ' ' }.length < 5 || password.trim().length > 12 -> {
                passwordTIL?.showError("Password should be 8 to 12 character long!")
                requestFocus(passwordTIET)
                return false
            }
            else -> passwordTIL?.hideError()
        }
        return true
    }

    private fun checkStringHasSymbol(s: String): Boolean {

        val p = Pattern.compile("[:`!@#$%&*()_ +/=;|'\"<>?{}\\[\\]~-]")


        for (i in 0 until s.length) {
            symbol = s[i].toString()
            val m = p.matcher(symbol)
            if (m.matches()) {
                return true
            }
        }
        return false
    }

    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }
}