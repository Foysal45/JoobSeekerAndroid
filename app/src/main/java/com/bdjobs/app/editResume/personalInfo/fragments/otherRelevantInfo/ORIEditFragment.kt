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
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.ORIdataItem
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_oriedit.*
import org.jetbrains.anko.toast

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
        Log.d("ORIData", "data: ${data.keywords}")
        val keywords = data.keywords?.removeLastComma()
        val keyArray: List<String>? = keywords?.split(",")?.map { it.trim() }
        keyArray?.forEach {
            addChip(it)
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
    }

    private fun removeItem(s: String) {
        if (idArr.contains(s))
            idArr.remove(s)
        exps = TextUtils.join(",", idArr)
        d("selected rmv: $exps and $idArr")
    }

}
