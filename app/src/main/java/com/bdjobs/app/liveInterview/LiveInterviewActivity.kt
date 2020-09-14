package com.bdjobs.app.liveInterview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.bdjobs.app.R
import timber.log.Timber

class LiveInterviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_interview)

        val activityDate = if (intent.getStringExtra("from") == "homePage") "0" else intent.getStringExtra("time")
        val from = intent.getStringExtra("from")
        //Log.d("rakib", "activity date $activityDate")
        Timber.d("activity $activityDate from $from")

        val navController = findNavController(R.id.liveInterviewNavHostFragment)
        val bundle = Bundle().also {
            it.putString("activityDate",activityDate)
        }
        navController.setGraph(R.navigation.live_interview_nav_graph,bundle)
    }
}