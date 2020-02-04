package com.bdjobs.app.assessment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.bdjobs.app.R
import com.bdjobs.app.Utilities.openUrlInBrowser
import com.bdjobs.app.assessment.viewmodels.ResultViewModel
import com.bdjobs.app.assessment.viewmodels.ResultViewModelFactory
import com.bdjobs.app.databinding.FragmentResultBinding

/**
 * A simple [Fragment] subclass.
 */
class ResultFragment : Fragment() {

    lateinit var resultViewModel: ResultViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val application = requireNotNull(activity).application

        val binding = FragmentResultBinding.inflate(inflater)

        val result = ResultFragmentArgs.fromBundle(arguments!!).certificateData

        val viewModelFactory = ResultViewModelFactory(result!!,application)

        resultViewModel = ViewModelProvider(this,viewModelFactory).get(ResultViewModel::class.java)

        binding.resultViewModel = resultViewModel

        binding.lifecycleOwner = this

        resultViewModel.resultMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(activity, resultViewModel.resultMessage.value, Toast.LENGTH_SHORT).show()
            }
        })

        binding.btnCl.setOnClickListener {
            activity?.openUrlInBrowser(resultViewModel.downloadLink)
        }

        return binding.root
    }
}
