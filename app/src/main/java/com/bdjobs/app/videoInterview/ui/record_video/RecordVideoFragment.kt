package com.bdjobs.app.videoInterview.ui.record_video

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.Utilities.toFormattedSeconds
import com.bdjobs.app.databinding.FragmentRecordVideoBinding
import com.bdjobs.app.videoInterview.ui.question_list.QuestionListViewModel
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraOptions
import kotlinx.android.synthetic.main.fragment_record_video.*

class RecordVideoFragment : Fragment() {

    private val questionListViewModel: QuestionListViewModel by navGraphViewModels(R.id.questionListFragment)
    private val recordVideoViewModel: RecordVideoViewModel by viewModels { ViewModelFactoryUtil.provideVideoInterviewRecordVideoViewModelFactory(this) }
    lateinit var binding: FragmentRecordVideoBinding


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

        initializeCamera()

        initializeUI()

        recordVideoViewModel.apply {
            onVideoRecordingStartedEvent.observe(viewLifecycleOwner, EventObserver {
                updateUI(it)
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
        }
    }


    private fun initializeUI() {
        recordVideoViewModel.prepareData(questionListViewModel.videoManagerData.value)
        tv_question_heading?.text = "Question ${questionListViewModel.videoManagerData.value?.questionSerial} of ${questionListViewModel.videoManagerData.value?.totalQuestion}"
        tv_question_details?.text = "${questionListViewModel.videoManagerData.value?.questionText}"
        tv_time_value?.text = "${questionListViewModel.videoManagerData.value?.questionDuration?.toFormattedSeconds()}"
    }

    private fun initializeCamera() {
        camera_view?.setLifecycleOwner(viewLifecycleOwner)

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

        })
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

        } else {

        }
    }
}