package com.bdjobs.app.videoResume.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Web.WebActivity
import com.bdjobs.app.databinding.FragmentVideoResumeLandingBinding
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import kotlinx.android.synthetic.main.fragment_hot_jobs_fragment_new.*
import kotlinx.android.synthetic.main.fragment_video_resume_landing.*
import kotlinx.android.synthetic.main.fragment_video_resume_landing.tool_bar
import org.jetbrains.anko.startActivity
import timber.log.Timber

class VideoResumeLandingFragment : Fragment() {

    private val videoResumeLandingViewModel: VideoResumeLandingViewModel by viewModels {
        ViewModelFactoryUtil.provideVideoResumeLandingViewModelFactory(this)
    }
    lateinit var binding: FragmentVideoResumeLandingBinding
    private lateinit var session: BdjobsUserSession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
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
            Timber.d("Loaded")
            getStatistics()

            openTurnOffVisibilityDialogEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    openTurnOffVisibilityDialog()
                }
            })

            openTurnOnVisibilityDialogEvent.observe(viewLifecycleOwner,EventObserver {
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
                context?.startActivity<WebActivity>(
                    "url" to "https://youtube.com/playlist?list=PLR1m9fmwtfMUMuiWv9m60Z5WhdaI_S76p",
                    "from" to "videoResume"
                )
            }

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
        builder.setMessage("If you don't show Video Resume to employers, they can no longer view your video. Do you not want to showing employers?")
        builder.setPositiveButton("YES, CONTINUE") { _, _ ->
            Timber.d("yes please hide")
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