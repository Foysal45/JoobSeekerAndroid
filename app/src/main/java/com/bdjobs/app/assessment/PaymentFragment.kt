package com.bdjobs.app.assessment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController

import com.bdjobs.app.R
import com.bdjobs.app.Web.WebActivity
import com.bdjobs.app.assessment.models.Booking
import com.bdjobs.app.assessment.viewmodels.BookingOverviewViewModel
import com.bdjobs.app.assessment.viewmodels.HomeViewModel
import com.bdjobs.app.assessment.viewmodels.PaymentViewModelFactory
import com.bdjobs.app.assessment.viewmodels.PaymentViewModel
import com.bdjobs.app.databinding.FragmentPaymentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_payment.*
import org.jetbrains.anko.startActivity

/**
 * A simple [Fragment] subclass.
 */
class PaymentFragment : Fragment() {

    lateinit var paymentViewModel: PaymentViewModel
    lateinit var homeViewModel : HomeViewModel
    lateinit var snackbar: Snackbar


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentPaymentBinding.inflate(inflater)

        val application = requireNotNull(activity).application

        val bookingData = PaymentFragmentArgs.fromBundle(arguments!!).bookingData

        val scheduleData = PaymentFragmentArgs.fromBundle(arguments!!).scheduleData

        val viewModelFactory = PaymentViewModelFactory(bookingData!!,application)

        paymentViewModel = ViewModelProvider(this,viewModelFactory).get(PaymentViewModel::class.java)

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        binding.homeViewModel = homeViewModel

        binding.viewModel = paymentViewModel

        binding.lifecycleOwner = this.viewLifecycleOwner

        binding.payCashCard?.setOnClickListener {
            paymentViewModel.bookSchedule()
        }

        Log.d("rakib","$bookingData")

        if (bookingData.isFromHome == "0")
        {
            binding.payCashCard.visibility = View.VISIBLE
        } else {
            binding.payCashCard.visibility = View.GONE
        }

        binding.testHomeCard.setOnClickListener {
            snackbar = Snackbar.make(binding.testLocationCl, "Action needs to complete from BDJobs website", Snackbar.LENGTH_INDEFINITE)
            snackbar.apply {
                setAction("Go To Website") {
                    val url = "https://mybdjobs.bdjobs.com/mybdjobs/assessment/smnt_certification_home.asp?device=app"
                    context.startActivity<WebActivity>("url" to url, "from" to "assessment")
                }.show()
            }
        }

        paymentViewModel.navigateToSuccessful.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(PaymentFragmentDirections.actionPaymentFragmentToPaymentSuccessfulFragment(scheduleData))
        })

        return binding.root
    }

}
