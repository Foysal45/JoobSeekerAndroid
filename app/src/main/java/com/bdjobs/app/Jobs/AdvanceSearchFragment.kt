package com.bdjobs.app.Jobs

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.Constants
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
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
        backIV.setOnClickListener {
            jobCommunicator.backButtonPressesd()
        }
        keywordET.setOnClickListener {
            jobCommunicator.goToSuggestiveSearch(Constants.key_jobtitleET, keywordET.text.toString())
        }
        generalCatET.setOnClickListener {
            jobCommunicator.goToSuggestiveSearch(Constants.key_categoryET, generalCatET.text.toString())
        }
        loacationET.setOnClickListener {
            jobCommunicator.goToSuggestiveSearch(Constants.key_loacationET, loacationET.text.toString())
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
        generalCatET.setText(dataStorage.getCategoryNameByID(jobCommunicator.getCategory()))
        loacationET.setText(dataStorage.getLocationNameByID(jobCommunicator.getLocation()))


        val count = orgCG.childCount
        for (i in 0 until count) {
            Log.d("chip", "text:$i")
            val chip = orgCG.getChildAt(i) as Chip
            Log.d("chip", "text: ${chip.text}")
        }

    }


}