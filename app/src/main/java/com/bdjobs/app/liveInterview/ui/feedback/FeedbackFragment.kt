package com.bdjobs.app.liveInterview.ui.feedback

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentFeedbackBinding
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository
import com.bdjobs.app.liveInterview.ui.interview_details.LiveInterviewDetailsViewModel
import com.bdjobs.app.videoInterview.util.EventObserver
import org.jetbrains.anko.sdk27.coroutines.onRatingBarChange


class FeedbackFragment : Fragment() {


    private lateinit var binding:FragmentFeedbackBinding
    private val args: FeedbackFragmentArgs by navArgs()
    private val feedbackViewModel: FeedbackViewModel by viewModels {
        FeedBackViewModelFactory(
                LiveInterviewRepository(requireActivity().application as Application),
                args.applyID,
                args.jobID
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentFeedbackBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = feedbackViewModel
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolBar.setupWithNavController(findNavController(), AppBarConfiguration(findNavController().graph))
        binding.toolBar.title = "Feedback"

        binding.rating.onRatingBarChange { ratingBar, rating, fromUser ->
            feedbackViewModel.apply {
                this.rating.value = rating.toInt()
                onRatingChanged()
            }
        }

        setUpObservers()
    }

    private fun setUpObservers() {
        feedbackViewModel.apply {
            messageButtonClickEvent.observe(viewLifecycleOwner,EventObserver{
                if (it) {
                    findNavController().navigate(FeedbackFragmentDirections.actionFeedbackFragmentToChatFragment(args.processID,args.companyName))
                }
            })

            navigateToListEvent.observe(viewLifecycleOwner,EventObserver{
                if (it)
                findNavController().popBackStack(R.id.liveInterviewListFragment,false)
            })
        }
    }

}