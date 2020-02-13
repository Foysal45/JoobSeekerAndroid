package com.bdjobs.app.assessment


import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.pickDate
import com.bdjobs.app.assessment.viewmodels.ChooseScheduleVewModel
import com.bdjobs.app.databinding.FragmentFilterScheduleBinding
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class FilterScheduleFragment : Fragment() {

    lateinit var scheduleViewModel: ChooseScheduleVewModel

    var now = Calendar.getInstance()

    private val startDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        now.set(Calendar.YEAR, year)
        now.set(Calendar.MONTH, monthOfYear)
        now.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        //updateDateInView(0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        scheduleViewModel = ViewModelProvider(requireNotNull(activity)).get(ChooseScheduleVewModel::class.java)

        val binding = FragmentFilterScheduleBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = scheduleViewModel

        binding.filterFromTv?.setOnClickListener {
            openDialog()
        }

        return binding.root
    }


    private fun openDialog(){
        Log.d("rakib", "callllllll")
        pickDate(activity?.application!!.applicationContext, now, startDateSetListener)
    }

}
