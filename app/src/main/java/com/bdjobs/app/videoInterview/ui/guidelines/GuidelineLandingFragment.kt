package com.bdjobs.app.videoInterview.ui.guidelines

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentGuidelineLandingBinding
import kotlinx.android.synthetic.main.fragment_guideline_landing.*

class GuidelineLandingFragment : Fragment() {

    private val guidelineVIewModel : GuidelineVIewModel by navGraphViewModels(R.id.guidelineLandingFragment)
    lateinit var binding : FragmentGuidelineLandingBinding

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
            findNavController().navigate(R.id.action_guidelineLandingFragment_to_guidelinesViewpagerFragment)
        }

        guidelineVIewModel.apply {
            viewInBangla.observe(viewLifecycleOwner, Observer { checked ->
                if (checked){
                    cb_view_in_english?.isChecked = false
                    cb_view_in_bangla?.isEnabled = false
                    cb_view_in_english?.isEnabled = true
                } else{
                    cb_view_in_bangla?.isEnabled = true
                }
            })
            viewInEnglish.observe(viewLifecycleOwner, Observer {checked ->
                if (checked){
                    cb_view_in_bangla?.isChecked = false
                    cb_view_in_english?.isEnabled = false
                    cb_view_in_bangla?.isEnabled = true
                } else{
                    cb_view_in_bangla?.isEnabled = true
                }
            })
        }

    }


}