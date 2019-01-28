package com.bdjobs.app.ManageResume


import android.os.Bundle
import android.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.EmailResume

import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.error
import kotlinx.android.synthetic.main.fragment_email_resume.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmailResumeFragment : Fragment() {

    lateinit var bdjobsUserSession: BdjobsUserSession
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_email_resume, container, false)
    }

    override fun onResume() {
        super.onResume()
        bdjobsUserSession = BdjobsUserSession(activity)
        // Log.d("isis", bdjobsUserSession. )
        /*if (bdjobsUserSession.resumeUpdateON ==){

        }*/
        cbFAB.setOnClickListener {
            callApi()
        }
    }

    private fun callApi() {
        ApiServiceMyBdjobs.create().getEmailResumeMsg(
             /*   userID = bdjobsUserSession.userId,
                decodeID = bdjobsUserSession.decodId,
                isResumeUpdate = bdjobsUserSession.IsResumeUpdate,
                userEmail = "a[elpoalpl@jhjnhkj.coijoi",
                companyEmail = "rumana.cse7@gmail.com",
                mailSubject = "ewaerwrwerwer",
                application = "",
                fullName = "stertert",
                uploadedCv = "0",
                Jobid = "0"*/

                userID = bdjobsUserSession.userId,
                decodeID = bdjobsUserSession.decodId,
                isResumeUpdate = bdjobsUserSession.IsResumeUpdate,
                userEmail = et_from.text?.toString(),
                companyEmail = et_to.text?.toString(),
                mailSubject = et_Subject.text?.toString(),
                application = "",
                fullName = bdjobsUserSession.fullName,
                uploadedCv = "0",
                Jobid = "0"



        ).enqueue(object : Callback<EmailResume> {
            override fun onFailure(call: Call<EmailResume>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<EmailResume>, response: Response<EmailResume>) {

                toast(response.body()?.message!!)
            }

        })
    }

}
