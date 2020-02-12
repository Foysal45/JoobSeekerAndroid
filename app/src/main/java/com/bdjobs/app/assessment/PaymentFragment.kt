package com.bdjobs.app.assessment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController

import com.bdjobs.app.R
import com.bdjobs.app.assessment.models.Booking
import com.bdjobs.app.assessment.viewmodels.PaymentViewModelFactory
import com.bdjobs.app.assessment.viewmodels.PaymentViewModel
import com.bdjobs.app.databinding.FragmentPaymentBinding
import kotlinx.android.synthetic.main.fragment_payment.*

/**
 * A simple [Fragment] subclass.
 */
class PaymentFragment : Fragment() {

    lateinit var paymentViewModel: PaymentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentPaymentBinding.inflate(inflater)

        val application = requireNotNull(activity).application

        val bookingData = PaymentFragmentArgs.fromBundle(arguments!!).bookingData

        val viewModelFactory = PaymentViewModelFactory(bookingData!!,application)

        paymentViewModel = ViewModelProvider(this,viewModelFactory).get(PaymentViewModel::class.java)

        binding.viewModel = paymentViewModel

        binding.lifecycleOwner = this.viewLifecycleOwner

        binding.payCashCard?.setOnClickListener {
            paymentViewModel.bookSchedule()
        }

        paymentViewModel.navigateToSuccessful.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(R.id.action_paymentFragment_to_paymentSuccessfulFragment)
        })

        return binding.root
    }

}
