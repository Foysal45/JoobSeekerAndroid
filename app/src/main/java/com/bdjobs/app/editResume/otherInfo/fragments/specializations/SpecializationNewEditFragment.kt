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
import com.bdjobs.app.API.ModelClasses.AddExpModel
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.SpecializationSkillAdapter
import com.bdjobs.app.editResume.callbacks.OtherInfo
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_specialization_edit.*
import kotlinx.android.synthetic.main.fragment_specialization_new_edit.*
import kotlinx.android.synthetic.main.fragment_specialization_new_edit.etCaricular
import kotlinx.android.synthetic.main.fragment_specialization_new_edit.etSkillDescription
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import java.util.*


class SpecializationNewEditFragment : Fragment() {
    private lateinit var dialog: Dialog
    private lateinit var dataStorage: DataStorage
    private var workSkillID = ""
    private var skills: String = ""
    private var isEmpty = false
    private var idArr: ArrayList<String> = ArrayList()

    private lateinit var bcCategories: ArrayList<String>
    private lateinit var specializationSkillAdapter: SpecializationSkillAdapter
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var selectedPosition = -1
    private var checkBoxStatus = false
    private var addExpList: ArrayList<AddExpModel>? = ArrayList()
    private var workExp = ""
    private var NTVQF = ""
    private var skillDuplicateStatus = false
    private var skillSourceNotEmptyStatus = false
    private var ntvqfStatus = false
    private lateinit var levelList: Array<String>
    private lateinit var dialogEdit: Dialog
    private lateinit var eduCB: OtherInfo

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onclick()
        initialization()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specialization_new_edit, container, false)
    }



    private fun initialization(){

        dataStorage = DataStorage(activity!!)
        levelList = arrayOf(
                "Pre-Voc Level 1", "Pre-Voc Level 2", "NTVQF Level 1",
                "NTVQF Level 2", "NTVQF Level 3", "NTVQF Level 4", "NTVQF Level 5", "NTVQF Level 6"
        )


        layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        skillListView?.layoutManager = layoutManager
        specializationSkillAdapter = SpecializationSkillAdapter(activity, addExpList)
        skillListView.adapter = specializationSkillAdapter
        eduCB = activity as OtherInfo

    }

    private fun onclick(){

        addSkillButton.onClick {

            showDialog(activity)


        }


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
        val addExperienceTIET = dialog.findViewById<TextInputEditText>(R.id.addExperienceTIET)
        val whereSkillText = dialog.findViewById<TextView>(R.id.whereSkillText)
        val firstCheckbox = dialog.findViewById<CheckBox>(R.id.firstCheckbox)
        val secondCheckBox = dialog.findViewById<CheckBox>(R.id.secondCheckBox)
        val thirdCheckBox = dialog.findViewById<CheckBox>(R.id.thirdCheckBox)
        val fourthCheckBox = dialog.findViewById<CheckBox>(R.id.fourthCheckBox)
        val fifthCheckBox = dialog.findViewById<CheckBox>(R.id.fifthCheckBox)
        val refnameATCTV = dialog.findViewById<AutoCompleteTextView>(R.id.refnameATCTV)
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
            d("specialization test idArr : ${idArr} ")
            if (idArr.size != 0) {
                if (!idArr.contains(workSkillID)){
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
                experienceLevelTIET!!.setText("NTVQF লেভেল")
                }
                else {
                    refnameATCTV?.closeKeyboard(activity)
                    activity?.toast("Skill already added")
                }
                skillTIL.hideError()
            } else {
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
                experienceLevelTIET!!.setText("NTVQF লেভেল")



                d("specialization test Array size : ${idArr.size} and $skills and id : $id")
                isEmpty = true
                skillTIL?.isErrorEnabled = true
                skillTIL?.hideError()
            }
        }



     /*   addExperienceTIET?.setOnClickListener {
            activity.selector("কাজের ধরন/দক্ষতা নির্বাচন করুন ", subCategories.toList()) { dialogInterface, i ->

                addExperienceTIET.setText(subCategories[i])
                whereSkillText.text = "কিভাবে '${subCategories[i]}' কাজের দক্ষতাটি শিখেছেন ?"
                workExp = subCategories[i]!!
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
                experienceLevelTIET!!.setText("NTVQF লেভেল")


            }
        }*/


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
                experienceLevelTIL?.show()
                ntvqfStatus = true

            } else {
                workSource[4] = "-5"
                experienceLevelTIL?.hide()
                ntvqfStatus = false
            }

        }
        experienceLevelTIET.setOnClickListener {
            activity.selector("NTVQF লেভেল নির্বাচন করুন", levelList.toList()) { dialogInterface, i ->
                experienceLevelTIET.setText(levelList[i])
                NTVQF = levelList[i]


            }
        }
        declineButton?.setOnClickListener {
            dialog.dismiss()
        }
        saveButton?.setOnClickListener {


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
                activity.toast("এই দক্ষতাটি ইতোমধ্যে যোগ করা হয়েছে।")
                skillDuplicateStatus = false
                skillSourceNotEmptyStatus = false
            } else {
                if (addExpList!!.size == 10) {
                    activity.toast("কাজের ধরন/দক্ষতা ১০টির বেশি হবে না ")
                    skillSourceNotEmptyStatus = false
                     dialog.dismiss()
                } else {
                    if (!item.workExp.isNullOrEmpty()) {
                        if (skillSourceNotEmptyStatus) {
                            if (ntvqfStatus) {

                                if (item.NTVQF.isNullOrEmpty()) {
                                    activity.toast("NTVQF লেভেল  নির্বাচন করুন")
                                    skillSourceNotEmptyStatus = false
                                } else {


                                    d("fjnjfhn  in first condition ${item.expSource} ")

                                    addExpList?.add(item)
                                    specializationSkillAdapter.notifyItemInserted(addExpList!!.size - 1)
                                    skillDuplicateStatus = false
                                    skillSourceNotEmptyStatus = false
                                    ntvqfStatus = false
                                    dialog.dismiss()


                                }

                            } else {

                                d("fjnjfhn  in second condition ${item.expSource} ")

                                addExpList?.add(item)
                                specializationSkillAdapter.notifyItemInserted(addExpList!!.size - 1)
                                skillDuplicateStatus = false
                                skillSourceNotEmptyStatus = false
                                dialog.dismiss()

                            }


                        } else {
                            skillSourceNotEmptyStatus = false
                            activity.toast("কাজের ধরন/দক্ষতা শেখার মাধ্যমটি নির্বাচন করুন।")

                        }
                    } else {
                        activity.toast("কাজের ধরন/দক্ষতা লিখুন")
                        skillSourceNotEmptyStatus = false

                    }


                }


            }

            if (addExpList?.size == 0) {

                 addExperienceLayout.show()
                skillListView.hide()

            } else {

                addExperienceLayout.hide()
                skillListView.show()
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
        dialogEdit.setContentView(R.layout.add_skill_dialog_layout)
        dialogEdit.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val addExperienceTIET = dialogEdit.findViewById<TextInputEditText>(R.id.addExperienceTIET)


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

        whereSkillText.show()
        firstCheckbox.show()
        secondCheckBox.show()
        thirdCheckBox.show()
        fourthCheckBox.show()
        fifthCheckBox.show()

        //set View start

        addExperienceTIET.setText(item.workExp)
        whereSkillText.text = "কিভাবে '${item.workExp}' কাজের দক্ষতাটি শিখেছেন ?"
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
                ntvqfStatus = true

            } else {
                workSource[4] = "-5"
                experienceLevelTIL.hide()
                ntvqfStatus = false
            }

        }
        experienceLevelTIET.setOnClickListener {

            activity.selector("NTVQF লেভেল নির্বাচন করুন ", levelList.toList()) { dialogInterface, i ->

                experienceLevelTIET.setText(levelList[i])

                NTVQF = levelList[i]


            }


        }
     /*   addExperienceTIET?.setOnClickListener {

            activity.selector("কাজের ধরন/দক্ষতা নির্বাচন করুন ", subCategories.toList()) { dialogInterface, i ->

                addExperienceTIET.setText(subCategories[i])
                whereSkillText.text = "কিভাবে ${subCategories[i]} কাজের দক্ষতাটি শিখেছেন ?"


                firstCheckbox.isChecked = false
                secondCheckBox.isChecked = false
                thirdCheckBox.isChecked = false
                fourthCheckBox.isChecked = false
                fifthCheckBox.isChecked = false
                experienceLevelTIET!!.setText("NTVQF লেভেল")

                workSource[0] = "-1"
                workSource[1] = "-2"
                workSource[2] = "-3"
                workSource[3] = "-4"
                workSource[4] = "-5"


            }

        }*/
        declineButton?.setOnClickListener {
            dialogEdit.dismiss()
        }
        saveButton?.setOnClickListener {

            workExp = addExperienceTIET.getString()

            d("ntvqf test in update ${fifthCheckBox.isChecked}")

            NTVQF = if (fifthCheckBox.isChecked) {

                experienceLevelTIET.getString()

            } else {

                ""

            }



            d("ntvqf test in update $NTVQF")

            val addItem = AddExpModel(workExp, workSource, NTVQF)

            d("ntvqf test in update ${addItem.NTVQF}")

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


            if (skillDuplicateStatus) {

                activity.toast("এই দক্ষতাটি ইতোমধ্যে যোগ করা হয়েছে।")
                skillDuplicateStatus = false
                skillSourceNotEmptyStatus = false


            } else {

                if (skillSourceNotEmptyStatus) {
                    if (ntvqfStatus) {
                        if (addItem.NTVQF.isNullOrEmpty()) {
                            activity.toast("NTVQF লেভেল  নির্বাচন করুন")
                            skillSourceNotEmptyStatus = false

                        } else {

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
                        updateItem(addItem)
                        dialogEdit.dismiss()
                    }
                } else {
                    skillSourceNotEmptyStatus = false
                    activity.toast("কাজের ধরন/দক্ষতা শেখার মাধ্যমটি নির্বাচন করুন।")
                }

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
        //jgkhgfjkh



        val data = eduCB.getSpecializationData()
        data.skills?.forEach {
           /* addChip(it?.skillName!!, it.id!!)*/

        }
        etSkillDescription?.setText(data.description)
        etCaricular?.setText(data.extracurricular)
    }

}
