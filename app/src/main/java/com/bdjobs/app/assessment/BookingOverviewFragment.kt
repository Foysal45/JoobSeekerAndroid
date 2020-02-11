package com.bdjobs.app.assessment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.bdjobs.app.R
import com.bdjobs.app.assessment.viewmodels.BookingOverviewViewModel
import com.bdjobs.app.assessment.viewmodels.BookingOverviewViewModelFactory
import com.bdjobs.app.databinding.FragmentBookingOverviewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_booking_overview.*

/**
 * A simple [Fragment] subclass.
 */
class BookingOverviewFragment : Fragment(), OnMapReadyCallback {

    lateinit var bookingOverviewViewModel: BookingOverviewViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val application = requireNotNull(activity).application

        val binding = FragmentBookingOverviewBinding.inflate(inflater)

        val scheduleData = BookingOverviewFragmentArgs.fromBundle(arguments!!).scheduleData

        val viewModelFactory = BookingOverviewViewModelFactory(scheduleData!!, application)

        bookingOverviewViewModel = ViewModelProvider(this, viewModelFactory).get(BookingOverviewViewModel::class.java)

        binding.viewmodel = bookingOverviewViewModel

        binding.lifecycleOwner = this

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as? SupportMapFragment

        mapFragment?.getMapAsync(this)

        bookingOverviewViewModel.navigateToPayment.observe(viewLifecycleOwner, Observer {it->
            it.getContentIfNotHandled()?.let {
                findNavController().navigate(BookingOverviewFragmentDirections.actionBookingOverviewFragmentToPaymentFragment(it))
            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



        cancel_booking_btn?.setOnClickListener {
            bookingOverviewViewModel.cancelSchedule()
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val latLng = LatLng(23.751009, 90.393092)
        googleMap?.addMarker(MarkerOptions().position(latLng).title("Bdjobs"))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
    }


}
