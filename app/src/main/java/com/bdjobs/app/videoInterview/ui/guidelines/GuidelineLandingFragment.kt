package com.bdjobs.app.videoInterview.ui.guidelines

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_guideline_landing.*

class GuidelineLandingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guideline_landing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        img_close?.setOnClickListener {
            findNavController().popBackStack()
        }

        btn_start?.setOnClickListener {
            findNavController().navigate(R.id.action_guidelineLandingFragment_to_guidelinesViewpagerFragment)
        }
    }

}