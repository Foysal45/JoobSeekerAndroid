package com.bdjobs.app.Settings

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentResumePrivacyBinding


class ResumePrivacyFragment : Fragment() {

    private lateinit var binding : FragmentResumePrivacyBinding
    private lateinit var communicator: SettingsCommunicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResumePrivacyBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        communicator = activity as SettingsCommunicator
        binding.backIV.setOnClickListener {
            communicator.backButtonPressed()
        }
    }


}