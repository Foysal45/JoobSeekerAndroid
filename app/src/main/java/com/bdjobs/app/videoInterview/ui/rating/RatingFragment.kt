package com.bdjobs.app.videoInterview.ui.rating

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentRatingBinding
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import com.bdjobs.app.videoInterview.ui.interview_details.VideoInterviewDetailsViewModel
import com.bdjobs.app.videoInterview.util.EventObserver
import kotlinx.android.synthetic.main.fragment_rating.*
import kotlinx.android.synthetic.main.fragment_rating.tool_bar
import org.jetbrains.anko.sdk27.coroutines.onRatingBarChange

class RatingFragment : Fragment() {

    private val questionDetailsViewModel: VideoInterviewDetailsViewModel by navGraphViewModels(R.id.videoInterviewDetailsFragment)
    private val ratingViewModel: RatingViewModel by viewModels {
        RatingViewModelFactory(
                VideoInterviewRepository(requireActivity().application as Application),
                questionDetailsViewModel.applyId.value, questionDetailsViewModel.jobId.value
        )
    }

    lateinit var binding: FragmentRatingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentRatingBinding.inflate(inflater).apply {
            viewModel = ratingViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        tool_bar?.setupWithNavController(navController, appBarConfiguration)

        tool_bar?.title = questionDetailsViewModel.jobTitle.value

        rating?.onRatingBarChange { ratingBar, rating, fromUser ->
            //toast("${rating.toInt()}")
            ratingViewModel.apply {
                this.rating.value = rating.toInt()
                onRatingChanged()
            }
        }

        ratingViewModel.navigateToListEvent.observe(viewLifecycleOwner,EventObserver{
            if (it)
                findNavController().popBackStack(R.id.videoInterviewListFragment,false)
        })
    }
}