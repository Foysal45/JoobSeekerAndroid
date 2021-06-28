package com.bdjobs.app.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        communicator = activity as SettingsCommunicator
        binding.backIV.setOnClickListener {
            communicator.backButtonPressed()
        }
    }


}