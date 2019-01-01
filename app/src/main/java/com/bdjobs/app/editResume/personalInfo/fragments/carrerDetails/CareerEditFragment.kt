package com.bdjobs.app.editResume.personalInfo.fragments.carrerDetails

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.editResume.callbacks.PersonalInfo

class CareerEditFragment : Fragment() {

    private lateinit var personalInfo: PersonalInfo
    private lateinit var session: BdjobsUserSession
    private var hID: String = ""
    private var hCID: String? = ""
    var isEdit = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_career_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        personalInfo = activity as PersonalInfo
    }

    override fun onResume() {
        super.onResume()
        personalInfo.setTitle(getString(R.string.title_career))
        //doWork()
        if (isEdit) {
            personalInfo.setEditButton(false)
            personalInfo.setDeleteButton(true)
            hID = "4"
            //preloadedData()
        } else {
            personalInfo.setDeleteButton(true)
            personalInfo.setEditButton(false)
            hID = "-4"
            //clearEditText()

        }
    }

    private fun doWork() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun clearEditText() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun preloadedData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
