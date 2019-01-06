package com.bdjobs.app.editResume

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import com.bdjobs.app.R
import com.bdjobs.app.editResume.educationInfo.AcademicBaseActivity
import com.bdjobs.app.editResume.employmentHistory.EmploymentHistoryActivity
import com.bdjobs.app.editResume.otherInfo.OtherInfoActivity
import com.bdjobs.app.editResume.personalInfo.PersonalInfoActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_edit_res_landing.*

class EditResLandingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_res_landing)
        //setContentView(R.layout.edit_res_landing_test)
        //setToolbarTitle(appBar, collapsingToolbar, "Edit Resume")


        btnEh.setOnClickListener {
            startActivity(Intent(this@EditResLandingActivity, EmploymentHistoryActivity::class.java))
        }
        btnOt.setOnClickListener {
            startActivity(Intent(this@EditResLandingActivity, OtherInfoActivity::class.java))
        }
        btnPer.setOnClickListener {
            startActivity(Intent(this@EditResLandingActivity, PersonalInfoActivity::class.java))
        }
        btnPer.setOnClickListener {
            startActivity(Intent(this@EditResLandingActivity, PersonalInfoActivity::class.java))
        }
        btnEdu.setOnClickListener {
            startActivity(Intent(this@EditResLandingActivity, AcademicBaseActivity::class.java))
        }


        photoUpload.setOnClickListener {

            startActivity(Intent(this@EditResLandingActivity, PhotoUploadActivity::class.java))


        }


    }


    fun setToolbarTitle(appBarLayout: AppBarLayout, collapsingToolbarLayout: CollapsingToolbarLayout, title: String) {
        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.title = SpannableString(title)
                    isShow = true
                } else if (isShow) {
                    collapsingToolbarLayout.title = " "//careful there should a space between double quote otherwise it wont work
                    isShow = false
                }
            }
        })
    }
}
