package com.bdjobs.app.AppliedJobs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.AppliedJobModel
import com.bdjobs.app.API.ModelClasses.AppliedJobModelExprience
import com.bdjobs.app.API.ModelClasses.EmployerInteraction
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.employmentHistory.EmploymentHistoryActivity
import kotlinx.android.synthetic.main.fragment_employer_interaction.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EmployerInteractionFragment : androidx.fragment.app.Fragment() {

    private lateinit var bdjobsUserSession: BdjobsUserSession

    private var status: String = ""
    private var expID: String = "0"
    //  private var populateshowExp = "no"
    private lateinit var appliedJobsCommunicator: AppliedJobsCommunicator
    private var experienceListInteraction: ArrayList<AppliedJobModelExprience>? = ArrayList()
    private var hire = "0"
    private var contracted = "0"
    private var Ncontracted = "0"
    private var changeEXP = "0"
    //val buttons = 5
    var jobIDapplied = ""

    fun expAPIcall(activityDate: String) {
        //   populateshowExp = "no"
        experienceListInteraction?.clear()
        loadingProgressBar.visibility = View.VISIBLE
        try {
            ApiServiceMyBdjobs.create().getAppliedJobs(
                    userId = bdjobsUserSession.userId,
                    decodeId = bdjobsUserSession.decodId,
                    isActivityDate = activityDate,
                    pageNumber = "1",
                    itemsPerPage = "20"
            ).enqueue(object : Callback<AppliedJobModel> {
                override fun onFailure(call: Call<AppliedJobModel>, t: Throwable) {
                    activity?.toast("${t.message}")
                }

                override fun onResponse(call: Call<AppliedJobModel>, response: Response<AppliedJobModel>) {
                    response.body()?.common?.totalNumberOfApplication
                    //Log.d("totalrecords", "totalrecords  = $totalRecords")
                    //Log.d("expEXP", "expexperienceListInteraction=${experienceListInteraction}")
                    try {
                        //Log.d("callAppliURl", "url: ${call?.request()} and ")
                        if (!response.body()?.data.isNullOrEmpty()) {

                            experienceListInteraction?.addAll(response.body()?.exprience as List<AppliedJobModelExprience>)
                            //Log.d("expEXP", "---${response.body()?.exprience}")


                            addRadioButton()
                            onClick()

                        }
                        loadingProgressBar.visibility = View.GONE
                        //Log.d("tot", "total = $totalRecords")


                    } catch (e: Exception) {
                        logException(e)
                    }

                }

            })
        } catch (e: Exception) {
            logException(e)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_employer_interaction, container, false)


    }

    override fun onPause() {
        super.onPause()
        //Log.d("calling", " onPause")
    }

    override fun onStop() {
        super.onStop()
        //Log.d("calling", " onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //Log.d("calling", " onDestroyView")
    }

    override fun onDetach() {
        super.onDetach()
        //Log.d("calling", " onDetach")
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        //Log.d("calling", " onResume")
        //Log.d("calling", " hire = $hire, contracted = $contracted, notcontracted = $Ncontracted")
        bdjobsUserSession = BdjobsUserSession(requireActivity())
        appliedJobsCommunicator = activity as AppliedJobsCommunicator
        //----------------------------------
        //   onClick()
        // experienceListInteraction = appliedJobsCommunicator.getExperience()
        companyTV.text = appliedJobsCommunicator.getCompany()
        positionTV.text = appliedJobsCommunicator.getTitle2()
        designation_TV_below.text = "Please select the employer that hired you for" +
                " " + appliedJobsCommunicator.getTitle2().trim()
        val getStatus = appliedJobsCommunicator.getStatus()

        //Log.d("calling", " onDestroy + $getStatus")

        when (getStatus) {
            "1" -> notcontracted()
            "2" -> contracted()
            "3" -> hired()
        }

        expAPIcall("0")
        EmpInteractionFab?.setEnabled(false)
        //  EmpInteractionFab?.setBackgroundColor(Color.parseColor("#757575"))
        //Log.d("expEXP", "hire = $hire")
        if (hire.equals("1") || contracted.equals("1") || Ncontracted.equals("1")) {
            EmpInteractionFab?.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#13A10E")))
            EmpInteractionFab?.setEnabled(true)
        } else {
            EmpInteractionFab?.setEnabled(false)
            EmpInteractionFab?.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#88D086")))

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //Log.d("calling", " onDestroy")
        hire = "0"
        contracted = "0"
        Ncontracted = "0"
        //toast("$populateshowExp")
        /* populateshowExp = "no"
         experienceListInteraction?.clear()*/
    }

    @SuppressLint("SetTextI18n")
    private fun addRadioButton() {
        // val rgp = findViewById(com.bdjobs.app.R.id.radio_group) as RadioGroup
        //   populateshowExp = "yes"
        var buttonsize = experienceListInteraction?.size
        //Log.d("expEXP", "button size = $buttonsize")
        radio_group?.removeAllViews()
        //foundTV.text = "We found " + buttonsize?.toString() + " experience from Your Resume"

        buttonsize?.let {
            buttonsize = buttonsize?.minus(1)
            buttonsize?.let {
                for (i in 0..buttonsize!!) {
                    val designationradioBTN = RadioButton(activity)
                    val companyTV = TextView(activity)
                    designationradioBTN.id = View.generateViewId()
                    companyTV.id = View.generateViewId()
                    designationradioBTN.text = experienceListInteraction?.get(i)?.designation?.trim()
                    companyTV.text = experienceListInteraction?.get(i)?.companyName?.trim()
                    val jobID = experienceListInteraction?.get(i)?.jobid?.trim()
                    val jobIDApplied = appliedJobsCommunicator.getjobID().trim()
                    jobIDapplied = jobIDApplied


                    val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    val paramsTV = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    designationradioBTN.setTextSize(16F)
                    companyTV.setTextSize(14F)
                    designationradioBTN.layoutParams = params
                    companyTV.layoutParams = paramsTV
                    params.setMargins(0, 10, 0, 0)
                    paramsTV.setMargins(80, 0, 0, 25)

                    radio_group?.addView(designationradioBTN)
                    radio_group?.addView(companyTV)

                    if (jobID?.equalIgnoreCase(jobIDApplied)!!) {
                        //Log.d("matched", " jobID + $jobID appliedjobid = $jobIDApplied")
                        designationradioBTN.isChecked = true
                        designationradioBTN.isEnabled = false
                    }


                    //Log.d("matched", " exp id = ${experienceListInteraction?.get(i)?.jobid?.trim()}")


                    if (!experienceListInteraction?.get(i)?.jobid?.trim()?.equalIgnoreCase("0")!!){
                        designationradioBTN.isEnabled = false
                    }

                    designationradioBTN.setOnClickListener {
                        expID = experienceListInteraction?.get(i)?.experienceID!!
                        //Log.d("matched", " exp id = $expID")
                        changeEXP = "1"
                        // toast(experienceListInteraction?.get(i)?.designation!! + " = " + expID)
                    }
                }
            }
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
        params.setMargins(0, 10, 0, 0)
        paramsTV.setMargins(80, 0, 0, 25)
        radio_group?.addView(addExp)
        radio_group?.addView(expTV)


        addExp.setOnClickListener {
            val updateExpDialog = Dialog(requireActivity())
            updateExpDialog.setContentView(R.layout.update_exp_popup)
            updateExpDialog.setCancelable(true)
            updateExpDialog.show()
            val cancelBTN = updateExpDialog.findViewById(com.bdjobs.app.R.id.cancelBTN) as Button
            val yesBTN = updateExpDialog.findViewById(R.id.updateBTN) as Button
            yesBTN.setOnClickListener {
                activity?.startActivity<EmploymentHistoryActivity>(
                        "name" to "null",
                        "emp_his_add" to "addDirect")
                updateExpDialog.dismiss()
            }
            cancelBTN.setOnClickListener {
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

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun notcontracted() {
        notContractedBTN.iconTint = ContextCompat.getColorStateList(requireContext(), R.color.colorWhite)
        notContractedBTN.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
        notContractedBTN.setTextColor(ContextCompat.getColor(requireContext(),  R.color.colorWhite))


        contractedBTN.iconTint = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
        contractedBTN.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorWhite)
        contractedBTN.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))

        hiredBTN.iconTint = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
        hiredBTN.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorWhite)
        hiredBTN.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        //     registrationCommunicator.bcGenderSelected("M")
        status = "1"
        hiredLayoutHide()
        EmpInteractionFab?.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#13A10E")))
        EmpInteractionFab?.setEnabled(true)
        Ncontracted = "1"
        contracted = "0"
        hire = "0"
    }

    private fun contracted() {
//        contractedBTN.iconTint = resources.getColorStateList(com.bdjobs.app.R.color.colorWhite)
//        contractedBTN.backgroundTintList = resources.getColorStateList(com.bdjobs.app.R.color.colorPrimary)
//        contractedBTN.setTextColor(resources.getColor(com.bdjobs.app.R.color.colorWhite))

        contractedBTN.iconTint = (ContextCompat.getColorStateList(requireActivity(),R.color.colorWhite))
        contractedBTN.backgroundTintList = (ContextCompat.getColorStateList(requireActivity(),R.color.colorPrimary))
        contractedBTN.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorWhite))


        notContractedBTN.iconTint = ContextCompat.getColorStateList(requireActivity(), R.color.colorPrimary)
        notContractedBTN.backgroundTintList = ContextCompat.getColorStateList(requireActivity(), R.color.colorWhite)
        notContractedBTN.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))

        hiredBTN.iconTint = ContextCompat.getColorStateList(requireActivity(), R.color.colorPrimary)
        hiredBTN.backgroundTintList = ContextCompat.getColorStateList(requireActivity(), R.color.colorWhite)
        hiredBTN.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
        //  registrationCommunicator.bcGenderSelected("F")
        status = "2"
        hiredLayoutHide()
        EmpInteractionFab?.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#13A10E")))
        EmpInteractionFab?.setEnabled(true)
        Ncontracted = "0"
        contracted = "1"
        hire = "0"

    }

    private fun hired() {
        hiredBTN.iconTint = ContextCompat.getColorStateList(requireActivity(), R.color.colorWhite)
        hiredBTN.backgroundTintList = ContextCompat.getColorStateList(requireActivity(), R.color.colorPrimary)
        hiredBTN.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhite))

        contractedBTN.iconTint = ContextCompat.getColorStateList(requireActivity(), R.color.colorPrimary)
        contractedBTN.backgroundTintList = ContextCompat.getColorStateList(requireActivity(), R.color.colorWhite)
        contractedBTN.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))


        notContractedBTN.iconTint = ContextCompat.getColorStateList(requireActivity(), R.color.colorPrimary)
        notContractedBTN.backgroundTintList = ContextCompat.getColorStateList(requireActivity(), R.color.colorWhite)
        notContractedBTN.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
        status = "3"
        Ncontracted = "0"
        contracted = "0"
        hire = "1"

        hiredLayoutShow()
