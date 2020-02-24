package com.bdjobs.app.assessment


import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.bdjobs.app.R
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.openUrlInBrowser
import com.bdjobs.app.Web.WebActivity
import com.bdjobs.app.assessment.models.ScheduleData
import com.bdjobs.app.assessment.viewmodels.TestLocationViewModel
import com.bdjobs.app.databinding.FragmentTestLocationBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_test_location.*
import org.jetbrains.anko.startActivity

/**
 * A simple [Fragment] subclass.
 */
class TestLocationFragment : Fragment() {

    lateinit var testLocationViewModel: TestLocationViewModel
    lateinit var snackbar: Snackbar


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentTestLocationBinding.inflate(inflater)

        testLocationViewModel = ViewModelProvider(requireNotNull(activity)).get(TestLocationViewModel::class.java)

        binding.lifecycleOwner = this.viewLifecycleOwner

        binding.viewModel = testLocationViewModel

        binding.testHomeCard?.setOnClickListener {
//            findNavController().navigate(TestLocationFragmentDirections.actionTestLocationFragmentToPaymentFragment(testLocationViewModel.booking, ScheduleData()))
            snackbar = Snackbar.make(binding.testLocationCl, "Action needs to complete from website", Snackbar.LENGTH_INDEFINITE)
            snackbar.apply {
                setAction("Go To Website") {
//                    context.startActivity<WebActivity>("url" to Constants.base_url_assessment_web, "from" to "assessment")
                    context.openUrlInBrowser(Constants.base_url_assessment_web)

                }.show()
            }
        }

        binding.testCenterCard?.setOnClickListener {
            findNavController().navigate(R.id.action_testLocationFragment_to_chooseScheduleFragment)
        }

        return binding.root
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//        test_home_card?.setOnClickListener{
////            test_home_card.strokeColor = ContextCompat.getColor(activity!!.applicationContext,R.color.selected)
////            test_home_card.strokeWidth = 3
////            test_center_card.strokeWidth = 0
//        }
//
//        test_center_card?.setOnClickListener{
////            test_center_card.strokeColor = ContextCompat.getColor(activity!!.applicationContext,R.color.selected)
////            test_center_card.strokeWidth = 3
////            test_home_card.strokeWidth = 0
//        }
//
//        btn_cl?.setOnClickListener {
//            findNavController().navigate(R.id.action_testLocationFragment_to_chooseScheduleFragment)
//        }
//    }

    override fun onPause() {
        super.onPause()
        try{
            snackbar.dismiss()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }


}
