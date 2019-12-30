package com.bdjobs.app.FavouriteSearch

import android.app.Fragment
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.FavouriteSearch
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.fragment_favourite_search_filter_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class FavouriteSearchFilterListFragment : Fragment() {
    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB
    lateinit var favCommunicator: FavCommunicator
    var favListSize = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favourite_search_filter_list, container, false)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bdjobsUserSession = BdjobsUserSession(activity)
        bdjobsDB = BdjobsDB.getInstance(activity)
        favCommunicator = activity as FavCommunicator

        val adRequest = AdRequest.Builder().build()
        adView?.loadAd(adRequest)

        backIV.setOnClickListener {
            favCommunicator.backButtonPressed()
        }


        doAsync {
            val favouriteSearchFilters = bdjobsDB.favouriteSearchFilterDao().getAllFavouriteSearchFilter()
            uiThread {
                try {
                    favListSize = favouriteSearchFilters.size
                    var data = "filter"
                    if (favListSize > 1) {
                        data = "filters"
                    }


                    if (favListSize!! > 0) {
                        favouriteFilterNoDataLL?.hide()
                        favRV?.show()
                        //Log.d("totalJobs", "data ase")
                    } else {
                        favouriteFilterNoDataLL?.show()
                        favRV?.hide()
                        //Log.d("totalJobs", "zero")
                    }

                    val styledText = "<b><font color='#13A10E'>$favListSize</font></b> favorite search $data"
                    favCountTV?.text = Html.fromHtml(styledText)
                    val favouriteSearchFilterAdapter = FavouriteSearchFilterAdapter(items = favouriteSearchFilters as MutableList<FavouriteSearch>, context = activity)
                    favRV?.adapter = favouriteSearchFilterAdapter
                } catch (e: Exception) {
                    logException(e)
                }
            }
        }

    }

    fun scrollToUndoPosition(position:Int){
        favRV?.scrollToPosition(position)
        favListSize++
        val styledText = "<b><font color='#13A10E'>$favListSize</font></b> favorite search filter"
        favCountTV.text = Html.fromHtml(styledText)
    }

    fun decrementCounter(){
        favListSize--
        val styledText = "<b><font color='#13A10E'>$favListSize</font></b> favorite search filter"
        favCountTV.text = Html.fromHtml(styledText)
    }

}