package com.bdjobs.app.FavouriteSearch

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import kotlinx.android.synthetic.main.fragment_favourite_search_filter_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class FavouriteSearchFilterListFragment : Fragment() {
    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB
    lateinit var favCommunicator: FavCommunicator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_favourite_search_filter_list, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsUserSession = BdjobsUserSession(activity)
        bdjobsDB = BdjobsDB.getInstance(activity)
        favCommunicator = activity as FavCommunicator

        backIV.setOnClickListener {
            favCommunicator.backButtonPressed()
        }


        doAsync {
            val favouriteSearchFilters = bdjobsDB.favouriteSearchFilterDao().getAllFavouriteSearchFilter()
            uiThread {
                favCountTV.text = "${favouriteSearchFilters.size} favorite search"
                favRV?.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
                val favouriteSearchFilterAdapter = FavouriteSearchFilterAdapter(items = favouriteSearchFilters, context = activity)
                favRV?.adapter = favouriteSearchFilterAdapter
            }
        }


    }

}