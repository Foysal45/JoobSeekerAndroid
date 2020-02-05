package com.bdjobs.app.assessment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.R
import com.bdjobs.app.assessment.viewmodels.HomeViewModel
import com.bdjobs.app.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_assessment_no_pending_test.*
import kotlinx.android.synthetic.main.layout_what_is_employability_certification.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    lateinit var viewModel: HomeViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentHomeBinding.inflate(inflater)

        viewModel = ViewModelProvider(requireNotNull(activity)).get(HomeViewModel::class.java)

        binding.lifecycleOwner = this

        binding.homeViewModel = viewModel


        learn_more_btn?.setOnClickListener {view ->
            view.findNavController().navigate(R.id.action_viewPagerFragment_to_testInstructionFragment)
        }

        btn_cl?.setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_testLocationFragment)
        }

        take_test_btn?.setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_testLocationFragment)
        }

        return binding.root
    }


}
