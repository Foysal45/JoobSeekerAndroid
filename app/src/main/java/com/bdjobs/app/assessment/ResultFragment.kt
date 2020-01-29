package com.bdjobs.app.assessment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.bdjobs.app.R
import com.bdjobs.app.assessment.viewmodels.ResultViewModel
import com.bdjobs.app.assessment.viewmodels.ResultViewModelFactory
import com.bdjobs.app.databinding.FragmentResultBinding

/**
 * A simple [Fragment] subclass.
 */
class ResultFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val application = requireNotNull(activity).application

        val binding = FragmentResultBinding.inflate(inflater)

        val result = ResultFragmentArgs.fromBundle(arguments!!).certificateData

        val viewModelFactory = ResultViewModelFactory(result!!,application)

        binding.resultViewModel = ViewModelProvider(this,viewModelFactory).get(ResultViewModel::class.java)

        binding.lifecycleOwner = this

        return binding.root
    }
}
