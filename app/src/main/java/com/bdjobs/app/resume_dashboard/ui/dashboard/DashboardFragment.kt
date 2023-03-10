package com.bdjobs.app.resume_dashboard.ui.dashboard

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Settings.SettingBaseActivity
import com.bdjobs.app.utilities.equalIgnoreCase
import com.bdjobs.app.databinding.DashboardFragmentBinding
import com.bdjobs.app.resume_dashboard.data.repositories.ResumeDashboardRepository
import org.jetbrains.anko.support.v4.startActivity

class DashboardFragment : Fragment() {

    private lateinit var binding : DashboardFragmentBinding
    private lateinit var session: BdjobsUserSession
    private val dashboardViewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(ResumeDashboardRepository(requireActivity().application as Application))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DashboardFragmentBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = dashboardViewModel
        }

        session = BdjobsUserSession(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    override fun onResume() {
        super.onResume()

        setUpObserver()
    }

    private fun setUpObserver() {
        dashboardViewModel.apply {
            if (session.isCvPosted!!.equalIgnoreCase("true")) {
                resumePrivacyStatus()
            }

            manageResumeStats()
        }
    }

    private fun initViews() {
        binding.tvChangeVisibility.paint.isUnderlineText = true
        binding.tvChangeVisibility.setOnClickListener { startActivity<SettingBaseActivity>("from" to "dashboard") }

        if (session.isCvPosted!!.equalIgnoreCase("true")) {
            binding.clResumeVisibilityView.visibility = View.VISIBLE
        } else binding.clResumeVisibilityView.visibility = View.GONE
    }

}