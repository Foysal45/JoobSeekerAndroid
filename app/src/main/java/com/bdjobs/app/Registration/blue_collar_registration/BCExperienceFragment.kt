package com.bdjobs.app.Registration.blue_collar_registration

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.app.ProgressDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bdjobs.app.Databases.External.DataStorage

import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.callHelpLine
import kotlinx.android.synthetic.main.footer_bc_layout.*
import kotlinx.android.synthetic.main.fragment_bc_adress.*
import kotlinx.android.synthetic.main.fragment_bc_experience.*
import org.jetbrains.anko.makeCall
import java.util.ArrayList


class BCExperienceFragment : Fragment() {

    private lateinit var registrationCommunicator : RegistrationCommunicator

    private lateinit var categoryId: String
    private lateinit var category: String
    private lateinit var progressDialog: ProgressDialog
    private var selectedSubcategories = ArrayList<String>()
    private lateinit var subCategories: Array<String>
    internal var count: Int = 0
    private lateinit var dataStorage: DataStorage
    private lateinit var returnView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        returnView = inflater.inflate(R.layout.fragment_bc_experience, container, false)
        return returnView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onClick()
        initialization()
    }


    private fun onClick(){

        bcExperinceFAButton.setOnClickListener {

           if (bcExperienceTIET.text!!.length  > 0){


               var subCategoriesID = ""
               for (i in selectedSubcategories.indices) {
                   if (i == selectedSubcategories.size - 1) {
                       subCategoriesID = subCategoriesID + dataStorage.getBlueCollarSubCategoryIDByName(selectedSubcategories[i])
                   } else {
                       subCategoriesID = subCategoriesID + dataStorage.getBlueCollarSubCategoryIDByName(selectedSubcategories[i]) + ","
                   }
               }

               registrationCommunicator.bcSelectedBlueCollarSubCategoriesIDandExperince(subCategoriesID, bcExperienceYearTIET.text.toString())
               //progressDialog.setMessage("Please Wait");
               //progressDialog.show();
               registrationCommunicator.bcGoToStepEducation()

           }



        }

        bcExperienceTIET.setOnClickListener {


            val builder = AlertDialog.Builder(activity)
            builder.setTitle("আপনার কর্ম দক্ষতাগুলো নির্বাচন করুন")
                    .setMultiChoiceItems(subCategories, null) { dialog, which, isChecked ->
                        /*if (isChecked) {
                    selectedSubcategories.add(subCategories[which]);
                } else if (selectedSubcategories.contains(subCategories[which])) {
                    selectedSubcategories.remove(subCategories[which]);
                }*/
                        count += if (isChecked) 1 else -1
                        if (count <= 10) {
                            if (isChecked) {
                                // If the user checked the item, add it to the selected items
                                selectedSubcategories.add(subCategories[which])
                            } else if (selectedSubcategories.contains(subCategories[which])) {
                                // Else, if the item is already in the array, remove it
                                selectedSubcategories.remove(subCategories[which])
                            }
                        } else if (count > 10) {
                            Toast.makeText(activity, "কাজের ধরন ১০টির বেশি নির্বাচন করা যাবে না।",
                                    Toast.LENGTH_SHORT).show()

                            count--
                            (dialog as AlertDialog).listView.setItemChecked(which, false)
                        }
                    }
                    .setPositiveButton("ঠিক আছে") { dialog, id ->
                        var cat = ""
                        for (i in selectedSubcategories.indices) {

                            cat = cat + selectedSubcategories[i] + ", "
                        }
                        cat = cat.replace(", $".toRegex(), "")
                        bcExperienceTIET.setText(cat)
                        dialog.dismiss()
                    }
            val dialog = builder.create()
            dialog.setCancelable(false)
            dialog.show()
            if (selectedSubcategories.size != 0) {

                for (i in subCategories.indices) {
                    for (j in selectedSubcategories.indices) {
                        if (subCategories[i].equals(selectedSubcategories[j], ignoreCase = true)) {
                            dialog.listView.setItemChecked(i, true)
                        }
                    }
                }
            }



        }

        supportTextView.setOnClickListener {

           activity.callHelpLine()

        }

        bcHelpLineLayout.setOnClickListener {

            activity.callHelpLine()
        }


    }

    private fun initialization(){

        registrationCommunicator = activity as RegistrationCommunicator
        categoryTV.text = category

        registrationCommunicator = activity as RegistrationCommunicator
        dataStorage = DataStorage(activity)
        subCategories = dataStorage.getSubCategoriesByBlueCollarCategoryID(categoryId)

    }

    fun categoryInformation(category: String, categoryID: String) {
        this.category = category
        this.categoryId = categoryID
    }



}