//            if (populateshowExp == "no") {
//                //addRadioButton()
//            }
        EmpInteractionFab?.setEnabled(true)
        EmpInteractionFab?.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#13A10E")))


        // registrationCommunicator.bcGenderSelected("O")

    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun onClick() {

        notContractedBTN.setOnClickListener {
            notContractedBTN.iconTint = ContextCompat.getColorStateList(requireContext(), R.color.colorWhite)
            notContractedBTN.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
            notContractedBTN.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhite))


            contractedBTN.iconTint = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
            contractedBTN.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorWhite)
            contractedBTN.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))

            hiredBTN.iconTint = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
            hiredBTN.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorWhite)
            hiredBTN.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            //     registrationCommunicator.bcGenderSelected("M")
            status = "1"
            hiredLayoutHide()
            EmpInteractionFab?.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#13A10E")))
            EmpInteractionFab?.setEnabled(true)
            Ncontracted = "1"
            contracted = "0"
            hire = "0"
        }


        contractedBTN.setOnClickListener {
            contractedBTN.iconTint = ContextCompat.getColorStateList(requireContext(), R.color.colorWhite)
            contractedBTN.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
            contractedBTN.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhite))


            notContractedBTN.iconTint = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
            notContractedBTN.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorWhite)
            notContractedBTN.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))

            hiredBTN.iconTint = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
            hiredBTN.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorWhite)
            hiredBTN.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            //  registrationCommunicator.bcGenderSelected("F")
            status = "2"
            hiredLayoutHide()
            EmpInteractionFab?.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#13A10E")))
            EmpInteractionFab?.setEnabled(true)
            Ncontracted = "0"
            contracted = "1"
            hire = "0"
        }


        hiredBTN.setOnClickListener {
            hiredBTN.iconTint = ContextCompat.getColorStateList(requireContext(), R.color.colorWhite)
            hiredBTN.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
            hiredBTN.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhite))

            contractedBTN.iconTint = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
            contractedBTN.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorWhite)
            contractedBTN.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))


            notContractedBTN.iconTint = ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)
            notContractedBTN.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.colorWhite)
            notContractedBTN.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            status = "3"
            Ncontracted = "0"
            contracted = "0"
            hire = "1"

            hiredLayoutShow()
