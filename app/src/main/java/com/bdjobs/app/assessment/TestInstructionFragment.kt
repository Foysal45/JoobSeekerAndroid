package com.bdjobs.app.assessment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_test_instruction.*

/**
 * A simple [Fragment] subclass.
 */
class TestInstructionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_instruction, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_cl.setOnClickListener {
            findNavController().navigate(R.id.action_testInstructionFragment_to_homeFragment)
        }
    }
}
