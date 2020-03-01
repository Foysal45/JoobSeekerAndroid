package com.bdjobs.app.assessment


import android.content.Intent
import android.net.Uri
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
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.openUrlInBrowser
import com.bdjobs.app.Web.WebActivity
import com.bdjobs.app.assessment.models.Booking
import com.bdjobs.app.assessment.viewmodels.BookingOverviewViewModel
import com.bdjobs.app.assessment.viewmodels.HomeViewModel
import com.bdjobs.app.assessment.viewmodels.PaymentViewModelFactory
import com.bdjobs.app.assessment.viewmodels.PaymentViewModel
import com.bdjobs.app.databinding.FragmentPaymentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_payment.*
import kotlinx.android.synthetic.main.layout_need_more_information.view.*
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
            snackbar = Snackbar.make(binding.testLocationCl, "Action needs to complete from website", Snackbar.LENGTH_INDEFINITE)
            snackbar.apply {
                setAction("Go To Website") {
//                    context.startActivity<WebActivity>("url" to Constants.base_url_assessment_web, "from" to "assessment")
                    context.openUrlInBrowser(Constants.base_url_assessment_web)
                }.show()
            }
        }

        paymentViewModel.navigateToSuccessful.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(PaymentFragmentDirections.actionPaymentFragmentToPaymentSuccessfulFragment(scheduleData))
        })

        binding.needMoreCl.call_tv.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:01844519336")
            startActivity(intent)
        }

        binding.needMoreCl.call_helpline_tv.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:16479")
            startActivity(intent)
        }


        return binding.root
    }

    override fun onPause() {
        super.onPause()
        try{
            snackbar.dismiss()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

}
