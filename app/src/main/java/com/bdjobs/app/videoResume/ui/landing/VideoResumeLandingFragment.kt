package com.bdjobs.app.videoResume.ui.landing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_video_resume_landing.*

class VideoResumeLandingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_resume_landing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        tool_bar?.setupWithNavController(navController, appBarConfiguration)

        btn_view_questions?.setOnClickListener {
            findNavController().navigate(VideoResumeLandingFragmentDirections.actionVideoResumeLandingFragmentToPublicVisibilityFragment())
        }

        btn_guidelines?.setOnClickListener {
            findNavController().navigate(VideoResumeLandingFragmentDirections.actionVideoResumeLandingFragmentToGuidelineFragment())
        }
    }

}