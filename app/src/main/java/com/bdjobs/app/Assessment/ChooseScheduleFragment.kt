package com.bdjobs.app.Assessment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_choose_schedule.*

/**
 * A simple [Fragment] subclass.
 */
class ChooseScheduleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_cl?.setOnClickListener {
            findNavController().navigate(R.id.action_chooseScheduleFragment_to_bookScheduleFragment)
        }
    }


}
