package com.bdjobs.app.Login

import android.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.LoginUserModel
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.api_request_result_code_ok
import kotlinx.android.synthetic.main.fragment_login_landing.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class LoginUserNameFragment : Fragment() {

    lateinit var loginCommunicator: LoginCommunicator
    lateinit var symbol: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_login_landing, container, false)!!
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

        userNameTIET.easyOnTextChangedListener { charSequence ->
            validateUserName(charSequence.toString())
        }
    }

    private fun doLogin() {

        val userName = userNameTIET.getString()
        if (!validateUserName(userName)) {
            return
        } else {
            activity.showProgressBar(userNameProgressBar)
            ApiServiceMyBdjobs.create().getLoginUserDetails(userName).enqueue(object : Callback<LoginUserModel> {

                override fun onFailure(call: Call<LoginUserModel>?, t: Throwable?) {
                    activity.stopProgressBar(userNameProgressBar)
                }

                override fun onResponse(call: Call<LoginUserModel>?, response: Response<LoginUserModel>?) {
                    activity.stopProgressBar(userNameProgressBar)
                    if (response?.body()?.statuscode == api_request_result_code_ok) {
                        userNameTIL.hideError()

                    } else {
                        userNameTIL.showError(response?.body()?.message)
                        requestFocus(userNameTIET)
                    }
                }
            })
        }
    }


    private fun validateUserName(userName: String): Boolean {

        if (TextUtils.isEmpty(userName)) {
            userNameTIL.showError(getString(R.string.field_empty_error_message_common))
            requestFocus(userNameTIET)
            return false
        } else if (checkStringHasSymbol(userName)) {
            userNameTIL.showError("Username can not contain $symbol")
            requestFocus(userNameTIET)
            return false
        } else if (userName.trim { it <= ' ' }.length < 5 || userName.trim { it <= ' ' }.length > 15) {
            userNameTIL.showError("Username should be 5 to 15 character long!")
            requestFocus(userNameTIET)
            return false
        } else {
            userNameTIL.hideError()
        }
        return true
    }

    private fun checkStringHasSymbol(s: String): Boolean {
        val p = Pattern.compile("[:`!#$%&*()_ +/=;|'\"<>?{}\\[\\]~-]")
        var b: Boolean
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

    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

}