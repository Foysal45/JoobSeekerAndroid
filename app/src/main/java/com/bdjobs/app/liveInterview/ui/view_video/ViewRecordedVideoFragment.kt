package com.bdjobs.app.liveInterview.ui.view_video

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.liveInterview.ui.record_video.VideoRecordViewModel
import kotlinx.android.synthetic.main.fragment_view_recorded_video.*
import timber.log.Timber


class ViewRecordedVideoFragment : Fragment() {

//
//    val args:ViewRecordedVideoFragmentArgs by navArgs()
    private lateinit var mediaController: MediaController
    private val videoRecordViewModel : VideoRecordViewModel by navGraphViewModels(R.id.recordVideoFragment)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Timber.d("Create View")
        return inflater.inflate(R.layout.fragment_view_recorded_video, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Timber.d("Activity Created")

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        tool_bar?.setupWithNavController(navController, appBarConfiguration)
        tool_bar.title = "Test Recording"

        mediaController = MediaController(requireContext())

//        btn_record_again?.setOnClickListener {
//            findNavController().popBackStack()
//        }

        showVideo()
    }

    private fun showVideo() {
        val videoData = videoRecordViewModel.videoManagerData.value

        Timber.d("VideoData: Path: ${videoData?.path}")

        try {
            video_view?.setVideoPath(videoData?.file?.path)
        } catch (e: Exception) {
            e.printStackTrace()
            video_view?.stopPlayback()
            Timber.e("Error: ${e.localizedMessage}")
        }

        img_play?.setOnClickListener {
            img_play?.hide()
            progress_bar?.show()
            video_view?.start()
        }

        video_view?.setOnPreparedListener {
            progress_bar?.hide()
            video_view?.setMediaController(mediaController)
            mediaController.setAnchorView(video_view)
        }

        video_view?.setOnInfoListener { mp, what, extra ->
            when (what) {
                MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                    progress_bar?.show()
                    return@setOnInfoListener true
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                    progress_bar?.hide()
                    return@setOnInfoListener true
                }
            }
            return@setOnInfoListener false
        }
    }

//    override fun onResume() {
//        super.onResume()
//
//
//    }

    override fun onPause() {
        super.onPause()
        video_view.stopPlayback()
    }

}