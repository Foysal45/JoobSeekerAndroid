package com.bdjobs.app.ajkerDeal.ui.video_shopping.video_pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bdjobs.app.ajkerDeal.api.models.catalog.CatalogData
import com.google.android.exoplayer2.SimpleExoPlayer

class VideoPagingAdapter(
    fa: FragmentActivity,
    private val list: MutableList<CatalogData>,
    private val exoPlayer: SimpleExoPlayer
) : FragmentStateAdapter(fa) {

    override fun createFragment(position: Int): Fragment {
        val model = list[position]
        return if (model.facebookVideoUrl.isNotEmpty() || model.isYoutubeVideo) {
                FBLiveShowFragment.newInstance(model, position, exoPlayer)
            } else {
                LiveShowFragment.newInstance(model, position, exoPlayer)
            }

    }

    override fun getItemCount(): Int = list.size

}