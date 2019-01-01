package com.bdjobs.app.LoggedInUserLanding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.Fragment
import android.graphics.Color
import android.graphics.Color.*
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.MybdjobsData
import com.bdjobs.app.R
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapePathModel
import kotlinx.android.synthetic.main.fragment_mybdjobs_layout.*

class MyBdjobsFragment : Fragment() {


    private var mybdjobsAdapter: MybdjobsAdapter? = null
    private var bdjobsList: ArrayList<MybdjobsData> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater?.inflate(R.layout.fragment_mybdjobs_layout, container, false)!!


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)




        val leftShapePathModel = ShapePathModel()
        leftShapePathModel.topLeftCorner = RoundedCornerTreatment(50F)
        leftShapePathModel.bottomLeftCorner= RoundedCornerTreatment(50F)
        val leftRoundedMaterialShape = MaterialShapeDrawable(leftShapePathModel)
        lastmonth_MBTN.setBackgroundDrawable(leftRoundedMaterialShape)


        val leftShapePathModel1 = ShapePathModel()
        leftShapePathModel1.topRightCorner = RoundedCornerTreatment(50F)
        leftShapePathModel1.bottomRightCorner= RoundedCornerTreatment(50F)
        val leftRoundedMaterialShape1 = MaterialShapeDrawable(leftShapePathModel1)
        alltime_MBTN.setBackgroundDrawable(leftRoundedMaterialShape1)
        alltime_MBTN.setBackgroundResource(R.drawable.online_application)
        alltime_MBTN.setTextColor(BLACK)


        initializeViews()
        if (bdjobsList.isNullOrEmpty()){
            populateData()
        }
        else {
            mybdjobsAdapter?.removeAll()
            bdjobsList.clear()
            populateData()
            Log.d("ddd", "asche")
        }



    }
    private fun initializeViews(){
        mybdjobsAdapter = MybdjobsAdapter(activity)
        myBdjobsgridView_RV!!.adapter = mybdjobsAdapter
        myBdjobsgridView_RV!!.setHasFixedSize(true)
        myBdjobsgridView_RV?.layoutManager = GridLayoutManager (activity, 2, RecyclerView.VERTICAL, false) as RecyclerView.LayoutManager?
        Log.d("initPag", "called")
        myBdjobsgridView_RV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
    }
    private fun populateData(){
        bdjobsList.add(MybdjobsData("500", "Online\nApplications", R.drawable.online_application, R.drawable.ic_online_application))
        bdjobsList.add(MybdjobsData("20", "Times emailed\nmy resume",R.drawable.times_emailed, R.drawable.ic_view_resum))
        bdjobsList.add(MybdjobsData("300", "Employers viewed\nresume",R.drawable.viewed_resume, R.drawable.ic_privacy_policy))
        bdjobsList.add(MybdjobsData("400", "Employers\nFollowed",R.drawable.employer_followed, R.drawable.ic_privacy_policy))
        bdjobsList.add(MybdjobsData("50", "Interview\nInvitation",R.drawable.interview_invitation, R.drawable.ic_privacy_policy))
        bdjobsList.add(MybdjobsData("6", "Messages by\nEmployer",R.drawable.message_employers, R.drawable.ic_privacy_policy))
        // horizontaList.add(horizontalDataa
        //Log.d("ddd", bdjobsList.toString())


           // mybdjobsAdapter?.removeAll()
        mybdjobsAdapter?.addAll(bdjobsList)


    }
}