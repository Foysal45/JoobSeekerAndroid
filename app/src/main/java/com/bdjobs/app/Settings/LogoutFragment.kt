package com.bdjobs.app.Settings


import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.CookieModel

import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.logException
import kotlinx.android.synthetic.main.fragment_logout.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logout, container, false)
    }

    override fun onResume() {
        super.onResume()
        communicator = activity as SettingsCommunicator
        bdjobsUserSession = BdjobsUserSession(activity)
        signOutBTN.setOnClickListener {
            logout()
        }
        backIV.setOnClickListener {
            communicator.backButtonPressed()
        }
        changepass.setOnClickListener {
            communicator.gotoChangePasswordFragment()
        }
    }

    private fun logout() {
        val loadingDialog = indeterminateProgressDialog("Logging out")
        loadingDialog.setCancelable(false)
        loadingDialog.show()
        ApiServiceMyBdjobs.create().logout(userId = bdjobsUserSession.userId, decodeId = bdjobsUserSession.decodId).enqueue(object : Callback<CookieModel> {
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
