package com.bdjobs.app.resume_dashboard.ui.view_edit_resume

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import com.bdjobs.app.R
import com.bdjobs.app.databinding.ViewEditResumeFragmentBinding
import com.bdjobs.app.resume_dashboard.data.models.DataMRD
import com.bdjobs.app.resume_dashboard.data.repositories.ResumeDashboardRepository
import kotlinx.android.synthetic.main.layout_bdjobs_resume_steps.*

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

        initViews()

        setUpObservers()
    }

    private fun setUpObservers() {
        viewEditResumeViewModel.apply {

            detailResumeStat.observe(viewLifecycleOwner, Observer {
                bdJobsResumeStepStatus(it)
            })
        }
    }

    private fun bdJobsResumeStepStatus(it: DataMRD) {
        if (it.personalDetailsInfo == 1) tv_personal_details.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_check_sign_resume, 0, R.drawable.ic_back_arrow_resume, 0)
        else tv_personal_details.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_cross_sign_resume, 0,  R.drawable.ic_back_arrow_resume, 0)

        if (it.academicInfo == 1) tv_academic_qualification.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_check_sign_resume, 0, R.drawable.ic_back_arrow_resume, 0)
        else tv_academic_qualification.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_cross_sign_resume, 0,  R.drawable.ic_back_arrow_resume, 0)

        if (it.trainingInfo == 1) tv_training.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_check_sign_resume, 0, R.drawable.ic_back_arrow_resume, 0)
        else tv_training.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_cross_sign_resume, 0,  R.drawable.ic_back_arrow_resume, 0)

        if (it.proQualificationInfo == 1) tv_professional_qualification.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_check_sign_resume, 0, R.drawable.ic_back_arrow_resume, 0)
        else tv_professional_qualification.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_cross_sign_resume, 0,  R.drawable.ic_back_arrow_resume, 0)

        if (it.experienceInfo == 1) tv_experience.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_check_sign_resume, 0, R.drawable.ic_back_arrow_resume, 0)
        else tv_experience.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_cross_sign_resume, 0,  R.drawable.ic_back_arrow_resume, 0)

        if (it.specializationInfo == 1) tv_specialization.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_check_sign_resume, 0, R.drawable.ic_back_arrow_resume, 0)
        else tv_specialization.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_cross_sign_resume, 0,  R.drawable.ic_back_arrow_resume, 0)

        if (it.referenceInfo == 1) tv_reference.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_check_sign_resume, 0, R.drawable.ic_back_arrow_resume, 0)
        else tv_reference.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_cross_sign_resume, 0,  R.drawable.ic_back_arrow_resume, 0)

        if (it.photographInfo == 1) tv_photograph.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_check_sign_resume, 0, R.drawable.ic_back_arrow_resume, 0)
        else tv_photograph.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_cross_sign_resume, 0,  R.drawable.ic_back_arrow_resume, 0)
    }

    private fun initViews() {
        binding.tvChangeVisibility.paint.isUnderlineText = true
    }

}