package com.bdjobs.app.editResume.educationInfo.fragments


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.editResume.callbacks.EduInfo

class AcademicInfoEditFragment : Fragment() {

    var isEdit: Boolean = false
    private lateinit var eduCB: EduInfo
    private lateinit var session: BdjobsUserSession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_academic_info_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        eduCB = activity as EduInfo
        eduCB.setDeleteButton(true)
    }

}
