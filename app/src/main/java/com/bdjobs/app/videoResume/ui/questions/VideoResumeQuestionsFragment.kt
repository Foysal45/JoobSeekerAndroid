package com.bdjobs.app.videoResume.ui.questions

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController

import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil

import kotlinx.android.synthetic.main.fragment_video_resume_landing.*

//fragment_create_video_resume
class VideoResumeQuestionsFragment : Fragment() {

    private val videoResumeQuestionsViewModel: VideoResumeQuestionsViewModel by viewModels {
        ViewModelFactoryUtil.provideVideoResumeQuestionsViewModelFactory(this)
    }
    lateinit var binding: FragmentVideoResumeQuestionsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentVideoResumeQuestionsBinding.inflate(inflater).apply {
            viewModel = videoResumeQuestionsViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        tool_bar?.setupWithNavController(navController, appBarConfiguration)


        videoResumeQuestionsViewModel.apply {
            Log.d("Salvin", "Loaded")
        }
    }
}