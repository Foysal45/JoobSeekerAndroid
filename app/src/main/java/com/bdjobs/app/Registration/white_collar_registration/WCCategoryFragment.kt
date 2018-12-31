package com.bdjobs.app.Registration.white_collar_registration


import android.os.Bundle
import android.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.Databases.External.DataStorage

import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.callHelpLine
import kotlinx.android.synthetic.main.footer_bc_layout.*
import kotlinx.android.synthetic.main.footer_wc_layout.*
import kotlinx.android.synthetic.main.fragment_wc_category.*
import org.jetbrains.anko.makeCall


class WCCategoryFragment : Fragment() {


    private lateinit var returnview : View
    private lateinit var registrationCommunicator : RegistrationCommunicator
    private lateinit var dataStorage: DataStorage
    private lateinit var categories: ArrayList<String>
    private lateinit var categoryAdapter: WCCategoryAdapter
    private var layoutManager: RecyclerView.LayoutManager? = null
    internal var selectedPosition = -1
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        intialization()

        onClick()




    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        returnview = inflater.inflate(R.layout.fragment_wc_category, container, false)

        return returnview
    }



    private fun onClick(){

        Log.d("selectedPosition","selectedPosition in fragment $selectedPosition  }")
        wcSupportTextView.setOnClickListener {

            activity.callHelpLine()

        }

        wcHelplineLayout.setOnClickListener {

            activity.callHelpLine()
        }


    }

    private fun intialization(){
        registrationCommunicator = activity as RegistrationCommunicator
        dataStorage = DataStorage(activity)
        categories = dataStorage.allWhiteCollarCategories

        Log.d("elkgjtsdlg","Size ${categories.size}")

        categoryAdapter = WCCategoryAdapter(activity,categories)


        layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        wcCategoryList?.layoutManager = layoutManager

        wcCategoryList.adapter = categoryAdapter



    }


    override fun onResume() {
        super.onResume()

        val whiteCollarCategoryAdapter = WCCategoryAdapter(activity, categories)
        wcCategoryList.setAdapter(whiteCollarCategoryAdapter)
        whiteCollarCategoryAdapter.SetCategoryPositionSelected(selectedPosition)
        whiteCollarCategoryAdapter.notifyDataSetChanged()
        if (selectedPosition != -1) {
           wcGoToNextStep()
        }

    }

     fun wcGoToNextStep(){

        floatingActionButton.setOnClickListener {
            registrationCommunicator.wcGoToStepSocialInfo()
        }


    }
}
