package com.bdjobs.app.Settings

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.CookieModel
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.utilities.*
import kotlinx.android.synthetic.main.activity_setting_base.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingBaseActivity : FragmentActivity(), SettingsCommunicator {

    private val logoutFragment = LogoutFragment()

    private var from:String?= null

    override fun gotoChangePasswordFragment() {
        transitFragment(changePasswordFragment, R.id.fragmentHolder, true)
    }

    override fun gotoResumePrivacyFragment() {
        transitFragment(resumePrivacyFragment,R.id.fragmentHolder,true)
    }

    override fun backButtonPressed() {
        onBackPressed()
    }

    lateinit var bdjobsUserSession: BdjobsUserSession
    private val changePasswordFragment = ChangePasswordFragment()
    private val resumePrivacyFragment = ResumePrivacyFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_base)
        bdjobsUserSession = BdjobsUserSession(this@SettingBaseActivity)
        from = intent.getStringExtra("from")

        if (from=="dashboard") transitFragment(resumePrivacyFragment,R.id.fragmentHolder,false)
        else transitFragment(logoutFragment, R.id.fragmentHolder,false)
        /*    signOutBTN.setOnClickListener {
                logout()
            }
            backIV.setOnClickListener {
                onBackPressed()
            }
            changepass.setOnClickListener {

                transitFragment(changePasswordFragment, R.id.fragmentHolder, true)
            }*/
    }

    private fun logout() {
        val loadingDialog = indeterminateProgressDialog("Logging out")
        loadingDialog.setCancelable(false)
        loadingDialog.show()
        ApiServiceMyBdjobs.create().logout(userId = bdjobsUserSession.userId, decodeId = bdjobsUserSession.decodId, deviceID = applicationContext.getDeviceID()).enqueue(object : Callback<CookieModel> {
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
