package com.bdjobs.app.assessment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.assessment.adapters.CertificateListAdapter
import com.bdjobs.app.assessment.adapters.ClickListener
import com.bdjobs.app.assessment.enums.Status
import com.bdjobs.app.assessment.viewmodels.CertificateViewModel
import com.bdjobs.app.databinding.FragmentCertificateListBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_certificate_list.*

/**
 * A simple [Fragment] subclass.
 */
class CertificateListFragment : Fragment() {

    lateinit var viewModel: CertificateViewModel

    lateinit var snackbar: Snackbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentCertificateListBinding.inflate(inflater)

        viewModel = ViewModelProvider(requireNotNull(activity)).get(CertificateViewModel::class.java)

        binding.lifecycleOwner = this

        binding.certificateViewModel = viewModel

        binding.certificateListRv.adapter = CertificateListAdapter(requireNotNull(context), ClickListener {
            viewModel.displayResultDetails(it)
        })

        viewModel.navigateToResultDetails.observe(viewLifecycleOwner, Observer { it ->
            it.getContentIfNotHandled()?.let {
                findNavController().navigate(ViewPagerFragmentDirections.actionViewPagerFragmentToResultFragment(it))
                //viewModel.displayResultDetailsCompleted()
            }
        })

        viewModel.status.observe(viewLifecycleOwner, Observer {
            try {
                snackbar = Snackbar.make(certificate_list_cl, "Something went wrong", Snackbar.LENGTH_LONG)
                when (it) {
                    Status.ERROR ->

                        snackbar.apply {
                            setAction(
                                    "Retry"
                            ) {
                                viewModel.getCertificateList()
                            }.show()

                        }
                    else -> {
                        snackbar.dismiss()
                    }
                }
            } catch (e: Exception) {
            }
        })

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        try {
            snackbar.dismiss()
        } catch (e: Exception) {
        }
    }
}
