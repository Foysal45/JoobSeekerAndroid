package com.bdjobs.app.assessment


import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.layout_assessment_test_info.view.*
import kotlinx.android.synthetic.main.layout_what_is_employability_certification.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    lateinit var viewModel: HomeViewModel
    lateinit var binding : FragmentHomeBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        Log.d("rakib", "called onCreateView")

        binding = FragmentHomeBinding.inflate(inflater)

        viewModel = ViewModelProvider(requireNotNull(activity)).get(HomeViewModel::class.java)

        binding.lifecycleOwner = this

        binding.homeViewModel = viewModel

        learn_more_btn?.setOnClickListener {view ->
            view.findNavController().navigate(R.id.action_viewPagerFragment_to_testInstructionFragment)
        }

        binding.btnCl?.setOnClickListener {
            it.findNavController().navigate(R.id.action_viewPagerFragment_to_testLocationFragment)
        }

        binding.noPendingTest?.takeNewTestBtn?.setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_testLocationFragment)
        }

        binding.assessmentInfo?.changeBtn?.setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_testLocationFragment)
        }



        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.takeIf { it.containsKey("status") }?.apply {
            Log.d("rakibe in home" , getString("status"))
            if(getString("status").equals("true"))
            {
                viewModel.getHomeInfo()
                binding.homeViewModel = viewModel
            }
        }
    }


}
