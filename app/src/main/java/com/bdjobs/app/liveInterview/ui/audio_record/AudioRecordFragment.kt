package com.bdjobs.app.liveInterview.ui.audio_record

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.Utilities.toFormattedSeconds
import com.bdjobs.app.Web.WebActivity
import com.bdjobs.app.databinding.FragmentAudioRecordBinding
import com.bdjobs.app.videoInterview.util.EventObserver
import com.google.android.material.snackbar.Snackbar
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Facing
import org.jetbrains.anko.startActivity
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AudioRecordFragment : Fragment() {

    private lateinit var binding:FragmentAudioRecordBinding
    private val audioRecordViewModel : AudioRecordViewModel by navGraphViewModels(R.id.audioRecordFragment)


    private val recorder: MediaRecorder = MediaRecorder()
    private val handler: Handler = Handler()
    val updater: Runnable = object : Runnable {
        override fun run() {
            handler.postDelayed(this, 1)
            val maxAmplitude: Int = recorder.maxAmplitude
            if (maxAmplitude != 0) {
                binding.lvAudioVisualizer.progress = maxAmplitude
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentAudioRecordBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = audioRecordViewModel
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolBar.setupWithNavController(navController, appBarConfiguration)
        binding.toolBar.title = "Check Audio / Video"


        binding.btnGuide.setOnClickListener {
            context?.startActivity<WebActivity>("url" to "https://mybdjobs.bdjobs.com/mybdjobs/bdjobs-app-user-guide-for-video-resume.asp", "from" to "liveInterview")
        }

        initializeCamera()
//        dispatchTakeVideoIntent()
        initializeUI()

        setUpObservers()
    }

    private fun setUpObservers() {
        audioRecordViewModel.apply {
            onVideoRecordingStartedEvent.observe(viewLifecycleOwner, EventObserver {
                updateUI(it)
                captureVideo()
            })

            progressPercentage.observe(viewLifecycleOwner, Observer {
                binding.seekbarVideoDuration.progress = it.toInt()
            })

            elapsedTimeInString.observe(viewLifecycleOwner, Observer {
                binding.tvTimeRemainingValue.text = it
            })

            onVideoDoneEvent.observe(viewLifecycleOwner, Observer {
                binding.cameraView.close()
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initializeUI() {
        binding.tvTimeValue.text = AudioRecordViewModel.TOTAL_TIME.toFormattedSeconds()
    }

    private fun initializeCamera() {
        binding.cameraView.setLifecycleOwner(viewLifecycleOwner)

        try {
            binding.cameraView.facing = Facing.FRONT
        } catch (e: Exception) {
            binding.cameraView.facing = Facing.BACK
        } finally {

        }

        binding.cameraView.addCameraListener(object : CameraListener() {
            override fun onVideoRecordingStart() {
                Timber.d("Video Recording Start")
                super.onVideoRecordingStart()
            }

            override fun onVideoRecordingEnd() {
                Timber.d("Video Recording End")
                super.onVideoRecordingEnd()
            }

            override fun onCameraOpened(options: CameraOptions) {
                Timber.d("Camera Opened")
                super.onCameraOpened(options)
            }

            override fun onCameraClosed() {
                Timber.d("Camera Closed")
                super.onCameraClosed()

                showSnackBar()

                handler.removeCallbacks(updater)
                recorder.stop()
                recorder.reset()
                recorder.release()
            }

            override fun onVideoTaken(result: VideoResult) {
                Timber.d("Video Taken")
                super.onVideoTaken(result)
                if (audioRecordViewModel.onVideoDoneEvent.value==true) {
//                    audioRecordViewModel.stopRecording()
                    showSnackBar()
//                    Toast.makeText(requireContext(), "Audio testing time limit is over", Toast.LENGTH_LONG).show()
//                    findNavController().popBackStack()
//                    result.file.delete()
                }

            }

        })
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun updateUI(recordingStarted: Boolean) {
        if (recordingStarted) {
            binding.apply {
//                viewIntermediate.hide()
                clTotalTime.hide()
//                btnRecordVideo.hide()
//                viewTimeline.hide()
                clTimeline.show()
//                btnStopVideo.show()
                tvRec.show()

                seekbarVideoDuration.setOnTouchListener { _, _ -> true }

                toolBar.title = "Check Audio / Video"
            }


            try {
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                recorder.setOutputFile("/dev/null")
                recorder.prepare()
                recorder.start()
            } catch (ignored: IllegalStateException) {
            } catch (ignored: IOException) {
            }

            handler.post(updater)
        }
    }

    private fun captureVideo() {
        val dir = File(requireContext().getExternalFilesDir(null)!!.absoluteFile, "live_interview")
        dir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val newFile = File(dir.path + File.separator + "bdjobs_testAudioRecording_$timeStamp.mp4")
        binding.cameraView.takeVideoSnapshot(newFile)


    }


    private fun showSnackBar() {
        snackbar = Snackbar.make(binding.clTimeline, "Audio testing time limit is over", Snackbar.LENGTH_INDEFINITE).also {
            it.show()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            snackbar.dismiss()
            handler.removeCallbacks(updater)
            recorder.stop()
            recorder.reset()
            recorder.release()
        } catch (e: Exception) {
        }
    }

//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        handler.post(updater)
//    }

    override fun onDestroy() {
        super.onDestroy()
//        handler.removeCallbacks(updater)
//        recorder.stop()
//        recorder.reset()
//        recorder.release()
    }

    private lateinit var snackbar: Snackbar
}