package com.bdjobs.app.videoInterview.ui.record_video

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.FileUtil
import com.bdjobs.app.Utilities.camera.CameraFactory
import com.bdjobs.app.Utilities.camera.CameraProvider
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.Utilities.toFormattedSeconds
import com.bdjobs.app.databinding.FragmentRecordVideoBinding
import com.bdjobs.app.videoInterview.ui.question_list.QuestionListViewModel
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import com.google.android.material.snackbar.Snackbar
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Facing
import kotlinx.android.synthetic.main.fragment_record_video.*
import kotlinx.android.synthetic.main.fragment_record_video.btn_done
import kotlinx.android.synthetic.main.fragment_record_video.camera_view
import kotlinx.android.synthetic.main.fragment_record_video.cl_timeline
import kotlinx.android.synthetic.main.fragment_record_video.cl_total_time
import kotlinx.android.synthetic.main.fragment_record_video.seekbar_video_duration
import kotlinx.android.synthetic.main.fragment_record_video.tool_bar
import kotlinx.android.synthetic.main.fragment_record_video.tv_question_heading
import kotlinx.android.synthetic.main.fragment_record_video.tv_rec
import kotlinx.android.synthetic.main.fragment_record_video.tv_time_remaining_value
import kotlinx.android.synthetic.main.fragment_record_video.tv_time_value
import kotlinx.android.synthetic.main.fragment_record_video.view_timeline
import kotlinx.android.synthetic.main.fragment_record_video_resume.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RecordVideoFragment : Fragment(), CameraProvider.OutputCallBack {

    lateinit var snackbar: Snackbar
    lateinit var videoFile: File

    private val questionListViewModel: QuestionListViewModel by navGraphViewModels(R.id.questionListFragment)
    private val recordVideoViewModel: RecordVideoViewModel by viewModels { ViewModelFactoryUtil.provideVideoInterviewRecordVideoViewModelFactory(this) }
    lateinit var binding: FragmentRecordVideoBinding
    lateinit var provider : CameraProvider


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecordVideoBinding.inflate(inflater).apply {
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

        provider  = CameraFactory.getProvider(requireContext(),camera_view, pre_view, viewLifecycleOwner, this )
        provider.initlize()

        initializeUI()

        recordVideoViewModel.apply {
            onVideoRecordingStartedEvent.observe(viewLifecycleOwner, EventObserver {
                updateUI(it)
                captureVideo()
            })
            progressPercentage.observe(viewLifecycleOwner, Observer {
                seekbar_video_duration.progress = it.toInt()
            })
            elapsedTimeInString.observe(viewLifecycleOwner, Observer {
                tv_time_remaining_value?.text = it
            })

            shouldShowDoneButton.observe(viewLifecycleOwner, Observer { shouldShow ->
                if (shouldShow)
                    btn_done?.show()
                else
                    btn_done?.hide()
            })

            onVideoDoneEvent.observe(viewLifecycleOwner, Observer {
                provider.stop()
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
         provider.record(FileUtil.instance.getNewFile(recordVideoViewModel.videoManagerData.value?.applyId+"_"+recordVideoViewModel.videoManagerData.value?.questionId, requireContext()))

    }


    private fun initializeUI() {
        recordVideoViewModel.prepareData(questionListViewModel.videoManagerData.value)
        tv_question_heading?.text = "Question ${questionListViewModel.videoManagerData.value?.questionSerial} of ${questionListViewModel.videoManagerData.value?.totalQuestion}"
        tv_question_details?.text = "${questionListViewModel.videoManagerData.value?.questionText}"
        tv_time_value?.text = "${questionListViewModel.videoManagerData.value?.questionDuration?.toFormattedSeconds()}"
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
                    recordVideoViewModel.videoManagerData.value?.file = result.file
                    recordVideoViewModel.uploadSingleVideoToServer(recordVideoViewModel.videoManagerData.value)
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
            tv_question_details?.hide()
            view_intermediate?.hide()
            cl_total_time?.hide()
            btn_record_video?.hide()
            view_timeline?.show()
            cl_timeline?.show()

            tv_rec?.show()

            seekbar_video_duration.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    return true
                }
            })

            tool_bar?.title = "Recording Question ${recordVideoViewModel.videoManagerData.value?.questionSerial}"

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

    override fun videoRecordSuccess(file: File) {

        if (recordVideoViewModel.onVideoDoneEvent.value == true) {
            recordVideoViewModel.videoManagerData.value?.file = file
           // recordVideoViewModel.uploadSingleVideoToServer(recordVideoViewModel.videoManagerData.value)
            showSnackbar()
            Log.e("factory", "videoRecordSuccess"+file)
        }
    }

    override fun videoRecordFailed(message: String) {

    }
}