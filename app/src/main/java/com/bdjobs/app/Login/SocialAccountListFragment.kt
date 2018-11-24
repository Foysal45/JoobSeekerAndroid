package com.bdjobs.app.Login

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_social_account_list_layout.*

class SocialAccountListFragment: Fragment() {
    private lateinit var loginCommunicator: LoginCommunicator
    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_social_account_list_layout, container, false)!!
        return rootView
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginCommunicator = activity as LoginCommunicator
    }

    override fun onResume() {
        super.onResume()
        socialAccountsRecyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        val socialAccountAdapter = SocialRVadapter(items = loginCommunicator.getSocialLoginAccountDataList(),context = activity)
        socialAccountsRecyclerView.adapter = socialAccountAdapter

    }


}