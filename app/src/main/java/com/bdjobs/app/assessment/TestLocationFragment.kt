package com.bdjobs.app.assessment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.R
import com.bdjobs.app.assessment.viewmodels.TestLocationViewModel
import com.bdjobs.app.databinding.FragmentTestLocationBinding
import com.google.android.material.snackbar.Snackbar

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
            snackbar = Snackbar.make(binding.testLocationCl, getString(R.string.assessment_requirement_instruction), Snackbar.LENGTH_LONG)
            snackbar.show()
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
