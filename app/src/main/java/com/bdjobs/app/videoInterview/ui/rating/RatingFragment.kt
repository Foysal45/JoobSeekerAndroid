package com.bdjobs.app.videoInterview.ui.rating

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentRatingBinding
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository
import kotlinx.android.synthetic.main.fragment_rating.*
import org.jetbrains.anko.sdk27.coroutines.onRatingBarChange
import org.jetbrains.anko.support.v4.toast
import timber.log.Timber

class RatingFragment : Fragment() {

    private val ratingViewModel: RatingViewModel by viewModels { RatingViewModelFactory(
            VideoInterviewRepository(requireActivity().application as Application),
            "",""
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

        rating?.onRatingBarChange { ratingBar, rating, fromUser ->
            toast("${rating.toInt()}")
            ratingViewModel.apply {
                this.rating.value = rating.toInt()
                onRatingChanged()
            }
        }
    }
}