package com.bdjobs.app.liveInterview.ui.interview_details

import android.app.Application
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentLiveInterviewDetailsBinding
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository
import com.bdjobs.app.videoInterview.util.EventObserver
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_live_interview_details.*
import kotlinx.android.synthetic.main.fragment_live_interview_details.btn_job_detail
import kotlinx.android.synthetic.main.fragment_live_interview_details.tool_bar
import kotlinx.android.synthetic.main.fragment_video_interview_details.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.toast
import timber.log.Timber

class LiveInterviewDetailsFragment : Fragment() {

    var snackbar: Snackbar? = null

    private val args: LiveInterviewDetailsFragmentArgs by navArgs()

    private val liveInterviewDetailsViewModel: LiveInterviewDetailsViewModel by viewModels {
        LiveInterviewDetailsViewModelFactory(
                LiveInterviewRepository(requireActivity().application as Application),
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



    override fun onPause() {
        super.onPause()
        snackbar?.dismiss()
    }

}