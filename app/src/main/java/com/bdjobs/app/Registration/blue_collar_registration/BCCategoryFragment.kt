package com.bdjobs.app.Registration.blue_collar_registration

import android.content.Context
import android.net.Uri
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
import com.bdjobs.app.Registration.white_collar_registration.WCCategoryAdapter
import kotlinx.android.synthetic.main.fragment_bc_category.*
import kotlinx.android.synthetic.main.fragment_wc_category.*


class BCCategoryFragment : Fragment() {


    private lateinit var returnview : View
    private lateinit var registrationCommunicator : RegistrationCommunicator
    private lateinit var dataStorage: DataStorage
    private lateinit var bcCategories: ArrayList<String>
    private lateinit var bcCategoryAdapter: BCCategoryAdapter
    private var layoutManager: RecyclerView.LayoutManager? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        returnview = inflater.inflate(R.layout.fragment_bc_category, container, false)
        return returnview
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        intialization()
        onClick()
    }


    private fun onClick(){

        bcCategoryFAButton.setOnClickListener {
            registrationCommunicator.bcGoToStepName()
        }


    }

    private fun intialization(){


        registrationCommunicator = activity as RegistrationCommunicator
        dataStorage = DataStorage(activity)
        bcCategories = dataStorage.allBlueCollarCategoriesInBangla

        bcCategoryAdapter = BCCategoryAdapter(activity,bcCategories)
        layoutManager = LinearLayoutManager(activity,LinearLayout.VERTICAL, false)
        bcCategoryList?.layoutManager = layoutManager

        bcCategoryList.adapter = bcCategoryAdapter


    }



}
