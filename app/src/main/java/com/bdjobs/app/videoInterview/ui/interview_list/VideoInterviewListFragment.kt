package com.bdjobs.app.videoInterview.ui.interview_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.assessment.models.Booking
import com.bdjobs.app.assessment.viewmodels.PaymentViewModel
import com.bdjobs.app.assessment.viewmodels.PaymentViewModelFactory
import com.bdjobs.app.databinding.FragmentPaymentSuccessfulBinding
import com.bdjobs.app.databinding.FragmentVideoInterviewListBinding
import com.bdjobs.app.videoInterview.data.repository.InterviewListRepository
import com.bdjobs.app.videoInterview.ui.question_details.QuestionDetailsViewModel
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import kotlinx.android.synthetic.main.fragment_video_interview_list.*




class VideoInterviewListFragment : Fragment() {

    private val interviewListViewModel : InterviewListViewModel by viewModels { ViewModelFactoryUtil.provideInterViewListModelFactory(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentVideoInterviewListBinding.inflate(inflater)

        /*interviewListViewModel = ViewModelProvider(this,viewModelFactory).get(InterviewListViewModel::class.java)
*/
        binding.viewModel = interviewListViewModel

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