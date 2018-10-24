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
import com.bdjobs.app.Utilities.CommonMethods.Companion.showProgressBar
import com.bdjobs.app.Utilities.CommonMethods.Companion.stopProgressBar
import com.bdjobs.app.Utilities.Constants.Companion.api_request_result_code_ok
import com.bdjobs.app.Utilities.getString
import kotlinx.android.synthetic.main.fragment_login_landing.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class LoginLandingFragment : Fragment() {

    lateinit var loginCommunicator: LoginCommunicator
    lateinit var symbol: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_login_landing, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginCommunicator = activity as LoginCommunicator
        userNameTIET.addTextChangedListener(LoginTextWatcher(userNameTIET))
        onClicks()
    }


    private fun onClicks() {
        backBtnIMGV.onClick { loginCommunicator?.backButtonClicked() }
        nextButtonFAB.onClick {
            doLogin()
        }
    }

    private fun doLogin() {
        if (!validateUserName()) {
            return
        } else {



            val userName = userNameTIET.getString()

            showProgressBar(userNameProgressBar, activity)

            ApiServiceMyBdjobs.create().getLoginUserDetails(userName).enqueue(object : Callback<LoginUserModel> {


                override fun onFailure(call: Call<LoginUserModel>?, t: Throwable?) {
                    stopProgressBar(userNameProgressBar, activity)
                }

                override fun onResponse(call: Call<LoginUserModel>?, response: Response<LoginUserModel>?) {
                    stopProgressBar(userNameProgressBar, activity)
                    if (response?.body()?.statuscode == api_request_result_code_ok) {
                        userNameTIL.isErrorEnabled = false

                    } else {
                        userNameTIL.isErrorEnabled = true
                        userNameTIL.error = response?.body()?.message
                        requestFocus(userNameTIET)
                    }
                }
            })
        }
    }


    private fun validateUserName(): Boolean {
        val user = userNameTIET.text.toString()
        if (TextUtils.isEmpty(user)) {
            userNameTIL.isErrorEnabled = true
            userNameTIL.error = getString(R.string.field_empty_error_message_common)
            requestFocus(userNameTIET)
            return false
        } else if (checkStringHasSymbol(user)) {
            userNameTIL.isErrorEnabled = true
            userNameTIL.error = "Username can not contain $symbol"
            requestFocus(userNameTIET)
            return false
        } else if (user.trim { it <= ' ' }.length < 5 || user.trim { it <= ' ' }.length > 15) {
            userNameTIL.isErrorEnabled = true
            userNameTIL.error = "Username should be 5 to 15 character long!"
            requestFocus(userNameTIET)
            return false
        } else {
            userNameTIL.isErrorEnabled = false
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

    private inner class LoginTextWatcher(private val view: View) : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            when (view.id) {
                R.id.userNameTIET -> validateUserName()
            }
        }
    }

}