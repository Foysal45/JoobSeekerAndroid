package com.bdjobs.app.Jobs

import android.app.Activity
import android.os.Bundle
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.transitFragment

class JobBaseActivity : Activity() {
    private val joblistFragment = JoblistFragment()
    private val jobDetailsFragment = JobDetailsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_landing)
        transitFragment(joblistFragment, R.id.jobFragmentHolder)
    }
}
