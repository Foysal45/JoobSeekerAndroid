package com.bdjobs.app.videoInterview.ui.rating

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentAfterSubmitFeedbackBinding
import com.bdjobs.app.videoInterview.VideoInterviewViewModel
import com.bdjobs.app.videoResume.VideoResumeActivity
import kotlinx.android.synthetic.main.fragment_after_submit_feedback.*
import kotlinx.android.synthetic.main.layout_create_video_resume_bottom_guide.*

class AfterSubmitFragment : Fragment() {

    lateinit var binding: FragmentAfterSubmitFeedbackBinding
    private val baseViewModel: VideoInterviewViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAfterSubmitFeedbackBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupWithNavController(submit_toolbar, navController, appBarConfiguration)

        submit_toolbar?.setNavigationOnClickListener {
            findNavController().popBackStack(R.id.videoInterviewListFragment,false)
        }

        submit_toolbar?.title = baseViewModel.jobTitle

        tv_learn_more_label.setOnClickListener {
            requireContext().startActivity(Intent(requireContext(),VideoResumeActivity::class.java))
        }
    }


    override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                // handle back button's click listener
                findNavController().popBackStack(R.id.videoInterviewListFragment,false)
                return@OnKeyListener true
            }
            false
        })
    }



}