package com.bdjobs.app.editResume.personalInfo.fragments.carrerDetails

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
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
        fab_cai_edit.setOnClickListener {
            var validation = 0
            validation = isValidate(etCrObj, crObjTIL, etCrObj, true, validation)
            if (validation == 1) {
                updateData()
            } else {
                assert(activity != null)
                activity?.toast("Please fill up the mandatory field first")
            }
        }
        personalInfo.setTitle(getString(R.string.title_career))
        personalInfo.setEditButton(false, "dd")
        preloadedData()
        crObjTIL.hideError()
    }

    private fun updateData() {
        activity.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().updateCareerData(session.userId, session.decodId, session.IsResumeUpdate,
                etCrObj.getString(), etCrPresentSalary.getString(), etCrExpSalary.getString(), jobLevel, jobNature)
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                activity.stopProgressBar(loadingProgressBar)
                activity.toast("Can not connect to the server! Try again")
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity.stopProgressBar(loadingProgressBar)
                        response.body()?.message?.let { activity.toast(it) }
                        if (response.body()?.statuscode == "4") {
                            personalInfo.goBack()
                        }
                    }
                } catch (e: Exception) {
                    activity.stopProgressBar(loadingProgressBar)
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
        val data = personalInfo.getCareerData()
        etCrObj.setText(data.objective)
        etCrPresentSalary.setText(data.presentSalary)
        etCrExpSalary.setText(data.expectedSalary)
        selectChip(cgLookingFor, data.lookingFor!!)
        selectChip(cgAvailable, data.availableFor!!)
    }


    private fun getDataFromChipGroup(chipGroup: ChipGroup) {
        chipGroup.setOnCheckedChangeListener { chipGroup, i ->
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
                when (chipGroup.id) {
                    R.id.jobLevelCG -> {
                        jobLevel = ""
                    }
                    R.id.jobNatureCG -> {
                        jobNature = ""
                    }
                }
            }
        }
    }

    private fun selectChip(chipGroup: ChipGroup, data: String) {
        val count = chipGroup.childCount
        for (i in 0 until count) {
            val chip = chipGroup.getChildAt(i) as Chip
            val chipText = chip.text.toString()
            if (data.equalIgnoreCase(chipText)) {
                Log.d("chip_entry", "text:$i")
                chip.isChecked = true
            }
        }
    }

}
