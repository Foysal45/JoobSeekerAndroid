package com.bdjobs.app.videoResume.ui.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.MediaController
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.databinding.FragmentViewVideoResumeBinding
import com.bdjobs.app.editResume.EditResLandingActivity
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
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.alert


class ViewVideoResumeFragment : Fragment() {


    val args: ViewVideoResumeFragmentArgs by navArgs()
    private val videoResumeQuestionsViewModel: VideoResumeQuestionsViewModel by navGraphViewModels(R.id.videoResumeQuestionsFragment)
    private val viewVideoResumeViewModel: ViewVideoResumeViewModel by viewModels { ViewModelFactoryUtil.provideVideoResumeViewVideoViewModelFactory(this) }

    lateinit var binding: FragmentViewVideoResumeBinding
    private lateinit var session: BdjobsUserSession

    lateinit var medialController : MediaController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_video_resume, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = BdjobsUserSession(requireContext())

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        tool_bar?.setupWithNavController(navController, appBarConfiguration)

        medialController = MediaController(requireContext())


        btn_record_again?.setOnClickListener {
            findNavController().navigate(ViewVideoResumeFragmentDirections.actionViewVideoResumeFragmentToRecordVideoResumeFragment())
        }

        btn_delete_video?.setOnClickListener {

            try {

                val dialog = Dialog(this.requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.layout_video_resume_delete_popup)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val deleteTV = dialog.findViewById<TextView>(R.id.deleteTV)
                val noBTN = dialog.findViewById<Button>(R.id.btn_delete_video_no)
                val yesBTN = dialog.findViewById<Button>(R.id.btn_delete_video_yes)

                if(session.videoResumeTotalAnswered!!.toInt() < session.videoResumeThreshold!!.toInt()){
                    deleteTV.text = "If you delete it, employers won't be able to view your video. Do you want to delete this video?"
                }else {
                    deleteTV.text = "Are you sure you want to delete the video?"

                }
                yesBTN?.setOnClickListener {
                    viewVideoResumeViewModel.onDeleteResumeButtonClick()
                    dialog.dismiss()
                }

                noBTN?.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            } catch (e: Exception) {
                logException(e)
            }


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

        tv_question_bn.text = args.question
        tv_video_duration.hide()
//        tv_video_duration.text = videoResumeQuestionsViewModel.videoResumeManagerData.value?.questionDuration

        try {
            video_view?.setVideoURI(Uri.parse(args.url))
        } catch (e: Exception) {
            e.printStackTrace()
            video_view?.stopPlayback()
        }

        img_play?.setOnClickListener {
            img_play?.hide()
            progress_bar?.show()
            tv_video_duration.hide()
            tv_question_bn.hide()
            video_view?.start()
        }

        video_view?.setOnPreparedListener { //Log.d("rakib", "on Prepared called")
            progress_bar?.hide()
            video_view?.setMediaController(medialController)
            medialController.setAnchorView(video_view)
        }

        video_view?.setOnInfoListener { _, what, _ ->
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