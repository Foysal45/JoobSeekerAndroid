package com.bdjobs.app.editResume.personalInfo.fragments.preferredAreas


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.d
import com.bdjobs.app.editResume.adapters.models.PreferredAreasData
import com.bdjobs.app.editResume.callbacks.PersonalInfo

class PreferredAreasEditFragment : Fragment() {
    private lateinit var prefCallBack: PersonalInfo
    private lateinit var session: BdjobsUserSession
    private lateinit var ds: DataStorage
    private lateinit var data: PreferredAreasData
    private var idArr: ArrayList<String> = ArrayList()
    private var exps: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preferred_areas_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ds = DataStorage(activity)
        session = BdjobsUserSession(activity)
        prefCallBack = activity as PersonalInfo
        d("onActivityCreated")
        doWork()
        prefCallBack.setTitle(getString(R.string.title_pref_areas))
        prefCallBack.setEditButton(false, "dd")
    }

    private fun doWork() {
        data = prefCallBack.getPrefAreasData()
        //onClicks()
    }

    private fun onClicks() {

    }
}
