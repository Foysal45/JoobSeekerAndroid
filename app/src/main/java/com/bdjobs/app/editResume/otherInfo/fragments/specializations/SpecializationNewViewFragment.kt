package com.bdjobs.app.editResume.otherInfo.fragments.specializations


import android.app.Activity
import android.app.Dialog
import android.app.Fragment
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.AddExpModel
import com.bdjobs.app.API.ModelClasses.AutoSuggestionModel
import com.bdjobs.app.API.ModelClasses.DataAutoSuggestion
import com.bdjobs.app.databases.External.DataStorage
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
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.regex.Pattern


class SpecializationNewViewFragment : Fragment() {
    // TODO: Rename and change types of parameters


    private lateinit var specializationSkillAdapter: SpecializationSkillAdapter
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var arr: ArrayList<Skill?>? = null
    private lateinit var dialog: Dialog
    private lateinit var dataStorage: DataStorage
    private var workSkillID = ""
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

    var currentDialogValue: String? = null

    var suggestionMap = HashMap<String?,String?>()


    private var list = ArrayList<DataAutoSuggestion>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return inflater.inflate(R.layout.fragment_specialization_new_view, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onClick()
    }

    override fun onResume() {
        super.onResume()
        eduCB = activity as OtherInfo
        eduCB.setTitle("Specialization")
        eduCB.setEditButton(false)
        levelList = arrayOf(
            "Pre-Voc Level 1", "Pre-Voc Level 2", "NTVQF Level 1",
            "NTVQF Level 2", "NTVQF Level 3", "NTVQF Level 4", "NTVQF Level 5", "NTVQF Level 6"
        )
        session = BdjobsUserSession(activity)
        dataStorage = DataStorage(activity!!)

        if(eduCB.getBackFrom() == ""){
            setData(eduCB.getSkills()!!, eduCB.getSkillDes()!!, eduCB.getExtraCuri()!!, eduCB.getSpecializationData())
        } else {
            doWork()
        }



    }

