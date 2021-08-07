package com.bdjobs.app.videoInterview.ui.view_video

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.videoInterview.ui.question_list.QuestionListViewModel
import kotlinx.android.synthetic.main.fragment_view_video.*

class ViewVideoFragment : Fragment() {

    val args: ViewVideoFragmentArgs by navArgs()
    private val questionListViewModel: QuestionListViewModel by navGraphViewModels(R.id.questionListFragment)

    lateinit var medialController : MediaController


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

        medialController = MediaController(requireContext())

        btn_record_again?.setOnClickListener {
            findNavController().navigate(ViewVideoFragmentDirections.actionViewVideoFragmentToRecordViedeoFragment())
        }
    }

    private fun setupData() {
        try {

            val canAttempt = questionListViewModel.questionCommonData.value?.totalAttempt!!.toInt() - questionListViewModel.questionCommonData.value?.remainingExtraAttempt!!.toInt()
            if(canAttempt >0){
                btn_record_again?.show()
            }else btn_record_again?.hide()


            val extraAttempts = if (questionListViewModel.questionCommonData.value?.remainingExtraAttempt!!.toInt() >= questionListViewModel.questionCommonData.value?.totalAttempt!!.toInt())
                questionListViewModel.questionCommonData.value?.totalAttempt!!.toInt()
            else
                questionListViewModel.questionCommonData.value?.remainingExtraAttempt!!.toInt()

            tv_extra_attempts_value?.text = "$extraAttempts/${questionListViewModel.questionCommonData.value?.totalAttempt}"

            if (questionListViewModel.questionCommonData.value!!.submissionDate == "") {
                cl_submission_date?.hide()
            } else {
                cl_submission_date?.show()
                tv_submission_date?.text = HtmlCompat.fromHtml(getString(R.string.submission_info, questionListViewModel.questionCommonData.value!!.submissionDate), HtmlCompat.FROM_HTML_MODE_COMPACT)
                btn_record_again?.hide()
                cl_extra_attempts?.hide()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onPause() {
        super.onPause()
        video_view?.stopPlayback()
    }

    override fun onResume() {
        super.onResume()
        setupData()

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