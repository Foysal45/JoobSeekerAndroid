package com.bdjobs.app.Jobs

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationScrollListener
/**
 * Supporting only LinearLayoutManager for now.
 *
 * @param layoutManager
 */
(internal var layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

    abstract val totalPageCount: Int
    abstract val isLastPage: Boolean
    abstract val isLoading: Boolean

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        Log.d("Loading", "$visibleItemCount $totalItemCount $firstVisibleItemPosition")

        if (!isLoading && !isLastPage) {

            //Log.d("Loading", "!isLoading && !isLastPage ")

            /*Log.d("Loading", "visibleItemCount:  $visibleItemCount " +
                    "firstVisibleItemPosition: $firstVisibleItemPosition totalItemCount: $totalItemCount ")*/


            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                Log.d("Loading", "loadMoreItems ")

                if(isLastPage != true){

                    loadMoreItems()
                }


            }
        } else if (isLastPage) {
            //Log.d("Loading", "isLastPage")
        }

    }

    protected abstract fun loadMoreItems()

}
