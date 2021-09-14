package com.bdjobs.app.sms.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.databinding.FragmentSmsHomeBinding
import com.bdjobs.app.videoInterview.util.ViewModelFactoryUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_sms_home.*

class HomeFragment : Fragment() {

    private val homeViewModel : SMSHomeViewModel by viewModels { ViewModelFactoryUtil.provideSMSHomeViewModelFactory(this) }
    lateinit var binding : FragmentSmsHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentSmsHomeBinding.inflate(inflater).apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.checkIfSMSFree()

        binding.thirdCl100Sms.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionSmsHomeFragmentToSmsPaymentFragment(100,homeViewModel.price.value!!,"False"))
        }

        binding.imgBuy100Sms.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionSmsHomeFragmentToSmsPaymentFragment(100,homeViewModel.price.value!!,"False"))
        }

        binding.imgBuyFreeTrial.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionSmsHomeFragmentToSmsPaymentFragment(20,0,"True"))
        }

        binding.thirdClFreeTrial.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionSmsHomeFragmentToSmsPaymentFragment(20,0,"True"))
        }
    }

    override fun onResume() {
        super.onResume()

        setUpObservers()
    }

    private fun setUpObservers() {
        homeViewModel.apply {
            fetchSMSSettingsData()

            error.observe(viewLifecycleOwner,{
                Snackbar.make(binding.clParentSmsLanding,it,Snackbar.LENGTH_LONG).show()
            })
        }
    }

}