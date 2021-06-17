package com.bdjobs.app.resume_dashboard.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.navGraphViewModels
import com.bdjobs.app.R
import com.bdjobs.app.databinding.DashboardFragmentBinding

class DashboardFragment : Fragment() {

    private lateinit var binding : DashboardFragmentBinding
    private val dashboardViewModel: DashboardViewModel by navGraphViewModels(R.id.dashboardFragment)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DashboardFragmentBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = dashboardViewModel
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        binding.tvChangeVisibility.paint.isUnderlineText = true
    }

}