package com.bdjobs.app.assessment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_book_schedule.*

/**
 * A simple [Fragment] subclass.
 */
class BookScheduleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_schedule, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        manage_booking_btn?.setOnClickListener {
            findNavController().navigate(R.id.action_bookScheduleFragment_to_paymentFragment)
        }
    }

}
