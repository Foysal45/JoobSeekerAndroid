package com.bdjobs.app.videoInterview.ui.question_list

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.databinding.FragmentQuestionDetailsBinding
import com.bdjobs.app.videoInterview.ui.interview_details.VideoInterviewDetailsViewModel
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import com.fondesa.kpermissions.*
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_question_details.*
import kotlinx.android.synthetic.main.fragment_question_details.tool_bar
import kotlinx.android.synthetic.main.fragment_video_interview_details.*
import timber.log.Timber

class QuestionListFragment : Fragment() {

    private val args: QuestionListFragmentArgs by navArgs()
    private val questionListViewModel: QuestionListViewModel by viewModels { ViewModelFactoryUtil.provideVideoInterviewQuestionListViewModelFactory(this) }
    private val questionDetailsViewModel : VideoInterviewDetailsViewModel by navGraphViewModels(R.id.videoInterviewDetailsFragment)
    lateinit var binding: FragmentQuestionDetailsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentQuestionDetailsBinding.inflate(inflater).apply {
            viewModel = questionListViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        tool_bar?.setupWithNavController(navController, appBarConfiguration)


        questionListViewModel.getQuestionList(questionDetailsViewModel.jobId.value,questionDetailsViewModel.applyId.value)

        val adapter = QuestionListAdapter(requireContext(), ClickListener {
            askForPermission()
        })

        rv_question?.adapter = adapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(rv_question)

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rv_question?.layoutManager = layoutManager

        rv_question?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val snapPosition = (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                Log.d("rakib", "snap position $snapPosition")
                updateStepperText(snapPosition)
                when (snapPosition) {
                    0 -> {
                        img_previous_question?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_previous_question_grey)
                        img_previous_question?.isEnabled = false
                    }
                    adapter.itemCount - 1 -> {
                        img_next_question?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_next_question_grey)
                        img_next_question?.isEnabled = false
                    }
                    else -> {
                        img_previous_question?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_previous_question_black)
                        img_next_question?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_next_question_black)
                        img_previous_question?.isEnabled = true
                        img_next_question?.isEnabled = true
                    }
                }
            }
        })

        questionListViewModel.apply {

            questionListData.observe(viewLifecycleOwner, Observer {
                it?.let { updateSteppers(it.size) }
                updateIndicators()
                adapter.submitList(it)
                Log.d("rakib", "${adapter.itemCount}")

            })

            onSubmitButtonClickEvent.observe(viewLifecycleOwner, EventObserver { notInterested ->
                if (notInterested) {
                    openWarningDialog()
                } else {

                }
            })

            onNextQuestionClickEvent.observe(viewLifecycleOwner, EventObserver {
                rv_question?.smoothScrollToPosition(layoutManager.findFirstCompletelyVisibleItemPosition() + 1)
            })

            onPreviousQuestionClickEvent.observe(viewLifecycleOwner, EventObserver {
                rv_question?.smoothScrollToPosition(layoutManager.findFirstCompletelyVisibleItemPosition() - 1)
            })

        }

        binding.btnSubmitLater.setOnClickListener {
            askForPermission()
        }
    }

    private fun updateStepperText(position : Int) {
        when(position){
            0 ->{
                tv_q1?.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorBlack))

                tv_q2?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
                tv_q3?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
                tv_q4?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
                tv_q5?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
            }

            1 -> {
                tv_q2?.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorBlack))

                tv_q1?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
                tv_q3?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
                tv_q4?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
                tv_q5?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
            }

            2->{
                tv_q3?.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorBlack))

                tv_q1?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
                tv_q2?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
                tv_q4?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
                tv_q5?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
            }

            3 ->{
                tv_q4?.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorBlack))

                tv_q1?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
                tv_q2?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
                tv_q3?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
                tv_q5?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
            }

            4 ->{
                tv_q5?.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorBlack))

                tv_q1?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
                tv_q2?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
                tv_q3?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
                tv_q4?.setTextColor(ContextCompat.getColor(requireContext(),R.color.inactive_question_text_color))
            }
        }

    }

    private fun updateIndicators() {
        img_previous_question?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_previous_question_grey)
        img_previous_question?.isEnabled = false
        img_next_question?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_next_question_black)
        img_next_question?.isEnabled = true
    }

    private fun updateSteppers(totalQuestions: Int) {
        when (totalQuestions) {
            4 -> {
                line_view_question5?.hide()
                img_question5?.hide()
                tv_q5?.hide()
            }
            3->{
                line_view_question5?.hide()
                img_question5?.hide()
                tv_q5?.hide()

                line_view_question4?.hide()
                img_question4?.hide()
                tv_q4.hide()
            }
            2 ->{
                line_view_question5?.hide()
                img_question5?.hide()
                tv_q5?.hide()

                line_view_question4?.hide()
                img_question4?.hide()
                tv_q4.hide()

                line_view_question3?.hide()
                img_question3?.hide()
                tv_q3.hide()
            }
            1 ->{
                line_view_question5?.hide()
                img_question5?.hide()
                tv_q5?.hide()

                line_view_question4?.hide()
                img_question4?.hide()
                tv_q4.hide()

                line_view_question3?.hide()
                img_question3?.hide()
                tv_q3.hide()

                line_view_question2?.hide()
                img_question2?.hide()
                tv_q2.hide()
            }
        }
    }

    private fun openWarningDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext()).create()
        val view = layoutInflater.inflate(R.layout.dialog_video_not_interested_to_submit, null)
        view?.apply {
            findViewById<Button>(R.id.dialog_btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            findViewById<Button>(R.id.dialog_btn_yes).setOnClickListener {
                questionListViewModel.onDialogYesButtonClick()
                dialog.dismiss()
            }
        }
        dialog.setView(view)
        dialog.show()
    }


    private fun askForPermission() {
        permissionsBuilder(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).build().send { result ->
            when {
                result.allGranted() -> {
                    Timber.d("Granted")
                    findNavController().navigate(R.id.recordViedeoFragment)
                }
                result.allDenied() || result.anyDenied() -> {
                    //Toast.makeText(context,"Please enable this permission to record answer(s)",Toast.LENGTH_SHORT).show()
                    openSettingsDialog()

                }

                result.allPermanentlyDenied() || result.anyPermanentlyDenied() -> {
                    Log.d("rakib", "permanently denied")
                    openSettingsDialog()
                    //openSettingsDialog()
                }
            }
        }
    }

    private fun openSettingsDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext()).create()
        val view = layoutInflater.inflate(R.layout.dialog_enable_video_permissions, null)
        view?.apply {
            findViewById<Button>(R.id.dialog_btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            findViewById<Button>(R.id.dialog_btn_go_to_settings).setOnClickListener {
                val intent = createAppSettingsIntent()
                startActivity(intent)
                dialog.dismiss()
            }
        }
        dialog.setView(view)
        dialog.show()
    }

    private fun createAppSettingsIntent() = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", context?.packageName, null)
    }


}