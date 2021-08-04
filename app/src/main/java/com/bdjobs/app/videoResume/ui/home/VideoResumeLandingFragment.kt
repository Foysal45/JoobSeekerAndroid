package com.bdjobs.app.videoResume.ui.home

import android.app.AlertDialog
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
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.openUrlInBrowser
import com.bdjobs.app.databinding.FragmentVideoResumeLandingBinding
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import kotlinx.android.synthetic.main.fragment_video_resume_landing.*
import kotlinx.android.synthetic.main.fragment_video_resume_landing.tool_bar
import timber.log.Timber

class VideoResumeLandingFragment : Fragment() {

    private val videoResumeLandingViewModel: VideoResumeLandingViewModel by viewModels {
        ViewModelFactoryUtil.provideVideoResumeLandingViewModelFactory(this)
    }
    lateinit var binding: FragmentVideoResumeLandingBinding
    private lateinit var session: BdjobsUserSession

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVideoResumeLandingBinding.inflate(inflater).apply {
            viewModel = videoResumeLandingViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = BdjobsUserSession(requireContext())

        val navController = findNavController()
        val appBarConfiguration =
            AppBarConfiguration.Builder().setFallbackOnNavigateUpListener { onNavigateUp() }.build()
        tool_bar?.setupWithNavController(navController, appBarConfiguration)

        btn_view_questions?.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.videoResumeLandingFragment) {
                findNavController().navigate(
                    VideoResumeLandingFragmentDirections.actionVideoResumeLandingFragmentToQuestionListDialogFragment(
                        videoResumeLandingViewModel.getAllQuestions().toTypedArray()
                    )
                )
            }

        }

        btn_guidelines?.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.videoResumeLandingFragment) {
                findNavController().navigate(VideoResumeLandingFragmentDirections.actionVideoResumeLandingFragmentToGuidelineFragment())
            }

        }

        img_info?.setOnClickListener {
            openPublicWarningDialog()
        }


        videoResumeLandingViewModel.apply {
            Timber.d("Loaded")
            getStatistics()

            openTurnOffVisibilityDialogEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    openTurnOffVisibilityDialog()
                }
            })

            openTurnOnVisibilityDialogEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    openTurnOnVisibilityDialog()
                }
            })

            openMessageDialogEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    openMessageDialog()
                }
            })

            threshold.value?.let { session.insertVideoResumeThresholdValue(it) }
            totalAnswered.value?.let { session.insertVideoResumeTotalAnswered(it) }

            tv_intro_yt_link.setOnClickListener {
                activity?.openUrlInBrowser("https://www.youtube.com/watch?v=iuFwzMk4-PI&list=PLR1m9fmwtfMUMuiWv9m60Z5WhdaI_S76p&index=2")
            }

            btn_video_tutorial.setOnClickListener {
                activity?.openUrlInBrowser("https://www.youtube.com/watch?v=iuFwzMk4-PI&list=PLR1m9fmwtfMUMuiWv9m60Z5WhdaI_S76p&index=2")
            }

            btn_create_video?.setOnClickListener {
                if (findNavController().currentDestination?.id == R.id.videoResumeLandingFragment) {
                    findNavController().navigate(VideoResumeLandingFragmentDirections.actionVideoResumeLandingFragmentToVideoResumeQuestionsFragment())
                }

            }

            btn_edit_video?.setOnClickListener {
                if (findNavController().currentDestination?.id == R.id.videoResumeLandingFragment) {
                    findNavController().navigate(VideoResumeLandingFragmentDirections.actionVideoResumeLandingFragmentToVideoResumeQuestionsFragment())
                }
            }

//            showVideoResumeToEmployers.observe(viewLifecycleOwner, {
//                session.isVideoResumeShowToEmployers = it
//                if (isAlertOn.value !== "0") session.insertVideoResumeVisibility(false) else session.insertVideoResumeVisibility(
//                    true
//                )
//            })
        }
    }

    private fun openPublicWarningDialog() {
        if (findNavController().currentDestination?.id == R.id.videoResumeLandingFragment) {
            findNavController().navigate(VideoResumeLandingFragmentDirections.actionVideoResumeLandingFragmentToPublicVisibilityFragment())
        }

    }

    private fun openTurnOffVisibilityDialog() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Confirmation")
        builder.setMessage("If you don't show Video Resume to employers, they can no longer view your video. Do you not want to showing employers?")
        builder.setPositiveButton("YES, CONTINUE") { _, _ ->
            Timber.d("yes please hide")
            session.insertVideoResumeVisibility(false)
            this.videoResumeLandingViewModel.onHideResumeVisibility()
        }
        builder.setNegativeButton("CANCEL") { _, _ ->
            Timber.d("no,keep visible")
            this.videoResumeLandingViewModel.notChangeResumeVisibility()
        }
        builder.show()

    }

    private fun openTurnOnVisibilityDialog() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Confirmation")
        builder.setMessage("This action will make your Video Resume  visible to employer(s). Do you want to continue?")
        builder.setPositiveButton("YES, CONTINUE") { _, _ ->
            Timber.d("yes please show")
            session.insertVideoResumeVisibility(true)
            this.videoResumeLandingViewModel.onShowResumeVisibility()
        }
        builder.setNegativeButton("CANCEL") { _, _ ->
            Timber.d("no,keep invisible")
            this.videoResumeLandingViewModel.notChangeResumeVisibility()
        }
        builder.show()

    }

    private fun openMessageDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Message")
        builder.setMessage("Please record at least ${videoResumeLandingViewModel.threshold.value} answers for showing video resume to employers.")
        builder.setPositiveButton("OK") { _, _ ->
            Timber.d("yes")
        }
        builder.show()

    }


    private fun onNavigateUp(): Boolean {
        activity?.onBackPressed()
        return true
    }
}