package com.bdjobs.app.InviteCode.InviteCodeOwner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.databases.External.DataStorage
import com.bdjobs.app.InviteCode.InviteCodeCommunicator
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.getBlueCollarUserId
import com.bdjobs.app.Utilities.loadCircularImageFromUrl
import kotlinx.android.synthetic.main.invite_code_owner_base_fragment_layout.*

class OwnerBaseFragment : Fragment(){
    private var bdjobsUserSession: BdjobsUserSession?=null
    private var inviteCodeCommunicator:InviteCodeCommunicator?=null
    private var dataStorage: DataStorage?=null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.invite_code_owner_base_fragment_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bdjobsUserSession = BdjobsUserSession(activity!!)
        inviteCodeCommunicator = activity as InviteCodeCommunicator
        dataStorage = DataStorage(activity!!)

        nameTV.text = bdjobsUserSession?.fullName
        emailTV.text = dataStorage?.getCategoryBanglaNameByID(activity?.getBlueCollarUserId().toString())
        profilePicIMGV.loadCircularImageFromUrl(bdjobsUserSession?.userPicUrl)

        backIMGV.setOnClickListener {
            inviteCodeCommunicator?.backButtonClicked()
        }

        val navController = Navigation.findNavController(activity!!, R.id.ownerBottomNavFragmentHolder)
        bottomNavigationView.setupWithNavController(navController)
    }
}