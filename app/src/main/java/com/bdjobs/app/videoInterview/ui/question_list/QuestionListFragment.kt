package com.bdjobs.app.videoInterview.ui.question_list

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.databinding.FragmentQuestionDetailsBinding
import com.bdjobs.app.videoInterview.data.models.VideoInterviewQuestionList
import com.bdjobs.app.videoInterview.data.models.VideoManager
import com.bdjobs.app.videoInterview.ui.interview_details.VideoInterviewDetailsViewModel
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import com.fondesa.kpermissions.*
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_question_details.*
import java.io.File

const val NUM_BYTES_NEEDED = 1024 * 1024 * 500L

class QuestionListFragment : Fragment() {

    private val args: QuestionListFragmentArgs by navArgs()
    private val questionListViewModel: QuestionListViewModel by navGraphViewModels(R.id.questionListFragment) { ViewModelFactoryUtil.provideVideoInterviewQuestionListViewModelFactory(this) }
    private val questionDetailsViewModel: VideoInterviewDetailsViewModel by navGraphViewModels(R.id.videoInterviewDetailsFragment)
    private var permissionGranted: Boolean = false
    lateinit var binding: FragmentQuestionDetailsBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentQuestionDetailsBinding.inflate(inflater).apply {
            viewModel = questionListViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        tool_bar?.setupWithNavController(navController, appBarConfiguration)


        questionListViewModel.getQuestionList(questionDetailsViewModel.jobId.value, questionDetailsViewModel.applyId.value)

        val adapter = QuestionListAdapter(requireContext(), ClickListener {

            if (it.buttonName!!.equalIgnoreCase("Record Video") || it.buttonName!!.equalIgnoreCase("Record Again") || it.buttonName!!.equalIgnoreCase("")) {
//                 permissionGranted =
                askForPermission(it)
//                if (permissionGranted){
//
//
//                }
            } else {
                val videoManager = VideoManager(
                        jobId = questionDetailsViewModel.jobId.value,
                        applyId = questionDetailsViewModel.applyId.value,
                        questionId = it.questionId,
                        questionSerial = it.questionSerialNo,
                        questionText = it.questionText,
                        questionDuration = it.questionDuration,
                        totalQuestion = questionListViewModel.questionListData.value?.size
                )

                questionListViewModel._videoManagerData.postValue(videoManager)
                findNavController().navigate(QuestionListFragmentDirections.actionQuestionListFragmentToViewVideoFragment(it.videoUrl))
            }


        })

        rv_question?.adapter = adapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(rv_question)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rv_question?.layoutManager = layoutManager

        rv_question?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val snapPosition = (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                Log.d("rakib", "snap position $snapPosition")
                updateStepperText(snapPosition)
                when (snapPosition) {
                    0 -> {
                        img_previous_question?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_previous_question_grey)
                        img_previous_question?.isEnabled = false
                    }
                    adapter.itemCount - 1 -> {
                        img_next_question?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_next_question_grey)
                        img_next_question?.isEnabled = false
                    }
                    else -> {
                        img_previous_question?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_previous_question_black)
                        img_next_question?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_next_question_black)
                        img_previous_question?.isEnabled = true
                        img_next_question?.isEnabled = true
                    }
                }
            }
        })

        questionListViewModel.apply {

            questionListData.observe(viewLifecycleOwner, Observer {
                it?.let {
                    updateSteppers(it.size)
                    updateIndicators(it.size)
                    updateQuestionStatus(it.size)
                    adapter.submitList(it)
                }
            })

            onSubmitButtonClickEvent.observe(viewLifecycleOwner, EventObserver { notInterested ->
                if (notInterested) {
                    binding.btnSubmit.apply {
                        text = "Submit"
                        isEnabled = true
                    }
                    openWarningDialog()
                } else {
                    questionListViewModel.submitAnswerToServer()
                }
            })

            onNextQuestionClickEvent.observe(viewLifecycleOwner, EventObserver {
                rv_question?.smoothScrollToPosition(layoutManager.findFirstCompletelyVisibleItemPosition() + 1)
            })

            onPreviousQuestionClickEvent.observe(viewLifecycleOwner, EventObserver {
                rv_question?.smoothScrollToPosition(layoutManager.findFirstCompletelyVisibleItemPosition() - 1)
            })

            onSubmissionDoneEvent.observe(viewLifecycleOwner, EventObserver { submitted ->
                if (submitted) {
                    cb_not_interested?.hide()
                    btn_submit?.hide()
                    btn_submit_later?.hide()
                }
            })

            onNotInterestedSubmissionDoneEvent.observe(viewLifecycleOwner,EventObserver{
                findNavController().navigate(QuestionListFragmentDirections.actionQuestionListFragmentToVideoInterviewListFragment())
            })

        }

        binding.btnSubmitLater.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnGuide.setOnClickListener {
            findNavController().navigate(R.id.action_questionListFragment_to_guidelineLandingFragment)
        }
    }

    private fun createDirectory() {
        val storageDir = File(requireContext().getExternalFilesDir(null)!!.absoluteFile, "video_interview")
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
    }

    private fun updateQuestionStatus(size: Int) {
        if (size > 0) {
            for (i in 0 until size) {
                when {
                    questionListViewModel.questionListData.value?.get(i)?.questionStatus.equals("2") -> {
                        img_question1.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_video_submitted)
                        img_question2.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_video_submitted)
                        img_question3.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_video_submitted)
                        img_question4.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_video_submitted)
                        img_question5.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_video_submitted)
                    }
                    questionListViewModel.questionListData.value?.get(i)?.questionStatus.equals("3") -> {
                        img_question1.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_video_not_submitted)
                        img_question2.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_video_not_submitted)
                        img_question3.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_video_not_submitted)
                        img_question4.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_video_not_submitted)
                        img_question5.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_video_not_submitted)
                    }
                    else -> {
                        img_question1.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_question_not_recorded)
                        img_question2.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_question_not_recorded)
                        img_question3.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_question_not_recorded)
                        img_question4.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_question_not_recorded)
                        img_question5.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_question_not_recorded)
                    }
                }
            }
        }
    }

    private fun updateStepperText(position: Int) {
        when (position) {
            0 -> {
                tv_q1?.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))

                tv_q2?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
                tv_q3?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
                tv_q4?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
                tv_q5?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
            }

            1 -> {
                tv_q2?.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))

                tv_q1?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
                tv_q3?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
                tv_q4?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
                tv_q5?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
            }

            2 -> {
                tv_q3?.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))

                tv_q1?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
                tv_q2?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
                tv_q4?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
                tv_q5?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
            }

            3 -> {
                tv_q4?.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))

                tv_q1?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
                tv_q2?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
                tv_q3?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
                tv_q5?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
            }

            4 -> {
                tv_q5?.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))

                tv_q1?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
                tv_q2?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
                tv_q3?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
                tv_q4?.setTextColor(ContextCompat.getColor(requireContext(), R.color.inactive_question_text_color))
            }
        }

    }

    private fun updateIndicators(size: Int) {
        if (size > 1) {
            img_previous_question?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_previous_question_grey)
            img_previous_question?.isEnabled = false
            img_next_question?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_next_question_black)
            img_next_question?.isEnabled = true
        } else {
            img_previous_question?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_previous_question_grey)
            img_previous_question?.isEnabled = false
            img_next_question?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_next_question_grey)
            img_next_question?.isEnabled = true
        }

    }

    private fun updateSteppers(totalQuestions: Int) {
        when (totalQuestions) {
            4 -> {
                line_view_question5?.hide()
                img_question5?.hide()
                tv_q5?.hide()
            }
            3 -> {
                line_view_question5?.hide()
                img_question5?.hide()
                tv_q5?.hide()

                line_view_question4?.hide()
                img_question4?.hide()
                tv_q4.hide()
            }
            2 -> {
                line_view_question5?.hide()
                img_question5?.hide()
                tv_q5?.hide()

                line_view_question4?.hide()
                img_question4?.hide()
                tv_q4.hide()

                line_view_question3?.hide()
                img_question3?.hide()
                tv_q3.hide()
            }
            1 -> {
                line_view_question5?.hide()
                img_question5?.hide()
                tv_q5?.hide()

                line_view_question4?.hide()
                img_question4?.hide()
                tv_q4.hide()

                line_view_question3?.hide()
                img_question3?.hide()
                tv_q3.hide()

                line_view_question2?.hide()
                img_question2?.hide()
                tv_q2.hide()
            }
        }
    }

    private fun openWarningDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext()).create()
        val view = layoutInflater.inflate(R.layout.dialog_video_not_interested_to_submit, null)
        view?.apply {

            findViewById<TextView>(R.id.dialog_tv_body).text = if (questionListViewModel.questionCommonData.value?.vUserTotalAnswerequestion!!.toInt() > 0)
                getText(R.string.dialog_body_when_answered)
            else
                getText(R.string.dialog_body_when_not_answered)

            findViewById<Button>(R.id.dialog_btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            findViewById<Button>(R.id.dialog_btn_yes).setOnClickListener {
                questionListViewModel.submitAnswerToServer()
                dialog.dismiss()
            }
        }
        dialog.setView(view)
        dialog.show()
    }

    private fun askForPermission(data: VideoInterviewQuestionList.Data): Boolean {

        permissionsBuilder(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).build().send { result ->
            when {
                result.allGranted() -> {
                    //Timber.d("Granted")
                    //findNavController().navigate(R.id.recordViedeoFragment)
                    permissionGranted = true

                    val videoManager = VideoManager(
                            jobId = questionDetailsViewModel.jobId.value,
                            applyId = questionDetailsViewModel.applyId.value,
                            questionId = data.questionId,
                            questionSerial = data.questionSerialNo,
                            questionText = data.questionText,
                            questionDuration = data.questionDuration,
                            totalQuestion = questionListViewModel.questionListData.value?.size
                    )

                    questionListViewModel._videoManagerData.postValue(videoManager)

                    createDirectory()

                    findNavController().navigate(R.id.recordViedeoFragment)

                }
                result.allDenied() || result.anyDenied() -> {
                    //Toast.makeText(context,"Please enable this permission to record answer(s)",Toast.LENGTH_SHORT).show()
                    openSettingsDialog()
                    //permissionGranted =  false
                }

                result.allPermanentlyDenied() || result.anyPermanentlyDenied() -> {
                    Log.d("rakib", "permanently denied")
                    //openSettingsDialog()
                    openSettingsDialog()
                    //permissionGranted =  false
                }
            }
        }
        return permissionGranted!!
    }

    private fun openSettingsDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext()).create()
        val view = layoutInflater.inflate(R.layout.dialog_enable_video_permissions, null)
        view?.apply {
            findViewById<Button>(R.id.dialog_btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            findViewById<Button>(R.id.dialog_btn_go_to_settings).setOnClickListener {
                val intent = createAppSettingsIntent()
                startActivity(intent)
                dialog.dismiss()
            }
        }
        dialog.setView(view)
        dialog.show()
    }

    private fun createAppSettingsIntent() = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", context?.packageName, null)
    }

}