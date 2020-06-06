package com.bdjobs.app.videoInterview.ui.record_video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.databinding.FragmentRecordVideoBinding
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import kotlinx.android.synthetic.main.fragment_record_video.*
import kotlinx.android.synthetic.main.fragment_record_video.btn_record_video
import kotlinx.android.synthetic.main.fragment_record_video.tv_question_heading

class RecordVideoFragment : Fragment() {

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

        recordVideoViewModel.apply {
            onVideoRecordingStartedEvent.observe(viewLifecycleOwner, EventObserver {
                updateUI(it)
            })
            progressPercentage.observe(viewLifecycleOwner, Observer {
                seekbar_video_duration.progress = it.toInt()
            })
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

        } else {

        }
    }
}