package com.bdjobs.app.editResume

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.d
import com.bdjobs.app.Utilities.loadCircularImageFromUrl
import com.bdjobs.app.editResume.educationInfo.AcademicBaseActivity
import com.bdjobs.app.editResume.employmentHistory.EmploymentHistoryActivity
import com.bdjobs.app.editResume.personalInfo.PersonalInfoActivity
import kotlinx.android.synthetic.main.activity_edit_res_landing.*
import org.jetbrains.anko.startActivity

class EditResLandingActivity : Activity() {

    private lateinit var session: BdjobsUserSession
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = BdjobsUserSession(this@EditResLandingActivity)
        setContentView(R.layout.activity_edit_res_landing)
    }

    override fun onResume() {
        super.onResume()
        if (!session.userPicUrl.isNullOrEmpty()) {
            ivProfileImage.loadCircularImageFromUrl(session.userPicUrl)
        } else {
            ivProfileImage.setImageDrawable(ContextCompat.getDrawable(this@EditResLandingActivity, R.drawable.ic_account_circle_black_24px))
        }
        d("editResLanding photo:" + session.userPicUrl)
        d("editResLanding name:" + session.fullName)
        d("editResLanding email:" + session.email)
        tvname.text = session.fullName
        tvEmail.text = session.email
        doWork()
    }

    private fun doWork() {
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

        icBackEr.setOnClickListener {
            finish()
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

}
