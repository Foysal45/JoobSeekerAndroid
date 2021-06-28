package com.bdjobs.app.Settings


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.ChangePassword
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.easyOnTextChangedListener
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.logException
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_change_password.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class ChangePasswordFragment : Fragment() {
    lateinit var bdjobsUserSession: BdjobsUserSession
    private lateinit var communicator: SettingsCommunicator
    lateinit var symbol: String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onResume() {
        super.onResume()
        bdjobsUserSession = BdjobsUserSession(requireActivity())
        communicator = requireActivity() as SettingsCommunicator
        tv_username.text = bdjobsUserSession.userName
        backIV.setOnClickListener {
            communicator.backButtonPressed()
        }

        cbFAB.setOnClickListener {
            //Log.d("value", validateOldPassword2().toString())

            login()
        }
        /*     if (oldpassword_TIL.isNotEmpty()){
                // onClick()
                 login()
             }*/

        et_old_pass.easyOnTextChangedListener {
            validateOldPassword2()
        }
        et_new_pass.easyOnTextChangedListener {
            validateNewPassword2()

        }
        et_confirm_pass.easyOnTextChangedListener {
            validateConfirmPassword2()

        }
    }


    private fun login() {
        if (!validateOldPassword2()) {
            return

        }

        if (!validateNewPassword2()) {
            return
        }

        if (!validateConfirmPassword2()) {
            return
        }

        networkCall()
        //  Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show()
    }


    private fun networkCall() {
        ApiServiceMyBdjobs.create().getChangePassword(
                userId = bdjobsUserSession.userId,
                decodeId = bdjobsUserSession.decodId,
                userName = bdjobsUserSession.userName,
                isResumeUpdate = bdjobsUserSession.IsResumeUpdate,
                OldPass = et_old_pass.text.toString(),
                NewPass = et_new_pass.text.toString(),
                ConfirmPass = et_confirm_pass.text.toString(),
                isSmMedia = "false",
                packageName = "",
                packageNameVersion = ""
//

        ).enqueue(object : Callback<ChangePassword> {
            override fun onFailure(call: Call<ChangePassword>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<ChangePassword>, response: Response<ChangePassword>) {
                try {
                    // toast(response.body()?.message!!)

                /*    //Log.d("msg", "user = " + bdjobsUserSession.userId +
                            "decode = " + bdjobsUserSession.decodId

                    )*/
                    requireActivity().toast(response.body()?.message!!)
                    if (response.body()?.statuscode == "0" || response.body()?.statuscode == "4") {
                        //Log.d("msg", response.body()?.message)
                        // toast(response.body()?.message!!)
                        if (response.body()?.message == "The information has been updated Successfully") {
                            communicator.backButtonPressed()
                        }


                    }
                } catch (e: Exception) {
                    logException(e)
                }

            }

        })
    }


    private fun validateOldPassword2(): Boolean {
        val pass = et_old_pass.text.toString()
        if (et_old_pass.text.toString().trim { it <= ' ' }.isEmpty()) {
            oldpassword_TIL.isErrorEnabled = true
            oldpassword_TIL.error = getString(R.string.err_msg_password_not_empty)
            requestFocus(et_old_pass)
            return false
        }
 //       else if (checkStringHasSymbol(pass)) {
//            oldpassword_TIL.setErrorEnabled(true)
//            //  oldpassword_TIL.setError("Password can not contain $symbol")
//            oldpassword_TIL.setError(getString(R.string.err_msg_password_use))
//            requestFocus(et_old_pass)
//            return false
  //      }
    else if (pass.trim { it <= ' ' }.length < 8 || pass.trim { it <= ' ' }.length > 12) {
            oldpassword_TIL.isErrorEnabled = true
            oldpassword_TIL.error = getString(R.string.err_msg_password_limit)
            requestFocus(et_old_pass)
            return false
        } else {
            oldpassword_TIL.isErrorEnabled = false
        }

        return true
    }

    private fun validateNewPassword2(): Boolean {
        val pass = et_new_pass.text.toString()
        if (et_new_pass.text.toString().trim { it <= ' ' }.isEmpty()) {
            newpassword_TIL.isErrorEnabled = true
            newpassword_TIL.error = getString(R.string.err_msg_password_not_empty)
            requestFocus(et_new_pass)
            return false
        }
 //       else if (checkStringHasSymbol(pass)) {
         /*   newpassword_TIL.setErrorEnabled(true)
            // newpassword_TIL.setError("Password can not contain $symbol")
            newpassword_TIL.setError(getString(R.string.err_msg_password_use))
            requestFocus(et_new_pass)
            return false*/
//    }
        else if (pass.trim { it <= ' ' }.length < 8 || pass.trim { it <= ' ' }.length > 12) {
            newpassword_TIL.isErrorEnabled = true
            newpassword_TIL.error = getString(R.string.err_msg_password_limit)
            requestFocus(et_new_pass)
            return false
        } else {
            newpassword_TIL.isErrorEnabled = false
        }

        return true
    }

    private fun validateConfirmPassword2(): Boolean {
        val pass = et_confirm_pass.text.toString()
        if (et_confirm_pass.text.toString().trim { it <= ' ' }.isEmpty()) {
            confirmpassword_TIL.isErrorEnabled = true
            confirmpassword_TIL.error = getString(R.string.err_msg_password_confirm_not_empty)
            requestFocus(et_confirm_pass)
            return false
        } else if (!pass.equals(et_new_pass.text.toString(), true)) {
            // return enableValidation(confirmpassword_TIL,et_confirm_pass, getString(com.bdjobs.app.R.string.err_msg_password_not_match) )
            confirmpassword_TIL.isErrorEnabled = true
            confirmpassword_TIL.error = getString(R.string.err_msg_password_not_match)
            requestFocus(et_confirm_pass)
            return false

        }


/*        else if (checkStringHasSymbol(pass)) {
            confirmpassword_TIL.setErrorEnabled(true)
            confirmpassword_TIL.setError(getString(R.string.err_msg_password_use))
            requestFocus(et_confirm_pass)
            return false
        } */
        else if (pass.trim { it <= ' ' }.length < 8 || pass.trim { it <= ' ' }.length > 12) {
            confirmpassword_TIL.isErrorEnabled = true
            confirmpassword_TIL.error = "Password should be 8 to 12 character long!"
            requestFocus(et_confirm_pass)
            return false
        } else {
            confirmpassword_TIL.isErrorEnabled = false
        }

        return true
    }


    private fun enableValidation(til: TextInputLayout, tied: TextInputEditText, errorText: String): Boolean {
        til.isErrorEnabled = true
        til.error = errorText
        requestFocus(tied)
        return false
    }

    private fun checkStringHasSymbol(s: String): Boolean {

        val p = Pattern.compile("[:`!@#$%&*()_ +/=;|'\"<>?{}\\[\\]~-]")

        var b = false
        for (element in s) {
            symbol = element.toString()
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
            // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        }
    }


}
