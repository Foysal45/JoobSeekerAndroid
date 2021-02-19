package com.bdjobs.app.videoResume.ui.view

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.databinding.FragmentViewVideoResumeBinding
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import com.bdjobs.app.videoResume.ui.questions.VideoResumeQuestionsViewModel
import kotlinx.android.synthetic.main.fragment_view_video.*
import kotlinx.android.synthetic.main.fragment_view_video_resume.*
import kotlinx.android.synthetic.main.fragment_view_video_resume.btn_record_again
import kotlinx.android.synthetic.main.fragment_view_video_resume.img_play
import kotlinx.android.synthetic.main.fragment_view_video_resume.progress_bar
import kotlinx.android.synthetic.main.fragment_view_video_resume.tool_bar
import kotlinx.android.synthetic.main.fragment_view_video_resume.video_view


class ViewVideoResumeFragment : Fragment() {


    val args: ViewVideoResumeFragmentArgs by navArgs()
    private val videoResumeQuestionsViewModel: VideoResumeQuestionsViewModel by navGraphViewModels(R.id.videoResumeQuestionsFragment)
    private val viewVideoResumeViewModel: ViewVideoResumeViewModel by viewModels { ViewModelFactoryUtil.provideVideoResumeViewVideoViewModelFactory(this) }

    lateinit var binding: FragmentViewVideoResumeBinding

    lateinit var medialController : MediaController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_video_resume, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        tool_bar?.setupWithNavController(navController, appBarConfiguration)

        medialController = MediaController(requireContext())


        btn_record_again?.setOnClickListener {
            findNavController().navigate(ViewVideoResumeFragmentDirections.actionViewVideoResumeFragmentToRecordVideoResumeFragment())
        }

        btn_delete_video?.setOnClickListener {
            viewVideoResumeViewModel.onDeleteResumeButtonClick()
        }

        viewVideoResumeViewModel.apply {

            prepareData(videoResumeQuestionsViewModel.videoResumeManagerData.value)

            onDeleteDoneEvent.observe(viewLifecycleOwner, EventObserver {
                findNavController().popBackStack()
            })
        }
    }


    override fun onPause() {
        super.onPause()
        video_view?.stopPlayback()
    }

    override fun onResume() {
        super.onResume()

        try {
            video_view?.setVideoURI(Uri.parse(args.url))
        } catch (e: Exception) {
            e.printStackTrace()
            video_view?.stopPlayback()
        }

        img_play?.setOnClickListener {
            img_play?.hide()
            progress_bar?.show()
            video_view?.start()
        }

        video_view?.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
            override fun onPrepared(mp: MediaPlayer?) {
                //Log.d("rakib", "on Prepared called")
                progress_bar?.hide()
                video_view?.setMediaController(medialController)
                medialController.setAnchorView(video_view)
            }
        })

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

}