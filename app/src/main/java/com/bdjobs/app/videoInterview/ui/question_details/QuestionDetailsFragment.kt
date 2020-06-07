package com.bdjobs.app.videoInterview.ui.question_details

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentQuestionDetailsBinding
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import com.fondesa.kpermissions.*
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber

class QuestionDetailsFragment : Fragment() {

    private val questionDetailsViewModel: QuestionDetailsViewModel by viewModels { ViewModelFactoryUtil.provideVideoInterviewQuestionDetailsViewModelFactory(this) }
    lateinit var binding: FragmentQuestionDetailsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentQuestionDetailsBinding.inflate(inflater).apply {
            viewModel = questionDetailsViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        questionDetailsViewModel.apply {
            onSubmitButtonClickEvent.observe(viewLifecycleOwner, EventObserver { notInterested ->
                if (notInterested) {
                    openWarningDialog()
                } else {

                }
            })
        }

        binding.btnSubmitLater.setOnClickListener {
            askForPermission()
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
                questionDetailsViewModel.onDialogYesButtonClick()
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