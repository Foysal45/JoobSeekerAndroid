package com.bdjobs.app.assessment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.utilities.equalIgnoreCase
import com.bdjobs.app.assessment.models.Booking
import com.bdjobs.app.assessment.models.ScheduleData
import com.bdjobs.app.assessment.viewmodels.PaymentViewModel
import com.bdjobs.app.assessment.viewmodels.PaymentViewModelFactory
import com.bdjobs.app.databinding.FragmentPaymentSuccessfulBinding
import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * A simple [Fragment] subclass.
 */
class PaymentSuccessfulFragment : Fragment() {

    var scheduleData : ScheduleData? = null
    lateinit var paymentViewModel: PaymentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentPaymentSuccessfulBinding.inflate(inflater)

        val application = requireNotNull(activity).application

        val viewModelFactory = PaymentViewModelFactory(Booking(),application)

        paymentViewModel = ViewModelProvider(this,viewModelFactory).get(PaymentViewModel::class.java)

        binding.viewModel = paymentViewModel

        binding.lifecycleOwner = this.viewLifecycleOwner

        try {
            scheduleData = PaymentSuccessfulFragmentArgs.fromBundle(arguments!!).scheduleData
        } catch (e: Exception) {
        }

        scheduleData?.let {
            binding.tvMessageDetails.text = "Dear ${paymentViewModel.fullname}, your test booking is\n" + "successfully placed"
            binding.tvTestDate.text = formatDate(scheduleData?.testDate)
            binding.tvTestTime.text = scheduleData?.testTime
            binding.tvVenue.text = if (scheduleData?.testCenter!!.equalIgnoreCase("Dhaka")) "8th Floor - West BDBL Building, 12 Kawran Bazar C/A, Dhaka-1215" else "1745, Sheikh Mujib Road (2nd Floor)\n" +
                    "Agrabad (Nearby Hotel Land Mark), Chittagong"
        }

        binding.btnCertificationHomeCl.setOnClickListener {
            findNavController().navigate(PaymentSuccessfulFragmentDirections.actionPaymentSuccessfulFragmentToViewPagerFragment("true"))
        }
        return binding.root
    }


    fun formatDate(dateString:String?): String? {
        dateString?.let {
            var dateFormat = SimpleDateFormat("MM/dd/yyyy")
            val dateFormat2 = SimpleDateFormat("dd MMM, yyyy")
            try {
                val date = dateFormat.parse(dateString)
                val out = dateFormat2.format(date)
                return out
                //Log.e("Time", out)
            } catch (e: ParseException) {
                //Log.d("Time", e.toString())
            }
        }
        return dateString
    }
}
