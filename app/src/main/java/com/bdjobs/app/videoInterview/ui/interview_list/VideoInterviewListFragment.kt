package com.bdjobs.app.videoInterview.ui.interview_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentVideoInterviewListBinding
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_video_interview_list.*


class VideoInterviewListFragment : Fragment() {

    private val interviewViewModel: InterviewListViewModel by viewModels { ViewModelFactoryUtil.provideInterViewListModelFactory(this) }
    lateinit var snackbar: Snackbar
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentVideoInterviewListBinding.inflate(inflater)
        recyclerView = binding.interviewRecyclerView

        binding.lifecycleOwner = this
        val adapter = InterviewListAdapter(requireContext(), ClickListenerInterViewList {jobId,jobTitle->
            //interviewViewModel.displayInterViewDetails(it)
            findNavController().navigate(VideoInterviewListFragmentDirections.actionVideoInterviewListFragmentToVideoInterviewDetailsFragment(jobId,jobTitle))
        })
        recyclerView.adapter = adapter
        interviewViewModel.interviews.observe(viewLifecycleOwner, Observer {
            Log.d("Activity", "list: ${it?.size}")

            adapter.submitList(it)

        })
        binding.interviewListViewModel = interviewViewModel


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration.Builder().setFallbackOnNavigateUpListener { onNavigateUp() }.build()
        //setSupportActionBar(tool_bar)
        tool_bar?.setupWithNavController(navController, appBarConfiguration)
    }

    private fun onNavigateUp(): Boolean {
        activity?.onBackPressed()
        return true
    }
}