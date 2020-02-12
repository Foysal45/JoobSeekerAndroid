package com.bdjobs.app.assessment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_payment_successful.*

/**
 * A simple [Fragment] subclass.
 */
class PaymentSuccessfulFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_successful, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_certification_home_cl?.setOnClickListener {
            findNavController().navigate(PaymentSuccessfulFragmentDirections.actionPaymentSuccessfulFragmentToViewPagerFragment())
        }
    }

}
