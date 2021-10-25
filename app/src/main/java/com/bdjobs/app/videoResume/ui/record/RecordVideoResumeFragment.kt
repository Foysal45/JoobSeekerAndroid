package com.bdjobs.app.videoResume.ui.record

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.openSettingsDialog
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.Utilities.toFormattedSeconds
import com.bdjobs.app.databinding.FragmentRecordVideoResumeBinding
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import com.bdjobs.app.videoResume.data.models.VideoResumeQuestionList
import com.bdjobs.app.videoResume.ui.questions.VideoResumeQuestionsFragmentDirections
import com.bdjobs.app.videoResume.ui.questions.VideoResumeQuestionsViewModel
import com.fondesa.kpermissions.*
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.android.material.snackbar.Snackbar
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Mode
import kotlinx.android.synthetic.main.fragment_record_video_resume.*
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class RecordVideoResumeFragment : Fragment() {
    lateinit var snackbar: Snackbar
    lateinit var videoFile: File

    private val videoResumeQuestionsViewModel: VideoResumeQuestionsViewModel by navGraphViewModels(R.id.videoResumeQuestionsFragment)
    private val recordVideoResumeViewModel: RecordVideoResumeViewModel by viewModels {
        ViewModelFactoryUtil.provideVideoResumeRecordVideoViewModelFactory(
            this
        )
    }
    lateinit var binding: FragmentRecordVideoResumeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecordVideoResumeBinding.inflate(inflater).apply {
            viewModel = recordVideoResumeViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        tool_bar?.setupWithNavController(navController, appBarConfiguration)

        initializeCamera()

        initializeUI()

        setUpObservers()
    }

    private fun setUpObservers() {
        recordVideoResumeViewModel.apply {
            onVideoRecordingStartedEvent.observe(viewLifecycleOwner, EventObserver {
                updateUI(it)
                captureVideo()
            })

            progressPercentage.observe(viewLifecycleOwner, {
                seekbar_video_duration.progress = it.toInt()
            })
            progressPercentage.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                seekbar_video_duration.progress = it.toInt()
            })
            elapsedTimeInString.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                tv_time_remaining_value?.text = it
            })

            shouldShowDoneButton.observe(
                viewLifecycleOwner,
                { shouldShow ->
                    if (shouldShow)
                        btn_done?.show()
                    else
                        btn_done?.hide()
                })

            onVideoDoneEvent.observe(viewLifecycleOwner, {
                if (it) {
                    camera_view.stopVideo()
                }
            })

            onUploadStartEvent.observe(viewLifecycleOwner, EventObserver { uploadStarted ->
                if (uploadStarted) {
                    btn_done.isEnabled = false
                    showSnackbar()
                }
            })

            onUploadDoneEvent.observe(viewLifecycleOwner, EventObserver { uploadDone ->
                if (uploadDone) {
                    try {
                        videoFile.delete()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    Toast.makeText(
                        context,
                        "Your video has been uploaded successfully.",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, "There was an error", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            })
        }
    }

    private fun captureVideo() {
        Timber.d("Video capture start")
        try {
            val dir =
                File(requireContext().getExternalFilesDir(null)!!.absoluteFile, "video_resume")
            dir.mkdirs()
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val newFile =
                File(dir.path + File.separator + "bdjobs_${recordVideoResumeViewModel.videoResumeManagerData.value?.questionId}_$timeStamp.mp4")
            camera_view.mode = Mode.VIDEO
            camera_view?.takeVideoSnapshot(newFile)
        } catch (e: Exception) {
            Timber.e("captureVideo: ${e.localizedMessage}")
        }
    }


    @SuppressLint("SetTextI18n")
    private fun initializeUI() {
        recordVideoResumeViewModel.prepareData(videoResumeQuestionsViewModel.videoResumeManagerData.value)
        tv_question_heading?.text =
            "Q${videoResumeQuestionsViewModel.videoResumeManagerData.value?.questionSerialNo}. ${videoResumeQuestionsViewModel.videoResumeManagerData.value?.questionText}"
        tv_question_title?.text =
            videoResumeQuestionsViewModel.videoResumeManagerData.value?.questionTextBng
        tv_time_value?.text =
            "${videoResumeQuestionsViewModel.videoResumeManagerData.value?.questionDuration?.toFormattedSeconds()}"

        binding.apply {
            tvAnswerTips.paint.isUnderlineText = true
            tvAnswerTips.setOnClickListener {
                buildTipsDialog(
                    videoResumeQuestionsViewModel.videoResumeManagerData.value?.questionTextBng,
                    videoResumeQuestionsViewModel.videoResumeManagerData.value?.answerHintBn
                )
            }
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

    private fun initializeCamera() {
        camera_view?.setLifecycleOwner(viewLifecycleOwner)

        try {
            camera_view?.facing = Facing.FRONT
        } catch (e: Exception) {
            camera_view?.facing = Facing.BACK
        } finally {

        }

        camera_view.addCameraListener(object : CameraListener() {
            override fun onVideoTaken(result: VideoResult) {
                super.onVideoTaken(result)
                Timber.d("video taken!")
                if (recordVideoResumeViewModel.onVideoDoneEvent.value == true) {
                    videoFile = result.file
                    recordVideoResumeViewModel.videoResumeManagerData.value?.file = result.file
                    recordVideoResumeViewModel.uploadSingleVideoToServer(recordVideoResumeViewModel.videoResumeManagerData.value)
                    showSnackbar()
                }
            }

            override fun onVideoRecordingStart() {
                Timber.d("video recording start!")
                super.onVideoRecordingStart()
            }

            override fun onVideoRecordingEnd() {
                Timber.d("video recording start!")
                super.onVideoRecordingEnd()
            }
        })
    }

    private fun showSnackbar() {
        snackbar = Snackbar.make(
            cl_timeline,
            "Your recorded video is saving automatically in system. Please wait ...",
            Snackbar.LENGTH_INDEFINITE
        ).also {
            it.show()
        }
    }

    private fun updateUI(recordingStarted: Boolean) {
        if (recordingStarted) {
            tv_question_heading?.hide()
            cl_total_time?.hide()
            btn_start_recording?.hide()
            view_timeline?.show()
            cl_timeline?.show()

            tv_rec?.show()

            seekbar_video_duration.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    return true
                }
            })

            tool_bar?.title =
                "Recording Question ${recordVideoResumeViewModel.videoResumeManagerData.value?.questionSerialNo}"

        } else {

        }
    }

    override fun onPause() {
        super.onPause()
        try {
            snackbar.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (findNavController().currentDestination?.id == R.id.recordVideoResumeFragment) {
                findNavController().popBackStack(R.id.videoResumeQuestionsFragment, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
    }
}