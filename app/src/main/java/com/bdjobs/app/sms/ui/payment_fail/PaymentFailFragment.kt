package com.bdjobs.app.sms.ui.payment_fail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import kotlinx.android.synthetic.main.fragment_payment_fail.*

class PaymentFailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_fail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = BdjobsUserSession(requireContext())
        tv_body?.text = "Dear ${session.fullName?.trim()}, your payment was processed fail."
    }

}