    private fun doWork(){
        shimmerStart()
        populateData()
        eduCB.setBackFrom("")
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
//                        //Log.d("rakib", respo?.data)
                        arr = respo?.data!![0]?.skills as ArrayList<Skill?>

                        eduCB.setSkills(arr!!)
                        eduCB.setSkillDescription(respo.data[0]?.description!!)
                        eduCB.setExtraCurricularActivity(respo.data[0]?.extracurricular!!)
                        eduCB.passSpecializationData(respo.data[0]!!)


                        setData(arr!!, respo.data[0]?.description!!, respo.data[0]?.extracurricular!!, respo.data[0]!!)


                    }
                } catch (e: Exception) {
                    shimmerStop()
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

        if (array.size == 0) {
            mainlayout?.show()
            imageView22?.hide()
            textView48?.hide()
            skillListView?.hide()
            skillDescriptionTV?.text = skillDes
            curricularTV?.text = curricular
            eduCB.passSpecializationData(response)
        } else {

            imageView22?.show()
            textView48?.show()

            mainlayout?.show()
            fab_specialization_add?.hide()
            skillDescriptionTV?.text = skillDes
            curricularTV?.text = curricular

            if (array.size == 10) {

                addSkillButton?.hide()

            } else {

                addSkillButton?.show()
            }

            /*noSkillTVLayout.hide()*/
            skillListView?.show()



            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            skillListView?.layoutManager = layoutManager
            specializationSkillAdapter = SpecializationSkillAdapter(activity, array)
            skillListView.adapter = specializationSkillAdapter



            eduCB.passSpecializationData(response)


        }

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
            shimmer_view_container_specialization_new?.startShimmer()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    private fun shimmerStop() {
        try {
            shimmer_view_container_specialization_new?.hide()
            shimmer_view_container_specialization_new?.stopShimmer()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    fun showEditDialog(item: Skill?) {

        ////Log.d("rakib", "came here")
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
        dialogEdit.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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

        whereSkillText?.show()
        firstCheckbox?.show()
        secondCheckBox?.show()
        thirdCheckBox?.show()
        fourthCheckBox?.show()
        fifthCheckBox?.show()

        //set View start
        refnameATCTV?.setText(item?.skillName)

        if (item != null) {
            currentDialogValue = item.skillName?.slice(0 until item.skillName.length - 1)

            //Log.d("rakib", "currentDialogValue: ${currentDialogValue}")
        }

        refnameATCTV?.setSelection(refnameATCTV.getString().length)
        whereSkillText?.text = "Skill you learned from"
        val listLearned = item?.skillBy!!
        skilledBy = "${item.skillBy},"

        for (each in listLearned) {
            when (each.toString()) {
                "1" -> {
                    firstCheckbox?.isChecked = true
                }
                "2" -> {
                    secondCheckBox?.isChecked = true
                }
                "3" -> {

                    thirdCheckBox?.isChecked = true
                }
                "4" -> {
                    fourthCheckBox?.isChecked = true
                }
                "5" -> {
                    fifthCheckBox?.isChecked = true
                    experienceLevelTIET?.setText(getNtvqf(item.ntvqfLevel.toString()))
                    experienceLevelTIL?.show()
                    experienceLevelTIET?.show()
                }

            }


        }
        // set View end

        //set data start
        NTVQF = item.ntvqfLevel.toString()

//        val skillList: Array<String> = dataStorage.allSkills
        val skillList: ArrayList<String> = ArrayList()
        val skillAdapter = ArrayAdapter<String>(activity!!,
                android.R.layout.simple_dropdown_item_1line, skillList)
        refnameATCTV?.setAdapter(skillAdapter)
        refnameATCTV?.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT
        refnameATCTV?.setOnItemClickListener { _, _, position, id ->

//            workSkillID = dataStorage.getSkillIDBySkillType(refnameATCTV?.getString().trim())!!
            workSkillID = list[position].subCatId!!

            refnameATCTV.setText(refnameATCTV.getString())

            currentDialogValue = refnameATCTV.text.toString().slice(0 until refnameATCTV.text.length - 1)

            saveButton.isEnabled = true

            refnameATCTV.setSelection(refnameATCTV.getString().length)
            workExp = refnameATCTV.getString()
            whereSkillText?.show()
            firstCheckbox?.show()
            secondCheckBox?.show()
            thirdCheckBox?.show()
            fourthCheckBox?.show()
            fifthCheckBox?.show()


            firstCheckbox?.isChecked = false
            secondCheckBox?.isChecked = false
            thirdCheckBox?.isChecked = false
            fourthCheckBox?.isChecked = false
            fifthCheckBox?.isChecked = false
            ntvqfStatus = false
            experienceLevelTIET?.setText("NTVQF Level")


        }


        // set data end
        firstCheckbox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                skilledBy += "1,"
            } else {
                skilledBy = skilledBy.replace("1", "", true)
            }
        }
        secondCheckBox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                skilledBy += "2,"
            } else {
                skilledBy = skilledBy.replace("2", "", true)
            }


        }
        thirdCheckBox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                skilledBy += "3,"
            } else {
                skilledBy = skilledBy.replace("3", "", true)
            }

        }
        fourthCheckBox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                skilledBy += "4,"
            } else {
                skilledBy = skilledBy.replace("4", "", true)
            }
        }
        fifthCheckBox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                skilledBy += "5,"
                experienceLevelTIL?.show()
                experienceLevelTIET?.show()
                ntvqfStatus = true
                NTVQF = ""


            } else {
                skilledBy = skilledBy.replace("5", "", true)
                experienceLevelTIL?.hide()
                experienceLevelTIET?.hide()
                ntvqfStatus = false
                NTVQF = ""

            }

        }


        refnameATCTV.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                //Log.d("rakib", "${refnameATCTV.isPopupShowing}")

                //Log.d("rakib", "called after text changed")

                if (s.toString().length == 2) {
//                    refnameATCTV.hideKeyboard()

                }

                if (s.toString().length > 2 && !refnameATCTV.isPopupShowing) {
                    //Log.d("rakib", "not permitted")
                    saveButton.isEnabled = true
                }
