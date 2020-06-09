package com.bdjobs.app.videoInterview.ui.interview_details

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.InterviewInvitation.InterviewInvitationCommunicator
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.databinding.FragmentVideoInterviewDetailsBinding
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import kotlinx.android.synthetic.main.fragment_video_interview_details.*

/**
 * A simple [Fragment] subclass.
 */
class VideoInterviewDetailsFragment : androidx.fragment.app.Fragment() {

    private val args: VideoInterviewDetailsFragmentArgs by navArgs()
    private val videoInterviewDetailsViewModel: VideoInterviewDetailsViewModel by navGraphViewModels(R.id.videoInterviewDetailsFragment) {
        ViewModelFactoryUtil.provideVideoInterviewInvitationDetailsViewModelFactory(this, args.jobId)
    }

    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB
    lateinit var interviewInvitationCommunicator: InterviewInvitationCommunicator

    lateinit var binding: FragmentVideoInterviewDetailsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentVideoInterviewDetailsBinding.inflate(inflater).apply {
            viewModel = videoInterviewDetailsViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        tool_bar?.setupWithNavController(navController, appBarConfiguration)

        tool_bar?.title = args.jobTitle

        //getVideoInterviewDetails()

        videoInterviewDetailsViewModel.apply {
            displayQuestionListEvent.observe(viewLifecycleOwner, EventObserver {
                //findNavController().navigate(VideoInterviewDetailsFragmentDirections.actionVideoInterviewDetailsFragmentToQuestionListFragment("854375", "182982535"))
                findNavController().navigate(R.id.questionListFragment)
            })

            displayGuidelineEvent.observe(viewLifecycleOwner,EventObserver{
                findNavController().navigate(VideoInterviewDetailsFragmentDirections.actionVideoInterviewDetailsFragmentToGuidelineLandingFragment())
            })
        }
    }
}
