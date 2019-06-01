package com.bdjobs.app.editResume.otherInfo.fragments.specializations


import android.app.Activity
import android.app.Dialog
import android.app.Fragment
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.AddExpModel
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.SpecializationSkillAdapter
import com.bdjobs.app.editResume.adapters.models.AddorUpdateModel
import com.bdjobs.app.editResume.callbacks.OtherInfo
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_specialization_new_edit.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class SpecializationNewEditFragment : Fragment() {
    private lateinit var dialog: Dialog
    private lateinit var dataStorage: DataStorage
    private var workSkillID = ""
    private var skills: String = ""
    private var isEmpty = false
    private var idArr: ArrayList<String> = ArrayList()
    private lateinit var specializationSkillAdapter: SpecializationSkillAdapter
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var addExpList: ArrayList<AddExpModel>? = ArrayList()
    private var workExp = ""
    private var NTVQF = ""
    private var skillDuplicateStatus = false
    private var skillSourceNotEmptyStatus = false
    private var ntvqfStatus = false
    private lateinit var levelList: Array<String>
    private lateinit var dialogEdit: Dialog
    private lateinit var eduCB: OtherInfo
    private lateinit var session: BdjobsUserSession
    var isEdit: Boolean = false


    override fun onResume() {
        super.onResume()

        if (isEdit) {
            eduCB.setDeleteButton(true)
            eduCB.setEditButton(false)


        } else {
            eduCB.setEditButton(false)
            eduCB.setDeleteButton(false)


        }


    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_specialization_new_edit, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialization()
        onclick()

    }

    private fun initialization() {

        dataStorage = DataStorage(activity!!)
        levelList = arrayOf(
                "Pre-Voc Level 1", "Pre-Voc Level 2", "NTVQF Level 1",
                "NTVQF Level 2", "NTVQF Level 3", "NTVQF Level 4", "NTVQF Level 5", "NTVQF Level 6"
        )



        eduCB = activity as OtherInfo
        session = BdjobsUserSession(activity)


    }

    private fun onclick() {

        if (isEdit) {
            preloadedData()

        }



        new_fab_specialization_update.onClick {


            var subCategoriesID = ""
            var skilledBy = ""
            var ntvqfLevel = ""

            for (i in addExpList!!.indices) {
                if (i == addExpList!!.size - 1) {
                    subCategoriesID += dataStorage.getSkillIDBySkillType(addExpList!![i].workExp!!)
                } else {
                    subCategoriesID = subCategoriesID + dataStorage.getSkillIDBySkillType(addExpList!![i].workExp!!) + ","

                }
            }

            addExpList?.forEach {

                it.expSource?.forEach {

                    if (it.toInt() > 0) {
                        skilledBy += it + ","
                        /*  d("dhjb inside $skilledBy" )*/
                    }
                }

                skilledBy += "s"

            }
            val updateSkilledBy = skilledBy.replace(",s", "s")
            val updateNewSkilledBy = updateSkilledBy.removeSuffix("s")

            addExpList?.forEach {

                /* d("ntvqf test in fa button ${it.NTVQF}")*/

                if (it.NTVQF.isNullOrEmpty()) {

                    ntvqfLevel += "0" + ","
                } else {
                    when {
                        it.NTVQF!!.equalIgnoreCase("Pre-Voc Level 1") -> ntvqfLevel += "1" + ","
                        it.NTVQF!!.equalIgnoreCase("Pre-Voc Level 2") -> ntvqfLevel += "2" + ","
                        it.NTVQF!!.equalIgnoreCase("NTVQF Level 1") -> ntvqfLevel += "3" + ","
                        it.NTVQF!!.equalIgnoreCase("NTVQF Level 2") -> ntvqfLevel += "4" + ","
                        it.NTVQF!!.equalIgnoreCase("NTVQF Level 3") -> ntvqfLevel += "5" + ","
                        it.NTVQF!!.equalIgnoreCase("NTVQF Level 4") -> ntvqfLevel += "6" + ","
                        it.NTVQF!!.equalIgnoreCase("NTVQF Level 5") -> ntvqfLevel += "7" + ","
                        it.NTVQF!!.equalIgnoreCase("NTVQF Level 6") -> ntvqfLevel += "8" + ","

                    }


                }

            }

            val updateNtvqf = ntvqfLevel.removeSuffix(",")


            d("fjnn subCategoriesID $subCategoriesID updateNewSkilledBy $updateNewSkilledBy updateNtvqf $updateNtvqf ")


            updateData(subCategoriesID, updateNewSkilledBy, updateNtvqf)


        }


    }


    private fun updateData(skills: String, skillBy: String, ntvqf: String) {
        activity?.showProgressBar(newSpecializationLoadingProgressBar)
        val call = ApiServiceMyBdjobs.create().updateSpecializationTest(session.userId, session.decodId,
                session.IsResumeUpdate, skills, etSkillDescription.getString(), etCaricular.getString(), skillBy, ntvqf)
        call?.enqueue(object : Callback<AddorUpdateModel> {
            override fun onFailure(call: Call<AddorUpdateModel>, t: Throwable) {
                try {
                    activity?.stopProgressBar(newSpecializationLoadingProgressBar)
                    activity?.toast(R.string.message_common_error)
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<AddorUpdateModel>, response: Response<AddorUpdateModel>) {
                try {
                    if (response.isSuccessful) {
                        activity?.stopProgressBar(newSpecializationLoadingProgressBar)
                        val resp = response.body()

                        if (resp?.statuscode == "4") {

                            if (isEdit) {
                                activity?.toast("The information has been updated successfully")
                            } else {
                                activity?.toast("The information has been added successfully")
                            }
                          /*  eduCB.setBackFrom(Constants.specUpdate)*/
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

    private fun showDialog(activity: Activity) {
        val workSource = ArrayList<String>()
        workSource.add(0, "-1")
        workSource.add(1, "-2")
        workSource.add(2, "-3")
        workSource.add(3, "-4")
        workSource.add(4, "-5")

        dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.specialization_add_skill_dialog_layout)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val whereSkillText = dialog.findViewById<TextView>(R.id.whereSkillText)
        val firstCheckbox = dialog.findViewById<CheckBox>(R.id.firstCheckbox)
        val secondCheckBox = dialog.findViewById<CheckBox>(R.id.secondCheckBox)
        val thirdCheckBox = dialog.findViewById<CheckBox>(R.id.thirdCheckBox)
        val fourthCheckBox = dialog.findViewById<CheckBox>(R.id.fourthCheckBox)
        val fifthCheckBox = dialog.findViewById<CheckBox>(R.id.fifthCheckBox)
        val refnameATCTV = dialog.findViewById<AutoCompleteTextView>(R.id.newRefnameATCTV)
        val experienceLevelTIET = dialog.findViewById<TextInputEditText>(R.id.experienceLevelTIET)
        val experienceLevelTIL = dialog.findViewById<TextInputLayout>(R.id.experienceLevelTIL)
        val declineButton = dialog.findViewById<MaterialButton>(R.id.declineButton)
        val saveButton = dialog.findViewById<MaterialButton>(R.id.saveButton)


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


            /* addChip(refnameATCTV.getString(), workSkillID)*/
            refnameATCTV.setText(refnameATCTV.getString())
            workExp = refnameATCTV.getString()
            whereSkillText.show()
            firstCheckbox.show()
            secondCheckBox.show()
            thirdCheckBox.show()
            fourthCheckBox.show()
            fifthCheckBox.show()


            firstCheckbox.isChecked = false
            secondCheckBox.isChecked = false
            thirdCheckBox.isChecked = false
            fourthCheckBox.isChecked = false
            fifthCheckBox.isChecked = false
            ntvqfStatus = false
            experienceLevelTIET!!.setText("NTVQF Level")
        }

        firstCheckbox?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                workSource[0] = "1"
            } else {
                workSource[0] = "-1"
            }

        }
        secondCheckBox?.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {

                workSource[1] = "2"

            } else {

                workSource[1] = "-2"
            }


        }
        thirdCheckBox?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {

                workSource[2] = "3"

            } else {

                workSource[2] = "-3"
            }


        }
        fourthCheckBox?.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {

                workSource[3] = "4"

            } else {

                workSource[3] = "-4"
            }


        }
        fifthCheckBox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                workSource[4] = "5"
                experienceLevelTIL?.show()
                experienceLevelTIET.show()
                ntvqfStatus = true
                activity.toast("$ntvqfStatus")

            } else {
                workSource[4] = "-5"
                experienceLevelTIL?.hide()
                experienceLevelTIET.hide()
                ntvqfStatus = false
                activity.toast("$ntvqfStatus")
            }

        }
        experienceLevelTIET.setOnClickListener {
            activity.selector("Select NTVQF Level", levelList.toList()) { dialogInterface, i ->
                experienceLevelTIET.setText(levelList[i])
                NTVQF = levelList[i]


            }
        }
        declineButton?.setOnClickListener {
            dialog.dismiss()
        }
        saveButton?.setOnClickListener {

            workExp = refnameATCTV.getString()

            val item = AddExpModel(workExp, workSource, NTVQF)

            item.expSource?.forEach {
                if (it.toInt() > 0) {
                    skillSourceNotEmptyStatus = true
                }

            }

            addExpList!!.forEach {

                d("expTest ${it.workExp}")

                if (it.workExp!!.equalIgnoreCase(item.workExp!!)) {

                    d("expTest already exist")
                    skillDuplicateStatus = true
                }

            }

            d("fjnjfhn  out of condition ${item.expSource} skillSourceNotEmptyStatus $skillSourceNotEmptyStatus ")


            if (skillDuplicateStatus) {
                activity.toast("Already exists!")
                skillDuplicateStatus = false
                skillSourceNotEmptyStatus = false
            } else {
                if (addExpList!!.size == 10) {
                    activity.toast("Skill maximum 10")
                    skillSourceNotEmptyStatus = false
                    dialog.dismiss()
                } else {
                    if (!item.workExp.isNullOrEmpty()) {
                        if (skillSourceNotEmptyStatus) {
                            if (ntvqfStatus) {

                                if (item.NTVQF.isNullOrEmpty()) {
                                    activity.toast("Please select NTVQF level")
                                    skillSourceNotEmptyStatus = false
                                } else  {

                                    d("fjdgnfj 1 $ntvqfStatus")
                                        addExpList?.add(item)
                                        specializationSkillAdapter.notifyItemInserted(addExpList!!.size - 1)
                                        skillDuplicateStatus = false
                                        skillSourceNotEmptyStatus = false
                                        ntvqfStatus = false
                                        dialog.dismiss()





                                }

                            } else {
                                d("fjdgnfj 2 $ntvqfStatus")
                                d("fjnjfhn  in second condition ${item.expSource} ")

                                addExpList?.add(item)
                                specializationSkillAdapter.notifyItemInserted(addExpList!!.size - 1)
                                skillDuplicateStatus = false
                                skillSourceNotEmptyStatus = false
                                dialog.dismiss()

                            }


                        } else {
                            skillSourceNotEmptyStatus = false
                            activity.toast("Please select Skill learning process")

                        }
                    } else {
                        activity.toast("Please select Skill")
                        skillSourceNotEmptyStatus = false

                    }


                }


            }






            d("uhiuhiu addExpList?.size  ${addExpList?.size}")


        }
        dialog.show()

    }

    fun showEditDialog(item: AddExpModel) {

        val workSource = ArrayList<String>()
        workSource.add(0, "-1")
        workSource.add(1, "-2")
        workSource.add(2, "-3")
        workSource.add(3, "-4")
        workSource.add(4, "-5")

        dialogEdit = Dialog(activity)
        dialogEdit.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogEdit.setCancelable(true)
        dialogEdit.setContentView(R.layout.specialization_add_skill_dialog_layout)
        dialogEdit.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val whereSkillText = dialogEdit.findViewById<TextView>(R.id.whereSkillText)
        val firstCheckbox = dialogEdit.findViewById<CheckBox>(R.id.firstCheckbox)
        val secondCheckBox = dialogEdit.findViewById<CheckBox>(R.id.secondCheckBox)
        val thirdCheckBox = dialogEdit.findViewById<CheckBox>(R.id.thirdCheckBox)
        val fourthCheckBox = dialogEdit.findViewById<CheckBox>(R.id.fourthCheckBox)
        val fifthCheckBox = dialogEdit.findViewById<CheckBox>(R.id.fifthCheckBox)
        val experienceLevelTIL = dialogEdit.findViewById<TextInputLayout>(R.id.experienceLevelTIL)
        val experienceLevelTIET = dialogEdit.findViewById<TextInputEditText>(R.id.experienceLevelTIET)
        val declineButton = dialogEdit.findViewById<MaterialButton>(R.id.declineButton)
        val saveButton = dialogEdit.findViewById<MaterialButton>(R.id.saveButton)
        val refnameATCTV = dialogEdit.findViewById<AutoCompleteTextView>(R.id.newRefnameATCTV)

        whereSkillText.show()
        firstCheckbox.show()
        secondCheckBox.show()
        thirdCheckBox.show()
        fourthCheckBox.show()
        fifthCheckBox.show()

        //set View start

        refnameATCTV.setText(item.workExp)
        whereSkillText.text = "Skill you learned from"
        val list = item?.expSource!!

        for (each in list) {
            when (each) {
                "1" -> {
                    firstCheckbox.isChecked = true
                }
                "2" -> {
                    secondCheckBox.isChecked = true
                }
                "3" -> {

                    thirdCheckBox.isChecked = true
                }
                "4" -> {
                    fourthCheckBox.isChecked = true
                }
                "5" -> {
                    fifthCheckBox.isChecked = true
                    experienceLevelTIET.setText(item.NTVQF)
                    experienceLevelTIL.show()
                    experienceLevelTIET.show()
                }

            }


        }
        // set View end

        //set data start
        NTVQF = item.NTVQF.toString()
        workExp = item.workExp.toString()
        workSource[0] = item.expSource!![0]
        workSource[1] = item.expSource!![1]
        workSource[2] = item.expSource!![2]
        workSource[3] = item.expSource!![3]
        workSource[4] = item.expSource!![4]


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

            /* addChip(refnameATCTV.getString(), workSkillID)*/
            refnameATCTV.setText(refnameATCTV.getString())
            workExp = refnameATCTV.getString()
            whereSkillText.show()
            firstCheckbox.show()
            secondCheckBox.show()
            thirdCheckBox.show()
            fourthCheckBox.show()
            fifthCheckBox.show()


            firstCheckbox.isChecked = false
            secondCheckBox.isChecked = false
            thirdCheckBox.isChecked = false
            fourthCheckBox.isChecked = false
            fifthCheckBox.isChecked = false
            ntvqfStatus = false
            experienceLevelTIET!!.setText("NTVQF Level")


        }

        // set data end
        firstCheckbox?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                workSource[0] = "1"
            } else {
                workSource[0] = "-1"
            }
        }
        secondCheckBox?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {

                workSource[1] = "2"

            } else {

                workSource[1] = "-2"
            }


        }
        thirdCheckBox?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {

                workSource[2] = "3"

            } else {

                workSource[2] = "-3"
            }

        }
        fourthCheckBox?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                workSource[3] = "4"
            } else {
                workSource[3] = "-4"
            }
        }
        fifthCheckBox?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                workSource[4] = "5"
                experienceLevelTIL.show()
                experienceLevelTIET.show()
                ntvqfStatus = true
                NTVQF =""


            } else {
                workSource[4] = "-5"
                experienceLevelTIL.hide()
                experienceLevelTIET.hide()
                ntvqfStatus = false
                NTVQF = ""

            }

        }

        experienceLevelTIET.setOnClickListener {

            activity.selector("Select NTVQF level", levelList.toList()) { dialogInterface, i ->

                experienceLevelTIET.setText(levelList[i])

                NTVQF = levelList[i]


            }


        }

        declineButton?.setOnClickListener {
            dialogEdit.dismiss()
        }
        saveButton?.setOnClickListener {

            workExp = refnameATCTV.getString()

            if (fifthCheckBox.isChecked) {
                if(!experienceLevelTIET.getString().equalIgnoreCase("NTVQF Level")){

                    NTVQF =  experienceLevelTIET.getString()
                }
            } else {
                NTVQF =  ""

            }

            val addItem = AddExpModel(workExp, workSource, NTVQF)

            d("ntvqf test in update ${addItem.NTVQF}")

            d("tttdf ${addItem.workExp} ${addItem.expSource} ${addItem.NTVQF}")

            addItem.expSource?.forEach {
                if (it.toInt() > 0) {
                    skillSourceNotEmptyStatus = true
                    /*  d("expTest  skillSourceNotEmptyStatus $skillSourceNotEmptyStatus")*/

                }
            }


            addExpList!!.forEachIndexed { index, element ->
                d("expTest update position ${eduCB.getItemClick()} index $index")

                if (eduCB.getItemClick() != index) {

                    if (element.workExp!!.equalIgnoreCase(addItem.workExp!!)) {

                        d("expTest already exist")
                        skillDuplicateStatus = true
                    }

                }


            }





            if (!addItem.workExp.isNullOrEmpty()){

                if (skillDuplicateStatus) {

                    activity.toast("Already exists!")
                    skillDuplicateStatus = false
                    skillSourceNotEmptyStatus = false


                } else {

                    if (skillSourceNotEmptyStatus) {
                        if (ntvqfStatus) {
                            if (addItem.NTVQF.isNullOrEmpty()) {
                                activity.toast("Please select NTVQF level")
                                skillSourceNotEmptyStatus = false

                            } else if (!addItem.NTVQF.isNullOrEmpty()) {

                                d("fjdgnfj 1 $ntvqfStatus")

                                updateItem(addItem)
                                skillSourceNotEmptyStatus = false
                                skillDuplicateStatus = false
                                ntvqfStatus = false
                                dialogEdit.dismiss()


                            }

                        } else {
                            skillDuplicateStatus = false
                            skillSourceNotEmptyStatus = false
                            ntvqfStatus = false

                            d("fjdgnfj 2 $ntvqfStatus")

                            updateItem(addItem)
                            dialogEdit.dismiss()
                        }
                    } else {
                        skillSourceNotEmptyStatus = false
                        activity.toast("Please select Skill learning process")
                    }

                }

            } else {
                activity.toast("Please select Skill")
                skillSourceNotEmptyStatus = false
            }



        }

        dialogEdit.show()
    }

    private fun updateItem(item: AddExpModel) {

        addExpList?.removeAt(eduCB.getItemClick())
        specializationSkillAdapter.notifyItemRemoved(eduCB.getItemClick())
        specializationSkillAdapter.notifyItemRangeChanged(eduCB.getItemClick(), addExpList!!.size - 1)
        addExpList?.add(eduCB.getItemClick(), item)
        specializationSkillAdapter.notifyItemInserted(eduCB.getItemClick())
        specializationSkillAdapter.notifyItemRangeChanged(eduCB.getItemClick(), addExpList!!.size - 1)


    }

    private fun preloadedData() {

        addExpList!!.clear()
        val data = eduCB.getSpecializationDataNew()

        for (item in data!!) {

            addExpList!!.add(item)
            specializationSkillAdapter.notifyItemInserted(addExpList!!.size - 1)


        }

        etSkillDescription.setText(eduCB.getSkillDes())
        etCaricular.setText(eduCB.getExtraCuri())


    }

}