//                if (!s.toString().isEmpty() && s.toString().length > 2)
//                {
//                    //Log.d("rakib", "called")
//                    if (!refnameATCTV.isPopupShowing){
//                        //Log.d("rakib", "popup showing")
//                        toast("No skill found!")
//                        saveButton?.isEnabled = false
//                    } else {
//                        saveButton.isEnabled = true
//                    }
//                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                fetchAutoSuggestion(s.toString(),refnameATCTV)

                if (s.toString().length > 2) {


                    //Log.d("rakib", "called on text change: ${s}")
                    //Log.d("rakib", "currentDialogValue: ${currentDialogValue}")

                    d("popup showing ${refnameATCTV.isPopupShowing}")
                    if (refnameATCTV?.isPopupShowing!!) {
                        //saveButton?.isEnabled = true

                        //toast("opened")
                    } else {
                        if (s.toString() != currentDialogValue)
                        {
                            toast("No skill found!")
                            currentDialogValue = ""
                        }


                        saveButton?.isEnabled = false
                        refnameATCTV.clearText()
                        whereSkillText?.hide()
                        firstCheckbox?.hide()
                        secondCheckBox?.hide()
                        thirdCheckBox?.hide()
                        fourthCheckBox?.hide()
                        fifthCheckBox?.hide()
                        experienceLevelTIET?.hide()
                        experienceLevelTIL?.hide()

                        firstCheckbox?.isChecked = false
                        secondCheckBox?.isChecked = false
                        thirdCheckBox?.isChecked = false
                        fourthCheckBox?.isChecked = false
                        fifthCheckBox?.isChecked = false
                        ntvqfStatus = false

                    }

                }


            }
        })

        experienceLevelTIET?.setOnClickListener {

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

                    NTVQF = experienceLevelTIET?.getString()!!
                }
            } else {
                NTVQF = ""

            }

            val addItem = AddExpModel(workExp, workSource, NTVQF)
            val skilbyID = skilledBy.replace(",,,", ",")
            val skilbyIDNew = skilbyID.replace(",,", ",")
            val removeSuffixSkill = skilbyIDNew.removeSuffix(",")
            val removePrefixSkill = removeSuffixSkill.removePrefix(",")
            val removePrefixSkillAgain = removePrefixSkill.removePrefix(",")
            val skil = removePrefixSkill.replace(",", "")

            skillSourceNotEmptyStatus = stringContainsNumber(skil.trim())

//            updateSkill = dataStorage.getSkillIDBySkillType(addItem.workExp.toString()).toString()
            updateSkill = suggestionMap[addItem.workExp].toString()
            updateNtvqf = getNtvqfLevel(NTVQF)

            arr!!.forEachIndexed { index, element ->
                if (eduCB.getItemClick() != index) {
                    if (element?.skillName!!.equalIgnoreCase(addItem.workExp!!)) {
                        skillDuplicateStatus = true
                    }

                }


            }

            if (updateSkill.isEmpty() && refnameATCTV.getString().length == 2) {

                activity?.toast("Please type valid skill")

            } else {

                if (!addItem.workExp.isNullOrEmpty()) {
                    if (skillDuplicateStatus) {
                        activity?.toast("The skill is already exists!")
                        skillDuplicateStatus = false
                        skillSourceNotEmptyStatus = false


                    } else {
                        if (skillSourceNotEmptyStatus) {
                            if (ntvqfStatus) {
                                if (addItem.NTVQF.isNullOrEmpty()) {
                                    activity?.toast("Please select NTVQF level")
                                    skillSourceNotEmptyStatus = false

                                } else if (!addItem.NTVQF.isNullOrEmpty()) {
                                    skillSourceNotEmptyStatus = false
                                    skillDuplicateStatus = false
                                    ntvqfStatus = false
                                    dialogEdit.dismiss()

                                    NTVQF = ""
                                    addOrUpdateItem(updateSkill, item.sId.toString(), removePrefixSkillAgain, updateNtvqf, "1")


                                }

                            } else {
                                skillDuplicateStatus = false
                                skillSourceNotEmptyStatus = false
                                ntvqfStatus = false
                                NTVQF = ""
                                addOrUpdateItem(updateSkill, item.sId.toString(), removePrefixSkillAgain, updateNtvqf, "1")
                                dialogEdit.dismiss()
                            }
                        } else {


                            skillSourceNotEmptyStatus = false
                            activity?.toast("Please select how you have learned the skill")


                        }

                    }

                } else {
                    activity?.toast("Please type your Skill")
                    skillSourceNotEmptyStatus = false
                }

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
        //Log.d("rakib", "came here")
        val workSource = java.util.ArrayList<String>()
        workSource.add(0, "-1")
        workSource.add(1, "-2")
        workSource.add(2, "-3")
        workSource.add(3, "-4")
        workSource.add(4, "-5")

        dialog = Dialog(activity)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.specialization_add_skill_dialog_layout)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val whereSkillText = dialog?.findViewById<TextView>(R.id.whereSkillText)
        val firstCheckbox = dialog?.findViewById<CheckBox>(R.id.firstCheckbox)
        val secondCheckBox = dialog?.findViewById<CheckBox>(R.id.secondCheckBox)
        val thirdCheckBox = dialog?.findViewById<CheckBox>(R.id.thirdCheckBox)
        val fourthCheckBox = dialog?.findViewById<CheckBox>(R.id.fourthCheckBox)
        val fifthCheckBox = dialog?.findViewById<CheckBox>(R.id.fifthCheckBox)
        val refnameATCTV = dialog?.findViewById<AutoCompleteTextView>(R.id.newRefnameATCTV)
        val experienceLevelTIET = dialog?.findViewById<TextInputEditText>(R.id.experienceLevelTIET)
        val experienceLevelTIL = dialog?.findViewById<TextInputLayout>(R.id.experienceLevelTIL)
        val declineButton = dialog?.findViewById<MaterialButton>(R.id.declineButton)
        val saveButton = dialog?.findViewById<MaterialButton>(R.id.saveButton)
        saveButton?.isEnabled = false


