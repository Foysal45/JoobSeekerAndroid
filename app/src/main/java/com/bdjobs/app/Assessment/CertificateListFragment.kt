package com.bdjobs.app.Assessment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_certificate_list.*

/**
 * A simple [Fragment] subclass.
 */
class CertificateListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_certificate_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        result_btn?.setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_resultFragment)
        }
    }


}
