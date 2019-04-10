package com.bdjobs.app.Registration.blue_collar_registration

import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
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
import kotlinx.android.synthetic.main.fragment_bc_category.*
import org.jetbrains.anko.toast


class BCCategoryFragment : Fragment() {

    private lateinit var returnview: View
    private lateinit var registrationCommunicator: RegistrationCommunicator
    private lateinit var dataStorage: DataStorage
    private lateinit var bcCategories: ArrayList<String>
    private lateinit var bcCategoryAdapter: BCCategoryAdapter
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var selectedPosition = -1

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


    private fun onClick() {
        supportTextView?.setOnClickListener {
            activity?.callHelpLine()
        }
        bcHelpLineLayout?.setOnClickListener {
            activity?.callHelpLine()
        }
        bcCategoryFAButton?.setOnClickListener {
            if (TextUtils.isEmpty(registrationCommunicator.getCategory())) {
                activity?.toast("অন্তত একটি দক্ষতা নির্বাচন করুন")
            }
        }
    }

    private fun intialization() {
        registrationCommunicator = activity as RegistrationCommunicator
        dataStorage = DataStorage(activity)
        bcCategories = dataStorage.allBlueCollarCategoriesInBangla
        bcCategoryAdapter = BCCategoryAdapter(activity, bcCategories)
        layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        bcCategoryList?.layoutManager = layoutManager
        bcCategoryList.adapter = bcCategoryAdapter
    }

    fun bcGoToNextStep() {
        bcCategoryFAButton?.setOnClickListener {
            registrationCommunicator.bcGoToStepName()
        }
    }


    override fun onResume() {
        super.onResume()
        val bcCategoryAdapter = BCCategoryAdapter(activity, bcCategories)
        bcCategoryList.adapter = bcCategoryAdapter
        bcCategoryAdapter.SetCategoryPositionSelected(selectedPosition)
        bcCategoryFAButton?.setOnClickListener {
            if (TextUtils.isEmpty(registrationCommunicator.getCategory())) {
                activity?.toast("অন্তত একটি দক্ষতা নির্বাচন করুন")
            }
        }
        if (selectedPosition != -1) {
            bcCategoryFAButton?.setOnClickListener {
                registrationCommunicator.bcGoToStepName()
            }
        }
    }
    fun getSelectedPosition(position: Int) {
        selectedPosition = position
    }
}
