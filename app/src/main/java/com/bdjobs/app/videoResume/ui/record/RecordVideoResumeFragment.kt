package com.bdjobs.app.videoResume.ui.record

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.Utilities.toFormattedSeconds
import com.bdjobs.app.databinding.FragmentRecordVideoResumeBinding
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import com.bdjobs.app.videoResume.ui.questions.VideoResumeQuestionsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_record_video_resume.*
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


@SuppressLint("RestrictedApi")
class RecordVideoResumeFragment : Fragment() {
    lateinit var snackbar: Snackbar
    lateinit var videoFile: File
    var sdk = 0

    private val videoResumeQuestionsViewModel: VideoResumeQuestionsViewModel by navGraphViewModels(R.id.videoResumeQuestionsFragment)
    private val recordVideoResumeViewModel: RecordVideoResumeViewModel by viewModels {
        ViewModelFactoryUtil.provideVideoResumeRecordVideoViewModelFactory(
            this
        )
    }
    lateinit var binding: FragmentRecordVideoResumeBinding
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var imagePreview: Preview? = null
    private var videoCapture: VideoCapture? = null
    private var cameraControl: CameraControl? = null
    private var cameraInfo: CameraInfo? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        sdk =  Integer.valueOf(Build.VERSION.SDK_INT)


        startCamera()
        initializeUI()
        setUpObservers()
    }


    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())
        var cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()


        cameraProviderFuture.addListener({
            imagePreview = Preview.Builder().apply {
                setTargetAspectRatio(AspectRatio.RATIO_16_9)
            }.build()



            videoCapture = VideoCapture.Builder().apply {
                setTargetAspectRatio(AspectRatio.RATIO_16_9)
            }.build()

            val cameraProvider = cameraProviderFuture.get()

            val camera = cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                imagePreview,
                videoCapture
            )
            camera_view.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            imagePreview?.setSurfaceProvider(camera_view.surfaceProvider)
            cameraControl = camera.cameraControl
            cameraInfo = camera.cameraInfo
        }, ContextCompat.getMainExecutor(requireContext()))
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

                        videoCapture?.stopRecording()
                   // cameraExecutor.shutdown()
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

            startRecord(newFile)

        } catch (e: Exception) {
            Timber.e("captureVideo: ${e.localizedMessage}")
        }
    }

    private fun startRecord(newFile: File) {

        val outputFileOptions = VideoCapture.OutputFileOptions.Builder(newFile).build()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }


        videoCapture?.startRecording(outputFileOptions, cameraExecutor, object : VideoCapture.OnVideoSavedCallback {
            override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                if (recordVideoResumeViewModel.onVideoDoneEvent.value == true) {
                        videoFile = newFile
                        recordVideoResumeViewModel.videoResumeManagerData.value?.file = newFile
                        recordVideoResumeViewModel.uploadSingleVideoToServer(recordVideoResumeViewModel.videoResumeManagerData.value)
                    showSnackbar()

                }



            }

            override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                val msg = "Video capture failed: $message"
                camera_view.post {
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                }
            }
        })

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