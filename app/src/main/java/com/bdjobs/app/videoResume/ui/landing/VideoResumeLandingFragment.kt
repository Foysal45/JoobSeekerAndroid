package com.bdjobs.app.videoResume.ui.landing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentSettingsBinding
import com.bdjobs.app.databinding.FragmentVideoResumeLandingBinding
import com.bdjobs.app.sms.ui.settings.SettingsViewModel
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import com.bdjobs.app.videoResume.data.repository.VideoResumeRepository
import kotlinx.android.synthetic.main.fragment_video_resume_landing.*

class VideoResumeLandingFragment : Fragment() {

    private val videoResumeLandingViewModel: VideoResumeLandingViewModel by viewModels { VideoResumeLandingViewModelFactory(VideoResumeRepository(requireContext())) }
    lateinit var binding: FragmentVideoResumeLandingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentVideoResumeLandingBinding.inflate(inflater).apply {
            viewModel = videoResumeLandingViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        tool_bar?.setupWithNavController(navController, appBarConfiguration)

        btn_view_questions?.setOnClickListener {
            findNavController().navigate(VideoResumeLandingFragmentDirections.actionVideoResumeLandingFragmentToQuestionListDialogFragment(videoResumeLandingViewModel.getAllQuestions().toTypedArray()))
        }

        btn_guidelines?.setOnClickListener {
            findNavController().navigate(VideoResumeLandingFragmentDirections.actionVideoResumeLandingFragmentToGuidelineFragment())
        }

        img_info?.setOnClickListener {
            openPublicWarningDialog()
        }

        videoResumeLandingViewModel.apply {

        }
    }

    private fun openPublicWarningDialog(){
        findNavController().navigate(VideoResumeLandingFragmentDirections.actionVideoResumeLandingFragmentToPublicVisibilityFragment())
    }

}