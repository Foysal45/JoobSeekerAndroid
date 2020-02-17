package com.bdjobs.app.assessment


import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.pickDate
import com.bdjobs.app.assessment.viewmodels.ChooseScheduleViewModel
import com.bdjobs.app.databinding.FragmentFilterScheduleBinding
import kotlinx.android.synthetic.main.fragment_filter_schedule.*
import org.jetbrains.anko.support.v4.selector
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class FilterScheduleFragment : Fragment() {

    lateinit var scheduleViewModel: ChooseScheduleViewModel

    var now = Calendar.getInstance()

    private val startDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        now.set(Calendar.YEAR, year)
        now.set(Calendar.MONTH, monthOfYear)
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        updateDateInView(0)
    }

    private val endDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        now.set(Calendar.YEAR, year)
        now.set(Calendar.MONTH, monthOfYear)
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        updateDateInView(1)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        scheduleViewModel = ViewModelProvider(requireNotNull(activity)).get(ChooseScheduleViewModel::class.java)

        val binding = FragmentFilterScheduleBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = scheduleViewModel

        binding.filterFromTv?.setOnClickListener {
            openDialog(it)
        }

        binding.filterToTv?.setOnClickListener {
            openDialog(it)
        }

        binding.filterVenueTv?.setOnClickListener {
            val venues = listOf("Dhaka", "Chattogram")
            selector("Please select your venue", venues) { dialogInterface, i ->
                filter_venue_tv?.setText(venues[i])
                scheduleViewModel.scheduleRequest.venue = when(venues[i]){
                    "Dhaka" -> "1"
                    "Chattogram" -> "3"
                    else -> "0"
                }
            }
        }

        binding.filterSearchBtn?.setOnClickListener {
            findNavController().navigate(FilterScheduleFragmentDirections.actionScheduleFilterFragmentToChooseScheduleFragment(scheduleViewModel.scheduleRequest))
        }

        return binding.root
    }


    private fun openDialog(view: View){

        when(view.id){
            R.id.filter_from_tv->{
                activity?.apply {
                    pickDate(this, now, startDateSetListener, from = "assessment")
                }
            }
            R.id.filter_to_tv->{
                activity?.apply {
                    pickDate(this, now, endDateSetListener, from = "assessment")
                }
            }
        }

//        Log.d("rakib", "callllllll")
//        activity?.apply{
//            pickDate(this, now, startDateSetListener,from = "assessment")
//        }
    }

    private fun updateDateInView(c: Int) {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        if (c == 0) {
            val from = sdf.format(now.time)
            filter_from_tv.setText(from)
            scheduleViewModel.scheduleRequest.fromDate = from
        } else {
            val to = sdf.format(now.time)
            filter_to_tv.setText(to)
            scheduleViewModel.scheduleRequest.toDate = to
        }
    }

}
