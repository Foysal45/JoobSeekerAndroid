package com.bdjobs.app.sms.ui.guideline

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_guideline_sms.*

class GuidelineSmsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guideline_sms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_guideline1.text = getText(R.string.sms_guideline_emp1)
        tv_guideline2.text = getText(R.string.sms_guideline_emp2)
        tv_guideline3.text = getText(R.string.sms_guideline_emp3)

        tv_guideline4.text = getText(R.string.sms_guideline_fav1)
        tv_guideline5.text = getText(R.string.sms_guideline_fav2)
        tv_guideline6.text = getText(R.string.sms_guideline_fav3)
    }
}