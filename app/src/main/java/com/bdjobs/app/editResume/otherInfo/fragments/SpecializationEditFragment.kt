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
import com.bdjobs.app.editResume.callbacks.OtherInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
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
        eduCB.setTitle(getString(R.string.title_specialization))
        refnameATCTV?.addTextChangedListener(TW.CrossIconBehaveACTV(refnameATCTV))
        etSkillDescription?.addTextChangedListener(TW.CrossIconBehave(etSkillDescription))
        etCaricular?.addTextChangedListener(TW.CrossIconBehave(etCaricular))
        doWork()

    }


    override fun onResume() {
        super.onResume()
        d(isEdit.toString())
        if (isEdit) {
            eduCB.setDeleteButton(true)
            eduCB.setEditButton(false)
            refnameATCTV?.clearText()

        } else {
            eduCB.setEditButton(false)
            eduCB.setDeleteButton(false)
            clearEditText()

        }

    }


    private fun clearEditText() {
        etSkillDescription?.clearText()
        etCaricular?.clearText()
        refnameATCTV?.clearText()
    }


    private fun preloadedData() {
        //jgkhgfjkh

        refnameATCTV?.clearText()

        val data = eduCB.getSpecializationData()
        data.skills?.forEach {
            addChip(it?.skillName!!, it.id!!)

        }
        etSkillDescription?.setText(data.description)
        etCaricular?.setText(data.extracurricular)
    }

    private fun doWork() {

        /*   getDataFromChip()*/
        if (isEdit) {
            preloadedData()

        } else {
            skills = ""
            /*idArr.clear()*/
        }


        val skillList: Array<String> = dataStorage.allSkills
        val skillAdapter = ArrayAdapter<String>(activity!!,
                android.R.layout.simple_dropdown_item_1line, skillList)
        refnameATCTV?.setAdapter(skillAdapter)
        refnameATCTV?.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT
        refnameATCTV?.setOnItemClickListener { _, _, position, id ->
            d("specialization test Array size : pos : $position id : $id")
            //activity.toast("Selected : ${workExperineceList[position + 1]} and gotStr : ${experiencesMACTV.text}")
            d("Selected : ${skillList[position + 1]} and gotStr : ${refnameATCTV.text}")
            workSkillID = dataStorage.getSkillIDBySkillType(refnameATCTV.text.toString())!!

            d("specialization test workSkillID : ${workSkillID} ")
            d("specialization test idArr : ${idArr} ")
            if (idArr.size != 0) {
                if (!idArr.contains(workSkillID))
                    addChip(refnameATCTV.getString(), workSkillID)
                else {
                    refnameATCTV?.closeKeyboard(activity)
                    activity.toast("Skill already added")
                }
                skillTIL.hideError()
            } else {
                addChip(refnameATCTV.getString().trim(), workSkillID)
                d("specialization test Array size : ${idArr.size} and $skills and id : $id")
                isEmpty = true
                skillTIL?.isErrorEnabled = true
                skillTIL?.hideError()
            }
        }

        fab_specialization_update?.setOnClickListener {


            //updateData(skills.removePrefix(","))
            Log.d("specialization", " val: ${TextUtils.join(",", idArr)}")
            updateData(TextUtils.join(",", idArr).removePrefix(","))


        }


    }


    private fun addAsString(expID: String) {
        if (!idArr.contains(expID)) {
            idArr.add(expID.trim())
            d("specialization test $idArr  ")
        }
    }


    private fun addChip(input: String, skillId: String) {

        d("specialization test addChip child count ${specialization_chip_group.childCount} ")

        if (specialization_chip_group.childCount <= 9) {
            addAsString(skillId)
            val c1 = getChip(specialization_chip_group, input, R.xml.chip_entry)
            specialization_chip_group.addView(c1)
            refnameATCTV?.clearText()
        } else {
            activity.toast("Maximum 10 skills can be added.")

        }
        refnameATCTV?.closeKeyboard(activity)
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
        val id = dataStorage.getSkillIDBySkillType(s.trim())

        d("specialization test removeItem id $id ")

        if (idArr.contains(id))
            idArr.remove(id)
        d("specialization test removeItem  idArr $idArr")
    }


    private fun updateData(skills: String) {
        activity?.showProgressBar(specializationLoadingProgressBar)
        d("specialization test updateData $skills ")

        //companyBusinessID = dataStorage.getOrgIDByOrgName(companyBusinessACTV.getString())
        val call = ApiServiceMyBdjobs.create().updateSpecialization(session.userId, session.decodId,
                session.IsResumeUpdate, skills, etSkillDescription.getString(), etCaricular.getString())
        call.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                try {
                    activity?.stopProgressBar(specializationLoadingProgressBar)
                    activity?.toast(R.string.message_common_error)
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity.stopProgressBar(specializationLoadingProgressBar)
                        val resp = response.body()

                        if (resp?.statuscode == "4") {

                            if (isEdit) {
                                activity.toast("The information has been updated successfully")
                            } else {
                                activity.toast("The information has been added successfully")
                            }

                            eduCB.goBack()
                        }
                    }
                } catch (e: Exception) {
                    /*  activity.stopProgressBar(specializationLoadingProgressBar)*/
                    e.printStackTrace()
                    logException(e)
                }
            }
        })
    }


}
