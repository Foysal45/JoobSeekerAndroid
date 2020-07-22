package com.bdjobs.app.sms.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentPaymentSmsBinding
import com.bdjobs.app.databinding.FragmentSmsHomeFreeTrialBinding
import com.bdjobs.app.sms.ui.payment.PaymentViewModel
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import kotlinx.android.synthetic.main.fragment_sms_home_free_trial.*


class HomeFreeTrialFragment : Fragment() {

    private val homeCommonViewModel : HomeCommonViewModel by viewModels { ViewModelFactoryUtil.provideSMSHomeCommonViewModelFactory(this) }
    lateinit var binding : FragmentSmsHomeFreeTrialBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSmsHomeFreeTrialBinding.inflate(inflater).apply {
            viewModel = homeCommonViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        img_buy?.setOnClickListener {
            findNavController().navigate(HomeFreeTrialFragmentDirections.actionSmsHomeFreeTrialFragmentToPaymentFragment(100,10))
        }

        btn_start_trial?.setOnClickListener {
            findNavController().navigate(HomeFreeTrialFragmentDirections.actionSmsHomeFreeTrialFragmentToPaymentFragment(20,0))
        }
    }
}