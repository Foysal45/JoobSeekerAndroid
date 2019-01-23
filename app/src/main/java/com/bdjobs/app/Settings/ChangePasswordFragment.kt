package com.bdjobs.app.Settings


import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.ChangePassword

import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.error
import kotlinx.android.synthetic.main.fragment_change_password.*
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
class ChangePasswordFragment : Fragment() {
    lateinit var bdjobsUserSession: BdjobsUserSession
    private lateinit var communicator: SettingsCommunicator


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onResume() {
        super.onResume()
        bdjobsUserSession = BdjobsUserSession(activity)
        communicator = activity as SettingsCommunicator
        onClick()
    }

    private fun onClick() {
        cbFAB.setOnClickListener {
            ApiServiceMyBdjobs.create().getChangePassword(
                    userId = bdjobsUserSession.userId,
                    decodeId = bdjobsUserSession.decodId,
                    userName = bdjobsUserSession.userName,
                    isResumeUpdate = bdjobsUserSession.IsResumeUpdate,
                    OldPass = et_old_pass.text.toString(),
                    NewPass = et_new_pass.text.toString(),
                    ConfirmPass = et_confirm_pass.text.toString(),
                    isSmMedia = "",
                    packageName = "",
                    packageNameVersion = ""


            ).enqueue(object : Callback<ChangePassword> {
                override fun onFailure(call: Call<ChangePassword>, t: Throwable) {
                    error("onFailure", t)
                }

                override fun onResponse(call: Call<ChangePassword>, response: Response<ChangePassword>) {
                   toast(response.body()?.message!!)
                }

            })
        }
        backIV.setOnClickListener {
            communicator.backButtonPressed()
        }
    }


}
