package com.bdjobs.app.videoInterview.ui.interview_details

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.InterviewInvitation.InterviewInvitationCommunicator
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.databinding.FragmentVideoInterviewDetailsBinding
import com.bdjobs.app.videoInterview.VideoInterviewViewModel
import com.bdjobs.app.videoInterview.ui.interview_list.VideoInterviewListViewModel
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import kotlinx.android.synthetic.main.fragment_video_interview_details.*
import org.jetbrains.anko.support.v4.startActivity
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class VideoInterviewDetailsFragment : androidx.fragment.app.Fragment() {

    private val args: VideoInterviewDetailsFragmentArgs by navArgs()
    private val videoInterviewDetailsViewModel: VideoInterviewDetailsViewModel by activityViewModels {
        ViewModelFactoryUtil.provideVideoInterviewInvitationDetailsViewModelFactory(
            this,
            args.jobId
        )
    }
    private val videoInterListViewModel: VideoInterviewListViewModel by navGraphViewModels(R.id.videoInterviewListFragment)
    private val baseViewModel: VideoInterviewViewModel by activityViewModels()

    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB

    lateinit var binding: FragmentVideoInterviewDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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

            getVideoInterviewDetails()

            displayQuestionListEvent.observe(viewLifecycleOwner, EventObserver {
                baseViewModel.jobId = videoInterviewDetailsViewModel.jobID!!
                baseViewModel.applyId = videoInterviewDetailsViewModel.applyId.value!!
                baseViewModel.jobTitle = args.jobTitle ?:""
                //findNavController().navigate(VideoInterviewDetailsFragmentDirections.actionVideoInterviewDetailsFragmentToQuestionListFragment("854375", "182982535"))
                if (findNavController().currentDestination?.id == R.id.videoInterviewDetailsFragment)
                    findNavController().navigate(VideoInterviewDetailsFragmentDirections.actionVideoInterviewDetailsFragmentToQuestionListFragment())
            })

            displayGuidelineEvent.observe(viewLifecycleOwner, EventObserver {
                baseViewModel.jobId = videoInterviewDetailsViewModel.jobID!!
                baseViewModel.applyId = videoInterviewDetailsViewModel.applyId.value!!
                videoInterListViewModel.commonData.value?.totalVideoInterview?.toInt()?.let {
                    Timber.d("Common Data: ${videoInterListViewModel.commonData.value}")
                    if (it < 4) {
                        Timber.d("Less than 4")
                        if (videoInterviewDetailsViewModel.detailsData.value?.vStatuCode == "3") {
                            if (findNavController().currentDestination?.id == R.id.videoInterviewDetailsFragment)
                                findNavController().navigate(VideoInterviewDetailsFragmentDirections.actionVideoInterviewDetailsFragmentToQuestionListFragment())
                        } else {
                            if (findNavController().currentDestination?.id == R.id.videoInterviewDetailsFragment)
                                findNavController().navigate(VideoInterviewDetailsFragmentDirections.actionVideoInterviewDetailsFragmentToGuidelineLandingFragment())
                        }

                    } else {
                        if (findNavController().currentDestination?.id == R.id.videoInterviewDetailsFragment)
                            findNavController().navigate(VideoInterviewDetailsFragmentDirections.actionVideoInterviewDetailsFragmentToQuestionListFragment())
                    }
//                    findNavController().navigate(VideoInterviewDetailsFragmentDirections.actionVideoInterviewDetailsFragmentToGuidelineLandingFragment())

                }
            })
        }

        btn_job_detail?.setOnClickListener {
            val jobids = ArrayList<String>()
            val lns = ArrayList<String>()
            val deadline = ArrayList<String>()
            jobids.add(args.jobId!!)
            lns.add("0")
            deadline.add("")
            startActivity<JobBaseActivity>(
                "from" to "employer",
                "jobids" to jobids,
                "lns" to lns,
                "position" to 0,
                "deadline" to deadline
            )
        }
    }
}
