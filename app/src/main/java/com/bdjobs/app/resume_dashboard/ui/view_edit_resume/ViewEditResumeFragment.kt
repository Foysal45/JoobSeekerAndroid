package com.bdjobs.app.resume_dashboard.ui.view_edit_resume

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bdjobs.app.ManageResume.ManageResumeActivity
import com.bdjobs.app.ManageResume.ViewPersonalizedResume
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Settings.SettingBaseActivity
import com.bdjobs.app.utilities.equalIgnoreCase
import com.bdjobs.app.utilities.logException
import com.bdjobs.app.Web.WebActivity
import com.bdjobs.app.databinding.ViewEditResumeFragmentBinding
import com.bdjobs.app.editResume.EditResLandingActivity
import com.bdjobs.app.editResume.PhotoUploadActivity
import com.bdjobs.app.editResume.educationInfo.AcademicBaseActivity
import com.bdjobs.app.editResume.employmentHistory.EmploymentHistoryActivity
import com.bdjobs.app.editResume.otherInfo.OtherInfoBaseActivity
import com.bdjobs.app.editResume.personalInfo.PersonalInfoActivity
import com.bdjobs.app.resume_dashboard.data.repositories.ResumeDashboardRepository
import com.bdjobs.app.videoResume.VideoResumeActivity
import kotlinx.android.synthetic.main.layout_no_bdjobs_resume.*
import kotlinx.android.synthetic.main.layout_no_personalized_resume.*
import kotlinx.android.synthetic.main.layout_no_video_resume.*
import org.jetbrains.anko.support.v4.startActivity
import java.util.*

class ViewEditResumeFragment : Fragment() {

    private lateinit var binding: ViewEditResumeFragmentBinding
    private lateinit var session: BdjobsUserSession
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

        session = BdjobsUserSession(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.apply {
            tvChangeVisibility.paint.isUnderlineText = true

            tvChangeVisibility.setOnClickListener { startActivity<SettingBaseActivity>("from" to "dashboard") }

            tvEditVideoResume.setOnClickListener { startActivity<VideoResumeActivity>() }
            tvViewVideoResume.setOnClickListener { navigateToVideoResumeQuestionFragment() }
            tvEditBdjobsResume.setOnClickListener { startActivity<EditResLandingActivity>() }
            tvViewBdjobsResume.setOnClickListener {
                try {
                    val str1 = random()
                    val str2 = random()
                    val id = str1 + session.userId + session.decodId + str2
                    startActivity<WebActivity>(
                        "url" to "https://mybdjobs.bdjobs.com/mybdjobs/masterview_for_apps.asp?id=$id",
                        "from" to "cvview"
                    )
                } catch (e: Exception) {
                    logException(e)
                }
            }
            tvEditPersonalizedResume.setOnClickListener {
                startActivity<ManageResumeActivity>(
                        "from" to "uploadResume"
                )
            }
            tvViewPersonalizedResume.setOnClickListener {
                if (viewEditResumeViewModel.downloadCVStat.value!=null) {
                    activity?.startActivity(
                        Intent(activity, ViewPersonalizedResume::class.java)
                            .putExtra("PDF_URL", viewEditResumeViewModel.downloadCVStat.value?.data?.get(0)!!.path))
                } else {
                    Toast.makeText(requireContext(), "Couldn't fetch link! Try again", Toast.LENGTH_SHORT).show()
                }

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
            tvQuestionSix.setOnClickListener { navigateToVideoResumeQuestionFragment() }
        }

        btn_add_bdjobs_resume.setOnClickListener {
            startActivity<EditResLandingActivity>()
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

    override fun onResume() {
        super.onResume()

        setUpObserver()
    }

    private fun setUpObserver() {
        viewEditResumeViewModel.apply {
//            showBdJobsResumeSteps.value = true
//            showVideoResumeSteps.value = true

            val session = BdjobsUserSession(requireContext())

            if (session.isCvPosted!=null && session.isCvPosted.equalIgnoreCase("true")) {
                resumePrivacyStatus()
            }
            videoResumeQuestionList()
            downloadCv("download")
            manageResumeDetailsStat(session.isCvPosted?:"False")

            isLoading.observe(viewLifecycleOwner,{
                if (it) {
                    binding.cvResumePrivacy.visibility = View.GONE
                } else {
                    if (session.isCvPosted!=null && session.isCvPosted.equalIgnoreCase("true")) {
                        binding.cvResumePrivacy.visibility = View.VISIBLE
                    } else {
                        binding.cvResumePrivacy.visibility = View.GONE
                    }
                }
            })
        }
    }

    private fun random(): String {
        val chars = "abcdefghijklmnopqrstuvwxyz12345678910".toCharArray()
        val sb = StringBuilder()
        val random = Random()
        for (i in 0..4) {
            val c = chars[random.nextInt(chars.size)]
            sb.append(c)
        }
        val output = sb.toString()
        println(output)
        return output
    }


}