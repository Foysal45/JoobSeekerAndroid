package com.bdjobs.app.videoInterview.ui.rating

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.ajkerDeal.utilities.hideKeyboard
import com.bdjobs.app.databinding.FragmentRatingBinding
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoResume.VideoResumeActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_rating.*
import kotlinx.android.synthetic.main.layout_create_video_resume_bottom_guide.view.*
import org.jetbrains.anko.sdk27.coroutines.onRatingBarChange

class RatingFragment : Fragment() {

    private val args : RatingFragmentArgs by navArgs()
    private val ratingViewModel: RatingViewModel by viewModels {
        RatingViewModelFactory(
                VideoInterviewRepository(requireActivity().application as Application),
                args.applyID, args.jobID
        )
    }

    lateinit var binding: FragmentRatingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
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

        tool_bar?.title = args.jobTitle

        rating?.onRatingBarChange { _, rating, _ ->
            ratingViewModel.apply {
                this.rating.value = rating.toInt()
                onRatingChanged()
            }
        }

        scrollview_feedback.viewTreeObserver.addOnScrollChangedListener {
            hideKeyboard()
        }

        ratingViewModel.showSnackbar.observe(viewLifecycleOwner, { it ->
            it?.getContentIfNotHandled()?.let {
                Snackbar.make(cl_root,it, Snackbar.LENGTH_SHORT).show()
            }
        })

        ratingViewModel.navigateToListEvent.observe(viewLifecycleOwner,EventObserver {
            if (it)
                findNavController().navigate(R.id.afterSubmitFragment)
        })

        video_resume_guide.tv_learn_more_label.setOnClickListener {
            requireContext().startActivity(
                Intent(requireContext(),
                    VideoResumeActivity::class.java)
            )
        }
    }

}