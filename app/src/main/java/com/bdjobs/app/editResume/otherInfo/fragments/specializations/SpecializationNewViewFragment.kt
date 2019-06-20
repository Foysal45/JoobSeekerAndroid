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
import com.bdjobs.app.editResume.adapters.models.*
import com.bdjobs.app.editResume.callbacks.OtherInfo
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_specialization_new_view.*
import kotlinx.android.synthetic.main.fragment_specialization_new_view.curricularTV
import kotlinx.android.synthetic.main.fragment_specialization_new_view.fab_specialization_add
import kotlinx.android.synthetic.main.fragment_specialization_new_view.mainlayout
import kotlinx.android.synthetic.main.fragment_specialization_new_view.skillDescriptionTV
import kotlinx.android.synthetic.main.fragment_view_specialization.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


class SpecializationNewViewFragment : Fragment() {
    // TODO: Rename and change types of parameters


    private lateinit var specializationSkillAdapter: SpecializationSkillAdapter
    private var layoutManager: RecyclerView.LayoutManager? = null

    private var addSkillList: ArrayList<AddExpModel>? = ArrayList()
    private var arr: ArrayList<Skill?>? = null
    private lateinit var dialog: Dialog
    private lateinit var dataStorage: DataStorage
    private var workSkillID = ""
    private var skills: String = ""
    private var isEmpty = false
    private var idArr: java.util.ArrayList<String> = java.util.ArrayList()

    private var addExpList: java.util.ArrayList<AddExpModel>? = java.util.ArrayList()
    private var workExp = ""
    private var NTVQF = ""
    private var skillDuplicateStatus = false
    private var skillSourceNotEmptyStatus = false
    private var ntvqfStatus = false
    private lateinit var levelList: Array<String>
    private lateinit var dialogEdit: Dialog

    private lateinit var session: BdjobsUserSession
    var isEdit: Boolean = false

