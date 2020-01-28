package com.bdjobs.app.assessment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import com.bdjobs.app.R
import com.bdjobs.app.assessment.viewmodels.CertificateViewModel
import com.bdjobs.app.databinding.FragmentCertificateListBinding

/**
 * A simple [Fragment] subclass.
 */
class CertificateListFragment : Fragment() {

    lateinit var viewModel: CertificateViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val binding = FragmentCertificateListBinding.inflate(inflater)

        viewModel = ViewModelProvider(requireNotNull(activity)).get(CertificateViewModel::class.java)

        binding.lifecycleOwner = this

        return binding.root

        //return inflater.inflate(R.layout.fragment_certificate_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}
