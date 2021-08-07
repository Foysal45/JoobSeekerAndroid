package com.bdjobs.app.liveInterview.ui.record_video

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.Utilities.toFormattedSeconds
import com.bdjobs.app.Web.WebActivity
import com.bdjobs.app.databinding.FragmentVideoRecordBinding
import com.bdjobs.app.liveInterview.SharedViewModel
import com.bdjobs.app.videoInterview.util.EventObserver
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Facing
import kotlinx.android.synthetic.main.fragment_video_record.*
import org.jetbrains.anko.startActivity
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class RecordVideoFragment : Fragment() {

    private lateinit var binding: FragmentVideoRecordBinding

    lateinit var videoFile: File
    private val videoRecordViewModel: VideoRecordViewModel by navGraphViewModels(R.id.recordVideoFragment)

    private val sharedViewModel : SharedViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentVideoRecordBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = videoRecordViewModel
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.toolBar.setupWithNavController(navController, appBarConfiguration)
        binding.toolBar.title = "Test Recording"

        initializeCamera()

        initializeUI()

        setUpObservers()

        binding.btnGuide.setOnClickListener {
            context?.startActivity<WebActivity>("url" to "https://mybdjobs.bdjobs.com/mybdjobs/UserGuideFor_LiveInterview.asp", "from" to "liveInterview")
        }

    }

    private fun setUpObservers() {
        videoRecordViewModel.apply {
            onVideoRecordingStartedEvent.observe(viewLifecycleOwner, EventObserver {
                updateUI(it)
                captureVideo()
            })

            progressPercentage.observe(viewLifecycleOwner, {
                seekbar_video_duration.progress = it.toInt()
            })
            elapsedTimeInString.observe(viewLifecycleOwner, {
                    tv_time_remaining_value?.text = it
                }
            )

            onVideoDoneEvent.observe(viewLifecycleOwner, {
                // if (it){
                camera_view?.close()
                // }
            })

            /*navigateToViewVideoEvent.observe(viewLifecycleOwner, Observer {
                Timber.d("Should I navigate: $it")

                if (binding.cameraView.isOpened) binding.cameraView.close()

                findNavController().navigate(RecordVideoFragmentDirections.actionRecordVideoFragmentToViewRecordedVideoFragment())
            })*/
        }
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
            }

            override fun onVideoTaken(result: VideoResult) {
                Timber.d("Video Taken")
                super.onVideoTaken(result)
                if (videoRecordViewModel.onVideoDoneEvent.value == true) {
                    videoFile = result.file
                    Timber.d("Path: ${videoFile.path} :: AbsolutePath: ${videoFile.absolutePath}")
                    sharedViewModel.storeVideoFile(videoFile)
                    findNavController().navigate(RecordVideoFragmentDirections.actionRecordVideoFragmentToViewRecordedVideoFragment())
//                    navigateToVideoView(videoFile)

                }

            }

        })
    }

    @SuppressLint("SetTextI18n")
    private fun initializeUI() {
        binding.tvTimeValue.text = VideoRecordViewModel.TOTAL_TIME.toFormattedSeconds()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun updateUI(recordingStarted: Boolean) {
        if (recordingStarted) {
            binding.apply {
                viewIntermediate.hide()
                clTotalTime.hide()
                btnRecordVideo.hide()
                viewTimeline.hide()
                clTimeline.show()
                btnStopVideo.show()
                tvRec.show()

                seekbarVideoDuration.setOnTouchListener { _, _ -> true }

                toolBar.title = "Test Recording"
            }

//            view_intermediate?.hide()
//            cl_total_time?.hide()
//            btn_record_video?.hide()
//            view_timeline?.show()
//            cl_timeline?.show()
//            btn_stop_video.show()
//
//            tv_rec?.show()
//
//            seekbar_video_duration.setOnTouchListener { _, _ -> true }
//
//            tool_bar?.title = "Test Recording"

        }
    }

    private fun captureVideo() {
        val dir = File(requireContext().getExternalFilesDir(null)!!.absoluteFile, "live_interview")
        dir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val newFile = File(dir.path + File.separator + "bdjobs_testRecording_$timeStamp.mp4")
        camera_view?.takeVideoSnapshot(newFile)
    }



}