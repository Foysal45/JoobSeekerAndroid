package com.bdjobs.app.editResume.otherInfo.fragments.languagePref


import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.callbacks.OtherInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_lang_proficiency.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LangProficiencyEditFragment : Fragment() {
    var isEdit: Boolean = false
    private lateinit var eduCB: OtherInfo
    private lateinit var session: BdjobsUserSession
    private lateinit var hLanguageID: String
    private lateinit var hID: String
    private var readingLevel = ""
    private var speakingLevel = ""
    private var writingLevel = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lang_proficiency, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        session = BdjobsUserSession(activity)
        eduCB = activity as OtherInfo
        eduCB.setTitle(getString(R.string.title_language))
        initialization()
        doWork()
        if (!isEdit) {
            eduCB.setDeleteButton(false)
            hID = "-2"
            clearEditText()
            hLanguageID = ""
            d("hid val $isEdit: $hID")
            readingLevel = ""
            speakingLevel = ""
            writingLevel = ""
        }


    }


    private fun initialization() {
        languageTIET?.addTextChangedListener(TW.CrossIconBehave(languageTIET))
        languageTIET?.easyOnTextChangedListener { charSequence ->
            eduCB.validateField(charSequence.toString(), languageTIET, languageTIL)
        }

        languageTIL?.hideError()

    }


    fun dataDelete() {
        activity.showProgressBar(languageLoadingProgressBar)
        val call = ApiServiceMyBdjobs.create().deleteData("Language", hID, session.IsResumeUpdate!!, session.userId!!, session.decodId!!)
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                try {
                    activity?.toast(R.string.message_common_error)
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity.stopProgressBar(languageLoadingProgressBar)
                        val resp = response.body()
                        activity.toast(resp?.message.toString())
                        clearEditText()
                        eduCB.goBack()
                    }
                } catch (e: Exception) {
                    activity.stopProgressBar(languageLoadingProgressBar)
                    activity.toast(response.body()?.message.toString())
                    e.printStackTrace()
                }
            }
        })
    }

    private fun clearEditText() {
        languageTIET?.clear()
        languageTIET?.clearFocus()
        languageTIL?.hideError()

    }


    private fun disableError() {
        languageTIL?.hideError()

    }


    private fun doWork() {
        addTextChangedListener(languageTIET, languageTIL)
        getDataFromChip()



        fab_language_update?.setOnClickListener {


            if (languageValidity()) {

                d("dnjhgbdjgb ${languageValidity()} and ${languageLevelValidity()}")

                d("dnjhgbdjgb in first condition ${languageValidity()}}")

                if (languageLevelValidity()) {
                    updateData()
                }
            }
        }
    }


    private fun updateData() {
        activity.showProgressBar(languageLoadingProgressBar)
        val call = ApiServiceMyBdjobs.create().updateLanguageList(session.userId, session.decodId, languageTIET.getString(),
                readingLevel, session.IsResumeUpdate, writingLevel, speakingLevel, hID)

        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                try {
                    activity?.stopProgressBar(languageLoadingProgressBar)
                    activity?.toast(R.string.message_common_error)
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {

                try {
                    activity?.stopProgressBar(languageLoadingProgressBar)
                    if (response.isSuccessful) {
                        activity.stopProgressBar(languageLoadingProgressBar)
                        val resp = response.body()
                        activity.toast(resp?.message.toString())
                        if (resp?.statuscode == "4") {
                            eduCB.goBack()
                        }
                    }
                } catch (e: Exception) {
                    assert(activity != null)
                    activity?.stopProgressBar(languageLoadingProgressBar)
                    e.printStackTrace()
                }
            }
        })
    }


    private fun addTextChangedListener(editText: TextInputEditText, inputLayout: TextInputLayout) {
        editText.easyOnTextChangedListener { charSequence ->
            eduCB.validateField(charSequence.toString(), editText, inputLayout)
        }
    }

    override fun onResume() {
        super.onResume()
        d(isEdit.toString())

        if (isEdit) {
            eduCB.setDeleteButton(true)
            preloadedData()
            d("hid val $isEdit : $hID")
        } else {
            eduCB.setDeleteButton(false)
            hID = ""
            clearEditText()
            readingLevel = ""
            speakingLevel = ""
            writingLevel = ""

            d("hid val $isEdit: $hID")
        }
    }


    private fun getDataFromChip() {
        getDataFromChipGroup(cgReading)
        getDataFromChipGroup(cgWriting)
        getDataFromChipGroup(cgSpeaking)
    }


    private fun preloadedData() {

        getDataFromChipGroup(cgReading)
        getDataFromChipGroup(cgWriting)
        getDataFromChipGroup(cgSpeaking)

        val data = eduCB.getLanguageData()
        hID = data.lnId.toString()
        languageTIET?.setText(data.language)

        selectChip(cgReading, data.reading.toString())
        selectChip(cgWriting, data.writing.toString())
        selectChip(cgSpeaking, data.speaking.toString())

        disableError()
        /* d("values : ${data.country}")*/
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


    private fun getDataFromChipGroup(cg: ChipGroup) {

        Log.d("djkgnhdg", "cg $cg")



        cg.setOnCheckedChangeListener { chipGroup, i ->


            Log.d("djkgnhdg", " i $i")

            if (i > 0) {
                val chip = chipGroup.findViewById(i) as Chip
                cg.radioCheckableChip(chip)
                Log.d("chip_entry", "text: ${chip.text}")
                val data = chip.text.toString()
                when (chipGroup.id) {
                    R.id.cgReading -> {
                        val chips = when (data) {
                            "High" -> "High"
                            "Medium" -> "Medium"
                            "Low" -> "Low"
                            else -> ""
                        }
                        debug("value : $chips")
                        readingLevel = chips
                    }
                    R.id.cgWriting -> {
                        val chips = when (data) {
                            "High" -> "High"
                            "Medium" -> "Medium"
                            "Low" -> "Low"
                            else -> ""
                        }
                        debug("value : $chips")
                        writingLevel = chips
                        debug("value : $chips")
                    }
                    R.id.cgSpeaking -> {
                        val chips = when (data) {
                            "High" -> "High"
                            "Medium" -> "Medium"
                            "Low" -> "Low"
                            else -> ""
                        }
                        debug("value : $chips")
                        speakingLevel = chips
                        debug("value : $chips")
                    }
                }
            } else {

                Log.d("djkgnhdg", " else")

                when (chipGroup.id) {
                    R.id.cgReading -> {
                        readingLevel = ""
                    }
                    R.id.cgWriting -> {
                        writingLevel = ""
                    }
                    R.id.cgSpeaking -> {
                        speakingLevel = ""
                    }
                }
            }


        }


    }


    private fun languageValidity(): Boolean {

        if (TextUtils.isEmpty(languageTIET?.getString())) {
            languageTIL?.showError("This Field can not be empty")
            activity.requestFocus(languageTIET)
            return false

        } else {
            languageTIET?.requestFocus()
            return true
        }


    }


    private fun languageLevelValidity(): Boolean {


        if (readingLevel.equalIgnoreCase("")) {
            activity.toast("Please select Reading proficiency")
            return false
        } else if (writingLevel.equalIgnoreCase("")) {
            activity.toast("Please select Writing proficiency")
            return false
        } else if (speakingLevel.equalIgnoreCase("")) {
            activity.toast("Please select Speaking proficiency")
            return false
        } else if (!readingLevel.isEmpty() && !writingLevel.isEmpty() && !speakingLevel.isEmpty()) {
            return true
        }

        return false

    }


}
