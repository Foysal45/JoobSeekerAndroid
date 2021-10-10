package com.bdjobs.app.sms.ui.package_details

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bdjobs.app.R
import kotlinx.android.synthetic.main.package_details_fragment.*

class PackageDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = PackageDetailsFragment()
    }

    private lateinit var viewModel: PackageDetailsViewModel
    private val args : PackageDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.package_details_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PackageDetailsViewModel::class.java)

        tv_free_sms_package.text = "${args.freeSms} SMS"
        tv_free_trial_instruction_1.text = "Get ${args.freeSms} SMS"

        if (args.freeSms=="0") {
            cl_sms_package_20.visibility = View.GONE
        } else {
            cl_sms_package_20.visibility = View.VISIBLE
        }

    }

}