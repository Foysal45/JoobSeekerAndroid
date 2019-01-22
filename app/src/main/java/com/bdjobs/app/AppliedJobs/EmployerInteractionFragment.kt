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
import androidx.appcompat.widget.AppCompatRadioButton
import com.bdjobs.app.API.ModelClasses.AppliedJobModelExprience
import android.R
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.*


class EmployerInteractionFragment : Fragment() {

    private lateinit var bdjobsUserSession: BdjobsUserSession
    private var status: String = ""
    private var expID : String = ""
    private var populateshowExp = "no"
    private lateinit var appliedJobsCommunicator: AppliedJobsCommunicator
    private var experienceListInteraction: ArrayList<AppliedJobModelExprience>? = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.bdjobs.app.R.layout.fragment_employer_interaction, container, false)


    }

    override fun onResume() {
        super.onResume()
        bdjobsUserSession = BdjobsUserSession(activity)
        appliedJobsCommunicator = activity as AppliedJobsCommunicator
        onClick()
        // experienceListInteraction = appliedJobsCommunicator.getExperience()
        experienceListInteraction = appliedJobsCommunicator.getExperience()
        Log.d("ububua", experienceListInteraction?.toString())

    }

    override fun onDestroy() {
        super.onDestroy()
        toast("$populateshowExp")
        populateshowExp = "no"
        experienceListInteraction?.clear()
    }

    private fun addRadioButton() {
        populateshowExp = "yes"
        var buttons = experienceListInteraction?.size

        foundTV.text = "We found " + buttons?.toString() + " experience from Your Resume"

        buttons = buttons?.minus(1)
        toast("$buttons")
        // val rb = arrayOfNulls<AppCompatRadioButton>(buttons!!)

        // val rgp = findViewById(R.id.radio_group) as RadioGroup
        radio_group.orientation = LinearLayout.VERTICAL

        val rbn = RadioButton(activity)
        val companyTV = TextView(activity)
        for (i in 0..buttons!!) {

            rbn.id = View.generateViewId()
            companyTV.id = View.generateViewId()



            //rbn.text = experienceListInteraction?.get(i)?.designation + "\n" + experienceListInteraction?.get(i)?.companyTVName
            rbn.text = experienceListInteraction?.get(i)?.designation
            companyTV.text = experienceListInteraction?.get(i)?.companyName

            rbn.setOnClickListener {
                toast("msg = "+ experienceListInteraction?.get(i)?.experienceID)
            }


            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            val paramsTV = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            companyTV.setTextSize(14F)
            rbn.setTextSize(16F)

/*            rbn.setTextColor(Color.parseColor("#212121"))
            companyTV.setTextColor(Color.parseColor(" #767676"))
            companyTV.setTextSize(14F)
            rbn.setTextSize(16F)*/

            rbn.layoutParams = params
            companyTV.layoutParams = paramsTV

            params.setMargins(0, 10, 0, 0);
            paramsTV.setMargins(90, 0, 0, 25);

            radio_group.addView(rbn)
            radio_group.addView(companyTV)
        }


        val addExp = RadioButton(activity)
        val expTV = TextView(activity)
        addExp.id = View.generateViewId()
        addExp.text = "Add Experience"
        expTV.id = View.generateViewId()
        expTV.text = "New work experience"
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        val paramsTV = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        expTV.setTextSize(14F)
        addExp.setTextSize(16F)
        addExp.layoutParams = params
        expTV.layoutParams = paramsTV
        params.setMargins(0, 10, 0, 0);
        paramsTV.setMargins(90, 0, 0, 25);
        radio_group.addView(addExp)
        radio_group.addView(expTV)


        addExp.setOnClickListener {
            val updateExpDialog = Dialog(activity)
            updateExpDialog?.setContentView(com.bdjobs.app.R.layout.update_exp_popup)
            updateExpDialog?.setCancelable(true)
            updateExpDialog?.show()
            val cancelBTN = updateExpDialog?.findViewById(com.bdjobs.app.R.id.cancelBTN) as Button
            cancelBTN?.setOnClickListener {
                updateExpDialog.dismiss()
            }
        }

    }

    private fun hiredLayoutShow() {
        scrollView.visibility = View.VISIBLE
    }

    private fun hiredLayoutHide() {
        scrollView.visibility = View.GONE
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    private fun onClick() {

        notContractedBTN.setOnClickListener {
            notContractedBTN.iconTint = resources.getColorStateList(com.bdjobs.app.R.color.colorWhite)
            notContractedBTN.backgroundTintList = resources.getColorStateList(com.bdjobs.app.R.color.colorPrimary)
            notContractedBTN.setTextColor(resources.getColor(com.bdjobs.app.R.color.colorWhite))


            contractedBTN.iconTint = resources.getColorStateList(com.bdjobs.app.R.color.colorPrimary)
            contractedBTN.backgroundTintList = resources.getColorStateList(com.bdjobs.app.R.color.colorWhite)
            contractedBTN.setTextColor(resources.getColor(com.bdjobs.app.R.color.colorPrimary))

            hiredBTN.iconTint = resources.getColorStateList(com.bdjobs.app.R.color.colorPrimary)
            hiredBTN.backgroundTintList = resources.getColorStateList(com.bdjobs.app.R.color.colorWhite)
            hiredBTN.setTextColor(resources.getColor(com.bdjobs.app.R.color.colorPrimary))
            //     registrationCommunicator.bcGenderSelected("M")
            status = "1"
            hiredLayoutHide()


        }


        contractedBTN.setOnClickListener {
            contractedBTN.iconTint = resources.getColorStateList(com.bdjobs.app.R.color.colorWhite)
            contractedBTN.backgroundTintList = resources.getColorStateList(com.bdjobs.app.R.color.colorPrimary)
            contractedBTN.setTextColor(resources.getColor(com.bdjobs.app.R.color.colorWhite))


            notContractedBTN.iconTint = resources.getColorStateList(com.bdjobs.app.R.color.colorPrimary)
            notContractedBTN.backgroundTintList = resources.getColorStateList(com.bdjobs.app.R.color.colorWhite)
            notContractedBTN.setTextColor(resources.getColor(com.bdjobs.app.R.color.colorPrimary))

            hiredBTN.iconTint = resources.getColorStateList(com.bdjobs.app.R.color.colorPrimary)
            hiredBTN.backgroundTintList = resources.getColorStateList(com.bdjobs.app.R.color.colorWhite)
            hiredBTN.setTextColor(resources.getColor(com.bdjobs.app.R.color.colorPrimary))
            //  registrationCommunicator.bcGenderSelected("F")
            status = "2"
            hiredLayoutHide()

        }


        hiredBTN.setOnClickListener {
            hiredBTN.iconTint = resources.getColorStateList(com.bdjobs.app.R.color.colorWhite)
            hiredBTN.backgroundTintList = resources.getColorStateList(com.bdjobs.app.R.color.colorPrimary)
            hiredBTN.setTextColor(resources.getColor(com.bdjobs.app.R.color.colorWhite))

            contractedBTN.iconTint = resources.getColorStateList(com.bdjobs.app.R.color.colorPrimary)
            contractedBTN.backgroundTintList = resources.getColorStateList(com.bdjobs.app.R.color.colorWhite)
            contractedBTN.setTextColor(resources.getColor(com.bdjobs.app.R.color.colorPrimary))


            notContractedBTN.iconTint = resources.getColorStateList(com.bdjobs.app.R.color.colorPrimary)
            notContractedBTN.backgroundTintList = resources.getColorStateList(com.bdjobs.app.R.color.colorWhite)
            notContractedBTN.setTextColor(resources.getColor(com.bdjobs.app.R.color.colorPrimary))
            status = "3"

            hiredLayoutShow()
            if (populateshowExp == "no") {
                addRadioButton()
            }

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
