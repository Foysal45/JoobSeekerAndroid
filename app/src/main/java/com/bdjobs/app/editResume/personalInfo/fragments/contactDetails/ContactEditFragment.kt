package com.bdjobs.app.editResume.contactInfo.fragments.contactDetails

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import kotlinx.android.synthetic.main.fragment_contact_edit.*

class ContactEditFragment : Fragment() {

    private lateinit var contactInfo: PersonalInfo
    private lateinit var session: BdjobsUserSession
    lateinit var dataStorage: DataStorage
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dataStorage = DataStorage(activity)
        session = BdjobsUserSession(activity)
        contactInfo = activity as PersonalInfo
    }

    override fun onResume() {
        super.onResume()
        contactInfo.setTitle(getString(R.string.title_contact))
        doWork()
    }

    private fun doWork() {
        /* preloadedData()*/
        fab_contact_update.setOnClickListener {
            //updateData()
        }


        insideBangladeshChip.setOnClickListener {
            presentContactCountryTIL.hide()
            thanaPostLayout.show()

        }


        outSideBangladesh.setOnClickListener {

            thanaPostLayout.hide()
            presentContactCountryTIL.show()


        }


    }

    private fun updateData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun preloadedData() {
        val data = contactInfo.getContactData()
    }

}
