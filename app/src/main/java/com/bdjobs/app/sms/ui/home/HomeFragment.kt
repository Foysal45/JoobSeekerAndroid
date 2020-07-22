package com.bdjobs.app.sms.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_sms_home_free_trial.*

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sms_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        img_buy?.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionSmsHomeFragmentToSmsPaymentFragment(100,10))
        }
    }
}