//            if (populateshowExp == "no") {
//                //addRadioButton()
//            }
            EmpInteractionFab?.setEnabled(true)
            EmpInteractionFab?.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#13A10E")))


            // registrationCommunicator.bcGenderSelected("O")
        }

        EmpInteractionFab.setOnClickListener {
            activity?.showProgressBar(loadingProgressBar)
            /* println("hbghkjgfg " + "jobid = " + appliedJobsCommunicator.getjobID() + " " + "userid = " + bdjobsUserSession.userId + " " + "decdeid = " + bdjobsUserSession.decodId + " " +
                     "exp = " + expID + " " + "status = " + status + " " + "changeexp = " + 0)*/

            ApiServiceMyBdjobs.create().getEmpInteraction(
                    userId = bdjobsUserSession.userId,
                    decodeId = bdjobsUserSession.decodId,
                    status = status,
                    experienceId = expID,
                    changeExprience = changeEXP,
                    JobId = jobIDapplied
            ).enqueue(object : Callback<EmployerInteraction> {

                override fun onFailure(call: Call<EmployerInteraction>, t: Throwable) {
                    try {
                        activity?.stopProgressBar(loadingProgressBar)
                        error("onFailure", t)
                    } catch (e: Exception) {
                        logException(e)
                    }
                    /* //Log.d("key", "userid = " + bdjobsUserSession.userId
                             + "decode id = " + bdjobsUserSession.decodId + "status = "
                             + status + "jobid = " + appliedJobsCommunicator.getjobID()
                             + "experienceid = " + expID)*/
                }

                override fun onResponse(call: Call<EmployerInteraction>, response: Response<EmployerInteraction>) {
                    /*Log.d("key", "userid = " + bdjobsUserSession.userId
                            + "decode id = " + bdjobsUserSession.decodId + "status = "
                            + status + "jobid = " + appliedJobsCommunicator.getjobID()
                            + "experienceid = " + expID
                            + "changeEXP = " + changeEXP
                    )*/
                    try {
                        activity?.stopProgressBar(loadingProgressBar)
                        if (response.body()?.statuscode == "0" || response.body()?.statuscode == "4")
                            activity?.toast("${response.body()?.message}")
                        appliedJobsCommunicator.backButtonPressed()
                        //---
                    } catch (e: Exception) {
                        logException(e)
                    }

                }

            })
        }


        backIMV?.setOnClickListener {
          //  appliedJobsCommunicator.backButtonPressed()
            requireActivity().supportFragmentManager.popBackStack()
        }


        /*   bcGenderFAButton.setOnClickListener {

               if (TextUtils.isEmpty(registrationCommunicator.getGender())) {

                   activity.toast("লিঙ্গ নির্বাচন করুন")
               }
           }*/


    }
}
