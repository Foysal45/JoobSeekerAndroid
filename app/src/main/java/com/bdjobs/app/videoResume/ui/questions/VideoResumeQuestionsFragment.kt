package com.bdjobs.app.videoResume.ui.questions

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.Web.WebActivity
import com.bdjobs.app.databinding.FragmentVideoResumeQuestionsBinding
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import com.bdjobs.app.videoResume.data.models.VideoResumeManager
import com.bdjobs.app.videoResume.data.models.VideoResumeQuestionList
import com.fondesa.kpermissions.*
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_video_resume_questions.*
import org.jetbrains.anko.startActivity
import timber.log.Timber
import java.io.File


class VideoResumeQuestionsFragment : Fragment() {

//    private val baseVideoResumeViewModel : VideoResumeViewModel by activityViewModels()

    private val videoResumeQuestionsViewModel: VideoResumeQuestionsViewModel by navGraphViewModels(R.id.videoResumeQuestionsFragment) {
        ViewModelFactoryUtil.provideVideoResumeQuestionsViewModelFactory(this)
    }
    lateinit var binding: FragmentVideoResumeQuestionsBinding

    private var permissionGranted: Boolean = false

    private lateinit var session: BdjobsUserSession


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVideoResumeQuestionsBinding.inflate(inflater).apply {
            viewModel = videoResumeQuestionsViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        session = BdjobsUserSession(requireContext())

        val navController = findNavController()
//        val appBarConfiguration = AppBarConfiguration(navController.graph)
        val appBarConfiguration =
            AppBarConfiguration.Builder().setFallbackOnNavigateUpListener { onNavigateUp() }.build()

        tool_bar?.setupWithNavController(navController, appBarConfiguration)

        val adapter = VideoResumeQuestionsAdapter(requireContext(), ClickListener {
            val videoResumeManager = VideoResumeManager(
                slNo = it.slNo,
                questionSerialNo = it.questionSerialNo,
                questionId = it.questionId,
                questionText = it.questionText,
                questionTextBng = it.questionTextBng,
                questionDuration = it.questionDuration,
                answerHintEn = it.answerHint,
                answerHintBn = it.answerHintBn,
                aID = it.aID,
                totalView = it.totalView
            )

            videoResumeQuestionsViewModel._videoResumeManagerData.postValue(videoResumeManager)

            if (it.buttonStatus == "1") {
                createDirectory()
                askForPermission(it)

            } else {
                if (findNavController().currentDestination?.id == R.id.videoResumeQuestionsFragment) {
                    findNavController().navigate(
                        VideoResumeQuestionsFragmentDirections.actionVideoResumeQuestionsFragmentToViewVideoResumeFragment(
                            it.videoUrl,
                            it.questionTextBng
                        )
                    )
                }

            }
        })

        rv_question?.adapter = adapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(rv_question)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rv_question?.layoutManager = layoutManager
        rv_question?.isNestedScrollingEnabled = false
        rv_question?.setHasFixedSize(false)

        rv_question?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val snapPosition =
                    layoutManager.findFirstCompletelyVisibleItemPosition()

                adapter.notifyDataSetChanged()

                videoResumeQuestionsViewModel._selectedItemPosition.value = snapPosition
                updateStepperText(snapPosition)
                updateIndicators(snapPosition)

            }

        })

        adapter.onTipsClicked { data ->
            buildTipsDialog(data.questionTextBng, data.answerHintBn)
        }

        binding.cvVideoResumeVisibilityRoot.setOnClickListener {
            findNavController().navigate(VideoResumeQuestionsFragmentDirections.actionVideoResumeQuestionsFragmentToVideoResumeLandingFragment())
        }

        videoResumeQuestionsViewModel.apply {
            getQuestions()
            getStats()

            questionListData.observe(viewLifecycleOwner, Observer {
                it?.let {
                    updateSteppers(it.size)
                    updateIndicators(videoResumeQuestionsViewModel.selectedItemPosition.value!!)
                    updateQuestionStatus(it.size)
                    adapter.submitList(it)
                }
            })

            onNextQuestionClickEvent.observe(viewLifecycleOwner, EventObserver {
                rv_question?.smoothScrollToPosition(layoutManager.findFirstCompletelyVisibleItemPosition() + 1)
            })

            onPreviousQuestionClickEvent.observe(viewLifecycleOwner, EventObserver {
                rv_question?.smoothScrollToPosition(layoutManager.findFirstCompletelyVisibleItemPosition() - 1)
            })

            showVideoResumeToEmployers.observe(viewLifecycleOwner, {
                session.isVideoResumeShowToEmployers = it
                if (isAlertOn.value !== "0") session.insertVideoResumeVisibility(false) else session.insertVideoResumeVisibility(
                    true
                )
            })
        }

        binding.btnGuide.setOnClickListener {
            context?.startActivity<WebActivity>(
                "url" to "https://mybdjobs.bdjobs.com/mybdjobs/bdjobs-app-user-guide-for-video-resume.asp",
                "from" to "videoResume"
            )
        }

        binding.tvChangeVisibility.setOnClickListener {
            findNavController().navigateUp()
        }

