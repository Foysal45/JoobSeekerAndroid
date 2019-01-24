package com.bdjobs.app.AppliedJobs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.transitFragment

class AppliedJobsActivity : AppCompatActivity(), AppliedJobsCommunicator {
    override fun scrollToUndoPosition(position: Int) {
        if (position  >0){
            appliedJobsFragment.scrollToUndoPosition(position)
        }
    }

    override fun decrementCounter() {
       appliedJobsFragment.decrementCounter()

         }

    private val appliedJobsFragment = AppliedJobsFragment()
    private var time: String = ""

    override fun getTime(): String {
        return time
    }



    override fun backButtonPressed() {
        onBackPressed()
    }

    override fun gotoAppliedJobsFragment() {
        transitFragment(appliedJobsFragment, R.id.fragmentHolder, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applied_jobs)
        try {
            time = intent.getStringExtra("time")
        } catch (e: Exception) {
            logException(e)
        }
        Log.d("time", "time: " + time)
        gotoAppliedJobsFragment()
    }
}
