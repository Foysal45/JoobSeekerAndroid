package com.bdjobs.app.LoggedInUserLanding

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.R
import com.bdjobs.app.editResume.employmentHistory.EmploymentHistoryActivity
import com.bdjobs.app.editResume.otherInfo.OtherInfoActivity
import com.bdjobs.app.editResume.personalInfo.PersonalInfoActivity
import kotlinx.android.synthetic.main.fragment_more_layout.*

class MoreFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_more_layout, container, false)!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnEh.setOnClickListener {
            startActivity(Intent(activity, EmploymentHistoryActivity::class.java))
        }
        btnOt.setOnClickListener {
            startActivity(Intent(activity, OtherInfoActivity::class.java))
        }
        btnPer.setOnClickListener {
            startActivity(Intent(activity, PersonalInfoActivity::class.java))
        }
    }

}