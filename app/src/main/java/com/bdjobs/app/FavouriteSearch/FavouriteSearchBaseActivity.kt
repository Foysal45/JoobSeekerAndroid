package com.bdjobs.app.FavouriteSearch

import android.app.Activity
import android.os.Bundle
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.transitFragment
import org.jetbrains.anko.startActivity

class FavouriteSearchBaseActivity : Activity(), FavCommunicator {



    private val favouriteSearchFilterListFragment = FavouriteSearchFilterListFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite_search_base)
        transitFragment(favouriteSearchFilterListFragment, R.id.fragmentHolder)
    }

    override fun backButtonPressed() {
        onBackPressed()
    }
    override fun goToJobSearch(favID: String) {
        startActivity<JobBaseActivity>("from" to "favsearch", "filterid" to favID)
    }

}
