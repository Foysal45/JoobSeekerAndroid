package com.bdjobs.app.editResume.otherInfo.fragments


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.editResume.adapters.TrainingInfoAdapter
import com.bdjobs.app.editResume.adapters.models.Tr_DataItem
import com.bdjobs.app.editResume.callbacks.OtherInfo

class SpecializationViewFragment : Fragment() {
    private lateinit var eduCB: OtherInfo
    private var adapter: TrainingInfoAdapter? = null
    private var arr: ArrayList<Tr_DataItem>? = null
    private lateinit var session: BdjobsUserSession


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_specialization, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        eduCB = activity as OtherInfo
        doWork()

    }

    private fun doWork() {
        /*  populateData()*/
        /*   rv_lang_view.behaveYourself(fab_language_add)*/
        eduCB.setDeleteButton(false)
        eduCB.setTitle("Specialization")


    }

}
