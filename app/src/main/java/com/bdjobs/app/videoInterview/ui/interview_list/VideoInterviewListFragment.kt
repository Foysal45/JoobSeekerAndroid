package com.bdjobs.app.videoInterview.ui.interview_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentVideoInterviewListBinding
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import kotlinx.android.synthetic.main.fragment_video_interview_list.*


class VideoInterviewListFragment : Fragment() {

    private val videoInterviewListViewModel: VideoInterviewListViewModel by navGraphViewModels(R.id.videoInterviewListFragment){ ViewModelFactoryUtil.provideVideoInterviewListModelFactory(this) }
    lateinit var binding: FragmentVideoInterviewListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentVideoInterviewListBinding.inflate(inflater).apply {
            viewModel = videoInterviewListViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration.Builder().setFallbackOnNavigateUpListener { onNavigateUp() }.build()
        //setSupportActionBar(tool_bar)
        tool_bar?.setupWithNavController(navController, appBarConfiguration)

        videoInterviewListViewModel.getVideoInterviewList()

        val adapter = VideoInterviewListAdapter(requireContext(), ClickListener {
            findNavController().navigate(VideoInterviewListFragmentDirections.actionVideoInterviewListFragmentToVideoInterviewDetailsFragment(it.jobId, it.jobTitle))
        })

        rv_video_interview?.adapter = adapter

        rv_video_interview.apply {
            this.adapter = adapter
        }

        videoInterviewListViewModel.apply {
            videoInterviewListData.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })
        }

    }


    private fun onNavigateUp(): Boolean {
        activity?.onBackPressed()
        return true
    }
}