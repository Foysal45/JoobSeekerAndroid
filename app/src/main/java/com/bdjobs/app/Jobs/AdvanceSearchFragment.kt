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
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.clearTextOnDrawableRightClick
import com.bdjobs.app.Utilities.easyOnTextChangedListener
import com.bdjobs.app.Utilities.logException
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_advance_search_layout.*

class AdvanceSearchFragment : Fragment() {
    lateinit var jobCommunicator: JobCommunicator
    lateinit var dataStorage: DataStorage

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
        keywordET.easyOnTextChangedListener {
            showHideCrossButton(keywordET)
        }

        generalCatET.easyOnTextChangedListener {
            showHideCrossButton(generalCatET)
        }

        loacationET.easyOnTextChangedListener {
            showHideCrossButton(loacationET)
        }
        specialCatET.easyOnTextChangedListener {
            showHideCrossButton(specialCatET)
        }
        newsPaperET.easyOnTextChangedListener {
            showHideCrossButton(newsPaperET)
        }
        industryET.easyOnTextChangedListener {
            showHideCrossButton(industryET)
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

        orgCG.setOnCheckedChangeListener { chipGroup, i ->
            Log.d("chip", "id: $i")
            if (i >= 0) {
                val chip = chipGroup.findViewById(i) as Chip
                Log.d("chip", "text: ${chip.text}")
            } else {
                Log.d("chip", "text: nothing is selected")
            }
        }
    }


    override fun onResume() {
        super.onResume()

        keywordET.setText(jobCommunicator.getKeyword())

        try {
            if (jobCommunicator.getCategory().toInt() < 30) {
                generalCatET.setText(dataStorage.getCategoryNameByID(jobCommunicator.getCategory()))
                specialCatET.text?.clear()
            }
            else{
                generalCatET.text?.clear()
            }
        } catch (e: Exception) {
            logException(e)
            generalCatET.text?.clear()
        }

        try {
            if (jobCommunicator.getCategory().toInt() > 60) {
                specialCatET.setText(dataStorage.getCategoryBanglaNameByID(jobCommunicator.getCategory()))
                generalCatET.text?.clear()
            }
            else{
                specialCatET.text?.clear()
            }
        } catch (e: Exception) {
            logException(e)
            specialCatET.text?.clear()
        }

        loacationET.setText(dataStorage.getLocationNameByID(jobCommunicator.getLocation()))
        newsPaperET.setText(dataStorage.getNewspaperNameById(jobCommunicator.getNewsPaper()))
        industryET.setText(dataStorage.getJobSearcIndustryNameByID(jobCommunicator.getIndustry()))


        val count = orgCG.childCount
        for (i in 0 until count) {
            Log.d("chip", "text:$i")
            val chip = orgCG.getChildAt(i) as Chip
            Log.d("chip", "text: ${chip.text}")
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