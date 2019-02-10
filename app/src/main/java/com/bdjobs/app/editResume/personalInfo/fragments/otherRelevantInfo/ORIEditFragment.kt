package com.bdjobs.app.editResume.personalInfo.fragments.otherRelevantInfo

import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.adapters.models.ORIdataItem
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_oriedit.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ORIEditFragment : Fragment() {
    private lateinit var oriEditCB: PersonalInfo
    private lateinit var session: BdjobsUserSession
    private lateinit var ds: DataStorage
    private lateinit var data: ORIdataItem
    private var idArr: ArrayList<String> = ArrayList()
    private var exps: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_oriedit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ds = DataStorage(activity)
        session = BdjobsUserSession(activity)
        oriEditCB = activity as PersonalInfo
        d("onActivityCreated")
        doWork()
        oriEditCB.setTitle(getString(R.string.title_ORI))
        oriEditCB.setEditButton(false, "dd")
    }

    private fun doWork() {
        data = oriEditCB.getOriData()
        btn_spq.hide()
        btn_career_sum.hide()
        onClicks()
        etOriKeywords.easyOnTextChangedListener { charSequence ->
            if (!idArr.isEmpty()) {
                textInputLayout4.hideError()
            } else
                oriEditCB.validateField(charSequence.toString(), etOriKeywords, textInputLayout4)
        }
        etOriKeywords?.addTextChangedListener(TW.CrossIconBehave(etOriKeywords))
        Log.d("ORIData", "data: ${data.keywords}")
        val keywords = data.keywords?.removeLastComma()
        val keyArray: List<String>? = keywords?.split(",")?.map { it.trim() }
        keyArray?.forEach {
            addChip(it)
        }
        fab_ori_update.setOnClickListener {
            clORIedit.closeKeyboard(activity)
            val isEmpty = ori_entry_chip_group.childCount < 1
            if (!isEmpty) {
                textInputLayout4.hideError()
                updateData()
            } else {
                checkIfEmpty()
                //activity?.toast("Pgasdinkgyword")
            }
        }
        etOriCareerSummary.setText(data.careerSummery)
        etOriSpecialQualification.setText(data.specialQualifications)

        etOriKeywords.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    addChip(etOriKeywords.getString())
                    true
                }
                else -> false
            }
        }
    }

    private fun onClicks() {
        tv_info_career.setOnClickListener {
            activity?.alert(Constants.career_tips_details, Constants.career_tips) {
                yesButton { it.dismiss() }
            }?.show()
        }
        tv_info_spec.setOnClickListener {
            activity?.alert(Constants.spQ_tips_details, Constants.spQ_tips) {
                yesButton { it.dismiss() }
            }?.show()
        }
        tv_info_keyword.setOnClickListener {
            activity?.alert(Constants.keyword_tips_details, Constants.keyword_tips) {
                yesButton { it.dismiss() }
            }?.show()
        }
        btn_career_sum.setOnClickListener {
            activity?.alert(Constants.career_tips_details, Constants.career_tips) {
                yesButton { it.dismiss() }
            }?.show()
        }
        btn_spq.setOnClickListener {
            activity?.alert(Constants.spQ_tips_details, Constants.spQ_tips) {
                yesButton { it.dismiss() }
            }?.show()
        }
    }

    private fun checkIfEmpty() {
        if (idArr.isEmpty()) {
            textInputLayout4.isErrorEnabled = true
            textInputLayout4.error = "Please add keywords"
            activity?.toast("Please type at least one keyword")
        }
    }


    private fun addChip(input: String) {
        if (ori_entry_chip_group.childCount <= 15) {
            addAsString(input)
            val c1 = getChip(ori_entry_chip_group, input, R.xml.chip_entry)
            ori_entry_chip_group.addView(c1)
            etOriKeywords?.clearText()
        } else activity.toast("Maximum 15 experiences can be added.")
        etOriKeywords?.closeKeyboard(activity)
    }

    private fun getChip(entryChipGroup: ChipGroup, text: String, item: Int): Chip {
        val chip = Chip(activity)
        chip.setChipDrawable(ChipDrawable.createFromResource(activity, item))
        val paddingDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10f,
                resources.displayMetrics
        ).toInt()
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.text = text
        chip.setOnCloseIconClickListener {
            entryChipGroup.removeView(chip)
            removeItem(chip.text.toString())
        }
        return chip
    }

    private fun addAsString(str: String) {
        if (!idArr.contains(str)) {
            idArr.add(str.trim())
            exps = TextUtils.join(",", idArr)
        }
        d("selected exps:$exps and ids $idArr")
        if (!idArr.isEmpty())
            textInputLayout4.hideError()
    }

    private fun updateData() {
        activity.showProgressBar(loadingProgressBar)
        val call = ApiServiceMyBdjobs.create().updateORIData(session.userId, session.decodId, session.IsResumeUpdate,
                etOriCareerSummary.getString(), etOriSpecialQualification.getString(), exps)
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
                            oriEditCB.goBack()
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

    private fun removeItem(s: String) {
        if (idArr.contains(s))
            idArr.remove(s)
        exps = TextUtils.join(",", idArr)
        checkIfEmpty()

        d("selected rmv: $exps and $idArr")

    }

}
