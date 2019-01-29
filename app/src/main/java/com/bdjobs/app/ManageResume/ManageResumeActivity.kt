package com.bdjobs.app.ManageResume

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.transitFragment

class ManageResumeActivity : AppCompatActivity() {

    private val emailResumeFragment = EmailResumeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_resume)
        transitFragment(emailResumeFragment, R.id.fragmentHolder)
    }
}
