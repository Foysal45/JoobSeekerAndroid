package com.bdjobs.app.videoInterview.ui.guidelines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentGuidelineLandingBinding
import kotlinx.android.synthetic.main.fragment_guideline_landing.*

class GuidelineLandingFragment : Fragment() {

    private val guidelineVIewModel: GuidelineVIewModel by navGraphViewModels(R.id.guidelineLandingFragment)
    lateinit var binding: FragmentGuidelineLandingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentGuidelineLandingBinding.inflate(inflater).apply {
            viewModel = guidelineVIewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        img_close?.setOnClickListener {
            findNavController().popBackStack()
        }

        btn_start?.setOnClickListener {

            var language = if (guidelineVIewModel.viewInBangla.value!!) "bangla" else "english"

            findNavController().navigate(GuidelineLandingFragmentDirections.actionGuidelineLandingFragmentToGuidelinesViewpagerFragment(language))
        }

        guidelineVIewModel.apply {
            viewInBangla.observe(viewLifecycleOwner, Observer { checked ->
                if (checked) {
                    cb_view_in_english?.isChecked = false
                    cb_view_in_bangla?.isEnabled = false
                    cb_view_in_english?.isEnabled = true
                } else {
                    cb_view_in_bangla?.isEnabled = true
                }

                tv_guideline_heading.text = "ভিডিও ইন্টারভিউ এর গাইডলাইনগুলো"
                tv_guideline_body.text = "ভিডিও রেকর্ডিং শুরু করার আগে প্রয়োজনীয় ডিভাইসগুলো এবং নির্দেশনাগুলো সম্পর্কে প্রথমেই জেনে নিন"
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

                tv_guideline_heading.text = "Guidelines for Video Interview"
                tv_guideline_body.text = "Learn about the required devices and Interview instructions before you start recording video."
                btn_start.text = "Start"
            })
        }

    }


}