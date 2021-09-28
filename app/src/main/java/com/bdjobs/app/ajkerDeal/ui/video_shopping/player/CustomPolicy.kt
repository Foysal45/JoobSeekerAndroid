package com.bdjobs.app.ajkerDeal.ui.video_shopping.player

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy

class CustomPolicy: DefaultLoadErrorHandlingPolicy(DefaultLoadErrorHandlingPolicy.DEFAULT_MIN_LOADABLE_RETRY_COUNT_PROGRESSIVE_LIVE) {


    override fun getRetryDelayMsFor(loadErrorInfo: LoadErrorHandlingPolicy.LoadErrorInfo): Long {
        return if (loadErrorInfo.exception is HttpDataSource.HttpDataSourceException) {
            3000 // Retry every 5 seconds.
        } else {
            C.TIME_UNSET // Anything else is surfaced.
        }
    }

    override fun getMinimumLoadableRetryCount(dataType: Int): Int {
        return 10
    }

}