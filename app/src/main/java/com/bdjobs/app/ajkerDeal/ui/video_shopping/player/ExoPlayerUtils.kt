package com.bdjobs.app.ajkerDeal.ui.video_shopping.player

import android.app.Application
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector

object ExoPlayerUtils {

    fun getInstance(application: Application): SimpleExoPlayer {

        val adaptiveTrackSelection = AdaptiveTrackSelection.Factory()
        val builder = DefaultTrackSelector.ParametersBuilder(application)
        val trackSelectorParameters = builder.build()
        val trackSelector = DefaultTrackSelector(application, adaptiveTrackSelection).apply {
            parameters = trackSelectorParameters
        }
        return SimpleExoPlayer.Builder(application).setTrackSelector(trackSelector).build()
    }

}