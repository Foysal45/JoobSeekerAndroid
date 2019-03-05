package com.bdjobs.app.editResume.personalInfo.fragments.otherRelevantInfo

import android.app.Activity
import android.app.Dialog
import android.app.Fragment
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
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
import com.bdjobs.app.editResume.adapters.models.ORIdataItem
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_oriedit.*
import org.jetbrains.anko.toast
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
    private lateinit var dialog: Dialog
    private lateinit var cgORI: ChipGroup

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
        oriEditCB.setTitle(getString(R.string.title_ORI))
        oriEditCB.setEditButton(false, "dd")
        doWork()
    }

    private fun doWork() {

        //entry_chip_group.removeAllViews()
        data = oriEditCB.getOriData()

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
            if (it.isNotBlank()) addChip(it)
        }
        fab_ori_update.setOnClickListener {
            clORIedit.closeKeyboard(activity)

            if (etOriKeywords.getString() != "") {
                addChip(etOriKeywords.getString().removeLastComma())
            }

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

        etOriKeywords.easyOnTextChangedListener {
            val str = etOriKeywords.getString()

            if (it.isNotBlank() and (str.endsWith(",") || str.endsWith(" "))) {
                clORIedit.closeKeyboard(activity)
                addChip(str.removeLastComma())
            }
        }
    }

    private fun onClicks() {
        tv_info_career.setOnClickListener {
            showDialog(activity, "summery")
        }
        tv_info_spec.setOnClickListener {
            showDialog(activity, "specialization")
        }
        tv_info_keyword.setOnClickListener {
            showDialog(activity, "keyword")
        }


        btn_spq.setOnClickListener {
            showDialog(activity, "specializationExample")
        }
        btn_career_sum.setOnClickListener {
            showDialog(activity, "summeryExample")
        }


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
            from.equalIgnoreCase("summeryExample") -> {

                headingText.show()
                mainText.hide()
                constraintLayout.show()
                headingText.setText(R.string.career_summery_heading)
                goodExampleText.setText(R.string.career_summery_good_example)
                badExampleText.setText(R.string.career_summery_bad_example)

            }
            from.equalIgnoreCase("summery") -> constraintLayout.hide()
            from.equalIgnoreCase("specialization") -> {
                constraintLayout.hide()
                headingText.setText(R.string.specialization_heading)
                mainText.setText(R.string.specialization_text)
            }
            from.equalIgnoreCase("specializationExample") -> {
                headingText.show()
                mainText.hide()
                constraintLayout.show()
                headingText.setText(R.string.specialization_heading)
                goodExampleText.setText(R.string.specialization_good_example)
                badExampleText.setText(R.string.specialization_bad_example)
            }
            from.equalIgnoreCase("keyword") -> {
                headingText.show()
                mainText.show()
                constraintLayout.hide()
                headingText.setText(R.string.keyword_heading)
                mainText.setText(R.string.keyword_text)
            }


        }
        okButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }


    private fun checkIfEmpty() {
        if (idArr.isEmpty()) {
            textInputLayout4.isErrorEnabled = true
            textInputLayout4.error = "Please add keywords"
            //activity?.toast("Please type at least one keyword")
        }
    }


    private fun addChip(input: String) {
        if (ori_entry_chip_group.childCount < 15) {
            addAsString(input)
            val c1 = getChip(ori_entry_chip_group, input, R.xml.chip_entry)
            ori_entry_chip_group.addView(c1)
            etOriKeywords?.clearText()
        } else {
            activity.toast("Maximum 15 experiences can be added.")
        }
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
