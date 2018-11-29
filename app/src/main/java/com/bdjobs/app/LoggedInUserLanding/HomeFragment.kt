package com.bdjobs.app.LoggedInUserLanding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.Fragment
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.FavouriteSearch
import com.bdjobs.app.Databases.Internal.FollowedEmployer
import com.bdjobs.app.FavouriteSearch.FavouriteSearchFilterAdapter
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.loadCircularImageFromUrl
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_home_layout.*
import kotlinx.android.synthetic.main.my_favourite_search_filter_layout.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class HomeFragment : Fragment() {
    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_home_layout, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsUserSession = BdjobsUserSession(activity)
        bdjobsDB = BdjobsDB.getInstance(activity)
    }

    override fun onResume() {
        super.onResume()
        nameTV.text = bdjobsUserSession.fullName
        emailTV.text = bdjobsUserSession.email
        profilePicIMGV.loadCircularImageFromUrl(bdjobsUserSession.userPicUrl)
        getData()
    }

    private fun getData() {
        doAsync {
            val favouriteSearchFilters = bdjobsDB.favouriteSearchFilterDao().getLatest2FavouriteSearchFilter()
            val followedEmployerList = bdjobsDB.followedEmployerDao().getAllFollowedEmployer()
            uiThread {
                setFavFilterLastSearchAnFollowedEmployer(favouriteSearchFilters, followedEmployerList)
            }
        }


    }

    private fun setFavFilterLastSearchAnFollowedEmployer(favouriteSearchFilters: List<FavouriteSearch>, followedEmployerList: List<FollowedEmployer>) {
        if (followedEmployerList.isEmpty() && favouriteSearchFilters.isEmpty()) {
            blankCL.show()
            mainLL.hide()
        } else {
            blankCL.hide()
            mainLL.show()

            favRV?.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
            val favouriteSearchFilterAdapter = FavouriteSearchFilterAdapter(items = favouriteSearchFilters, context = activity)
            favRV.adapter = favouriteSearchFilterAdapter

        }
    }
}