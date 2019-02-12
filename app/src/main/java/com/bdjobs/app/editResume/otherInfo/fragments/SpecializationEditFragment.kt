package com.bdjobs.app.editResume.otherInfo.fragments

import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.adapters.models.Skill
import com.bdjobs.app.editResume.callbacks.OtherInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_emp_history_edit.*
import kotlinx.android.synthetic.main.fragment_specialization_edit.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SpecializationEditFragment : Fragment() {

    private lateinit var dataStorage: DataStorage
    var isEdit: Boolean = false
    private lateinit var eduCB: OtherInfo
    private lateinit var session: BdjobsUserSession
    private var workSkillID = ""
    private var skills: String = ""
    private var isEmpty = false
    private var idArr: ArrayList<String> = ArrayList()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specialization_edit, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dataStorage = DataStorage(activity)
        session = BdjobsUserSession(activity)
        eduCB = activity as OtherInfo
        doWork()


    }

    override fun onResume() {
        super.onResume()

        d(isEdit.toString())

        if (isEdit) {
            eduCB.setDeleteButton(true)
            preloadedData()
            /* hID = "1"*/

            /*   d("hid val $isEdit : $hID")*/
        } else {
            eduCB.setDeleteButton(false)
            clearEditText()
            /*  d("hid val $isEdit: $hID")*/
        }

    }


    private fun clearEditText() {

        etSkillDescription.clearText()
        etCaricular.clearText()
    }


    private fun preloadedData() {


        val data = eduCB.getSpecializationData()
        /*  hReferenceID = data.refId.toString()*/


        addAllChip((data.skills as List<Skill>?)!!)
        etSkillDescription.setText(data.description)
        etCaricular.setText(data.extracurricular)

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


    private fun doWork() {

        /*   getDataFromChip()*/

        skills = ""
        val skillList: Array<String> = dataStorage.allSkills
        val skillAdapter = ArrayAdapter<String>(activity!!,
                android.R.layout.simple_dropdown_item_1line, skillList)
        refnameATCTV.setAdapter(skillAdapter)
        refnameATCTV.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT
        refnameATCTV.setOnItemClickListener { _, _, position, id ->
            d("Array size : pos : $position id : $id")
            //activity.toast("Selected : ${workExperineceList[position + 1]} and gotStr : ${experiencesMACTV.text}")
            d("Selected : ${skillList[position + 1]} and gotStr : ${refnameATCTV.text}")
            workSkillID = dataStorage.getSkillIDBySkillType(refnameATCTV.text.toString())!!

            d("workSkillID : ${workSkillID} ")

            if (idArr.size != 0) {
                if (!idArr.contains(workSkillID))
                    addChip(refnameATCTV.getString())
                else {
                    refnameATCTV.closeKeyboard(activity)
                    activity.toast("Experience already added")
                }
                skillTIL.hideError()
            } else {
                addChip(refnameATCTV.getString())
                d("Array size : ${idArr.size} and $skills and id : $id")
                isEmpty = true
                skillTIL.isErrorEnabled = true
                skillTIL.hideError()
            }
        }

        fab_specialization_update.setOnClickListener {

            try {
                val chars: Char = skills[0]
                if (!chars.equals(","))
                    skills = ",$skills,"
                skills = skills.replace(",,".toRegex(), ",")
            } catch (e: Exception) {
                Log.e("updateEx: ", "error: ${e.printStackTrace()}")
            }

            updateData(skills)


        }


    }


    private fun addAsString(expID: String) {
        if (!idArr.contains(expID)) {
            idArr.add(expID.trim())
            skills = TextUtils.join(",", idArr)
        }

        d("selected exps:$skills and ids $idArr")
    }


    private fun addChip(input: String) {
        if (specialization_chip_group.childCount <= 10) {
            addAsString(workSkillID)
            val c1 = getChip(specialization_chip_group, input, R.xml.chip_entry)
            specialization_chip_group.addView(c1)
            refnameATCTV?.clearText()
        } else {
            activity.toast("Maximum 10 skills can be added.")
        }
        refnameATCTV?.closeKeyboard(activity)
    }

    private fun addAllChip(input: List<Skill>) {

        for (item in input) {
            val c1 = getChip(specialization_chip_group, item.skillName!!, R.xml.chip_entry)
            specialization_chip_group.addView(c1)
            refnameATCTV?.clearText()
        }


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


    private fun removeItem(s: String) {
        val id = dataStorage.workDisciplineIDByWorkDiscipline(s)
        if (idArr.contains(id))
            idArr.remove("$id")
        skills = TextUtils.join(",", idArr)
        d("selected rmv: $skills and $idArr")
    }


    private fun updateData(skills: String) {
        /*    activity.showProgressBar(loadingProgressBar)*/
        Log.d("allValuesExp", skills)

        //companyBusinessID = dataStorage.getOrgIDByOrgName(companyBusinessACTV.getString())
        val call = ApiServiceMyBdjobs.create().updateSpecialization(session.userId, session.decodId,
                session.IsResumeUpdate, skills, etSkillDescription.getString(), etCaricular.getString())
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                activity.stopProgressBar(loadingProgressBar)
                activity.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                /*   try {*/
                if (response.isSuccessful) {
                    /*    activity.stopProgressBar(loadingProgressBar)*/
                    val resp = response.body()
                    activity.toast(resp?.message.toString())
                    if (resp?.statuscode == "4") {
                        eduCB.goBack()
                    }
                }
                /* } catch (e: Exception) {
                  *//*   activity.stopProgressBar(loadingProgressBar)*//*
                    e.printStackTrace()
                }*/
            }
        })
    }





}
