package com.bdjobs.app.sms.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bdjobs.app.databinding.FragmentPaymentSmsBinding
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import kotlinx.android.synthetic.main.fragment_payment_sms.*

class PaymentFragment : Fragment() {

    private val args : PaymentFragmentArgs by navArgs()
    private val paymentViewModel : PaymentViewModel by viewModels { ViewModelFactoryUtil.provideSMSPaymentViewModelFactory(this,args.totalSMS,args.totalTaka) }
    lateinit var binding : FragmentPaymentSmsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentPaymentSmsBinding.inflate(inflater).apply {
            viewModel = paymentViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_confirm_payment?.setOnClickListener {
            findNavController().navigate(PaymentFragmentDirections.actionSmsPaymentFragmentToPaymentSuccessSmsFragment())
        }
    }
}