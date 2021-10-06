package com.bdjobs.app.liveInterview.ui.interview_list

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.databinding.FragmentLiveInterviewListBinding
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository
import kotlinx.android.synthetic.main.fragment_live_interview_list.*
import kotlinx.android.synthetic.main.layout_no_data_found.*
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
                              savedInstanceState: Bundle?): View {
        binding = FragmentLiveInterviewListBinding.inflate(inflater).apply {
            viewModel = liveInterviewListViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d(time)

        textView10.text = "You don't have any Live Interview yet."
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration.Builder().setFallbackOnNavigateUpListener { onNavigateUp() }.build()
        tool_bar?.setupWithNavController(navController, appBarConfiguration)

        liveInterviewListViewModel.getLiveInterviewList(time)

        val adapter = LiveInterviewListAdapter(requireContext(),ClickListener{
            findNavController().navigate(LiveInterviewListFragmentDirections.actionLiveInterviewListFragmentToLiveInterviewDetailsFragment(it.jobId!!,it.jobTitle!!))
        })

        binding.rvLiveInterview.adapter = adapter

        liveInterviewListViewModel.apply {
            liveInterviewListData.observe(viewLifecycleOwner, {
                if (it!=null && it.isNotEmpty()) {
                    binding.invitationNoDataLL.visibility = View.GONE
                    binding.rvLiveInterview.visibility = View.VISIBLE
                    adapter.submitList(it)
                } else {
                    binding.rvLiveInterview.visibility = View.GONE
                    binding.invitationNoDataLL.visibility = View.VISIBLE
                }

            })

        }
    }

    private fun onNavigateUp(): Boolean {
        activity?.onBackPressed()
        return true
    }
}