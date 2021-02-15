package com.bdjobs.app.videoResume

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController

import com.bdjobs.app.R

class VideoResumeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_resume)

        val navController = findNavController(R.id.videoResumeNavHostFragment)

        navController.setGraph(R.navigation.video_resume_nav_graph)

    }


}