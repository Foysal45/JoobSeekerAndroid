package com.bdjobs.app.AppliedJobs

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.EmployerInteraction

import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.showProgressBar
import com.bdjobs.app.Utilities.stopProgressBar
import kotlinx.android.synthetic.main.fragment_employer_interaction.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class EmployerInteractionFragment : Fragment() {

    private lateinit var bdjobsUserSession: BdjobsUserSession
    private var status: String = ""
    private lateinit var appliedJobsCommunicator: AppliedJobsCommunicator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employer_interaction, container, false)


    }

    override fun onResume() {
        super.onResume()
        bdjobsUserSession = BdjobsUserSession(activity)
        appliedJobsCommunicator = activity as AppliedJobsCommunicator
        onClick()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    private fun onClick() {

        notContractedBTN.setOnClickListener {
            notContractedBTN.iconTint = resources.getColorStateList(R.color.colorWhite)
            notContractedBTN.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            notContractedBTN.setTextColor(resources.getColor(R.color.colorWhite))


            contractedBTN.iconTint = resources.getColorStateList(R.color.colorPrimary)
            contractedBTN.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            contractedBTN.setTextColor(resources.getColor(R.color.colorPrimary))

            hiredBTN.iconTint = resources.getColorStateList(R.color.colorPrimary)
            hiredBTN.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            hiredBTN.setTextColor(resources.getColor(R.color.colorPrimary))
            //     registrationCommunicator.bcGenderSelected("M")
            status = "1"

        }


        contractedBTN.setOnClickListener {
            contractedBTN.iconTint = resources.getColorStateList(R.color.colorWhite)
            contractedBTN.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            contractedBTN.setTextColor(resources.getColor(R.color.colorWhite))


            notContractedBTN.iconTint = resources.getColorStateList(R.color.colorPrimary)
            notContractedBTN.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            notContractedBTN.setTextColor(resources.getColor(R.color.colorPrimary))

            hiredBTN.iconTint = resources.getColorStateList(R.color.colorPrimary)
            hiredBTN.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            hiredBTN.setTextColor(resources.getColor(R.color.colorPrimary))
            //  registrationCommunicator.bcGenderSelected("F")
            status = "2"
        }


        hiredBTN.setOnClickListener {
            hiredBTN.iconTint = resources.getColorStateList(R.color.colorWhite)
            hiredBTN.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
            hiredBTN.setTextColor(resources.getColor(R.color.colorWhite))

            contractedBTN.iconTint = resources.getColorStateList(R.color.colorPrimary)
            contractedBTN.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            contractedBTN.setTextColor(resources.getColor(R.color.colorPrimary))


            notContractedBTN.iconTint = resources.getColorStateList(R.color.colorPrimary)
            notContractedBTN.backgroundTintList = resources.getColorStateList(R.color.colorWhite)
            notContractedBTN.setTextColor(resources.getColor(R.color.colorPrimary))
            status = "3"
            // registrationCommunicator.bcGenderSelected("O")
        }

            EmpInteractionFab.setOnClickListener {
                activity.showProgressBar(loadingProgressBar)
                ApiServiceMyBdjobs.create().getEmpInteraction(
                        userId = bdjobsUserSession.userId,
                        decodeId = bdjobsUserSession.decodId,
                        status = status,
                        experienceId = "null",
                        changeExprience = "0",
                        JobId = appliedJobsCommunicator.getjobID()
                ).enqueue(object : Callback<EmployerInteraction> {

                    override fun onFailure(call: Call<EmployerInteraction>, t: Throwable) {
                        activity.stopProgressBar(loadingProgressBar)
                        error("onFailure", t)
                        Log.d("key", "userid = " + bdjobsUserSession.userId
                                + "decode id = " + bdjobsUserSession.decodId + "status = "
                                + status + "jobid = " + appliedJobsCommunicator.getjobID())
                    }

                    override fun onResponse(call: Call<EmployerInteraction>, response: Response<EmployerInteraction>) {
                        Log.d("key", "userid = " + bdjobsUserSession.userId
                                + "decode id = " + bdjobsUserSession.decodId + "status = "
                                + status + "jobid = " + appliedJobsCommunicator.getjobID()
                        )
                        try {
                            activity.stopProgressBar(loadingProgressBar)
                            if (response.body()?.statuscode == "0" || response.body()?.statuscode == "4")
                                toast("${response.body()?.message}")
                            appliedJobsCommunicator?.backButtonPressed()
                        } catch (e: Exception) {
                            logException(e)
                        }

                    }

                })
            }


        backIMV?.setOnClickListener {
            appliedJobsCommunicator.backButtonPressed()
        }


        /*   bcGenderFAButton.setOnClickListener {

               if (TextUtils.isEmpty(registrationCommunicator.getGender())) {

                   activity.toast("লিঙ্গ নির্বাচন করুন")
               }
           }*/


    }
}
