package com.bdjobs.app.videoInterview.ui.rating

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentRatingBinding
import com.bdjobs.app.videoInterview.data.repository.VideoInterviewRepository

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

}