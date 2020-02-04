package com.bdjobs.app.assessment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController

import com.bdjobs.app.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_book_schedule.*

/**
 * A simple [Fragment] subclass.
 */
class BookScheduleFragment : Fragment(), OnMapReadyCallback {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_book_schedule, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as? SupportMapFragment

        mapFragment?.getMapAsync(this)

        manage_booking_btn?.setOnClickListener {
            findNavController().navigate(R.id.action_bookScheduleFragment_to_paymentFragment)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val latLng = LatLng(23.751009, 90.393092)
        googleMap?.addMarker(MarkerOptions().position(latLng).title("Bdjobs"))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f))
    }


}
