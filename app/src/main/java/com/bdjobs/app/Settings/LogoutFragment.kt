package com.bdjobs.app.Settings


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import androidx.fragment.app.Fragment
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.CookieModel
import com.bdjobs.app.Ads.Ads
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants.Companion.changePassword_Eligibility
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.getDeviceID
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.removeShortcut
import com.bdjobs.app.Web.WebActivity
import com.bdjobs.app.sms.SmsBaseActivity
import com.google.android.gms.ads.AdListener
import kotlinx.android.synthetic.main.fragment_logout.*
import org.jetbrains.anko.noButton
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogoutFragment : Fragment() {
    private lateinit var communicator: SettingsCommunicator
    lateinit var bdjobsUserSession: BdjobsUserSession
    val cookieManager: CookieManager = CookieManager.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        bdjobsUserSession = BdjobsUserSession(requireActivity())

        if (bdjobsUserSession.isCvPosted!=null && bdjobsUserSession.isCvPosted!!.equalIgnoreCase("true")) {
            resumePrivacySettingsBTN.visibility = View.VISIBLE
        } else {
            resumePrivacySettingsBTN.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        communicator = activity as SettingsCommunicator

        notificationSettingsBTN?.setOnClickListener {
            try {
                val intent = Intent()
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                        intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                        intent.putExtra("app_package", requireActivity().packageName)
                        intent.putExtra("app_uid", requireActivity().applicationInfo.uid)
                    }
                }
                requireActivity().startActivity(intent)
            } catch (e: Exception) {
            }
        }



        resumePrivacySettingsBTN.setOnClickListener {
            communicator.gotoResumePrivacyFragment()
        }

        smsSettingsBTN?.setOnClickListener {
            requireContext().startActivity<SmsBaseActivity>("from" to "settings")
        }

        setUserIdBTN.setOnClickListener{
            requireContext().startActivity<WebActivity>("url" to "https://mybdjobs.bdjobs.com/mybdjobs/set_userId/set_userID_cart.asp", "from" to "setUserId")
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
        //Log.d("isblue", "isis = ${activity.isBlueCollarUser()}")
        //Log.d("isblue", "isis = ${changePassword_Eligibility}")

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

        Ads.showNativeAd(ad_small_template, requireActivity())
    }

    private fun logout() {
        val loadingDialog = indeterminateProgressDialog("Logging out")
        loadingDialog.setCancelable(false)
        loadingDialog.show()
        loadingDialog.setCancelable(false)
        ApiServiceMyBdjobs.create().logout(userId = bdjobsUserSession.userId, decodeId = bdjobsUserSession.decodId, deviceID = requireActivity().getDeviceID()).enqueue(object : Callback<CookieModel> {
            override fun onFailure(call: Call<CookieModel>, t: Throwable) {
//                error("onFailure", t)
//                loadingDialog.dismiss()
//                bdjobsUserSession.logoutUser()
//                removeShortcut(activity)
            }

            override fun onResponse(call: Call<CookieModel>, response: Response<CookieModel>) {
                try {
                    loadingDialog.dismiss()
                    if (Ads.mInterstitialAd != null && Ads.mInterstitialAd?.isLoaded!!) {
                        Ads.mInterstitialAd!!.adListener = object : AdListener() {
                            override fun onAdLoaded() {
                                // Code to be executed when an ad finishes loading.
                                //Log.d("mInterstitialAd", "Ad Loaded")
                            }

                            override fun onAdFailedToLoad(errorCode: Int) {
                                // Code to be executed when an ad request fails.
                            }

                            override fun onAdOpened() {
                                // Code to be executed when the ad is displayed.
                            }

                            override fun onAdClicked() {
                                // Code to be executed when the user clicks on an ad.
                            }

                            override fun onAdLeftApplication() {
                                // Code to be executed when the user has left the app.
                            }

                            override fun onAdClosed() {
                                bdjobsUserSession.logoutUser()

                            }
                        }
                        Ads.mInterstitialAd?.show()
                    } else {
                        bdjobsUserSession.cancelAlarms()
                        bdjobsUserSession.logoutUser()

                    }
                    toast(response.body()?.message!!)
                    cookieManager.removeAllCookies(null)
                    removeShortcut(requireActivity())

                } catch (e: Exception) {
                    logException(e)
                    bdjobsUserSession.logoutUser()
                    cookieManager.removeAllCookies(null)
                    removeShortcut(requireActivity())
                }
            }
        })

    }


}
