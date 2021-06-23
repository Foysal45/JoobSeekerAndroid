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
import com.bdjobs.app.ManageResume.ManageResumeActivity
import com.bdjobs.app.R
import com.bdjobs.app.databinding.ViewEditResumeFragmentBinding
import com.bdjobs.app.editResume.EditResLandingActivity
import com.bdjobs.app.editResume.PhotoUploadActivity
import com.bdjobs.app.editResume.educationInfo.AcademicBaseActivity
import com.bdjobs.app.editResume.employmentHistory.EmploymentHistoryActivity
import com.bdjobs.app.editResume.otherInfo.OtherInfoBaseActivity
import com.bdjobs.app.editResume.personalInfo.PersonalInfoActivity
import com.bdjobs.app.resume_dashboard.data.models.DataMRD
import com.bdjobs.app.resume_dashboard.data.repositories.ResumeDashboardRepository
import com.bdjobs.app.videoResume.VideoResumeActivity
import kotlinx.android.synthetic.main.layout_bdjobs_resume_steps.*
import kotlinx.android.synthetic.main.layout_no_personalized_resume.*
import kotlinx.android.synthetic.main.layout_no_video_resume.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity

class ViewEditResumeFragment : Fragment() {

    private lateinit var binding: ViewEditResumeFragmentBinding
    private val viewEditResumeViewModel: ViewEditResumeViewModel by viewModels {
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
    }


    private fun initViews() {
        binding.apply {
            tvChangeVisibility.paint.isUnderlineText = true
            tvEditVideoResume.setOnClickListener { startActivity<VideoResumeActivity>() }
            tvEditBdjobsResume.setOnClickListener { startActivity<EditResLandingActivity>() }
            tvEditPersonalizedResume.setOnClickListener {
                startActivity<ManageResumeActivity>(
                        "from" to "uploadResume"
                )
            }

            tvPersonalDetails.setOnClickListener {
                goToFragment("personal", "P")
            }
            tvAcademicQualification.setOnClickListener {
                goToFragment("academic", "E")
            }
            tvTraining.setOnClickListener {
                goToFragment("training", "E")
            }
            tvProfessionalQualification.setOnClickListener {
                goToFragment("professional", "E")
            }
            tvExperience.setOnClickListener {
                goToFragment("employ", "Emp")
            }
            tvSpecialization.setOnClickListener {
                goToFragment("specialization", "Other")
            }
            tvReference.setOnClickListener {
                goToFragment("reference", "Other")
            }
            tvPhotograph.setOnClickListener {
                startActivity<PhotoUploadActivity>()
            }

            tvQuestionOne.setOnClickListener { navigateToVideoResumeQuestionFragment() }
            tvQuestionTwo.setOnClickListener { navigateToVideoResumeQuestionFragment() }
            tvQuestionThree.setOnClickListener { navigateToVideoResumeQuestionFragment() }
            tvQuestionFour.setOnClickListener { navigateToVideoResumeQuestionFragment() }
            tvQuestionFive.setOnClickListener { navigateToVideoResumeQuestionFragment() }
        }

        btn_add_video_resume.setOnClickListener {
            startActivity<VideoResumeActivity>()
        }

        btn_add_personalized_resume.setOnClickListener {
            startActivity<ManageResumeActivity>(
                    "from" to "uploadResume"
            )
        }

    }

    private fun goToFragment(s: String, check: String) {
        when (check) {
            "P" ->
                startActivity<PersonalInfoActivity>("name" to s, "personal_info_edit" to "null")
            "E" ->
                startActivity<AcademicBaseActivity>("name" to s, "education_info_add" to "null")
            "Emp" ->
                startActivity<EmploymentHistoryActivity>("name" to s, "emp_his_add" to "null")
            "Other" ->
                startActivity<OtherInfoBaseActivity>("name" to s, "other_info_add" to "null")

        }
    }

    private fun navigateToVideoResumeQuestionFragment() {
        startActivity<VideoResumeActivity>("from" to "ViewEditResume")
    }

}