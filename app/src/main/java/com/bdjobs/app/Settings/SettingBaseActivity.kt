package com.bdjobs.app.Settings

import android.app.Activity
import android.os.Bundle
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.CookieModel
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.logException
import kotlinx.android.synthetic.main.activity_setting_base.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingBaseActivity : Activity() {
    lateinit var bdjobsUserSession: BdjobsUserSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_base)
        bdjobsUserSession = BdjobsUserSession(this@SettingBaseActivity)
        signOutBTN.setOnClickListener {
            logout()
        }
        backIV.setOnClickListener {
            onBackPressed()
        }
    }

    private fun logout() {
        val loadingDialog = indeterminateProgressDialog("Logging out")
        loadingDialog.setCancelable(false)
        loadingDialog.show()
        ApiServiceMyBdjobs.create().logout(userId = bdjobsUserSession.userId,decodeId = bdjobsUserSession.decodId).enqueue(object : Callback<CookieModel> {
            override fun onFailure(call: Call<CookieModel>, t: Throwable) {
                error("onFailure", t)
                loadingDialog.dismiss()
                toast("Something went wrong! Please try again later.")
            }

            override fun onResponse(call: Call<CookieModel>, response: Response<CookieModel>) {
                try {
                    loadingDialog.dismiss()
                    toast(response.body()?.message!!)
                    if (response.body()?.statuscode?.equalIgnoreCase(Constants.api_request_result_code_ok)!!) {
                        bdjobsUserSession.logoutUser()
                    }
                } catch (e: Exception) {
                    logException(e)
                    toast("Something went wrong! Please try again later.")
                }
            }
        })

    }
}
