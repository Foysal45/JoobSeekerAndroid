package com.bdjobs.app.resume_dashboard.ui.view_edit_resume

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.navGraphViewModels
import com.bdjobs.app.R
import com.bdjobs.app.databinding.ViewEditResumeFragmentBinding
import com.bdjobs.app.resume_dashboard.data.repositories.ResumeDashboardRepository

class ViewEditResumeFragment : Fragment() {

    private lateinit var binding:ViewEditResumeFragmentBinding
    private val viewEditResumeViewModel : ViewEditResumeViewModel by viewModels {
        ViewEditResumeViewModelFactory(ResumeDashboardRepository(requireActivity().application as Application))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = ViewEditResumeFragmentBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = viewEditResumeViewModel
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}