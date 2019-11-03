package com.bdjobs.app.Settings


import android.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.CookieModel
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.changePassword_Eligibility
import kotlinx.android.synthetic.main.fragment_logout.*
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class LogoutFragment : Fragment() {
    private lateinit var communicator: SettingsCommunicator
    lateinit var bdjobsUserSession: BdjobsUserSession
    val cookieManager: CookieManager = CookieManager.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logout, container, false)
    }

    override fun onResume() {
        super.onResume()
        communicator = activity as SettingsCommunicator
        bdjobsUserSession = BdjobsUserSession(activity)

        notificationSettingsBTN?.setOnClickListener {
            try {
                val intent = Intent()
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                        intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                        intent.putExtra("app_package", activity.packageName)
                        intent.putExtra("app_uid", activity.applicationInfo.uid)
                    }
                }
                activity.startActivity(intent)
            } catch (e: Exception) {
            }
        }

        signOutBTN.setOnClickListener {
            try {
                alert("Are you sure you want to Sign out?") {
                    yesButton {
                        logout()
                    }
                    noButton { dialog ->
                        dialog.dismiss()
                    }
                }.show()
            } catch (e: Exception) {
                logException(e)
            }

        }
        backIV.setOnClickListener {
            communicator.backButtonPressed()
        }
        Log.d("isblue", "isis = ${activity.isBlueCollarUser()}")
        Log.d("isblue", "isis = ${changePassword_Eligibility}")

        if (changePassword_Eligibility == "1") {
            // changepass.show()
            changepass?.visibility = View.VISIBLE
            changepass.setOnClickListener {
                communicator.gotoChangePasswordFragment()
            }
        } else if (changePassword_Eligibility == "0") {
            // changepass.hide()
            changepass?.visibility = View.GONE
        }

        Constants.showNativeAd(ad_small_template, activity)
    }

    private fun logout() {
        val loadingDialog = indeterminateProgressDialog("Logging out")
        loadingDialog.setCancelable(false)
        loadingDialog.show()
        loadingDialog.setCancelable(false)
        ApiServiceMyBdjobs.create().logout(userId = bdjobsUserSession.userId, decodeId = bdjobsUserSession.decodId,deviceID = activity.getDeviceID()).enqueue(object : Callback<CookieModel> {
            override fun onFailure(call: Call<CookieModel>, t: Throwable) {
//                error("onFailure", t)
//                loadingDialog.dismiss()
//                bdjobsUserSession.logoutUser()
//                removeShortcut(activity)
            }

            override fun onResponse(call: Call<CookieModel>, response: Response<CookieModel>) {
                try {
                    loadingDialog.dismiss()
                    bdjobsUserSession.logoutUser()
                    toast(response.body()?.message!!)
                    cookieManager?.removeAllCookies(null)
                    removeShortcut(activity)
                } catch (e: Exception) {
                    logException(e)
                    bdjobsUserSession.logoutUser()
                    cookieManager?.removeAllCookies(null)
                    removeShortcut(activity)
                }
            }
        })

    }


}
