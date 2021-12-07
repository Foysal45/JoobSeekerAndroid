package com.bdjobs.app.videoResume

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bdjobs.app.ads.Ads
import com.bdjobs.app.ManageResume.ManageResumeActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.utilities.equalIgnoreCase
import com.bdjobs.app.utilities.logException
import com.bdjobs.app.Web.WebActivity
import com.bdjobs.app.editResume.EditResLandingActivity
import com.google.android.ads.nativetemplates.TemplateView
import kotlinx.android.synthetic.main.fragment_applied_jobs.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import java.util.*

class ResumeManagerActivity : AppCompatActivity() {

    private lateinit var session: BdjobsUserSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resume_manager)

        session = BdjobsUserSession(applicationContext)

        val editResume = findViewById<Button>(R.id.editResume)
        val viewResume = findViewById<Button>(R.id.viewResume)
        val uploadResume = findViewById<Button>(R.id.uploadResume)
        val videoResume = findViewById<Button>(R.id.videoResume)
        val ad_small_template = findViewById<TemplateView>(R.id.ad_small_template)


        Ads.showNativeAd(ad_small_template, this@ResumeManagerActivity)

        editResume?.setOnClickListener {
            startActivity<EditResLandingActivity>()
        }
        viewResume?.setOnClickListener {
            if (!session.isCvPosted?.equalIgnoreCase("true")!!) {
                try {
                    val alertd = alert("To Access this feature please post your resume") {
                        title = "Your resume is not posted!"
                        positiveButton("Post Resume") { startActivity<EditResLandingActivity>() }
                        negativeButton("Cancel") { dd ->
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
                    startActivity<WebActivity>("url" to "https://mybdjobs.bdjobs.com/mybdjobs/masterview_for_apps.asp?id=$id", "from" to "cvview")
                } catch (e: Exception) {
                    logException(e)
                }
            }
        }
        uploadResume?.setOnClickListener {
            startActivity<ManageResumeActivity>(
                    "from" to "uploadResume"
            )
        }

        videoResume?.setOnClickListener {
            if (!session.isCvPosted?.equalIgnoreCase("true")!!) {
                try {
                    val alertd = alert("To Access this feature please post your resume") {
                        title = "Your resume is not posted!"
                        positiveButton("Post Resume") { startActivity<EditResLandingActivity>() }
                        negativeButton("Cancel") { dd ->
                            dd.dismiss()
                        }
                    }
                    alertd.isCancelable = false
                    alertd.show()
                } catch (e: Exception) {
                    logException(e)
                }
            } else {
                startActivity<VideoResumeActivity>()
            }
        }

        backIMV?.setOnClickListener {
           onBackPressed()
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



}