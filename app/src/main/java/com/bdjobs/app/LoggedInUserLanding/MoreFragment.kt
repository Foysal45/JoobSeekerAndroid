package com.bdjobs.app.LoggedInUserLanding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.Fragment
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.API.ModelClasses.MoreHorizontalData
import com.bdjobs.app.Employers.EmployersBaseActivity
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_more_layout.*
import org.jetbrains.anko.startActivity

class MoreFragment : Fragment() {

    private var horizontalAdapter: HorizontalAdapter? = null
    private var horizontaList: ArrayList<MoreHorizontalData> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_more_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeViews()
        clearAddPopulateData()
        onclick()
    }
    private fun onclick() {
        employerList_MBTN.setOnClickListener {
            startActivity<EmployersBaseActivity>("from" to "employer")
        }
    }
    private fun clearAddPopulateData() {
        /*this  function deletes duplicates data lists  */
        if (horizontaList.isNullOrEmpty()){
            populateData()
        }
        else {
            horizontalAdapter?.removeAll()
            horizontaList.clear()
            populateData()
        }
        horizontalAdapter?.addAll(horizontaList)
    }
    private fun populateData() {
        horizontaList.add(MoreHorizontalData(R.drawable.ic_manage, "Manage\nResume"))
        horizontaList.add(MoreHorizontalData(R.drawable.ic_applied, "Applied\nJobs"))
        horizontaList.add(MoreHorizontalData(R.drawable.ic_favorite, "Favorite\nSearch"))
        horizontaList.add(MoreHorizontalData(R.drawable.ic_followed, "Followed\nEmployers"))
    }
    private fun initializeViews(){
        horizontalAdapter = HorizontalAdapter(activity)
        horizontal_RV!!.adapter = horizontalAdapter
        horizontal_RV!!.setHasFixedSize(true)
        horizontal_RV?.layoutManager = LinearLayoutManager(activity, LinearLayout.HORIZONTAL, false)
        Log.d("initPag", "called")
        horizontal_RV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

    }

}