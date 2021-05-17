package com.bdjobs.app.liveInterview.ui.instructions

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentInstructionLandingBinding
import kotlinx.android.synthetic.main.fragment_instruction_landing.*

class InstructionLandingFragment : Fragment() {

    private val instructionViewModel : InstructionViewModel by navGraphViewModels(R.id.instructionLandingFragment)
    private lateinit var binding:FragmentInstructionLandingBinding
    private val args: InstructionLandingFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentInstructionLandingBinding.inflate(inflater).apply {
            viewModel = instructionViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgClose.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnStart.setOnClickListener {

            var language = if (instructionViewModel.viewInBengali.value!!) "bangla" else "english"

            findNavController().navigate(InstructionLandingFragmentDirections.actionInstructionLandingFragmentToInstructionViewPageFragment(language,args.jobID,args.jobTitle))
        }

        instructionViewModel.apply {
            viewInBengali.observe(viewLifecycleOwner, Observer { checked ->
                if (checked) {
                    cb_view_in_english?.isChecked = false
                    cb_view_in_bangla?.isEnabled = false
                    cb_view_in_english?.isEnabled = true
                } else {
                    cb_view_in_bangla?.isEnabled = true
                }

                tv_guideline_heading.text = "লাইভ ইন্টারভিউ এর গাইডলাইনগুলো"
                tv_guideline_body.text = "লাইভ ইন্টারভিউ শুরু করার আগে প্রয়োজনীয় ডিভাইসগুলো এবং নির্দেশনাগুলো সম্পর্কে জেনে নিন"
                btn_start.text = "শুরু করুন"
            })
            viewInEnglish.observe(viewLifecycleOwner, Observer { checked ->
                if (checked) {
                    cb_view_in_bangla?.isChecked = false
                    cb_view_in_english?.isEnabled = false
                    cb_view_in_bangla?.isEnabled = true
                } else {
                    cb_view_in_bangla?.isEnabled = true
                }

                tv_guideline_heading.text = "Live Interview Guidelines"
                tv_guideline_body.text = "Know about necessary devices & instructions before starting Live Interview."
                btn_start.text = "Get started"
            })
        }
    }
}