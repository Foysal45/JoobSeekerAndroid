package com.bdjobs.app.editResume

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.loadCircularImageFromUrl
import com.bdjobs.app.editResume.educationInfo.AcademicBaseActivity
import com.bdjobs.app.editResume.employmentHistory.EmploymentHistoryActivity
import com.bdjobs.app.editResume.personalInfo.PersonalInfoActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_edit_res_landing.*
import org.jetbrains.anko.startActivity

class EditResLandingActivity : Activity() {

    private lateinit var session: BdjobsUserSession
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = BdjobsUserSession(this@EditResLandingActivity)
        setContentView(R.layout.activity_edit_res_landing)
        doWork()
    }

    private fun doWork() {
        ivProfileImage.loadCircularImageFromUrl(session.userPicUrl)
        tvname.text = session.fullName
        tvEmail.text = session.email

        btnPerItem1.setOnClickListener {
            goToFragment("personal", "P")
        }
        btnPerItem2.setOnClickListener {
            goToFragment("contact", "P")
        }
        btnPerItem3.setOnClickListener {
            goToFragment("career", "P")
        }
        btnEduItem1.setOnClickListener {
            goToFragment("academic", "E")
        }
        btnEduItem2.setOnClickListener {
            goToFragment("training", "E")
        }
        btnEmpItem1.setOnClickListener {
            goToFragment("employ", "Emp")
        }
        btnUploadPhoto.setOnClickListener {
            startActivity(Intent(this@EditResLandingActivity, PhotoUploadActivity::class.java))
        }

    }

    private fun goToFragment(s: String, check: String) {
        when (check) {
            "P" ->
                startActivity<PersonalInfoActivity>("name" to s)
            "E" ->
                startActivity<AcademicBaseActivity>("name" to s)
            "Emp" ->
                startActivity<EmploymentHistoryActivity>("name" to s)
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
