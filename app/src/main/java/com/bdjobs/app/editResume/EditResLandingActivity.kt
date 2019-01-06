package com.bdjobs.app.editResume

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bdjobs.app.R
import com.bdjobs.app.editResume.educationInfo.AcademicBaseActivity
import com.bdjobs.app.editResume.employmentHistory.EmploymentHistoryActivity
import com.bdjobs.app.editResume.otherInfo.OtherInfoActivity
import com.bdjobs.app.editResume.personalInfo.PersonalInfoActivity
import kotlinx.android.synthetic.main.activity_edit_res_landing.*

class EditResLandingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_res_landing)


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
}
