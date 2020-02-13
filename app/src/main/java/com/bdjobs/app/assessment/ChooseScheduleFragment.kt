package com.bdjobs.app.assessment


import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.bdjobs.app.R
import com.bdjobs.app.assessment.adapters.ClickListener
import com.bdjobs.app.assessment.adapters.ScheduleClickListener
import com.bdjobs.app.assessment.adapters.ScheduleListAdapter
import com.bdjobs.app.assessment.viewmodels.ChooseScheduleVewModel
import com.bdjobs.app.databinding.FragmentChooseScheduleBinding
import kotlinx.android.synthetic.main.fragment_choose_schedule.*
import org.jetbrains.anko.support.v4.toast
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ChooseScheduleFragment : Fragment() {

    lateinit var scheduleViewModel : ChooseScheduleVewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        scheduleViewModel = ViewModelProvider(requireNotNull(activity)).get(ChooseScheduleVewModel::class.java)

        val binding = FragmentChooseScheduleBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.scheduleViewModel = scheduleViewModel

        binding.scheduleRv.adapter = ScheduleListAdapter(requireNotNull(context), ScheduleClickListener {
            //Toast.makeText(activity, it.schlId, Toast.LENGTH_SHORT).show()
            findNavController().navigate(ChooseScheduleFragmentDirections.actionChooseScheduleFragmentToBookingOverviewFragment(it))

        })

        binding.filterImg?.setOnClickListener {
            findNavController().navigate(R.id.action_chooseScheduleFragment_to_scheduleFilterFragment)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        scheduleViewModel.getScheduleList()
    }

}
