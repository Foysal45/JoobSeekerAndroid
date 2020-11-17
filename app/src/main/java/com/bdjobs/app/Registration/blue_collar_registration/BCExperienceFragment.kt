package com.bdjobs.app.Registration.blue_collar_registration

import android.app.AlertDialog
import android.app.Fragment
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bdjobs.app.databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.callHelpLine
import com.bdjobs.app.Utilities.showError
import kotlinx.android.synthetic.main.footer_bc_layout.*
import kotlinx.android.synthetic.main.fragment_bc_experience.*
import java.util.*


class BCExperienceFragment : Fragment() {

    private lateinit var registrationCommunicator: RegistrationCommunicator
    private var categoryId: String = ""
    private var category: String = ""
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
        initialization()
        onClick()
    }


    private fun onClick() {
        bcExperinceFAButton?.setOnClickListener {
            if (bcExperienceTIET?.text!!.isNotEmpty()) {
                var subCategoriesID = ""
                for (i in selectedSubcategories.indices) {
                    if (i == selectedSubcategories.size - 1) {
                        subCategoriesID += dataStorage.getBlueCollarSubCategoryIDByName(selectedSubcategories[i])
                    } else {
                        subCategoriesID = subCategoriesID + dataStorage.getBlueCollarSubCategoryIDByName(selectedSubcategories[i]) + ","
                    }
                }
               /* registrationCommunicator.bcSelectedBlueCollarSubCategoriesIDandExperince(subCategoriesID, bcExperienceYearTIET.getString())
                registrationCommunicator.bcGoToStepEducation()*/
            }else {
                bcExperienceTIL?.showError("কর্ম দক্ষতা গুলো নির্বাচন করুন")
            }
        }
        bcExperienceTIET?.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("নিচের অপশন থেকে কাজের ধরন নির্বাচন করুন")
                    .setMultiChoiceItems(subCategories, null) { dialog, which, isChecked ->
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
                            Toast.makeText(activity, "সর্বোচ্চ ১০টি কর্ম দক্ষতা নির্বাচন করতে পারবেন",
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
                        bcExperienceTIET?.setText(cat)
                        bcExperienceTIL?.isErrorEnabled = false
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
        dataStorage = DataStorage(activity)
        subCategories = dataStorage.getSubCategoriesByBlueCollarCategoryID(categoryId)
    }

    fun categoryInformation(category: String, categoryID: String) {
        this.category = category
        this.categoryId = categoryID
    }
}
