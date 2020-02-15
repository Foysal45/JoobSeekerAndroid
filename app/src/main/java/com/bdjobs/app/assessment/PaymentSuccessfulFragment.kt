package com.bdjobs.app.assessment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.assessment.models.ScheduleData
import kotlinx.android.synthetic.main.fragment_payment_successful.*

/**
 * A simple [Fragment] subclass.
 */
class PaymentSuccessfulFragment : Fragment() {

    var scheduleData : ScheduleData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_successful, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        try {
            scheduleData = PaymentSuccessfulFragmentArgs.fromBundle(arguments!!).scheduleData
        } catch (e: Exception) {
        }

        scheduleData?.let {
            tv_test_date?.text = scheduleData?.testDate
            tv_test_time?.text = scheduleData?.testTime
            tv_venue?.text = if (scheduleData?.testCenter!!.equalIgnoreCase("Dhaka")) "8th Floor - West BDBL Building, 12 Kawran Bazar C/A, Dhaka-1215" else "1745, Sheikh Mujib Road (2nd Floor)\n" +
                    "Agrabad (Nearby Hotel Land Mark), Chittagong"
        }

        btn_certification_home_cl?.setOnClickListener {
            findNavController().navigate(PaymentSuccessfulFragmentDirections.actionPaymentSuccessfulFragmentToViewPagerFragment("true"))
        }
    }

}
