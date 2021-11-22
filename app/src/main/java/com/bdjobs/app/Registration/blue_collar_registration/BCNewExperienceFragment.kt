package com.bdjobs.app.Registration.blue_collar_registration

import android.annotation.SuppressLint
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
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.AddExpModel
import com.bdjobs.app.API.ModelClasses.BCWorkSkillModel
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.databases.External.DataStorage
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.footer_bc_layout.*
import kotlinx.android.synthetic.main.fragment_bc_experience.*
import kotlinx.android.synthetic.main.fragment_bc_experience.bcExperinceFAButton
import kotlinx.android.synthetic.main.fragment_bc_experience.categoryTV
import kotlinx.android.synthetic.main.fragment_bc_new_experience.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class BCNewExperienceFragment : Fragment() {


    private lateinit var registrationCommunicator: RegistrationCommunicator
    private var bcCategoryId: String = "0"
    private var category: String = ""
//    private lateinit var subCategories: Array<String>
    private lateinit var subCategories: ArrayList<String>
    private lateinit var levelList: Array<String>
    internal var count: Int = 0
    private lateinit var dataStorage: DataStorage
    private lateinit var returnView: View
    private lateinit var dialog: Dialog
    private lateinit var dialogEdit: Dialog
    private lateinit var categoryEdit: Dialog

    private lateinit var bcCategories: ArrayList<String>
    private lateinit var bcSkillAdapter: BCSkillAdapter
    private var layoutManager: RecyclerView.LayoutManager? = null

    private var addExpList: ArrayList<AddExpModel>? = ArrayList()
    private var workExp = ""
    private var NTVQF = ""
    private var skillDuplicateStatus = false
    private var skillSourceNotEmptyStatus = false
    private var ntvqfStatus = false

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        returnView = inflater.inflate(R.layout.fragment_bc_new_experience, container, false)
        return returnView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initialization()
        onClick()
    }

    private fun onClick() {

        bcAddExperienceButton.onClick {
            NTVQF = ""
            workExp = ""
            showDialog(activity)

        }

        bcExperinceFAButton?.setOnClickListener {
            var subCategoriesID = "0"
            var skilledBy = ""
            var ntvqfLevel = ""


            for (i in addExpList!!.indices) {
                if (i == addExpList!!.size - 1) {
                    subCategoriesID += dataStorage.getBlueCollarSubCategoryIDByName(addExpList!![i].workExp!!)
                } else {
                    subCategoriesID =
                            subCategoriesID + dataStorage.getBlueCollarSubCategoryIDByName(addExpList!![i].workExp!!) + ","
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

            if (addExpList!!.isNotEmpty()) {

                Timber.d("SubCat: $subCategoriesID .. CatID: $bcCategoryId")

                registrationCommunicator.bcSelectedBlueCollarSubCategoriesIDandExperince(
                        subCategoriesID,
                        bcNewExperienceYearTIET.getString(),
                        updateNewSkilledBy,
                        updateNtvqf, bcCategoryId, categoryTV.text.toString()
                )
                registrationCommunicator.bcGoToStepEducation()

            } else {
                activity.toast("কাজের ধরন/দক্ষতাগুলো নির্বাচন করুন")
            }

        }

        categoryEditIcon.setOnClickListener {

            showCategoryDialog()

        }


        supportTextView?.setOnClickListener {
            activity?.callHelpLine()
        }
        bcHelpLineLayout?.setOnClickListener {
            activity?.callHelpLine()
        }


    }

    private fun initialization() {
        registrationCommunicator = activity as RegistrationCommunicator
        categoryTV?.text = category
        dataStorage = DataStorage(activity!!)
//        subCategories = fetchSubCategories(bcCategoryId)
//        subCategories = dataStorage.getSubCategoriesByBlueCollarCategoryID(bcCategoryId)
        dataStorage = DataStorage(activity!!)
        bcCategories = dataStorage.allBlueCollarCategoriesInBangla

        levelList = arrayOf(
                "Pre-Voc Level 1", "Pre-Voc Level 2", "NTVQF Level 1",
                "NTVQF Level 2", "NTVQF Level 3", "NTVQF Level 4", "NTVQF Level 5", "NTVQF Level 6"
        )

        layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        skillListView?.layoutManager = layoutManager
        bcSkillAdapter = BCSkillAdapter(activity, addExpList)
        skillListView.adapter = bcSkillAdapter

        //----

    }

    fun categoryInformation(category: String, categoryID: String) {
        this.category = category
        this.bcCategoryId = categoryID
    }


    @SuppressLint("SetTextI18n")
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
        dialog.setContentView(R.layout.add_skill_dialog_layout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val addExperienceTIET = dialog.findViewById<TextInputEditText>(R.id.addExperienceTIET)
        val whereSkillText = dialog.findViewById<TextView>(R.id.whereSkillText)
        val firstCheckbox = dialog.findViewById<CheckBox>(R.id.firstCheckbox)
        val secondCheckBox = dialog.findViewById<CheckBox>(R.id.secondCheckBox)
        val thirdCheckBox = dialog.findViewById<CheckBox>(R.id.thirdCheckBox)
        val fourthCheckBox = dialog.findViewById<CheckBox>(R.id.fourthCheckBox)
        val fifthCheckBox = dialog.findViewById<CheckBox>(R.id.fifthCheckBox)
        val experienceLevelTIL = dialog.findViewById<TextInputLayout>(R.id.experienceLevelTIL)
        val experienceLevelTIET = dialog.findViewById<TextInputEditText>(R.id.experienceLevelTIET)
        val declineButton = dialog.findViewById<MaterialButton>(R.id.declineButton)
        val saveButton = dialog.findViewById<MaterialButton>(R.id.saveButton)
        saveButton?.isEnabled = false

//        subCategories = fetchSubCategories(bcCategoryId)
//        subCategories = dataStorage.getSubCategoriesByBlueCollarCategoryID(bcCategoryId)


        addExperienceTIET?.setOnClickListener {
            activity.selector("কাজের ধরন/দক্ষতা নির্বাচন করুন ", subCategories.toList()) { dialogInterface, i ->

                addExperienceTIET.setText(subCategories[i])
                whereSkillText.text = "কিভাবে '${subCategories[i]}' কাজের দক্ষতাটি শিখেছেন ?"
                workExp = subCategories[i]
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

                saveButton?.isEnabled = true
            }
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

                /*   d("expTest ${it.workExp}")*/

                if (it.workExp!!.equalIgnoreCase(item.workExp!!)) {

                    /*  d("expTest already exist")*/
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
                    /* dialog.dismiss()*/
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
                                    bcSkillAdapter.notifyItemInserted(addExpList!!.size - 1)
                                    skillDuplicateStatus = false
                                    skillSourceNotEmptyStatus = false
                                    ntvqfStatus = false
                                    dialog.dismiss()


                                }

                            } else {

                                d("fjnjfhn  in second condition ${item.expSource} ")

                                addExpList?.add(item)
                                bcSkillAdapter.notifyItemInserted(addExpList!!.size - 1)
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

                /* addExperienceLayout.show()*/
                skillListView.hide()

            } else {

                addExperienceLayout.hide()
                skillListView.show()
            }




            d("uhiuhiu addExpList?.size  ${addExpList?.size}")


        }
        dialog.show()

    }

    @SuppressLint("SetTextI18n")
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
        dialogEdit.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
        //saveButton?.isEnabled = false

        whereSkillText.show()
        firstCheckbox.show()
        secondCheckBox.show()
        thirdCheckBox.show()
        fourthCheckBox.show()
        fifthCheckBox.show()

        //set View start

        addExperienceTIET.setText(item.workExp)
        whereSkillText.text = "কিভাবে '${item.workExp}' কাজের দক্ষতাটি শিখেছেন ?"
        val list = item.expSource!!

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
        addExperienceTIET?.setOnClickListener {

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

        }
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
                d("expTest update position ${registrationCommunicator.getItemClick()} index $index")

                if (registrationCommunicator.getItemClick() != index) {

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

    fun showCategoryDialog() {


        categoryEdit = Dialog(activity)
        categoryEdit.requestWindowFeature(Window.FEATURE_NO_TITLE)
        categoryEdit.setCancelable(true)
        categoryEdit.setContentView(R.layout.add_category_dialog_layout)
        categoryEdit.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val skillTIET = categoryEdit.findViewById<TextInputEditText>(R.id.skillTIET)
        val declineButton = categoryEdit.findViewById<MaterialButton>(R.id.declineButton)
        val saveButton = categoryEdit.findViewById<MaterialButton>(R.id.saveButton)

        skillTIET?.setText(category)
        skillTIET?.setOnClickListener {


            activity.selector("ক্যাটাগরি নির্বাচন করুন", bcCategories.toList()) { _, i ->


                skillTIET.setText(bcCategories[i])

                this.bcCategoryId = dataStorage.getCategoryIDByBanglaName(bcCategories[i])!!
                this.subCategories = fetchSubCategories(bcCategoryId)
//                this.subCategories = dataStorage.getSubCategoriesByBlueCollarCategoryID(bcCategoryId)


                d("kflgflk ${bcCategories[i]} categoryId $bcCategoryId  subCategories ${subCategories.size}")

            }

        }
        declineButton?.setOnClickListener {
            categoryEdit.dismiss()
        }
        saveButton?.setOnClickListener {
            addExpList!!.clear()
            bcSkillAdapter.notifyDataSetChanged()
            skillListView.hide()
            addExperienceLayout.show()
            categoryTV?.text = skillTIET.getString()
            categoryEdit.dismiss()


            //added by Rakib
            category = categoryTV.text.toString()
            bcCategoryId = dataStorage.getCategoryIDByBanglaName(category)!!
            debug(" category id {$bcCategoryId}")
            registrationCommunicator.bcCategorySelected(category,bcCategoryId.toInt())
        }

        categoryEdit.show()
    }

    private fun updateItem(item: AddExpModel) {

        addExpList?.removeAt(registrationCommunicator.getItemClick())
        bcSkillAdapter.notifyItemRemoved(registrationCommunicator.getItemClick())
        bcSkillAdapter.notifyItemRangeChanged(registrationCommunicator.getItemClick(), addExpList!!.size - 1)
        addExpList?.add(registrationCommunicator.getItemClick(), item)
        bcSkillAdapter.notifyItemInserted(registrationCommunicator.getItemClick())
        bcSkillAdapter.notifyItemRangeChanged(registrationCommunicator.getItemClick(), addExpList!!.size - 1)


    }

    private fun fetchSubCategories(categoryID: String):ArrayList<String> {

        Timber.d("Fetch subCategories called!")
        val subCategories = ArrayList<String> ()
        ApiServiceMyBdjobs.create().workSkillSuggestionsBC(categoryID).enqueue(object: Callback<BCWorkSkillModel>{
            override fun onFailure(call: Call<BCWorkSkillModel>, t: Throwable) {
                Timber.e("Sub category fetch failed: ${t.localizedMessage}")
            }

            override fun onResponse(call: Call<BCWorkSkillModel>, response: Response<BCWorkSkillModel>) {

                if (response.isSuccessful) {
                    if (response.code()==200) {
                        val body = response.body()

                        if (body?.statuscode=="0") {
                            for (i in body.data?.indices!!) {
                                if (body.data[i].skillNameBn!="")
                                    subCategories.add(body.data[i].skillNameBn!!)
                            }
                        }

                    } else Timber.e("Failed_ code: ${response.code()}")
                } else Timber.e("Unsuccessful response")
            }

        })

        return subCategories
    }


    override fun onResume() {
        super.onResume()

        try {

            categoryTV?.text = registrationCommunicator.getCategory()
            this.subCategories = fetchSubCategories(registrationCommunicator.getCategoryId())
//            this.subCategories = dataStorage.getSubCategoriesByBlueCollarCategoryID(registrationCommunicator.getCategoryId())

            if (addExpList!!.size > 0) {

                skillListView.show()
                addExperienceLayout.hide()

            }
        } catch (e: Exception) {

        }


    }

    override fun onPause() {
        super.onPause()
        bcExperienceTIL?.hideKeyboard()
    }

}
