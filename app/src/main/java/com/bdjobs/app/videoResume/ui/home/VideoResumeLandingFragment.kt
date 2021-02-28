package com.bdjobs.app.videoResume.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.Employers.EmployersBaseActivity
import com.bdjobs.app.FavouriteSearch.FavouriteSearchBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentVideoResumeLandingBinding
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_video_interview_list.*
import kotlinx.android.synthetic.main.fragment_video_resume_landing.*
import kotlinx.android.synthetic.main.fragment_video_resume_landing.tool_bar
import org.jetbrains.anko.startActivity

class VideoResumeLandingFragment : Fragment() {

    private val videoResumeLandingViewModel: VideoResumeLandingViewModel by viewModels {
        ViewModelFactoryUtil.provideVideoResumeLandingViewModelFactory(this)
    }
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
        val appBarConfiguration = AppBarConfiguration.Builder().setFallbackOnNavigateUpListener { onNavigateUp() }.build()
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
            Log.d("Salvin", "Loaded")
            getStatistics()

            openTurnOffVisibilityDialogEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    openTurnOffVisibilityDialog()
                }
            })

            btn_create_video?.setOnClickListener {
                findNavController().navigate(VideoResumeLandingFragmentDirections.actionVideoResumeLandingFragmentToVideoResumeQuestionsFragment())
            }

            btn_edit_video?.setOnClickListener {
                findNavController().navigate(VideoResumeLandingFragmentDirections.actionVideoResumeLandingFragmentToVideoResumeQuestionsFragment())
            }
        }
    }

    private fun openPublicWarningDialog() {
        findNavController().navigate(VideoResumeLandingFragmentDirections.actionVideoResumeLandingFragmentToPublicVisibilityFragment())
    }

    private fun openTurnOffVisibilityDialog() {
        val builder = AlertDialog.Builder(requireContext())


        builder.setTitle("Confirmation")
        builder.setMessage("If you hide, employers can no longer view your video resume. Do you really want to hide resume from profile?")
        builder.setPositiveButton("YES, CONTINUE") { dialog, which ->
            Log.d("Salvin", "yes please hide")
            this.videoResumeLandingViewModel.onHideResumeVisibility()
        }
        builder.setNegativeButton("CANCEL") { dialog, which ->
            Log.d("Salvin", "no,keep visible")
            this.videoResumeLandingViewModel.notChangeResumeVisibility()
        }
        builder.show()

    }

    private fun onNavigateUp(): Boolean {
        activity?.onBackPressed()
        return true
    }
}