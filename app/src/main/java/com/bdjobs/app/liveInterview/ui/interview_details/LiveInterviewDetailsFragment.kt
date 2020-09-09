package com.bdjobs.app.liveInterview.ui.interview_details

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.databinding.FragmentLiveInterviewDetailsBinding
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository
import kotlinx.android.synthetic.main.fragment_live_interview_details.*
import kotlinx.android.synthetic.main.fragment_live_interview_details.tool_bar
import kotlinx.android.synthetic.main.fragment_video_interview_details.*

class LiveInterviewDetailsFragment : Fragment() {

    private val args : LiveInterviewDetailsFragmentArgs by navArgs()

    private val liveInterviewDetailsViewModel : LiveInterviewDetailsViewModel by viewModels {
        LiveInterviewDetailsViewModelFactory(
                LiveInterviewRepository(requireActivity().application as Application),
                args.jobId
        )
    }

    lateinit var binding: FragmentLiveInterviewDetailsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentLiveInterviewDetailsBinding.inflate(inflater).apply {
            viewModel = liveInterviewDetailsViewModel
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

        val adapter = LiveInterviewDetailsAdapter(requireContext(), ClickListener{

        })

        rv_live_interview_details?.adapter = adapter

        liveInterviewDetailsViewModel.apply {
            liveInterviewDetailsData.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })
        }
    }

}