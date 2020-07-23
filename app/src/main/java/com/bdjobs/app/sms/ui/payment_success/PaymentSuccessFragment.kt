package com.bdjobs.app.sms.ui.payment_success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.databinding.FragmentPaymentSuccessBinding
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import kotlinx.android.synthetic.main.fragment_payment_success.*

class PaymentSuccessFragment : Fragment() {

    private val paymentSuccessViewModel : PaymentSuccessViewModel by viewModels { ViewModelFactoryUtil.provideSMSPaymentSuccessViewModelFactory(this) }
    lateinit var binding : FragmentPaymentSuccessBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPaymentSuccessBinding.inflate(inflater).apply {
            viewModel = paymentSuccessViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_following_employer?.setOnClickListener {
            findNavController().navigate(PaymentSuccessFragmentDirections.actionPaymentSuccessFragmentToGuidelineSmsFragment())
        }

        btn_favourite_search?.setOnClickListener {
            findNavController().navigate(PaymentSuccessFragmentDirections.actionPaymentSuccessFragmentToGuidelineSmsFragment())
        }

        btn_sms_job_alert?.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}