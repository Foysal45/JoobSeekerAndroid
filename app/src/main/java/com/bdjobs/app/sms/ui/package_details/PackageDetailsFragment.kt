package com.bdjobs.app.sms.ui.package_details

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.R

class PackageDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = PackageDetailsFragment()
    }

    private lateinit var viewModel: PackageDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.package_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PackageDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}