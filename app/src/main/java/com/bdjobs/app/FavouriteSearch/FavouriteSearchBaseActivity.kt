package com.bdjobs.app.FavouriteSearch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.transitFragment
import com.bdjobs.app.Utilities.transitFragmentX
import org.jetbrains.anko.startActivity

class FavouriteSearchBaseActivity : AppCompatActivity(), FavCommunicator {


    private val favouriteSearchFilterListFragment = FavouriteSearchFilterListFragment()

    private val favouriteSearchFilterEditFragment = FavouriteSearchFilterEditFragment()
    private var filterID :String?= ""
    private var from :String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite_search_base)
        from = intent.getStringExtra("from")
        filterID = intent.getStringExtra("favID")
        if (from=="MyJobs") goToEditMode(filterID!!)
        else transitFragmentX(favouriteSearchFilterListFragment, R.id.fragmentHolder,false)
    }

    override fun backButtonPressed() {
        onBackPressed()
    }

    override fun goToJobSearch(favID: String) {
        startActivity<JobBaseActivity>("from" to "favsearch", "filterid" to favID)
    }

    override fun goToEditMode(favID: String) {
        this.filterID = favID
        if (from=="MyJobs") transitFragment(favouriteSearchFilterEditFragment, R.id.fragmentHolder, false)
        else transitFragment(favouriteSearchFilterEditFragment, R.id.fragmentHolder, true)
    }

    override fun getFilterID(): String {
        return filterID!!
    }

    override fun scrollToUndoPosition(position: Int) {
        if(position>=0)
        favouriteSearchFilterListFragment.scrollToUndoPosition(position)
    }

    override fun decrementCounter() {
        favouriteSearchFilterListFragment.decrementCounter()
    }


}
