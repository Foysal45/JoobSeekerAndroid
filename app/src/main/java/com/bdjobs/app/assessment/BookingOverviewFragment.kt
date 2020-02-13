package com.bdjobs.app.assessment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.bdjobs.app.R
import com.bdjobs.app.assessment.enums.Status
import com.bdjobs.app.assessment.viewmodels.BookingOverviewViewModel
import com.bdjobs.app.assessment.viewmodels.BookingOverviewViewModelFactory
import com.bdjobs.app.databinding.FragmentBookingOverviewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
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

        Log.d("rakib", "$scheduleData")

        val viewModelFactory = BookingOverviewViewModelFactory(scheduleData!!, application)

        bookingOverviewViewModel = ViewModelProvider(this, viewModelFactory).get(BookingOverviewViewModel::class.java)

        binding.viewmodel = bookingOverviewViewModel

        binding.lifecycleOwner = this.viewLifecycleOwner

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as? SupportMapFragment

        mapFragment?.getMapAsync(this)

        bookingOverviewViewModel.navigateToPayment.observe(viewLifecycleOwner, Observer {it->
            it.getContentIfNotHandled()?.let {
                findNavController().navigate(BookingOverviewFragmentDirections.actionBookingOverviewFragmentToPaymentFragment(it))
            }
        })

        bookingOverviewViewModel.navigateToHome.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled()?.let {
                findNavController().navigate(BookingOverviewFragmentDirections.actionBookingOverviewFragmentToViewPagerFragment("true"))
            }
        })

        bookingOverviewViewModel.showSnackbar.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled()?.let {
                showSnackbar()
            }
        })

        bookingOverviewViewModel.status.observe(viewLifecycleOwner, Observer {
          when(it){
              Status.DONE-> binding.loadingProgressBar.visibility = View.GONE
              Status.LOADING-> binding.loadingProgressBar.visibility = View.VISIBLE
              Status.ERROR-> binding.loadingProgressBar.visibility = View.GONE
          }
        })

        return binding.root
    }

    private fun showSnackbar(){
        Snackbar.make(test_location_cl,"Unable to book schedule. Please try again after some time.", Snackbar.LENGTH_SHORT).show()
    }



    override fun onMapReady(googleMap: GoogleMap?) {
        val latLng = LatLng(23.751009, 90.393092)
        googleMap?.addMarker(MarkerOptions().position(latLng).title("Bdjobs"))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
    }


}
