package com.bdjobs.app.manageResume


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_times_emailed_my_resume_filter.*

/**
 * A simple [Fragment] subclass.
 */
class TimesEmailedMyResumeFilterFragment : Fragment() {

    private lateinit var manageCommunicator: ManageResumeCommunicator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_times_emailed_my_resume_filter, container, false)
    }

    override fun onResume() {
        super.onResume()
        manageCommunicator = requireActivity() as ManageResumeCommunicator

        backIMV.setOnClickListener {
            manageCommunicator.backButtonPressed()
        }

        searchBTN_fab?.setOnClickListener {
            manageCommunicator.setBackFrom("filter")
            manageCommunicator.gotoTimesResumeFrag()
        }
    }

}
