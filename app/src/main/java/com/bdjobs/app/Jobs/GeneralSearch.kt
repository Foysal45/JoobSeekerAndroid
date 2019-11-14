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
import kotlinx.android.synthetic.main.fragment_general_search_layout.*

class GeneralSearch : Fragment() {
    lateinit var jobCommunicator: JobCommunicator
    lateinit var dataStorage: DataStorage
    var gender: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_general_search_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        jobCommunicator = activity as JobCommunicator
        dataStorage = DataStorage(activity)
        jobCommunicator.setBackFrom("")
        onClicks()
    }

    private fun onClicks() {
        searchBTN_fab?.setOnClickListener {
            jobCommunicator.gotoJobList()
        }

        generalCatET?.easyOnTextChangedListener { text ->
            showHideCrossButton(generalCatET)
            if (text.isBlank()) {
                Log.d("catTest", "generalCatET : isBlank")
                try {
                    val catid = jobCommunicator.getCategory()?.trim()?.toInt()
                    if (catid in 1..30 || catid == -10) {
                        Log.d("eryfdh", "white")
                        jobCommunicator.setCategory("")
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        }

        specialCatET?.easyOnTextChangedListener { text ->
            showHideCrossButton(specialCatET)
            if (text.isBlank()) {
                Log.d("catTest", "specialCatET : isBlank")
                try {
                    val catid = jobCommunicator.getCategory()?.trim()?.toInt()
                    if (catid!! > 60 || catid == -11) {
                        Log.d("eryfdh", "blue")
                        jobCommunicator.setCategory("")
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        }

        keywordET?.easyOnTextChangedListener { text ->
            showHideCrossButton(keywordET)
            if (text.isBlank()) {
                Log.d("catTest", "typedData : isBlank")
                jobCommunicator.setKeyword("")
            }
        }

        loacationET?.easyOnTextChangedListener { text ->
            showHideCrossButton(loacationET)
            if (text.isBlank()) {
                Log.d("catTest", "typedData : isBlank")
                jobCommunicator.setLocation("")
            }
        }


        backIV?.setOnClickListener {
            jobCommunicator.backButtonPressesd()
        }
        keywordET?.setOnClickListener {
            jobCommunicator.goToSuggestiveSearch(Constants.key_jobtitleET, keywordET.text.toString())
        }
        generalCatET?.setOnClickListener {
            jobCommunicator.goToSuggestiveSearch(Constants.key_categoryET, generalCatET.text.toString())
        }
        specialCatET?.setOnClickListener {
            jobCommunicator.goToSuggestiveSearch(Constants.key_special_categoryET, specialCatET.text.toString())
        }
        loacationET?.setOnClickListener {
            jobCommunicator.goToSuggestiveSearch(Constants.key_loacationET, loacationET.text.toString())
        }

        getDataFromChipGroup(experienceCG)

    }

    private fun getDataFromChipGroup(chipGroup: ChipGroup) {
        chipGroup.setOnCheckedChangeListener { chipGroup, i ->
            if (i > 0) {
                val chip = chipGroup.findViewById(i) as Chip
                Log.d("chip_entry", "text: ${chip.text}")
                val data = chip.text.toString()
                when (chipGroup.id) {
                    R.id.orgCG -> {
                        jobCommunicator.setOrganization(dataStorage.getJobSearcOrgTypeIDByName(data))
                    }
                    R.id.experienceCG -> {
                        jobCommunicator.setExperience(dataStorage.getJobExperineceIDByName(data))
                    }
                    R.id.jobTypeCG -> {
                        jobCommunicator.setJobType(dataStorage.getJobTypeIDByName(data))
                    }
                    R.id.jobLevelCG -> {
                        jobCommunicator.setJobLevel(dataStorage.getJobLevelIDByName(data.toLowerCase()))
                    }
                    R.id.jobNatureCG -> {
                        jobCommunicator.setJobNature(dataStorage.getJobNatureIDByName(data.toLowerCase()))
                    }
                    R.id.postedWithinCG -> {
                        jobCommunicator.setPostedWithin(dataStorage.getPostedWithinIDByName(data))
                    }
                    R.id.deadlineCG -> {
                        jobCommunicator.setDeadline(dataStorage.getDeadlineIDByNAme(data))
                    }
                    R.id.ageRangeCG -> {
                        jobCommunicator.setAge(dataStorage.getAgeRangeIDByName(data))
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
        keywordET?.setText(jobCommunicator?.getKeyword())
        Log.d("eryfdh", "category Adv : ${jobCommunicator?.getCategory()}")

        try {
            val catid = jobCommunicator?.getCategory()?.trim()?.toInt()
            if (catid!! > 60 || catid == -11) {
                Log.d("eryfdh", "blue")
                specialCatET?.setText(dataStorage?.getCategoryBanglaNameByID(jobCommunicator?.getCategory()))
                generalCatET.text?.clear()
            } else if (catid in 1..30 || catid == -10) {
                Log.d("eryfdh", "white")
                generalCatET?.setText(dataStorage?.getCategoryNameByID(jobCommunicator?.getCategory()))
                specialCatET?.text?.clear()
            }
        } catch (e: Exception) {
            logException(e)
        }

        loacationET?.setText(dataStorage?.getLocationNameByID(jobCommunicator?.getLocation()))
        try {
            selectChip(experienceCG, dataStorage?.getJobExperineceByID(jobCommunicator?.getExperience()))
        } catch (e: Exception) {
            logException(e)
        }

    }


    private fun selectChip(chipGroup: ChipGroup?, data: String?) {
        try {
            val count = chipGroup?.childCount
            for (i in 0 until count!!) {
                val chip = chipGroup.getChildAt(i) as Chip
                val chipText = chip.text.toString()
                if (data?.equalIgnoreCase(chipText)!!) {
                    Log.d("chip_entry", "text:$i")
                    chip.isChecked = true
                }
            }
        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun showHideCrossButton(editText: EditText) {
        try {
            if (editText?.text?.isBlank()!!) {
                editText?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_advance_search_24dp, 0)
            } else {
                editText?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close_ash, 0)
                editText?.clearTextOnDrawableRightClick()
            }
        } catch (e: Exception) {
            logException(e)
        }
    }
}