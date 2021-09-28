package com.bdjobs.app.ajkerDeal.ui.video_shopping.player

import android.content.Context
import android.net.Uri
import com.bdjobs.app.R
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.Util
import timber.log.Timber

class MediaSourceBuilder {

    //Build various MediaSource depending upon the type of Media for a given video/audio uri
    fun build(context: Context, uri: Uri, isDefaultDataSource: Boolean = false): MediaSource {

        val userAgent = Util.getUserAgent(context, context.getString(R.string.app_name))
        val lastPath = uri.lastPathSegment ?: ""
        val defaultDataSourceFactory = DefaultDataSourceFactory(context, userAgent)
        //val simpleCache = MainApplication.simpleCache
        val dataSourceFactory = if (isDefaultDataSource) {
            defaultDataSourceFactory
        } else {
            CacheDataSource.Factory()
                //.setCache(simpleCache!!)
                .setUpstreamDataSourceFactory(defaultDataSourceFactory)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            //.setCacheWriteDataSinkFactory(null) // Disable writing.
        }
        val mediaItem = MediaItem.fromUri(uri)
        Timber.d("MediaSourceBuilder lastPath $lastPath")
        if (lastPath.contains(PlayerConstants.FORMAT_M3U8)) {
            Timber.d("MediaSourceBuilder type HlsMediaSource")

            return HlsMediaSource.Factory(dataSourceFactory)
                .setAllowChunklessPreparation(true)
                .setLoadErrorHandlingPolicy(CustomPolicy())
                .createMediaSource(mediaItem)
        } else if (lastPath.contains(PlayerConstants.FORMAT_MP3) || lastPath.contains(PlayerConstants.FORMAT_MP4)) {
            Timber.d("MediaSourceBuilder type ProgressiveMediaSource")
            return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        } else if (uri.scheme == "rtmp") {
            Timber.d("MediaSourceBuilder type rtmp ProgressiveMediaSource")
            //val rtmpDataSourceFactory = RtmpDataSourceFactory()
            return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        } else { // .mpd
            Timber.d("MediaSourceBuilder type DefaultDashChunkSource")
            val dashChunkSourceFactory = DefaultDashChunkSource.Factory(dataSourceFactory)
            return DashMediaSource.Factory(dashChunkSourceFactory, dataSourceFactory).createMediaSource(mediaItem)
        }
    }


    //Overloaded function to Build various MediaSource for whole playlist of video/audio uri
    fun build(context: Context, uriList: Array<Uri>): MediaSource {
        val playlistMediaSource = ConcatenatingMediaSource()
        uriList.forEach { playlistMediaSource.addMediaSource(build(context, it)) }
        return playlistMediaSource
    }


}