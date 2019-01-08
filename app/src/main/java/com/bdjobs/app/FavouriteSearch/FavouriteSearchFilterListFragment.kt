package com.bdjobs.app.FavouriteSearch

import android.app.Fragment
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.FavouriteSearch
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import kotlinx.android.synthetic.main.fragment_favourite_search_filter_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class FavouriteSearchFilterListFragment : Fragment() {
    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB
    lateinit var favCommunicator: FavCommunicator
    var favListSize = 0

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
                favListSize =favouriteSearchFilters.size
              //  val styledText = "<b><font color='#13A10E'>$favListSize</font></b> favorite search filter"
             //   favCountTV?.text = Html.fromHtml(styledText)
                favRV?.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
                var data = "filter"
                if(favListSize>1){
                    data = "filters"
                }
                val styledText = "<b><font color='#13A10E'>$favListSize</font></b> favorite search $data"
                favCountTV.text = Html.fromHtml(styledText)
                val favouriteSearchFilterAdapter = FavouriteSearchFilterAdapter(items = favouriteSearchFilters as MutableList<FavouriteSearch>, context = activity)
                favRV?.adapter = favouriteSearchFilterAdapter
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