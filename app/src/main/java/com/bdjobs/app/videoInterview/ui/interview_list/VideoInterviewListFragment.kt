package com.bdjobs.app.videoInterview.ui.interview_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.assessment.adapters.CertificateListAdapter
import com.bdjobs.app.assessment.adapters.ClickListener
import com.bdjobs.app.assessment.enums.Status
import com.bdjobs.app.assessment.models.Booking
import com.bdjobs.app.assessment.viewmodels.PaymentViewModel
import com.bdjobs.app.assessment.viewmodels.PaymentViewModelFactory
import com.bdjobs.app.databinding.FragmentPaymentSuccessfulBinding
import com.bdjobs.app.databinding.FragmentVideoInterviewListBinding
import com.bdjobs.app.videoInterview.data.repository.InterviewListRepository
import com.bdjobs.app.videoInterview.ui.question_details.QuestionDetailsViewModel
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_certificate_list.*
import kotlinx.android.synthetic.main.fragment_video_interview_list.*




class VideoInterviewListFragment : Fragment() {

    private val interviewViewModel : InterviewListViewModel by viewModels { ViewModelFactoryUtil.provideInterViewListModelFactory(this) }
    lateinit var snackbar: Snackbar
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentVideoInterviewListBinding.inflate(inflater)
        recyclerView = binding.interviewRecyclerView
        interviewViewModel.getInterviewList()
        /*interviewListViewModel = ViewModelProvider(this,viewModelFactory).get(InterviewListViewModel::class.java)
*/
        /*interviewViewModel.getInterviewList()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.interviewListViewModel = interviewViewModel
      *//*  binding.interviewList.adapter = InterviewListAdapter(requireNotNull(context), ClickListener {
            interviewListViewModel.displayResultDetails(it)
        })*//*
        binding.interviewList.adapter = InterviewListAdapter(requireNotNull(context),ClickListenerInterViewList {
            interviewViewModel.displayInterViewDetails(it)
        })*/

       /* binding.interviewListViewModel*/

     /*  var  viewModel = ViewModelProvider(requireNotNull(activity)).get(InterviewListViewModel::class.java)
*/
        binding.lifecycleOwner = this

        binding.interviewListViewModel = interviewViewModel

        recyclerView.adapter = InterviewListAdapter(requireNotNull(context))

       /* binding.interviewList.adapter = InterviewListAdapter(requireNotNull(context),ClickListenerInterViewList {
            interviewViewModel.displayInterViewDetails(it)
        })*/
      /*  interviewViewModel.status.observe(viewLifecycleOwner, Observer {
            try {
                snackbar = Snackbar.make(vedioInterviewCL, "Something went wrong", Snackbar.LENGTH_LONG)
                when (it) {
                    Status.ERROR ->

                        snackbar.apply {
                            setAction(
                                    "Retry"
                            ) {
                                interviewViewModel.getInterviewList()
                            }.show()

                        }
                    else -> {
                        snackbar.dismiss()
                    }
                }
            } catch (e: Exception) {
            }
        })*/

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