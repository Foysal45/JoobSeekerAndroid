package com.bdjobs.app.assessment


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.R
import com.bdjobs.app.utilities.equalIgnoreCase
import com.bdjobs.app.assessment.enums.Status
import com.bdjobs.app.assessment.viewmodels.BookingOverviewViewModel
import com.bdjobs.app.assessment.viewmodels.BookingOverviewViewModelFactory
import com.bdjobs.app.databinding.FragmentBookingOverviewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_booking_overview.*
import kotlinx.android.synthetic.main.layout_need_more_information.view.*

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

        binding.lifecycleOwner = this.viewLifecycleOwner

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as? SupportMapFragment

        mapFragment?.getMapAsync(this)

        bookingOverviewViewModel.navigateToPayment.observe(viewLifecycleOwner, Observer {it->
            it.getContentIfNotHandled()?.let {
                findNavController().navigate(BookingOverviewFragmentDirections.actionBookingOverviewFragmentToPaymentFragment(it,scheduleData))
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
              Status.ERROR-> {
                  binding.loadingProgressBar.visibility = View.GONE
                  Toast.makeText(activity,"Something went wrong",Toast.LENGTH_SHORT).show()
              }
          }
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

    private fun showSnackbar(){
        Snackbar.make(test_location_cl,"Unable to book schedule. Please try again after some time.", Snackbar.LENGTH_SHORT).show()
    }


    override fun onMapReady(googleMap: GoogleMap?) {

        if (bookingOverviewViewModel.scheduleData.value?.testCenter!!.equalIgnoreCase("Dhaka"))
        {
            val latLng = LatLng(23.751009, 90.393092)
            googleMap?.addMarker(MarkerOptions().position(latLng).title("Bdjobs"))
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        }
        else {
            val latLng = LatLng(22.332859, 91.812864)
            googleMap?.addMarker(MarkerOptions().position(latLng).title("Bdjobs"))
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        }

    }

}
