package com.bdjobs.app.Login2


import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
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
import kotlinx.android.synthetic.main.fragment_login2_user_name.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern
import kotlin.error

/**
 * A simple [Fragment] subclass.
 */
class Login2UserNameFragment : android.app.Fragment() {

    private lateinit var login2Communicator: Login2Communicator
    private lateinit var symbol: String
    private lateinit var rootView: View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        rootView = inflater.inflate(R.layout.fragment_login2_user_name, container, false)!!
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        login2Communicator = activity as Login2Communicator
        onClicks()
    }


    private fun onClicks() {
        backBtnIMGV?.setOnClickListener {
            login2Communicator.backButtonClicked()
        }


        nextButtonFAB?.setOnClickListener {
            val userName = usernameTIET?.getString()
            if (validateUserName(userName)) {
                checkUserHasAccount(userName)
            }
        }

        usernameTIET?.easyOnTextChangedListener { charSequence ->
            validateUserName(charSequence.toString())
        }

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }


    private fun checkUserHasAccount(userName: String?) {
        activity?.showProgressBar(loadingProgressBar)
        ApiServiceMyBdjobs.create().getLoginUserDetails(userName).enqueue(object : Callback<LoginUserModel> {

            override fun onFailure(call: Call<LoginUserModel>?, t: Throwable) {
                activity?.stopProgressBar(loadingProgressBar)
                error("onFailure", t)
            }

            override fun onResponse(call: Call<LoginUserModel>?, response: Response<LoginUserModel>?) {
                try {
                    activity?.stopProgressBar(loadingProgressBar)
                    if (response?.body()?.statuscode == api_request_result_code_ok) {
                        useNameTIL.hideError()
                        if (response.body()?.data?.get(0)?.isBlueCollar?.equalIgnoreCase(key_true)!!) {
                            login2Communicator.goToOtpFragment(userName, response.body()?.data?.get(0)?.userId, response.body()?.data?.get(0)?.userFullName, response.body()?.data?.get(0)?.imageurl)
                        } else if (response.body()?.data?.get(0)?.isBlueCollar?.equalIgnoreCase(key_false)!!) {
                            login2Communicator.goToPasswordFragment(userName, response.body()?.data?.get(0)?.userId, response.body()?.data?.get(0)?.userFullName, response.body()?.data?.get(0)?.imageurl)
                        }

                    } else {
                        useNameTIL.showError(response?.body()?.message)
                        //requestFocus(usernameTIET)
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        })

    }

    private fun validateUserName(userName: String?): Boolean {

        try {
            when {
                TextUtils.isEmpty(userName) -> {
                    useNameTIL?.showError("Please enter Username, Email or Mobile No")
                    //requestFocus(usernameTIET)
                    return false
                }
                else -> useNameTIL?.hideError()
            }
        } catch (e: Exception) {
            logException(e)
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
        try {
            if (view.requestFocus()) {
                activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        } catch (e: Exception) {
            logException(e)
        }
    }


}
