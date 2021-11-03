package com.bdjobs.app.Jobs

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomLayoutManager(var context:Context, @RecyclerView.Orientation orientation:Int,
                          reverseLayout:Boolean): LinearLayoutManager(context,orientation,reverseLayout) {

    private var isScrollingEnable: Boolean = true

    public fun setScrollingEnable(flag:Boolean) {
        this.isScrollingEnable = flag
    }

    override fun canScrollHorizontally(): Boolean {
        return isScrollingEnable && super.canScrollHorizontally()
    }
}