package com.bdjobs.app.Login

import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.LoginUserModel
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.api_request_result_code_ok
import com.bdjobs.app.Utilities.Constants.Companion.key_false
import com.bdjobs.app.Utilities.Constants.Companion.key_true
import kotlinx.android.synthetic.main.fragment_login_username.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class LoginUserNameFragment : Fragment() {

    lateinit var loginCommunicator: LoginCommunicator
    lateinit var symbol: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_login_username, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginCommunicator = activity as LoginCommunicator
        onClicks()
    }


    private fun onClicks() {
        backBtnIMGV.setOnClickListener {
            loginCommunicator?.backButtonClicked()
        }
        nextButtonFAB.setOnClickListener {
            doLogin()
        }

        usernameTIET.easyOnTextChangedListener { charSequence ->
            validateUserName(charSequence.toString())
        }
    }

    private fun doLogin() {

        val userName = usernameTIET.getString()
        if (!validateUserName(userName)) {
            return
        } else {
            activity.showProgressBar(loadingProgressBar)
            ApiServiceMyBdjobs.create().getLoginUserDetails(userName).enqueue(object : Callback<LoginUserModel> {

                override fun onFailure(call: Call<LoginUserModel>?, t: Throwable?) {
                    activity.stopProgressBar(loadingProgressBar)
                }

                override fun onResponse(call: Call<LoginUserModel>?, response: Response<LoginUserModel>?) {
                    activity.stopProgressBar(loadingProgressBar)
                    if (response?.body()?.statuscode == api_request_result_code_ok) {
                        useNameTIL.hideError()
                        if (response?.body()?.data?.get(0)?.isBlueCollar?.equalIgnoreCase(key_true)!!) {
                            loginCommunicator.goToOtpFragment(userName,response?.body()?.data?.get(0)?.userId, response?.body()?.data?.get(0)?.userFullName, response?.body()?.data?.get(0)?.imageurl)
                        } else if (response?.body()?.data?.get(0)?.isBlueCollar?.equalIgnoreCase(key_false)!!) {
                            loginCommunicator.goToPasswordFragment(userName,response?.body()?.data?.get(0)?.userId, response?.body()?.data?.get(0)?.userFullName, response?.body()?.data?.get(0)?.imageurl)
                        }

                    } else {
                        useNameTIL.showError(response?.body()?.message)
                        requestFocus(usernameTIET)
                    }
                }
            })
        }
    }


    private fun validateUserName(userName: String): Boolean {

        when {
            TextUtils.isEmpty(userName) -> {
                useNameTIL.showError(getString(R.string.field_empty_error_message_common))
                requestFocus(usernameTIET)
                return false
            }
            checkStringHasSymbol(userName) -> {
                useNameTIL.showError("Username can not contain $symbol")
                requestFocus(usernameTIET)
                return false
            }
            userName.trim { it <= ' ' }.length < 5 /*|| userName.trim { it <= ' ' }.length > 15*/ -> {
                useNameTIL.showError("Username is too short!")
                requestFocus(usernameTIET)
                return false
            }
            else -> useNameTIL.hideError()
        }
        return true
    }

    private fun checkStringHasSymbol(s: String): Boolean {
        val p = Pattern.compile("[:`!#$%&*()_ +/=;|'\"<>?{}\\[\\]~-]")
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
            activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

}