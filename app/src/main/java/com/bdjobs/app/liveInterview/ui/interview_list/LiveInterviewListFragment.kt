package com.bdjobs.app.liveInterview.ui.interview_list

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.databinding.FragmentLiveInterviewListBinding
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository
import kotlinx.android.synthetic.main.fragment_live_interview_list.*
import timber.log.Timber

class LiveInterviewListFragment : Fragment() {

    lateinit var time : String

    private val liveInterviewListViewModel: LiveInterviewListViewModel by viewModels {
        LiveInterviewListViewModelFactory(
                LiveInterviewRepository(requireActivity().application as Application),
                time
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        time = requireArguments().getString("activityDate", "0")

    }

    lateinit var binding: FragmentLiveInterviewListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentLiveInterviewListBinding.inflate(inflater).apply {
            viewModel = liveInterviewListViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("$time")

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration.Builder().setFallbackOnNavigateUpListener { onNavigateUp() }.build()
        tool_bar?.setupWithNavController(navController, appBarConfiguration)

        liveInterviewListViewModel.getLiveInterviewList(time)

        val adapter = LiveInterviewListAdapter(requireContext(),ClickListener{
//            it.userSeenLiveInterview = "True"
            findNavController().navigate(LiveInterviewListFragmentDirections.actionLiveInterviewListFragmentToLiveInterviewDetailsFragment(it.jobId!!,it.jobTitle!!))
        })

        rv_live_interview?.adapter = adapter

        liveInterviewListViewModel.apply {
            liveInterviewListData.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })

//            list.observe(viewLifecycleOwner, Observer {
//                adapter.submitList(it)
//            })
        }
    }

    private fun onNavigateUp(): Boolean {
        activity?.onBackPressed()
        return true
    }
}