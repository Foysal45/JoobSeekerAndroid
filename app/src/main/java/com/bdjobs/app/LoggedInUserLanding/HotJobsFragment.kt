package com.bdjobs.app.LoggedInUserLanding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.Fragment
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.loadCircularImageFromUrl
import kotlinx.android.synthetic.main.fragment_hotjobs_layout.*

class HotJobsFragment : Fragment() {
    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var homeCommunicator: HomeCommunicator



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_hotjobs_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bdjobsUserSession = BdjobsUserSession(activity)
        homeCommunicator = activity as HomeCommunicator

        profilePicIMGV.loadCircularImageFromUrl(bdjobsUserSession.userPicUrl)

        searchIMGV.setOnClickListener {
            homeCommunicator.goToKeywordSuggestion()
        }

        webView.loadUrl(Constants.HOTJOBS_WEB_LINK)
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

}