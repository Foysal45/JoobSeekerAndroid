package com.bdjobs.app.liveInterview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.bdjobs.app.R

class LiveInterviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_interview)

        val activityDate = intent.getStringExtra("time")
        //Log.d("rakib", "activity date $activityDate")

        val navController = findNavController(R.id.liveInterviewNavHostFragment)
        val bundle = Bundle().also {
            it.putString("activityDate",activityDate)
        }
        navController.setGraph(R.navigation.live_interview_nav_graph,bundle)
    }
}