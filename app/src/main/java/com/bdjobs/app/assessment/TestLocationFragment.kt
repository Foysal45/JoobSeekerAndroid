package com.bdjobs.app.assessment


import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController

import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_test_location.*

/**
 * A simple [Fragment] subclass.
 */
class TestLocationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_location, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        test_home_card?.setOnClickListener{
            test_home_card.strokeColor = ContextCompat.getColor(activity!!.applicationContext,R.color.selected)
            test_home_card.strokeWidth = 3
            test_center_card.strokeWidth = 0
        }

        test_center_card?.setOnClickListener{
            test_center_card.strokeColor = ContextCompat.getColor(activity!!.applicationContext,R.color.selected)
            test_center_card.strokeWidth = 3
            test_home_card.strokeWidth = 0

        }

        btn_cl?.setOnClickListener {
            findNavController().navigate(R.id.action_testLocationFragment_to_chooseScheduleFragment)
        }
    }

}
