package com.bdjobs.app.Jobs

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_advance_search_layout.*
import java.util.*
import java.util.Arrays.asList


class AdvanceSearchFragment : Fragment() {
    lateinit var jobCommunicator: JobCommunicator
    lateinit var dataStorage: DataStorage
    var gender: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_advance_search_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        jobCommunicator = activity as JobCommunicator
        dataStorage = DataStorage(activity)

        onClicks()
    }

    private fun onClicks() {
        keywordET.easyOnTextChangedListener { text ->
            showHideCrossButton(keywordET)
            if (text.isBlank()) {
                Log.d("catTest", "typedData : isBlank")
                jobCommunicator.setKeyword("")
            }
        }
        generalCatET.easyOnTextChangedListener { text ->
            showHideCrossButton(generalCatET)
            if (text.isBlank()) {
                Log.d("catTest", "typedData : isBlank")
                jobCommunicator.setCategory("")
            }

        }

        loacationET.easyOnTextChangedListener { text ->
            showHideCrossButton(loacationET)
            if (text.isBlank()) {
                Log.d("catTest", "typedData : isBlank")
                jobCommunicator.setLocation("")
            }
        }
        specialCatET.easyOnTextChangedListener { text ->
            showHideCrossButton(specialCatET)
            if (text.isBlank()) {
                Log.d("catTest", "typedData : isBlank")
                jobCommunicator.setCategory("")
            }

        }
        newsPaperET.easyOnTextChangedListener { text ->
            showHideCrossButton(newsPaperET)
            if (text.isBlank()) {
                Log.d("catTest", "typedData : isBlank")
                jobCommunicator.setNewsPaper("")
            }
        }
        industryET.easyOnTextChangedListener { text ->
            showHideCrossButton(industryET)
            if (text.isBlank()) {
                Log.d("catTest", "typedData : isBlank")
                jobCommunicator.setIndustry("")
            }
        }




        backIV.setOnClickListener {
            jobCommunicator.backButtonPressesd()
        }
        keywordET.setOnClickListener {
            jobCommunicator.goToSuggestiveSearch(Constants.key_jobtitleET, keywordET.text.toString())
        }
        generalCatET.setOnClickListener {
            jobCommunicator.goToSuggestiveSearch(Constants.key_categoryET, generalCatET.text.toString())
        }
        specialCatET.setOnClickListener {
            jobCommunicator.goToSuggestiveSearch(Constants.key_special_categoryET, specialCatET.text.toString())
        }
        loacationET.setOnClickListener {
            jobCommunicator.goToSuggestiveSearch(Constants.key_loacationET, loacationET.text.toString())
        }

        newsPaperET.setOnClickListener {
            jobCommunicator.goToSuggestiveSearch(Constants.key_newspaperET, newsPaperET.text.toString())
        }

        industryET.setOnClickListener {
            jobCommunicator.goToSuggestiveSearch(Constants.key_industryET, industryET.text.toString())
        }

        getDataFromChipGroup(orgCG)
        getDataFromChipGroup(experienceCG)
        getDataFromChipGroup(jobTypeCG)
        getDataFromChipGroup(jobLevelCG)
        getDataFromChipGroup(jobNatureCG)
        getDataFromChipGroup(postedWithinCG)
        getDataFromChipGroup(deadlineCG)
        getDataFromChipGroup(ageRangeCG)
        getDataFromChipGroup(armyCG)


        maleChip.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                gender += "M,"
            } else {
                gender = gender.replace("M,", "")
            }
            jobCommunicator.setGender(gender.removeLastComma())
            Log.d("GenderCheck", "gender: ${jobCommunicator.getGender()}")
        }

        femaleChip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                gender += "F,"
            } else {
                gender = gender.replace("F,", "")
            }
            jobCommunicator.setGender(gender.removeLastComma())
            Log.d("GenderCheck", "gender: ${jobCommunicator.getGender()}")
        }

        otherChip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                gender += "O,"
            } else {
                gender = gender.replace("O,", "")
            }
            jobCommunicator.setGender(gender.removeLastComma())
            Log.d("GenderCheck", "gender: ${jobCommunicator.getGender()}")
        }


    }


    private fun getDataFromChipGroup(chipGroup: ChipGroup) {
        chipGroup.setOnCheckedChangeListener { chipGroup, i ->
            if (i > 0) {
                val chip = chipGroup.findViewById(i) as Chip
                Log.d("chip", "text: ${chip.text}")
                val data = chip.text.toString()
                when (chipGroup.id) {
                    R.id.orgCG -> {
                        jobCommunicator.setOrganization(dataStorage.getJobSearcOrgTypeIDByName(data)!!)
                    }
                    R.id.experienceCG -> {
                        jobCommunicator.setExperience(dataStorage.getJobExperineceIDByName(data)!!)
                    }
                    R.id.jobTypeCG -> {
                        jobCommunicator.setJobType(dataStorage.getJobTypeIDByName(data)!!)
                    }
                    R.id.jobLevelCG -> {
                        jobCommunicator.setJobLevel(dataStorage.getJobLevelIDByName(data.toLowerCase())!!)
                    }
                    R.id.jobNatureCG -> {
                        jobCommunicator.setJobNature(dataStorage.getJobNatureIDByName(data.toLowerCase())!!)
                    }
                    R.id.postedWithinCG -> {
                        jobCommunicator.setPostedWithin(dataStorage.getPostedWithinIDByName(data)!!)
                    }
                    R.id.deadlineCG -> {
                        jobCommunicator.setDeadline(dataStorage.getDeadlineIDByNAme(data)!!)
                    }
                    R.id.ageRangeCG -> {
                        jobCommunicator.setAge(dataStorage.getAgeRangeIDByName(data)!!)
                    }
                    R.id.armyCG -> {
                        jobCommunicator.setArmy("1")
                    }
                }
            } else {
                when (chipGroup.id) {
                    R.id.orgCG -> {
                        jobCommunicator.setOrganization("")
                    }
                    R.id.experienceCG -> {
                        jobCommunicator.setExperience("")
                    }
                    R.id.jobTypeCG -> {
                        jobCommunicator.setJobType("")
                    }
                    R.id.jobLevelCG -> {
                        jobCommunicator.setJobLevel("")
                    }
                    R.id.jobNatureCG -> {
                        jobCommunicator.setJobNature("")
                    }
                    R.id.postedWithinCG -> {
                        jobCommunicator.setPostedWithin("")
                    }
                    R.id.deadlineCG -> {
                        jobCommunicator.setDeadline("")
                    }
                    R.id.ageRangeCG -> {
                        jobCommunicator.setAge("")
                    }
                    R.id.armyCG -> {
                        jobCommunicator.setArmy("")
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        setGenderData()
        keywordET.setText(jobCommunicator.getKeyword())

        Log.d("catTest", "category : ${jobCommunicator.getCategory()}")

        if (jobCommunicator.getCategory().isNotBlank()) {
            if (jobCommunicator.getCategory().toInt() < 30) {
                generalCatET.setText(dataStorage.getCategoryNameByID(jobCommunicator.getCategory()))
                specialCatET.text?.clear()
            } else {
                generalCatET.text?.clear()
            }

            if (jobCommunicator.getCategory().toInt() > 60) {
                specialCatET.setText(dataStorage.getCategoryBanglaNameByID(jobCommunicator.getCategory()))
                generalCatET.text?.clear()
            } else {
                specialCatET.text?.clear()
            }
        }


        loacationET.setText(dataStorage.getLocationNameByID(jobCommunicator.getLocation()))
        newsPaperET.setText(dataStorage.getNewspaperNameById(jobCommunicator.getNewsPaper()))
        industryET.setText(dataStorage.getJobSearcIndustryNameByID(jobCommunicator.getIndustry()))
        selectChip(orgCG, dataStorage.getJobSearcOrgTypeByID(jobCommunicator?.getOrganization())!!)
        selectChip(experienceCG, dataStorage.getJobExperineceByID(jobCommunicator?.getExperience())!!)
        selectChip(jobTypeCG, dataStorage.getJobTypeByID(jobCommunicator?.getJobType())!!)
        selectChip(jobLevelCG, dataStorage.getJobLevelByID(jobCommunicator?.getJobLevel())!!)
        selectChip(jobNatureCG, dataStorage.getJobNatureByID(jobCommunicator?.getJobNature())!!)
        selectChip(postedWithinCG, dataStorage.getPostedWithinNameByID(jobCommunicator?.getPostedWithin())!!)
        selectChip(deadlineCG, dataStorage.getDedlineNameByID(jobCommunicator?.getDeadline())!!)
        selectChip(ageRangeCG, dataStorage.getAgeRangeNameByID(jobCommunicator?.getAge())!!)

        if (jobCommunicator.getArmy() == "1") {
            selectChip(armyCG, "Yes")
        }

    }

    private fun setGenderData() {
        gender = ""
        maleChip.isChecked = false
        femaleChip.isChecked = false
        otherChip.isChecked = false
    }

    private fun selectChip(chipGroup: ChipGroup, data: String) {
        val count = chipGroup.childCount
        for (i in 0 until count) {
            val chip = chipGroup.getChildAt(i) as Chip
            val chipText = chip.text.toString()
            if (data.equalIgnoreCase(chipText)) {
                Log.d("chip", "text:$i")
                chip.isChecked = true
            }
        }
    }


    private fun showHideCrossButton(editText: EditText) {
        if (editText.text.isBlank()) {
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_ash, 0)
            editText.clearTextOnDrawableRightClick()
        }
    }


}