package com.bdjobs.app.LoggedInUserLanding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.Fragment
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.MybdjobsData
import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_mybdjobs_layout.*

class MyBdjobsFragment : Fragment() {


    private var mybdjobsAdapter: MybdjobsAdapter? = null
    private var bdjobsList: ArrayList<MybdjobsData> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_mybdjobs_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mybdjobsAdapter = MybdjobsAdapter(activity)

        myBdjobsgridView_RV!!.adapter = mybdjobsAdapter
        myBdjobsgridView_RV!!.setHasFixedSize(true)
        myBdjobsgridView_RV?.layoutManager = GridLayoutManager (activity, 2, RecyclerView.VERTICAL, false)
        Log.d("initPag", "called")
        myBdjobsgridView_RV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        bdjobsList.add(MybdjobsData("500", "Online\nApplications", R.drawable.online_application, R.drawable.ic_online_application))
        bdjobsList.add(MybdjobsData("20", "Times emailed\nmy resume",R.drawable.times_emailed, R.drawable.ic_view_resum))
        bdjobsList.add(MybdjobsData("300", "Employers viewed\nresume",R.drawable.viewed_resume, R.drawable.ic_privacy_policy))
        bdjobsList.add(MybdjobsData("400", "Employers\nFollowed",R.drawable.employer_followed, R.drawable.ic_privacy_policy))
        bdjobsList.add(MybdjobsData("50", "Interview\nInvitation",R.drawable.interview_invitation, R.drawable.ic_privacy_policy))
        bdjobsList.add(MybdjobsData("6", "Messages by\nEmployer",R.drawable.message_employers, R.drawable.ic_privacy_policy))
        // horizontaList.add(horizontalDataa
        mybdjobsAdapter?.addAll(bdjobsList)
    }
}