    private lateinit var eduCB: OtherInfo
    var updateNewSkilledBy = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specialization_new_view, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializaion()
        onClick()

    }


    private fun initializaion() {

        eduCB = activity as OtherInfo
        eduCB.setTitle("Specialization")
        eduCB.setEditButton(false)
        levelList = arrayOf(
                "Pre-Voc Level 1", "Pre-Voc Level 2", "NTVQF Level 1",
                "NTVQF Level 2", "NTVQF Level 3", "NTVQF Level 4", "NTVQF Level 5", "NTVQF Level 6"
        )
        session = BdjobsUserSession(activity)
        dataStorage = DataStorage(activity!!)
        fab_specialization_add?.setOnClickListener {

        }

        populateData()


    }


    private fun populateData() {
        mainlayout?.hide()
        shimmerStart()
        val call = ApiServiceMyBdjobs.create().getSpecializationInfo(session.userId, session.decodId)
        call.enqueue(object : Callback<SpecialzationModel> {
            override fun onFailure(call: Call<SpecialzationModel>, t: Throwable) {
                try {
                    shimmerStop()
                    activity?.toast("Error occurred")
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<SpecialzationModel>, response: Response<SpecialzationModel>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()

                        val respo = response.body()
                        arr = respo?.data!![0]?.skills as ArrayList<Skill?>
                        setData(arr!!, respo.data[0]?.description!!, respo.data[0]?.extracurricular!!, respo.data[0]!!)


                    }
                } catch (e: Exception) {
                    /*  shimmerStop()*/
                    if (activity != null) {
                        //activity.toast("${response.body()?.message}")
                        logException(e)
                        d("++${e.message}")
                    }
                }

            }
        })
    }


    private fun setData(array: ArrayList<Skill?>, skillDes: String, curricular: String, response: SpecializationDataModel) {

       /* if (array.size == 0 && TextUtils.isEmpty(skillDes) && TextUtils.isEmpty(curricular)) {
            mainlayout?.hide()
            fab_specialization_add?.show()
            eduCB.setEditButton(false)
        } else {*/
            mainlayout.show()
            fab_specialization_add?.hide()
            skillDescriptionTV?.text = skillDes
            curricularTV?.text = curricular

            if (array.size == 10) {

                addSkillButton.hide()

            } else {

                addSkillButton.show()
            }

            /*noSkillTVLayout.hide()*/
            skillListView.show()



            layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
            skillListView?.layoutManager = layoutManager
            specializationSkillAdapter = SpecializationSkillAdapter(activity, array)
            skillListView.adapter = specializationSkillAdapter



            eduCB.passSpecializationData(response)


      /*  }*/

    }

    private fun getNtvqf(item: String): String {

        var ntvqflevel = ""

        when (item) {
            "1" -> {
                ntvqflevel = "Pre-Voc Level 1"
            }
            "2" -> {
                ntvqflevel = "Pre-Voc Level 2"

            }
            "3" -> {
                ntvqflevel = "NTVQF Level 1"

            }
            "4" -> {
                ntvqflevel = "NTVQF Level 2"
            }
            "5" -> {
                ntvqflevel = "NTVQF Level 3"
            }
            "6" -> {
                ntvqflevel = "NTVQF Level 4"
            }
            "7" -> {
                ntvqflevel = "NTVQF Level 5"
            }
            "8" -> {
                ntvqflevel = "NTVQF Level 6"
            }

            /*val dataItem = AddExpModel(item.skillName,skillArrayList,)*/
        }

        return ntvqflevel
    }

    private fun shimmerStart() {
        try {
            shimmer_view_container_specialization_new?.show()
            shimmer_view_container_specialization?.startShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    private fun shimmerStop() {
        try {
            shimmer_view_container_specialization_new?.hide()
            shimmer_view_container_specialization?.stopShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }


    fun showEditDialog(item: Skill?) {

        val workSource = java.util.ArrayList<String>()
        workSource.add(0, "-1")
        workSource.add(1, "-2")
        workSource.add(2, "-3")
        workSource.add(3, "-4")
        workSource.add(4, "-5")

        var updateSkill = ""
        var updateNtvqf = ""
        var skilledBy = ""

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

        refnameATCTV.setText(item?.skillName)
        whereSkillText.text = "Skill you learned from"
        val list = item?.skillBy!!
        skilledBy = item.skillBy

        for (each in list) {
            when (each.toString()) {
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
                    experienceLevelTIET.setText(getNtvqf(item.ntvqfLevel.toString()))
                    experienceLevelTIL.show()
                    experienceLevelTIET.show()
                }

            }


        }
        // set View end

        //set data start
        NTVQF = item.ntvqfLevel.toString()

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
        firstCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                skilledBy += "1,"
            } else {
                skilledBy = skilledBy.replace("1","",true)
            }
        }
        secondCheckBox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                skilledBy += "2,"
            } else {
                skilledBy = skilledBy.replace("2","",true)
            }


        }
        thirdCheckBox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                skilledBy += "3,"
            } else {
                skilledBy = skilledBy.replace("3","",true)
            }

        }
        fourthCheckBox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                skilledBy += "4,"
            } else {
                skilledBy = skilledBy.replace("4","",true)
            }
        }
        fifthCheckBox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                skilledBy += "5,"
                experienceLevelTIL.show()
                experienceLevelTIET.show()
                ntvqfStatus = true
                NTVQF = ""


            } else {
                skilledBy = skilledBy.replace("5","",true)
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
                if (!experienceLevelTIET.getString().equalIgnoreCase("NTVQF Level")) {

                    NTVQF = experienceLevelTIET.getString()
                }
            } else {
                NTVQF = ""

            }

            val addItem = AddExpModel(workExp, workSource, NTVQF)
            var skilbyID = ""
            val skil = skilledBy.replace(",","")

            if (stringContainsNumber(skil.trim())){

                skillSourceNotEmptyStatus = true
                skilbyID  = skilledBy.replace(",,",",")
                d("skillTest three $skilbyID")
            } else {

                skillSourceNotEmptyStatus = false

            }


            updateSkill = dataStorage.getSkillIDBySkillType(addItem.workExp.toString()).toString()
            updateNtvqf = getNtvqfLevel(NTVQF)


            arr!!.forEachIndexed { index, element ->
                d("expTest update position ${eduCB.getItemClick()} index $index")

                if (eduCB.getItemClick() != index) {

                    if (element?.skillName!!.equalIgnoreCase(addItem.workExp!!)) {

                        d("expTest already exist")
                        skillDuplicateStatus = true
                    }

                }


            }



            if (!addItem.workExp.isNullOrEmpty()) {

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




                                skillSourceNotEmptyStatus = false
                                skillDuplicateStatus = false
                                ntvqfStatus = false
                                dialogEdit.dismiss()

                                d("SkillBy  item.skillBy ${item.skillBy}")
                                d("SkillBy updateNewSkilledBy $updateNewSkilledBy")


                                addOrUpdateItem(updateSkill, item.sId.toString(), skilbyID, updateNtvqf, "1")


                            }

                        } else {
                            skillDuplicateStatus = false
                            skillSourceNotEmptyStatus = false
                            ntvqfStatus = false

                            d("fjdgnfj 2 $ntvqfStatus")
                            d("SkillBy ${item.skillBy}")
                            d("SkillBy $skilledBy")

                            d("SkillBy  item.skillBy ${item.skillBy}")
                            d("SkillBy updateNewSkilledBy $updateNewSkilledBy")

                            addOrUpdateItem(updateSkill, item.sId.toString(), skilbyID, updateNtvqf, "1")
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


    fun stringContainsNumber(s: String): Boolean {

        d("skillTest in contains number $s")

        val p = Pattern.compile("[0-9]")
        val m = p.matcher(s)

        return m.find()
    }


    private fun showDialog(activity: Activity) {
        val workSource = java.util.ArrayList<String>()
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

            workSkillID = dataStorage.getSkillIDBySkillType(refnameATCTV.text.toString())!!

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

        firstCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                workSource[0] = "1"
            } else {
                workSource[0] = "-1"
            }

        }
        secondCheckBox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                workSource[1] = "2"
            } else {
                workSource[1] = "-2"
            }


        }
        thirdCheckBox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                workSource[2] = "3"
            } else {
                workSource[2] = "-3"
            }


        }
        fourthCheckBox?.setOnCheckedChangeListener { _, isChecked ->
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


            } else {
                workSource[4] = "-5"
                experienceLevelTIL?.hide()
                experienceLevelTIET.hide()
                ntvqfStatus = false

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


            var skill = ""
            var ntvqf = ""
            updateNewSkilledBy = ""
            var skilledBy = ""

            workExp = refnameATCTV.getString()

            val item = AddExpModel(workExp, workSource, NTVQF)

            item.expSource?.forEach {
                if (it.toInt() > 0) {
                    skillSourceNotEmptyStatus = true
                }

            }

            arr!!.forEach {
                if (it?.skillName!!.equalIgnoreCase(item.workExp!!)) {
                    skillDuplicateStatus = true
                }
            }


            item.expSource?.forEach {

                if (it.toInt() > 0) {
                    skilledBy += "$it,"

                }
            }

            updateNewSkilledBy = skilledBy.removeSuffix(",")

            skill = dataStorage.getSkillIDBySkillType(item.workExp.toString()).toString()
            ntvqf = getNtvqfLevel(NTVQF)




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
                                } else {

                                    addOrUpdateItem(skill, "", updateNewSkilledBy, ntvqf, "-1")
                                    skillDuplicateStatus = false
                                    skillSourceNotEmptyStatus = false
                                    ntvqfStatus = false

                                }

                            } else {

                                addOrUpdateItem(skill, "", updateNewSkilledBy, ntvqf, "-1")
                                skillDuplicateStatus = false
                                skillSourceNotEmptyStatus = false
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

        }
        dialog.show()

    }



    private fun onClick() {

        addSkillButton.onClick {
            showDialog(activity)
        }

        descriptionEditIcon.onClick {

            eduCB.goToEditInfo("editSpecialization")

        }

        curicularEditIcon.onClick {

            eduCB.goToEditInfo("editSpecialization")

        }


    }

    fun dataDelete(deleteItemId: String) {
         /* activity?.showProgressBar(newSpecializationLoadingProgressBar)*/
        d("deleteItemId: in fragment   $deleteItemId")
        val call = ApiServiceMyBdjobs.create().deleteData("Specialist", deleteItemId, session.IsResumeUpdate!!, session.userId!!, session.decodId!!)
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
                         /*  activity?.stopProgressBar(newSpecializationLoadingProgressBar)*/
                        val resp = response.body()
                        activity?.toast(resp?.message.toString())
                        populateData()
                        /* clearEditText()*/
                        /*eduCB.goBack()*/
                    }
                } catch (e: Exception) {
                    //activity.stopProgressBar(referenceLoadingProgressBar)
                    //activity.toast(response.body()?.message.toString())
                    e.printStackTrace()
                    logException(e)
                }
            }
        })
    }

    private fun addOrUpdateItem(skills: String, s_id: String, skilledBy: String, ntvqfLevel: String, insertOrUpdate: String) {

        d("nbnnn  ${session.userId} ${session.decodId} ${session.IsResumeUpdate} $skills  $s_id  $skilledBy $ntvqfLevel  $insertOrUpdate")

      /*  activity?.showProgressBar(newSpecializationLoadingProgressBar)*/
        ApiServiceMyBdjobs.create().specializationAddOrUpdate(session.userId, session.decodId, session.IsResumeUpdate!!, skills.trim(), s_id, skilledBy.trim(), ntvqfLevel, insertOrUpdate).enqueue(object : Callback<SpecializationAdModel> {
            override fun onFailure(call: Call<SpecializationAdModel>, t: Throwable) {

                activity?.toast(R.string.message_common_error)
                d("nbnnn ${t.message}")

            }

            override fun onResponse(call: Call<SpecializationAdModel>, response: Response<SpecializationAdModel>) {

                if (response.isSuccessful) {
                      /* activity?.stopProgressBar(newSpecializationLoadingProgressBar)*/
                    val resp = response.body()

                    d("nbnnn ${resp!!.statuscode!!}")
                    activity?.toast(resp.message.toString())
                    populateData()
                    if (resp.statuscode!!.equalIgnoreCase("2")) {

                        activity?.toast(resp.message.toString())

                    } else if (resp.statuscode!!.equalIgnoreCase("4")) {


                        if (insertOrUpdate.equalIgnoreCase("-1")) {
                            dialog.dismiss()

                        } else if (insertOrUpdate.equalIgnoreCase("1")) {

                            dialogEdit.dismiss()
                        }


                    }


                }


            }
        }


        )


    }

    private fun getNtvqfLevel(item: String): String {

        var ntvqflevel = ""

        when (item) {
            "Pre-Voc Level 1" -> {
                ntvqflevel = "1"
            }
            "Pre-Voc Level 2" -> {
                ntvqflevel = "2"

            }
            "NTVQF Level 1" -> {
                ntvqflevel = "3"

            }
            "NTVQF Level 2" -> {
                ntvqflevel = "4"
            }
            "NTVQF Level 3" -> {
                ntvqflevel = "5"

            }
            "NTVQF Level 4" -> {
                ntvqflevel = "6"
            }
            "NTVQF Level 5" -> {
                ntvqflevel = "7"

            }
            "NTVQF Level 6" -> {
                ntvqflevel = "8"

            }


        }

        return ntvqflevel
    }




}
