package com.bdjobs.app.sms.ui.settings

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.Employers.EmployersBaseActivity
import com.bdjobs.app.FavouriteSearch.FavouriteSearchBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentSettingsBinding
import com.bdjobs.app.videoInterview.util.EventObserver
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.startActivity

class SettingsFragment : Fragment() {

    private val settingsViewModel: SettingsViewModel by viewModels { ViewModelFactoryUtil.provideSMSSettingsViewModelFactory(this) }
    lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater).apply {
            viewModel = settingsViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsViewModel.apply {

            navigateToHome.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToSmsHomeFragment())
                }
            })

            navigateToHomeFreeTrial.observe(viewLifecycleOwner,EventObserver{
                if (it){
                    findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToSmsFreeTrialHomeFragment())
                }
            })

            openDialogEvent.observe(viewLifecycleOwner, EventObserver {
                if (it)
                    openChooseLimitDialog()
            })

            openTurnOffSMSDialogEvent.observe(viewLifecycleOwner, EventObserver {
                if (it) {
                    openTurnOffSMSDialog()
                }
            })
        }
    }

    private fun openTurnOffSMSDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        builder.setView(inflater.inflate(R.layout.dialog_turn_off_sms_alert, null))
        builder.create().apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
            findViewById<ImageView>(R.id.img_close).setOnClickListener {
                this.cancel()
            }
            findViewById<MaterialButton>(R.id.btn_following_employer).setOnClickListener {
                requireContext().startActivity<EmployersBaseActivity>(
                        "from" to "follow",
                        "time" to 0
                )
            }
            findViewById<MaterialButton>(R.id.btn_favourite_search).setOnClickListener {
                requireContext().startActivity(Intent(requireActivity(),FavouriteSearchBaseActivity::class.java))
            }
            findViewById<TextView>(R.id.sms_count_tv).text = "You have more than ${settingsViewModel.remainingSMS.value} SMS Job alert left. Avail this service from following features"
        }
    }

    private fun openChooseLimitDialog() {
        val limits = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        MaterialAlertDialogBuilder(requireContext()).setTitle("Choose Limit")
                .setItems(limits) { dialog, which ->
                    settingsViewModel.setLimit(limits[which])
                }.show()
    }


}