package com.bdjobs.app.videoInterview.ui.view_video

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_record_video.*
import kotlinx.android.synthetic.main.fragment_view_video.*
import kotlinx.android.synthetic.main.fragment_view_video.tool_bar

class ViewVideoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        tool_bar?.setupWithNavController(navController, appBarConfiguration)

        val medialController = MediaController(requireContext())

        video_view?.setVideoURI(Uri.parse("https://vdo.bdjobs.com/Videos/Corporate//854375/182982535/182982535_3.webm"))

        img_play?.setOnClickListener {
            img_play?.hide()
            progress_bar?.show()
            video_view?.start()
        }

        video_view?.setOnPreparedListener(object : MediaPlayer.OnPreparedListener{
            override fun onPrepared(mp: MediaPlayer?) {
                Log.d("rakib","on Prepared called")
                progress_bar?.hide()
                video_view?.setMediaController(medialController)
                medialController.setAnchorView(video_view)
            }
        })

        video_view?.setOnInfoListener { mp, what, extra ->
            when(what){
                MediaPlayer.MEDIA_INFO_BUFFERING_START->{
                    progress_bar?.show()
                    return@setOnInfoListener true
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_END->{
                    progress_bar?.hide()
                    return@setOnInfoListener true
                }
            }
            return@setOnInfoListener false
        }
    }
}