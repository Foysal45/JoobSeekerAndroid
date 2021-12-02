package com.bdjobs.app.videoInterview.ui.rating

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentAfterSubmitFeedbackBinding
import com.bdjobs.app.videoInterview.ui.interview_details.VideoInterviewDetailsViewModel
import kotlinx.android.synthetic.main.fragment_after_submit_feedback.*
import kotlinx.android.synthetic.main.layout_create_video_resume_bottom_guide.*

class AfterSubmitFragment : Fragment() {

    lateinit var binding: FragmentAfterSubmitFeedbackBinding
    private val questionDetailsViewModel: VideoInterviewDetailsViewModel by navGraphViewModels(R.id.videoInterviewDetailsFragment)

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

        submit_toolbar?.title = questionDetailsViewModel.jobTitle.value

        tv_learn_more_label.setOnClickListener {
            findNavController().navigate(R.id.videoResumeLandingFragment2)
        }
    }


    override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                // handle back button's click listener
                findNavController().popBackStack(R.id.videoInterviewListFragment,false)
                return@OnKeyListener true
            }
            false
        })
    }



}