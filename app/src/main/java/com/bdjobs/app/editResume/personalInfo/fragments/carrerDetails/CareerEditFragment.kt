package com.bdjobs.app.editResume.personalInfo.fragments.carrerDetails

import android.app.Activity
import android.app.Dialog
import android.app.Fragment
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.adapters.models.Ca_DataItem
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_career_edit.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CareerEditFragment : Fragment() {

    private lateinit var personalInfo: PersonalInfo
    private lateinit var session: BdjobsUserSession
    private var jobLevel: String? = ""
    private var jobNature: String? = ""
    lateinit var dataStorage: DataStorage
    private lateinit var dialog: Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_career_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dataStorage = DataStorage(activity)
        session = BdjobsUserSession(activity)
        personalInfo = activity as PersonalInfo
        initViews()
        doWork()
    }

    private fun initViews() {
        etCrObj?.addTextChangedListener(TW.CrossIconBehave(etCrObj))
        etCrPresentSalary?.addTextChangedListener(TW.CrossIconBehave(etCrPresentSalary))
        etCrExpSalary?.addTextChangedListener(TW.CrossIconBehave(etCrExpSalary))
    }

    private fun doWork() {
        addTextChangedListener(etCrObj, crObjTIL)
        tv_info_objective.setOnClickListener {
            showDialog(activity, "objective")
        }


        btn_example_objective.setOnClickListener {
            showDialog(activity, "objectiveExample")
        }


        fab_cai_edit.setOnClickListener {
            clCareerEdit.clearFocus()
            clCareerEdit.closeKeyboard(activity)
            var validation = 0
            validation = isValidate(etCrObj, crObjTIL, etCrObj, true, validation)
            if (validation == 1) {
                updateData()
                Log.d("chip_entry2", "text:$jobNature and $jobLevel")
            }/* else {
                assert(activity != null)
                activity?.toast("Please fill up the mandatory field first")
            }*/
        }
        personalInfo.setTitle(getString(R.string.title_career))
        personalInfo.setEditButton(false, "dd")
        preloadedData()
        crObjTIL.hideError()
    }


    private fun showDialog(activity: Activity, from: String) {
        dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.ori_example_dialog_layout)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val okButton = dialog.findViewById<TextView>(R.id.ok_button)
        val headingText = dialog.findViewById<TextView>(R.id.textView53)
        val mainText = dialog.findViewById<TextView>(R.id.textView55)
        val goodExampleText = dialog.findViewById<TextView>(R.id.textView57)
        val badExampleText = dialog.findViewById<TextView>(R.id.textView59)
        val constraintLayout = dialog.findViewById<LinearLayout>(R.id.constraintLayout4)


        when {
            from.equalIgnoreCase("objective") -> {

                headingText.show()
                mainText.show()
                constraintLayout.hide()
                headingText.setText(R.string.objective_heading)
                mainText.setText(R.string.objective_text)


            }
            from.equalIgnoreCase("objectiveExample") -> {
                headingText.show()
                mainText.hide()
                constraintLayout.show()
                headingText.setText(R.string.objective_heading)
                goodExampleText.setText(R.string.objective_good_example)
                badExampleText.setText(R.string.objective_bad_example)

            }
        }

        okButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun updateData() {
        activity?.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().updateCareerData(session.userId, session.decodId, session.IsResumeUpdate,
                etCrObj.getString(), etCrPresentSalary.getString(), etCrExpSalary.getString(), jobLevel, jobNature)
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                activity?.stopProgressBar(loadingProgressBar)
                activity?.toast(getString(R.string.message_common_error))
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity?.stopProgressBar(loadingProgressBar)
                        response.body()?.message?.let { activity?.toast(it) }
                        if (response.body()?.statuscode == "4") {
                            personalInfo.goBack()
                        }
                    }
                } catch (e: Exception) {
                    //activity.stopProgressBar(loadingProgressBar)
                    e.printStackTrace()
                    logException(e)
                }
            }
        })
    }

    private fun addTextChangedListener(editText: TextInputEditText, inputLayout: TextInputLayout) {
        editText.easyOnTextChangedListener { charSequence ->
            personalInfo.validateField(charSequence.toString(), editText, inputLayout)
        }
    }

    private fun preloadedData() {
        getDataFromChipGroup(cgLookingFor)
        getDataFromChipGroup(cgAvailable)
        var data: Ca_DataItem? = null
        try {
            data = personalInfo.getCareerData()
        } catch (e: Exception) {
            logException(e)
            d("++${e.message}")
        }
        etCrObj.setText(data?.objective)
        etCrPresentSalary.setText(data?.presentSalary)
        etCrExpSalary.setText(data?.expectedSalary)
        selectChip(cgLookingFor, data?.lookingFor)
        selectChip(cgAvailable, data?.availableFor)
    }


    private fun getDataFromChipGroup(cg: ChipGroup) {
        cg.setOnCheckedChangeListener { chipGroup, i ->
            debug("valueig : $i")
            if (i > 0) {
                val chip = chipGroup.findViewById(i) as Chip
                Log.d("chip_entry", "text: ${chip.text}")
                val data = chip.text.toString()
                when (chipGroup.id) {
                    R.id.cgLookingFor -> {
                        val chips = dataStorage.getLookingIDByName(data)
                        debug("value : $chips")
                        jobLevel = chips
                    }
                    R.id.cgAvailable -> {
                        jobNature = data
                        debug("value : $data")
                    }
                }
            } else {
                debug("value3 : $i and $jobLevel")
                when (chipGroup.id) {
                    R.id.cgLookingFor -> {
                        jobLevel = ""
                        debug("value2 : $jobLevel")
                    }
                    R.id.cgAvailable -> {
                        jobNature = ""
                        debug("value2 : $jobNature")
                    }
                }
            }
        }
    }

    private fun selectChip(chipGroup: ChipGroup, data: String?) {
        val count = chipGroup.childCount
        for (i in 0 until count) {
            val chip = chipGroup.getChildAt(i) as Chip
            val chipText = chip.text.toString()
            if (data?.equalIgnoreCase(chipText) as Boolean) {
                Log.d("chip_entry", "text:$i")
                chip.isChecked = true
            }
        }
    }

}
