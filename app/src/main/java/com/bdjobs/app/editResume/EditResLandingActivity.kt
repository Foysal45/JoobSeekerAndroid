package com.bdjobs.app.editResume

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.d
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.loadCircularImageFromUrl
import com.bdjobs.app.editResume.educationInfo.AcademicBaseActivity
import com.bdjobs.app.editResume.employmentHistory.EmploymentHistoryActivity
import com.bdjobs.app.editResume.personalInfo.PersonalInfoActivity
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_edit_res_landing.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class EditResLandingActivity : Activity() {

    private var isResumeUpdate = ""
    private lateinit var session: BdjobsUserSession
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_res_landing)
    }

    override fun onResume() {
        super.onResume()
        session = BdjobsUserSession(this@EditResLandingActivity)
        if (!session.userPicUrl.isNullOrEmpty()) {
            ivProfileImage.loadCircularImageFromUrl(session.userPicUrl)
        } else {
            ivProfileImage.setImageDrawable(ContextCompat.getDrawable(this@EditResLandingActivity, R.drawable.ic_account_circle_black_24px))
        }
        isResumeUpdate = session.IsResumeUpdate.toString()
        d("editResLanding photo:" + session.userPicUrl)
        d("editResLanding name:" + session.fullName)
        d("editResLanding isResumeUpdate:$isResumeUpdate")
        tvname.text = session.fullName
        tvEmail.text = session.email
        if (isResumeUpdate.equalIgnoreCase("false")) {
            disableAll()
            btnPerItem1.setOnClickListener {
                goToFragment("personal", "P")
            }

        } else {
            doWork()
        }
        icBackEr.setOnClickListener {
            finish()
        }
    }

    private fun doWork() {
        enableAll()
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
        btnPerItem1.setOnClickListener {
            goToFragment("personal", "P")
        }

    }

    private fun disableAll() {
        btnPerItem2.disable(true)
        btnPerItem3.disable(true)
        btnEduItem1.disable(true)
        btnEduItem2.disable(true)
        btnEmpItem1.disable(true)
        btnUploadPhoto.disable(true)
    }

    private fun enableAll() {
        btnPerItem2.disable(false)
        btnPerItem3.disable(false)
        btnEduItem1.disable(false)
        btnEduItem2.disable(false)
        btnEmpItem1.disable(false)
        btnUploadPhoto.disable(false)
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


    private fun MaterialButton.disable(b: Boolean) {
        if (b) {
            this.setTextIsSelectable(false)
            this.setRippleColorResource(R.color.ripple_transparent)
            this.setTextColor(Color.parseColor("#9C9C9C"))
            this.setOnClickListener { toast(getString(R.string.error_fill_up_personal)) }
        } else {
            this.setTextIsSelectable(false)
            this.setRippleColorResource(R.color.colorAccent)
            this.setTextColor(Color.parseColor("#212121"))
        }
    }

}
