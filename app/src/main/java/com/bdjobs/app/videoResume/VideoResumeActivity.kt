package com.bdjobs.app.videoResume

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController

import com.bdjobs.app.R
import timber.log.Timber

class VideoResumeActivity : AppCompatActivity() {

    private var from:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_resume)

        val navController = findNavController(R.id.videoResumeNavHostFragment)

//        from = intent.getStringExtra("from")

        Timber.d("From: $from")

//        if (from!=null) {
//
//            if (from=="ViewEditResume") navController.graph.startDestination = R.id.videoResumeQuestionsFragment
//            else navController.graph.startDestination = R.id.videoResumeLandingFragment
//        }

        navController.setGraph(R.navigation.video_resume_nav_graph)

    }


}