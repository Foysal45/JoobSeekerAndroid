package com.bdjobs.app.InviteCode.InviteCodeOwner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.loadCircularImageFromUrl
import kotlinx.android.synthetic.main.invite_code_owner_base_fragment_layout.*

class OwnerBaseFragment : Fragment(){
    private var bdjobsUserSession: BdjobsUserSession?=null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.invite_code_owner_base_fragment_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bdjobsUserSession = BdjobsUserSession(activity!!)

        nameTV.text = bdjobsUserSession?.fullName
        emailTV.text = bdjobsUserSession?.email
        profilePicIMGV.loadCircularImageFromUrl(bdjobsUserSession?.userPicUrl)

        val navController = Navigation.findNavController(activity!!, R.id.ownerBottomNavFragmentHolder)
        bottomNavigationView.setupWithNavController(navController)
    }
}