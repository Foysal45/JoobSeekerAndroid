package com.bdjobs.app.videoInterview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.bdjobs.app.R

class VideoInterviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_interview)

        val navController = findNavController(R.id.videoInterviewNavHostFragment)
        val bundle = Bundle().also {
            it.putString("activityDate","0")
        }
        navController.setGraph(R.navigation.video_interview_nav_graph,bundle)
    }
}