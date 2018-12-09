package com.bdjobs.app.Registration


import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.Databases.External.DataStorage

import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_registration_landing.*
import org.jetbrains.anko.toast


class RegistrationLandingFragment : Fragment() {

    private lateinit var  registrationCommunicator : RegistrationCommunicator
    private lateinit var returnView : View
    private lateinit var dataStrage : DataStorage
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        returnView = inflater.inflate(R.layout.fragment_registration_landing, container, false)
        return returnView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        intialization()
        onClick()
    }

   private fun onClick(){


       whiteCollar.setOnClickListener {

           registrationCommunicator.gotToStepWhiteCollar()


       }
       blueCollar.setOnClickListener {

           registrationCommunicator.goToStepBlueCollar()

       }

    }



   private fun intialization(){


       registrationCommunicator = activity as RegistrationCommunicator
       dataStrage = DataStorage(activity)
       bcCategoryTV.isSelected = true
       wcCategoryTV.isSelected = true

       val allWCCategory = dataStrage.allWhiteCollarCategories.toString().replace("[","")
       val allBCcategory = dataStrage.allBlueCollarCategoriesInBangla.toString().replace("[","")
       wcCategoryTV.text = allWCCategory.replace("]","")
       bcCategoryTV.text = allBCcategory.replace("]","")



    }

}
