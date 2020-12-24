package com.bdjobs.app.editResume.personalInfo.fragments.otherRelevantInfo

import android.app.Activity
import android.app.Dialog
import android.app.Fragment
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.databases.External.DataStorage
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
    private var data: ORIdataItem? = null
    private var idArr: ArrayList<String> = ArrayList()
    private var exps: String = ""
    private var keywordsCount: Int = 0
    private var maxInput: Int = 250
    private var toatalLength : Int= 0
    private lateinit var dialog: Dialog
    private lateinit var cgORI: ChipGroup
    private var keywords : String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
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

    }

    override fun onResume() {
        super.onResume()
        doWork()
    }

    private fun doWork() {
        try {
            data = oriEditCB.getOriData()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
        onClicks()

//        etOriKeywords.easyOnTextChangedListener { charSequence ->
//            //Log.d("rakib", "adadad")
//            if (maxInput - keywordsCount != 0) {
//                etOriKeywords.isEnabled = true
//                if (keywordsCount + charSequence.length <= maxInput) {
//                    val maxLength = maxInput - keywordsCount
//                    etOriKeywords.isEnabled = true
//                    etOriKeywords?.filters = arrayOf(InputFilter.LengthFilter(maxLength))
//                } else {
//                    etOriKeywords.isEnabled = false
//                    activity?.toast("Reached max limit of keywords.\nPlease remove one first")
//                }
//            } else {
//                etOriKeywords.isEnabled = false
//            }
//
//            if (!idArr.isEmpty()) {
//                textInputLayout4.hideError()
//            } else
//                oriEditCB.validateField(charSequence.toString(), etOriKeywords, textInputLayout4)
//        }

        etOriKeywords?.addTextChangedListener(TW.CrossIconBehave(etOriKeywords))

        //Log.d("ORIData", "data: ${data?.keywords}")

       // val keywords = data?.keywords

        keywords?.let {
            toatalLength = keywords!!.length
        }

        //Log.d("qqqqq", "total length $toatalLength")

//        try {
//            maxInput -= data?.keywords?.countCommas()!!
//        } catch (e: Exception) {
//            logException(e)
//        }
//        //Log.d("rakib", "${keywords?.length}")

        val keyArray: List<String>? = keywords?.split(",")?.map { it.trim() }

//        if (data?.keywords?.length!! <= 250)
//            activity?.toast("Keywords maximum 250 characters")
        keyArray?.forEach {
            //Log.d("rakib", "$keyArray ${keyArray.size}")
            if (it.isNotBlank()){
                addChip(it)
            }
        }
        fab_ori_update.setOnClickListener {
            clORIedit.closeKeyboard(activity)

            if (true) {

                //Log.d("rakib", "total length $toatalLength")

                if (etOriKeywords?.text.toString().length + toatalLength < 250) {
                    if (etOriKeywords.text.toString() != "") {
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
                } else {
                    activity?.toast("Reached max limit of keywords.\nPlease remove one first")
                }
            }


        }
        etOriCareerSummary.setText(data?.careerSummery)
        etOriSpecialQualification.setText(data?.specialQualifications)

        etOriKeywords.easyOnTextChangedListener {
            val str = etOriKeywords.getString()

            if (it.isNotBlank() && (str.endsWith(","))) {
                clORIedit.closeKeyboard(activity)
                if (str.trim() != ",") {
                    etOriKeywords.isEnabled = true
                    etOriKeywords.clear()
                    etOriKeywords.clearFocus()
                    addChip(str.removeLastComma())
                } else {
                    etOriKeywords.clear()
                    etOriKeywords.clearFocus()
                    activity?.toast("Reached the max limit of keywords")
                }
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
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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
        //Log.d("rakib", "came here $keywordsCount ${data?.keywords?.length!!}")
        if (data?.keywords?.length!! <= 250) {
            keywordsCount += input.length
            //Log.d("ORIcount", "totalCount: ${data?.keywords}|<- ${data?.keywords?.countCommas()}")
            addAsString(input)
            val c1 = getChip(ori_entry_chip_group, input, R.xml.chip_entry)
            ori_entry_chip_group.addView(c1)
            etOriKeywords?.clearText()
        } else {
            val c1 = getChip(ori_entry_chip_group, input, R.xml.chip_entry)
            ori_entry_chip_group.addView(c1)

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
                try {
                    activity?.stopProgressBar(loadingProgressBar)
                    activity?.toast("Can not connect to the server! Try again")
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity?.stopProgressBar(loadingProgressBar)
                        response.body()?.message?.let { activity.toast(it) }
                        if (response.body()?.statuscode == "4") {
                            oriEditCB.setBackFrom(Constants.oriUpdate)
                            oriEditCB.goBack()
                        }
                    }
                } catch (e: Exception) {
                    activity?.stopProgressBar(loadingProgressBar)
                    e.printStackTrace()
                    logException(e)
                }
            }
        })
    }

    private fun removeItem(s: String) {
        //Log.d("rakib", "remove called")
        if (idArr.contains(s)) {
            idArr.remove(s)
//            keywordsCount -= s.length
//            keywordsCount = data?.keywords?.length!! - s.length
            toatalLength -= s.length
        }
        etOriKeywords.isEnabled = toatalLength <= 250

        exps = TextUtils.join(",", idArr)
        checkIfEmpty()

        d("ORIcount: count = $keywordsCount ")

    }

}
