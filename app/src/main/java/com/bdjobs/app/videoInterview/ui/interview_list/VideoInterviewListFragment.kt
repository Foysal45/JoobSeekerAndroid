package com.bdjobs.app.videoInterview.ui.interview_list

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentVideoInterviewListBinding
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import kotlinx.android.synthetic.main.fragment_video_interview_list.*
import kotlinx.android.synthetic.main.layout_no_data_found.*


class VideoInterviewListFragment : Fragment() {

    private val videoInterviewListViewModel: VideoInterviewListViewModel by navGraphViewModels(R.id.videoInterviewListFragment) {
        ViewModelFactoryUtil.provideVideoInterviewListModelFactory(
            this
        )
    }
    lateinit var binding: FragmentVideoInterviewListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentVideoInterviewListBinding.inflate(inflater).apply {
            viewModel = videoInterviewListViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val time: String = requireArguments().getString("activityDate", "0")


        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration.Builder().setFallbackOnNavigateUpListener { onNavigateUp() }.build()
        tool_bar?.setupWithNavController(navController, appBarConfiguration)

        videoInterviewListViewModel.getVideoInterviewList(time)

        val adapter = VideoInterviewListAdapter(requireContext(), ClickListener {
            if (findNavController().currentDestination?.id == R.id.videoInterviewListFragment) {
                findNavController().navigate(
                    VideoInterviewListFragmentDirections.actionVideoInterviewListFragmentToVideoInterviewDetailsFragment(
                        it.jobId,
                        it.jobTitle
                    )
                )
            }

        })

        rv_video_interview?.adapter = adapter

        rv_video_interview.apply {
            this.adapter = adapter
        }

        videoInterviewListViewModel.apply {
            videoInterviewListData.observe(viewLifecycleOwner, {
                adapter.submitList(it)
            })
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        textView10.text = "You don't have any Video Interview yet."
    }


    private fun onNavigateUp(): Boolean {
        activity?.onBackPressed()
        return true
    }
}