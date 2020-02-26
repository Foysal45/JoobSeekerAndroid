package com.bdjobs.app.assessment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.R
import com.bdjobs.app.assessment.adapters.ScheduleClickListener
import com.bdjobs.app.assessment.adapters.ScheduleListAdapter
import com.bdjobs.app.assessment.models.ScheduleRequest
import com.bdjobs.app.assessment.viewmodels.ChooseScheduleViewModel
import com.bdjobs.app.databinding.FragmentChooseScheduleBinding

/**
 * A simple [Fragment] subclass.
 */
class ChooseScheduleFragment : Fragment() {

    lateinit var scheduleViewModel: ChooseScheduleViewModel
    var scheduleRequestData: ScheduleRequest? = null
    var from = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        scheduleViewModel = ViewModelProvider(requireNotNull(activity)).get(ChooseScheduleViewModel::class.java)

        val binding = FragmentChooseScheduleBinding.inflate(inflater)

        binding.lifecycleOwner = this.viewLifecycleOwner

        binding.scheduleViewModel = scheduleViewModel

        try {

            binding.scheduleRv.adapter = ScheduleListAdapter(requireNotNull(context), viewLifecycleOwner, ScheduleClickListener {
                //Toast.makeText(activity, it.schlId, Toast.LENGTH_SHORT).show()
                findNavController().navigate(ChooseScheduleFragmentDirections.actionChooseScheduleFragmentToBookingOverviewFragment(it))
            })
            binding.scheduleRv?.adapter?.notifyDataSetChanged()
        } catch (e: Exception) {
            Log.d("rakib", e.message)
        }

        try {
            scheduleRequestData = ChooseScheduleFragmentArgs.fromBundle(arguments!!).scheduleRequestData

            Log.d("rakib", "schedule ${scheduleRequestData?.fromDate}")
            from = "filter"
        } catch (e: Exception) {

        }


        binding.filterImg?.setOnClickListener {
            findNavController().navigate(R.id.action_chooseScheduleFragment_to_scheduleFilterFragment)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (from == "filter")
            scheduleViewModel.filterScheduleList()
        else{
            Log.d("rakib", "called get list")
            scheduleViewModel.getScheduleList()
        }

    }

}
