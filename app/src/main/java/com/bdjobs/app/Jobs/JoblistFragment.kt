package com.bdjobs.app.Jobs

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_joblist_layout.*

class JoblistFragment : Fragment() {
    private lateinit var session:BdjobsUserSession;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_joblist_layout, container, false)!!

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity);
        if(session?.isLoggedIn!!){
            homeIMGV.hide()
        }else{
            homeIMGV.show()
        }

        shimmer_view_container.startShimmerAnimation()

    }
}