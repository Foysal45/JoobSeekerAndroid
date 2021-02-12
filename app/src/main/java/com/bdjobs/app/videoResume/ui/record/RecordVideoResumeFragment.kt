package com.bdjobs.app.videoResume.ui.record

import android.os.Bundle
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
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.Utilities.toFormattedSeconds
import com.bdjobs.app.databinding.FragmentRecordVideoResumeBinding
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import com.bdjobs.app.videoResume.ui.questions.VideoResumeQuestionsViewModel
import com.google.android.material.snackbar.Snackbar
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Facing
import kotlinx.android.synthetic.main.fragment_record_video_resume.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class RecordVideoResumeFragment : Fragment() {
    lateinit var snackbar: Snackbar
    lateinit var videoFile: File

    private val questionListViewModel: VideoResumeQuestionsViewModel by navGraphViewModels(R.id.questionListFragment)
    private val recordVideoViewModel: RecordVideoResumeViewModel by viewModels { ViewModelFactoryUtil.provideVideoResumeRecordVideoViewModelFactory(this) }
    lateinit var binding: FragmentRecordVideoResumeBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecordVideoResumeBinding.inflate(inflater).apply {
            viewModel = recordVideoViewModel
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

        recordVideoViewModel.apply {
            onVideoRecordingStartedEvent.observe(viewLifecycleOwner, EventObserver {
                updateUI(it)
                captureVideo()
            })

            progressPercentage.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                seekbar_video_duration.progress = it.toInt()
            })
            progressPercentage.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                seekbar_video_duration.progress = it.toInt()
            })
            elapsedTimeInString.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                tv_time_remaining_value?.text = it
            })

            shouldShowDoneButton.observe(viewLifecycleOwner, androidx.lifecycle.Observer { shouldShow ->
                if (shouldShow)
                    btn_done?.show()
                else
                    btn_done?.hide()
            })

            onVideoDoneEvent.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                // if (it){
                camera_view?.close()
                // }
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
                    Toast.makeText(context, "Your video has been uploaded uploaded successfully", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, "There was an error", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            })
        }
    }

    private fun captureVideo() {
        val dir = File(requireContext().getExternalFilesDir(null)!!.absoluteFile, "video_resume")
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val newFile = File(dir.path + File.separator + "bdjobs_${recordVideoViewModel.videoResumeManagerData.value?.questionId}_$timeStamp.mp4")
        camera_view?.takeVideoSnapshot(newFile)
    }


    private fun initializeUI() {
        recordVideoViewModel.prepareData(questionListViewModel.videoResumeManagerData.value)
        tv_question_heading?.text = "Question ${questionListViewModel.videoResumeManagerData.value?.questionSerialNo} of 5"
        tv_time_value?.text = "${questionListViewModel.videoResumeManagerData.value?.questionDuration?.toFormattedSeconds()}"
    }

    private fun initializeCamera() {
        camera_view?.setLifecycleOwner(viewLifecycleOwner)

        try {
            camera_view?.facing = Facing.FRONT
        } catch (e: Exception) {
            camera_view?.facing = Facing.BACK
        } finally {

        }

        camera_view?.addCameraListener(object : CameraListener() {
            override fun onVideoRecordingStart() {
                super.onVideoRecordingStart()
            }

            override fun onVideoRecordingEnd() {
                super.onVideoRecordingEnd()
            }

            override fun onCameraOpened(options: CameraOptions) {
                super.onCameraOpened(options)
            }

            override fun onCameraClosed() {
                super.onCameraClosed()
            }

            override fun onVideoTaken(result: VideoResult) {
                super.onVideoTaken(result)
                if (recordVideoViewModel.onVideoDoneEvent.value == true) {
                    videoFile = result.file
                    recordVideoViewModel.videoResumeManagerData.value?.file = result.file
                    recordVideoViewModel.uploadSingleVideoToServer(recordVideoViewModel.videoResumeManagerData.value)
                    showSnackbar()
                }

            }

        })
    }

    private fun showSnackbar() {
        snackbar = Snackbar.make(cl_timeline, "Your recorded video is uploading, please wait...", Snackbar.LENGTH_INDEFINITE).also {
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

            tool_bar?.title = "Recording Question ${recordVideoViewModel.videoResumeManagerData.value?.questionSerialNo}"

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
            findNavController().popBackStack(R.id.questionListFragment, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
    }
}