//        val skillList: Array<String> = dataStorage.allSkills
        val skillList: ArrayList<String> = ArrayList()
        val skillAdapter = ArrayAdapter<String>(activity!!,
                android.R.layout.simple_dropdown_item_1line, skillList)
        refnameATCTV?.setAdapter(skillAdapter)
        refnameATCTV?.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT
        refnameATCTV?.setOnItemClickListener { _, _, position, id ->

//            workSkillID = dataStorage.getSkillIDBySkillType(refnameATCTV.getString().trim())!!
            workSkillID = list[position].subCatId!!

            refnameATCTV.setText(refnameATCTV.getString())

            currentDialogValue = refnameATCTV?.text.toString().slice(0 until refnameATCTV?.text.length - 1)

            //Log.d("rakib", "currentDialogValue: ${currentDialogValue}")

            refnameATCTV?.setSelection(refnameATCTV.getString().length)
            workExp = refnameATCTV?.getString()
            whereSkillText?.show()
            firstCheckbox?.show()
            secondCheckBox?.show()
            thirdCheckBox?.show()
            fourthCheckBox?.show()
            fifthCheckBox?.show()


            firstCheckbox?.isChecked = false
            secondCheckBox?.isChecked = false
            thirdCheckBox?.isChecked = false
            fourthCheckBox?.isChecked = false
            fifthCheckBox?.isChecked = false
            ntvqfStatus = false
            experienceLevelTIET!!.setText("NTVQF Level")
        }


        refnameATCTV.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 2) {
//                    refnameATCTV.hideKeyboard()

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                fetchAutoSuggestion(s.toString(),refnameATCTV)

                if (s.toString().length > 2) {

                    d("popup showing ${refnameATCTV.isPopupShowing}")
                    if (refnameATCTV?.isPopupShowing!!) {
                        saveButton?.isEnabled = true

                    } else {
                        saveButton?.isEnabled = false

                        if (s.toString() != currentDialogValue)
                        {
                            toast("No skill found!")
                            currentDialogValue = ""
                        }

                        refnameATCTV?.clearText()
                        whereSkillText?.hide()
                        firstCheckbox?.hide()
                        secondCheckBox?.hide()
                        thirdCheckBox?.hide()
                        fourthCheckBox?.hide()
                        fifthCheckBox?.hide()
                        experienceLevelTIET?.hide()
                        experienceLevelTIL?.hide()

                        firstCheckbox?.isChecked = false
                        secondCheckBox?.isChecked = false
                        thirdCheckBox?.isChecked = false
                        fourthCheckBox?.isChecked = false
                        fifthCheckBox?.isChecked = false
                        ntvqfStatus = false
                    }

                }
            }
        })


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
        experienceLevelTIET?.setOnClickListener {
            activity.selector("Select NTVQF Level", levelList.toList()) { dialogInterface, i ->
                experienceLevelTIET.setText(levelList[i])
                NTVQF = levelList[i]


            }
        }
        declineButton?.setOnClickListener {
            dialog.dismiss()
        }

        try {
            saveButton?.setOnClickListener {

                var skill:String? = ""
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

                arr?.forEach {
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

//                skill = dataStorage.getSkillIDBySkillType(item.workExp.toString()).toString()
                skill = suggestionMap[item.workExp]
                
                ntvqf = getNtvqfLevel(NTVQF)


                if (skill.isNullOrEmpty()) Toast.makeText(activity, "No skill found", Toast.LENGTH_LONG).show()
                else if (refnameATCTV.getString().length == 2) activity.toast("Please type valid skill")
                else {

                    if (skillDuplicateStatus) {
                        activity.toast("The skill is already exists!")
                        skillDuplicateStatus = false
                        skillSourceNotEmptyStatus = false
                    } else {
                        if (addExpList!!.size == 10) {
                            activity?.toast("Skill maximum 10")
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
                                            NTVQF = ""
                                            addOrUpdateItem(skill!!, "", updateNewSkilledBy, ntvqf, "-1")
                                            skillDuplicateStatus = false
                                            skillSourceNotEmptyStatus = false
                                            ntvqfStatus = false

                                        }

                                    } else {
                                        NTVQF = ""
                                        addOrUpdateItem(skill!!, "", updateNewSkilledBy, ntvqf, "-1")
                                        skillDuplicateStatus = false
                                        skillSourceNotEmptyStatus = false
                                    }

                                } else {

                                    skillSourceNotEmptyStatus = false
                                    activity?.toast("Please select how you have learned the skill")

                                }
                            } else {
                                activity?.toast("Please type your Skill")
                                skillSourceNotEmptyStatus = false

                            }


                        }

                    }

                }


            }
        } catch (e: Exception) {
        }
        dialog?.show()

    }

    private fun fetchAutoSuggestion(query: String?,textView:AutoCompleteTextView) {
        Timber.d("Query: $query")
        ApiServiceMyBdjobs.create().fetchAutoSuggestion(query, "4").enqueue(object : Callback<AutoSuggestionModel> {
            override fun onFailure(call: Call<AutoSuggestionModel>, t: Throwable) {
                Timber.e("Failed Fetching Auto Suggestion: ${t.localizedMessage}")
            }

            override fun onResponse(call: Call<AutoSuggestionModel>, response: Response<AutoSuggestionModel>) {
                try {
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                val body = response.body()

                                if (body?.statuscode == "0") {

                                    list = body.data as ArrayList<DataAutoSuggestion>

                                    if (list.isNullOrEmpty()) Timber.d("List is empty / null")
                                    else {
                                        val suggestion = ArrayList<String>()
                                        for (i in list.indices) {
                                            suggestion.add(list[i].subName!!)

                                            if (!suggestionMap.containsKey(list[i].subName)) suggestionMap[list[i].subName] = list[i].subCatId
                                        }

                                        val a = ArrayAdapter<String>(activity!!, android.R.layout.simple_dropdown_item_1line, suggestion)
                                        textView.setAdapter(a)
                                        a.notifyDataSetChanged()
                                    }
                                }


                            }
                            else -> Timber.e("Response not fetched: Error: ${response.code()}")
                        }
                    } else Timber.e("Unsuccessful response: ${response.code()}")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Timber.e("Exception: ${e.localizedMessage}")
                }
            }

        })
    }

    private fun onClick() {

        addSkillButton?.onClick {
            showDialog(activity)
        }

        descriptionEditIcon?.onClick {

            eduCB.goToEditInfo("editSpecialization")

        }

        curicularEditIcon?.onClick {

            eduCB.goToEditInfo("editSpecialization")

        }


    }

    fun confirmationPopUp(item: String) {


        val builder = AlertDialog.Builder(activity)

        builder.setPositiveButton("ok") { _, which ->

            dataDelete(item)

        }
        builder.setNegativeButton("cancel") { dialog, which ->

            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.setTitle("Are you sure, want to delete this record?")
        dialog.show()


    }

    fun dataDelete(deleteItemId: String) {
        activity?.showProgressBar(newSpecializationLoadingProgressBar)
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
                        activity?.stopProgressBar(newSpecializationLoadingProgressBar)
                        val resp = response.body()
                        activity?.toast(resp?.message.toString())
                        populateData()

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
        //Log.d("nbnnn", skilledBy)
        //Log.d("nbnnn", skills)
        //Log.d("nbnnn", s_id)
        d("nbnnn  ${session.userId} ${session.decodId} ${session.IsResumeUpdate} $skills  $s_id  $skilledBy $ntvqfLevel  $insertOrUpdate")

        activity?.showProgressBar(newSpecializationLoadingProgressBar)
        ApiServiceMyBdjobs.create().specializationAddOrUpdate(session.userId, session.decodId, session.IsResumeUpdate!!, skills.trim(), s_id, skilledBy.trim(), ntvqfLevel, insertOrUpdate).enqueue(object : Callback<SpecializationAdModel> {
            override fun onFailure(call: Call<SpecializationAdModel>, t: Throwable) {

                activity?.toast(R.string.message_common_error)
                d("nbnnn ${t.message}")

            }

            override fun onResponse(call: Call<SpecializationAdModel>, response: Response<SpecializationAdModel>) {

                try {
                    if (response.isSuccessful) {
                        activity?.stopProgressBar(newSpecializationLoadingProgressBar)
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
                } catch (e: Exception) {
                    e.printStackTrace()
                    logException(e)
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

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }


}
