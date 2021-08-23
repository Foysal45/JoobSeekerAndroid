package com.bdjobs.app.editResume

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.ManageResume.utils.formatDateVP
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Web.WebActivity
import com.bdjobs.app.editResume.educationInfo.AcademicBaseActivity
import com.bdjobs.app.editResume.employmentHistory.EmploymentHistoryActivity
import com.bdjobs.app.editResume.otherInfo.OtherInfoBaseActivity
import com.bdjobs.app.editResume.personalInfo.PersonalInfoActivity
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_edit_res_landing.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import timber.log.Timber
import java.util.*

class EditResLandingActivity : Activity() {

    private lateinit var session: BdjobsUserSession
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_res_landing)
    }

    override fun onResume() {
        super.onResume()
        //Log.d("rakib", "called onResume")
        session = BdjobsUserSession(this@EditResLandingActivity)
        try {
            if (!session.userPicUrl.isNullOrEmpty()) {
                //Log.d("rakib", "${session.userPicUrl}")
                ivProfileImage?.loadCircularImageFromUrl(session.userPicUrl)
            } else {
                //Log.d("rakib", "called onresume else")
                ivProfileImage?.setImageResource(R.drawable.ic_user_thumb_small)
            }
        } catch (e: Exception) {
        }
        d("editResLanding photo:" + session.userPicUrl)
        d("editResLanding name:" + session.fullName)
        tvname.text = session.fullName
        tvEmail.text = session.email

        try {
            if (!session.isCvPosted?.equalIgnoreCase("true")!!) {
                disableAll()
                btnPerItem1?.setOnClickListener {
                    goToFragment("personal", "P")
                }

            } else {
                doWork()
                fetchPersonalizedResumeStat()
            }
        } catch (e: Exception) {
        }

        icBackEr?.setOnClickListener {
            finish()
        }

        viewResumeBTN?.setOnClickListener {
            if (!session.isCvPosted?.equalIgnoreCase("true")!!) {
                try {
                    val alertd = alert("To access this feature please post your Bdjobs Resume.") {
                        title = "Your Bdjobs Resume is not posted yet!"
                        negativeButton("OK") { dd ->
                            dd.dismiss()
                        }
                    }
                    alertd.isCancelable = false
                    alertd.show()
                } catch (e: Exception) {
                    logException(e)
                }
            } else {
                try {
                    val str1 = random()
                    val str2 = random()
                    val id = str1 + session.userId + session.decodId + str2
                    startActivity<WebActivity>(
                        "url" to "https://mybdjobs.bdjobs.com/mybdjobs/masterview_for_apps.asp?id=$id",
                        "from" to "cvview"
                    )
                } catch (e: Exception) {
                    logException(e)
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun fetchPersonalizedResumeStat() {
//        this.showProgressBar(loadingProgressBar)
//        cl_stat_personalized_resume.hide()
//        tv_label_stat_personalized_resume.hide()
//        tv_last_update.hide()

        GlobalScope.launch {
            try {
                val response = ApiServiceMyBdjobs.create().personalizedResumeStat(
                    session.userId,
                    session.decodId,
                    session.isCvPosted
                )



                if (response.statuscode == "0" && response.message == "Success") {
                    val data = response.data!![0]

                    val lastUpdated = if (data.personalizedLastUpdateDate!="") formatDateVP(data.personalizedLastUpdateDate!!) else ""

                    runOnUiThread {

//                        this@EditResLandingActivity.stopProgressBar(loadingProgressBar)
//                        if (!lastUpdated.isNullOrEmpty()) {
                        tv_label_stat_personalized_resume.show()
                        cl_stat_personalized_resume.show()
                        tv_last_update.show()


                        tv_personalized_resume_view_count.text = data.personalizedViewed
                        tv_personalized_resume_download_count.text = data.personalizedDownload
                        tv_personalized_resume_emailed_count.text = data.personalizedEmailed
                        if (lastUpdated!="") {
                            tv_last_update.show()
                            tv_last_update.text = "Last updated on: $lastUpdated"
                        }
                        else tv_last_update.hide()

//                        } else {
//                            tv_label_stat_personalized_resume.hide()
//                            cl_stat_personalized_resume.hide()
//
//                        }


                    }

                } else runOnUiThread { toast("Sorry, personalized resume stat fetching failed!") }

            } catch (e: Exception) {
                Timber.e("Exception while fetching personalized resume stat: ${e.localizedMessage}")
                runOnUiThread {
                    this@EditResLandingActivity.stopProgressBar(loadingProgressBar)
                    toast("Sorry, personalized resume stat fetching failed: ${e.localizedMessage}")
//                    cl_stat_personalized_resume.show()
//                    tv_label_stat_personalized_resume.show()
                }

            }


        }
    }

    private fun random(): String {
        val chars = "abcdefghijklmnopqrstuvwxyz12345678910".toCharArray()
        val sb = StringBuilder()
        val random = Random()
        for (i in 0..4) {
            val c = chars[random.nextInt(chars.size)]
            sb.append(c)
        }
        val output = sb.toString()
        println(output)
        return output
    }

    private fun doWork() {
        enableAll()

        btnPerItem1?.setOnClickListener {
            btnPerItem1?.isClickable = false
            goToFragment("personal", "P")
        }
        btnPerItem2?.setOnClickListener {
            btnPerItem2?.isClickable = false
            goToFragment("contact", "P")
        }
        btnPerItem3?.setOnClickListener {
            btnPerItem3?.isClickable = false
            goToFragment("career", "P")
        }
        btnPerItem4?.setOnClickListener {
            btnPerItem4?.isClickable = false
            goToFragment("ori", "P")
        }
        btnPerItem5?.setOnClickListener {
            btnPerItem5?.isClickable = false
            goToFragment("prefAreas", "P")
        }
        btnEduItem1?.setOnClickListener {
            btnEduItem1?.isClickable = false
            goToFragment("academic", "E")
        }
        btnEduItem2?.setOnClickListener {
            btnEduItem2?.isClickable = false
            goToFragment("training", "E")
        }
        btnEmpItem1?.setOnClickListener {
            btnEmpItem1?.isClickable = false
            goToFragment("employ", "Emp")
        }
        btnEmpItem2?.setOnClickListener {
            btnEmpItem2?.isClickable = false
            goToFragment("army", "Emp")
        }
        btnUploadPhoto?.setOnClickListener {
            btnUploadPhoto?.isClickable = false
            startActivity(Intent(this@EditResLandingActivity, PhotoUploadActivity::class.java))
        }
        btnSpecilaization?.setOnClickListener {
            btnSpecilaization?.isClickable = false
            goToFragment("specialization", "Other")
        }
        btnLanguage?.setOnClickListener {
            btnLanguage?.isClickable = false
            goToFragment("language", "Other")
        }
        btnReferences?.setOnClickListener {
            btnReferences?.isClickable = false
            goToFragment("reference", "Other")
        }
        btnProfessional?.setOnClickListener {
            btnProfessional?.isClickable = false
            goToFragment("professional", "E")
        }

        ivProfileImage?.onClick {
            ivProfileImage?.isClickable = false
            startActivity<PhotoUploadActivity>()

        }

    }

    private fun disableAll() {
        btnPerItem2.disable(true)
        btnPerItem3.disable(true)
        btnPerItem4.disable(true)
        btnPerItem5.disable(true)
        btnEduItem1.disable(true)
        btnEduItem2.disable(true)
        btnEmpItem1.disable(true)
        btnEmpItem2.disable(true)
        btnLanguage.disable(true)
        btnProfessional.disable(true)
        btnReferences.disable(true)
        btnSpecilaization.disable(true)
        btnUploadPhoto.disable(true)
    }

    private fun enableAll() {
        btnPerItem2.disable(false)
        btnPerItem3.disable(false)
        btnPerItem4.disable(false)
        btnPerItem5.disable(false)
        btnEduItem1.disable(false)
        btnEduItem2.disable(false)
        btnEmpItem1.disable(false)
        btnEmpItem2.disable(false)
        btnUploadPhoto.disable(false)
        btnLanguage.disable(false)
        btnProfessional.disable(false)
        btnReferences.disable(false)
        btnSpecilaization.disable(false)
    }

    private fun goToFragment(s: String, check: String) {
        when (check) {
            "P" ->
                startActivity<PersonalInfoActivity>("name" to s, "personal_info_edit" to "null")
            "E" ->
                startActivity<AcademicBaseActivity>("name" to s, "education_info_add" to "null")
            "Emp" ->
                startActivity<EmploymentHistoryActivity>("name" to s, "emp_his_add" to "null")
            "Other" ->
                startActivity<OtherInfoBaseActivity>("name" to s, "other_info_add" to "null")

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

    override fun onRestart() {
        super.onRestart()
        //Log.d("rakib", "called onRestart")
    }

}
