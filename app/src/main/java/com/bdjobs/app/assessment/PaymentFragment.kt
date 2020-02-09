package com.bdjobs.app.assessment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.bdjobs.app.R
import com.bdjobs.app.assessment.models.Booking
import com.bdjobs.app.assessment.viewmodels.BookingViewModelFactory
import com.bdjobs.app.assessment.viewmodels.BookingViewModel
import kotlinx.android.synthetic.main.fragment_payment.*

/**
 * A simple [Fragment] subclass.
 */
class PaymentFragment : Fragment() {

    lateinit var bookingViewModel : BookingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val application = requireNotNull(activity).application

        val viewModelFactory = BookingViewModelFactory(Booking(),application)

        bookingViewModel = ViewModelProvider(this,viewModelFactory).get(BookingViewModel::class.java)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_cl?.setOnClickListener {

            bookingViewModel.bookSchedule()

            //findNavController().navigate(R.id.action_paymentFragment_to_viewPagerFragment)
        }
    }
}
