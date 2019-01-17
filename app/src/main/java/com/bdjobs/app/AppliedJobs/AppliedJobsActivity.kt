package com.bdjobs.app.AppliedJobs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bdjobs.app.InterviewInvitation.InterviewInvitationBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.transitFragment
import org.jetbrains.anko.startActivity

class AppliedJobsActivity : AppCompatActivity(), AppliedJobsCommunicator {

    private var jobid : String = ""


    override fun gotoEmployerInteractionFragment() {
        transitFragment(employerInteractionFragment, R.id.fragmentHolder, false)
    }

    override fun gotoInterviewInvitationDetails(from: String, jobID: String, companyName: String, jobTitle: String) {

        startActivity<InterviewInvitationBaseActivity>(
                "from" to from,
                "jobid" to jobID,
                "companyname" to companyName,
                "jobtitle" to jobTitle
        )

    }


    override fun scrollToUndoPosition(position: Int) {
        if (position  >0){
            appliedJobsFragment.scrollToUndoPosition(position)
        }
    }

    override fun decrementCounter() {
       appliedJobsFragment.decrementCounter()

         }

    private val appliedJobsFragment = AppliedJobsFragment()
    private val employerInteractionFragment = EmployerInteractionFragment()
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
