package com.bdjobs.app.liveInterview.ui.interview_details

import android.Manifest
import android.app.Application
import android.app.Dialog
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.BalloonFactory
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
import kotlinx.android.synthetic.main.dialog_preparation.*
import kotlinx.android.synthetic.main.fragment_live_interview_details.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

private const val PROJECTION_ID_INDEX: Int = 0
private const val PROJECTION_ACCOUNT_NAME_INDEX: Int = 1
private const val PROJECTION_DISPLAY_NAME_INDEX: Int = 2
private const val PROJECTION_OWNER_ACCOUNT_INDEX: Int = 3

class LiveInterviewDetailsFragment : Fragment() {

    var eventID: Long? = 0

    private var cameraAndAudioPermissionGranted: Boolean = false

    private val EVENT_PROJECTION: Array<String> = arrayOf(
            CalendarContract.Calendars._ID,                     // 0
            CalendarContract.Calendars.ACCOUNT_NAME,            // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,   // 2
            CalendarContract.Calendars.OWNER_ACCOUNT            // 3
    )

    // The indices for the projection array above.

    val calendarInfos = arrayListOf<String>()
    val primaryCalendarInfos = arrayListOf<String>()

    private val balloon by lazy { BalloonFactory().create(context = requireContext(), lifecycle = viewLifecycleOwner) }

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
                              savedInstanceState: Bundle?): View? {
        binding = FragmentLiveInterviewDetailsBinding.inflate(inflater).apply {
            viewModel = liveInterviewDetailsViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //getAllCalendars()

//        getPrimaryCalendar()

//        modifyCalendar()

        //insertEvent()

        //sEventAlreadyExist()

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

        liveInterviewDetailsViewModel.apply {

            liveInterviewDetailsData.observe(viewLifecycleOwner, Observer {
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

//            showUndoSnackbar.observe(viewLifecycleOwner, EventObserver {
//                if (it) {
//                    snackbar = Snackbar.make(cl_root, "Your request has been sent to the employer.", Snackbar.LENGTH_INDEFINITE).apply {
//                        setAction("Undo") {
//                            Timber.d("undo clicked")
//                            onUndoButtonClick()
//                        }.show()
//                    }
//                } else {
//                    snackbar?.dismiss()
//                }
//            })

            showTooltip.observe(viewLifecycleOwner, EventObserver {
                if (it)
                    try {
                        balloon.showAlignTop(img_web_info)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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
//                        icon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_check_circle_black_14dp)
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

        img_web_info?.setOnClickListener {
            try {
                balloon.showAlignTop(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        btn_job_detail?.setOnClickListener {
            val jobids = ArrayList<String>()
            val lns = ArrayList<String>()
            val deadline = ArrayList<String>()
            jobids.add(args.jobId!!)
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

    private fun getAllCalendars() {
        doAsync {
            val contentResolver = requireContext().contentResolver
            val selection = CalendarContract.Calendars.VISIBLE + " = 1 AND " + CalendarContract.Calendars.IS_PRIMARY + "=1"
            val cur = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, EVENT_PROJECTION, null, null, null)

            if (cur != null) {
                while (cur.moveToNext()) {
                    var calID: Long = 0
                    var displayName = ""
                    var accountName = ""
                    var ownerName = ""
                    // Get the field values
                    calID = cur.getLong(PROJECTION_ID_INDEX)
                    displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX)
                    accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX)
                    ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX)
                    val calendarInfo = String.format("Calendar ID: %s\nDisplay Name: %s\nAccount Name: %s\nOwner Name: %s", calID, displayName, accountName, ownerName)
                    calendarInfos.add(calendarInfo)
                }
            } else {
                Timber.d("cursor null")
            }
            cur?.close()
        }
    }

    private fun getPrimaryCalendar() {
        doAsync {
            val contentResolver = requireContext().contentResolver
            val selection = CalendarContract.Calendars.VISIBLE + " = 1 AND " + CalendarContract.Calendars.IS_PRIMARY + "=1"
            val cur = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, EVENT_PROJECTION, selection, null, null)

            if (cur != null) {
                while (cur.moveToNext()) {
                    var calID: Long = 0
                    var displayName = ""
                    var accountName = ""
                    var ownerName = ""
                    // Get the field values
                    calID = cur.getLong(PROJECTION_ID_INDEX)
                    displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX)
                    accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX)
                    ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX)
                    val calendarInfo = String.format("Calendar ID: %s\nDisplay Name: %s\nAccount Name: %s\nOwner Name: %s", calID, displayName, accountName, ownerName)
                    primaryCalendarInfos.add(calendarInfo)
                }
            } else {
                Timber.d("cursor null")
            }
            cur?.close()
        }
    }

    private fun modifyCalendar() {
        val calID: Long = 1
        val values = ContentValues().apply {
            // The new display name for the calendar
            put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "Rakib's new Calendar")
        }
        val updateUri: Uri = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, calID)
        val rows: Int = requireContext().contentResolver.update(updateUri, values, null, null)

        if (rows > 0)
            Timber.d("Modified")
    }

    private fun insertEvent() {
        Timber.d("Inserting event")
        doAsync {
            val calID: Long = 1
            val startMillis: Long = Calendar.getInstance().run {
                set(2020, 11, 15, 7, 30)
                timeInMillis
            }
            val endMillis: Long = Calendar.getInstance().run {
                set(2020, 11, 15, 8, 45)
                timeInMillis
            }

            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.TITLE, "Live Interview by Rakib")
                put(CalendarContract.Events.DESCRIPTION, "Test Test Test")
                put(CalendarContract.Events.CALENDAR_ID, calID)
                put(CalendarContract.Events.ORIGINAL_ID, 5000)
                put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles")
            }
            val uri: Uri? = requireContext().contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)

            eventID = uri?.lastPathSegment?.toLong()
        }
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

        reasonRG.setOnCheckedChangeListener { group, checkedId ->
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
            val checkedId = reasonRG.checkedRadioButtonId
            if (checkedId == R.id.reason1RB) {
                selectedReason = "1"
            } else if (checkedId == R.id.reason2RB) {
                selectedReason = "2"
            } else if (checkedId == R.id.reason3RB) {
                selectedReason = "3"
            } else if (checkedId == R.id.reason4RB) {
                selectedReason = "4"
                otherReason = otherReasonET.text.toString()
            }

            if (TextUtils.isEmpty(selectedReason)) {
                Toast.makeText(activity, "Please select an option from the list.", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(otherReason) && selectedReason.equals("4", ignoreCase = true)) {
                Toast.makeText(activity, "Please write your reason briefly.", Toast.LENGTH_SHORT).show()
            } else if (selectedReason.equals("4", ignoreCase = true) && otherReason.trim { it <= ' ' }.length == 0) {
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
//                    findNavController().navigate(LiveInterviewDetailsFragmentDirections)
                    if (from=="video") findNavController().navigate(LiveInterviewDetailsFragmentDirections.actionLiveInterviewDetailsFragmentToRecordVideoFragment())
                    else if (from=="session") findNavController().navigate(LiveInterviewDetailsFragmentDirections.actionLiveInterviewDetailsFragmentToInterviewSessionFragment(liveInterviewDetailsViewModel.jobId,args.jobTitle,liveInterviewDetailsViewModel.processID.value))
                    else findNavController().navigate(LiveInterviewDetailsFragmentDirections.actionLiveInterviewDetailsFragmentToAudioRecordFragment())
//                    else requireContext().startActivity(Intent(requireContext(),RecordingActivity::class.java))

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

    private fun isEventAlreadyExist(id: Int): Boolean {
        val INSTANCE_PROJECTION = arrayOf(
                CalendarContract.Instances.EVENT_ID,  // 0
                CalendarContract.Instances.BEGIN,  // 1
                CalendarContract.Instances.TITLE, // 2
                CalendarContract.Instances.ORIGINAL_ID
        )
        var startMillis: Long = 0
        var endMillis: Long = 0
        val beginTime = Calendar.getInstance()
        beginTime[2017, 1, 15, 6] = 0
        startMillis = beginTime.timeInMillis
        val endTime = Calendar.getInstance()
        endTime[2020, 11, 15, 8] = 0
        endMillis = endTime.timeInMillis

        // The ID of the recurring event whose instances you are searching for in the Instances table
        val selection = CalendarContract.Instances.ORIGINAL_ID + " = ?"
        val selectionArgs = arrayOf(id.toString())

        // Construct the query with the desired date range.
        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, startMillis)
        ContentUris.appendId(builder, endMillis)

        // Submit the query
        val cur: Cursor? = requireContext().contentResolver.query(builder.build(), INSTANCE_PROJECTION, selection, selectionArgs, null)
        return cur!!.count > 0
    }

}