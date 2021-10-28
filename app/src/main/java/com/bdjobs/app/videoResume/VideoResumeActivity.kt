package com.bdjobs.app.videoResume

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment

import com.bdjobs.app.R
import kotlinx.android.synthetic.main.activity_live_interview.*
import kotlinx.android.synthetic.main.activity_video_resume.*
import timber.log.Timber

class VideoResumeActivity : AppCompatActivity() {

    private var from: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_resume)


        from = intent.getStringExtra("from")

        Timber.d("From: $from")

//        val navController = findNavController(R.id.videoResumeNavHostFragment)

        val navHostFragment = videoResumeNavHostFragment as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.video_resume_nav_graph)

        if (from == "ViewEditResume") graph.startDestination = R.id.videoResumeQuestionsFragment
        else if (from == "JobDetails") graph.startDestination = R.id.guidelineFragment
        else graph.startDestination = R.id.videoResumeLandingFragment

        navHostFragment.navController.graph = graph

    }


}