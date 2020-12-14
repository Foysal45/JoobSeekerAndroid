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

/**
 * A simple [Fragment] subclass.
 */
class VideoInterviewDetailsFragment : androidx.fragment.app.Fragment() {

    private val args: VideoInterviewDetailsFragmentArgs by navArgs()
    private val videoInterviewDetailsViewModel: VideoInterviewDetailsViewModel by navGraphViewModels(R.id.videoInterviewDetailsFragment) {
        ViewModelFactoryUtil.provideVideoInterviewInvitationDetailsViewModelFactory(this, args.jobId)
    }
    private val videoInterListViewModel: VideoInterviewListViewModel by navGraphViewModels(R.id.videoInterviewListFragment)
    private val baseViewModel: VideoInterviewViewModel by activityViewModels()

    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB
    lateinit var interviewInvitationCommunicator: InterviewInvitationCommunicator

    lateinit var binding: FragmentVideoInterviewDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

            getVideoInterviewDetails()

            displayQuestionListEvent.observe(viewLifecycleOwner, EventObserver {
                baseViewModel.jobId = videoInterviewDetailsViewModel.jobID!!
                baseViewModel.applyId = videoInterviewDetailsViewModel.applyId.value!!
                //findNavController().navigate(VideoInterviewDetailsFragmentDirections.actionVideoInterviewDetailsFragmentToQuestionListFragment("854375", "182982535"))
                findNavController().navigate(R.id.questionListFragment)
            })

            displayGuidelineEvent.observe(viewLifecycleOwner, EventObserver {
                baseViewModel.jobId = videoInterviewDetailsViewModel.jobID!!
                baseViewModel.applyId = videoInterviewDetailsViewModel.applyId.value!!
                videoInterListViewModel.commonData.value?.totalVideoInterview?.toInt()?.let {
//                    if (it < 4)
//                        if (videoInterviewDetailsViewModel.detailsData.value?.vStatuCode == "3")
//                            findNavController().navigate(VideoInterviewDetailsFragmentDirections.actionVideoInterviewDetailsFragmentToQuestionListFragment())
//                        else
//                            findNavController().navigate(VideoInterviewDetailsFragmentDirections.actionVideoInterviewDetailsFragmentToGuidelineLandingFragment())
//                    else
//                        findNavController().navigate(VideoInterviewDetailsFragmentDirections.actionVideoInterviewDetailsFragmentToQuestionListFragment())
                    findNavController().navigate(VideoInterviewDetailsFragmentDirections.actionVideoInterviewDetailsFragmentToGuidelineLandingFragment())

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
            startActivity<JobBaseActivity>("from" to "employer", "jobids" to jobids, "lns" to lns, "position" to 0, "deadline" to deadline)
        }
    }
}
