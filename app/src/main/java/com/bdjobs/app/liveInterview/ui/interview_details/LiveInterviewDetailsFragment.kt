package com.bdjobs.app.liveInterview.ui.interview_details

import android.Manifest
import android.app.Application
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentLiveInterviewDetailsBinding
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository
import com.bdjobs.app.videoInterview.util.EventObserver
import com.fondesa.kpermissions.*
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.fragment_live_interview_details.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.toast
import timber.log.Timber
import kotlin.collections.ArrayList

class LiveInterviewDetailsFragment : Fragment() {

    var eventID: Long? = 0

    private var cameraAndAudioPermissionGranted: Boolean = false

    val calendarInfos = arrayListOf<String>()
    val primaryCalendarInfos = arrayListOf<String>()

//    private val balloon by lazy { BalloonFactory().create(context = requireContext(), lifecycle = viewLifecycleOwner) }

    var snackbar: Snackbar? = null

    private val args: LiveInterviewDetailsFragmentArgs by navArgs()

    private val liveInterviewDetailsViewModel: LiveInterviewDetailsViewModel by viewModels {
        LiveInterviewDetailsViewModelFactory(
                LiveInterviewRepository(requireActivity().application as Application),
                requireContext().contentResolver,
                args.jobId
        )
    }

    lateinit var binding: FragmentLiveInterviewDetailsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentLiveInterviewDetailsBinding.inflate(inflater).apply {
            viewModel = liveInterviewDetailsViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        tool_bar?.setupWithNavController(navController, appBarConfiguration)

        tool_bar?.title = args.jobTitle

        val adapter = LiveInterviewDetailsAdapter(requireContext(), ClickListener {
            liveInterviewDetailsViewModel.apply {
                liveInterviewDetailsViewModel.onChangeButtonClick()
            }
        })

        rv_live_interview_details?.adapter = adapter

        Timber.tag("live").d("Load Live Detail View")

        liveInterviewDetailsViewModel.apply {

            liveInterviewDetailsData.observe(viewLifecycleOwner, {
                Timber.tag("live").d("API called for liveInterviewDetailsData")
                adapter.submitList(it)
            })

            openNoPopup.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    openCancelPopup()
                }
            })

            openReschedulePopup.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    openReschedulePopup()
                }
            })

            showToast.observe(viewLifecycleOwner, EventObserver {
                if (it.isNotEmpty())
                    requireActivity().toast(it)
            })

            addToCalendarClickEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    askForPermission()
                }
            })

            onAddedToCalendarEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    btn_add_to_calendar?.apply {
                        text = "Added to Calendar"
                        isEnabled = false
                    }
                }
            })

            takePreparationClickEvent.observe(viewLifecycleOwner,EventObserver{
                if (it) {
                    showPreparationDialog()
                }
            })

            joinInterviewClickEvent.observe(viewLifecycleOwner,EventObserver{
                if (it) {
                    // do work here
                    askForCameraAndAudioPermission("session")
                }
            })
        }

        btn_job_detail?.setOnClickListener {
            val jobids = ArrayList<String>()
            val lns = ArrayList<String>()
            val deadline = ArrayList<String>()
            jobids.add(args.jobId)
            lns.add("0")
            deadline.add("")
            startActivity<JobBaseActivity>("from" to "employer", "jobids" to jobids, "lns" to lns, "position" to 0, "deadline" to deadline)
        }
    }

    private fun showPreparationDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_preparation)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val recordVideoBtn:MaterialButton = dialog.findViewById(R.id.btn_record_video_prep)
        val recordAudioBtn:MaterialButton = dialog.findViewById(R.id.btn_record_audio_prep)
        val cancelText:MaterialTextView = dialog.findViewById(R.id.tv_cancel_prep)

        recordVideoBtn.setOnClickListener {
            askForCameraAndAudioPermission("video")
            dialog.dismiss()
        }

        recordAudioBtn.setOnClickListener {
            askForCameraAndAudioPermission("audio")
            dialog.dismiss()
        }


        cancelText.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun openCancelPopup() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded).create()
        val view = layoutInflater.inflate(R.layout.job_invitaion_cancel_pop_up_layout, null)

        val reasonRG = view.findViewById(R.id.reasonRG) as RadioGroup
        val closeIMGV = view.findViewById(R.id.closeIMGV) as ImageView
        val otherReasonET = view.findViewById(R.id.otherReasonET) as EditText
        val submitBTN = view.findViewById(R.id.submitTV) as Button


        closeIMGV.setOnClickListener {
            try {
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        reasonRG.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.reason1RB -> {
                    otherReasonET.visibility = View.GONE
                }
                R.id.reason2RB -> {
                    otherReasonET.visibility = View.GONE
                }
                R.id.reason3RB -> {
                    otherReasonET.visibility = View.GONE
                }
                R.id.reason4RB -> {
                    otherReasonET.visibility = View.VISIBLE
                }
            }
        }

        submitBTN.setOnClickListener {
            var otherReason = ""
            var selectedReason = ""
            when (reasonRG.checkedRadioButtonId) {
                R.id.reason1RB -> {
                    selectedReason = "1"
                }
                R.id.reason2RB -> {
                    selectedReason = "2"
                }
                R.id.reason3RB -> {
                    selectedReason = "3"
                }
                R.id.reason4RB -> {
                    selectedReason = "4"
                    otherReason = otherReasonET.text.toString()
                }
            }

            if (TextUtils.isEmpty(selectedReason)) {
                Toast.makeText(activity, "Please select an option from the list.", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(otherReason) && selectedReason.equals("4", ignoreCase = true)) {
                Toast.makeText(activity, "Please write your reason briefly.", Toast.LENGTH_SHORT).show()
            } else if (selectedReason.equals("4", ignoreCase = true) && otherReason.trim { it <= ' ' }
                    .isEmpty()) {
                Toast.makeText(activity, "Please write your reason briefly.", Toast.LENGTH_SHORT).show()
            } else {
                liveInterviewDetailsViewModel.apply {
                    cancelReason = selectedReason
                    this.otherReason = otherReason
                    onCancelSubmitButtonClick()
                }
                dialog.dismiss()
            }
        }

        dialog.setView(view)
        dialog.show()
    }

    private fun openReschedulePopup() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded).create()
        val view = layoutInflater.inflate(R.layout.job_invitaion_reschedule_pop_up_layout, null)

        val closeIMGV = view.findViewById(R.id.closeIMGV) as ImageView
        val otherReasonET = view.findViewById(R.id.otherReasonET) as EditText
        val submitTV = view.findViewById(R.id.submitTV) as Button

        closeIMGV.setOnClickListener {
            try {
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        submitTV.setOnClickListener {
            val reason = otherReasonET.text.toString()
            when {
                TextUtils.isEmpty(reason) -> Toast.makeText(activity, "Please write your reason for rescheduling.", Toast.LENGTH_SHORT).show()
                reason.trim { it <= ' ' }.isEmpty() -> Toast.makeText(activity, "Please write your reason for rescheduling.", Toast.LENGTH_SHORT).show()
                else -> {

                    liveInterviewDetailsViewModel.apply {
                        rescheduleComment = reason
                        onRescheduleSubmitClick()
                    }
                    dialog.dismiss()
//                    sendInterviewConfirmation(
//                            applyID = applyID,
//                            userActivity = "4",
//                            invitationID = invitationID,
//                            rescheduleComment = reason
//                    )
                }
            }
        }
        dialog.setView(view)
        dialog.show()
    }

    private fun askForPermission() {

        Timber.d("Asking for permission")
        permissionsBuilder(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR).build().send { result ->
            when {
                result.allGranted() -> {
                    Timber.d("All granted!!")
                    liveInterviewDetailsViewModel.insert()
                }
                result.allDenied() || result.anyDenied() -> {
                    Timber.d("Denied!!")
                }

                result.allPermanentlyDenied() || result.anyPermanentlyDenied() -> {
                    Timber.d("Permanently denied!!")
                }
            }
        }
    }

    private fun askForCameraAndAudioPermission(from:String):Boolean {
        permissionsBuilder(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).build().send { result ->
            when {
                result.allGranted() -> {
                    cameraAndAudioPermissionGranted = true
                    when (from) {
                        "video" -> findNavController().navigate(LiveInterviewDetailsFragmentDirections.actionLiveInterviewDetailsFragmentToRecordVideoFragment())
                        "session" -> findNavController().navigate(LiveInterviewDetailsFragmentDirections.actionLiveInterviewDetailsFragmentToInterviewSessionFragment(liveInterviewDetailsViewModel.jobId,args.jobTitle, liveInterviewDetailsViewModel.processID.value,liveInterviewDetailsViewModel.applyId,liveInterviewDetailsViewModel.commonData.value?.companyName))
                        else -> findNavController().navigate(LiveInterviewDetailsFragmentDirections.actionLiveInterviewDetailsFragmentToAudioRecordFragment())
                    }

                }
                result.allDenied() || result.anyDenied() -> {
                    openSettingsDialog()
                }

                result.allPermanentlyDenied() || result.anyPermanentlyDenied() -> {
                    openSettingsDialog()
                }
            }
        }
        return cameraAndAudioPermissionGranted
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

    override fun onPause() {
        super.onPause()
        snackbar?.dismiss()
    }

}