//        videoResumeQuestionsViewModel.showVideoResumeToEmployers.value = session.isVideoResumeShowToEmployers
//        videoResumeQuestionsViewModel.isVideoResumeVisible.value =  session.videoResumeIsVisible
    }


    private fun createDirectory() {
        val storageDir =
            File(requireContext().getExternalFilesDir(null)!!.absoluteFile, "video_resume")
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
    }

    private fun buildTipsDialog(title: String?, message: String?) {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("প্রশ্নঃ $title")
        builder.setMessage("টিপসঃ  ${Html.fromHtml(message)}")
        builder.setPositiveButton("ওকে") { dialog, _ ->
            Timber.d("yes please hide")
            dialog.dismiss()
        }

        builder.show()
    }

    private fun updateQuestionStatus(size: Int) {
        if (size > 0) {

            when (size) {
                1 -> {
                    when (videoResumeQuestionsViewModel.questionListData.value?.get(0)?.buttonStatus) {
                        "2" -> img_question1.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question1.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }
                }
                2 -> {
                    when (videoResumeQuestionsViewModel.questionListData.value?.get(0)?.buttonStatus) {
                        "2" -> img_question1.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question1.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }

                    when (videoResumeQuestionsViewModel.questionListData.value?.get(1)?.buttonStatus) {
                        "2" -> img_question2.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question2.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }
                }
                3 -> {
                    when (videoResumeQuestionsViewModel.questionListData.value?.get(0)?.buttonStatus) {
                        "2" -> img_question1.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question1.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }

                    when (videoResumeQuestionsViewModel.questionListData.value?.get(1)?.buttonStatus) {
                        "2" -> img_question2.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question2.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }

                    when (videoResumeQuestionsViewModel.questionListData.value?.get(2)?.buttonStatus) {
                        "2" -> img_question3.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question3.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }
                }
                4 -> {
                    when (videoResumeQuestionsViewModel.questionListData.value?.get(0)?.buttonStatus) {
                        "2" -> img_question1.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question1.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }

                    when (videoResumeQuestionsViewModel.questionListData.value?.get(1)?.buttonStatus) {
                        "2" -> img_question2.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question2.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }

                    when (videoResumeQuestionsViewModel.questionListData.value?.get(2)?.buttonStatus) {
                        "2" -> img_question3.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question3.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }

                    when (videoResumeQuestionsViewModel.questionListData.value?.get(3)?.buttonStatus) {
                        "2" -> img_question4.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question4.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }

                }
                5 -> {
                    when (videoResumeQuestionsViewModel.questionListData.value?.get(0)?.buttonStatus) {
                        "2" -> img_question1.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question1.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }

                    when (videoResumeQuestionsViewModel.questionListData.value?.get(1)?.buttonStatus) {
                        "2" -> img_question2.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question2.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }

                    when (videoResumeQuestionsViewModel.questionListData.value?.get(2)?.buttonStatus) {
                        "2" -> img_question3.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question3.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }

                    when (videoResumeQuestionsViewModel.questionListData.value?.get(3)?.buttonStatus) {
                        "2" -> img_question4.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question4.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }

                    when (videoResumeQuestionsViewModel.questionListData.value?.get(4)?.buttonStatus) {
                        "2" -> img_question5.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question5.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }
                }
                6 -> {
                    when (videoResumeQuestionsViewModel.questionListData.value?.get(0)?.buttonStatus) {
                        "2" -> img_question1.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question1.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }

                    when (videoResumeQuestionsViewModel.questionListData.value?.get(1)?.buttonStatus) {
                        "2" -> img_question2.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question2.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }

                    when (videoResumeQuestionsViewModel.questionListData.value?.get(2)?.buttonStatus) {
                        "2" -> img_question3.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question3.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }

                    when (videoResumeQuestionsViewModel.questionListData.value?.get(3)?.buttonStatus) {
                        "2" -> img_question4.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question4.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }

                    when (videoResumeQuestionsViewModel.questionListData.value?.get(4)?.buttonStatus) {
                        "2" -> img_question5.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question5.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }

                    when (videoResumeQuestionsViewModel.questionListData.value?.get(5)?.buttonStatus) {
                        "2" -> img_question6.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_video_submitted
                        )
                        "1" -> img_question6.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_question_not_recorded
                        )
                    }
                }
            }
        }
    }

    private fun updateStepperText(position: Int) {
        when (position) {
            0 -> {
                tv_q1?.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))

                tv_q2?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q3?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q4?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q5?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
            }

            1 -> {
                tv_q2?.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))

                tv_q1?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q3?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q4?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q5?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
            }

            2 -> {
                tv_q3?.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))

                tv_q1?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q2?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q4?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q5?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
            }

            3 -> {
                tv_q4?.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))

                tv_q1?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q2?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q3?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q5?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
            }

            4 -> {
                tv_q5?.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))

                tv_q1?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q2?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q3?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q4?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
            }

            5 -> {
                tv_q6?.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))

                tv_q1?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q2?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q3?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q4?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
                tv_q5?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_question_text_color
                    )
                )
            }
        }

    }

    private fun updateIndicators(position: Int) {

        videoResumeQuestionsViewModel.questionListData.value?.let {
            if (it.size == 1) {
                Log.d("rakib", "size ${it.size}")
                img_previous_question?.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_previous_question_grey
                )
                img_previous_question?.isEnabled = false
                img_next_question?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_next_question_grey)
                img_next_question?.isEnabled = false
            } else {
                if (position == 0) {
                    Log.d("rakib", "camer if")
                    img_previous_question?.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_previous_question_grey
                    )
                    img_previous_question?.isEnabled = false
                    img_next_question?.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_next_question_black
                    )
                    img_next_question?.isEnabled = true
                } else if (position == videoResumeQuestionsViewModel.questionListData.value!!.size - 1) {
                    Log.d("rakib", "camer else if")
                    img_previous_question?.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_previous_question_black
                    )
                    img_previous_question?.isEnabled = true
                    img_next_question?.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_next_question_grey
                    )
                    img_next_question?.isEnabled = false
                } else {
                    img_previous_question?.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_previous_question_black
                    )
                    img_previous_question?.isEnabled = true
                    img_next_question?.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_next_question_black
                    )
                    img_next_question?.isEnabled = true
                }
            }
        }

    }

    private fun updateSteppers(totalQuestions: Int) {
        when (totalQuestions) {
            5 -> {
                line_view_question6?.hide()
                img_question6?.hide()
                tv_q6?.hide()
            }
            4 -> {
                line_view_question6?.hide()
                img_question6?.hide()
                tv_q6?.hide()

                line_view_question5?.hide()
                img_question5?.hide()
                tv_q5?.hide()
            }
            3 -> {
                line_view_question6?.hide()
                img_question6?.hide()
                tv_q6?.hide()

                line_view_question5?.hide()
                img_question5?.hide()
                tv_q5?.hide()

                line_view_question4?.hide()
                img_question4?.hide()
                tv_q4.hide()
            }
            2 -> {
                line_view_question6?.hide()
                img_question6?.hide()
                tv_q6?.hide()

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

                line_view_question6?.hide()
                img_question6?.hide()
                tv_q6?.hide()

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


    private fun askForPermission(data: VideoResumeQuestionList.Data): Boolean {

        permissionsBuilder(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).build()
            .send { result ->
                when {
                    result.allGranted() -> {
                        //Timber.d("Granted")
                        //findNavController().navigate(R.id.recordViedeoFragment)
                        permissionGranted = true

//                    val videoManager = VideoManager(
//                            jobId = questionDetailsViewModel.jobId.value,
//                            applyId = questionDetailsViewModel.applyId.value,
//                            questionId = data.questionId,
//                            questionSerial = data.questionSerialNo,
//                            questionText = data.questionText,
//                            questionDuration = data.questionDuration,
//                            totalQuestion = questionListViewModel.questionListData.value?.size
//                    )
//
//                    questionListViewModel._videoManagerData.postValue(videoManager)
//                    findNavController().navigate(QuestionListFragmentDirections.actionQuestionDetailsFragmentToRecordViedeoFragment())
                        if (findNavController().currentDestination?.id == R.id.videoResumeQuestionsFragment) {
                            findNavController().navigate(VideoResumeQuestionsFragmentDirections.actionVideoResumeQuestionsFragmentToRecordVideoResumeFragment())
                        }


                    }
                    result.allDenied() || result.anyDenied() -> {
                        //Toast.makeText(context,"Please enable this permission to record answer(s)",Toast.LENGTH_SHORT).show()
                        openSettingsDialog()
                        //permissionGranted =  false
                    }

                    result.allPermanentlyDenied() || result.anyPermanentlyDenied() -> {
                        //Log.d("rakib", "permanently denied")
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

    override fun onPause() {
        super.onPause()
        Timber.d("onPause called")
        //Log.d("rakib", "onPause called")
    }

    override fun onResume() {
        super.onResume()
        //Log.d("rakib position value " ,"${questionListViewModel.selectedItemPosition.value!!}")
        updateStepperText(videoResumeQuestionsViewModel.selectedItemPosition.value!!)
        updateIndicators(videoResumeQuestionsViewModel.selectedItemPosition.value!!)

//        questionListViewModel.applyId = baseViewModel.applyId.value
//        questionListViewModel.jobId = baseViewModel.jobId.value

    }

    private fun onNavigateUp(): Boolean {
        activity?.onBackPressed()
        return true
    }

}


