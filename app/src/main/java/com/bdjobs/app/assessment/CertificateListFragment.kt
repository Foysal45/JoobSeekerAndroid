package com.bdjobs.app.assessment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import com.bdjobs.app.R
import com.bdjobs.app.assessment.adapters.CertificateListAdapter
import com.bdjobs.app.assessment.adapters.ClickListener
import com.bdjobs.app.assessment.viewmodels.CertificateViewModel
import com.bdjobs.app.databinding.FragmentCertificateListBinding
import kotlinx.android.synthetic.main.fragment_certificate_list.*

/**
 * A simple [Fragment] subclass.
 */
class CertificateListFragment : Fragment() {

    lateinit var viewModel: CertificateViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentCertificateListBinding.inflate(inflater)

        viewModel = ViewModelProvider(requireNotNull(activity)).get(CertificateViewModel::class.java)

        binding.lifecycleOwner = this

        binding.certificateViewModel = viewModel

        binding.certificateListRv.adapter = CertificateListAdapter(requireNotNull(context), ClickListener {
            Toast.makeText(context, "${it?.testName}",Toast.LENGTH_SHORT).show()
            viewModel.displayResultDetails(it)
        })

        viewModel.navigateToResultDetails.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(ViewPagerFragmentDirections.actionViewPagerFragmentToResultFragment(it))
                viewModel.displayResultDetailsCompleted()
            }
        })
        return binding.root
    }
}
